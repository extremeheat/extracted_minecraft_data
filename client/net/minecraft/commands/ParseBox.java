package net.minecraft.commands;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.network.chat.SignableCommand;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ParseBox extends CommandBox<ParseContext> {
   private static final Logger LOGGER = LogUtils.getLogger();

   private ParseBox(final CommandDispatcher<ParseContext> dispatcher) {
      super(dispatcher);
   }

   public static ParseBox fromFullTree(final CommandDispatcher<CommandSourceStack> dispatcher) {
      return new ParseBox((new ParseNodeConverter()).convert(dispatcher));
   }

   public void findAmbiguities(final ParseContext context) {
      runWithParseContext(() -> this.dispatcher.findAmbiguities((var1, child, sibling, ambiguities) -> LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", new Object[]{this.dispatcher.getPath(child), this.dispatcher.getPath(sibling), ambiguities})), context);
   }

   public Result parse(final String commandString, final ParseContext context) {
      return new Result(commandString, this.parseWithContext(commandString, context));
   }

   public Result parse(final StringReader commandReader, final ParseContext context) {
      return new Result(commandReader.getString(), this.parseWithContext(commandReader, context));
   }

   public Map<CommandNode<ParseContext>, String> getSmartUsage(final Result result, final ParseContext parseContext) {
      return this.getSmartUsage(((ParsedCommandNode)Iterables.getLast(result.parseResults.getContext().getNodes())).getNode(), parseContext);
   }

   private static class ParseNodeConverter extends CommandBox.NodeConverter<CommandSourceStack, ParseContext> {
      private ParseNodeConverter() {
         super();
      }

      protected Predicate<ParseContext> convert(final Predicate<CommandSourceStack> requirement) {
         return CommandBox.<ParseContext>convertNodePermissionCheck(requirement);
      }

      protected Command<ParseContext> convert(final Command<CommandSourceStack> command) {
         return loosen(command);
      }

      protected RedirectModifier<ParseContext> convert(final RedirectModifier<CommandSourceStack> redirectModifier) {
         return loosen(redirectModifier);
      }

      protected @Nullable SuggestionProvider<ParseContext> convert(final SuggestionProvider<CommandSourceStack> customSuggestions) {
         return null;
      }
   }

   public static class Result {
      private final String commandString;
      private final ParseResults<ParseContext> parseResults;

      public Result(final String commandString, final ParseResults<ParseContext> parseResults) {
         super();
         this.commandString = commandString;
         this.parseResults = parseResults;
      }

      public String commandString() {
         return this.commandString;
      }

      public ContextChain<CommandSourceStack> unpack() throws CommandSyntaxException {
         Commands.validateParseResults(this.parseResults);
         CommandContext<ParseContext> parsedContext = this.parseResults.getContext().build(this.commandString);
         return (ContextChain)ContextChain.tryFlatten(parsedContext).orElseThrow(() -> CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(this.parseResults.getReader()));
      }

      public SignableCommand<?> signables() {
         return SignableCommand.of(this.parseResults);
      }

      public boolean isEmpty() {
         return this.parseResults.getContext().getNodes().isEmpty();
      }
   }
}
