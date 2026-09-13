package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntityBreakingFX extends EntityFX {
   public EntityBreakingFX(World var1, double var2, double var4, double var6, Item var8) {
      this(var1, var2, var4, var6, var8, 0);
   }

   public EntityBreakingFX(World var1, double var2, double var4, double var6, Item var8, int var9) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.func_110125_a(var8.func_77617_a(var9));
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 1.0F;
      this.field_70545_g = Blocks.field_150433_aE.field_149763_I;
      this.field_70544_f /= 2.0F;
   }

   public EntityBreakingFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, Item var14, int var15) {
      this(var1, var2, var4, var6, var14, var15);
      this.field_70159_w *= 0.10000000149011612;
      this.field_70181_x *= 0.10000000149011612;
      this.field_70179_y *= 0.10000000149011612;
      this.field_70159_w += var8;
      this.field_70181_x += var10;
      this.field_70179_y += var12;
   }

   @Override
   public int func_70537_b() {
      return 2;
   }

   @Override
   public void func_70539_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = ((float)this.field_94054_b + this.field_70548_b / 4.0F) / 16.0F;
      float var9 = var8 + 0.015609375F;
      float var10 = ((float)this.field_94055_c + this.field_70549_c / 4.0F) / 16.0F;
      float var11 = var10 + 0.015609375F;
      float var12 = 0.1F * this.field_70544_f;
      if (this.field_70550_a != null) {
         var8 = this.field_70550_a.func_94214_a((double)(this.field_70548_b / 4.0F * 16.0F));
         var9 = this.field_70550_a.func_94214_a((double)((this.field_70548_b + 1.0F) / 4.0F * 16.0F));
         var10 = this.field_70550_a.func_94207_b((double)(this.field_70549_c / 4.0F * 16.0F));
         var11 = this.field_70550_a.func_94207_b((double)((this.field_70549_c + 1.0F) / 4.0F * 16.0F));
      }

      float var13 = (float)(this.field_70169_q + (this.field_70165_t - this.field_70169_q) * (double)var2 - field_70556_an);
      float var14 = (float)(this.field_70167_r + (this.field_70163_u - this.field_70167_r) * (double)var2 - field_70554_ao);
      float var15 = (float)(this.field_70166_s + (this.field_70161_v - this.field_70166_s) * (double)var2 - field_70555_ap);
      var1.func_78386_a(this.field_70552_h, this.field_70553_i, this.field_70551_j);
      var1.func_78374_a(
         (double)(var13 - var3 * var12 - var6 * var12),
         (double)(var14 - var4 * var12),
         (double)(var15 - var5 * var12 - var7 * var12),
         (double)var8,
         (double)var11
      );
      var1.func_78374_a(
         (double)(var13 - var3 * var12 + var6 * var12),
         (double)(var14 + var4 * var12),
         (double)(var15 - var5 * var12 + var7 * var12),
         (double)var8,
         (double)var10
      );
      var1.func_78374_a(
         (double)(var13 + var3 * var12 + var6 * var12),
         (double)(var14 + var4 * var12),
         (double)(var15 + var5 * var12 + var7 * var12),
         (double)var9,
         (double)var10
      );
      var1.func_78374_a(
         (double)(var13 + var3 * var12 - var6 * var12),
         (double)(var14 - var4 * var12),
         (double)(var15 + var5 * var12 - var7 * var12),
         (double)var9,
         (double)var11
      );
   }
}
