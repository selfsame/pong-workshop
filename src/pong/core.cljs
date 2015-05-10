(ns pong.core
  (:use-macros
    [ec.macros :only [C E dom]])
  (:use [ec.core :only [e c ! init update draw destroy clone CDATA]]
   [ec.game :only [find pump tag]])
  (:require
    [ec.draw :as d]))


(comment TODO
  [x] get e from c
    (e thing)
  [/] add and remove c's
    (mount c e) or ((c) e)
    (unmount c e)?
  [ ] duplicate CDATA entries
  )


(enable-console-print!)



(C position [x y])
(C scale [x y])
(C velocity [x y]
 (update [me]
    (set! (.-x (:position (e me))) (+ (.-x (:position (e me))) x))
    (set! (.-y (:position (e me))) (+ (.-y (:position (e me))) y))))

(C box [w h color]
  (draw [me]
    (d/fill-style color)
    (d/fill-rect (:x (:position (e me))) (:y (:position (e me))) w h)))

(C circle [r color]
  (draw [me]
   (d/fill-style color)
   (d/fill-rect (.-x (:position (e me))) (.-y (:position (e me))) 10 10)
   ))

(C ball []
  (update [me]
    (when-not (< 0 (.-y (:position (e me))) 480)
      (set! (.-y (:velocity (e me))) (- (.-y (:velocity (e me))))))
   (when-not (< 0 (.-x (:position (e me))) 640)
      (set! (.-x (:velocity (e me))) (- (.-x (:velocity (e me))))))))

(def game
  (E {:position {:x 5 :y 5}
    :tag {:key :game}
    :pump {}
    :canvas {:width 640 :height 480}
    :children {:col [
        (E {:position {:x 600 :y 240}
             :box {:w 5 :h 40 :color "red"}})
        (E {:position {:x 5 :y 240}
             :box {:w 5 :h 40 :color "orange"}})]}}))

(assoc (:children game) :col
  (concat (:col (:children game))
   (take 2000
    (repeatedly
      #(E {:tag {:key :ball}
           :ball {}
           :velocity {:x (+ (rand-nth [-2 2]) (rand)) :y (- (rand) 0.5)}
           :position {:x (+ (rand-int 60) 210) :y 240}
           :circle {:r 5 :color (str "rgb("(rand-int 255)","(rand-int 255)","(rand-int 255)")")}})))  ))



(init game)
(update game)


(find :ball)

