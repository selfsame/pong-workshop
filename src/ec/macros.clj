(ns ec.macros)

(defn- kw->str [s]
  (apply str (rest (str s))))

(defmacro dom [-tag -m & -c]
  (let [tag (str -tag)
        [m c] (if (map? -m) [-m -c] [{} (cons -c -m)])
        setcode (mapv (fn [[k v]] (list '.setAttribute '__el (kw->str k) v)) -m)
        c-e '.createElement]
  `(let [~'__el (~c-e ~'js/document ~tag)]
     ~@setcode
     ~'__el)))

(def passmap {'init '(init [o#])
              'update '(update [o#])
              'destroy '(destroy [o#])
              'draw '(draw [o#])
              'merge '(merge [a# b#])})

(defn pass-exant [code]
  (let [sym (first code)]
    (if (get passmap sym) {sym code} {})))

(defmacro C [sym -args & fns]
  (let [syms (str sym)
        symk (keyword syms)
        argmap (into {} (map (juxt keyword str) -args))
        fieldmap  (mapv keyword -args)
        args (vec (concat ['parent] -args))
        passfns (vals (conj passmap (into {} (map pass-exant fns))))]
  `(do
     (deftype ~sym ~args
      ~'IPrintWithWriter
        (~'-pr-writer [o# writer# opts#] (~'-write writer# (str "<" ~syms (vals o#) ">")))
      ~' ISeqable
        (~'-seq [this#] (map (fn [[k# v#]] [k# (aget this# v#)]) ~argmap))
      ~'ILookup
        (~'-lookup [this# k#]  (aget this# (get ~argmap k#)))
      ~'IAssociative
        (~'-contains-key? [_# k#] (~'-contains-key? ~argmap k#))
        (~'-assoc [this# k# v#] (aset this# (get ~argmap k#) v#))
      ~'ec.core/IComponent
        (~'-keyword [~'this] ~symk)
      ~'IFn
      (~'-invoke [o# e#]
         (~'ec.core/mount o# e#))
      ~'ec.core/IPass
       (~'clone [o#] (~'new ~sym ~@args))
        ~@passfns )

     (swap! ~'ec.core/CDATA update-in [~symk] merge
      {:type ~sym
       :field-keys ~fieldmap
       :mapstructor (fn [m#]
                      (let [{:keys ~-args} m#]
                         (~'new ~sym nil ~@-args)
                        ))}) ~sym)))





 (defmacro E [data]

     `(let [cmap# (atom (into {} (mapv ~'ec.core/map->compmap ~data)))
            comps# (into-array (vals @cmap#))
            e#
         (~'ec.core/Entity.
           (swap! ~'ec.core/UID inc)
           comps#
           cmap#)]
        (mapv (fn [c#] (~'aset c# "parent" e#)) comps#)
        e#))



