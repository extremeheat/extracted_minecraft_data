package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDoor extends Item {
   private Block field_179236_a;

   public ItemDoor(Block var1) {
      super();
      this.field_179236_a = var1;
      this.func_77637_a(CreativeTabs.field_78028_d);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var5 != EnumFacing.UP) {
         return false;
      } else {
         IBlockState var9 = var3.func_180495_p(var4);
         Block var10 = var9.func_177230_c();
         if (!var10.func_176200_f(var3, var4)) {
            var4 = var4.func_177972_a(var5);
         }

         if (!var2.func_175151_a(var4, var5, var1)) {
            return false;
         } else if (!this.field_179236_a.func_176196_c(var3, var4)) {
            return false;
         } else {
            func_179235_a(var3, var4, EnumFacing.func_176733_a((double)var2.field_70177_z), this.field_179236_a);
            --var1.field_77994_a;
            return true;
         }
      }
   }

   public static void func_179235_a(World var0, BlockPos var1, EnumFacing var2, Block var3) {
      BlockPos var4 = var1.func_177972_a(var2.func_176746_e());
      BlockPos var5 = var1.func_177972_a(var2.func_176735_f());
      int var6 = (var0.func_180495_p(var5).func_177230_c().func_149721_r() ? 1 : 0) + (var0.func_180495_p(var5.func_177984_a()).func_177230_c().func_149721_r() ? 1 : 0);
      int var7 = (var0.func_180495_p(var4).func_177230_c().func_149721_r() ? 1 : 0) + (var0.func_180495_p(var4.func_177984_a()).func_177230_c().func_149721_r() ? 1 : 0);
      boolean var8 = var0.func_180495_p(var5).func_177230_c() == var3 || var0.func_180495_p(var5.func_177984_a()).func_177230_c() == var3;
      boolean var9 = var0.func_180495_p(var4).func_177230_c() == var3 || var0.func_180495_p(var4.func_177984_a()).func_177230_c() == var3;
      boolean var10 = false;
      if (var8 && !var9 || var7 > var6) {
         var10 = true;
      }

      BlockPos var11 = var1.func_177984_a();
      IBlockState var12 = var3.func_176223_P().func_177226_a(BlockDoor.field_176520_a, var2).func_177226_a(BlockDoor.field_176521_M, var10 ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT);
      var0.func_180501_a(var1, var12.func_177226_a(BlockDoor.field_176523_O, BlockDoor.EnumDoorHalf.LOWER), 2);
      var0.func_180501_a(var11, var12.func_177226_a(BlockDoor.field_176523_O, BlockDoor.EnumDoorHalf.UPPER), 2);
      var0.func_175685_c(var1, var3);
      var0.func_175685_c(var11, var3);
   }
}
