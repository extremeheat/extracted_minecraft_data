package net.minecraft.world.level.storage.loot.providers.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.score.ContextScoreboardNameProvider;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider;
import net.minecraft.world.scores.Objective;

public class ScoreboardValue implements NumberProvider {
   final ScoreboardNameProvider target;
   final String score;
   final float scale;

   ScoreboardValue(ScoreboardNameProvider var1, String var2, float var3) {
      super();
      this.target = var1;
      this.score = var2;
      this.scale = var3;
   }

   public LootNumberProviderType getType() {
      return NumberProviders.SCORE;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.target.getReferencedContextParams();
   }

   public static ScoreboardValue fromScoreboard(LootContext.EntityTarget var0, String var1) {
      return fromScoreboard(var0, var1, 1.0F);
   }

   public static ScoreboardValue fromScoreboard(LootContext.EntityTarget var0, String var1, float var2) {
      return new ScoreboardValue(ContextScoreboardNameProvider.forTarget(var0), var1, var2);
   }

   public float getFloat(LootContext var1) {
      String var2 = this.target.getScoreboardName(var1);
      if (var2 == null) {
         return 0.0F;
      } else {
         ServerScoreboard var3 = var1.getLevel().getScoreboard();
         Objective var4 = var3.getObjective(this.score);
         if (var4 == null) {
            return 0.0F;
         } else {
            return !var3.hasPlayerScore(var2, var4) ? 0.0F : (float)var3.getOrCreatePlayerScore(var2, var4).getScore() * this.scale;
         }
      }
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ScoreboardValue> {
      public Serializer() {
         super();
      }

      public ScoreboardValue deserialize(JsonObject var1, JsonDeserializationContext var2) {
         String var3 = GsonHelper.getAsString(var1, "score");
         float var4 = GsonHelper.getAsFloat(var1, "scale", 1.0F);
         ScoreboardNameProvider var5 = (ScoreboardNameProvider)GsonHelper.getAsObject(var1, "target", var2, ScoreboardNameProvider.class);
         return new ScoreboardValue(var5, var3, var4);
      }

      public void serialize(JsonObject var1, ScoreboardValue var2, JsonSerializationContext var3) {
         var1.addProperty("score", var2.score);
         var1.add("target", var3.serialize(var2.target));
         var1.addProperty("scale", var2.scale);
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
