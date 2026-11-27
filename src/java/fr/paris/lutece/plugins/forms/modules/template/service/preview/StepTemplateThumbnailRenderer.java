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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Draws the thumbnail of a step template: an SVG wireframe of its fields, one row per field drawn by its type (a text box, a text area, radio buttons,
 * check boxes, a drop-down list, a date, a file, a map, a comment), the groups framed around their content. No text is written in the image: the labels
 * are grey bars whose length follows the title, so the image needs no escaping and no translation.
 */
@ApplicationScoped
public class StepTemplateThumbnailRenderer
{
    /** Width of the image, in SVG units */
    public static final int WIDTH = 240;
    /** Number of rows drawn at most; the rows after it are announced by an ellipsis */
    public static final int MAX_ROWS = 8;

    private static final String DATA_URL_PREFIX = "data:image/svg+xml;base64,";
    private static final int PADDING = 12;
    private static final int INDENT = 10;
    private static final int GAP = 8;
    private static final int LABEL_HEIGHT = 6;
    private static final int LABEL_SPACE = 10;
    private static final int INPUT_HEIGHT = 14;
    private static final int AREA_HEIGHT = 28;
    private static final int OPTION_STEP = 10;
    private static final int GROUP_HEADER = 14;
    private static final int MIN_LABEL = 36;
    private static final int LABEL_PER_CHAR = 3;
    private static final int EMPTY_HEIGHT = 60;

    private static final String COLOR_BACKGROUND = "#f8fafc";
    private static final String COLOR_BORDER = "#dce1e7";
    private static final String COLOR_LABEL = "#c3cad3";
    private static final String COLOR_CONTROL = "#9aa4b1";
    private static final String COLOR_FIELD = "#ffffff";
    private static final String COLOR_ACCENT = "#206bc4";
    private static final String COLOR_MANDATORY = "#d63939";

    /**
     * Draws the thumbnail of a list of fields
     *
     * @param listFields
     *            the fields directly in the step, each group holding its own
     * @return the SVG document
     */
    public String renderSvg( List<StepTemplatePreviewField> listFields )
    {
        Canvas canvas = new Canvas( );
        canvas._nY = PADDING;
        drawFields( canvas, listFields, PADDING );
        if ( canvas._bTruncated )
        {
            int nCenter = WIDTH / 2;
            for ( int i = -1; i <= 1; i++ )
            {
                canvas.circle( nCenter + i * 8, canvas._nY + 2, 2, COLOR_CONTROL, null );
            }
            canvas._nY += GAP;
        }
        int nHeight = Math.max( EMPTY_HEIGHT, canvas._nY + PADDING - GAP );

        StringBuilder sb = new StringBuilder( );
        sb.append( "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 " ).append( WIDTH ).append( ' ' ).append( nHeight ).append( "\" width=\"" )
                .append( WIDTH ).append( "\" height=\"" ).append( nHeight ).append( "\">" );
        sb.append( "<rect x=\"0.5\" y=\"0.5\" width=\"" ).append( WIDTH - 1 ).append( "\" height=\"" ).append( nHeight - 1 )
                .append( "\" rx=\"6\" fill=\"" ).append( COLOR_BACKGROUND ).append( "\" stroke=\"" ).append( COLOR_BORDER ).append( "\"/>" );
        sb.append( canvas._sb );
        sb.append( "</svg>" );
        return sb.toString( );
    }

    /**
     * Draws the thumbnail of a list of fields as a data URL, for the src attribute of an img
     *
     * @param listFields
     *            the fields directly in the step, each group holding its own
     * @return the data URL of the SVG image
     */
    public String renderDataUrl( List<StepTemplatePreviewField> listFields )
    {
        return DATA_URL_PREFIX + Base64.getEncoder( ).encodeToString( renderSvg( listFields ).getBytes( StandardCharsets.UTF_8 ) );
    }

