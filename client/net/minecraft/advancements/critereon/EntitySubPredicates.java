package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
   public static final MapCodec<LightningBoltPredicate> LIGHTNING = register("lightning", LightningBoltPredicate.CODEC);
   public static final MapCodec<FishingHookPredicate> FISHING_HOOK = register("fishing_hook", FishingHookPredicate.CODEC);
   public static final MapCodec<PlayerPredicate> PLAYER = register("player", PlayerPredicate.CODEC);
   public static final MapCodec<SlimePredicate> SLIME = register("slime", SlimePredicate.CODEC);
   public static final MapCodec<RaiderPredicate> RAIDER = register("raider", RaiderPredicate.CODEC);
   public static final EntitySubPredicates.EntityVariantPredicateType<Axolotl.Variant> AXOLOTL = register(
      "axolotl",
      EntitySubPredicates.EntityVariantPredicateType.create(
         Axolotl.Variant.CODEC, var0 -> var0 instanceof Axolotl var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );
   public static final EntitySubPredicates.EntityVariantPredicateType<Boat.Type> BOAT = register(
      "boat",
      EntitySubPredicates.EntityVariantPredicateType.create(
         Boat.Type.CODEC, var0 -> var0 instanceof Boat var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );
   public static final EntitySubPredicates.EntityVariantPredicateType<Fox.Type> FOX = register(
      "fox",
      EntitySubPredicates.EntityVariantPredicateType.create(
         Fox.Type.CODEC, var0 -> var0 instanceof Fox var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );
   public static final EntitySubPredicates.EntityVariantPredicateType<MushroomCow.MushroomType> MOOSHROOM = register(
      "mooshroom",
      EntitySubPredicates.EntityVariantPredicateType.create(
         MushroomCow.MushroomType.CODEC, var0 -> var0 instanceof MushroomCow var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );
   public static final EntitySubPredicates.EntityVariantPredicateType<Rabbit.Variant> RABBIT = register(
      "rabbit",
      EntitySubPredicates.EntityVariantPredicateType.create(
         Rabbit.Variant.CODEC, var0 -> var0 instanceof Rabbit var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );
   public static final EntitySubPredicates.EntityVariantPredicateType<Variant> HORSE = register(
      "horse",
      EntitySubPredicates.EntityVariantPredicateType.create(
         Variant.CODEC, var0 -> var0 instanceof Horse var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );
   public static final EntitySubPredicates.EntityVariantPredicateType<Llama.Variant> LLAMA = register(
      "llama",
      EntitySubPredicates.EntityVariantPredicateType.create(
         Llama.Variant.CODEC, var0 -> var0 instanceof Llama var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );
   public static final EntitySubPredicates.EntityVariantPredicateType<VillagerType> VILLAGER = register(
      "villager",
      EntitySubPredicates.EntityVariantPredicateType.create(
         BuiltInRegistries.VILLAGER_TYPE.byNameCodec(), var0 -> var0 instanceof VillagerDataHolder var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );
   public static final EntitySubPredicates.EntityVariantPredicateType<Parrot.Variant> PARROT = register(
      "parrot",
      EntitySubPredicates.EntityVariantPredicateType.create(
         Parrot.Variant.CODEC, var0 -> var0 instanceof Parrot var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );
   public static final EntitySubPredicates.EntityVariantPredicateType<TropicalFish.Pattern> TROPICAL_FISH = register(
      "tropical_fish",
      EntitySubPredicates.EntityVariantPredicateType.create(
         TropicalFish.Pattern.CODEC, var0 -> var0 instanceof TropicalFish var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );
   public static final EntitySubPredicates.EntityHolderVariantPredicateType<PaintingVariant> PAINTING = register(
      "painting",
      EntitySubPredicates.EntityHolderVariantPredicateType.create(
         Registries.PAINTING_VARIANT, var0 -> var0 instanceof Painting var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );
   public static final EntitySubPredicates.EntityHolderVariantPredicateType<CatVariant> CAT = register(
      "cat",
      EntitySubPredicates.EntityHolderVariantPredicateType.create(
         Registries.CAT_VARIANT, var0 -> var0 instanceof Cat var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );
   public static final EntitySubPredicates.EntityHolderVariantPredicateType<FrogVariant> FROG = register(
      "frog",
      EntitySubPredicates.EntityHolderVariantPredicateType.create(
         Registries.FROG_VARIANT, var0 -> var0 instanceof Frog var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );
   public static final EntitySubPredicates.EntityHolderVariantPredicateType<WolfVariant> WOLF = register(
      "wolf",
      EntitySubPredicates.EntityHolderVariantPredicateType.create(
         Registries.WOLF_VARIANT, var0 -> var0 instanceof Wolf var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      )
   );

   public EntitySubPredicates() {
      super();
   }

   private static <T extends EntitySubPredicate> MapCodec<T> register(String var0, MapCodec<T> var1) {
      return Registry.register(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, var0, var1);
   }

   private static <V> EntitySubPredicates.EntityVariantPredicateType<V> register(String var0, EntitySubPredicates.EntityVariantPredicateType<V> var1) {
      Registry.register(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, var0, var1.codec);
      return var1;
   }

   private static <V> EntitySubPredicates.EntityHolderVariantPredicateType<V> register(
      String var0, EntitySubPredicates.EntityHolderVariantPredicateType<V> var1
   ) {
      Registry.register(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, var0, var1.codec);
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

   public static class EntityHolderVariantPredicateType<V> {
      final MapCodec<EntitySubPredicates.EntityHolderVariantPredicateType<V>.Instance> codec;
      final Function<Entity, Optional<Holder<V>>> getter;

      public static <V> EntitySubPredicates.EntityHolderVariantPredicateType<V> create(
         ResourceKey<? extends Registry<V>> var0, Function<Entity, Optional<Holder<V>>> var1
      ) {
         return new EntitySubPredicates.EntityHolderVariantPredicateType<>(var0, var1);
      }

      public EntityHolderVariantPredicateType(ResourceKey<? extends Registry<V>> var1, Function<Entity, Optional<Holder<V>>> var2) {
         super();
         this.getter = var2;
         this.codec = RecordCodecBuilder.mapCodec(
            var2x -> var2x.group(RegistryCodecs.homogeneousList(var1).fieldOf("variant").forGetter(var0 -> var0.variants))
                  .apply(var2x, var1xx -> new EntitySubPredicates.EntityHolderVariantPredicateType.Instance(var1xx))
         );
      }

      public EntitySubPredicate createPredicate(HolderSet<V> var1) {
         return new EntitySubPredicates.EntityHolderVariantPredicateType.Instance(var1);
      }

      class Instance implements EntitySubPredicate {
         final HolderSet<V> variants;

         Instance(HolderSet<V> var2) {
            super();
            this.variants = var2;
         }

         @Override
         public MapCodec<EntitySubPredicates.EntityHolderVariantPredicateType<V>.Instance> codec() {
            return EntityHolderVariantPredicateType.this.codec;
         }

         @Override
         public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
            return EntityHolderVariantPredicateType.this.getter.apply(var1).filter(this.variants::contains).isPresent();
         }
      }
   }

   public static class EntityVariantPredicateType<V> {
      final MapCodec<EntitySubPredicates.EntityVariantPredicateType<V>.Instance> codec;
      final Function<Entity, Optional<V>> getter;

      public static <V> EntitySubPredicates.EntityVariantPredicateType<V> create(Registry<V> var0, Function<Entity, Optional<V>> var1) {
         return new EntitySubPredicates.EntityVariantPredicateType<>(var0.byNameCodec(), var1);
      }

      public static <V> EntitySubPredicates.EntityVariantPredicateType<V> create(Codec<V> var0, Function<Entity, Optional<V>> var1) {
         return new EntitySubPredicates.EntityVariantPredicateType<>(var0, var1);
      }

      public EntityVariantPredicateType(Codec<V> var1, Function<Entity, Optional<V>> var2) {
         super();
         this.getter = var2;
         this.codec = RecordCodecBuilder.mapCodec(
            var2x -> var2x.group(var1.fieldOf("variant").forGetter(var0 -> var0.variant))
                  .apply(var2x, var1xx -> new EntitySubPredicates.EntityVariantPredicateType.Instance(var1xx))
         );
      }

      public EntitySubPredicate createPredicate(V var1) {
         return new EntitySubPredicates.EntityVariantPredicateType.Instance(var1);
      }

      class Instance implements EntitySubPredicate {
         final V variant;

         Instance(V var2) {
            super();
            this.variant = (V)var2;
         }

         @Override
         public MapCodec<EntitySubPredicates.EntityVariantPredicateType<V>.Instance> codec() {
            return EntityVariantPredicateType.this.codec;
         }

         @Override
         public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
            return EntityVariantPredicateType.this.getter.apply(var1).filter(this.variant::equals).isPresent();
         }
      }
   }
}
