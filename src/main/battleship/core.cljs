(ns battleship.core
  (:require [three :as t]
            [battleship.three :as bst]
            [battleship.events :as e]
            ["three/addons/controls/ArcballControls.js" :refer [ArcballControls]]
            ["three/addons/geometries/TextGeometry.js" :refer [TextGeometry]]))

(defonce canvas (.querySelector js/document "#grid"))
(defonce renderer (t/WebGLRenderer. (js-obj "canvas" canvas
                                            "antialias" true)))
(defonce camera (t/PerspectiveCamera. 45 1.3 0.1 1000))
 
(e/listen js/window "resize"
          (fn [_]
            (let [w (.-innerWidth js/window)
                  h (.-innerHeight js/window)
                  a (/ w h)]
              (.setPixelRatio renderer (.-devicePixelRatio js/window))
              (.setSize renderer w h)
              (set! (.-aspect camera) a)
              (.updateProjectionMatrix camera))))

(defn get-text
  [font text size depth color]
  (let [geometry (TextGeometry. text (js-obj "font" font "size" size "depth" depth))
        material (t/MeshPhysicalMaterial. (js-obj "color" color))]
    (.computeBoundingBox geometry)
    (let [bbox (.-boundingBox geometry)]
      (.translate geometry
                  (* -0.5 (- (.-x (.-max bbox)) (.-x (.-min bbox))))
                  (* -0.5 (- (.-y (.-max bbox)) (.-y (.-min bbox))))
                  (* -0.5 (- (.-z (.-max bbox)) (.-z (.-min bbox)))))
      (t/Mesh. geometry material))))

(defn title-scene
  [_]
  (let [scene (t/Scene.)
        light (t/PointLight. "white" 10000)
        dir-light (t/DirectionalLight. "white" 1)]
    (bst/pos light 20 30 40)
    (.add scene light)
    (bst/pos dir-light -4 -4 10)
    (.add scene dir-light)
    (bst/load
     (bst/get-loader :font)
     "fonts/helvetiker_bold.typeface.json"
     (fn [font]
       (let [title (get-text font "BATTLESHIT" 3 1 "red")
             gabor (get-text font ":gabor" 1 0.5 "green")
             anykey (get-text font "press anykey" 1 0.5 "yellow")]           
         (bst/pos title 0 7 0)
         (.add scene title)
         (bst/pos gabor 10 4.5 1)
         (.add scene gabor)
         (bst/pos anykey 0 -5 0)
         (.add scene anykey)
         (bst/pos camera 0 1.5 30))))
    (bst/load
     (bst/get-loader :model)
     "models/ship.glb"
     (fn [model]
       (let [boat (.-scene model)]
         (bst/scale boat 2 2 2)
         (bst/pos boat 0 -1 0)
         (.add scene boat)
         (.setAnimationLoop renderer (fn [_]
                                       (let [y (.-y (.-rotation boat))]
                                         (bst/rot boat 0 (+ y 0.01) 0)
                                         (.render renderer scene camera)))))))))

(defn players
  [_]
  (let [scene (t/Scene.)
        light (t/PointLight. "white" 10000)
        dir-light (t/DirectionalLight. "white" 1)]
    (bst/pos light 20 30 40)
    (.add scene light)
    (bst/pos dir-light -2 -2 10)
    (.add scene dir-light)
    (bst/load
     (bst/get-loader :font)
     "fonts/helvetiker_bold.typeface.json"
     (fn [font]
       (let [title (get-text font "YOU SUCK!" 3 1 "orange")]
         (bst/pos title 0 0 0)
         (.add scene title)
         (bst/pos camera 0 0 20)
         (.setAnimationLoop renderer (fn [_] (.render renderer scene camera))))))))

;; shadow-cljs stuff

(defn init []
  (e/listen canvas "title-scene" title-scene)
  (e/listen canvas "players" players)
  (.dispatchEvent js/window (js/Event. "resize"))
  (e/dispatch canvas "title-scene")
  (js/console.log "init"))

(defn start []
  (e/listen canvas "title-scene" title-scene)
  (js/console.log "start"))

(defn stop []
  (.removeEventListener canvas "title-scene" title-scene false)
  (.removeEventListener canvas "players" players false)
  (js/console.log "stop"))


(comment

  *)
