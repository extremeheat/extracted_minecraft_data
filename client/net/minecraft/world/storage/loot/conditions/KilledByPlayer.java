package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class KilledByPlayer implements LootCondition {
   private final boolean field_186620_a;

   public KilledByPlayer(boolean var1) {
      super();
      this.field_186620_a = var1;
   }

   public boolean func_186618_a(Random var1, LootContext var2) {
      boolean var3 = var2.func_186495_b() != null;
      return var3 == !this.field_186620_a;
   }

   public static class Serializer extends LootCondition.Serializer<KilledByPlayer> {
      protected Serializer() {
         super(new ResourceLocation("killed_by_player"), KilledByPlayer.class);
      }

      public void func_186605_a(JsonObject var1, KilledByPlayer var2, JsonSerializationContext var3) {
         var1.addProperty("inverse", var2.field_186620_a);
      }

      public KilledByPlayer func_186603_b(JsonObject var1, JsonDeserializationContext var2) {
         return new KilledByPlayer(JsonUtils.func_151209_a(var1, "inverse", false));
      }

      // $FF: synthetic method
      public LootCondition func_186603_b(JsonObject var1, JsonDeserializationContext var2) {
         return this.func_186603_b(var1, var2);
      }
   }
}
