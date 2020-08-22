package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public abstract class CompositeEntryBase extends LootPoolEntryContainer {
   protected final LootPoolEntryContainer[] children;
   private final ComposableEntryContainer composedChildren;

   protected CompositeEntryBase(LootPoolEntryContainer[] var1, LootItemCondition[] var2) {
      super(var2);
      this.children = var1;
      this.composedChildren = this.compose(var1);
   }

   public void validate(ValidationContext var1) {
      super.validate(var1);
      if (this.children.length == 0) {
         var1.reportProblem("Empty children list");
      }

      for(int var2 = 0; var2 < this.children.length; ++var2) {
         this.children[var2].validate(var1.forChild(".entry[" + var2 + "]"));
      }

   }

   protected abstract ComposableEntryContainer compose(ComposableEntryContainer[] var1);

   public final boolean expand(LootContext var1, Consumer var2) {
      return !this.canRun(var1) ? false : this.composedChildren.expand(var1, var2);
   }

   public static CompositeEntryBase.Serializer createSerializer(ResourceLocation var0, Class var1, final CompositeEntryBase.CompositeEntryConstructor var2) {
      return new CompositeEntryBase.Serializer(var0, var1) {
         protected CompositeEntryBase deserialize(JsonObject var1, JsonDeserializationContext var2x, LootPoolEntryContainer[] var3, LootItemCondition[] var4) {
            return var2.create(var3, var4);
         }
      };
   }

   public abstract static class Serializer extends LootPoolEntryContainer.Serializer {
      public Serializer(ResourceLocation var1, Class var2) {
         super(var1, var2);
      }

      public void serialize(JsonObject var1, CompositeEntryBase var2, JsonSerializationContext var3) {
         var1.add("children", var3.serialize(var2.children));
      }

      public final CompositeEntryBase deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         LootPoolEntryContainer[] var4 = (LootPoolEntryContainer[])GsonHelper.getAsObject(var1, "children", var2, LootPoolEntryContainer[].class);
         return this.deserialize(var1, var2, var4, var3);
      }

      protected abstract CompositeEntryBase deserialize(JsonObject var1, JsonDeserializationContext var2, LootPoolEntryContainer[] var3, LootItemCondition[] var4);

      // $FF: synthetic method
      public LootPoolEntryContainer deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }

   @FunctionalInterface
   public interface CompositeEntryConstructor {
      CompositeEntryBase create(LootPoolEntryContainer[] var1, LootItemCondition[] var2);
   }
}
