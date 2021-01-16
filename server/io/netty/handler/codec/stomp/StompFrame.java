package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;

public interface StompFrame extends StompHeadersSubframe, LastStompContentSubframe {
   StompFrame copy();

   StompFrame duplicate();

   StompFrame retainedDuplicate();

   StompFrame replace(ByteBuf var1);

   StompFrame retain();

   StompFrame retain(int var1);

   StompFrame touch();

   StompFrame touch(Object var1);
}
