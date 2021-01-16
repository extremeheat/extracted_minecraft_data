package io.netty.channel;

import io.netty.util.internal.StringUtil;

public class ReflectiveChannelFactory<T extends Channel> implements ChannelFactory<T> {
   private final Class<? extends T> clazz;

   public ReflectiveChannelFactory(Class<? extends T> var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("clazz");
      } else {
         this.clazz = var1;
      }
   }

   public T newChannel() {
      try {
         return (Channel)this.clazz.getConstructor().newInstance();
      } catch (Throwable var2) {
         throw new ChannelException("Unable to create Channel from class " + this.clazz, var2);
      }
   }

   public String toString() {
      return StringUtil.simpleClassName(this.clazz) + ".class";
   }
}
