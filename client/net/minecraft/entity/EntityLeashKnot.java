package net.minecraft.entity;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityLeashKnot extends EntityHanging {
   public EntityLeashKnot(World var1) {
      super(var1);
   }

   public EntityLeashKnot(World var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, 0);
      this.func_70107_b((double)var2 + 0.5, (double)var3 + 0.5, (double)var4 + 0.5);
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
   }

   @Override
   public void func_82328_a(int var1) {
   }

   @Override
   public int func_82329_d() {
      return 9;
   }

   @Override
   public int func_82330_g() {
      return 9;
   }

   @Override
   public boolean func_70112_a(double var1) {
      return var1 < 1024.0;
   }

   @Override
   public void func_110128_b(Entity var1) {
   }

   @Override
   public boolean func_70039_c(NBTTagCompound var1) {
      return false;
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
   }

   @Override
   public boolean func_130002_c(EntityPlayer var1) {
      ItemStack var2 = var1.func_70694_bm();
      boolean var3 = false;
      if (var2 != null && var2.func_77973_b() == Items.field_151058_ca && !this.field_70170_p.field_72995_K) {
         double var4 = 7.0;
         List var6 = this.field_70170_p
            .func_72872_a(
               EntityLiving.class,
               AxisAlignedBB.func_72330_a(
                  this.field_70165_t - var4,
                  this.field_70163_u - var4,
                  this.field_70161_v - var4,
                  this.field_70165_t + var4,
                  this.field_70163_u + var4,
                  this.field_70161_v + var4
               )
            );
         if (var6 != null) {
            for(EntityLiving var8 : var6) {
               if (var8.func_110167_bD() && var8.func_110166_bE() == var1) {
                  var8.func_110162_b(this, true);
                  var3 = true;
               }
            }
         }
      }

      if (!this.field_70170_p.field_72995_K && !var3) {
         this.func_70106_y();
         if (var1.field_71075_bZ.field_75098_d) {
            double var9 = 7.0;
            List var10 = this.field_70170_p
               .func_72872_a(
                  EntityLiving.class,
                  AxisAlignedBB.func_72330_a(
                     this.field_70165_t - var9,
                     this.field_70163_u - var9,
                     this.field_70161_v - var9,
                     this.field_70165_t + var9,
                     this.field_70163_u + var9,
                     this.field_70161_v + var9
                  )
               );
            if (var10 != null) {
               for(EntityLiving var12 : var10) {
                  if (var12.func_110167_bD() && var12.func_110166_bE() == this) {
                     var12.func_110160_i(true, false);
                  }
               }
            }
         }
      }

      return true;
   }

   @Override
   public boolean func_70518_d() {
      return this.field_70170_p.func_147439_a(this.field_146063_b, this.field_146064_c, this.field_146062_d).func_149645_b() == 11;
   }

   public static EntityLeashKnot func_110129_a(World var0, int var1, int var2, int var3) {
      EntityLeashKnot var4 = new EntityLeashKnot(var0, var1, var2, var3);
      var4.field_98038_p = true;
      var0.func_72838_d(var4);
      return var4;
   }

   public static EntityLeashKnot func_110130_b(World var0, int var1, int var2, int var3) {
      List var4 = var0.func_72872_a(
         EntityLeashKnot.class,
         AxisAlignedBB.func_72330_a((double)var1 - 1.0, (double)var2 - 1.0, (double)var3 - 1.0, (double)var1 + 1.0, (double)var2 + 1.0, (double)var3 + 1.0)
      );
      if (var4 != null) {
         for(EntityLeashKnot var6 : var4) {
            if (var6.field_146063_b == var1 && var6.field_146064_c == var2 && var6.field_146062_d == var3) {
               return var6;
            }
         }
      }

      return null;
   }
}
