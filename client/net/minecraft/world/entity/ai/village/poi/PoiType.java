package net.minecraft.world.entity.ai.village.poi;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class PoiType {
   private static final Supplier<Set<PoiType>> ALL_JOB_POI_TYPES = Suppliers.memoize(() -> {
      return (Set)Registry.VILLAGER_PROFESSION.stream().map(VillagerProfession::getJobPoiType).collect(Collectors.toSet());
   });
   public static final Predicate<PoiType> ALL_JOBS = (var0) -> {
      return ((Set)ALL_JOB_POI_TYPES.get()).contains(var0);
   };
   public static final Predicate<PoiType> ALL = (var0) -> {
      return true;
   };
   private static final Set<BlockState> BEDS;
   private static final Map<BlockState, PoiType> TYPE_BY_STATE;
   public static final PoiType UNEMPLOYED;
   public static final PoiType ARMORER;
   public static final PoiType BUTCHER;
   public static final PoiType CARTOGRAPHER;
   public static final PoiType CLERIC;
   public static final PoiType FARMER;
   public static final PoiType FISHERMAN;
   public static final PoiType FLETCHER;
   public static final PoiType LEATHERWORKER;
   public static final PoiType LIBRARIAN;
   public static final PoiType MASON;
   public static final PoiType NITWIT;
   public static final PoiType SHEPHERD;
   public static final PoiType TOOLSMITH;
   public static final PoiType WEAPONSMITH;
   public static final PoiType HOME;
   public static final PoiType MEETING;
   public static final PoiType BEEHIVE;
   public static final PoiType BEE_NEST;
   public static final PoiType NETHER_PORTAL;
   public static final PoiType LODESTONE;
   protected static final Set<BlockState> ALL_STATES;
   private final String name;
   private final Set<BlockState> matchingStates;
   private final int maxTickets;
   private final Predicate<PoiType> predicate;
   private final int validRange;

   private static Set<BlockState> getBlockStates(Block var0) {
      return ImmutableSet.copyOf(var0.getStateDefinition().getPossibleStates());
   }

   private PoiType(String var1, Set<BlockState> var2, int var3, Predicate<PoiType> var4, int var5) {
      super();
      this.name = var1;
      this.matchingStates = ImmutableSet.copyOf(var2);
      this.maxTickets = var3;
      this.predicate = var4;
      this.validRange = var5;
   }

   private PoiType(String var1, Set<BlockState> var2, int var3, int var4) {
      super();
      this.name = var1;
      this.matchingStates = ImmutableSet.copyOf(var2);
      this.maxTickets = var3;
      this.predicate = (var1x) -> {
         return var1x == this;
      };
      this.validRange = var4;
   }

   public int getMaxTickets() {
      return this.maxTickets;
   }

   public Predicate<PoiType> getPredicate() {
      return this.predicate;
   }

   public int getValidRange() {
      return this.validRange;
   }

   public String toString() {
      return this.name;
   }

   private static PoiType register(String var0, Set<BlockState> var1, int var2, int var3) {
      return registerBlockStates((PoiType)Registry.register(Registry.POINT_OF_INTEREST_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new PoiType(var0, var1, var2, var3)));
   }

   private static PoiType register(String var0, Set<BlockState> var1, int var2, Predicate<PoiType> var3, int var4) {
      return registerBlockStates((PoiType)Registry.register(Registry.POINT_OF_INTEREST_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new PoiType(var0, var1, var2, var3, var4)));
   }

   private static PoiType registerBlockStates(PoiType var0) {
      var0.matchingStates.forEach((var1) -> {
         PoiType var2 = (PoiType)TYPE_BY_STATE.put(var1, var0);
         if (var2 != null) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException(String.format("%s is defined in too many tags", var1)));
         }
      });
      return var0;
   }

   public static Optional<PoiType> forState(BlockState var0) {
      return Optional.ofNullable(TYPE_BY_STATE.get(var0));
   }

   static {
      BEDS = (Set)ImmutableList.of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap((var0) -> {
         return var0.getStateDefinition().getPossibleStates().stream();
      }).filter((var0) -> {
         return var0.getValue(BedBlock.PART) == BedPart.HEAD;
      }).collect(ImmutableSet.toImmutableSet());
      TYPE_BY_STATE = Maps.newHashMap();
      UNEMPLOYED = register("unemployed", ImmutableSet.of(), 1, ALL_JOBS, 1);
      ARMORER = register("armorer", getBlockStates(Blocks.BLAST_FURNACE), 1, 1);
      BUTCHER = register("butcher", getBlockStates(Blocks.SMOKER), 1, 1);
      CARTOGRAPHER = register("cartographer", getBlockStates(Blocks.CARTOGRAPHY_TABLE), 1, 1);
      CLERIC = register("cleric", getBlockStates(Blocks.BREWING_STAND), 1, 1);
      FARMER = register("farmer", getBlockStates(Blocks.COMPOSTER), 1, 1);
      FISHERMAN = register("fisherman", getBlockStates(Blocks.BARREL), 1, 1);
      FLETCHER = register("fletcher", getBlockStates(Blocks.FLETCHING_TABLE), 1, 1);
      LEATHERWORKER = register("leatherworker", getBlockStates(Blocks.CAULDRON), 1, 1);
      LIBRARIAN = register("librarian", getBlockStates(Blocks.LECTERN), 1, 1);
      MASON = register("mason", getBlockStates(Blocks.STONECUTTER), 1, 1);
      NITWIT = register("nitwit", ImmutableSet.of(), 1, 1);
      SHEPHERD = register("shepherd", getBlockStates(Blocks.LOOM), 1, 1);
      TOOLSMITH = register("toolsmith", getBlockStates(Blocks.SMITHING_TABLE), 1, 1);
      WEAPONSMITH = register("weaponsmith", getBlockStates(Blocks.GRINDSTONE), 1, 1);
      HOME = register("home", BEDS, 1, 1);
      MEETING = register("meeting", getBlockStates(Blocks.BELL), 32, 6);
      BEEHIVE = register("beehive", getBlockStates(Blocks.BEEHIVE), 0, 1);
      BEE_NEST = register("bee_nest", getBlockStates(Blocks.BEE_NEST), 0, 1);
      NETHER_PORTAL = register("nether_portal", getBlockStates(Blocks.NETHER_PORTAL), 0, 1);
      LODESTONE = register("lodestone", getBlockStates(Blocks.LODESTONE), 0, 1);
      ALL_STATES = new ObjectOpenHashSet(TYPE_BY_STATE.keySet());
   }
}
