package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;

public class ContextFloatProviderArgument extends ValidatableResourceOrIdArgument<ContextFloatProvider> {
   public static final SerializableArgument.Type<ContextFloatProviderArgument> TYPE = ValidatableArgument.codecWithValidation(ContextFloatProviderArgument::new);

   private ContextFloatProviderArgument(final ContextKeySet contextKeySet) {
      super(Registries.CONTEXT_FLOAT_PROVIDER, ContextFloatProviders.DIRECT_CODEC, contextKeySet);
   }

   public SerializableArgument.Type<ContextFloatProviderArgument> type() {
      return TYPE;
   }

   public static ArgumentType<ValidationWrapper<ContextFloatProvider>> floatProvider(final ContextKeySet contextKeySet) {
      return (new ContextFloatProviderArgument(contextKeySet)).toBrigadier();
   }

   public static ValidationWrapper<ContextFloatProvider> getFloatProvider(final CommandContext<?> context, final String name) {
      return getResource(context, name);
   }
}
