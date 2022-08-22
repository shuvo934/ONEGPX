package com.shuvo.ttit.onegpx;

import android.content.Context;
import android.database.SQLException;

import java.sql.Connection;
import java.sql.DriverManager;

public class OracleConnection {
    private static final String DEFAULT_DRIVER = "oracle.jdbc.driver.OracleDriver";
    //private static final String DEFAULT_URL = "jdbc:oracle:thin:@192.168.1.5:1521:TT";
    private static final String DEFAULT_URL = "jdbc:oracle:thin:@103.56.208.123:1524:PDD";
    private static final String DEFAULT_USERNAME = "TR_KABIKHA";
    private static final String DEFAULT_PASSWORD = "TTI";
    private Connection connection;
    Context context;


    public static Connection createConnection(String driver, String url, String username, String password) throws ClassNotFoundException, SQLException {

        Class.forName(driver);
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static Connection createConnection() throws ClassNotFoundException, SQLException {
        return createConnection(DEFAULT_DRIVER, DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

}
