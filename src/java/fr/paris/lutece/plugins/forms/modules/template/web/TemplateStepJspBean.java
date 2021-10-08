package fr.paris.lutece.plugins.forms.modules.template.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateDisplayHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateStepHome;
import fr.paris.lutece.plugins.forms.modules.template.service.ITemplateService;
import fr.paris.lutece.plugins.forms.modules.template.service.TemplateDatabaseService;
import fr.paris.lutece.plugins.forms.modules.template.service.TemplateService;
import fr.paris.lutece.plugins.forms.service.IFormDatabaseService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;
import fr.paris.lutece.plugins.forms.web.admin.AbstractFormQuestionJspBean;
import fr.paris.lutece.plugins.forms.web.exception.CodeAlreadyExistsException;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

@Controller( controllerJsp = "ManageTemplatesStep.jsp", controllerPath = "jsp/admin/plugins/forms/modules/template/", right = "TEMPLATE_STEP_MANAGEMENT" )
public class TemplateStepJspBean extends AbstractFormQuestionJspBean
{
    private static final long serialVersionUID = -7133586597439667057L;

    // Markers
    private static final String MARK_TEMPLATE_LIST = "template_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_ACTION = "action";

    // Templates
    private static final String TEMPLATE_MANAGE_TEMPLATES_STEP = "/admin/plugins/forms/modules/template/manage_templates.html";
    private static final String TEMPLATE_CREATE_TEMPLATES_STEP = "/admin/plugins/forms/modules/template/create_template.html";
    private static final String TEMPLATE_MODIFY_TEMPLATES_STEP = "/admin/plugins/forms/modules/template/modify_template.html";
    private static final String TEMPLATE_CREATE_GROUP = "/admin/plugins/forms/modules/template/create_group.html";
    private static final String TEMPLATE_MODIFY_GROUP = "/admin/plugins/forms/modules/template/modify_group.html";
    private static final String TEMPLATE_CREATE_QUESTION = "/admin/plugins/forms/modules/template/create_question.html";
    private static final String TEMPLATE_MODIFY_QUESTION = "/admin/plugins/forms/modules/template/modify_question.html";
    private static final String TEMPLATE_BREADCRUMBS = "/admin/plugins/forms/modules/template/entries/all_entry_breadcrumbs.html";

    // Views
    private static final String VIEW_MANAGE_TEMPLATES = "manageTemplates";
    private static final String VIEW_CREATE_TEMPLATE = "createTemplate";
    private static final String VIEW_MODIFY_TEMPLATE = "manageQuestions";
    private static final String VIEW_CREATE_QUESTION = "createQuestion";
    private static final String VIEW_MODIFY_QUESTION = "modifyQuestion";
    private static final String VIEW_CONFIRM_REMOVE_COMPOSITE = "getConfirmRemoveComposite";

    // Actions
    private static final String ACTION_CREATE_TEMPLATE = "createTemplate";
    private static final String ACTION_CREATE_GROUP = "createGroup";
    private static final String ACTION_MODIFY_GROUP = "modifyGroup";
    private static final String ACTION_CREATE_QUESTION = "createQuestion";
    private static final String ACTION_CREATE_QUESTION_AND_MANAGE_ENTRIES = "createQuestionAndManageEntries";
    private static final String ACTION_MODIFY_QUESTION = "modifyQuestion";
    private static final String ACTION_REMOVE_COMPOSITE = "removeComposite";

    // Properties
    private static final String PROPERTY_ITEM_PER_PAGE = "forms-template.itemsPerPage";
    private static final String PROPERTY_CREATE_GROUP_TITLE = "forms.create_group.title";

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_CREATE_TEMPLATE = "module.forms.template.create_template.pageTitle";

    // Infos messages
    private static final String INFO_TEMPLATE_CREATED = "module.forms.template.info.template.created";
    private static final String INFO_QUESTION_CREATED = "forms.info.question.created";
    private static final String INFO_QUESTION_UPDATED = "forms.info.question.updated";
    private static final String INFO_DELETE_COMPOSITE_SUCCESSFUL = "forms.info.deleteComposite.successful";

    // Warning messages
    private static final String WARNING_CONFIRM_REMOVE_QUESTION = "forms.warning.deleteComposite.confirmRemoveQuestion";
    private static final String WARNING_CONFIRM_REMOVE_GROUP_ANY_QUESTIONS = "forms.warning.deleteComposite.confirmRemoveGroup";

    // Error messages
    private static final String ERROR_QUESTION_CODE_ALREADY_EXISTS = "forms.error.question.codeAlreadyExists";
    private static final String ERROR_CODE_EXISTS = " Provided code already exists ";

