(ns battleship.utils-test
  (:require [cljs.test :refer (deftest are)]
            [three :as t]
            [battleship.utils :as u]))

(deftest convienience-functions
  (let [obj (t/Mesh.)]
    (u/pos obj 1 2 3)
    (are [a b] (= a b)
      1 (.-x (.-position obj))
      2 (.-y (.-position obj))
      3 (.-z (.-position obj)))))

(comment

  (convienience-functions)
  
  (def obj (t/Mesh.))

  (.-position obj)

  (= (.-x (t/Vector3. 0 0 0)) (.-x (.-position obj)))

  (u/pos obj 3 2 1)

  (.-x (.-position obj))
  (.-y (.-position obj))
  (.-z (.-position obj))
  
  *)
