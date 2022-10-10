package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class EmptyChunk extends Chunk {
   private static final Biome[] field_201623_e = (Biome[])Util.func_200696_a(new Biome[256], (var0) -> {
      Arrays.fill(var0, Biomes.field_76772_c);
   });

   public EmptyChunk(World var1, int var2, int var3) {
      super(var1, var2, var3, field_201623_e);
   }

   public boolean func_76600_a(int var1, int var2) {
      return var1 == this.field_76635_g && var2 == this.field_76647_h;
   }

   public void func_76590_a() {
   }

   public void func_76603_b() {
   }

   public IBlockState func_180495_p(BlockPos var1) {
      return Blocks.field_201940_ji.func_176223_P();
   }

   public int func_201587_a(EnumLightType var1, BlockPos var2, boolean var3) {
      return var1.field_77198_c;
   }

   public void func_201580_a(EnumLightType var1, boolean var2, BlockPos var3, int var4) {
   }

   public int func_201586_a(BlockPos var1, int var2, boolean var3) {
      return 0;
   }

   public void func_76612_a(Entity var1) {
   }

   public void func_76622_b(Entity var1) {
   }

   public void func_76608_a(Entity var1, int var2) {
   }

   public boolean func_177444_d(BlockPos var1) {
      return false;
   }

   @Nullable
   public TileEntity func_177424_a(BlockPos var1, Chunk.EnumCreateEntityType var2) {
      return null;
   }

   public void func_150813_a(TileEntity var1) {
   }

   public void func_177426_a(BlockPos var1, TileEntity var2) {
   }

   public void func_177425_e(BlockPos var1) {
   }

   public void func_76631_c() {
   }

   public void func_76623_d() {
   }

   public void func_76630_e() {
   }

   public void func_177414_a(@Nullable Entity var1, AxisAlignedBB var2, List<Entity> var3, Predicate<? super Entity> var4) {
   }

   public <T extends Entity> void func_177430_a(Class<? extends T> var1, AxisAlignedBB var2, List<T> var3, Predicate<? super T> var4) {
   }

   public boolean func_76601_a(boolean var1) {
      return false;
   }

   public boolean func_76621_g() {
      return true;
   }

   public boolean func_76606_c(int var1, int var2) {
      return true;
   }
}
