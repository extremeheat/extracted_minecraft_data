package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

public class EntitySubPredicates {
   public static final MapCodec<LightningBoltPredicate> LIGHTNING;
   public static final MapCodec<FishingHookPredicate> FISHING_HOOK;
   public static final MapCodec<PlayerPredicate> PLAYER;
   public static final MapCodec<SlimePredicate> SLIME;
   public static final MapCodec<RaiderPredicate> RAIDER;
   public static final EntityVariantPredicateType<Axolotl.Variant> AXOLOTL;
   public static final EntityVariantPredicateType<Boat.Type> BOAT;
   public static final EntityVariantPredicateType<Fox.Type> FOX;
   public static final EntityVariantPredicateType<MushroomCow.MushroomType> MOOSHROOM;
   public static final EntityVariantPredicateType<Rabbit.Variant> RABBIT;
   public static final EntityVariantPredicateType<Variant> HORSE;
   public static final EntityVariantPredicateType<Llama.Variant> LLAMA;
   public static final EntityVariantPredicateType<VillagerType> VILLAGER;
   public static final EntityVariantPredicateType<Parrot.Variant> PARROT;
   public static final EntityVariantPredicateType<TropicalFish.Pattern> TROPICAL_FISH;
   public static final EntityHolderVariantPredicateType<PaintingVariant> PAINTING;
   public static final EntityHolderVariantPredicateType<CatVariant> CAT;
   public static final EntityHolderVariantPredicateType<FrogVariant> FROG;
   public static final EntityHolderVariantPredicateType<WolfVariant> WOLF;

   public EntitySubPredicates() {
      super();
   }

   private static <T extends EntitySubPredicate> MapCodec<T> register(String var0, MapCodec<T> var1) {
      return (MapCodec)Registry.register(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, (String)var0, var1);
   }

   private static <V> EntityVariantPredicateType<V> register(String var0, EntityVariantPredicateType<V> var1) {
      Registry.register(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, (String)var0, var1.codec);
      return var1;
   }

   private static <V> EntityHolderVariantPredicateType<V> register(String var0, EntityHolderVariantPredicateType<V> var1) {
      Registry.register(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, (String)var0, var1.codec);
      return var1;
   }

   public static MapCodec<? extends EntitySubPredicate> bootstrap(Registry<MapCodec<? extends EntitySubPredicate>> var0) {
      return LIGHTNING;
   }

   public static EntitySubPredicate catVariant(Holder<CatVariant> var0) {
      return CAT.createPredicate(HolderSet.direct(var0));
   }

   public static EntitySubPredicate frogVariant(Holder<FrogVariant> var0) {
      return FROG.createPredicate(HolderSet.direct(var0));
   }

