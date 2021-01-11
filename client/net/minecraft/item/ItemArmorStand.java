package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Rotations;
import net.minecraft.world.World;

public class ItemArmorStand extends Item {
   public ItemArmorStand() {
      super();
      this.func_77637_a(CreativeTabs.field_78031_c);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var5 == EnumFacing.DOWN) {
         return false;
      } else {
         boolean var9 = var3.func_180495_p(var4).func_177230_c().func_176200_f(var3, var4);
         BlockPos var10 = var9 ? var4 : var4.func_177972_a(var5);
         if (!var2.func_175151_a(var10, var5, var1)) {
            return false;
         } else {
            BlockPos var11 = var10.func_177984_a();
            boolean var12 = !var3.func_175623_d(var10) && !var3.func_180495_p(var10).func_177230_c().func_176200_f(var3, var10);
            var12 |= !var3.func_175623_d(var11) && !var3.func_180495_p(var11).func_177230_c().func_176200_f(var3, var11);
            if (var12) {
               return false;
            } else {
               double var13 = (double)var10.func_177958_n();
               double var15 = (double)var10.func_177956_o();
               double var17 = (double)var10.func_177952_p();
               List var19 = var3.func_72839_b((Entity)null, AxisAlignedBB.func_178781_a(var13, var15, var17, var13 + 1.0D, var15 + 2.0D, var17 + 1.0D));
               if (var19.size() > 0) {
                  return false;
               } else {
                  if (!var3.field_72995_K) {
                     var3.func_175698_g(var10);
                     var3.func_175698_g(var11);
                     EntityArmorStand var20 = new EntityArmorStand(var3, var13 + 0.5D, var15, var17 + 0.5D);
                     float var21 = (float)MathHelper.func_76141_d((MathHelper.func_76142_g(var2.field_70177_z - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                     var20.func_70012_b(var13 + 0.5D, var15, var17 + 0.5D, var21, 0.0F);
                     this.func_179221_a(var20, var3.field_73012_v);
                     NBTTagCompound var22 = var1.func_77978_p();
                     if (var22 != null && var22.func_150297_b("EntityTag", 10)) {
                        NBTTagCompound var23 = new NBTTagCompound();
                        var20.func_70039_c(var23);
                        var23.func_179237_a(var22.func_74775_l("EntityTag"));
                        var20.func_70020_e(var23);
                     }

                     var3.func_72838_d(var20);
                  }

                  --var1.field_77994_a;
                  return true;
               }
            }
         }
      }
   }

   private void func_179221_a(EntityArmorStand var1, Random var2) {
      Rotations var3 = var1.func_175418_s();
      float var5 = var2.nextFloat() * 5.0F;
      float var6 = var2.nextFloat() * 20.0F - 10.0F;
      Rotations var4 = new Rotations(var3.func_179415_b() + var5, var3.func_179416_c() + var6, var3.func_179413_d());
      var1.func_175415_a(var4);
      var3 = var1.func_175408_t();
      var5 = var2.nextFloat() * 10.0F - 5.0F;
      var4 = new Rotations(var3.func_179415_b(), var3.func_179416_c() + var5, var3.func_179413_d());
      var1.func_175424_b(var4);
   }
}
