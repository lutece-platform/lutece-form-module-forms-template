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

import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Question objects
 */
public final class TemplateQuestionDAO implements ITemplateQuestionDAO
{
    // Constants

    private static final String SQL_QUERY_SELECT_ALL = "SELECT id_question, title, code, description, id_entry, id_template, is_visible_multiview_global, is_visible_multiview_form_selected, column_title, is_filterable_multiview_global, is_filterable_multiview_form_selected, multiview_column_order FROM template_question";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_ALL + " WHERE id_question = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO template_question ( title, code, description, id_entry, id_template, is_visible_multiview_global, is_visible_multiview_form_selected, column_title, is_filterable_multiview_global, is_filterable_multiview_form_selected,multiview_column_order ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM template_question WHERE id_question = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE template_question SET id_question = ?, title = ?, code = ?, description = ?, id_entry = ?, id_template = ?, is_visible_multiview_global = ?, is_visible_multiview_form_selected = ?, column_title = ?, is_filterable_multiview_global = ?, is_filterable_multiview_form_selected = ?, multiview_column_order = ? WHERE id_question = ?";
    private static final String SQL_QUERY_SELECT_BY_TEMPLATE = SQL_QUERY_SELECT_ALL + " WHERE id_template = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Question question, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, question.getTitle( ) );
            daoUtil.setString( nIndex++, question.getCode( ) );
            daoUtil.setString( nIndex++, question.getDescription( ) );
            daoUtil.setInt( nIndex++, question.getIdEntry( ) );
            daoUtil.setInt( nIndex++, question.getIdStep( ) );
            daoUtil.setBoolean( nIndex++, question.isVisibleMultiviewGlobal( ) );
            daoUtil.setBoolean( nIndex++, question.isVisibleMultiviewFormSelected( ) );
            daoUtil.setString( nIndex++, question.getColumnTitle( ) );
            daoUtil.setBoolean( nIndex++, question.isFiltrableMultiviewGlobal( ) );
            daoUtil.setBoolean( nIndex++, question.isFiltrableMultiviewFormSelected( ) );
            daoUtil.setInt( nIndex++, question.getMultiviewColumnOrder( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                question.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Question load( int nKey, Plugin plugin )
    {
        Question question = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                question = dataToObject( daoUtil );
            }

            if ( question != null )
            {
                question.setEntry( getQuestionEntry( question.getIdEntry( ) ) );
            }
        }
        return question;
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
    public void store( Question question, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, question.getId( ) );
            daoUtil.setString( ++nIndex, question.getTitle( ) );
            daoUtil.setString( ++nIndex, question.getCode( ) );
            daoUtil.setString( ++nIndex, question.getDescription( ) );
            daoUtil.setInt( ++nIndex, question.getIdEntry( ) );
            daoUtil.setInt( ++nIndex, question.getIdStep( ) );
            daoUtil.setBoolean( ++nIndex, question.isVisibleMultiviewGlobal( ) );
            daoUtil.setBoolean( ++nIndex, question.isVisibleMultiviewFormSelected( ) );
            daoUtil.setString( ++nIndex, question.getColumnTitle( ) );
            daoUtil.setBoolean( ++nIndex, question.isFiltrableMultiviewGlobal( ) );
            daoUtil.setBoolean( ++nIndex, question.isFiltrableMultiviewFormSelected( ) );
            daoUtil.setInt( ++nIndex, question.getMultiviewColumnOrder( ) );

            daoUtil.setInt( ++nIndex, question.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    @Override
    public List<Question> selectQuestionsListByTemplate( int nIdTemplate, Plugin plugin )
    {
        List<Question> questionList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_TEMPLATE, plugin ) )
        {
            daoUtil.setInt( 1, nIdTemplate );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                questionList.add( dataToObject( daoUtil ) );
            }
        }
        for ( Question quest : questionList )
        {
            quest.setEntry( getQuestionEntry( quest.getIdEntry( ) ) );
        }
        return questionList;
    }

    /**
     * @param nIdEntry
     *            the entry primary key
     * @return the Entry
     */
    private Entry getQuestionEntry( int nIdEntry )
    {
        return TemplateEntryHome.findByPrimaryKey( nIdEntry );
    }

    /**
     * @param nIdTemplate
     *            the step primary key
     * @return the Step
     */
    private Step getQuestionStep( int nIdTemplate )
    {
        return TemplateStepHome.findByPrimaryKey( nIdTemplate );
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated Question object
     */
    private Question dataToObject( DAOUtil daoUtil )
    {
        Question question = new Question( );

        question.setId( daoUtil.getInt( "id_question" ) );
        question.setTitle( daoUtil.getString( "title" ) );
        question.setCode( daoUtil.getString( "code" ) );
        question.setDescription( daoUtil.getString( "description" ) );
        question.setIdEntry( daoUtil.getInt( "id_entry" ) );
        question.setIdStep( daoUtil.getInt( "id_template" ) );
        question.setVisibleMultiviewGlobal( daoUtil.getBoolean( "is_visible_multiview_global" ) );
        question.setVisibleMultiviewFormSelected( daoUtil.getBoolean( "is_visible_multiview_form_selected" ) );
        question.setColumnTitle( daoUtil.getString( "column_title" ) );
        question.setFiltrableMultiviewGlobal( daoUtil.getBoolean( "is_filterable_multiview_global" ) );
        question.setFiltrableMultiviewFormSelected( daoUtil.getBoolean( "is_filterable_multiview_form_selected" ) );
        question.setMultiviewColumnOrder( daoUtil.getInt( "multiview_column_order" ) );
        question.setStep( getQuestionStep( question.getIdStep( ) ) );

        return question;
    }
}
