package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityOtherPlayerMP extends AbstractClientPlayer {
   private boolean field_71186_a;
   private int field_71184_b;
   private double field_71185_c;
   private double field_71182_d;
   private double field_71183_e;
   private double field_71180_f;
   private double field_71181_g;

   public EntityOtherPlayerMP(World var1, GameProfile var2) {
      super(var1, var2);
      this.field_70138_W = 0.0F;
      this.field_70145_X = true;
      this.field_71082_cx = 0.25F;
      this.field_70155_l = 10.0D;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      return true;
   }

   public void func_180426_a(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.field_71185_c = var1;
      this.field_71182_d = var3;
      this.field_71183_e = var5;
      this.field_71180_f = (double)var7;
      this.field_71181_g = (double)var8;
      this.field_71184_b = var9;
   }

   public void func_70071_h_() {
      this.field_71082_cx = 0.0F;
      super.func_70071_h_();
      this.field_70722_aY = this.field_70721_aZ;
      double var1 = this.field_70165_t - this.field_70169_q;
      double var3 = this.field_70161_v - this.field_70166_s;
      float var5 = MathHelper.func_76133_a(var1 * var1 + var3 * var3) * 4.0F;
      if (var5 > 1.0F) {
         var5 = 1.0F;
      }

      this.field_70721_aZ += (var5 - this.field_70721_aZ) * 0.4F;
      this.field_70754_ba += this.field_70721_aZ;
      if (!this.field_71186_a && this.func_70113_ah() && this.field_71071_by.field_70462_a[this.field_71071_by.field_70461_c] != null) {
         ItemStack var6 = this.field_71071_by.field_70462_a[this.field_71071_by.field_70461_c];
         this.func_71008_a(this.field_71071_by.field_70462_a[this.field_71071_by.field_70461_c], var6.func_77973_b().func_77626_a(var6));
         this.field_71186_a = true;
      } else if (this.field_71186_a && !this.func_70113_ah()) {
         this.func_71041_bz();
         this.field_71186_a = false;
      }

   }

   public void func_70636_d() {
      if (this.field_71184_b > 0) {
         double var1 = this.field_70165_t + (this.field_71185_c - this.field_70165_t) / (double)this.field_71184_b;
         double var3 = this.field_70163_u + (this.field_71182_d - this.field_70163_u) / (double)this.field_71184_b;
         double var5 = this.field_70161_v + (this.field_71183_e - this.field_70161_v) / (double)this.field_71184_b;

         double var7;
         for(var7 = this.field_71180_f - (double)this.field_70177_z; var7 < -180.0D; var7 += 360.0D) {
         }

         while(var7 >= 180.0D) {
            var7 -= 360.0D;
         }

         this.field_70177_z = (float)((double)this.field_70177_z + var7 / (double)this.field_71184_b);
         this.field_70125_A = (float)((double)this.field_70125_A + (this.field_71181_g - (double)this.field_70125_A) / (double)this.field_71184_b);
         --this.field_71184_b;
         this.func_70107_b(var1, var3, var5);
         this.func_70101_b(this.field_70177_z, this.field_70125_A);
      }

      this.field_71107_bF = this.field_71109_bG;
      this.func_82168_bl();
      float var9 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      float var2 = (float)Math.atan(-this.field_70181_x * 0.20000000298023224D) * 15.0F;
      if (var9 > 0.1F) {
         var9 = 0.1F;
      }

      if (!this.field_70122_E || this.func_110143_aJ() <= 0.0F) {
         var9 = 0.0F;
      }

      if (this.field_70122_E || this.func_110143_aJ() <= 0.0F) {
         var2 = 0.0F;
      }

      this.field_71109_bG += (var9 - this.field_71109_bG) * 0.4F;
      this.field_70726_aT += (var2 - this.field_70726_aT) * 0.8F;
   }

   public void func_70062_b(int var1, ItemStack var2) {
      if (var1 == 0) {
         this.field_71071_by.field_70462_a[this.field_71071_by.field_70461_c] = var2;
      } else {
         this.field_71071_by.field_70460_b[var1 - 1] = var2;
      }

   }

   public void func_145747_a(IChatComponent var1) {
      Minecraft.func_71410_x().field_71456_v.func_146158_b().func_146227_a(var1);
   }

   public boolean func_70003_b(int var1, String var2) {
      return false;
   }

   public BlockPos func_180425_c() {
      return new BlockPos(this.field_70165_t + 0.5D, this.field_70163_u + 0.5D, this.field_70161_v + 0.5D);
   }
}
