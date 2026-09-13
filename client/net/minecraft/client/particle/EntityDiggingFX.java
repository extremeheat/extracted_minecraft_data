package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class EntityDiggingFX extends EntityFX {
   private Block field_145784_a;

   public EntityDiggingFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, Block var14, int var15) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.field_145784_a = var14;
      this.func_110125_a(var14.func_149691_a(0, var15));
      this.field_70545_g = var14.field_149763_I;
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 0.6F;
      this.field_70544_f /= 2.0F;
   }

   public EntityDiggingFX func_70596_a(int var1, int var2, int var3) {
      if (this.field_145784_a == Blocks.field_150349_c) {
         return this;
      } else {
         int var4 = this.field_145784_a.func_149720_d(this.field_70170_p, var1, var2, var3);
         this.field_70552_h *= (float)(var4 >> 16 & 0xFF) / 255.0F;
         this.field_70553_i *= (float)(var4 >> 8 & 0xFF) / 255.0F;
         this.field_70551_j *= (float)(var4 & 0xFF) / 255.0F;
         return this;
      }
   }

   public EntityDiggingFX func_90019_g(int var1) {
      if (this.field_145784_a == Blocks.field_150349_c) {
         return this;
      } else {
         int var2 = this.field_145784_a.func_149741_i(var1);
         this.field_70552_h *= (float)(var2 >> 16 & 0xFF) / 255.0F;
         this.field_70553_i *= (float)(var2 >> 8 & 0xFF) / 255.0F;
         this.field_70551_j *= (float)(var2 & 0xFF) / 255.0F;
         return this;
      }
   }

   @Override
   public int func_70537_b() {
      return 1;
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
