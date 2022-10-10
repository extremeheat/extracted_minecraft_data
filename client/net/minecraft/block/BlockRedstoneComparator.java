package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ComparatorMode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider {
   public static final EnumProperty<ComparatorMode> field_176463_b;

   public BlockRedstoneComparator(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_185512_D, EnumFacing.NORTH)).func_206870_a(field_196348_c, false)).func_206870_a(field_176463_b, ComparatorMode.COMPARE));
   }

   protected int func_196346_i(IBlockState var1) {
      return 2;
   }

   protected int func_176408_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      TileEntity var4 = var1.func_175625_s(var2);
      return var4 instanceof TileEntityComparator ? ((TileEntityComparator)var4).func_145996_a() : 0;
   }

   private int func_176460_j(World var1, BlockPos var2, IBlockState var3) {
      return var3.func_177229_b(field_176463_b) == ComparatorMode.SUBTRACT ? Math.max(this.func_176397_f(var1, var2, var3) - this.func_176407_c(var1, var2, var3), 0) : this.func_176397_f(var1, var2, var3);
   }

   protected boolean func_176404_e(World var1, BlockPos var2, IBlockState var3) {
      int var4 = this.func_176397_f(var1, var2, var3);
      if (var4 >= 15) {
         return true;
      } else if (var4 == 0) {
         return false;
      } else {
         return var4 >= this.func_176407_c(var1, var2, var3);
      }
   }

   protected void func_211326_a(World var1, BlockPos var2) {
      var1.func_175713_t(var2);
   }

   protected int func_176397_f(World var1, BlockPos var2, IBlockState var3) {
      int var4 = super.func_176397_f(var1, var2, var3);
      EnumFacing var5 = (EnumFacing)var3.func_177229_b(field_185512_D);
      BlockPos var6 = var2.func_177972_a(var5);
      IBlockState var7 = var1.func_180495_p(var6);
      if (var7.func_185912_n()) {
         var4 = var7.func_185888_a(var1, var6);
      } else if (var4 < 15 && var7.func_185915_l()) {
         var6 = var6.func_177972_a(var5);
         var7 = var1.func_180495_p(var6);
         if (var7.func_185912_n()) {
            var4 = var7.func_185888_a(var1, var6);
         } else if (var7.func_196958_f()) {
            EntityItemFrame var8 = this.func_176461_a(var1, var5, var6);
            if (var8 != null) {
               var4 = var8.func_174866_q();
            }
         }
      }

      return var4;
   }

   @Nullable
   private EntityItemFrame func_176461_a(World var1, EnumFacing var2, BlockPos var3) {
      List var4 = var1.func_175647_a(EntityItemFrame.class, new AxisAlignedBB((double)var3.func_177958_n(), (double)var3.func_177956_o(), (double)var3.func_177952_p(), (double)(var3.func_177958_n() + 1), (double)(var3.func_177956_o() + 1), (double)(var3.func_177952_p() + 1)), (var1x) -> {
         return var1x != null && var1x.func_174811_aO() == var2;
      });
      return var4.size() == 1 ? (EntityItemFrame)var4.get(0) : null;
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (!var4.field_71075_bZ.field_75099_e) {
         return false;
      } else {
         var1 = (IBlockState)var1.func_177231_a(field_176463_b);
         float var10 = var1.func_177229_b(field_176463_b) == ComparatorMode.SUBTRACT ? 0.55F : 0.5F;
         var2.func_184133_a(var4, var3, SoundEvents.field_187556_aj, SoundCategory.BLOCKS, 0.3F, var10);
         var2.func_180501_a(var3, var1, 2);
         this.func_176462_k(var2, var3, var1);
         return true;
      }
   }

   protected void func_176398_g(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.func_205220_G_().func_205361_b(var2, this)) {
         int var4 = this.func_176460_j(var1, var2, var3);
         TileEntity var5 = var1.func_175625_s(var2);
         int var6 = var5 instanceof TileEntityComparator ? ((TileEntityComparator)var5).func_145996_a() : 0;
         if (var4 != var6 || (Boolean)var3.func_177229_b(field_196348_c) != this.func_176404_e(var1, var2, var3)) {
            TickPriority var7 = this.func_176402_i(var1, var2, var3) ? TickPriority.HIGH : TickPriority.NORMAL;
            var1.func_205220_G_().func_205362_a(var2, this, 2, var7);
         }

      }
   }

   private void func_176462_k(World var1, BlockPos var2, IBlockState var3) {
      int var4 = this.func_176460_j(var1, var2, var3);
      TileEntity var5 = var1.func_175625_s(var2);
      int var6 = 0;
      if (var5 instanceof TileEntityComparator) {
         TileEntityComparator var7 = (TileEntityComparator)var5;
         var6 = var7.func_145996_a();
         var7.func_145995_a(var4);
      }

      if (var6 != var4 || var3.func_177229_b(field_176463_b) == ComparatorMode.COMPARE) {
         boolean var9 = this.func_176404_e(var1, var2, var3);
         boolean var8 = (Boolean)var3.func_177229_b(field_196348_c);
         if (var8 && !var9) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_196348_c, false), 2);
         } else if (!var8 && var9) {
            var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_196348_c, true), 2);
         }

         this.func_176400_h(var1, var2, var3);
      }

   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      this.func_176462_k(var2, var3, var1);
   }

   public boolean func_189539_a(IBlockState var1, World var2, BlockPos var3, int var4, int var5) {
      super.func_189539_a(var1, var2, var3, var4, var5);
      TileEntity var6 = var2.func_175625_s(var3);
      return var6 != null && var6.func_145842_c(var4, var5);
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityComparator();
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_185512_D, field_176463_b, field_196348_c);
   }

   static {
      field_176463_b = BlockStateProperties.field_208141_ap;
   }
}
