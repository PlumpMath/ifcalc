(ns ifcalc.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame :refer [register-sub]]))

(register-sub
 :name
 (fn [db]
   (reaction (:name @db))))

(register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))

(register-sub
 :weight-change
 (fn [db [_]]
   (let [weight-amount (reaction (get-in @db [:measurements :weight :amount]))
         weight-unit   (reaction (get-in @db [:measurements :weight :unit]))]
     (reaction {:amount @weight-amount
                :unit   @weight-unit}))))

(register-sub
 :bodyfat-change
 (fn [db [_]]
   (let [bodyfat (reaction (get-in @db [:measurements :bodyfat]))]
     (reaction @bodyfat))))
