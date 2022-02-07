package fr.paris.lutece.plugins.forms.modules.template.service.rbac;

import java.util.List;
import java.util.Locale;

import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.modules.template.business.Template;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateStepHome;
import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

public class TemplateResourceIdService extends ResourceIdService
{
    /**
     * Permission to create a template
     */
    public static final String PERMISSION_CREATE = "CREATE";
    
    /** Permission for deleting a template */
    public static final String PERMISSION_DELETE = "DELETE";

    /** Permission for modifying a template */
    public static final String PERMISSION_MODIFY = "MODIFY";

    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "module.forms.template.permission.label.resourceType";
    private static final String PROPERTY_LABEL_CREATE = "module.forms.template.permission.label.create";
    private static final String PROPERTY_LABEL_DELETE = "module.forms.template.permission.label.delete";
    private static final String PROPERTY_LABEL_MODIFY = "module.forms.template.permission.label.modify";
    
    /** Creates a new instance of DocumentTypeResourceIdService */
    public TemplateResourceIdService( )
    {
        super( );
        setPluginName( FormsPlugin.PLUGIN_NAME );
    }
    
    @Override
    public void register( )
    {
        ResourceType resourceType = new ResourceType( );
        resourceType.setResourceIdServiceClass( TemplateResourceIdService.class.getName( ) );
        resourceType.setPluginName( FormsPlugin.PLUGIN_NAME );
        resourceType.setResourceTypeKey( Template.RESOURCE_TYPE );
        resourceType.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission permission = new Permission( );
        permission.setPermissionKey( PERMISSION_CREATE );
        permission.setPermissionTitleKey( PROPERTY_LABEL_CREATE );
        resourceType.registerPermission( permission );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_MODIFY );
        permission.setPermissionTitleKey( PROPERTY_LABEL_MODIFY );
        resourceType.registerPermission( permission );

        permission = new Permission( );
        permission.setPermissionKey( PERMISSION_DELETE );
        permission.setPermissionTitleKey( PROPERTY_LABEL_DELETE );
        resourceType.registerPermission( permission );

        ResourceTypeManager.registerResourceType( resourceType );
    }
    
    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        ReferenceList referenceList = new ReferenceList( );
        List<Template> list = TemplateStepHome.getAllTemplates( );
        for ( Template template: list )
        {
            referenceList.addItem( template.getId( ), template.getTitle( ) );
        }
        return referenceList;
    }
    
    @Override
    public String getTitle( String strId, Locale locale )
    {
        int nIdTemplate = -1;

        try
        {
            nIdTemplate = Integer.parseInt( strId );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }
        
        Step template = TemplateStepHome.findByPrimaryKey( nIdTemplate );
        return template.getTitle( );
    }
}
