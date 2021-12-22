package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RangeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.NetherFossilFeature;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureFeature<C extends FeatureConfiguration> {
   public static final BiMap<String, StructureFeature<?>> STRUCTURES_REGISTRY = HashBiMap.create();
   private static final Map<StructureFeature<?>, GenerationStep.Decoration> STEP = Maps.newHashMap();
   private static final Logger LOGGER = LogManager.getLogger();
   public static final StructureFeature<JigsawConfiguration> PILLAGER_OUTPOST;
   public static final StructureFeature<MineshaftConfiguration> MINESHAFT;
   public static final StructureFeature<NoneFeatureConfiguration> WOODLAND_MANSION;
   public static final StructureFeature<NoneFeatureConfiguration> JUNGLE_TEMPLE;
   public static final StructureFeature<NoneFeatureConfiguration> DESERT_PYRAMID;
   public static final StructureFeature<NoneFeatureConfiguration> IGLOO;
   public static final StructureFeature<RuinedPortalConfiguration> RUINED_PORTAL;
   public static final StructureFeature<ShipwreckConfiguration> SHIPWRECK;
   public static final StructureFeature<NoneFeatureConfiguration> SWAMP_HUT;
   public static final StructureFeature<NoneFeatureConfiguration> STRONGHOLD;
   public static final StructureFeature<NoneFeatureConfiguration> OCEAN_MONUMENT;
   public static final StructureFeature<OceanRuinConfiguration> OCEAN_RUIN;
   public static final StructureFeature<NoneFeatureConfiguration> NETHER_BRIDGE;
   public static final StructureFeature<NoneFeatureConfiguration> END_CITY;
   public static final StructureFeature<ProbabilityFeatureConfiguration> BURIED_TREASURE;
   public static final StructureFeature<JigsawConfiguration> VILLAGE;
   public static final StructureFeature<RangeConfiguration> NETHER_FOSSIL;
   public static final StructureFeature<JigsawConfiguration> BASTION_REMNANT;
   public static final List<StructureFeature<?>> NOISE_AFFECTING_FEATURES;
   public static final int MAX_STRUCTURE_RANGE = 8;
   private final Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> configuredStructureCodec;
   private final PieceGeneratorSupplier<C> pieceGenerator;
   private final PostPlacementProcessor postPlacementProcessor;

   private static <F extends StructureFeature<?>> F register(String var0, F var1, GenerationStep.Decoration var2) {
      STRUCTURES_REGISTRY.put(var0.toLowerCase(Locale.ROOT), var1);
      STEP.put(var1, var2);
      return (StructureFeature)Registry.register(Registry.STRUCTURE_FEATURE, (String)var0.toLowerCase(Locale.ROOT), var1);
   }

   public StructureFeature(Codec<C> var1, PieceGeneratorSupplier<C> var2) {
      this(var1, var2, PostPlacementProcessor.NONE);
   }

   public StructureFeature(Codec<C> var1, PieceGeneratorSupplier<C> var2, PostPlacementProcessor var3) {
      super();
      this.configuredStructureCodec = var1.fieldOf("config").xmap((var1x) -> {
         return new ConfiguredStructureFeature(this, var1x);
      }, (var0) -> {
         return var0.config;
      }).codec();
      this.pieceGenerator = var2;
      this.postPlacementProcessor = var3;
   }

   public GenerationStep.Decoration step() {
      return (GenerationStep.Decoration)STEP.get(this);
   }

   public static void bootstrap() {
   }

   @Nullable
   public static StructureStart<?> loadStaticStart(StructurePieceSerializationContext var0, CompoundTag var1, long var2) {
      String var4 = var1.getString("id");
      if ("INVALID".equals(var4)) {
         return StructureStart.INVALID_START;
      } else {
         StructureFeature var5 = (StructureFeature)Registry.STRUCTURE_FEATURE.get(new ResourceLocation(var4.toLowerCase(Locale.ROOT)));
         if (var5 == null) {
            LOGGER.error("Unknown feature id: {}", var4);
            return null;
         } else {
            ChunkPos var6 = new ChunkPos(var1.getInt("ChunkX"), var1.getInt("ChunkZ"));
            int var7 = var1.getInt("references");
            ListTag var8 = var1.getList("Children", 10);

            try {
               PiecesContainer var9 = PiecesContainer.load(var8, var0);
               if (var5 == OCEAN_MONUMENT) {
                  var9 = OceanMonumentFeature.regeneratePiecesAfterLoad(var6, var2, var9);
               }

               return new StructureStart(var5, var6, var7, var9);
            } catch (Exception var10) {
               LOGGER.error("Failed Start with id {}", var4, var10);
               return null;
            }
         }
      }
   }

   public Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> configuredStructureCodec() {
      return this.configuredStructureCodec;
   }

   public ConfiguredStructureFeature<C, ? extends StructureFeature<C>> configured(C var1) {
      return new ConfiguredStructureFeature(this, var1);
   }

   public BlockPos getLocatePos(ChunkPos var1) {
      return new BlockPos(var1.getMinBlockX(), 0, var1.getMinBlockZ());
   }

   @Nullable
   public BlockPos getNearestGeneratedFeature(LevelReader var1, StructureFeatureManager var2, BlockPos var3, int var4, boolean var5, long var6, StructureFeatureConfiguration var8) {
      int var9 = var8.spacing();
      int var10 = SectionPos.blockToSectionCoord(var3.getX());
      int var11 = SectionPos.blockToSectionCoord(var3.getZ());

      for(int var12 = 0; var12 <= var4; ++var12) {
         for(int var13 = -var12; var13 <= var12; ++var13) {
            boolean var14 = var13 == -var12 || var13 == var12;

            for(int var15 = -var12; var15 <= var12; ++var15) {
               boolean var16 = var15 == -var12 || var15 == var12;
               if (var14 || var16) {
                  int var17 = var10 + var9 * var13;
                  int var18 = var11 + var9 * var15;
                  ChunkPos var19 = this.getPotentialFeatureChunk(var8, var6, var17, var18);
                  StructureCheckResult var20 = var2.checkStructurePresence(var19, this, var5);
                  if (var20 != StructureCheckResult.START_NOT_PRESENT) {
                     if (!var5 && var20 == StructureCheckResult.START_PRESENT) {
                        return this.getLocatePos(var19);
                     }

                     ChunkAccess var21 = var1.getChunk(var19.field_504, var19.field_505, ChunkStatus.STRUCTURE_STARTS);
                     StructureStart var22 = var2.getStartForFeature(SectionPos.bottomOf(var21), this, var21);
                     if (var22 != null && var22.isValid()) {
                        if (var5 && var22.canBeReferenced()) {
                           var2.addReference(var22);
                           return this.getLocatePos(var22.getChunkPos());
                        }

                        if (!var5) {
                           return this.getLocatePos(var22.getChunkPos());
                        }
                     }

                     if (var12 == 0) {
                        break;
                     }
                  }
               }
            }

            if (var12 == 0) {
               break;
            }
         }
      }

      return null;
   }

   protected boolean linearSeparation() {
      return true;
   }

   public final ChunkPos getPotentialFeatureChunk(StructureFeatureConfiguration var1, long var2, int var4, int var5) {
      int var6 = var1.spacing();
      int var7 = var1.separation();
      int var8 = Math.floorDiv(var4, var6);
      int var9 = Math.floorDiv(var5, var6);
      WorldgenRandom var10 = new WorldgenRandom(new LegacyRandomSource(0L));
      var10.setLargeFeatureWithSalt(var2, var8, var9, var1.salt());
      int var11;
      int var12;
      if (this.linearSeparation()) {
         var11 = var10.nextInt(var6 - var7);
         var12 = var10.nextInt(var6 - var7);
      } else {
         var11 = (var10.nextInt(var6 - var7) + var10.nextInt(var6 - var7)) / 2;
         var12 = (var10.nextInt(var6 - var7) + var10.nextInt(var6 - var7)) / 2;
      }

      return new ChunkPos(var8 * var6 + var11, var9 * var6 + var12);
   }

   public StructureStart<?> generate(RegistryAccess var1, ChunkGenerator var2, BiomeSource var3, StructureManager var4, long var5, ChunkPos var7, int var8, StructureFeatureConfiguration var9, C var10, LevelHeightAccessor var11, Predicate<Biome> var12) {
      ChunkPos var13 = this.getPotentialFeatureChunk(var9, var5, var7.field_504, var7.field_505);
      if (var7.field_504 == var13.field_504 && var7.field_505 == var13.field_505) {
         Optional var14 = this.pieceGenerator.createGenerator(new PieceGeneratorSupplier.Context(var2, var3, var5, var7, var10, var11, var12, var4, var1));
         if (var14.isPresent()) {
            StructurePiecesBuilder var15 = new StructurePiecesBuilder();
            WorldgenRandom var16 = new WorldgenRandom(new LegacyRandomSource(0L));
            var16.setLargeFeatureSeed(var5, var7.field_504, var7.field_505);
            ((PieceGenerator)var14.get()).generatePieces(var15, new PieceGenerator.Context(var10, var2, var4, var7, var11, var16, var5));
            StructureStart var17 = new StructureStart(this, var7, var8, var15.build());
            if (var17.isValid()) {
               return var17;
            }
         }
      }

      return StructureStart.INVALID_START;
   }

   public boolean canGenerate(RegistryAccess var1, ChunkGenerator var2, BiomeSource var3, StructureManager var4, long var5, ChunkPos var7, C var8, LevelHeightAccessor var9, Predicate<Biome> var10) {
      return this.pieceGenerator.createGenerator(new PieceGeneratorSupplier.Context(var2, var3, var5, var7, var8, var9, var10, var4, var1)).isPresent();
   }

   public PostPlacementProcessor getPostPlacementProcessor() {
      return this.postPlacementProcessor;
   }

   public String getFeatureName() {
      return (String)STRUCTURES_REGISTRY.inverse().get(this);
   }

   public BoundingBox adjustBoundingBox(BoundingBox var1) {
      return var1;
   }

   static {
      PILLAGER_OUTPOST = register("Pillager_Outpost", new PillagerOutpostFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      MINESHAFT = register("Mineshaft", new MineshaftFeature(MineshaftConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
      WOODLAND_MANSION = register("Mansion", new WoodlandMansionFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      JUNGLE_TEMPLE = register("Jungle_Pyramid", new JunglePyramidFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      DESERT_PYRAMID = register("Desert_Pyramid", new DesertPyramidFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      IGLOO = register("Igloo", new IglooFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      RUINED_PORTAL = register("Ruined_Portal", new RuinedPortalFeature(RuinedPortalConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      SHIPWRECK = register("Shipwreck", new ShipwreckFeature(ShipwreckConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      SWAMP_HUT = register("Swamp_Hut", new SwamplandHutFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      STRONGHOLD = register("Stronghold", new StrongholdFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.STRONGHOLDS);
      OCEAN_MONUMENT = register("Monument", new OceanMonumentFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      OCEAN_RUIN = register("Ocean_Ruin", new OceanRuinFeature(OceanRuinConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      NETHER_BRIDGE = register("Fortress", new NetherFortressFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_DECORATION);
      END_CITY = register("EndCity", new EndCityFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      BURIED_TREASURE = register("Buried_Treasure", new BuriedTreasureFeature(ProbabilityFeatureConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
      VILLAGE = register("Village", new VillageFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      NETHER_FOSSIL = register("Nether_Fossil", new NetherFossilFeature(RangeConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_DECORATION);
      BASTION_REMNANT = register("Bastion_Remnant", new BastionFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      NOISE_AFFECTING_FEATURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE, NETHER_FOSSIL, STRONGHOLD);
   }
}
