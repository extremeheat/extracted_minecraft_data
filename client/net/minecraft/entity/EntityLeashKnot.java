package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockFence;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityLeashKnot extends EntityHanging {
   public EntityLeashKnot(World var1) {
      super(var1);
   }

   public EntityLeashKnot(World var1, BlockPos var2) {
      super(var1, var2);
      this.func_70107_b((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D);
      float var3 = 0.125F;
      float var4 = 0.1875F;
      float var5 = 0.25F;
      this.func_174826_a(new AxisAlignedBB(this.field_70165_t - 0.1875D, this.field_70163_u - 0.25D + 0.125D, this.field_70161_v - 0.1875D, this.field_70165_t + 0.1875D, this.field_70163_u + 0.25D + 0.125D, this.field_70161_v + 0.1875D));
   }

   protected void func_70088_a() {
      super.func_70088_a();
   }

   public void func_174859_a(EnumFacing var1) {
   }

   public int func_82329_d() {
      return 9;
   }

   public int func_82330_g() {
      return 9;
   }

   public float func_70047_e() {
      return -0.0625F;
   }

   public boolean func_70112_a(double var1) {
      return var1 < 1024.0D;
   }

   public void func_110128_b(Entity var1) {
   }

   public boolean func_70039_c(NBTTagCompound var1) {
      return false;
   }

   public void func_70014_b(NBTTagCompound var1) {
   }

   public void func_70037_a(NBTTagCompound var1) {
   }

   public boolean func_130002_c(EntityPlayer var1) {
      ItemStack var2 = var1.func_70694_bm();
      boolean var3 = false;
      double var4;
      List var6;
      Iterator var7;
      EntityLiving var8;
      if (var2 != null && var2.func_77973_b() == Items.field_151058_ca && !this.field_70170_p.field_72995_K) {
         var4 = 7.0D;
         var6 = this.field_70170_p.func_72872_a(EntityLiving.class, new AxisAlignedBB(this.field_70165_t - var4, this.field_70163_u - var4, this.field_70161_v - var4, this.field_70165_t + var4, this.field_70163_u + var4, this.field_70161_v + var4));
         var7 = var6.iterator();

         while(var7.hasNext()) {
            var8 = (EntityLiving)var7.next();
            if (var8.func_110167_bD() && var8.func_110166_bE() == var1) {
               var8.func_110162_b(this, true);
               var3 = true;
            }
         }
      }

      if (!this.field_70170_p.field_72995_K && !var3) {
         this.func_70106_y();
         if (var1.field_71075_bZ.field_75098_d) {
            var4 = 7.0D;
            var6 = this.field_70170_p.func_72872_a(EntityLiving.class, new AxisAlignedBB(this.field_70165_t - var4, this.field_70163_u - var4, this.field_70161_v - var4, this.field_70165_t + var4, this.field_70163_u + var4, this.field_70161_v + var4));
            var7 = var6.iterator();

            while(var7.hasNext()) {
               var8 = (EntityLiving)var7.next();
               if (var8.func_110167_bD() && var8.func_110166_bE() == this) {
                  var8.func_110160_i(true, false);
               }
            }
         }
      }

      return true;
   }

   public boolean func_70518_d() {
      return this.field_70170_p.func_180495_p(this.field_174861_a).func_177230_c() instanceof BlockFence;
   }

   public static EntityLeashKnot func_174862_a(World var0, BlockPos var1) {
      EntityLeashKnot var2 = new EntityLeashKnot(var0, var1);
      var2.field_98038_p = true;
      var0.func_72838_d(var2);
      return var2;
   }

   public static EntityLeashKnot func_174863_b(World var0, BlockPos var1) {
      int var2 = var1.func_177958_n();
      int var3 = var1.func_177956_o();
      int var4 = var1.func_177952_p();
      List var5 = var0.func_72872_a(EntityLeashKnot.class, new AxisAlignedBB((double)var2 - 1.0D, (double)var3 - 1.0D, (double)var4 - 1.0D, (double)var2 + 1.0D, (double)var3 + 1.0D, (double)var4 + 1.0D));
      Iterator var6 = var5.iterator();

      EntityLeashKnot var7;
      do {
         if (!var6.hasNext()) {
            return null;
         }

         var7 = (EntityLeashKnot)var6.next();
      } while(!var7.func_174857_n().equals(var1));

      return var7;
   }
}
