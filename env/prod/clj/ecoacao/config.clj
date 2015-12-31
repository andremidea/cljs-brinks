(ns ecoacao.config
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[ecoacao started successfully]=-"))
   :middleware identity})
