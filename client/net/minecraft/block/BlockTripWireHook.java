package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockTripWireHook extends Block {
   public static final DirectionProperty field_176264_a;
   public static final BooleanProperty field_176263_b;
   public static final BooleanProperty field_176265_M;
   protected static final VoxelShape field_185743_d;
   protected static final VoxelShape field_185744_e;
   protected static final VoxelShape field_185745_f;
   protected static final VoxelShape field_185746_g;

   public BlockTripWireHook(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176264_a, EnumFacing.NORTH)).func_206870_a(field_176263_b, false)).func_206870_a(field_176265_M, false));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      switch((EnumFacing)var1.func_177229_b(field_176264_a)) {
      case EAST:
      default:
         return field_185746_g;
      case WEST:
         return field_185745_f;
      case SOUTH:
         return field_185744_e;
      case NORTH:
         return field_185743_d;
      }
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      EnumFacing var4 = (EnumFacing)var1.func_177229_b(field_176264_a);
      BlockPos var5 = var3.func_177972_a(var4.func_176734_d());
      IBlockState var6 = var2.func_180495_p(var5);
      boolean var7 = func_193382_c(var6.func_177230_c());
      return !var7 && var4.func_176740_k().func_176722_c() && var6.func_193401_d(var2, var5, var4) == BlockFaceShape.SOLID && !var6.func_185897_m();
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var2.func_176734_d() == var1.func_177229_b(field_176264_a) && !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = (IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_176263_b, false)).func_206870_a(field_176265_M, false);
      World var3 = var1.func_195991_k();
      BlockPos var4 = var1.func_195995_a();
      EnumFacing[] var5 = var1.func_196009_e();
      EnumFacing[] var6 = var5;
      int var7 = var5.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         EnumFacing var9 = var6[var8];
         if (var9.func_176740_k().func_176722_c()) {
            EnumFacing var10 = var9.func_176734_d();
            var2 = (IBlockState)var2.func_206870_a(field_176264_a, var10);
            if (var2.func_196955_c(var3, var4)) {
               return var2;
            }
         }
      }

      return null;
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      this.func_176260_a(var1, var2, var3, false, false, -1, (IBlockState)null);
   }

   public void func_176260_a(World var1, BlockPos var2, IBlockState var3, boolean var4, boolean var5, int var6, @Nullable IBlockState var7) {
      EnumFacing var8 = (EnumFacing)var3.func_177229_b(field_176264_a);
      boolean var9 = (Boolean)var3.func_177229_b(field_176265_M);
      boolean var10 = (Boolean)var3.func_177229_b(field_176263_b);
      boolean var11 = !var4;
      boolean var12 = false;
      int var13 = 0;
      IBlockState[] var14 = new IBlockState[42];

      BlockPos var16;
      for(int var15 = 1; var15 < 42; ++var15) {
         var16 = var2.func_177967_a(var8, var15);
         IBlockState var17 = var1.func_180495_p(var16);
         if (var17.func_177230_c() == Blocks.field_150479_bC) {
            if (var17.func_177229_b(field_176264_a) == var8.func_176734_d()) {
               var13 = var15;
            }
            break;
         }

         if (var17.func_177230_c() != Blocks.field_150473_bD && var15 != var6) {
            var14[var15] = null;
            var11 = false;
         } else {
            if (var15 == var6) {
               var17 = (IBlockState)MoreObjects.firstNonNull(var7, var17);
            }

            boolean var18 = !(Boolean)var17.func_177229_b(BlockTripWire.field_176295_N);
            boolean var19 = (Boolean)var17.func_177229_b(BlockTripWire.field_176293_a);
            var12 |= var18 && var19;
            var14[var15] = var17;
            if (var15 == var6) {
               var1.func_205220_G_().func_205360_a(var2, this, this.func_149738_a(var1));
               var11 &= var18;
            }
         }
      }

      var11 &= var13 > 1;
      var12 &= var11;
      IBlockState var20 = (IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_176265_M, var11)).func_206870_a(field_176263_b, var12);
      if (var13 > 0) {
         var16 = var2.func_177967_a(var8, var13);
         EnumFacing var22 = var8.func_176734_d();
         var1.func_180501_a(var16, (IBlockState)var20.func_206870_a(field_176264_a, var22), 3);
         this.func_176262_b(var1, var16, var22);
         this.func_180694_a(var1, var16, var11, var12, var9, var10);
      }

      this.func_180694_a(var1, var2, var11, var12, var9, var10);
      if (!var4) {
         var1.func_180501_a(var2, (IBlockState)var20.func_206870_a(field_176264_a, var8), 3);
         if (var5) {
            this.func_176262_b(var1, var2, var8);
         }
      }

      if (var9 != var11) {
         for(int var21 = 1; var21 < var13; ++var21) {
            BlockPos var23 = var2.func_177967_a(var8, var21);
            IBlockState var24 = var14[var21];
            if (var24 != null) {
               var1.func_180501_a(var23, (IBlockState)var24.func_206870_a(field_176265_M, var11), 3);
               if (!var1.func_180495_p(var23).func_196958_f()) {
               }
            }
         }
      }

   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      this.func_176260_a(var2, var3, var1, false, true, -1, (IBlockState)null);
   }

   private void func_180694_a(World var1, BlockPos var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      if (var4 && !var6) {
         var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_187907_gg, SoundCategory.BLOCKS, 0.4F, 0.6F);
      } else if (!var4 && var6) {
         var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_187906_gf, SoundCategory.BLOCKS, 0.4F, 0.5F);
      } else if (var3 && !var5) {
         var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_187905_ge, SoundCategory.BLOCKS, 0.4F, 0.7F);
      } else if (!var3 && var5) {
         var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_187908_gh, SoundCategory.BLOCKS, 0.4F, 1.2F / (var1.field_73012_v.nextFloat() * 0.2F + 0.9F));
      }

   }

   private void func_176262_b(World var1, BlockPos var2, EnumFacing var3) {
      var1.func_195593_d(var2, this);
      var1.func_195593_d(var2.func_177972_a(var3.func_176734_d()), this);
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (!var5 && var1.func_177230_c() != var4.func_177230_c()) {
         boolean var6 = (Boolean)var1.func_177229_b(field_176265_M);
         boolean var7 = (Boolean)var1.func_177229_b(field_176263_b);
         if (var6 || var7) {
            this.func_176260_a(var2, var3, var1, true, false, -1, (IBlockState)null);
         }

         if (var7) {
            var2.func_195593_d(var3, this);
            var2.func_195593_d(var3.func_177972_a(((EnumFacing)var1.func_177229_b(field_176264_a)).func_176734_d()), this);
         }

         super.func_196243_a(var1, var2, var3, var4, var5);
      }
   }

   public int func_180656_a(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return (Boolean)var1.func_177229_b(field_176263_b) ? 15 : 0;
   }

   public int func_176211_b(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      if (!(Boolean)var1.func_177229_b(field_176263_b)) {
         return 0;
      } else {
         return var1.func_177229_b(field_176264_a) == var4 ? 15 : 0;
      }
   }

   public boolean func_149744_f(IBlockState var1) {
      return true;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176264_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176264_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_176264_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176264_a, field_176263_b, field_176265_M);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   static {
      field_176264_a = BlockHorizontal.field_185512_D;
      field_176263_b = BlockStateProperties.field_208194_u;
      field_176265_M = BlockStateProperties.field_208174_a;
      field_185743_d = Block.func_208617_a(5.0D, 0.0D, 10.0D, 11.0D, 10.0D, 16.0D);
      field_185744_e = Block.func_208617_a(5.0D, 0.0D, 0.0D, 11.0D, 10.0D, 6.0D);
      field_185745_f = Block.func_208617_a(10.0D, 0.0D, 5.0D, 16.0D, 10.0D, 11.0D);
      field_185746_g = Block.func_208617_a(0.0D, 0.0D, 5.0D, 6.0D, 10.0D, 11.0D);
   }
}
