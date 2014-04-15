(ns pixcell-clj.handler
  (:use compojure.core)
  (:require [pixcell-clj.editor :as editor])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/" [state op] (editor/ui state op))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
