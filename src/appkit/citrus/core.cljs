(ns appkit.citrus.core
  (:require [appkit.citrus.reconciler :as r]
            [appkit.citrus.cursor :as c]))

(defn reconciler
  "Creates an instance of Reconciler

    (citrus/reconciler {:state (atom {})
                        :handler handler
                        :effect-handlers {:http effects/http}
                        :batched-updates f
                        :chunked-updates f})

  Arguments

    config              - a map of
      state             - app state atom
      handler           - event handler
      effect-handlers   - a hash of effects handlers
      batched-updates   - a function used to batch reconciler updates, defaults to `js/requestAnimationFrame`
      chunked-updates   - a function used to divide reconciler update into chunks, doesn't used by default

  Returned value supports deref, watches and metadata.
  The only supported option is `:meta`"
  [{:keys [state handler effect-handlers batched-updates chunked-updates]} & {:as options}]
  (r/Reconciler.
    handler
    effect-handlers
    state
    (volatile! [])
    (volatile! nil)
    (or batched-updates js/requestAnimationFrame)
    chunked-updates
    (:meta options)))

(defn dispatch!
  "Invoke an event on particular controller asynchronously

    (citrus/dispatch! reconciler :users :load \"id\")

  Arguments

    reconciler - an instance of Reconciler
        event      - a dispatch value of a method defined in the controller
    args       - arguments to be passed into the controller"
  [reconciler event & args]
  (r/dispatch! reconciler event args))

(defn dispatch-sync!
  "Invoke an event on particular controller synchronously

    (citrus/dispatch! reconciler :users :load \"id\")

  Arguments

    reconciler - an instance of Reconciler
    event      - a dispatch value of a method defined in the controller
    args       - arguments to be passed into the controller"
  [reconciler event & args]
  (r/dispatch-sync! reconciler event args))

(defn subscription
  "Create a subscription to state updates

    (citrus/subscription reconciler [:users 0] (juxt [:fname :lname]))

  Arguments

    reconciler - an instance of Reconciler
    path       - a vector which describes a path into reconciler's atom value
    reducer    - an aggregate function which computes a materialized view of data behind the path"
  ([reconciler path]
   (subscription reconciler path identity))
  ([reconciler path reducer]
   (c/reduce-cursor-in reconciler path reducer)))
