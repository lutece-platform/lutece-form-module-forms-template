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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/**
 * The thumbnail of a step template, drawn without a container: one shape per field type, groups framed, rows capped, no text from the data
 */
public class StepTemplateThumbnailRendererTest
{
    private final StepTemplateThumbnailRenderer _renderer = new StepTemplateThumbnailRenderer( );

    private static StepTemplatePreviewField field( String strTitle, StepTemplateFieldKind kind )
    {
        StepTemplatePreviewField field = new StepTemplatePreviewField( );
        field.setTitle( strTitle );
        field.setKind( kind );
        return field;
    }

    private static int height( String strSvg )
    {
        Matcher matcher = Pattern.compile( "viewBox=\"0 0 240 (\\d+)\"" ).matcher( strSvg );
        assertTrue( matcher.find( ), strSvg );
        return Integer.parseInt( matcher.group( 1 ) );
    }

    @Test
    public void testKindFromBeanName( )
    {
        assertEquals( StepTemplateFieldKind.DATE, StepTemplateFieldKind.fromBeanName( "forms.entryTypeDate" ) );
        assertEquals( StepTemplateFieldKind.GROUP, StepTemplateFieldKind.fromBeanName( "forms.entryTypeGroup" ) );
        assertEquals( StepTemplateFieldKind.INPUT, StepTemplateFieldKind.fromBeanName( "forms.entryTypeText" ) );
        assertEquals( StepTemplateFieldKind.INPUT, StepTemplateFieldKind.fromBeanName( "forms.entryTypeUnknown" ) );
        assertEquals( StepTemplateFieldKind.INPUT, StepTemplateFieldKind.fromBeanName( null ) );
    }

    @Test
    public void testEmptyTemplateIsAValidImage( )
    {
        String strSvg = _renderer.renderSvg( new ArrayList<>( ) );
        assertTrue( strSvg.startsWith( "<svg xmlns=\"http://www.w3.org/2000/svg\"" ) );
        assertTrue( strSvg.endsWith( "</svg>" ) );
        assertEquals( 60, height( strSvg ) );
    }

    @Test
    public void testEachTypeHasItsShape( )
    {
        String strSvg = _renderer.renderSvg( List.of( field( "Radio", StepTemplateFieldKind.RADIO ) ) );
        assertTrue( strSvg.contains( "<circle" ), "radio buttons are circles" );
        strSvg = _renderer.renderSvg( List.of( field( "File", StepTemplateFieldKind.FILE ) ) );
        assertTrue( strSvg.contains( "stroke-dasharray" ), "a file is a dashed drop zone" );
        strSvg = _renderer.renderSvg( List.of( field( "Select", StepTemplateFieldKind.SELECT ) ) );
        assertTrue( strSvg.contains( "<path" ), "a drop-down list has its chevron" );
        assertTrue( height( _renderer.renderSvg( List.of( field( "Long", StepTemplateFieldKind.TEXTAREA ) ) ) ) > height(
                _renderer.renderSvg( List.of( field( "Short", StepTemplateFieldKind.INPUT ) ) ) ), "a text area is taller than a text box" );
    }

    @Test
    public void testGroupFramesItsContentBehindIt( )
    {
        StepTemplatePreviewField group = field( "Group", StepTemplateFieldKind.GROUP );
        group.getChildren( ).add( field( "Inside", StepTemplateFieldKind.INPUT ) );
        String strSvg = _renderer.renderSvg( List.of( group ) );
        int nFrame = strSvg.indexOf( "fill=\"none\" stroke=\"#dce1e7\"" );
        int nHeader = strSvg.indexOf( "fill=\"#206bc4\"" );
        assertTrue( nFrame > 0 && nHeader > nFrame, "the frame of the group is drawn before (under) its header: " + strSvg );
    }

    @Test
    public void testRowsAreCapped( )
    {
        List<StepTemplatePreviewField> listFields = new ArrayList<>( );
        for ( int i = 0; i < 30; i++ )
        {
            listFields.add( field( "Field " + i, StepTemplateFieldKind.INPUT ) );
        }
        List<StepTemplatePreviewField> listMax = listFields.subList( 0, StepTemplateThumbnailRenderer.MAX_ROWS );
        assertTrue( height( _renderer.renderSvg( listFields ) ) < height( _renderer.renderSvg( listMax ) ) + 20,
                "the fields after the cap only add an ellipsis" );
    }

    @Test
    public void testNoDataTextInTheImage( )
    {
        String strSvg = _renderer.renderSvg( List.of( field( "<script>alert(1)</script> & \"title\"", StepTemplateFieldKind.INPUT ) ) );
        assertFalse( strSvg.contains( "script" ) );
        assertFalse( strSvg.contains( "title" ) );
        assertFalse( strSvg.contains( "&" ) );
    }

    @Test
    public void testDataUrl( )
    {
        List<StepTemplatePreviewField> listFields = List.of( field( "Date", StepTemplateFieldKind.DATE ) );
        String strUrl = _renderer.renderDataUrl( listFields );
        assertTrue( strUrl.startsWith( "data:image/svg+xml;base64," ) );
        String strDecoded = new String( Base64.getDecoder( ).decode( strUrl.substring( "data:image/svg+xml;base64,".length( ) ) ), StandardCharsets.UTF_8 );
        assertEquals( _renderer.renderSvg( listFields ), strDecoded );
    }
}
