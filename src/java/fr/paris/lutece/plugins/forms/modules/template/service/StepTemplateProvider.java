package fr.paris.lutece.plugins.forms.modules.template.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateStepHome;
import fr.paris.lutece.plugins.forms.modules.template.service.json.TemplateJsonService;
import fr.paris.lutece.plugins.forms.service.json.IStepTemplateProvider;
import fr.paris.lutece.plugins.forms.service.json.StepJsonData;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

/**
 * Implements {@link IStepTemplateProvider} 
 *
 */
public class StepTemplateProvider implements IStepTemplateProvider
{

    private static final String BEAN_NAME = "forms-template.stepTemplateProvider";
    
    @Override
    public ReferenceList getStepTemplateList( )
    {
        ReferenceList referenceList = new ReferenceList( );
        List<Step> templateList = TemplateStepHome.getAllTemplates( );
        for ( Step template : templateList )
        {
            referenceList.addItem( template.getId( ), template.getTitle( ) );
        }
        return referenceList;
    }

    @Override
    public StepJsonData getStepTemplateData( int idTemplate )
    {
        try
        {
            return TemplateJsonService.getInstance( ).jsonExportStepAsData( -1, idTemplate );
        }
        catch( JsonProcessingException e )
        {
            AppLogService.error( e.getMessage( ) );
            return null;
        }
    }

    @Override
    public String getProviderBeanName( )
    {
        return BEAN_NAME;
    }

}
