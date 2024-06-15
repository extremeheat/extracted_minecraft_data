package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import org.slf4j.Logger;

public record ConditionReference(ResourceKey<LootItemCondition> name) implements LootItemCondition {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<ConditionReference> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(ResourceKey.codec(Registries.PREDICATE).fieldOf("name").forGetter(ConditionReference::name)).apply(var0, ConditionReference::new)
   );

   public ConditionReference(ResourceKey<LootItemCondition> name) {
      super();
      this.name = name;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.REFERENCE;
   }

   @Override
   public void validate(ValidationContext var1) {
      if (var1.hasVisitedElement(this.name)) {
         var1.reportProblem("Condition " + this.name.location() + " is recursively called");
      } else {
         LootItemCondition.super.validate(var1);
         var1.resolver()
            .get(Registries.PREDICATE, this.name)
            .ifPresentOrElse(
               var2 -> var2.value().validate(var1.enterElement(".{" + this.name.location() + "}", this.name)),
               () -> var1.reportProblem("Unknown condition table called " + this.name.location())
            );
      }
   }

   public boolean test(LootContext var1) {
      LootItemCondition var2 = var1.getResolver().get(Registries.PREDICATE, this.name).map(Holder.Reference::value).orElse(null);
      if (var2 == null) {
         LOGGER.warn("Tried using unknown condition table called {}", this.name.location());
         return false;
      } else {
         LootContext.VisitedEntry var3 = LootContext.createVisitedEntry(var2);
         if (var1.pushVisitedElement(var3)) {
            boolean var4;
            try {
               var4 = var2.test(var1);
            } finally {
               var1.popVisitedElement(var3);
            }

            return var4;
         } else {
            LOGGER.warn("Detected infinite loop in loot tables");
            return false;
         }
      }
   }

   public static LootItemCondition.Builder conditionReference(ResourceKey<LootItemCondition> var0) {
      return () -> new ConditionReference(var0);
   }
}
