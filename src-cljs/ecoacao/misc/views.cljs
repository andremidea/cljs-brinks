(ns ecoacao.misc.views)

(defn row [label & input]
  [:div.row
   [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

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
