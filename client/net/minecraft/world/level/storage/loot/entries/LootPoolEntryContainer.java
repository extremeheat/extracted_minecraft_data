package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootPoolEntryContainer implements ComposableEntryContainer {
   protected final LootItemCondition[] conditions;
   private final Predicate<LootContext> compositeCondition;

   protected LootPoolEntryContainer(LootItemCondition[] var1) {
      super();
      this.conditions = var1;
      this.compositeCondition = LootItemConditions.andConditions(var1);
   }

   public void validate(ValidationContext var1) {
      for(int var2 = 0; var2 < this.conditions.length; ++var2) {
         this.conditions[var2].validate(var1.forChild(".condition[" + var2 + "]"));
      }
   }

   protected final boolean canRun(LootContext var1) {
      return this.compositeCondition.test(var1);
   }

   public abstract LootPoolEntryType getType();

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
         return this.conditions.toArray(new LootItemCondition[0]);
      }

      public AlternativesEntry.Builder otherwise(LootPoolEntryContainer.Builder<?> var1) {
         return new AlternativesEntry.Builder(this, var1);
      }

      public EntryGroup.Builder append(LootPoolEntryContainer.Builder<?> var1) {
         return new EntryGroup.Builder(this, var1);
      }

      public SequentialEntry.Builder then(LootPoolEntryContainer.Builder<?> var1) {
         return new SequentialEntry.Builder(this, var1);
      }

      public abstract LootPoolEntryContainer build();
   }

   public abstract static class Serializer<T extends LootPoolEntryContainer> implements net.minecraft.world.level.storage.loot.Serializer<T> {
      public Serializer() {
         super();
      }

      public final void serialize(JsonObject var1, T var2, JsonSerializationContext var3) {
         if (!ArrayUtils.isEmpty(var2.conditions)) {
            var1.add("conditions", var3.serialize(var2.conditions));
         }

         this.serializeCustom(var1, (T)var2, var3);
      }

      public final T deserialize(JsonObject var1, JsonDeserializationContext var2) {
         LootItemCondition[] var3 = (LootItemCondition[])GsonHelper.getAsObject(var1, "conditions", new LootItemCondition[0], var2, LootItemCondition[].class);
         return this.deserializeCustom(var1, var2, var3);
      }

      public abstract void serializeCustom(JsonObject var1, T var2, JsonSerializationContext var3);

      public abstract T deserializeCustom(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3);
   }
}
