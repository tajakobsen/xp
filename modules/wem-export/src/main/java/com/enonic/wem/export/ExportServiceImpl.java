package com.enonic.wem.export;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.export.ExportService;
import com.enonic.wem.api.export.NodeExportResult;
import com.enonic.wem.api.export.NodeImportResult;
import com.enonic.wem.api.home.HomeDir;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.export.internal.BatchedNodeExporter;
import com.enonic.wem.export.internal.NodeImporter;
import com.enonic.wem.export.internal.writer.ExportItemPath;
import com.enonic.wem.export.internal.writer.FileExportWriter;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

@Component(immediate = true)
public class ExportServiceImpl
    implements ExportService
{
    private NodeService nodeService;

    private final XmlNodeSerializer xmlNodeSerializer = new XmlNodeSerializer();


    @Override
    public NodeExportResult exportNodes( final NodePath nodePath )
    {

        return BatchedNodeExporter.create().
            xmlNodeSerializer( xmlNodeSerializer ).
            nodePath( nodePath ).
            nodeService( this.nodeService ).
            nodeExportWriter( new FileExportWriter() ).
            exportHome( ExportItemPath.from( HomeDir.get().toString() + "/exports" ) ).
            exportName( "node_" + LocalDateTime.now().format( DateTimeFormatter.ISO_LOCAL_DATE_TIME ) ).
            build().
            export();
    }

    @Override
    public NodeImportResult importNodes( final String exportName, final NodePath importRoot )
    {
        return NodeImporter.create().
            xmlNodeSerializer( this.xmlNodeSerializer ).
            nodeService( this.nodeService ).
            exportHome( ExportItemPath.from( HomeDir.get().toString() + "/exports" ) ).
            exportName( exportName ).
            importRoot( importRoot ).
            build().
            execute();
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
