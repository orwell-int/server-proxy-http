(ns server-proxy-web.server
  (:use [compojure.core]
        [noir.util.middleware :as mw])
  (:require [compojure.route :as route]
            [server-proxy-web.views.orwell :as orwell]))

(def app-routes
  [(GET "/orwell/router/:message" [message & params]
        (orwell/router message params))
   (route/resources "/orwell/resources/")
   (route/not-found "Not found")])

(def app
  (mw/app-handler app-routes))
