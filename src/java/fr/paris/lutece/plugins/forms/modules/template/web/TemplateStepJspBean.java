package fr.paris.lutece.plugins.forms.modules.template.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateDisplayHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateEntryHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateFieldHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateGroupHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateQuestionHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateStepHome;
import fr.paris.lutece.plugins.forms.modules.template.service.ITemplateService;
import fr.paris.lutece.plugins.forms.modules.template.service.TemplateService;
import fr.paris.lutece.plugins.forms.service.download.FormDatabaseFileService;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeCheckBox;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeComment;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeDate;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeRadioButton;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeSelect;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;
import fr.paris.lutece.plugins.forms.web.admin.AbstractJspBean;
import fr.paris.lutece.plugins.forms.web.entrytype.EntryTypeCommentDisplayService;
import fr.paris.lutece.plugins.forms.web.exception.CodeAlreadyExistsException;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.EntryTypeHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.referencelist.service.ReferenceListService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;

@Controller( controllerJsp = "ManageTemplatesStep.jsp", controllerPath = "jsp/admin/plugins/forms/modules/template", right = "TEMPLATE_STEP_MANAGEMENT" )
public class TemplateStepJspBean extends AbstractJspBean
{
    private static final Class<?> [ ] ENTRY_TYPE_USER_REF_LIT = {
            EntryTypeCheckBox.class, EntryTypeRadioButton.class, EntryTypeSelect.class
    };
    
    private static final Class<?> [ ] FILTERABLE = {
            EntryTypeCheckBox.class, EntryTypeRadioButton.class, EntryTypeSelect.class, EntryTypeDate.class
    };
    
    private static final long serialVersionUID = -7133586597439667057L;

    // Markers
    private static final String MARK_TEMPLATE_LIST = "template_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_ENTRY_TYPE_SERVICE = "entryTypeService";
    private static final String MARK_LIST_PARAM_DEFAULT_VALUES = "list_param_default_values";
    private static final String MARK_ACTION = "action";
    private static final String MARK_LIST = "list";
    private static final String MARK_GROUP_VALIDATED = "groupValidated";
    private static final String MARK_STEP_VALIDATED = "stepValidated";
    
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
    private static final String VIEW_CREATE_GROUP = "createGroup";
    private static final String VIEW_MODIFY_GROUP = "modifyGroup";
    
    // Actions
    private static final String ACTION_CREATE_TEMPLATE = "createTemplate";
    private static final String ACTION_CREATE_GROUP = "createGroup";
    private static final String ACTION_MODIFY_GROUP = "modifyGroup";
    private static final String ACTION_CREATE_QUESTION = "createQuestion";
    private static final String ACTION_CREATE_QUESTION_AND_MANAGE_ENTRIES = "createQuestionAndManageEntries";
    private static final String ACTION_MODIFY_QUESTION = "modifyQuestion";
    
    // Properties
    private static final String PROPERTY_ITEM_PER_PAGE = "forms-template.itemsPerPage";
    private static final String PROPERTY_CREATE_COMMENT_TITLE = "forms.create_Question.titleComment";
    private static final String PROPERTY_CREATE_QUESTION_TITLE = "forms.create_Question.titleQuestion";
    private static final String PROPERTY_CREATE_GROUP_TITLE = "forms.create_group.title";
    private static final String PROPERTY_MODIFY_COMMENT_TITLE = "forms.modifyEntry.titleComment";
    private static final String PROPERTY_MODIFY_QUESTION_TITLE = "forms.modifyEntry.titleQuestion";
    private static final String PROPERTY_MOVE_GROUP_TITLE = "forms.moveComposite.group.title";
    private static final String PROPERTY_MOVE_QUESTION_TITLE = "forms.moveComposite.question.title";
    
    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    
    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_CREATE_TEMPLATE = "module.forms.template.create_template.pageTitle";
    
