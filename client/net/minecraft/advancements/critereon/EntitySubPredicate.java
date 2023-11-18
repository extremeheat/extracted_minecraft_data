package net.minecraft.advancements.critereon;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.TropicalFish;
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

public interface EntitySubPredicate {
   Codec<EntitySubPredicate> CODEC = EntitySubPredicate.Types.TYPE_CODEC.dispatch(EntitySubPredicate::type, var0 -> var0.codec().codec());

   boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3);

   EntitySubPredicate.Type type();

   static EntitySubPredicate variant(CatVariant var0) {
      return EntitySubPredicate.Types.CAT.createPredicate(var0);
   }

   static EntitySubPredicate variant(FrogVariant var0) {
      return EntitySubPredicate.Types.FROG.createPredicate(var0);
   }

   public static record Type(MapCodec<? extends EntitySubPredicate> a) {
      private final MapCodec<? extends EntitySubPredicate> codec;

      public Type(MapCodec<? extends EntitySubPredicate> var1) {
         super();
         this.codec = var1;
      }
   }

   public static final class Types {
      public static final EntitySubPredicate.Type ANY = new EntitySubPredicate.Type(MapCodec.unit(new EntitySubPredicate() {
         @Override
         public boolean matches(Entity var1, ServerLevel var2, @Nullable Vec3 var3) {
            return true;
         }

         @Override
         public EntitySubPredicate.Type type() {
            return EntitySubPredicate.Types.ANY;
         }
      }));
      public static final EntitySubPredicate.Type LIGHTNING = new EntitySubPredicate.Type(LightningBoltPredicate.CODEC);
      public static final EntitySubPredicate.Type FISHING_HOOK = new EntitySubPredicate.Type(FishingHookPredicate.CODEC);
      public static final EntitySubPredicate.Type PLAYER = new EntitySubPredicate.Type(PlayerPredicate.CODEC);
      public static final EntitySubPredicate.Type SLIME = new EntitySubPredicate.Type(SlimePredicate.CODEC);
      public static final EntityVariantPredicate<CatVariant> CAT = EntityVariantPredicate.create(
         BuiltInRegistries.CAT_VARIANT, var0 -> var0 instanceof Cat var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final EntityVariantPredicate<FrogVariant> FROG = EntityVariantPredicate.create(
         BuiltInRegistries.FROG_VARIANT, var0 -> var0 instanceof Frog var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final EntityVariantPredicate<Axolotl.Variant> AXOLOTL = EntityVariantPredicate.create(
         Axolotl.Variant.CODEC, var0 -> var0 instanceof Axolotl var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final EntityVariantPredicate<Boat.Type> BOAT = EntityVariantPredicate.create(
         Boat.Type.CODEC, var0 -> var0 instanceof Boat var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final EntityVariantPredicate<Fox.Type> FOX = EntityVariantPredicate.create(
         Fox.Type.CODEC, var0 -> var0 instanceof Fox var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final EntityVariantPredicate<MushroomCow.MushroomType> MOOSHROOM = EntityVariantPredicate.create(
         MushroomCow.MushroomType.CODEC, var0 -> var0 instanceof MushroomCow var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final EntityVariantPredicate<Holder<PaintingVariant>> PAINTING = EntityVariantPredicate.create(
         BuiltInRegistries.PAINTING_VARIANT.holderByNameCodec(), var0 -> var0 instanceof Painting var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final EntityVariantPredicate<Rabbit.Variant> RABBIT = EntityVariantPredicate.create(
         Rabbit.Variant.CODEC, var0 -> var0 instanceof Rabbit var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final EntityVariantPredicate<Variant> HORSE = EntityVariantPredicate.create(
         Variant.CODEC, var0 -> var0 instanceof Horse var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final EntityVariantPredicate<Llama.Variant> LLAMA = EntityVariantPredicate.create(
         Llama.Variant.CODEC, var0 -> var0 instanceof Llama var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final EntityVariantPredicate<VillagerType> VILLAGER = EntityVariantPredicate.create(
         BuiltInRegistries.VILLAGER_TYPE.byNameCodec(), var0 -> var0 instanceof VillagerDataHolder var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final EntityVariantPredicate<Parrot.Variant> PARROT = EntityVariantPredicate.create(
         Parrot.Variant.CODEC, var0 -> var0 instanceof Parrot var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final EntityVariantPredicate<TropicalFish.Pattern> TROPICAL_FISH = EntityVariantPredicate.create(
         TropicalFish.Pattern.CODEC, var0 -> var0 instanceof TropicalFish var1 ? Optional.of(var1.getVariant()) : Optional.empty()
      );
      public static final BiMap<String, EntitySubPredicate.Type> TYPES = ImmutableBiMap.builder()
         .put("any", ANY)
         .put("lightning", LIGHTNING)
         .put("fishing_hook", FISHING_HOOK)
         .put("player", PLAYER)
         .put("slime", SLIME)
         .put("cat", CAT.type())
         .put("frog", FROG.type())
         .put("axolotl", AXOLOTL.type())
         .put("boat", BOAT.type())
         .put("fox", FOX.type())
         .put("mooshroom", MOOSHROOM.type())
         .put("painting", PAINTING.type())
         .put("rabbit", RABBIT.type())
         .put("horse", HORSE.type())
         .put("llama", LLAMA.type())
         .put("villager", VILLAGER.type())
         .put("parrot", PARROT.type())
         .put("tropical_fish", TROPICAL_FISH.type())
         .buildOrThrow();
      public static final Codec<EntitySubPredicate.Type> TYPE_CODEC = ExtraCodecs.stringResolverCodec(TYPES.inverse()::get, TYPES::get);

      public Types() {
         super();
      }
   }
}
