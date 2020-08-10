package com.mrtao.qinlinopen.server;

import com.mrtao.qinlinopen.utils.HttpRequest;
import com.mrtao.qinlinopen.utils.IpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: LiuTao
 * @Date: 2019/11/18 18:00
 * @Description:
 */
@Service
public class QinLinServerImpl implements QinLinServer {


    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    HttpRequest httpRequest = new HttpRequest();

    private String sessionId = "wxmini:2c9a191b6e5ad841016e7db417063856";

    private List<Map> communities = new ArrayList<>();

    Map resultMap = new HashMap();


    @Autowired
    private HttpServletRequest httpServletRequest;


    @Override
    public Map open() {
        return qinlinOpenByDoorControlId(23522);
    }

    @Override
    public Map qinlinOpenByDoorControlId(Integer doorControlId) {

        System.out.println(dateFormat.format(new Date()) + ": IP[" + IpUtil.getIpAddr(httpServletRequest) + "] 请求开门...");
        if (communities.size() <= 0) {
            resultMap.put("code", 401);
            resultMap.put("message", "请初始化SessionId...");
            return resultMap;
        }

        for (Map communitie : communities) {
            List<Map> doors = (List) communitie.get("commonlyUsedDoor");
            for (Map door : doors) {
                if (Integer.parseInt(door.get("doorControlId").toString()) == doorControlId) {
                    String param = "?sessionId=" + sessionId + "&doorControlId=" + doorControlId + "&macAddress==" + door.get("macAddress").toString() + "&communityId=" + communitie.get("communityId").toString();
                    String string = httpRequest.sendSSLPostRequest("https://mobileapi.qinlinkeji.com/api/open/doorcontrol/v2/open" + param, null);
                    JSONObject jsonStr = new JSONObject(string);
                    Integer code = Integer.parseInt(jsonStr.get("code").toString());
                    if (code == 0) {

                        //获取返回的信息体
                        JSONObject jsonData = jsonStr.getJSONObject("data");
                        if (jsonData != null) {


                            if ("1".equals(jsonData.get("openDoorState").toString())) {
                                resultMap.put("code", 200);
                                resultMap.put("message", " " + door.get("doorControlName").toString() + "开门成功");
                                System.out.println(dateFormat.format(new Date()) + " " + door.get("doorControlName").toString() + " : 开门成功！");
                            }
                        } else {
                            resultMap.put("code", 401);
                            resultMap.put("message", "开门失败");
                        }
                    } else if (code == 401) {
                        resultMap.put("code", 401);
                        resultMap.put("message", " Code [" + code + "]" + " 消息 [" + jsonStr.get("message").toString() + "]");
                        System.out.println(dateFormat.format(new Date()) + ": 开门失败！ Code [\"+code+\"]\"+\" 消息 [\"+jsonStr.get(\"message\").toString()+\"]");
                    } else {
                        resultMap.put("code", 401);
                        resultMap.put("message", " Code [" + code + "]" + " 消息 [" + jsonStr.get("message").toString() + "]");
                        System.out.println(dateFormat.format(new Date()) + ": 开门失败！ Code [\"+code+\"]\"+\" 消息 [\"+jsonStr.get(\"message\").toString()+\"]");
                    }
                    return resultMap;
                }

            }
        }
        resultMap.put("code", 401);
        resultMap.put("message", "这个门不存在");
        return resultMap;
    }

    @Override
    public String refresh() {
        String param = "?sessionId=" + sessionId;
        String string = httpRequest.sendSSLPostRequest("https://mobileapi.qinlinkeji.com/api/wxmini/v3/appuser/refresh" + param, null);

        try {

            if(StringUtils.isEmpty(string)){
                Thread.sleep(6000);
                refresh();
                return string;
            }
            JSONObject jsonStr = new JSONObject(string);
            Integer code = Integer.parseInt(jsonStr.get("code").toString());
            //判断是否获取成功


            if (code == 0) {
//            获取返回的信息体
                JSONObject jsonData = jsonStr.getJSONObject("data");
                if (jsonData != null) {
                    System.out.println(dateFormat.format(new Date()) + " 请求: " + sessionId + " 返回：" + jsonStr.get("message").toString());
                }
            } else {
                System.out.println(dateFormat.format(new Date()) + " 请求失败: " + jsonStr.get("message").toString());
            }
        } catch (Exception e) {
            System.out.println(dateFormat.format(new Date()) + " 发生了异常。。。");
            e.printStackTrace();

        }
        return string;
    }

