package net.minecraft.util;

import java.util.HashSet;
import java.util.Set;

public class IntHashMap {
   private transient IntHashMap$Entry[] field_76055_a;
   private transient int field_76053_b;
   private int field_76054_c;
   private final float field_76051_d;
   private transient volatile int field_76052_e;
   private Set field_76050_f = new HashSet();

   public IntHashMap() {
      super();
      this.field_76051_d = 0.75F;
      this.field_76054_c = 12;
      this.field_76055_a = new IntHashMap$Entry[16];
   }

   private static int func_76044_g(int var0) {
      var0 ^= var0 >>> 20 ^ var0 >>> 12;
      return var0 ^ var0 >>> 7 ^ var0 >>> 4;
   }

   private static int func_76043_a(int var0, int var1) {
      return var0 & var1 - 1;
   }

   public Object func_76041_a(int var1) {
      int var2 = func_76044_g(var1);

      for(IntHashMap$Entry var3 = this.field_76055_a[func_76043_a(var2, this.field_76055_a.length)]; var3 != null; var3 = var3.field_76034_c) {
         if (var3.field_76035_a == var1) {
            return var3.field_76033_b;
         }
      }

      return null;
   }

   public boolean func_76037_b(int var1) {
      return this.func_76045_c(var1) != null;
   }

   final IntHashMap$Entry func_76045_c(int var1) {
      int var2 = func_76044_g(var1);

      for(IntHashMap$Entry var3 = this.field_76055_a[func_76043_a(var2, this.field_76055_a.length)]; var3 != null; var3 = var3.field_76034_c) {
         if (var3.field_76035_a == var1) {
            return var3;
         }
      }

      return null;
   }

   public void func_76038_a(int var1, Object var2) {
      this.field_76050_f.add(var1);
      int var3 = func_76044_g(var1);
      int var4 = func_76043_a(var3, this.field_76055_a.length);

      for(IntHashMap$Entry var5 = this.field_76055_a[var4]; var5 != null; var5 = var5.field_76034_c) {
         if (var5.field_76035_a == var1) {
            var5.field_76033_b = var2;
            return;
         }
      }

      ++this.field_76052_e;
      this.func_76040_a(var3, var1, var2, var4);
   }

   private void func_76047_h(int var1) {
      IntHashMap$Entry[] var2 = this.field_76055_a;
      int var3 = var2.length;
      if (var3 == 1073741824) {
         this.field_76054_c = 2147483647;
      } else {
         IntHashMap$Entry[] var4 = new IntHashMap$Entry[var1];
         this.func_76048_a(var4);
         this.field_76055_a = var4;
         this.field_76054_c = (int)((float)var1 * this.field_76051_d);
      }
   }

   private void func_76048_a(IntHashMap$Entry[] var1) {
      IntHashMap$Entry[] var2 = this.field_76055_a;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var2.length; ++var4) {
         IntHashMap$Entry var5 = var2[var4];
         if (var5 != null) {
            var2[var4] = null;

            while(true) {
               IntHashMap$Entry var6 = var5.field_76034_c;
               int var7 = func_76043_a(var5.field_76032_d, var3);
               var5.field_76034_c = var1[var7];
               var1[var7] = var5;
               var5 = var6;
               if (var6 == null) {
                  break;
               }
            }
         }
      }
   }

   public Object func_76049_d(int var1) {
      this.field_76050_f.remove(var1);
      IntHashMap$Entry var2 = this.func_76036_e(var1);
      return var2 == null ? null : var2.field_76033_b;
   }

   final IntHashMap$Entry func_76036_e(int var1) {
      int var2 = func_76044_g(var1);
      int var3 = func_76043_a(var2, this.field_76055_a.length);
      IntHashMap$Entry var4 = this.field_76055_a[var3];

      IntHashMap$Entry var5;
      IntHashMap$Entry var6;
      for(var5 = var4; var5 != null; var5 = var6) {
         var6 = var5.field_76034_c;
         if (var5.field_76035_a == var1) {
            ++this.field_76052_e;
            --this.field_76053_b;
            if (var4 == var5) {
               this.field_76055_a[var3] = var6;
            } else {
               var4.field_76034_c = var6;
            }

            return var5;
         }

         var4 = var5;
      }

      return var5;
   }

   public void func_76046_c() {
      ++this.field_76052_e;
      IntHashMap$Entry[] var1 = this.field_76055_a;

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = null;
      }

      this.field_76053_b = 0;
   }

   private void func_76040_a(int var1, int var2, Object var3, int var4) {
      IntHashMap$Entry var5 = this.field_76055_a[var4];
      this.field_76055_a[var4] = new IntHashMap$Entry(var1, var2, var3, var5);
      if (this.field_76053_b++ >= this.field_76054_c) {
         this.func_76047_h(2 * this.field_76055_a.length);
      }
   }
}
