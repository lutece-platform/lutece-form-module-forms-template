package fr.paris.lutece.plugins.forms.modules.template.service.rbac;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class TemplateRbacActionProducer
{

	@Produces
	@ApplicationScoped
	@Named( "forms-template.rbacActionModify" )
	public TemplateRbacAction produceTemplateRbacActionModify(
			@ConfigProperty( name = "forms-template.rbacActionModify.url"  ) String url,
			@ConfigProperty( name = "forms-template.rbacActionModify.nameKey"  ) String nameKey,			
			@ConfigProperty( name = "forms-template.rbacActionModify.iconUrl"  ) String iconUrl,
			@ConfigProperty( name = "forms-template.rbacActionModify.permission"  ) String permission)
	{
		return new TemplateRbacAction( url, nameKey, iconUrl, permission);
	}
	
	@Produces
	@ApplicationScoped
	@Named( "forms-template.rbacActionDelete" )
	public TemplateRbacAction produceTemplateRbacActionDelete(
			@ConfigProperty( name = "forms-template.rbacActionDelete.url"  ) String url,
			@ConfigProperty( name = "forms-template.rbacActionDelete.nameKey"  ) String nameKey,			
			@ConfigProperty( name = "forms-template.rbacActionDelete.iconUrl"  ) String iconUrl,
			@ConfigProperty( name = "forms-template.rbacActionDelete.permission"  ) String permission)
	{
		return new TemplateRbacAction( url, nameKey, iconUrl, permission);
	}
	
	@Produces
	@ApplicationScoped
	@Named( "forms-template.rbacActionCopy" )
	public TemplateRbacAction produceTemplateRbacActionCopy(
			@ConfigProperty( name = "forms-template.rbacActionCopy.url"  ) String url,
			@ConfigProperty( name = "forms-template.rbacActionCopy.nameKey"  ) String nameKey,			
			@ConfigProperty( name = "forms-template.rbacActionCopy.iconUrl"  ) String iconUrl,
			@ConfigProperty( name = "forms-template.rbacActionCopy.permission"  ) String permission)
	{
		return new TemplateRbacAction( url, nameKey, iconUrl, permission);
	}
	
	@Produces
	@ApplicationScoped
	@Named( "forms-template.rbacActionExport" )
	public TemplateRbacAction produceTemplateRbacActionExport(
			@ConfigProperty( name = "forms-template.rbacActionExport.url"  ) String url,
			@ConfigProperty( name = "forms-template.rbacActionExport.nameKey"  ) String nameKey,			
			@ConfigProperty( name = "forms-template.rbacActionExport.iconUrl"  ) String iconUrl,
			@ConfigProperty( name = "forms-template.rbacActionExport.permission"  ) String permission)
	{
		return new TemplateRbacAction( url, nameKey, iconUrl, permission);
	}
}