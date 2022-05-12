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

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.plugins.forms.modules.template.business.Template;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateStepHome;
import fr.paris.lutece.plugins.forms.modules.template.service.json.TemplateJsonService;
import fr.paris.lutece.plugins.forms.service.json.IStepTemplateProvider;
import fr.paris.lutece.plugins.forms.service.json.StepJsonData;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

/**
 * Implements {@link IStepTemplateProvider}
 *
 */
public class StepTemplateProvider implements IStepTemplateProvider
{

    private static final String BEAN_NAME = "forms-template.stepTemplateProvider";

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
    public StepJsonData getStepTemplateData( int idTemplate )
    {
        try
        {
            return TemplateJsonService.getInstance( ).jsonExportStepAsData( -1, idTemplate );
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
