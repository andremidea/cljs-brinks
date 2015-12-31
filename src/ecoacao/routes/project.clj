(ns ecoacao.routes.project
  (:require [ecoacao.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [ecoacao.db.core :as db]))


(defroutes project-routes
  (GET "/project" [] (ok))
  (POST "/project" [name
                    goals
                    argument
                    expected_results]
    (db/create-project! name goals argument expected_results)))

