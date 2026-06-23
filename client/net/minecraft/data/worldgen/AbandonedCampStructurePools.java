package net.minecraft.data.worldgen;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class AbandonedCampStructurePools {
   public static final AbandonedCampStructure BAMBOO_JUNGLE = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/bamboo_jungle"), Pools.createKey("abandoned_camp/tent/bamboo_jungle"), "bamboo_jungle");
   public static final AbandonedCampStructure BIRCH_FOREST = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/birch_forest"), Pools.createKey("abandoned_camp/tent/birch_forest"), "birch_forest");
   public static final AbandonedCampStructure CHERRY_GROVE = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/cherry_grove"), Pools.createKey("abandoned_camp/tent/cherry_grove"), "cherry_grove");
   public static final AbandonedCampStructure DAPPLED_FOREST = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/dappled_forest"), Pools.createKey("abandoned_camp/tent/dappled_forest"), "dappled_forest");
   public static final AbandonedCampStructure FLOWER_FOREST = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/flower_forest"), Pools.createKey("abandoned_camp/tent/flower_forest"), "flower_forest");
   public static final AbandonedCampStructure FOREST = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/forest"), Pools.createKey("abandoned_camp/tent/forest"), "forest");
   public static final AbandonedCampStructure MEADOW = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/meadow"), Pools.createKey("abandoned_camp/tent/meadow"), "meadow");
   public static final AbandonedCampStructure OLD_GROWTH_BIRCH_FOREST = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/old_growth_birch_forest"), Pools.createKey("abandoned_camp/tent/old_growth_birch_forest"), "old_growth_birch_forest");
   public static final AbandonedCampStructure OLD_GROWTH_PINE_TAIGA = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/old_growth_pine_taiga"), Pools.createKey("abandoned_camp/tent/old_growth_pine_taiga"), "old_growth_pine_taiga");
   public static final AbandonedCampStructure OLD_GROWTH_SPRUCE_TAIGA = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/old_growth_spruce_taiga"), Pools.createKey("abandoned_camp/tent/old_growth_spruce_taiga"), "old_growth_spruce_taiga");
   public static final AbandonedCampStructure PALE_GARDEN = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/pale_garden"), Pools.createKey("abandoned_camp/tent/pale_garden"), "pale_garden");
   public static final AbandonedCampStructure SAVANNA = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/savanna"), Pools.createKey("abandoned_camp/tent/savanna"), "savanna");
   public static final AbandonedCampStructure SNOWY_TAIGA = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/snowy_taiga"), Pools.createKey("abandoned_camp/tent/snowy_taiga"), "snowy_taiga");
   public static final AbandonedCampStructure SPARSE_JUNGLE = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/sparse_jungle"), Pools.createKey("abandoned_camp/tent/sparse_jungle"), "sparse_jungle");
   public static final AbandonedCampStructure SWAMP = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/swamp"), Pools.createKey("abandoned_camp/tent/swamp"), "swamp");
   public static final AbandonedCampStructure TAIGA = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/taiga"), Pools.createKey("abandoned_camp/tent/taiga"), "taiga");
   public static final AbandonedCampStructure WINDSWEPT_FOREST = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/windswept_forest"), Pools.createKey("abandoned_camp/tent/windswept_forest"), "windswept_forest");
   public static final AbandonedCampStructure WOODED_BADLANDS = new AbandonedCampStructure(Pools.createKey("abandoned_camp/camp/wooded_badlands"), Pools.createKey("abandoned_camp/tent/wooded_badlands"), "wooded_badlands");
   private static final String TENT_IDENTIFIER_TEMPLATE = "abandoned_camp/tent/%s/tent_%s_%d";
   private static final String CAMP_BIOME_IDENTIFIER_TEMPLATE = "abandoned_camp/camp/%s/campsite_%s_%d";
   private static final String CAMP_DEFAULT_IDENTIFIER_TEMPLATE = "abandoned_camp/camp/default/campsite_default_%s_%d";
   private static final int NUM_OF_BIOME_SPECIFIC_CAMPSITES = 3;
   private static final int NUM_OF_DEFAULT_CAMPSITES = 15;
   private static final int NUM_OF_TENTS = 10;
   private static final List<AbandonedCampStructure> BIOME_VARIANTS;
   private static final List<String> DEFAULT_CAMP_TYPES;

   public AbandonedCampStructurePools() {
      super();
   }

   public static void bootstrap(final BootstrapContext<StructureTemplatePool> context) {
      HolderGetter<StructureTemplatePool> pools = context.<StructureTemplatePool>lookup(Registries.TEMPLATE_POOL);
      Holder<StructureTemplatePool> empty = pools.getOrThrow(Pools.EMPTY);
      bootstrapCampsitePools(context, empty);
   }

   private static void bootstrapCampsitePools(final BootstrapContext<StructureTemplatePool> context, final Holder<StructureTemplatePool> empty) {
      for(AbandonedCampStructure biomeVariant : BIOME_VARIANTS) {
         registerTentPool(context, empty, biomeVariant);
         registerCampsitePool(context, empty, biomeVariant);
      }

   }

   private static void registerTentPool(final BootstrapContext<StructureTemplatePool> context, final Holder<StructureTemplatePool> empty, final AbandonedCampStructure biomeVariant) {
      List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> tentStructures = new ArrayList();

      for(int numOfTentVariants = 1; numOfTentVariants <= 10; ++numOfTentVariants) {
         tentStructures.add(Pair.of(StructurePoolElement.legacy(getBiomeSpecificStructureName(biomeVariant, "abandoned_camp/tent/%s/tent_%s_%d", numOfTentVariants)), 1));
      }

      Pools.register(context, biomeVariant.tentStructureDirectory.identifier().toShortString(), new StructureTemplatePool(empty, tentStructures, StructureTemplatePool.Projection.RIGID));
   }

   private static void registerCampsitePool(final BootstrapContext<StructureTemplatePool> context, final Holder<StructureTemplatePool> empty, final AbandonedCampStructure biomeVariant) {
      List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> campsiteStructures = new ArrayList();

      for(String defaultCampSiteTypes : DEFAULT_CAMP_TYPES) {
         for(int numOfDefaultCampVariants = 1; numOfDefaultCampVariants <= 15; ++numOfDefaultCampVariants) {
            campsiteStructures.add(Pair.of(StructurePoolElement.legacy(getDefaultCampsiteStructureName(defaultCampSiteTypes, numOfDefaultCampVariants)), 1));
         }
      }

      for(int numOfBiomeCampVariants = 1; numOfBiomeCampVariants <= 3; ++numOfBiomeCampVariants) {
         campsiteStructures.add(Pair.of(StructurePoolElement.legacy(getBiomeSpecificStructureName(biomeVariant, "abandoned_camp/camp/%s/campsite_%s_%d", numOfBiomeCampVariants)), 1));
      }

      Pools.register(context, biomeVariant.campStructureDirectory.identifier().toShortString(), new StructureTemplatePool(empty, campsiteStructures, StructureTemplatePool.Projection.RIGID));
   }

   private static String getBiomeSpecificStructureName(final AbandonedCampStructure biomeVariant, final String identifierTemplate, final int numOfTentVariants) {
      return String.format(Locale.ROOT, identifierTemplate, biomeVariant.name, biomeVariant.name, numOfTentVariants);
   }

   private static String getDefaultCampsiteStructureName(final String defaultCampSiteTypes, final int numOfDefaultCampVariants) {
      return String.format(Locale.ROOT, "abandoned_camp/camp/default/campsite_default_%s_%d", defaultCampSiteTypes, numOfDefaultCampVariants);
   }

   static {
      BIOME_VARIANTS = List.of(SAVANNA, FLOWER_FOREST, BIRCH_FOREST, FOREST, SNOWY_TAIGA, BAMBOO_JUNGLE, SPARSE_JUNGLE, CHERRY_GROVE, MEADOW, OLD_GROWTH_BIRCH_FOREST, OLD_GROWTH_SPRUCE_TAIGA, OLD_GROWTH_PINE_TAIGA, SWAMP, TAIGA, WINDSWEPT_FOREST, DAPPLED_FOREST, WOODED_BADLANDS, PALE_GARDEN);
      DEFAULT_CAMP_TYPES = List.of("chest", "barrel", "special");
   }

   public static record AbandonedCampStructure(ResourceKey<StructureTemplatePool> campStructureDirectory, ResourceKey<StructureTemplatePool> tentStructureDirectory, String name) {
      public AbandonedCampStructure {
         super();
      }
   }
}
