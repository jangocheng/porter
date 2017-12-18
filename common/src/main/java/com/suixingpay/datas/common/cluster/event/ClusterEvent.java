package com.suixingpay.datas.common.cluster.event;/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月18日 10:46
 * @Copyright ©2017 Suixingpay. All rights reserved.
 * 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月18日 10:46
 * @version: V1.0
 * @review: zhangkewei[zhang_kw@suixingpay.com]/2017年12月18日 10:46
 */
public class ClusterEvent {
    private final EventType eventType;
    private final String data;
    public ClusterEvent(EventType eventType, String data) {
        this.eventType = eventType;
        this.data = data;
    }
    public EventType getEventType() {
        return eventType;
    }


    public String getData() {
        return data;
    }
}
