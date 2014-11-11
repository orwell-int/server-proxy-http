(ns server-proxy-web.views.orwell
  (:use [hiccup.core :as hiccup]))

;; Functions and variables
(defn todo-item [{:keys [id title due]}]
  (hiccup/html
   [:li {:id id}
    [:h3 title]
    [:span.due due]]))

(defn todos-list [items]
  (hiccup/html
   [:ul#todoItems (map todo-item items)]))

(def all-todos
  [{:id "Id" :title "Help" :due "Yoo"}
   {:id "Other" :title "A title" :due "today"}])

(defn add-todo
  [title due]
  (println "Adding todo"))

;; Create a page that lists out all our to-dos
(defn index-page [arg]
  (println arg)
  (let [items all-todos]
    (hiccup/html
     [:head [:title "Orwell"]]
     [:h1 "Todo list!"]
     (todos-list items))))
