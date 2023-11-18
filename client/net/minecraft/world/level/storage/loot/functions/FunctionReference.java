package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class FunctionReference extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   final ResourceLocation name;

   FunctionReference(LootItemCondition[] var1, ResourceLocation var2) {
      super(var1);
      this.name = var2;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.REFERENCE;
   }

   @Override
   public void validate(ValidationContext var1) {
      LootDataId var2 = new LootDataId<>(LootDataType.MODIFIER, this.name);
      if (var1.hasVisitedElement(var2)) {
         var1.reportProblem("Function " + this.name + " is recursively called");
      } else {
         super.validate(var1);
         var1.resolver()
            .<LootItemFunction>getElementOptional(var2)
            .ifPresentOrElse(
               var3 -> var3.validate(var1.enterElement(".{" + this.name + "}", var2)), () -> var1.reportProblem("Unknown function table called " + this.name)
            );
      }
   }

   @Override
   protected ItemStack run(ItemStack var1, LootContext var2) {
      LootItemFunction var3 = var2.getResolver().getElement(LootDataType.MODIFIER, this.name);
      if (var3 == null) {
         LOGGER.warn("Unknown function: {}", this.name);
         return var1;
      } else {
         LootContext.VisitedEntry var4 = LootContext.createVisitedEntry(var3);
         if (var2.pushVisitedElement(var4)) {
            ItemStack var5;
            try {
               var5 = var3.apply(var1, var2);
            } finally {
               var2.popVisitedElement(var4);
            }

            return var5;
         } else {
            LOGGER.warn("Detected infinite loop in loot tables");
            return var1;
         }
      }
   }

   public static LootItemConditionalFunction.Builder<?> functionReference(ResourceLocation var0) {
      return simpleBuilder(var1 -> new FunctionReference(var1, var0));
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<FunctionReference> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, FunctionReference var2, JsonSerializationContext var3) {
         var1.addProperty("name", var2.name.toString());
      }

      public FunctionReference deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(var1, "name"));
         return new FunctionReference(var3, var4);
      }
   }
}
