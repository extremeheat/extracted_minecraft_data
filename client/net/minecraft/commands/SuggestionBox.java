package net.minecraft.commands;

import com.google.gson.JsonElement;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.SerializableArgument;
import net.minecraft.commands.suggestions.CustomSuggestion;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.chat.SignableCommand;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.PermissionSetSupplier;
import org.jspecify.annotations.Nullable;

public class SuggestionBox extends CommandBox<SuggestionContext> {
   public static final Permission RESTRICTED_COMMAND = Permission.Atom.create("client/commands/restricted");
   public static final PermissionCheck RESTRICTED_COMMAND_CHECK;
   public static final PermissionSet ALLOW_RESTRICTED_COMMANDS;
   public static final SuggestionBox EMPTY;
   private static final ClientboundCommandsPacket.NodeInspector<PermissionSetSupplier> COMMAND_NODE_INSPECTOR;
   private static final ClientboundCommandsPacket.NodeBuilder<SuggestionContext> COMMAND_NODE_BUILDER;

   private SuggestionBox(final CommandDispatcher<SuggestionContext> dispatcher) {
      super(dispatcher);
   }

   public static SuggestionBox fromFullTree(final CommandDispatcher<CommandSourceStack> dispatcher) {
      return new SuggestionBox((new SuggestionNodeConverter()).convert(dispatcher));
   }

   public static SuggestionBox fromPacket(final ClientboundCommandsPacket packet) {
      return new SuggestionBox(new CommandDispatcher(packet.getRoot(COMMAND_NODE_BUILDER)));
   }

   public ClientboundCommandsPacket toPacket(final ServerPlayer player) {
      Map<CommandNode<SuggestionContext>, CommandNode<SuggestionContext>> playerCommands = new HashMap();
      RootCommandNode<SuggestionContext> root = new RootCommandNode();
      playerCommands.put(this.dispatcher.getRoot(), root);
      fillUsableCommands(this.dispatcher.getRoot(), root, new ServerPlayerSuggestionContext(player), playerCommands);
      return new ClientboundCommandsPacket(root, COMMAND_NODE_INSPECTOR);
   }

   private static <S> void fillUsableCommands(final CommandNode<S> source, final CommandNode<S> target, final S commandFilter, final Map<CommandNode<S>, CommandNode<S>> converted) {
      for(CommandNode<S> child : source.getChildren()) {
         if (child.canUse(commandFilter)) {
            ArgumentBuilder<S, ?> builder = child.createBuilder();
            if (builder.getRedirect() != null) {
               builder.redirect((CommandNode)converted.get(builder.getRedirect()));
            }

            CommandNode<S> node = builder.build();
            converted.put(child, node);
            target.addChild(node);
            if (!child.getChildren().isEmpty()) {
               fillUsableCommands(child, node, commandFilter, converted);
            }
         }
      }

   }

   public Result parse(final StringReader commandReader, final SuggestionContext context) {
      return new Result(this.parseWithContext(commandReader, context));
   }

   public Result parse(final String commandString, final SuggestionContext context) {
      return new Result(this.parseWithContext(commandString, context));
   }

   public CommandCheckResult verifyCommand(final String command, final SuggestionContext normalContext, final SuggestionContext restrictedContext) {
      ParseResults<SuggestionContext> parseWithCurrentPermissions = this.parseWithContext(command, normalContext);
      if (!isValidCommand(parseWithCurrentPermissions)) {
         return SuggestionBox.CommandCheckResult.PARSE_ERRORS;
      } else if (SignableCommand.requiresSignature(parseWithCurrentPermissions)) {
         return SuggestionBox.CommandCheckResult.SIGNATURE_REQUIRED;
      } else {
         ParseResults<SuggestionContext> parseWithoutPermissions = this.parseWithContext(command, restrictedContext);
         return !isValidCommand(parseWithoutPermissions) ? SuggestionBox.CommandCheckResult.PERMISSIONS_REQUIRED : SuggestionBox.CommandCheckResult.NO_ISSUES;
      }
   }

   private static boolean isValidCommand(final ParseResults<?> parseResults) {
      return !parseResults.getReader().canRead() && parseResults.getExceptions().isEmpty() && parseResults.getContext().getLastChild().getCommand() != null;
   }

   public JsonElement dumpReport() {
      return ArgumentUtils.serializeNodeToJson(this.dispatcher, this.dispatcher.getRoot());
   }

