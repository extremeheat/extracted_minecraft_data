package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockChest extends BlockContainer implements IBucketPickupHandler, ILiquidContainer {
   public static final DirectionProperty field_176459_a;
   public static final EnumProperty<ChestType> field_196314_b;
   public static final BooleanProperty field_204511_c;
   protected static final VoxelShape field_196316_c;
   protected static final VoxelShape field_196317_y;
   protected static final VoxelShape field_196318_z;
   protected static final VoxelShape field_196313_A;
   protected static final VoxelShape field_196315_B;

   protected BlockChest(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176459_a, EnumFacing.NORTH)).func_206870_a(field_196314_b, ChestType.SINGLE)).func_206870_a(field_204511_c, false));
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_190946_v(IBlockState var1) {
      return true;
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.func_177229_b(field_204511_c)) {
         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
      }

      if (var3.func_177230_c() == this && var2.func_176740_k().func_176722_c()) {
         ChestType var7 = (ChestType)var3.func_177229_b(field_196314_b);
         if (var1.func_177229_b(field_196314_b) == ChestType.SINGLE && var7 != ChestType.SINGLE && var1.func_177229_b(field_176459_a) == var3.func_177229_b(field_176459_a) && func_196311_i(var3) == var2.func_176734_d()) {
            return (IBlockState)var1.func_206870_a(field_196314_b, var7.func_208081_a());
         }
      } else if (func_196311_i(var1) == var2) {
         return (IBlockState)var1.func_206870_a(field_196314_b, ChestType.SINGLE);
      }

      return super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      if (var1.func_177229_b(field_196314_b) == ChestType.SINGLE) {
         return field_196315_B;
      } else {
         switch(func_196311_i(var1)) {
         case NORTH:
         default:
            return field_196316_c;
         case SOUTH:
            return field_196317_y;
         case WEST:
            return field_196318_z;
         case EAST:
            return field_196313_A;
         }
      }
   }

   public static EnumFacing func_196311_i(IBlockState var0) {
      EnumFacing var1 = (EnumFacing)var0.func_177229_b(field_176459_a);
      return var0.func_177229_b(field_196314_b) == ChestType.LEFT ? var1.func_176746_e() : var1.func_176735_f();
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      ChestType var2 = ChestType.SINGLE;
      EnumFacing var3 = var1.func_195992_f().func_176734_d();
      IFluidState var4 = var1.func_195991_k().func_204610_c(var1.func_195995_a());
      boolean var5 = var1.func_195998_g();
      EnumFacing var6 = var1.func_196000_l();
      if (var6.func_176740_k().func_176722_c() && var5) {
         EnumFacing var7 = this.func_196312_a(var1, var6.func_176734_d());
         if (var7 != null && var7.func_176740_k() != var6.func_176740_k()) {
            var3 = var7;
            var2 = var7.func_176735_f() == var6.func_176734_d() ? ChestType.RIGHT : ChestType.LEFT;
         }
      }

      if (var2 == ChestType.SINGLE && !var5) {
         if (var3 == this.func_196312_a(var1, var3.func_176746_e())) {
            var2 = ChestType.LEFT;
         } else if (var3 == this.func_196312_a(var1, var3.func_176735_f())) {
            var2 = ChestType.RIGHT;
         }
      }

      return (IBlockState)((IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_176459_a, var3)).func_206870_a(field_196314_b, var2)).func_206870_a(field_204511_c, var4.func_206886_c() == Fluids.field_204546_a);
   }

   public Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_204511_c)) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204511_c, false), 3);
         return Fluids.field_204546_a;
      } else {
         return Fluids.field_204541_a;
      }
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_204511_c) ? Fluids.field_204546_a.func_207204_a(false) : super.func_204507_t(var1);
   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return !(Boolean)var3.func_177229_b(field_204511_c) && var4 == Fluids.field_204546_a;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      if (!(Boolean)var3.func_177229_b(field_204511_c) && var4.func_206886_c() == Fluids.field_204546_a) {
         if (!var1.func_201670_d()) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_204511_c, true), 3);
            var1.func_205219_F_().func_205360_a(var2, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var1));
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   private EnumFacing func_196312_a(BlockItemUseContext var1, EnumFacing var2) {
      IBlockState var3 = var1.func_195991_k().func_180495_p(var1.func_195995_a().func_177972_a(var2));
      return var3.func_177230_c() == this && var3.func_177229_b(field_196314_b) == ChestType.SINGLE ? (EnumFacing)var3.func_177229_b(field_176459_a) : null;
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityChest) {
            ((TileEntityChest)var6).func_200226_a(var5.func_200301_q());
         }
      }

   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         TileEntity var6 = var2.func_175625_s(var3);
         if (var6 instanceof IInventory) {
            InventoryHelper.func_180175_a(var2, var3, (IInventory)var6);
            var2.func_175666_e(var3, this);
         }

         super.func_196243_a(var1, var2, var3, var4, var5);
      }
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (var2.field_72995_K) {
         return true;
      } else {
         ILockableContainer var10 = this.func_196309_a(var1, var2, var3, false);
         if (var10 != null) {
            var4.func_71007_a(var10);
            var4.func_71029_a(this.func_196310_d());
         }

         return true;
      }
   }

   protected Stat<ResourceLocation> func_196310_d() {
      return StatList.field_199092_j.func_199076_b(StatList.field_188063_ac);
   }

   @Nullable
   public ILockableContainer func_196309_a(IBlockState var1, World var2, BlockPos var3, boolean var4) {
      TileEntity var5 = var2.func_175625_s(var3);
      if (!(var5 instanceof TileEntityChest)) {
         return null;
      } else if (!var4 && this.func_176457_m(var2, var3)) {
         return null;
      } else {
         Object var6 = (TileEntityChest)var5;
         ChestType var7 = (ChestType)var1.func_177229_b(field_196314_b);
         if (var7 == ChestType.SINGLE) {
            return (ILockableContainer)var6;
         } else {
            BlockPos var8 = var3.func_177972_a(func_196311_i(var1));
            IBlockState var9 = var2.func_180495_p(var8);
            if (var9.func_177230_c() == this) {
               ChestType var10 = (ChestType)var9.func_177229_b(field_196314_b);
               if (var10 != ChestType.SINGLE && var7 != var10 && var9.func_177229_b(field_176459_a) == var1.func_177229_b(field_176459_a)) {
                  if (!var4 && this.func_176457_m(var2, var8)) {
                     return null;
                  }

                  TileEntity var11 = var2.func_175625_s(var8);
                  if (var11 instanceof TileEntityChest) {
                     Object var12 = var7 == ChestType.RIGHT ? var6 : (ILockableContainer)var11;
                     Object var13 = var7 == ChestType.RIGHT ? (ILockableContainer)var11 : var6;
                     var6 = new InventoryLargeChest(new TextComponentTranslation("container.chestDouble", new Object[0]), (ILockableContainer)var12, (ILockableContainer)var13);
                  }
               }
            }

            return (ILockableContainer)var6;
         }
      }
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityChest();
   }

   private boolean func_176457_m(World var1, BlockPos var2) {
      return this.func_176456_n(var1, var2) || this.func_176453_o(var1, var2);
   }

   private boolean func_176456_n(IBlockReader var1, BlockPos var2) {
      return var1.func_180495_p(var2.func_177984_a()).func_185915_l();
   }

   private boolean func_176453_o(World var1, BlockPos var2) {
      List var3 = var1.func_72872_a(EntityOcelot.class, new AxisAlignedBB((double)var2.func_177958_n(), (double)(var2.func_177956_o() + 1), (double)var2.func_177952_p(), (double)(var2.func_177958_n() + 1), (double)(var2.func_177956_o() + 2), (double)(var2.func_177952_p() + 1)));
      if (!var3.isEmpty()) {
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            EntityOcelot var5 = (EntityOcelot)var4.next();
            if (var5.func_70906_o()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean func_149740_M(IBlockState var1) {
      return true;
   }

   public int func_180641_l(IBlockState var1, World var2, BlockPos var3) {
      return Container.func_94526_b(this.func_196309_a(var1, var2, var3, false));
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176459_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176459_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_176459_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176459_a, field_196314_b, field_204511_c);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176459_a = BlockHorizontal.field_185512_D;
      field_196314_b = BlockStateProperties.field_208140_ao;
      field_204511_c = BlockStateProperties.field_208198_y;
      field_196316_c = Block.func_208617_a(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
      field_196317_y = Block.func_208617_a(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
      field_196318_z = Block.func_208617_a(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
      field_196313_A = Block.func_208617_a(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
      field_196315_B = Block.func_208617_a(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
   }
}
