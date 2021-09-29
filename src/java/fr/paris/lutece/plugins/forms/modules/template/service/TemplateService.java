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
import fr.paris.lutece.plugins.forms.modules.template.web.CompositeTemplateGroupDisplay;
import fr.paris.lutece.plugins.forms.modules.template.web.CompositeTemplateQuestionDisplay;
import fr.paris.lutece.plugins.forms.modules.template.web.TemplateDisplayTree;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;

public class TemplateService implements ITemplateService
{
    public static final String BEAN_NAME = "forms-template.templateService";
    
    @Override
    public List<ICompositeDisplay> getTemplateCompositeList( int nIdTemplate )
    {
        TemplateDisplayTree displayTree = new TemplateDisplayTree( nIdTemplate );
        return displayTree.getCompositeList( );
    }
    
    @Override
    public ICompositeDisplay templateDisplayToComposite( FormDisplay templateDisplay, int nIterationNumber )
    {
        ICompositeDisplay composite = null;
        if ( FormsConstants.COMPOSITE_GROUP_TYPE.equals( templateDisplay.getCompositeType( ) ) )
        {
            composite = new CompositeTemplateGroupDisplay( templateDisplay, nIterationNumber );

        }
        else
            if ( FormsConstants.COMPOSITE_QUESTION_TYPE.equals( templateDisplay.getCompositeType( ) ) )
            {
                composite = new CompositeTemplateQuestionDisplay( templateDisplay, nIterationNumber );
            }

        return composite;
    }
    
    @Override
    public void deleteDisplayAndDescendants( FormDisplay formDisplayToDelete )
    {
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
            deleteDisplayAndDescendants( childDisplay );
        }
    }
    
    @Override
    public void rebuildDisplayPositionSequence( List<FormDisplay> listDisplay )
    {
        int nUpdatedPosition = 0;
        for ( FormDisplay displayToUpdate : listDisplay )
        {
            nUpdatedPosition++;
            displayToUpdate.setDisplayOrder( nUpdatedPosition );
            TemplateDisplayHome.update( displayToUpdate );
        }
    }
}
