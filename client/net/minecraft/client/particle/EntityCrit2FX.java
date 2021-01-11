package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCrit2FX extends EntityFX {
   float field_174839_a;

   protected EntityCrit2FX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6, var8, var10, var12, 1.0F);
   }

   protected EntityCrit2FX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.field_70159_w *= 0.10000000149011612D;
      this.field_70181_x *= 0.10000000149011612D;
      this.field_70179_y *= 0.10000000149011612D;
      this.field_70159_w += var8 * 0.4D;
      this.field_70181_x += var10 * 0.4D;
      this.field_70179_y += var12 * 0.4D;
      this.field_70552_h = this.field_70553_i = this.field_70551_j = (float)(Math.random() * 0.30000001192092896D + 0.6000000238418579D);
      this.field_70544_f *= 0.75F;
      this.field_70544_f *= var14;
      this.field_174839_a = this.field_70544_f;
      this.field_70547_e = (int)(6.0D / (Math.random() * 0.8D + 0.6D));
      this.field_70547_e = (int)((float)this.field_70547_e * var14);
      this.field_70145_X = false;
      this.func_70536_a(65);
      this.func_70071_h_();
   }

   public void func_180434_a(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_70546_d + var3) / (float)this.field_70547_e * 32.0F;
      var9 = MathHelper.func_76131_a(var9, 0.0F, 1.0F);
      this.field_70544_f = this.field_174839_a * var9;
      super.func_180434_a(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_70106_y();
      }

      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70553_i = (float)((double)this.field_70553_i * 0.96D);
      this.field_70551_j = (float)((double)this.field_70551_j * 0.9D);
      this.field_70159_w *= 0.699999988079071D;
      this.field_70181_x *= 0.699999988079071D;
      this.field_70179_y *= 0.699999988079071D;
      this.field_70181_x -= 0.019999999552965164D;
      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071D;
         this.field_70179_y *= 0.699999988079071D;
      }

   }

   public static class MagicFactory implements IParticleFactory {
      public MagicFactory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         EntityCrit2FX var16 = new EntityCrit2FX(var2, var3, var5, var7, var9, var11, var13);
         var16.func_70538_b(var16.func_70534_d() * 0.3F, var16.func_70542_f() * 0.8F, var16.func_70535_g());
         var16.func_94053_h();
         return var16;
      }
   }

   public static class Factory implements IParticleFactory {
      public Factory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityCrit2FX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
