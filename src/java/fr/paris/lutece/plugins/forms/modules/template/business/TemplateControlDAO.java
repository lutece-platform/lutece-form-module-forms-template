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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlMapping;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Control objects
 */
public final class TemplateControlDAO implements ITemplateControlDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT fc.id_control, fc.value, fc.error_message, fc.validator_name, fc.control_type, fc.id_control_target FROM template_control fc ";
    private static final String SQL_QUERY_CONTROLQUESTIONS_SELECTALL = "SELECT fc.id_control, fc.value, fc.error_message, fc.validator_name, fc.control_type, fc.id_control_target FROM template_control fc join template_control_question fcq on(fcq.id_control = fc.id_control) ";

    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE fc.id_control = ?";
    private static final String SQL_QUERY_SELECT_BY_QUESTION = SQL_QUERY_CONTROLQUESTIONS_SELECTALL + "WHERE fcq.id_question = ?";
    private static final String SQL_QUERY_SELECT_BY_QUESTION_AND_TYPE = SQL_QUERY_CONTROLQUESTIONS_SELECTALL
            + " WHERE fcq.id_question = ? AND fc.control_type = ?";
    private static final String SQL_QUERY_SELECT_BY_CONTROL_TARGET = SQL_QUERY_SELECTALL + " WHERE fc.id_control_target = ? AND fc.control_type = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO template_control ( value, error_message, validator_name, control_type, id_control_target ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_INSERT_CONTROL_QUESTION = "INSERT INTO template_control_question ( id_control, id_question ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_INSERT_CONTROL_QUESTION_VALUE = "INSERT INTO template_control_question_mapping ( id_control, id_question, value ) VALUES ( ?, ?, ? ) ";

    private static final String SQL_QUERY_DELETE = "DELETE FROM template_control WHERE id_control = ? ";
    private static final String SQL_QUERY_DELETE_CONTROL_QUESTION = "DELETE FROM template_control_question WHERE id_control = ? ";
    private static final String SQL_QUERY_DELETE_CONTROL_QUESTION_VALUE = "DELETE FROM template_control_question_mapping WHERE id_control = ? ";

    private static final String SQL_QUERY_DELETE_BY_CONTROL_TARGET = "DELETE FROM template_control WHERE id_control_target = ? AND control_type = ?";
    private static final String SQL_QUERY_UPDATE = "UPDATE template_control SET id_control = ?, value = ?, error_message = ?,  validator_name = ?, control_type = ?, id_control_target = ? WHERE id_control = ?";
    private static final String SQL_QUERY_CONTROL_QUESTION_SELECTALL = "SELECT fcq.id_question FROM template_control_question fcq where fcq.id_control= ? ";
    private static final String SQL_QUERY_CONTROL_MAPPING_BY_IDCONTROL = "SELECT id_question, value FROM template_control_question_mapping  where id_control= ? ";

    private static final String PARAMETER_CONTROL_ID = "id_control";
    private static final String PARAMETER_VALUE = "value";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Control control, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, control.getValue( ) );
            daoUtil.setString( nIndex++, control.getErrorMessage( ) );
            daoUtil.setString( nIndex++, control.getValidatorName( ) );
            daoUtil.setString( nIndex++, control.getControlType( ) );
            daoUtil.setInt( nIndex++, control.getIdControlTarget( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                control.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( int nIdControl, int nIdQuestion, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_CONTROL_QUESTION, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, nIdControl );
            daoUtil.setInt( nIndex, nIdQuestion );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( int nIdControl, int nIdQuestion, String strValue, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_CONTROL_QUESTION_VALUE, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, nIdControl );
            daoUtil.setInt( nIndex++, nIdQuestion );
            daoUtil.setString( nIndex, strValue );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Control load( int nKey, Plugin plugin )
    {
        Control control = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                control = dataToObject( daoUtil );
            }
        }
        return control;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<Integer> loadIdQuestions( int nIdControl, Plugin plugin )
    {
        Set<Integer> listQuestion = new HashSet<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CONTROL_QUESTION_SELECTALL, plugin ) )
        {
            daoUtil.setInt( 1, nIdControl );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listQuestion.add( daoUtil.getInt( "id_question" ) );
            }
        }
        return listQuestion;
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
    public void deleteControlQuestion( int nControl, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_CONTROL_QUESTION, plugin ) )
        {
            daoUtil.setInt( 1, nControl );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteControlQuestionValue( int nControl, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_CONTROL_QUESTION_VALUE, plugin ) )
        {
            daoUtil.setInt( 1, nControl );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteByControlTarget( int nIdControlTarget, ControlType controlType, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_CONTROL_TARGET, plugin ) )
        {
            daoUtil.setInt( 1, nIdControlTarget );
            daoUtil.setString( 2, controlType.getLabel( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Control control, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, control.getId( ) );
            daoUtil.setString( nIndex++, control.getValue( ) );
            daoUtil.setString( nIndex++, control.getErrorMessage( ) );
            daoUtil.setString( nIndex++, control.getValidatorName( ) );
            daoUtil.setString( nIndex++, control.getControlType( ) );
            daoUtil.setInt( nIndex++, control.getIdControlTarget( ) );

            daoUtil.setInt( nIndex, control.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    @Override
    public List<Control> selectControlByControlTargetAndType( int nIdControlTarget, ControlType controlType, Plugin plugin )
    {
        List<Control> listControl = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_CONTROL_TARGET, plugin ) )
        {
            daoUtil.setInt( 1, nIdControlTarget );
            daoUtil.setString( 2, controlType.getLabel( ) );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listControl.add( dataToObject( daoUtil ) );
            }
        }
        return listControl;
    }

    @Override
    public List<Control> selectControlByQuestionAndType( int nIdQuestion, String strControlType, Plugin plugin )
    {
        List<Control> listControl = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_QUESTION_AND_TYPE, plugin ) )
        {
            daoUtil.setInt( 1, nIdQuestion );
            daoUtil.setString( 2, strControlType );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listControl.add( dataToObject( daoUtil ) );
            }
        }
        return listControl;
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated Control object
     */
    private Control dataToObject( DAOUtil daoUtil )
    {
        Control control = new Control( );

        control.setId( daoUtil.getInt( PARAMETER_CONTROL_ID ) );
        control.setValue( daoUtil.getString( PARAMETER_VALUE ) );
        control.setErrorMessage( daoUtil.getString( "error_message" ) );
        control.setValidatorName( daoUtil.getString( "validator_name" ) );
        control.setControlType( daoUtil.getString( "control_type" ) );
        control.setIdControlTarget( daoUtil.getInt( "id_control_target" ) );
        return control;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Control> selectControlByQuestion( int nIdQuestion, Plugin plugin )
    {
        List<Control> controlList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_QUESTION, plugin ) )
        {
            daoUtil.setInt( 1, nIdQuestion );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                controlList.add( dataToObject( daoUtil ) );
            }
        }
        return controlList;
    }

    @Override
    public List<ControlMapping> selectMappingControlList( int nIdControl, Plugin plugin )
    {
        List<ControlMapping> list = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CONTROL_MAPPING_BY_IDCONTROL, plugin ) )
        {
            daoUtil.setInt( 1, nIdControl );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                ControlMapping mapping = new ControlMapping( );
                mapping.setIdControl( nIdControl );
                mapping.setIdQuestion( daoUtil.getInt( "id_question" ) );
                mapping.setValue( daoUtil.getString( PARAMETER_VALUE ) );
                list.add( mapping );
            }
        }
        return list;
    }
}
