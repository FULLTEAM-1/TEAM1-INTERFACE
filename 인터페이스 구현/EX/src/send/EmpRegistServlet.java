package send;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * [시험 학생용] 사원 입사등록 Servlet — ☆ TODO
 *
 * <pre>
 * 능력단위요소 2 / 수행준거 2.1, 2.2, 2.3 (송신측 검증)
 * 체크리스트 항목 5 (폼/empId 자동생성), 11 (직급 화이트리스트)
 * </pre>
 *
 * <h3>처리 단계</h3>
 * <ol>
 *   <li>empName / deptCd / position / email 폼 수신</li>
 *   <li>필수값 검증 (empName/deptCd/position 비면 E10)</li>
 *   <li>empName.length() > 50 → E10</li>
 *   <li>deptCd 정규식 "D\\d{3}" 미일치 → E10</li>
 *   <li>직급 화이트리스트 {사원,대리,과장,차장,부장} 미포함 → E31</li>
 *   <li>empId 자동생성: "E" + new SimpleDateFormat("yyyyMMdd").format(new Date())
 *      + String.format("%03d", Random.nextInt(1000))</li>
 *   <li>Employee VO 생성 → EmployeeDAO.createEmployeeWithOutbox()</li>
 *   <li>성공/실패 result.jsp forward (empId 포함)</li>
 * </ol>
 *
 * <p>정답: ../EX_답/src/send/EmpRegistServlet.java</p>
 */

@WebServlet("/send/emp_")
public class EmpRegistServlet extends HttpServlet {

    private static final Set<String> ALLOWED_POSITIONS =
            Set.of("사원", "대리", "과장", "차장", "부장");

    private EmployeeDAO empDAO;

    @Override
    public void init() {
        empDAO = new EmployeeDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	// TODO: 위 8단계 구현
    	// HTTP 요청 확인
        System.out.println("POST /send/emp");

        // 파라미터 받기 후 공백 제거
        String empName  = trim(req.getParameter("empName"));
        String deptCd   = trim(req.getParameter("deptCd"));
        String position = trim(req.getParameter("position"));
        String email    = trim(req.getParameter("email"));

        // 유효성 검증
        if (empName.isEmpty() || deptCd.isEmpty() || position.isEmpty()) {
            forward(req, resp, "FAIL", "E10: 필수값(empName/deptCd/position) 누락", null);
            return;
        }
        if (empName.length() > 50) {
            forward(req, resp, "FAIL", "E10: empName 길이 초과 (50)", null);
            return;
        }
        if (!deptCd.matches("D\\d{3}")) {
            forward(req, resp, "FAIL", "E10: deptCd 형식 오류 (Dnnn)", null);
            return;
        }
        if (!ALLOWED_POSITIONS.contains(position)) {
            forward(req, resp, "FAIL", "E31: 미지원 직급 (" + position + ")", null);
            return;
        }

        // 비즈니스 데이터(사원 정보) 생성
        String hireDt = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String empId  = "E" + hireDt + String.format("%03d", new Random().nextInt(1000));
        // 입력받은 데이터와 생성된 데이터를 묶어 하나의 객체 생성
        Employee emp = new Employee(empId, empName, deptCd, position, hireDt, email);

        try {
        	// empDAO의 메서드 호출
            long outboxId = empDAO.createEmployeeWithOutbox(emp);
            // 성공 메시지 구현
            String msg = String.format(
                "입사 등록 완료. empId=%s, outboxId=%d (계정은 배치 처리 후 발급됩니다)",
                empId, outboxId);
            forward(req, resp, "OK", msg, empId);
        } 
        // 에러 제어용 예외 처리
        catch (SQLException e) {
            e.printStackTrace();
            forward(req, resp, "FAIL", "E90 DB 오류: " + e.getMessage(), null);
        }
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    // 뷰(JSP) 화면으로 데이터 전달 및 이동
    private void forward(HttpServletRequest req, HttpServletResponse resp,
                         String status, String message, String empId)
            throws ServletException, IOException {
        req.setAttribute("status",  status);
        req.setAttribute("message", message);
        req.setAttribute("empId",   empId);
        req.getRequestDispatcher("/send/result.jsp").forward(req, resp);
    }
}
