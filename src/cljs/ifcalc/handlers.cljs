(ns ifcalc.handlers
    (:require [re-frame.core :as re-frame :refer [register-handler]]
              [ifcalc.db :as db]
              [ifcalc.lib :as lib :refer [lb-to-kg
                                          kg-to-lb]]))

(register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(register-handler
 :update-value
 (fn [db [_ value path]]
   (assoc-in db path value)))

(register-handler
  :convert-weight
  (fn [db [_ amount unit]]
    (let [updated-amount (if (= unit "lb")
                           (lb-to-kg amount)
                           (kg-to-lb amount))
          updated-unit (if (= unit "kg") "lb" "kg")]
      (assoc-in db [:measurements :weight] {:amount updated-amount
                                            :unit updated-unit}))))

(register-handler
 :update-bodyfat-percentage
 (fn [db [_ pct]]
   (assoc-in db [:measurements :bodyfat :percentage] pct)))


(register-handler
 :update-weight-amount
 (fn [db [_ amount]]
   (assoc-in db [:measurements :weight :amount] amount)))
