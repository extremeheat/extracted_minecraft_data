package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockPistonExtension extends BlockDirectional {
   public static final EnumProperty<PistonType> field_176325_b;
   public static final BooleanProperty field_176327_M;
   protected static final VoxelShape field_185635_c;
   protected static final VoxelShape field_185637_d;
   protected static final VoxelShape field_185639_e;
   protected static final VoxelShape field_185641_f;
   protected static final VoxelShape field_185643_g;
   protected static final VoxelShape field_185634_B;
   protected static final VoxelShape field_185636_C;
   protected static final VoxelShape field_185638_D;
   protected static final VoxelShape field_185640_E;
   protected static final VoxelShape field_185642_F;
   protected static final VoxelShape field_185644_G;
   protected static final VoxelShape field_185645_I;
   protected static final VoxelShape field_190964_J;
   protected static final VoxelShape field_190965_K;
   protected static final VoxelShape field_190966_L;
   protected static final VoxelShape field_190967_M;
   protected static final VoxelShape field_190968_N;
   protected static final VoxelShape field_190969_O;

   public BlockPistonExtension(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176387_N, EnumFacing.NORTH)).func_206870_a(field_176325_b, PistonType.DEFAULT)).func_206870_a(field_176327_M, false));
   }

   private VoxelShape func_196424_i(IBlockState var1) {
      switch((EnumFacing)var1.func_177229_b(field_176387_N)) {
      case DOWN:
      default:
         return field_185634_B;
      case UP:
         return field_185643_g;
      case NORTH:
         return field_185641_f;
      case SOUTH:
         return field_185639_e;
      case WEST:
         return field_185637_d;
      case EAST:
         return field_185635_c;
      }
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return VoxelShapes.func_197872_a(this.func_196424_i(var1), this.func_196425_x(var1));
   }

   private VoxelShape func_196425_x(IBlockState var1) {
      boolean var2 = (Boolean)var1.func_177229_b(field_176327_M);
      switch((EnumFacing)var1.func_177229_b(field_176387_N)) {
      case DOWN:
      default:
         return var2 ? field_190965_K : field_185638_D;
      case UP:
         return var2 ? field_190964_J : field_185636_C;
      case NORTH:
         return var2 ? field_190967_M : field_185642_F;
      case SOUTH:
         return var2 ? field_190966_L : field_185640_E;
      case WEST:
         return var2 ? field_190969_O : field_185645_I;
      case EAST:
         return var2 ? field_190968_N : field_185644_G;
      }
   }

   public boolean func_185481_k(IBlockState var1) {
      return var1.func_177229_b(field_176387_N) == EnumFacing.UP;
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (!var1.field_72995_K && var4.field_71075_bZ.field_75098_d) {
         BlockPos var5 = var2.func_177972_a(((EnumFacing)var3.func_177229_b(field_176387_N)).func_176734_d());
         Block var6 = var1.func_180495_p(var5).func_177230_c();
         if (var6 == Blocks.field_150331_J || var6 == Blocks.field_150320_F) {
            var1.func_175698_g(var5);
         }
      }

      super.func_176208_a(var1, var2, var3, var4);
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         super.func_196243_a(var1, var2, var3, var4, var5);
         EnumFacing var6 = ((EnumFacing)var1.func_177229_b(field_176387_N)).func_176734_d();
         var3 = var3.func_177972_a(var6);
         IBlockState var7 = var2.func_180495_p(var3);
         if ((var7.func_177230_c() == Blocks.field_150331_J || var7.func_177230_c() == Blocks.field_150320_F) && (Boolean)var7.func_177229_b(BlockPistonBase.field_176320_b)) {
            var7.func_196949_c(var2, var3, 0);
            var2.func_175698_g(var3);
         }

      }
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 0;
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var2.func_176734_d() == var1.func_177229_b(field_176387_N) && !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      Block var4 = var2.func_180495_p(var3.func_177972_a(((EnumFacing)var1.func_177229_b(field_176387_N)).func_176734_d())).func_177230_c();
      return var4 == Blocks.field_150331_J || var4 == Blocks.field_150320_F || var4 == Blocks.field_196603_bb;
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      if (var1.func_196955_c(var2, var3)) {
         BlockPos var6 = var3.func_177972_a(((EnumFacing)var1.func_177229_b(field_176387_N)).func_176734_d());
         var2.func_180495_p(var6).func_189546_a(var2, var6, var4, var5);
      }

   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return new ItemStack(var3.func_177229_b(field_176325_b) == PistonType.STICKY ? Blocks.field_150320_F : Blocks.field_150331_J);
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176387_N, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176387_N)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_176387_N)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176387_N, field_176325_b, field_176327_M);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return var4 == var2.func_177229_b(field_176387_N) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176325_b = BlockStateProperties.field_208144_as;
      field_176327_M = BlockStateProperties.field_208195_v;
      field_185635_c = Block.func_208617_a(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      field_185637_d = Block.func_208617_a(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
      field_185639_e = Block.func_208617_a(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
      field_185641_f = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
      field_185643_g = Block.func_208617_a(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      field_185634_B = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
      field_185636_C = Block.func_208617_a(6.0D, -4.0D, 6.0D, 10.0D, 12.0D, 10.0D);
      field_185638_D = Block.func_208617_a(6.0D, 4.0D, 6.0D, 10.0D, 20.0D, 10.0D);
      field_185640_E = Block.func_208617_a(6.0D, 6.0D, -4.0D, 10.0D, 10.0D, 12.0D);
      field_185642_F = Block.func_208617_a(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 20.0D);
      field_185644_G = Block.func_208617_a(-4.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
      field_185645_I = Block.func_208617_a(4.0D, 6.0D, 6.0D, 20.0D, 10.0D, 10.0D);
      field_190964_J = Block.func_208617_a(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
      field_190965_K = Block.func_208617_a(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
      field_190966_L = Block.func_208617_a(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 12.0D);
      field_190967_M = Block.func_208617_a(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 16.0D);
      field_190968_N = Block.func_208617_a(0.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
      field_190969_O = Block.func_208617_a(4.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
   }
}
