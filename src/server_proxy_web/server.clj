(ns server-proxy-web.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [server-proxy-web.views.orwell :as orwell]
            [server-proxy-web.views.admin :as admin]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]))


(defroutes app-routes
  (GET  "/orwell/html/router/:message" [message & params] (orwell/router-html message params))
  (GET  "/orwell/router/:message" [message & params] (orwell/router message params))
  (GET  "/orwell/admin/" [] (admin/admin))
  (POST "/orwell/admin/" [params] (admin/admin params))

  ;; Default routes
  (route/resources "/orwell/resources/")
  (route/not-found "Not found"))

(def app
  (wrap-defaults app-routes site-defaults))
