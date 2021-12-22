package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTables extends SimpleJsonResourceReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = Deserializers.createLootTableSerializer().create();
   private Map<ResourceLocation, LootTable> tables = ImmutableMap.of();
   private final PredicateManager predicateManager;

   public LootTables(PredicateManager var1) {
      super(GSON, "loot_tables");
      this.predicateManager = var1;
   }

   public LootTable get(ResourceLocation var1) {
      return (LootTable)this.tables.getOrDefault(var1, LootTable.EMPTY);
   }

   protected void apply(Map<ResourceLocation, JsonElement> var1, ResourceManager var2, ProfilerFiller var3) {
      Builder var4 = ImmutableMap.builder();
      JsonElement var5 = (JsonElement)var1.remove(BuiltInLootTables.EMPTY);
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
      LootContextParamSet var10002 = LootContextParamSets.ALL_PARAMS;
      PredicateManager var10003 = this.predicateManager;
      Objects.requireNonNull(var10003);
      Function var8 = var10003::get;
      Objects.requireNonNull(var6);
      ValidationContext var7 = new ValidationContext(var10002, var8, var6::get);
      var6.forEach((var1x, var2x) -> {
         validate(var7, var1x, var2x);
      });
      var7.getProblems().forEach((var0, var1x) -> {
         LOGGER.warn("Found validation problem in {}: {}", var0, var1x);
      });
      this.tables = var6;
   }

   public static void validate(ValidationContext var0, ResourceLocation var1, LootTable var2) {
      var2.validate(var0.setParams(var2.getParamSet()).enterTable("{" + var1 + "}", var1));
   }

   public static JsonElement serialize(LootTable var0) {
      return GSON.toJsonTree(var0);
   }

   public Set<ResourceLocation> getIds() {
      return this.tables.keySet();
   }
}
