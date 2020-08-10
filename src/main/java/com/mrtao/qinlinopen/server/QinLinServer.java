package com.mrtao.qinlinopen.server;

import com.mrtao.qinlinopen.utils.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @Auther: LiuTao
 * @Date: 2019/11/18 18:00
 * @Description:
 */
public interface QinLinServer {



    public Map open();

    public Map qinlinOpenByDoorControlId(Integer  doorControlId);

    public String refresh();

    public List queryUserDoorByCache(String communityId);

    public Map updateSessionId(String sessionId);



}