   static {
      RESTRICTED_COMMAND_CHECK = new PermissionCheck.Require(RESTRICTED_COMMAND);
      ALLOW_RESTRICTED_COMMANDS = (permission) -> permission.equals(RESTRICTED_COMMAND);
      EMPTY = new SuggestionBox(new CommandDispatcher());
      COMMAND_NODE_INSPECTOR = new ClientboundCommandsPacket.NodeInspector<PermissionSetSupplier>() {
         private final PermissionSetSupplier noPermissionSource = () -> PermissionSet.NO_PERMISSIONS;

         public boolean isExecutable(final CommandNode<? extends PermissionSetSupplier> node) {
            return node.getCommand() != null;
         }

         public boolean isRestricted(final CommandNode<? extends PermissionSetSupplier> node) {
            return !CommandBox.canNodeRun(node, this.noPermissionSource);
         }
      };
      COMMAND_NODE_BUILDER = new ClientboundCommandsPacket.NodeBuilder<SuggestionContext>() {
         public ArgumentBuilder<SuggestionContext, ?> createLiteral(final String id) {
            return LiteralArgumentBuilder.literal(id);
         }

         public ArgumentBuilder<SuggestionContext, ?> createArgument(final String id, final ArgumentType<?> argumentType, final @Nullable CustomSuggestion suggestion) {
            RequiredArgumentBuilder<SuggestionContext, ?> builder = RequiredArgumentBuilder.argument(id, argumentType);
            if (suggestion != null) {
               builder.suggests(suggestion.toBrigadier());
            }

            return builder;
         }

         public ArgumentBuilder<SuggestionContext, ?> configure(final ArgumentBuilder<SuggestionContext, ?> builder, final boolean executable, final boolean restricted) {
            if (executable) {
               builder.executes((var0) -> 0);
            }

            if (restricted) {
               builder.requires(Commands.hasPermission(SuggestionBox.RESTRICTED_COMMAND_CHECK));
            }

            return builder;
         }
      };
   }

   private static class SuggestionNodeConverter extends CommandBox.NodeConverter<CommandSourceStack, SuggestionContext> {
      private final Command<SuggestionContext> commandPlaceholder = (var0) -> {
         throw new IllegalStateException("Can't execute commands in suggestion mode");
      };
      private final RedirectModifier<SuggestionContext> redirectPlaceholder = (var0) -> {
         throw new IllegalStateException("Can't execute redirections in suggestion mode");
      };

      private SuggestionNodeConverter() {
         super();
      }

      protected Predicate<SuggestionContext> convert(final Predicate<CommandSourceStack> requirement) {
         return CommandBox.<SuggestionContext>convertNodePermissionCheck(requirement);
      }

      protected Command<SuggestionContext> convert(final Command<CommandSourceStack> command) {
         return this.commandPlaceholder;
      }

      protected RedirectModifier<SuggestionContext> convert(final RedirectModifier<CommandSourceStack> redirectModifier) {
         return this.redirectPlaceholder;
      }

      protected SuggestionProvider<SuggestionContext> convert(final SuggestionProvider<CommandSourceStack> customSuggestions) {
         return CustomSuggestion.unwrapOrThrow(customSuggestions).<SuggestionContext>cast();
      }

      protected ArgumentType<?> convert(final ArgumentType<?> type) {
         return SerializableArgument.unwrapOrThrow(type);
      }
   }

   public static enum CommandCheckResult {
      NO_ISSUES,
      PARSE_ERRORS,
      SIGNATURE_REQUIRED,
      PERMISSIONS_REQUIRED;

      private CommandCheckResult() {
      }

      // $FF: synthetic method
      private static CommandCheckResult[] $values() {
         return new CommandCheckResult[]{NO_ISSUES, PARSE_ERRORS, SIGNATURE_REQUIRED, PERMISSIONS_REQUIRED};
      }
   }

   public class Result {
      private final ParseResults<SuggestionContext> parseResults;

      private Result(final ParseResults<SuggestionContext> parseResults) {
         Objects.requireNonNull(SuggestionBox.this);
         super();
         this.parseResults = parseResults;
      }

      public boolean hasMessageArguments() {
         class Visitor implements ArgumentVisitor.Output<SuggestionContext> {
            private boolean foundMessageArgument;

            Visitor() {
               Objects.requireNonNull(Result.this);
               super();
            }

            public <T> void accept(final CommandContextBuilder<SuggestionContext> context, final ArgumentCommandNode<SuggestionContext, T> argument, final @Nullable ParsedArgument<SuggestionContext, T> value) {
               this.foundMessageArgument |= value != null && value.getResult() instanceof MessageArgument.Message;
            }
         }

         Visitor visitor = new Visitor();
         ArgumentVisitor.visitArguments(this.parseResults, visitor, false);
         return visitor.foundMessageArgument;
      }

      public CompletableFuture<Suggestions> computeSuggestions(final int cursorPosition) {
         return SuggestionBox.this.dispatcher.getCompletionSuggestions(this.parseResults, cursorPosition);
      }

      public CompletableFuture<Suggestions> computeSuggestions() {
         return SuggestionBox.this.dispatcher.getCompletionSuggestions(this.parseResults);
      }

      public ParseResults<SuggestionContext> parseResults() {
         return this.parseResults;
      }

      public SignableCommand<?> signables() {
         return SignableCommand.of(this.parseResults);
      }
   }
}
