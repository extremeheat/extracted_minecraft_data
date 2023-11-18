package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.slf4j.Logger;

public class LootDataManager implements PreparableReloadListener, LootDataResolver {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final LootDataId<LootTable> EMPTY_LOOT_TABLE_KEY = new LootDataId<>(LootDataType.TABLE, BuiltInLootTables.EMPTY);
   private Map<LootDataId<?>, ?> elements = Map.of();
   private Multimap<LootDataType<?>, ResourceLocation> typeKeys = ImmutableMultimap.of();

   public LootDataManager() {
      super();
   }

   @Override
   public final CompletableFuture<Void> reload(
      PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6
   ) {
      HashMap var7 = new HashMap();
      CompletableFuture[] var8 = LootDataType.values()
         .map(var3x -> scheduleElementParse(var3x, var2, var5, var7))
         .toArray(var0 -> new CompletableFuture[var0]);
      return CompletableFuture.allOf(var8).thenCompose(var1::wait).thenAcceptAsync(var2x -> this.apply(var7), var6);
   }

   private static <T> CompletableFuture<?> scheduleElementParse(
      LootDataType<T> var0, ResourceManager var1, Executor var2, Map<LootDataType<?>, Map<ResourceLocation, ?>> var3
   ) {
      HashMap var4 = new HashMap();
      var3.put(var0, var4);
      return CompletableFuture.runAsync(() -> {
         HashMap var3x = new HashMap();
         SimpleJsonResourceReloadListener.scanDirectory(var1, var0.directory(), var0.parser(), var3x);
         var3x.forEach((var2xx, var3xx) -> var0.deserialize(var2xx, var3xx).ifPresent(var2xxx -> var4.put(var2xx, var2xxx)));
      }, var2);
   }

   private void apply(Map<LootDataType<?>, Map<ResourceLocation, ?>> var1) {
      Object var2 = ((Map)var1.get(LootDataType.TABLE)).remove(BuiltInLootTables.EMPTY);
      if (var2 != null) {
         LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", BuiltInLootTables.EMPTY);
      }

      Builder var3 = ImmutableMap.builder();
      com.google.common.collect.ImmutableMultimap.Builder var4 = ImmutableMultimap.builder();
      var1.forEach((var2x, var3x) -> var3x.forEach((var3xx, var4x) -> {
            var3.put(new LootDataId(var2x, var3xx), var4x);
            var4.put(var2x, var3xx);
         }));
      var3.put(EMPTY_LOOT_TABLE_KEY, LootTable.EMPTY);
      final ImmutableMap var5 = var3.build();
      ValidationContext var6 = new ValidationContext(LootContextParamSets.ALL_PARAMS, new LootDataResolver() {
         @Nullable
         @Override
         public <T> T getElement(LootDataId<T> var1) {
            return (T)var5.get(var1);
         }
      });
      var5.forEach((var1x, var2x) -> castAndValidate(var6, var1x, var2x));
      var6.getProblems().forEach((var0, var1x) -> LOGGER.warn("Found loot table element validation problem in {}: {}", var0, var1x));
      this.elements = var5;
      this.typeKeys = var4.build();
   }

   private static <T> void castAndValidate(ValidationContext var0, LootDataId<T> var1, Object var2) {
      var1.type().runValidation(var0, var1, var2);
   }

   @Nullable
   @Override
   public <T> T getElement(LootDataId<T> var1) {
      return (T)this.elements.get(var1);
   }

   public Collection<ResourceLocation> getKeys(LootDataType<?> var1) {
      return this.typeKeys.get(var1);
   }

   public static LootItemCondition createComposite(LootItemCondition[] var0) {
      return new LootDataManager.CompositePredicate(var0);
   }

   public static LootItemFunction createComposite(LootItemFunction[] var0) {
      return new LootDataManager.FunctionSequence(var0);
   }

   static class CompositePredicate implements LootItemCondition {
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

      @Override
      public void validate(ValidationContext var1) {
         LootItemCondition.super.validate(var1);

         for(int var2 = 0; var2 < this.terms.length; ++var2) {
            this.terms[var2].validate(var1.forChild(".term[" + var2 + "]"));
         }
      }

      @Override
      public LootItemConditionType getType() {
         throw new UnsupportedOperationException();
      }
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
      public void validate(ValidationContext var1) {
         LootItemFunction.super.validate(var1);

         for(int var2 = 0; var2 < this.functions.length; ++var2) {
            this.functions[var2].validate(var1.forChild(".function[" + var2 + "]"));
         }
      }

      @Override
      public LootItemFunctionType getType() {
         throw new UnsupportedOperationException();
      }
   }
}
