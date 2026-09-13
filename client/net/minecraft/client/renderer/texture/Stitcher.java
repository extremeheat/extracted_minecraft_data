package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.util.MathHelper;

public class Stitcher {
   private final int field_147971_a;
   private final Set field_94319_a = new HashSet(256);
   private final List field_94317_b = new ArrayList(256);
   private int field_94318_c;
   private int field_94315_d;
   private final int field_94316_e;
   private final int field_94313_f;
   private final boolean field_94314_g;
   private final int field_94323_h;

   public Stitcher(int var1, int var2, boolean var3, int var4, int var5) {
      super();
      this.field_147971_a = var5;
      this.field_94316_e = var1;
      this.field_94313_f = var2;
      this.field_94314_g = var3;
      this.field_94323_h = var4;
   }

   public int func_110935_a() {
      return this.field_94318_c;
   }

   public int func_110936_b() {
      return this.field_94315_d;
   }

   public void func_110934_a(TextureAtlasSprite var1) {
      Stitcher$Holder var2 = new Stitcher$Holder(var1, this.field_147971_a);
      if (this.field_94323_h > 0) {
         var2.func_94196_a(this.field_94323_h);
      }

      this.field_94319_a.add(var2);
   }

   public void func_94305_f() {
      Stitcher$Holder[] var1 = this.field_94319_a.toArray(new Stitcher$Holder[this.field_94319_a.size()]);
      Arrays.sort((Object[])var1);

      for(Stitcher$Holder var5 : var1) {
         if (!this.func_94310_b(var5)) {
            String var6 = String.format(
               "Unable to fit: %s - size: %dx%d - Maybe try a lowerresolution resourcepack?",
               var5.func_98150_a().func_94215_i(),
               var5.func_98150_a().func_94211_a(),
               var5.func_98150_a().func_94216_b()
            );
            throw new StitcherException(var5, var6);
         }
      }

      if (this.field_94314_g) {
         this.field_94318_c = MathHelper.func_151236_b(this.field_94318_c);
         this.field_94315_d = MathHelper.func_151236_b(this.field_94315_d);
      }
   }

   public List func_94309_g() {
      ArrayList var1 = Lists.newArrayList();

      for(Stitcher$Slot var3 : this.field_94317_b) {
         var3.func_94184_a(var1);
      }

      ArrayList var7 = Lists.newArrayList();

      for(Stitcher$Slot var4 : var1) {
         Stitcher$Holder var5 = var4.func_94183_a();
         TextureAtlasSprite var6 = var5.func_98150_a();
         var6.func_110971_a(this.field_94318_c, this.field_94315_d, var4.func_94186_b(), var4.func_94185_c(), var5.func_94195_e());
         var7.add(var6);
      }

      return var7;
   }

   private static int func_147969_b(int var0, int var1) {
      return (var0 >> var1) + ((var0 & (1 << var1) - 1) == 0 ? 0 : 1) << var1;
   }

   private boolean func_94310_b(Stitcher$Holder var1) {
      for(int var2 = 0; var2 < this.field_94317_b.size(); ++var2) {
         if (((Stitcher$Slot)this.field_94317_b.get(var2)).func_94182_a(var1)) {
            return true;
         }

         var1.func_94194_d();
         if (((Stitcher$Slot)this.field_94317_b.get(var2)).func_94182_a(var1)) {
            return true;
         }

         var1.func_94194_d();
      }

      return this.func_94311_c(var1);
   }

   private boolean func_94311_c(Stitcher$Holder var1) {
      int var2 = Math.min(var1.func_94197_a(), var1.func_94199_b());
      boolean var3 = this.field_94318_c == 0 && this.field_94315_d == 0;
      boolean var4;
      if (this.field_94314_g) {
         int var5 = MathHelper.func_151236_b(this.field_94318_c);
         int var6 = MathHelper.func_151236_b(this.field_94315_d);
         int var7 = MathHelper.func_151236_b(this.field_94318_c + var2);
         int var8 = MathHelper.func_151236_b(this.field_94315_d + var2);
         boolean var9 = var7 <= this.field_94316_e;
         boolean var10 = var8 <= this.field_94313_f;
         if (!var9 && !var10) {
            return false;
         }

         boolean var11 = var5 != var7;
         boolean var12 = var6 != var8;
         if (var11 ^ var12) {
            var4 = !var11;
         } else {
            var4 = var9 && var5 <= var6;
         }
      } else {
         boolean var13 = this.field_94318_c + var2 <= this.field_94316_e;
         boolean var15 = this.field_94315_d + var2 <= this.field_94313_f;
         if (!var13 && !var15) {
            return false;
         }

         var4 = var13 && (var3 || this.field_94318_c <= this.field_94315_d);
      }

      int var14 = Math.max(var1.func_94197_a(), var1.func_94199_b());
      if (MathHelper.func_151236_b((var4 ? this.field_94315_d : this.field_94318_c) + var14) > (var4 ? this.field_94313_f : this.field_94316_e)) {
         return false;
      } else {
         Stitcher$Slot var16;
         if (var4) {
            if (var1.func_94197_a() > var1.func_94199_b()) {
               var1.func_94194_d();
            }

            if (this.field_94315_d == 0) {
               this.field_94315_d = var1.func_94199_b();
            }

            var16 = new Stitcher$Slot(this.field_94318_c, 0, var1.func_94197_a(), this.field_94315_d);
            this.field_94318_c += var1.func_94197_a();
         } else {
            var16 = new Stitcher$Slot(0, this.field_94315_d, this.field_94318_c, var1.func_94199_b());
            this.field_94315_d += var1.func_94199_b();
         }

         var16.func_94182_a(var1);
         this.field_94317_b.add(var16);
         return true;
      }
   }
}
