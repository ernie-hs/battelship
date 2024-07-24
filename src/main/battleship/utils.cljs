(ns battleship.utils
  (:require [three :as t]))

(defprotocol Convienience
  (pos [this x y z] "set the position of the Object3D")
  (rot [this x y z] "set the rotation of the Object3D"))

(extend-type t/Object3D
  Convienience
  (pos [this x y z]
    (.set (.-position this) x y z))
  (rot [this x y z]
    (.set (.-rotation this) x y z)))

(comment

  (def s (t/Scene.))

  (pos s 1 2 3)

  (.-y (.-position s))

  *)
