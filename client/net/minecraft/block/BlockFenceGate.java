package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockHorizontal {
   public static final BooleanProperty field_176466_a;
   public static final BooleanProperty field_176465_b;
   public static final BooleanProperty field_176467_M;
   protected static final VoxelShape field_185541_d;
   protected static final VoxelShape field_185542_e;
   protected static final VoxelShape field_185543_f;
   protected static final VoxelShape field_185544_g;
   protected static final VoxelShape field_208068_x;
   protected static final VoxelShape field_185540_C;
   protected static final VoxelShape field_208069_z;
   protected static final VoxelShape field_185539_B;
   protected static final VoxelShape field_208066_B;
   protected static final VoxelShape field_208067_C;

   public BlockFenceGate(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176466_a, false)).func_206870_a(field_176465_b, false)).func_206870_a(field_176467_M, false));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      if ((Boolean)var1.func_177229_b(field_176467_M)) {
         return ((EnumFacing)var1.func_177229_b(field_185512_D)).func_176740_k() == EnumFacing.Axis.X ? field_185544_g : field_185543_f;
      } else {
         return ((EnumFacing)var1.func_177229_b(field_185512_D)).func_176740_k() == EnumFacing.Axis.X ? field_185542_e : field_185541_d;
      }
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      EnumFacing.Axis var7 = var2.func_176740_k();
      if (((EnumFacing)var1.func_177229_b(field_185512_D)).func_176746_e().func_176740_k() != var7) {
         return super.func_196271_a(var1, var2, var3, var4, var5, var6);
      } else {
         boolean var8 = this.func_196380_i(var3) || this.func_196380_i(var4.func_180495_p(var5.func_177972_a(var2.func_176734_d())));
         return (IBlockState)var1.func_206870_a(field_176467_M, var8);
      }
   }

   public VoxelShape func_196268_f(IBlockState var1, IBlockReader var2, BlockPos var3) {
      if ((Boolean)var1.func_177229_b(field_176466_a)) {
         return VoxelShapes.func_197880_a();
      } else {
         return ((EnumFacing)var1.func_177229_b(field_185512_D)).func_176740_k() == EnumFacing.Axis.Z ? field_208068_x : field_185540_C;
      }
   }

   public VoxelShape func_196247_c(IBlockState var1, IBlockReader var2, BlockPos var3) {
      if ((Boolean)var1.func_177229_b(field_176467_M)) {
         return ((EnumFacing)var1.func_177229_b(field_185512_D)).func_176740_k() == EnumFacing.Axis.X ? field_208067_C : field_208066_B;
      } else {
         return ((EnumFacing)var1.func_177229_b(field_185512_D)).func_176740_k() == EnumFacing.Axis.X ? field_185539_B : field_208069_z;
      }
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      switch(var4) {
      case LAND:
         return (Boolean)var1.func_177229_b(field_176466_a);
      case WATER:
         return false;
      case AIR:
         return (Boolean)var1.func_177229_b(field_176466_a);
      default:
         return false;
      }
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      boolean var4 = var2.func_175640_z(var3);
      EnumFacing var5 = var1.func_195992_f();
      EnumFacing.Axis var6 = var5.func_176740_k();
      boolean var7 = var6 == EnumFacing.Axis.Z && (this.func_196380_i(var2.func_180495_p(var3.func_177976_e())) || this.func_196380_i(var2.func_180495_p(var3.func_177974_f()))) || var6 == EnumFacing.Axis.X && (this.func_196380_i(var2.func_180495_p(var3.func_177978_c())) || this.func_196380_i(var2.func_180495_p(var3.func_177968_d())));
      return (IBlockState)((IBlockState)((IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_185512_D, var5)).func_206870_a(field_176466_a, var4)).func_206870_a(field_176465_b, var4)).func_206870_a(field_176467_M, var7);
   }

   private boolean func_196380_i(IBlockState var1) {
      return var1.func_177230_c() == Blocks.field_150463_bK || var1.func_177230_c() == Blocks.field_196723_eg;
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if ((Boolean)var1.func_177229_b(field_176466_a)) {
         var1 = (IBlockState)var1.func_206870_a(field_176466_a, false);
         var2.func_180501_a(var3, var1, 10);
      } else {
         EnumFacing var10 = var4.func_174811_aO();
         if (var1.func_177229_b(field_185512_D) == var10.func_176734_d()) {
            var1 = (IBlockState)var1.func_206870_a(field_185512_D, var10);
         }

         var1 = (IBlockState)var1.func_206870_a(field_176466_a, true);
         var2.func_180501_a(var3, var1, 10);
      }

      var2.func_180498_a(var4, (Boolean)var1.func_177229_b(field_176466_a) ? 1008 : 1014, var3, 0);
      return true;
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      if (!var2.field_72995_K) {
         boolean var6 = var2.func_175640_z(var3);
         if ((Boolean)var1.func_177229_b(field_176465_b) != var6) {
            var2.func_180501_a(var3, (IBlockState)((IBlockState)var1.func_206870_a(field_176465_b, var6)).func_206870_a(field_176466_a, var6), 2);
            if ((Boolean)var1.func_177229_b(field_176466_a) != var6) {
               var2.func_180498_a((EntityPlayer)null, var6 ? 1008 : 1014, var3, 0);
            }
         }

      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_185512_D, field_176466_a, field_176465_b, field_176467_M);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      if (var4 != EnumFacing.UP && var4 != EnumFacing.DOWN) {
         return ((EnumFacing)var2.func_177229_b(field_185512_D)).func_176740_k() == var4.func_176746_e().func_176740_k() ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.UNDEFINED;
      } else {
         return BlockFaceShape.UNDEFINED;
      }
   }

   static {
      field_176466_a = BlockStateProperties.field_208193_t;
      field_176465_b = BlockStateProperties.field_208194_u;
      field_176467_M = BlockStateProperties.field_208189_p;
      field_185541_d = Block.func_208617_a(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
      field_185542_e = Block.func_208617_a(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
      field_185543_f = Block.func_208617_a(0.0D, 0.0D, 6.0D, 16.0D, 13.0D, 10.0D);
      field_185544_g = Block.func_208617_a(6.0D, 0.0D, 0.0D, 10.0D, 13.0D, 16.0D);
      field_208068_x = Block.func_208617_a(0.0D, 0.0D, 6.0D, 16.0D, 24.0D, 10.0D);
      field_185540_C = Block.func_208617_a(6.0D, 0.0D, 0.0D, 10.0D, 24.0D, 16.0D);
      field_208069_z = VoxelShapes.func_197872_a(Block.func_208617_a(0.0D, 5.0D, 7.0D, 2.0D, 16.0D, 9.0D), Block.func_208617_a(14.0D, 5.0D, 7.0D, 16.0D, 16.0D, 9.0D));
      field_185539_B = VoxelShapes.func_197872_a(Block.func_208617_a(7.0D, 5.0D, 0.0D, 9.0D, 16.0D, 2.0D), Block.func_208617_a(7.0D, 5.0D, 14.0D, 9.0D, 16.0D, 16.0D));
      field_208066_B = VoxelShapes.func_197872_a(Block.func_208617_a(0.0D, 2.0D, 7.0D, 2.0D, 13.0D, 9.0D), Block.func_208617_a(14.0D, 2.0D, 7.0D, 16.0D, 13.0D, 9.0D));
      field_208067_C = VoxelShapes.func_197872_a(Block.func_208617_a(7.0D, 2.0D, 0.0D, 9.0D, 13.0D, 2.0D), Block.func_208617_a(7.0D, 2.0D, 14.0D, 9.0D, 13.0D, 16.0D));
   }
}
