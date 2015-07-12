(ns ifcalc.views
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
              [reagent.core :as reagent :refer [atom]]
              [ifcalc.lib :as lib]))

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

(defn bodyfat-input
  []
  (let [bodyfat    (subscribe [:bodyfat-change])
        percentage (reaction (get @bodyfat :percentage))
        pct        (atom @percentage)]
    (fn []
      [:div
       [:input {:type "text"
                :value @pct
                :on-change #(do (reset! pct (-> % .-target .-value))
                                (dispatch [:update-bodyfat-percentage @pct]))}]])))

(defn weight-output
  []
  (let [weight (subscribe [:weight-change])
        amount (reaction (get @weight :amount))
        unit   (reaction (get @weight :unit))]
    (fn []
      [:h2 @amount @unit])))

(defn bodyfat-pct-output
  []
  (let [bodyfat    (subscribe [:bodyfat-change])
        percentage (reaction (get @bodyfat :percentage))]
    (fn []
      [:h2 @percentage])))

(defn lbm-output
  []
  (let [weight  (subscribe [:weight-change])
        bodyfat (subscribe [:bodyfat-change])
        weight-amt  (reaction (get @weight :amount))
        weight-unit (reaction (get @weight :unit))
        bf-pct      (reaction (get @bodyfat :percentage))
        lbm         (reaction (lib/lbm @weight-amt @bf-pct))]
    (fn []
      [:div [:h4 "Lean body mass"]
            [:p @lbm]
            [:h4 "BMR"]
            [:p (lib/bmr @lbm @weight-unit)]])))


(defn home-panel []
  (let [name (subscribe [:name])]
    (fn []
      [:div
       [weight-input]
       [bodyfat-input]
       [weight-output]
       [bodyfat-pct-output]
       [lbm-output]
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
