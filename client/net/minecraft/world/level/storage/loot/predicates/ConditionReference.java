package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import org.slf4j.Logger;

public record ConditionReference(ResourceKey<LootItemCondition> name) implements LootItemCondition {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<ConditionReference> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ResourceKey.codec(Registries.PREDICATE).fieldOf("name").forGetter(ConditionReference::name)).apply(i, ConditionReference::new));

   public ConditionReference {
      super();
   }

   public MapCodec<ConditionReference> codec() {
      return MAP_CODEC;
   }

   public void validate(final ValidationContext context) {
      LootItemCondition.super.validate(context);
      Validatable.validateReference(context, this.name);
   }

   public boolean test(final LootContext lootContext) {
      LootItemCondition condition = (LootItemCondition)lootContext.getResolver().get(this.name).map(Holder.Reference::value).orElse((Object)null);
      if (condition == null) {
         LOGGER.warn("Tried using unknown condition table called {}", this.name.identifier());
         return false;
      } else {
         LootContext.VisitedEntry<?> breadcrumb = LootContext.createVisitedEntry(condition);
         if (lootContext.pushVisitedElement(breadcrumb)) {
            boolean var4;
            try {
               var4 = condition.test(lootContext);
            } finally {
               lootContext.popVisitedElement(breadcrumb);
            }

            return var4;
         } else {
            LOGGER.warn("Detected infinite loop in loot tables");
            return false;
         }
      }
   }

   public static LootItemCondition.Builder conditionReference(final Holder.Reference<LootItemCondition> name) {
      return () -> new ConditionReference(name.key());
   }
}
