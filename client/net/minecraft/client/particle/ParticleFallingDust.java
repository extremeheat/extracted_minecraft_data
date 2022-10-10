package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleFallingDust extends Particle {
   private final float field_190018_a;
   private final float field_190019_b;

   protected ParticleFallingDust(World var1, double var2, double var4, double var6, float var8, float var9, float var10) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.field_187129_i = 0.0D;
      this.field_187130_j = 0.0D;
      this.field_187131_k = 0.0D;
      this.field_70552_h = var8;
      this.field_70553_i = var9;
      this.field_70551_j = var10;
      float var11 = 0.9F;
      this.field_70544_f *= 0.75F;
      this.field_70544_f *= 0.9F;
      this.field_190018_a = this.field_70544_f;
      this.field_70547_e = (int)(32.0D / (Math.random() * 0.8D + 0.2D));
      this.field_70547_e = (int)((float)this.field_70547_e * 0.9F);
      this.field_70547_e = Math.max(this.field_70547_e, 1);
      this.field_190019_b = ((float)Math.random() - 0.5F) * 0.1F;
      this.field_190014_F = (float)Math.random() * 6.2831855F;
   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_70546_d + var3) / (float)this.field_70547_e * 32.0F;
      var9 = MathHelper.func_76131_a(var9, 0.0F, 1.0F);
      this.field_70544_f = this.field_190018_a * var9;
      super.func_180434_a(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      }

      this.field_190015_G = this.field_190014_F;
      this.field_190014_F += 3.1415927F * this.field_190019_b * 2.0F;
      if (this.field_187132_l) {
         this.field_190015_G = this.field_190014_F = 0.0F;
      }

      this.func_70536_a(7 - this.field_70546_d * 8 / this.field_70547_e);
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      this.field_187130_j -= 0.003000000026077032D;
      this.field_187130_j = Math.max(this.field_187130_j, -0.14000000059604645D);
   }

   public static class Factory implements IParticleFactory<BlockParticleData> {
      public Factory() {
         super();
      }

      @Nullable
      public Particle func_199234_a(BlockParticleData var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         IBlockState var15 = var1.func_197584_c();
         if (!var15.func_196958_f() && var15.func_185901_i() == EnumBlockRenderType.INVISIBLE) {
            return null;
         } else {
            int var16 = Minecraft.func_71410_x().func_184125_al().func_189991_a(var15, var2, new BlockPos(var3, var5, var7));
            if (var15.func_177230_c() instanceof BlockFalling) {
               var16 = ((BlockFalling)var15.func_177230_c()).func_189876_x(var15);
            }

            float var17 = (float)(var16 >> 16 & 255) / 255.0F;
            float var18 = (float)(var16 >> 8 & 255) / 255.0F;
            float var19 = (float)(var16 & 255) / 255.0F;
            return new ParticleFallingDust(var2, var3, var5, var7, var17, var18, var19);
         }
      }
   }
}
