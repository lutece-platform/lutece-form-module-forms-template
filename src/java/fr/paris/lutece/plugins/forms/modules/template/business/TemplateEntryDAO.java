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

import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Entry objects
 */
public final class TemplateEntryDAO implements ITemplateEntryDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT_LIST = "ent.id_type,typ.title,typ.is_group,typ.is_comment,typ.class_name,typ.is_mylutece_user,typ.icon_name,"
            + "ent.id_entry,ent.code,ent.title,ent.help_message, ent.comment,ent.mandatory,ent.fields_in_line,"
            + "ent.pos,ent.field_unique, ent.css_class, ent.pos_conditional, ent.error_message, " + "ent.is_only_display_back, ent.is_indexed ";
    private static final String SQL_QUERY_SELECT_ENTRY_ATTRIBUTES = "SELECT " + SQL_QUERY_SELECT_LIST
            + "FROM template_entry ent,genatt_entry_type typ WHERE ent.id_type=typ.id_type ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = SQL_QUERY_SELECT_ENTRY_ATTRIBUTES + " AND ent.id_entry = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO template_entry ( id_type,code,title,help_message, comment,mandatory,fields_in_line,"
            + "pos,field_unique,css_class, pos_conditional, error_message, is_only_display_back, is_indexed ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM template_entry WHERE id_entry = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE template_entry SET id_entry=?,id_type=?,code=?,title=?,help_message=?,"
            + "comment=?,mandatory=?, fields_in_line=?,pos=?,field_unique=?,css_class=?, pos_conditional=?, "
            + "error_message=?, is_only_display_back = ?, is_indexed = ? WHERE id_entry=?";
    private static final int CONSTANT_ZERO = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public int insert( Entry entry, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, entry.getEntryType( ).getIdType( ) );
            daoUtil.setString( nIndex++, entry.getCode( ) );
            daoUtil.setString( nIndex++, trimEntryTitle( entry ) );
            daoUtil.setString( nIndex++, entry.getHelpMessage( ) );
            daoUtil.setString( nIndex++, entry.getComment( ) );
            daoUtil.setBoolean( nIndex++, entry.isMandatory( ) );
            daoUtil.setBoolean( nIndex++, entry.isFieldInLine( ) );
            daoUtil.setInt( nIndex++, CONSTANT_ZERO );
            daoUtil.setBoolean( nIndex++, entry.isUnique( ) );
            daoUtil.setString( nIndex++, ( entry.getCSSClass( ) == null ) ? StringUtils.EMPTY : entry.getCSSClass( ) );
            daoUtil.setInt( nIndex++, CONSTANT_ZERO );
            daoUtil.setString( nIndex++, entry.getErrorMessage( ) );
            daoUtil.setBoolean( nIndex++, entry.isOnlyDisplayInBack( ) );
            daoUtil.setBoolean( nIndex++, entry.isIndexed( ) );

            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                entry.setIdEntry( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

        return entry.getIdEntry( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entry load( int nId, Plugin plugin )
    {
        Entry entry = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                entry = getEntryValues( daoUtil );
            }

        }
        return entry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdEntry, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdEntry );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Entry entry, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, entry.getIdEntry( ) );
            daoUtil.setInt( nIndex++, entry.getEntryType( ).getIdType( ) );
            daoUtil.setString( nIndex++, entry.getCode( ) );
            daoUtil.setString( nIndex++, trimEntryTitle( entry ) );
            daoUtil.setString( nIndex++, entry.getHelpMessage( ) );
            daoUtil.setString( nIndex++, entry.getComment( ) );
            daoUtil.setBoolean( nIndex++, entry.isMandatory( ) );
            daoUtil.setBoolean( nIndex++, entry.isFieldInLine( ) );
            daoUtil.setInt( nIndex++, CONSTANT_ZERO );
            daoUtil.setBoolean( nIndex++, entry.isUnique( ) );
            daoUtil.setString( nIndex++, ( entry.getCSSClass( ) == null ) ? StringUtils.EMPTY : entry.getCSSClass( ) );
            daoUtil.setInt( nIndex++, CONSTANT_ZERO );
            daoUtil.setString( nIndex++, entry.getErrorMessage( ) );
            daoUtil.setBoolean( nIndex++, entry.isOnlyDisplayInBack( ) );
            daoUtil.setBoolean( nIndex++, entry.isIndexed( ) );

            daoUtil.setInt( nIndex++, entry.getIdEntry( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Get values of an entry from the current row of a daoUtil. The class to daoUtil.next( ) will NOT be made by this method.
     * 
     * @param daoUtil
     *            The DAOUtil
     * @return The entry, or null if the entry was not found
     */
    private Entry getEntryValues( DAOUtil daoUtil )
    {
        Entry entry = new Entry( );

        int nIndex = 1;
        EntryType entryType = new EntryType( );
        entryType.setIdType( daoUtil.getInt( nIndex++ ) );
        entryType.setTitle( daoUtil.getString( nIndex++ ) );
        entryType.setGroup( daoUtil.getBoolean( nIndex++ ) );
        entryType.setComment( daoUtil.getBoolean( nIndex++ ) );
        entryType.setBeanName( daoUtil.getString( nIndex++ ) );
        entryType.setMyLuteceUser( daoUtil.getBoolean( nIndex++ ) );
        entryType.setIconName( daoUtil.getString( nIndex++ ) );
        entry.setEntryType( entryType );
        entry.setIdEntry( daoUtil.getInt( nIndex++ ) );

        entry.setCode( daoUtil.getString( nIndex++ ) );
        entry.setTitle( daoUtil.getString( nIndex++ ) );
        entry.setHelpMessage( daoUtil.getString( nIndex++ ) );
        entry.setComment( daoUtil.getString( nIndex++ ) );
        entry.setMandatory( daoUtil.getBoolean( nIndex++ ) );
        entry.setFieldInLine( daoUtil.getBoolean( nIndex++ ) );
        entry.setPosition( daoUtil.getInt( nIndex++ ) );
        entry.setUnique( daoUtil.getBoolean( nIndex++ ) );
        entry.setCSSClass( daoUtil.getString( nIndex++ ) );

        if ( daoUtil.getInt( nIndex++ ) > 0 )
        {
            entry.setPosition( daoUtil.getInt( nIndex - 1 ) );
        }

        entry.setErrorMessage( daoUtil.getString( nIndex++ ) );
        entry.setOnlyDisplayInBack( daoUtil.getBoolean( nIndex++ ) );
        entry.setIndexed( daoUtil.getBoolean( nIndex ) );

        return entry;
    }

    /**
     * Return the trim of the title of the entry or null if the entry doesn't have a title
     * 
     * @param entry
     *            The entry to retrieve the title from
     * @return the trim of the title of the entry or null if the entry doesn't have a title
     */
    private String trimEntryTitle( Entry entry )
    {
        String strEntryTitle = entry.getTitle( );

        if ( strEntryTitle != null )
        {
            strEntryTitle = strEntryTitle.trim( );
        }

        return strEntryTitle;
    }
}
