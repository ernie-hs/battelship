(ns battleship.assets-test
  (:require [cljs.test :refer (testing deftest is)]
            [battleship.assets :as a]))

(testing "asset atom creation"

  (deftest load-assets-test
    (let [items {:my-font1 {:type :font :src "../fonts/gentilis_bold.typeface.json"}
                 :my-font2 {:type :font :src "../fonts/gentilis_regular.typeface.json"}}
          *assets (a/load-assets items)]
      (is (instance? cljs.core.Atom *assets) "must return an asset atom")
      (is (object? (-> @*assets :items :my-font1 :obj)) "must load a font")
      (is (object? (-> @*assets :items :my-font2 :obj)) "must load a font")
      (is (true? (:ready @*assets)) "all assets loaded, so ready")) 3000))


(comment





  *)
