package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

public interface IChunkGenerator<C extends IChunkGenSettings> {
   void func_202088_a(IChunk var1);

   void func_202091_a(WorldGenRegion var1, GenerationStage.Carving var2);

   void func_202092_b(WorldGenRegion var1);

   void func_202093_c(WorldGenRegion var1);

   List<Biome.SpawnListEntry> func_177458_a(EnumCreatureType var1, BlockPos var2);

   @Nullable
   BlockPos func_211403_a(World var1, String var2, BlockPos var3, int var4, boolean var5);

   C func_201496_a_();

   int func_203222_a(World var1, boolean var2, boolean var3);

   boolean func_202094_a(Biome var1, Structure<? extends IFeatureConfig> var2);

   @Nullable
   IFeatureConfig func_202087_b(Biome var1, Structure<? extends IFeatureConfig> var2);

   Long2ObjectMap<StructureStart> func_203224_a(Structure<? extends IFeatureConfig> var1);

   Long2ObjectMap<LongSet> func_203223_b(Structure<? extends IFeatureConfig> var1);

   BiomeProvider func_202090_b();

   long func_202089_c();

   int func_205470_d();

   int func_207511_e();
}
