package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record VillagerProfession(
   String q, Predicate<Holder<PoiType>> r, Predicate<Holder<PoiType>> s, ImmutableSet<Item> t, ImmutableSet<Block> u, @Nullable SoundEvent v
) {
   private final String name;
   private final Predicate<Holder<PoiType>> heldJobSite;
   private final Predicate<Holder<PoiType>> acquirableJobSite;
   private final ImmutableSet<Item> requestedItems;
   private final ImmutableSet<Block> secondaryPoi;
   @Nullable
   private final SoundEvent workSound;
   public static final Predicate<Holder<PoiType>> ALL_ACQUIRABLE_JOBS = var0 -> var0.is(PoiTypeTags.ACQUIRABLE_JOB_SITE);
   public static final VillagerProfession NONE = register("none", PoiType.NONE, ALL_ACQUIRABLE_JOBS, null);
   public static final VillagerProfession ARMORER = register("armorer", PoiTypes.ARMORER, SoundEvents.VILLAGER_WORK_ARMORER);
   public static final VillagerProfession BUTCHER = register("butcher", PoiTypes.BUTCHER, SoundEvents.VILLAGER_WORK_BUTCHER);
   public static final VillagerProfession CARTOGRAPHER = register("cartographer", PoiTypes.CARTOGRAPHER, SoundEvents.VILLAGER_WORK_CARTOGRAPHER);
   public static final VillagerProfession CLERIC = register("cleric", PoiTypes.CLERIC, SoundEvents.VILLAGER_WORK_CLERIC);
   public static final VillagerProfession FARMER = register(
      "farmer",
      PoiTypes.FARMER,
      ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL),
      ImmutableSet.of(Blocks.FARMLAND),
      SoundEvents.VILLAGER_WORK_FARMER
   );
   public static final VillagerProfession FISHERMAN = register("fisherman", PoiTypes.FISHERMAN, SoundEvents.VILLAGER_WORK_FISHERMAN);
   public static final VillagerProfession FLETCHER = register("fletcher", PoiTypes.FLETCHER, SoundEvents.VILLAGER_WORK_FLETCHER);
   public static final VillagerProfession LEATHERWORKER = register("leatherworker", PoiTypes.LEATHERWORKER, SoundEvents.VILLAGER_WORK_LEATHERWORKER);
   public static final VillagerProfession LIBRARIAN = register("librarian", PoiTypes.LIBRARIAN, SoundEvents.VILLAGER_WORK_LIBRARIAN);
   public static final VillagerProfession MASON = register("mason", PoiTypes.MASON, SoundEvents.VILLAGER_WORK_MASON);
   public static final VillagerProfession NITWIT = register("nitwit", PoiType.NONE, PoiType.NONE, null);
   public static final VillagerProfession SHEPHERD = register("shepherd", PoiTypes.SHEPHERD, SoundEvents.VILLAGER_WORK_SHEPHERD);
   public static final VillagerProfession TOOLSMITH = register("toolsmith", PoiTypes.TOOLSMITH, SoundEvents.VILLAGER_WORK_TOOLSMITH);
   public static final VillagerProfession WEAPONSMITH = register("weaponsmith", PoiTypes.WEAPONSMITH, SoundEvents.VILLAGER_WORK_WEAPONSMITH);

   public VillagerProfession(
      String var1,
      Predicate<Holder<PoiType>> var2,
      Predicate<Holder<PoiType>> var3,
      ImmutableSet<Item> var4,
      ImmutableSet<Block> var5,
      @Nullable SoundEvent var6
   ) {
      super();
      this.name = var1;
      this.heldJobSite = var2;
      this.acquirableJobSite = var3;
      this.requestedItems = var4;
      this.secondaryPoi = var5;
      this.workSound = var6;
   }

   @Override
   public String toString() {
      return this.name;
   }

   private static VillagerProfession register(String var0, ResourceKey<PoiType> var1, @Nullable SoundEvent var2) {
      return register(var0, var1x -> var1x.is(var1), var1x -> var1x.is(var1), var2);
   }

   private static VillagerProfession register(String var0, Predicate<Holder<PoiType>> var1, Predicate<Holder<PoiType>> var2, @Nullable SoundEvent var3) {
      return register(var0, var1, var2, ImmutableSet.of(), ImmutableSet.of(), var3);
   }

   private static VillagerProfession register(
      String var0, ResourceKey<PoiType> var1, ImmutableSet<Item> var2, ImmutableSet<Block> var3, @Nullable SoundEvent var4
   ) {
      return register(var0, var1x -> var1x.is(var1), var1x -> var1x.is(var1), var2, var3, var4);
   }

   private static VillagerProfession register(
      String var0,
      Predicate<Holder<PoiType>> var1,
      Predicate<Holder<PoiType>> var2,
      ImmutableSet<Item> var3,
      ImmutableSet<Block> var4,
      @Nullable SoundEvent var5
   ) {
      return Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, new ResourceLocation(var0), new VillagerProfession(var0, var1, var2, var3, var4, var5));
   }
}
