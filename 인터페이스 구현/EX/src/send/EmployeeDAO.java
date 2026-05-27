package send;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

import common.DBManager;
import common.JsonUtil;

/**
 * EmployeeDAO — employee + if_outbox 트랜잭션 통합 — ★ 정답
 *
 * <pre>
 * 능력단위요소 2 / 수행준거 2.2
 * 학습모듈 2-1 §2 (테이블 인터페이스), Transactional Outbox 패턴
 * </pre>
 *  <p><b>createEmployeeWithOutbox(Employee)</b>:</p>
 * <ol>
 *   <li>conn = DBManager.getHrmConnection(); conn.setAutoCommit(false)</li>
 *   <li>PreparedStatement(SQL_INSERT_EMP) — 6개 ? 바인딩 → executeUpdate</li>
 *   <li>payload JSON 생성 — JsonUtil.toJson(LinkedHashMap 6필드)</li>
 *   <li>PreparedStatement(SQL_INSERT_OUTBOX, RETURN_GENERATED_KEYS) — 3개 ?
 *       (if_id=IF_HR_001, tx_no=empId, payload) → executeUpdate → getGeneratedKeys → outboxId</li>
 *   <li>conn.commit() → return outboxId</li>
 *   <li>예외 시 rollback + throw</li>
 *   <li>finally setAutoCommit(true) + close</li>
 * </ol>
 *
 * <p>정답: ../EX_답/src/send/EmployeeDAO.java</p>
 */

public class EmployeeDAO {

    private static final String SQL_INSERT_EMP =
            "INSERT INTO employee (emp_id, emp_name, dept_cd, position, hire_dt, email) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_INSERT_OUTBOX =
            "INSERT INTO if_outbox (if_id, tx_no, payload, status) VALUES (?, ?, ?, 'N')";

    private static final String IF_ID = "IF_HR_001";

    /**
     * 사원 등록 + outbox 한 트랜잭션.
     * @return 생성된 outbox_id
     */
    public long createEmployeeWithOutbox(Employee emp) throws SQLException {
    	// TODO: 위 7단계 구현
        Connection conn = null;
        try {
        	// DB연결 후 수동으로 커밋 설정
        	conn = DBManager.getHrmConnection();
            conn.setAutoCommit(false);

        	// 사원 정보 데이터 삽입
            try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_EMP)) {
                pstmt.setString(1, emp.getEmpId());
                pstmt.setString(2, emp.getEmpName());
                pstmt.setString(3, emp.getDeptCd());
                pstmt.setString(4, emp.getPosition());
                pstmt.setString(5, emp.getHireDt());
                pstmt.setString(6, emp.getEmail());
                pstmt.executeUpdate();
            }

        	// Json으로 변환
            String payload = toPayloadJson(emp);
            long outboxId;
            // Outbox 테이블(SQL_INSERT_OUTBOX)에 데이터를 삽입
            try (
            		PreparedStatement pstmt = conn.prepareStatement(
                    SQL_INSERT_OUTBOX, Statement.RETURN_GENERATED_KEYS)
            	) 
            {
                pstmt.setString(1, IF_ID);
                pstmt.setString(2, emp.getEmpId());   // 멱등키
                pstmt.setString(3, payload);
                pstmt.executeUpdate();
                // ID 추출
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    outboxId = keys.next() ? keys.getLong(1) : -1L;
                }
            }

            // 최종 커밋 
            conn.commit();
            return outboxId;

        } 
        // 예외 발생시 원상복구
        catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            throw e;
        } 
        // 커밋 설정 초기화 및 DB 연결 반납
        finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                DBManager.close(conn);
            }
        }
    }

    /** payload JSON (인터페이스설계서 §3.3 형식) */
    private String toPayloadJson(Employee e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("empId",    e.getEmpId());
        m.put("empName",  e.getEmpName());
        m.put("deptCd",   e.getDeptCd());
        m.put("position", e.getPosition());
        m.put("hireDt",   e.getHireDt());
        m.put("email",    e.getEmail());
        return JsonUtil.toJson(m);
    }
}
