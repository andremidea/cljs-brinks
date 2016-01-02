(ns ecoacao.models.project)


(defrecord Project [id
                    name
                    goals
                    argument
                    expected-results
                    approved
                    comments
                    user-id])

(defn create [map]
  (-> map
      (assoc :approved false)
      (map->Project)))

