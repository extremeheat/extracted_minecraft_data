package net.minecraft.advancements.predicates.entity;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public class EntitySubPredicates {
   public EntitySubPredicates() {
      super();
   }

   public static Codec<? extends EntitySubPredicate> bootstrap(final Registry<Codec<? extends EntitySubPredicate>> registry) {
      Registry.register(registry, (String)"entity_type", EntityTypePredicate.CODEC);
      Registry.register(registry, (String)"location", EntityLocationPredicate.CODEC);
      Registry.register(registry, (String)"stepping_on", SteppingOnPredicate.CODEC);
      Registry.register(registry, (String)"movement_affected_by", MovementAffectedByPredicate.CODEC);
      Registry.register(registry, (String)"distance", DistanceToPlayerPredicate.CODEC);
      Registry.register(registry, (String)"movement", MovementPredicate.CODEC);
      Registry.register(registry, (String)"effects", EntityEffectsPredicate.CODEC);
      Registry.register(registry, (String)"nbt", EntityNbtPredicate.CODEC);
      Registry.register(registry, (String)"flags", EntityFlagsPredicate.CODEC);
      Registry.register(registry, (String)"equipment", EntityEquipmentPredicate.CODEC);
      Registry.register(registry, (String)"periodic_tick", PeriodicEntityTickPredicate.CODEC);
      Registry.register(registry, (String)"vehicle", VehiclePredicate.CODEC);
      Registry.register(registry, (String)"passenger", PassengerPredicate.CODEC);
      Registry.register(registry, (String)"targeted_entity", TargetedEntityPredicate.CODEC);
      Registry.register(registry, (String)"team", TeamPredicate.CODEC);
      Registry.register(registry, (String)"slots", EntitySlotsPredicate.CODEC);
      Registry.register(registry, (String)"components", EntityExactDataComponentsPredicate.CODEC);
      Registry.register(registry, (String)"predicates", EntityPartialComponentsPredicate.CODEC);
      Registry.register(registry, (String)"entity_tags", EntityTagPredicate.CODEC);
      Registry.register(registry, (String)"type_specific/lightning", LightningBoltPredicate.CODEC);
      Registry.register(registry, (String)"type_specific/fishing_hook", FishingHookPredicate.CODEC);
      Registry.register(registry, (String)"type_specific/player", PlayerPredicate.CODEC);
      Registry.register(registry, (String)"type_specific/slime", CubeMobPredicate.CODEC);
      Registry.register(registry, (String)"type_specific/raider", RaiderPredicate.CODEC);
      return (Codec)Registry.register(registry, (String)"type_specific/sheep", SheepPredicate.CODEC);
   }
}
