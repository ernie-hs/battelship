(ns battleship.three-test
  (:require [cljs.test :refer (testing deftest is)]
            [three :as t]
            [battleship.three :as bst]))

(testing "Object3D tests"
  (deftest pos-test
    (let [obj (t/Mesh.)]
      (bst/pos obj 1 2 3)
      (is (= 1 (.-x (.-position obj))) "x must be 1")
      (is (= 2 (.-y (.-position obj))) "y must be 2")
      (is (= 3 (.-z (.-position obj))) "z must be 3")))

  (deftest rot-test
    (let [obj (t/Mesh.)]
      (bst/rot obj 1 2 3)
      (is (= 1 (.-x (.-rotation obj))) "x must be 1")
      (is (= 2 (.-y (.-rotation obj))) "y must be 2")
      (is (= 3 (.-z (.-rotation obj))) "z must be 3")))

  (deftest scale-test
    (let [obj (t/Mesh.)]
      (bst/scale obj 1 2 3)
      (is (= 1 (.-x (.-scale obj))) "x must be 1")
      (is (= 2 (.-y (.-scale obj))) "y must be 2")
      (is (= 3 (.-z (.-scale obj))) "z must be 3"))))


