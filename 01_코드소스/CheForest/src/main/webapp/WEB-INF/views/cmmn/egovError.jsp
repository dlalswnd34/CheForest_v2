<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page isErrorPage="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>오류 발생</title>
</head>

<body>
    <h2>⚠ 오류가 발생했습니다</h2>
    
    <%-- ✅ 수정 시작: 예외 메시지와 원인 출력 --%>
    <p><strong>메시지:</strong> <%= exception != null ? exception.getMessage() : "예외 정보 없음" %></p>
    <p><strong>원인:</strong> <%= (exception != null && exception.getCause() != null) ? exception.getCause().toString() : "원인 정보 없음" %></p>
    <%-- ✅ 수정 끝 --%>

    <hr/>

    <%-- 기존 다국어 메시지 출력 (fail.common.msg에 해당 값이 없을 경우 출력 안됨) --%>
    <p><spring:message code="fail.common.msg" /></p>
</body>
</html>
