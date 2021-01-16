package io.netty.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultAttributeMap implements AttributeMap {
   private static final AtomicReferenceFieldUpdater<DefaultAttributeMap, AtomicReferenceArray> updater = AtomicReferenceFieldUpdater.newUpdater(DefaultAttributeMap.class, AtomicReferenceArray.class, "attributes");
   private static final int BUCKET_SIZE = 4;
   private static final int MASK = 3;
   private volatile AtomicReferenceArray<DefaultAttributeMap.DefaultAttribute<?>> attributes;

   public DefaultAttributeMap() {
      super();
   }

   public <T> Attribute<T> attr(AttributeKey<T> var1) {
      if (var1 == null) {
         throw new NullPointerException("key");
      } else {
         AtomicReferenceArray var2 = this.attributes;
         if (var2 == null) {
            var2 = new AtomicReferenceArray(4);
            if (!updater.compareAndSet(this, (Object)null, var2)) {
               var2 = this.attributes;
            }
         }

         int var3 = index(var1);
         DefaultAttributeMap.DefaultAttribute var4 = (DefaultAttributeMap.DefaultAttribute)var2.get(var3);
         if (var4 == null) {
            var4 = new DefaultAttributeMap.DefaultAttribute();
            DefaultAttributeMap.DefaultAttribute var5 = new DefaultAttributeMap.DefaultAttribute(var4, var1);
            var4.next = var5;
            var5.prev = var4;
            if (var2.compareAndSet(var3, (Object)null, var4)) {
               return var5;
            }

            var4 = (DefaultAttributeMap.DefaultAttribute)var2.get(var3);
         }

         synchronized(var4) {
            DefaultAttributeMap.DefaultAttribute var6 = var4;

            while(true) {
               DefaultAttributeMap.DefaultAttribute var7 = var6.next;
               if (var7 == null) {
                  DefaultAttributeMap.DefaultAttribute var8 = new DefaultAttributeMap.DefaultAttribute(var4, var1);
                  var6.next = var8;
                  var8.prev = var6;
                  return var8;
               }

               if (var7.key == var1 && !var7.removed) {
                  return var7;
               }

               var6 = var7;
            }
         }
      }
   }

   public <T> boolean hasAttr(AttributeKey<T> var1) {
      if (var1 == null) {
         throw new NullPointerException("key");
      } else {
         AtomicReferenceArray var2 = this.attributes;
         if (var2 == null) {
            return false;
         } else {
            int var3 = index(var1);
            DefaultAttributeMap.DefaultAttribute var4 = (DefaultAttributeMap.DefaultAttribute)var2.get(var3);
            if (var4 == null) {
               return false;
            } else {
               synchronized(var4) {
                  for(DefaultAttributeMap.DefaultAttribute var6 = var4.next; var6 != null; var6 = var6.next) {
                     if (var6.key == var1 && !var6.removed) {
                        return true;
                     }
                  }

                  return false;
               }
            }
         }
      }
   }

   private static int index(AttributeKey<?> var0) {
      return var0.id() & 3;
   }

   private static final class DefaultAttribute<T> extends AtomicReference<T> implements Attribute<T> {
      private static final long serialVersionUID = -2661411462200283011L;
      private final DefaultAttributeMap.DefaultAttribute<?> head;
      private final AttributeKey<T> key;
      private DefaultAttributeMap.DefaultAttribute<?> prev;
      private DefaultAttributeMap.DefaultAttribute<?> next;
      private volatile boolean removed;

      DefaultAttribute(DefaultAttributeMap.DefaultAttribute<?> var1, AttributeKey<T> var2) {
         super();
         this.head = var1;
         this.key = var2;
      }

      DefaultAttribute() {
         super();
         this.head = this;
         this.key = null;
      }

      public AttributeKey<T> key() {
         return this.key;
      }

      public T setIfAbsent(T var1) {
         while(true) {
            if (!this.compareAndSet((Object)null, var1)) {
               Object var2 = this.get();
               if (var2 == null) {
                  continue;
               }

               return var2;
            }

            return null;
         }
      }

      public T getAndRemove() {
         this.removed = true;
         Object var1 = this.getAndSet((Object)null);
         this.remove0();
         return var1;
      }

      public void remove() {
         this.removed = true;
         this.set((Object)null);
         this.remove0();
      }

      private void remove0() {
         synchronized(this.head) {
            if (this.prev != null) {
               this.prev.next = this.next;
               if (this.next != null) {
                  this.next.prev = this.prev;
               }

               this.prev = null;
               this.next = null;
            }
         }
      }
   }
}
