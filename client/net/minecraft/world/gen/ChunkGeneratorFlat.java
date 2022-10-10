package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.PhantomSpawner;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.WorldCarverWrapper;
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkGeneratorFlat extends AbstractChunkGenerator<FlatGenSettings> {
   private static final Logger field_202101_d = LogManager.getLogger();
   private final FlatGenSettings field_82699_e;
   private final Biome field_202103_f;
   private final PhantomSpawner field_203229_i = new PhantomSpawner();

   public ChunkGeneratorFlat(IWorld var1, BiomeProvider var2, FlatGenSettings var3) {
      super(var1, var2);
      this.field_82699_e = var3;
      this.field_202103_f = this.func_202099_e();
   }

   private Biome func_202099_e() {
      Biome var1 = this.field_82699_e.func_82648_a();
      ChunkGeneratorFlat.BiomeWrapper var2 = new ChunkGeneratorFlat.BiomeWrapper(var1.func_205401_q(), var1.func_201851_b(), var1.func_201856_r(), var1.func_185355_j(), var1.func_185360_m(), var1.func_185353_n(), var1.func_76727_i(), var1.func_185361_o(), var1.func_204274_p(), var1.func_205402_s());
      Map var3 = this.field_82699_e.func_82644_b();
      Iterator var4 = var3.keySet().iterator();

      while(true) {
         CompositeFeature[] var6;
         int var8;
         do {
            if (!var4.hasNext()) {
               boolean var13 = (!this.field_82699_e.func_202238_o() || var1 == Biomes.field_185440_P) && var3.containsKey("decoration");
               if (var13) {
                  ArrayList var14 = Lists.newArrayList();
                  var14.add(GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
                  var14.add(GenerationStage.Decoration.SURFACE_STRUCTURES);
                  GenerationStage.Decoration[] var15 = GenerationStage.Decoration.values();
                  int var16 = var15.length;

                  for(var8 = 0; var8 < var16; ++var8) {
                     GenerationStage.Decoration var17 = var15[var8];
                     if (!var14.contains(var17)) {
                        Iterator var18 = var1.func_203607_a(var17).iterator();

                        while(var18.hasNext()) {
                           CompositeFeature var19 = (CompositeFeature)var18.next();
                           var2.func_203611_a(var17, var19);
                        }
                     }
                  }
               }

               return var2;
            }

            String var5 = (String)var4.next();
            var6 = (CompositeFeature[])FlatGenSettings.field_202247_j.get(var5);
         } while(var6 == null);

         CompositeFeature[] var7 = var6;
         var8 = var6.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            CompositeFeature var10 = var7[var9];
            var2.func_203611_a((GenerationStage.Decoration)FlatGenSettings.field_202248_k.get(var10), var10);
            Feature var11 = var10.func_202349_a();
            if (var11 instanceof Structure) {
               IFeatureConfig var12 = var1.func_201857_b((Structure)var11);
               var2.func_201865_a((Structure)var11, var12 != null ? var12 : (IFeatureConfig)FlatGenSettings.field_202249_l.get(var10));
            }
         }
      }
   }

   public void func_202088_a(IChunk var1) {
      ChunkPos var2 = var1.func_76632_l();
      int var3 = var2.field_77276_a;
      int var4 = var2.field_77275_b;
      Biome[] var5 = this.field_202097_c.func_201539_b(var3 * 16, var4 * 16, 16, 16);
      var1.func_201577_a(var5);
      this.func_202100_a(var3, var4, var1);
      var1.func_201588_a(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
      var1.func_201574_a(ChunkStatus.BASE);
   }

   public void func_202091_a(WorldGenRegion var1, GenerationStage.Carving var2) {
      boolean var3 = true;
      int var4 = var1.func_201679_a();
      int var5 = var1.func_201680_b();
      BitSet var6 = new BitSet(65536);
      SharedSeedRandom var7 = new SharedSeedRandom();

      for(int var8 = var4 - 8; var8 <= var4 + 8; ++var8) {
         for(int var9 = var5 - 8; var9 <= var5 + 8; ++var9) {
            List var10 = this.field_202103_f.func_203603_a(GenerationStage.Carving.AIR);
            ListIterator var11 = var10.listIterator();

            while(var11.hasNext()) {
               int var12 = var11.nextIndex();
               WorldCarverWrapper var13 = (WorldCarverWrapper)var11.next();
               var7.func_202425_c(var1.func_201672_e().func_72905_C() + (long)var12, var8, var9);
               if (var13.func_212246_a(var1, var7, var8, var9, (NoFeatureConfig)IFeatureConfig.field_202429_e)) {
                  var13.func_202522_a(var1, var7, var8, var9, var4, var5, var6, (NoFeatureConfig)IFeatureConfig.field_202429_e);
               }
            }
         }
      }

   }

   public FlatGenSettings func_201496_a_() {
      return this.field_82699_e;
   }

   public double[] func_205473_a(int var1, int var2) {
      return new double[0];
   }

   public int func_205470_d() {
      IChunk var1 = this.field_202095_a.func_72964_e(0, 0);
      return var1.func_201576_a(Heightmap.Type.MOTION_BLOCKING, 8, 8);
   }

   public void func_202092_b(WorldGenRegion var1) {
      int var2 = var1.func_201679_a();
      int var3 = var1.func_201680_b();
      int var4 = var2 * 16;
      int var5 = var3 * 16;
      BlockPos var6 = new BlockPos(var4, 0, var5);
      SharedSeedRandom var7 = new SharedSeedRandom();
      long var8 = var7.func_202424_a(var1.func_72905_C(), var4, var5);
      GenerationStage.Decoration[] var10 = GenerationStage.Decoration.values();
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         GenerationStage.Decoration var13 = var10[var12];
         this.field_202103_f.func_203608_a(var13, this, var1, var8, var7, var6);
      }

   }

   public void func_202093_c(WorldGenRegion var1) {
   }

   public void func_202100_a(int var1, int var2, IChunk var3) {
      IBlockState[] var4 = this.field_82699_e.func_202233_q();
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

      for(int var6 = 0; var6 < var4.length; ++var6) {
         IBlockState var7 = var4[var6];
         if (var7 != null) {
            for(int var8 = 0; var8 < 16; ++var8) {
               for(int var9 = 0; var9 < 16; ++var9) {
                  var3.func_177436_a(var5.func_181079_c(var8, var6, var9), var7, false);
               }
            }
         }
      }

   }

   public List<Biome.SpawnListEntry> func_177458_a(EnumCreatureType var1, BlockPos var2) {
      Biome var3 = this.field_202095_a.func_180494_b(var2);
      return var3.func_76747_a(var1);
   }

   public int func_203222_a(World var1, boolean var2, boolean var3) {
      byte var4 = 0;
      int var5 = var4 + this.field_203229_i.func_203232_a(var1, var2, var3);
      return var5;
   }

   public boolean func_202094_a(Biome var1, Structure<? extends IFeatureConfig> var2) {
      return this.field_202103_f.func_201858_a(var2);
   }

   @Nullable
   public IFeatureConfig func_202087_b(Biome var1, Structure<? extends IFeatureConfig> var2) {
      return this.field_202103_f.func_201857_b(var2);
   }

   @Nullable
   public BlockPos func_211403_a(World var1, String var2, BlockPos var3, int var4, boolean var5) {
      return !this.field_82699_e.func_82644_b().keySet().contains(var2) ? null : super.func_211403_a(var1, var2, var3, var4, var5);
   }

   // $FF: synthetic method
   public IChunkGenSettings func_201496_a_() {
      return this.func_201496_a_();
   }

   class BiomeWrapper extends Biome {
      protected BiomeWrapper(CompositeSurfaceBuilder<?> var2, Biome.RainType var3, Biome.Category var4, float var5, float var6, float var7, float var8, int var9, int var10, String var11) {
         super((new Biome.BiomeBuilder()).func_205416_a(var2).func_205415_a(var3).func_205419_a(var4).func_205421_a(var5).func_205420_b(var6).func_205414_c(var7).func_205417_d(var8).func_205412_a(var9).func_205413_b(var10).func_205418_a(var11));
      }
   }
}
