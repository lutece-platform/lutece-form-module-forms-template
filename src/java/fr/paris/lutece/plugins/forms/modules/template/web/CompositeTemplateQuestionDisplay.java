package fr.paris.lutece.plugins.forms.modules.template.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateQuestionHome;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;

public class CompositeTemplateQuestionDisplay implements ICompositeDisplay
{
    private Question _question;
    private final FormDisplay _formDisplay;
    private String _strIconName;
    
    /**
     * Constructor
     * 
     * @param templateDisplay
     *            the template display
     * @param nIterationNumber
     *            the iteration number
     */
    public CompositeTemplateQuestionDisplay( FormDisplay templateDisplay, int nIterationNumber )
    {
        _formDisplay = templateDisplay;

        initComposite( nIterationNumber );
    }
    
    /**
     * Initializes the composite
     * 
     * @param formResponse
     *            the form response
     * @param nIterationNumber
     *            the iteration number
     */
    private void initComposite( int nIterationNumber )
    {
        _question = TemplateQuestionHome.findByPrimaryKey( _formDisplay.getCompositeId( ) );

        if ( _question.getEntry( ) != null && _question.getEntry( ).getEntryType( ) != null )
        {
            _strIconName = _question.getEntry( ).getEntryType( ).getIconName( );
        }

        _question.setIterationNumber( nIterationNumber );
    }

    @Override
    public String getCompositeHtml( HttpServletRequest request, List<FormQuestionResponse> listFormQuestionResponse, Locale locale, DisplayType displayType )
    {
        // Only used when displaying the form in FO
        return null;
    }

    @Override
    public void iterate( int nIdFormDisplay )
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
        List<ICompositeDisplay> listICompositeDisplay = new ArrayList<>( );
        listICompositeDisplay.add( this );
        return listICompositeDisplay;
    }

    @Override
    public String getTitle( )
    {
        String strTitle = "";
        if ( _question != null && StringUtils.isNotEmpty( _question.getTitle( ) ) )
        {
            strTitle = _question.getTitle( );
        }
        return strTitle;
    }

    @Override
    public String getType( )
    {
        return _question != null ? CompositeDisplayType.QUESTION.getLabel( ) : StringUtils.EMPTY;
    }

    @Override
    public FormDisplay getFormDisplay( )
    {
        return _formDisplay;
    }

    @Override
    public List<Control> getAllDisplayControls( )
    {
        List<Control> listDisplayControls = new ArrayList<>( );

        if ( _formDisplay.getDisplayControl( ) != null )
        {
            listDisplayControls.add( _formDisplay.getDisplayControl( ) );
        }

        return listDisplayControls;
    }

    @Override
    public void addQuestions( List<Question> listQuestion )
    {
        listQuestion.add( _question );
    }

    @Override
    public boolean isVisible( )
    {
        return false;
    }

    @Override
    public ICompositeDisplay filter( List<Integer> listQuestionIds )
    {
        // Only used when displaying the form in FO
        return null;
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
}
