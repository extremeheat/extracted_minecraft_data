package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityDolphin;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EntityAIJump extends EntityAIBase {
   private static final int[] field_211697_a = new int[]{0, 1, 4, 5, 6, 7};
   private final EntityDolphin field_205149_a;
   private final int field_205150_b;
   private boolean field_205151_c;

   public EntityAIJump(EntityDolphin var1, int var2) {
      super();
      this.field_205149_a = var1;
      this.field_205150_b = var2;
      this.func_75248_a(5);
   }

   public boolean func_75250_a() {
      if (this.field_205149_a.func_70681_au().nextInt(this.field_205150_b) != 0) {
         return false;
      } else {
         EnumFacing var1 = this.field_205149_a.func_184172_bi();
         int var2 = var1.func_82601_c();
         int var3 = var1.func_82599_e();
         BlockPos var4 = new BlockPos(this.field_205149_a);
         int[] var5 = field_211697_a;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            int var8 = var5[var7];
            if (!this.func_211695_a(var4, var2, var3, var8) || !this.func_211696_b(var4, var2, var3, var8)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean func_211695_a(BlockPos var1, int var2, int var3, int var4) {
      BlockPos var5 = var1.func_177982_a(var2 * var4, 0, var3 * var4);
      return this.field_205149_a.field_70170_p.func_204610_c(var5).func_206884_a(FluidTags.field_206959_a) && !this.field_205149_a.field_70170_p.func_180495_p(var5).func_185904_a().func_76230_c();
   }

   private boolean func_211696_b(BlockPos var1, int var2, int var3, int var4) {
      return this.field_205149_a.field_70170_p.func_180495_p(var1.func_177982_a(var2 * var4, 1, var3 * var4)).func_196958_f() && this.field_205149_a.field_70170_p.func_180495_p(var1.func_177982_a(var2 * var4, 2, var3 * var4)).func_196958_f();
   }

   public boolean func_75253_b() {
      return (this.field_205149_a.field_70181_x * this.field_205149_a.field_70181_x >= 0.029999999329447746D || this.field_205149_a.field_70125_A == 0.0F || Math.abs(this.field_205149_a.field_70125_A) >= 10.0F || !this.field_205149_a.func_70090_H()) && !this.field_205149_a.field_70122_E;
   }

   public boolean func_75252_g() {
      return false;
   }

   public void func_75249_e() {
      EnumFacing var1 = this.field_205149_a.func_184172_bi();
      EntityDolphin var10000 = this.field_205149_a;
      var10000.field_70159_w += (double)var1.func_82601_c() * 0.6D;
      var10000 = this.field_205149_a;
      var10000.field_70181_x += 0.7D;
      var10000 = this.field_205149_a;
      var10000.field_70179_y += (double)var1.func_82599_e() * 0.6D;
      this.field_205149_a.func_70661_as().func_75499_g();
   }

   public void func_75251_c() {
      this.field_205149_a.field_70125_A = 0.0F;
   }

   public void func_75246_d() {
      boolean var1 = this.field_205151_c;
      if (!var1) {
         IFluidState var2 = this.field_205149_a.field_70170_p.func_204610_c(new BlockPos(this.field_205149_a));
         this.field_205151_c = var2.func_206884_a(FluidTags.field_206959_a);
      }

      if (this.field_205151_c && !var1) {
         this.field_205149_a.func_184185_a(SoundEvents.field_205209_aZ, 1.0F, 1.0F);
      }

      if (this.field_205149_a.field_70181_x * this.field_205149_a.field_70181_x < 0.029999999329447746D && this.field_205149_a.field_70125_A != 0.0F) {
         this.field_205149_a.field_70125_A = this.func_205147_a(this.field_205149_a.field_70125_A, 0.0F, 0.2F);
      } else {
         double var8 = Math.sqrt(this.field_205149_a.field_70159_w * this.field_205149_a.field_70159_w + this.field_205149_a.field_70181_x * this.field_205149_a.field_70181_x + this.field_205149_a.field_70179_y * this.field_205149_a.field_70179_y);
         double var4 = Math.sqrt(this.field_205149_a.field_70159_w * this.field_205149_a.field_70159_w + this.field_205149_a.field_70179_y * this.field_205149_a.field_70179_y);
         double var6 = Math.signum(-this.field_205149_a.field_70181_x) * Math.acos(var4 / var8) * 57.2957763671875D;
         this.field_205149_a.field_70125_A = (float)var6;
      }

   }

   protected float func_205147_a(float var1, float var2, float var3) {
      float var4;
      for(var4 = var2 - var1; var4 < -180.0F; var4 += 360.0F) {
      }

      while(var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return var1 + var3 * var4;
   }
}
