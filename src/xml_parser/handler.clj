(ns xml-parser.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [hiccup.page :as hiccup]
            [xml-parser.parse :as parser]))

;;Gets the block names of a script in order of execution
(defn get-block-names [script]
  (reduce #(str %1 " -> " (:s (:attrs %2))) "Start of script" (:content script)))

(defn format-response [fileName custom-blocks scripts]
  (hiccup/html5 [:head (hiccup/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css")]
                [:body
                 [:div {:class "container"}
                  [:h1 "File Breakdown"]
                  [:table {:class "table table-bordered"}
                   [:thead " "
                    [:tr " "
                     [:th "#"]
                     [:th "Number of custom blocks"]
                     [:th "List of custom blocks"]
                     [:th (str (count scripts) " scripts in the frame")]]]
                   [:tbody " "
                    [:tr " "
                     [:th {:scope "row"} fileName]
                     [:td (count custom-blocks)]
                     [:td
                      [:ul (for [block custom-blocks] [:li [:strong "Name of block: "] (:s block) (str " (type: " (:type block) ")") ])]]
                     [:td (for [script scripts] [:li (get-block-names script)])]
                     ]]]]]))

(defroutes app-routes
  (GET "/" [] (format-response "alonzo.xml"
                               (parser/find-custom-blocks (first (parser/find-tag (parser/load-content "alonzo.xml") :blocks)))
                               (parser/find-nested-tag (parser/load-content "alonzo.xml") [:stage :sprites :sprite :scripts])))
  (route/not-found "Not Found"))


(def app
  (wrap-defaults app-routes site-defaults))
