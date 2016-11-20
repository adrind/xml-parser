(ns xml-parser.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [clojure.xml :as xml]
            [clojure.java.io :as io]
            [hiccup.page :as hiccup]))

(defn load-content [file] (:content (xml/parse (io/input-stream file))))

;;Filter by any tag name
(defn find-tag [content tag] (filter #(= (:tag %) tag) content))

;;Find block definitions
(defn find-custom-blocks [content]
  (map #(select-keys (:attrs %) [:s, :type]) (find-tag (:content content) :block-definition)))


(defn format-response [fileName custom-blocks]
  (hiccup/html5 [:head (hiccup/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css")]
                [:body
                 [:div {:class "container"}
                  [:h1 "File Breakdown"]
                  [:table {:class "table table-bordered"}
                   [:thead " "
                    [:tr " "
                     [:th "#"]
                     [:th "Number of custom blocks"]
                     [:th "List of custom blocks"]]]
                   [:tbody " "
                    [:tr " "
                     [:th {:scope "row"} fileName]
                     [:td (count custom-blocks)]
                     [:td
                      [:ul (for [block custom-blocks] [:li [:strong "Name of block: "] (:s block) (str " (type: " (:type block) ")") ])]
                      ]]]]]]))

(defroutes app-routes
  (GET "/" [] (format-response "small-test.xml" (find-custom-blocks (first (find-tag (load-content "candy.xml") :blocks)))))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
