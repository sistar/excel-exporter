(ns excel-exporter.kiez2013
  (:use excel-exporter.excel-reader))
(def sheet-info {:file "examples/data/Starterliste2013-120Starter.xlsx" :heading-row-index 0 :startnummer-title "Nr." :verein-title "Verein"})
(defonce sheet (first (sheets  (workbook (:file sheet-info)))))
(defonce col-indices {:verein-title (col-idx sheet-info sheet :verein-title )
                      :startnummer-title (col-idx sheet-info sheet :startnummer-title)
                      })
(def verein-fn (partial cell-value-of-column (col-indices :verein-title )))
(def startnummer-fn (partial cell-value-of-column (col-indices :startnummer-title )))
(def row-by-startnummer (partial row-by-number-col startnummer-fn sheet ))
(defn map-for-verein-by-starnummer [startnummer]
  {:verein (verein-fn(row-by-startnummer startnummer))}
  )
(def map-for-verein-by-starnummer-memo (memoize map-for-verein-by-starnummer))
(defn verein-by-startnummer [startnummer] (map-for-verein-by-starnummer-memo startnummer))
(defn vereine-list [] (vereine sheet))