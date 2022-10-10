package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockFlowerPot extends Block {
   private static final Map<Block, Block> field_196451_b = Maps.newHashMap();
   protected static final VoxelShape field_196450_a = Block.func_208617_a(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
   private final Block field_196452_c;

   public BlockFlowerPot(Block var1, Block.Properties var2) {
      super(var2);
      this.field_196452_c = var1;
      field_196451_b.put(var1, this);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196450_a;
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.MODEL;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      ItemStack var10 = var4.func_184586_b(var5);
      Item var11 = var10.func_77973_b();
      Block var12 = var11 instanceof ItemBlock ? (Block)field_196451_b.getOrDefault(((ItemBlock)var11).func_179223_d(), Blocks.field_150350_a) : Blocks.field_150350_a;
      boolean var13 = var12 == Blocks.field_150350_a;
      boolean var14 = this.field_196452_c == Blocks.field_150350_a;
      if (var13 != var14) {
         if (var14) {
            var2.func_180501_a(var3, var12.func_176223_P(), 3);
            var4.func_195066_a(StatList.field_188088_V);
            if (!var4.field_71075_bZ.field_75098_d) {
               var10.func_190918_g(1);
            }
         } else {
            ItemStack var15 = new ItemStack(this.field_196452_c);
            if (var10.func_190926_b()) {
               var4.func_184611_a(var5, var15);
            } else if (!var4.func_191521_c(var15)) {
               var4.func_71019_a(var15, false);
            }

            var2.func_180501_a(var3, Blocks.field_150457_bL.func_176223_P(), 3);
         }
      }

      return true;
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return this.field_196452_c == Blocks.field_150350_a ? super.func_185473_a(var1, var2, var3) : new ItemStack(this.field_196452_c);
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Blocks.field_150457_bL;
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      super.func_196255_a(var1, var2, var3, var4, var5);
      if (this.field_196452_c != Blocks.field_150350_a) {
         func_180635_a(var2, var3, new ItemStack(this.field_196452_c));
      }

   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var2 == EnumFacing.DOWN && !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }
}
