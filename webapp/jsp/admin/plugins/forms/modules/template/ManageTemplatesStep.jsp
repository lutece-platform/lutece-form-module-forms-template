${ pageContext.setAttribute( 'strContent', templateStepJspBean.processController ( pageContext.request, pageContext.response ) ) }

<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

${ pageContext.getAttribute( 'strContent' ) }

<%@ include file="../../../../AdminFooter.jsp" %>
