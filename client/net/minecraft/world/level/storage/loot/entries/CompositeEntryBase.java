package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class CompositeEntryBase extends LootPoolEntryContainer {
   protected final LootPoolEntryContainer[] children;
   private final ComposableEntryContainer composedChildren;

   protected CompositeEntryBase(LootPoolEntryContainer[] var1, LootItemCondition[] var2) {
      super(var2);
      this.children = var1;
      this.composedChildren = this.compose(var1);
   }

   public void validate(LootTableProblemCollector var1, Function<ResourceLocation, LootTable> var2, Set<ResourceLocation> var3, LootContextParamSet var4) {
      super.validate(var1, var2, var3, var4);
      if (this.children.length == 0) {
         var1.reportProblem("Empty children list");
      }

      for(int var5 = 0; var5 < this.children.length; ++var5) {
         this.children[var5].validate(var1.forChild(".entry[" + var5 + "]"), var2, var3, var4);
      }

   }

   protected abstract ComposableEntryContainer compose(ComposableEntryContainer[] var1);

   public final boolean expand(LootContext var1, Consumer<LootPoolEntry> var2) {
      return !this.canRun(var1) ? false : this.composedChildren.expand(var1, var2);
   }

   public static <T extends CompositeEntryBase> CompositeEntryBase.Serializer<T> createSerializer(ResourceLocation var0, Class<T> var1, final CompositeEntryBase.CompositeEntryConstructor<T> var2) {
      return new CompositeEntryBase.Serializer<T>(var0, var1) {
         protected T deserialize(JsonObject var1, JsonDeserializationContext var2x, LootPoolEntryContainer[] var3, LootItemCondition[] var4) {
            return var2.create(var3, var4);
         }
      };
   }

   public abstract static class Serializer<T extends CompositeEntryBase> extends LootPoolEntryContainer.Serializer<T> {
      public Serializer(ResourceLocation var1, Class<T> var2) {
         super(var1, var2);
      }

      public void serialize(JsonObject var1, T var2, JsonSerializationContext var3) {
         var1.add("children", var3.serialize(var2.children));
      }

      public final T deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         LootPoolEntryContainer[] var4 = (LootPoolEntryContainer[])GsonHelper.getAsObject(var1, "children", var2, LootPoolEntryContainer[].class);
         return this.deserialize(var1, var2, var4, var3);
      }

      protected abstract T deserialize(JsonObject var1, JsonDeserializationContext var2, LootPoolEntryContainer[] var3, LootItemCondition[] var4);

      // $FF: synthetic method
      public LootPoolEntryContainer deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }

   @FunctionalInterface
   public interface CompositeEntryConstructor<T extends CompositeEntryBase> {
      T create(LootPoolEntryContainer[] var1, LootItemCondition[] var2);
   }
}
