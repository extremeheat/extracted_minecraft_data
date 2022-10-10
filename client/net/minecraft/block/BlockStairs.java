package net.minecraft.block;

import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockStairs extends Block implements IBucketPickupHandler, ILiquidContainer {
   public static final DirectionProperty field_176309_a;
   public static final EnumProperty<Half> field_176308_b;
   public static final EnumProperty<StairsShape> field_176310_M;
   public static final BooleanProperty field_204513_t;
   protected static final VoxelShape field_185712_d;
   protected static final VoxelShape field_185719_G;
   protected static final VoxelShape field_196512_A;
   protected static final VoxelShape field_196513_B;
   protected static final VoxelShape field_196514_C;
   protected static final VoxelShape field_196515_D;
   protected static final VoxelShape field_196516_E;
   protected static final VoxelShape field_196517_F;
   protected static final VoxelShape field_196518_G;
   protected static final VoxelShape field_196519_H;
   protected static final VoxelShape[] field_196520_I;
   protected static final VoxelShape[] field_196521_J;
   private static final int[] field_196522_K;
   private final Block field_150149_b;
   private final IBlockState field_150151_M;

   private static VoxelShape[] func_199779_a(VoxelShape var0, VoxelShape var1, VoxelShape var2, VoxelShape var3, VoxelShape var4) {
      return (VoxelShape[])IntStream.range(0, 16).mapToObj((var5) -> {
         return func_199781_a(var5, var0, var1, var2, var3, var4);
      }).toArray((var0x) -> {
         return new VoxelShape[var0x];
      });
   }

   private static VoxelShape func_199781_a(int var0, VoxelShape var1, VoxelShape var2, VoxelShape var3, VoxelShape var4, VoxelShape var5) {
      VoxelShape var6 = var1;
      if ((var0 & 1) != 0) {
         var6 = VoxelShapes.func_197872_a(var1, var2);
      }

      if ((var0 & 2) != 0) {
         var6 = VoxelShapes.func_197872_a(var6, var3);
      }

      if ((var0 & 4) != 0) {
         var6 = VoxelShapes.func_197872_a(var6, var4);
      }

      if ((var0 & 8) != 0) {
         var6 = VoxelShapes.func_197872_a(var6, var5);
      }

      return var6;
   }

   protected BlockStairs(IBlockState var1, Block.Properties var2) {
      super(var2);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176309_a, EnumFacing.NORTH)).func_206870_a(field_176308_b, Half.BOTTOM)).func_206870_a(field_176310_M, StairsShape.STRAIGHT)).func_206870_a(field_204513_t, false));
      this.field_150149_b = var1.func_177230_c();
      this.field_150151_M = var1;
   }

   public int func_200011_d(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var2.func_201572_C();
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return (var1.func_177229_b(field_176308_b) == Half.TOP ? field_196520_I : field_196521_J)[field_196522_K[this.func_196511_x(var1)]];
   }

   private int func_196511_x(IBlockState var1) {
      return ((StairsShape)var1.func_177229_b(field_176310_M)).ordinal() * 4 + ((EnumFacing)var1.func_177229_b(field_176309_a)).func_176736_b();
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      if (var4.func_176740_k() == EnumFacing.Axis.Y) {
         return var4 == EnumFacing.UP == (var2.func_177229_b(field_176308_b) == Half.TOP) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
      } else {
         StairsShape var5 = (StairsShape)var2.func_177229_b(field_176310_M);
         if (var5 != StairsShape.OUTER_LEFT && var5 != StairsShape.OUTER_RIGHT) {
            EnumFacing var6 = (EnumFacing)var2.func_177229_b(field_176309_a);
            switch(var5) {
            case STRAIGHT:
               return var6 == var4 ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
            case INNER_LEFT:
               return var6 != var4 && var6 != var4.func_176746_e() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
            case INNER_RIGHT:
               return var6 != var4 && var6 != var4.func_176735_f() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
            default:
               return BlockFaceShape.UNDEFINED;
            }
         } else {
            return BlockFaceShape.UNDEFINED;
         }
      }
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      this.field_150149_b.func_180655_c(var1, var2, var3, var4);
   }

   public void func_196270_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4) {
      this.field_150151_M.func_196942_a(var2, var3, var4);
   }

   public void func_176206_d(IWorld var1, BlockPos var2, IBlockState var3) {
      this.field_150149_b.func_176206_d(var1, var2, var3);
   }

   public int func_185484_c(IBlockState var1, IWorldReader var2, BlockPos var3) {
      return this.field_150151_M.func_185889_a(var2, var3);
   }

   public float func_149638_a() {
      return this.field_150149_b.func_149638_a();
   }

   public BlockRenderLayer func_180664_k() {
      return this.field_150149_b.func_180664_k();
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return this.field_150149_b.func_149738_a(var1);
   }

   public boolean func_149703_v() {
      return this.field_150149_b.func_149703_v();
   }

   public boolean func_200293_a(IBlockState var1) {
      return this.field_150149_b.func_200293_a(var1);
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var1.func_177230_c() != var1.func_177230_c()) {
         this.field_150151_M.func_189546_a(var2, var3, Blocks.field_150350_a, var3);
         this.field_150149_b.func_196259_b(this.field_150151_M, var2, var3, var4);
      }
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         this.field_150151_M.func_196947_b(var2, var3, var4, var5);
      }
   }

   public void func_176199_a(World var1, BlockPos var2, Entity var3) {
      this.field_150149_b.func_176199_a(var1, var2, var3);
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      this.field_150149_b.func_196267_b(var1, var2, var3, var4);
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      return this.field_150151_M.func_196943_a(var2, var3, var4, var5, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
   }

   public void func_180652_a(World var1, BlockPos var2, Explosion var3) {
      this.field_150149_b.func_180652_a(var1, var2, var3);
   }

   public boolean func_185481_k(IBlockState var1) {
      return var1.func_177229_b(field_176308_b) == Half.TOP;
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      EnumFacing var2 = var1.func_196000_l();
      IFluidState var3 = var1.func_195991_k().func_204610_c(var1.func_195995_a());
      IBlockState var4 = (IBlockState)((IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_176309_a, var1.func_195992_f())).func_206870_a(field_176308_b, var2 != EnumFacing.DOWN && (var2 == EnumFacing.UP || (double)var1.func_195993_n() <= 0.5D) ? Half.BOTTOM : Half.TOP)).func_206870_a(field_204513_t, var3.func_206886_c() == Fluids.field_204546_a);
      return (IBlockState)var4.func_206870_a(field_176310_M, func_208064_n(var4, var1.func_195991_k(), var1.func_195995_a()));
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.func_177229_b(field_204513_t)) {
         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
      }

      return var2.func_176740_k().func_176722_c() ? (IBlockState)var1.func_206870_a(field_176310_M, func_208064_n(var1, var4, var5)) : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   private static StairsShape func_208064_n(IBlockState var0, IBlockReader var1, BlockPos var2) {
      EnumFacing var3 = (EnumFacing)var0.func_177229_b(field_176309_a);
      IBlockState var4 = var1.func_180495_p(var2.func_177972_a(var3));
      if (func_185709_i(var4) && var0.func_177229_b(field_176308_b) == var4.func_177229_b(field_176308_b)) {
         EnumFacing var5 = (EnumFacing)var4.func_177229_b(field_176309_a);
         if (var5.func_176740_k() != ((EnumFacing)var0.func_177229_b(field_176309_a)).func_176740_k() && func_185704_d(var0, var1, var2, var5.func_176734_d())) {
            if (var5 == var3.func_176735_f()) {
               return StairsShape.OUTER_LEFT;
            }

            return StairsShape.OUTER_RIGHT;
         }
      }

      IBlockState var7 = var1.func_180495_p(var2.func_177972_a(var3.func_176734_d()));
      if (func_185709_i(var7) && var0.func_177229_b(field_176308_b) == var7.func_177229_b(field_176308_b)) {
         EnumFacing var6 = (EnumFacing)var7.func_177229_b(field_176309_a);
         if (var6.func_176740_k() != ((EnumFacing)var0.func_177229_b(field_176309_a)).func_176740_k() && func_185704_d(var0, var1, var2, var6)) {
            if (var6 == var3.func_176735_f()) {
               return StairsShape.INNER_LEFT;
            }

            return StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   private static boolean func_185704_d(IBlockState var0, IBlockReader var1, BlockPos var2, EnumFacing var3) {
      IBlockState var4 = var1.func_180495_p(var2.func_177972_a(var3));
      return !func_185709_i(var4) || var4.func_177229_b(field_176309_a) != var0.func_177229_b(field_176309_a) || var4.func_177229_b(field_176308_b) != var0.func_177229_b(field_176308_b);
   }

   public static boolean func_185709_i(IBlockState var0) {
      return var0.func_177230_c() instanceof BlockStairs;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176309_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176309_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      EnumFacing var3 = (EnumFacing)var1.func_177229_b(field_176309_a);
      StairsShape var4 = (StairsShape)var1.func_177229_b(field_176310_M);
      switch(var2) {
      case LEFT_RIGHT:
         if (var3.func_176740_k() == EnumFacing.Axis.Z) {
            switch(var4) {
            case INNER_LEFT:
               return (IBlockState)var1.func_185907_a(Rotation.CLOCKWISE_180).func_206870_a(field_176310_M, StairsShape.INNER_RIGHT);
            case INNER_RIGHT:
               return (IBlockState)var1.func_185907_a(Rotation.CLOCKWISE_180).func_206870_a(field_176310_M, StairsShape.INNER_LEFT);
            case OUTER_LEFT:
               return (IBlockState)var1.func_185907_a(Rotation.CLOCKWISE_180).func_206870_a(field_176310_M, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return (IBlockState)var1.func_185907_a(Rotation.CLOCKWISE_180).func_206870_a(field_176310_M, StairsShape.OUTER_LEFT);
            default:
               return var1.func_185907_a(Rotation.CLOCKWISE_180);
            }
         }
         break;
      case FRONT_BACK:
         if (var3.func_176740_k() == EnumFacing.Axis.X) {
            switch(var4) {
            case STRAIGHT:
               return var1.func_185907_a(Rotation.CLOCKWISE_180);
            case INNER_LEFT:
               return (IBlockState)var1.func_185907_a(Rotation.CLOCKWISE_180).func_206870_a(field_176310_M, StairsShape.INNER_LEFT);
            case INNER_RIGHT:
               return (IBlockState)var1.func_185907_a(Rotation.CLOCKWISE_180).func_206870_a(field_176310_M, StairsShape.INNER_RIGHT);
            case OUTER_LEFT:
               return (IBlockState)var1.func_185907_a(Rotation.CLOCKWISE_180).func_206870_a(field_176310_M, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return (IBlockState)var1.func_185907_a(Rotation.CLOCKWISE_180).func_206870_a(field_176310_M, StairsShape.OUTER_LEFT);
            }
         }
      }

      return super.func_185471_a(var1, var2);
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176309_a, field_176308_b, field_176310_M, field_204513_t);
   }

   public Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_204513_t)) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204513_t, false), 3);
         return Fluids.field_204546_a;
      } else {
         return Fluids.field_204541_a;
      }
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_204513_t) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(var1);
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return !(Boolean)var3.func_177229_b(field_204513_t) && var4 == Fluids.field_204546_a;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      if (!(Boolean)var3.func_177229_b(field_204513_t) && var4.func_206886_c() == Fluids.field_204546_a) {
         if (!var1.func_201670_d()) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204513_t, true), 3);
            var1.func_205219_F_().func_205360_a(var2, var4.func_206886_c(), var4.func_206886_c().func_205569_a(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176309_a = BlockHorizontal.field_185512_D;
      field_176308_b = BlockStateProperties.field_208164_Q;
      field_176310_M = BlockStateProperties.field_208146_au;
      field_204513_t = BlockStateProperties.field_208198_y;
      field_185712_d = BlockSlab.field_196507_c;
      field_185719_G = BlockSlab.field_196506_b;
      field_196512_A = Block.func_208617_a(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
      field_196513_B = Block.func_208617_a(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
      field_196514_C = Block.func_208617_a(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
      field_196515_D = Block.func_208617_a(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
      field_196516_E = Block.func_208617_a(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
      field_196517_F = Block.func_208617_a(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
      field_196518_G = Block.func_208617_a(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
      field_196519_H = Block.func_208617_a(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
      field_196520_I = func_199779_a(field_185712_d, field_196512_A, field_196516_E, field_196513_B, field_196517_F);
      field_196521_J = func_199779_a(field_185719_G, field_196514_C, field_196518_G, field_196515_D, field_196519_H);
      field_196522_K = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
   }
}
