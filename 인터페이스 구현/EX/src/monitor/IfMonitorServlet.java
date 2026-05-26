package monitor;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import recv.AccountDAO;
import recv.InboxDAO;
import send.OutboxDAO;


//----------------------------------------------------------------
//발신함(Outbox), 수신함(Inbox), 계정(Account) 데이터를 한 번에 조회하여 
//"/monitor/list.jsp" 뷰로 전달하는 MVC 패턴의 컨트롤러
//----------------------------------------------------------------

@WebServlet("/monitor/list_")	// "/monitor/list_" URL로 요청 보내면 이 서블릿이 실행되도록 매핑
public class IfMonitorServlet extends HttpServlet {

	// 데이터베이스 연동 처리할 DAO 객체 선언
    private OutboxDAO  outboxDAO;	// 발신함(Outbox) 데이터 조회 DAO
    private InboxDAO   inboxDAO;	// 수신함(Inbox) 데이터 조회 DAO
    private AccountDAO accountDAO; 	// 계정(Account) 목록 조회 DAO

    // 서블릿 초기화. 사용할 DAO 객체들을 미리 생성
    @Override
    public void init() { // init(): 서블릿이 메모리에 로드될 때 최초 1회 실행되는 초기화 메서드
        outboxDAO  = new OutboxDAO();	// 발신함(Outbox) DAO 인스턴스 생성
        inboxDAO   = new InboxDAO();	// 수신함(Inbox) DAO 인스턴스 생성
        accountDAO = new AccountDAO();	// 계정(Account) DAO 인스턴스 생성
    }


    // HTTP POST 요청 처리 메서드
    // 브라우저가 GET /monitor/list_ 요청 시 호출
    // 3가지 목록 데이터를 DB에서 읽어 request에 담아 JSP 포워딩
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	// TODO
        System.out.println("GET /monitor/list");	// 서버 콘솔에 요청 수신 로그 출력

        try {
        	// 발신함 전체 목록 조회. listAll()이 반환한 List를 "outboxList" 키로 request에 저장
            req.setAttribute("outboxList",  outboxDAO.listAll());	
            // 수신함 전체 목록 조회. listAll()이 반환한 List를 "inboxList" 키로 request에 저장.
            req.setAttribute("inboxList",   inboxDAO.listAll());
            // 계정 전체 목록 조회. listAccounts()가 반환한 List를 "accountList" 키로 저장.
            req.setAttribute("accountList", accountDAO.listAccounts());
        } catch (SQLException e) {
        	// DB 오류 처리 서버 콘솔 출력.
            e.printStackTrace();
            // 오류 메시지를 클라이언트에게 전송.
            req.setAttribute("error", "조회 오류: " + e.getMessage());
        }
        // 화면전환(Forward). 데이터를 담은 request(req)와 response(resp) 객체를 가지고 지정된 JSP 페이지로 이동.
        // 브라우저 URL 바뀌지 않고 request 속성 그대로 유지
        req.getRequestDispatcher("/monitor/list.jsp").forward(req, resp);
    }
}