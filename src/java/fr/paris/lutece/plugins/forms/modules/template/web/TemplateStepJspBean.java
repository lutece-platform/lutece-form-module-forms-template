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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormLayout;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.modules.template.business.Template;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateDisplayHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateStepHome;
import fr.paris.lutece.plugins.forms.modules.template.service.TemplateDatabaseService;
import fr.paris.lutece.plugins.forms.modules.template.service.TemplateDisplayService;
import fr.paris.lutece.plugins.forms.modules.template.service.TemplateService;
import fr.paris.lutece.plugins.forms.modules.template.service.json.TemplateJsonService;
import fr.paris.lutece.plugins.forms.modules.template.service.rbac.TemplateRbacAction;
import fr.paris.lutece.plugins.forms.modules.template.service.preview.StepTemplatePreview;
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
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.file.FileUtil;
import fr.paris.lutece.util.html.IPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.util.url.UrlItem;

@SessionScoped
@Named
@Controller( controllerJsp = "ManageTemplatesStep.jsp", controllerPath = "jsp/admin/plugins/forms/modules/template/", right = "TEMPLATE_STEP_MANAGEMENT", securityTokenEnabled = true )
public class TemplateStepJspBean extends AbstractFormQuestionJspBean
{
    private static final long serialVersionUID = -7133586597439667057L;

    // Markers
    private static final String MARK_TEMPLATE_LIST = "template_list";
    private static final String MARK_TEMPLATE_PREVIEWS = "template_previews";
    private static final String MARK_PERMISSION_CREATE_TEMPLATE = "permission_create_template";
    private static final String MARK_WEBAPP_URL = "webapp_url";

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
    private static final String VIEW_MODIFY_TEMPLATE = "modifyTemplate";
    private static final String JSP_MANAGE_TEMPLATES_STEP = "jsp/admin/plugins/forms/modules/template/ManageTemplatesStep.jsp";
    private static final String VIEW_CONFIRM_REMOVE_COMPOSITE = "getConfirmRemoveComposite";
    private static final String VIEW_CONFIRM_REMOVE_TEMPLATE = "getConfirmRemoveTemplate";

    // Actions
    private static final String ACTION_CREATE_TEMPLATE = "createTemplate";
    private static final String ACTION_MODIFY_TEMPLATE = "modifyTemplate";
    private static final String ACTION_REMOVE_TEMPLATE = "removeTemplate";
    private static final String ACTION_EXPORT_FORM = "doExportJson";
    private static final String ACTION_IMPORT_STEP = "doImportJson";
    private static final String ACTION_CANCEL_IMPORT = "doCancelImport";
    private static final String ACTION_DUPLICATE_TEMPLATE = "duplicateTemplate";

    // Properties
    private static final String PROPERTY_ITEM_PER_PAGE = "forms-template.itemsPerPage";
    private static final String PROPERTY_ROOT_LEVEL_LABEL = "forms.moveComposite.rootLevel.label";
    private static final String PARAMETER_ACTION = "action";
    private static final String PARAMETER_VIEW = "view";
    private static final String PROPERTY_CREATE_GROUP_TITLE = "forms.create_group.title";

    // Parameters
    private static final String PARAMETER_JSON_FILE = "json_file";
    private static final String PARAMETER_CANCEL = "cancel";

    // Validation
    private static final String TEMPLATE_VALIDATION_ATTRIBUTES_PREFIX = "forms.model.entity.step.attribute.";

    // Content types
    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_CREATE_TEMPLATE = "module.forms.template.create_template.pageTitle";

    // Messages
    private static final String ERROR_TEMPLATE_NOT_IMPORTED = "module.forms.template.error.template.not.imported";
    private static final String ERROR_TEMPLATE_NOT_EXPORTED = "module.forms.template.error.template.not.exported";
    private static final String ERROR_TEMPLATE_NOT_COPIED = "module.forms.template.error.template.not.copied";
    private static final String ERROR_TEMPLATE_NOT_FOUND = "module.forms.template.error.template.not.found";
    private static final String INFO_TEMPLATE_CREATED = "module.forms.template.info.template.created";
    private static final String WARNING_CONFIRM_REMOVE_TEMPLATE = "module.forms.template.warning.deleteTemplate";
    private static final String INFO_DELETE_TEMPLATE_SUCCESSFUL = "module.forms.template.info.deleteTemplate.successful";

