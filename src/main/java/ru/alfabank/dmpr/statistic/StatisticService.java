package ru.alfabank.dmpr.statistic;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.alfabank.dmpr.infrastructure.spring.security.UserContext;
import ru.alfabank.dmpr.infrastructure.spring.security.UserPrincipal;
import ru.alfabank.dmpr.statistic.mapper.StatisticMapper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Service
public class StatisticService {

    private static final Logger logger = Logger.getLogger(StatisticService.class);

    private ExecutorService executorService;

    private BatchBlockingQueue<Statistic> batchBlockingQueue;

    @Autowired
    private StatisticMapper statisticMapper;

    @PostConstruct
    private void init() {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("UserStatistic-%d")
                .setDaemon(true)
                .build();
        executorService = Executors.newFixedThreadPool(6, threadFactory);
        batchBlockingQueue = new BatchBlockingQueue<>(5);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        logger.info("poll statistic");
                        List<Statistic> statistics = batchBlockingQueue.poll();
                        statisticMapper.insertStatistic(statistics.toArray(new Statistic[statistics.size()]));
                    } catch (Exception e) {
                        logger.error(e.getLocalizedMessage(), e);
                    }
                }
            }
        });
    }

    public void serveStatistic(final String page) {
        final UserPrincipal user = UserContext.getUser();
        final LocalDateTime currentDate = LocalDateTime.now();

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    batchBlockingQueue.put(new Statistic(user.getDisplayName(), page, currentDate));
                } catch (InterruptedException e) {
                    logger.error(e.getLocalizedMessage(), e);
                }
            }
        });
    }
// blev ждет завершения  , чтобы статистику не терять .-- нафиг статистику ждать!
//    @PreDestroy
//    private void destroy() throws InterruptedException {
//        logger.info("shutdown statistic");
//        executorService.shutdown();
//        boolean success = executorService.awaitTermination(5, TimeUnit.SECONDS);
//        logger.info("shutdown statistic is " + success);
//        if (!success) {
//            logger.info("shutdown statistic now");
//            executorService.shutdownNow();
//        }
//    }
}
