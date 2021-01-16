package io.netty.handler.codec.redis;

import io.netty.handler.codec.CodecException;

public final class RedisCodecException extends CodecException {
   private static final long serialVersionUID = 5570454251549268063L;

   public RedisCodecException(String var1) {
      super(var1);
   }

   public RedisCodecException(Throwable var1) {
      super(var1);
   }
}
