(ns battleship.assets-test
  (:require [cljs.test :refer (testing deftest is)]
            [battleship.assets :as a]))

(testing "asset atom creation"
  (let [items {:my-font1 {:type :font :src "../fonts/gentilis_bold.typeface.json"}
               :my-font2 {:type :font :src "../fonts/gentilis_regular.typeface.json"}}
        asset-loaders {:font (js-obj "load" (fn [src fn] (fn (js/Object.))))
                       :model (js-obj "load" (fn [src fn] (fn (js/Object.))))}]
    
    (deftest load-assets-test
      (let [*assets (a/load-assets items asset-loaders)]
        (is (instance? cljs.core.Atom *assets) "must return an asset atom")
        (is (object? (-> @*assets :items :my-font1 :obj)) "must load a font")
        (is (object? (-> @*assets :items :my-font2 :obj)) "must load a font")
        (is (true? (:ready @*assets)) "all assets loaded, so ready")))

    (deftest bad-asset-load-test
      (is (thrown? js/Error (a/load-assets items {})))
      (is (thrown? js/Error (a/load-assets {} {})))
      (is (thrown? js/Error (a/load-assets {:my-font {:type :cheese :src "idiot.fon"}}
                                           {:cheese (js/Object.)}))))))

(comment

  (def items {:my-font1 {:type :font :src "../fonts/gentilis_bold.typeface.json"}
               :my-font2 {:type :font :src "../fonts/gentilis_regular.typeface.json"}})   

  (a/load-assets items {})

  (a/load-assets [] {})

  *)
