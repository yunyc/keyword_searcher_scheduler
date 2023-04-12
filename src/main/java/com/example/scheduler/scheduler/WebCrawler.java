package com.example.scheduler.scheduler;

import com.example.scheduler.adapter.SchedulerProducer;
import com.example.scheduler.domain.event.AlarmChanged;
import com.example.scheduler.domain.event.NoticeChanged;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.aspectj.weaver.ast.Not;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebCrawler {

    private SchedulerProducer schedulerProducer;

    private AlarmChanged alarmChanged;

    private static final Logger log = LoggerFactory.getLogger(WebCrawler.class);

    public WebCrawler(SchedulerProducer schedulerProducer, AlarmChanged alarmChanged) {
        this.schedulerProducer = schedulerProducer;
        this.alarmChanged = alarmChanged;
    }

    public void findKeyword() throws ExecutionException, InterruptedException, IOException {
        log.info("크롤링 실행");
        // 웹 크롤링 코드

        String url = alarmChanged.getSiteUrl(); // 크롤링할 웹사이트 URL
        String[] keywordList = alarmChanged.getKeyword().split(","); // 추출할 키워드
        String selector = "a";

        for (String excluedurl : alarmChanged.getExcludeUrl()) {
            selector = selector + ":not([href*='" + excluedurl + "'])";
        }

        for (String keyword : keywordList) {
            selector = selector + ":contains(" + keyword + ")";
        }

        Document doc = Jsoup.connect(url).get(); // 웹사이트 HTML 가져오기
        Elements elements = doc.select(selector); // a 태그에서 "키워드"가 포함된 태그 선택

        for (Element element : elements) {
            log.info("단어 저장");
            NoticeChanged noticeChanged = new NoticeChanged(
                element.text(),
                alarmChanged.getUserId(),
                element.attr("href"),
                null,
                alarmChanged.getAlarmId()
            );
            schedulerProducer.sendNoticeCreateEvent(new NoticeChanged("content", "id", "site", null, 1L));
        }
    }
}
