<%--
  Created by IntelliJ IDEA.
  User: Adrian
  Date: 28/06/2015
  Time: 14:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
  <title><spring:message code="request.reset.password.title"/></title>
</head>
<body>
<%@include file="fragments/navigation.jspf" %>
<h1><spring:message code="request.reset.password.title"/></h1>

<c:if  test="${not empty param.error}">
  <div class="error">${param.error}</div>
</c:if>

<c:if test="${not empty param.msg}">
  <div class="msg">${param.msg}</div>
</c:if>

<form:form class="requestResetPasswordForm" action="/requestResetPassword" method="POST" modelAttribute="requestResetPasswordForm">
  <label for="emailInput"><spring:message code="request.reset.password.email"/>:</label>
  <form:input type="email" path="email" id="emailInput" />
  <form:errors path="email" cssClass="error" />
  <br/>


  <input class="submit" type="submit" value="<spring:message code="request.reset.password.submit"/>" />
</form:form>


</body>
</html>
