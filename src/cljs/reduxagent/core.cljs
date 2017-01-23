(ns reduxagent.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))



(enable-console-print!)

(defn counter-reducer
  [state action]
  (let [st (or state 0)]
    (case (:type action)
      :increment (+ state 1)
      :decrement (- state 1)
      state)))


;; Mimic of Redux 
(defn create-store
  [reducer]
  (let [state (atom nil)
        listeners (atom [])]
    {:get-state (fn [] @state)
     :dispatch (fn [action]
                (reset! state (reducer @state action))
                (doseq [listener @listeners] (listener)))
     :subscribe (fn [listener]
                  (swap! listeners conj listener))
     :unsubscribe (fn [listener]
                    (swap! listeners
                           (fn [x]
                             (remove #(= listener %) x))))}))


(def store (create-store counter-reducer))

;; -------------------------
;; Views

(defn counter [value on-increment on-decrement]
  [:div
   [:h1 value]
   [:button {:on-click on-increment} "+"]
   [:button {:on-click on-decrement} "-"]])


(defn home-page []
  [:div
   [counter ((:get-state store))
    #((:dispatch store) {:type :increment})
    #((:dispatch store) {:type :decrement})]])

((:subscribe store) home-page)

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
