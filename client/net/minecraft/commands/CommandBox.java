package net.minecraft.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.server.permissions.PermissionProviderCheck;
import net.minecraft.server.permissions.PermissionSetSupplier;
import org.jspecify.annotations.Nullable;

public class CommandBox<C extends ParseContext> {
   private static final ScopedValue<ParseContext> CONTEXT = ScopedValue.newInstance();
   private static final Predicate<?> DEFAULT_ARGUMENT_PREDICATE = Commands.literal("test").getRequirement();
   protected final CommandDispatcher<C> dispatcher;

   protected CommandBox(final CommandDispatcher<C> dispatcher) {
      super();
      this.dispatcher = dispatcher;
   }

   protected static boolean canNodeRun(final CommandNode<? extends PermissionSetSupplier> node, final PermissionSetSupplier context) {
      if (node instanceof RootCommandNode) {
         return true;
      } else {
         Predicate<? extends PermissionSetSupplier> requirement = node.getRequirement();
         if (requirement instanceof PermissionProviderCheck) {
            PermissionProviderCheck<?> check = (PermissionProviderCheck)requirement;
            return check.test(context);
         } else {
            throw new UnsupportedOperationException("Unsupported predicate: " + String.valueOf(requirement) + ", use PermissionProviderCheck instead");
         }
      }
   }

   protected static <T extends PermissionSetSupplier> Predicate<T> convertNodePermissionCheck(final Predicate<? extends PermissionSetSupplier> original) {
      if (original instanceof PermissionProviderCheck<?> check) {
         return check.<T>cast();
      } else if (original == DEFAULT_ARGUMENT_PREDICATE) {
         return PermissionProviderCheck.ALWAYS_PASS.<T>cast();
      } else {
         throw new UnsupportedOperationException("Unsupported predicate: " + String.valueOf(original) + ", use PermissionProviderCheck instead");
      }
   }

   protected static <T> T runWithParseContext(final ScopedValue.CallableOp<T, RuntimeException> action, final ParseContext context) {
      return (T)ScopedValue.where(CONTEXT, context).call(action);
   }

   protected static void runWithParseContext(final Runnable action, final ParseContext context) {
      ScopedValue.where(CONTEXT, context).run(action);
   }

   public static ParseContext grabParseContext() {
      return (ParseContext)CONTEXT.get();
   }

   protected ParseResults<C> parseWithContext(final String commandString, final C context) {
      return (ParseResults)runWithParseContext((ScopedValue.CallableOp)(() -> this.dispatcher.parse(commandString, context)), context);
   }

   protected ParseResults<C> parseWithContext(final StringReader commandString, final C context) {
      return (ParseResults)runWithParseContext((ScopedValue.CallableOp)(() -> this.dispatcher.parse(commandString, context)), context);
   }

   public Map<CommandNode<C>, String> getSmartUsage(final CommandNode<? extends C> node, final C context) {
      return this.dispatcher.getSmartUsage(node, context);
   }

   public abstract static class NodeConverter<I extends PermissionSetSupplier, O extends PermissionSetSupplier> {
      private final Map<CommandNode<I>, CommandNode<O>> clonedNodes = new IdentityHashMap();
      private final List<CommandNode<I>> postponedNodes = new ArrayList();

      public NodeConverter() {
         super();
      }

      public CommandDispatcher<O> convert(final CommandDispatcher<I> input) {
         return new CommandDispatcher(this.convert(input.getRoot()));
      }

      public RootCommandNode<O> convert(final RootCommandNode<I> input) {
         RootCommandNode<O> output = new RootCommandNode();
         this.clonedNodes.put(input, output);

         for(CommandNode<I> child : input.getChildren()) {
            this.discoverNodes(child);
         }

         for(CommandNode<I> postponedNode : this.postponedNodes) {
            this.resolvePostponedNodes(postponedNode);
         }

         this.clonedNodes.forEach(this::linkNodes);
         return output;
      }

