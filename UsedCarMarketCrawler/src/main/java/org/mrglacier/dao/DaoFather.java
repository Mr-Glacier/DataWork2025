package org.mrglacier.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

/**
 * Dao父类链接层,底层使用JDBC进行数据库操作
 *
 * @author Mr-Glacier
 * @version 1.0
 * @since 2024-04-08 15:06
 */

public class DaoFather {
    /**
     * 链接对象
     */
    protected Connection connection = null;
    /**
     * 语句对象
     */
    protected Statement statement = null;
    /**
     * 驱动名
     */
    protected String driverName;
    /**
     * 数据库名
     */
    protected String dbName;
    /**
     * 连接URL
     */
    protected String connectionUrl;
    /**
     * 数据库用户名
     */
    protected String dbUser;
    /**
     * 数据库密码
     */
    protected String dbPass;
    /**
     * 实体类地址
     */
    protected String entityName;
    /**
     * 表名
     */
    protected String tableName;
    /**
     * 主键
     */
    protected String primaryKey;

    /**
     * DaoFather 构造方法
     *
     * @param choseDb    数据库选择
     * @param choseTable 数据表选择
     */
    public DaoFather(int choseDb, int choseTable) {
        //读取配置文件
//        FileToolsUntil readFileUtil = new FileToolsUntil();
        // 获取类加载器
        InputStream inputStream = DaoFather.class.getClassLoader().getResourceAsStream("DBConfig.json");
        StringBuilder configContent = new StringBuilder();
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    configContent.append(currentLine);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONObject dbConfig = JSON.parseObject(String.valueOf(configContent));
        JSONArray dbArray = dbConfig.getJSONArray("DatabaseList");
        // 选择列表中数据库
        JSONObject theDbConfig = dbArray.getJSONObject(choseDb);
        this.driverName = theDbConfig.getString("DBDriver");
        this.dbName = theDbConfig.getString("DBName");
        this.connectionUrl = theDbConfig.getString("DBConnectionStr");
        this.dbUser = theDbConfig.getString("DBUserName");
        this.dbPass = theDbConfig.getString("DBUserPass");
        String beanPath = theDbConfig.getString("EntityPath");

        // 选择列表中表
        JSONArray tableArray = theDbConfig.getJSONArray("EntityList");
        JSONObject theTableConfig = tableArray.getJSONObject(choseTable);
        this.entityName = beanPath + theTableConfig.getString("EntityName");
        this.tableName = theTableConfig.getString("TableName");
        this.primaryKey = theTableConfig.getString("PrimaryKey");
        System.out.println("本次调用Dao 参数情况如下:\n本次数据库名称: " + dbName + "\n" + "本次执行表名: " + this.tableName);
    }

