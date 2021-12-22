package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LocationCheck implements LootItemCondition {
   final LocationPredicate predicate;
   final BlockPos offset;

   LocationCheck(LocationPredicate var1, BlockPos var2) {
      super();
      this.predicate = var1;
      this.offset = var2;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.LOCATION_CHECK;
   }

   public boolean test(LootContext var1) {
      Vec3 var2 = (Vec3)var1.getParamOrNull(LootContextParams.ORIGIN);
      return var2 != null && this.predicate.matches(var1.getLevel(), var2.method_2() + (double)this.offset.getX(), var2.method_3() + (double)this.offset.getY(), var2.method_4() + (double)this.offset.getZ());
   }

   public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder var0) {
      return () -> {
         return new LocationCheck(var0.build(), BlockPos.ZERO);
      };
   }

   public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder var0, BlockPos var1) {
      return () -> {
         return new LocationCheck(var0.build(), var1);
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LocationCheck> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, LocationCheck var2, JsonSerializationContext var3) {
         var1.add("predicate", var2.predicate.serializeToJson());
         if (var2.offset.getX() != 0) {
            var1.addProperty("offsetX", var2.offset.getX());
         }

         if (var2.offset.getY() != 0) {
            var1.addProperty("offsetY", var2.offset.getY());
         }

         if (var2.offset.getZ() != 0) {
            var1.addProperty("offsetZ", var2.offset.getZ());
         }

      }

      public LocationCheck deserialize(JsonObject var1, JsonDeserializationContext var2) {
         LocationPredicate var3 = LocationPredicate.fromJson(var1.get("predicate"));
         int var4 = GsonHelper.getAsInt(var1, "offsetX", 0);
         int var5 = GsonHelper.getAsInt(var1, "offsetY", 0);
         int var6 = GsonHelper.getAsInt(var1, "offsetZ", 0);
         return new LocationCheck(var3, new BlockPos(var4, var5, var6));
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
