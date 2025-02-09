package org.mrglacier.run;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.mrglacier.controller.YouXinPaiController;
import org.mrglacier.entity.BeanYouXinPaiCarInfo;
import org.mrglacier.until.FileToolsUntil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mr-Glacier
 * @version 1.0
 * @apiNote 优信拍单次执行类
 * @since 2025/2/9 16:54
 */
public class YouXinPaiRun {


    public static void main(String[] args) {

        String mainSavePath = "E:\\ZKZD2025\\二手车数据\\优信拍\\";
        String logsPath = mainSavePath + "\\logs";
        String mainUrl = "https://www.youxinpai.com/trade/getTradeList";

        // 2026-02-09 过期
        String xxzlclientid = "7a923008-bbf8-4b78-b1f2-1717568308111";
        String xxzlxxid = "pfmxwtDfh9JN7gAwBs330bDz99xCin6xXlLaiBRy0BNp5uBTsaB9cJPDll/1KrKTlE4J";
        String xxzlbbid = "pfmbM3wxMDI5M3wxLjEwLjF8MTczOTA5MTUyODcxMDk4MTA4N3xSYnV1TmczSVRpNyttd2o5OTRlQXFKOHQ4WFJWeHdiaHBCMFJzeWRwdFRFPXwxMWM1YWI1NTgwM2Y2YjkxNjQ3ODE5ODIxYmY2ZDVlN18xNzM5MDkxNTI5MTA0XzgxYzkyMThmNWM2OTQ1YWRhZGYyM2MyN2M1NDM0N2FlXzE2OTYzMjkwMzR8OWVlZmNjYjQwMzJkN2YyZGNlN2JhMDFjZTNkNWE4NWRfMTczOTA5MTUyODE3N18yNTY=";
        String id58 = "CkwALmeobJ+qhSSlmfPLAg==";

        String csrfTokenKeyStr = "vlB58stc42x735nQ5wt6OxdQ";
        String csrfTokenStr = "TNhrI9Lv-9socTDdgzmL6qspBwHuglBHM2PY";
        String jwtTokenStr = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjoiVE5ockk5THYtOXNvY1REZGd6bUw2cXNwQndIdWdsQkhNMlBZIiwiaWF0IjoxNzM5MDk0MzY3LCJleHAiOjE3MzkwOTYxNjd9.Kce0xKoGw_boiM29suOGb0Qk3uhy_2tqzVVAjka78jo";
        String tokenStrModel = "xxzlclientid=" + xxzlclientid + "; " +
                "xxzlxxid=" + xxzlxxid + "; " +
                "id58=" + id58 + "; " +
                "xxzlbbid=" + xxzlbbid + "; " +
                "csrfToken_key=csrfTokenKeyStr; " +
                "csrfToken=csrfTokenStr; " +
                "jwt_token=JwtTokenStr";

        YouXinPaiController controllerYouXinPai = new YouXinPaiController();
        FileToolsUntil fileToolsUntil = new FileToolsUntil();

        // 数据收集器
        List<BeanYouXinPaiCarInfo> beanList = new ArrayList<>();
        try {
            String currentDateStr = DateUtil.format(DateUtil.date(), "yyyyMMdd_HHmm");
            String savePath = mainSavePath + currentDateStr + "\\";
            fileToolsUntil.methodCreatFolder(savePath);

            fileToolsUntil.methodWriteFile(logsPath + "YouXinPaiSUCCESS.txt", "本次执行时间---> " + currentDateStr + "\n", true);

            int pageNum = 1;
            while (true) {
                String parmStr = "{\"entities\":\"{\\\"req\\\":{\\\"cityIds\\\":[],\\\"serialIds\\\":[],\\\"appearanceGrades\\\":[],\\\"skeletonGrades\\\":[],\\\"interiorGrades\\\":[],\\\"emissionStandards\\\":[],\\\"carPriceLevel\\\":[],\\\"carYearLevel\\\":[],\\\"carGearbox\\\":[],\\\"carOwners\\\":[],\\\"carUseTypes\\\":[],\\\"fuelTypes\\\":[],\\\"conditionPriceType\\\":[],\\\"transferCounts\\\":[],\\\"startPriceType\\\":[],\\\"isNotBubbleCar\\\":false,\\\"isNotBurnCar\\\":false,\\\"isNotSmallReport\\\":false,\\\"orderFields\\\":10},\\\"page\\\":[{\\\"page\\\":1,\\\"pageSize\\\":2,\\\"pageTab\\\":\\\"pc_circle\\\"},{\\\"page\\\":" + pageNum + ",\\\"pageSize\\\":15,\\\"pageTab\\\":\\\"immediately\\\"},{\\\"page\\\":1,\\\"pageSize\\\":2,\\\"pageTab\\\":\\\"delay\\\"},{\\\"page\\\":1,\\\"pageSize\\\":2,\\\"pageTab\\\":\\\"fixedPrice\\\"},{\\\"page\\\":1,\\\"pageSize\\\":2,\\\"pageTab\\\":\\\"benz\\\"},{\\\"page\\\":1,\\\"pageSize\\\":2,\\\"pageTab\\\":\\\"attention\\\"}]}\"}";
                String tokenStr = tokenStrModel.replace("csrfTokenKeyStr", csrfTokenKeyStr)
                        .replace("csrfTokenStr", csrfTokenStr)
                        .replace("JwtTokenStr", jwtTokenStr);
                Map<String, String> map = controllerYouXinPai.postRequestByHttpClient(mainUrl, parmStr, tokenStr);
                if ("200".equals(map.get("httpStatus"))) {
                    String responseBody = map.get("responseBody");
                    JSONObject jsonObject = (JSONObject.parseObject(responseBody)).getJSONObject("data").getJSONObject("entities").getJSONObject("immediately");
                    JSONArray jsonArray = jsonObject.getJSONArray("auctionListEntity");
                    if (jsonArray.size() == 0) {
                        break;
                    } else {
                        fileToolsUntil.methodWriteFile(savePath + pageNum + ".txt", responseBody);
                        List<BeanYouXinPaiCarInfo> oneBeanList = controllerYouXinPai.methodAnalysisData(responseBody, currentDateStr);
                        beanList.addAll(oneBeanList);
                        csrfTokenKeyStr = map.get("csrfTokenKey");
                        csrfTokenStr = map.get("csrfToken");
                        jwtTokenStr = map.get("jwtToken");
                        pageNum++;
                    }
                } else {
                    break;
                }
            }
            int insertNum = controllerYouXinPai.methodInsertData(beanList);
            fileToolsUntil.methodWriteFile(logsPath + "YouXinPaiSUCCESS.txt", "本次共收集" + insertNum + "条数据\n" +
                    "============================================================\n", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
