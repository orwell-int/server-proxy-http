(defproject server-proxy-web "0.1.0-SNAPSHOT"
            :description "HTTP Proxy for Orwell's main project'"
            :dependencies [[org.clojure/clojure "1.5.1"]
                           [org.clojure/tools.macro "0.1.5"]
                           [org.clojure/data.json "0.2.5"]
                           [clout "2.0.0"]
                           [compojure "1.2.1"]
                           ;; We need version 2 of clout
                           [lib-noir "0.9.4" :exclusions [clout]]
                           [ring-server "0.3.1"]
                           [hiccup "1.0.5"]
                           [org.pingles/protobuf "0.7.3-2.5.0"]
                           [org.clojars.mikejs/clojure-zmq "2.0.7-SNAPSHOT"]
                           [byte-streams "0.2.0-alpha4"]
                           [org.zeromq/jzmq "3.1.0"]]
            :native-deps "/opt/jzmq/lib/"
            :ring {:handler server-proxy-web.server/app}
            :plugins [[lein-ring "0.8.13"]
                      [lein-protobuf-cn "0.3.1"]
                      ;; This could probably be moved elsewhere
                      [cider/cider-nrepl "0.8.0-SNAPSHOT"]]
            :proto-path "resources/messages"
            :min-lein-version "2.0.0"
            :aliases {"test-all" ^{:doc "Compiles protobuf and runs tests"}
                      ["do" "clean" ["protobuf"] ["test"]]}
            :jvm-opts ["-Djava.library.path=/opt/jzmq/lib"]
            :main server-proxy-web.server)
