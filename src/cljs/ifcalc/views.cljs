(ns ifcalc.views
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

;; --------------------

(defn measurement-input
  [amount unit data-key]
  (fn []
    [:div
     [:input {:type "text"
              :value amount
              :on-change #(re-frame/dispatch
                           [:update-input-value amount [:measurements data-key :amount]])}]
     [:input {:type "button"
              :value unit}]]))

(defn weight-input
  []
  (let [weight (re-frame/subscribe [:weight-change])
        amount (reaction (get @weight :amount))
        unit   (reaction (get @weight :unit))]
    (fn []
      [measurement-input @amount @unit :weight])))


(defn home-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:div [weight-input]
       [:div [:a {:href "#/about"} "go to About Page"]]])))

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
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      (panels @active-panel))))
