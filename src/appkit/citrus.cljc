(ns appkit.citrus
  (:require [appkit.citrus.core :as citrus]
            [appkit.reconciler :as r]
            [rum.core :as rum]))

(defn dispatch!
  "Invoke an event on particular controller asynchronously

    (citrus/dispatch! :user/load \"id\")

  Arguments

    event      - a dispatch value of a method defined in the controller
    args       - arguments to be passed into the controller"
  [event & args]
  (apply citrus/dispatch! @r/reconciler-inner event args))

(defn dispatch-sync!
  "Invoke an event on particular controller synchronously

    (citrus/dispatch-sync! :users/load \"id\")

  Arguments

   event      - a dispatch value of a method defined in the controller
    args       - arguments to be passed into the controller"
  [event & args]
  (apply citrus/dispatch-sync! @r/reconciler-inner event args))

(defn subscription
  "Create a subscription to state updates

    (citrus/subscription [:users 0] (juxt [:fname :lname]))

  Arguments

    path       - a vector which describes a path into reconciler's atom value
    reducer    - an aggregate function which computes a materialized view of data behind the path"
  ([path]
   (subscription (if (vector? path) path [path]) identity))
  ([path reducer]
   (citrus/subscription @r/reconciler-inner (if (vector? path) path [path]) reducer)))

(defn react
  [path]
  (rum/react (subscription path)))
