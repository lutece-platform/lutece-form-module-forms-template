package fr.paris.lutece.plugins.forms.modules.template.service;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;

public interface ITemplateService
{
    /**
     * Get the full children composite list of the given template
     * 
     * @param nIdTemplate
     *            The template primary key
     * @return the Html of the given template
     */
    List<ICompositeDisplay> getTemplateCompositeList( int nIdTemplate );
    
    /**
     * Get the right composite from the given templateDisplay
     * 
     * @param templateDisplay
     *            The templateDisplay
     * @param nIterationNumber
     *            the iteration number
     * @return the right composite
     */
    ICompositeDisplay templateDisplayToComposite( FormDisplay templateDisplay, int nIterationNumber );
}
