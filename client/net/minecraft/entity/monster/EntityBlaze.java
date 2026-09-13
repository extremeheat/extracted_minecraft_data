package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBlaze extends EntityMob {
   private float field_70847_d = 0.5F;
   private int field_70848_e;
   private int field_70846_g;

   public EntityBlaze(World var1) {
      super(var1);
      this.field_70178_ae = true;
      this.field_70728_aV = 10;
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(6.0);
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, new Byte((byte)0));
   }

   @Override
   protected String func_70639_aQ() {
      return "mob.blaze.breathe";
   }

   @Override
   protected String func_70621_aR() {
      return "mob.blaze.hit";
   }

   @Override
   protected String func_70673_aS() {
      return "mob.blaze.death";
   }

   @Override
   public int func_70070_b(float var1) {
      return 15728880;
   }

   @Override
   public float func_70013_c(float var1) {
      return 1.0F;
   }

   @Override
   public void func_70636_d() {
      if (!this.field_70170_p.field_72995_K) {
         if (this.func_70026_G()) {
            this.func_70097_a(DamageSource.field_76369_e, 1.0F);
         }

         --this.field_70848_e;
         if (this.field_70848_e <= 0) {
            this.field_70848_e = 100;
            this.field_70847_d = 0.5F + (float)this.field_70146_Z.nextGaussian() * 3.0F;
         }

         if (this.func_70777_m() != null
            && this.func_70777_m().field_70163_u + (double)this.func_70777_m().func_70047_e()
               > this.field_70163_u + (double)this.func_70047_e() + (double)this.field_70847_d) {
            this.field_70181_x += (0.30000001192092896 - this.field_70181_x) * 0.30000001192092896;
         }
      }

      if (this.field_70146_Z.nextInt(24) == 0) {
         this.field_70170_p
            .func_72908_a(
               this.field_70165_t + 0.5,
               this.field_70163_u + 0.5,
               this.field_70161_v + 0.5,
               "fire.fire",
               1.0F + this.field_70146_Z.nextFloat(),
               this.field_70146_Z.nextFloat() * 0.7F + 0.3F
            );
      }

      if (!this.field_70122_E && this.field_70181_x < 0.0) {
         this.field_70181_x *= 0.6;
      }

      for(int var1 = 0; var1 < 2; ++var1) {
         this.field_70170_p
            .func_72869_a(
               "largesmoke",
               this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5) * (double)this.field_70130_N,
               this.field_70163_u + this.field_70146_Z.nextDouble() * (double)this.field_70131_O,
               this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5) * (double)this.field_70130_N,
               0.0,
               0.0,
               0.0
            );
      }

      super.func_70636_d();
   }

   @Override
   protected void func_70785_a(Entity var1, float var2) {
      if (this.field_70724_aR <= 0
         && var2 < 2.0F
         && var1.field_70121_D.field_72337_e > this.field_70121_D.field_72338_b
         && var1.field_70121_D.field_72338_b < this.field_70121_D.field_72337_e) {
         this.field_70724_aR = 20;
         this.func_70652_k(var1);
      } else if (var2 < 30.0F) {
         double var3 = var1.field_70165_t - this.field_70165_t;
         double var5 = var1.field_70121_D.field_72338_b + (double)(var1.field_70131_O / 2.0F) - (this.field_70163_u + (double)(this.field_70131_O / 2.0F));
         double var7 = var1.field_70161_v - this.field_70161_v;
         if (this.field_70724_aR == 0) {
            ++this.field_70846_g;
            if (this.field_70846_g == 1) {
               this.field_70724_aR = 60;
               this.func_70844_e(true);
            } else if (this.field_70846_g <= 4) {
               this.field_70724_aR = 6;
            } else {
               this.field_70724_aR = 100;
               this.field_70846_g = 0;
               this.func_70844_e(false);
            }

            if (this.field_70846_g > 1) {
               float var9 = MathHelper.func_76129_c(var2) * 0.5F;
               this.field_70170_p.func_72889_a(null, 1009, (int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v, 0);

               for(int var10 = 0; var10 < 1; ++var10) {
                  EntitySmallFireball var11 = new EntitySmallFireball(
                     this.field_70170_p,
                     this,
                     var3 + this.field_70146_Z.nextGaussian() * (double)var9,
                     var5,
                     var7 + this.field_70146_Z.nextGaussian() * (double)var9
                  );
                  var11.field_70163_u = this.field_70163_u + (double)(this.field_70131_O / 2.0F) + 0.5;
                  this.field_70170_p.func_72838_d(var11);
               }
            }
         }

         this.field_70177_z = (float)(Math.atan2(var7, var3) * 180.0 / 3.1415927410125732) - 90.0F;
         this.field_70787_b = true;
      }
   }

   @Override
   protected void func_70069_a(float var1) {
   }

   @Override
   protected Item func_146068_u() {
      return Items.field_151072_bj;
   }

   @Override
   public boolean func_70027_ad() {
      return this.func_70845_n();
   }

   @Override
   protected void func_70628_a(boolean var1, int var2) {
      if (var1) {
         int var3 = this.field_70146_Z.nextInt(2 + var2);

         for(int var4 = 0; var4 < var3; ++var4) {
            this.func_145779_a(Items.field_151072_bj, 1);
         }
      }
   }

   public boolean func_70845_n() {
      return (this.field_70180_af.func_75683_a(16) & 1) != 0;
   }

   public void func_70844_e(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(16);
      if (var1) {
         var2 = (byte)(var2 | 1);
      } else {
         var2 = (byte)(var2 & -2);
      }

      this.field_70180_af.func_75692_b(16, var2);
   }

   @Override
   protected boolean func_70814_o() {
      return true;
   }
}
