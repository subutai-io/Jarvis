package org.safehaus.upsource.client;


import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.safehaus.upsource.model.Project;
import org.safehaus.upsource.model.Revision;
import org.safehaus.upsource.model.RevisionDiffItem;
import org.safehaus.util.JsonUtil;
import org.safehaus.util.RestUtil;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


public class UpsourceManagerImpl implements UpsourceManager
{

    private final String baseUrl;

    protected RestUtil restUtil;

    protected JsonUtil jsonUtil = new JsonUtil();


    public UpsourceManagerImpl( final String baseUrl, final String username, final String password )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( baseUrl ) );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( username ) );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( password ) );

        this.baseUrl = baseUrl;
        this.restUtil = new RestUtil( username, password );
    }


    private static class ParamBuilder
    {
        private Map<String, String> params = Maps.newHashMap();


        public ParamBuilder add( String name, String value )
        {
            params.put( String.format( "\"%s\"", name ), String.format( "\"%s\"", value ) );

            return this;
        }


        public ParamBuilder addValueNoQuote( String name, String value )
        {
            params.put( String.format( "\"%s\"", name ), value );

            return this;
        }


        public ParamBuilder add( String name, Integer value )
        {
            params.put( String.format( "\"%s\"", name ), value.toString() );

            return this;
        }


        public Map<String, String> build()
        {

            Map<String, String> finalMap = Maps.newHashMap();

            StringBuilder paramJson = new StringBuilder( "{" );

            Iterator<Map.Entry<String, String>> paramEntryIter = params.entrySet().iterator();
            while ( paramEntryIter.hasNext() )
            {
                Map.Entry<String, String> paramEntry = paramEntryIter.next();
                paramJson.append( paramEntry.getKey() ).append( ":" ).append( paramEntry.getValue() );
                if ( paramEntryIter.hasNext() )
                {
                    paramJson.append( "," );
                }
            }

            paramJson.append( "}" );

            finalMap.put( "params", paramJson.toString() );

            return finalMap;
        }


        public String getParamString()
        {
            return build().get( "params" );
        }
    }


    protected JsonElement get( String apiPath, ParamBuilder paramBuilder, String elementName )
            throws RestUtil.RestException, UpsourceManagerException
    {
        String response = restUtil.get( String.format( "%s/~rpc/%s", baseUrl, apiPath ),
                paramBuilder == null ? null : paramBuilder.build() );
        JsonObject responseJO = jsonUtil.from( response, JsonObject.class );

        if ( responseJO.has( "error" ) )
        {
            JsonObject errorJO = ( JsonObject ) responseJO.get( "error" );
            throw new UpsourceManagerException( errorJO.get( "message" ).getAsString() );
        }


        JsonObject result = ( JsonObject ) responseJO.get( "result" );

        if ( result == null )
        {
            throw new UpsourceManagerException( String.format( "Could not parse response %s", response ) );
        }


        if ( Strings.isNullOrEmpty( elementName ) )
        {
            return result;
        }
        else
        {
            return result.get( elementName );
        }
    }


    @Override
    public Set<Project> getAllProjects() throws UpsourceManagerException
    {
        try
        {
            return jsonUtil.from( get( "getAllProjects", null, "project" ).toString(), new TypeToken<Set<Project>>()
            {}.getType() );
        }
        catch ( Exception e )
        {
            throw new UpsourceManagerException( e );
        }
    }


    @Override
    public Project getProject( final String projectId ) throws UpsourceManagerException
    {
        try
        {
            return jsonUtil
                    .from( get( "getProjectInfo", new ParamBuilder().add( "projectId", projectId ), null ).toString(),
                            Project.class );
        }
        catch ( Exception e )
        {
            throw new UpsourceManagerException( e );
        }
    }


    @Override
    public Set<Revision> getRevisions( final String projectId, final int limit ) throws UpsourceManagerException
    {
        try
        {
            return jsonUtil.from( get( "getRevisionsList",
                            new ParamBuilder().add( "projectId", projectId ).add( "limit", limit ), "revision" )
                            .toString(), new TypeToken<Set<Revision>>()
                    {}.getType() );
        }
        catch ( Exception e )
        {
            throw new UpsourceManagerException( e );
        }
    }


    @Override
    public Revision getHeadRevision( final String projectId ) throws UpsourceManagerException
    {
        try
        {
            return jsonUtil
                    .from( get( "getHeadRevision", new ParamBuilder().add( "projectId", projectId ), null ).toString(),
                            Revision.class );
        }
        catch ( Exception e )
        {
            throw new UpsourceManagerException( e );
        }
    }


    @Override
    public Set<Revision> getFilteredRevisions( final String projectId, final int limit, final String revisionFilter )
            throws UpsourceManagerException
    {
        try
        {
            return jsonUtil.from( get( "getRevisionsListFiltered",
                            new ParamBuilder().add( "projectId", projectId ).add( "limit", limit )
                                              .add( "query", revisionFilter ), "revision" ).toString(),
                    new TypeToken<Set<Revision>>()
                    {}.getType() );
        }
        catch ( Exception e )
        {
            throw new UpsourceManagerException( e );
        }
    }


    @Override
    public Revision getRevision( final String projectId, final String revisionId ) throws UpsourceManagerException
    {
        try
        {
            return jsonUtil.from( get( "getRevisionInfo",
                            new ParamBuilder().add( "projectId", projectId ).add( "revisionId", revisionId ), null )
                            .toString(), Revision.class );
        }
        catch ( Exception e )
        {
            throw new UpsourceManagerException( e );
        }
    }


    @Override
    public Set<RevisionDiffItem> getRevisionChanges( final String projectId, final String revisionId,
                                                     final String compareToRevisionId, final int limit )
            throws UpsourceManagerException
    {
        try
        {
            ParamBuilder paramBuilder = new ParamBuilder().addValueNoQuote( "revision",
                    new ParamBuilder().add( "projectId", projectId ).add( "revisionId", revisionId ).getParamString() )
                                                          .add( "limit", limit );

            if ( !Strings.isNullOrEmpty( compareToRevisionId ) )
            {
                paramBuilder.add( "compareToRevisionId", compareToRevisionId );
            }

            return jsonUtil.from( get( "getRevisionChanges", paramBuilder, "diff" ).toString(),
                    new TypeToken<Set<RevisionDiffItem>>()
                    {}.getType() );
        }
        catch ( Exception e )
        {
            throw new UpsourceManagerException( e );
        }
    }


    @Override
    public Set<String> getRevisionBranches( final String projectId, final String revisionId )
            throws UpsourceManagerException
    {
        try
        {
            return jsonUtil.from( get( "getRevisionBranches",
                    new ParamBuilder().add( "projectId", projectId ).add( "revisionId", revisionId ), "branchName" )
                    .toString(), new TypeToken<Set<String>>()
            {}.getType() );
        }
        catch ( Exception e )
        {
            throw new UpsourceManagerException( e );
        }
    }
}
