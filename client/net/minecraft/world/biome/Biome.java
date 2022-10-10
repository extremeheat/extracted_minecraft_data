package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.FoliageColors;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.carver.CanyonWorldCarver;
import net.minecraft.world.gen.carver.CaveWorldCarver;
import net.minecraft.world.gen.carver.IWorldCarver;
import net.minecraft.world.gen.carver.NetherCaveWorldCarver;
import net.minecraft.world.gen.carver.UnderwaterCanyonWorldCarver;
import net.minecraft.world.gen.carver.UnderwaterCaveWorldCarver;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.carver.WorldCarverWrapper;
import net.minecraft.world.gen.feature.AbstractFlowersFeature;
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.CompositeFlowerFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.structure.BuriedTreasureConfig;
import net.minecraft.world.gen.feature.structure.DesertPyramidConfig;
import net.minecraft.world.gen.feature.structure.IglooConfig;
import net.minecraft.world.gen.feature.structure.JunglePyramidConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanMonumentConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.StrongholdConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.SwampHutConfig;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.VillagePieces;
import net.minecraft.world.gen.feature.structure.WoodlandMansionConfig;
import net.minecraft.world.gen.placement.AtHeight64;
import net.minecraft.world.gen.placement.AtSurface;
import net.minecraft.world.gen.placement.AtSurfaceRandomCount;
import net.minecraft.world.gen.placement.AtSurfaceWithChance;
import net.minecraft.world.gen.placement.AtSurfaceWithChanceMultiple;
import net.minecraft.world.gen.placement.AtSurfaceWithExtra;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.BasePlacement;
import net.minecraft.world.gen.placement.CaveEdge;
import net.minecraft.world.gen.placement.CaveEdgeConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.ChanceRange;
import net.minecraft.world.gen.placement.ChanceRangeConfig;
import net.minecraft.world.gen.placement.ChorusPlant;
import net.minecraft.world.gen.placement.CountRange;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.DepthAverage;
import net.minecraft.world.gen.placement.DepthAverageConfig;
import net.minecraft.world.gen.placement.DungeonRoom;
import net.minecraft.world.gen.placement.DungeonRoomConfig;
import net.minecraft.world.gen.placement.EndGateway;
import net.minecraft.world.gen.placement.EndIsland;
import net.minecraft.world.gen.placement.EndSpikes;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Height4To32;
import net.minecraft.world.gen.placement.HeightBiasedRange;
import net.minecraft.world.gen.placement.HeightVeryBiasedRange;
import net.minecraft.world.gen.placement.HeightWithChanceConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.IcebergPlacement;
import net.minecraft.world.gen.placement.LakeChanceConfig;
import net.minecraft.world.gen.placement.LakeLava;
import net.minecraft.world.gen.placement.LakeWater;
import net.minecraft.world.gen.placement.NetherFire;
import net.minecraft.world.gen.placement.NetherGlowstone;
import net.minecraft.world.gen.placement.NetherMagma;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.NoiseDependant;
import net.minecraft.world.gen.placement.Passthrough;
import net.minecraft.world.gen.placement.RandomCountWithRange;
import net.minecraft.world.gen.placement.RoofedTree;
import net.minecraft.world.gen.placement.SurfacePlus32;
import net.minecraft.world.gen.placement.SurfacePlus32WithNoise;
import net.minecraft.world.gen.placement.TopSolid;
import net.minecraft.world.gen.placement.TopSolidOnce;
import net.minecraft.world.gen.placement.TopSolidRange;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraft.world.gen.placement.TopSolidWithChance;
import net.minecraft.world.gen.placement.TopSolidWithNoise;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraft.world.gen.placement.TwiceSurface;
import net.minecraft.world.gen.placement.TwiceSurfaceWithChance;
import net.minecraft.world.gen.placement.TwiceSurfaceWithChanceMultiple;
import net.minecraft.world.gen.placement.TwiceSurfaceWithNoise;
import net.minecraft.world.gen.placement.WithChance;
import net.minecraft.world.gen.surfacebuilders.CompositeSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.DefaultSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ExtremeHillsMutatedSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ExtremeHillsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.FrozenOceanSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.MesaBryceSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.MesaForestSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.MesaSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.NetherSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.NoopSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SavanaMutatedSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SwampSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.TaigaMegaSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Biome {
   public static final Logger field_150586_aC = LogManager.getLogger();
   public static final WorldCarver<ProbabilityConfig> field_201907_b = new CaveWorldCarver();
   public static final WorldCarver<ProbabilityConfig> field_201908_c = new NetherCaveWorldCarver();
   public static final WorldCarver<ProbabilityConfig> field_201909_d = new CanyonWorldCarver();
   public static final WorldCarver<ProbabilityConfig> field_203612_e = new UnderwaterCanyonWorldCarver();
   public static final WorldCarver<ProbabilityConfig> field_203613_f = new UnderwaterCaveWorldCarver();
   public static final BasePlacement<FrequencyConfig> field_201910_e = new AtSurface();
   public static final BasePlacement<FrequencyConfig> field_201911_f = new TopSolid();
   public static final BasePlacement<FrequencyConfig> field_201912_g = new SurfacePlus32();
   public static final BasePlacement<FrequencyConfig> field_201913_h = new TwiceSurface();
   public static final BasePlacement<FrequencyConfig> field_201914_i = new AtHeight64();
   public static final BasePlacement<NoiseDependant> field_201915_j = new SurfacePlus32WithNoise();
   public static final BasePlacement<NoiseDependant> field_201916_k = new TwiceSurfaceWithNoise();
   public static final BasePlacement<NoPlacementConfig> field_201917_l = new Passthrough();
   public static final BasePlacement<ChanceConfig> field_201919_n = new AtSurfaceWithChance();
   public static final BasePlacement<ChanceConfig> field_201920_o = new TwiceSurfaceWithChance();
   public static final BasePlacement<ChanceConfig> field_201921_p = new WithChance();
   public static final BasePlacement<ChanceConfig> field_204908_s = new TopSolidWithChance();
   public static final BasePlacement<AtSurfaceWithExtraConfig> field_201922_q = new AtSurfaceWithExtra();
   public static final BasePlacement<CountRangeConfig> field_201923_r = new CountRange();
   public static final BasePlacement<CountRangeConfig> field_201924_s = new HeightBiasedRange();
   public static final BasePlacement<CountRangeConfig> field_201925_t = new HeightVeryBiasedRange();
   public static final BasePlacement<CountRangeConfig> field_205162_x = new RandomCountWithRange();
   public static final BasePlacement<ChanceRangeConfig> field_201926_u = new ChanceRange();
   public static final BasePlacement<HeightWithChanceConfig> field_201927_v = new AtSurfaceWithChanceMultiple();
   public static final BasePlacement<HeightWithChanceConfig> field_201928_w = new TwiceSurfaceWithChanceMultiple();
   public static final BasePlacement<DepthAverageConfig> field_201929_x = new DepthAverage();
   public static final BasePlacement<NoPlacementConfig> field_203197_y = new TopSolidOnce();
   public static final BasePlacement<TopSolidRangeConfig> field_204617_B = new TopSolidRange();
   public static final BasePlacement<TopSolidWithNoiseConfig> field_204618_C = new TopSolidWithNoise();
   public static final BasePlacement<CaveEdgeConfig> field_206855_F = new CaveEdge();
   public static final BasePlacement<FrequencyConfig> field_201930_y = new AtSurfaceRandomCount();
   public static final BasePlacement<FrequencyConfig> field_201931_z = new NetherFire();
   public static final BasePlacement<FrequencyConfig> field_201881_A = new NetherMagma();
   public static final BasePlacement<NoPlacementConfig> field_201882_B = new Height4To32();
   public static final BasePlacement<LakeChanceConfig> field_201883_C = new LakeLava();
   public static final BasePlacement<LakeChanceConfig> field_201884_D = new LakeWater();
   public static final BasePlacement<DungeonRoomConfig> field_201885_E = new DungeonRoom();
   public static final BasePlacement<NoPlacementConfig> field_201886_F = new RoofedTree();
   public static final BasePlacement<ChanceConfig> field_205161_N = new IcebergPlacement();
   public static final BasePlacement<FrequencyConfig> field_201887_G = new NetherGlowstone();
   public static final BasePlacement<NoPlacementConfig> field_201888_H = new EndSpikes();
   public static final BasePlacement<NoPlacementConfig> field_201889_I = new EndIsland();
   public static final BasePlacement<NoPlacementConfig> field_201890_J = new ChorusPlant();
   public static final BasePlacement<NoPlacementConfig> field_201891_K = new EndGateway();
   protected static final IBlockState field_205411_T;
   protected static final IBlockState field_203802_aB;
   protected static final IBlockState field_203803_aC;
   protected static final IBlockState field_203804_aD;
   protected static final IBlockState field_203805_aE;
   protected static final IBlockState field_203806_aF;
   protected static final IBlockState field_203807_aG;
   protected static final IBlockState field_203956_U;
   protected static final IBlockState field_203957_V;
   protected static final IBlockState field_203958_W;
   protected static final IBlockState field_203959_X;
   protected static final IBlockState field_205406_ae;
   protected static final IBlockState field_205407_af;
   public static final SurfaceBuilderConfig field_205408_ag;
   public static final SurfaceBuilderConfig field_203960_Y;
   public static final SurfaceBuilderConfig field_203961_Z;
   public static final SurfaceBuilderConfig field_203946_aa;
   public static final SurfaceBuilderConfig field_203947_ab;
   public static final SurfaceBuilderConfig field_203948_ac;
   public static final SurfaceBuilderConfig field_203949_ad;
   public static final SurfaceBuilderConfig field_203950_ae;
   public static final SurfaceBuilderConfig field_203951_af;
   public static final SurfaceBuilderConfig field_203952_ag;
   public static final SurfaceBuilderConfig field_203953_ah;
   public static final SurfaceBuilderConfig field_203954_ai;
   public static final SurfaceBuilderConfig field_205409_as;
   public static final SurfaceBuilderConfig field_205410_at;
   public static final ISurfaceBuilder<SurfaceBuilderConfig> field_203955_aj;
   public static final ISurfaceBuilder<SurfaceBuilderConfig> field_201899_S;
   public static final ISurfaceBuilder<SurfaceBuilderConfig> field_201900_T;
   public static final ISurfaceBuilder<SurfaceBuilderConfig> field_201901_U;
   public static final ISurfaceBuilder<SurfaceBuilderConfig> field_201902_V;
   public static final ISurfaceBuilder<SurfaceBuilderConfig> field_201903_W;
   public static final ISurfaceBuilder<SurfaceBuilderConfig> field_201904_X;
   public static final ISurfaceBuilder<SurfaceBuilderConfig> field_201905_Y;
   public static final ISurfaceBuilder<SurfaceBuilderConfig> field_201906_Z;
   public static final ISurfaceBuilder<SurfaceBuilderConfig> field_205160_ax;
   public static final ISurfaceBuilder<SurfaceBuilderConfig> field_205404_aE;
   public static final ISurfaceBuilder<SurfaceBuilderConfig> field_201869_aa;
   public static final Set<Biome> field_201870_ab;
   public static final ObjectIntIdentityMap<Biome> field_185373_j;
   protected static final NoiseGeneratorPerlin field_150605_ac;
   public static final NoiseGeneratorPerlin field_180281_af;
   @Nullable
   protected String field_205405_aL;
   protected final float field_76748_D;
   protected final float field_76749_E;
   protected final float field_76750_F;
   protected final float field_76751_G;
   protected final int field_76759_H;
   protected final int field_204275_aE;
   @Nullable
   protected final String field_185364_H;
   protected final CompositeSurfaceBuilder<?> field_201875_ar;
   protected final Biome.Category field_201877_au;
   protected final Biome.RainType field_201878_av;
   protected final Map<GenerationStage.Carving, List<WorldCarverWrapper<?>>> field_201871_ag = Maps.newHashMap();
   protected final Map<GenerationStage.Decoration, List<CompositeFeature<?, ?>>> field_201872_ah = Maps.newHashMap();
   protected final List<CompositeFlowerFeature<?>> field_201873_ai = Lists.newArrayList();
   protected final Map<Structure<?>, IFeatureConfig> field_201874_aj = Maps.newHashMap();
   private final Map<EnumCreatureType, List<Biome.SpawnListEntry>> field_201880_ax = Maps.newHashMap();

   @Nullable
   public static Biome func_185356_b(Biome var0) {
      return (Biome)field_185373_j.func_148745_a(IRegistry.field_212624_m.func_148757_b(var0));
   }

   public static <C extends IFeatureConfig> WorldCarverWrapper<C> func_203606_a(IWorldCarver<C> var0, C var1) {
      return new WorldCarverWrapper(var0, var1);
   }

   public static <F extends IFeatureConfig, D extends IPlacementConfig> CompositeFeature<F, D> func_201864_a(Feature<F> var0, F var1, BasePlacement<D> var2, D var3) {
      return new CompositeFeature(var0, var1, var2, var3);
   }

   public static <D extends IPlacementConfig> CompositeFlowerFeature<D> func_201861_a(AbstractFlowersFeature var0, BasePlacement<D> var1, D var2) {
      return new CompositeFlowerFeature(var0, var1, var2);
   }

   protected Biome(Biome.BiomeBuilder var1) {
      super();
      if (var1.field_205422_a != null && var1.field_205423_b != null && var1.field_205424_c != null && var1.field_205425_d != null && var1.field_205426_e != null && var1.field_205427_f != null && var1.field_205428_g != null && var1.field_205429_h != null && var1.field_205430_i != null) {
         this.field_201875_ar = var1.field_205422_a;
         this.field_201878_av = var1.field_205423_b;
         this.field_201877_au = var1.field_205424_c;
         this.field_76748_D = var1.field_205425_d;
         this.field_76749_E = var1.field_205426_e;
         this.field_76750_F = var1.field_205427_f;
         this.field_76751_G = var1.field_205428_g;
         this.field_76759_H = var1.field_205429_h;
         this.field_204275_aE = var1.field_205430_i;
         this.field_185364_H = var1.field_205431_j;
         GenerationStage.Decoration[] var2 = GenerationStage.Decoration.values();
         int var3 = var2.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            GenerationStage.Decoration var5 = var2[var4];
            this.field_201872_ah.put(var5, Lists.newArrayList());
         }

         EnumCreatureType[] var6 = EnumCreatureType.values();
         var3 = var6.length;

         for(var4 = 0; var4 < var3; ++var4) {
            EnumCreatureType var7 = var6[var4];
            this.field_201880_ax.put(var7, Lists.newArrayList());
         }

      } else {
         throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + var1);
      }
   }

   protected void func_203605_a() {
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, func_201864_a(Feature.field_202329_g, new MineshaftConfig(0.004000000189989805D, MineshaftStructure.Type.NORMAL), field_201917_l, IPlacementConfig.field_202468_e));
      this.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, func_201864_a(Feature.field_202328_f, new VillageConfig(0, VillagePieces.Type.OAK), field_201917_l, IPlacementConfig.field_202468_e));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, func_201864_a(Feature.field_202335_m, new StrongholdConfig(), field_201917_l, IPlacementConfig.field_202468_e));
      this.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, func_201864_a(Feature.field_202334_l, new SwampHutConfig(), field_201917_l, IPlacementConfig.field_202468_e));
      this.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, func_201864_a(Feature.field_202332_j, new DesertPyramidConfig(), field_201917_l, IPlacementConfig.field_202468_e));
      this.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, func_201864_a(Feature.field_202331_i, new JunglePyramidConfig(), field_201917_l, IPlacementConfig.field_202468_e));
      this.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, func_201864_a(Feature.field_202333_k, new IglooConfig(), field_201917_l, IPlacementConfig.field_202468_e));
      this.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, func_201864_a(Feature.field_204751_l, new ShipwreckConfig(false), field_201917_l, IPlacementConfig.field_202468_e));
      this.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, func_201864_a(Feature.field_202336_n, new OceanMonumentConfig(), field_201917_l, IPlacementConfig.field_202468_e));
      this.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, func_201864_a(Feature.field_202330_h, new WoodlandMansionConfig(), field_201917_l, IPlacementConfig.field_202468_e));
      this.func_203611_a(GenerationStage.Decoration.SURFACE_STRUCTURES, func_201864_a(Feature.field_204029_o, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F), field_201917_l, IPlacementConfig.field_202468_e));
      this.func_203611_a(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, func_201864_a(Feature.field_204292_r, new BuriedTreasureConfig(0.01F), field_201917_l, IPlacementConfig.field_202468_e));
   }

   public boolean func_185363_b() {
      return this.field_185364_H != null;
   }

   public int func_76731_a(float var1) {
      var1 /= 3.0F;
      var1 = MathHelper.func_76131_a(var1, -1.0F, 1.0F);
      return MathHelper.func_181758_c(0.62222224F - var1 * 0.05F, 0.5F + var1 * 0.1F, 1.0F);
   }

   protected void func_201866_a(EnumCreatureType var1, Biome.SpawnListEntry var2) {
      ((List)this.field_201880_ax.get(var1)).add(var2);
   }

   public List<Biome.SpawnListEntry> func_76747_a(EnumCreatureType var1) {
      return (List)this.field_201880_ax.get(var1);
   }

   public Biome.RainType func_201851_b() {
      return this.field_201878_av;
   }

   public boolean func_76736_e() {
      return this.func_76727_i() > 0.85F;
   }

   public float func_76741_f() {
      return 0.1F;
   }

   public float func_180626_a(BlockPos var1) {
      if (var1.func_177956_o() > 64) {
         float var2 = (float)(field_150605_ac.func_151601_a((double)((float)var1.func_177958_n() / 8.0F), (double)((float)var1.func_177952_p() / 8.0F)) * 4.0D);
         return this.func_185353_n() - (var2 + (float)var1.func_177956_o() - 64.0F) * 0.05F / 30.0F;
      } else {
         return this.func_185353_n();
      }
   }

   public boolean func_201848_a(IWorldReaderBase var1, BlockPos var2) {
      return this.func_201854_a(var1, var2, true);
   }

   public boolean func_201854_a(IWorldReaderBase var1, BlockPos var2, boolean var3) {
      if (this.func_180626_a(var2) >= 0.15F) {
         return false;
      } else {
         if (var2.func_177956_o() >= 0 && var2.func_177956_o() < 256 && var1.func_175642_b(EnumLightType.BLOCK, var2) < 10) {
            IBlockState var4 = var1.func_180495_p(var2);
            IFluidState var5 = var1.func_204610_c(var2);
            if (var5.func_206886_c() == Fluids.field_204546_a && var4.func_177230_c() instanceof BlockFlowingFluid) {
               if (!var3) {
                  return true;
               }

               boolean var6 = var1.func_201671_F(var2.func_177976_e()) && var1.func_201671_F(var2.func_177974_f()) && var1.func_201671_F(var2.func_177978_c()) && var1.func_201671_F(var2.func_177968_d());
               if (!var6) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean func_201850_b(IWorldReaderBase var1, BlockPos var2) {
      if (this.func_180626_a(var2) >= 0.15F) {
         return false;
      } else {
         if (var2.func_177956_o() >= 0 && var2.func_177956_o() < 256 && var1.func_175642_b(EnumLightType.BLOCK, var2) < 10) {
            IBlockState var3 = var1.func_180495_p(var2);
            if (var3.func_196958_f() && Blocks.field_150433_aE.func_176223_P().func_196955_c(var1, var2)) {
               return true;
            }
         }

         return false;
      }
   }

   public void func_203611_a(GenerationStage.Decoration var1, CompositeFeature<?, ?> var2) {
      if (var2 instanceof CompositeFlowerFeature) {
         this.field_201873_ai.add((CompositeFlowerFeature)var2);
      }

      ((List)this.field_201872_ah.get(var1)).add(var2);
   }

   public <C extends IFeatureConfig> void func_203609_a(GenerationStage.Carving var1, WorldCarverWrapper<C> var2) {
      ((List)this.field_201871_ag.computeIfAbsent(var1, (var0) -> {
         return Lists.newArrayList();
      })).add(var2);
   }

   public List<WorldCarverWrapper<?>> func_203603_a(GenerationStage.Carving var1) {
      return (List)this.field_201871_ag.computeIfAbsent(var1, (var0) -> {
         return Lists.newArrayList();
      });
   }

   public <C extends IFeatureConfig> void func_201865_a(Structure<C> var1, C var2) {
      this.field_201874_aj.put(var1, var2);
   }

   public <C extends IFeatureConfig> boolean func_201858_a(Structure<C> var1) {
      return this.field_201874_aj.containsKey(var1);
   }

   @Nullable
   public <C extends IFeatureConfig> IFeatureConfig func_201857_b(Structure<C> var1) {
      return (IFeatureConfig)this.field_201874_aj.get(var1);
   }

   public List<CompositeFlowerFeature<?>> func_201853_g() {
      return this.field_201873_ai;
   }

   public List<CompositeFeature<?, ?>> func_203607_a(GenerationStage.Decoration var1) {
      return (List)this.field_201872_ah.get(var1);
   }

   public void func_203608_a(GenerationStage.Decoration var1, IChunkGenerator<? extends IChunkGenSettings> var2, IWorld var3, long var4, SharedSeedRandom var6, BlockPos var7) {
      int var8 = 0;

      for(Iterator var9 = ((List)this.field_201872_ah.get(var1)).iterator(); var9.hasNext(); ++var8) {
         CompositeFeature var10 = (CompositeFeature)var9.next();
         var6.func_202426_b(var4, var8, var1.ordinal());
         var10.func_212245_a(var3, var2, var6, var7, (NoFeatureConfig)IFeatureConfig.field_202429_e);
      }

   }

   public int func_180627_b(BlockPos var1) {
      double var2 = (double)MathHelper.func_76131_a(this.func_180626_a(var1), 0.0F, 1.0F);
      double var4 = (double)MathHelper.func_76131_a(this.func_76727_i(), 0.0F, 1.0F);
      return GrassColors.func_77480_a(var2, var4);
   }

   public int func_180625_c(BlockPos var1) {
      double var2 = (double)MathHelper.func_76131_a(this.func_180626_a(var1), 0.0F, 1.0F);
      double var4 = (double)MathHelper.func_76131_a(this.func_76727_i(), 0.0F, 1.0F);
      return FoliageColors.func_77470_a(var2, var4);
   }

   public void func_206854_a(Random var1, IChunk var2, int var3, int var4, int var5, double var6, IBlockState var8, IBlockState var9, int var10, long var11) {
      this.field_201875_ar.func_205548_a(var11);
      this.field_201875_ar.func_205610_a_(var1, var2, this, var3, var4, var5, var6, var8, var9, var10, var11, field_205408_ag);
   }

   public Biome.TempCategory func_150561_m() {
      if (this.field_201877_au == Biome.Category.OCEAN) {
         return Biome.TempCategory.OCEAN;
      } else if ((double)this.func_185353_n() < 0.2D) {
         return Biome.TempCategory.COLD;
      } else {
         return (double)this.func_185353_n() < 1.0D ? Biome.TempCategory.MEDIUM : Biome.TempCategory.WARM;
      }
   }

   public static Biome func_180276_a(int var0, Biome var1) {
      Biome var2 = (Biome)IRegistry.field_212624_m.func_148754_a(var0);
      return var2 == null ? var1 : var2;
   }

   public final float func_185355_j() {
      return this.field_76748_D;
   }

   public final float func_76727_i() {
      return this.field_76751_G;
   }

   public ITextComponent func_205403_k() {
      return new TextComponentTranslation(this.func_210773_k(), new Object[0]);
   }

   public String func_210773_k() {
      if (this.field_205405_aL == null) {
         this.field_205405_aL = Util.func_200697_a("biome", IRegistry.field_212624_m.func_177774_c(this));
      }

      return this.field_205405_aL;
   }

   public final float func_185360_m() {
      return this.field_76749_E;
   }

   public final float func_185353_n() {
      return this.field_76750_F;
   }

   public final int func_185361_o() {
      return this.field_76759_H;
   }

   public final int func_204274_p() {
      return this.field_204275_aE;
   }

   public final Biome.Category func_201856_r() {
      return this.field_201877_au;
   }

   public CompositeSurfaceBuilder<?> func_205401_q() {
      return this.field_201875_ar;
   }

   public ISurfaceBuilderConfig func_203944_q() {
      return this.field_201875_ar.func_205549_a();
   }

   @Nullable
   public String func_205402_s() {
      return this.field_185364_H;
   }

   public static void func_185358_q() {
      func_185354_a(0, "ocean", new OceanBiome());
      func_185354_a(1, "plains", new PlainsBiome());
      func_185354_a(2, "desert", new DesertBiome());
      func_185354_a(3, "mountains", new MountainsBiome());
      func_185354_a(4, "forest", new ForestBiome());
      func_185354_a(5, "taiga", new TaigaBiome());
      func_185354_a(6, "swamp", new SwampBiome());
      func_185354_a(7, "river", new RiverBiome());
      func_185354_a(8, "nether", new NetherBiome());
      func_185354_a(9, "the_end", new TheEndBiome());
      func_185354_a(10, "frozen_ocean", new FrozenOceanBiome());
      func_185354_a(11, "frozen_river", new FrozenRiverBiome());
      func_185354_a(12, "snowy_tundra", new SnowyTundraBiome());
      func_185354_a(13, "snowy_mountains", new SnowyMountainsBiome());
      func_185354_a(14, "mushroom_fields", new MushroomFieldsBiome());
      func_185354_a(15, "mushroom_field_shore", new MushroomFieldShoreBiome());
      func_185354_a(16, "beach", new BeachBiome());
      func_185354_a(17, "desert_hills", new DesertHillsBiome());
      func_185354_a(18, "wooded_hills", new WoodedHillsBiome());
      func_185354_a(19, "taiga_hills", new TaigaHillsBiome());
      func_185354_a(20, "mountain_edge", new MountainEdgeBiome());
      func_185354_a(21, "jungle", new JungleBiome());
      func_185354_a(22, "jungle_hills", new JungleHillsBiome());
      func_185354_a(23, "jungle_edge", new JungleEdgeBiome());
      func_185354_a(24, "deep_ocean", new DeepOceanBiome());
      func_185354_a(25, "stone_shore", new StoneShoreBiome());
      func_185354_a(26, "snowy_beach", new SnowyBeachBiome());
      func_185354_a(27, "birch_forest", new BirchForestBiome());
      func_185354_a(28, "birch_forest_hills", new BirchForestHillsBiome());
      func_185354_a(29, "dark_forest", new DarkForestBiome());
      func_185354_a(30, "snowy_taiga", new SnowyTaigaBiome());
      func_185354_a(31, "snowy_taiga_hills", new SnowyTaigaHillsBiome());
      func_185354_a(32, "giant_tree_taiga", new GiantTreeTaigaBiome());
      func_185354_a(33, "giant_tree_taiga_hills", new GiantTreeTaigaHillsBiome());
      func_185354_a(34, "wooded_mountains", new WoodedMountainsBiome());
      func_185354_a(35, "savanna", new SavannaBiome());
      func_185354_a(36, "savanna_plateau", new SavannaPlateauBiome());
      func_185354_a(37, "badlands", new BadlandsBiome());
      func_185354_a(38, "wooded_badlands_plateau", new WoodedBadlandsPlateauBiome());
      func_185354_a(39, "badlands_plateau", new BadlandsPlateauBiome());
      func_185354_a(40, "small_end_islands", new SmallEndIslandsBiome());
      func_185354_a(41, "end_midlands", new EndMidlandsBiome());
      func_185354_a(42, "end_highlands", new EndHighlandsBiome());
      func_185354_a(43, "end_barrens", new EndBarrensBiome());
      func_185354_a(44, "warm_ocean", new WarmOceanBiome());
      func_185354_a(45, "lukewarm_ocean", new LukewarmOceanBiome());
      func_185354_a(46, "cold_ocean", new ColdOceanBiome());
      func_185354_a(47, "deep_warm_ocean", new DeepWarmOceanBiome());
      func_185354_a(48, "deep_lukewarm_ocean", new DeepLukewarmOceanBiome());
      func_185354_a(49, "deep_cold_ocean", new DeepColdOceanBiome());
      func_185354_a(50, "deep_frozen_ocean", new DeepFrozenOceanBiome());
      func_185354_a(127, "the_void", new TheVoidBiome());
      func_185354_a(129, "sunflower_plains", new SunflowerPlainsBiome());
      func_185354_a(130, "desert_lakes", new DesertLakesBiome());
      func_185354_a(131, "gravelly_mountains", new GravellyMountainsBiome());
      func_185354_a(132, "flower_forest", new FlowerForestBiome());
      func_185354_a(133, "taiga_mountains", new TaigaMountainsBiome());
      func_185354_a(134, "swamp_hills", new SwampHillsBiome());
      func_185354_a(140, "ice_spikes", new IceSpikesBiome());
      func_185354_a(149, "modified_jungle", new ModifiedJungleBiome());
      func_185354_a(151, "modified_jungle_edge", new ModifiedJungleEdgeBiome());
      func_185354_a(155, "tall_birch_forest", new TallBirchForestBiome());
      func_185354_a(156, "tall_birch_hills", new TallBirchHillsBiome());
      func_185354_a(157, "dark_forest_hills", new DarkForestHillsBiome());
      func_185354_a(158, "snowy_taiga_mountains", new SnowyTaigaMountainsBiome());
      func_185354_a(160, "giant_spruce_taiga", new GiantSpruceTaigaBiome());
      func_185354_a(161, "giant_spruce_taiga_hills", new GiantSpruceTaigaHillsBiome());
      func_185354_a(162, "modified_gravelly_mountains", new ModifiedGravellyMountainsBiome());
      func_185354_a(163, "shattered_savanna", new ShatteredSavannaBiome());
      func_185354_a(164, "shattered_savanna_plateau", new ShatteredSavannaPlateauBiome());
      func_185354_a(165, "eroded_badlands", new ErodedBadlandsBiome());
      func_185354_a(166, "modified_wooded_badlands_plateau", new ModifiedWoodedBadlandsPlateauBiome());
      func_185354_a(167, "modified_badlands_plateau", new ModifiedBadlandsPlateauBiome());
      Collections.addAll(field_201870_ab, new Biome[]{Biomes.field_76771_b, Biomes.field_76772_c, Biomes.field_76769_d, Biomes.field_76770_e, Biomes.field_76767_f, Biomes.field_76768_g, Biomes.field_76780_h, Biomes.field_76781_i, Biomes.field_76777_m, Biomes.field_76774_n, Biomes.field_76775_o, Biomes.field_76789_p, Biomes.field_76788_q, Biomes.field_76787_r, Biomes.field_76786_s, Biomes.field_76785_t, Biomes.field_76784_u, Biomes.field_76782_w, Biomes.field_76792_x, Biomes.field_150574_L, Biomes.field_150575_M, Biomes.field_150576_N, Biomes.field_150577_O, Biomes.field_150583_P, Biomes.field_150582_Q, Biomes.field_150585_R, Biomes.field_150584_S, Biomes.field_150579_T, Biomes.field_150578_U, Biomes.field_150581_V, Biomes.field_150580_W, Biomes.field_150588_X, Biomes.field_150587_Y, Biomes.field_150589_Z, Biomes.field_150607_aa, Biomes.field_150608_ab});
   }

   private static void func_185354_a(int var0, String var1, Biome var2) {
      IRegistry.field_212624_m.func_177775_a(var0, new ResourceLocation(var1), var2);
      if (var2.func_185363_b()) {
         field_185373_j.func_148746_a(var2, IRegistry.field_212624_m.func_148757_b(IRegistry.field_212624_m.func_212608_b(new ResourceLocation(var2.field_185364_H))));
      }

   }

   static {
      field_205411_T = Blocks.field_150350_a.func_176223_P();
      field_203802_aB = Blocks.field_150346_d.func_176223_P();
      field_203803_aC = Blocks.field_196658_i.func_176223_P();
      field_203804_aD = Blocks.field_196661_l.func_176223_P();
      field_203805_aE = Blocks.field_150351_n.func_176223_P();
      field_203806_aF = Blocks.field_150348_b.func_176223_P();
      field_203807_aG = Blocks.field_196660_k.func_176223_P();
      field_203956_U = Blocks.field_150354_m.func_176223_P();
      field_203957_V = Blocks.field_196611_F.func_176223_P();
      field_203958_W = Blocks.field_196777_fo.func_176223_P();
      field_203959_X = Blocks.field_150391_bh.func_176223_P();
      field_205406_ae = Blocks.field_150424_aL.func_176223_P();
      field_205407_af = Blocks.field_150377_bs.func_176223_P();
      field_205408_ag = new SurfaceBuilderConfig(field_205411_T, field_205411_T, field_205411_T);
      field_203960_Y = new SurfaceBuilderConfig(field_203802_aB, field_203802_aB, field_203805_aE);
      field_203961_Z = new SurfaceBuilderConfig(field_203803_aC, field_203802_aB, field_203805_aE);
      field_203946_aa = new SurfaceBuilderConfig(field_203806_aF, field_203806_aF, field_203805_aE);
      field_203947_ab = new SurfaceBuilderConfig(field_203805_aE, field_203805_aE, field_203805_aE);
      field_203948_ac = new SurfaceBuilderConfig(field_203807_aG, field_203802_aB, field_203805_aE);
      field_203949_ad = new SurfaceBuilderConfig(field_203804_aD, field_203802_aB, field_203805_aE);
      field_203950_ae = new SurfaceBuilderConfig(field_203956_U, field_203956_U, field_203956_U);
      field_203951_af = new SurfaceBuilderConfig(field_203803_aC, field_203802_aB, field_203956_U);
      field_203952_ag = new SurfaceBuilderConfig(field_203956_U, field_203956_U, field_203805_aE);
      field_203953_ah = new SurfaceBuilderConfig(field_203957_V, field_203958_W, field_203805_aE);
      field_203954_ai = new SurfaceBuilderConfig(field_203959_X, field_203802_aB, field_203805_aE);
      field_205409_as = new SurfaceBuilderConfig(field_205406_ae, field_205406_ae, field_205406_ae);
      field_205410_at = new SurfaceBuilderConfig(field_205407_af, field_205407_af, field_205407_af);
      field_203955_aj = new DefaultSurfaceBuilder();
      field_201899_S = new ExtremeHillsSurfaceBuilder();
      field_201900_T = new SavanaMutatedSurfaceBuilder();
      field_201901_U = new ExtremeHillsMutatedSurfaceBuilder();
      field_201902_V = new TaigaMegaSurfaceBuilder();
      field_201903_W = new SwampSurfaceBuilder();
      field_201904_X = new MesaSurfaceBuilder();
      field_201905_Y = new MesaForestSurfaceBuilder();
      field_201906_Z = new MesaBryceSurfaceBuilder();
      field_205160_ax = new FrozenOceanSurfaceBuilder();
      field_205404_aE = new NetherSurfaceBuilder();
      field_201869_aa = new NoopSurfaceBuilder();
      field_201870_ab = Sets.newHashSet();
      field_185373_j = new ObjectIntIdentityMap();
      field_150605_ac = new NoiseGeneratorPerlin(new Random(1234L), 1);
      field_180281_af = new NoiseGeneratorPerlin(new Random(2345L), 1);
   }

   public static class BiomeBuilder {
      @Nullable
      private CompositeSurfaceBuilder<?> field_205422_a;
      @Nullable
      private Biome.RainType field_205423_b;
      @Nullable
      private Biome.Category field_205424_c;
      @Nullable
      private Float field_205425_d;
      @Nullable
      private Float field_205426_e;
      @Nullable
      private Float field_205427_f;
      @Nullable
      private Float field_205428_g;
      @Nullable
      private Integer field_205429_h;
      @Nullable
      private Integer field_205430_i;
      @Nullable
      private String field_205431_j;

      public BiomeBuilder() {
         super();
      }

      public Biome.BiomeBuilder func_205416_a(CompositeSurfaceBuilder<?> var1) {
         this.field_205422_a = var1;
         return this;
      }

      public Biome.BiomeBuilder func_205415_a(Biome.RainType var1) {
         this.field_205423_b = var1;
         return this;
      }

      public Biome.BiomeBuilder func_205419_a(Biome.Category var1) {
         this.field_205424_c = var1;
         return this;
      }

      public Biome.BiomeBuilder func_205421_a(float var1) {
         this.field_205425_d = var1;
         return this;
      }

      public Biome.BiomeBuilder func_205420_b(float var1) {
         this.field_205426_e = var1;
         return this;
      }

      public Biome.BiomeBuilder func_205414_c(float var1) {
         this.field_205427_f = var1;
         return this;
      }

      public Biome.BiomeBuilder func_205417_d(float var1) {
         this.field_205428_g = var1;
         return this;
      }

      public Biome.BiomeBuilder func_205412_a(int var1) {
         this.field_205429_h = var1;
         return this;
      }

      public Biome.BiomeBuilder func_205413_b(int var1) {
         this.field_205430_i = var1;
         return this;
      }

      public Biome.BiomeBuilder func_205418_a(@Nullable String var1) {
         this.field_205431_j = var1;
         return this;
      }

      public String toString() {
         return "BiomeBuilder{\nsurfaceBuilder=" + this.field_205422_a + ",\nprecipitation=" + this.field_205423_b + ",\nbiomeCategory=" + this.field_205424_c + ",\ndepth=" + this.field_205425_d + ",\nscale=" + this.field_205426_e + ",\ntemperature=" + this.field_205427_f + ",\ndownfall=" + this.field_205428_g + ",\nwaterColor=" + this.field_205429_h + ",\nwaterFogColor=" + this.field_205430_i + ",\nparent='" + this.field_205431_j + '\'' + "\n" + '}';
      }
   }

   public static class SpawnListEntry extends WeightedRandom.Item {
      public EntityType<? extends EntityLiving> field_200702_b;
      public int field_76301_c;
      public int field_76299_d;

      public SpawnListEntry(EntityType<? extends EntityLiving> var1, int var2, int var3, int var4) {
         super(var2);
         this.field_200702_b = var1;
         this.field_76301_c = var3;
         this.field_76299_d = var4;
      }

      public String toString() {
         return EntityType.func_200718_a(this.field_200702_b) + "*(" + this.field_76301_c + "-" + this.field_76299_d + "):" + this.field_76292_a;
      }
   }

   public static enum RainType {
      NONE,
      RAIN,
      SNOW;

      private RainType() {
      }
   }

   public static enum Category {
      NONE,
      TAIGA,
      EXTREME_HILLS,
      JUNGLE,
      MESA,
      PLAINS,
      SAVANNA,
      ICY,
      THEEND,
      BEACH,
      FOREST,
      OCEAN,
      DESERT,
      RIVER,
      SWAMP,
      MUSHROOM,
      NETHER;

      private Category() {
      }
   }

   public static enum TempCategory {
      OCEAN,
      COLD,
      MEDIUM,
      WARM;

      private TempCategory() {
      }
   }
}
