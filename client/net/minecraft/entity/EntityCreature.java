package net.minecraft.entity;

import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class EntityCreature extends EntityLiving {
   private BlockPos field_70775_bC;
   private float field_70772_bD;

   protected EntityCreature(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.field_70775_bC = BlockPos.field_177992_a;
      this.field_70772_bD = -1.0F;
   }

   public float func_180484_a(BlockPos var1) {
      return this.func_205022_a(var1, this.field_70170_p);
   }

   public float func_205022_a(BlockPos var1, IWorldReaderBase var2) {
      return 0.0F;
   }

   public boolean func_205020_a(IWorld var1, boolean var2) {
      return super.func_205020_a(var1, var2) && this.func_205022_a(new BlockPos(this.field_70165_t, this.func_174813_aQ().field_72338_b, this.field_70161_v), var1) >= 0.0F;
   }

   public boolean func_70781_l() {
      return !this.field_70699_by.func_75500_f();
   }

   public boolean func_110173_bK() {
      return this.func_180485_d(new BlockPos(this));
   }

   public boolean func_180485_d(BlockPos var1) {
      if (this.field_70772_bD == -1.0F) {
         return true;
      } else {
         return this.field_70775_bC.func_177951_i(var1) < (double)(this.field_70772_bD * this.field_70772_bD);
      }
   }

   public void func_175449_a(BlockPos var1, int var2) {
      this.field_70775_bC = var1;
      this.field_70772_bD = (float)var2;
   }

   public BlockPos func_180486_cf() {
      return this.field_70775_bC;
   }

   public float func_110174_bM() {
      return this.field_70772_bD;
   }

   public void func_110177_bN() {
      this.field_70772_bD = -1.0F;
   }

   public boolean func_110175_bO() {
      return this.field_70772_bD != -1.0F;
   }

   protected void func_110159_bB() {
      super.func_110159_bB();
      if (this.func_110167_bD() && this.func_110166_bE() != null && this.func_110166_bE().field_70170_p == this.field_70170_p) {
         Entity var1 = this.func_110166_bE();
         this.func_175449_a(new BlockPos((int)var1.field_70165_t, (int)var1.field_70163_u, (int)var1.field_70161_v), 5);
         float var2 = this.func_70032_d(var1);
         if (this instanceof EntityTameable && ((EntityTameable)this).func_70906_o()) {
            if (var2 > 10.0F) {
               this.func_110160_i(true, true);
            }

            return;
         }

         this.func_142017_o(var2);
         if (var2 > 10.0F) {
            this.func_110160_i(true, true);
            this.field_70714_bg.func_188526_c(1);
         } else if (var2 > 6.0F) {
            double var3 = (var1.field_70165_t - this.field_70165_t) / (double)var2;
            double var5 = (var1.field_70163_u - this.field_70163_u) / (double)var2;
            double var7 = (var1.field_70161_v - this.field_70161_v) / (double)var2;
            this.field_70159_w += var3 * Math.abs(var3) * 0.4D;
            this.field_70181_x += var5 * Math.abs(var5) * 0.4D;
            this.field_70179_y += var7 * Math.abs(var7) * 0.4D;
         } else {
            this.field_70714_bg.func_188525_d(1);
            float var9 = 2.0F;
            Vec3d var4 = (new Vec3d(var1.field_70165_t - this.field_70165_t, var1.field_70163_u - this.field_70163_u, var1.field_70161_v - this.field_70161_v)).func_72432_b().func_186678_a((double)Math.max(var2 - 2.0F, 0.0F));
            this.func_70661_as().func_75492_a(this.field_70165_t + var4.field_72450_a, this.field_70163_u + var4.field_72448_b, this.field_70161_v + var4.field_72449_c, this.func_190634_dg());
         }
      }

   }

   protected double func_190634_dg() {
      return 1.0D;
   }

   protected void func_142017_o(float var1) {
   }
}
