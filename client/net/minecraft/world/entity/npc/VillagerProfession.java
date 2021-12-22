package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class VillagerProfession {
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
   private final String name;
   private final PoiType jobPoiType;
   private final ImmutableSet<Item> requestedItems;
   private final ImmutableSet<Block> secondaryPoi;
   @Nullable
   private final SoundEvent workSound;

   private VillagerProfession(String var1, PoiType var2, ImmutableSet<Item> var3, ImmutableSet<Block> var4, @Nullable SoundEvent var5) {
      super();
      this.name = var1;
      this.jobPoiType = var2;
      this.requestedItems = var3;
      this.secondaryPoi = var4;
      this.workSound = var5;
   }

   public String getName() {
      return this.name;
   }

   public PoiType getJobPoiType() {
      return this.jobPoiType;
   }

   public ImmutableSet<Item> getRequestedItems() {
      return this.requestedItems;
   }

   public ImmutableSet<Block> getSecondaryPoi() {
      return this.secondaryPoi;
   }

   @Nullable
   public SoundEvent getWorkSound() {
      return this.workSound;
   }

   public String toString() {
      return this.name;
   }

   static VillagerProfession register(String var0, PoiType var1, @Nullable SoundEvent var2) {
      return register(var0, var1, ImmutableSet.of(), ImmutableSet.of(), var2);
   }

   static VillagerProfession register(String var0, PoiType var1, ImmutableSet<Item> var2, ImmutableSet<Block> var3, @Nullable SoundEvent var4) {
      return (VillagerProfession)Registry.register(Registry.VILLAGER_PROFESSION, (ResourceLocation)(new ResourceLocation(var0)), new VillagerProfession(var0, var1, var2, var3, var4));
   }

   static {
      NONE = register("none", PoiType.UNEMPLOYED, (SoundEvent)null);
      ARMORER = register("armorer", PoiType.ARMORER, SoundEvents.VILLAGER_WORK_ARMORER);
      BUTCHER = register("butcher", PoiType.BUTCHER, SoundEvents.VILLAGER_WORK_BUTCHER);
      CARTOGRAPHER = register("cartographer", PoiType.CARTOGRAPHER, SoundEvents.VILLAGER_WORK_CARTOGRAPHER);
      CLERIC = register("cleric", PoiType.CLERIC, SoundEvents.VILLAGER_WORK_CLERIC);
      FARMER = register("farmer", PoiType.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL), ImmutableSet.of(Blocks.FARMLAND), SoundEvents.VILLAGER_WORK_FARMER);
      FISHERMAN = register("fisherman", PoiType.FISHERMAN, SoundEvents.VILLAGER_WORK_FISHERMAN);
      FLETCHER = register("fletcher", PoiType.FLETCHER, SoundEvents.VILLAGER_WORK_FLETCHER);
      LEATHERWORKER = register("leatherworker", PoiType.LEATHERWORKER, SoundEvents.VILLAGER_WORK_LEATHERWORKER);
      LIBRARIAN = register("librarian", PoiType.LIBRARIAN, SoundEvents.VILLAGER_WORK_LIBRARIAN);
      MASON = register("mason", PoiType.MASON, SoundEvents.VILLAGER_WORK_MASON);
      NITWIT = register("nitwit", PoiType.NITWIT, (SoundEvent)null);
      SHEPHERD = register("shepherd", PoiType.SHEPHERD, SoundEvents.VILLAGER_WORK_SHEPHERD);
      TOOLSMITH = register("toolsmith", PoiType.TOOLSMITH, SoundEvents.VILLAGER_WORK_TOOLSMITH);
      WEAPONSMITH = register("weaponsmith", PoiType.WEAPONSMITH, SoundEvents.VILLAGER_WORK_WEAPONSMITH);
   }
}
