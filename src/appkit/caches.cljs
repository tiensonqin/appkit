(ns appkit.caches
  (:require [appkit.promise :as p]))

(defn clear
  [cb]
  (-> (.keys js/caches)
      (p/then (fn [keys]
                (p/all
                 (doall
                  (map (fn [key]
                         (prn "[ServiceWorker] Removing old cache " key)
                         (if key (.delete js/caches key)))
                    keys)))
                (if cb (cb))))))
