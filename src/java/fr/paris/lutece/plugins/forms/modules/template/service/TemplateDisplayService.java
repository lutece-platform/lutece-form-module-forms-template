/*
 * Copyright (c) 2002-2022, City of Paris
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
