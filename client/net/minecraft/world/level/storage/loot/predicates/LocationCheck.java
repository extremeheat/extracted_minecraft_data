package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LocationCheck implements LootItemCondition {
   private final LocationPredicate predicate;

   private LocationCheck(LocationPredicate var1) {
      super();
      this.predicate = var1;
   }

   public boolean test(LootContext var1) {
      BlockPos var2 = (BlockPos)var1.getParamOrNull(LootContextParams.BLOCK_POS);
      return var2 != null && this.predicate.matches(var1.getLevel(), (float)var2.getX(), (float)var2.getY(), (float)var2.getZ());
   }

   public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder var0) {
      return () -> {
         return new LocationCheck(var0.build());
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   // $FF: synthetic method
   LocationCheck(LocationPredicate var1, Object var2) {
      this(var1);
   }

   public static class Serializer extends LootItemCondition.Serializer<LocationCheck> {
      public Serializer() {
         super(new ResourceLocation("location_check"), LocationCheck.class);
      }

      public void serialize(JsonObject var1, LocationCheck var2, JsonSerializationContext var3) {
         var1.add("predicate", var2.predicate.serializeToJson());
      }

      public LocationCheck deserialize(JsonObject var1, JsonDeserializationContext var2) {
         LocationPredicate var3 = LocationPredicate.fromJson(var1.get("predicate"));
         return new LocationCheck(var3);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
