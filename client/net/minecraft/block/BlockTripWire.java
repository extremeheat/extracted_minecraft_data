package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockTripWire extends Block {
   public static final BooleanProperty field_176293_a;
   public static final BooleanProperty field_176294_M;
   public static final BooleanProperty field_176295_N;
   public static final BooleanProperty field_176296_O;
   public static final BooleanProperty field_176291_P;
   public static final BooleanProperty field_176289_Q;
   public static final BooleanProperty field_176292_R;
   private static final Map<EnumFacing, BooleanProperty> field_196537_E;
   protected static final VoxelShape field_185747_B;
   protected static final VoxelShape field_185748_C;
   private final BlockTripWireHook field_196538_F;

   public BlockTripWire(BlockTripWireHook var1, Block.Properties var2) {
      super(var2);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176293_a, false)).func_206870_a(field_176294_M, false)).func_206870_a(field_176295_N, false)).func_206870_a(field_176296_O, false)).func_206870_a(field_176291_P, false)).func_206870_a(field_176289_Q, false)).func_206870_a(field_176292_R, false));
      this.field_196538_F = var1;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return (Boolean)var1.func_177229_b(field_176294_M) ? field_185747_B : field_185748_C;
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      return (IBlockState)((IBlockState)((IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_176296_O, this.func_196536_a(var2.func_180495_p(var3.func_177978_c()), EnumFacing.NORTH))).func_206870_a(field_176291_P, this.func_196536_a(var2.func_180495_p(var3.func_177974_f()), EnumFacing.EAST))).func_206870_a(field_176289_Q, this.func_196536_a(var2.func_180495_p(var3.func_177968_d()), EnumFacing.SOUTH))).func_206870_a(field_176292_R, this.func_196536_a(var2.func_180495_p(var3.func_177976_e()), EnumFacing.WEST));
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var2.func_176740_k().func_176722_c() ? (IBlockState)var1.func_206870_a((IProperty)field_196537_E.get(var2), this.func_196536_a(var3, var2)) : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var4.func_177230_c() != var1.func_177230_c()) {
         this.func_176286_e(var2, var3, var1);
      }
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (!var5 && var1.func_177230_c() != var4.func_177230_c()) {
         this.func_176286_e(var2, var3, (IBlockState)var1.func_206870_a(field_176293_a, true));
      }
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (!var1.field_72995_K && !var4.func_184614_ca().func_190926_b() && var4.func_184614_ca().func_77973_b() == Items.field_151097_aZ) {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_176295_N, true), 4);
      }

      super.func_176208_a(var1, var2, var3, var4);
   }

   private void func_176286_e(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing[] var4 = new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.WEST};
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         EnumFacing var7 = var4[var6];

         for(int var8 = 1; var8 < 42; ++var8) {
            BlockPos var9 = var2.func_177967_a(var7, var8);
            IBlockState var10 = var1.func_180495_p(var9);
            if (var10.func_177230_c() == this.field_196538_F) {
               if (var10.func_177229_b(BlockTripWireHook.field_176264_a) == var7.func_176734_d()) {
                  this.field_196538_F.func_176260_a(var1, var9, var10, false, true, var8, var3);
               }
               break;
            }

            if (var10.func_177230_c() != this) {
               break;
            }
         }
      }

   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      if (!var2.field_72995_K) {
         if (!(Boolean)var1.func_177229_b(field_176293_a)) {
            this.func_176288_d(var2, var3);
         }
      }
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var2.field_72995_K) {
         if ((Boolean)var2.func_180495_p(var3).func_177229_b(field_176293_a)) {
            this.func_176288_d(var2, var3);
         }
      }
   }

   private void func_176288_d(World var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      boolean var4 = (Boolean)var3.func_177229_b(field_176293_a);
      boolean var5 = false;
      List var6 = var1.func_72839_b((Entity)null, var3.func_196954_c(var1, var2).func_197752_a().func_186670_a(var2));
      if (!var6.isEmpty()) {
         Iterator var7 = var6.iterator();

         while(var7.hasNext()) {
            Entity var8 = (Entity)var7.next();
            if (!var8.func_145773_az()) {
               var5 = true;
               break;
            }
         }
      }

      if (var5 != var4) {
         var3 = (IBlockState)var3.func_206870_a(field_176293_a, var5);
         var1.func_180501_a(var2, var3, 3);
         this.func_176286_e(var1, var2, var3);
      }

      if (var5) {
         var1.func_205220_G_().func_205360_a(new BlockPos(var2), this, this.func_149738_a(var1));
      }

   }

   public boolean func_196536_a(IBlockState var1, EnumFacing var2) {
      Block var3 = var1.func_177230_c();
      if (var3 == this.field_196538_F) {
         return var1.func_177229_b(BlockTripWireHook.field_176264_a) == var2.func_176734_d();
      } else {
         return var3 == this;
      }
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      switch(var2) {
      case CLOCKWISE_180:
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_176296_O, var1.func_177229_b(field_176289_Q))).func_206870_a(field_176291_P, var1.func_177229_b(field_176292_R))).func_206870_a(field_176289_Q, var1.func_177229_b(field_176296_O))).func_206870_a(field_176292_R, var1.func_177229_b(field_176291_P));
      case COUNTERCLOCKWISE_90:
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_176296_O, var1.func_177229_b(field_176291_P))).func_206870_a(field_176291_P, var1.func_177229_b(field_176289_Q))).func_206870_a(field_176289_Q, var1.func_177229_b(field_176292_R))).func_206870_a(field_176292_R, var1.func_177229_b(field_176296_O));
      case CLOCKWISE_90:
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_176296_O, var1.func_177229_b(field_176292_R))).func_206870_a(field_176291_P, var1.func_177229_b(field_176296_O))).func_206870_a(field_176289_Q, var1.func_177229_b(field_176291_P))).func_206870_a(field_176292_R, var1.func_177229_b(field_176289_Q));
      default:
         return var1;
      }
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      switch(var2) {
      case LEFT_RIGHT:
         return (IBlockState)((IBlockState)var1.func_206870_a(field_176296_O, var1.func_177229_b(field_176289_Q))).func_206870_a(field_176289_Q, var1.func_177229_b(field_176296_O));
      case FRONT_BACK:
         return (IBlockState)((IBlockState)var1.func_206870_a(field_176291_P, var1.func_177229_b(field_176292_R))).func_206870_a(field_176292_R, var1.func_177229_b(field_176291_P));
      default:
         return super.func_185471_a(var1, var2);
      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176293_a, field_176294_M, field_176295_N, field_176296_O, field_176291_P, field_176292_R, field_176289_Q);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   static {
      field_176293_a = BlockStateProperties.field_208194_u;
      field_176294_M = BlockStateProperties.field_208174_a;
      field_176295_N = BlockStateProperties.field_208178_e;
      field_176296_O = BlockSixWay.field_196488_a;
      field_176291_P = BlockSixWay.field_196490_b;
      field_176289_Q = BlockSixWay.field_196492_c;
      field_176292_R = BlockSixWay.field_196495_y;
      field_196537_E = BlockFourWay.field_196415_z;
      field_185747_B = Block.func_208617_a(0.0D, 1.0D, 0.0D, 16.0D, 2.5D, 16.0D);
      field_185748_C = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   }
}
