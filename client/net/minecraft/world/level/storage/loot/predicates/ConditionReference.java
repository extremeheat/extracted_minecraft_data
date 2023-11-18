package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.ValidationContext;
import org.slf4j.Logger;

public record ConditionReference(ResourceLocation b) implements LootItemCondition {
   private final ResourceLocation name;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<ConditionReference> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(ResourceLocation.CODEC.fieldOf("name").forGetter(ConditionReference::name)).apply(var0, ConditionReference::new)
   );

   public ConditionReference(ResourceLocation var1) {
      super();
      this.name = var1;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.REFERENCE;
   }

   @Override
   public void validate(ValidationContext var1) {
      LootDataId var2 = new LootDataId<>(LootDataType.PREDICATE, this.name);
      if (var1.hasVisitedElement(var2)) {
         var1.reportProblem("Condition " + this.name + " is recursively called");
      } else {
         LootItemCondition.super.validate(var1);
         var1.resolver()
            .<LootItemCondition>getElementOptional(var2)
            .ifPresentOrElse(
               var3 -> var3.validate(var1.enterElement(".{" + this.name + "}", var2)), () -> var1.reportProblem("Unknown condition table called " + this.name)
            );
      }
   }

   public boolean test(LootContext var1) {
      LootItemCondition var2 = var1.getResolver().getElement(LootDataType.PREDICATE, this.name);
      if (var2 == null) {
         LOGGER.warn("Tried using unknown condition table called {}", this.name);
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

   public static LootItemCondition.Builder conditionReference(ResourceLocation var0) {
      return () -> new ConditionReference(var0);
   }
}
