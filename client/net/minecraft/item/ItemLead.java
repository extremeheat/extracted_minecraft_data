package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemLead extends Item {
   public ItemLead() {
      super();
      this.func_77637_a(CreativeTabs.field_78040_i);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      Block var9 = var3.func_180495_p(var4).func_177230_c();
      if (var9 instanceof BlockFence) {
         if (var3.field_72995_K) {
            return true;
         } else {
            func_180618_a(var2, var3, var4);
            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean func_180618_a(EntityPlayer var0, World var1, BlockPos var2) {
      EntityLeashKnot var3 = EntityLeashKnot.func_174863_b(var1, var2);
      boolean var4 = false;
      double var5 = 7.0D;
      int var7 = var2.func_177958_n();
      int var8 = var2.func_177956_o();
      int var9 = var2.func_177952_p();
      List var10 = var1.func_72872_a(EntityLiving.class, new AxisAlignedBB((double)var7 - var5, (double)var8 - var5, (double)var9 - var5, (double)var7 + var5, (double)var8 + var5, (double)var9 + var5));
      Iterator var11 = var10.iterator();

      while(var11.hasNext()) {
         EntityLiving var12 = (EntityLiving)var11.next();
         if (var12.func_110167_bD() && var12.func_110166_bE() == var0) {
            if (var3 == null) {
               var3 = EntityLeashKnot.func_174862_a(var1, var2);
            }

            var12.func_110162_b(var3, true);
            var4 = true;
         }
      }

      return var4;
   }
}
