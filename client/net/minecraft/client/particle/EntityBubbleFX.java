package net.minecraft.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBubbleFX extends EntityFX {
   public EntityBubbleFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.field_70552_h = 1.0F;
      this.field_70553_i = 1.0F;
      this.field_70551_j = 1.0F;
      this.func_70536_a(32);
      this.func_70105_a(0.02F, 0.02F);
      this.field_70544_f *= this.field_70146_Z.nextFloat() * 0.6F + 0.2F;
      this.field_70159_w = var8 * 0.20000000298023224 + (double)((float)(Math.random() * 2.0 - 1.0) * 0.02F);
      this.field_70181_x = var10 * 0.20000000298023224 + (double)((float)(Math.random() * 2.0 - 1.0) * 0.02F);
      this.field_70179_y = var12 * 0.20000000298023224 + (double)((float)(Math.random() * 2.0 - 1.0) * 0.02F);
      this.field_70547_e = (int)(8.0 / (Math.random() * 0.8 + 0.2));
   }

   @Override
   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      this.field_70181_x += 0.002;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.8500000238418579;
      this.field_70181_x *= 0.8500000238418579;
      this.field_70179_y *= 0.8500000238418579;
      if (this.field_70170_p
            .func_147439_a(
               MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)
            )
            .func_149688_o()
         != Material.field_151586_h) {
         this.func_70106_y();
      }

      if (this.field_70547_e-- <= 0) {
         this.func_70106_y();
      }
   }
}
