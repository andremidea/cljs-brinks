(ns ecoacao.misc
  (:require [clojure.string :as str]
            [clojure.walk :as walk]))

(defn replace-char
  [s from to exceptions]
  (if (contains? exceptions s) s (keyword (str/replace (name s) from to))))

(defn replace-char-gen
  ([from  to] (replace-char-gen from to #{}))
  ([from to exceptions]
   #(if (keyword? %) (replace-char % from to exceptions) %)))

(def underscore->dash-exceptions #{:_links :_id})

(defn dash->underscore [json-doc]
  (walk/postwalk (replace-char-gen \- \_) json-doc))

(defn underscore->dash [json-doc]
  (walk/postwalk (replace-char-gen \_ \- underscore->dash-exceptions) json-doc))