   static {
      LIGHTNING = register("lightning", LightningBoltPredicate.CODEC);
      FISHING_HOOK = register("fishing_hook", FishingHookPredicate.CODEC);
      PLAYER = register("player", PlayerPredicate.CODEC);
      SLIME = register("slime", SlimePredicate.CODEC);
      RAIDER = register("raider", RaiderPredicate.CODEC);
      AXOLOTL = register("axolotl", EntitySubPredicates.EntityVariantPredicateType.create(Axolotl.Variant.CODEC, (var0) -> {
         Optional var10000;
         if (var0 instanceof Axolotl var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
      BOAT = register("boat", EntitySubPredicates.EntityVariantPredicateType.create((Codec)Boat.Type.CODEC, (var0) -> {
         Optional var10000;
         if (var0 instanceof Boat var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
      FOX = register("fox", EntitySubPredicates.EntityVariantPredicateType.create((Codec)Fox.Type.CODEC, (var0) -> {
         Optional var10000;
         if (var0 instanceof Fox var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
      MOOSHROOM = register("mooshroom", EntitySubPredicates.EntityVariantPredicateType.create((Codec)MushroomCow.MushroomType.CODEC, (var0) -> {
         Optional var10000;
         if (var0 instanceof MushroomCow var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
      RABBIT = register("rabbit", EntitySubPredicates.EntityVariantPredicateType.create(Rabbit.Variant.CODEC, (var0) -> {
         Optional var10000;
         if (var0 instanceof Rabbit var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
      HORSE = register("horse", EntitySubPredicates.EntityVariantPredicateType.create(Variant.CODEC, (var0) -> {
         Optional var10000;
         if (var0 instanceof Horse var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
      LLAMA = register("llama", EntitySubPredicates.EntityVariantPredicateType.create(Llama.Variant.CODEC, (var0) -> {
         Optional var10000;
         if (var0 instanceof Llama var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
      VILLAGER = register("villager", EntitySubPredicates.EntityVariantPredicateType.create(BuiltInRegistries.VILLAGER_TYPE.byNameCodec(), (var0) -> {
         Optional var10000;
         if (var0 instanceof VillagerDataHolder var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
      PARROT = register("parrot", EntitySubPredicates.EntityVariantPredicateType.create(Parrot.Variant.CODEC, (var0) -> {
         Optional var10000;
         if (var0 instanceof Parrot var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
      TROPICAL_FISH = register("tropical_fish", EntitySubPredicates.EntityVariantPredicateType.create(TropicalFish.Pattern.CODEC, (var0) -> {
         Optional var10000;
         if (var0 instanceof TropicalFish var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
      PAINTING = register("painting", EntitySubPredicates.EntityHolderVariantPredicateType.create(Registries.PAINTING_VARIANT, (var0) -> {
         Optional var10000;
         if (var0 instanceof Painting var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
      CAT = register("cat", EntitySubPredicates.EntityHolderVariantPredicateType.create(Registries.CAT_VARIANT, (var0) -> {
         Optional var10000;
         if (var0 instanceof Cat var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
      FROG = register("frog", EntitySubPredicates.EntityHolderVariantPredicateType.create(Registries.FROG_VARIANT, (var0) -> {
         Optional var10000;
         if (var0 instanceof Frog var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
      WOLF = register("wolf", EntitySubPredicates.EntityHolderVariantPredicateType.create(Registries.WOLF_VARIANT, (var0) -> {
         Optional var10000;
         if (var0 instanceof Wolf var1) {
            var10000 = Optional.of(var1.getVariant());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }));
   }

   public static class EntityVariantPredicateType<V> {
      final MapCodec<EntityVariantPredicateType<V>.Instance> codec;
      final Function<Entity, Optional<V>> getter;

      public static <V> EntityVariantPredicateType<V> create(Registry<V> var0, Function<Entity, Optional<V>> var1) {
         return new EntityVariantPredicateType(var0.byNameCodec(), var1);
      }

      public static <V> EntityVariantPredicateType<V> create(Codec<V> var0, Function<Entity, Optional<V>> var1) {
         return new EntityVariantPredicateType(var0, var1);
      }

      public EntityVariantPredicateType(Codec<V> var1, Function<Entity, Optional<V>> var2) {
         super();
         this.getter = var2;
         this.codec = RecordCodecBuilder.mapCodec((var2x) -> {
            return var2x.group(var1.fieldOf("variant").forGetter((var0) -> {
               return var0.variant;
            })).apply(var2x, (var1x) -> {
               return new Instance(var1x);
            });
         });
      }

      public EntitySubPredicate createPredicate(V var1) {
         return new Instance(var1);
      }

      private class Instance implements EntitySubPredicate {
         final V variant;

         Instance(V var2) {
            super();
            this.variant = var2;
         }

         public MapCodec<EntityVariantPredicateType<V>.Instance> codec() {
            return EntityVariantPredicateType.this.codec;
         }

         public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
            Optional var10000 = (Optional)EntityVariantPredicateType.this.getter.apply(var1);
            Object var10001 = this.variant;
            Objects.requireNonNull(var10001);
            return var10000.filter(var10001::equals).isPresent();
         }
      }
   }

   public static class EntityHolderVariantPredicateType<V> {
      final MapCodec<EntityHolderVariantPredicateType<V>.Instance> codec;
      final Function<Entity, Optional<Holder<V>>> getter;

      public static <V> EntityHolderVariantPredicateType<V> create(ResourceKey<? extends Registry<V>> var0, Function<Entity, Optional<Holder<V>>> var1) {
         return new EntityHolderVariantPredicateType(var0, var1);
      }

      public EntityHolderVariantPredicateType(ResourceKey<? extends Registry<V>> var1, Function<Entity, Optional<Holder<V>>> var2) {
         super();
         this.getter = var2;
         this.codec = RecordCodecBuilder.mapCodec((var2x) -> {
            return var2x.group(RegistryCodecs.homogeneousList(var1).fieldOf("variant").forGetter((var0) -> {
               return var0.variants;
            })).apply(var2x, (var1x) -> {
               return new Instance(var1x);
            });
         });
      }

      public EntitySubPredicate createPredicate(HolderSet<V> var1) {
         return new Instance(var1);
      }

      private class Instance implements EntitySubPredicate {
         final HolderSet<V> variants;

         Instance(HolderSet<V> var2) {
            super();
            this.variants = var2;
         }

         public MapCodec<EntityHolderVariantPredicateType<V>.Instance> codec() {
            return EntityHolderVariantPredicateType.this.codec;
         }

         public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
            Optional var10000 = (Optional)EntityHolderVariantPredicateType.this.getter.apply(var1);
            HolderSet var10001 = this.variants;
            Objects.requireNonNull(var10001);
            return var10000.filter(var10001::contains).isPresent();
         }
      }
   }
}
