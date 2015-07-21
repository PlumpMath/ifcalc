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
      [:div.input-group
       [:input.form-control {:type "number"
                             :value @amount
                             :on-change #(do (reset! amt (-> % .-target .-value))
                                             (dispatch [:update-weight-amount @amt]))}]
       [:span.input-group-btn
         [:input.btn.btn-default {:type "button"
                                  :value @unit
                                  :on-click #(dispatch [:convert-weight @amount @unit])}]]])))

(defn bodyfat-input
  []
  (let [bodyfat    (subscribe [:bodyfat-change])
        percentage (reaction (get @bodyfat :percentage))
        pct        (atom @percentage)]
    (fn []
      [:div.input-group
       [:input.form-control {:type "number"
                             :value @pct
                             :on-change #(do (reset! pct (-> % .-target .-value))
                                             (dispatch [:update-bodyfat-percentage @pct]))}]
       [:span.input-group-addon "%"]])))

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
      [:h1 @percentage])))

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

(defn form
  []
  (fn []
    [:form.form-horizontal
     [:div.row
      [:div.form-group [:label.col-sm-6.control-label "Weight"]
                       [:div.col-sm-6 [weight-input]]]]
     [:div.row
      [:div.form-group [:label.col-sm-6.control-label "Bodyfat %"]
                       [:div.col-sm-6 [bodyfat-input]]]]]))

(defn draw-weight-graph
  [d]
  (let [[lbm fat-mass unit] (reagent/children d)]
    (.addGraph js/nv (fn []
                       (let [chart (.. js/nv -models pieChart
                                     (x #(.-label %))
                                     (y #(.-value %))
                                     (showLabels true))]
                       (let [weight-data [{:label "LBM" :value lbm} {:label "Fat Mass" :value fat-mass}]]
                         (.. js/d3 (select "#weight-graph svg")
                                   (datum (clj->js weight-data))
                                   (call chart))))))))

(defn weight-graph-placeholder []
  [:section#weight-graph
   [:svg]])

(def weight-graph (reagent/create-class {:reagent-render weight-graph-placeholder
                                         :component-did-mount draw-weight-graph
                                         :component-did-update draw-weight-graph}))

(defn weight-graph-container
  []
  (let [weight  (subscribe [:weight-change])
        bodyfat (subscribe [:bodyfat-change])
        weight-amount (reaction (get @weight :amount))
        weight-unit   (reaction (get @weight :unit))
        bf-percentage (reaction (get @bodyfat :percentage))
        lbm           (reaction (lib/lbm @weight-amount @bf-percentage))
        fat-mass      (reaction (- @weight-amount @lbm))]
    (fn []
      [weight-graph @lbm @fat-mass "lb"])))

(defn home-panel []
  (let [name (subscribe [:name])]
    (fn []
      [:div.container-fluid
       [:div.row
         [:div.col-sm-4
          [form]]
         [:div.col-sm-8
          [weight-graph-container]]]
        [:div.row
         [:div.col-sm-12
          [weight-output]
          [bodyfat-pct-output]
          [lbm-output]
          [:div [:a {:href "#/about"} "Go to About Page"]]]]])))

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
