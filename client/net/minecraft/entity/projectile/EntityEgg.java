package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityEgg extends EntityThrowable {
   public EntityEgg(World var1) {
      super(EntityType.field_200751_aq, var1);
   }

   public EntityEgg(World var1, EntityLivingBase var2) {
      super(EntityType.field_200751_aq, var2, var1);
   }

   public EntityEgg(World var1, double var2, double var4, double var6) {
      super(EntityType.field_200751_aq, var2, var4, var6, var1);
   }

   public void func_70103_a(byte var1) {
      if (var1 == 3) {
         double var2 = 0.08D;

         for(int var4 = 0; var4 < 8; ++var4) {
            this.field_70170_p.func_195594_a(new ItemParticleData(Particles.field_197591_B, new ItemStack(Items.field_151110_aK)), this.field_70165_t, this.field_70163_u, this.field_70161_v, ((double)this.field_70146_Z.nextFloat() - 0.5D) * 0.08D, ((double)this.field_70146_Z.nextFloat() - 0.5D) * 0.08D, ((double)this.field_70146_Z.nextFloat() - 0.5D) * 0.08D);
         }
      }

   }

   protected void func_70184_a(RayTraceResult var1) {
      if (var1.field_72308_g != null) {
         var1.field_72308_g.func_70097_a(DamageSource.func_76356_a(this, this.func_85052_h()), 0.0F);
      }

      if (!this.field_70170_p.field_72995_K) {
         if (this.field_70146_Z.nextInt(8) == 0) {
            byte var2 = 1;
            if (this.field_70146_Z.nextInt(32) == 0) {
               var2 = 4;
            }

            for(int var3 = 0; var3 < var2; ++var3) {
               EntityChicken var4 = new EntityChicken(this.field_70170_p);
               var4.func_70873_a(-24000);
               var4.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, 0.0F);
               this.field_70170_p.func_72838_d(var4);
            }
         }

         this.field_70170_p.func_72960_a(this, (byte)3);
         this.func_70106_y();
      }

   }
}
