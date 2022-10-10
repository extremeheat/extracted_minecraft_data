package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

public abstract class BlockRedstoneDiode extends BlockHorizontal {
   protected static final VoxelShape field_196347_b = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   public static final BooleanProperty field_196348_c;

   protected BlockRedstoneDiode(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196347_b;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      return var2.func_180495_p(var3.func_177977_b()).func_185896_q();
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!this.func_176405_b(var2, var3, var1)) {
         boolean var5 = (Boolean)var1.func_177229_b(field_196348_c);
         boolean var6 = this.func_176404_e(var2, var3, var1);
         if (var5 && !var6) {
            var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_196348_c, false), 2);
         } else if (!var5) {
            var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_196348_c, true), 2);
            if (!var6) {
               var2.func_205220_G_().func_205362_a(var3, this, this.func_196346_i(var1), TickPriority.HIGH);
            }
         }

      }
   }

   public int func_176211_b(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return var1.func_185911_a(var2, var3, var4);
   }

   public int func_180656_a(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      if (!(Boolean)var1.func_177229_b(field_196348_c)) {
         return 0;
      } else {
         return var1.func_177229_b(field_185512_D) == var4 ? this.func_176408_a(var2, var3, var1) : 0;
      }
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      if (var1.func_196955_c(var2, var3)) {
         this.func_176398_g(var2, var3, var1);
      } else {
         var1.func_196949_c(var2, var3, 0);
         var2.func_175698_g(var3);
         EnumFacing[] var6 = EnumFacing.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            EnumFacing var9 = var6[var8];
            var2.func_195593_d(var3.func_177972_a(var9), this);
         }

      }
   }

   protected void func_176398_g(World var1, BlockPos var2, IBlockState var3) {
      if (!this.func_176405_b(var1, var2, var3)) {
         boolean var4 = (Boolean)var3.func_177229_b(field_196348_c);
         boolean var5 = this.func_176404_e(var1, var2, var3);
         if (var4 != var5 && !var1.func_205220_G_().func_205361_b(var2, this)) {
            TickPriority var6 = TickPriority.HIGH;
            if (this.func_176402_i(var1, var2, var3)) {
               var6 = TickPriority.EXTREMELY_HIGH;
            } else if (var4) {
               var6 = TickPriority.VERY_HIGH;
            }

            var1.func_205220_G_().func_205362_a(var2, this, this.func_196346_i(var3), var6);
         }

      }
   }

   public boolean func_176405_b(IWorldReaderBase var1, BlockPos var2, IBlockState var3) {
      return false;
   }

   protected boolean func_176404_e(World var1, BlockPos var2, IBlockState var3) {
      return this.func_176397_f(var1, var2, var3) > 0;
   }

   protected int func_176397_f(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_185512_D);
      BlockPos var5 = var2.func_177972_a(var4);
      int var6 = var1.func_175651_c(var5, var4);
      if (var6 >= 15) {
         return var6;
      } else {
         IBlockState var7 = var1.func_180495_p(var5);
         return Math.max(var6, var7.func_177230_c() == Blocks.field_150488_af ? (Integer)var7.func_177229_b(BlockRedstoneWire.field_176351_O) : 0);
      }
   }

   protected int func_176407_c(IWorldReaderBase var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_185512_D);
      EnumFacing var5 = var4.func_176746_e();
      EnumFacing var6 = var4.func_176735_f();
      return Math.max(this.func_176401_c(var1, var2.func_177972_a(var5), var5), this.func_176401_c(var1, var2.func_177972_a(var6), var6));
   }

   protected int func_176401_c(IWorldReaderBase var1, BlockPos var2, EnumFacing var3) {
      IBlockState var4 = var1.func_180495_p(var2);
      Block var5 = var4.func_177230_c();
      if (this.func_185545_A(var4)) {
         if (var5 == Blocks.field_150451_bX) {
            return 15;
         } else {
            return var5 == Blocks.field_150488_af ? (Integer)var4.func_177229_b(BlockRedstoneWire.field_176351_O) : var1.func_175627_a(var2, var3);
         }
      } else {
         return 0;
      }
   }

   public boolean func_149744_f(IBlockState var1) {
      return true;
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_185512_D, var1.func_195992_f().func_176734_d());
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (this.func_176404_e(var1, var2, var3)) {
         var1.func_205220_G_().func_205360_a(var2, this, 1);
      }

   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      this.func_176400_h(var2, var3, var1);
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (!var5 && var1.func_177230_c() != var4.func_177230_c()) {
         super.func_196243_a(var1, var2, var3, var4, var5);
         this.func_211326_a(var2, var3);
         this.func_176400_h(var2, var3, var1);
      }
   }

   protected void func_211326_a(World var1, BlockPos var2) {
   }

   protected void func_176400_h(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_185512_D);
      BlockPos var5 = var2.func_177972_a(var4.func_176734_d());
      var1.func_190524_a(var5, this, var2);
      var1.func_175695_a(var5, this, var4);
   }

   protected boolean func_185545_A(IBlockState var1) {
      return var1.func_185897_m();
   }

   protected int func_176408_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return 15;
   }

   public static boolean func_185546_B(IBlockState var0) {
      return var0.func_177230_c() instanceof BlockRedstoneDiode;
   }

   public boolean func_176402_i(IBlockReader var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = ((EnumFacing)var3.func_177229_b(field_185512_D)).func_176734_d();
      IBlockState var5 = var1.func_180495_p(var2.func_177972_a(var4));
      return func_185546_B(var5) && var5.func_177229_b(field_185512_D) != var4;
   }

   protected abstract int func_196346_i(IBlockState var1);

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean func_200124_e(IBlockState var1) {
      return true;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return var4 == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   static {
      field_196348_c = BlockStateProperties.field_208194_u;
   }
}
