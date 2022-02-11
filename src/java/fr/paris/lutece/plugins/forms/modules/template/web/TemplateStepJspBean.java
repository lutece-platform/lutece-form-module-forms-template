/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.forms.modules.template.web;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.modules.template.business.Template;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateDisplayHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateStepHome;
import fr.paris.lutece.plugins.forms.modules.template.service.ITemplateService;
import fr.paris.lutece.plugins.forms.modules.template.service.TemplateDatabaseService;
import fr.paris.lutece.plugins.forms.modules.template.service.TemplateDisplayService;
import fr.paris.lutece.plugins.forms.modules.template.service.TemplateService;
import fr.paris.lutece.plugins.forms.modules.template.service.json.TemplateJsonService;
import fr.paris.lutece.plugins.forms.modules.template.service.rbac.TemplateRbacAction;
import fr.paris.lutece.plugins.forms.modules.template.service.rbac.TemplateResourceIdService;
import fr.paris.lutece.plugins.forms.service.IFormDatabaseService;
import fr.paris.lutece.plugins.forms.service.IFormDisplayService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;
import fr.paris.lutece.plugins.forms.web.admin.AbstractFormQuestionJspBean;
import fr.paris.lutece.plugins.forms.web.exception.CodeAlreadyExistsException;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.file.FileUtil;
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
    private static final String MARK_PERMISSION_CREATE_TEMPLATE = "permission_create_template";
    
    // Templates
    private static final String TEMPLATE_MANAGE_TEMPLATES_STEP = "/admin/plugins/forms/modules/template/manage_templates.html";
    private static final String TEMPLATE_CREATE_TEMPLATES_STEP = "/admin/plugins/forms/modules/template/create_template.html";
    private static final String TEMPLATE_MODIFY_TEMPLATES_STEP = "/admin/plugins/forms/modules/template/modify_template.html";
    private static final String TEMPLATE_CREATE_GROUP = "/admin/plugins/forms/modules/template/create_group.html";
    private static final String TEMPLATE_MODIFY_GROUP = "/admin/plugins/forms/modules/template/modify_group.html";
    private static final String TEMPLATE_CREATE_QUESTION = "/admin/plugins/forms/modules/template/create_question.html";
    private static final String TEMPLATE_MODIFY_QUESTION = "/admin/plugins/forms/modules/template/modify_question.html";
    private static final String TEMPLATE_BREADCRUMBS = "/admin/plugins/forms/modules/template/entries/all_entry_breadcrumbs.html";
    private static final String TEMPLATE_MOVE_COMPOSITE = "/admin/plugins/forms/modules/template/move_composite.html";

    // Views
    private static final String VIEW_MANAGE_TEMPLATES = "manageTemplates";
    private static final String VIEW_CREATE_TEMPLATE = "createTemplate";
    private static final String VIEW_MODIFY_TEMPLATE = "manageQuestions";
    private static final String VIEW_CONFIRM_REMOVE_COMPOSITE = "getConfirmRemoveComposite";
    private static final String VIEW_CONFIRM_REMOVE_TEMPLATE = "getConfirmRemoveTemplate";

    // Actions
    private static final String ACTION_CREATE_TEMPLATE = "createTemplate";
    private static final String ACTION_REMOVE_TEMPLATE = "removeTemplate";
    private static final String ACTION_EXPORT_FORM = "doExportJson";
    private static final String ACTION_IMPORT_STEP = "doImportJson";
    private static final String ACTION_DUPLICATE_TEMPLATE = "duplicateTemplate";

    // Properties
    private static final String PROPERTY_ITEM_PER_PAGE = "forms-template.itemsPerPage";
    private static final String PROPERTY_CREATE_GROUP_TITLE = "forms.create_group.title";

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_JSON_FILE = "json_file";
    private static final String PARAMETER_CANCEL = "cancel";
    
    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_CREATE_TEMPLATE = "module.forms.template.create_template.pageTitle";

    // Messages
    private static final String ERROR_TEMPLATE_NOT_IMPORTED = "module.forms.template.error.template.not.imported";
    private static final String ERROR_TEMPLATE_NOT_EXPORTED = "module.forms.template.error.template.not.exported";
    private static final String ERROR_TEMPLATE_NOT_COPIED = "module.forms.template.error.template.not.copied";
    private static final String INFO_TEMPLATE_CREATED = "module.forms.template.info.template.created";
    private static final String WARNING_CONFIRM_REMOVE_QUESTION = "module.forms.template.warning.deleteTemplate";
    private static final String INFO_DELETE_TEMPLATE_SUCCESSFUL = "module.forms.template.info.deleteTemplate.successful";
    
    private ITemplateService _templateService = SpringContextService.getBean( TemplateService.BEAN_NAME );
    protected Template _template;
    
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
        List<Template> listTemplates = TemplateStepHome.getAllTemplates( );

        int defaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, defaultItemsPerPage );

        Map<String, Object> model = getModel( );
        LocalizedPaginator<Template> paginator = new LocalizedPaginator<>( listTemplates, _nItemsPerPage, getJspManageForm( request ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale( ) );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, String.valueOf( _nItemsPerPage ) );

        model.put( MARK_TEMPLATE_LIST, paginator.getPageItems( ) );
        model.put( MARK_LOCALE, getLocale( ) );
        
        AdminUser adminUser = getUser( );
        model.put( MARK_PERMISSION_CREATE_TEMPLATE, RBACService.isAuthorized( Template.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, TemplateResourceIdService.PERMISSION_CREATE, (User) adminUser ) );
        
        List<TemplateRbacAction> listActions = SpringContextService.getBeansOfType( TemplateRbacAction.class );
        listActions = I18nService.localizeCollection( listActions, getLocale( ) );

        for ( Template template : paginator.getPageItems( ) )
        {
            List<TemplateRbacAction> listAuthorizedFormActions = (List<TemplateRbacAction>) RBACService.getAuthorizedActionsCollection( listActions, template, (User) getUser( ) );
            template.setActions( listAuthorizedFormActions );

        }

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_TEMPLATES_STEP, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Gets the confirmation page of template deletion
     * 
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete entry
     */
    @View( value = VIEW_CONFIRM_REMOVE_TEMPLATE )
    public String getConfirmRemoveTemplate( HttpServletRequest request )
    {

        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        if ( _template == null || ( nIdStep != FormsConstants.DEFAULT_ID_VALUE && nIdStep != _template.getId( ) ) )
        {
            _template = TemplateStepHome.findByPrimaryKey( nIdStep );
        }

        if ( _template == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TEMPLATE ) );
        url.addParameter( FormsConstants.PARAMETER_ID_STEP, nIdStep );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, WARNING_CONFIRM_REMOVE_QUESTION, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
        return redirect( request, strMessageUrl );
    }

    /**
     * Perform the template suppression
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_REMOVE_TEMPLATE )
    public String doRemoveTemplate( HttpServletRequest request )
    {

        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        if ( _template == null || ( nIdStep != FormsConstants.DEFAULT_ID_VALUE && nIdStep != _template.getId( ) ) )
        {
            _template = TemplateStepHome.findByPrimaryKey( nIdStep );
        }

        if ( _template == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        _templateService.deleteTemplate( nIdStep );
        addInfo( INFO_DELETE_TEMPLATE_SUCCESSFUL, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_TEMPLATES );
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
        _template = ( _template != null ) ? _template : new Template( );

        Map<String, Object> model = getModel( );

        model.put( FormsConstants.MARK_STEP, _template );
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
        populate( _template, request, request.getLocale( ) );

        TemplateStepHome.create( _template );
        addInfo( INFO_TEMPLATE_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_TEMPLATES );
    }

    @View( value = VIEW_MODIFY_TEMPLATE )
    public String getModifyTemplate( HttpServletRequest request )
    {
        int nIdTemplate = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );

        _template = TemplateStepHome.findByPrimaryKey( nIdTemplate );

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_STEP, _template );
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
        Map<String, Object> model = initCreateQuestionModel( request );
        if ( model == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        Form mockForm = new Form( );
        mockForm.setTitle( _template.getTitle( ) );
        model.put( FormsConstants.MARK_FORM, mockForm );
        model.put( MARK_ADD_FILE_COMMENT, false );
        model.put( MARK_ACTION, "jsp/admin/plugins/forms/modules/template/ManageTemplatesStep.jsp" );
        model.put( FormsConstants.MARK_BREADCRUMBS, AppTemplateService.getTemplate( TEMPLATE_BREADCRUMBS, request.getLocale( ), model ).getHtml( ) );
        model.put( FormsConstants.MARK_QUESTION_CREATE_TEMPLATE,
                AppTemplateService.getTemplate( TEMPLATE_CREATE_QUESTION, request.getLocale( ), model ).getHtml( ) );

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
        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _template.getId( ) );

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
                    : redirect( request, VIEW_MODIFY_QUESTION, FormsConstants.PARAMETER_ID_STEP, _template.getId( ), FormsConstants.PARAMETER_ID_QUESTION,
                            _question.getId( ) );

        }
        catch( CodeAlreadyExistsException e )
        {
            AppLogService.error( ERROR_CODE_EXISTS, e );
            addError( ERROR_QUESTION_CODE_ALREADY_EXISTS, getLocale( ) );
            return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _template.getId( ) );
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
        Map<String, Object> model = initModifyQuestionModel( request );

        Form mockForm = new Form( );
        mockForm.setTitle( _template.getTitle( ) );
        model.put( FormsConstants.MARK_FORM, mockForm );
        model.put( MARK_ADD_FILE_COMMENT, false );
        model.put( MARK_ACTION, "jsp/admin/plugins/forms/modules/template/ManageTemplatesStep.jsp" );
        model.put( FormsConstants.MARK_BREADCRUMBS, AppTemplateService.getTemplate( TEMPLATE_BREADCRUMBS, request.getLocale( ), model ).getHtml( ) );
        model.put( FormsConstants.MARK_QUESTION_MODIFY_TEMPLATE,
                AppTemplateService.getTemplate( TEMPLATE_MODIFY_QUESTION, request.getLocale( ), model ).getHtml( ) );

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
        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _template.getId( ) );
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

        if ( _template == null || ( nIdStep != FormsConstants.DEFAULT_ID_VALUE && nIdStep != _template.getId( ) ) )
        {
            _template = TemplateStepHome.findByPrimaryKey( nIdStep );
        }

        if ( _template == null )
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
            return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _template.getId( ) );
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
            return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _template.getId( ) );
        }

        getFormDisplayService( ).deleteDisplayAndDescendants( nIdDisplay );

        List<FormDisplay> listFormDisplaySibling = getFormDatabaseService( ).getFormDisplayListByParent( _formDisplay.getStepId( ),
                _formDisplay.getParentId( ) );
        getFormDisplayService( ).rebuildDisplayPositionSequence( listFormDisplaySibling );
        addInfo( INFO_DELETE_COMPOSITE_SUCCESSFUL, getLocale( ) );
        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _formDisplay.getStepId( ) );
    }

    /**
     * Gets the Move component page
     * 
     * @param request
     *            The HTTP request
     * @return The move component page
     */
    @View( value = VIEW_MOVE_COMPOSITE )
    public String getMoveComposite( HttpServletRequest request )
    {
        Map<String, Object> model = initMoveCompositeModel( request );
        if ( model == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MOVE_COMPOSITE, getLocale( ), model );
        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Process the FormDisplay moving action
     * 
     * @param request
     *            The HTTP request
     * @return The move component page
     */
    @Action( ACTION_MOVE_COMPOSITE )
    public String doMoveComposite( HttpServletRequest request )
    {

        boolean bGroupValidated = false;

        int nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), -1 );

        if ( nIdDisplay == INTEGER_MINUS_ONE )
        {
            redirectToViewManageForm( request );
        }

        if ( _formDisplay == null || _formDisplay.getId( ) != nIdDisplay )
        {
            _formDisplay = FormDisplayHome.findByPrimaryKey( nIdDisplay );
        }

        String strIsGroupValidated = request.getParameter( FormsConstants.PARAMETER_GROUP_VALIDATED );
        if ( StringUtils.isNotBlank( strIsGroupValidated ) )
        {
            bGroupValidated = BooleanUtils.toBoolean( strIsGroupValidated );
        }

        int nIdStepTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), INTEGER_MINUS_ONE );
        int nIdParentTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_PARENT ), INTEGER_MINUS_ONE );
        int nDisplayOrderTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_DISPLAY_ORDER ), INTEGER_MINUS_ONE );

        if ( ( nIdStepTarget == INTEGER_MINUS_ONE ) || ( nIdParentTarget == INTEGER_MINUS_ONE ) )
        {
            addError( ERROR_OCCURED_MOVING_COMPOSITE, getLocale( ) );
            return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _formDisplay.getStepId( ) );
        }

        if ( nIdParentTarget != _nIdParentTarget )
        {
            bGroupValidated = false;
        }

        if ( !bGroupValidated )
        {
            addError( ERROR_STEP_OR_GROUP_NOT_VALIDATED, getLocale( ) );
            return redirect( request, VIEW_MOVE_COMPOSITE, FormsConstants.PARAMETER_ID_DISPLAY, nIdDisplay );
        }

        moveDisplay( _formDisplay, nIdStepTarget, nIdParentTarget, nDisplayOrderTarget );

        addInfo( INFO_MOVE_COMPOSITE_SUCCESSFUL, getLocale( ) );

        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _formDisplay.getStepId( ) );
    }

    @Action( value = ACTION_DUPLICATE_QUESTION )
    public String doDuplicateQuestion( HttpServletRequest request )
    {
        String strReturnUrl = processQuestionDuplication( request );

        if ( strReturnUrl != null )
        {
            return redirect( request, strReturnUrl );
        }
        else
        {
            addInfo( INFO_QUESTION_DUPLICATED, getLocale( ) );
        }
        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _template.getId( ) );
    }

    @Action( ACTION_EXPORT_FORM )
    public void doExportJson( HttpServletRequest request )
    {
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            addError( ERROR_TEMPLATE_NOT_EXPORTED, getLocale( ) );
            return;
        }

        try
        {
            String content = TemplateJsonService.getInstance( ).jsonExportStep( -1, nId );
            Step step = TemplateStepHome.findByPrimaryKey( nId );
            download( content.getBytes( StandardCharsets.UTF_8 ), FileUtil.normalizeFileName( step.getTitle( ) ) + ".json", "application/json" );
        }
        catch( JsonProcessingException e )
        {
            AppLogService.error( e.getMessage( ) );
            addError( ERROR_TEMPLATE_NOT_EXPORTED, getLocale( ) );
        }
    }

    @Action( ACTION_IMPORT_STEP )
    public String doImportJson( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        FileItem fileItem = multipartRequest.getFile( PARAMETER_JSON_FILE );

        try
        {
            TemplateJsonService.getInstance( ).jsonImportStep( 0, new String( fileItem.get( ), StandardCharsets.UTF_8 ), getLocale( ) );
            addInfo( INFO_TEMPLATE_CREATED, getLocale( ) );
        }
        catch( JsonProcessingException e )
        {
            AppLogService.error( e.getMessage( ) );
            addError( ERROR_TEMPLATE_NOT_IMPORTED, getLocale( ) );
        }
        return redirectView( request, VIEW_MANAGE_TEMPLATES );
    }

    /**
     * Manages the copy of a template whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_DUPLICATE_TEMPLATE )
    public String doDuplicateTemplate( HttpServletRequest request )
    {
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            addError( ERROR_TEMPLATE_NOT_COPIED, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        try
        {
            String content = TemplateJsonService.getInstance( ).jsonExportStep( -1, nId );
            TemplateJsonService.getInstance( ).jsonImportStep( 0, content, getLocale( ) );
            addInfo( INFO_TEMPLATE_CREATED, getLocale( ) );
        }
        catch( JsonProcessingException e )
        {
            AppLogService.error( e.getMessage( ) );
            addError( ERROR_TEMPLATE_NOT_COPIED, getLocale( ) );
        }
        return redirectView( request, VIEW_MANAGE_TEMPLATES );
    }
    
    /**
     * Perform the template modification
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doModifyTemplate( HttpServletRequest request ) throws AccessDeniedException
    {
        
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
            _template = TemplateStepHome.findByPrimaryKey( nId );
            populate( _template, request, request.getLocale( ) );
            
            TemplateStepHome.update( _template );
            return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, nId );
        }
        return redirectView( request, VIEW_MANAGE_TEMPLATES );
        
    }

    @Override
    protected IFormDatabaseService initFormDatabaseService( )
    {
        return SpringContextService.getBean( TemplateDatabaseService.BEAN_NAME );
    }

    @Override
    protected IFormDisplayService initFormDisplayService( )
    {
        return SpringContextService.getBean( TemplateDisplayService.BEAN_NAME );
    }
}
