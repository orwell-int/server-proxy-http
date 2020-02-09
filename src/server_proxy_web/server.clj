(ns server-proxy-web.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [server-proxy-web.views.orwell :as orwell]
            [server-proxy-web.views.admin :as admin]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]))


(defroutes app-routes
  (GET  "/router/:message" [message & params] (orwell/wrapper message params))
  (GET  "/admin/" [] (admin/admin))
  (POST "/admin/" [command] (admin/admin command))

  ;; Default routes
  (route/resources "/orwell/resources/")
  (route/not-found "Not found"))

(def app
  (handler/site app-routes))
