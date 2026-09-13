package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkOverlayFX extends EntityFX {
   protected EntityFireworkOverlayFX(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
      this.field_70547_e = 4;
   }

   @Override
   public void func_70539_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = 0.25F;
      float var9 = var8 + 0.25F;
      float var10 = 0.125F;
      float var11 = var10 + 0.25F;
      float var12 = 7.1F * MathHelper.func_76126_a(((float)this.field_70546_d + var2 - 1.0F) * 0.25F * 3.1415927F);
      this.field_82339_as = 0.6F - ((float)this.field_70546_d + var2 - 1.0F) * 0.25F * 0.5F;
      float var13 = (float)(this.field_70169_q + (this.field_70165_t - this.field_70169_q) * (double)var2 - field_70556_an);
      float var14 = (float)(this.field_70167_r + (this.field_70163_u - this.field_70167_r) * (double)var2 - field_70554_ao);
      float var15 = (float)(this.field_70166_s + (this.field_70161_v - this.field_70166_s) * (double)var2 - field_70555_ap);
      var1.func_78369_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as);
      var1.func_78374_a(
         (double)(var13 - var3 * var12 - var6 * var12),
         (double)(var14 - var4 * var12),
         (double)(var15 - var5 * var12 - var7 * var12),
         (double)var9,
         (double)var11
      );
      var1.func_78374_a(
         (double)(var13 - var3 * var12 + var6 * var12),
         (double)(var14 + var4 * var12),
         (double)(var15 - var5 * var12 + var7 * var12),
         (double)var9,
         (double)var10
      );
      var1.func_78374_a(
         (double)(var13 + var3 * var12 + var6 * var12),
         (double)(var14 + var4 * var12),
         (double)(var15 + var5 * var12 + var7 * var12),
         (double)var8,
         (double)var10
      );
      var1.func_78374_a(
         (double)(var13 + var3 * var12 - var6 * var12),
         (double)(var14 - var4 * var12),
         (double)(var15 + var5 * var12 - var7 * var12),
         (double)var8,
         (double)var11
      );
   }
}