    // Infos messages
    private static final String INFO_TEMPLATE_CREATED = "module.forms.template.info.template.created";
    private static final String INFO_QUESTION_CREATED = "forms.info.question.created";
    private static final String INFO_QUESTION_UPDATED = "forms.info.question.updated";
    private static final String INFO_GROUP_UPDATED = "forms.info.group.updated";
    private static final String INFO_GROUP_CREATED = "forms.info.group.created";
    private static final String INFO_MOVE_COMPOSITE_SUCCESSFUL = "forms.info.moveComposite.successful";
    private static final String INFO_DELETE_COMPOSITE_SUCCESSFUL = "forms.info.deleteComposite.successful";
    private static final String INFO_QUESTION_DUPLICATED = "forms.info.question.duplicated";
    private static final String ENTRY_COMMENT_TITLE = "forms.manage_questions.type.comment.title";
    
    // Error messages
    private static final String ERROR_QUESTION_NOT_CREATED = "forms.error.question.notCreated";
    private static final String ERROR_GROUP_NOT_CREATED = "forms.error.group.notCreated";
    private static final String ERROR_GROUP_NOT_UPDATED = "forms.error.group.notUpdated";
    private static final String ERROR_STEP_OR_GROUP_NOT_VALIDATED = "forms.error.moveComposite.stepOrGroup.notvalidated";
    private static final String ERROR_OCCURED_MOVING_COMPOSITE = "forms.error.moveComposite.notCompleted";
    private static final String ERROR_ITERATION_NUMBER = "forms.error.group.iterationNumber";
    private static final String ERROR_QUESTION_CODE_ALREADY_EXISTS = "forms.error.question.codeAlreadyExists";
    private static final String ERROR_CODE_EXISTS = " Provided code already exists ";
    
    // Validations
    private static final String GROUP_VALIDATION_ATTRIBUTES_PREFIX = "forms.model.entity.group.attribute.";
    
    private ITemplateService _templateService = SpringContextService.getBean( TemplateService.BEAN_NAME );
    private IFileStoreServiceProvider _fileStoreProvider = FileService.getInstance( )
            .getFileStoreServiceProvider( FormDatabaseFileService.FILE_STORE_PROVIDER_NAME );
    
    private Step _step;
    private int _nIdParentSelected = 0;

    private Group _group;
    private Entry _entry;

    private Question _question;
    
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

