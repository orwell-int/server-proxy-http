(ns server-proxy-web.orwell-test
  (:require [clojure.test :refer :all])
  (:use [flatland.protobuf.core :as protobuf]
        [server-proxy-web.views.orwell :as orwell]))

(import orwell.messages.Controller)
(import orwell.messages.ServerGame$EnumTeam)

;; Test that the objects built with clj-protobuf are created correctly
(deftest protobuf-consistency
  (is (protobuf/protodef? orwell/proto-hello))
  (is (protobuf/protodef? orwell/proto-goodbye))
  (is (protobuf/protodef? orwell/proto-welcome)))

;; Creation of a message is successful
(deftest simple-hello-message
  (is (protobuf/protobuf?
       (orwell/-build-message-hello {:name "Robot"
                                     :ready true}))))
;; Fields of a message are set correctly
(deftest hello-message-fields
  (let [hello-message (orwell/-build-message-hello {:name "Robot" :ready true})]
    (is (= (hello-message :name) "Robot"))
    (is (= (hello-message :ready) true))))

;; Correctness of fields
(deftest hello-null-message
  (let [hello-message (orwell/-build-message-hello {:non-name "Robot"})]
    (is (= nil hello-message))))

;; Optional field, message gets constructed properly with default value of true
(deftest hello-optional-field
  (let [hello-message (orwell/-build-message-hello {:name "Robot"})]
    (is (not (= nil hello-message)))
    (is (= true (hello-message :ready)))))

;; Welcome message
(deftest welcome-message-test
  (let [welcome-message (orwell/-build-message-welcome {:robot "Robot"
                                                        :team :RED
                                                        :id "id"
                                                        :video-address "localhost"
                                                        :video-port 80})]
    (is (protobuf/protobuf? welcome-message))))

;; Message Builder
(deftest message-builder
  (is (protobuf/protobuf? (orwell/-message-builder "hello" {:name "Robot"}))))
