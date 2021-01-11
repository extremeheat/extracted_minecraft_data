package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAIFindEntityNearestPlayer extends EntityAIBase {
   private static final Logger field_179436_a = LogManager.getLogger();
   private EntityLiving field_179434_b;
   private final Predicate<Entity> field_179435_c;
   private final EntityAINearestAttackableTarget.Sorter field_179432_d;
   private EntityLivingBase field_179433_e;

   public EntityAIFindEntityNearestPlayer(EntityLiving var1) {
      super();
      this.field_179434_b = var1;
      if (var1 instanceof EntityCreature) {
         field_179436_a.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
      }

      this.field_179435_c = new Predicate<Entity>() {
         public boolean apply(Entity var1) {
            if (!(var1 instanceof EntityPlayer)) {
               return false;
            } else if (((EntityPlayer)var1).field_71075_bZ.field_75102_a) {
               return false;
            } else {
               double var2 = EntityAIFindEntityNearestPlayer.this.func_179431_f();
               if (var1.func_70093_af()) {
                  var2 *= 0.800000011920929D;
               }

               if (var1.func_82150_aj()) {
                  float var4 = ((EntityPlayer)var1).func_82243_bO();
                  if (var4 < 0.1F) {
                     var4 = 0.1F;
                  }

                  var2 *= (double)(0.7F * var4);
               }

               return (double)var1.func_70032_d(EntityAIFindEntityNearestPlayer.this.field_179434_b) > var2 ? false : EntityAITarget.func_179445_a(EntityAIFindEntityNearestPlayer.this.field_179434_b, (EntityLivingBase)var1, false, true);
            }
         }

         // $FF: synthetic method
         public boolean apply(Object var1) {
            return this.apply((Entity)var1);
         }
      };
      this.field_179432_d = new EntityAINearestAttackableTarget.Sorter(var1);
   }

   public boolean func_75250_a() {
      double var1 = this.func_179431_f();
      List var3 = this.field_179434_b.field_70170_p.func_175647_a(EntityPlayer.class, this.field_179434_b.func_174813_aQ().func_72314_b(var1, 4.0D, var1), this.field_179435_c);
      Collections.sort(var3, this.field_179432_d);
      if (var3.isEmpty()) {
         return false;
      } else {
         this.field_179433_e = (EntityLivingBase)var3.get(0);
         return true;
      }
   }

   public boolean func_75253_b() {
      EntityLivingBase var1 = this.field_179434_b.func_70638_az();
      if (var1 == null) {
         return false;
      } else if (!var1.func_70089_S()) {
         return false;
      } else if (var1 instanceof EntityPlayer && ((EntityPlayer)var1).field_71075_bZ.field_75102_a) {
         return false;
      } else {
         Team var2 = this.field_179434_b.func_96124_cp();
         Team var3 = var1.func_96124_cp();
         if (var2 != null && var3 == var2) {
            return false;
         } else {
            double var4 = this.func_179431_f();
            if (this.field_179434_b.func_70068_e(var1) > var4 * var4) {
               return false;
            } else {
               return !(var1 instanceof EntityPlayerMP) || !((EntityPlayerMP)var1).field_71134_c.func_73083_d();
            }
         }
      }
   }

   public void func_75249_e() {
      this.field_179434_b.func_70624_b(this.field_179433_e);
      super.func_75249_e();
   }

   public void func_75251_c() {
      this.field_179434_b.func_70624_b((EntityLivingBase)null);
      super.func_75249_e();
   }

   protected double func_179431_f() {
      IAttributeInstance var1 = this.field_179434_b.func_110148_a(SharedMonsterAttributes.field_111265_b);
      return var1 == null ? 16.0D : var1.func_111126_e();
   }
}
