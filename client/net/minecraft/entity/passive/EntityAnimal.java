package net.minecraft.entity.passive;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAnimal extends EntityAgeable implements IAnimals {
   private int field_70881_d;
   private int field_70882_e;
   private EntityPlayer field_146084_br;

   public EntityAnimal(World var1) {
      super(var1);
   }

   @Override
   protected void func_70629_bd() {
      if (this.func_70874_b() != 0) {
         this.field_70881_d = 0;
      }

      super.func_70629_bd();
   }

   @Override
   public void func_70636_d() {
      super.func_70636_d();
      if (this.func_70874_b() != 0) {
         this.field_70881_d = 0;
      }

      if (this.field_70881_d > 0) {
         --this.field_70881_d;
         String var1 = "heart";
         if (this.field_70881_d % 10 == 0) {
            double var2 = this.field_70146_Z.nextGaussian() * 0.02;
            double var4 = this.field_70146_Z.nextGaussian() * 0.02;
            double var6 = this.field_70146_Z.nextGaussian() * 0.02;
            this.field_70170_p
               .func_72869_a(
                  var1,
                  this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N,
                  this.field_70163_u + 0.5 + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O),
                  this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N,
                  var2,
                  var4,
                  var6
               );
         }
      } else {
         this.field_70882_e = 0;
      }
   }

   @Override
   protected void func_70785_a(Entity var1, float var2) {
      if (var1 instanceof EntityPlayer) {
         if (var2 < 3.0F) {
            double var3 = var1.field_70165_t - this.field_70165_t;
            double var5 = var1.field_70161_v - this.field_70161_v;
            this.field_70177_z = (float)(Math.atan2(var5, var3) * 180.0 / 3.1415927410125732) - 90.0F;
            this.field_70787_b = true;
         }

         EntityPlayer var7 = (EntityPlayer)var1;
         if (var7.func_71045_bC() == null || !this.func_70877_b(var7.func_71045_bC())) {
            this.field_70789_a = null;
         }
      } else if (var1 instanceof EntityAnimal) {
         EntityAnimal var8 = (EntityAnimal)var1;
         if (this.func_70874_b() > 0 && var8.func_70874_b() < 0) {
            if ((double)var2 < 2.5) {
               this.field_70787_b = true;
            }
         } else if (this.field_70881_d > 0 && var8.field_70881_d > 0) {
            if (var8.field_70789_a == null) {
               var8.field_70789_a = this;
            }

            if (var8.field_70789_a == this && (double)var2 < 3.5) {
               ++var8.field_70881_d;
               ++this.field_70881_d;
               ++this.field_70882_e;
               if (this.field_70882_e % 4 == 0) {
                  this.field_70170_p
                     .func_72869_a(
                        "heart",
                        this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N,
                        this.field_70163_u + 0.5 + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O),
                        this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N,
                        0.0,
                        0.0,
                        0.0
                     );
               }

               if (this.field_70882_e == 60) {
                  this.func_70876_c((EntityAnimal)var1);
               }
            } else {
               this.field_70882_e = 0;
            }
         } else {
            this.field_70882_e = 0;
            this.field_70789_a = null;
         }
      }
   }

   private void func_70876_c(EntityAnimal var1) {
      EntityAgeable var2 = this.func_90011_a(var1);
      if (var2 != null) {
         if (this.field_146084_br == null && var1.func_146083_cb() != null) {
            this.field_146084_br = var1.func_146083_cb();
         }

         if (this.field_146084_br != null) {
            this.field_146084_br.func_71029_a(StatList.field_151186_x);
            if (this instanceof EntityCow) {
               this.field_146084_br.func_71029_a(AchievementList.field_150962_H);
            }
         }

         this.func_70873_a(6000);
         var1.func_70873_a(6000);
         this.field_70881_d = 0;
         this.field_70882_e = 0;
         this.field_70789_a = null;
         var1.field_70789_a = null;
         var1.field_70882_e = 0;
         var1.field_70881_d = 0;
         var2.func_70873_a(-24000);
         var2.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);

         for(int var3 = 0; var3 < 7; ++var3) {
            double var4 = this.field_70146_Z.nextGaussian() * 0.02;
            double var6 = this.field_70146_Z.nextGaussian() * 0.02;
            double var8 = this.field_70146_Z.nextGaussian() * 0.02;
            this.field_70170_p
               .func_72869_a(
                  "heart",
                  this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N,
                  this.field_70163_u + 0.5 + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O),
                  this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N,
                  var4,
                  var6,
                  var8
               );
         }

         this.field_70170_p.func_72838_d(var2);
      }
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_85032_ar()) {
         return false;
      } else {
         this.field_70788_c = 60;
         if (!this.func_70650_aV()) {
            IAttributeInstance var3 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
            if (var3.func_111127_a(field_110179_h) == null) {
               var3.func_111121_a(field_110181_i);
            }
         }

         this.field_70789_a = null;
         this.field_70881_d = 0;
         return super.func_70097_a(var1, var2);
      }
   }

   @Override
   public float func_70783_a(int var1, int var2, int var3) {
      return this.field_70170_p.func_147439_a(var1, var2 - 1, var3) == Blocks.field_150349_c
         ? 10.0F
         : this.field_70170_p.func_72801_o(var1, var2, var3) - 0.5F;
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("InLove", this.field_70881_d);
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_70881_d = var1.func_74762_e("InLove");
   }

   @Override
   protected Entity func_70782_k() {
      if (this.field_70788_c > 0) {
         return null;
      } else {
         float var1 = 8.0F;
         if (this.field_70881_d > 0) {
            List var2 = this.field_70170_p.func_72872_a(this.getClass(), this.field_70121_D.func_72314_b((double)var1, (double)var1, (double)var1));

            for(int var3 = 0; var3 < var2.size(); ++var3) {
               EntityAnimal var4 = (EntityAnimal)var2.get(var3);
               if (var4 != this && var4.field_70881_d > 0) {
                  return var4;
               }
            }
         } else if (this.func_70874_b() == 0) {
            List var5 = this.field_70170_p.func_72872_a(EntityPlayer.class, this.field_70121_D.func_72314_b((double)var1, (double)var1, (double)var1));

            for(int var7 = 0; var7 < var5.size(); ++var7) {
               EntityPlayer var9 = (EntityPlayer)var5.get(var7);
               if (var9.func_71045_bC() != null && this.func_70877_b(var9.func_71045_bC())) {
                  return var9;
               }
            }
         } else if (this.func_70874_b() > 0) {
            List var6 = this.field_70170_p.func_72872_a(this.getClass(), this.field_70121_D.func_72314_b((double)var1, (double)var1, (double)var1));

            for(int var8 = 0; var8 < var6.size(); ++var8) {
               EntityAnimal var10 = (EntityAnimal)var6.get(var8);
               if (var10 != this && var10.func_70874_b() < 0) {
                  return var10;
               }
            }
         }

         return null;
      }
   }

   @Override
   public boolean func_70601_bi() {
      int var1 = MathHelper.func_76128_c(this.field_70165_t);
      int var2 = MathHelper.func_76128_c(this.field_70121_D.field_72338_b);
      int var3 = MathHelper.func_76128_c(this.field_70161_v);
      return this.field_70170_p.func_147439_a(var1, var2 - 1, var3) == Blocks.field_150349_c
         && this.field_70170_p.func_72883_k(var1, var2, var3) > 8
         && super.func_70601_bi();
   }

   @Override
   public int func_70627_aG() {
      return 120;
   }

   @Override
   protected boolean func_70692_ba() {
      return false;
   }

   @Override
   protected int func_70693_a(EntityPlayer var1) {
      return 1 + this.field_70170_p.field_73012_v.nextInt(3);
   }

   public boolean func_70877_b(ItemStack var1) {
      return var1.func_77973_b() == Items.field_151015_O;
   }

   @Override
   public boolean func_70085_c(EntityPlayer var1) {
      ItemStack var2 = var1.field_71071_by.func_70448_g();
      if (var2 != null && this.func_70877_b(var2) && this.func_70874_b() == 0 && this.field_70881_d <= 0) {
         if (!var1.field_71075_bZ.field_75098_d) {
            --var2.field_77994_a;
            if (var2.field_77994_a <= 0) {
               var1.field_71071_by.func_70299_a(var1.field_71071_by.field_70461_c, null);
            }
         }

         this.func_146082_f(var1);
         return true;
      } else {
         return super.func_70085_c(var1);
      }
   }

   public void func_146082_f(EntityPlayer var1) {
      this.field_70881_d = 600;
      this.field_146084_br = var1;
      this.field_70789_a = null;
      this.field_70170_p.func_72960_a(this, (byte)18);
   }

   public EntityPlayer func_146083_cb() {
      return this.field_146084_br;
   }

   public boolean func_70880_s() {
      return this.field_70881_d > 0;
   }

   public void func_70875_t() {
      this.field_70881_d = 0;
   }

   public boolean func_70878_b(EntityAnimal var1) {
      if (var1 == this) {
         return false;
      } else if (var1.getClass() != this.getClass()) {
         return false;
      } else {
         return this.func_70880_s() && var1.func_70880_s();
      }
   }

   @Override
   public void func_70103_a(byte var1) {
      if (var1 == 18) {
         for(int var2 = 0; var2 < 7; ++var2) {
            double var3 = this.field_70146_Z.nextGaussian() * 0.02;
            double var5 = this.field_70146_Z.nextGaussian() * 0.02;
            double var7 = this.field_70146_Z.nextGaussian() * 0.02;
            this.field_70170_p
               .func_72869_a(
                  "heart",
                  this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N,
                  this.field_70163_u + 0.5 + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O),
                  this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N,
                  var3,
                  var5,
                  var7
               );
         }
      } else {
         super.func_70103_a(var1);
      }
   }
}