    private ITemplateService _templateService = SpringContextService.getBean( TemplateService.BEAN_NAME );
    private FormDisplay _formDisplay;

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

        model.put( FormsConstants.MARK_STEP, _step );
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

    @View( value = VIEW_MODIFY_TEMPLATE )
    public String getModifyTemplate( HttpServletRequest request )
    {
        int nIdTemplate = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );

        _step = TemplateStepHome.findByPrimaryKey( nIdTemplate );

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_ENTRY_TYPE_REF_LIST, FormsEntryUtils.initListEntryType( ) );
        model.put( FormsConstants.MARK_ID_PARENT, _nIdParentSelected );

        List<ICompositeDisplay> listICompositeDisplay = _templateService.getTemplateCompositeList( nIdTemplate );
        model.put( FormsConstants.MARK_COMPOSITE_LIST, listICompositeDisplay );

        setPageTitleProperty( StringUtils.EMPTY );
        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MODIFY_TEMPLATES_STEP, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Gets the group creation page
     * 
     * @param request
     *            The HTTP request
     * @return The entry creation page
     */
    @View( value = VIEW_CREATE_GROUP )
    public String getCreateGroup( HttpServletRequest request )
    {

        Map<String, Object> model = initCreateGroupModel( request );
        if ( model == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        setPageTitleProperty( PROPERTY_CREATE_GROUP_TITLE );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_GROUP, getLocale( ), model );
        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the Group creation
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CREATE_GROUP )
    public String doCreateGroup( HttpServletRequest request )
    {
        return createGroup( request, VIEW_MODIFY_TEMPLATE );
    }

    /**
     * Gets the question creation page
     * 
     * @param request
     *            The HTTP request
     * @return The entry creation page
     */
    @View( value = VIEW_CREATE_QUESTION )
    public String getCreateQuestion( HttpServletRequest request )
    {
        Map<String, Object> model = initCreateQuestionModel( request, TEMPLATE_BREADCRUMBS, TEMPLATE_CREATE_QUESTION );
        if ( model == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }
        
        Form mockForm = new Form( );
        mockForm.setTitle( _step.getTitle( ) );
        model.put( FormsConstants.MARK_FORM, mockForm );
        model.put( MARK_ACTION, "jsp/admin/plugins/forms/modules/template/ManageTemplatesStep.jsp" );
        
        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( _entry );
        HtmlTemplate template = AppTemplateService.getTemplate( entryTypeService.getTemplateCreate( _entry, false ), getLocale( ), model );
        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the Question creation with its Entry
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CREATE_QUESTION )
    public String doCreateQuestion( HttpServletRequest request )
    {
        try
        {
            String strReturnUrl = processQuestionCreation( request, VIEW_MODIFY_TEMPLATE );

            if ( strReturnUrl != null )
            {
                return redirect( request, strReturnUrl );
            }
            else
            {
                addInfo( INFO_QUESTION_CREATED, getLocale( ) );
            }
        }
        catch( CodeAlreadyExistsException e )
        {
            AppLogService.error( ERROR_CODE_EXISTS + e.getCode( ), e );
            addError( ERROR_QUESTION_CODE_ALREADY_EXISTS, getLocale( ) );
        }
        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );

    }

    /**
     * Perform the Question creation with its Entry and redirect to Entry fields management view
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CREATE_QUESTION_AND_MANAGE_ENTRIES )
    public String doCreateQuestionAndManageEntries( HttpServletRequest request )
    {
        try
        {
            String strReturnUrl = processQuestionCreation( request, VIEW_MODIFY_TEMPLATE );
            return strReturnUrl != null ? strReturnUrl
                    : redirect( request, VIEW_MODIFY_QUESTION, FormsConstants.PARAMETER_ID_STEP, _step.getId( ), FormsConstants.PARAMETER_ID_QUESTION,
                            _question.getId( ) );

        }
        catch( CodeAlreadyExistsException e )
        {
            AppLogService.error( ERROR_CODE_EXISTS, e );
            addError( ERROR_QUESTION_CODE_ALREADY_EXISTS, getLocale( ) );
            return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );
        }
    }

    /**
     * Gets the group modification page
     * 
     * @param request
     *            The HTTP request
     * @return The entry creation page
     */
    @View( value = VIEW_MODIFY_GROUP )
    public String getModifyGroup( HttpServletRequest request )
    {
        Map<String, Object> model = initModifyGroupModel( request );
        if ( model == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        setPageTitleProperty( PROPERTY_CREATE_GROUP_TITLE );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_GROUP, getLocale( ), model );
        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the Group creation
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_MODIFY_GROUP )
    public String doModifyGroup( HttpServletRequest request )
    {
        return modifyGroup( request, VIEW_MODIFY_TEMPLATE );
    }

    /**
     * Gets the Question entry modification page
     * 
     * @param request
     *            The HTTP request
     * @return The entry modification page
     */
    @View( value = VIEW_MODIFY_QUESTION )
    public String getModifyQuestion( HttpServletRequest request )
    {
        Map<String, Object> model = initModifyQuestionModel( request, TEMPLATE_BREADCRUMBS, TEMPLATE_MODIFY_QUESTION );
        
        Form mockForm = new Form( );
        mockForm.setTitle( _step.getTitle( ) );
        model.put( FormsConstants.MARK_FORM, mockForm );
        model.put( MARK_ACTION, "jsp/admin/plugins/forms/modules/template/ManageTemplatesStep.jsp" );
        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( _entry );

        HtmlTemplate template = AppTemplateService.getTemplate( entryTypeService.getTemplateModify( _entry, false ), getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the Question update with its Entry
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_MODIFY_QUESTION )
    public String doModifyQuestion( HttpServletRequest request )
    {
        try
        {
            String strReturnUrl = processQuestionUpdate( request );

            if ( strReturnUrl != null )
            {
                return redirect( request, strReturnUrl );
            }
            else
            {
                addInfo( INFO_QUESTION_UPDATED, getLocale( ) );
            }
        }
        catch( CodeAlreadyExistsException e )
        {
            AppLogService.error( ERROR_CODE_EXISTS + e.getCode( ), e );
            addError( ERROR_QUESTION_CODE_ALREADY_EXISTS, getLocale( ) );
        }
        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );
    }


    /**
     * Gets the confirmation page of question/group deletion
     * 
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete entry
     */
    @View( value = VIEW_CONFIRM_REMOVE_COMPOSITE )
    public String getConfirmRemoveComposite( HttpServletRequest request )
    {

        String strMessage = StringUtils.EMPTY;
        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );

        if ( _step == null || nIdStep != FormsConstants.DEFAULT_ID_VALUE && nIdStep != _step.getId( ) )
        {
            _step = TemplateStepHome.findByPrimaryKey( nIdStep );
        }

        if ( _step == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        int nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), -1 );
        if ( _formDisplay == null || _formDisplay.getId( ) != nIdDisplay )
        {
            _formDisplay = TemplateDisplayHome.findByPrimaryKey( nIdDisplay );
        }

        if ( _formDisplay == null )
        {
            return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );
        }

        if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( _formDisplay.getCompositeType( ) ) )
        {
            strMessage = WARNING_CONFIRM_REMOVE_QUESTION;
        }

        if ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( _formDisplay.getCompositeType( ) ) )
        {
            strMessage = WARNING_CONFIRM_REMOVE_GROUP_ANY_QUESTIONS;
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_COMPOSITE ) );
        url.addParameter( FormsConstants.PARAMETER_ID_DISPLAY, nIdDisplay );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, strMessage, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
        return redirect( request, strMessageUrl );
    }

    /**
     * Perform the question suppression
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_REMOVE_COMPOSITE )
    public String doRemoveComposite( HttpServletRequest request )
    {

        int nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), -1 );
        if ( _formDisplay == null || _formDisplay.getId( ) != nIdDisplay )
        {
            _formDisplay = TemplateDisplayHome.findByPrimaryKey( nIdDisplay );
        }

        if ( _formDisplay == null )
        {
            return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _formDisplay.getStepId( ) );
        }

        _templateService.deleteDisplayAndDescendants( _formDisplay );

        List<FormDisplay> listFormDisplaySibling = TemplateDisplayHome.getFormDisplayListByParent( _formDisplay.getStepId( ), _formDisplay.getParentId( ) );
        _templateService.rebuildDisplayPositionSequence( listFormDisplaySibling );

        addInfo( INFO_DELETE_COMPOSITE_SUCCESSFUL, getLocale( ) );
        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _formDisplay.getStepId( ) );
    }

    @Override
    protected int getDisplayDepthFromParent( int parentGroup )
    {
        int nDisplayDepth = 0;
        if ( parentGroup > 0 )
        {
            FormDisplay templateDisplayParent = TemplateDisplayHome.findByPrimaryKey( parentGroup );
            if ( templateDisplayParent != null )
            {
                nDisplayDepth = templateDisplayParent.getDepth( ) + 1;
            }
        }
        return nDisplayDepth;
    }
    
    @Override
    protected IFormDatabaseService initFormDatabaseService( )
    {
        return SpringContextService.getBean( TemplateDatabaseService.BEAN_NAME );
    }
}
