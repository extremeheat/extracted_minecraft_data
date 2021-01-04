package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class PoiType {
   private static final Predicate<PoiType> ALL_JOBS = (var0) -> {
      return ((Set)Registry.VILLAGER_PROFESSION.stream().map(VillagerProfession::getJobPoiType).collect(Collectors.toSet())).contains(var0);
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
   private final String name;
   private final Set<BlockState> matchingStates;
   private final int maxTickets;
   @Nullable
   private final SoundEvent soundEvent;
   private final Predicate<PoiType> predicate;
   private final int validRange;

   private static Set<BlockState> getBlockStates(Block var0) {
      return ImmutableSet.copyOf(var0.getStateDefinition().getPossibleStates());
   }

   private PoiType(String var1, Set<BlockState> var2, int var3, @Nullable SoundEvent var4, Predicate<PoiType> var5, int var6) {
      super();
      this.name = var1;
      this.matchingStates = ImmutableSet.copyOf(var2);
      this.maxTickets = var3;
      this.soundEvent = var4;
      this.predicate = var5;
      this.validRange = var6;
   }

   private PoiType(String var1, Set<BlockState> var2, int var3, @Nullable SoundEvent var4, int var5) {
      super();
      this.name = var1;
      this.matchingStates = ImmutableSet.copyOf(var2);
      this.maxTickets = var3;
      this.soundEvent = var4;
      this.predicate = (var1x) -> {
         return var1x == this;
      };
      this.validRange = var5;
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

   @Nullable
   public SoundEvent getUseSound() {
      return this.soundEvent;
   }

   private static PoiType register(String var0, Set<BlockState> var1, int var2, @Nullable SoundEvent var3, int var4) {
      return registerBlockStates((PoiType)Registry.POINT_OF_INTEREST_TYPE.register(new ResourceLocation(var0), new PoiType(var0, var1, var2, var3, var4)));
   }

   private static PoiType register(String var0, Set<BlockState> var1, int var2, @Nullable SoundEvent var3, Predicate<PoiType> var4, int var5) {
      return registerBlockStates((PoiType)Registry.POINT_OF_INTEREST_TYPE.register(new ResourceLocation(var0), new PoiType(var0, var1, var2, var3, var4, var5)));
   }

   private static PoiType registerBlockStates(PoiType var0) {
      var0.matchingStates.forEach((var1) -> {
         PoiType var2 = (PoiType)TYPE_BY_STATE.put(var1, var0);
         if (var2 != null) {
            throw new IllegalStateException(String.format("%s is defined in too many tags", var1));
         }
      });
      return var0;
   }

   public static Optional<PoiType> forState(BlockState var0) {
      return Optional.ofNullable(TYPE_BY_STATE.get(var0));
   }

   public static Stream<BlockState> allPoiStates() {
      return TYPE_BY_STATE.keySet().stream();
   }

   static {
      BEDS = (Set)ImmutableList.of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap((var0) -> {
         return var0.getStateDefinition().getPossibleStates().stream();
      }).filter((var0) -> {
         return var0.getValue(BedBlock.PART) == BedPart.HEAD;
      }).collect(ImmutableSet.toImmutableSet());
      TYPE_BY_STATE = Maps.newHashMap();
      UNEMPLOYED = register("unemployed", ImmutableSet.of(), 1, (SoundEvent)null, ALL_JOBS, 1);
      ARMORER = register("armorer", getBlockStates(Blocks.BLAST_FURNACE), 1, SoundEvents.VILLAGER_WORK_ARMORER, 1);
      BUTCHER = register("butcher", getBlockStates(Blocks.SMOKER), 1, SoundEvents.VILLAGER_WORK_BUTCHER, 1);
      CARTOGRAPHER = register("cartographer", getBlockStates(Blocks.CARTOGRAPHY_TABLE), 1, SoundEvents.VILLAGER_WORK_CARTOGRAPHER, 1);
      CLERIC = register("cleric", getBlockStates(Blocks.BREWING_STAND), 1, SoundEvents.VILLAGER_WORK_CLERIC, 1);
      FARMER = register("farmer", getBlockStates(Blocks.COMPOSTER), 1, SoundEvents.VILLAGER_WORK_FARMER, 1);
      FISHERMAN = register("fisherman", getBlockStates(Blocks.BARREL), 1, SoundEvents.VILLAGER_WORK_FISHERMAN, 1);
      FLETCHER = register("fletcher", getBlockStates(Blocks.FLETCHING_TABLE), 1, SoundEvents.VILLAGER_WORK_FLETCHER, 1);
      LEATHERWORKER = register("leatherworker", getBlockStates(Blocks.CAULDRON), 1, SoundEvents.VILLAGER_WORK_LEATHERWORKER, 1);
      LIBRARIAN = register("librarian", getBlockStates(Blocks.LECTERN), 1, SoundEvents.VILLAGER_WORK_LIBRARIAN, 1);
      MASON = register("mason", getBlockStates(Blocks.STONECUTTER), 1, SoundEvents.VILLAGER_WORK_MASON, 1);
      NITWIT = register("nitwit", ImmutableSet.of(), 1, (SoundEvent)null, 1);
      SHEPHERD = register("shepherd", getBlockStates(Blocks.LOOM), 1, SoundEvents.VILLAGER_WORK_SHEPHERD, 1);
      TOOLSMITH = register("toolsmith", getBlockStates(Blocks.SMITHING_TABLE), 1, SoundEvents.VILLAGER_WORK_TOOLSMITH, 1);
      WEAPONSMITH = register("weaponsmith", getBlockStates(Blocks.GRINDSTONE), 1, SoundEvents.VILLAGER_WORK_WEAPONSMITH, 1);
      HOME = register("home", BEDS, 1, (SoundEvent)null, 1);
      MEETING = register("meeting", getBlockStates(Blocks.BELL), 32, (SoundEvent)null, 6);
   }
}
