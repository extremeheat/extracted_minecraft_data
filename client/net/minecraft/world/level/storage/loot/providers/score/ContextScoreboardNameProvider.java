package net.minecraft.world.level.storage.loot.providers.score;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class ContextScoreboardNameProvider implements ScoreboardNameProvider {
   final LootContext.EntityTarget target;

   ContextScoreboardNameProvider(LootContext.EntityTarget var1) {
      super();
      this.target = var1;
   }

   public static ScoreboardNameProvider forTarget(LootContext.EntityTarget var0) {
      return new ContextScoreboardNameProvider(var0);
   }

   public LootScoreProviderType getType() {
      return ScoreboardNameProviders.CONTEXT;
   }

   @Nullable
   public String getScoreboardName(LootContext var1) {
      Entity var2 = (Entity)var1.getParamOrNull(this.target.getParam());
      return var2 != null ? var2.getScoreboardName() : null;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(this.target.getParam());
   }

   public static class InlineSerializer implements GsonAdapterFactory.InlineSerializer<ContextScoreboardNameProvider> {
      public InlineSerializer() {
         super();
      }

      public JsonElement serialize(ContextScoreboardNameProvider var1, JsonSerializationContext var2) {
         return var2.serialize(var1.target);
      }

      public ContextScoreboardNameProvider deserialize(JsonElement var1, JsonDeserializationContext var2) {
         LootContext.EntityTarget var3 = (LootContext.EntityTarget)var2.deserialize(var1, LootContext.EntityTarget.class);
         return new ContextScoreboardNameProvider(var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ContextScoreboardNameProvider> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, ContextScoreboardNameProvider var2, JsonSerializationContext var3) {
         var1.addProperty("target", var2.target.name());
      }

      public ContextScoreboardNameProvider deserialize(JsonObject var1, JsonDeserializationContext var2) {
         LootContext.EntityTarget var3 = (LootContext.EntityTarget)GsonHelper.getAsObject(var1, "target", var2, LootContext.EntityTarget.class);
         return new ContextScoreboardNameProvider(var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
