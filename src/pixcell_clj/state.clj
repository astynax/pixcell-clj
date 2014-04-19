(ns pixcell-clj.state)

(def COLOR-COUNT 8)
(def ROWS 16)
(def COLS 16)
(def CELL-COUNT (* ROWS COLS))

(def SIZE (+ 2     ;; 2 chars for color & palette
             CELL-COUNT)) ;; one char for each cell

;; === helpers ===

;; conversion byte <-> hex string
(defn- byte->hex [b] (let [h (java.lang.Integer/toHexString b)]
                       (if (= 1 (count h))
                         (str "0" h)
                         h)))
(defn- hex->byte [h] (java.lang.Integer/parseInt h 16))

;; convertion: pair of 4bit <-> byte
(defn- pair->byte [[a b]] (+ (* 16 a) b))
(defn- byte->pair [p] (let [b (mod p 16)
                            a (/ (- p b) 16)]
                        [a b]))

;; unpacks the hex-string into array of cells
(defn- hex->cells [h]
  (mapcat (comp byte->pair
                hex->byte
                (partial apply str))
          (partition 2 h)))

;; packs the array of cells into hex-string
(defn- cells->hex [cs]
  (clojure.string/join
   (mapcat (comp byte->hex pair->byte)
           (partition 2 cs))))

;; tries to convert string to integer, and returns the 'default for bad strings
(defn try-int [s default]
  (try (Integer/parseInt s)
       (catch NumberFormatException _ default)))

;; === API ===

;; initial state (all cells are black)
(def initial
  {:palette 0
   :color 0
   :cells (replicate CELL-COUNT 0)})

(defn encode
  "Encode editor state to string"
  [state]
  (let [{p :palette c :color cs :cells} state]
    (str (->> [p c]
              pair->byte
              byte->hex)
         (cells->hex cs))))

(defn decode
  "Decode editor state from string"
  [state]
  (when (and (= SIZE (count state))
             (every? (set "0123456789abcdef") state))
    (let [[pal col] (->> state
                         (take 2)
                         (apply str)
                         hex->byte
                         byte->pair)
          cells (->> state
                     (drop 2)
                     hex->cells)]
      {:palette pal
       :color col
       :cells cells})))

(defn set-color
  "Sets the current color"
  [state color]
  {:pre [(<= 0 color (dec COLOR-COUNT))]}
  (assoc state :color color))

(defn cycle-palette
  "Switches the palette to next"
  [state palette-count]
  {:pre [(pos? palette-count)]}
  (update-in state
             [:palette]
             #(mod (inc %) palette-count)))

(defn set-cell
  "Sets cell color to current color"
  [state cell]
  (let [{col :color cs :cells } state
        cs (if (zero? cell)
             (cons col (rest cs))
             (concat (take cell cs)
                     [col]
                     (drop (inc cell) cs)))]
    (assoc state :cells cs)))

(defn- map-rows
  [fn state _]
  (let [cs (->> (:cells state)
                (partition COLS)
                fn
                (apply concat))]
    (assoc state :cells cs)))

(defn- map-cells
  [fn state _]
  (let [cs (map fn (:cells state))]
    (assoc state :cells cs)))

(defn- mirror-clone
  [coll]
  (let [half-len (/ (count coll) 2)
        src (take half-len coll)]
    (concat src (reverse src))))

;; === Operations ===

(def operations
  {"set-color"
   (fn [state arg]
     (set-color state
                (min (dec COLOR-COUNT)
                     (try-int arg 0))))
   "set-cell"
   (fn [state arg]
     (set-cell state
               (min (dec CELL-COUNT)
                    (try-int arg 0))))

   "cycle-palette"
   (fn [state _] (cycle-palette state 3))

   "fill"
   (fn [state _]
     (assoc state
       :cells (replicate CELL-COUNT (:color state))))

   "rand-fill"
   (partial map-cells (fn [_] (rand-int COLOR-COUNT)))

   "flip"
   (partial map-rows reverse)

   "mirror"
   (partial map-rows (partial map reverse))

   "flip-clone"
   (partial map-rows mirror-clone)

   "mirror-clone"
   (partial map-rows (partial map mirror-clone))

   "simple-clone"
   (partial map-rows (comp (partial map mirror-clone)
                           mirror-clone))

   "cycle-colors"
   (partial map-cells #(mod (inc %) COLOR-COUNT))
   })

(defn perform
  [state op arg]
  (if-let [op-fn (operations op nil)]
    (op-fn state arg)
    state))
