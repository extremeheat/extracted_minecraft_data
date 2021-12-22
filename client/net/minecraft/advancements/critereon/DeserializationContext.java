package net.minecraft.advancements.critereon;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeserializationContext {
   private static final Logger LOGGER = LogManager.getLogger();
   // $FF: renamed from: id net.minecraft.resources.ResourceLocation
   private final ResourceLocation field_349;
   private final PredicateManager predicateManager;
   private final Gson predicateGson = Deserializers.createConditionSerializer().create();

   public DeserializationContext(ResourceLocation var1, PredicateManager var2) {
      super();
      this.field_349 = var1;
      this.predicateManager = var2;
   }

   public final LootItemCondition[] deserializeConditions(JsonArray var1, String var2, LootContextParamSet var3) {
      LootItemCondition[] var4 = (LootItemCondition[])this.predicateGson.fromJson(var1, LootItemCondition[].class);
      PredicateManager var10003 = this.predicateManager;
      Objects.requireNonNull(var10003);
      ValidationContext var5 = new ValidationContext(var3, var10003::get, (var0) -> {
         return null;
      });
      LootItemCondition[] var6 = var4;
      int var7 = var4.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         LootItemCondition var9 = var6[var8];
         var9.validate(var5);
         var5.getProblems().forEach((var1x, var2x) -> {
            LOGGER.warn("Found validation problem in advancement trigger {}/{}: {}", var2, var1x, var2x);
         });
      }

      return var4;
   }

   public ResourceLocation getAdvancementId() {
      return this.field_349;
   }
}
