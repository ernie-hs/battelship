(ns battleship.assets-test
  (:require [cljs.test :refer (testing deftest is)]
            [battleship.assets :as a]))

;; TODO look into async tests in cljs, or how do i re-ify?

(testing "loading assets"
  (let [items [{:name :my-font-1 :type :font :src "../fonts/gentilis_bold.typeface.json"}
               {:name :my-font-2 :type :font :src "../fonts/gentilis_regular.typeface.json"}
               {:name :my-model-1 :type :model :src "../models/ship.glb"}]
        bad-src {:name :my-font-3 :type :font :src "blah!"}
        bad-type {:name :my-font-4 :type :blah :src "blah blah!"}]

    
    (deftest load-assets-test
      (let [*a (a/load-all items)]
        (is (instance? cljs.core.Atom *a) "return an atom")
        (is (= (count items) (:count @*a))) "items count"
        (is (false? (:error @*a)) "no errors")))

    (deftest bad-asset-load-test
      (is (thrown? js/Error (a/load-all (conj items bad-type))) "bad asset type")
      (is (instance? cljs.core.Atom (a/load-all (conj items bad-src))) "should mark error for bad src"))))

(comment

  (identity false)

  *)
