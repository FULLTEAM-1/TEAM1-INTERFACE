package monitor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import send.OutboxDAO;

//----------------------------------------------------------------
//실패한 발신 메시지(Outbox)의 상태를 '재시도(F→N)' 가능하도록 변경하고
//모니터링 목록 페이지로 리다이렉트(Redirect)하는 MVC 패턴의 컨트롤러
//----------------------------------------------------------------

@WebServlet("/monitor/retry_")	// "/monitor/retry_" URL로 요청 보내면 이 서블릿이 실행되도록 매핑
public class IfRetryServlet extends HttpServlet {
	
	// 데이터베이스 연동 처리할 OutboxDAO 객체 선언
    private OutboxDAO outboxDAO; 

    // 서블릿 초기화. 사용할 OutboxDAO 객체 미리 생성
    @Override
    public void init() { // init(): 서블릿이 메모리에 로드될 때 최초 1회 실행되는 초기화 메서드
        outboxDAO = new OutboxDAO();
    }

    // HTTP GET 요청 처리 메서드
    // JSP/HTML 폼에서 outboxId를 POST로 전송 시 호출
    // 유효성 검사 → DB 상태 변경 → 목록 페이지로 리다이렉트 진행
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	// TODO
        System.out.println("POST /monitor/retry");	// 서버 콘솔에 요청 수신 로그 출력

        
        String idStr = req.getParameter("outboxId");   // 요청 파라미터 "outboxId" 추출. 없으면 null 반환
        
        // 파라미터 존재 여부 검증
        // outboxId가 없으면 Error 400 응답하고 종료. return으로 이후 로직 실행 막음
        if (idStr == null) { resp.sendError(400, "outboxId 필요"); return; }

        try {
        	// Long.parseLong(idStr): 문자열 -> Long 변환. DB의 PK가 Long형이므로 문자열 파싱 필요
        	// OutboxDAO.reopen(): outboxID 레코드 상태를 'F'(실패)에서 'N'(대기)으로 업데이트.         	
            outboxDAO.reopen(Long.parseLong(idStr));
            
        } catch (Exception e) {
            e.printStackTrace();  // DB 오류 처리 서버 콘솔 출력.
            resp.sendError(500, "재시도 오류: " + e.getMessage());	// 오류 메시지를 클라이언트에게 전송.

            return;
        }
        // 화면전환(Redirect). contextPath 경로를 얻어 지정된 URL(/monitor/list)로 페이지 이동.
        // 브라우저 URL이 바뀌어 새롭게 GET 요청을 보냄
        // req.getContextPath() : 경로가 없으면 빈 문자열 반환.
        resp.sendRedirect(req.getContextPath() + "/monitor/list");
    }
}
