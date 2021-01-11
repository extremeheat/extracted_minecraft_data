package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockCommandBlock extends BlockContainer {
   public static final PropertyBool field_176452_a = PropertyBool.func_177716_a("triggered");

   public BlockCommandBlock() {
      super(Material.field_151573_f, MapColor.field_151676_q);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176452_a, false));
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityCommandBlock();
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.field_72995_K) {
         boolean var5 = var1.func_175640_z(var2);
         boolean var6 = (Boolean)var3.func_177229_b(field_176452_a);
         if (var5 && !var6) {
            var1.func_180501_a(var2, var3.func_177226_a(field_176452_a, true), 4);
            var1.func_175684_a(var2, this, this.func_149738_a(var1));
         } else if (!var5 && var6) {
            var1.func_180501_a(var2, var3.func_177226_a(field_176452_a, false), 4);
         }
      }

   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      TileEntity var5 = var1.func_175625_s(var2);
      if (var5 instanceof TileEntityCommandBlock) {
         ((TileEntityCommandBlock)var5).func_145993_a().func_145755_a(var1);
         var1.func_175666_e(var2, this);
      }

   }

   public int func_149738_a(World var1) {
      return 1;
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      TileEntity var9 = var1.func_175625_s(var2);
      return var9 instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)var9).func_145993_a().func_175574_a(var4) : false;
   }

   public boolean func_149740_M() {
      return true;
   }

   public int func_180641_l(World var1, BlockPos var2) {
      TileEntity var3 = var1.func_175625_s(var2);
      return var3 instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)var3).func_145993_a().func_145760_g() : 0;
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      TileEntity var6 = var1.func_175625_s(var2);
      if (var6 instanceof TileEntityCommandBlock) {
         CommandBlockLogic var7 = ((TileEntityCommandBlock)var6).func_145993_a();
         if (var5.func_82837_s()) {
            var7.func_145754_b(var5.func_82833_r());
         }

         if (!var1.field_72995_K) {
            var7.func_175573_a(var1.func_82736_K().func_82766_b("sendCommandFeedback"));
         }

      }
   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   public int func_149645_b() {
      return 3;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176452_a, (var1 & 1) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      int var2 = 0;
      if ((Boolean)var1.func_177229_b(field_176452_a)) {
         var2 |= 1;
      }

      return var2;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176452_a});
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176223_P().func_177226_a(field_176452_a, false);
   }
}
