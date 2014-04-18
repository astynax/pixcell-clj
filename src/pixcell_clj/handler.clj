(ns pixcell-clj.handler
  (:use compojure.core)
  (:require [pixcell-clj.editor :as editor])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/" [state op arg] (editor/ui state op arg))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