    private void drawFields( Canvas canvas, List<StepTemplatePreviewField> listFields, int nX )
    {
        for ( StepTemplatePreviewField field : listFields )
        {
            if ( canvas._nRows >= MAX_ROWS )
            {
                canvas._bTruncated = true;
                return;
            }
            canvas._nRows++;
            if ( field.isGroup( ) )
            {
                drawGroup( canvas, field, nX );
            }
            else
            {
                drawQuestion( canvas, field, nX );
            }
        }
    }

    private void drawGroup( Canvas canvas, StepTemplatePreviewField field, int nX )
    {
        int nTop = canvas._nY;
        int nWidth = WIDTH - PADDING - nX;
        // the frame of the group is drawn under its content: it goes where the group starts in the document
        int nFrameIndex = canvas._sb.length( );
        canvas.rect( nX + 6, nTop + 4, labelWidth( field.getTitle( ), nWidth - 12 ), LABEL_HEIGHT, 3, COLOR_ACCENT, null, false );
        canvas._nY = nTop + GROUP_HEADER + 4;
        drawFields( canvas, field.getChildren( ), nX + INDENT );
        int nBottom = Math.max( canvas._nY, nTop + GROUP_HEADER + GAP );
        canvas._sb.insert( nFrameIndex, Canvas.rectElement( nX, nTop, nWidth, nBottom - nTop, 6, "none", COLOR_BORDER, false ) );
        canvas._nY = nBottom + GAP;
    }

    private void drawQuestion( Canvas canvas, StepTemplatePreviewField field, int nX )
    {
        int nWidth = WIDTH - PADDING - nX;
        int nY = canvas._nY;
        StepTemplateFieldKind kind = field.getKind( ) == null ? StepTemplateFieldKind.INPUT : field.getKind( );

        if ( kind == StepTemplateFieldKind.COMMENT )
        {
            for ( int i = 0; i < 3; i++ )
            {
                int nLine = ( i == 2 ) ? nWidth * 2 / 3 : nWidth;
                canvas.rect( nX, nY + i * 8, nLine, 4, 2, COLOR_LABEL, null, false );
            }
            canvas._nY = nY + 20 + GAP;
            return;
        }

        int nLabel = labelWidth( field.getTitle( ), nWidth - 8 );
        canvas.rect( nX, nY, nLabel, LABEL_HEIGHT, 3, COLOR_LABEL, null, false );
        if ( field.isMandatory( ) )
        {
            canvas.circle( nX + nLabel + 5, nY + 3, 2, COLOR_MANDATORY, null );
        }
        int nControl = nY + LABEL_SPACE;

        switch( kind )
        {
            case TEXTAREA:
                canvas.rect( nX, nControl, nWidth, AREA_HEIGHT, 3, COLOR_FIELD, COLOR_CONTROL, false );
                canvas._nY = nControl + AREA_HEIGHT;
                break;
            case RADIO:
            case CHECKBOX:
                for ( int i = 0; i < 2; i++ )
                {
                    int nOption = nControl + i * OPTION_STEP;
                    if ( kind == StepTemplateFieldKind.RADIO )
                    {
                        canvas.circle( nX + 4, nOption + 4, 4, COLOR_FIELD, COLOR_CONTROL );
                        if ( i == 0 )
                        {
                            canvas.circle( nX + 4, nOption + 4, 2, COLOR_ACCENT, null );
                        }
                    }
                    else
                    {
                        canvas.rect( nX, nOption, 8, 8, 2, i == 0 ? COLOR_ACCENT : COLOR_FIELD, i == 0 ? null : COLOR_CONTROL, false );
                    }
                    canvas.rect( nX + 14, nOption + 2, Math.min( 60 - i * 14, nWidth - 14 ), 4, 2, COLOR_LABEL, null, false );
                }
                canvas._nY = nControl + 2 * OPTION_STEP - 2;
                break;
            case SELECT:
                canvas.rect( nX, nControl, nWidth, INPUT_HEIGHT, 3, COLOR_FIELD, COLOR_CONTROL, false );
                canvas.path( "M" + ( nX + nWidth - 14 ) + " " + ( nControl + 5 ) + " l4 4 l4 -4", COLOR_CONTROL );
                canvas._nY = nControl + INPUT_HEIGHT;
                break;
            case DATE:
                canvas.rect( nX, nControl, Math.min( 90, nWidth ), INPUT_HEIGHT, 3, COLOR_FIELD, COLOR_CONTROL, false );
                canvas.rect( nX + Math.min( 90, nWidth ) - 14, nControl + 3, 8, 8, 1, null, COLOR_CONTROL, false );
                canvas._nY = nControl + INPUT_HEIGHT;
                break;
            case FILE:
                canvas.rect( nX, nControl, nWidth, 18, 3, COLOR_FIELD, COLOR_CONTROL, true );
                canvas.path( "M" + ( nX + nWidth / 2 ) + " " + ( nControl + 13 ) + " v-8 m-4 4 l4 -4 l4 4", COLOR_CONTROL );
                canvas._nY = nControl + 18;
                break;
            case MAP:
                canvas.rect( nX, nControl, nWidth, AREA_HEIGHT, 3, "#e9eef4", COLOR_CONTROL, false );
                canvas.circle( nX + nWidth / 2, nControl + AREA_HEIGHT / 2 - 2, 4, COLOR_MANDATORY, null );
                canvas._nY = nControl + AREA_HEIGHT;
                break;
            default:
                canvas.rect( nX, nControl, nWidth, INPUT_HEIGHT, 3, COLOR_FIELD, COLOR_CONTROL, false );
                canvas._nY = nControl + INPUT_HEIGHT;
                break;
        }
        canvas._nY += GAP;
    }

