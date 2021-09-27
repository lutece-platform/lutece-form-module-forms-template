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
package fr.paris.lutece.plugins.forms.modules.template.business;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class provides instances management methods (create, find, ...) for FormDisplay objects
 */
public final class TemplateDisplayHome
{
    // Static variable pointed at the DAO instance
    private static ITemplateFormDisplayDAO _dao = SpringContextService.getBean( "forms-template.templateDisplayDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TemplateDisplayHome( )
    {
    }

    /**
     * Create an instance of the formDisplay class
     * 
     * @param formDisplay
     *            The instance of the FormDisplay which contains the informations to store
     */
    public static void create( FormDisplay formDisplay )
    {
        _dao.insert( formDisplay, _plugin );
    }

    /**
     * Update of the formDisplay which is specified in parameter
     * 
     * @param formDisplay
     *            The instance of the FormDisplay which contains the data to store
     */
    public static void update( FormDisplay formDisplay )
    {
        _dao.store( formDisplay, _plugin );
    }

    /**
     * Remove the formDisplay whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formDisplay Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a formDisplay whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formDisplay primary key
     * @return an instance of FormDisplay
     */
    public static FormDisplay findByPrimaryKey( int nKey )
    {
        FormDisplay formDisplay = _dao.load( nKey, _plugin );
        if ( formDisplay != null )
        {
            initConditionalDisplayForFormDisplay( formDisplay );
        }
        return formDisplay;
    }

    /**
     * Load the data of all the formDisplay that are direct children of a given parent and returns them as a list
     * 
     * @param nIdTemplate
     *            The step primary key
     * @param nIdParent
     *            The parent primary key
     * @return the list which contains the data of all the formDisplay objects by parent
     */
    public static List<FormDisplay> getFormDisplayListByParent( int nIdTemplate, int nIdParent )
    {
        List<FormDisplay> result = _dao.selectFormDisplayListByParent( nIdTemplate, nIdParent, _plugin );
        for ( FormDisplay formDisplay : result )
        {
            initConditionalDisplayForFormDisplay( formDisplay );
        }
        return result;
    }

    /**
     * Load the data of all the FormDisplay objects of type "Group" linked to a given FormStep and returns them as a referenceList
     * 
     * @param nIdTemplate
     *            the step identifier
     * @return the referenceList which contains the data of all the Display objects of group type
     */
    public static ReferenceList getGroupDisplayReferenceListByStep( int nIdTemplate )
    {
        return _dao.selectGroupDisplayReferenceListByStep( nIdTemplate, _plugin );
    }
    /**
     * Inits the Conditional display for a given form display
     * 
     * @param formDisplay
     *            the form display to init
     */
    public static void initConditionalDisplayForFormDisplay( FormDisplay formDisplay )
    {
        List<Control> listDisplayControl = TemplateControlHome.getControlByControlTargetAndType( formDisplay.getId( ), ControlType.CONDITIONAL );

        if ( !listDisplayControl.isEmpty( ) )
        {
            formDisplay.setDisplayControl( listDisplayControl.get( 0 ) );
        }

        List<Control> listValidationControl = TemplateControlHome.getControlByQuestionAndType( formDisplay.getCompositeId( ), ControlType.VALIDATION.getLabel( ) );

        if ( !listValidationControl.isEmpty( ) )
        {
            formDisplay.setValidationControlList( listValidationControl );
        }
    }
}
