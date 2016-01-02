(ns ecoacao.routes.project
  (:require [compojure.api.sweet :refer [defapi GET* POST* PUT* context*]]
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

(defn update-project [params]
    (-> params
        parse-project
       (db/to-db)
       (db/update-project!))
  (get-project (:id params)))

(defn get-project [id]
  (-> {:id (Integer/parseInt id)}
      (db/get-project)
      first
      parse-project))

(defn list-projects []
  (->> (db/list-projects)
       (map parse-project)))


(defapi project-routes
  (context* "/api" []
    (GET* "/project" [] {:body (list-projects)})
    (GET* "/project/:id" [id] {:body (get-project id)})
    (POST* "/project" {params :params}
           (let [project (create-project params)]
             (ok project)))
    (POST* "/project/:id" {params :params}
           (let [project (update-project params)]
             (ok project)))))
