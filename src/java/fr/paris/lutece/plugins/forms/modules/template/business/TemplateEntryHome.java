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

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * This class provides instances management methods (create, find, ...) for Entry objects
 */
public final class TemplateEntryHome
{
    // Static variable pointed at the DAO instance
    private static ITemplateEntryDAO _dao = SpringContextService.getBean( "forms-template.templateEntryDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TemplateEntryHome( )
    {
    }

    /**
     * Creation of an instance of Entry
     * 
     * @param entry
     *            The instance of the Entry which contains the informations to store
     * @return The primary key of the new entry.
     */
    public static int create( Entry entry )
    {
        return _dao.insert( entry, _plugin );
    }

    /**
     * Update of the entry which is specified in parameter
     * 
     * @param entry
     *            The instance of the Entry which contains the informations to update
     */
    public static void update( Entry entry )
    {
        _dao.store( entry, _plugin );
    }

    /**
     * Remove the entry whose identifier is specified in parameter
     * 
     * @param nIdEntry
     *            The entry Id
     */
    public static void remove( int nIdEntry )
    {
        Entry entry = findByPrimaryKey( nIdEntry );

        if ( entry != null )
        {
            TransactionManager.beginTransaction( _plugin );

            try
            {
                for ( Field field : entry.getFields( ) )
                {
                    if ( IEntryTypeService.FIELD_ANSWER_CHOICE.equals( field.getCode( ) ) )
                    {
                        TemplateReferenceItemFieldHome.removeByField( field.getIdField( ) );
                    }
                    TemplateFieldHome.remove( field.getIdField( ) );
                }

                _dao.delete( nIdEntry, _plugin );
                TransactionManager.commitTransaction( _plugin );
            }
            catch( Exception e )
            {
                TransactionManager.rollBack( _plugin );
                throw new AppException( e.getMessage( ), e );
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a Entry whose identifier is specified in parameter
     * 
     * @param nKey
     *            The entry primary key
     * @return an instance of Entry
     */
    public static Entry findByPrimaryKey( int nKey )
    {
        Entry entry = _dao.load( nKey, _plugin );

        if ( entry != null )
        {
            entry.setFields( TemplateFieldHome.getFieldListByIdEntry( nKey ) );
        }

        return entry;
    }
    
    /**
     * Copy of an instance of Entry
     * 
     * @param entry
     *            The instance of the Entry who must copy
     * @return the copied Entry
     */
    public static Entry copy( Entry entry )
    {
        Entry entryCopy = (Entry) entry.clone( );
        List<Field> listField = TemplateFieldHome.getFieldListByIdEntry( entry.getIdEntry( ) );

        TransactionManager.beginTransaction( _plugin );

        try
        {
            entryCopy.setIdEntry( create( entry ) );

            for ( Field field : listField )
            {
                field = TemplateFieldHome.findByPrimaryKey( field.getIdField( ) );
                field.setParentEntry( entryCopy );
                TemplateFieldHome.copy( field );
            }

            if ( Boolean.TRUE.equals( entryCopy.getEntryType( ).getGroup( ) ) )
            {
                for ( Entry entryChild : entry.getChildren( ) )
                {
                    entryChild = TemplateEntryHome.findByPrimaryKey( entryChild.getIdEntry( ) );
                    if ( entryChild != null )
                    {
                        entryChild.setParent( entryCopy );
                        entryChild.setIdResource( entryCopy.getIdResource( ) );
                        entryChild.setResourceType( entryCopy.getResourceType( ) );
                        copy( entryChild );
                    }
                }
            }

            TransactionManager.commitTransaction( _plugin );
            return entryCopy;
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( _plugin );
            throw new AppException( e.getMessage( ), e );
        }
    }
}
