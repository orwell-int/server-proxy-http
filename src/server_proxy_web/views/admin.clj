(ns server-proxy-web.views.admin
  (:use [hiccup.core :as hiccup]
        [hiccup.page :as page]
        [server-proxy-web.models.messages :as messages]
        [flatland.protobuf.core :as protobuf]
        [org.zeromq.clojure :as zmq]
        [ring.middleware.anti-forgery :as af]
        [ring.util.anti-forgery]
        [clojure.data.json :as json :only [write-str read-str read-json]]
        [clojure.java.io :as io]
        [byte-streams]
        [clojure.string :only [split]]))

(defn build-page [& {:keys [response error]}]
  (page/html5
   [:head [:title "Orwell Router | Admin Interface"]
    (page/include-css "/orwell/resources/css/simple.css")]
   [:body
    [:div#container
     [:div#header [:h1 "Orwell Router | Admin Interface"]]
     [:form#main {:action "/orwell/admin/" :method "post"}
      [:input.command {:type "text"
                       :name "command"
                       :size "40"
                       :placeholder "Issue a command"}] [:br]
      [:input {:type "submit" :name "submit" :value "Go!"}]]
     (if (or response error)
       [:div#response [:h3.response "The server answers"]
        (if response
          [:code response])
        (if error
          "An error occured " error)])]]))


(defn admin
  "This is the main view for administrating the server game"

  ;; Get interface is quite simple
  ([] (build-page))


  ;; This is for when we receive a POST
  ([command]
  (let [context (zmq/make-context 1)
        socket (zmq/make-socket context zmq/+req+)
        server-address "tcp://localhost:9003"]
    (zmq/connect socket server-address)
    (if (zmq/send- socket (messages/string-to-bytes command))
      (let [response (bytes-to-string (zmq/recv socket))]
        (build-page :response response))
      (build-page :error "Communication error ?")))))
