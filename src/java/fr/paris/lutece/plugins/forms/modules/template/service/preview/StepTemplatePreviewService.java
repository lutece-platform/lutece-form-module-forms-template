/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.forms.modules.template.service.preview;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.modules.template.business.Template;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateDisplayHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateGroupHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateQuestionHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateStepHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Builds the preview of the step templates: the tree of their questions and groups, and a thumbnail drawn from it
 */
@ApplicationScoped
public class StepTemplatePreviewService
{
    private static final int ROOT_PARENT_ID = 0;

    @Inject
    private StepTemplateThumbnailRenderer _thumbnailRenderer;

    /**
     * Builds the preview of a template
     *
     * @param nIdTemplate
     *            the identifier of the template
     * @return its preview; null when no template has this identifier
     */
    public StepTemplatePreview getPreview( int nIdTemplate )
    {
        Template template = TemplateStepHome.findByPrimaryKey( nIdTemplate );
        return template == null ? null : buildPreview( template );
    }

    /**
     * Builds the preview of every template
     *
     * @return the previews keyed by the identifier of their template, as a string (the code of the template in the list of the provider)
     */
    public Map<String, StepTemplatePreview> getPreviews( )
    {
        Map<String, StepTemplatePreview> mapPreviews = new LinkedHashMap<>( );
        for ( Template template : TemplateStepHome.getAllTemplates( ) )
        {
            mapPreviews.put( String.valueOf( template.getId( ) ), buildPreview( template ) );
        }
        return mapPreviews;
    }

    private StepTemplatePreview buildPreview( Template template )
    {
        StepTemplatePreview preview = new StepTemplatePreview( );
        preview.setIdTemplate( template.getId( ) );
        preview.setTitle( template.getTitle( ) );
        addFields( template.getId( ), ROOT_PARENT_ID, 0, preview.getFields( ) );
        preview.setThumbnailUrl( _thumbnailRenderer.renderDataUrl( preview.getFields( ) ) );
        return preview;
    }

    /**
     * Adds the questions and groups of a parent display, in display order, each group with its own content
     */
    private void addFields( int nIdTemplate, int nIdParent, int nDepth, List<StepTemplatePreviewField> listFields )
    {
        for ( FormDisplay display : TemplateDisplayHome.getFormDisplayListByParent( nIdTemplate, nIdParent ) )
        {
            StepTemplatePreviewField field = null;
            if ( FormsConstants.COMPOSITE_GROUP_TYPE.equals( display.getCompositeType( ) ) )
            {
                field = groupField( TemplateGroupHome.findByPrimaryKey( display.getCompositeId( ) ) );
                if ( field != null )
                {
                    addFields( nIdTemplate, display.getId( ), nDepth + 1, field.getChildren( ) );
                }
            }
            else
                if ( FormsConstants.COMPOSITE_QUESTION_TYPE.equals( display.getCompositeType( ) ) )
                {
                    field = questionField( TemplateQuestionHome.findByPrimaryKey( display.getCompositeId( ) ) );
                }
            if ( field != null )
            {
                field.setDepth( nDepth );
                listFields.add( field );
            }
        }
    }

    private static StepTemplatePreviewField groupField( Group group )
    {
        if ( group == null )
        {
            return null;
        }
        StepTemplatePreviewField field = new StepTemplatePreviewField( );
        field.setTitle( group.getTitle( ) );
        field.setKind( StepTemplateFieldKind.GROUP );
        return field;
    }

    private static StepTemplatePreviewField questionField( Question question )
    {
        if ( question == null )
        {
            return null;
        }
        StepTemplatePreviewField field = new StepTemplatePreviewField( );
        field.setTitle( question.getTitle( ) );
        Entry entry = question.getEntry( );
        EntryType entryType = entry != null ? entry.getEntryType( ) : null;
        field.setKind( StepTemplateFieldKind.fromBeanName( entryType != null ? entryType.getBeanName( ) : null ) );
        if ( entryType != null )
        {
            field.setTypeTitle( entryType.getTitle( ) );
            field.setIconName( entryType.getIconName( ) );
        }
        field.setMandatory( entry != null && entry.isMandatory( ) );
        return field;
    }
}
