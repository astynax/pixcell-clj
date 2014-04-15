(ns pixcell-clj.state-test
  (:require [pixcell-clj.state :refer :all]
            [clojure.test :refer :all]))

(def PAL-COUNT 3)

(def some-state
  (-> initial
      (set-color 5)
      (cycle-palette 3)))


(testing "Modifiers"
  (letfn [(shift-color [dc st]
            (set-color st (+ dc
                             (:color st))))]
    (testing "set-color"
      ;; state must become changed on color change
      (is (not= initial (shift-color 1 initial)))
      (is (= initial (shift-color -2 (shift-color 2 initial))))
      (is (thrown-with-msg? AssertionError #"Assert failed"
                            (set-color initial -1)))
      (is (thrown-with-msg? AssertionError #"Assert failed"
                            (set-color initial 16)))))

  (testing "cycle-palette"
    ;; switches must be cycled
    (is (= initial
           (-> initial
               (cycle-palette 2)
               (cycle-palette 2))
           (-> initial
               (cycle-palette 3)
               (cycle-palette 3)
               (cycle-palette 3))))
    ;; state must become changed on palatte switch
    (is (not= initial
              (cycle-palette initial 2)))
    ;; palette count must be positive
    (is (thrown-with-msg? AssertionError #"Assert failed"
                          (cycle-palette initial 0)))))


(testing "Codec"
  (testing "for SIZE"
    ;; encoded state mus fin in SIZE
    (is (= SIZE (count (encode some-state)))))

  (testing "for symmetry"
    ;; coding must be symmetrical
    (is (= some-state (decode (encode some-state)))))

  (testing "representation of changes"
    (is (not= initial (decode (encode some-state)))))

  (testing "error handling"
    ;; wrong state must not to be decoded
    (is (nil? (decode "")))
    (is (nil? (decode "fsdfsdfs dfsdfs dfsf sdfsdf sdf1231233")))))
