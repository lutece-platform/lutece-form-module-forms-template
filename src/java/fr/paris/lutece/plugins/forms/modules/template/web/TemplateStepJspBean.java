package fr.paris.lutece.plugins.forms.modules.template.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateStepHome;
import fr.paris.lutece.plugins.forms.web.admin.AbstractJspBean;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;

@Controller( controllerJsp = "ManageTemplatesStep.jsp", controllerPath = "jsp/admin/plugins/forms/modules/template", right = "TEMPLATE_STEP_MANAGEMENT" )
public class TemplateStepJspBean extends AbstractJspBean
{
    private static final long serialVersionUID = -7133586597439667057L;

    // Markers
    private static final String MARK_TEMPLATE_LIST = "template_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_STEP = "step";
    
    // Templates
    private static final String TEMPLATE_MANAGE_TEMPLATES_STEP = "/admin/plugins/forms/modules/template/manage_templates.html";
    private static final String TEMPLATE_CREATE_TEMPLATES_STEP = "/admin/plugins/forms/modules/template/create_template.html";
    
    // Views
    private static final String VIEW_MANAGE_TEMPLATES = "manageTemplates";
    private static final String VIEW_CREATE_TEMPLATE = "createTemplate";
    
    // Actions
    private static final String ACTION_CREATE_TEMPLATE = "createTemplate";
    
    // Properties
    private static final String PROPERTY_ITEM_PER_PAGE = "forms-template.itemsPerPage";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    
    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_CREATE_TEMPLATE = "module.forms.template.create_template.pageTitle";
    
    // Infos
    private static final String INFO_TEMPLATE_CREATED = "module.forms.template.info.template.created";
    
    private Step _step;
    
    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TEMPLATES, defaultView = true )
    public String getManageTemplates( HttpServletRequest request )
    {
        List<Step> listTemplates = TemplateStepHome.getAllTemplates( );
        
        int defaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, defaultItemsPerPage );
        
        Map<String, Object> model = getModel( );
        LocalizedPaginator<Step> paginator = new LocalizedPaginator<>( listTemplates, _nItemsPerPage, getJspManageForm( request ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale( ) );
        
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, String.valueOf( _nItemsPerPage ) );

        model.put( MARK_TEMPLATE_LIST, paginator.getPageItems( ) );
        model.put( MARK_LOCALE, getLocale( ) );
        
        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_TEMPLATES_STEP, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }
    
    /**
     * Returns the form to create a template
     *
     * @param request
     *            The Http request
     * @return the html code of the form form
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             the possible AccessDeniedException
     */
    @View( VIEW_CREATE_TEMPLATE )
    public String getCreateTemplate( HttpServletRequest request ) throws AccessDeniedException
    {
        _step = ( _step != null ) ? _step : new Step( );
        
        Map<String, Object> model = getModel( );
        
        model.put( MARK_STEP, _step );
        
        return getPage( PROPERTY_PAGE_TITLE_CREATE_TEMPLATE, TEMPLATE_CREATE_TEMPLATES_STEP, model );
    }
    
    /**
     * Process the data capture form of a new template
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TEMPLATE )
    public String doCreateTemplate( HttpServletRequest request )
    {
        populate( _step, request, request.getLocale( ) );

        TemplateStepHome.create( _step );
        addInfo( INFO_TEMPLATE_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_TEMPLATES );
    }
}
