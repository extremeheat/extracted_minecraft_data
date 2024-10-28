package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class PoiTypes {
   public static final ResourceKey<PoiType> ARMORER = createKey("armorer");
   public static final ResourceKey<PoiType> BUTCHER = createKey("butcher");
   public static final ResourceKey<PoiType> CARTOGRAPHER = createKey("cartographer");
   public static final ResourceKey<PoiType> CLERIC = createKey("cleric");
   public static final ResourceKey<PoiType> FARMER = createKey("farmer");
   public static final ResourceKey<PoiType> FISHERMAN = createKey("fisherman");
   public static final ResourceKey<PoiType> FLETCHER = createKey("fletcher");
   public static final ResourceKey<PoiType> LEATHERWORKER = createKey("leatherworker");
   public static final ResourceKey<PoiType> LIBRARIAN = createKey("librarian");
   public static final ResourceKey<PoiType> MASON = createKey("mason");
   public static final ResourceKey<PoiType> SHEPHERD = createKey("shepherd");
   public static final ResourceKey<PoiType> TOOLSMITH = createKey("toolsmith");
   public static final ResourceKey<PoiType> WEAPONSMITH = createKey("weaponsmith");
   public static final ResourceKey<PoiType> HOME = createKey("home");
   public static final ResourceKey<PoiType> MEETING = createKey("meeting");
   public static final ResourceKey<PoiType> BEEHIVE = createKey("beehive");
   public static final ResourceKey<PoiType> BEE_NEST = createKey("bee_nest");
   public static final ResourceKey<PoiType> NETHER_PORTAL = createKey("nether_portal");
   public static final ResourceKey<PoiType> LODESTONE = createKey("lodestone");
   public static final ResourceKey<PoiType> LIGHTNING_ROD = createKey("lightning_rod");
   private static final Set<BlockState> BEDS;
   private static final Set<BlockState> CAULDRONS;
   private static final Map<BlockState, Holder<PoiType>> TYPE_BY_STATE;

   public PoiTypes() {
      super();
   }

   private static Set<BlockState> getBlockStates(Block var0) {
      return ImmutableSet.copyOf(var0.getStateDefinition().getPossibleStates());
   }

   private static ResourceKey<PoiType> createKey(String var0) {
      return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, ResourceLocation.withDefaultNamespace(var0));
   }

   private static PoiType register(Registry<PoiType> var0, ResourceKey<PoiType> var1, Set<BlockState> var2, int var3, int var4) {
      PoiType var5 = new PoiType(var2, var3, var4);
      Registry.register(var0, (ResourceKey)var1, var5);
      registerBlockStates(var0.getHolderOrThrow(var1), var2);
      return var5;
   }

   private static void registerBlockStates(Holder<PoiType> var0, Set<BlockState> var1) {
      var1.forEach((var1x) -> {
         Holder var2 = (Holder)TYPE_BY_STATE.put(var1x, var0);
         if (var2 != null) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException(String.format(Locale.ROOT, "%s is defined in more than one PoI type", var1x)));
         }
      });
   }

   public static Optional<Holder<PoiType>> forState(BlockState var0) {
      return Optional.ofNullable((Holder)TYPE_BY_STATE.get(var0));
   }

   public static boolean hasPoi(BlockState var0) {
      return TYPE_BY_STATE.containsKey(var0);
   }

   public static PoiType bootstrap(Registry<PoiType> var0) {
      register(var0, ARMORER, getBlockStates(Blocks.BLAST_FURNACE), 1, 1);
      register(var0, BUTCHER, getBlockStates(Blocks.SMOKER), 1, 1);
      register(var0, CARTOGRAPHER, getBlockStates(Blocks.CARTOGRAPHY_TABLE), 1, 1);
      register(var0, CLERIC, getBlockStates(Blocks.BREWING_STAND), 1, 1);
      register(var0, FARMER, getBlockStates(Blocks.COMPOSTER), 1, 1);
      register(var0, FISHERMAN, getBlockStates(Blocks.BARREL), 1, 1);
      register(var0, FLETCHER, getBlockStates(Blocks.FLETCHING_TABLE), 1, 1);
      register(var0, LEATHERWORKER, CAULDRONS, 1, 1);
      register(var0, LIBRARIAN, getBlockStates(Blocks.LECTERN), 1, 1);
      register(var0, MASON, getBlockStates(Blocks.STONECUTTER), 1, 1);
      register(var0, SHEPHERD, getBlockStates(Blocks.LOOM), 1, 1);
      register(var0, TOOLSMITH, getBlockStates(Blocks.SMITHING_TABLE), 1, 1);
      register(var0, WEAPONSMITH, getBlockStates(Blocks.GRINDSTONE), 1, 1);
      register(var0, HOME, BEDS, 1, 1);
      register(var0, MEETING, getBlockStates(Blocks.BELL), 32, 6);
      register(var0, BEEHIVE, getBlockStates(Blocks.BEEHIVE), 0, 1);
      register(var0, BEE_NEST, getBlockStates(Blocks.BEE_NEST), 0, 1);
      register(var0, NETHER_PORTAL, getBlockStates(Blocks.NETHER_PORTAL), 0, 1);
      register(var0, LODESTONE, getBlockStates(Blocks.LODESTONE), 0, 1);
      return register(var0, LIGHTNING_ROD, getBlockStates(Blocks.LIGHTNING_ROD), 0, 1);
   }

   static {
      BEDS = (Set)ImmutableList.of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap((var0) -> {
         return var0.getStateDefinition().getPossibleStates().stream();
      }).filter((var0) -> {
         return var0.getValue(BedBlock.PART) == BedPart.HEAD;
      }).collect(ImmutableSet.toImmutableSet());
      CAULDRONS = (Set)ImmutableList.of(Blocks.CAULDRON, Blocks.LAVA_CAULDRON, Blocks.WATER_CAULDRON, Blocks.POWDER_SNOW_CAULDRON).stream().flatMap((var0) -> {
         return var0.getStateDefinition().getPossibleStates().stream();
      }).collect(ImmutableSet.toImmutableSet());
      TYPE_BY_STATE = Maps.newHashMap();
   }
}
