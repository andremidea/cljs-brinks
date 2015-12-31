(ns ecoacao.routes.project
  (:require [ecoacao.layout :as layout]
            [compojure.api.sweet :refer [defapi GET* POST*]]
            [ring.util.http-response :refer [ok]]
            [ecoacao.db.core :as db]))


(defapi project-routes
  (GET* "/api/project" [] {:body (db/list-projects)})
  (GET* "/api/project/:id" [id] {:body (first (db/get-project {:id (Integer/parseInt id)}))})
  (POST* "/api/project" {params :params}
         (let [project (db/create-project<! params)]
           (ok project))))

