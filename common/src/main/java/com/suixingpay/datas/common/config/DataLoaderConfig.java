/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2018年02月03日 16:44
 * @Copyright ©2018 Suixingpay. All rights reserved.
 * 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */

package com.suixingpay.datas.common.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2018年02月03日 16:44
 * @version: V1.0
 * @review: zhangkewei[zhang_kw@suixingpay.com]/2018年02月03日 16:44
 */
@NoArgsConstructor
public class DataLoaderConfig {
    public DataLoaderConfig(String loaderName, Map<String, String> source) {
        this.loaderName = loaderName;
        this.source = source;
    }

    // 目标插件
    @Getter
    @Setter
    private String loaderName;
    @Getter
    @Setter
    private Map<String, String> source;

    //新增更新转插入策略开关
    @Getter @Setter private boolean insertOnUpdateError = true;
}
