package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleCloud extends Particle {
   private final float field_70569_a;

   protected ParticleCloud(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      float var14 = 2.5F;
      this.field_187129_i *= 0.10000000149011612D;
      this.field_187130_j *= 0.10000000149011612D;
      this.field_187131_k *= 0.10000000149011612D;
      this.field_187129_i += var8;
      this.field_187130_j += var10;
      this.field_187131_k += var12;
      float var15 = 1.0F - (float)(Math.random() * 0.30000001192092896D);
      this.field_70552_h = var15;
      this.field_70553_i = var15;
      this.field_70551_j = var15;
      this.field_70544_f *= 0.75F;
      this.field_70544_f *= 2.5F;
      this.field_70569_a = this.field_70544_f;
      this.field_70547_e = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
      this.field_70547_e = (int)((float)this.field_70547_e * 2.5F);
      this.field_70547_e = Math.max(this.field_70547_e, 1);
      this.field_190017_n = false;
   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_70546_d + var3) / (float)this.field_70547_e * 32.0F;
      var9 = MathHelper.func_76131_a(var9, 0.0F, 1.0F);
      this.field_70544_f = this.field_70569_a * var9;
      super.func_180434_a(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      }

      this.func_70536_a(7 - this.field_70546_d * 8 / this.field_70547_e);
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      this.field_187129_i *= 0.9599999785423279D;
      this.field_187130_j *= 0.9599999785423279D;
      this.field_187131_k *= 0.9599999785423279D;
      EntityPlayer var1 = this.field_187122_b.func_184137_a(this.field_187126_f, this.field_187127_g, this.field_187128_h, 2.0D, false);
      if (var1 != null) {
         AxisAlignedBB var2 = var1.func_174813_aQ();
         if (this.field_187127_g > var2.field_72338_b) {
            this.field_187127_g += (var2.field_72338_b - this.field_187127_g) * 0.2D;
            this.field_187130_j += (var1.field_70181_x - this.field_187130_j) * 0.2D;
            this.func_187109_b(this.field_187126_f, this.field_187127_g, this.field_187128_h);
         }
      }

      if (this.field_187132_l) {
         this.field_187129_i *= 0.699999988079071D;
         this.field_187131_k *= 0.699999988079071D;
      }

   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleCloud(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
