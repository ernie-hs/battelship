(ns battleship.game-test
  (:require [cljs.test :refer (testing deftest is)]
            [battleship.game :as g]))

(testing "game creation"
  (let [player-names [{:name "ernie" :type :human} {:name "computer" :type :gabor}]
        grid-dims {:w 10 :h 10}
        no-of-ships 5
        game (g/create-game player-names no-of-ships grid-dims)]

    (deftest create-game
      (is (false? (:ready game)) "game is not ready to start as no ships deployed")
      (is (= 0 (:turn game)) "first player starts")
      (is (= 2 (count (:players game))))
      (let [players (:players game)]
        (is (vector? players) "required for nth/index access")
        (apply #(do (is (= 0 (:hits %)) "player has not started yet")
                    (is (= 0 (:misses %)) "player has not started yet")
                    (is (= 0 (:remaining-ships %)) "player has not deployed any ships")
                    (is (= (:w grid-dims) (-> % :grid :rect :w)) "should be the same width as the passed grid dims for the game")
                    (is (= (:h grid-dims) (-> % :grid :rect :h)) "should be the same height as the passed grid dims for the game")
                    (is (= 100 (count (-> % :grid :data))) "the grid should be the required dimensions of the game")
                    (is (vector? (-> % :grid :data)) "required for nth/index access")
                    (is (apply = 0 (-> % :grid :data)) "the grid should be initialised to 0")) players)
        (is (= "ernie" (:name (nth players 0))))
        (is (= :human (:type (nth players 0))))
        (is (= "computer" (:name (nth players 1))))
        (is (= :gabor (:type (nth players 1)))))))

  (deftest create-game-with-less-than-two-players
    (is (thrown? js/Error (g/create-game nil -1 nil)))
    (is (thrown? js/Error (g/create-game [] 5 {:w 10 :h 10})) "no players!")
    (is (thrown? js/Error (g/create-game [{:name "only me!"}] 5 {:w 10 :h 10})) "must have at least two players?!")))

(testing "player ship placement"

    (deftest place-ships
      (let [game (atom (g/create-game [{:name "ernie" :type :human} {:name "T2000" :type :computer}] 2 {:w 5 :h 5}))]

        ;; player 0
        (reset! game (g/place-ship @game 0 {:x 0 :y 0} {:rect {:w 1 :h 1} :v 2}))
        (is (some? @game))
        (is (false? (:ready @game)))
        (let [player (nth (:players @game) 0)]
          (is (= 1 (:remaining-ships player)) "player 0 should have 1 ship")
          (is (= 2 (nth (-> player :grid :data) 0))))
          
        (reset! game (g/place-ship @game 0 {:x 2 :y 4} {:rect {:w 1 :h 1} :v 2}))
        (is (some? @game))
        (is (false? (:ready @game)))
        (let [player (nth (:players @game) 0)]
          (is (= 2 (:remaining-ships player)) "player 0 should have 2 ships")
          (is (= 2 (nth (-> player :grid :data) 22))))

        ;;player 1
        (reset! game (g/place-ship @game 1 {:x 1 :y 1} {:rect {:w 1 :h 1} :v 1}))
        (is (some? @game))
        (is (false? (:ready @game)))
        (let [player (nth (:players @game) 1)]
          (is (= 1 (:remaining-ships player)) "player 1 should have 1 ship")
          (is (= 1 (nth (-> player :grid :data) 6))))
        
        (reset! game (g/place-ship @game 1 {:x 4 :y 4} {:rect {:w 1 :h 1} :v 1}))
        (is (some? @game))
        (is (true? (:ready @game)) "the game is ready to play, all ships placed")
        (let [player (nth (:players @game) 1)]
          (is (= 2 (:remaining-ships player)) "player 1 should have 2 ships")
          (is (= 1 (nth (-> player :grid :data) 24))))))
        
    (deftest place-ships-different-of-different-size
      (let [game (atom (g/create-game [{:name "ernie"} {:name "T2000"}] 2 {:w 5 :h 5}))]))
    
    (deftest place-too-many-ships)
        
    (deftest place-ships-when-game-ready)
          
    (deftest place-ships-outside-of-grid-bounds
      (let [game (g/create-game [{:name "ernie"} {:name "Sara"}] 2 {:w 10 :h 10})]
        (is (thrown? js/Error (g/place-ship game 0 {:x 10 :y 10} {:area {:w 3 :h 1} :v 5})) "ship outside of grid bounds")
        (is (thrown? js/Error (g/place-ship game 0 {:x 9 :y 9} {:area {:w 3 :h 1} :v 5})) "ship outside of grid bounds")
        (is (thrown? js/Error (g/place-ship game 0 {:x 9 :y 0} {:area {:w 3 :h 1} :v 5})) "ship outside of grid bounds")
        (is (thrown? js/Error (g/place-ship game 0 {:x 0 :y 0} {:area {:w 100 :h 100} :v 9})) "ship is too huge!")
        (is (thrown? js/Error (g/place-ship game 0 {:x 0 :y 0} {:area {:w 0 :h 0} :v 6})) "ship has no size!")))
            
    (deftest place-ships-overlap-other-ships))

(testing "play some rounds"
  (let [game (atom (g/create-game [{:name "ernie"} {:name "idiot"}] 2 {:w 10 :h 10}))]

    (deftest play-game-rounds)))

(testing "end of game, a player wins"
  (let [game (g/create-game [{:name "ernie"} {:name "T2"}] 2 {:w 10 :h 10})]

    (deftest end-game)))

(comment

  (is (thrown? Exception (throw (ex-info "ernie" {:x 1}))))
  
  (def d {:data {:w 23}})

  (-> d :data :w)
  
  (are [a b] (= a b)
    0 0
    1 0)
  
  (def players [{:name "ernie" :type :human} {:name "computer" :type :computer}])

  (= 0(:turns 0))
  
  (def grid [0 0 1 0 0])

  (apply = 0 grid)
  
  (for [p players] p)
  
  (g/create-game [{:name "ernie"} {:name "computer"}] 4 {:w 3 :h 3})

  *)
