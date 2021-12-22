package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PredicateManager extends SimpleJsonResourceReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = Deserializers.createConditionSerializer().create();
   private Map<ResourceLocation, LootItemCondition> conditions = ImmutableMap.of();

   public PredicateManager() {
      super(GSON, "predicates");
   }

   @Nullable
   public LootItemCondition get(ResourceLocation var1) {
      return (LootItemCondition)this.conditions.get(var1);
   }

   protected void apply(Map<ResourceLocation, JsonElement> var1, ResourceManager var2, ProfilerFiller var3) {
      Builder var4 = ImmutableMap.builder();
      var1.forEach((var1x, var2x) -> {
         try {
            if (var2x.isJsonArray()) {
               LootItemCondition[] var3 = (LootItemCondition[])GSON.fromJson(var2x, LootItemCondition[].class);
               var4.put(var1x, new PredicateManager.CompositePredicate(var3));
            } else {
               LootItemCondition var5 = (LootItemCondition)GSON.fromJson(var2x, LootItemCondition.class);
               var4.put(var1x, var5);
            }
         } catch (Exception var4x) {
            LOGGER.error("Couldn't parse loot table {}", var1x, var4x);
         }

      });
      ImmutableMap var5 = var4.build();
      LootContextParamSet var10002 = LootContextParamSets.ALL_PARAMS;
      Objects.requireNonNull(var5);
      ValidationContext var6 = new ValidationContext(var10002, var5::get, (var0) -> {
         return null;
      });
      var5.forEach((var1x, var2x) -> {
         var2x.validate(var6.enterCondition("{" + var1x + "}", var1x));
      });
      var6.getProblems().forEach((var0, var1x) -> {
         LOGGER.warn("Found validation problem in {}: {}", var0, var1x);
      });
      this.conditions = var5;
   }

   public Set<ResourceLocation> getKeys() {
      return Collections.unmodifiableSet(this.conditions.keySet());
   }

   private static class CompositePredicate implements LootItemCondition {
      private final LootItemCondition[] terms;
      private final Predicate<LootContext> composedPredicate;

      CompositePredicate(LootItemCondition[] var1) {
         super();
         this.terms = var1;
         this.composedPredicate = LootItemConditions.andConditions(var1);
      }

      public final boolean test(LootContext var1) {
         return this.composedPredicate.test(var1);
      }

      public void validate(ValidationContext var1) {
         LootItemCondition.super.validate(var1);

         for(int var2 = 0; var2 < this.terms.length; ++var2) {
            this.terms[var2].validate(var1.forChild(".term[" + var2 + "]"));
         }

      }

      public LootItemConditionType getType() {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((LootContext)var1);
      }
   }
}
