package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySpellParticleFX extends EntityFX {
   private static final Random field_174848_a = new Random();
   private int field_70590_a = 128;

   protected EntitySpellParticleFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.5D - field_174848_a.nextDouble(), var10, 0.5D - field_174848_a.nextDouble());
      this.field_70181_x *= 0.20000000298023224D;
      if (var8 == 0.0D && var12 == 0.0D) {
         this.field_70159_w *= 0.10000000149011612D;
         this.field_70179_y *= 0.10000000149011612D;
      }

      this.field_70544_f *= 0.75F;
      this.field_70547_e = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.field_70145_X = false;
   }

   public void func_180434_a(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_70546_d + var3) / (float)this.field_70547_e * 32.0F;
      var9 = MathHelper.func_76131_a(var9, 0.0F, 1.0F);
      super.func_180434_a(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_70106_y();
      }

      this.func_70536_a(this.field_70590_a + (7 - this.field_70546_d * 8 / this.field_70547_e));
      this.field_70181_x += 0.004D;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      if (this.field_70163_u == this.field_70167_r) {
         this.field_70159_w *= 1.1D;
         this.field_70179_y *= 1.1D;
      }

      this.field_70159_w *= 0.9599999785423279D;
      this.field_70181_x *= 0.9599999785423279D;
      this.field_70179_y *= 0.9599999785423279D;
      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071D;
         this.field_70179_y *= 0.699999988079071D;
      }

   }

   public void func_70589_b(int var1) {
      this.field_70590_a = var1;
   }

   public static class InstantFactory implements IParticleFactory {
      public InstantFactory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         EntitySpellParticleFX var16 = new EntitySpellParticleFX(var2, var3, var5, var7, var9, var11, var13);
         ((EntitySpellParticleFX)var16).func_70589_b(144);
         return var16;
      }
   }

   public static class WitchFactory implements IParticleFactory {
      public WitchFactory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         EntitySpellParticleFX var16 = new EntitySpellParticleFX(var2, var3, var5, var7, var9, var11, var13);
         ((EntitySpellParticleFX)var16).func_70589_b(144);
         float var17 = var2.field_73012_v.nextFloat() * 0.5F + 0.35F;
         var16.func_70538_b(1.0F * var17, 0.0F * var17, 1.0F * var17);
         return var16;
      }
   }

   public static class AmbientMobFactory implements IParticleFactory {
      public AmbientMobFactory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         EntitySpellParticleFX var16 = new EntitySpellParticleFX(var2, var3, var5, var7, var9, var11, var13);
         var16.func_82338_g(0.15F);
         var16.func_70538_b((float)var9, (float)var11, (float)var13);
         return var16;
      }
   }

   public static class MobFactory implements IParticleFactory {
      public MobFactory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         EntitySpellParticleFX var16 = new EntitySpellParticleFX(var2, var3, var5, var7, var9, var11, var13);
         var16.func_70538_b((float)var9, (float)var11, (float)var13);
         return var16;
      }
   }

   public static class Factory implements IParticleFactory {
      public Factory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntitySpellParticleFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
