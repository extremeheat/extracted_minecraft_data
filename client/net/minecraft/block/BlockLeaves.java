package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockLeaves extends Block {
   public static final IntegerProperty field_208494_a;
   public static final BooleanProperty field_208495_b;
   protected static boolean field_196478_c;

   public BlockLeaves(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_208494_a, 7)).func_206870_a(field_208495_b, false));
   }

   public boolean func_149653_t(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_208494_a) == 7 && !(Boolean)var1.func_177229_b(field_208495_b);
   }

   public void func_196265_a(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!(Boolean)var1.func_177229_b(field_208495_b) && (Integer)var1.func_177229_b(field_208494_a) == 7) {
         var1.func_196949_c(var2, var3, 0);
         var2.func_175698_g(var3);
      }

   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      var2.func_180501_a(var3, func_208493_b(var1, var2, var3), 3);
   }

   public int func_200011_d(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return 1;
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      int var7 = func_208492_w(var3) + 1;
      if (var7 != 1 || (Integer)var1.func_177229_b(field_208494_a) != var7) {
         var4.func_205220_G_().func_205360_a(var5, this, 1);
      }

      return var1;
   }

   private static IBlockState func_208493_b(IBlockState var0, IWorld var1, BlockPos var2) {
      int var3 = 7;
      BlockPos.PooledMutableBlockPos var4 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var5 = null;

      try {
         EnumFacing[] var6 = EnumFacing.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            EnumFacing var9 = var6[var8];
            var4.func_189533_g(var2).func_189536_c(var9);
            var3 = Math.min(var3, func_208492_w(var1.func_180495_p(var4)) + 1);
            if (var3 == 1) {
               break;
            }
         }
      } catch (Throwable var17) {
         var5 = var17;
         throw var17;
      } finally {
         if (var4 != null) {
            if (var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var16) {
                  var5.addSuppressed(var16);
               }
            } else {
               var4.close();
            }
         }

      }

      return (IBlockState)var0.func_206870_a(field_208494_a, var3);
   }

   private static int func_208492_w(IBlockState var0) {
      if (BlockTags.field_200031_h.func_199685_a_(var0.func_177230_c())) {
         return 0;
      } else {
         return var0.func_177230_c() instanceof BlockLeaves ? (Integer)var0.func_177229_b(field_208494_a) : 7;
      }
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (var2.func_175727_C(var3.func_177984_a()) && !var2.func_180495_p(var3.func_177977_b()).func_185896_q() && var4.nextInt(15) == 1) {
         double var5 = (double)((float)var3.func_177958_n() + var4.nextFloat());
         double var7 = (double)var3.func_177956_o() - 0.05D;
         double var9 = (double)((float)var3.func_177952_p() + var4.nextFloat());
         var2.func_195594_a(Particles.field_197618_k, var5, var7, var9, 0.0D, 0.0D, 0.0D);
      }

   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return var2.nextInt(20) == 0 ? 1 : 0;
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      Block var5 = var1.func_177230_c();
      if (var5 == Blocks.field_196642_W) {
         return Blocks.field_196674_t;
      } else if (var5 == Blocks.field_196645_X) {
         return Blocks.field_196675_u;
      } else if (var5 == Blocks.field_196647_Y) {
         return Blocks.field_196676_v;
      } else if (var5 == Blocks.field_196648_Z) {
         return Blocks.field_196678_w;
      } else if (var5 == Blocks.field_196572_aa) {
         return Blocks.field_196679_x;
      } else {
         return var5 == Blocks.field_196574_ab ? Blocks.field_196680_y : Blocks.field_196674_t;
      }
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      if (!var2.field_72995_K) {
         int var6 = this.func_196472_i(var1);
         if (var5 > 0) {
            var6 -= 2 << var5;
            if (var6 < 10) {
               var6 = 10;
            }
         }

         if (var2.field_73012_v.nextInt(var6) == 0) {
            func_180635_a(var2, var3, new ItemStack(this.func_199769_a(var1, var2, var3, var5)));
         }

         var6 = 200;
         if (var5 > 0) {
            var6 -= 10 << var5;
            if (var6 < 40) {
               var6 = 40;
            }
         }

         this.func_196474_a(var2, var3, var1, var6);
      }

   }

   protected void func_196474_a(World var1, BlockPos var2, IBlockState var3, int var4) {
      if ((var3.func_177230_c() == Blocks.field_196642_W || var3.func_177230_c() == Blocks.field_196574_ab) && var1.field_73012_v.nextInt(var4) == 0) {
         func_180635_a(var1, var2, new ItemStack(Items.field_151034_e));
      }

   }

   protected int func_196472_i(IBlockState var1) {
      return var1.func_177230_c() == Blocks.field_196648_Z ? 40 : 20;
   }

   public static void func_196475_b(boolean var0) {
      field_196478_c = var0;
   }

   public BlockRenderLayer func_180664_k() {
      return field_196478_c ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.SOLID;
   }

   public boolean func_176214_u(IBlockState var1) {
      return false;
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      if (!var1.field_72995_K && var6.func_77973_b() == Items.field_151097_aZ) {
         var2.func_71029_a(StatList.field_188065_ae.func_199076_b(this));
         var2.func_71020_j(0.005F);
         func_180635_a(var1, var3, new ItemStack(this));
      } else {
         super.func_180657_a(var1, var2, var3, var4, var5, var6);
      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_208494_a, field_208495_b);
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return func_208493_b((IBlockState)this.func_176223_P().func_206870_a(field_208495_b, true), var1.func_195991_k(), var1.func_195995_a());
   }

   static {
      field_208494_a = BlockStateProperties.field_208514_aa;
      field_208495_b = BlockStateProperties.field_208515_s;
   }
}
