(ns ecoacao.models.project)


(defrecord Project [id
                    name
                    goals
                    argument
                    expected-results
                    approved
                    comments])

(defn create [map]
  (-> map
      assoc :authorized false
      (map->Project)))

