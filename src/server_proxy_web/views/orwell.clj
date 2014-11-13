(ns server-proxy-web.views.orwell
  (:use [hiccup.core :as hiccup]
        [flatland.protobuf.core :as protobuf]))

(import orwell.messages.ServerGame$Welcome)
(import orwell.messages.ServerGame$Goodbye)
(import orwell.messages.Controller$Hello)
(import orwell.messages.ServerGame$EnumTeam)

;; Definition of the messages that will be used
(def proto-hello (protobuf/protodef orwell.messages.Controller$Hello))
(def proto-goodbye (protobuf/protodef orwell.messages.ServerGame$Goodbye))
(def proto-welcome (protobuf/protodef orwell.messages.ServerGame$Welcome))

;; Main entry point to the service
(defn router
  [id params]
  (try
    (cond (= id "welcome") (protobuf/protobuf proto-welcome params)
          (= id "hello") (protobuf/protobuf proto-hello params)
          (= id "goodbye") (protobuf/protobuf proto-goodbye)
          :else (throw (Throwable. "I do not know how to build that message")))
    (catch com.google.protobuf.UninitializedMessageException e
      nil)))
