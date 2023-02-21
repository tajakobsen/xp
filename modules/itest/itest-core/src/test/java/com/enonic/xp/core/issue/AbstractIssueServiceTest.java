package com.enonic.xp.core.issue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.event.EventPublisherImpl;
import com.enonic.xp.core.impl.issue.IssueServiceImpl;
import com.enonic.xp.core.impl.project.init.ContentInitializer;
import com.enonic.xp.core.impl.project.init.IssueInitializer;
import com.enonic.xp.internal.blobstore.MemoryBlobStore;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.repo.impl.binary.BinaryServiceImpl;
import com.enonic.xp.repo.impl.branch.storage.BranchServiceImpl;
import com.enonic.xp.repo.impl.elasticsearch.AbstractElasticsearchIntegrationTest;
import com.enonic.xp.repo.impl.elasticsearch.IndexServiceInternalImpl;
import com.enonic.xp.repo.impl.elasticsearch.search.SearchDaoImpl;
import com.enonic.xp.repo.impl.elasticsearch.storage.StorageDaoImpl;
import com.enonic.xp.repo.impl.index.IndexServiceImpl;
import com.enonic.xp.repo.impl.node.NodeServiceImpl;
import com.enonic.xp.repo.impl.node.dao.NodeVersionServiceImpl;
import com.enonic.xp.repo.impl.repository.NodeRepositoryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryEntryServiceImpl;
import com.enonic.xp.repo.impl.repository.RepositoryServiceImpl;
import com.enonic.xp.repo.impl.search.NodeSearchServiceImpl;
import com.enonic.xp.repo.impl.storage.IndexDataServiceImpl;
import com.enonic.xp.repo.impl.storage.NodeStorageServiceImpl;
import com.enonic.xp.repo.impl.version.VersionServiceImpl;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class AbstractIssueServiceTest
    extends AbstractElasticsearchIntegrationTest
{
    public static final RepositoryId TEST_REPO_ID = RepositoryId.from( "com.enonic.cms.default" );

    public static final User TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "test-user" ) ).login( "test-user" ).build();

    public static final AuthenticationInfo TEST_DEFAULT_USER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        principals( RoleKeys.CONTENT_MANAGER_ADMIN ).
        user( TEST_DEFAULT_USER ).
        build();

    protected static final Branch WS_DEFAULT = Branch.create().
        value( "draft" ).
        build();

    protected IssueServiceImpl issueService;

    protected NodeServiceImpl nodeService;

    protected BinaryServiceImpl binaryService;

    private IndexServiceImpl indexService;

    private RepositoryServiceImpl repositoryService;

    private ExecutorService executorService;

    private Context initialContext;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        executorService = Executors.newSingleThreadExecutor();

        deleteAllIndices();

        final Context ctx = ContextBuilder.create().
            branch( WS_DEFAULT ).
            repositoryId( TEST_REPO_ID ).
            authInfo( TEST_DEFAULT_USER_AUTHINFO ).
            build();


        initialContext = ContextAccessor.current();
        ContextAccessor.INSTANCE.set( ctx );

        final MemoryBlobStore blobStore = new MemoryBlobStore();

        binaryService = new BinaryServiceImpl();
        binaryService.setBlobStore( blobStore );

        final StorageDaoImpl storageDao = new StorageDaoImpl();
        storageDao.setClient( client );

        final EventPublisherImpl eventPublisher = new EventPublisherImpl( executorService );

        SearchDaoImpl searchDao = new SearchDaoImpl();
        searchDao.setClient( client );

        BranchServiceImpl branchService = new BranchServiceImpl();
        branchService.setStorageDao( storageDao );
        branchService.setSearchDao(searchDao);

        VersionServiceImpl versionService = new VersionServiceImpl();
        versionService.setStorageDao( storageDao );

        IndexServiceInternalImpl indexServiceInternal = new IndexServiceInternalImpl();
        indexServiceInternal.setClient( client );

        NodeVersionServiceImpl nodeDao = new NodeVersionServiceImpl( blobStore );

        issueService = new IssueServiceImpl();

        IndexDataServiceImpl indexedDataService = new IndexDataServiceImpl();
        indexedDataService.setStorageDao( storageDao );

        indexService = new IndexServiceImpl();
        indexService.setIndexServiceInternal(indexServiceInternal);

        NodeStorageServiceImpl storageService = new NodeStorageServiceImpl();
        storageService.setBranchService(branchService);
        storageService.setVersionService(versionService);
        storageService.setNodeVersionService(nodeDao);
        storageService.setIndexDataService(indexedDataService);

        NodeSearchServiceImpl searchService = new NodeSearchServiceImpl();
        searchService.setSearchDao( searchDao );

        final NodeRepositoryServiceImpl nodeRepositoryService = new NodeRepositoryServiceImpl();
        nodeRepositoryService.setIndexServiceInternal( indexServiceInternal );

        final IndexServiceInternalImpl elasticsearchIndexService = new IndexServiceInternalImpl();
        elasticsearchIndexService.setClient( client );

        final RepositoryEntryServiceImpl repositoryEntryService = new RepositoryEntryServiceImpl();
        repositoryEntryService.setIndexServiceInternal( elasticsearchIndexService );
        repositoryEntryService.setNodeStorageService( storageService );
        repositoryEntryService.setNodeSearchService( searchService );
        repositoryEntryService.setEventPublisher( eventPublisher );
        repositoryEntryService.setBinaryService( binaryService );

        repositoryService =
            new RepositoryServiceImpl( repositoryEntryService, elasticsearchIndexService, nodeRepositoryService, storageService,
                                       searchService );
        repositoryService.initialize();

        nodeService = new NodeServiceImpl();
        nodeService.setIndexServiceInternal( indexServiceInternal );
        nodeService.setNodeStorageService( storageService );
        nodeService.setNodeSearchService( searchService );
        nodeService.setEventPublisher( eventPublisher );
        nodeService.setBinaryService( binaryService );
        nodeService.setRepositoryService( repositoryService );
        nodeService.initialize();

        issueService.setNodeService( nodeService );

        initializeRepository();
    }

    @AfterEach
    void tearDown()
    {
        ContextAccessor.INSTANCE.set( initialContext );
        executorService.shutdownNow();
    }

    protected Issue createIssue( CreateIssueParams.Builder builder )
    {
        return this.issueService.create( builder.build() );
    }

    private void initializeRepository()
    {
        ContentInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            setRepositoryService( repositoryService ).
            build().
            initialize();
        IssueInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            build().
            initialize();
    }
}
