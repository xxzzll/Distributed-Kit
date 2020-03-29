package com.huawei.lock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * @author xixi
 * @Description： 订单编号生产器
 * @create 2020/3/29
 * @since 1.0.0
 */
public class OrderCodeGenerator {

    // 自增张序列
    private static int i = 0;

    // 按照： "年-月-日-小时-分钟-秒-自增序列" 的规则生成订单编号
    public String getOrderCode(){
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-");
        return sf.format(date) + ++i;
    }

    public static void main(String[] args) {
        OrderCodeGenerator generator = new OrderCodeGenerator();
        for (int j = 0; j < 10; j++) {
            System.out.println(generator.getOrderCode());
        }
    }
}
