package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityDiggingFX extends EntityFX {
   private IBlockState field_174847_a;
   private BlockPos field_181019_az;

   protected EntityDiggingFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, IBlockState var14) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.field_174847_a = var14;
      this.func_180435_a(Minecraft.func_71410_x().func_175602_ab().func_175023_a().func_178122_a(var14));
      this.field_70545_g = var14.func_177230_c().field_149763_I;
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 0.6F;
      this.field_70544_f /= 2.0F;
   }

   public EntityDiggingFX func_174846_a(BlockPos var1) {
      this.field_181019_az = var1;
      if (this.field_174847_a.func_177230_c() == Blocks.field_150349_c) {
         return this;
      } else {
         int var2 = this.field_174847_a.func_177230_c().func_176202_d(this.field_70170_p, var1);
         this.field_70552_h *= (float)(var2 >> 16 & 255) / 255.0F;
         this.field_70553_i *= (float)(var2 >> 8 & 255) / 255.0F;
         this.field_70551_j *= (float)(var2 & 255) / 255.0F;
         return this;
      }
   }

   public EntityDiggingFX func_174845_l() {
      this.field_181019_az = new BlockPos(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      Block var1 = this.field_174847_a.func_177230_c();
      if (var1 == Blocks.field_150349_c) {
         return this;
      } else {
         int var2 = var1.func_180644_h(this.field_174847_a);
         this.field_70552_h *= (float)(var2 >> 16 & 255) / 255.0F;
         this.field_70553_i *= (float)(var2 >> 8 & 255) / 255.0F;
         this.field_70551_j *= (float)(var2 & 255) / 255.0F;
         return this;
      }
   }

   public int func_70537_b() {
      return 1;
   }

   public void func_180434_a(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_94054_b + this.field_70548_b / 4.0F) / 16.0F;
      float var10 = var9 + 0.015609375F;
      float var11 = ((float)this.field_94055_c + this.field_70549_c / 4.0F) / 16.0F;
      float var12 = var11 + 0.015609375F;
      float var13 = 0.1F * this.field_70544_f;
      if (this.field_70550_a != null) {
         var9 = this.field_70550_a.func_94214_a((double)(this.field_70548_b / 4.0F * 16.0F));
         var10 = this.field_70550_a.func_94214_a((double)((this.field_70548_b + 1.0F) / 4.0F * 16.0F));
         var11 = this.field_70550_a.func_94207_b((double)(this.field_70549_c / 4.0F * 16.0F));
         var12 = this.field_70550_a.func_94207_b((double)((this.field_70549_c + 1.0F) / 4.0F * 16.0F));
      }

      float var14 = (float)(this.field_70169_q + (this.field_70165_t - this.field_70169_q) * (double)var3 - field_70556_an);
      float var15 = (float)(this.field_70167_r + (this.field_70163_u - this.field_70167_r) * (double)var3 - field_70554_ao);
      float var16 = (float)(this.field_70166_s + (this.field_70161_v - this.field_70166_s) * (double)var3 - field_70555_ap);
      int var17 = this.func_70070_b(var3);
      int var18 = var17 >> 16 & '\uffff';
      int var19 = var17 & '\uffff';
      var1.func_181662_b((double)(var14 - var4 * var13 - var7 * var13), (double)(var15 - var5 * var13), (double)(var16 - var6 * var13 - var8 * var13)).func_181673_a((double)var9, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_181671_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)(var14 - var4 * var13 + var7 * var13), (double)(var15 + var5 * var13), (double)(var16 - var6 * var13 + var8 * var13)).func_181673_a((double)var9, (double)var11).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_181671_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)(var14 + var4 * var13 + var7 * var13), (double)(var15 + var5 * var13), (double)(var16 + var6 * var13 + var8 * var13)).func_181673_a((double)var10, (double)var11).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_181671_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)(var14 + var4 * var13 - var7 * var13), (double)(var15 - var5 * var13), (double)(var16 + var6 * var13 - var8 * var13)).func_181673_a((double)var10, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, 1.0F).func_181671_a(var18, var19).func_181675_d();
   }

   public int func_70070_b(float var1) {
      int var2 = super.func_70070_b(var1);
      int var3 = 0;
      if (this.field_70170_p.func_175667_e(this.field_181019_az)) {
         var3 = this.field_70170_p.func_175626_b(this.field_181019_az, 0);
      }

      return var2 == 0 ? var3 : var2;
   }

   public static class Factory implements IParticleFactory {
      public Factory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return (new EntityDiggingFX(var2, var3, var5, var7, var9, var11, var13, Block.func_176220_d(var15[0]))).func_174845_l();
      }
   }
}
