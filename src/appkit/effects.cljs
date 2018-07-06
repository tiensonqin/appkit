(ns appkit.effects
  (:require [appkit.citrus.core :as citrus]
            [appkit.api :as api]
            [appkit.storage :as storage]
            [appkit.cookie :as cookie]
            [goog.net.cookies]
            [clojure.string :as str]))

(defmulti dispatch! (fn [_ effect]
                      (type effect)))

(defmethod dispatch! Keyword [r event & args]
  (apply citrus/dispatch! r event args))

(defmethod dispatch! PersistentArrayMap [r effects & oargs]
  (doseq [[effect [event & args]] effects]
    (apply dispatch! r event (concat args oargs))))

(defmethod dispatch! :default [r & args]
  )

;; cookie ==========================================================

(defn cookie [opts r [op k v]]
  (case op
    ;; session
    :set         (cookie/cookie-set k v)
    :set-forever (cookie/cookie-set-forever k v opts)
    :remove      (cookie/cookie-remove k)))

;; http ==========================================================
(def refresh-times (atom 0))
(defn http [api-host r {:keys [endpoint params on-load on-error method type headers
                               on-progress on-upload on-download]
                        :or {method :post
                             type :transit
                             on-error :citrus/default-error
                             on-progress nil
                             on-upload nil
                             on-download nil
                             }
                        :as options}]
  (api/fetch api-host {:endpoint endpoint
                       :params   params
                       :method   method
                       :type     type
                       :headers  headers
                       :on-progress on-progress
                       :on-upload on-upload
                       :on-download on-download
                       :on-success (fn [result]
                                     (if (vector? on-load)
                                       (apply dispatch! r (conj on-load result))
                                       (dispatch! r on-load result)))
                       :on-error (fn [{:keys [status] :as resp}]
                                   (cond
                                     (= status 401)
                                     (citrus/dispatch! r :user/show-signin-modal?)

                                     :else
                                     (if (vector? on-error)
                                       (apply dispatch! r (conj on-error resp))
                                       (dispatch! r on-error resp))))
                       }))

(defmulti local-storage (fn [_ params] (:action params)))

(defmethod local-storage :get [r {:keys [key on-success on-error]}]
  (if-let [data (storage/get key)]
    (dispatch! r on-success data)
    (dispatch! r on-error)))

(defmethod local-storage :set [r {:keys [key value]}]

  (storage/set key value))

(defmethod local-storage :conj [_ {:keys [key value]}]
  (let [old (storage/get key)]
    (storage/set key (vec (remove nil? (distinct (conj old value)))))))

(defmethod local-storage :disj [_ {:keys [key value]}]
  (when-let [old (storage/get key)]
    (storage/set key (vec (remove #(= % value) old)))))

(defmethod local-storage :assoc [_ {:keys [key assoc-key assoc-value]}]
  (when-let [old (storage/get key)]
    (when (or (nil? old)
              (map? old))
      (storage/set key (assoc old assoc-key assoc-value)))))

(defmethod local-storage :dissoc [_ {:keys [key assoc-key]}]
  (when-let [old (storage/get key)]
    (when (map? old)
      (storage/set key (dissoc old assoc-key)))))

(defmethod local-storage :remove [_ {:keys [key]}]
  (storage/remove key))

(defmethod local-storage :default [r & args]
  (prn "local-storage debug: " {:args args}))

(defn redirect
  [r route]
  (citrus/dispatch! r :router/push route true))

(defn- set-timeout [t f]
  (js/setTimeout f t))

(defn timeout
  [r {:keys [duration events]}]
  (set-timeout duration
               (fn []
                 (apply citrus/dispatch! r events))))

(defn dispatch [r events]
  (let [events (if (and (vector? events)
                        (keyword? (first events)))
                 [events]
                 events)]
    (doseq [event-vector events]
      (apply citrus/dispatch! r event-vector))))

(defn dispatch-sync [r events]
  (let [events (if (and (vector? events)
                        (keyword? (first events)))
                 [events]
                 events)]
    (doseq [event-vector events]
      (apply citrus/dispatch-sync! r event-vector))))
