package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;

public class IntIdentityHashBiMap<K> implements IObjectIntIterable<K> {
   private static final Object field_186817_a = null;
   private K[] field_186818_b;
   private int[] field_186819_c;
   private K[] field_186820_d;
   private int field_186821_e;
   private int field_186822_f;

   public IntIdentityHashBiMap(int var1) {
      super();
      var1 = (int)((float)var1 / 0.8F);
      this.field_186818_b = (Object[])(new Object[var1]);
      this.field_186819_c = new int[var1];
      this.field_186820_d = (Object[])(new Object[var1]);
   }

   public int func_186815_a(@Nullable K var1) {
      return this.func_186805_c(this.func_186816_b(var1, this.func_186811_d(var1)));
   }

   @Nullable
   public K func_186813_a(int var1) {
      return var1 >= 0 && var1 < this.field_186820_d.length ? this.field_186820_d[var1] : null;
   }

   private int func_186805_c(int var1) {
      return var1 == -1 ? -1 : this.field_186819_c[var1];
   }

   public int func_186808_c(K var1) {
      int var2 = this.func_186809_c();
      this.func_186814_a(var1, var2);
      return var2;
   }

   private int func_186809_c() {
      while(this.field_186821_e < this.field_186820_d.length && this.field_186820_d[this.field_186821_e] != null) {
         ++this.field_186821_e;
      }

      return this.field_186821_e;
   }

   private void func_186807_d(int var1) {
      Object[] var2 = this.field_186818_b;
      int[] var3 = this.field_186819_c;
      this.field_186818_b = (Object[])(new Object[var1]);
      this.field_186819_c = new int[var1];
      this.field_186820_d = (Object[])(new Object[var1]);
      this.field_186821_e = 0;
      this.field_186822_f = 0;

      for(int var4 = 0; var4 < var2.length; ++var4) {
         if (var2[var4] != null) {
            this.func_186814_a(var2[var4], var3[var4]);
         }
      }

   }

   public void func_186814_a(K var1, int var2) {
      int var3 = Math.max(var2, this.field_186822_f + 1);
      int var4;
      if ((float)var3 >= (float)this.field_186818_b.length * 0.8F) {
         for(var4 = this.field_186818_b.length << 1; var4 < var2; var4 <<= 1) {
         }

         this.func_186807_d(var4);
      }

      var4 = this.func_186806_e(this.func_186811_d(var1));
      this.field_186818_b[var4] = var1;
      this.field_186819_c[var4] = var2;
      this.field_186820_d[var2] = var1;
      ++this.field_186822_f;
      if (var2 == this.field_186821_e) {
         ++this.field_186821_e;
      }

   }

   private int func_186811_d(@Nullable K var1) {
      return (MathHelper.func_188208_f(System.identityHashCode(var1)) & 2147483647) % this.field_186818_b.length;
   }

   private int func_186816_b(@Nullable K var1, int var2) {
      int var3;
      for(var3 = var2; var3 < this.field_186818_b.length; ++var3) {
         if (this.field_186818_b[var3] == var1) {
            return var3;
         }

         if (this.field_186818_b[var3] == field_186817_a) {
            return -1;
         }
      }

      for(var3 = 0; var3 < var2; ++var3) {
         if (this.field_186818_b[var3] == var1) {
            return var3;
         }

         if (this.field_186818_b[var3] == field_186817_a) {
            return -1;
         }
      }

      return -1;
   }

   private int func_186806_e(int var1) {
      int var2;
      for(var2 = var1; var2 < this.field_186818_b.length; ++var2) {
         if (this.field_186818_b[var2] == field_186817_a) {
            return var2;
         }
      }

      for(var2 = 0; var2 < var1; ++var2) {
         if (this.field_186818_b[var2] == field_186817_a) {
            return var2;
         }
      }

      throw new RuntimeException("Overflowed :(");
   }

   public Iterator<K> iterator() {
      return Iterators.filter(Iterators.forArray(this.field_186820_d), Predicates.notNull());
   }

   public void func_186812_a() {
      Arrays.fill(this.field_186818_b, (Object)null);
      Arrays.fill(this.field_186820_d, (Object)null);
      this.field_186821_e = 0;
      this.field_186822_f = 0;
   }

   public int func_186810_b() {
      return this.field_186822_f;
   }
}
