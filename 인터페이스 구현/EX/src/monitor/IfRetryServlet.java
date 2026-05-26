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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("POST /monitor/retry");

        String idStr = req.getParameter("outboxId");
        if (idStr == null) { resp.sendError(400, "outboxId 필요"); return; }

        try {
            outboxDAO.reopen(Long.parseLong(idStr));
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "재시도 오류: " + e.getMessage());
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/monitor/list");
    }
}
