package net.minecraft.world.entity.ai.village.poi;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class PoiTypeIds {
   public static final ResourceKey<PoiType> ARMORER = register("armorer");
   public static final ResourceKey<PoiType> BUTCHER = register("butcher");
   public static final ResourceKey<PoiType> CARTOGRAPHER = register("cartographer");
   public static final ResourceKey<PoiType> CLERIC = register("cleric");
   public static final ResourceKey<PoiType> FARMER = register("farmer");
   public static final ResourceKey<PoiType> FISHERMAN = register("fisherman");
   public static final ResourceKey<PoiType> FLETCHER = register("fletcher");
   public static final ResourceKey<PoiType> LEATHERWORKER = register("leatherworker");
   public static final ResourceKey<PoiType> LIBRARIAN = register("librarian");
   public static final ResourceKey<PoiType> MASON = register("mason");
   public static final ResourceKey<PoiType> SHEPHERD = register("shepherd");
   public static final ResourceKey<PoiType> TOOLSMITH = register("toolsmith");
   public static final ResourceKey<PoiType> WEAPONSMITH = register("weaponsmith");
   public static final ResourceKey<PoiType> HOME = register("home");
   public static final ResourceKey<PoiType> MEETING = register("meeting");
   public static final ResourceKey<PoiType> BEEHIVE = register("beehive");
   public static final ResourceKey<PoiType> BEE_NEST = register("bee_nest");
   public static final ResourceKey<PoiType> NETHER_PORTAL = register("nether_portal");
   public static final ResourceKey<PoiType> LODESTONE = register("lodestone");
   public static final ResourceKey<PoiType> LIGHTNING_ROD = register("lightning_rod");
   public static final ResourceKey<PoiType> TEST_INSTANCE = register("test_instance");

   public PoiTypeIds() {
      super();
   }

   private static ResourceKey<PoiType> register(final String name) {
      return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Identifier.withDefaultNamespace(name));
   }
}
