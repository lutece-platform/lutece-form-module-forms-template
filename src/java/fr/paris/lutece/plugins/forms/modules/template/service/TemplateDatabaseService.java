package fr.paris.lutece.plugins.forms.modules.template.service;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateControlHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateDisplayHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateEntryHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateFieldHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateGroupHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateQuestionHome;
import fr.paris.lutece.plugins.forms.modules.template.business.TemplateStepHome;
import fr.paris.lutece.plugins.forms.service.IFormDatabaseService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;

public class TemplateDatabaseService implements IFormDatabaseService
{
    public static final String BEAN_NAME = "forms-template.templateDatabaseService";

    @Override
    public void createFormDisplay( FormDisplay formDisplay )
    {
        TemplateDisplayHome.create( formDisplay );
    }
    
    @Override
    public List<Question> getListQuestionByForm( int nIdTemplate )
    {
        return TemplateQuestionHome.getQuestionsListByStep( nIdTemplate );
    }
    
    @Override
    public Question findQuestionByPrimaryKey( int idQuestion )
    {
        return TemplateQuestionHome.findByPrimaryKey( idQuestion );
    }
    
    @Override
    public Entry findEntryByPrimaryKey( int idEntry )
    {
        return TemplateEntryHome.findByPrimaryKey( idEntry );
    }
    
    @Override
    public void updateField( Field field )
    {
        TemplateFieldHome.update( field );
    }
    
    @Override
    public Field findFieldByPrimaryKey( int idField )
    {
        return TemplateFieldHome.findByPrimaryKey( idField );
    }
    
    @Override
    public void updateQuestion( Question question )
    {
        TemplateQuestionHome.update( question );
    }
    
    @Override
    public void createGroup( Group group )
    {
        TemplateGroupHome.create( group );
    }
    
    @Override
    public void updateGroup( Group group )
    {
        TemplateGroupHome.update( group );
    }
    
    @Override
    public Group findGroupByPrimaryKey( int idGroup )
    {
        return TemplateGroupHome.findByPrimaryKey( idGroup );
    }
    
    @Override
    public FormDisplay findDisplayByPrimaryKey( int idDisplay )
    {
        return TemplateDisplayHome.findByPrimaryKey( idDisplay );
    }

    @Override
    public void createEntry( Entry entry )
    {
        TemplateEntryHome.create( entry );
    }

    @Override
    public void createField( Field field )
    {
        TemplateFieldHome.create( field );
    }

    @Override
    public void createQuestion( Question question )
    {
        TemplateQuestionHome.create( question );
    }

    @Override
    public Step findStepByPrimaryKey( int idStep )
    {
        return TemplateStepHome.findByPrimaryKey( idStep );
    }

    @Override
    public void updateEntry( Entry entry )
    {
        TemplateEntryHome.update( entry );
    }
    
    @Override
    public List<FormDisplay> getFormDisplayListByParent( int nIdStep, int nIdParent )
    {
        return TemplateDisplayHome.getFormDisplayListByParent( nIdStep, nIdParent );
    }
    
    @Override
    public void updateFormDisplay( FormDisplay formDisplay )
    {
        TemplateDisplayHome.update( formDisplay );
    }
    
    @Override
    public Entry copyEntry( Entry entry )
    {
        return TemplateEntryHome.copy( entry );
    }
    
    @Override
    public List<Control> getControlByQuestion( int nIdQuestion )
    {
        return TemplateControlHome.getControlByQuestion( nIdQuestion );
    }
    
    @Override
    public FormDisplay getFormDisplayByFormStepAndComposite( int nIdForm, int nIdStep, int nIdComposite )
    {
        return TemplateDisplayHome.getFormDisplayByStepAndComposite( nIdStep, nIdComposite );
    }
    
    @Override
    public void createControl( Control control )
    {
        TemplateControlHome.create( control );
    }
}
