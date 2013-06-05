(ns excel-exporter.test.excel-reader
  (:use midje.sweet)
  (:use excel-exporter.excel-reader)
  (:require [excel-exporter.kiez2013 :as kiez2013])
  )

(facts "till-empty"
  (fact "empty"
    (till-empty '()) => -1
    )
  (fact "one elment"
    (till-empty '(1)) => 0
    )
  )

(facts "excel meta col-idx "
  (let [file-name (:file kiez2013/sheet-info)
        wb (workbook file-name)
        the-sheet (first (sheets wb))]
    (fact ":startnummer-title"
      (col-idx kiez2013/sheet-info the-sheet :startnummer-title ) => 0
      )
    (fact ":nonsense"
      (col-idx kiez2013/sheet-info the-sheet :nonsense ) => nil
      )
    (fact "verein-title"
      (col-idx kiez2013/sheet-info the-sheet :verein-title ) => 5
      )))

(facts "excel parsing"
  (let [file-name (:file kiez2013/sheet-info)
        wb (workbook file-name)
        the-sheet (first (sheets wb))]
    (fact "read from excel with poi "
      (cell-value (
                    first (
                            cells (
                                    first (rows the-sheet)))))
      => "Nr.")))

(facts "excel from kiez"
  (fact "indices" (kiez2013/col-indices :verein-title ) => 5
    (kiez2013/col-indices :startnummer-title ) => 0)
  (fact "startnummer-fn"
    (kiez2013/startnummer-fn (nth (rows kiez2013/sheet) 2)) => 2)
  (fact "rows-with-number filters rows with numbers in the given column"
    (count (rows-with-number kiez2013/startnummer-fn kiez2013/sheet)) => 120)

  (fact "Verein fuer Startnummer"
    (let [r (kiez2013/row-by-startnummer 1)]
      (kiez2013/verein-fn r) => "TSV Nusse"))
  (fact "Verein fuer Startnummer 62"
    (let [r (kiez2013/row-by-startnummer 62)]
      (kiez2013/verein-fn r) => nil)))
