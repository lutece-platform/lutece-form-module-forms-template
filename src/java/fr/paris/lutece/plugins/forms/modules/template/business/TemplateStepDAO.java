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
package fr.paris.lutece.plugins.forms.modules.template.business;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Step objects
 */
public final class TemplateStepDAO implements ITemplateStepDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id_template, title, description FROM template_step";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_template = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO template_step ( title, description) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM template_step WHERE id_template = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE template_step SET id_template = ?, title = ?, description = ? WHERE id_template = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Template template, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setString( ++nIndex, template.getTitle( ) );
            daoUtil.setString( ++nIndex, template.getDescription( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                template.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Template load( int nKey, Plugin plugin )
    {
        Template template = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                template = dataToObject( daoUtil );
            }
        }
        return template;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Template template, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, template.getId( ) );
            daoUtil.setString( ++nIndex, template.getTitle( ) );
            daoUtil.setString( ++nIndex, template.getDescription( ) );

            daoUtil.setInt( ++nIndex, template.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    @Override
    public List<Template> findAll( Plugin plugin )
    {
        List<Template> list = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                list.add( dataToObject( daoUtil ) );
            }
        }
        return list;
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated Template object
     */
    private Template dataToObject( DAOUtil daoUtil )
    {
        Template template = new Template( );

        template.setId( daoUtil.getInt( "id_template" ) );
        template.setTitle( daoUtil.getString( "title" ) );
        template.setDescription( daoUtil.getString( "description" ) );
        return template;
    }

}
