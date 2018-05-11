(ns appkit.cookie
  (:require [goog.net.cookies]
            [clojure.string :as str]))

(defn- as-key [k]
  (cond
    (string? k)  k
    (keyword? k) (name k)
    :else        (str k)))

(defn cookie-get [k]
  (.get goog.net.cookies (as-key k)))

(defn cookie-set [k v]
  (.set goog.net.cookies k v))

(defn cookie-set-forever [k v {:keys [expire domain]
                               :or {expire (* 2 365 24 3600)}
                               :as opts}]
  (.set goog.net.cookies (as-key k) v expire "/" domain))

(defn cookie-remove [k]
  (.remove goog.net.cookies (as-key k)))
