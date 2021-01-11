package net.minecraft.client.particle;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityDropParticleFX extends EntityFX {
   private Material field_70563_a;
   private int field_70564_aq;

   protected EntityDropParticleFX(World var1, double var2, double var4, double var6, Material var8) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.field_70159_w = this.field_70181_x = this.field_70179_y = 0.0D;
      if (var8 == Material.field_151586_h) {
         this.field_70552_h = 0.0F;
         this.field_70553_i = 0.0F;
         this.field_70551_j = 1.0F;
      } else {
         this.field_70552_h = 1.0F;
         this.field_70553_i = 0.0F;
         this.field_70551_j = 0.0F;
      }

      this.func_70536_a(113);
      this.func_70105_a(0.01F, 0.01F);
      this.field_70545_g = 0.06F;
      this.field_70563_a = var8;
      this.field_70564_aq = 40;
      this.field_70547_e = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
      this.field_70159_w = this.field_70181_x = this.field_70179_y = 0.0D;
   }

   public int func_70070_b(float var1) {
      return this.field_70563_a == Material.field_151586_h ? super.func_70070_b(var1) : 257;
   }

   public float func_70013_c(float var1) {
      return this.field_70563_a == Material.field_151586_h ? super.func_70013_c(var1) : 1.0F;
   }

   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      if (this.field_70563_a == Material.field_151586_h) {
         this.field_70552_h = 0.2F;
         this.field_70553_i = 0.3F;
         this.field_70551_j = 1.0F;
      } else {
         this.field_70552_h = 1.0F;
         this.field_70553_i = 16.0F / (float)(40 - this.field_70564_aq + 16);
         this.field_70551_j = 4.0F / (float)(40 - this.field_70564_aq + 8);
      }

      this.field_70181_x -= (double)this.field_70545_g;
      if (this.field_70564_aq-- > 0) {
         this.field_70159_w *= 0.02D;
         this.field_70181_x *= 0.02D;
         this.field_70179_y *= 0.02D;
         this.func_70536_a(113);
      } else {
         this.func_70536_a(112);
      }

      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.9800000190734863D;
      this.field_70181_x *= 0.9800000190734863D;
      this.field_70179_y *= 0.9800000190734863D;
      if (this.field_70547_e-- <= 0) {
         this.func_70106_y();
      }

      if (this.field_70122_E) {
         if (this.field_70563_a == Material.field_151586_h) {
            this.func_70106_y();
            this.field_70170_p.func_175688_a(EnumParticleTypes.WATER_SPLASH, this.field_70165_t, this.field_70163_u, this.field_70161_v, 0.0D, 0.0D, 0.0D);
         } else {
            this.func_70536_a(114);
         }

         this.field_70159_w *= 0.699999988079071D;
         this.field_70179_y *= 0.699999988079071D;
      }

      BlockPos var1 = new BlockPos(this);
      IBlockState var2 = this.field_70170_p.func_180495_p(var1);
      Material var3 = var2.func_177230_c().func_149688_o();
      if (var3.func_76224_d() || var3.func_76220_a()) {
         double var4 = 0.0D;
         if (var2.func_177230_c() instanceof BlockLiquid) {
            var4 = (double)BlockLiquid.func_149801_b((Integer)var2.func_177229_b(BlockLiquid.field_176367_b));
         }

         double var6 = (double)(MathHelper.func_76128_c(this.field_70163_u) + 1) - var4;
         if (this.field_70163_u < var6) {
            this.func_70106_y();
         }
      }

   }

   public static class LavaFactory implements IParticleFactory {
      public LavaFactory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityDropParticleFX(var2, var3, var5, var7, Material.field_151587_i);
      }
   }

   public static class WaterFactory implements IParticleFactory {
      public WaterFactory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityDropParticleFX(var2, var3, var5, var7, Material.field_151586_h);
      }
   }
}
