package com.mrtao.qinlinopen.contrller;

import com.mrtao.qinlinopen.server.QinLinServer;
import com.mrtao.qinlinopen.utils.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Auther: LiuTao
 * @Date: 2019/11/18 17:29
 * @Description:
 */
@RestController
public class QinLinOpenController {

    @Autowired
    private QinLinServer qinLinServer;




    @RequestMapping(value="/qinlinOpen",method =  RequestMethod.GET,produces = "application/json; charset=utf-8")
    public Map Open(){

        return qinLinServer.open();
    }


    @RequestMapping(value="/qinlinOpenByDoorControlId",method =  RequestMethod.GET,produces = "application/json; charset=utf-8")
    public Map qinlinOpenByDoorControlId(@RequestParam(value="doorControlId") Integer  doorControlId){
        return qinLinServer.qinlinOpenByDoorControlId(doorControlId);
    }



    @RequestMapping(value="/updateSessionId",method =  RequestMethod.GET,produces = "application/json; charset=utf-8")
    public Map updateSessionId(@RequestParam(value="code") String  code){
        return qinLinServer.updateSessionId(code);
    }

    @RequestMapping(value="/test",method =  RequestMethod.GET,produces = "application/json; charset=utf-8")
    public String test(@RequestParam(value="code") String  code){

        return "Success";
    }

}
