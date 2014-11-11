(defproject server-proxy-web "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :dependencies [[org.clojure/clojure "1.5.1"]
                           [org.clojure/tools.macro "0.1.5"]
                           [clout "2.0.0"]
                           [compojure "1.2.1"]
                           ;; We need version 2 of clout
                           [lib-noir "0.9.4" :exclusions [clout]]
                           [ring-server "0.3.1"]
                           [hiccup "1.0.5"]
                           [org.pingles/protobuf "0.7.3-2.5.0"]]
            :ring {:handler server-proxy-web.server/app}
            :plugins [[lein-ring "0.8.13"]
                      [lein-protobuf-cn "0.3.1"]
                      ;; This could probably be moved elsewhere
                      [cider/cider-nrepl "0.8.0-SNAPSHOT"]]
            :proto-path "resources/messages"
            :main server-proxy-web.server)

