package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.ParseContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.Validatable;

public abstract class ValidatableResourceOrIdArgument<T extends Validatable> extends ResourceOrIdArgument<T, ValidationWrapper<T>> implements ValidatableArgument {
   private final ContextKeySet contextKeySet;

   protected ValidatableResourceOrIdArgument(final ResourceKey<? extends Registry<T>> registryKey, final Codec<T> codec, final ContextKeySet contextKeySet) {
      super(registryKey, codec);
      this.contextKeySet = contextKeySet;
   }

   public abstract SerializableArgument.Type<? extends ValidatableResourceOrIdArgument<T>> type();

   public ContextKeySet contextKeySet() {
      return this.contextKeySet;
   }

   protected static <T extends Validatable> ValidationWrapper<T> getResource(final CommandContext<?> context, final String name) {
      return (ValidationWrapper)context.getArgument(name, ValidationWrapper.class);
   }

   protected ValidationWrapper<T> convert(final ParseContext context, final Holder<T> raw) throws CommandSyntaxException {
      return context.<T>getOrCreateValidationWrapper(raw, this.contextKeySet);
   }
}
