package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public abstract class LootPoolEntryContainer implements ComposableEntryContainer {
   protected final LootItemCondition[] conditions;
   private final Predicate<LootContext> compositeCondition;

   protected LootPoolEntryContainer(LootItemCondition[] var1) {
      super();
      this.conditions = var1;
      this.compositeCondition = LootItemConditions.andConditions(var1);
   }

   public void validate(LootTableProblemCollector var1, Function<ResourceLocation, LootTable> var2, Set<ResourceLocation> var3, LootContextParamSet var4) {
      for(int var5 = 0; var5 < this.conditions.length; ++var5) {
         this.conditions[var5].validate(var1.forChild(".condition[" + var5 + "]"), var2, var3, var4);
      }

   }

   protected final boolean canRun(LootContext var1) {
      return this.compositeCondition.test(var1);
   }

   public abstract static class Serializer<T extends LootPoolEntryContainer> {
      private final ResourceLocation name;
      private final Class<T> clazz;

      protected Serializer(ResourceLocation var1, Class<T> var2) {
         super();
         this.name = var1;
         this.clazz = var2;
      }

      public ResourceLocation getName() {
         return this.name;
      }

      public Class<T> getContainerClass() {
         return this.clazz;
      }

      public abstract void serialize(JsonObject var1, T var2, JsonSerializationContext var3);

      public abstract T deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3);
   }

   public abstract static class Builder<T extends LootPoolEntryContainer.Builder<T>> implements ConditionUserBuilder<T> {
      private final List<LootItemCondition> conditions = Lists.newArrayList();

      public Builder() {
         super();
      }

      protected abstract T getThis();

      public T when(LootItemCondition.Builder var1) {
         this.conditions.add(var1.build());
         return this.getThis();
      }

      public final T unwrap() {
         return this.getThis();
      }

      protected LootItemCondition[] getConditions() {
         return (LootItemCondition[])this.conditions.toArray(new LootItemCondition[0]);
      }

      public AlternativesEntry.Builder otherwise(LootPoolEntryContainer.Builder<?> var1) {
         return new AlternativesEntry.Builder(new LootPoolEntryContainer.Builder[]{this, var1});
      }

      public abstract LootPoolEntryContainer build();

      // $FF: synthetic method
      public Object unwrap() {
         return this.unwrap();
      }

      // $FF: synthetic method
      public Object when(LootItemCondition.Builder var1) {
         return this.when(var1);
      }
   }
}
