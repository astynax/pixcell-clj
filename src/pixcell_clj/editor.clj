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


(defn grid [palette cells]
  (let [rows (partition 16 cells)]
    [:table#grid
     (seq->tag :tbody
               (for [row rows]
                 (seq->tag :tr
                           (for [color row]
                             [:td {:bgcolor (nth palette color)}]))))]))

(def header [:h2 "✎ PixCell"])
(def footer [:i "by @alex_pir"])

(def toolbar
  (let [tools [["■ New"    ""]
               ["▧ Fill"   "fill"]
               ["░ Random" "rand_fill"]
               ["⇄ Mirror" "mirror"]
               ["⇅ Flip"   "flip"]
               ["◰"        "simple_clone"]
               ["◕"        "rot_clone"]
               ["◑"        "mirror_clone"]
               ["◒"        "flip_clone"]]]
    [:table.toolbar
     [:tbody
      (seq->tag :tr
                (for [[icon url] tools]
                  [:td.button {:onclick (str "alert(\"" url "\")")}
                   (str "&nbsp;" icon "&nbsp;")]))]]))

(defn colorbar [pal col]
  [:table.toolbar
   [:tbody
    (seq->tag :tr
              (for [c pal]
                [(if (= c col)
                   :td.current-color
                   :td.color)
                 {:style (str "background-color:" c ";")}
                 "&nbsp;"]))]])

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
       border: 4bx inset gray;
    }
    "])

(defn ui
  "Editor UI page"
  [state-str op]
  (let [state (if (nil? state-str) ;; can be empty
                initial
                (or (decode state-str)
                    initial))      ;; can contain errors
        pal (nth PALETTES (:palette state))
        col (nth pal (:color state))]
    (html
     [:html
      [:head
       [:title "PixCell by @alex_pir"]
       css
       ;; current state as text
       [:script {:type "text/javascript"}
        (str "var state=\"" (encode state) "\";")]]
      [:body
       header
       toolbar
       (grid pal (:cells state))
       (colorbar pal col)
       [:br]
       footer]])))
