package com.enonic.wem.api.blob;

import java.io.InputStream;

import com.google.common.io.ByteSource;

public interface BlobService
{
    Blob create( InputStream byteSource );

    Blob get( BlobKey blobKey );

    ByteSource getByteSource( final BlobKey blobKey );
}
