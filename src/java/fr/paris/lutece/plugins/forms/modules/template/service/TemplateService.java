package fr.paris.lutece.plugins.forms.modules.template.service;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.FormDisplay;
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
}
