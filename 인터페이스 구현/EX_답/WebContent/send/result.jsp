<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String status  = (String) request.getAttribute("status");
    String message = (String) request.getAttribute("message");
    String empId   = (String) request.getAttribute("empId");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>입사 등록 결과</title>
<style>
    body { font-family: 'Malgun Gothic', sans-serif; max-width: 560px; margin: 40px auto; color: #222; }
    .ok { color: #222; font-weight: bold; } .fail { color: #222; font-weight: bold; }
    nav a { font-size: 13px; }
</style>
</head>
<body>
    <nav><a href="../index.jsp">메인으로</a> | <a href="../monitor/list">모니터링</a></nav>
    <h2>
    <% if ("OK".equals(status)) { %>
        <span class="ok">[OK] 입사 등록 완료</span>
    <% } else { %>
        <span class="fail">[X] 입사 등록 실패</span>
    <% } %>
    </h2>
    <p>${message}</p>

    <% if (empId != null && !empId.isEmpty()) { %>
        <p>사원번호: <strong><%= empId %></strong></p>
        <p>다음: <a href="../recv/batch">[2] 배치 실행</a> → 계정 발급</p>
    <% } %>

    <p style="margin-top:24px"><a href="empRegist.jsp">다시 등록</a></p>
</body>
</html>
