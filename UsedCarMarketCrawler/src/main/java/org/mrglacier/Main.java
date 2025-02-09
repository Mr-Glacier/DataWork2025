package org.mrglacier;

import org.mrglacier.dao.DaoFather;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mr-Glacier
 * @version 1.0
 * @apiNote ${description}
 * @since ${DATE} ${TIME}
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        DaoFather daoFather = new DaoFather(0,0);

        List<String> columnList = new ArrayList<>();
        columnList.add("id");
        columnList.add("year");
        columnList.add("article_url");

        List<Map<String, String>> maps = daoFather.methodSelectFree(columnList, "");
        System.out.println(maps.size());
        for (Map<String, String> map : maps) {
            System.out.println(map.get("id"));
        }
    }
}