package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
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

   private VillagerProfession(String var1, PoiType var2, ImmutableSet<Item> var3, ImmutableSet<Block> var4) {
      super();
      this.name = var1;
      this.jobPoiType = var2;
      this.requestedItems = var3;
      this.secondaryPoi = var4;
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

   public String toString() {
      return this.name;
   }

   static VillagerProfession register(String var0, PoiType var1) {
      return register(var0, var1, ImmutableSet.of(), ImmutableSet.of());
   }

   static VillagerProfession register(String var0, PoiType var1, ImmutableSet<Item> var2, ImmutableSet<Block> var3) {
      return (VillagerProfession)Registry.register(Registry.VILLAGER_PROFESSION, (ResourceLocation)(new ResourceLocation(var0)), new VillagerProfession(var0, var1, var2, var3));
   }

   static {
      NONE = register("none", PoiType.UNEMPLOYED);
      ARMORER = register("armorer", PoiType.ARMORER);
      BUTCHER = register("butcher", PoiType.BUTCHER);
      CARTOGRAPHER = register("cartographer", PoiType.CARTOGRAPHER);
      CLERIC = register("cleric", PoiType.CLERIC);
      FARMER = register("farmer", PoiType.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS), ImmutableSet.of(Blocks.FARMLAND));
      FISHERMAN = register("fisherman", PoiType.FISHERMAN);
      FLETCHER = register("fletcher", PoiType.FLETCHER);
      LEATHERWORKER = register("leatherworker", PoiType.LEATHERWORKER);
      LIBRARIAN = register("librarian", PoiType.LIBRARIAN);
      MASON = register("mason", PoiType.MASON);
      NITWIT = register("nitwit", PoiType.NITWIT);
      SHEPHERD = register("shepherd", PoiType.SHEPHERD);
      TOOLSMITH = register("toolsmith", PoiType.TOOLSMITH);
      WEAPONSMITH = register("weaponsmith", PoiType.WEAPONSMITH);
   }
}
