package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PlayerTrigger extends SimpleCriterionTrigger<PlayerTrigger.TriggerInstance> {
   final ResourceLocation id;

   public PlayerTrigger(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   @Override
   public ResourceLocation getId() {
      return this.id;
   }

   public PlayerTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      return new PlayerTrigger.TriggerInstance(this.id, var2);
   }

   public void trigger(ServerPlayer var1) {
      this.trigger(var1, var0 -> true);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      public TriggerInstance(ResourceLocation var1, ContextAwarePredicate var2) {
         super(var1, var2);
      }

      public static PlayerTrigger.TriggerInstance located(LocationPredicate var0) {
         return new PlayerTrigger.TriggerInstance(CriteriaTriggers.LOCATION.id, EntityPredicate.wrap(EntityPredicate.Builder.entity().located(var0).build()));
      }

      public static PlayerTrigger.TriggerInstance located(EntityPredicate var0) {
         return new PlayerTrigger.TriggerInstance(CriteriaTriggers.LOCATION.id, EntityPredicate.wrap(var0));
      }

      public static PlayerTrigger.TriggerInstance sleptInBed() {
         return new PlayerTrigger.TriggerInstance(CriteriaTriggers.SLEPT_IN_BED.id, ContextAwarePredicate.ANY);
      }

      public static PlayerTrigger.TriggerInstance raidWon() {
         return new PlayerTrigger.TriggerInstance(CriteriaTriggers.RAID_WIN.id, ContextAwarePredicate.ANY);
      }

      public static PlayerTrigger.TriggerInstance avoidVibration() {
         return new PlayerTrigger.TriggerInstance(CriteriaTriggers.AVOID_VIBRATION.id, ContextAwarePredicate.ANY);
      }

      public static PlayerTrigger.TriggerInstance tick() {
         return new PlayerTrigger.TriggerInstance(CriteriaTriggers.TICK.id, ContextAwarePredicate.ANY);
      }

      public static PlayerTrigger.TriggerInstance walkOnBlockWithEquipment(Block var0, Item var1) {
         return located(
            EntityPredicate.Builder.entity()
               .equipment(EntityEquipmentPredicate.Builder.equipment().feet(ItemPredicate.Builder.item().of(var1).build()).build())
               .steppingOn(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var0).build()).build())
               .build()
         );
      }
   }
}
