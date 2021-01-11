package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;

public abstract class EntityAITarget extends EntityAIBase {
   protected final EntityCreature field_75299_d;
   protected boolean field_75297_f;
   private boolean field_75303_a;
   private int field_75301_b;
   private int field_75302_c;
   private int field_75298_g;

   public EntityAITarget(EntityCreature var1, boolean var2) {
      this(var1, var2, false);
   }

   public EntityAITarget(EntityCreature var1, boolean var2, boolean var3) {
      super();
      this.field_75299_d = var1;
      this.field_75297_f = var2;
      this.field_75303_a = var3;
   }

   public boolean func_75253_b() {
      EntityLivingBase var1 = this.field_75299_d.func_70638_az();
      if (var1 == null) {
         return false;
      } else if (!var1.func_70089_S()) {
         return false;
      } else {
         Team var2 = this.field_75299_d.func_96124_cp();
         Team var3 = var1.func_96124_cp();
         if (var2 != null && var3 == var2) {
            return false;
         } else {
            double var4 = this.func_111175_f();
            if (this.field_75299_d.func_70068_e(var1) > var4 * var4) {
               return false;
            } else {
               if (this.field_75297_f) {
                  if (this.field_75299_d.func_70635_at().func_75522_a(var1)) {
                     this.field_75298_g = 0;
                  } else if (++this.field_75298_g > 60) {
                     return false;
                  }
               }

               return !(var1 instanceof EntityPlayer) || !((EntityPlayer)var1).field_71075_bZ.field_75102_a;
            }
         }
      }
   }

   protected double func_111175_f() {
      IAttributeInstance var1 = this.field_75299_d.func_110148_a(SharedMonsterAttributes.field_111265_b);
      return var1 == null ? 16.0D : var1.func_111126_e();
   }

   public void func_75249_e() {
      this.field_75301_b = 0;
      this.field_75302_c = 0;
      this.field_75298_g = 0;
   }

   public void func_75251_c() {
      this.field_75299_d.func_70624_b((EntityLivingBase)null);
   }

   public static boolean func_179445_a(EntityLiving var0, EntityLivingBase var1, boolean var2, boolean var3) {
      if (var1 == null) {
         return false;
      } else if (var1 == var0) {
         return false;
      } else if (!var1.func_70089_S()) {
         return false;
      } else if (!var0.func_70686_a(var1.getClass())) {
         return false;
      } else {
         Team var4 = var0.func_96124_cp();
         Team var5 = var1.func_96124_cp();
         if (var4 != null && var5 == var4) {
            return false;
         } else {
            if (var0 instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable)var0).func_152113_b())) {
               if (var1 instanceof IEntityOwnable && ((IEntityOwnable)var0).func_152113_b().equals(((IEntityOwnable)var1).func_152113_b())) {
                  return false;
               }

               if (var1 == ((IEntityOwnable)var0).func_70902_q()) {
                  return false;
               }
            } else if (var1 instanceof EntityPlayer && !var2 && ((EntityPlayer)var1).field_71075_bZ.field_75102_a) {
               return false;
            }

            return !var3 || var0.func_70635_at().func_75522_a(var1);
         }
      }
   }

   protected boolean func_75296_a(EntityLivingBase var1, boolean var2) {
      if (!func_179445_a(this.field_75299_d, var1, var2, this.field_75297_f)) {
         return false;
      } else if (!this.field_75299_d.func_180485_d(new BlockPos(var1))) {
         return false;
      } else {
         if (this.field_75303_a) {
            if (--this.field_75302_c <= 0) {
               this.field_75301_b = 0;
            }

            if (this.field_75301_b == 0) {
               this.field_75301_b = this.func_75295_a(var1) ? 1 : 2;
            }

            if (this.field_75301_b == 2) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean func_75295_a(EntityLivingBase var1) {
      this.field_75302_c = 10 + this.field_75299_d.func_70681_au().nextInt(5);
      PathEntity var2 = this.field_75299_d.func_70661_as().func_75494_a(var1);
      if (var2 == null) {
         return false;
      } else {
         PathPoint var3 = var2.func_75870_c();
         if (var3 == null) {
            return false;
         } else {
            int var4 = var3.field_75839_a - MathHelper.func_76128_c(var1.field_70165_t);
            int var5 = var3.field_75838_c - MathHelper.func_76128_c(var1.field_70161_v);
            return (double)(var4 * var4 + var5 * var5) <= 2.25D;
         }
      }
   }
}
