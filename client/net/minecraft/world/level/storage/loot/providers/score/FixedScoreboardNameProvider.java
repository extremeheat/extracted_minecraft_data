package net.minecraft.world.level.storage.loot.providers.score;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class FixedScoreboardNameProvider implements ScoreboardNameProvider {
   final String name;

   FixedScoreboardNameProvider(String var1) {
      super();
      this.name = var1;
   }

   public static ScoreboardNameProvider forName(String var0) {
      return new FixedScoreboardNameProvider(var0);
   }

   @Override
   public LootScoreProviderType getType() {
      return ScoreboardNameProviders.FIXED;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   @Override
   public String getScoreboardName(LootContext var1) {
      return this.name;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of();
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<FixedScoreboardNameProvider> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, FixedScoreboardNameProvider var2, JsonSerializationContext var3) {
         var1.addProperty("name", var2.name);
      }

      public FixedScoreboardNameProvider deserialize(JsonObject var1, JsonDeserializationContext var2) {
         String var3 = GsonHelper.getAsString(var1, "name");
         return new FixedScoreboardNameProvider(var3);
      }
   }
}
