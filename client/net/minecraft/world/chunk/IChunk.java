package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ITickList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.apache.logging.log4j.LogManager;

public interface IChunk extends IBlockReader {
   @Nullable
   IBlockState func_177436_a(BlockPos var1, IBlockState var2, boolean var3);

   void func_177426_a(BlockPos var1, TileEntity var2);

   void func_76612_a(Entity var1);

   void func_201574_a(ChunkStatus var1);

   @Nullable
   default ChunkSection func_186031_y() {
      ChunkSection[] var1 = this.func_76587_i();

      for(int var2 = var1.length - 1; var2 >= 0; --var2) {
         if (var1[var2] != Chunk.field_186036_a) {
            return var1[var2];
         }
      }

      return null;
   }

   default int func_76625_h() {
      ChunkSection var1 = this.func_186031_y();
      return var1 == null ? 0 : var1.func_76662_d();
   }

   ChunkSection[] func_76587_i();

   int func_201587_a(EnumLightType var1, BlockPos var2, boolean var3);

   int func_201586_a(BlockPos var1, int var2, boolean var3);

   boolean func_177444_d(BlockPos var1);

   int func_201576_a(Heightmap.Type var1, int var2, int var3);

   ChunkPos func_76632_l();

   void func_177432_b(long var1);

   @Nullable
   StructureStart func_201585_a(String var1);

   void func_201584_a(String var1, StructureStart var2);

   Map<String, StructureStart> func_201609_c();

   @Nullable
   LongSet func_201578_b(String var1);

   void func_201583_a(String var1, long var2);

   Map<String, LongSet> func_201604_d();

   Biome[] func_201590_e();

   ChunkStatus func_201589_g();

   void func_177425_e(BlockPos var1);

   void func_201580_a(EnumLightType var1, boolean var2, BlockPos var3, int var4);

   default void func_201594_d(BlockPos var1) {
      LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", var1);
   }

   default void func_201591_a(NBTTagCompound var1) {
      LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
   }

   @Nullable
   default NBTTagCompound func_201579_g(BlockPos var1) {
      throw new UnsupportedOperationException();
   }

   default void func_201577_a(Biome[] var1) {
      throw new UnsupportedOperationException();
   }

   default void func_201588_a(Heightmap.Type... var1) {
      throw new UnsupportedOperationException();
   }

   default List<BlockPos> func_201582_h() {
      throw new UnsupportedOperationException();
   }

   ITickList<Block> func_205218_i_();

   ITickList<Fluid> func_212247_j();

   BitSet func_205749_a(GenerationStage.Carving var1);
}
