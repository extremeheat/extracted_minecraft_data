package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;

public class LootModifierArgument extends ValidatableResourceOrIdArgument<LootItemFunction> {
   public static final SerializableArgument.Type<LootModifierArgument> TYPE = ValidatableArgument.codecWithValidation(LootModifierArgument::new);

   private LootModifierArgument(final ContextKeySet contextKeySet) {
      super(Registries.ITEM_MODIFIER, LootItemFunctions.DIRECT_CODEC, contextKeySet);
   }

   public SerializableArgument.Type<LootModifierArgument> type() {
      return TYPE;
   }

   public static ArgumentType<ValidationWrapper<LootItemFunction>> lootModifier(final ContextKeySet contextKeySet) {
      return (new LootModifierArgument(contextKeySet)).toBrigadier();
   }

   public static ValidationWrapper<LootItemFunction> getLootModifier(final CommandContext<?> context, final String name) {
      return getResource(context, name);
   }
}
