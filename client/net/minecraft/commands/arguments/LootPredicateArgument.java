package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootPredicateArgument extends ValidatableResourceOrIdArgument<LootItemCondition> {
   public static final SerializableArgument.Type<LootPredicateArgument> TYPE = ValidatableArgument.codecWithValidation(LootPredicateArgument::new);

   private LootPredicateArgument(final ContextKeySet contextKeySet) {
      super(Registries.PREDICATE, LootItemCondition.DIRECT_CODEC, contextKeySet);
   }

   public SerializableArgument.Type<LootPredicateArgument> type() {
      return TYPE;
   }

   public static ArgumentType<ValidationWrapper<LootItemCondition>> lootPredicate(final ContextKeySet contextKeySet) {
      return (new LootPredicateArgument(contextKeySet)).toBrigadier();
   }

   public static ValidationWrapper<LootItemCondition> getLootPredicate(final CommandContext<?> context, final String name) {
      return getResource(context, name);
   }
}
