package fr.paris.lutece.plugins.forms.modules.template.service;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateControlHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateDisplayHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateGroupHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateQuestionHome;
import fr.paris.lutece.plugins.forms.service.AbstractFormDisplayService;
import fr.paris.lutece.plugins.forms.service.IFormDatabaseService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

public class TemplateDisplayService extends AbstractFormDisplayService
{
    public static final String BEAN_NAME = "forms-template.templateDisplayService";
    
    @Override
    public void deleteDisplayAndDescendants( int nIdDisplay )
    {
        FormDisplay formDisplayToDelete = TemplateDisplayHome.findByPrimaryKey( nIdDisplay );
        if ( formDisplayToDelete == null )
        {
            return;
        }
        
        int formDisplayCompositeId = formDisplayToDelete.getCompositeId( );
        List<FormDisplay> listChildrenDisplay = TemplateDisplayHome.getFormDisplayListByParent( formDisplayToDelete.getStepId( ),
                formDisplayToDelete.getId( ) );

        if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( formDisplayToDelete.getCompositeType( ) ) )
        {
            List<Control> listControl = TemplateControlHome.getControlByQuestionAndType( formDisplayCompositeId, ControlType.VALIDATION.getLabel( ) );

            for ( Control control : listControl )
            {
                TemplateControlHome.remove( control.getId( ) );
            }

            listControl = TemplateControlHome.getControlByQuestionAndType( formDisplayCompositeId, ControlType.CONDITIONAL.getLabel( ) );

            for ( Control control : listControl )
            {
                TemplateControlHome.remove( control.getId( ) );
            }
            TemplateControlHome.removeByControlTarget( formDisplayCompositeId, ControlType.CONDITIONAL );

            // Delete the Question and its Entry
            TemplateQuestionHome.remove( formDisplayCompositeId );
        }

        if ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( formDisplayToDelete.getCompositeType( ) ) )
        {
            TemplateGroupHome.remove( formDisplayCompositeId );
        }

        TemplateDisplayHome.remove( formDisplayToDelete.getId( ) );

        for ( FormDisplay childDisplay : listChildrenDisplay )
        {
            deleteDisplayAndDescendants( childDisplay.getId( ) );
        }
    }
    
    @Override
    protected List<FormDisplay> getFormDisplayListByParent( int nIdStep, int nIdParent )
    {
        return TemplateDisplayHome.getFormDisplayListByParent( nIdStep, nIdParent );
    }
    
    @Override
    protected ReferenceList getGroupDisplayReferenceListByStep( int nIdStep )
    {
        return TemplateDisplayHome.getGroupDisplayReferenceListByStep( nIdStep );
    }
    
    @Override
    protected IFormDatabaseService initFormDatabaseService( )
    {
        return SpringContextService.getBean( TemplateDatabaseService.BEAN_NAME );
    }
}
