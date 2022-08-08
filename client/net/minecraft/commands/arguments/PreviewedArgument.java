package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public interface PreviewedArgument<T> extends ArgumentType<T> {
   @Nullable
   static CompletableFuture<Component> resolvePreviewed(ArgumentCommandNode<?, ?> var0, CommandContextBuilder<CommandSourceStack> var1) throws CommandSyntaxException {
      ArgumentType var3 = var0.getType();
      if (var3 instanceof PreviewedArgument var2) {
         return var2.resolvePreview(var1, var0.getName());
      } else {
         return null;
      }
   }

   static boolean isPreviewed(CommandNode<?> var0) {
      boolean var10000;
      if (var0 instanceof ArgumentCommandNode var1) {
         if (var1.getType() instanceof PreviewedArgument) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   @Nullable
   default CompletableFuture<Component> resolvePreview(CommandContextBuilder<CommandSourceStack> var1, String var2) throws CommandSyntaxException {
      ParsedArgument var3 = (ParsedArgument)var1.getArguments().get(var2);
      return var3 != null && this.getValueType().isInstance(var3.getResult()) ? this.resolvePreview((CommandSourceStack)var1.getSource(), this.getValueType().cast(var3.getResult())) : null;
   }

   CompletableFuture<Component> resolvePreview(CommandSourceStack var1, T var2) throws CommandSyntaxException;

   Class<T> getValueType();
}
