package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTables extends SimpleJsonResourceReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(RandomValueBounds.class, new RandomValueBounds.Serializer()).registerTypeAdapter(BinomialDistributionGenerator.class, new BinomialDistributionGenerator.Serializer()).registerTypeAdapter(ConstantIntValue.class, new ConstantIntValue.Serializer()).registerTypeAdapter(IntLimiter.class, new IntLimiter.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootPoolEntryContainer.class, new LootPoolEntries.Serializer()).registerTypeHierarchyAdapter(LootItemFunction.class, new LootItemFunctions.Serializer()).registerTypeHierarchyAdapter(LootItemCondition.class, new LootItemConditions.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
   private Map<ResourceLocation, LootTable> tables = ImmutableMap.of();

   public LootTables() {
      super(GSON, "loot_tables");
   }

   public LootTable get(ResourceLocation var1) {
      return (LootTable)this.tables.getOrDefault(var1, LootTable.EMPTY);
   }

   protected void apply(Map<ResourceLocation, JsonObject> var1, ResourceManager var2, ProfilerFiller var3) {
      Builder var4 = ImmutableMap.builder();
      JsonObject var5 = (JsonObject)var1.remove(BuiltInLootTables.EMPTY);
      if (var5 != null) {
         LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", BuiltInLootTables.EMPTY);
      }

      var1.forEach((var1x, var2x) -> {
         try {
            LootTable var3 = (LootTable)GSON.fromJson(var2x, LootTable.class);
            var4.put(var1x, var3);
         } catch (Exception var4x) {
            LOGGER.error("Couldn't parse loot table {}", var1x, var4x);
         }

      });
      var4.put(BuiltInLootTables.EMPTY, LootTable.EMPTY);
      ImmutableMap var6 = var4.build();
      LootTableProblemCollector var7 = new LootTableProblemCollector();
      var6.forEach((var2x, var3x) -> {
         validate(var7, var2x, var3x, var6::get);
      });
      var7.getProblems().forEach((var0, var1x) -> {
         LOGGER.warn("Found validation problem in " + var0 + ": " + var1x);
      });
      this.tables = var6;
   }

   public static void validate(LootTableProblemCollector var0, ResourceLocation var1, LootTable var2, Function<ResourceLocation, LootTable> var3) {
      ImmutableSet var4 = ImmutableSet.of(var1);
      var2.validate(var0.forChild("{" + var1.toString() + "}"), var3, var4, var2.getParamSet());
   }

   public static JsonElement serialize(LootTable var0) {
      return GSON.toJsonTree(var0);
   }

   public Set<ResourceLocation> getIds() {
      return this.tables.keySet();
   }
}
