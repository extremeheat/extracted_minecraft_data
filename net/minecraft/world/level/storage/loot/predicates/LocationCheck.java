package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LocationCheck implements LootItemCondition {
   private final LocationPredicate predicate;
   private final BlockPos offset;

   public LocationCheck(LocationPredicate var1, BlockPos var2) {
      this.predicate = var1;
      this.offset = var2;
   }

   public boolean test(LootContext var1) {
      BlockPos var2 = (BlockPos)var1.getParamOrNull(LootContextParams.BLOCK_POS);
      return var2 != null && this.predicate.matches(var1.getLevel(), (float)(var2.getX() + this.offset.getX()), (float)(var2.getY() + this.offset.getY()), (float)(var2.getZ() + this.offset.getZ()));
   }

   public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder var0) {
      return () -> {
         return new LocationCheck(var0.build(), BlockPos.ZERO);
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      public Serializer() {
         super(new ResourceLocation("location_check"), LocationCheck.class);
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
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
