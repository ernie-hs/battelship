(ns battleship.assets-test
  (:require [cljs.test :refer (testing deftest is)]
            [battleship.assets :as a]))

(testing "asset atom creation"
  (let [items {:my-font1 {:type :font :src "../fonts/gentilis_bold.typeface.json"}
               :my-font2 {:type :font :src "../fonts/gentilis_regular.typeface.json"}}]
    
    (deftest load-assets-test)
        
    (deftest bad-asset-load-test)))

(comment

  *)
