package io.netty.util;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractConstant<T extends AbstractConstant<T>> implements Constant<T> {
   private static final AtomicLong uniqueIdGenerator = new AtomicLong();
   private final int id;
   private final String name;
   private final long uniquifier;

   protected AbstractConstant(int var1, String var2) {
      super();
      this.id = var1;
      this.name = var2;
      this.uniquifier = uniqueIdGenerator.getAndIncrement();
   }

   public final String name() {
      return this.name;
   }

   public final int id() {
      return this.id;
   }

   public final String toString() {
      return this.name();
   }

   public final int hashCode() {
      return super.hashCode();
   }

   public final boolean equals(Object var1) {
      return super.equals(var1);
   }

   public final int compareTo(T var1) {
      if (this == var1) {
         return 0;
      } else {
         int var3 = this.hashCode() - var1.hashCode();
         if (var3 != 0) {
            return var3;
         } else if (this.uniquifier < var1.uniquifier) {
            return -1;
         } else if (this.uniquifier > var1.uniquifier) {
            return 1;
         } else {
            throw new Error("failed to compare two different constants");
         }
      }
   }
}
