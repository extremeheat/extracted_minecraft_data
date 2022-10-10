package net.minecraft.client.renderer.color;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockShearableDoublePlant;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;

public class BlockColors {
   private final ObjectIntIdentityMap<IBlockColor> field_186725_a = new ObjectIntIdentityMap(32);

   public BlockColors() {
      super();
   }

   public static BlockColors func_186723_a() {
      BlockColors var0 = new BlockColors();
      var0.func_186722_a((var0x, var1, var2, var3) -> {
         return var1 != null && var2 != null ? BiomeColors.func_180286_a(var1, var0x.func_177229_b(BlockShearableDoublePlant.field_208063_b) == DoubleBlockHalf.UPPER ? var2.func_177977_b() : var2) : -1;
      }, Blocks.field_196805_gi, Blocks.field_196804_gh);
      var0.func_186722_a((var0x, var1, var2, var3) -> {
         return var1 != null && var2 != null ? BiomeColors.func_180286_a(var1, var2) : GrassColors.func_77480_a(0.5D, 1.0D);
      }, Blocks.field_196658_i, Blocks.field_196554_aH, Blocks.field_150349_c, Blocks.field_196683_eB);
      var0.func_186722_a((var0x, var1, var2, var3) -> {
         return FoliageColors.func_77466_a();
      }, Blocks.field_196645_X);
      var0.func_186722_a((var0x, var1, var2, var3) -> {
         return FoliageColors.func_77469_b();
      }, Blocks.field_196647_Y);
      var0.func_186722_a((var0x, var1, var2, var3) -> {
         return var1 != null && var2 != null ? BiomeColors.func_180287_b(var1, var2) : FoliageColors.func_77468_c();
      }, Blocks.field_196642_W, Blocks.field_196648_Z, Blocks.field_196572_aa, Blocks.field_196574_ab, Blocks.field_150395_bd);
      var0.func_186722_a((var0x, var1, var2, var3) -> {
         return var1 != null && var2 != null ? BiomeColors.func_180288_c(var1, var2) : -1;
      }, Blocks.field_150355_j, Blocks.field_203203_C, Blocks.field_150383_bp);
      var0.func_186722_a((var0x, var1, var2, var3) -> {
         return BlockRedstoneWire.func_176337_b((Integer)var0x.func_177229_b(BlockRedstoneWire.field_176351_O));
      }, Blocks.field_150488_af);
      var0.func_186722_a((var0x, var1, var2, var3) -> {
         return var1 != null && var2 != null ? BiomeColors.func_180286_a(var1, var2) : -1;
      }, Blocks.field_196608_cF);
      var0.func_186722_a((var0x, var1, var2, var3) -> {
         return 14731036;
      }, Blocks.field_196713_dt, Blocks.field_196711_ds);
      var0.func_186722_a((var0x, var1, var2, var3) -> {
         int var4 = (Integer)var0x.func_177229_b(BlockStem.field_176484_a);
         int var5 = var4 * 32;
         int var6 = 255 - var4 * 8;
         int var7 = var4 * 4;
         return var5 << 16 | var6 << 8 | var7;
      }, Blocks.field_150394_bc, Blocks.field_150393_bb);
      var0.func_186722_a((var0x, var1, var2, var3) -> {
         return var1 != null && var2 != null ? 2129968 : 7455580;
      }, Blocks.field_196651_dG);
      return var0;
   }

   public int func_189991_a(IBlockState var1, World var2, BlockPos var3) {
      IBlockColor var4 = (IBlockColor)this.field_186725_a.func_148745_a(IRegistry.field_212618_g.func_148757_b(var1.func_177230_c()));
      if (var4 != null) {
         return var4.getColor(var1, (IWorldReaderBase)null, (BlockPos)null, 0);
      } else {
         MaterialColor var5 = var1.func_185909_g(var2, var3);
         return var5 != null ? var5.field_76291_p : -1;
      }
   }

   public int func_186724_a(IBlockState var1, @Nullable IWorldReaderBase var2, @Nullable BlockPos var3, int var4) {
      IBlockColor var5 = (IBlockColor)this.field_186725_a.func_148745_a(IRegistry.field_212618_g.func_148757_b(var1.func_177230_c()));
      return var5 == null ? -1 : var5.getColor(var1, var2, var3, var4);
   }

   public void func_186722_a(IBlockColor var1, Block... var2) {
      Block[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Block var6 = var3[var5];
         this.field_186725_a.func_148746_a(var1, IRegistry.field_212618_g.func_148757_b(var6));
      }

   }
}
