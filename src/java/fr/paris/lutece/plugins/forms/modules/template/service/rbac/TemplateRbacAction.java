package fr.paris.lutece.plugins.forms.modules.template.service.rbac;

import java.util.Locale;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.i18n.Localizable;
import fr.paris.lutece.portal.service.rbac.RBACAction;

public class TemplateRbacAction implements RBACAction, Localizable
{
    // Variables declarations
    private String _strUrl;
    private String _strNameKey;
    private String _strIconUrl;
    private String _strPermission;
    private Locale _locale;

    /**
     * Returns the Url
     *
     * @return The Url
     */
    public String getUrl( )
    {
        return _strUrl;
    }

    /**
     * Sets the Url
     *
     * @param strUrl
     *            The Url
     */
    public void setUrl( String strUrl )
    {
        _strUrl = strUrl;
    }

    /**
     * Returns the NameKey
     *
     * @return The NameKey
     */
    public String getNameKey( )
    {
        return _strNameKey;
    }

    /**
     * Returns the Name
     *
     * @return The Name
     */
    public String getName( )
    {
        return I18nService.getLocalizedString( _strNameKey, _locale );
    }

    /**
     * Sets the NameKey
     *
     * @param strNameKey
     *            The NameKey
     */
    public void setNameKey( String strNameKey )
    {
        _strNameKey = strNameKey;
    }

    /**
     * Returns the IconUrl
     *
     * @return The IconUrl
     */
    public String getIconUrl( )
    {
        return _strIconUrl;
    }

    /**
     * Sets the IconUrl
     *
     * @param strIconUrl
     *            The IconUrl
     */
    public void setIconUrl( String strIconUrl )
    {
        _strIconUrl = strIconUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermission( )
    {
        return _strPermission;
    }

    /**
     * Sets the Permission
     *
     * @param strPermission
     *            The Permission
     */
    public void setPermission( String strPermission )
    {
        _strPermission = strPermission;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocale( Locale locale )
    {
        _locale = locale;
    }
}
