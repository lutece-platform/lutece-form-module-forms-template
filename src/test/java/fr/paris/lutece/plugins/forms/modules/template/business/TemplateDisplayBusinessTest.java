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

import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * This is the business class test for the object Form
 */
public class TemplateDisplayBusinessTest extends LuteceTestCase
{

    private static final int INT_0 = 0;
    private static final int INT_1 = 1;
    private static final String TYPE_GROUP = "group";
    private static final String TYPE_QUESTION = "question";

    /**
     * test FormDisplay
     */
    public void testBusiness( )
    {
        // Initialize an object
        FormDisplay formDisplay = new FormDisplay( );
        formDisplay.setStepId( INT_0 );
        formDisplay.setCompositeId( INT_0 );
        formDisplay.setParentId( INT_0 );
        formDisplay.setDisplayOrder( INT_0 );
        formDisplay.setCompositeType( TYPE_GROUP );
        formDisplay.setDepth( INT_0 );

        // Create test
        TemplateDisplayHome.create( formDisplay );
        FormDisplay formDisplayStored = TemplateDisplayHome.findByPrimaryKey( formDisplay.getId( ) );
        assertEquals( formDisplayStored.getStepId( ), formDisplay.getStepId( ) );
        assertEquals( formDisplayStored.getCompositeId( ), formDisplay.getCompositeId( ) );
        assertEquals( formDisplayStored.getParentId( ), formDisplay.getParentId( ) );
        assertEquals( formDisplayStored.getCompositeType( ), formDisplay.getCompositeType( ) );
        assertEquals( formDisplayStored.getDepth( ), formDisplay.getDepth( ) );

        // Update test
        formDisplay.setStepId( INT_1 );
        formDisplay.setCompositeId( INT_1 );
        formDisplay.setParentId( INT_1 );
        formDisplay.setDisplayOrder( INT_1 );
        formDisplay.setCompositeType( TYPE_QUESTION );
        formDisplay.setDepth( INT_1 );
        TemplateDisplayHome.update( formDisplay );
        formDisplayStored = TemplateDisplayHome.findByPrimaryKey( formDisplay.getId( ) );
        assertEquals( formDisplayStored.getStepId( ), formDisplay.getStepId( ) );
        assertEquals( formDisplayStored.getCompositeId( ), formDisplay.getCompositeId( ) );
        assertEquals( formDisplayStored.getParentId( ), formDisplay.getParentId( ) );
        assertEquals( formDisplayStored.getDisplayOrder( ), formDisplay.getDisplayOrder( ) );
        assertEquals( formDisplayStored.getCompositeType( ), formDisplay.getCompositeType( ) );
        assertEquals( formDisplayStored.getDepth( ), formDisplay.getDepth( ) );

        // Delete test
        TemplateDisplayHome.remove( formDisplay.getId( ) );
        formDisplayStored = TemplateDisplayHome.findByPrimaryKey( formDisplay.getId( ) );
        assertNull( formDisplayStored );

        formDisplayStored = TemplateDisplayHome.getFormDisplayByStepAndComposite( 1, 1 );
        assertNull( formDisplayStored );

    }

}
