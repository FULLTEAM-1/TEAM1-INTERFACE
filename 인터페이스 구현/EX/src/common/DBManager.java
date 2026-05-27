package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * [시험 학생용] DBManager — HrmDB + GroupwareDB — ☆ TODO
 *
 * <p>제공되는 properties:</p>
 * <ul>
 *   <li>hrm.url / hrm.user / hrm.password</li>
 *   <li>gw.url / gw.user / gw.password</li>
 *   <li>batch.fetch.size / batch.max.retry</li>
 *   <li>account.init.password.suffix</li>
 * </ul>
 *
 * <p>정답: ../EX_답/src/common/DBManager.java</p>
 */
public class DBManager {
    private DBManager() {}

    /** 송신측 HrmDB Connection */
    public static Connection getHrmConnection() throws SQLException {
    	// TODO : 
        String driver   = "com.mysql.cj.jdbc.Driver";
        String url      = "jdbc:mysql://localhost:3306/HrmDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8";
        String user     = "ifuserx";
        String password = "ifx1234!";
        // db.properties에 입력한 driver + hrm.url + hrm.user+ hrm.password 값을 각 매개변수에 담기

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("DBManager.getHrmConnection Class.forName..." + e.getCause());
        }
        return DriverManager.getConnection(url, user, password);
        // getHrmConnection 클래스 실행되면 JDBC 드라이버에 (url,user,password) 접속 + 런타임에러 예외처리 + 에러원인
        
    }

    /** 수신측 GroupwareDB Connection */
    public static Connection getGroupwareConnection() throws SQLException {
    	// TODO
        String driver   = "com.mysql.cj.jdbc.Driver";
        String url      = "jdbc:mysql://localhost:3306/GroupwareDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8";
        String user     = "ifuserx";
        String password = "ifx1234!";
        // db.properties에 입력한 driver + gw.url + gw.user+ gw.password 값을 각 매개변수에 담기

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("DBManager.getGroupwareConnection Class.forName..." + e.getCause());
        }
        return DriverManager.getConnection(url, user, password);
        // getGroupwareConnection 클래스 실행되면 JDBC 드라이버에 (url,user,password) 접속 + 런타임에러 예외처리 + 에러원인
    }

    public static int    getFetchSize()           { return 100; } 
    /* TODO : 한 번에 처리할 수 있는 행의 수 설정 */ 
    public static int    getMaxRetry()            { return 3;   } 
    /* TODO : 재시도 횟수 설정 */
    
    /** 계정 초기 비밀번호 suffix — empId + 이 값을 SHA-256 해시한 게 pwd_hash */
    public static String getInitPasswordSuffix() { return "1234"; } 
    /* TODO : 초기 비밀번호 값 설정 */

    /** AutoCloseable 자원 여러 개 안전 close */
    public static void close(AutoCloseable... closeables) {
    	/* TODO : 연결 종료 후 JDBC driver 자동으로 종료 */
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try { c.close(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
        // close() 처리가 되지 않을시 예외처리
    }
}



