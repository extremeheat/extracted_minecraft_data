package net.minecraft.world.gen;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFalling;
import net.minecraft.init.Blocks;
import net.minecraft.util.ExpiringMap;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.WorldCarverWrapper;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

public abstract class AbstractChunkGenerator<C extends IChunkGenSettings> implements IChunkGenerator<C> {
   protected final IWorld field_202095_a;
   protected final long field_202096_b;
   protected final BiomeProvider field_202097_c;
   protected final Map<Structure<? extends IFeatureConfig>, Long2ObjectMap<StructureStart>> field_203227_d = Maps.newHashMap();
   protected final Map<Structure<? extends IFeatureConfig>, Long2ObjectMap<LongSet>> field_203228_e = Maps.newHashMap();

   public AbstractChunkGenerator(IWorld var1, BiomeProvider var2) {
      super();
      this.field_202095_a = var1;
      this.field_202096_b = var1.func_72905_C();
      this.field_202097_c = var2;
   }

   public void func_202091_a(WorldGenRegion var1, GenerationStage.Carving var2) {
      SharedSeedRandom var3 = new SharedSeedRandom(this.field_202096_b);
      boolean var4 = true;
      int var5 = var1.func_201679_a();
      int var6 = var1.func_201680_b();
      BitSet var7 = var1.func_72964_e(var5, var6).func_205749_a(var2);

      for(int var8 = var5 - 8; var8 <= var5 + 8; ++var8) {
         for(int var9 = var6 - 8; var9 <= var6 + 8; ++var9) {
            List var10 = var1.func_72863_F().func_201711_g().func_202090_b().func_180300_a(new BlockPos(var8 * 16, 0, var9 * 16), (Biome)null).func_203603_a(var2);
            ListIterator var11 = var10.listIterator();

            while(var11.hasNext()) {
               int var12 = var11.nextIndex();
               WorldCarverWrapper var13 = (WorldCarverWrapper)var11.next();
               var3.func_202425_c(var1.func_201672_e().func_72905_C() + (long)var12, var8, var9);
               if (var13.func_212246_a(var1, var3, var8, var9, (NoFeatureConfig)IFeatureConfig.field_202429_e)) {
                  var13.func_202522_a(var1, var3, var8, var9, var5, var6, var7, (NoFeatureConfig)IFeatureConfig.field_202429_e);
               }
            }
         }
      }

   }

   @Nullable
   public BlockPos func_211403_a(World var1, String var2, BlockPos var3, int var4, boolean var5) {
      Structure var6 = (Structure)Feature.field_202300_at.get(var2.toLowerCase(Locale.ROOT));
      return var6 != null ? var6.func_211405_a(var1, this, var3, var4, var5) : null;
   }

   protected void func_205472_a(IChunk var1, Random var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
      int var4 = var1.func_76632_l().func_180334_c();
      int var5 = var1.func_76632_l().func_180333_d();
      Iterator var6 = BlockPos.func_191532_a(var4, 0, var5, var4 + 16, 0, var5 + 16).iterator();

      while(var6.hasNext()) {
         BlockPos var7 = (BlockPos)var6.next();

         for(int var8 = 4; var8 >= 0; --var8) {
            if (var8 <= var2.nextInt(5)) {
               var1.func_177436_a(var3.func_181079_c(var7.func_177958_n(), var8, var7.func_177952_p()), Blocks.field_150357_h.func_176223_P(), false);
            }
         }
      }

   }

   public void func_202092_b(WorldGenRegion var1) {
      BlockFalling.field_149832_M = true;
      int var2 = var1.func_201679_a();
      int var3 = var1.func_201680_b();
      int var4 = var2 * 16;
      int var5 = var3 * 16;
      BlockPos var6 = new BlockPos(var4, 0, var5);
      Biome var7 = var1.func_72964_e(var2 + 1, var3 + 1).func_201590_e()[0];
      SharedSeedRandom var8 = new SharedSeedRandom();
      long var9 = var8.func_202424_a(var1.func_72905_C(), var4, var5);
      GenerationStage.Decoration[] var11 = GenerationStage.Decoration.values();
      int var12 = var11.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         GenerationStage.Decoration var14 = var11[var13];
         var7.func_203608_a(var14, this, var1, var9, var8, var6);
      }

      BlockFalling.field_149832_M = false;
   }

   public void func_205471_a(IChunk var1, Biome[] var2, SharedSeedRandom var3, int var4) {
      double var5 = 0.03125D;
      ChunkPos var7 = var1.func_76632_l();
      int var8 = var7.func_180334_c();
      int var9 = var7.func_180333_d();
      double[] var10 = this.func_205473_a(var7.field_77276_a, var7.field_77275_b);

      for(int var11 = 0; var11 < 16; ++var11) {
         for(int var12 = 0; var12 < 16; ++var12) {
            int var13 = var8 + var11;
            int var14 = var9 + var12;
            int var15 = var1.func_201576_a(Heightmap.Type.WORLD_SURFACE_WG, var11, var12) + 1;
            var2[var12 * 16 + var11].func_206854_a(var3, var1, var13, var14, var15, var10[var12 * 16 + var11], this.func_201496_a_().func_205532_l(), this.func_201496_a_().func_205533_m(), var4, this.field_202095_a.func_72905_C());
         }
      }

   }

   public abstract C func_201496_a_();

   public abstract double[] func_205473_a(int var1, int var2);

   public boolean func_202094_a(Biome var1, Structure<? extends IFeatureConfig> var2) {
      return var1.func_201858_a(var2);
   }

   @Nullable
   public IFeatureConfig func_202087_b(Biome var1, Structure<? extends IFeatureConfig> var2) {
      return var1.func_201857_b(var2);
   }

   public BiomeProvider func_202090_b() {
      return this.field_202097_c;
   }

   public long func_202089_c() {
      return this.field_202096_b;
   }

   public Long2ObjectMap<StructureStart> func_203224_a(Structure<? extends IFeatureConfig> var1) {
      return (Long2ObjectMap)this.field_203227_d.computeIfAbsent(var1, (var0) -> {
         return Long2ObjectMaps.synchronize(new ExpiringMap(8192, 10000));
      });
   }

   public Long2ObjectMap<LongSet> func_203223_b(Structure<? extends IFeatureConfig> var1) {
      return (Long2ObjectMap)this.field_203228_e.computeIfAbsent(var1, (var0) -> {
         return Long2ObjectMaps.synchronize(new ExpiringMap(8192, 10000));
      });
   }

   public int func_207511_e() {
      return 256;
   }
}
