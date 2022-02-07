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

import fr.paris.lutece.test.LuteceTestCase;

/**
 * This is the business class test for the object Step
 */
public class TemplateStepBusinessTest extends LuteceTestCase
{
    private static final String TITLE1 = "Title1";
    private static final String TITLE2 = "Title2";
    private static final String DESCRIPTION1 = "Description1";
    private static final String DESCRIPTION2 = "Description2";

    /**
     * test Step
     */
    public void testBusiness( )
    {
        // Initialize an object
        Template template = new Template( );
        template.setTitle( TITLE1 );
        template.setDescription( DESCRIPTION1 );

        // Create test
        TemplateStepHome.create( template );
        Template templateStored = TemplateStepHome.findByPrimaryKey( template.getId( ) );
        assertEquals( templateStored.getTitle( ), template.getTitle( ) );
        assertEquals( templateStored.getDescription( ), template.getDescription( ) );

        // Update test
        template.setTitle( TITLE2 );
        template.setDescription( DESCRIPTION2 );
        TemplateStepHome.update( template );
        templateStored = TemplateStepHome.findByPrimaryKey( template.getId( ) );
        assertEquals( templateStored.getTitle( ), template.getTitle( ) );
        assertEquals( templateStored.getDescription( ), template.getDescription( ) );

        // Delete test
        TemplateStepHome.remove( template.getId( ) );
        templateStored = TemplateStepHome.findByPrimaryKey( template.getId( ) );
        assertNull( templateStored );

    }

}
