<jsp:useBean id="manageTemplatesStep" scope="session" class="fr.paris.lutece.plugins.forms.modules.template.web.TemplateStepJspBean" />
<% String strContent = manageTemplatesStep.processController ( request , response ); %>

<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../../AdminFooter.jsp" %>
