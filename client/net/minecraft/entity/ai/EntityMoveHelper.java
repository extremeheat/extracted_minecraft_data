package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.MathHelper;

public class EntityMoveHelper {
   protected final EntityLiving field_75648_a;
   protected double field_75646_b;
   protected double field_75647_c;
   protected double field_75644_d;
   protected double field_75645_e;
   protected float field_188489_f;
   protected float field_188490_g;
   protected EntityMoveHelper.Action field_188491_h;

   public EntityMoveHelper(EntityLiving var1) {
      super();
      this.field_188491_h = EntityMoveHelper.Action.WAIT;
      this.field_75648_a = var1;
   }

   public boolean func_75640_a() {
      return this.field_188491_h == EntityMoveHelper.Action.MOVE_TO;
   }

   public double func_75638_b() {
      return this.field_75645_e;
   }

   public void func_75642_a(double var1, double var3, double var5, double var7) {
      this.field_75646_b = var1;
      this.field_75647_c = var3;
      this.field_75644_d = var5;
      this.field_75645_e = var7;
      if (this.field_188491_h != EntityMoveHelper.Action.JUMPING) {
         this.field_188491_h = EntityMoveHelper.Action.MOVE_TO;
      }

   }

   public void func_188488_a(float var1, float var2) {
      this.field_188491_h = EntityMoveHelper.Action.STRAFE;
      this.field_188489_f = var1;
      this.field_188490_g = var2;
      this.field_75645_e = 0.25D;
   }

   public void func_188487_a(EntityMoveHelper var1) {
      this.field_188491_h = var1.field_188491_h;
      this.field_75646_b = var1.field_75646_b;
      this.field_75647_c = var1.field_75647_c;
      this.field_75644_d = var1.field_75644_d;
      this.field_75645_e = Math.max(var1.field_75645_e, 1.0D);
      this.field_188489_f = var1.field_188489_f;
      this.field_188490_g = var1.field_188490_g;
   }

   public void func_75641_c() {
      float var9;
      if (this.field_188491_h == EntityMoveHelper.Action.STRAFE) {
         float var1 = (float)this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e();
         float var2 = (float)this.field_75645_e * var1;
         float var3 = this.field_188489_f;
         float var4 = this.field_188490_g;
         float var5 = MathHelper.func_76129_c(var3 * var3 + var4 * var4);
         if (var5 < 1.0F) {
            var5 = 1.0F;
         }

         var5 = var2 / var5;
         var3 *= var5;
         var4 *= var5;
         float var6 = MathHelper.func_76126_a(this.field_75648_a.field_70177_z * 0.017453292F);
         float var7 = MathHelper.func_76134_b(this.field_75648_a.field_70177_z * 0.017453292F);
         float var8 = var3 * var7 - var4 * var6;
         var9 = var4 * var7 + var3 * var6;
         PathNavigate var10 = this.field_75648_a.func_70661_as();
         if (var10 != null) {
            NodeProcessor var11 = var10.func_189566_q();
            if (var11 != null && var11.func_186330_a(this.field_75648_a.field_70170_p, MathHelper.func_76128_c(this.field_75648_a.field_70165_t + (double)var8), MathHelper.func_76128_c(this.field_75648_a.field_70163_u), MathHelper.func_76128_c(this.field_75648_a.field_70161_v + (double)var9)) != PathNodeType.WALKABLE) {
               this.field_188489_f = 1.0F;
               this.field_188490_g = 0.0F;
               var2 = var1;
            }
         }

         this.field_75648_a.func_70659_e(var2);
         this.field_75648_a.func_191989_p(this.field_188489_f);
         this.field_75648_a.func_184646_p(this.field_188490_g);
         this.field_188491_h = EntityMoveHelper.Action.WAIT;
      } else if (this.field_188491_h == EntityMoveHelper.Action.MOVE_TO) {
         this.field_188491_h = EntityMoveHelper.Action.WAIT;
         double var12 = this.field_75646_b - this.field_75648_a.field_70165_t;
         double var13 = this.field_75644_d - this.field_75648_a.field_70161_v;
         double var14 = this.field_75647_c - this.field_75648_a.field_70163_u;
         double var15 = var12 * var12 + var14 * var14 + var13 * var13;
         if (var15 < 2.500000277905201E-7D) {
            this.field_75648_a.func_191989_p(0.0F);
            return;
         }

         var9 = (float)(MathHelper.func_181159_b(var13, var12) * 57.2957763671875D) - 90.0F;
         this.field_75648_a.field_70177_z = this.func_75639_a(this.field_75648_a.field_70177_z, var9, 90.0F);
         this.field_75648_a.func_70659_e((float)(this.field_75645_e * this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e()));
         if (var14 > (double)this.field_75648_a.field_70138_W && var12 * var12 + var13 * var13 < (double)Math.max(1.0F, this.field_75648_a.field_70130_N)) {
            this.field_75648_a.func_70683_ar().func_75660_a();
            this.field_188491_h = EntityMoveHelper.Action.JUMPING;
         }
      } else if (this.field_188491_h == EntityMoveHelper.Action.JUMPING) {
         this.field_75648_a.func_70659_e((float)(this.field_75645_e * this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e()));
         if (this.field_75648_a.field_70122_E) {
            this.field_188491_h = EntityMoveHelper.Action.WAIT;
         }
      } else {
         this.field_75648_a.func_191989_p(0.0F);
      }

   }

   protected float func_75639_a(float var1, float var2, float var3) {
      float var4 = MathHelper.func_76142_g(var2 - var1);
      if (var4 > var3) {
         var4 = var3;
      }

      if (var4 < -var3) {
         var4 = -var3;
      }

      float var5 = var1 + var4;
      if (var5 < 0.0F) {
         var5 += 360.0F;
      } else if (var5 > 360.0F) {
         var5 -= 360.0F;
      }

      return var5;
   }

   public double func_179917_d() {
      return this.field_75646_b;
   }

   public double func_179919_e() {
      return this.field_75647_c;
   }

   public double func_179918_f() {
      return this.field_75644_d;
   }

   public static enum Action {
      WAIT,
      MOVE_TO,
      STRAFE,
      JUMPING;

      private Action() {
      }
   }
}
