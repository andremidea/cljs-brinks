(ns ecoacao.components.user
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [ajax.core :refer [GET POST PUT]]
            [reagent-forms.core :refer [bind-fields]]
            [ecoacao.misc.views :refer [row text-area form-group]]
            [ecoacao.components.project :as project]
            [ecoacao.misc.http :as http]))

(defonce user (atom {}))

(defn fetch-data []
  (let [cuser (session/get :user)]
    (http/get-resource-by-id "user" (:id cuser) user)))

(defn user-profile []
  [:h1 (:name @user)])

(defn user-things []
  [:div.row
   [:div.row
    [:div.col-md-12
     [:div.page-header
      [:h2 "Projetos"]
      (project/projects-list (:projects @user))]]
    [:div.row
     [:div.col-md-12
      [:div.page-header
       [:h2 "Equipes"]
       [:table.table
        [:thead
         [:th "Nome"]]
        [:tbody
         (for [team (:teams @user)]
              [:tr
               [:td (:name team)]])]]]]]]])

(defn user-home []
  (fetch-data)
  (fn []
    [:div.container
     [:div.row
      [:div.col-md-4
       (user-profile)]
      [:div.col-md-8
       (user-things)]]]))