    @Override
    public List queryUserDoorByCache(String communityId) {
        List commonlyUsedDoorList = new ArrayList();
        System.out.println(dateFormat.format(new Date()) + ": 获取当前社区的门，社区ID：" + communityId);
        this.sessionId = sessionId;
        String param = "?sessionId=" + sessionId + "&communityId=" + communityId;
        //请求获取用户信息
        String string = httpRequest.sendSSLPostRequest("https://mobileapi.qinlinkeji.com/api/doorcontrol/queryUserDoorByCache" + param, null);
        try {
            JSONObject jsonStr = new JSONObject(string);
            Integer code = Integer.parseInt(jsonStr.get("code").toString());
            //判断是否获取成功
            if (code == 0) {

                //获取返回的信息体
                JSONObject jsonData = jsonStr.getJSONObject("data");
                if (jsonData != null) {
                    //获取返回的当前小区门信息
                    JSONObject commonlyUsedDoor = jsonData.getJSONObject("commonlyUsedDoor");
                    if (commonlyUsedDoor != null) {
                        System.out.print(commonlyUsedDoor.get("doorControlId").toString() + ":" + commonlyUsedDoor.get("doorControlName").toString() + " ");
                        Map<String, String> commonlyUsedDoorMap = new HashMap<>();
                        commonlyUsedDoorMap.put("doorControlId", commonlyUsedDoor.get("doorControlId").toString());
                        commonlyUsedDoorMap.put("doorControlName", commonlyUsedDoor.get("doorControlName").toString());
                        commonlyUsedDoorMap.put("has3gModule", commonlyUsedDoor.get("has3gModule").toString());
                        commonlyUsedDoorMap.put("hasBluetoothModule", commonlyUsedDoor.get("hasBluetoothModule").toString());
                        commonlyUsedDoorMap.put("macAddress", commonlyUsedDoor.get("macAddress").toString());
                        commonlyUsedDoorMap.put("module", commonlyUsedDoor.get("module").toString());
                        commonlyUsedDoorMap.put("status", commonlyUsedDoor.get("status").toString());
                        commonlyUsedDoorList.add(commonlyUsedDoorMap);
                    }

                    //获取用户的该小区门集合
                    JSONArray userDoorDTOS = jsonData.getJSONArray("userDoorDTOS");
                    if (userDoorDTOS.length() > 0) {

                        //循环获取所有的门
                        for (int i = 0; i < userDoorDTOS.length(); i++) {
                            Map<String, String> doorMap = new HashMap<>();
                            JSONObject door = new JSONObject(userDoorDTOS.get(i).toString());
                            System.out.print(door.get("doorControlId").toString() + ":" + door.get("doorControlName").toString() + " ");
                            doorMap.put("doorControlId", door.get("doorControlId").toString());
                            doorMap.put("doorControlName", door.get("doorControlName").toString());
                            doorMap.put("has3gModule", door.get("has3gModule").toString());
                            doorMap.put("hasBluetoothModule", door.get("hasBluetoothModule").toString());
                            doorMap.put("macAddress", door.get("macAddress").toString());
                            doorMap.put("module", door.get("module").toString());
                            doorMap.put("status", door.get("status").toString());
                            commonlyUsedDoorList.add(doorMap);
                        }
                    }
                    System.out.println();
                    System.out.println(dateFormat.format(new Date()) + ": 获取成功！.....共获取到" + commonlyUsedDoorList.size() + "个门！");
                }
            } else if (code == 401) {
                System.out.println(dateFormat.format(new Date()) + ": 初始化失败.....");
                System.out.println(dateFormat.format(new Date()) + ": " + jsonStr.get("message").toString());
            } else {
                System.out.println(dateFormat.format(new Date()) + ": 初始化失败.....");
                System.out.println(dateFormat.format(new Date()) + ": Code [" + code + "]" + " 消息 [" + jsonStr.get("message").toString() + "]");
            }
        } catch (Exception e) {
            System.out.println(dateFormat.format(new Date()) + ": 发生异常.....");
            e.printStackTrace();
        }

        return commonlyUsedDoorList;
    }


