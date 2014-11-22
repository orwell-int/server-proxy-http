(ns server-proxy-web.views.orwell
  (:use [hiccup.core :as hiccup]
        [hiccup.page :as page]
        [server-proxy-web.models.messages :as messages]
        [flatland.protobuf.core :as protobuf]
        [org.zeromq.clojure :as zmq]
        [clojure.data.json :as json :only [write-str read-str read-json]]
        [clojure.java.io :as io]
        [byte-streams]
        [clojure.string :only [split]]))

(import '(org.zeromq ZMQ ZContext))
(import '(org.zeromq.ZMQ.Socket))

;; ZMQ context that will be used to push to the server
(def zmq-context (zmq/make-context 1))
(def client-name "Clorwell")

(defn print-string-list
  "Print a list of strings, mainly a debug function"
  [list]
  (if (empty? list) nil
      (do (println (first list))
          (print-string-list (rest list)))))

(defn is-message-for-us?
  "Just a quick function to determine whether a message is for us or not"
  [message]
  (let [id (first (split message #" "))]
    (= id client-name)))

(defn run-subscriber
  "This function runs a subscriber in background and
  for each message it receives, it will call a
  callback, if one is set"
  [limit]
  (println "Running subscriber")
  (let [socket (zmq/make-socket zmq-context zmq/+sub+)
        pull-server-address "tcp://localhost:9000"]
    (.subscribe socket (messages/string-to-bytes ""))
    (zmq/connect socket pull-server-address)
    (loop [message (messages/bytes-to-string (zmq/recv socket))
           x limit]
      (println x)
      (if (= x 0) ["Unable to retrieve a message" {}]
          (if (is-message-for-us? message)
            (messages/build-message-from-zmq message)
            (recur (messages/bytes-to-string (zmq/recv socket)) (dec x)))))))

(defn send-protobuf-to-server
  "This function sends a protobuf to the server and receives a protobuf back"
  [message-id protobuf]
  (let [socket (zmq/make-socket zmq-context ZMQ/PUSH)
        message (str client-name " " message-id " " (messages/build-zmq-from-message protobuf))
        push-server-address "tcp://localhost:9001"]
    (zmq/connect socket push-server-address)
    (zmq/send- socket (string-to-bytes message))))

(defn router
  "This is the main entry point of the service"
  [id params]
  (let [ret-value (future (run-subscriber 3))]
    (try
      (let [message-def (string-to-protodef id)
            message (protobuf/protobuf message-def params)]
        (.toString (send-protobuf-to-server id message)))
      (catch com.google.protobuf.UninitializedMessageException e nil))
    (json/write-str {:tag (first @ret-value) :message (rest @ret-value)})))

(defn router-html
  "This is the same as the normal router, except that it gives an HTML result"
  [message params]
  (let [protobuf (json/read-json (router message params))
        message-identity (map identity (first (protobuf :message)))]
    (page/html5
     [:head [:title "Orwell Router | Message Debug"]
      (page/include-css "/orwell/resources/css/simple.css")]
     [:body
      [:div#container
       [:div#header [:h1 "Orwell Router | Message Debug"]]
       [:div#content
        [:h2 "Conversation"]
        [:p [:strong message] " --server--> "
         [:strong (protobuf :tag)]]
        [:table#parameters
         [:tr [:th "Key"] [:th "Value"]]
         (map (fn [x] [:tr [:td.key (first x)] [:td.value (last x)]]) message-identity)]]]])))

(defn wrapper
  [message params]
  (let [format (params :format)]
    (if (= format "html")
      (router-html message params)
      (router message params))))