    /**
     * 创建链接以及会话对象
     */
    public void methodCreateSomeObject() {
        try {
            // 注册驱动
            Class.forName(this.driverName);
            // 根据数据库 (当前支持 MySql 以及 SQLServer)
            String mySqlServerTag = "sqlserver";
            String mySqlTag = "mysql";
            // jdbc:mysql://[host]:[port]/[database]?user=[username]&password=[password]
            if (this.driverName.contains(mySqlServerTag)) {
                if (null == connection || connection.isClosed()) {
                    connection = DriverManager.getConnection(this.connectionUrl + "databaseName=" + this.dbName, this.dbUser, this.dbPass);
                }
                if (null == statement || statement.isClosed()) {
                    statement = connection.createStatement();
                }
            } else if (this.driverName.contains(mySqlTag)) {
                if (null == connection || connection.isClosed()) {
                    connection = DriverManager.getConnection(this.connectionUrl + this.dbName, this.dbUser, this.dbPass);
                }
                if (null == statement || statement.isClosed()) {
                    statement = connection.createStatement();
                }
            } else {
                System.out.println("不支持的数据库类型");
                throw new Exception("不支持的数据库类型");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 执行增删改语句
     *
     * @param sql sql语句
     */
    public boolean methodIUD(String sql) {
        // 创建相关对象
        methodCreateSomeObject();
        try {
            statement.executeUpdate(sql.replace("\t", "").replace("\n", "").replace("\r", ""));
            statement.close();
            connection.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 通用插入数据方法
     *
     * @param obj 实体对象
     */
    public void methodInsert(Object obj) {
        try {
            Class<?> c = obj.getClass();
            // 获取其中的get方法
            Method[] methods = c.getDeclaredMethods();
            StringBuilder columnList = new StringBuilder();
            StringBuilder valueList = new StringBuilder();
            for (Method method : methods) {
                // 跳过主键
                if (method.getName().equals("get" + this.primaryKey)) {
                    continue;
                }
                if (method.getName().startsWith("get")) {
                    String columnName = method.getName().replace("get", "");
                    columnList.append(columnName).append(",");

                    String value = method.invoke(obj) == null ? "-" : method.invoke(obj).toString().replace("\t", "").replace("\n", "").replace("\r", "");
                    if (method.getReturnType() == String.class) {
                        valueList.append("'").append(value).append("',");
                    } else {
                        valueList.append(value).append(",");
                    }
                }
            }
            String columnListStr = columnList.substring(0, columnList.length() - 1);
            String valueListStr = valueList.substring(0, valueList.length() - 1);
            String sql = "INSERT INTO  " + this.tableName + "(" + columnListStr + ")Values(" + valueListStr + ");";
            methodIUD(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 全表查询
     *
     * @return BeanList
     */
    public ArrayList<Object> methodSelectAll() {
        ArrayList<Object> beanList = new ArrayList<>();
        try {
            String sql = "Select * from " + this.tableName;
            methodCreateSomeObject();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Class<?> c = Class.forName(this.entityName);

                Object o = c.newInstance();

                // 获取列名
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                // 获取列数
                int lines = resultSetMetaData.getColumnCount();

                for (int i = 0; i < lines; i++) {
                    String columnName = resultSetMetaData.getColumnName(i + 1);
                    // 获取值a
                    Object columnValue = resultSet.getObject(i + 1);
                    Field field = c.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(o, columnValue);
                }
                beanList.add(o);
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beanList;
    }

    /**
     * 自由查询
     *
     * @param columnList     列名列表
     * @param queryCondition 查询条件
     * @return List<Map < String, String>> 数据结果
     */
    public List<Map<String, String>> methodSelectFree(List<String> columnList, String queryCondition) {
        List<Map<String, String>> beanList = new ArrayList<>();
        try {
            // 拼接Sql语句
            StringBuilder column = new StringBuilder();
            for (String s : columnList) {
                column.append(s).append(",");
            }
            String sql = "Select " + column.substring(0, column.length() - 1) + " from " + this.tableName + queryCondition;
            methodCreateSomeObject();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Map<String, String> map = new HashMap<>();
                for (String columnName : columnList) {
                    map.put(columnName, resultSet.getString(columnName));
                }
                beanList.add(map);
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beanList;
    }

    /**
     * 更新数据
     *
     * @param updateColumn    待更新列
     * @param updateValue     更新值(int)
     * @param updateCondition 更新条件
     * @return boolean 更新成功返回true
     */
    public boolean methodUpdate(String updateColumn, Object updateValue, String updateCondition) {
        try {
            methodCreateSomeObject();
            String sql = "";
            if (updateValue instanceof Integer) {
                sql = "UPDATE " + this.tableName + " SET " + updateColumn + " = " + updateValue + " WHERE " + updateCondition;
            } else if (updateValue instanceof String) {
                sql = "UPDATE " + this.tableName + " SET " + updateColumn + " = '" + updateValue + "' WHERE " + updateCondition;
            } else {
                throw new IllegalArgumentException("Unsupported value type: " + updateValue.getClass().getName());
            }
            return methodIUD(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 自由执行
     *
     * @param sqlStr sql语句
     * @return boolean 是否执行成功
     */
    public boolean methodFreeExecution(String sqlStr) {
        try {
            methodCreateSomeObject();
            return methodIUD(sqlStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
