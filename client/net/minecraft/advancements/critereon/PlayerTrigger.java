package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PlayerTrigger extends SimpleCriterionTrigger<PlayerTrigger.TriggerInstance> {
   public PlayerTrigger() {
      super();
   }

   @Override
   public Codec<PlayerTrigger.TriggerInstance> codec() {
      return PlayerTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1) {
      this.trigger(var1, var0 -> true);
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b) implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      public static final Codec<PlayerTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(PlayerTrigger.TriggerInstance::player))
               .apply(var0, PlayerTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1) {
         super();
         this.player = var1;
      }

      public static Criterion<PlayerTrigger.TriggerInstance> located(LocationPredicate.Builder var0) {
         return CriteriaTriggers.LOCATION
            .createCriterion(new PlayerTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().located(var0)))));
      }

      public static Criterion<PlayerTrigger.TriggerInstance> located(EntityPredicate.Builder var0) {
         return CriteriaTriggers.LOCATION.createCriterion(new PlayerTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(var0.build()))));
      }

      public static Criterion<PlayerTrigger.TriggerInstance> located(Optional<EntityPredicate> var0) {
         return CriteriaTriggers.LOCATION.createCriterion(new PlayerTrigger.TriggerInstance(EntityPredicate.wrap(var0)));
      }

      public static Criterion<PlayerTrigger.TriggerInstance> sleptInBed() {
         return CriteriaTriggers.SLEPT_IN_BED.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<PlayerTrigger.TriggerInstance> raidWon() {
         return CriteriaTriggers.RAID_WIN.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<PlayerTrigger.TriggerInstance> avoidVibration() {
         return CriteriaTriggers.AVOID_VIBRATION.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<PlayerTrigger.TriggerInstance> tick() {
         return CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<PlayerTrigger.TriggerInstance> walkOnBlockWithEquipment(Block var0, Item var1) {
         return located(
            EntityPredicate.Builder.entity()
               .equipment(EntityEquipmentPredicate.Builder.equipment().feet(ItemPredicate.Builder.item().of(var1)))
               .steppingOn(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var0)))
         );
      }
   }
}