        int nIdTemplate = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        _nIdParentSelected = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ), FormsConstants.DEFAULT_ID_VALUE );

        if ( _step == null || nIdTemplate != FormsConstants.DEFAULT_ID_VALUE && nIdTemplate != _step.getId( ) )
        {
            _step = TemplateStepHome.findByPrimaryKey( nIdTemplate );
        }

        if ( _step == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        _group = new Group( );
        _group.setIterationMin( NumberUtils.INTEGER_ONE );
        _group.setIterationMax( NumberUtils.INTEGER_ONE );

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_GROUP, _group );
        model.put( FormsConstants.MARK_ID_PARENT, _nIdParentSelected );

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

        int nIdTemplate = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        int nParentGroup = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ), 0 );

        if ( _step == null || nIdTemplate != _step.getId( ) )
        {
            _step = TemplateStepHome.findByPrimaryKey( nIdTemplate );
        }

        _group = ( _group != null ) ? _group : new Group( );
        populate( _group, request );

        if ( !validateGroup( ) )
        {
            return redirect( request, VIEW_CREATE_GROUP, FormsConstants.PARAMETER_ID_DISPLAY_PARENT, nParentGroup );
        }

        TemplateGroupHome.create( _group );
        
        int nDisplayDepth = getDisplayDepthFromParent( nParentGroup );

        if ( _group.getId( ) != -1 )
        {
            FormDisplay templateDisplay = new FormDisplay( );
            templateDisplay.setFormId( _step.getIdForm( ) );
            templateDisplay.setStepId( nIdTemplate );
            templateDisplay.setParentId( nParentGroup );
            templateDisplay.setCompositeId( _group.getId( ) );
            templateDisplay.setCompositeType( CompositeDisplayType.GROUP.getLabel( ) );
            templateDisplay.setDepth( nDisplayDepth );
            TemplateDisplayHome.create( templateDisplay );

            if ( templateDisplay.getId( ) == -1 )
            {
                addError( ERROR_GROUP_NOT_CREATED, getLocale( ) );
            }
        }
        else
        {
            addError( ERROR_GROUP_NOT_CREATED, getLocale( ) );
        }

        addInfo( INFO_GROUP_CREATED, getLocale( ) );
        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );

    }
    
    /**
    * Returns the display depth of a child display element
    * 
    * @param nParentDisplay
    *            the Identifier of the parent display element (zero if we are at the step root)
    * 
    * @return the display depth
    */
   private int getDisplayDepthFromParent( int nParentDisplay )
   {
       int nDisplayDepth = 0;
       if ( nParentDisplay > 0 )
       {
           FormDisplay templateDisplayParent = TemplateDisplayHome.findByPrimaryKey( nParentDisplay );
           if ( templateDisplayParent != null )
           {
               nDisplayDepth = templateDisplayParent.getDepth( ) + 1;
           }
       }
       return nDisplayDepth;
   }
    
    /**
     * Perform the group bean validation
     * 
     * @return The boolean which indicate if a group is valid
     */
    private boolean validateGroup( )
    {
        boolean bValidGroup = true;

        if ( _group.getIterationMax( ) != 0 && _group.getIterationMin( ) > _group.getIterationMax( ) )
        {
            bValidGroup = false;
            addError( ERROR_ITERATION_NUMBER, getLocale( ) );
        }

        if ( !validateBean( _group, GROUP_VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            bValidGroup = false;
        }

        return bValidGroup;
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

        int nIdTemplate = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        int nIdTypeEntry = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_BUTTON_TYPE_ENTRY ) );
        _nIdParentSelected = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ) );

        _step = TemplateStepHome.findByPrimaryKey( nIdTemplate );
        if ( _step == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        _entry = createEntryByType( nIdTypeEntry );

        if ( _entry == null )
        {
            return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );
        }

        ReferenceList listParamDefaultValues = new ReferenceList( );

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( _entry );

        Form mockForm = new Form( );
        mockForm.setTitle( _step.getTitle( ) );
        
        Map<String, Object> model = new HashMap<>( );
        model.put( FormsConstants.MARK_ENTRY, _entry );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_FORM, mockForm );
        model.put( FormsConstants.MARK_ID_PARENT, _nIdParentSelected );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( MARK_LIST_PARAM_DEFAULT_VALUES, listParamDefaultValues );
        model.put( MARK_ENTRY_TYPE_SERVICE, entryTypeService );
        model.put( MARK_ACTION, "jsp/admin/plugins/forms/modules/template/ManageTemplatesStep.jsp" );

        if ( Boolean.TRUE.equals( _entry.getEntryType( ).getComment( ) ) )
        {
            setPageTitleProperty( PROPERTY_CREATE_COMMENT_TITLE );
        }
        else
        {
            setPageTitleProperty( PROPERTY_CREATE_QUESTION_TITLE );
        }

        boolean canBeFiltered = Arrays.asList( FILTERABLE ).contains( entryTypeService.getClass( ) );

        model.put( FormsConstants.MARK_CAN_BE_FILTERED, canBeFiltered );
        model.put( FormsConstants.MARK_BREADCRUMBS, AppTemplateService.getTemplate( TEMPLATE_BREADCRUMBS, request.getLocale( ), model ).getHtml( ) );
        model.put( FormsConstants.MARK_QUESTION_CREATE_TEMPLATE,
                AppTemplateService.getTemplate( TEMPLATE_CREATE_QUESTION, request.getLocale( ), model ).getHtml( ) );
        model.put( FormsConstants.MARK_ANONYMIZATION_HELP, entryTypeService.getAnonymizationHelpMessage( request.getLocale( ) ) );

        if ( Arrays.asList( ENTRY_TYPE_USER_REF_LIT ).contains( entryTypeService.getClass( ) ) )
        {
            model.put( FormsConstants.MARK_REFERENCE_LIST_SELECT, ReferenceListService.getInstance( ).getReferencesList( ) );
        }

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
            String strReturnUrl = processQuestionCreation( request );

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
    
    private Entry createEntryByType( int nIdType )
    {
        if ( nIdType == -1 )
        {
            return null;
        }

        Entry entry = new Entry( );
        EntryType entryType = EntryTypeHome.findByPrimaryKey( nIdType );
        entry.setEntryType( entryType );
        return entry;
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
           String strReturnUrl = processQuestionCreation( request );
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
     * Perform the Question creation with its Entry
     * 
     * @param request
     *            The HTTP request
     * @return The URL to display error message after performing the action or null if no error occurred
     */
    private String processQuestionCreation( HttpServletRequest request ) throws CodeAlreadyExistsException
    {
        int nIdTemplate = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        int nParentGroup = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ) );
        int nIdType = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_ENTRY_TYPE ) );

        if ( _step == null || nIdTemplate != _step.getId( ) )
        {
            _step = TemplateStepHome.findByPrimaryKey( nIdTemplate );
        }

        _entry = createEntryByType( nIdType );

        if ( _entry == null )
        {
            addError( ERROR_QUESTION_NOT_CREATED, getLocale( ) );
            return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, nIdTemplate );
        }

        String strError = EntryTypeServiceManager.getEntryTypeService( _entry ).getRequestData( _entry, request, getLocale( ) );

        if ( strError != null )
        {
            return strError;
        }

        TemplateEntryHome.create( _entry );

        // If the entry code is empty, provide a new one
        if ( StringUtils.isEmpty( _entry.getCode( ) ) )
        {
            _entry.setCode( "question_" + _entry.getIdEntry( ) );
        }

        if ( checkCodeAlreadyExists( _entry.getCode( ), _step.getId( ), _entry.getIdEntry( ) ) )
        {
            throw new CodeAlreadyExistsException( _entry.getCode( ) );
        }

        TemplateEntryHome.update( _entry );

        for ( Field field : _entry.getFields( ) )
        {
            field.setParentEntry( _entry );
            TemplateFieldHome.create( field );
        }

        _question = new Question( );
        String strTitle = Boolean.TRUE.equals( _entry.getEntryType( ).getComment( ) ) ? I18nService.getLocalizedString( ENTRY_COMMENT_TITLE, getLocale( ) )
                : _entry.getTitle( );
        _question.setTitle( strTitle );
        _question.setCode( _entry.getCode( ) );
        _question.setDescription( _entry.getComment( ) );
        _question.setIdEntry( _entry.getIdEntry( ) );
        _question.setIdStep( nIdTemplate );
        _question.setVisibleMultiviewGlobal( request.getParameter( FormsConstants.PARAMETER_MULTIVIEW_GLOBAL ) != null );
        _question.setVisibleMultiviewFormSelected( request.getParameter( FormsConstants.PARAMETER_MULTIVIEW_FORM_SELECTED ) != null );
        _question.setFiltrableMultiviewGlobal( request.getParameter( FormsConstants.PARAMETER_FILTERABLE_MULTIVIEW_GLOBAL ) != null );
        _question.setFiltrableMultiviewFormSelected( request.getParameter( FormsConstants.PARAMETER_FILTERABLE_MULTIVIEW_FORM_SELECTED ) != null );

        String columnTitle = request.getParameter( FormsConstants.PARAMETER_COLUMN_TITLE );
        columnTitle = ( columnTitle == null || columnTitle.isEmpty( ) ) ? _question.getTitle( ) : columnTitle;
        _question.setColumnTitle( columnTitle );
        _question.setMultiviewColumnOrder( NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_MULTIVIEW_ORDER ), 0 ) );
        TemplateQuestionHome.create( _question );

        int nDisplayDepth = getDisplayDepthFromParent( nParentGroup );

        if ( _question.getId( ) != -1 )
        {
            FormDisplay templateDisplay = new FormDisplay( );
            templateDisplay.setStepId( nIdTemplate );
            templateDisplay.setParentId( nParentGroup );
            templateDisplay.setCompositeId( _question.getId( ) );
            templateDisplay.setCompositeType( CompositeDisplayType.QUESTION.getLabel( ) );
            templateDisplay.setDepth( nDisplayDepth );
            TemplateDisplayHome.create( templateDisplay );
        }
        return null;
    }
    
    /**
     * Check if the question code already exists or not
     * 
     * @param strCode
     *            The question code
     * @param nIdTemplate
     *            The id of the form
     * @return true if the code already exists; false otherwise
     */
    private boolean checkCodeAlreadyExists( String strCode, int nIdTemplate, int nIdEntry )
    {
        List<Question> listQuestionOfTemplate = TemplateQuestionHome.getQuestionsListByStep( nIdTemplate );

        for ( Question question : listQuestionOfTemplate )
        {
            if ( question.getCode( ).equals( strCode ) && nIdEntry != question.getIdEntry( ) )
            {
                return true;
            }
        }
        return false;
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

        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        int nIdGroup = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_GROUP ), FormsConstants.DEFAULT_ID_VALUE );

        if ( _step == null || nIdStep != FormsConstants.DEFAULT_ID_VALUE && nIdStep != _step.getId( ) )
        {
            _step = TemplateStepHome.findByPrimaryKey( nIdStep );
        }

        if ( _step == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        if ( _group == null || nIdGroup != FormsConstants.DEFAULT_ID_VALUE && _group.getId( ) != nIdGroup )
        {
            _group = TemplateGroupHome.findByPrimaryKey( nIdGroup );
        }

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_GROUP, _group );

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
        int nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );

        if ( _step == null || nIdStep != _step.getId( ) )
        {
            _step = TemplateStepHome.findByPrimaryKey( nIdStep );
        }

        _group = _group != null ? _group : new Group( );
        populate( _group, request );

        if ( !validateGroup( ) )
        {
            return redirectView( request, VIEW_MODIFY_GROUP );
        }

        TemplateGroupHome.update( _group );

        if ( _group.getId( ) == -1 )
        {
            addError( ERROR_GROUP_NOT_UPDATED, getLocale( ) );
        }
        else
        {
            addInfo( INFO_GROUP_UPDATED, getLocale( ) );
        }

        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );
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

        int nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );

        if ( _step == null || nIdStep != _step.getId( ) )
        {
            _step = TemplateStepHome.findByPrimaryKey( nIdStep );
        }

        String strIdQuestion = request.getParameter( FormsConstants.PARAMETER_ID_QUESTION );
        int nIdQuestion = -1;

        if ( StringUtils.isNotEmpty( strIdQuestion ) )
        {
            nIdQuestion = Integer.parseInt( strIdQuestion );
        }

        _question = TemplateQuestionHome.findByPrimaryKey( nIdQuestion );
        _entry = TemplateEntryHome.findByPrimaryKey( _question.getIdEntry( ) );

        List<Field> listField = new ArrayList<>( _entry.getFields( ).size( ) );

        for ( Field field : _entry.getFields( ) )
        {
            field = TemplateFieldHome.findByPrimaryKey( field.getIdField( ) );
            listField.add( field );
        }

        _entry.setFields( listField );
        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( _entry );

        Form mockForm = new Form( );
        mockForm.setTitle( _step.getTitle( ) );
        
        Map<String, Object> model = new HashMap<>( );
        model.put( FormsConstants.MARK_ENTRY, _entry );
        model.put( FormsConstants.MARK_FORM, mockForm );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_QUESTION, _question );

        model.put( MARK_LIST, _entry.getFields( ) );

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( MARK_ENTRY_TYPE_SERVICE, EntryTypeServiceManager.getEntryTypeService( _entry ) );
        model.put( MARK_ACTION, "jsp/admin/plugins/forms/modules/template/ManageTemplatesStep.jsp" );
        
        if ( Boolean.TRUE.equals( _entry.getEntryType( ).getComment( ) ) )
        {
            setPageTitleProperty( PROPERTY_MODIFY_COMMENT_TITLE );
        }
        else
        {
            setPageTitleProperty( PROPERTY_MODIFY_QUESTION_TITLE );
        }

        boolean canBeFiltered = Arrays.asList( FILTERABLE ).contains( entryTypeService.getClass( ) );

        model.put( FormsConstants.MARK_CAN_BE_FILTERED, canBeFiltered );
        model.put( FormsConstants.MARK_BREADCRUMBS, AppTemplateService.getTemplate( TEMPLATE_BREADCRUMBS, request.getLocale( ), model ).getHtml( ) );
        model.put( FormsConstants.MARK_QUESTION_MODIFY_TEMPLATE,
                AppTemplateService.getTemplate( TEMPLATE_MODIFY_QUESTION, request.getLocale( ), model ).getHtml( ) );
        model.put( FormsConstants.MARK_ANONYMIZATION_HELP, entryTypeService.getAnonymizationHelpMessage( request.getLocale( ) ) );

        if ( entryTypeService instanceof EntryTypeComment )
        {
            Field fieldFile = _entry.getFieldByCode( IEntryTypeService.FIELD_DOWNLOADABLE_FILE );
            if ( fieldFile != null )
            {
                Map<String, String> additionnalData = new HashMap<>( );
                additionnalData.put( FileService.PARAMETER_RESOURCE_ID, String.valueOf( _entry.getIdResource( ) ) );
                additionnalData.put( FileService.PARAMETER_RESOURCE_TYPE, Form.RESOURCE_TYPE );
                additionnalData.put( FileService.PARAMETER_PROVIDER, _fileStoreProvider.getName( ) );

                model.put( EntryTypeCommentDisplayService.MARK_FILENAME, fieldFile.getTitle( ) );
                model.put( EntryTypeCommentDisplayService.MARK_URL_DOWNLOAD_BO,
                        _fileStoreProvider.getFileDownloadUrlBO( fieldFile.getValue( ), additionnalData ) );
            }
        }
        if ( Arrays.asList( ENTRY_TYPE_USER_REF_LIT ).contains( entryTypeService.getClass( ) ) )
        {
            model.put( FormsConstants.MARK_REFERENCE_LIST_SELECT, ReferenceListService.getInstance( ).getReferencesList( ) );
        }
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
     * Perform the entry modification
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    private String processQuestionUpdate( HttpServletRequest request ) throws CodeAlreadyExistsException
    {
        int nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );

        if ( _step == null || nIdStep != _step.getId( ) )
        {
            _step = TemplateStepHome.findByPrimaryKey( nIdStep );
        }

        String strIdQuestion = request.getParameter( FormsConstants.PARAMETER_ID_QUESTION );
        int nIdQuestion = -1;

        if ( StringUtils.isNotEmpty( strIdQuestion ) )
        {
            nIdQuestion = Integer.parseInt( strIdQuestion );
        }

        if ( _question == null || _question.getId( ) != nIdQuestion )
        {
            _question = TemplateQuestionHome.findByPrimaryKey( nIdQuestion );
        }

        _entry = TemplateEntryHome.findByPrimaryKey( _question.getIdEntry( ) );
        String strError = EntryTypeServiceManager.getEntryTypeService( _entry ).getRequestData( _entry, request, getLocale( ) );

        if ( strError != null )
        {
            return strError;
        }

        if ( checkCodeAlreadyExists( _entry.getCode( ), _step.getIdForm( ), _entry.getIdEntry( ) ) )
        {
            throw new CodeAlreadyExistsException( _entry.getCode( ) );
        }

        TemplateEntryHome.update( _entry );

        if ( _entry.getFields( ) != null )
        {
            for ( Field field : _entry.getFields( ) )
            {
                // Check if the field already exists in the database
                Field fieldStored = TemplateFieldHome.findByPrimaryKey( field.getIdField( ) );

                if ( fieldStored != null )
                {
                    // If it exists, update
                    TemplateFieldHome.update( field );
                }
                else
                {
                    // If it does not exist, create
                    TemplateFieldHome.create( field );
                }
            }
        }
        String strTitle = Boolean.TRUE.equals( _entry.getEntryType( ).getComment( ) ) ? I18nService.getLocalizedString( ENTRY_COMMENT_TITLE, getLocale( ) )
                : _entry.getTitle( );
        _question.setVisibleMultiviewGlobal( request.getParameter( FormsConstants.PARAMETER_MULTIVIEW_GLOBAL ) != null );
        _question.setVisibleMultiviewFormSelected( request.getParameter( FormsConstants.PARAMETER_MULTIVIEW_FORM_SELECTED ) != null );
        _question.setFiltrableMultiviewGlobal( request.getParameter( FormsConstants.PARAMETER_FILTERABLE_MULTIVIEW_GLOBAL ) != null );
        _question.setFiltrableMultiviewFormSelected( request.getParameter( FormsConstants.PARAMETER_FILTERABLE_MULTIVIEW_FORM_SELECTED ) != null );
        String columnTitle = request.getParameter( FormsConstants.PARAMETER_COLUMN_TITLE );
        columnTitle = StringUtils.isEmpty( columnTitle ) ? _question.getTitle( ) : columnTitle;
        _question.setColumnTitle( columnTitle );
        _question.setTitle( strTitle );
        _question.setCode( _entry.getCode( ) );
        _question.setDescription( _entry.getComment( ) );
        _question.setMultiviewColumnOrder( NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_MULTIVIEW_ORDER ), 0 ) );
        TemplateQuestionHome.update( _question );

        return null;

    }
}
