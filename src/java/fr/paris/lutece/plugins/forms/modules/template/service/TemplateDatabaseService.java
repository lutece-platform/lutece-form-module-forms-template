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

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlMapping;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateControlHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateDisplayHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateEntryHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateFieldHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateGroupHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateQuestionHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateReferenceItemFieldHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateStepHome;
import fr.paris.lutece.plugins.forms.service.IFormDatabaseService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;

public class TemplateDatabaseService implements IFormDatabaseService
{
    public static final String BEAN_NAME = "forms-template.templateDatabaseService";

    @Override
    public void createFormDisplay( FormDisplay formDisplay )
    {
        TemplateDisplayHome.create( formDisplay );
    }

    @Override
    public List<Question> getListQuestionByForm( int nIdTemplate )
    {
        return TemplateQuestionHome.getQuestionsListByStep( nIdTemplate );
    }

    @Override
    public Question findQuestionByPrimaryKey( int idQuestion )
    {
        return TemplateQuestionHome.findByPrimaryKey( idQuestion );
    }

    @Override
    public Entry findEntryByPrimaryKey( int idEntry )
    {
        return TemplateEntryHome.findByPrimaryKey( idEntry );
    }

    @Override
    public void updateField( Field field )
    {
        TemplateFieldHome.update( field );
    }

    @Override
    public Field findFieldByPrimaryKey( int idField )
    {
        return TemplateFieldHome.findByPrimaryKey( idField );
    }

    @Override
    public void updateQuestion( Question question )
    {
        TemplateQuestionHome.update( question );
    }

    @Override
    public void createGroup( Group group )
    {
        TemplateGroupHome.create( group );
    }

    @Override
    public void updateGroup( Group group )
    {
        TemplateGroupHome.update( group );
    }

    @Override
    public Group findGroupByPrimaryKey( int idGroup )
    {
        return TemplateGroupHome.findByPrimaryKey( idGroup );
    }

    @Override
    public FormDisplay findDisplayByPrimaryKey( int idDisplay )
    {
        return TemplateDisplayHome.findByPrimaryKey( idDisplay );
    }

    @Override
    public void createEntry( Entry entry )
    {
        TemplateEntryHome.create( entry );
    }

    @Override
    public void createField( Field field )
    {
        TemplateFieldHome.create( field );
    }

    @Override
    public void createQuestion( Question question )
    {
        TemplateQuestionHome.create( question );
    }

    @Override
    public Step findStepByPrimaryKey( int idStep )
    {
        return TemplateStepHome.findByPrimaryKey( idStep );
    }

    @Override
    public void updateEntry( Entry entry )
    {
        TemplateEntryHome.update( entry );
    }

    @Override
    public List<FormDisplay> getFormDisplayListByParent( int nIdStep, int nIdParent )
    {
        return TemplateDisplayHome.getFormDisplayListByParent( nIdStep, nIdParent );
    }

    @Override
    public void updateFormDisplay( FormDisplay formDisplay )
    {
        TemplateDisplayHome.update( formDisplay );
    }

    @Override
    public Entry copyEntry( Entry entry )
    {
        return TemplateEntryHome.copy( entry );
    }

    @Override
    public List<Control> getControlByQuestion( int nIdQuestion )
    {
        return TemplateControlHome.getControlByQuestion( nIdQuestion );
    }

    @Override
    public FormDisplay getFormDisplayByFormStepAndComposite( int nIdForm, int nIdStep, int nIdComposite )
    {
        return TemplateDisplayHome.getFormDisplayByStepAndComposite( nIdStep, nIdComposite );
    }

    @Override
    public void createControl( Control control )
    {
        TemplateControlHome.create( control );
    }

    @Override
    public List<Group> getGroupsListByIdStepList( List<Integer> idStepList )
    {
        return TemplateGroupHome.getGroupsListByIdStepList( idStepList );
    }

    @Override
    public List<Question> getQuestionsListByStep( int nIdStep )
    {
        return TemplateQuestionHome.getQuestionsListByStep( nIdStep );
    }

    @Override
    public Integer findIdReferenceItemByIdField( int idField )
    {
        return TemplateReferenceItemFieldHome.findIdItemByIdField( idField );
    }

    @Override
    public List<ControlMapping> getControlMappingListByIdControl( int nIdControl )
    {
        return TemplateControlHome.getControlMappingListByIdControl( nIdControl );
    }

    @Override
    public void createStep( Step step )
    {
        TemplateStepHome.create( step );
    }

    @Override
    public void createMappingControl( int nIdcontrol, int nIdQuestion, String strValue )
    {
        TemplateControlHome.createMappingControl( nIdcontrol, nIdQuestion, strValue );
    }
}
