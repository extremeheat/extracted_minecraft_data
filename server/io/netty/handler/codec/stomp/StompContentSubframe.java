package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public interface StompContentSubframe extends ByteBufHolder, StompSubframe {
   StompContentSubframe copy();

   StompContentSubframe duplicate();

   StompContentSubframe retainedDuplicate();

   StompContentSubframe replace(ByteBuf var1);

   StompContentSubframe retain();

   StompContentSubframe retain(int var1);

   StompContentSubframe touch();

   StompContentSubframe touch(Object var1);
}
