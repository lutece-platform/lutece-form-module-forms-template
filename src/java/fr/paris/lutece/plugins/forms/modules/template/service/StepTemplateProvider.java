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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.plugins.forms.modules.template.business.Template;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateStepHome;
import fr.paris.lutece.plugins.forms.modules.template.service.json.TemplateJsonService;
import fr.paris.lutece.plugins.forms.modules.template.service.preview.StepTemplatePreview;
import fr.paris.lutece.plugins.forms.modules.template.service.preview.StepTemplatePreviewField;
import fr.paris.lutece.plugins.forms.modules.template.service.preview.StepTemplatePreviewService;
import fr.paris.lutece.plugins.forms.service.json.IStepTemplateProvider;
import fr.paris.lutece.plugins.forms.service.json.StepJsonData;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;
import org.apache.commons.lang3.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Implements {@link IStepTemplateProvider}
 *
 */
@ApplicationScoped
@Named( StepTemplateProvider.BEAN_NAME )
public class StepTemplateProvider implements IStepTemplateProvider
{

    public static final String BEAN_NAME = "forms-template.stepTemplateProvider";

    @Inject
    private TemplateJsonService _templateJsonService;

    @Inject
    private StepTemplatePreviewService _previewService;

    @Override
    public ReferenceList getStepTemplateList( )
    {
        ReferenceList referenceList = new ReferenceList( );
        List<Template> templateList = TemplateStepHome.getAllTemplates( );
        for ( Template template : templateList )
        {
            referenceList.addItem( template.getId( ), template.getTitle( ) );
        }
        return referenceList;
    }

    @Override
    public Map<String, String> getStepTemplateDescriptions( )
    {
        Map<String, String> mapDescriptions = new HashMap<>( );
        for ( Template template : TemplateStepHome.getAllTemplates( ) )
        {
            if ( StringUtils.isNotBlank( template.getDescription( ) ) )
            {
                mapDescriptions.put( String.valueOf( template.getId( ) ), template.getDescription( ) );
            }
        }
        return mapDescriptions;
    }

    /**
     * {@inheritDoc} The thumbnail is an SVG wireframe drawn from the questions of the template, the fields are listed in display order.
     */
    @Override
    public Map<String, fr.paris.lutece.plugins.forms.service.json.StepTemplatePreview> getStepTemplatePreviews( )
    {
        Map<String, fr.paris.lutece.plugins.forms.service.json.StepTemplatePreview> mapPreviews = new LinkedHashMap<>( );
        for ( Map.Entry<String, StepTemplatePreview> entry : _previewService.getPreviews( ).entrySet( ) )
        {
            mapPreviews.put( entry.getKey( ), toFormsPreview( entry.getValue( ) ) );
        }
        return mapPreviews;
    }

    /**
     * Converts the preview of the module to the one the template selector of forms shows
     */
    private static fr.paris.lutece.plugins.forms.service.json.StepTemplatePreview toFormsPreview( StepTemplatePreview preview )
    {
        fr.paris.lutece.plugins.forms.service.json.StepTemplatePreview formsPreview = new fr.paris.lutece.plugins.forms.service.json.StepTemplatePreview( );
        formsPreview.setThumbnailUrl( preview.getThumbnailUrl( ) );
        for ( StepTemplatePreviewField field : preview.getFlatFields( ) )
        {
            fr.paris.lutece.plugins.forms.service.json.StepTemplatePreviewField formsField = new fr.paris.lutece.plugins.forms.service.json.StepTemplatePreviewField( );
            formsField.setTitle( field.getTitle( ) );
            formsField.setTypeTitle( field.getTypeTitle( ) );
            formsField.setIconName( field.getIconName( ) );
            formsField.setDepth( field.getDepth( ) );
            formsField.setGroup( field.isGroup( ) );
            formsField.setMandatory( field.isMandatory( ) );
            formsPreview.getFields( ).add( formsField );
        }
        return formsPreview;
    }

    @Override
    public StepJsonData getStepTemplateData( int idTemplate )
    {
        try
        {
            return _templateJsonService.jsonExportStepAsData( -1, idTemplate );
        }
        catch( JsonProcessingException e )
        {
            AppLogService.error( e.getMessage( ) );
            return null;
        }
    }

    @Override
    public String getProviderBeanName( )
    {
        return BEAN_NAME;
    }

}
