package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
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

   public PlayerTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      return new PlayerTrigger.TriggerInstance(var2);
   }

   public void trigger(ServerPlayer var1) {
      this.trigger(var1, var0 -> true);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      public TriggerInstance(Optional<ContextAwarePredicate> var1) {
         super(var1);
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
