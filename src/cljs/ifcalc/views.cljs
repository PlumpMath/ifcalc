(ns ifcalc.views
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
              [reagent.core :as reagent :refer [atom]]))

;; --------------------



(defn weight-input
  []
  (let [weight (subscribe [:weight-change])
        amount (reaction (get @weight :amount))
        unit   (reaction (get @weight :unit))
        amt    (atom @amount)]
    (fn []
      [:div
       [:input {:type "text"
                :value @amount
                :on-change #(do (reset! amt (-> % .-target .-value))
                                (dispatch [:update-value @amt [:measurements :weight :amount]]))}]
       [:input {:type "button"
                :value @unit
                :on-click #(dispatch [:convert-weight @amount @unit])}]])))

(defn weight-output
  []
  (let [weight (subscribe [:weight-change])
        amount (reaction (get @weight :amount))
        unit   (reaction (get @weight :unit))]
    (fn []
      [:h2 @amount @unit])))


(defn home-panel []
  (let [name (subscribe [:name])]
    (fn []
      [:div
       [weight-input]
       [weight-output]
       [:div [:a {:href "#/about"} "Go to About Page"]]])))

(defn about-panel []
  (fn []
    [:div "This is the About Page."
     [:div [:a {:href "#/"} "go to Home Page"]]]))

;; --------------------
(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (subscribe [:active-panel])]
    (fn []
      (panels @active-panel))))
