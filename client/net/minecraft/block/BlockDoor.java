package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockDoor extends Block {
   public static final DirectionProperty field_176520_a;
   public static final BooleanProperty field_176519_b;
   public static final EnumProperty<DoorHingeSide> field_176521_M;
   public static final BooleanProperty field_176522_N;
   public static final EnumProperty<DoubleBlockHalf> field_176523_O;
   protected static final VoxelShape field_185658_f;
   protected static final VoxelShape field_185659_g;
   protected static final VoxelShape field_185656_B;
   protected static final VoxelShape field_185657_C;

   protected BlockDoor(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176520_a, EnumFacing.NORTH)).func_206870_a(field_176519_b, false)).func_206870_a(field_176521_M, DoorHingeSide.LEFT)).func_206870_a(field_176522_N, false)).func_206870_a(field_176523_O, DoubleBlockHalf.LOWER));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      EnumFacing var4 = (EnumFacing)var1.func_177229_b(field_176520_a);
      boolean var5 = !(Boolean)var1.func_177229_b(field_176519_b);
      boolean var6 = var1.func_177229_b(field_176521_M) == DoorHingeSide.RIGHT;
      switch(var4) {
      case EAST:
      default:
         return var5 ? field_185657_C : (var6 ? field_185659_g : field_185658_f);
      case SOUTH:
         return var5 ? field_185658_f : (var6 ? field_185657_C : field_185656_B);
      case WEST:
         return var5 ? field_185656_B : (var6 ? field_185658_f : field_185659_g);
      case NORTH:
         return var5 ? field_185659_g : (var6 ? field_185656_B : field_185657_C);
      }
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      DoubleBlockHalf var7 = (DoubleBlockHalf)var1.func_177229_b(field_176523_O);
      if (var2.func_176740_k() == EnumFacing.Axis.Y && var7 == DoubleBlockHalf.LOWER == (var2 == EnumFacing.UP)) {
         return var3.func_177230_c() == this && var3.func_177229_b(field_176523_O) != var7 ? (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_176520_a, var3.func_177229_b(field_176520_a))).func_206870_a(field_176519_b, var3.func_177229_b(field_176519_b))).func_206870_a(field_176521_M, var3.func_177229_b(field_176521_M))).func_206870_a(field_176522_N, var3.func_177229_b(field_176522_N)) : Blocks.field_150350_a.func_176223_P();
      } else {
         return var7 == DoubleBlockHalf.LOWER && var2 == EnumFacing.DOWN && !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
      }
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      super.func_180657_a(var1, var2, var3, Blocks.field_150350_a.func_176223_P(), var5, var6);
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      DoubleBlockHalf var5 = (DoubleBlockHalf)var3.func_177229_b(field_176523_O);
      boolean var6 = var5 == DoubleBlockHalf.LOWER;
      BlockPos var7 = var6 ? var2.func_177984_a() : var2.func_177977_b();
      IBlockState var8 = var1.func_180495_p(var7);
      if (var8.func_177230_c() == this && var8.func_177229_b(field_176523_O) != var5) {
         var1.func_180501_a(var7, Blocks.field_150350_a.func_176223_P(), 35);
         var1.func_180498_a(var4, 2001, var7, Block.func_196246_j(var8));
         if (!var1.field_72995_K && !var4.func_184812_l_()) {
            if (var6) {
               var3.func_196949_c(var1, var2, 0);
            } else {
               var8.func_196949_c(var1, var7, 0);
            }
         }
      }

      super.func_176208_a(var1, var2, var3, var4);
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      switch(var4) {
      case LAND:
         return (Boolean)var1.func_177229_b(field_176519_b);
      case WATER:
         return false;
      case AIR:
         return (Boolean)var1.func_177229_b(field_176519_b);
      default:
         return false;
      }
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   private int func_185654_e() {
      return this.field_149764_J == Material.field_151573_f ? 1011 : 1012;
   }

   private int func_185655_g() {
      return this.field_149764_J == Material.field_151573_f ? 1005 : 1006;
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      BlockPos var2 = var1.func_195995_a();
      if (var2.func_177956_o() < 255 && var1.func_195991_k().func_180495_p(var2.func_177984_a()).func_196953_a(var1)) {
         World var3 = var1.func_195991_k();
         boolean var4 = var3.func_175640_z(var2) || var3.func_175640_z(var2.func_177984_a());
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_176520_a, var1.func_195992_f())).func_206870_a(field_176521_M, this.func_208073_b(var1))).func_206870_a(field_176522_N, var4)).func_206870_a(field_176519_b, var4)).func_206870_a(field_176523_O, DoubleBlockHalf.LOWER);
      } else {
         return null;
      }
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      var1.func_180501_a(var2.func_177984_a(), (IBlockState)var3.func_206870_a(field_176523_O, DoubleBlockHalf.UPPER), 3);
   }

   private DoorHingeSide func_208073_b(BlockItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      EnumFacing var4 = var1.func_195992_f();
      BlockPos var5 = var3.func_177984_a();
      EnumFacing var6 = var4.func_176735_f();
      IBlockState var7 = var2.func_180495_p(var3.func_177972_a(var6));
      IBlockState var8 = var2.func_180495_p(var5.func_177972_a(var6));
      EnumFacing var9 = var4.func_176746_e();
      IBlockState var10 = var2.func_180495_p(var3.func_177972_a(var9));
      IBlockState var11 = var2.func_180495_p(var5.func_177972_a(var9));
      int var12 = (var7.func_185898_k() ? -1 : 0) + (var8.func_185898_k() ? -1 : 0) + (var10.func_185898_k() ? 1 : 0) + (var11.func_185898_k() ? 1 : 0);
      boolean var13 = var7.func_177230_c() == this && var7.func_177229_b(field_176523_O) == DoubleBlockHalf.LOWER;
      boolean var14 = var10.func_177230_c() == this && var10.func_177229_b(field_176523_O) == DoubleBlockHalf.LOWER;
      if ((!var13 || var14) && var12 <= 0) {
         if ((!var14 || var13) && var12 >= 0) {
            int var15 = var4.func_82601_c();
            int var16 = var4.func_82599_e();
            float var17 = var1.func_195997_m();
            float var18 = var1.func_195994_o();
            return (var15 >= 0 || var18 >= 0.5F) && (var15 <= 0 || var18 <= 0.5F) && (var16 >= 0 || var17 <= 0.5F) && (var16 <= 0 || var17 >= 0.5F) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
         } else {
            return DoorHingeSide.LEFT;
         }
      } else {
         return DoorHingeSide.RIGHT;
      }
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (this.field_149764_J == Material.field_151573_f) {
         return false;
      } else {
         var1 = (IBlockState)var1.func_177231_a(field_176519_b);
         var2.func_180501_a(var3, var1, 10);
         var2.func_180498_a(var4, (Boolean)var1.func_177229_b(field_176519_b) ? this.func_185655_g() : this.func_185654_e(), var3, 0);
         return true;
      }
   }

   public void func_176512_a(World var1, BlockPos var2, boolean var3) {
      IBlockState var4 = var1.func_180495_p(var2);
      if (var4.func_177230_c() == this && (Boolean)var4.func_177229_b(field_176519_b) != var3) {
         var1.func_180501_a(var2, (IBlockState)var4.func_206870_a(field_176519_b, var3), 10);
         this.func_196426_b(var1, var2, var3);
      }
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      boolean var6 = var2.func_175640_z(var3) || var2.func_175640_z(var3.func_177972_a(var1.func_177229_b(field_176523_O) == DoubleBlockHalf.LOWER ? EnumFacing.UP : EnumFacing.DOWN));
      if (var4 != this && var6 != (Boolean)var1.func_177229_b(field_176522_N)) {
         if (var6 != (Boolean)var1.func_177229_b(field_176519_b)) {
            this.func_196426_b(var2, var3, var6);
         }

         var2.func_180501_a(var3, (IBlockState)((IBlockState)var1.func_206870_a(field_176522_N, var6)).func_206870_a(field_176519_b, var6), 2);
      }

   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      IBlockState var4 = var2.func_180495_p(var3.func_177977_b());
      if (var1.func_177229_b(field_176523_O) == DoubleBlockHalf.LOWER) {
         return var4.func_185896_q();
      } else {
         return var4.func_177230_c() == this;
      }
   }

   private void func_196426_b(World var1, BlockPos var2, boolean var3) {
      var1.func_180498_a((EntityPlayer)null, var3 ? this.func_185655_g() : this.func_185654_e(), var2, 0);
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return (IItemProvider)(var1.func_177229_b(field_176523_O) == DoubleBlockHalf.UPPER ? Items.field_190931_a : super.func_199769_a(var1, var2, var3, var4));
   }

   public EnumPushReaction func_149656_h(IBlockState var1) {
      return EnumPushReaction.DESTROY;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176520_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176520_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var2 == Mirror.NONE ? var1 : (IBlockState)var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_176520_a))).func_177231_a(field_176521_M);
   }

   public long func_209900_a(IBlockState var1, BlockPos var2) {
      return MathHelper.func_180187_c(var2.func_177958_n(), var2.func_177979_c(var1.func_177229_b(field_176523_O) == DoubleBlockHalf.LOWER ? 0 : 1).func_177956_o(), var2.func_177952_p());
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176523_O, field_176520_a, field_176519_b, field_176521_M, field_176522_N);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   static {
      field_176520_a = BlockHorizontal.field_185512_D;
      field_176519_b = BlockStateProperties.field_208193_t;
      field_176521_M = BlockStateProperties.field_208142_aq;
      field_176522_N = BlockStateProperties.field_208194_u;
      field_176523_O = BlockStateProperties.field_208163_P;
      field_185658_f = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
      field_185659_g = Block.func_208617_a(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
      field_185656_B = Block.func_208617_a(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      field_185657_C = Block.func_208617_a(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
   }
}
