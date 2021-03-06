package ru.alfabank.dmpr.model.ctq;

import org.joda.time.LocalDate;

/**
 * Created by U_M0U9C on 09.09.2016.
 */
public class CTQDashboardSumData {

public LocalDate valueday;
public  String period;
public  String typeperiod;
public  Long numperiod;
public  String directorate;
public  String nametype;
public  String codekpi;
public  String namekpi;
public  String direction;
public  Double summary;
public  Double successfully;
public  Double factkpi;
public  Double goalkpi;
public  Double executekpi;
}

/*  valueday     DATE not null,
  period       VARCHAR2(21),
  typeperiod   VARCHAR2(10),
  numperiod    NUMBER,
  directorate  VARCHAR2(255),
  nametype     VARCHAR2(255) not null,
  codekpi      VARCHAR2(128) not null,
  namekpi      VARCHAR2(255) not null,
  regions      VARCHAR2(255),
  regioncenter VARCHAR2(255),
  direction    VARCHAR2(255),
  summary      NUMBER,
  successfully NUMBER,
  factkpi      NUMBER,
  goatkpi      NUMBER,
  executekpi   NUMBER,
  descript     VARCHAR2(500),
  duodr_flag   CHAR(1)*/
