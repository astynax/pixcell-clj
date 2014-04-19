(ns pixcell-clj.editor
  (:require [hiccup.core :refer [html]])
  (:require [pixcell-clj.state :refer :all]))

(def PALETTES [;; common
               ["#000000" "#0000FF" "#00FF00" "#00FFFF"
                "#FF0000" "#FF00FF" "#FFFF00" "#FFFFFF"]
               ;; reds
               ["#000000" "#200810" "#401020" "#601830"
                "#802040" "#BB2850" "#DD3060" "#FF3870"]
               ;; greens
               ["#000000" "#102000" "#204000" "#306000"
                "#408000" "#50BB00" "#60DD00" "#70FF00"]])

(def PAL-COUNT 3)


(defn- seq->tag
  ([tag items]
     (vec (cons tag items)))
  ([tag attrs items]
     (vec (cons tag (cons attrs items)))))

(defn op
  ([op] (str "perform(\"" op "\")"))
  ([op arg] (str "perform(\"" op "\", \"" arg "\")")))

;; seq "item1","item2"... -> seq [0 "item1"],[1 "item2]...
(def enumerate (partial map vector (range)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn grid [palette cells]
  (let [rows (partition 16 (enumerate cells))]
    [:table#grid
     (seq->tag :tbody
               (for [row rows]
                 (seq->tag :tr
                           (for [[idx color] row]
                             [:td {:bgcolor (nth palette color)
                                   :onclick (op "set-cell" idx)}]))))]))

(def header [:h2 "✎ PixCell"])
(def footer [:i "by @alex_pir"])

(def toolbar
  (let [tools [["■ New"    ""]
               ["▧ Fill"   "fill"]
               ["░ Random" "rand-fill"]
               ["⇄ Mirror" "mirror"]
               ["⇅ Flip"   "flip"]
               ["▟"        "simple-clone"]
               ["◕"        "rot-clone"]
               ["▐"        "mirror-clone"]
               ["▄"        "flip-clone"]
               ["⊛"        "cycle-colors"]]]
    [:table.toolbar
     [:tbody
      (seq->tag :tr
                (for [[icon op-name] tools]
                  [:td.button {:onclick (op op-name)}
                   (str "&nbsp;" icon "&nbsp;")]))]]))

(defn colorbar [pal col]
  [:table.toolbar
   [:tbody
    (seq->tag :tr
              (cons
               [:td.button
                {:onclick (op "cycle-palette")}
                "⇸"]
               (for [[idx c] (map vector (range) pal)]
                 [(if (= idx col)
                    :td.current-color
                    :td.color)
                  {:style (str "background-color:" c ";")
                   :onclick (op "set-color" idx)}
                  "&nbsp;"]))
              )]])

(def css [:style {:type "text/css"} "
    table#grid, table.toolbar {
       border: 0px;
       padding: 0;
       margin: 0;
       border-spacing: 0;
    }
    table#grid, tr {
       height: 30px;
       padding: 0;
    }
    table#grid td {
       width: 30px;
       padding: 0;
    }
    table.toolbar td.button {
       background-color: #404040;
       color: #FFFFFF;
       cursor: pointer;
       border: 4px outset gray;
       text-align: center;
    }
    table.toolbar td.color,
    table.toolbar td.current-color {
       width: 40px;
       height: 40px;
       cursor: pointer;
       border: 4px outset gray;
       padding: 0;
       margin: 0;
    }
    table.toolbar td.current-color {
       border: 4px inset gray;
    }
    "])

(def callback "
  function perform(what, arg) {
    if (!what) {
      var url = '/';
    } else {
      var url = '/?state=' + state + '&op=' + what;
    }
    if (arg) {
      url = url + '&arg=' + arg
    }
    window.location = url;
  };")

(defn ui
  "Editor UI page"
  [state-str op arg]
  (let [old-state (if (nil? state-str) ;; can be empty
                initial
                (or (decode state-str)
                    initial))      ;; can contain errors
        state (perform old-state op arg)
        pal (nth PALETTES (:palette state))
        col (:color state)]
    (html
     [:html
      [:head
       [:title "PixCell by @alex_pir"]
       css
       ;; current state as text
       [:script {:type "text/javascript"}
        (str "var state=\"" (encode state) "\";")
        callback]]
      [:body
       header
       toolbar
       (grid pal (:cells state))
       (colorbar pal col)
       [:br]
       footer]])))
