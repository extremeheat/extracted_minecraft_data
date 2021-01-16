package org.apache.logging.log4j.core.layout;

import java.nio.ByteBuffer;

public interface ByteBufferDestination {
   ByteBuffer getByteBuffer();

   ByteBuffer drain(ByteBuffer var1);
}
