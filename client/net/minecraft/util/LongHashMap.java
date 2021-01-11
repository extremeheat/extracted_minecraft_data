package net.minecraft.util;

public class LongHashMap<V> {
   private transient LongHashMap.Entry<V>[] field_76169_a = new LongHashMap.Entry[4096];
   private transient int field_76167_b;
   private int field_180201_c;
   private int field_76168_c = 3072;
   private final float field_76165_d = 0.75F;
   private transient volatile int field_76166_e;

   public LongHashMap() {
      super();
      this.field_180201_c = this.field_76169_a.length - 1;
   }

   private static int func_76155_g(long var0) {
      return func_76157_a((int)(var0 ^ var0 >>> 32));
   }

   private static int func_76157_a(int var0) {
      var0 ^= var0 >>> 20 ^ var0 >>> 12;
      return var0 ^ var0 >>> 7 ^ var0 >>> 4;
   }

   private static int func_76158_a(int var0, int var1) {
      return var0 & var1;
   }

   public int func_76162_a() {
      return this.field_76167_b;
   }

   public V func_76164_a(long var1) {
      int var3 = func_76155_g(var1);

      for(LongHashMap.Entry var4 = this.field_76169_a[func_76158_a(var3, this.field_180201_c)]; var4 != null; var4 = var4.field_76149_c) {
         if (var4.field_76150_a == var1) {
            return var4.field_76148_b;
         }
      }

      return null;
   }

   public boolean func_76161_b(long var1) {
      return this.func_76160_c(var1) != null;
   }

   final LongHashMap.Entry<V> func_76160_c(long var1) {
      int var3 = func_76155_g(var1);

      for(LongHashMap.Entry var4 = this.field_76169_a[func_76158_a(var3, this.field_180201_c)]; var4 != null; var4 = var4.field_76149_c) {
         if (var4.field_76150_a == var1) {
            return var4;
         }
      }

      return null;
   }

   public void func_76163_a(long var1, V var3) {
      int var4 = func_76155_g(var1);
      int var5 = func_76158_a(var4, this.field_180201_c);

      for(LongHashMap.Entry var6 = this.field_76169_a[var5]; var6 != null; var6 = var6.field_76149_c) {
         if (var6.field_76150_a == var1) {
            var6.field_76148_b = var3;
            return;
         }
      }

      ++this.field_76166_e;
      this.func_76156_a(var4, var1, var3, var5);
   }

   private void func_76153_b(int var1) {
      LongHashMap.Entry[] var2 = this.field_76169_a;
      int var3 = var2.length;
      if (var3 == 1073741824) {
         this.field_76168_c = 2147483647;
      } else {
         LongHashMap.Entry[] var4 = new LongHashMap.Entry[var1];
         this.func_76154_a(var4);
         this.field_76169_a = var4;
         this.field_180201_c = this.field_76169_a.length - 1;
         this.field_76168_c = (int)((float)var1 * this.field_76165_d);
      }
   }

   private void func_76154_a(LongHashMap.Entry<V>[] var1) {
      LongHashMap.Entry[] var2 = this.field_76169_a;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var2.length; ++var4) {
         LongHashMap.Entry var5 = var2[var4];
         if (var5 != null) {
            var2[var4] = null;

            LongHashMap.Entry var6;
            do {
               var6 = var5.field_76149_c;
               int var7 = func_76158_a(var5.field_76147_d, var3 - 1);
               var5.field_76149_c = var1[var7];
               var1[var7] = var5;
               var5 = var6;
            } while(var6 != null);
         }
      }

   }

   public V func_76159_d(long var1) {
      LongHashMap.Entry var3 = this.func_76152_e(var1);
      return var3 == null ? null : var3.field_76148_b;
   }

   final LongHashMap.Entry<V> func_76152_e(long var1) {
      int var3 = func_76155_g(var1);
      int var4 = func_76158_a(var3, this.field_180201_c);
      LongHashMap.Entry var5 = this.field_76169_a[var4];

      LongHashMap.Entry var6;
      LongHashMap.Entry var7;
      for(var6 = var5; var6 != null; var6 = var7) {
         var7 = var6.field_76149_c;
         if (var6.field_76150_a == var1) {
            ++this.field_76166_e;
            --this.field_76167_b;
            if (var5 == var6) {
               this.field_76169_a[var4] = var7;
            } else {
               var5.field_76149_c = var7;
            }

            return var6;
         }

         var5 = var6;
      }

      return var6;
   }

   private void func_76156_a(int var1, long var2, V var4, int var5) {
      LongHashMap.Entry var6 = this.field_76169_a[var5];
      this.field_76169_a[var5] = new LongHashMap.Entry(var1, var2, var4, var6);
      if (this.field_76167_b++ >= this.field_76168_c) {
         this.func_76153_b(2 * this.field_76169_a.length);
      }

   }

   static class Entry<V> {
      final long field_76150_a;
      V field_76148_b;
      LongHashMap.Entry<V> field_76149_c;
      final int field_76147_d;

      Entry(int var1, long var2, V var4, LongHashMap.Entry<V> var5) {
         super();
         this.field_76148_b = var4;
         this.field_76149_c = var5;
         this.field_76150_a = var2;
         this.field_76147_d = var1;
      }

      public final long func_76146_a() {
         return this.field_76150_a;
      }

      public final V func_76145_b() {
         return this.field_76148_b;
      }

      public final boolean equals(Object var1) {
         if (!(var1 instanceof LongHashMap.Entry)) {
            return false;
         } else {
            LongHashMap.Entry var2 = (LongHashMap.Entry)var1;
            Long var3 = this.func_76146_a();
            Long var4 = var2.func_76146_a();
            if (var3 == var4 || var3 != null && var3.equals(var4)) {
               Object var5 = this.func_76145_b();
               Object var6 = var2.func_76145_b();
               if (var5 == var6 || var5 != null && var5.equals(var6)) {
                  return true;
               }
            }

            return false;
         }
      }

      public final int hashCode() {
         return LongHashMap.func_76155_g(this.field_76150_a);
      }

      public final String toString() {
         return this.func_76146_a() + "=" + this.func_76145_b();
      }
   }
}
