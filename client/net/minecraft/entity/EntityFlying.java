package net.minecraft.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class EntityFlying extends EntityLiving {
   protected EntityFlying(EntityType<?> var1, World var2) {
      super(var1, var2);
   }

   public void func_180430_e(float var1, float var2) {
   }

   protected void func_184231_a(double var1, boolean var3, IBlockState var4, BlockPos var5) {
   }

   public void func_191986_a(float var1, float var2, float var3) {
      if (this.func_70090_H()) {
         this.func_191958_b(var1, var2, var3, 0.02F);
         this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.field_70159_w *= 0.800000011920929D;
         this.field_70181_x *= 0.800000011920929D;
         this.field_70179_y *= 0.800000011920929D;
      } else if (this.func_180799_ab()) {
         this.func_191958_b(var1, var2, var3, 0.02F);
         this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.field_70159_w *= 0.5D;
         this.field_70181_x *= 0.5D;
         this.field_70179_y *= 0.5D;
      } else {
         float var4 = 0.91F;
         if (this.field_70122_E) {
            var4 = this.field_70170_p.func_180495_p(new BlockPos(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b) - 1, MathHelper.func_76128_c(this.field_70161_v))).func_177230_c().func_208618_m() * 0.91F;
         }

         float var5 = 0.16277137F / (var4 * var4 * var4);
         this.func_191958_b(var1, var2, var3, this.field_70122_E ? 0.1F * var5 : 0.02F);
         var4 = 0.91F;
         if (this.field_70122_E) {
            var4 = this.field_70170_p.func_180495_p(new BlockPos(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b) - 1, MathHelper.func_76128_c(this.field_70161_v))).func_177230_c().func_208618_m() * 0.91F;
         }

         this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.field_70159_w *= (double)var4;
         this.field_70181_x *= (double)var4;
         this.field_70179_y *= (double)var4;
      }

      this.field_184618_aE = this.field_70721_aZ;
      double var9 = this.field_70165_t - this.field_70169_q;
      double var6 = this.field_70161_v - this.field_70166_s;
      float var8 = MathHelper.func_76133_a(var9 * var9 + var6 * var6) * 4.0F;
      if (var8 > 1.0F) {
         var8 = 1.0F;
      }

      this.field_70721_aZ += (var8 - this.field_70721_aZ) * 0.4F;
      this.field_184619_aG += this.field_70721_aZ;
   }

   public boolean func_70617_f_() {
      return false;
   }
}
