(ns ifcalc.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :name
 (fn [db]
   (reaction (:name @db))))

(re-frame/register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))

(re-frame/register-sub
 :weight-change
 (fn [db [_]]
   (let [weight-amount (reaction (get-in @db [:measurements :weight :amount]))
         weight-unit   (reaction (get-in @db [:measurements :weight :unit]))]
     (reaction {:amount @weight-amount
                :unit   @weight-unit}))))
