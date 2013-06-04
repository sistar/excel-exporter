(ns excel-exporter.test.kiez2013
(:use midje.sweet)
  (:use excel-exporter.kiez2013)
  (:use excel-exporter.excel-reader)
  )

(facts "verein-by-startnummer"
  (fact "startnummer-fn"
    (startnummer-fn (second(rows sheet))) => 1
    (startnummer-fn (nth (rows sheet)3)) => 3
    )
  (fact "row by startnummer"
    (.getRowNum (row-by-startnummer 1)) => 1
    )
  (fact "verein-fn"
    (verein-fn (second(rows sheet))) => "TSV Nusse"
    )
  (fact "valid startnummer"
    (verein-by-startnummer 1) => {:verein "TSV Nusse" }
    (verein-by-startnummer 115) => {:verein "Stadtteilschule Poppenbüttel" }
    )
  (fact "invalid startnummer"
    (verein-by-startnummer -3) => {:verein nil}
    )
  )
(facts "vereine"
  (fact "get"
    (vereine-list) => #(not(nil? %))
    )
  )