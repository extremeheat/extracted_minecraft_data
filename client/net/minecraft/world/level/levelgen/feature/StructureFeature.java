package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.NetherFossilFeature;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureFeature<C extends FeatureConfiguration> {
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
   public static final SwamplandHutFeature SWAMP_HUT;
   public static final StructureFeature<NoneFeatureConfiguration> STRONGHOLD;
   public static final StructureFeature<NoneFeatureConfiguration> OCEAN_MONUMENT;
   public static final StructureFeature<OceanRuinConfiguration> OCEAN_RUIN;
   public static final StructureFeature<NoneFeatureConfiguration> NETHER_BRIDGE;
   public static final StructureFeature<NoneFeatureConfiguration> END_CITY;
   public static final StructureFeature<ProbabilityFeatureConfiguration> BURIED_TREASURE;
   public static final StructureFeature<JigsawConfiguration> VILLAGE;
   public static final StructureFeature<NoneFeatureConfiguration> NETHER_FOSSIL;
   public static final StructureFeature<JigsawConfiguration> BASTION_REMNANT;
   public static final List<StructureFeature<?>> NOISE_AFFECTING_FEATURES;
   private static final ResourceLocation JIGSAW_RENAME;
   private static final Map<ResourceLocation, ResourceLocation> RENAMES;
   private final Codec<ConfiguredStructureFeature<C, StructureFeature<C>>> configuredStructureCodec;

   private static <F extends StructureFeature<?>> F register(String var0, F var1, GenerationStep.Decoration var2) {
      STRUCTURES_REGISTRY.put(var0.toLowerCase(Locale.ROOT), var1);
      STEP.put(var1, var2);
      return (StructureFeature)Registry.register(Registry.STRUCTURE_FEATURE, (String)var0.toLowerCase(Locale.ROOT), var1);
   }

   public StructureFeature(Codec<C> var1) {
      super();
      this.configuredStructureCodec = var1.fieldOf("config").xmap((var1x) -> {
         return new ConfiguredStructureFeature(this, var1x);
      }, (var0) -> {
         return var0.config;
      }).codec();
   }

   public GenerationStep.Decoration step() {
      return (GenerationStep.Decoration)STEP.get(this);
   }

   public static void bootstrap() {
   }

   @Nullable
   public static StructureStart<?> loadStaticStart(StructureManager var0, CompoundTag var1, long var2) {
      String var4 = var1.getString("id");
      if ("INVALID".equals(var4)) {
         return StructureStart.INVALID_START;
      } else {
         StructureFeature var5 = (StructureFeature)Registry.STRUCTURE_FEATURE.get(new ResourceLocation(var4.toLowerCase(Locale.ROOT)));
         if (var5 == null) {
            LOGGER.error("Unknown feature id: {}", var4);
            return null;
         } else {
            int var6 = var1.getInt("ChunkX");
            int var7 = var1.getInt("ChunkZ");
            int var8 = var1.getInt("references");
            BoundingBox var9 = var1.contains("BB") ? new BoundingBox(var1.getIntArray("BB")) : BoundingBox.getUnknownBox();
            ListTag var10 = var1.getList("Children", 10);

            try {
               StructureStart var11 = var5.createStart(var6, var7, var9, var8, var2);

               for(int var12 = 0; var12 < var10.size(); ++var12) {
                  CompoundTag var13 = var10.getCompound(var12);
                  String var14 = var13.getString("id").toLowerCase(Locale.ROOT);
                  ResourceLocation var15 = new ResourceLocation(var14);
                  ResourceLocation var16 = (ResourceLocation)RENAMES.getOrDefault(var15, var15);
                  StructurePieceType var17 = (StructurePieceType)Registry.STRUCTURE_PIECE.get(var16);
                  if (var17 == null) {
                     LOGGER.error("Unknown structure piece id: {}", var16);
                  } else {
                     try {
                        StructurePiece var18 = var17.load(var0, var13);
                        var11.getPieces().add(var18);
                     } catch (Exception var19) {
                        LOGGER.error("Exception loading structure piece with id {}", var16, var19);
                     }
                  }
               }

               return var11;
            } catch (Exception var20) {
               LOGGER.error("Failed Start with id {}", var4, var20);
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

   @Nullable
   public BlockPos getNearestGeneratedFeature(LevelReader var1, StructureFeatureManager var2, BlockPos var3, int var4, boolean var5, long var6, StructureFeatureConfiguration var8) {
      int var9 = var8.spacing();
      int var10 = var3.getX() >> 4;
      int var11 = var3.getZ() >> 4;
      int var12 = 0;

      for(WorldgenRandom var13 = new WorldgenRandom(); var12 <= var4; ++var12) {
         for(int var14 = -var12; var14 <= var12; ++var14) {
            boolean var15 = var14 == -var12 || var14 == var12;

            for(int var16 = -var12; var16 <= var12; ++var16) {
               boolean var17 = var16 == -var12 || var16 == var12;
               if (var15 || var17) {
                  int var18 = var10 + var9 * var14;
                  int var19 = var11 + var9 * var16;
                  ChunkPos var20 = this.getPotentialFeatureChunk(var8, var6, var13, var18, var19);
                  ChunkAccess var21 = var1.getChunk(var20.x, var20.z, ChunkStatus.STRUCTURE_STARTS);
                  StructureStart var22 = var2.getStartForFeature(SectionPos.of(var21.getPos(), 0), this, var21);
                  if (var22 != null && var22.isValid()) {
                     if (var5 && var22.canBeReferenced()) {
                        var22.addReference();
                        return var22.getLocatePos();
                     }

                     if (!var5) {
                        return var22.getLocatePos();
                     }
                  }

                  if (var12 == 0) {
                     break;
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

   public final ChunkPos getPotentialFeatureChunk(StructureFeatureConfiguration var1, long var2, WorldgenRandom var4, int var5, int var6) {
      int var7 = var1.spacing();
      int var8 = var1.separation();
      int var9 = Math.floorDiv(var5, var7);
      int var10 = Math.floorDiv(var6, var7);
      var4.setLargeFeatureWithSalt(var2, var9, var10, var1.salt());
      int var11;
      int var12;
      if (this.linearSeparation()) {
         var11 = var4.nextInt(var7 - var8);
         var12 = var4.nextInt(var7 - var8);
      } else {
         var11 = (var4.nextInt(var7 - var8) + var4.nextInt(var7 - var8)) / 2;
         var12 = (var4.nextInt(var7 - var8) + var4.nextInt(var7 - var8)) / 2;
      }

      return new ChunkPos(var9 * var7 + var11, var10 * var7 + var12);
   }

   protected boolean isFeatureChunk(ChunkGenerator var1, BiomeSource var2, long var3, WorldgenRandom var5, int var6, int var7, Biome var8, ChunkPos var9, C var10) {
      return true;
   }

   private StructureStart<C> createStart(int var1, int var2, BoundingBox var3, int var4, long var5) {
      return this.getStartFactory().create(this, var1, var2, var3, var4, var5);
   }

   public StructureStart<?> generate(RegistryAccess var1, ChunkGenerator var2, BiomeSource var3, StructureManager var4, long var5, ChunkPos var7, Biome var8, int var9, WorldgenRandom var10, StructureFeatureConfiguration var11, C var12) {
      ChunkPos var13 = this.getPotentialFeatureChunk(var11, var5, var10, var7.x, var7.z);
      if (var7.x == var13.x && var7.z == var13.z && this.isFeatureChunk(var2, var3, var5, var10, var7.x, var7.z, var8, var13, var12)) {
         StructureStart var14 = this.createStart(var7.x, var7.z, BoundingBox.getUnknownBox(), var9, var5);
         var14.generatePieces(var1, var2, var4, var7.x, var7.z, var8, var12);
         if (var14.isValid()) {
            return var14;
         }
      }

      return StructureStart.INVALID_START;
   }

   public abstract StructureFeature.StructureStartFactory<C> getStartFactory();

   public String getFeatureName() {
      return (String)STRUCTURES_REGISTRY.inverse().get(this);
   }

   public List<MobSpawnSettings.SpawnerData> getSpecialEnemies() {
      return ImmutableList.of();
   }

   public List<MobSpawnSettings.SpawnerData> getSpecialAnimals() {
      return ImmutableList.of();
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
      SWAMP_HUT = (SwamplandHutFeature)register("Swamp_Hut", new SwamplandHutFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      STRONGHOLD = register("Stronghold", new StrongholdFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.STRONGHOLDS);
      OCEAN_MONUMENT = register("Monument", new OceanMonumentFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      OCEAN_RUIN = register("Ocean_Ruin", new OceanRuinFeature(OceanRuinConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      NETHER_BRIDGE = register("Fortress", new NetherFortressFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_DECORATION);
      END_CITY = register("EndCity", new EndCityFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      BURIED_TREASURE = register("Buried_Treasure", new BuriedTreasureFeature(ProbabilityFeatureConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_STRUCTURES);
      VILLAGE = register("Village", new VillageFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      NETHER_FOSSIL = register("Nether_Fossil", new NetherFossilFeature(NoneFeatureConfiguration.CODEC), GenerationStep.Decoration.UNDERGROUND_DECORATION);
      BASTION_REMNANT = register("Bastion_Remnant", new BastionFeature(JigsawConfiguration.CODEC), GenerationStep.Decoration.SURFACE_STRUCTURES);
      NOISE_AFFECTING_FEATURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE, NETHER_FOSSIL);
      JIGSAW_RENAME = new ResourceLocation("jigsaw");
      RENAMES = ImmutableMap.builder().put(new ResourceLocation("nvi"), JIGSAW_RENAME).put(new ResourceLocation("pcp"), JIGSAW_RENAME).put(new ResourceLocation("bastionremnant"), JIGSAW_RENAME).put(new ResourceLocation("runtime"), JIGSAW_RENAME).build();
   }

   public interface StructureStartFactory<C extends FeatureConfiguration> {
      StructureStart<C> create(StructureFeature<C> var1, int var2, int var3, BoundingBox var4, int var5, long var6);
   }
}
