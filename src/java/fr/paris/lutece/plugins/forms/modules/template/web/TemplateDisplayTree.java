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
