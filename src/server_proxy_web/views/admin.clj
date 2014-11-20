(ns server-proxy-web.views.admin
  (:use [hiccup.core :as hiccup]
        [hiccup.page :as page]
        [server-proxy-web.models.messages :as messages]
        [flatland.protobuf.core :as protobuf]
        [org.zeromq.clojure :as zmq]
        [ring.util.anti-forgery :refer [anti-forgery-field]]
        [clojure.data.json :as json :only [write-str read-str read-json]]
        [clojure.java.io :as io]
        [byte-streams]
        [clojure.string :only [split]]))


(def main-page
  (page/html5
   [:head [:title "Orwell Router | Admin Interface"]
    (page/include-css "/orwell/resources/css/simple.css")]
   [:body
    [:div#container
     [:div#header [:h1 "Orwell Router | Admin Interface"]]
     [:form#main {:action "/orwell/admin/" :method "post"}
      [:input.command {:type "text" :name "command" :placeholder "Issue a command"}]
      (anti-forgery-field)
      [:input {:type "submit" :name "submit" :value "Go!"}]]]]))

(defn parse-parameters
  [params] "list robot")

(defn admin
  "This is the main view for administrating the server game"
  ([] main-page)


  ([params]
  (let [context (zmq/make-context 1)
        socket (zmq/make-socket context zmq/+req+)
        server-address "tcp://localhost:9003"
        command (parse-parameters params)]
    (zmq/connect socket server-address)
    (if (zmq/send- socket (messages/string-to-bytes command))
      (let [response (bytes-to-string (zmq/recv socket))]
        (println "Received \n" response))
      (println "Message NOT sent")))))
