package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;

public abstract class LootPoolEntryContainer implements ComposableEntryContainer {
   protected final LootItemCondition[] conditions;
   private final Predicate compositeCondition;

   protected LootPoolEntryContainer(LootItemCondition[] var1) {
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

   public abstract static class Serializer {
      private final ResourceLocation name;
      private final Class clazz;

      protected Serializer(ResourceLocation var1, Class var2) {
         this.name = var1;
         this.clazz = var2;
      }

      public ResourceLocation getName() {
         return this.name;
      }

      public Class getContainerClass() {
         return this.clazz;
      }

      public abstract void serialize(JsonObject var1, LootPoolEntryContainer var2, JsonSerializationContext var3);

      public abstract LootPoolEntryContainer deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3);
   }

   public abstract static class Builder implements ConditionUserBuilder {
      private final List conditions = Lists.newArrayList();

      protected abstract LootPoolEntryContainer.Builder getThis();

      public LootPoolEntryContainer.Builder when(LootItemCondition.Builder var1) {
         this.conditions.add(var1.build());
         return this.getThis();
      }

      public final LootPoolEntryContainer.Builder unwrap() {
         return this.getThis();
      }

      protected LootItemCondition[] getConditions() {
         return (LootItemCondition[])this.conditions.toArray(new LootItemCondition[0]);
      }

      public AlternativesEntry.Builder otherwise(LootPoolEntryContainer.Builder var1) {
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
