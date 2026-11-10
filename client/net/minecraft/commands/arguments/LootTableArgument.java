package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootTable;

public class LootTableArgument extends ValidatableResourceOrIdArgument<LootTable> {
   public static final SerializableArgument.Type<LootTableArgument> TYPE = ValidatableArgument.codecWithValidation(LootTableArgument::new);

   private LootTableArgument(final ContextKeySet contextKeySet) {
      super(Registries.LOOT_TABLE, LootTable.DIRECT_CODEC, contextKeySet);
   }

   public SerializableArgument.Type<LootTableArgument> type() {
      return TYPE;
   }

   public static ArgumentType<ValidationWrapper<LootTable>> lootTable(final ContextKeySet contextKeySet) {
      return (new LootTableArgument(contextKeySet)).toBrigadier();
   }

   public static ValidationWrapper<LootTable> getLootTable(final CommandContext<?> context, final String name) throws CommandSyntaxException {
      return getResource(context, name);
   }
}
