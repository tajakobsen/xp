package com.enonic.xp.impl.server.rest.task;

import java.util.Map;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.impl.server.rest.model.NodeImportResultJson;
import com.enonic.xp.impl.server.rest.task.listener.ImportListenerImpl;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

import static com.google.common.base.Strings.emptyToNull;

public class ImportRunnableTask
    implements RunnableTask
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private final NodePath nodePath;

    private final String exportName;

    private final boolean dryRun;

    private final boolean importWithIds;

    private final boolean importWithPermissions;

    private final String xslSource;

    private final Map<String, Object> xslParams;

    private final ExportService exportService;

    private final RepositoryService repositoryService;

    private final NodeRepositoryService nodeRepositoryService;

    private ImportRunnableTask( Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.branch = builder.branch;
        this.nodePath = builder.nodePath;
        this.exportName = builder.exportName;
        this.dryRun = builder.dryRun;
        this.importWithIds = builder.importWithIds;
        this.importWithPermissions = builder.importWithPermissions;
        this.xslSource = builder.xslSource;
        this.xslParams = builder.xslParams;

        this.exportService = builder.exportService;
        this.repositoryService = builder.repositoryService;
        this.nodeRepositoryService = builder.nodeRepositoryService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final NodeImportResult result = getContext().callWith( () -> {
            final ImportNodesParams.Builder builder = ImportNodesParams.create()
                .exportName( exportName )
                .targetNodePath( nodePath )
                .dryRun( dryRun )
                .includeNodeIds( importWithIds )
                .includePermissions( importWithPermissions )
                .xsltFileName( emptyToNull( xslSource ) )
                .xsltParams( xslParams )
                .nodeImportListener( new ImportListenerImpl( progressReporter ) );

            return this.exportService.importNodes( builder.build() );
        } );

        if ( targetIsSystemRepo() )
        {
            initializeStoredRepositories();
        }

        progressReporter.info( NodeImportResultJson.from( result ).toString() );
    }

    private Context getContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .branch( branch )
            .repositoryId( repositoryId )
            .build();
    }

    private boolean targetIsSystemRepo()
    {
        return SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) &&
            SystemConstants.BRANCH_SYSTEM.equals( branch );
    }

    private void initializeStoredRepositories()
    {
        this.repositoryService.invalidateAll();
        for ( Repository repository : repositoryService.list() )
        {
            if ( !this.nodeRepositoryService.isInitialized( repository.getId() ) )
            {
                final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create()
                    .repositoryId( repository.getId() )
                    .repositorySettings( repository.getSettings() )
                    .data( repository.getData() )
                    .build();
                this.nodeRepositoryService.create( createRepositoryParams );
            }
        }
    }

    public static class Builder
    {
        private RepositoryId repositoryId;

        private Branch branch;

        private NodePath nodePath;

        private String exportName;

        private boolean dryRun;

        private boolean importWithIds;

        private boolean importWithPermissions;

        private String xslSource;

        private Map<String, Object> xslParams;

        private ExportService exportService;

        private RepositoryService repositoryService;

        private NodeRepositoryService nodeRepositoryService;

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder branch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder exportName( String exportName )
        {
            this.exportName = exportName;
            return this;
        }

        public Builder dryRun( boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public Builder importWithIds( boolean importWithIds )
        {
            this.importWithIds = importWithIds;
            return this;
        }

        public Builder importWithPermissions( boolean importWithPermissions )
        {
            this.importWithPermissions = importWithPermissions;
            return this;
        }

        public Builder xslSource( String xslSource )
        {
            this.xslSource = xslSource;
            return this;
        }

        public Builder xslParams( Map<String, Object> xslParams )
        {
            this.xslParams = xslParams;
            return this;
        }

        public Builder exportService( final ExportService exportService )
        {
            this.exportService = exportService;
            return this;
        }

        public Builder repositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return this;
        }

        public Builder nodeRepositoryService( final NodeRepositoryService nodeRepositoryService )
        {
            this.nodeRepositoryService = nodeRepositoryService;
            return this;
        }

        public ImportRunnableTask build()
        {
            return new ImportRunnableTask( this );
        }
    }
}
