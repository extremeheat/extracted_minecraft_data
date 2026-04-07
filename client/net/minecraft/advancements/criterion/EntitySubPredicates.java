package net.minecraft.advancements.criterion;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class EntitySubPredicates {
   public static final MapCodec<LightningBoltPredicate> LIGHTNING;
   public static final MapCodec<FishingHookPredicate> FISHING_HOOK;
   public static final MapCodec<PlayerPredicate> PLAYER;
   public static final MapCodec<SlimePredicate> SLIME;
   public static final MapCodec<RaiderPredicate> RAIDER;
   public static final MapCodec<SheepPredicate> SHEEP;

   public EntitySubPredicates() {
      super();
   }

   private static <T extends EntitySubPredicate> MapCodec<T> register(final String id, final MapCodec<T> value) {
      return (MapCodec)Registry.register(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, (String)id, value);
   }

   public static MapCodec<? extends EntitySubPredicate> bootstrap(final Registry<MapCodec<? extends EntitySubPredicate>> registry) {
      return LIGHTNING;
   }

   static {
      LIGHTNING = register("lightning", LightningBoltPredicate.CODEC);
      FISHING_HOOK = register("fishing_hook", FishingHookPredicate.CODEC);
      PLAYER = register("player", PlayerPredicate.CODEC);
      SLIME = register("slime", SlimePredicate.CODEC);
      RAIDER = register("raider", RaiderPredicate.CODEC);
      SHEEP = register("sheep", SheepPredicate.CODEC);
   }
}
