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

import java.util.Map;

/**
 * The shape a step template thumbnail draws for a field, read from the bean name of its entry type
 */
public enum StepTemplateFieldKind
{
    GROUP,
    INPUT,
    TEXTAREA,
    RADIO,
    CHECKBOX,
    SELECT,
    DATE,
    FILE,
    MAP,
    COMMENT;

    private static final Map<String, StepTemplateFieldKind> MAP_BEAN_NAMES = Map.ofEntries( Map.entry( "forms.entryTypeGroup", GROUP ),
            Map.entry( "forms.entryTypeTextArea", TEXTAREA ), Map.entry( "forms.entryTypeRadioButton", RADIO ),
            Map.entry( "forms.entryTypeCheckBox", CHECKBOX ), Map.entry( "forms.entryTypeTermsOfService", CHECKBOX ),
            Map.entry( "forms.entryTypeSelect", SELECT ), Map.entry( "forms.entryTypeSelectOrder", SELECT ), Map.entry( "forms.entryTypeDate", DATE ),
            Map.entry( "forms.entryTypeSlot", DATE ), Map.entry( "forms.entryTypeFile", FILE ), Map.entry( "forms.entryTypeImage", FILE ),
            Map.entry( "forms.entryTypeCamera", FILE ), Map.entry( "forms.entryTypeGalleryImage", FILE ),
            Map.entry( "forms.entryTypeAutomaticFileReading", FILE ), Map.entry( "forms.entryTypeGeolocation", MAP ),
            Map.entry( "forms.entryTypeCartography", MAP ), Map.entry( "forms.entryTypeComment", COMMENT ) );

    /**
     * Gives the shape of an entry type
     *
     * @param strBeanName
     *            the bean name of the entry type (forms.entryTypeDate...), may be null
     * @return its shape; a one-line input for an unknown type (text, number, phone, user attribute...)
     */
    public static StepTemplateFieldKind fromBeanName( String strBeanName )
    {
        return strBeanName == null ? INPUT : MAP_BEAN_NAMES.getOrDefault( strBeanName, INPUT );
    }
}
