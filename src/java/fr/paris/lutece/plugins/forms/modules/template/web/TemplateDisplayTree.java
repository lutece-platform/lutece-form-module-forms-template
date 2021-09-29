package fr.paris.lutece.plugins.forms.modules.template.web;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateDisplayHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateStepHome;
import fr.paris.lutece.plugins.forms.modules.template.service.ITemplateService;
import fr.paris.lutece.plugins.forms.modules.template.service.TemplateService;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class TemplateDisplayTree
{
    private final List<ICompositeDisplay> _listChildren = new ArrayList<>( );
    private final List<ICompositeDisplay> _listICompositeDisplay = new ArrayList<>( );
    private List<Control> _listDisplayControls = new ArrayList<>( );

    private ITemplateService _templateService = SpringContextService.getBean( TemplateService.BEAN_NAME );
    private Step _step;
    
    /**
     * Constructor
     * 
     * @param nIdTemplate
     *            the template identifier
     */
    public TemplateDisplayTree( int nIdTemplate )
    {
        initTemplateTree( nIdTemplate );
    }
    
    /**
     * Initialize the composite tree
     * 
     * @param nIdTemplate
     *            The template primary key
     */
    public void initTemplateTree( int nIdTemplate )
    {
        _step = TemplateStepHome.findByPrimaryKey( nIdTemplate );

        if ( _step != null )
        {
            List<FormDisplay> listTemplateDisplay = TemplateDisplayHome.getFormDisplayListByParent( nIdTemplate, 0 );
            
            _listDisplayControls = new ArrayList<>( );
            for ( FormDisplay formDisplayChild : listTemplateDisplay )
            {
                ICompositeDisplay composite = _templateService.templateDisplayToComposite( formDisplayChild, 0 );
                _listChildren.add( composite );
                _listDisplayControls.addAll( composite.getAllDisplayControls( ) );
            }
        }
    }
    
    /**
     * Build and return all the composite display of the tree as a flat List
     * 
     * @return the list of composite display
     */
    public List<ICompositeDisplay> getCompositeList( )
    {
        for ( ICompositeDisplay child : _listChildren )
        {
            _listICompositeDisplay.addAll( child.getCompositeList( ) );
        }
        return _listICompositeDisplay;
    }
}
