package io.netty.handler.codec.redis;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public abstract class AbstractStringRedisMessage implements RedisMessage {
   private final String content;

   AbstractStringRedisMessage(String var1) {
      super();
      this.content = (String)ObjectUtil.checkNotNull(var1, "content");
   }

   public final String content() {
      return this.content;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "content=" + this.content + ']';
   }
}
