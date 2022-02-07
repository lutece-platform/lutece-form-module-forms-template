package fr.paris.lutece.plugins.forms.modules.template.business;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.modules.template.service.rbac.TemplateRbacAction;
import fr.paris.lutece.portal.service.rbac.RBACResource;

public class Template extends Step implements RBACResource
{
    private static final long serialVersionUID = -69707611597729199L;

    public static final String RESOURCE_TYPE = "FORMS_TEMPLATE";
    
    private List<TemplateRbacAction> _listActions;
    
    @Override
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    @Override
    public String getResourceId( )
    {
        return StringUtils.EMPTY +  getId( );
    }
    
    /**
    *
    * @return a list of action can be use for the template
    */
   public List<TemplateRbacAction> getActions( )
   {
       return _listActions;
   }

   /**
    * set a list of action can be use for the template
    * 
    * @param actions
    *            a list of action must be use for the template
    */
   public void setActions( List<TemplateRbacAction> actions )
   {
       _listActions = actions;
   }

}
