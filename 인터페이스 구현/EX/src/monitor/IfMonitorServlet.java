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

    @Override
    public void init() {
        outboxDAO  = new OutboxDAO();
        inboxDAO   = new InboxDAO();
        accountDAO = new AccountDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("GET /monitor/list");

        try {
            req.setAttribute("outboxList",  outboxDAO.listAll());
            req.setAttribute("inboxList",   inboxDAO.listAll());
            req.setAttribute("accountList", accountDAO.listAccounts());
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "조회 오류: " + e.getMessage());
        }
        req.getRequestDispatcher("/monitor/list.jsp").forward(req, resp);
    }
}
