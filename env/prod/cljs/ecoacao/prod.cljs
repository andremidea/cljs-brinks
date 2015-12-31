(ns ecoacao.app
  (:require [ecoacao.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
