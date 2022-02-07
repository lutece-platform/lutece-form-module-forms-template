<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:useBean id="manageTemplatesStep" scope="session" class="fr.paris.lutece.plugins.forms.modules.template.web.TemplateStepJspBean" />
<% 
	manageTemplatesStep.init( request, "TEMPLATE_STEP_MANAGEMENT"); 
	response.sendRedirect( manageTemplatesStep.doModifyTemplate(request) );
%>
