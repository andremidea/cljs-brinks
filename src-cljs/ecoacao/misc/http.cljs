(ns ecoacao.misc.http
  (:require [ajax.core :refer [GET POST PUT]]))

(defn get-resource [name at]
  (GET (str "/api/" name)
      {:response-format :transit
       :error-handler #(js/console.log "erro" %)
       :handler #(reset! at %)}))

(defn get-resource-by-id [name id at]
  (get-resource (str name "/" id) at))
