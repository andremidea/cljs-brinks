(ns ecoacao.components.project
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [ajax.core :refer [GET POST PUT]]
            [reagent-forms.core :refer [bind-fields]]
            [ecoacao.misc.views :refer [row text-area form-group]]))

(defn new-project-page []
  (let [user (session/get :user)
        project (atom {:name "" :goals "" :argument "" :expected-results "" :user-id (:id user)})
        save-project (fn []
                       (js/console.log @project)
                       (POST "/api/project"
                           {:params @project
                            :error-handler #(js/console.log "error" %)
                            :response-format :transit
                            :handler (fn [response]
                                       (session/put! :project response)
                                       (secretary/dispatch! (str "/project/" (:id response))))}))]
    (fn []
      [:form
       (form-group "name" "text" "Qual o nome do Projeto?" "Nome do Projeto: " project :name)
       (text-area "goals" "Qual o Objetivo do Projeto?" "Objetivo:" 3 project :goals)
       (text-area "argument" "Porque esse projeto existe?" "Justificativa:" 3 project :argument)
       (text-area "expected" "Quais os Resultados Esperados?" "Resultados Esperados:" 3 project :expected-results)
       [:button {:class "btn btn-default" :on-click save-project} "Cadastrar" ]])))


(def project-form
  [:div
    (row "Nome" [:input.form-control {:field :text :id :name :readOnly true}])
    (row "Aprovado?"
         [:input.radio-inline {:field :radio :value true :name :approved} "Sim"]
         [:input.radio-inline {:field :radio :value false :name :approved} "Não"])
   (row "Comentários"
        [:textarea.form-control {:field :textarea :id :comments :rows 3}])])

(defn project-page []
  (let [project (atom (session/get! :project))
        id (session/get :project-id)
        update-project (fn []
                         (POST (str "/api/project/" (:id @project))
                             {:params @project
                              :error-handler #(js/console.log "error" %)
                              :response-format :transit
                              :handler (fn [response]
                                         (session/put! :project response)
                                         (secretary/dispatch! (str "/project/" (:id response))))})) ]
    (when (nil? (:id @project))
      (prn (str @project))
      (GET (str "/api/project/" id)
          {:response-format :transit
           :handler #(reset! project %)}))
    (fn []
      [:div.container
       [:div.page-header "Moderar Projeto"]
        [bind-fields project-form project]
        [:button.btn.btn-default {:on-click update-project} "Salvar"]])))

(defn projects-list [projects]
  [:table.table
   [:thead
    [:th "Nome"][:th "Objetivo"][:th "Justificativa"][:th "Resultados Esperados"]]
   [:tbody
    (for [project projects]
      ^{:key project}
      [:tr
       [:td (:name project)]
       [:td (:goals project)]
       [:td (:argument project)]
       [:td (:expected-results project)]
       [:td [:button {:on-click #(secretary/dispatch! (str "/project/" (:id project)))} "Detalhes"]]])]])

(defn projects-page []
  (let [projects (atom [])]
    (fn []
      (when (empty? @projects)
        (GET "/api/project"
          {:response-format :transit
           :error-handler #(js/console.log "erro" %)
           :handler #(reset! projects %)}))
      [:div.container
       [:div.row
        [:div.col-md-12
          (projects-list @projects)] ] ])))
