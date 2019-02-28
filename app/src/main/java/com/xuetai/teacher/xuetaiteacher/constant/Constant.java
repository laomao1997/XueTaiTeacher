package com.xuetai.teacher.xuetaiteacher.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 常量类
 */
public class Constant {
    public static final String NET_OK = "0";

    public static final String CODE_2100 = "2100";
    public static final String CODE_0111 = "0111";
    public static final String CODE_0112 = "0112";
    public static final String CODE_999999999 = "999999999";

    // 参数key
    public static final String PROPERTY_PROGECT_NO = "projectNo";// 项目编号
    public static final String PROPERTY_REQ_RUL = "reqUrl";// 请求url
    public static final String PROPERTY_REQ_PARAM = "reqParam";// 请求参数
    public static final String PROPERTY_REQ_HEAD_PARAM = "reqHeadParam";// 请求头参数
    public static final String PROPERTY_REQ_TIMESTAMP = "reqTimestamp";
    public static final String PROPERTY_SN = "sn";
    public static final String PROPERTY_SIGN = "sign";
    public static final String PROPERTY_SESSIONTOKEN = "sessionToken";
    public static final String PROPERTY_APP_VERSION = "version";
    public static final String PROPERTY_MECHANISM = "mechanism";//机构（证大）
    public static final String PROPERTY_PLATFORM = "platform";//平台（无线）
    public static final String PROPERTY_TOGATHERTYPE = "togatherType";//合作类型（证大无线）
    public static final String PROPERTY_OPENCHANNEL = "openchannel";//渠道id
    public static final String PROPERTY_UMENG_CHANNEL = "UMENG_CHANNEL";//友盟渠道
    public static final String PROPERTY_TOKEN = "token";
    public static final String PROPERTY_USERAGENT = "userAgent";

    public static final String PROGECT_NO = "Lc_WS2015";

    public static final String logged = "logged";
    public static final String token = "token";

    //web页面栈
    public static List<String> webUrls = new ArrayList<>();

    // 一对一课程状态
    public static final int STATUS_EMPTY = 0;
    public static final int STATUS_FREE = 1; // 空闲
    public static final int STATUS_SOLD = 2; // 被约
    public static final int STATUS_SERIES = 3; // 系列课时间
    public static final int STATUS_MODIFY = 4; // 改课申请
    public static final int STATUS_PUBLIC = 5; // 专题课
    public static final int STATUS_UNPAIED = 6; // 待支付
    public static final int STATUS_NO = 9;


}
