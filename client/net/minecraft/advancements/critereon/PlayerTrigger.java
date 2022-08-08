package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class PlayerTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   final ResourceLocation id;

   public PlayerTrigger(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return new TriggerInstance(this.id, var2);
   }

   public void trigger(ServerPlayer var1) {
      this.trigger(var1, (var0) -> {
         return true;
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      public TriggerInstance(ResourceLocation var1, EntityPredicate.Composite var2) {
         super(var1, var2);
      }

      public static TriggerInstance located(LocationPredicate var0) {
         return new TriggerInstance(CriteriaTriggers.LOCATION.id, EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().located(var0).build()));
      }

      public static TriggerInstance located(EntityPredicate var0) {
         return new TriggerInstance(CriteriaTriggers.LOCATION.id, EntityPredicate.Composite.wrap(var0));
      }

      public static TriggerInstance sleptInBed() {
         return new TriggerInstance(CriteriaTriggers.SLEPT_IN_BED.id, EntityPredicate.Composite.ANY);
      }

      public static TriggerInstance raidWon() {
         return new TriggerInstance(CriteriaTriggers.RAID_WIN.id, EntityPredicate.Composite.ANY);
      }

      public static TriggerInstance avoidVibration() {
         return new TriggerInstance(CriteriaTriggers.AVOID_VIBRATION.id, EntityPredicate.Composite.ANY);
      }

      public static TriggerInstance walkOnBlockWithEquipment(Block var0, Item var1) {
         return located(EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().feet(ItemPredicate.Builder.item().of(var1).build()).build()).steppingOn(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var0).build()).build()).build());
      }
   }
}
