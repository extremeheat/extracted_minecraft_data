package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootPoolSingletonContainer extends LootPoolEntryContainer {
   public static final int DEFAULT_WEIGHT = 1;
   public static final int DEFAULT_QUALITY = 0;
   protected final int weight;
   protected final int quality;
   protected final LootItemFunction[] functions;
   final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
   private final LootPoolEntry entry = new LootPoolSingletonContainer.EntryBase() {
      public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
         LootPoolSingletonContainer.this.createItemStack(LootItemFunction.decorate(LootPoolSingletonContainer.this.compositeFunction, var1, var2), var2);
      }
   };

   protected LootPoolSingletonContainer(int var1, int var2, LootItemCondition[] var3, LootItemFunction[] var4) {
      super(var3);
      this.weight = var1;
      this.quality = var2;
      this.functions = var4;
      this.compositeFunction = LootItemFunctions.compose(var4);
   }

   public void validate(ValidationContext var1) {
      super.validate(var1);

      for(int var2 = 0; var2 < this.functions.length; ++var2) {
         this.functions[var2].validate(var1.forChild(".functions[" + var2 + "]"));
      }

   }

   protected abstract void createItemStack(Consumer<ItemStack> var1, LootContext var2);

   public boolean expand(LootContext var1, Consumer<LootPoolEntry> var2) {
      if (this.canRun(var1)) {
         var2.accept(this.entry);
         return true;
      } else {
         return false;
      }
   }

   public static LootPoolSingletonContainer.Builder<?> simpleBuilder(LootPoolSingletonContainer.EntryConstructor var0) {
      return new LootPoolSingletonContainer.DummyBuilder(var0);
   }

   static class DummyBuilder extends LootPoolSingletonContainer.Builder<LootPoolSingletonContainer.DummyBuilder> {
      private final LootPoolSingletonContainer.EntryConstructor constructor;

      public DummyBuilder(LootPoolSingletonContainer.EntryConstructor var1) {
         super();
         this.constructor = var1;
      }

      protected LootPoolSingletonContainer.DummyBuilder getThis() {
         return this;
      }

      public LootPoolEntryContainer build() {
         return this.constructor.build(this.weight, this.quality, this.getConditions(), this.getFunctions());
      }

      // $FF: synthetic method
      protected LootPoolEntryContainer.Builder getThis() {
         return this.getThis();
      }
   }

   @FunctionalInterface
   protected interface EntryConstructor {
      LootPoolSingletonContainer build(int var1, int var2, LootItemCondition[] var3, LootItemFunction[] var4);
   }

   public abstract static class Serializer<T extends LootPoolSingletonContainer> extends LootPoolEntryContainer.Serializer<T> {
      public Serializer() {
         super();
      }

      public void serializeCustom(JsonObject var1, T var2, JsonSerializationContext var3) {
         if (var2.weight != 1) {
            var1.addProperty("weight", var2.weight);
         }

         if (var2.quality != 0) {
            var1.addProperty("quality", var2.quality);
         }

         if (!ArrayUtils.isEmpty(var2.functions)) {
            var1.add("functions", var3.serialize(var2.functions));
         }

      }

      public final T deserializeCustom(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         int var4 = GsonHelper.getAsInt(var1, "weight", 1);
         int var5 = GsonHelper.getAsInt(var1, "quality", 0);
         LootItemFunction[] var6 = (LootItemFunction[])GsonHelper.getAsObject(var1, "functions", new LootItemFunction[0], var2, LootItemFunction[].class);
         return this.deserialize(var1, var2, var4, var5, var3, var6);
      }

      protected abstract T deserialize(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, LootItemCondition[] var5, LootItemFunction[] var6);

      // $FF: synthetic method
      public LootPoolEntryContainer deserializeCustom(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserializeCustom(var1, var2, var3);
      }
   }

   public abstract static class Builder<T extends LootPoolSingletonContainer.Builder<T>> extends LootPoolEntryContainer.Builder<T> implements FunctionUserBuilder<T> {
      protected int weight = 1;
      protected int quality = 0;
      private final List<LootItemFunction> functions = Lists.newArrayList();

      public Builder() {
         super();
      }

      public T apply(LootItemFunction.Builder var1) {
         this.functions.add(var1.build());
         return (LootPoolSingletonContainer.Builder)this.getThis();
      }

      protected LootItemFunction[] getFunctions() {
         return (LootItemFunction[])this.functions.toArray(new LootItemFunction[0]);
      }

      public T setWeight(int var1) {
         this.weight = var1;
         return (LootPoolSingletonContainer.Builder)this.getThis();
      }

      public T setQuality(int var1) {
         this.quality = var1;
         return (LootPoolSingletonContainer.Builder)this.getThis();
      }

      // $FF: synthetic method
      public Object apply(LootItemFunction.Builder var1) {
         return this.apply(var1);
      }
   }

   protected abstract class EntryBase implements LootPoolEntry {
      protected EntryBase() {
         super();
      }

      public int getWeight(float var1) {
         return Math.max(Mth.floor((float)LootPoolSingletonContainer.this.weight + (float)LootPoolSingletonContainer.this.quality * var1), 0);
      }
   }
}
