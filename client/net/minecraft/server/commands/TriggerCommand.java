package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IntegerArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.commands.suggestions.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class TriggerCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_PRIMED = new SimpleCommandExceptionType(Component.translatable("commands.trigger.failed.unprimed"));
   private static final SimpleCommandExceptionType ERROR_INVALID_OBJECTIVE = new SimpleCommandExceptionType(Component.translatable("commands.trigger.failed.invalid"));
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_TRIGGERS = SuggestionProviders.serverOnly((server, context, builder) -> suggestTriggers(server, (SuggestionContext)context.getSource(), builder));

   public TriggerCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)Commands.literal("trigger").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective()).suggests(SUGGEST_TRIGGERS).executes((c) -> simpleTrigger((CommandSourceStack)c.getSource(), ((CommandSourceStack)c.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective(c, "objective")))).then(Commands.literal("add").then(Commands.argument("value", IntegerArgument.integer()).executes((c) -> addValue((CommandSourceStack)c.getSource(), ((CommandSourceStack)c.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective(c, "objective"), IntegerArgument.getInteger(c, "value")))))).then(Commands.literal("set").then(Commands.argument("value", IntegerArgument.integer()).executes((c) -> setValue((CommandSourceStack)c.getSource(), ((CommandSourceStack)c.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective(c, "objective"), IntegerArgument.getInteger(c, "value")))))));
   }

   public static CompletableFuture<Suggestions> suggestTriggers(final MinecraftServer server, final SuggestionContext source, final SuggestionsBuilder builder) {
      ScoreHolder entity = source.entity();
      if (entity != null) {
         Scoreboard scoreboard = server.getScoreboard();
         List<String> result = new ArrayList();

         for(Objective objective : scoreboard.getObjectives()) {
            if (objective.getCriteria() == ObjectiveCriteria.TRIGGER) {
               ReadOnlyScoreInfo scoreInfo = scoreboard.getPlayerScoreInfo(entity, objective);
               if (scoreInfo != null && !scoreInfo.isLocked()) {
                  result.add(objective.getName());
               }
            }
         }

         return SuggestionContext.suggest(result, builder);
      } else {
         return builder.buildFuture();
      }
   }

   private static int addValue(final CommandSourceStack source, final ServerPlayer player, final Objective objective, final int amount) throws CommandSyntaxException {
      ScoreAccess score = getScore(source.getServer().getScoreboard(), player, objective);
      int newValue = score.add(amount);
      source.sendSuccess(() -> Component.translatable("commands.trigger.add.success", objective.getFormattedDisplayName(), amount), true);
      return newValue;
   }

   private static int setValue(final CommandSourceStack source, final ServerPlayer player, final Objective objective, final int amount) throws CommandSyntaxException {
      ScoreAccess score = getScore(source.getServer().getScoreboard(), player, objective);
      score.set(amount);
      source.sendSuccess(() -> Component.translatable("commands.trigger.set.success", objective.getFormattedDisplayName(), amount), true);
      return amount;
   }

   private static int simpleTrigger(final CommandSourceStack source, final ServerPlayer player, final Objective objective) throws CommandSyntaxException {
      ScoreAccess score = getScore(source.getServer().getScoreboard(), player, objective);
      int newValue = score.add(1);
      source.sendSuccess(() -> Component.translatable("commands.trigger.simple.success", objective.getFormattedDisplayName()), true);
      return newValue;
   }

   private static ScoreAccess getScore(final Scoreboard scoreboard, final ScoreHolder scoreHolder, final Objective objective) throws CommandSyntaxException {
      if (objective.getCriteria() != ObjectiveCriteria.TRIGGER) {
         throw ERROR_INVALID_OBJECTIVE.create();
      } else {
         ReadOnlyScoreInfo scoreInfo = scoreboard.getPlayerScoreInfo(scoreHolder, objective);
         if (scoreInfo != null && !scoreInfo.isLocked()) {
            ScoreAccess score = scoreboard.getOrCreatePlayerScore(scoreHolder, objective);
            score.lock();
            return score;
         } else {
            throw ERROR_NOT_PRIMED.create();
         }
      }
   }
}
