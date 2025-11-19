package net.minecraft.world.entity.npc.villager;

import com.google.common.collect.ImmutableSet;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;

public record VillagerProfession(Component name, Predicate<Holder<PoiType>> heldJobSite, Predicate<Holder<PoiType>> acquirableJobSite, ImmutableSet<Item> requestedItems, ImmutableSet<Block> secondaryPoi, @Nullable SoundEvent workSound) {
   public static final Predicate<Holder<PoiType>> ALL_ACQUIRABLE_JOBS = (var0) -> var0.is(PoiTypeTags.ACQUIRABLE_JOB_SITE);
   public static final ResourceKey<VillagerProfession> NONE = createKey("none");
   public static final ResourceKey<VillagerProfession> ARMORER = createKey("armorer");
   public static final ResourceKey<VillagerProfession> BUTCHER = createKey("butcher");
   public static final ResourceKey<VillagerProfession> CARTOGRAPHER = createKey("cartographer");
   public static final ResourceKey<VillagerProfession> CLERIC = createKey("cleric");
   public static final ResourceKey<VillagerProfession> FARMER = createKey("farmer");
   public static final ResourceKey<VillagerProfession> FISHERMAN = createKey("fisherman");
   public static final ResourceKey<VillagerProfession> FLETCHER = createKey("fletcher");
   public static final ResourceKey<VillagerProfession> LEATHERWORKER = createKey("leatherworker");
   public static final ResourceKey<VillagerProfession> LIBRARIAN = createKey("librarian");
   public static final ResourceKey<VillagerProfession> MASON = createKey("mason");
   public static final ResourceKey<VillagerProfession> NITWIT = createKey("nitwit");
   public static final ResourceKey<VillagerProfession> SHEPHERD = createKey("shepherd");
   public static final ResourceKey<VillagerProfession> TOOLSMITH = createKey("toolsmith");
   public static final ResourceKey<VillagerProfession> WEAPONSMITH = createKey("weaponsmith");

   public VillagerProfession(Component var1, Predicate<Holder<PoiType>> var2, Predicate<Holder<PoiType>> var3, ImmutableSet<Item> var4, ImmutableSet<Block> var5, @Nullable SoundEvent var6) {
      super();
      this.name = var1;
      this.heldJobSite = var2;
      this.acquirableJobSite = var3;
      this.requestedItems = var4;
      this.secondaryPoi = var5;
      this.workSound = var6;
   }

   private static ResourceKey<VillagerProfession> createKey(String var0) {
      return ResourceKey.create(Registries.VILLAGER_PROFESSION, Identifier.withDefaultNamespace(var0));
   }

   private static VillagerProfession register(Registry<VillagerProfession> var0, ResourceKey<VillagerProfession> var1, ResourceKey<PoiType> var2, @Nullable SoundEvent var3) {
      return register(var0, var1, (var1x) -> var1x.is(var2), (var1x) -> var1x.is(var2), var3);
   }

   private static VillagerProfession register(Registry<VillagerProfession> var0, ResourceKey<VillagerProfession> var1, Predicate<Holder<PoiType>> var2, Predicate<Holder<PoiType>> var3, @Nullable SoundEvent var4) {
      return register(var0, var1, var2, var3, ImmutableSet.of(), ImmutableSet.of(), var4);
   }

   private static VillagerProfession register(Registry<VillagerProfession> var0, ResourceKey<VillagerProfession> var1, ResourceKey<PoiType> var2, ImmutableSet<Item> var3, ImmutableSet<Block> var4, @Nullable SoundEvent var5) {
      return register(var0, var1, (var1x) -> var1x.is(var2), (var1x) -> var1x.is(var2), var3, var4, var5);
   }

   private static VillagerProfession register(Registry<VillagerProfession> var0, ResourceKey<VillagerProfession> var1, Predicate<Holder<PoiType>> var2, Predicate<Holder<PoiType>> var3, ImmutableSet<Item> var4, ImmutableSet<Block> var5, @Nullable SoundEvent var6) {
      String var10004 = var1.identifier().getNamespace();
      return (VillagerProfession)Registry.register(var0, (ResourceKey)var1, new VillagerProfession(Component.translatable("entity." + var10004 + ".villager." + var1.identifier().getPath()), var2, var3, var4, var5, var6));
   }

   public static VillagerProfession bootstrap(Registry<VillagerProfession> var0) {
      register(var0, NONE, PoiType.NONE, ALL_ACQUIRABLE_JOBS, (SoundEvent)null);
      register(var0, ARMORER, PoiTypes.ARMORER, SoundEvents.VILLAGER_WORK_ARMORER);
      register(var0, BUTCHER, PoiTypes.BUTCHER, SoundEvents.VILLAGER_WORK_BUTCHER);
      register(var0, CARTOGRAPHER, PoiTypes.CARTOGRAPHER, SoundEvents.VILLAGER_WORK_CARTOGRAPHER);
      register(var0, CLERIC, PoiTypes.CLERIC, SoundEvents.VILLAGER_WORK_CLERIC);
      register(var0, FARMER, PoiTypes.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL), ImmutableSet.of(Blocks.FARMLAND), SoundEvents.VILLAGER_WORK_FARMER);
      register(var0, FISHERMAN, PoiTypes.FISHERMAN, SoundEvents.VILLAGER_WORK_FISHERMAN);
      register(var0, FLETCHER, PoiTypes.FLETCHER, SoundEvents.VILLAGER_WORK_FLETCHER);
      register(var0, LEATHERWORKER, PoiTypes.LEATHERWORKER, SoundEvents.VILLAGER_WORK_LEATHERWORKER);
      register(var0, LIBRARIAN, PoiTypes.LIBRARIAN, SoundEvents.VILLAGER_WORK_LIBRARIAN);
      register(var0, MASON, PoiTypes.MASON, SoundEvents.VILLAGER_WORK_MASON);
      register(var0, NITWIT, PoiType.NONE, PoiType.NONE, (SoundEvent)null);
      register(var0, SHEPHERD, PoiTypes.SHEPHERD, SoundEvents.VILLAGER_WORK_SHEPHERD);
      register(var0, TOOLSMITH, PoiTypes.TOOLSMITH, SoundEvents.VILLAGER_WORK_TOOLSMITH);
      return register(var0, WEAPONSMITH, PoiTypes.WEAPONSMITH, SoundEvents.VILLAGER_WORK_WEAPONSMITH);
   }
}
