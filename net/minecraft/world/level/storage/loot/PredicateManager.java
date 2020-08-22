package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PredicateManager extends SimpleJsonResourceReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(RandomValueBounds.class, new RandomValueBounds.Serializer()).registerTypeAdapter(BinomialDistributionGenerator.class, new BinomialDistributionGenerator.Serializer()).registerTypeAdapter(ConstantIntValue.class, new ConstantIntValue.Serializer()).registerTypeHierarchyAdapter(LootItemCondition.class, new LootItemConditions.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
   private Map conditions = ImmutableMap.of();

   public PredicateManager() {
      super(GSON, "predicates");
   }

   @Nullable
   public LootItemCondition get(ResourceLocation var1) {
      return (LootItemCondition)this.conditions.get(var1);
   }

   protected void apply(Map var1, ResourceManager var2, ProfilerFiller var3) {
      Builder var4 = ImmutableMap.builder();
      var1.forEach((var1x, var2x) -> {
         try {
            LootItemCondition var3 = (LootItemCondition)GSON.fromJson(var2x, LootItemCondition.class);
            var4.put(var1x, var3);
         } catch (Exception var4x) {
            LOGGER.error("Couldn't parse loot table {}", var1x, var4x);
         }

      });
      ImmutableMap var5 = var4.build();
      ValidationContext var6 = new ValidationContext(LootContextParamSets.ALL_PARAMS, var5::get, (var0) -> {
         return null;
      });
      var5.forEach((var1x, var2x) -> {
         var2x.validate(var6.enterCondition("{" + var1x + "}", var1x));
      });
      var6.getProblems().forEach((var0, var1x) -> {
         LOGGER.warn("Found validation problem in " + var0 + ": " + var1x);
      });
      this.conditions = var5;
   }

   public Set getKeys() {
      return Collections.unmodifiableSet(this.conditions.keySet());
   }
}
