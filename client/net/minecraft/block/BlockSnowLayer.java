package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockSnowLayer extends Block {
   public static final IntegerProperty field_176315_a;
   protected static final VoxelShape[] field_196508_b;

   protected BlockSnowLayer(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176315_a, 1));
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      switch(var4) {
      case LAND:
         return (Integer)var1.func_177229_b(field_176315_a) < 5;
      case WATER:
         return false;
      case AIR:
         return false;
      default:
         return false;
      }
   }

   public boolean func_149686_d(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176315_a) == 8;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return var4 == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196508_b[(Integer)var1.func_177229_b(field_176315_a)];
   }

   public VoxelShape func_196268_f(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196508_b[(Integer)var1.func_177229_b(field_176315_a) - 1];
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      IBlockState var4 = var2.func_180495_p(var3.func_177977_b());
      Block var5 = var4.func_177230_c();
      if (var5 != Blocks.field_150432_aD && var5 != Blocks.field_150403_cj && var5 != Blocks.field_180401_cv) {
         BlockFaceShape var6 = var4.func_193401_d(var2, var3.func_177977_b(), EnumFacing.UP);
         return var6 == BlockFaceShape.SOLID || var4.func_203425_a(BlockTags.field_206952_E) || var5 == this && (Integer)var4.func_177229_b(field_176315_a) == 8;
      } else {
         return false;
      }
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      Integer var7 = (Integer)var4.func_177229_b(field_176315_a);
      if (this.func_149700_E() && EnchantmentHelper.func_77506_a(Enchantments.field_185306_r, var6) > 0) {
         if (var7 == 8) {
            func_180635_a(var1, var3, new ItemStack(Blocks.field_196604_cC));
         } else {
            for(int var8 = 0; var8 < var7; ++var8) {
               func_180635_a(var1, var3, this.func_180643_i(var4));
            }
         }
      } else {
         func_180635_a(var1, var3, new ItemStack(Items.field_151126_ay, var7));
      }

      var1.func_175698_g(var3);
      var2.func_71029_a(StatList.field_188065_ae.func_199076_b(this));
      var2.func_71020_j(0.005F);
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_190931_a;
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (var2.func_175642_b(EnumLightType.BLOCK, var3) > 11) {
         var1.func_196949_c(var2, var3, 0);
         var2.func_175698_g(var3);
      }

   }

   public boolean func_196253_a(IBlockState var1, BlockItemUseContext var2) {
      int var3 = (Integer)var1.func_177229_b(field_176315_a);
      if (var2.func_195996_i().func_77973_b() == this.func_199767_j() && var3 < 8) {
         if (var2.func_196012_c()) {
            return var2.func_196000_l() == EnumFacing.UP;
         } else {
            return true;
         }
      } else {
         return var3 == 1;
      }
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = var1.func_195991_k().func_180495_p(var1.func_195995_a());
      if (var2.func_177230_c() == this) {
         int var3 = (Integer)var2.func_177229_b(field_176315_a);
         return (IBlockState)var2.func_206870_a(field_176315_a, Math.min(8, var3 + 1));
      } else {
         return super.func_196258_a(var1);
      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176315_a);
   }

   protected boolean func_149700_E() {
      return true;
   }

   static {
      field_176315_a = BlockStateProperties.field_208129_ad;
      field_196508_b = new VoxelShape[]{VoxelShapes.func_197880_a(), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
   }
}
