package net.minecraft.commands;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class ArgumentVisitor {
   public ArgumentVisitor() {
      super();
   }

   public static <S> void visitArguments(final ParseResults<S> command, final Output<S> output, final boolean rejectRootRedirects) {
      CommandContextBuilder<S> rootContext = command.getContext();
      CommandContextBuilder<S> context = rootContext;
      visitNodeArguments(rootContext, output);

      CommandContextBuilder<S> child;
      while((child = context.getChild()) != null && (!rejectRootRedirects || child.getRootNode() != rootContext.getRootNode())) {
         visitNodeArguments(child, output);
         context = child;
      }

   }

   private static <S> void visitNodeArguments(final CommandContextBuilder<S> context, final Output<S> output) {
      Map<String, ParsedArgument<S, ?>> values = context.getArguments();

      for(ParsedCommandNode<S> node : context.getNodes()) {
         CommandNode var6 = node.getNode();
         if (var6 instanceof ArgumentCommandNode<S, ?> argument) {
            ParsedArgument<S, ?> value = (ParsedArgument)values.get(argument.getName());
            callVisitor(context, output, argument, value);
         }
      }

   }

   private static <S, T> void callVisitor(final CommandContextBuilder<S> context, final Output<S> output, final ArgumentCommandNode<S, T> argument, final @Nullable ParsedArgument<S, ?> value) {
      output.accept(context, argument, value);
   }

   @FunctionalInterface
   public interface Output<S> {
      <T> void accept(CommandContextBuilder<S> context, ArgumentCommandNode<S, T> argument, final @Nullable ParsedArgument<S, T> value);
   }
}
