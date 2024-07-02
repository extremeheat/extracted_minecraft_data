package net.minecraft.world.entity.decoration;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class PaintingVariants {
   public static final ResourceKey<PaintingVariant> KEBAB = create("kebab");
   public static final ResourceKey<PaintingVariant> AZTEC = create("aztec");
   public static final ResourceKey<PaintingVariant> ALBAN = create("alban");
   public static final ResourceKey<PaintingVariant> AZTEC2 = create("aztec2");
   public static final ResourceKey<PaintingVariant> BOMB = create("bomb");
   public static final ResourceKey<PaintingVariant> PLANT = create("plant");
   public static final ResourceKey<PaintingVariant> WASTELAND = create("wasteland");
   public static final ResourceKey<PaintingVariant> POOL = create("pool");
   public static final ResourceKey<PaintingVariant> COURBET = create("courbet");
   public static final ResourceKey<PaintingVariant> SEA = create("sea");
   public static final ResourceKey<PaintingVariant> SUNSET = create("sunset");
   public static final ResourceKey<PaintingVariant> CREEBET = create("creebet");
   public static final ResourceKey<PaintingVariant> WANDERER = create("wanderer");
   public static final ResourceKey<PaintingVariant> GRAHAM = create("graham");
   public static final ResourceKey<PaintingVariant> MATCH = create("match");
   public static final ResourceKey<PaintingVariant> BUST = create("bust");
   public static final ResourceKey<PaintingVariant> STAGE = create("stage");
   public static final ResourceKey<PaintingVariant> VOID = create("void");
   public static final ResourceKey<PaintingVariant> SKULL_AND_ROSES = create("skull_and_roses");
   public static final ResourceKey<PaintingVariant> WITHER = create("wither");
   public static final ResourceKey<PaintingVariant> FIGHTERS = create("fighters");
   public static final ResourceKey<PaintingVariant> POINTER = create("pointer");
   public static final ResourceKey<PaintingVariant> PIGSCENE = create("pigscene");
   public static final ResourceKey<PaintingVariant> BURNING_SKULL = create("burning_skull");
   public static final ResourceKey<PaintingVariant> SKELETON = create("skeleton");
   public static final ResourceKey<PaintingVariant> DONKEY_KONG = create("donkey_kong");
   public static final ResourceKey<PaintingVariant> EARTH = create("earth");
   public static final ResourceKey<PaintingVariant> WIND = create("wind");
   public static final ResourceKey<PaintingVariant> WATER = create("water");
   public static final ResourceKey<PaintingVariant> FIRE = create("fire");
   public static final ResourceKey<PaintingVariant> BAROQUE = create("baroque");
   public static final ResourceKey<PaintingVariant> HUMBLE = create("humble");
   public static final ResourceKey<PaintingVariant> MEDITATIVE = create("meditative");
   public static final ResourceKey<PaintingVariant> PRAIRIE_RIDE = create("prairie_ride");
   public static final ResourceKey<PaintingVariant> UNPACKED = create("unpacked");
   public static final ResourceKey<PaintingVariant> BACKYARD = create("backyard");
   public static final ResourceKey<PaintingVariant> BOUQUET = create("bouquet");
   public static final ResourceKey<PaintingVariant> CAVEBIRD = create("cavebird");
   public static final ResourceKey<PaintingVariant> CHANGING = create("changing");
   public static final ResourceKey<PaintingVariant> COTAN = create("cotan");
   public static final ResourceKey<PaintingVariant> ENDBOSS = create("endboss");
   public static final ResourceKey<PaintingVariant> FERN = create("fern");
   public static final ResourceKey<PaintingVariant> FINDING = create("finding");
   public static final ResourceKey<PaintingVariant> LOWMIST = create("lowmist");
   public static final ResourceKey<PaintingVariant> ORB = create("orb");
   public static final ResourceKey<PaintingVariant> OWLEMONS = create("owlemons");
   public static final ResourceKey<PaintingVariant> PASSAGE = create("passage");
   public static final ResourceKey<PaintingVariant> POND = create("pond");
   public static final ResourceKey<PaintingVariant> SUNFLOWERS = create("sunflowers");
   public static final ResourceKey<PaintingVariant> TIDES = create("tides");

   public PaintingVariants() {
      super();
   }

   public static void bootstrap(BootstrapContext<PaintingVariant> var0) {
      register(var0, KEBAB, 1, 1);
      register(var0, AZTEC, 1, 1);
      register(var0, ALBAN, 1, 1);
      register(var0, AZTEC2, 1, 1);
      register(var0, BOMB, 1, 1);
      register(var0, PLANT, 1, 1);
      register(var0, WASTELAND, 1, 1);
      register(var0, POOL, 2, 1);
      register(var0, COURBET, 2, 1);
      register(var0, SEA, 2, 1);
      register(var0, SUNSET, 2, 1);
      register(var0, CREEBET, 2, 1);
      register(var0, WANDERER, 1, 2);
      register(var0, GRAHAM, 1, 2);
      register(var0, MATCH, 2, 2);
      register(var0, BUST, 2, 2);
      register(var0, STAGE, 2, 2);
      register(var0, VOID, 2, 2);
      register(var0, SKULL_AND_ROSES, 2, 2);
      register(var0, WITHER, 2, 2);
      register(var0, FIGHTERS, 4, 2);
      register(var0, POINTER, 4, 4);
      register(var0, PIGSCENE, 4, 4);
      register(var0, BURNING_SKULL, 4, 4);
      register(var0, SKELETON, 4, 3);
      register(var0, EARTH, 2, 2);
      register(var0, WIND, 2, 2);
      register(var0, WATER, 2, 2);
      register(var0, FIRE, 2, 2);
      register(var0, DONKEY_KONG, 4, 3);
      register(var0, BAROQUE, 2, 2);
      register(var0, HUMBLE, 2, 2);
      register(var0, MEDITATIVE, 1, 1);
      register(var0, PRAIRIE_RIDE, 1, 2);
      register(var0, UNPACKED, 4, 4);
      register(var0, BACKYARD, 3, 4);
      register(var0, BOUQUET, 3, 3);
      register(var0, CAVEBIRD, 3, 3);
      register(var0, CHANGING, 4, 2);
      register(var0, COTAN, 3, 3);
      register(var0, ENDBOSS, 3, 3);
      register(var0, FERN, 3, 3);
      register(var0, FINDING, 4, 2);
      register(var0, LOWMIST, 4, 2);
      register(var0, ORB, 4, 4);
      register(var0, OWLEMONS, 3, 3);
      register(var0, PASSAGE, 4, 2);
      register(var0, POND, 3, 4);
      register(var0, SUNFLOWERS, 3, 3);
      register(var0, TIDES, 3, 3);
   }

   private static void register(BootstrapContext<PaintingVariant> var0, ResourceKey<PaintingVariant> var1, int var2, int var3) {
      var0.register(var1, new PaintingVariant(var2, var3, var1.location()));
   }

   private static ResourceKey<PaintingVariant> create(String var0) {
      return ResourceKey.create(Registries.PAINTING_VARIANT, ResourceLocation.withDefaultNamespace(var0));
   }
}
