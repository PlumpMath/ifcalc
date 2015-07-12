(ns ifcalc.lib)

(defn lb-to-kg [amount]
  (/ amount 2.2))

(defn kg-to-lb [amount]
  (* amount 2.2))

(defn bmr-from-lb
  "Katch-McArdle Formula for calculating basal metabolic rate."
  [lbm-lb]
  (+ 370 (* 9.79759519 lbm-lb)))

(defn bmr-from-kg
  "Katch-McArdle Formula for calculating basal metabolic rate."
  [lbm-kg]
  (+ 370 (* 21.6 lbm-kg)))

(defn bmr [lbm unit]
  "Katch-McArdle Formula for calculating basal metabolic rate."
  (if (= unit "lb")
    (bmr-from-lb lbm)
    (bmr-from-kg lbm)))

(defn fat-mass [weight bf-pct]
  "Calculate fat mass based on weight and bodyfat percentage."
  (* weight (/ bf-pct 100)))

(defn lbm [weight bf-pct]
  "Calculate lbm based on weight and bodyfat percentage."
  (- weight (fat-mass weight bf-pct)))
