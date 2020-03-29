package com.huawei.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author xixi
 * @Description：
 * @create 2020/3/29
 * @since 1.0.0
 */
public class Test {

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<Integer>();
        list.add(2);
        list.add(5);
        list.add(1);
        list.add(8);
        list.add(4);
        list.add(3);
        Collections.sort(list);
        for (Integer integer : list) {
            System.out.println(integer);
        }


    }
}
