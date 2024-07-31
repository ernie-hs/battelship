(ns battleship.three
  (:require [clojure.spec.alpha :as s]
            [three :as t]
            ["three/addons/loaders/FontLoader.js" :refer [FontLoader]]
            ["three/addons/loaders/GLTFLoader.js" :refer [GLTFLoader]]))

;; three.js convienience/wrappers

(defprotocol Object3D
  (pos [this x y z] "set the position of the Object3D")
  (rot [this x y z] "set the rotation of the Object3D")
  (scale [this x y z] "set the scale of the Object3D"))

(extend-type t/Object3D
  Object3D
  (pos [this x y z]
    (.set (.-position this) x y z))
  (rot [this x y z]
    (.set (.-rotation this) x y z))
  (scale [this x y z]
    (.set (.-scale this) x y z)))


;; async. asset loading

(s/def :bs.asset/type #{:font :model})
(s/def :bs.asset/loader (fn [x] (instance? t/Loader x)))

(defn get-loader
  [type]
  {:pre [(s/valid? :bs.asset/type type)]
   :post [(s/valid? :bs.asset/loader %)]}
  (type {:font (FontLoader.)
         :model (GLTFLoader.)}))
                               
(defprotocol Loader
  (load [this url fn-success] [this url fn-success fn-monitor fn-error]))

(extend-type t/Loader
  Loader
  (load ([this url fn-success]
         (.load this url fn-success))
        ([this url fn-success fn-monitor fn-error]
         (.load this url fn-success fn-monitor fn-error))))

(comment

  (:font (:font "ernie"))

  (get-loader :model)
  
  (instance? t/Loader (FontLoader.))
  
  t/Loader
  
  (def s (t/Scene.))

  (pos s 1 2 3)

  (.-y (.-position s))
  
  *)
