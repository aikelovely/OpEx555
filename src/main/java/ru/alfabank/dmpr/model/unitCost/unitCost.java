package ru.alfabank.dmpr.model.unitCost;

import org.joda.time.LocalDate;

/**
 * Набор данных для формирования выгрузки в Excel.
 */
public class UnitCost {
    public String prdtypeCcode;
    public String lvl;
    public LocalDate valueDay;
    public String bpinnerendproductName;
    public String bpinnerendproductCcode;
    public String bporgregionCcode;
    public String bpdivisiongroupIepCcode;
    public Double totalbpiepquantityCnt;
    public Double totalBpiepQuantityCnt;
    public Double totallaborvalueCnt;
    public Double totalcostusdCnt;
    public Double totalcostrurCnt;
    public Double ucUsd;
    public Double ucRur;
}
