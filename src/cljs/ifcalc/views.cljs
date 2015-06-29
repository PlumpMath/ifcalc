(ns ifcalc.views
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

;; --------------------

(defn measurement-input [{:keys [amount unit path]}]
  (let [amt (atom amount)]
    (fn []
      [:div
       [:input {:type "text"
                :value @amt
                :on-change #(do (reset! amt (-> % .-target .-value))
                                (re-frame/dispatch [:update-value @amt [:measurements path :amount]]))}]
       [:input {:type "button"
                :value unit}]])))

(defn weight-input
  []
  (let [weight (re-frame/subscribe [:weight-change])
        amount (reaction (get @weight :amount))
        unit   (reaction (get @weight :unit))]
    (fn []
      [measurement-input {:amount @amount
                          :unit @unit
                          :path :weight}])))

(defn weight-output
  []
  (let [weight (re-frame/subscribe [:weight-change])
        amount (reaction (get @weight :amount))
        unit   (reaction (get @weight :unit))]
    (fn []
      [:h2 @amount])))


(defn home-panel []
  (let [name (re-frame/subscribe [:name])]
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
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      (panels @active-panel))))
