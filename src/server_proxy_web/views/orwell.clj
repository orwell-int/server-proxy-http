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


;; Hello
(defn -build-message-hello [map]
  (if (contains? map :name)
    (protobuf/protobuf proto-hello
                       :name (map :name)
                       :ready (map :ready))
    nil))

;; Goodbye
(defn -build-message-goodbye [map]
  (protobuf/protobuf proto-goodbye))

;; Welcome
(defn -build-message-welcome [map]
  (protobuf/protobuf proto-welcome
                     :robot (map :robot)
                     :team (map :team)
                     :id   (map :id)
                     :video_address (map :video-address)
                     :video_port (map :video-port)))


;; Just a simple router to create the different messages
(defn -message-builder [id params]
  (cond [= id "hello"] (-build-message-hello params)))


;; Main entry point to the service
(defn router [id params]
  (let [proto-message (-message-builder id params)]
    (when (= proto-message nil) nil)))
