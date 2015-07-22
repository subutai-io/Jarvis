package org.safehaus.service;


import java.util.List;
import org.safehaus.stash.model.StashMetricIssue;


public interface StashMetricService
{
	void insertStashMetricIssue( StashMetricIssue stashMetricIssue );


	StashMetricIssue findStashMetricIssueById( Long id );


	List<StashMetricIssue> findStashMetricIssuesByProjectName( String projectName );


	void updateStashMetricIssue( StashMetricIssue stashMetricIssue );


	void deleteStashMetricIssue( StashMetricIssue stashMetricIssue );
}
