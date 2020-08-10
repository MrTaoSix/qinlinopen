package com.mrtao.qinlinopen.scheduled;

import com.mrtao.qinlinopen.server.QinLinServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Auther: LiuTao
 * @Date: 2019/11/18 17:17
 * @Description:
 */
@Component
public class QinLinOpen {


    @Autowired
    private QinLinServer qinLinServer;

//   0/1 * * * * ?
    @Scheduled(cron="0 0/20 * * * ?")
    public void updateSession(){
        qinLinServer.refresh();
    }

}
