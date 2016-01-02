(ns ecoacao.routes.user
  (:require [compojure.api.sweet :refer [defapi GET* POST* PUT* context*]]
            [ecoacao.db.core :as db]
            [ecoacao.misc :as misc]
            [ecoacao.models.user :as user]
            [ecoacao.models.project :as project]
            [ring.util.http-response :refer [ok]]))

(defn get-user-projects [id]
  (->> (db/get-user-projects {:id id})
       (misc/underscore->dash)
       (map project/map->Project)))


(defn get-user-teams [id]
  [{:id 1 :name "Team1"}])

(defn get-user [id]
  (-> (db/get-user {:id id})
      first
      (assoc :projects (get-user-projects id))
      (assoc :teams (get-user-teams id))
      misc/underscore->dash
      user/map->User))

(defapi routes
  (context* "/api/user" []
            (GET* "/:id" [id]
                  (ok (get-user id)))))
