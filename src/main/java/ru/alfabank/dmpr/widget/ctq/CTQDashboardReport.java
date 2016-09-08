package ru.alfabank.dmpr.widget.ctq;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.alfabank.dmpr.infrastructure.export.excel.ReportBuilder;
import ru.alfabank.dmpr.infrastructure.export.excel.fluent.ColumnFactory;
import ru.alfabank.dmpr.infrastructure.export.excel.fluent.ColumnFactoryWrapper;
import ru.alfabank.dmpr.infrastructure.helper.PeriodSelectHelper;
import ru.alfabank.dmpr.infrastructure.linq.LinqWrapper;
import ru.alfabank.dmpr.infrastructure.linq.Selector;
import ru.alfabank.dmpr.model.Period;
import ru.alfabank.dmpr.model.PeriodSelectOption;
import ru.alfabank.dmpr.model.Week;
import ru.alfabank.dmpr.model.ctq.CTQDashboardIndexItem;
import ru.alfabank.dmpr.model.ctq.CTQDashboardReportOptions;
import ru.alfabank.dmpr.model.ctq.CTQDashboardReportQueryOptions;
import ru.alfabank.dmpr.model.ctq.CTQDashboardReportSummary;
import ru.alfabank.dmpr.repository.ctq.CTQFilterRepository;
import ru.alfabank.dmpr.repository.ctq.CTQRepository;
import ru.alfabank.dmpr.widget.BaseReport;

import java.text.SimpleDateFormat;

/**
 * Выгрузка в Excel
 */
@Service
public class CTQDashboardReport extends BaseReport<CTQDashboardReportOptions> {
    public class ReportRow{
        public String groupName;
        public String kpiName;
        public LocalDate calcDate;
        public String calcDateAsString;
        public Double normative;
        public Double qualityLevel;
        public Double fact;
        public Long inKpiCount;
        public Long totalCount;

        public ReportRow(int timeUnitId, CTQDashboardIndexItem item){
            groupName = item.kpiname;
            kpiName = item.kpishortname;
            calcDate = item.timeUnitDD;
            normative = (item.kpiNorm == null ? 100 : item.kpiNorm);
            qualityLevel = item.kpi2RatioAvg;
            fact = item.kpiRatioAvg;
            inKpiCount = item.inKpiCount;
            totalCount = item.totalCount;

            PeriodSelectOption pSOption = timeUnitId == Period.week.getValue() ?
                    PeriodSelectHelper.getWeekByYearAndNum(item.timeUnitYear, item.timeUnitPrdNum, weeks) :
                    PeriodSelectHelper.getMonthById(item.timeUnitYear, item.timeUnitPrdNum - 1);

            calcDateAsString = pSOption != null ? pSOption.name : PeriodSelectHelper.formatDate(calcDate);
        }
    }

    @Autowired
    private CTQRepository repository;

    @Autowired
    private CTQFilterRepository filterRepository;

    private Week[] weeks;

    public CTQDashboardReport() {
        super(CTQDashboardReportOptions.class);
    }

    @Override
    protected String getReportName(final CTQDashboardReportOptions options) {
        weeks = filterRepository.getWeeks();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        CTQDashboardReportQueryOptions queryOptions = new CTQDashboardReportQueryOptions(options, weeks);
        return "CTQDashboardReport_"
                + format.format(queryOptions.startDate.toDate())
                + "_" + format.format(queryOptions.endDate.toDate());
    }

    @Override
    protected void configure(ReportBuilder builder, final CTQDashboardReportOptions options) {
        CTQDashboardIndexItem[] data = repository.getReportData(new CTQDashboardReportQueryOptions(options, weeks));

       // CTQDashboardReportSummary[] sumdata = repository.getReportDataSummary(new CTQDashboardReportQueryOptions(options, weeks));

        builder.addWorksheet(ReportRow.class)
                .bindTo(LinqWrapper.from(data).select(new Selector<CTQDashboardIndexItem, ReportRow>() {
                    @Override
                    public ReportRow select(CTQDashboardIndexItem ctqDashboardIndexItem) {
                        return new ReportRow(options.timeUnitId, ctqDashboardIndexItem);
                    }
                }).toArray(ReportRow.class))
                .title("Данные")
                .columns(new ColumnFactoryWrapper() {
                    @Override
                    public void createColumns(ColumnFactory c) {
                        c.add("groupName").title("Группа показателей");
                        c.add("kpiName").title("Показатель");
                        c.add("calcDateAsString").title(options.timeUnitId == 3 ? "Неделя" : "Месяц");
                        c.add("normative").title("Цель").format("0.00%");
                        c.add("qualityLevel").title("Уровень качества").format("0.00%");
                        c.add("fact").title("Значение показателя").format("0.00%");
                        c.add("inKpiCount").title("Успешно");
                        c.add("totalCount").title("Всего");
                    }
                });
    }
}
