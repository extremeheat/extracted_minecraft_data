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

public record VillagerProfession(String name, Predicate<Holder<PoiType>> heldJobSite, Predicate<Holder<PoiType>> acquirableJobSite, ImmutableSet<Item> requestedItems, ImmutableSet<Block> secondaryPoi, @Nullable SoundEvent workSound) {
   public static final Predicate<Holder<PoiType>> ALL_ACQUIRABLE_JOBS = (var0) -> {
      return var0.is(PoiTypeTags.ACQUIRABLE_JOB_SITE);
   };
   public static final VillagerProfession NONE;
   public static final VillagerProfession ARMORER;
   public static final VillagerProfession BUTCHER;
   public static final VillagerProfession CARTOGRAPHER;
   public static final VillagerProfession CLERIC;
   public static final VillagerProfession FARMER;
   public static final VillagerProfession FISHERMAN;
   public static final VillagerProfession FLETCHER;
   public static final VillagerProfession LEATHERWORKER;
   public static final VillagerProfession LIBRARIAN;
   public static final VillagerProfession MASON;
   public static final VillagerProfession NITWIT;
   public static final VillagerProfession SHEPHERD;
   public static final VillagerProfession TOOLSMITH;
   public static final VillagerProfession WEAPONSMITH;

   public VillagerProfession(String name, Predicate<Holder<PoiType>> heldJobSite, Predicate<Holder<PoiType>> acquirableJobSite, ImmutableSet<Item> requestedItems, ImmutableSet<Block> secondaryPoi, @Nullable SoundEvent workSound) {
      super();
      this.name = name;
      this.heldJobSite = heldJobSite;
      this.acquirableJobSite = acquirableJobSite;
      this.requestedItems = requestedItems;
      this.secondaryPoi = secondaryPoi;
      this.workSound = workSound;
   }

   public String toString() {
      return this.name;
   }

   private static VillagerProfession register(String var0, ResourceKey<PoiType> var1, @Nullable SoundEvent var2) {
      return register(var0, (var1x) -> {
         return var1x.is(var1);
      }, (var1x) -> {
         return var1x.is(var1);
      }, var2);
   }

   private static VillagerProfession register(String var0, Predicate<Holder<PoiType>> var1, Predicate<Holder<PoiType>> var2, @Nullable SoundEvent var3) {
      return register(var0, var1, var2, ImmutableSet.of(), ImmutableSet.of(), var3);
   }

   private static VillagerProfession register(String var0, ResourceKey<PoiType> var1, ImmutableSet<Item> var2, ImmutableSet<Block> var3, @Nullable SoundEvent var4) {
      return register(var0, (var1x) -> {
         return var1x.is(var1);
      }, (var1x) -> {
         return var1x.is(var1);
      }, var2, var3, var4);
   }

   private static VillagerProfession register(String var0, Predicate<Holder<PoiType>> var1, Predicate<Holder<PoiType>> var2, ImmutableSet<Item> var3, ImmutableSet<Block> var4, @Nullable SoundEvent var5) {
      return (VillagerProfession)Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, (ResourceLocation)(new ResourceLocation(var0)), new VillagerProfession(var0, var1, var2, var3, var4, var5));
   }

   public String name() {
      return this.name;
   }

   public Predicate<Holder<PoiType>> heldJobSite() {
      return this.heldJobSite;
   }

   public Predicate<Holder<PoiType>> acquirableJobSite() {
      return this.acquirableJobSite;
   }

   public ImmutableSet<Item> requestedItems() {
      return this.requestedItems;
   }

   public ImmutableSet<Block> secondaryPoi() {
      return this.secondaryPoi;
   }

   @Nullable
   public SoundEvent workSound() {
      return this.workSound;
   }

   static {
      NONE = register("none", PoiType.NONE, ALL_ACQUIRABLE_JOBS, (SoundEvent)null);
      ARMORER = register("armorer", PoiTypes.ARMORER, SoundEvents.VILLAGER_WORK_ARMORER);
      BUTCHER = register("butcher", PoiTypes.BUTCHER, SoundEvents.VILLAGER_WORK_BUTCHER);
      CARTOGRAPHER = register("cartographer", PoiTypes.CARTOGRAPHER, SoundEvents.VILLAGER_WORK_CARTOGRAPHER);
      CLERIC = register("cleric", PoiTypes.CLERIC, SoundEvents.VILLAGER_WORK_CLERIC);
      FARMER = register("farmer", PoiTypes.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL), ImmutableSet.of(Blocks.FARMLAND), SoundEvents.VILLAGER_WORK_FARMER);
      FISHERMAN = register("fisherman", PoiTypes.FISHERMAN, SoundEvents.VILLAGER_WORK_FISHERMAN);
      FLETCHER = register("fletcher", PoiTypes.FLETCHER, SoundEvents.VILLAGER_WORK_FLETCHER);
      LEATHERWORKER = register("leatherworker", PoiTypes.LEATHERWORKER, SoundEvents.VILLAGER_WORK_LEATHERWORKER);
      LIBRARIAN = register("librarian", PoiTypes.LIBRARIAN, SoundEvents.VILLAGER_WORK_LIBRARIAN);
      MASON = register("mason", PoiTypes.MASON, SoundEvents.VILLAGER_WORK_MASON);
      NITWIT = register("nitwit", PoiType.NONE, PoiType.NONE, (SoundEvent)null);
      SHEPHERD = register("shepherd", PoiTypes.SHEPHERD, SoundEvents.VILLAGER_WORK_SHEPHERD);
      TOOLSMITH = register("toolsmith", PoiTypes.TOOLSMITH, SoundEvents.VILLAGER_WORK_TOOLSMITH);
      WEAPONSMITH = register("weaponsmith", PoiTypes.WEAPONSMITH, SoundEvents.VILLAGER_WORK_WEAPONSMITH);
   }
}
