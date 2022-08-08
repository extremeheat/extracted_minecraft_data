package net.minecraft.world.entity.decoration;

import net.minecraft.core.Registry;
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

   public PaintingVariants() {
      super();
   }

   public static PaintingVariant bootstrap(Registry<PaintingVariant> var0) {
      Registry.register(var0, (ResourceKey)KEBAB, new PaintingVariant(16, 16));
      Registry.register(var0, (ResourceKey)AZTEC, new PaintingVariant(16, 16));
      Registry.register(var0, (ResourceKey)ALBAN, new PaintingVariant(16, 16));
      Registry.register(var0, (ResourceKey)AZTEC2, new PaintingVariant(16, 16));
      Registry.register(var0, (ResourceKey)BOMB, new PaintingVariant(16, 16));
      Registry.register(var0, (ResourceKey)PLANT, new PaintingVariant(16, 16));
      Registry.register(var0, (ResourceKey)WASTELAND, new PaintingVariant(16, 16));
      Registry.register(var0, (ResourceKey)POOL, new PaintingVariant(32, 16));
      Registry.register(var0, (ResourceKey)COURBET, new PaintingVariant(32, 16));
      Registry.register(var0, (ResourceKey)SEA, new PaintingVariant(32, 16));
      Registry.register(var0, (ResourceKey)SUNSET, new PaintingVariant(32, 16));
      Registry.register(var0, (ResourceKey)CREEBET, new PaintingVariant(32, 16));
      Registry.register(var0, (ResourceKey)WANDERER, new PaintingVariant(16, 32));
      Registry.register(var0, (ResourceKey)GRAHAM, new PaintingVariant(16, 32));
      Registry.register(var0, (ResourceKey)MATCH, new PaintingVariant(32, 32));
      Registry.register(var0, (ResourceKey)BUST, new PaintingVariant(32, 32));
      Registry.register(var0, (ResourceKey)STAGE, new PaintingVariant(32, 32));
      Registry.register(var0, (ResourceKey)VOID, new PaintingVariant(32, 32));
      Registry.register(var0, (ResourceKey)SKULL_AND_ROSES, new PaintingVariant(32, 32));
      Registry.register(var0, (ResourceKey)WITHER, new PaintingVariant(32, 32));
      Registry.register(var0, (ResourceKey)FIGHTERS, new PaintingVariant(64, 32));
      Registry.register(var0, (ResourceKey)POINTER, new PaintingVariant(64, 64));
      Registry.register(var0, (ResourceKey)PIGSCENE, new PaintingVariant(64, 64));
      Registry.register(var0, (ResourceKey)BURNING_SKULL, new PaintingVariant(64, 64));
      Registry.register(var0, (ResourceKey)SKELETON, new PaintingVariant(64, 48));
      Registry.register(var0, (ResourceKey)EARTH, new PaintingVariant(32, 32));
      Registry.register(var0, (ResourceKey)WIND, new PaintingVariant(32, 32));
      Registry.register(var0, (ResourceKey)WATER, new PaintingVariant(32, 32));
      Registry.register(var0, (ResourceKey)FIRE, new PaintingVariant(32, 32));
      return (PaintingVariant)Registry.register(var0, (ResourceKey)DONKEY_KONG, new PaintingVariant(64, 48));
   }

   private static ResourceKey<PaintingVariant> create(String var0) {
      return ResourceKey.create(Registry.PAINTING_VARIANT_REGISTRY, new ResourceLocation(var0));
   }
}
