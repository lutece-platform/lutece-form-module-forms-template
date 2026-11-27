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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A field of a step template as its preview shows it: a question or a group, with the questions and groups it holds
 */
public class StepTemplatePreviewField implements Serializable
{
    private static final long serialVersionUID = 4398275610923465721L;

    private String _strTitle;
    private String _strTypeTitle;
    private String _strIconName;
    private StepTemplateFieldKind _kind;
    private int _nDepth;
    private boolean _bMandatory;
    private final List<StepTemplatePreviewField> _listChildren = new ArrayList<>( );

    /**
     * @return the title of the question or of the group
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * @param strTitle
     *            the title of the question or of the group
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * @return the title of the entry type (Date, Short text...), empty for a group
     */
    public String getTypeTitle( )
    {
        return _strTypeTitle;
    }

    /**
     * @param strTypeTitle
     *            the title of the entry type
     */
    public void setTypeTitle( String strTypeTitle )
    {
        _strTypeTitle = strTypeTitle;
    }

    /**
     * @return the Tabler icon name of the entry type
     */
    public String getIconName( )
    {
        return _strIconName;
    }

    /**
     * @param strIconName
     *            the Tabler icon name of the entry type
     */
    public void setIconName( String strIconName )
    {
        _strIconName = strIconName;
    }

    /**
     * @return the shape the thumbnail draws for this field
     */
    public StepTemplateFieldKind getKind( )
    {
        return _kind;
    }

    /**
     * @param kind
     *            the shape the thumbnail draws for this field
     */
    public void setKind( StepTemplateFieldKind kind )
    {
        _kind = kind;
    }

    /**
     * @return true for a group
     */
    public boolean isGroup( )
    {
        return _kind == StepTemplateFieldKind.GROUP;
    }

    /**
     * @return the depth of the field: 0 directly in the step, 1 in a group...
     */
    public int getDepth( )
    {
        return _nDepth;
    }

    /**
     * @param nDepth
     *            the depth of the field
     */
    public void setDepth( int nDepth )
    {
        _nDepth = nDepth;
    }

    /**
     * @return true when the question must be answered
     */
    public boolean isMandatory( )
    {
        return _bMandatory;
    }

    /**
     * @param bMandatory
     *            true when the question must be answered
     */
    public void setMandatory( boolean bMandatory )
    {
        _bMandatory = bMandatory;
    }

    /**
     * @return the questions and groups of a group, in display order; empty for a question
     */
    public List<StepTemplatePreviewField> getChildren( )
    {
        return _listChildren;
    }
}
