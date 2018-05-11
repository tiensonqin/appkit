(ns appkit.citrus.core
  (:require [appkit.citrus.resolver :as r]))

(defn reconciler
  "Accepts a hash of `:state` atom & `:resolvers` hash of subscription resolvers where keys are subscription path vectors and values are data resolving functions

    {:state (atom {})}

  Returns a hash of `resolvers` and `state` atom which will be populated with resolved subscriptions data during rendering"
  [{:keys [state]}]
  {:state state})

(defn dispatch!
  "dummy dispatch!"
  [_ _ _ & _])

(defn dispatch-sync!
  "dummy dispatch-sync!"
  [_ _ _ & _])

(defn broadcast!
  "dummy broadcast!"
  [_ _ & _])

(defn broadcast-sync!
  "dummy broadcast-sync!"
  [_ _ & _])

(defn subscription
  "Create a subscription to state updates

    (citrus/subscription reconciler [:users 0] (juxt [:fname :lname]))

  Arguments

    reconciler - reconciler hash
    path       - a vector which describes a path into resolver's result value
    reducer    - an aggregate function which computes a materialized view of data behind the path"
  ([reconciler path]
   (subscription reconciler path nil))
  ([{:keys [state]} path reducer]
   (r/make-resolver state path reducer)))
