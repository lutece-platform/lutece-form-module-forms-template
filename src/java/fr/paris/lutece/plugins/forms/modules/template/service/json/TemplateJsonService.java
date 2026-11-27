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
package fr.paris.lutece.plugins.forms.modules.template.service.json;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateDisplayHome;
import fr.paris.lutece.plugins.forms.modules.template.service.TemplateDatabaseService;
import fr.paris.lutece.plugins.forms.service.json.AbstractFormJsonService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

/**
 * Json service to export and import the step templates, working on the template tables
 */
@ApplicationScoped
public class TemplateJsonService extends AbstractFormJsonService
{
    /**
     * No-arg constructor required by CDI for client-proxy generation. The proxy never serves business calls, so the null database service passed to the
     * parent is never used.
     */
    protected TemplateJsonService( )
    {
        super( null );
    }

    /**
     * CDI constructor-injection entry point.
     *
     * @param templateDatabaseService
     *            the template database service
     */
    @Inject
    public TemplateJsonService( TemplateDatabaseService templateDatabaseService )
    {
        super( templateDatabaseService );
    }

    /**
     * Returns the template json service bean
     *
     * @return the template json service
     * @deprecated inject {@link TemplateJsonService} in a CDI bean instead
     */
    @Deprecated
    public static TemplateJsonService getInstance( )
    {
        return CDI.current( ).select( TemplateJsonService.class ).get( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<FormDisplay> getAllFormDisplays( int idForm, int idStep )
    {
        return TemplateDisplayHome.getFormDisplayByTemplate( idStep );
    }
}
