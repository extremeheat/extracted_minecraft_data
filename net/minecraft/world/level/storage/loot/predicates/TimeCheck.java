package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomValueBounds;

public class TimeCheck implements LootItemCondition {
   @Nullable
   private final Long period;
   private final RandomValueBounds value;

   private TimeCheck(@Nullable Long var1, RandomValueBounds var2) {
      this.period = var1;
      this.value = var2;
   }

   public boolean test(LootContext var1) {
      ServerLevel var2 = var1.getLevel();
      long var3 = var2.getDayTime();
      if (this.period != null) {
         var3 %= this.period;
      }

      return this.value.matchesValue((int)var3);
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   // $FF: synthetic method
   TimeCheck(Long var1, RandomValueBounds var2, Object var3) {
      this(var1, var2);
   }

   public static class Serializer extends LootItemCondition.Serializer {
      public Serializer() {
         super(new ResourceLocation("time_check"), TimeCheck.class);
      }

      public void serialize(JsonObject var1, TimeCheck var2, JsonSerializationContext var3) {
         var1.addProperty("period", var2.period);
         var1.add("value", var3.serialize(var2.value));
      }

      public TimeCheck deserialize(JsonObject var1, JsonDeserializationContext var2) {
         Long var3 = var1.has("period") ? GsonHelper.getAsLong(var1, "period") : null;
         RandomValueBounds var4 = (RandomValueBounds)GsonHelper.getAsObject(var1, "value", var2, RandomValueBounds.class);
         return new TimeCheck(var3, var4);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
