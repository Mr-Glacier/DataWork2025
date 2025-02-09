package org.mrglacier.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.mrglacier.dao.DaoFather;
import org.mrglacier.entity.BeanYouXinPaiCarInfo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Mr-Glacier
 * @version 1.0
 * @apiNote 优信拍数据获取
 * @since 2025/2/9 16:47
 */
public class YouXinPaiController {

    /**
     * 使用 httpClient 进行发送请求 并且获取得到 响应 cookie
     */
    public Map<String, String> postRequestByHttpClient(String mainUrl, String parmStr, String cookie) {
        Map<String, String> map = new HashMap<>();
        // 初始错误代码
        map.put("httpStatus", "400");
        // 初始化返回参数
        String csrfTokenKey = "";
        String csrfToken = "";
        String jwtToken = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(mainUrl);
            request.addHeader("Content-Type", "application/json; charset=utf-8");
            request.addHeader("Referer", "https://www.youxinpai.com/trade");
            request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36");
            request.addHeader("X-Requested-With", "XMLHttpRequest");
            request.addHeader("priority", "u=1, i");
            request.addHeader("origin", "https://www.youxinpai.com");
            request.addHeader("Cookie", cookie);
            StringEntity stringEntity = new StringEntity(parmStr, "UTF-8");
            request.setEntity(stringEntity);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                System.out.println("请求响应状态码:->" + response.getStatusLine().getStatusCode());
                map.remove("httpStatus");
                map.put("httpStatus", String.valueOf(response.getStatusLine().getStatusCode()));
                Header[] headers = response.getAllHeaders();
                for (Header header : headers) {
                    if ("Set-Cookie".equals(header.getName())) {

                    }
                    String waitDealStr = header.getValue();
                    // 待处理字符串
                    if (waitDealStr.contains("csrfToken_key=")) {
                        csrfTokenKey = waitDealStr.split(";")[0].replace("csrfToken_key=", "");
                    }
                    if (waitDealStr.contains("csrfToken=")) {
                        csrfToken = waitDealStr.split(";")[0].replace("csrfToken=", "");
                    }
                    if (waitDealStr.contains("jwt_token=")) {
                        jwtToken = waitDealStr.split(";")[0].replace("jwt_token=", "");
                    }
                    map.put("csrfTokenKey", csrfTokenKey);
                    map.put("csrfToken", csrfToken);
                    map.put("jwtToken", jwtToken);
                }
                String responseBody = EntityUtils.toString(response.getEntity());
                if ("200".equals(map.get("httpStatus"))) {
                    map.put("responseBody", responseBody);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 解析所有的数据并且返回本次解析的数据
     */
    public List<BeanYouXinPaiCarInfo> methodAnalysisData(String data, String currentTime) {
        List<BeanYouXinPaiCarInfo> beanList = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(data).getJSONObject("data").getJSONObject("entities").getJSONObject("immediately");
        JSONArray dataArray = jsonObject.getJSONArray("auctionListEntity");
        if (dataArray.size() > 0) {
            for (int i = 0; i < dataArray.size(); i++) {
                BeanYouXinPaiCarInfo beanYouXinPaiCarInfo = new BeanYouXinPaiCarInfo();
                JSONObject oneCarJson = dataArray.getJSONObject(i);
                beanYouXinPaiCarInfo.setC_wareHouseTime(currentTime);
                beanYouXinPaiCarInfo.setC_auctionStatus(oneCarJson.getString("auctionStatus"));
                beanYouXinPaiCarInfo.setC_auctionTitle(oneCarJson.getString("auctionTitle"));
                beanYouXinPaiCarInfo.setC_auctionType(oneCarJson.getString("auctionType"));
                beanYouXinPaiCarInfo.setC_bidEndTime(oneCarJson.getString("bidEndTime"));
                beanYouXinPaiCarInfo.setC_bidStartTime(oneCarJson.getString("bidStartTime"));
                beanYouXinPaiCarInfo.setC_carCityName(oneCarJson.getString("carCityName"));
                beanYouXinPaiCarInfo.setC_carColor(oneCarJson.getString("carColor"));
                beanYouXinPaiCarInfo.setC_carPlaceCity(oneCarJson.getString("carPlaceCity"));
                beanYouXinPaiCarInfo.setC_channelCount(oneCarJson.getString("channelCount"));
                beanYouXinPaiCarInfo.setC_channelTitle(oneCarJson.getString("channelTitle"));
                beanYouXinPaiCarInfo.setC_cityId(oneCarJson.getString("cityId"));
                beanYouXinPaiCarInfo.setC_conditionGradeSmall(oneCarJson.getString("conditionGradeSmall"));
                beanYouXinPaiCarInfo.setC_currentIndex(oneCarJson.getString("currentIndex"));
                beanYouXinPaiCarInfo.setC_currentPublishOrder(oneCarJson.getString("currentPublishOrder"));
                beanYouXinPaiCarInfo.setC_energyType(oneCarJson.getString("energyType"));
                beanYouXinPaiCarInfo.setC_hasVideo(oneCarJson.getString("hasVideo"));
                beanYouXinPaiCarInfo.setC_sourceId(oneCarJson.getString("id"));
                beanYouXinPaiCarInfo.setC_imgUrl(oneCarJson.getString("imgUrl"));
                beanYouXinPaiCarInfo.setC_isAttention(oneCarJson.getString("isAttention"));
                beanYouXinPaiCarInfo.setC_isNewPublish(oneCarJson.getString("isNewPublish"));
                beanYouXinPaiCarInfo.setC_kilometers(oneCarJson.getString("kilometers"));
                beanYouXinPaiCarInfo.setC_labelColor(oneCarJson.getString("labelColor"));
                beanYouXinPaiCarInfo.setC_labelName(oneCarJson.getString("labelName"));
                beanYouXinPaiCarInfo.setC_mileage(oneCarJson.getString("mileage"));
                beanYouXinPaiCarInfo.setC_parkingNum(oneCarJson.getString("parkingNum"));
                beanYouXinPaiCarInfo.setC_power(oneCarJson.getString("power"));
                beanYouXinPaiCarInfo.setC_pricesStart(oneCarJson.getString("pricesStart"));
                beanYouXinPaiCarInfo.setC_publishType(oneCarJson.getString("publishType"));
                beanYouXinPaiCarInfo.setC_redCar(oneCarJson.getString("redCar"));
                beanYouXinPaiCarInfo.setC_reportViewType(oneCarJson.getString("reportViewType"));
                beanYouXinPaiCarInfo.setC_reservePrice(oneCarJson.getString("reservePrice"));
                beanYouXinPaiCarInfo.setC_standardCode(oneCarJson.getString("standardCode"));
                beanYouXinPaiCarInfo.setC_startPriceType(oneCarJson.getString("startPriceType"));
                beanYouXinPaiCarInfo.setC_totalGrade(oneCarJson.getString("totalGrade"));
                beanYouXinPaiCarInfo.setC_year(oneCarJson.getString("year"));
                beanYouXinPaiCarInfo.setC_crykey(oneCarJson.getString("crykey"));
                beanList.add(beanYouXinPaiCarInfo);
            }
        }
        return beanList;
    }

    /**
     * 对于本次数据进行去重入库
     */
    public int methodInsertData(List<BeanYouXinPaiCarInfo> beanList) {
        // 初始化计数器
        int count = 0;
        // 首先对于本次数据 内部重复值进行去除
        List<BeanYouXinPaiCarInfo> distinctBeanList = new ArrayList<>(beanList.stream().collect(Collectors.toMap(
                        BeanYouXinPaiCarInfo::getC_sourceId,
                        beanCheYiPaiCarInfo -> beanCheYiPaiCarInfo,
                        (bean1, bean2) -> bean1))
                .values());

        // 然后查询得到数据库中所有的数据 C_sourceId List
        List<String> sourceIdList = new ArrayList<>();
        DaoFather daoFather = new DaoFather(0, 0);
        List<String> columnList = Collections.singletonList("C_sourceId");
        for (Map<String, String> map : daoFather.methodSelectFree(columnList, "")) {
            sourceIdList.add(map.get("C_sourceId"));
        }

        //如果 sourceId 不存在于数据中,则进行入库操作
        for (BeanYouXinPaiCarInfo bean : distinctBeanList) {
            if (!sourceIdList.contains(bean.getC_sourceId())) {
                daoFather.methodInsert(bean);
                count++;
            }
        }
        return count;
    }


}
