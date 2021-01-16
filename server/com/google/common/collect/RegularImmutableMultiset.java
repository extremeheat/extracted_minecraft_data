package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
class RegularImmutableMultiset<E> extends ImmutableMultiset<E> {
   static final RegularImmutableMultiset<Object> EMPTY = new RegularImmutableMultiset(ImmutableList.of());
   private final transient Multisets.ImmutableEntry<E>[] entries;
   private final transient Multisets.ImmutableEntry<E>[] hashTable;
   private final transient int size;
   private final transient int hashCode;
   @LazyInit
   private transient ImmutableSet<E> elementSet;

   RegularImmutableMultiset(Collection<? extends Multiset.Entry<? extends E>> var1) {
      super();
      int var2 = var1.size();
      Multisets.ImmutableEntry[] var3 = new Multisets.ImmutableEntry[var2];
      if (var2 == 0) {
         this.entries = var3;
         this.hashTable = null;
         this.size = 0;
         this.hashCode = 0;
         this.elementSet = ImmutableSet.of();
      } else {
         int var4 = Hashing.closedTableSize(var2, 1.0D);
         int var5 = var4 - 1;
         Multisets.ImmutableEntry[] var6 = new Multisets.ImmutableEntry[var4];
         int var7 = 0;
         int var8 = 0;
         long var9 = 0L;

         int var14;
         for(Iterator var11 = var1.iterator(); var11.hasNext(); var9 += (long)var14) {
            Multiset.Entry var12 = (Multiset.Entry)var11.next();
            Object var13 = Preconditions.checkNotNull(var12.getElement());
            var14 = var12.getCount();
            int var15 = var13.hashCode();
            int var16 = Hashing.smear(var15) & var5;
            Multisets.ImmutableEntry var17 = var6[var16];
            Object var18;
            if (var17 != null) {
               var18 = new RegularImmutableMultiset.NonTerminalEntry(var13, var14, var17);
            } else {
               boolean var19 = var12 instanceof Multisets.ImmutableEntry && !(var12 instanceof RegularImmutableMultiset.NonTerminalEntry);
               var18 = var19 ? (Multisets.ImmutableEntry)var12 : new Multisets.ImmutableEntry(var13, var14);
            }

            var8 += var15 ^ var14;
            var3[var7++] = (Multisets.ImmutableEntry)var18;
            var6[var16] = (Multisets.ImmutableEntry)var18;
         }

         this.entries = var3;
         this.hashTable = var6;
         this.size = Ints.saturatedCast(var9);
         this.hashCode = var8;
      }

   }

   boolean isPartialView() {
      return false;
   }

   public int count(@Nullable Object var1) {
      Multisets.ImmutableEntry[] var2 = this.hashTable;
      if (var1 != null && var2 != null) {
         int var3 = Hashing.smearedHash(var1);
         int var4 = var2.length - 1;

         for(Multisets.ImmutableEntry var5 = var2[var3 & var4]; var5 != null; var5 = var5.nextInBucket()) {
            if (Objects.equal(var1, var5.getElement())) {
               return var5.getCount();
            }
         }

         return 0;
      } else {
         return 0;
      }
   }

   public int size() {
      return this.size;
   }

   public ImmutableSet<E> elementSet() {
      ImmutableSet var1 = this.elementSet;
      return var1 == null ? (this.elementSet = new RegularImmutableMultiset.ElementSet()) : var1;
   }

   Multiset.Entry<E> getEntry(int var1) {
      return this.entries[var1];
   }

   public int hashCode() {
      return this.hashCode;
   }

   private final class ElementSet extends ImmutableSet.Indexed<E> {
      private ElementSet() {
         super();
      }

      E get(int var1) {
         return RegularImmutableMultiset.this.entries[var1].getElement();
      }

      public boolean contains(@Nullable Object var1) {
         return RegularImmutableMultiset.this.contains(var1);
      }

      boolean isPartialView() {
         return true;
      }

      public int size() {
         return RegularImmutableMultiset.this.entries.length;
      }

      // $FF: synthetic method
      ElementSet(Object var2) {
         this();
      }
   }

   private static final class NonTerminalEntry<E> extends Multisets.ImmutableEntry<E> {
      private final Multisets.ImmutableEntry<E> nextInBucket;

      NonTerminalEntry(E var1, int var2, Multisets.ImmutableEntry<E> var3) {
         super(var1, var2);
         this.nextInBucket = var3;
      }

      public Multisets.ImmutableEntry<E> nextInBucket() {
         return this.nextInBucket;
      }
   }
}