    private static int labelWidth( String strTitle, int nMax )
    {
        int nLength = strTitle == null ? 0 : strTitle.length( );
        return Math.max( Math.min( MIN_LABEL, nMax ), Math.min( MIN_LABEL + nLength * LABEL_PER_CHAR, nMax ) );
    }

    /**
     * The SVG elements being written, and the position reached
     */
    private static final class Canvas
    {
        private final StringBuilder _sb = new StringBuilder( );
        private int _nY;
        private int _nRows;
        private boolean _bTruncated;

        private void rect( int nX, int nY, int nWidth, int nHeight, int nRadius, String strFill, String strStroke, boolean bDashed )
        {
            _sb.append( rectElement( nX, nY, nWidth, nHeight, nRadius, strFill, strStroke, bDashed ) );
        }

        private void circle( int nX, int nY, int nRadius, String strFill, String strStroke )
        {
            _sb.append( "<circle cx=\"" ).append( nX ).append( "\" cy=\"" ).append( nY ).append( "\" r=\"" ).append( nRadius ).append( "\" fill=\"" )
                    .append( strFill == null ? "none" : strFill ).append( '"' );
            if ( strStroke != null )
            {
                _sb.append( " stroke=\"" ).append( strStroke ).append( '"' );
            }
            _sb.append( "/>" );
        }

        private void path( String strPath, String strStroke )
        {
            _sb.append( "<path d=\"" ).append( strPath ).append( "\" fill=\"none\" stroke=\"" ).append( strStroke )
                    .append( "\" stroke-width=\"1.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\"/>" );
        }

        private static String rectElement( int nX, int nY, int nWidth, int nHeight, int nRadius, String strFill, String strStroke, boolean bDashed )
        {
            StringBuilder sb = new StringBuilder( );
            sb.append( "<rect x=\"" ).append( nX ).append( "\" y=\"" ).append( nY ).append( "\" width=\"" ).append( Math.max( 1, nWidth ) )
                    .append( "\" height=\"" ).append( nHeight ).append( "\" rx=\"" ).append( nRadius ).append( "\" fill=\"" )
                    .append( strFill == null ? "none" : strFill ).append( '"' );
            if ( strStroke != null )
            {
                sb.append( " stroke=\"" ).append( strStroke ).append( '"' );
            }
            if ( bDashed )
            {
                sb.append( " stroke-dasharray=\"3 2\"" );
            }
            return sb.append( "/>" ).toString( );
        }
    }
}
