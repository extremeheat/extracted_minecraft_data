package io.netty.handler.codec.redis;

import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ArrayRedisMessage extends AbstractReferenceCounted implements RedisMessage {
   private final List<RedisMessage> children;
   public static final ArrayRedisMessage NULL_INSTANCE = new ArrayRedisMessage() {
      public boolean isNull() {
         return true;
      }

      public ArrayRedisMessage retain() {
         return this;
      }

      public ArrayRedisMessage retain(int var1) {
         return this;
      }

      public ArrayRedisMessage touch() {
         return this;
      }

      public ArrayRedisMessage touch(Object var1) {
         return this;
      }

      public boolean release() {
         return false;
      }

      public boolean release(int var1) {
         return false;
      }

      public String toString() {
         return "NullArrayRedisMessage";
      }
   };
   public static final ArrayRedisMessage EMPTY_INSTANCE = new ArrayRedisMessage() {
      public ArrayRedisMessage retain() {
         return this;
      }

      public ArrayRedisMessage retain(int var1) {
         return this;
      }

      public ArrayRedisMessage touch() {
         return this;
      }

      public ArrayRedisMessage touch(Object var1) {
         return this;
      }

      public boolean release() {
         return false;
      }

      public boolean release(int var1) {
         return false;
      }

      public String toString() {
         return "EmptyArrayRedisMessage";
      }
   };

   private ArrayRedisMessage() {
      super();
      this.children = Collections.emptyList();
   }

   public ArrayRedisMessage(List<RedisMessage> var1) {
      super();
      this.children = (List)ObjectUtil.checkNotNull(var1, "children");
   }

   public final List<RedisMessage> children() {
      return this.children;
   }

   public boolean isNull() {
      return false;
   }

   protected void deallocate() {
      Iterator var1 = this.children.iterator();

      while(var1.hasNext()) {
         RedisMessage var2 = (RedisMessage)var1.next();
         ReferenceCountUtil.release(var2);
      }

   }

   public ArrayRedisMessage touch(Object var1) {
      Iterator var2 = this.children.iterator();

      while(var2.hasNext()) {
         RedisMessage var3 = (RedisMessage)var2.next();
         ReferenceCountUtil.touch(var3);
      }

      return this;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "children=" + this.children.size() + ']';
   }

   // $FF: synthetic method
   ArrayRedisMessage(Object var1) {
      this();
   }
}
