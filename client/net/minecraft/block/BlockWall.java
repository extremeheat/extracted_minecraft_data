package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWall extends Block {
   public static final String[] field_150092_a = new String[]{"normal", "mossy"};

   public BlockWall(Block var1) {
      super(var1.field_149764_J);
      this.func_149711_c(var1.field_149782_v);
      this.func_149752_b(var1.field_149781_w / 3.0F);
      this.func_149672_a(var1.field_149762_H);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return var2 == 1 ? Blocks.field_150341_Y.func_149733_h(var1) : Blocks.field_150347_e.func_149733_h(var1);
   }

   @Override
   public int func_149645_b() {
      return 32;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public boolean func_149655_b(IBlockAccess var1, int var2, int var3, int var4) {
      return false;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      boolean var5 = this.func_150091_e(var1, var2, var3, var4 - 1);
      boolean var6 = this.func_150091_e(var1, var2, var3, var4 + 1);
      boolean var7 = this.func_150091_e(var1, var2 - 1, var3, var4);
      boolean var8 = this.func_150091_e(var1, var2 + 1, var3, var4);
      float var9 = 0.25F;
      float var10 = 0.75F;
      float var11 = 0.25F;
      float var12 = 0.75F;
      float var13 = 1.0F;
      if (var5) {
         var11 = 0.0F;
      }

      if (var6) {
         var12 = 1.0F;
      }

      if (var7) {
         var9 = 0.0F;
      }

      if (var8) {
         var10 = 1.0F;
      }

      if (var5 && var6 && !var7 && !var8) {
         var13 = 0.8125F;
         var9 = 0.3125F;
         var10 = 0.6875F;
      } else if (!var5 && !var6 && var7 && var8) {
         var13 = 0.8125F;
         var11 = 0.3125F;
         var12 = 0.6875F;
      }

      this.func_149676_a(var9, 0.0F, var11, var10, var13, var12);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      this.func_149719_a(var1, var2, var3, var4);
      this.field_149756_F = 1.5;
      return super.func_149668_a(var1, var2, var3, var4);
   }

   public boolean func_150091_e(IBlockAccess var1, int var2, int var3, int var4) {
      Block var5 = var1.func_147439_a(var2, var3, var4);
      if (var5 == this || var5 == Blocks.field_150396_be) {
         return true;
      } else if (var5.field_149764_J.func_76218_k() && var5.func_149686_d()) {
         return var5.field_149764_J != Material.field_151572_C;
      } else {
         return false;
      }
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
   }

   @Override
   public int func_149692_a(int var1) {
      return var1;
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return var5 == 0 ? super.func_149646_a(var1, var2, var3, var4, var5) : true;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
   }
}
