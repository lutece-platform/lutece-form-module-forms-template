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

import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods (create, find, ...) for Field objects
 */
public final class TemplateFieldHome
{
    // Static variable pointed at the DAO instance
    private static ITemplateFieldDAO _dao = SpringContextService.getBean( "forms-template.templateFieldDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TemplateFieldHome( )
    {
    }

    /**
     * Creation of an instance of field
     * 
     * @param field
     *            The instance of the Field which contains the informations to store
     * @return The primary key of the new Field.
     */
    public static int create( Field field )
    {
        int id = _dao.insert( field, _plugin );
        if ( field.getLinkedItem( ) != null )
        {
            TemplateReferenceItemFieldHome.create( field.getIdField( ), field.getLinkedItem( ).getId( ) );
        }
        return id;
    }

    /**
     * Update of the field which is specified in parameter
     * 
     * @param field
     *            The instance of the Field which contains the informations to update
     */
    public static void update( Field field )
    {
        _dao.store( field, _plugin );
    }

    /**
     * Remove the field whose identifier is specified in parameter
     * 
     * @param nIdField
     *            The field Id
     */
    public static void remove( int nIdField )
    {
        _dao.delete( nIdField, _plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a Field whose identifier is specified in parameter
     * 
     * @param nKey
     *            The field primary key
     * @return an instance of Field
     */
    public static Field findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the field of the entry and returns them in a list
     * 
     * @param nIdEntry
     *            the id of the entry
     * @return the list of field
     */
    public static List<Field> getFieldListByIdEntry( int nIdEntry )
    {
        return _dao.selectFieldListByIdEntry( nIdEntry, _plugin );
    }
}
