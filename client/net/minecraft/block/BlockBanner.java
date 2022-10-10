package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockBanner extends BlockAbstractBanner {
   public static final IntegerProperty field_176448_b;
   private static final Map<EnumDyeColor, Block> field_196288_b;
   private static final VoxelShape field_196289_c;

   public BlockBanner(EnumDyeColor var1, Block.Properties var2) {
      super(var1, var2);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176448_b, 0));
      field_196288_b.put(var1, this);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      return var2.func_180495_p(var3.func_177977_b()).func_185904_a().func_76220_a();
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196289_c;
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_176448_b, MathHelper.func_76128_c((double)((180.0F + var1.func_195990_h()) * 16.0F / 360.0F) + 0.5D) & 15);
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var2 == EnumFacing.DOWN && !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176448_b, var2.func_185833_a((Integer)var1.func_177229_b(field_176448_b), 16));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return (IBlockState)var1.func_206870_a(field_176448_b, var2.func_185802_a((Integer)var1.func_177229_b(field_176448_b), 16));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176448_b);
   }

   public static Block func_196287_a(EnumDyeColor var0) {
      return (Block)field_196288_b.getOrDefault(var0, Blocks.field_196784_gT);
   }

   static {
      field_176448_b = BlockStateProperties.field_208138_am;
      field_196288_b = Maps.newHashMap();
      field_196289_c = Block.func_208617_a(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
   }
}
