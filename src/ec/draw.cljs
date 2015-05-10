(ns ec.draw)

(def CTX (atom nil))


(defn fill-style [color] (aset @CTX "fillStyle" color))
(defn fill-rect [x y w h] (.fillRect @CTX x y w h))
(defn begin-path [] (.beginPath @CTX))
(defn close-path [] (.closePath @CTX))
(defn fill [] (.fill @CTX))
(defn arc [x y r start end] (.arc @CTX x y r start end true))
(defn fill-circle [x y r]
  (begin-path)
  (arc x y r 0 (* 2  (.-PI js/Math)))
  (close-path)
  (fill))