      private void discoverNodes(final CommandNode<I> input) {
         CommandNode<I> redirect = input.getRedirect();

         for(CommandNode<I> child : input.getChildren()) {
            this.discoverNodes(child);
         }

         CommandNode<O> convertedRedirect = null;
         if (redirect != null) {
            convertedRedirect = (CommandNode)this.clonedNodes.get(redirect);
            if (convertedRedirect == null) {
               this.postponedNodes.add(input);
               return;
            }
         }

         this.cloneNode(input, convertedRedirect);
      }

      private void resolvePostponedNodes(final CommandNode<I> input) {
         CommandNode<O> convertedRedirect = (CommandNode)this.clonedNodes.get(input.getRedirect());
         if (convertedRedirect == null) {
            throw new UnsupportedOperationException("Double redirect or hidden subtree detected");
         } else {
            this.cloneNode(input, convertedRedirect);
         }
      }

      private void cloneNode(final CommandNode<I> input, final @Nullable CommandNode<O> convertedRedirect) {
         ArgumentBuilder<O, ?> output = this.convertTypeSpecific(input);
         Predicate<O> convertedPredicate = this.convert(input.getRequirement());
         if (convertedPredicate != null) {
            output.requires(convertedPredicate);
         }

         Command<I> command = input.getCommand();
         if (command != null) {
            output.executes(this.convert(command));
         }

         if (convertedRedirect != null) {
            RedirectModifier<I> redirectModifier = input.getRedirectModifier();
            RedirectModifier<O> convertedRedirectModifier = redirectModifier != null ? this.convert(redirectModifier) : null;
            output.forward(convertedRedirect, convertedRedirectModifier, input.isFork());
         }

         this.clonedNodes.put(input, output.build());
      }

      private void linkNodes(final CommandNode<I> input, final CommandNode<O> output) {
         for(CommandNode<I> child : input.getChildren()) {
            CommandNode<O> clonedChild = (CommandNode)this.clonedNodes.get(child);
            if (clonedChild == null) {
               throw new IllegalStateException();
            }

            output.addChild(clonedChild);
         }

      }

      private ArgumentBuilder<O, ?> convertTypeSpecific(final CommandNode<I> input) {
         Objects.requireNonNull(input);
         byte var3 = 0;
         Object var10000;
         //$FF: var3->value
         //0->com/mojang/brigadier/tree/ArgumentCommandNode
         //1->com/mojang/brigadier/tree/LiteralCommandNode
         switch (input.typeSwitch<invokedynamic>(input, var3)) {
            case 0:
               ArgumentCommandNode<I, ?> argument = (ArgumentCommandNode)input;
               RequiredArgumentBuilder<O, ?> result = RequiredArgumentBuilder.argument(argument.getName(), this.convert(argument.getType()));
               SuggestionProvider<I> customSuggestions = argument.getCustomSuggestions();
               if (customSuggestions != null) {
                  result.suggests(this.convert(customSuggestions));
               }

               var10000 = result;
               break;
            case 1:
               LiteralCommandNode<I> literal = (LiteralCommandNode)input;
               var10000 = LiteralArgumentBuilder.literal(literal.getName());
               break;
            default:
               throw new UnsupportedOperationException("Unknown node " + String.valueOf(input));
         }

         return (ArgumentBuilder<O, ?>)var10000;
      }

      protected abstract @Nullable Predicate<O> convert(Predicate<I> requirement);

      protected abstract @Nullable Command<O> convert(Command<I> command);

      protected abstract @Nullable RedirectModifier<O> convert(RedirectModifier<I> redirectModifier);

      protected abstract @Nullable SuggestionProvider<O> convert(SuggestionProvider<I> customSuggestions);

      protected ArgumentType<?> convert(final ArgumentType<?> type) {
         return type;
      }

      protected static Command<?> loosen(final Command<?> input) {
         return input;
      }

      protected static RedirectModifier<?> loosen(final RedirectModifier<?> input) {
         return input;
      }
   }
}
