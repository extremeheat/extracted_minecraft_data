package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableProblemCollector;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public class InvertedLootItemCondition implements LootItemCondition {
   private final LootItemCondition term;

   private InvertedLootItemCondition(LootItemCondition var1) {
      super();
      this.term = var1;
   }

   public final boolean test(LootContext var1) {
      return !this.term.test(var1);
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.term.getReferencedContextParams();
   }

   public void validate(LootTableProblemCollector var1, Function<ResourceLocation, LootTable> var2, Set<ResourceLocation> var3, LootContextParamSet var4) {
      LootItemCondition.super.validate(var1, var2, var3, var4);
      this.term.validate(var1, var2, var3, var4);
   }

   public static LootItemCondition.Builder invert(LootItemCondition.Builder var0) {
      InvertedLootItemCondition var1 = new InvertedLootItemCondition(var0.build());
      return () -> {
         return var1;
      };
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   // $FF: synthetic method
   InvertedLootItemCondition(LootItemCondition var1, Object var2) {
      this(var1);
   }

   public static class Serializer extends LootItemCondition.Serializer<InvertedLootItemCondition> {
      public Serializer() {
         super(new ResourceLocation("inverted"), InvertedLootItemCondition.class);
      }

      public void serialize(JsonObject var1, InvertedLootItemCondition var2, JsonSerializationContext var3) {
         var1.add("term", var3.serialize(var2.term));
      }

      public InvertedLootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         LootItemCondition var3 = (LootItemCondition)GsonHelper.getAsObject(var1, "term", var2, LootItemCondition.class);
         return new InvertedLootItemCondition(var3);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
