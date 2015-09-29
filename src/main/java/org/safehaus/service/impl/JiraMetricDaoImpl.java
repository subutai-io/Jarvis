package org.safehaus.service.impl;


import java.util.List;

import javax.jws.WebService;

import org.safehaus.dao.Dao;
import org.safehaus.dao.entities.jira.JiraMetricIssue;
import org.safehaus.dao.entities.jira.JiraProject;
import org.safehaus.service.api.JiraMetricDao;
import org.safehaus.service.rest.JiraMetricsRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service( "jiraMetricDao" )
@WebService( serviceName = "JiraMetricDaoImpl",
        endpointInterface = "org.safehaus.service.rest.JiraMetricsRestService" )
public class JiraMetricDaoImpl implements JiraMetricDao, JiraMetricsRestService
{
    private static final Logger log = LoggerFactory.getLogger( JiraMetricDaoImpl.class );

    @Autowired
    private Dao dao;


    public JiraMetricDaoImpl()
    {
    }


    @Override
    public void insertJiraMetricIssue( JiraMetricIssue jiraMetricIssue )
    {
        log.info( "Inserting JiraMetricIssue with id {}", jiraMetricIssue.getIssueId() );
        dao.insert( jiraMetricIssue );
    }


    @Override
    public JiraMetricIssue findJiraMetricIssueById( Long id )
    {
        log.info( "Finding JiraMetricIssue with id {}", id );
        return dao.findById( JiraMetricIssue.class, id );
    }


    @Override
    public JiraMetricIssue findJiraMetricIssueById( final String id )
    {
        log.info( "Finding JiraMetricIssue with id {}", id );
        JiraMetricIssue result = new JiraMetricIssue();
        JiraMetricIssue tmp = dao.findById( JiraMetricIssue.class, Long.valueOf( id ) );
        if ( tmp != null )
        {
            result = tmp;
        }
        return result;
    }


    @Override
    public List<JiraMetricIssue> findJiraMetricIssuesByAssigneeName( final String assigneeName )
    {
        String query =
                "Select j from " + JiraMetricIssue.class.getSimpleName() + " j where j.assigneeName =:assignee_name";

        log.info( "Finding JiraMetricIssues by assigneeName: {}", assigneeName );

        List<JiraMetricIssue> jiraMetricIssues =
                ( List<JiraMetricIssue> ) dao.findByQuery( query, "assignee_name", assigneeName );
        return jiraMetricIssues;
    }


    @Override
    public List<JiraMetricIssue> findJiraMetricIssueByKeyId( final String keyId )
    {
        String query = "Select j from " + JiraMetricIssue.class.getSimpleName() + " j where j.issueKey =:issue_key";
        log.info( "Finding JiraMetricIssue by key id: {}", keyId );

        List<JiraMetricIssue> jiraMetricIssues = ( List<JiraMetricIssue> ) dao.findByQuery( query, "issue_key", keyId );
        return jiraMetricIssues;
    }


    @Override
    public void updateJiraMetricIssue( JiraMetricIssue jiraMetricIssue )
    {
        if ( jiraMetricIssue == null || jiraMetricIssue.getIssueId() == null )
        {
            throw new IllegalArgumentException( "Id should not be null to perform update." );
        }
        log.info( "Updating JiraMetricIssue, id: {}", jiraMetricIssue.getIssueId() );
        dao.merge( jiraMetricIssue );
    }


    @Override
    public void deleteJiraMetricIssue( JiraMetricIssue jiraMetricIssue )
    {
        if ( jiraMetricIssue == null || jiraMetricIssue.getIssueId() == null )
        {
            throw new IllegalArgumentException( "Id should not be null to perform delete." );
        }
        log.info( "Deleting JiraMetricIssue, id: {}", jiraMetricIssue.getIssueId() );
        dao.remove( jiraMetricIssue );
    }


    @Override
    public void batchInsert( final List<JiraMetricIssue> issues )
    {
        dao.batchInsert( issues );
    }


    @Override
    public List<JiraProject> getProjects()
    {
        String query = String.format( "select p from %s p", JiraProject.class.getSimpleName() );
        List<JiraProject> projects = ( List<JiraProject> ) dao.findByQuery( query );
        return dao.getAll( JiraProject.class );
    }


    @Override
    public JiraProject getProject( final String projectKey )
    {
        String query = String.format( "select p from %s p where p.key = :key", JiraProject.class.getSimpleName() );
        return ( JiraProject ) dao.findByQuery( query, "key", projectKey );
    }


    @Override
    public List<JiraProject> getProjectByKey( final String key )
    {
        String query = String.format( "select p from %s p where p.key = :key", JiraProject.class.getSimpleName() );
        return ( List<JiraProject> ) dao.findByQuery( query, "key", key );
    }


    @Override
    public List<JiraProject> getProjectById( final Long projectId )
    {
        String query =
                String.format( "select p from %s p where p.key = :projectId", JiraProject.class.getSimpleName() );
        return ( List<JiraProject> ) dao.findByQuery( query, "projectId", projectId );
    }


    @Override
    public void saveProject( final JiraProject project )
    {
        dao.insert( project );
    }


    @Override
    public void updateProject( final JiraProject project )
    {
        dao.merge( project );
    }


    @Override
    public void deleteProject( final JiraProject project )
    {
        dao.remove( project );
    }


    @Override
    public List<JiraMetricIssue> getProjectIssues( final String projectKey )
    {
        String query = String.format( "select issue from %s issue where issue.projectKey = :projectKey",
                JiraMetricIssue.class.getSimpleName() );
        List<JiraMetricIssue> projectIssues =
                ( List<JiraMetricIssue> ) dao.findByQuery( query, "projectKey", projectKey );
        return projectIssues;
    }


    public Dao getDao()
    {
        return dao;
    }


    public void setDao( final Dao dao )
    {
        this.dao = dao;
    }
}