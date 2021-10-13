/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
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
