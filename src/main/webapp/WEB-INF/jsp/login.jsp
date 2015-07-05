<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<spring:url value="resources/css/login/loginForm.css" var="loginFormCss"/>
<%--<spring:url value="resources/css/normalize/normalize.css" var="normalizeCss"/>--%>
<spring:url value="resources/css/social/bootstrap-social.css" var="bootstrapSocialCss"/>
<head>
  <title><spring:message code="login.title"/></title>
  <%--<link rel="stylesheet" href="${normalizeCss}"/>--%>
  <link rel="stylesheet" href="${loginFormCss}"/>
  <link rel="stylesheet" href="${bootstrapSocialCss}">

</head>
<body>
  <div id="fb-root"></div>
  <script>(function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/en_GB/sdk.js#xfbml=1&version=v2.3&appId=760180054098768";
    fjs.parentNode.insertBefore(js, fjs);
  }(document, 'script', 'facebook-jssdk'));</script>

  <%@include file="fragments/navigation.jspf" %>

  <div class="content-container">

    <br/>

    <c:if  test="${not empty param.error}">
      <div class="error">${param.error}</div>
    </c:if>

    <c:if test="${not empty param.msg}">
      <div class="msg">${param.msg}</div>
    </c:if>


    <div class="content-header">
      <h1 class="header-title"><spring:message code="login.title"/></h1></div>
    <div class="content-body">
      <form:form class="loginForm" action="/login" method="POST" modelAttribute="loginForm">
        <label for="emailInput"><spring:message code="login.email"/>:</label>
        <form:input path="email" id="emailInput" />
        <br/>
        <form:errors path="email" cssClass="error" />
        <c:if test="${not empty requestScope.requestResendEmail}">
          <br/>
          <a href="resendEmail" class="resendEmail"><spring:message code="login.link.resendemail"/></a>
        </c:if>
        <br/>


        <label for="passwordInput"><spring:message code="login.password"/>:</label>
        <form:input type="password" path="password" id="passwordInput" />
        <br/>
        <form:errors path="password" cssClass="error" />

        <input type="hidden" name="${_csrf.parameterName}"
               value="${_csrf.token}" />

        <spring:message code="login.rememberme"/> <input type="checkbox" name="remember-me" />
        <br/>
        <input class="submit" type="submit" value="<spring:message code="login.submit"/>" />

      </form:form>

      <div class="social-links">
        <%--<div>
          <!-- Add Facebook sign in button -->
          <a href="${pageContext.request.contextPath}/auth/facebook?scope=email" class="fb-login-button"><spring:message code="social.facebook.sign.in.button"/></a>
          <a href="${pageContext.request.contextPath}/auth/facebook?scope=email"><button class="btn btn-facebook"><i class="icon-facebook"></i>Sign in with fb</button></a>

        </div>--%>
        <div class="facebook-link">
          <a href="${pageContext.request.contextPath}/auth/facebook?scope=email" class="btn btn-block btn-social btn-facebook"><i class="fa fa-facebook"></i><spring:message code="social.facebook.sign.in.button"/></a>
        </div>
        <%--<div class="fb-login-button" data-max-rows="1" data-onlogin="onFacebookLogin" data-size="medium" data-show-faces="false" data-auto-logout-link="false"><spring:message code="social.facebook.sign.in.button"/></div>--%>
        <div class="twitter-link">
          <a href="${pageContext.request.contextPath}/auth/twitter" class="btn btn-block btn-social btn-twitter">
            <i class="fa fa-twitter"></i> <spring:message code="social.twitter.sign.in.button"/>
          </a>
        </div>

        <%--<div class="google-link">
          <a href="${pageContext.request.contextPath}/auth/google?scope=email&redirect_uri=${pageContext.request.contextPath}/social/register" class="btn btn-block btn-social btn-google">
            <i class="fa fa-google"></i> <spring:message code="social.google.sign.in.button"/>
          </a>
        </div>--%>

        <div class="linkedin-link">
          <a href="${pageContext.request.contextPath}/auth/linkedin" class="btn btn-block btn-social btn-linkedin">
            <i class="fa fa-linkedin"></i> <spring:message code="social.linkedin.sign.in.button"/>
          </a>
        </div>

      </div>

      <div class="quick-links">
        <ul>
          <li><a href="requestResetPassword"><spring:message code="login.link.resetpassword"/></a></li>
          <li><a href="resendEmail"><spring:message code="login.link.resendemail"/></a></li>
          <li><a href="register"><spring:message code="login.link.register"/></a></li>
        </ul>
      </div>
    </div>
  </div>
  <%-- Include footer --%>
  <%@include file="fragments/footer.jspf" %>

  <%--<script> function onFacebookLogin() { window.location = "${pageContext.request.contextPath}/auth/facebook?scope=email" } </script>--%>
</body>
</html>
