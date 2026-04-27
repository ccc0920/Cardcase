package com.team4.cardcase2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

public class JDBCutils {
     public static Connection getConnection() throws SQLException, ClassNotFoundException, IOException {

        String driverName = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/onlineq?useUnicode=true&characterEncoding=utf-8&useSSL=false";
        String user = "root";
        String password = "123456";
        // 数据库驱动
        Class.forName(driverName);
        // 获取数据库链接对象
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }


    public static void release(Statement stmt, Connection conn) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stmt = null;
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }

        public static void release(ResultSet rs, Statement stmt, Connection conn) {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                rs = null;
            }
            release(stmt, conn);
        }

}
