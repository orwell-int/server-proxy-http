(ns server-proxy-web.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [server-proxy-web.views.orwell :as orwell]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/orwell/html/router/:message" [message & params] (orwell/router-html message params))
  (GET "/orwell/router/:message" [message & params] (orwell/router message params))
  (route/resources "/orwell/resources/")
  (route/not-found "Not found"))

(def app
  (wrap-defaults app-routes site-defaults))
