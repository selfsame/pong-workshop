(ns ec.core)

(defonce UID (atom 0))
(defonce CDATA (atom {}))

(defn fast-iterate [col f]
  (let [c (count col)]
    (loop [i 0]
      (when (< i c)
        (f (get col i))
        (recur (inc i))))))


(defprotocol IComponent
  (-keyword [x]))

(defprotocol IPass
  (init [x])
  (update [x])
  (destroy [x])
  (draw [x])
  (clone [x])
  (merge [x y]))


(deftype Entity [uid comps data]
  IPrintWithWriter
  (-pr-writer [o writer opts] (-write writer (str "<E" uid ">")))
  ISeqable
  (-seq [this] (seq @data))
  ILookup
  (-lookup [this k] (get @data k))
  IAssociative
  (-contains-key? [_ k] (-contains-key? @data k))
  (-assoc [_ k v] (Entity. uid comps (-assoc @data k v)))
  IMap
  (-dissoc [_ k] (Entity. uid comps (-dissoc @data k)))

  IPass
  (init [o] (.every comps (fn [c] (init c) true)))
  (update [o] (.every comps (fn [c] (update c) true)))
  (destroy [o] (.every comps (fn [c] (destroy c) true)))
  (draw [o] (.every comps (fn [c] (draw c) true)))
  (clone [o] o)
  (merge [a b] a))


(defn map->compmap [[k m]]
  (if-let [{:keys [type mapstructor]} (get @CDATA k)]
    {k (mapstructor m)} {}))

(defn c [k m]
  (if-let [{:keys [type mapstructor]} (get @CDATA k)]
    (mapstructor m)))


(defn e [c] (.-parent c))

(defn mount [c e]
  (let [nc (clone c)]
    (aset nc "parent" e)
    (swap! (.-data e) conj {(-keyword c) nc}) ))

(def ! assoc)
