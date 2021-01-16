package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ConstantPool<T extends Constant<T>> {
   private final ConcurrentMap<String, T> constants = PlatformDependent.newConcurrentHashMap();
   private final AtomicInteger nextId = new AtomicInteger(1);

   public ConstantPool() {
      super();
   }

   public T valueOf(Class<?> var1, String var2) {
      if (var1 == null) {
         throw new NullPointerException("firstNameComponent");
      } else if (var2 == null) {
         throw new NullPointerException("secondNameComponent");
      } else {
         return this.valueOf(var1.getName() + '#' + var2);
      }
   }

   public T valueOf(String var1) {
      checkNotNullAndNotEmpty(var1);
      return this.getOrCreate(var1);
   }

   private T getOrCreate(String var1) {
      Constant var2 = (Constant)this.constants.get(var1);
      if (var2 == null) {
         Constant var3 = this.newConstant(this.nextId(), var1);
         var2 = (Constant)this.constants.putIfAbsent(var1, var3);
         if (var2 == null) {
            return var3;
         }
      }

      return var2;
   }

   public boolean exists(String var1) {
      checkNotNullAndNotEmpty(var1);
      return this.constants.containsKey(var1);
   }

   public T newInstance(String var1) {
      checkNotNullAndNotEmpty(var1);
      return this.createOrThrow(var1);
   }

   private T createOrThrow(String var1) {
      Constant var2 = (Constant)this.constants.get(var1);
      if (var2 == null) {
         Constant var3 = this.newConstant(this.nextId(), var1);
         var2 = (Constant)this.constants.putIfAbsent(var1, var3);
         if (var2 == null) {
            return var3;
         }
      }

      throw new IllegalArgumentException(String.format("'%s' is already in use", var1));
   }

   private static String checkNotNullAndNotEmpty(String var0) {
      ObjectUtil.checkNotNull(var0, "name");
      if (var0.isEmpty()) {
         throw new IllegalArgumentException("empty name");
      } else {
         return var0;
      }
   }

   protected abstract T newConstant(int var1, String var2);

   /** @deprecated */
   @Deprecated
   public final int nextId() {
      return this.nextId.getAndIncrement();
   }
}
