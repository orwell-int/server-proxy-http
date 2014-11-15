(ns server-proxy-web.orwell-test
  (:require [clojure.test :refer :all])
  (:use [flatland.protobuf.core :as protobuf]
        [server-proxy-web.views.orwell :as orwell]))

(import orwell.messages.Controller)
(import orwell.messages.ServerGame$EnumTeam)

;; Test that the objects built with clj-protobuf are created correctly
(deftest protobuf-consistency
  (is (protobuf/protodef? orwell/def-hello))
  (is (protobuf/protodef? orwell/def-goodbye))
  (is (protobuf/protodef? orwell/def-welcome)))

;; Conversion from a string to a protodef
(deftest string-to-protodef-test
  (is (protobuf/protodef? (orwell/string-to-protodef "hello"))))

;; Stupidest test ever!
(deftest is-message-for-us?-test
  (is (= true (orwell/is-message-for-us? "Clorwell 1 2")))
  (is (= false (orwell/is-message-for-us? "Machin 1 2"))))
