package net.minecraft.network.chat;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.ArgumentVisitor;
import net.minecraft.commands.arguments.SignedArgument;
import org.jspecify.annotations.Nullable;

public record SignableCommand<S>(List<Argument<S>> arguments) {
   public SignableCommand {
      super();
   }

   public static <S> boolean hasSignableArguments(final ParseResults<S> command) {
      return !of(command).arguments().isEmpty();
   }

   public static <S> SignableCommand<S> of(final ParseResults<S> command) {
      final String commandString = command.getReader().getString();
      final List<Argument<S>> arguments = new ArrayList();
      ArgumentVisitor.visitArguments(command, new ArgumentVisitor.Output<S>() {
         public <T> void accept(final CommandContextBuilder<S> context, final ArgumentCommandNode<S, T> argument, final @Nullable ParsedArgument<S, T> value) {
            if (value != null && argument.getType() instanceof SignedArgument) {
               String stringValue = value.getRange().get(commandString);
               arguments.add(new Argument(argument, stringValue));
            }

         }
      }, true);
      return new SignableCommand<S>(arguments);
   }

   public @Nullable Argument<S> getArgument(final String name) {
      for(Argument<S> argument : this.arguments) {
         if (name.equals(argument.name())) {
            return argument;
         }
      }

      return null;
   }

   public static record Argument<S>(ArgumentCommandNode<S, ?> node, String value) {
      public Argument {
         super();
      }

      public String name() {
         return this.node.getName();
      }
   }
}
