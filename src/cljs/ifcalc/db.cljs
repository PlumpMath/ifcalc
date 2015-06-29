(ns ifcalc.db)

(def default-db
  {:name "re-frame"
   :measurements {:weight {:amount 0
                           :unit "lb"}
                  :height {:amount 0
                           :unit "in"}}})
