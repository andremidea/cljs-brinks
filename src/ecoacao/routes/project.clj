(ns ecoacao.routes.project
  (:require [compojure.api.sweet :refer [defapi GET* POST*]]
            [ecoacao.db.core :as db]
            [ecoacao.layout :as layout]
            [ecoacao.misc :as misc]
            [ecoacao.models.project :as project]
            [ring.util.http-response :refer [ok]]))

(defn parse-project [map]
  (project/map->Project (misc/underscore->dash map)))

(defn create-project [params]
  (-> params
      (project/create)
      (db/to-db)
      (db/create-project<!)
      parse-project))

(defn get-project [id]
  (-> {:id (Integer/parseInt id)}
      (db/get-project)
      first
      parse-project))

(defn list-projects []
  (->> (db/list-projects)
       (map parse-project)))


(defapi project-routes
  (GET* "/api/project" [] {:body (list-projects)})
  (GET* "/api/project/:id" [id] {:body (get-project id)})
  (POST* "/api/project" {params :params}
         (let [project (create-project params)]
           (ok project))))
