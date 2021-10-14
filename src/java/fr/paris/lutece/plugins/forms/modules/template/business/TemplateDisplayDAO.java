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
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Form objects
 */
public final class TemplateDisplayDAO implements ITemplateFormDisplayDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id_display, id_template, id_composite, id_parent, display_order, composite_type, display_depth FROM template_display";

    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_display = ?";
    private static final String SQL_QUERY_SELECT_BY_PARENT = SQL_QUERY_SELECTALL + " WHERE id_template = ? AND id_parent = ? ORDER BY display_order ASC";
    private static final String SQL_QUERY_INSERT = "INSERT INTO template_display ( id_template, id_composite, id_parent, display_order, composite_type, display_depth ) VALUES ( ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM template_display WHERE id_display = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE template_display SET id_display = ?, id_template = ?, id_composite = ?, id_parent = ?, display_order = ?, composite_type = ?, display_depth = ? WHERE id_display = ?";
    private static final String SQL_QUERY_NEXT_POSITION_BY_PARENT = "SELECT MAX(display_order) from template_display WHERE id_template = ? AND id_parent = ?";
    private static final String SQL_QUERY_SELECTALL_GROUP_DISPLAY_BY_STEP = "SELECT d.id_display, g.title, d.id_template, d.id_composite, d.id_parent, d.display_order, d.composite_type, d.display_depth "
            + "FROM template_display d INNER JOIN template_group g ON d.id_composite = g.id_group "
            + "WHERE d.id_template = ? AND d.composite_type = ? order by d.id_parent, d.display_order";
    private static final String SQL_QUERY_SELECT_BY_FROM_STEP_COMPOSITE = SQL_QUERY_SELECTALL + " WHERE id_template = ? AND id_composite = ?";
    private static final String SQL_QUERY_SELECT_BY_STEP_COMPOSITE = SQL_QUERY_SELECTALL + " WHERE id_template = ? ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( FormDisplay templateDisplay, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, templateDisplay.getStepId( ) );
            daoUtil.setInt( nIndex++, templateDisplay.getCompositeId( ) );
            daoUtil.setInt( nIndex++, templateDisplay.getParentId( ) );
            int nDisplayOrder = getNextPositionInGroup( templateDisplay.getStepId( ), templateDisplay.getParentId( ), plugin );
            daoUtil.setInt( nIndex++, nDisplayOrder );
            daoUtil.setString( nIndex++, templateDisplay.getCompositeType( ) );
            daoUtil.setInt( nIndex++, templateDisplay.getDepth( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                templateDisplay.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FormDisplay load( int nKey, Plugin plugin )
    {
        FormDisplay templateDisplay = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                templateDisplay = dataToObject( daoUtil );
            }
        }
        return templateDisplay;
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

    @Override
    public ReferenceList selectGroupDisplayReferenceListByStep( int nIdStep, Plugin plugin )
    {
        ReferenceList groupList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_GROUP_DISPLAY_BY_STEP, plugin ) )
        {
            daoUtil.setInt( 1, nIdStep );
            daoUtil.setString( 2, CompositeDisplayType.GROUP.getLabel( ) );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                groupList.addItem( daoUtil.getInt( "id_display" ), daoUtil.getString( "title" ) );
            }
        }
        return groupList;
    }

    @Override
    public void store( FormDisplay templateDisplay, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, templateDisplay.getId( ) );
            daoUtil.setInt( nIndex++, templateDisplay.getStepId( ) );
            daoUtil.setInt( nIndex++, templateDisplay.getCompositeId( ) );
            daoUtil.setInt( nIndex++, templateDisplay.getParentId( ) );
            daoUtil.setInt( nIndex++, templateDisplay.getDisplayOrder( ) );
            daoUtil.setString( nIndex++, templateDisplay.getCompositeType( ) );
            daoUtil.setInt( nIndex++, templateDisplay.getDepth( ) );
            daoUtil.setInt( nIndex, templateDisplay.getId( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FormDisplay> selectFormDisplayListByParent( int nIdTemplate, int nIdParent, Plugin plugin )
    {
        List<FormDisplay> templateDisplayList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PARENT, plugin ) )
        {
            daoUtil.setInt( 1, nIdTemplate );
            daoUtil.setInt( 2, nIdParent );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                templateDisplayList.add( dataToObject( daoUtil ) );
            }
        }
        return templateDisplayList;
    }

    /**
     * Return the next available position with a given parent group
     * 
     * @param nIdStep
     *            the Step id
     * @param nIdParent
     *            the parent display identifier
     * @param plugin
     *            the Plugin to use
     * @return the next available position value in the group
     */
    private int getNextPositionInGroup( int nIdStep, int nIdParent, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEXT_POSITION_BY_PARENT, plugin );
        daoUtil.setInt( 1, nIdStep );
        daoUtil.setInt( 2, nIdParent );
        daoUtil.executeQuery( );

        int nNextPosition = 0;

        if ( daoUtil.next( ) )
        {
            nNextPosition = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.close( );

        return nNextPosition;
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated FormDisplay object
     */
    private FormDisplay dataToObject( DAOUtil daoUtil )
    {
        FormDisplay templateDisplay = new FormDisplay( );

        templateDisplay.setId( daoUtil.getInt( "id_display" ) );
        templateDisplay.setStepId( daoUtil.getInt( "id_template" ) );
        templateDisplay.setCompositeId( daoUtil.getInt( "id_composite" ) );
        templateDisplay.setParentId( daoUtil.getInt( "id_parent" ) );
        templateDisplay.setDisplayOrder( daoUtil.getInt( "display_order" ) );
        templateDisplay.setCompositeType( daoUtil.getString( "composite_type" ) );
        templateDisplay.setDepth( daoUtil.getInt( "display_depth" ) );

        return templateDisplay;
    }

    @Override
    public FormDisplay selectFormDisplayByStepAndComposite( int nIdStep, int nIdComposite, Plugin plugin )
    {
        FormDisplay formDisplay = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_FROM_STEP_COMPOSITE, plugin ) )
        {
            daoUtil.setInt( 1, nIdStep );
            daoUtil.setInt( 2, nIdComposite );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                formDisplay = dataToObject( daoUtil );
            }
        }
        return formDisplay;
    }

    @Override
    public List<FormDisplay> selectFormDisplayListByTemplate( int nIdTemplate, Plugin plugin )
    {
        List<FormDisplay> list = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_STEP_COMPOSITE, plugin ) )
        {
            daoUtil.setInt( 1, nIdTemplate );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                list.add( dataToObject( daoUtil ) );
            }
        }
        return list;
    }
}
