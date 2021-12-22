package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class LocationTrigger extends SimpleCriterionTrigger<LocationTrigger.TriggerInstance> {
   // $FF: renamed from: id net.minecraft.resources.ResourceLocation
   final ResourceLocation field_86;

   public LocationTrigger(ResourceLocation var1) {
      super();
      this.field_86 = var1;
   }

   public ResourceLocation getId() {
      return this.field_86;
   }

   public LocationTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      JsonObject var4 = GsonHelper.getAsJsonObject(var1, "location", var1);
      LocationPredicate var5 = LocationPredicate.fromJson(var4);
      return new LocationTrigger.TriggerInstance(this.field_86, var2, var5);
   }

   public void trigger(ServerPlayer var1) {
      this.trigger(var1, (var1x) -> {
         return var1x.matches(var1.getLevel(), var1.getX(), var1.getY(), var1.getZ());
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final LocationPredicate location;

      public TriggerInstance(ResourceLocation var1, EntityPredicate.Composite var2, LocationPredicate var3) {
         super(var1, var2);
         this.location = var3;
      }

      public static LocationTrigger.TriggerInstance located(LocationPredicate var0) {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.LOCATION.field_86, EntityPredicate.Composite.ANY, var0);
      }

      public static LocationTrigger.TriggerInstance located(EntityPredicate var0) {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.LOCATION.field_86, EntityPredicate.Composite.wrap(var0), LocationPredicate.ANY);
      }

      public static LocationTrigger.TriggerInstance sleptInBed() {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.SLEPT_IN_BED.field_86, EntityPredicate.Composite.ANY, LocationPredicate.ANY);
      }

      public static LocationTrigger.TriggerInstance raidWon() {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.RAID_WIN.field_86, EntityPredicate.Composite.ANY, LocationPredicate.ANY);
      }

      public static LocationTrigger.TriggerInstance walkOnBlockWithEquipment(Block var0, Item var1) {
         return located(EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().feet(ItemPredicate.Builder.item().method_90(var1).build()).build()).steppingOn(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().method_119(var0).build()).build()).build());
      }

      public boolean matches(ServerLevel var1, double var2, double var4, double var6) {
         return this.location.matches(var1, var2, var4, var6);
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("location", this.location.serializeToJson());
         return var2;
      }
   }
}
