package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class PoiTypes {
   private static final Map<BlockState, Holder<PoiType>> TYPE_BY_STATE = Maps.newHashMap();
   private static final Set<BlockState> BEDS;
   private static final Set<BlockState> CAULDRONS;
   private static final Set<BlockState> LIGHTNING_RODS;
   public static final Holder<PoiType> ARMORER;
   public static final Holder<PoiType> BUTCHER;
   public static final Holder<PoiType> CARTOGRAPHER;
   public static final Holder<PoiType> CLERIC;
   public static final Holder<PoiType> FARMER;
   public static final Holder<PoiType> FISHERMAN;
   public static final Holder<PoiType> FLETCHER;
   public static final Holder<PoiType> LEATHERWORKER;
   public static final Holder<PoiType> LIBRARIAN;
   public static final Holder<PoiType> MASON;
   public static final Holder<PoiType> SHEPHERD;
   public static final Holder<PoiType> TOOLSMITH;
   public static final Holder<PoiType> WEAPONSMITH;
   public static final Holder<PoiType> HOME;
   public static final Holder<PoiType> MEETING;
   public static final Holder<PoiType> BEEHIVE;
   public static final Holder<PoiType> BEE_NEST;
   public static final Holder<PoiType> NETHER_PORTAL;
   public static final Holder<PoiType> LODESTONE;
   public static final Holder<PoiType> LIGHTNING_ROD;
   public static final Holder<PoiType> TEST_INSTANCE;

   public PoiTypes() {
      super();
   }

   private static Set<BlockState> getBlockStates(final Block block) {
      return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
   }

   private static Holder<PoiType> register(final ResourceKey<PoiType> key, final Set<BlockState> matchingStates, final int maxTickets, final int validRange) {
      PoiType poiType = new PoiType(matchingStates, maxTickets, validRange);
      Holder.Reference<PoiType> value = Registry.registerForHolder(BuiltInRegistries.POINT_OF_INTEREST_TYPE, key, poiType);
      registerBlockStates(value, matchingStates);
      return value;
   }

   private static void registerBlockStates(final Holder<PoiType> type, final Set<BlockState> matchingStates) {
      matchingStates.forEach((blockState) -> {
         Holder<PoiType> previous = (Holder)TYPE_BY_STATE.put(blockState, type);
         if (previous != null) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException(String.format(Locale.ROOT, "%s is defined in more than one PoI type", blockState)));
         }
      });
   }

   public static Optional<Holder<PoiType>> forState(final BlockState state) {
      return Optional.ofNullable((Holder)TYPE_BY_STATE.get(state));
   }

   public static boolean hasPoi(final BlockState state) {
      return TYPE_BY_STATE.containsKey(state);
   }

   public static Holder<PoiType> bootstrap(final Registry<PoiType> registry) {
      return LIGHTNING_ROD;
   }

   static {
      BEDS = (Set)Blocks.BED.asList().stream().flatMap((block) -> block.getStateDefinition().getPossibleStates().stream()).filter((state) -> state.getValue(BedBlock.PART) == BedPart.HEAD).collect(ImmutableSet.toImmutableSet());
      CAULDRONS = (Set)ImmutableList.of(Blocks.CAULDRON, Blocks.LAVA_CAULDRON, Blocks.WATER_CAULDRON, Blocks.POWDER_SNOW_CAULDRON).stream().flatMap((block) -> block.getStateDefinition().getPossibleStates().stream()).collect(ImmutableSet.toImmutableSet());
      LIGHTNING_RODS = (Set)Blocks.LIGHTNING_ROD.asList().stream().flatMap((block) -> block.getStateDefinition().getPossibleStates().stream()).collect(ImmutableSet.toImmutableSet());
      ARMORER = register(PoiTypeIds.ARMORER, getBlockStates(Blocks.BLAST_FURNACE), 1, 1);
      BUTCHER = register(PoiTypeIds.BUTCHER, getBlockStates(Blocks.SMOKER), 1, 1);
      CARTOGRAPHER = register(PoiTypeIds.CARTOGRAPHER, getBlockStates(Blocks.CARTOGRAPHY_TABLE), 1, 1);
      CLERIC = register(PoiTypeIds.CLERIC, getBlockStates(Blocks.BREWING_STAND), 1, 1);
      FARMER = register(PoiTypeIds.FARMER, getBlockStates(Blocks.COMPOSTER), 1, 1);
      FISHERMAN = register(PoiTypeIds.FISHERMAN, getBlockStates(Blocks.BARREL), 1, 1);
      FLETCHER = register(PoiTypeIds.FLETCHER, getBlockStates(Blocks.FLETCHING_TABLE), 1, 1);
      LEATHERWORKER = register(PoiTypeIds.LEATHERWORKER, CAULDRONS, 1, 1);
      LIBRARIAN = register(PoiTypeIds.LIBRARIAN, getBlockStates(Blocks.LECTERN), 1, 1);
      MASON = register(PoiTypeIds.MASON, getBlockStates(Blocks.STONECUTTER), 1, 1);
      SHEPHERD = register(PoiTypeIds.SHEPHERD, getBlockStates(Blocks.LOOM), 1, 1);
      TOOLSMITH = register(PoiTypeIds.TOOLSMITH, getBlockStates(Blocks.SMITHING_TABLE), 1, 1);
      WEAPONSMITH = register(PoiTypeIds.WEAPONSMITH, getBlockStates(Blocks.GRINDSTONE), 1, 1);
      HOME = register(PoiTypeIds.HOME, BEDS, 1, 1);
      MEETING = register(PoiTypeIds.MEETING, getBlockStates(Blocks.BELL), 32, 6);
      BEEHIVE = register(PoiTypeIds.BEEHIVE, getBlockStates(Blocks.BEEHIVE), 0, 1);
      BEE_NEST = register(PoiTypeIds.BEE_NEST, getBlockStates(Blocks.BEE_NEST), 0, 1);
      NETHER_PORTAL = register(PoiTypeIds.NETHER_PORTAL, getBlockStates(Blocks.NETHER_PORTAL), 0, 1);
      LODESTONE = register(PoiTypeIds.LODESTONE, getBlockStates(Blocks.LODESTONE), 0, 1);
      LIGHTNING_ROD = register(PoiTypeIds.LIGHTNING_ROD, LIGHTNING_RODS, 0, 1);
      TEST_INSTANCE = register(PoiTypeIds.TEST_INSTANCE, getBlockStates(Blocks.TEST_INSTANCE_BLOCK), 0, 1);
   }
}
