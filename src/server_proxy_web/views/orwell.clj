(ns server-proxy-web.views.orwell
  (:use [hiccup.core :as hiccup]
        [flatland.protobuf.core :as protobuf]
        [org.zeromq.clojure :as zmq]
        [clojure.data.json :as json :only [write-str]]
        [clojure.java.io :as io]
        [byte-streams]
        [clojure.string :only [split]]))

(import orwell.messages.ServerGame$Welcome)
(import orwell.messages.ServerGame$Goodbye)
(import orwell.messages.ServerGame$EnumTeam)
(import orwell.messages.ServerGame$GameState)
(import orwell.messages.Controller$Hello)
(import '(org.zeromq ZMQ ZContext))
(import '(org.zeromq.ZMQ.Socket))

;; Definition of the messages that will be used
(def def-hello (protobuf/protodef orwell.messages.Controller$Hello))
(def def-goodbye (protobuf/protodef orwell.messages.ServerGame$Goodbye))
(def def-welcome (protobuf/protodef orwell.messages.ServerGame$Welcome))
(def def-gamestate (protobuf/protodef orwell.messages.ServerGame$GameState))

;; ZMQ context that will be used to push to the server
(def zmq-context (zmq/make-context 1))
(def client-name "Clorwell")

;; Server's address
(def push-server-address "tcp://localhost:9001")
(def pull-server-address "tcp://localhost:9000")
(def loop-running true)
(def message-received false)


;; Handy conversion functions
(defn string-to-bytes [s] (.getBytes s))
(defn bytes-to-string [b] (String. b))

(defn print-string-list
  "Print a list of strings, mainly a debug function"
  [list]
  (if (empty? list) nil
      (do (println (first list))
          (print-string-list (rest list)))))

(defn string-to-protodef
  "This routine retrieves a protodef from a string"
  [string]
  (let [lowercase-string (.toLowerCase string)]
    (cond (= "welcome" lowercase-string) def-welcome
          (= "hello" lowercase-string) def-hello
          (= "goodbye" lowercase-string) def-goodbye
          (= "gamestate" lowercase-string) def-gamestate
          :else nil)))

(defn message-to-protobuf
  "This routine helps retrieving a protobuf from a formatted message
  it will return a pair, where the first element is a string describing
  the message type, and the second is the message itself"
  [bytes]
  (let [parts (split bytes #" ")
        stream (to-input-stream (last parts))]
    (if (= 3 (count parts))
      [(first (rest parts))
       (protobuf/protobuf-load-stream (string-to-protodef (first (rest parts))) stream)])))

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
  (let [socket (zmq/make-socket zmq-context zmq/+sub+)]
    (.subscribe socket (string-to-bytes ""))
    (zmq/connect socket pull-server-address)
    (loop [message (bytes-to-string (zmq/recv socket))
           x limit]
      (println x)
      (if (= x 0) ["Unable to retrieve a message" {}]
          (if (is-message-for-us? message)
            (message-to-protobuf message)
            (recur (bytes-to-string (zmq/recv socket)) (dec x)))))))

(defn send-protobuf-to-server
  "This function sends a protobuf to the server and receives a protobuf back"
  [message-id protobuf]
  (let [socket (zmq/make-socket zmq-context ZMQ/PUSH)
        message (str client-name " " message-id " " (bytes-to-string (protobuf/protobuf-dump protobuf)))]
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
