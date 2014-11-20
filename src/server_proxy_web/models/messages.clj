(ns server-proxy-web.models.messages
  (:use [flatland.protobuf.core :as protobuf]
        [clojure.string :only [split]]
        [byte-streams]))

(import orwell.messages.ServerGame$Welcome)
(import orwell.messages.ServerGame$Goodbye)
(import orwell.messages.ServerGame$EnumTeam)
(import orwell.messages.ServerGame$GameState)
(import orwell.messages.Controller$Hello)

;; Definition of the messages that will be used
(def def-hello (protobuf/protodef orwell.messages.Controller$Hello))
(def def-goodbye (protobuf/protodef orwell.messages.ServerGame$Goodbye))
(def def-welcome (protobuf/protodef orwell.messages.ServerGame$Welcome))
(def def-gamestate (protobuf/protodef orwell.messages.ServerGame$GameState))

;; Handy conversion functions
(defn string-to-bytes [s] (.getBytes s))
(defn bytes-to-string [b] (String. b))

(defn string-to-protodef
  "This routine retrieves a protodef from a string"
  [string]
  (let [lowercase-string (.toLowerCase string)]
    (cond (= "welcome" lowercase-string) def-welcome
          (= "hello" lowercase-string) def-hello
          (= "goodbye" lowercase-string) def-goodbye
          (= "gamestate" lowercase-string) def-gamestate
          :else nil)))

(defn build-message-from-zmq
  "This routine builds a protobuf from a ZMQ bytes"
  [bytes]
  (let [parts (split bytes #" ")
        stream (to-input-stream (last parts))]
    (if (= 3 (count parts))
      [(first (rest parts))
       (protobuf/protobuf-load-stream (string-to-protodef (first (rest parts))) stream)])))

(defn build-zmq-from-message
  "This routine builds a ZMQ bytes from a protobuf"
  [protobuf]
  (bytes-to-string protobuf/protobuf-dump protobuf))
