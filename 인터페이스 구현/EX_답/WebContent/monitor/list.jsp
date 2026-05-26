<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Map, java.text.SimpleDateFormat" %>
<%@ page import="send.OutboxDAO.Outbox, recv.InboxDAO.Inbox" %>
<%
    List<Outbox> outboxList  = (List<Outbox>) request.getAttribute("outboxList");
    List<Inbox>  inboxList   = (List<Inbox>)  request.getAttribute("inboxList");
    List<Map<String,Object>> accountList = (List<Map<String,Object>>) request.getAttribute("accountList");
    String       error       = (String)       request.getAttribute("error");
    SimpleDateFormat fmt     = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat fmtT    = new SimpleDateFormat("HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>인터페이스 모니터링 (EX 시험)</title>
<style>
    body { font-family: 'Malgun Gothic', sans-serif; max-width: 1300px; margin: 24px auto; color: #222; }
    h2,h3 { color: #222; }
    h3 { margin-top: 32px; }
    table { width: 100%; border-collapse: collapse; font-size: 13px; }
    th,td { padding: 6px 8px; border: 1px solid #ddd; text-align: left; }
    th { background: #f5f5f5; }
    .ok   { color: #222; font-weight: bold; }
    .fail { color: #222; font-weight: bold; }
    .ing  { color: #222; font-weight: bold; }
    button{ padding: 4px 10px; background: #333; color: #fff; border: 0; cursor: pointer; border-radius: 3px; }
    .btn-batch { background: #333; padding: 8px 20px; text-decoration: none; color: #fff; border-radius: 4px; }
    nav a { font-size: 13px; }
</style>
</head>
<body>
    <nav><a href="../index.jsp">메인으로</a></nav>
    <h2>인터페이스 + 계정 모니터링</h2>
    <p><a class="btn-batch" href="../recv/batch">배치 실행</a></p>

    <% if (error != null) { %><p class="fail"><%= error %></p><% } %>

    <!-- 계정 -->
    <h3>발급된 계정 (account) — pwd_hash 노출 X</h3>
    <table>
        <thead><tr><th>accountId</th><th>deptCd</th><th>status</th><th>생성일시</th></tr></thead>
        <tbody>
        <% if (accountList != null) for (Map<String,Object> r : accountList) { %>
            <tr>
                <td><%= r.get("accountId") %></td>
                <td><%= r.get("deptCd") %></td>
                <td><%= r.get("status") %></td>
                <td><%= r.get("createDt") == null ? "" : fmt.format(r.get("createDt")) %></td>
            </tr>
        <% } %>
        </tbody>
    </table>

    <!-- Outbox -->
    <h3>송신 Outbox (if_outbox)</h3>
    <table>
        <thead>
            <tr><th>id</th><th>if_id</th><th>txNo(empId)</th><th>status</th><th>retry</th><th>적재</th><th>처리</th><th>오류</th><th>재시도</th></tr>
        </thead>
        <tbody>
        <% if (outboxList != null) for (Outbox r : outboxList) {
               String st  = r.getStatus();
               String cls = "S".equals(st) ? "ok" : ("F".equals(st) ? "fail" : "ing");
        %>
            <tr>
                <td><%= r.getOutboxId() %></td>
                <td><%= r.getIfId() %></td>
                <td><%= r.getTxNo() %></td>
                <td><span class="<%= cls %>"><%= st %></span></td>
                <td><%= r.getRetryCnt() %></td>
                <td><%= r.getRegDt() == null ? "" : fmtT.format(r.getRegDt()) %></td>
                <td><%= r.getProcDt() == null ? "" : fmtT.format(r.getProcDt()) %></td>
                <td><%= r.getErrMsg() == null ? "" : r.getErrMsg() %></td>
                <td>
                    <% if ("F".equals(st)) { %>
                        <form action="retry" method="post" style="margin:0">
                            <input type="hidden" name="outboxId" value="<%= r.getOutboxId() %>">
                            <button type="submit">재시도</button>
                        </form>
                    <% } %>
                </td>
            </tr>
        <% } %>
        </tbody>
    </table>

    <!-- Inbox -->
    <h3>수신 Inbox (if_inbox)</h3>
    <table>
        <thead>
            <tr><th>id</th><th>if_id</th><th>txNo</th><th>status</th><th>처리(ms)</th><th>수신</th><th>오류</th></tr>
        </thead>
        <tbody>
        <% if (inboxList != null) for (Inbox r : inboxList) {
               String st  = r.getStatus();
               String cls = "S".equals(st) ? "ok" : "fail";
        %>
            <tr>
                <td><%= r.getInboxId() %></td>
                <td><%= r.getIfId() %></td>
                <td><%= r.getTxNo() %></td>
                <td><span class="<%= cls %>"><%= st %></span></td>
                <td><%= r.getProcMs() %></td>
                <td><%= r.getRecvDt() == null ? "" : fmtT.format(r.getRecvDt()) %></td>
                <td><%= r.getErrMsg() == null ? "" : r.getErrMsg() %></td>
            </tr>
        <% } %>
        </tbody>
    </table>
</body>
</html>
