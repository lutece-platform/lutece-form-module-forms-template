package fr.paris.lutece.plugins.forms.modules.template.service.json;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateDisplayHome;
import fr.paris.lutece.plugins.forms.modules.template.service.TemplateDatabaseService;
import fr.paris.lutece.plugins.forms.service.json.AbstractFormJsonService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class TemplateJsonService extends AbstractFormJsonService
{

    public static final TemplateJsonService INSTANCE = new TemplateJsonService( );

    private TemplateJsonService( )
    {
        super( SpringContextService.getBean( TemplateDatabaseService.BEAN_NAME ) );
    }
    
    public static TemplateJsonService getInstance( )
    {
        return INSTANCE;
    }
    
    @Override
    protected List<FormDisplay> getAllFormDisplays( int idForm, int idStep )
    {
        return TemplateDisplayHome.getFormDisplayByTemplate( idStep );
    }
}
