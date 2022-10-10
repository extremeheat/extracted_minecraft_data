package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockDeadBush extends BlockBush {
   protected static final VoxelShape field_196397_a = Block.func_208617_a(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

   protected BlockDeadBush(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196397_a;
   }

   protected boolean func_200014_a_(IBlockState var1, IBlockReader var2, BlockPos var3) {
      Block var4 = var1.func_177230_c();
      return var4 == Blocks.field_150354_m || var4 == Blocks.field_196611_F || var4 == Blocks.field_150405_ch || var4 == Blocks.field_196777_fo || var4 == Blocks.field_196778_fp || var4 == Blocks.field_196780_fq || var4 == Blocks.field_196782_fr || var4 == Blocks.field_196783_fs || var4 == Blocks.field_196785_ft || var4 == Blocks.field_196787_fu || var4 == Blocks.field_196789_fv || var4 == Blocks.field_196791_fw || var4 == Blocks.field_196793_fx || var4 == Blocks.field_196795_fy || var4 == Blocks.field_196797_fz || var4 == Blocks.field_196719_fA || var4 == Blocks.field_196720_fB || var4 == Blocks.field_196721_fC || var4 == Blocks.field_196722_fD || var4 == Blocks.field_150346_d || var4 == Blocks.field_196660_k || var4 == Blocks.field_196661_l;
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return var2.nextInt(3);
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_151055_y;
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      boolean var7 = !var1.field_72995_K && var6.func_77973_b() == Items.field_151097_aZ;
      if (var7) {
         func_180635_a(var1, var3, new ItemStack(Blocks.field_196555_aI));
      }

      super.func_180657_a(var1, var2, var3, var7 ? Blocks.field_150350_a.func_176223_P() : var4, var5, var6);
   }
}
