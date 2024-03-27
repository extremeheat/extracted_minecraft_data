package net.minecraft.world.level.storage.loot.functions;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class FunctionReference extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<FunctionReference> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(ResourceKey.codec(Registries.ITEM_MODIFIER).fieldOf("name").forGetter(var0x -> var0x.name))
            .apply(var0, FunctionReference::new)
   );
   private final ResourceKey<LootItemFunction> name;

   private FunctionReference(List<LootItemCondition> var1, ResourceKey<LootItemFunction> var2) {
      super(var1);
      this.name = var2;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.REFERENCE;
   }

   @Override
   public void validate(ValidationContext var1) {
      if (var1.hasVisitedElement(this.name)) {
         var1.reportProblem("Function " + this.name.location() + " is recursively called");
      } else {
         super.validate(var1);
         var1.resolver()
            .get(Registries.ITEM_MODIFIER, this.name)
            .ifPresentOrElse(
               var2 -> ((LootItemFunction)var2.value()).validate(var1.enterElement(".{" + this.name.location() + "}", this.name)),
               () -> var1.reportProblem("Unknown function table called " + this.name.location())
            );
      }
   }

   @Override
   protected ItemStack run(ItemStack var1, LootContext var2) {
      LootItemFunction var3 = var2.getResolver().get(Registries.ITEM_MODIFIER, this.name).map(Holder::value).orElse(null);
      if (var3 == null) {
         LOGGER.warn("Unknown function: {}", this.name.location());
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

   public static LootItemConditionalFunction.Builder<?> functionReference(ResourceKey<LootItemFunction> var0) {
      return simpleBuilder(var1 -> new FunctionReference(var1, var0));
   }
}
