package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityCloudFX extends EntityFX {
   float field_70569_a;

   public EntityCloudFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      float var14 = 2.5F;
      this.field_70159_w *= 0.10000000149011612;
      this.field_70181_x *= 0.10000000149011612;
      this.field_70179_y *= 0.10000000149011612;
      this.field_70159_w += var8;
      this.field_70181_x += var10;
      this.field_70179_y += var12;
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 1.0F - (float)(Math.random() * 0.30000001192092896);
      this.field_70544_f *= 0.75F;
      this.field_70544_f *= var14;
      this.field_70569_a = this.field_70544_f;
      this.field_70547_e = (int)(8.0 / (Math.random() * 0.8 + 0.3));
      this.field_70547_e = (int)((float)this.field_70547_e * var14);
      this.field_70145_X = false;
   }

   @Override
   public void func_70539_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = ((float)this.field_70546_d + var2) / (float)this.field_70547_e * 32.0F;
      if (var8 < 0.0F) {
         var8 = 0.0F;
      }

      if (var8 > 1.0F) {
         var8 = 1.0F;
      }

      this.field_70544_f = this.field_70569_a * var8;
      super.func_70539_a(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_70106_y();
      }

      this.func_70536_a(7 - this.field_70546_d * 8 / this.field_70547_e);
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.9599999785423279;
      this.field_70181_x *= 0.9599999785423279;
      this.field_70179_y *= 0.9599999785423279;
      EntityPlayer var1 = this.field_70170_p.func_72890_a(this, 2.0);
      if (var1 != null && this.field_70163_u > var1.field_70121_D.field_72338_b) {
         this.field_70163_u += (var1.field_70121_D.field_72338_b - this.field_70163_u) * 0.2;
         this.field_70181_x += (var1.field_70181_x - this.field_70181_x) * 0.2;
         this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      }

      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071;
         this.field_70179_y *= 0.699999988079071;
      }
   }
}
