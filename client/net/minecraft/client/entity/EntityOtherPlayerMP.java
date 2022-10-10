package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class EntityOtherPlayerMP extends AbstractClientPlayer {
   public EntityOtherPlayerMP(World var1, GameProfile var2) {
      super(var1, var2);
      this.field_70138_W = 1.0F;
      this.field_70145_X = true;
      this.field_71082_cx = 0.25F;
   }

   public boolean func_70112_a(double var1) {
      double var3 = this.func_174813_aQ().func_72320_b() * 10.0D;
      if (Double.isNaN(var3)) {
         var3 = 1.0D;
      }

      var3 *= 64.0D * func_184183_bd();
      return var1 < var3 * var3;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      return true;
   }

   public void func_70071_h_() {
      this.field_71082_cx = 0.0F;
      super.func_70071_h_();
      this.field_184618_aE = this.field_70721_aZ;
      double var1 = this.field_70165_t - this.field_70169_q;
      double var3 = this.field_70161_v - this.field_70166_s;
      float var5 = MathHelper.func_76133_a(var1 * var1 + var3 * var3) * 4.0F;
      if (var5 > 1.0F) {
         var5 = 1.0F;
      }

      this.field_70721_aZ += (var5 - this.field_70721_aZ) * 0.4F;
      this.field_184619_aG += this.field_70721_aZ;
   }

   public void func_70636_d() {
      if (this.field_70716_bi > 0) {
         double var1 = this.field_70165_t + (this.field_184623_bh - this.field_70165_t) / (double)this.field_70716_bi;
         double var3 = this.field_70163_u + (this.field_184624_bi - this.field_70163_u) / (double)this.field_70716_bi;
         double var5 = this.field_70161_v + (this.field_184625_bj - this.field_70161_v) / (double)this.field_70716_bi;
         this.field_70177_z = (float)((double)this.field_70177_z + MathHelper.func_76138_g(this.field_184626_bk - (double)this.field_70177_z) / (double)this.field_70716_bi);
         this.field_70125_A = (float)((double)this.field_70125_A + (this.field_70709_bj - (double)this.field_70125_A) / (double)this.field_70716_bi);
         --this.field_70716_bi;
         this.func_70107_b(var1, var3, var5);
         this.func_70101_b(this.field_70177_z, this.field_70125_A);
      }

      if (this.field_208002_br > 0) {
         this.field_70759_as = (float)((double)this.field_70759_as + MathHelper.func_76138_g(this.field_208001_bq - (double)this.field_70759_as) / (double)this.field_208002_br);
         --this.field_208002_br;
      }

      this.field_71107_bF = this.field_71109_bG;
      this.func_82168_bl();
      float var7 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      float var2 = (float)Math.atan(-this.field_70181_x * 0.20000000298023224D) * 15.0F;
      if (var7 > 0.1F) {
         var7 = 0.1F;
      }

      if (!this.field_70122_E || this.func_110143_aJ() <= 0.0F) {
         var7 = 0.0F;
      }

      if (this.field_70122_E || this.func_110143_aJ() <= 0.0F) {
         var2 = 0.0F;
      }

      this.field_71109_bG += (var7 - this.field_71109_bG) * 0.4F;
      this.field_70726_aT += (var2 - this.field_70726_aT) * 0.8F;
      this.field_70170_p.field_72984_F.func_76320_a("push");
      this.func_85033_bc();
      this.field_70170_p.field_72984_F.func_76319_b();
   }

   public void func_145747_a(ITextComponent var1) {
      Minecraft.func_71410_x().field_71456_v.func_146158_b().func_146227_a(var1);
   }
}
