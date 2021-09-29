package fr.paris.lutece.plugins.forms.modules.template.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateDisplayHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateGroupHome;
import fr.paris.lutece.plugins.forms.modules.template.service.ITemplateService;
import fr.paris.lutece.plugins.forms.modules.template.service.TemplateService;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class CompositeTemplateGroupDisplay implements ICompositeDisplay
{
    // Properties
    private static final String PROPERTY_COMPOSITE_GROUP_ICON = "forms.composite.group.icon";
    private static final String DEFAULT_GROUP_ICON = "indent";
    
    private ITemplateService _templateService = SpringContextService.getBean( TemplateService.BEAN_NAME );
    
    private final List<ICompositeDisplay> _listChildren = new ArrayList<>( );
    private final FormDisplay _templateDisplay;
    private Group _group;
    private String _strIconName;
    private int _nIterationNumber;
    
    /**
    * Constructor
    * 
    * @param templateDisplay
    *            the template display
    * @param nIterationNumber
    *            the iteration number
    */
   public CompositeTemplateGroupDisplay( FormDisplay templateDisplay, int nIterationNumber )
   {
       _templateDisplay = templateDisplay;

       initComposite( );
   }
   
   /**
    * Initializes the composite
    * 
    * @param formResponse
    *            the form response
    * @param nIterationNumber
    *            the iteration number
    */
   private void initComposite( )
   {
       if ( !StringUtils.isEmpty( _templateDisplay.getCompositeType( ) ) )
       {
           _group = TemplateGroupHome.findByPrimaryKey( _templateDisplay.getCompositeId( ) );
           _strIconName = AppPropertiesService.getProperty( PROPERTY_COMPOSITE_GROUP_ICON, DEFAULT_GROUP_ICON );
       }

       List<FormDisplay> listTemplateDisplayChildren = TemplateDisplayHome.getFormDisplayListByParent( _templateDisplay.getStepId( ), _templateDisplay.getId( ) );
       _nIterationNumber = _group.getIterationMin( ) - 1;

       for ( int i = 0; i <= _nIterationNumber; i++ )
       {
           addChildren( listTemplateDisplayChildren, i );
       }
   }
   
   /**
    * Adds children from the specified list of template displays
    * 
    * @param listTemplateDisplayChildren
    *            the list of template displays
    * @param nIterationNumber
    *            the iteration number
    */
   private void addChildren( List<FormDisplay> listTemplateDisplayChildren, int nIterationNumber )
   {
       for ( FormDisplay templateDisplayChild : listTemplateDisplayChildren )
       {
           ICompositeDisplay composite = _templateService.templateDisplayToComposite( templateDisplayChild, nIterationNumber );
           _listChildren.add( composite );
       }
   }

    @Override
    public String getCompositeHtml( HttpServletRequest request, List<FormQuestionResponse> listFormQuestionResponse, Locale locale, DisplayType displayType )
    {
        // Only used when displaying the form in FO
        return null;
    }

    @Override
    public void iterate( int nIdTemplateDisplay )
    {
        // Only used when displaying the form in FO
    }

    @Override
    public void removeIteration( HttpServletRequest request, int nIdGroupParent, int nIndexIterationToRemove, FormResponse formResponse )
    {
        // Only used when displaying the form in FO
    }

    @Override
    public List<ICompositeDisplay> getCompositeList( )
    {
        List<ICompositeDisplay> listCompositeDisplay = new ArrayList<>( );
        listCompositeDisplay.add( this );

        for ( FormDisplay child : TemplateDisplayHome.getFormDisplayListByParent( _templateDisplay.getStepId( ), _templateDisplay.getId( ) ) )
        {
            ICompositeDisplay compositeChild = _templateService.templateDisplayToComposite( child, 0 );
            listCompositeDisplay.addAll( compositeChild.getCompositeList( ) );
        }
        return listCompositeDisplay;
    }

    @Override
    public String getTitle( )
    {
        String strTitle = "";
        if ( _group != null && StringUtils.isNotEmpty( _group.getTitle( ) ) )
        {
            strTitle = _group.getTitle( );
        }
        return strTitle;
    }

    @Override
    public String getType( )
    {
        return _group != null ? CompositeDisplayType.GROUP.getLabel( ) : StringUtils.EMPTY;
    }

    @Override
    public FormDisplay getFormDisplay( )
    {
        return _templateDisplay;
    }

    @Override
    public List<Control> getAllDisplayControls( )
    {
        List<Control> listDisplayControls = new ArrayList<>( );

        if ( _templateDisplay.getDisplayControl( ) != null )
        {
            listDisplayControls.add( _templateDisplay.getDisplayControl( ) );
        }

        for ( ICompositeDisplay child : _listChildren )
        {
            listDisplayControls.addAll( child.getAllDisplayControls( ) );
        }

        return listDisplayControls;
    }

    @Override
    public void addQuestions( List<Question> listQuestion )
    {
        for ( ICompositeDisplay child : _listChildren )
        {
            child.addQuestions( listQuestion );
        }
    }

    @Override
    public String getIcon( )
    {
        return _strIconName;
    }

    @Override
    public void addModel( Map<String, Object> model )
    {
        // Only used when displaying the form in FO
    }

    @Override
    public boolean isVisible( )
    {
        // Only used when displaying the form in FO
        return false;
    }

    @Override
    public ICompositeDisplay filter( List<Integer> listQuestionIds )
    {
     // Only used when displaying the form in FO
        return null;
    }
}
