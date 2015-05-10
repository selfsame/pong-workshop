(ns ec.game
  (:use-macros
    [ec.macros :only [C E dom]])
  (:use [ec.core :only [e c ! init update draw destroy clone]])
	(:require
    [ec.draw]
    [clojure.browser.event :as event]))

(defn animation-frame [f] (.requestAnimationFrame js/window f))
(defn append-child [el node] (.appendChild el node))


(def CANVAS (atom nil))

(C pump [_]
  (update [me] (draw (e me)) (animation-frame #(update (e me)))))

(C canvas [element context width height]
  (init [me]
    (when-not @CANVAS
      (reset! CANVAS (dom canvas {:width width :height height}))
      (append-child (.-body js/document) @CANVAS))
    (assoc me :element @CANVAS)
    (assoc me :context (.getContext (:element me) "2d"))
   (reset! ec.draw/CTX (:context me)))
  (draw [me]

   (ec.draw/fill-style "rgba(0,0,0,0.1)")
   (ec.draw/fill-rect 0 0 (:width me)  (:height me))
   ))

(defonce TAGGED (atom {}))

(C tag [key]
  (init [me]
   (swap! TAGGED update-in [key] conj (e me))))

(defn find [k] (get @TAGGED k))


(C children [col]
 (init [me]
  (assoc me :col (to-array col))
  (.every col (fn [c] (init c) true)))
 (update [this]
  (.every col (fn [c] (update c) true)))
 (draw [this]
  (.every col (fn [c] (draw c) true))))

