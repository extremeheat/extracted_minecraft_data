package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemLead extends Item {
   public ItemLead(Item.Properties var1) {
      super(var1);
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      Block var4 = var2.func_180495_p(var3).func_177230_c();
      if (var4 instanceof BlockFence) {
         EntityPlayer var5 = var1.func_195999_j();
         if (!var2.field_72995_K && var5 != null) {
            func_180618_a(var5, var2, var3);
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }

   public static boolean func_180618_a(EntityPlayer var0, World var1, BlockPos var2) {
      EntityLeashKnot var3 = EntityLeashKnot.func_174863_b(var1, var2);
      boolean var4 = false;
      double var5 = 7.0D;
      int var7 = var2.func_177958_n();
      int var8 = var2.func_177956_o();
      int var9 = var2.func_177952_p();
      List var10 = var1.func_72872_a(EntityLiving.class, new AxisAlignedBB((double)var7 - 7.0D, (double)var8 - 7.0D, (double)var9 - 7.0D, (double)var7 + 7.0D, (double)var8 + 7.0D, (double)var9 + 7.0D));
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
