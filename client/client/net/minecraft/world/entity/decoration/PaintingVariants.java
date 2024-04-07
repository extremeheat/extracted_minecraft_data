package net.minecraft.world.entity.decoration;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
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
      Registry.register(var0, KEBAB, new PaintingVariant(16, 16));
      Registry.register(var0, AZTEC, new PaintingVariant(16, 16));
      Registry.register(var0, ALBAN, new PaintingVariant(16, 16));
      Registry.register(var0, AZTEC2, new PaintingVariant(16, 16));
      Registry.register(var0, BOMB, new PaintingVariant(16, 16));
      Registry.register(var0, PLANT, new PaintingVariant(16, 16));
      Registry.register(var0, WASTELAND, new PaintingVariant(16, 16));
      Registry.register(var0, POOL, new PaintingVariant(32, 16));
      Registry.register(var0, COURBET, new PaintingVariant(32, 16));
      Registry.register(var0, SEA, new PaintingVariant(32, 16));
      Registry.register(var0, SUNSET, new PaintingVariant(32, 16));
      Registry.register(var0, CREEBET, new PaintingVariant(32, 16));
      Registry.register(var0, WANDERER, new PaintingVariant(16, 32));
      Registry.register(var0, GRAHAM, new PaintingVariant(16, 32));
      Registry.register(var0, MATCH, new PaintingVariant(32, 32));
      Registry.register(var0, BUST, new PaintingVariant(32, 32));
      Registry.register(var0, STAGE, new PaintingVariant(32, 32));
      Registry.register(var0, VOID, new PaintingVariant(32, 32));
      Registry.register(var0, SKULL_AND_ROSES, new PaintingVariant(32, 32));
      Registry.register(var0, WITHER, new PaintingVariant(32, 32));
      Registry.register(var0, FIGHTERS, new PaintingVariant(64, 32));
      Registry.register(var0, POINTER, new PaintingVariant(64, 64));
      Registry.register(var0, PIGSCENE, new PaintingVariant(64, 64));
      Registry.register(var0, BURNING_SKULL, new PaintingVariant(64, 64));
      Registry.register(var0, SKELETON, new PaintingVariant(64, 48));
      Registry.register(var0, EARTH, new PaintingVariant(32, 32));
      Registry.register(var0, WIND, new PaintingVariant(32, 32));
      Registry.register(var0, WATER, new PaintingVariant(32, 32));
      Registry.register(var0, FIRE, new PaintingVariant(32, 32));
      return Registry.register(var0, DONKEY_KONG, new PaintingVariant(64, 48));
   }

   private static ResourceKey<PaintingVariant> create(String var0) {
      return ResourceKey.create(Registries.PAINTING_VARIANT, new ResourceLocation(var0));
   }
}