    @Inject
    private TemplateService _templateService;
    protected Template _template;

    @Inject
    private Instance<TemplateRbacAction>_formsTemplateRbacAction;
    
    @Inject
    private TemplateDatabaseService _templateDatabaseService;
    
    @Inject
    private TemplateDisplayService _templateDisplayService;

    @Inject
    private TemplateJsonService _templateJsonService;

    @Inject
    @Pager( listBookmark = MARK_TEMPLATE_LIST, defaultItemsPerPage = PROPERTY_ITEM_PER_PAGE, baseUrl = JSP_MANAGE_TEMPLATES_STEP )
    private IPager<Template, Template> _pager;
    
    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TEMPLATES, defaultView = true, securityTokenAction = ACTION_IMPORT_STEP )
    public String getManageTemplates( Models model, HttpServletRequest request )
    {
        IPaginator<Template> paginator = _pager.withListItem( TemplateStepHome.getAllTemplates( ) ).populateModels( request, model, getLocale( ) );

        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_LINK_TOKENS, Map.of( ACTION_DUPLICATE_TEMPLATE, getSecurityToken( request, ACTION_DUPLICATE_TEMPLATE ) ) );

        AdminUser adminUser = getUser( );
        model.put( MARK_PERMISSION_CREATE_TEMPLATE,
                RBACService.isAuthorized( Template.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, TemplateResourceIdService.PERMISSION_CREATE, (User) adminUser ) );

        List<TemplateRbacAction> listActions = _formsTemplateRbacAction.stream( ).toList( );
        Map<String, StepTemplatePreview> mapPreviews = new HashMap<>( );

        for ( Template template : paginator.getPageItems( ) )
        {
            mapPreviews.put( String.valueOf( template.getId( ) ), _templateService.getTemplatePreview( template.getId( ) ) );
            List<TemplateRbacAction> listAuthorizedFormActions = (List<TemplateRbacAction>) RBACService.getAuthorizedActionsCollection( listActions, template,
                    (User) getUser( ) );
            template.setActions( listAuthorizedFormActions );
        }
        model.put( MARK_TEMPLATE_PREVIEWS, mapPreviews );

        return getPage( StringUtils.EMPTY, TEMPLATE_MANAGE_TEMPLATES_STEP );
    }

    /**
     * Gets the confirmation page of template deletion
     * 
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete entry
     */
    @View( value = VIEW_CONFIRM_REMOVE_TEMPLATE, securityTokenAction = ACTION_REMOVE_TEMPLATE )
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

        String strMessageUrl = AdminMessageService.getMessageUrl( request, WARNING_CONFIRM_REMOVE_TEMPLATE, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
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
    public String getCreateTemplate( Models model, HttpServletRequest request ) throws AccessDeniedException
    {
        _template = new Template( );

        model.put( FormsConstants.MARK_STEP, _template );
        return getPage( PROPERTY_PAGE_TITLE_CREATE_TEMPLATE, TEMPLATE_CREATE_TEMPLATES_STEP );
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
        if ( _template == null )
        {
            _template = new Template( );
        }
        populate( _template, request, request.getLocale( ) );

        if ( !validateBean( _template, TEMPLATE_VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_TEMPLATE );
        }

        TemplateStepHome.create( _template );
        addInfo( INFO_TEMPLATE_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_TEMPLATES );
    }

    @View( value = VIEW_MODIFY_TEMPLATE )
    public String getModifyTemplate( Models model, HttpServletRequest request )
    {
        int nIdTemplate = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );

        _template = TemplateStepHome.findByPrimaryKey( nIdTemplate );
        if ( _template == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, ERROR_TEMPLATE_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        model.put( FormsConstants.MARK_STEP, _template );
        model.put( FormsConstants.MARK_ENTRY_TYPE_REF_LIST, FormsEntryUtils.initListEntryType( ) );
        model.put( FormsConstants.MARK_ID_PARENT, _nIdParentSelected );

        List<ICompositeDisplay> listICompositeDisplay = _templateService.getTemplateCompositeList( nIdTemplate );
        model.put( FormsConstants.MARK_COMPOSITE_LIST, listICompositeDisplay );
        model.put( MARK_LINK_TOKENS, Map.of( ACTION_DUPLICATE_QUESTION, getSecurityToken( request, ACTION_DUPLICATE_QUESTION ), ACTION_MOVE_COMPOSITE,
                getSecurityToken( request, ACTION_MOVE_COMPOSITE ) ) );

        ReferenceList listMoveTargets = new ReferenceList( );
        listMoveTargets.addItem( 0, I18nService.getLocalizedString( PROPERTY_ROOT_LEVEL_LABEL, getLocale( ) ) );
        listMoveTargets.addAll( TemplateDisplayHome.getGroupDisplayReferenceListByStep( nIdTemplate ) );
        model.put( FormsConstants.MARK_LIST_GROUPS, listMoveTargets );

        return getPage( StringUtils.EMPTY, TEMPLATE_MODIFY_TEMPLATES_STEP );
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
    @Action( value = ACTION_MODIFY_TEMPLATE )
    public String doModifyTemplate( HttpServletRequest request ) throws AccessDeniedException
    {

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
            _template = TemplateStepHome.findByPrimaryKey( nId );
            if ( _template == null )
            {
                return redirectView( request, VIEW_MANAGE_TEMPLATES );
            }
            populate( _template, request, request.getLocale( ) );

            if ( !validateBean( _template, TEMPLATE_VALIDATION_ATTRIBUTES_PREFIX ) )
            {
                return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, nId );
            }

            TemplateStepHome.update( _template );

            return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, nId );
        }
        return redirectView( request, VIEW_MANAGE_TEMPLATES );
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

        Models model = initCreateGroupModel( request );
        if ( model == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );

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
        Models model = initCreateQuestionModel( request );
        if ( model == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        Form mockForm = new Form( );
        mockForm.setTitle( _step.getTitle( ) );
        model.put( FormsConstants.MARK_FORM, mockForm );
        model.put( MARK_ADD_FILE_COMMENT, false );
        model.put( MARK_ACTION, "jsp/admin/plugins/forms/modules/template/ManageTemplatesStep.jsp" );
        model.put( FormsConstants.MARK_BREADCRUMBS, HtmlMarkup.of( AppTemplateService.getTemplate( TEMPLATE_BREADCRUMBS, request.getLocale( ), model ).getHtml( ) ) );
        model.put( FormsConstants.MARK_QUESTION_CREATE_TEMPLATE,
                HtmlMarkup.of( AppTemplateService.getTemplate( TEMPLATE_CREATE_QUESTION, request.getLocale( ), model ).getHtml( ) ) );

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( _entry );
        HtmlTemplate template = getTemplateWithPageToken( entryTypeService.getTemplateCreate( _entry, false ), getLocale( ), request );
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
            String strReturnUrl = processQuestionCreation( request, VIEW_MODIFY_TEMPLATE, getErrorReturnUrlOnCreation( request ) );

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
            AppLogService.error( "{}{}", ERROR_CODE_EXISTS, e.getCode( ), e );
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
    @Action( value = ACTION_CREATE_QUESTION_AND_MANAGE_ENTRIES, securityTokenAction = ACTION_CREATE_QUESTION )
    public String doCreateQuestionAndManageEntries( HttpServletRequest request )
    {
        try
        {
            String strReturnUrl = processQuestionCreation( request, VIEW_MODIFY_TEMPLATE, getErrorReturnUrlOnCreation( request ) );
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
        Models model = initModifyGroupModel( request );
        if ( model == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );

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
        Models model = initModifyQuestionModel( request );
        if ( model == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        Form mockForm = new Form( );
        mockForm.setTitle( _step.getTitle( ) );
        model.put( FormsConstants.MARK_FORM, mockForm );
        model.put( MARK_ADD_FILE_COMMENT, false );
        model.put( MARK_ACTION, "jsp/admin/plugins/forms/modules/template/ManageTemplatesStep.jsp" );
        model.put( FormsConstants.MARK_BREADCRUMBS, HtmlMarkup.of( AppTemplateService.getTemplate( TEMPLATE_BREADCRUMBS, request.getLocale( ), model ).getHtml( ) ) );
        model.put( FormsConstants.MARK_QUESTION_MODIFY_TEMPLATE,
                HtmlMarkup.of( AppTemplateService.getTemplate( TEMPLATE_MODIFY_QUESTION, request.getLocale( ), model ).getHtml( ) ) );

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( _entry );

        HtmlTemplate template = getTemplateWithPageToken( entryTypeService.getTemplateModify( _entry, false ), getLocale( ), request );

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
            String strReturnUrl = processQuestionUpdate( request, getErrorReturnUrlOnModification( request ) );

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
            AppLogService.error( "{}{}", ERROR_CODE_EXISTS, e.getCode( ), e );
            addError( ERROR_QUESTION_CODE_ALREADY_EXISTS, getLocale( ) );
        }
        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );
    }

    /**
     * Perform the Question update with its Entry and redirect to the ModifyQuestion view
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     * @throws AccessDeniedException
     */
    @Action( value = ACTION_SAVE_QUESTION, securityTokenAction = ACTION_MODIFY_QUESTION )
    public String doSaveQuestion( HttpServletRequest request ) throws AccessDeniedException
    {
        try
        {
            String strReturnUrl = processQuestionUpdate( request, getErrorReturnUrlOnModification( request ) );

            if ( strReturnUrl != null )
            {
                return strReturnUrl;
            }
        }
        catch( CodeAlreadyExistsException e )
        {
            AppLogService.error( ERROR_CODE_EXISTS, e );
            addError( ERROR_QUESTION_CODE_ALREADY_EXISTS, getLocale( ) );
        }
        return redirect( request, VIEW_MODIFY_QUESTION, FormsConstants.PARAMETER_ID_STEP, _step.getId( ), FormsConstants.PARAMETER_ID_QUESTION,
                _question.getId( ) );
    }
    
    /**
     * Gets the confirmation page of question/group deletion
     * 
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete entry
     */
    @View( value = VIEW_CONFIRM_REMOVE_COMPOSITE, securityTokenAction = ACTION_REMOVE_COMPOSITE )
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

        String strMessageUrl = AdminMessageService.getMessageUrl( request, strMessage, new Object [ ] {
                getFormDisplayService( ).getDisplayTitle( _formDisplay )
        }, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
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
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        getFormDisplayService( ).deleteDisplayAndDescendants( nIdDisplay );

        List<FormDisplay> listFormDisplaySibling = getFormDatabaseService( ).getFormDisplayListByParent( _formDisplay.getStepId( ),
                _formDisplay.getParentId( ) );
        getFormDisplayService( ).rebuildDisplayPositionSequence( listFormDisplaySibling );
        addInfo( INFO_DELETE_COMPOSITE_SUCCESSFUL, getLocale( ) );
        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _formDisplay.getStepId( ) );
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
        int nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), -1 );

        if ( nIdDisplay == INTEGER_MINUS_ONE )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        if ( _formDisplay == null || _formDisplay.getId( ) != nIdDisplay )
        {
            _formDisplay = _templateDatabaseService.findDisplayByPrimaryKey( nIdDisplay );
        }

        if ( _formDisplay == null )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        int nIdStepTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), INTEGER_MINUS_ONE );
        int nIdParentTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_PARENT ), INTEGER_MINUS_ONE );
        int nDisplayOrderTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_DISPLAY_ORDER ), INTEGER_MINUS_ONE );

        if ( ( nIdStepTarget == INTEGER_MINUS_ONE ) || ( nIdParentTarget == INTEGER_MINUS_ONE ) )
        {
            addError( ERROR_OCCURED_MOVING_COMPOSITE, getLocale( ) );
            return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _formDisplay.getStepId( ) );
        }

        if ( !isValidMoveTarget( _formDisplay, nIdStepTarget, nIdParentTarget, nDisplayOrderTarget ) )
        {
            addError( ERROR_STEP_OR_GROUP_NOT_VALIDATED, getLocale( ) );
            return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _formDisplay.getStepId( ) );
        }

        // A template does not store any column: its displays all sit in the first one
        moveDisplay( _formDisplay, nIdStepTarget, nIdParentTarget, FormLayout.FIRST_COLUMN, nDisplayOrderTarget );

        addInfo( INFO_MOVE_COMPOSITE_SUCCESSFUL, getLocale( ) );

        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, _formDisplay.getStepId( ) );
    }

    /**
     * Tells whether a display can be moved to the given place: a template holds a single step, so the target step is the one of the display, the target
     * parent is the root level or a group of that step that is neither the display nor one of its descendants, and the position is one of those the parent
     * offers
     *
     * @param formDisplay
     *            the display to move
     * @param nIdStepTarget
     *            the target step
     * @param nIdParentTarget
     *            the target parent display, 0 for the root level
     * @param nDisplayOrderTarget
     *            the target position within the parent
     * @return true if the move is allowed
     */
    private boolean isValidMoveTarget( FormDisplay formDisplay, int nIdStepTarget, int nIdParentTarget, int nDisplayOrderTarget )
    {
        if ( nIdStepTarget != formDisplay.getStepId( ) )
        {
            return false;
        }

        String strParent = String.valueOf( nIdParentTarget );
        boolean bValidParent = getFormDisplayService( ).getTargetGroupDisplayListByStep( nIdStepTarget, formDisplay, getLocale( ) ).stream( )
                .anyMatch( item -> strParent.equals( item.getCode( ) ) );
        if ( !bValidParent )
        {
            return false;
        }

        boolean bMoveToDifferentGroup = formDisplay.getParentId( ) != nIdParentTarget;
        String strPosition = String.valueOf( nDisplayOrderTarget );
        return getFormDisplayService( ).getlistAvailablePositionsInGroup( nIdStepTarget, nIdParentTarget, bMoveToDifferentGroup, getLocale( ) ).stream( )
                .anyMatch( item -> strPosition.equals( item.getCode( ) ) );
    }

    /**
     * Duplicate the question whose identifier is in the request
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( value = ACTION_DUPLICATE_QUESTION )
    public String doDuplicateQuestion( HttpServletRequest request )
    {
        int nIdTemplate = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        int nIdQuestion = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_TARGET ), FormsConstants.DEFAULT_ID_VALUE );

        if ( !_templateService.duplicateQuestion( nIdTemplate, nIdQuestion ) )
        {
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }
        addInfo( INFO_QUESTION_DUPLICATED, getLocale( ) );

        return redirect( request, VIEW_MODIFY_TEMPLATE, FormsConstants.PARAMETER_ID_STEP, nIdTemplate );
    }

    /**
     * Build the URL to go back to after an error on question creation
     * 
     * @param request
     *            The HTTP request
     * @return The URL of the template modification view
     */
    private String getErrorReturnUrlOnCreation( HttpServletRequest request )
    {
        UrlItem errorReturnUrl = new UrlItem( JSP_MANAGE_TEMPLATES_STEP );
        errorReturnUrl.addParameter( FormsConstants.PARAMETER_TARGET_VIEW, VIEW_MODIFY_TEMPLATE );
        errorReturnUrl.addParameter( FormsConstants.PARAMETER_ID_STEP,
                NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE ) );

        return errorReturnUrl.getUrl( );
    }

    /**
     * Build the URL to go back to after an error on question modification
     * 
     * @param request
     *            The HTTP request
     * @return The URL of the question modification view
     */
    private String getErrorReturnUrlOnModification( HttpServletRequest request )
    {
        UrlItem errorReturnUrl = new UrlItem( JSP_MANAGE_TEMPLATES_STEP );
        errorReturnUrl.addParameter( FormsConstants.PARAMETER_TARGET_VIEW, VIEW_MODIFY_QUESTION );
        errorReturnUrl.addParameter( FormsConstants.PARAMETER_ID_STEP,
                NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE ) );
        errorReturnUrl.addParameter( FormsConstants.PARAMETER_ID_QUESTION,
                NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_QUESTION ), FormsConstants.DEFAULT_ID_VALUE ) );

        return errorReturnUrl.getUrl( );
    }

    /**
     * Download the template whose identifier is in the request as a JSON file. A read: the CSRF token is not required
     * 
     * @param request
     *            The HTTP request
     * @return null once the file is written, or the URL of the template list with an error when the template cannot be exported
     */
    @Action( value = ACTION_EXPORT_FORM, securityTokenDisabled = true )
    public String doExportJson( HttpServletRequest request )
    {
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        Step step = TemplateStepHome.findByPrimaryKey( nId );

        if ( step == null )
        {
            addError( ERROR_TEMPLATE_NOT_EXPORTED, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }

        try
        {
            String content = _templateJsonService.jsonExportStep( -1, nId );
            download( content, FileUtil.normalizeFileName( step.getTitle( ) ) + ".json", CONTENT_TYPE_JSON );
            return null;
        }
        catch( JsonProcessingException e )
        {
            AppLogService.error( "Forms template : the template {} could not be exported", nId, e );
            addError( ERROR_TEMPLATE_NOT_EXPORTED, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_TEMPLATES );
        }
    }

    /**
     * Import a template from the JSON file posted
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_IMPORT_STEP )
    public String doImportJson( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartItem fileItem = multipartRequest.getFile( PARAMETER_JSON_FILE );

        try
        {
            _templateJsonService.jsonImportStep( 0, new String( fileItem.get( ), StandardCharsets.UTF_8 ), getLocale( ) );
            addInfo( INFO_TEMPLATE_CREATED, getLocale( ) );
        }
        catch( JsonProcessingException | RuntimeException e )
        {
            AppLogService.error( "Forms template : the template could not be imported", e );
            addError( ERROR_TEMPLATE_NOT_IMPORTED, getLocale( ) );
        }
        return redirectView( request, VIEW_MANAGE_TEMPLATES );
    }

    /**
     * Cancel the import
     * 
     * @param request
     *            The HTTP request
     * @return The URL of the template list
     */
    @Action( value = ACTION_CANCEL_IMPORT, securityTokenDisabled = true )
    public String doCancelImport( HttpServletRequest request )
    {
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
            String content = _templateJsonService.jsonExportStep( -1, nId );
            _templateJsonService.jsonImportStep( 0, content, getLocale( ) );
            addInfo( INFO_TEMPLATE_CREATED, getLocale( ) );
        }
        catch( JsonProcessingException | RuntimeException e )
        {
            AppLogService.error( "Forms template : the template {} could not be copied", nId, e );
            addError( ERROR_TEMPLATE_NOT_COPIED, getLocale( ) );
        }
        return redirectView( request, VIEW_MANAGE_TEMPLATES );
    }

    /**
     * {@inheritDoc} The user must hold the permission on the template the request names: CREATE to create or import one, DELETE to remove one, MODIFY for
     * every other view or action carrying a template identifier
     */
    @Override
    public String processController( HttpServletRequest request, HttpServletResponse response ) throws AccessDeniedException
    {
        String strInvoked = getInvokedName( request, PARAMETER_ACTION );
        if ( strInvoked == null )
        {
            strInvoked = getInvokedName( request, PARAMETER_VIEW );
        }

        if ( ACTION_CREATE_TEMPLATE.equals( strInvoked ) || ACTION_IMPORT_STEP.equals( strInvoked ) )
        {
            checkUserPermission( Template.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, TemplateResourceIdService.PERMISSION_CREATE, request, null );
        }
        else
        {
            int nIdTemplate = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
            if ( nIdTemplate != FormsConstants.DEFAULT_ID_VALUE )
            {
                String strPermission = ( ACTION_REMOVE_TEMPLATE.equals( strInvoked ) || VIEW_CONFIRM_REMOVE_TEMPLATE.equals( strInvoked ) )
                        ? TemplateResourceIdService.PERMISSION_DELETE
                        : TemplateResourceIdService.PERMISSION_MODIFY;
                checkUserPermission( Template.RESOURCE_TYPE, String.valueOf( nIdTemplate ), strPermission, request, null );
            }
        }

        return super.processController( request, response );
    }

    /**
     * Gives the name of the action or view the request invokes, either as the value of the parameter or as the suffix of a parameter named
     * prefix_name
     * 
     * @param request
     *            The HTTP request
     * @param strPrefix
     *            action or view
     * @return the name, or null when the request invokes none
     */
    private static String getInvokedName( HttpServletRequest request, String strPrefix )
    {
        String strName = request.getParameter( strPrefix );
        if ( StringUtils.isNotEmpty( strName ) )
        {
            return strName;
        }
        String strNamePrefix = strPrefix + "_";
        return request.getParameterMap( ).keySet( ).stream( ).filter( p -> p.startsWith( strNamePrefix ) ).map( p -> p.substring( strNamePrefix.length( ) ) )
                .findFirst( ).orElse( null );
    }

    /**
     * {@inheritDoc} The fallback page of this bean is the list of the templates, not the list of the forms
     */
    @Override
    protected String getJspManageForm( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_TEMPLATES_STEP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFormDatabaseService initFormDatabaseService( )
    {
        return _templateDatabaseService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IFormDisplayService initFormDisplayService( )
    {
        return _templateDisplayService;
    }
}
