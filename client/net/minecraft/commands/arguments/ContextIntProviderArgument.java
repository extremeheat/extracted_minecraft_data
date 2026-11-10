package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public class ContextIntProviderArgument extends ValidatableResourceOrIdArgument<ContextIntProvider> {
   public static final SerializableArgument.Type<ContextIntProviderArgument> TYPE = ValidatableArgument.codecWithValidation(ContextIntProviderArgument::new);

   private ContextIntProviderArgument(final ContextKeySet contextKeySet) {
      super(Registries.CONTEXT_INT_PROVIDER, ContextIntProviders.DIRECT_CODEC, contextKeySet);
   }

   public SerializableArgument.Type<ContextIntProviderArgument> type() {
      return TYPE;
   }

   public static ArgumentType<ValidationWrapper<ContextIntProvider>> intProvider(final ContextKeySet contextKeySet) {
      return (new ContextIntProviderArgument(contextKeySet)).toBrigadier();
   }

   public static ValidationWrapper<ContextIntProvider> getIntProvider(final CommandContext<?> context, final String name) {
      return getResource(context, name);
   }
}
