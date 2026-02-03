package net.minecraft.network.chat;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.List;
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
      String commandString = command.getReader().getString();
      CommandContextBuilder<S> rootContext = command.getContext();
      CommandContextBuilder<S> context = rootContext;

      List<Argument<S>> arguments;
      CommandContextBuilder<S> child;
      for(arguments = collectArguments(commandString, rootContext); (child = context.getChild()) != null && child.getRootNode() != rootContext.getRootNode(); context = child) {
         arguments.addAll(collectArguments(commandString, child));
      }

      return new SignableCommand<S>(arguments);
   }

   private static <S> List<Argument<S>> collectArguments(final String commandString, final CommandContextBuilder<S> context) {
      List<Argument<S>> arguments = new ArrayList();

      for(ParsedCommandNode<S> node : context.getNodes()) {
         CommandNode var6 = node.getNode();
         if (var6 instanceof ArgumentCommandNode<S, ?> argument) {
            if (argument.getType() instanceof SignedArgument) {
               ParsedArgument<S, ?> parsed = (ParsedArgument)context.getArguments().get(argument.getName());
               if (parsed != null) {
                  String value = parsed.getRange().get(commandString);
                  arguments.add(new Argument(argument, value));
               }
            }
         }
      }

      return arguments;
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
