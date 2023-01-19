package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;

public class ItemModifierManager extends SimpleJsonResourceReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = Deserializers.createFunctionSerializer().create();
   private final PredicateManager predicateManager;
   private final LootTables lootTables;
   private Map<ResourceLocation, LootItemFunction> functions = ImmutableMap.of();

   public ItemModifierManager(PredicateManager var1, LootTables var2) {
      super(GSON, "item_modifiers");
      this.predicateManager = var1;
      this.lootTables = var2;
   }

   @Nullable
   public LootItemFunction get(ResourceLocation var1) {
      return this.functions.get(var1);
   }

   public LootItemFunction get(ResourceLocation var1, LootItemFunction var2) {
      return this.functions.getOrDefault(var1, var2);
   }

   protected void apply(Map<ResourceLocation, JsonElement> var1, ResourceManager var2, ProfilerFiller var3) {
      Builder var4 = ImmutableMap.builder();
      var1.forEach((var1x, var2x) -> {
         try {
            if (var2x.isJsonArray()) {
               LootItemFunction[] var3x = (LootItemFunction[])GSON.fromJson(var2x, LootItemFunction[].class);
               var4.put(var1x, new ItemModifierManager.FunctionSequence(var3x));
            } else {
               LootItemFunction var5x = (LootItemFunction)GSON.fromJson(var2x, LootItemFunction.class);
               var4.put(var1x, var5x);
            }
         } catch (Exception var4x) {
            LOGGER.error("Couldn't parse item modifier {}", var1x, var4x);
         }
      });
      ImmutableMap var5 = var4.build();
      ValidationContext var6 = new ValidationContext(LootContextParamSets.ALL_PARAMS, this.predicateManager::get, this.lootTables::get);
      var5.forEach((var1x, var2x) -> var2x.validate(var6));
      var6.getProblems().forEach((var0, var1x) -> LOGGER.warn("Found item modifier validation problem in {}: {}", var0, var1x));
      this.functions = var5;
   }

   public Set<ResourceLocation> getKeys() {
      return Collections.unmodifiableSet(this.functions.keySet());
   }

   static class FunctionSequence implements LootItemFunction {
      protected final LootItemFunction[] functions;
      private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

      public FunctionSequence(LootItemFunction[] var1) {
         super();
         this.functions = var1;
         this.compositeFunction = LootItemFunctions.compose(var1);
      }

      public ItemStack apply(ItemStack var1, LootContext var2) {
         return this.compositeFunction.apply(var1, var2);
      }

      @Override
      public LootItemFunctionType getType() {
         throw new UnsupportedOperationException();
      }
   }
}