    @Override
    public Map updateSessionId(String sessionId) {
        Map resultMap = new HashMap();
        System.out.println(dateFormat.format(new Date()) + ":初始化SessionId......");
        System.out.println(dateFormat.format(new Date()) + ":当前SessionId为：" + sessionId);
        this.sessionId = sessionId;
        String param = "?sessionId=" + sessionId;
        //请求获取用户信息
        String string = httpRequest.sendSSLPostRequest("https://mobileapi.qinlinkeji.com/api/wxmini/v3/appuser/refresh" + param, null);
        try {
            JSONObject jsonStr = new JSONObject(string);
            Integer code = Integer.parseInt(jsonStr.get("code").toString());
            //判断是否获取成功
            if (code == 0) {
                resultMap.put("code", 200);
                System.out.println(dateFormat.format(new Date()) + ": 初始化成功.....");
                //获取返回的信息体
                JSONObject jsonData = jsonStr.getJSONObject("data");
                if (jsonData != null) {
                    System.out.println(dateFormat.format(new Date()) + ": 当前用户为：" + jsonData.get("nickName"));
                    //获取返回的用户社区信息
                    JSONObject jsonUserQO = jsonData.getJSONObject("userQO");
                    if (jsonUserQO != null) {
                        //获取用户的社区集合
                        JSONArray jsonCommunities = jsonUserQO.getJSONArray("communities");
                        if (jsonCommunities.length() > 0) {
                            System.out.println(dateFormat.format(new Date()) + ": 共获取到" + jsonCommunities.length() + "个社区");
                            communities = new ArrayList();
                            //循环获取所有的社区
                            for (int i = 0; i < jsonCommunities.length(); i++) {
                                JSONObject communitie = new JSONObject(jsonCommunities.get(i).toString());
                                Map communitieMap = new HashMap<>();
                                communitieMap.put("cityCode", communitie.get("cityCode").toString());
                                communitieMap.put("communityId", communitie.get("communityId").toString());
                                communitieMap.put("communityName", communitie.get("communityName").toString());
                                communitieMap.put("districtCode", communitie.get("districtCode").toString());
                                communitieMap.put("expireDate", communitie.get("expireDate").toString());
                                communitieMap.put("isForceSubscribeWechat", communitie.get("isForceSubscribeWechat").toString());
                                communitieMap.put("permissionExpires", communitie.get("permissionExpires").toString());
                                communitieMap.put("propertyPhone", communitie.get("propertyPhone").toString());
                                communitieMap.put("provinceCode", communitie.get("provinceCode").toString());
                                List<Map> commonlyUsedDoors = queryUserDoorByCache(communitieMap.get("communityId").toString());
                                communitieMap.put("commonlyUsedDoor", commonlyUsedDoors);

                                List reusltList = commonlyUsedDoors.stream().map(doorTmep -> {
                                    Map doorMap = new HashMap();
                                    doorMap.put("门的名字", doorTmep.get("doorControlName"));
                                    doorMap.put("门的编号", doorTmep.get("doorControlId"));
                                    return doorMap;
                                }).collect(Collectors.toList());

                                resultMap.put(communitieMap.get("communityName").toString(), reusltList);
                                communities.add(communitieMap);
                            }
                        }
                    }
                }
            } else if (code == 401) {
                resultMap.put("code", 401);
                resultMap.put("message", jsonStr.get("message").toString());
                System.out.println(dateFormat.format(new Date()) + ": 初始化失败.....");
                System.out.println(dateFormat.format(new Date()) + ": " + jsonStr.get("message").toString());
            } else {
                resultMap.put("code", 401);
                resultMap.put("message", jsonStr.get("message").toString());
                System.out.println(dateFormat.format(new Date()) + ": 初始化失败.....");
                System.out.println(dateFormat.format(new Date()) + ": Code [" + code + "]" + " 消息 [" + jsonStr.get("message").toString() + "]");
            }
        } catch (Exception e) {
            System.out.println(dateFormat.format(new Date()) + ": 系统异常.....");
            e.printStackTrace();
        }
        return resultMap;
    }
}
