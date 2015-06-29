(ns ifcalc.handlers
    (:require [re-frame.core :as re-frame]
              [ifcalc.db :as db]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(re-frame/register-handler
 :update-input-value
 (fn [db [_ input-value key-path]]
   (.log js/console key-path)))
