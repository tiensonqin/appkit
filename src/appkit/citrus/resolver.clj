(ns appkit.citrus.resolver)

(deftype Resolver [state path reducer]

  clojure.lang.IDeref
  (deref [_]
    (if-let [result (get-in @state path)]
      (if reducer (reducer result) result)
      nil))

  clojure.lang.IRef
  (setValidator [this vf]
    (throw (UnsupportedOperationException. "citrus.resolver.Resolver/setValidator")))

  (getValidator [this]
    (throw (UnsupportedOperationException. "citrus.resolver.Resolver/getValidator")))

  (getWatches [this]
    (throw (UnsupportedOperationException. "citrus.resolver.Resolver/getWatches")))

  (addWatch [this key callback]
    this)

  (removeWatch [this key]
    this))

(defn make-resolver [state path reducer]
  (Resolver. state path reducer))
