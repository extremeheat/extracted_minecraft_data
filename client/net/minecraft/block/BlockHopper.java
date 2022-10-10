package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockHopper extends BlockContainer {
   public static final DirectionProperty field_176430_a;
   public static final BooleanProperty field_176429_b;
   private static final VoxelShape field_196328_c;
   private static final VoxelShape field_196339_z;
   private static final VoxelShape field_199607_z;
   private static final VoxelShape field_196326_A;
   private static final VoxelShape field_196333_G;
   private static final VoxelShape field_196334_H;
   private static final VoxelShape field_196335_I;
   private static final VoxelShape field_196336_J;
   private static final VoxelShape field_196337_K;
   private static final VoxelShape field_199602_G;
   private static final VoxelShape field_199603_H;
   private static final VoxelShape field_199604_I;
   private static final VoxelShape field_199605_J;
   private static final VoxelShape field_199606_K;

   public BlockHopper(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176430_a, EnumFacing.DOWN)).func_206870_a(field_176429_b, true));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      switch((EnumFacing)var1.func_177229_b(field_176430_a)) {
      case DOWN:
         return field_196333_G;
      case NORTH:
         return field_196335_I;
      case SOUTH:
         return field_196336_J;
      case WEST:
         return field_196337_K;
      case EAST:
         return field_196334_H;
      default:
         return field_196326_A;
      }
   }

   public VoxelShape func_199600_g(IBlockState var1, IBlockReader var2, BlockPos var3) {
      switch((EnumFacing)var1.func_177229_b(field_176430_a)) {
      case DOWN:
         return field_199602_G;
      case NORTH:
         return field_199604_I;
      case SOUTH:
         return field_199605_J;
      case WEST:
         return field_199606_K;
      case EAST:
         return field_199603_H;
      default:
         return IHopper.field_200101_a;
      }
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      EnumFacing var2 = var1.func_196000_l().func_176734_d();
      return (IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_176430_a, var2.func_176740_k() == EnumFacing.Axis.Y ? EnumFacing.DOWN : var2)).func_206870_a(field_176429_b, true);
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityHopper();
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityHopper) {
            ((TileEntityHopper)var6).func_200226_a(var5.func_200301_q());
         }
      }

   }

   public boolean func_185481_k(IBlockState var1) {
      return true;
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var4.func_177230_c() != var1.func_177230_c()) {
         this.func_176427_e(var2, var3, var1);
      }
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (var2.field_72995_K) {
         return true;
      } else {
         TileEntity var10 = var2.func_175625_s(var3);
         if (var10 instanceof TileEntityHopper) {
            var4.func_71007_a((TileEntityHopper)var10);
            var4.func_195066_a(StatList.field_188084_R);
         }

         return true;
      }
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      this.func_176427_e(var2, var3, var1);
   }

   private void func_176427_e(World var1, BlockPos var2, IBlockState var3) {
      boolean var4 = !var1.func_175640_z(var2);
      if (var4 != (Boolean)var3.func_177229_b(field_176429_b)) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_176429_b, var4), 4);
      }

   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         TileEntity var6 = var2.func_175625_s(var3);
         if (var6 instanceof TileEntityHopper) {
            InventoryHelper.func_180175_a(var2, var3, (TileEntityHopper)var6);
            var2.func_175666_e(var3, this);
         }

         super.func_196243_a(var1, var2, var3, var4, var5);
      }
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.MODEL;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_149740_M(IBlockState var1) {
      return true;
   }

   public int func_180641_l(IBlockState var1, World var2, BlockPos var3) {
      return Container.func_178144_a(var2.func_175625_s(var3));
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176430_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176430_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_176430_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176430_a, field_176429_b);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return var4 == EnumFacing.UP ? BlockFaceShape.BOWL : BlockFaceShape.UNDEFINED;
   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      TileEntity var5 = var2.func_175625_s(var3);
      if (var5 instanceof TileEntityHopper) {
         ((TileEntityHopper)var5).func_200113_a(var4);
      }

   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176430_a = BlockStateProperties.field_208156_I;
      field_176429_b = BlockStateProperties.field_208180_g;
      field_196328_c = Block.func_208617_a(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      field_196339_z = Block.func_208617_a(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
      field_199607_z = VoxelShapes.func_197872_a(field_196339_z, field_196328_c);
      field_196326_A = VoxelShapes.func_197878_a(field_199607_z, IHopper.field_200101_a, IBooleanFunction.ONLY_FIRST);
      field_196333_G = VoxelShapes.func_197872_a(field_196326_A, Block.func_208617_a(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
      field_196334_H = VoxelShapes.func_197872_a(field_196326_A, Block.func_208617_a(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
      field_196335_I = VoxelShapes.func_197872_a(field_196326_A, Block.func_208617_a(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
      field_196336_J = VoxelShapes.func_197872_a(field_196326_A, Block.func_208617_a(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
      field_196337_K = VoxelShapes.func_197872_a(field_196326_A, Block.func_208617_a(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
      field_199602_G = IHopper.field_200101_a;
      field_199603_H = VoxelShapes.func_197872_a(IHopper.field_200101_a, Block.func_208617_a(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
      field_199604_I = VoxelShapes.func_197872_a(IHopper.field_200101_a, Block.func_208617_a(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
      field_199605_J = VoxelShapes.func_197872_a(IHopper.field_200101_a, Block.func_208617_a(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
      field_199606_K = VoxelShapes.func_197872_a(IHopper.field_200101_a, Block.func_208617_a(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));
   }
}
