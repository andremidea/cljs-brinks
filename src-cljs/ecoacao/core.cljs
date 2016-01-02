(ns ecoacao.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [reagent-forms.core :refer [bind-fields]])
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  [:li {:class (when (= page (session/get :page)) "active")}
   [:a {:href uri
        :on-click #(reset! collapsed? true)}
    title]])

(defn navbar []
  (let [collapsed? (atom true)]
    (fn []
      [:nav.navbar.navbar-inverse.navbar-fixed-top
       [:div.container
        [:div.navbar-header
         [:button.navbar-toggle
          {:class         (when-not @collapsed? "collapsed")
           :data-toggle   "collapse"
           :aria-expanded @collapsed?
           :aria-controls "navbar"
           :on-click      #(swap! collapsed? not)}
          [:span.sr-only "Toggle Navigation"]
          [:span.icon-bar]
          [:span.icon-bar]
          [:span.icon-bar]]
         [:a.navbar-brand {:href "#/"} "ecoacao"]]
        [:div.navbar-collapse.collapse
         (when-not @collapsed? {:class "in"})
         [:ul.nav.navbar-nav
          [nav-link "#/" "Home" :home collapsed?]
          [nav-link "#/about" "About" :about collapsed?]
          [nav-link "#/new-project" "Novo Projeto" :new-project collapsed?]
          [nav-link "#/projects" "Listar Projetos" :projects collapsed?]]]]])))

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     "this is the story of ecoacao... work in progress"]]])

(defn home-page []
  [:div.container
   [:div.jumbotron
    [:h1 "Welcome to ecoacao"]
    [:p "Time to start building your site!"]
    [:p [:a.btn.btn-primary.btn-lg {:href "http://luminusweb.net"} "Learn more »"]]]
   [:div.row
    [:div.col-md-12
     [:h2 "Welcome to ClojureScript"]]]
   (when-let [docs (session/get :docs)]
     [:div.row
      [:div.col-md-12
       [:div {:dangerouslySetInnerHTML
              {:__html (md->html docs)}}]]])])

(defn form-group
  ([id type placeholder label v k]
   (form-group id type placeholder label v k :input))

  ([id type placeholder label v k input-type]
   [:div {:class "form-group"}
    [:label {:for id} label]
    [input-type {:type type, :class "form-control", :id id, :placeholder placeholder :value (k @v) :on-change #(swap! v assoc k (-> % .-target .-value))}]]))

(defn text-area
  [id placeholder label rows v k]
   [:div {:class "form-group"}
    [:label {:for id} label]
    [:textarea {:class "form-control", :id id, :placeholder placeholder :rows rows :value (k @v) :on-change #(swap! v assoc k (-> % .-target .-value))}]])


(defn new-project-page []
  (let [project (atom {:name "" :goals "" :argument "" :expected-results ""})
        save-project (fn []
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

(defn row [label & input]
  [:div.row
   [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

(def project-form
  [:div
    (row "Nome" [:input.form-control {:field :text :id :name :readOnly true}])
    (row "Aprovado?"
         [:input.radio-inline {:field :radio :value true :name :approved} "Sim"]
         [:input.radio-inline {:field :radio :value false :name :approved} "Não"])])

(defn project-page []
  (let [project (atom (session/get! :project))
        id (session/get :project-id)]
    (when (nil? (:id @project))
      (prn (str @project))
      (GET (str "/api/project/" id)
          {:response-format :transit
           :handler #(reset! project %)}))
    (fn []
      [:div.container
       [:div.page-header "Moderar Projeto"]
        [bind-fields project-form project]
        [:button.btn.btn-default {:on-click #(js/console.log (str @project))} "Salvar"]])))

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
         [:table.table
          [:thead
           [:th "Nome"][:th "Objetivo"][:th "Justificativa"][:th "Resultados Esperados"]]
          [:tbody
           (for [project @projects]
             ^{:key project}
             [:tr
              [:td (:name project)]
              [:td (:goals project)]
              [:td (:argument project)]
              [:td (:expected-results project)]
              [:td [:button {:on-click #(secretary/dispatch! (str "/project/" (:id project)))} "Detalhes"]]])]] ] ] ])))


(def pages
  {:home #'home-page
   :about #'about-page
   :new-project new-project-page
   :project project-page
   :projects projects-page})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

(secretary/defroute "/about" []
  (session/put! :page :about))

(secretary/defroute "/new-project" []
  (session/put! :page :new-project))

(secretary/defroute "/project/:id" [id]
  (session/put! :project-id id)
  (session/put! :page :project))

(secretary/defroute "/projects" []
  (session/put! :page :projects))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
              (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET (str js/context "/docs") {:handler #(session/put! :docs %)}))

(defn mount-components []
  (reagent/render [#'navbar] (.getElementById js/document "navbar"))
  (reagent/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
