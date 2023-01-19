package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public interface PreviewedArgument<T> extends ArgumentType<T> {
   @Nullable
   default CompletableFuture<Component> resolvePreview(CommandSourceStack var1, ParsedArgument<CommandSourceStack, ?> var2) throws CommandSyntaxException {
      return this.getValueType().isInstance(var2.getResult()) ? this.resolvePreview(var1, this.getValueType().cast(var2.getResult())) : null;
   }

   CompletableFuture<Component> resolvePreview(CommandSourceStack var1, T var2) throws CommandSyntaxException;

   Class<T> getValueType();
}
