(ns appkit.reconciler
  #?(:clj
     (:require [appkit.citrus.core :as citrus])

     :cljs
     (:require [appkit.citrus.core :as citrus]
               [appkit.effects :as effects]
               [appkit.db      :as db]
               [appkit.storage :as storage]
               [goog.net.cookies])))

#?(:cljs
   (defn effect-handlers [api-host cookie-opts]
     (atom {:http          (partial effects/http api-host)
            :local-storage effects/local-storage
            :cookie        (partial effects/cookie cookie-opts)
            :redirect      effects/redirect
            :dispatch      effects/dispatch
            :timeout       effects/timeout})))

;; used by appkit.citrus
(def reconciler-inner (atom nil))

(defn reconciler [handler api-host cookie-opts debug?]
  (let [r #?(:clj
             (citrus/reconciler
               {:state     (atom {})})
             :cljs
             (citrus/reconciler
               {:state           db/state
                :handler         handler
                :effect-handlers (effect-handlers api-host cookie-opts)
                :debug? debug?}))]
    (reset! reconciler-inner r)
    r))
