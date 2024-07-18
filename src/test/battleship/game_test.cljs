(ns battleship.game-test
  (:require [cljs.test :refer (testing deftest is)]
            [battleship.game :as g]))

;; spec anyone?

(testing "game creation"
  (let [player-names [{:name "ernie" :type :human} {:name "computer" :type :computer}]
        grid-dims {:w 10 :h 10}
        no-of-ships 5
        game (g/create-game player-names no-of-ships grid-dims)]

    (deftest create-game
      (is (= grid-dims (:grid-dimensions game)) "should be the same as what you passed in")
      (is (false? (:ready game)) "game is not ready to start as no ships deployed")
      (is (= 0 (:turn game)) "first player starts")
      (is (= 2 (:player-count game)))
      (is (= 2 (count (:players game))))
      (let [players (:players game)]
        (is (vector? players) "required for nth/index access")
        (apply #(do (is (= 0 (:turns %)) "player has not started yet")
                    (is (= 0 (:hits %)) "player has not started yet")
                    (is (= 0 (:misses %)) "player has not started yet")
                    (is (= 0 (:remaining-ships %)) "player has not deployed any ships")
                    (is (= 100 (count (:grid %))) "the grid should be the required dimensions of the game")
                    (is (vector? (:grid %)) "required for nth/index access")
                    (is (apply = 0 (:grid %)) "the grid should be initialised to 0")) players)
        (is (= "ernie" (:name (nth players 0))))
        (is (= :human (:type (nth players 0))))
        (is (= "computer" (:name (nth players 1))))
        (is (= :computer (:type (nth players 1))))))))

(testing "player ship placement"
  (let [game (atom (g/create-game [{:name "ernie"} {:name "T2000"}] 2 {:w 5 :h 5}))]

    (deftest place-ships

      ;; player 0
      (reset! game (g/place-ship @game 0 {:x 0 :y 0} {:type 1 :w 1 :h 1}))
      (is (some? @game))
      (is (false? (:ready @game)))
      (let [player (nth (:players @game) 0)]
        (is (= 0 (:turns player)))
        (is (= 1 (:remaining-ships player)))
        (is (= 1 (nth (:grid player) 0))))
      
      (reset! game (g/place-ship @game 0 {:x 2 :y 4} {:type 2 :w 1 :h 1}))
      (is (some? @game))
      (is (false? (:ready @game)))
      (let [player (nth (:players @game) 0)]
        (is (= 0 (:turns player)))
        (is (= 2 (:remaining-ships player)))
        (is (= 2 (nth (:grid player) 22))))

      ;;player 1
      (reset! game (g/place-ship @game 1 {:x 1 :y 1} {:type 1 :w 1 :h 1}))
      (is (some? @game))
      (is (false? (:ready @game)))
      (let [player (nth (:players @game) 1)]
        (is (= 0 (:turns player)))
        (is (= 2 (:remaining-ships player)))
        (is (= 1 (nth (:grid player) 6))))

      (reset! game (g/place-ship @game 1 {:x 4 :y 4} {:type 2 :w 1 :h 1}))
      (is (some? @game))
      (is (false? (:ready @game)))
      (let [player (nth (:players @game) 1)]
        (is (= 0 (:turns player)))
        (is (= 2 (:remaining-ships player)))
        (is (= 2 (nth (:grid player) 24))))

      ;; game can start
      (is (true? (:ready @game))))
     
    (deftest place-too-many-ships)

    (deftest place-ships-when-game-ready)
    
    (deftest place-ships-outside-of-grid-bounds)
  
    (deftest place-ships-overlap-other-ships)))

(testing "play some rounds"
  (let [game (atom (g/create-game [{:name "ernie"} {:name "idiot"}] 2 {:w 10 :h 10}))]

    (deftest play-game-rounds)))

(testing "end of game, a player wins"
  (let [game (g/create-game [{:name "ernie"} {:name "T2"}] 2 {:w 10 :h 10})]

    (deftest end-game)))


(comment

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
