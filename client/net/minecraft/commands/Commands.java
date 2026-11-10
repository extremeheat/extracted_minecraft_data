package net.minecraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.arguments.ValidationWrapper;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.gametest.framework.TestCommand;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.server.commands.AttributeCommand;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.commands.BanListCommands;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.commands.BossBarCommands;
import net.minecraft.server.commands.ChaseCommand;
import net.minecraft.server.commands.ClearInventoryCommands;
import net.minecraft.server.commands.CloneCommands;
import net.minecraft.server.commands.ComputeCommand;
import net.minecraft.server.commands.DamageCommand;
import net.minecraft.server.commands.DataPackCommand;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.commands.DebugCommand;
import net.minecraft.server.commands.DebugConfigCommand;
import net.minecraft.server.commands.DebugMobSpawningCommand;
import net.minecraft.server.commands.DebugPathCommand;
import net.minecraft.server.commands.DefaultGameModeCommands;
import net.minecraft.server.commands.DialogCommand;
import net.minecraft.server.commands.DifficultyCommand;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.server.commands.EmoteCommands;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.server.commands.EnvironmentAttributeCommand;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.commands.ExperienceCommand;
import net.minecraft.server.commands.FetchProfileCommand;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.server.commands.ForceLoadCommand;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.commands.HelpCommand;
import net.minecraft.server.commands.JfrCommand;
import net.minecraft.server.commands.KickCommand;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.server.commands.ListPlayersCommand;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.commands.OpCommand;
import net.minecraft.server.commands.PardonCommand;
import net.minecraft.server.commands.PardonIpCommand;
import net.minecraft.server.commands.ParticleCommand;
import net.minecraft.server.commands.PerfCommand;
import net.minecraft.server.commands.PlaceCommand;
import net.minecraft.server.commands.PlaySoundCommand;
import net.minecraft.server.commands.PostEffectCommand;
import net.minecraft.server.commands.PublishCommand;
import net.minecraft.server.commands.RaidCommand;
import net.minecraft.server.commands.RandomCommand;
import net.minecraft.server.commands.RecipeCommand;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.commands.ReturnCommand;
import net.minecraft.server.commands.RideCommand;
import net.minecraft.server.commands.RotateCommand;
import net.minecraft.server.commands.SaveAllCommand;
import net.minecraft.server.commands.SaveOffCommand;
import net.minecraft.server.commands.SaveOnCommand;
import net.minecraft.server.commands.SayCommand;
import net.minecraft.server.commands.ScheduleCommand;
import net.minecraft.server.commands.ScoreboardCommand;
import net.minecraft.server.commands.SeedCommand;
import net.minecraft.server.commands.ServerPackCommand;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.commands.SetPlayerIdleTimeoutCommand;
import net.minecraft.server.commands.SetSpawnCommand;
import net.minecraft.server.commands.SetWorldSpawnCommand;
import net.minecraft.server.commands.SpawnArmorTrimsCommand;
import net.minecraft.server.commands.SpectateCommand;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.commands.StopCommand;
import net.minecraft.server.commands.StopSoundCommand;
import net.minecraft.server.commands.StopwatchCommand;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.commands.SwingCommand;
import net.minecraft.server.commands.TagCommand;
import net.minecraft.server.commands.TeamCommand;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.commands.TellRawCommand;
import net.minecraft.server.commands.TickCommand;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.commands.TitleCommand;
import net.minecraft.server.commands.TransferCommand;
import net.minecraft.server.commands.TriggerCommand;
import net.minecraft.server.commands.UnpublishCommand;
import net.minecraft.server.commands.VersionCommand;
import net.minecraft.server.commands.WardenSpawnTrackerCommand;
import net.minecraft.server.commands.WaypointCommand;
import net.minecraft.server.commands.WeatherCommand;
import net.minecraft.server.commands.WhitelistCommand;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.commands.item.ItemCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.PermissionProviderCheck;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.PermissionSetSupplier;
import net.minecraft.util.DevUtil;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class Commands {
   public static final String COMMAND_PREFIX = "/";
   private static final ThreadLocal<@Nullable ExecutionContext<CommandSourceStack>> CURRENT_EXECUTION_CONTEXT = new ThreadLocal();
   private static final Logger LOGGER = LogUtils.getLogger();
   private final SuggestionBox suggestionBox;
   private final ParseBox parseBox;

   public Commands(final Consumer<CommandDispatcher<CommandSourceStack>> commandRegistration) {
      super();
      CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher();
      commandRegistration.accept(dispatcher);
      dispatcher.setConsumer(ExecutionCommandSource.resultConsumer());
      this.suggestionBox = SuggestionBox.fromFullTree(dispatcher);
      this.parseBox = ParseBox.fromFullTree(dispatcher);
   }

   public static Commands createVanilla(final CommandSelection commandSelection, final CommandBuildContext context) {
      return new Commands((dispatcher) -> registerVanillaCommands(dispatcher, commandSelection, context));
   }

   public static void registerVanillaCommands(final CommandDispatcher<CommandSourceStack> dispatcher, final CommandSelection commandSelection, final CommandBuildContext context) {
      AdvancementCommands.register(dispatcher);
      AttributeCommand.register(dispatcher);
      ExecuteCommand.register(dispatcher);
      BossBarCommands.register(dispatcher);
      ClearInventoryCommands.register(dispatcher);
      CloneCommands.register(dispatcher);
      ComputeCommand.register(dispatcher);
      DamageCommand.register(dispatcher);
      DataCommands.register(dispatcher);
      DataPackCommand.register(dispatcher);
      DebugCommand.register(dispatcher);
      DefaultGameModeCommands.register(dispatcher);
      DialogCommand.register(dispatcher);
      DifficultyCommand.register(dispatcher);
      EffectCommands.register(dispatcher);
      EmoteCommands.register(dispatcher);
      EnchantCommand.register(dispatcher);
      if (SharedConstants.DEBUG_ENABLED || DevUtil.IS_RUNNING_IN_IDE) {
         EnvironmentAttributeCommand.register(dispatcher);
      }

      ExperienceCommand.register(dispatcher);
      FillCommand.register(dispatcher);
      FillBiomeCommand.register(dispatcher);
      ForceLoadCommand.register(dispatcher);
      FunctionCommand.register(dispatcher);
      GameModeCommand.register(dispatcher);
      GameRuleCommand.register(dispatcher, context);
      GiveCommand.register(dispatcher);
      HelpCommand.register(dispatcher);
      ItemCommands.register(dispatcher);
      KickCommand.register(dispatcher);
      KillCommand.register(dispatcher);
      ListPlayersCommand.register(dispatcher);
      LocateCommand.register(dispatcher);
      LootCommand.register(dispatcher);
      MsgCommand.register(dispatcher);
      SwingCommand.register(dispatcher);
      ParticleCommand.register(dispatcher);
      PlaceCommand.register(dispatcher);
      PlaySoundCommand.register(dispatcher);
      PostEffectCommand.register(dispatcher);
      RandomCommand.register(dispatcher);
      ReloadCommand.register(dispatcher);
      RecipeCommand.register(dispatcher);
      FetchProfileCommand.register(dispatcher);
      ReturnCommand.register(dispatcher);
      RideCommand.register(dispatcher);
      RotateCommand.register(dispatcher);
      SayCommand.register(dispatcher);
      ScheduleCommand.register(dispatcher);
      ScoreboardCommand.register(dispatcher);
      SeedCommand.register(dispatcher, commandSelection != Commands.CommandSelection.INTEGRATED);
      VersionCommand.register(dispatcher, commandSelection != Commands.CommandSelection.INTEGRATED);
      SetBlockCommand.register(dispatcher);
      SetSpawnCommand.register(dispatcher);
      SetWorldSpawnCommand.register(dispatcher);
      SpectateCommand.register(dispatcher);
      SpreadPlayersCommand.register(dispatcher);
      StopSoundCommand.register(dispatcher);
      StopwatchCommand.register(dispatcher);
      SummonCommand.register(dispatcher);
      TagCommand.register(dispatcher);
      TeamCommand.register(dispatcher);
      TeamMsgCommand.register(dispatcher);
      TeleportCommand.register(dispatcher);
      TellRawCommand.register(dispatcher);
      TestCommand.register(dispatcher);
      TickCommand.register(dispatcher);
      TimeCommand.register(dispatcher);
      TitleCommand.register(dispatcher);
      TriggerCommand.register(dispatcher);
      WaypointCommand.register(dispatcher);
      WeatherCommand.register(dispatcher);
      WorldBorderCommand.register(dispatcher);
      if (JvmProfiler.INSTANCE.isAvailable()) {
         JfrCommand.register(dispatcher);
      }

      if (SharedConstants.DEBUG_CHASE_COMMAND) {
         ChaseCommand.register(dispatcher);
      }

      if (SharedConstants.DEBUG_DEV_COMMANDS || DevUtil.IS_RUNNING_IN_IDE) {
         RaidCommand.register(dispatcher);
         DebugPathCommand.register(dispatcher);
         DebugMobSpawningCommand.register(dispatcher);
         WardenSpawnTrackerCommand.register(dispatcher);
         SpawnArmorTrimsCommand.register(dispatcher);
         ServerPackCommand.register(dispatcher);
         if (commandSelection.includeDedicated) {
            DebugConfigCommand.register(dispatcher);
         }
      }

      if (commandSelection.includeDedicated) {
         BanIpCommands.register(dispatcher);
         BanListCommands.register(dispatcher);
         BanPlayerCommands.register(dispatcher);
         DeOpCommands.register(dispatcher);
         OpCommand.register(dispatcher);
         PardonCommand.register(dispatcher);
         PardonIpCommand.register(dispatcher);
         PerfCommand.register(dispatcher);
         SaveAllCommand.register(dispatcher);
         SaveOffCommand.register(dispatcher);
         SaveOnCommand.register(dispatcher);
         SetPlayerIdleTimeoutCommand.register(dispatcher);
         StopCommand.register(dispatcher);
         TransferCommand.register(dispatcher);
         WhitelistCommand.register(dispatcher);
      }

      if (commandSelection.includeIntegrated) {
         PublishCommand.register(dispatcher);
         UnpublishCommand.register(dispatcher);
      }

   }

   public void performPrefixedCommand(final CommandSourceStack sender, String command) {
      command = trimOptionalPrefix(command);
      performCommand(sender, this.parseBox.parse(command, ParseContext.of(sender)), command);
   }

   public static String trimOptionalPrefix(final String command) {
      return command.startsWith("/") ? command.substring(1) : command;
   }

   public static void performCommand(final CommandSourceStack executionContext, final ParseBox.Result command, final String commandString) {
      Profiler.get().push((Supplier)(() -> "/" + commandString));
      ContextChain<CommandSourceStack> commandChain = finishParsing(command, executionContext);

      try {
         if (commandChain != null) {
            executeCommandInContext(executionContext, (ec) -> ExecutionContext.queueInitialCommandExecution(ec, commandString, commandChain, executionContext, CommandResultCallback.EMPTY));
         }
      } catch (Exception var11) {
         MutableComponent hover = Component.literal(var11.getMessage() == null ? var11.getClass().getName() : var11.getMessage());
         if (LOGGER.isDebugEnabled()) {
            LOGGER.error("Command exception: /{}", commandString, var11);
            StackTraceElement[] stackTrace = var11.getStackTrace();

            for(int i = 0; i < Math.min(stackTrace.length, 3); ++i) {
               hover.append("\n\n").append(stackTrace[i].getMethodName()).append("\n ").append(stackTrace[i].getFileName()).append(":").append(String.valueOf(stackTrace[i].getLineNumber()));
            }
         }

         executionContext.sendFailure(Component.translatable("command.failed").withStyle((UnaryOperator)((s) -> s.withHoverEvent(new HoverEvent.ShowText(hover)))));
         if (SharedConstants.DEBUG_VERBOSE_COMMAND_ERRORS || DevUtil.IS_RUNNING_IN_IDE) {
            executionContext.sendFailure(Component.literal(Util.describeError(var11)));
            LOGGER.error("'/{}' threw an exception", commandString, var11);
         }
      } finally {
         Profiler.get().pop();
      }

   }

   private static @Nullable ContextChain<CommandSourceStack> finishParsing(final ParseBox.Result command, final CommandSourceStack sender) {
      try {
         return command.unpack();
      } catch (CommandSyntaxException e) {
         sender.sendFailure(ComponentUtils.fromMessage(e.getRawMessage()));
         if (e.getInput() != null && e.getCursor() >= 0) {
            int cursor = Math.min(e.getInput().length(), e.getCursor());
            MutableComponent context = Component.empty().withStyle(ChatFormatting.GRAY).withStyle((UnaryOperator)((s) -> s.withClickEvent(new ClickEvent.SuggestCommand("/" + command.commandString()))));
            if (cursor > 10) {
               context.append(CommonComponents.ELLIPSIS);
            }

            context.append(e.getInput().substring(Math.max(0, cursor - 10), cursor));
            if (cursor < e.getInput().length()) {
               Component remaining = Component.literal(e.getInput().substring(cursor)).withStyle(ChatFormatting.RED, ChatFormatting.UNDERLINE);
               context.append(remaining);
            }

            context.append((Component)Component.translatable("command.context.here").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
            sender.sendFailure(context);
         }

         return null;
      }
   }

   public static void executeCommandInContext(final CommandSourceStack context, final Consumer<ExecutionContext<CommandSourceStack>> config) {
      ExecutionContext<CommandSourceStack> currentContext = (ExecutionContext)CURRENT_EXECUTION_CONTEXT.get();
      boolean isTopContext = currentContext == null;
      if (isTopContext) {
         GameRules gameRules = context.getLevel().getGameRules();
         int chainLimit = Math.max(1, (Integer)gameRules.get(GameRules.MAX_COMMAND_SEQUENCE_LENGTH));
         int forkLimit = (Integer)gameRules.get(GameRules.MAX_COMMAND_FORKS);

         try {
            ExecutionContext<CommandSourceStack> executionContext = new ExecutionContext<CommandSourceStack>(chainLimit, forkLimit, Profiler.get());

            try {
               CURRENT_EXECUTION_CONTEXT.set(executionContext);
               config.accept(executionContext);
               executionContext.runCommandQueue();
            } catch (Throwable var15) {
               try {
                  executionContext.close();
               } catch (Throwable var14) {
                  var15.addSuppressed(var14);
               }

               throw var15;
            }

            executionContext.close();
         } finally {
            CURRENT_EXECUTION_CONTEXT.set((Object)null);
         }
      } else {
         config.accept(currentContext);
      }

   }

   public void sendCommands(final ServerPlayer player) {
      player.connection.send(this.suggestionBox.toPacket(player));
   }

   public static LiteralArgumentBuilder<CommandSourceStack> literal(final String literal) {
      return LiteralArgumentBuilder.literal(literal);
   }

   public static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(final String name, final ArgumentType<T> type) {
      return RequiredArgumentBuilder.argument(name, type);
   }

   public static Predicate<String> createValidator(final ParseFunction parser) {
      return (value) -> {
         try {
            parser.parse(new StringReader(value));
            return true;
         } catch (CommandSyntaxException var3) {
            return false;
         }
      };
   }

   public SuggestionBox suggestionBox() {
      return this.suggestionBox;
   }

   public ParseBox parseBox() {
      return this.parseBox;
   }

   public static void validateParseResults(final ParseResults<?> command) throws CommandSyntaxException {
      CommandSyntaxException parseException = getParseException(command);
      if (parseException != null) {
         throw parseException;
      }
   }

   public static @Nullable CommandSyntaxException getParseException(final ParseResults<?> parse) {
      if (!parse.getReader().canRead()) {
         return null;
      } else if (parse.getExceptions().size() == 1) {
         return (CommandSyntaxException)parse.getExceptions().values().iterator().next();
      } else {
         return parse.getContext().getRange().isEmpty() ? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader()) : CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parse.getReader());
      }
   }

   public static CommandBuildContext createValidationContext() {
      return CommandBuildContext.simple(FeatureFlags.REGISTRY.allFlags());
   }

   public static void validate() {
      CommandBuildContext buildContext = createValidationContext();
      ParseBox parseBox = createVanilla(Commands.CommandSelection.ALL, buildContext).parseBox();
      ParseContext.Simple parseContext = new ParseContext.Simple(PermissionSet.ALL_PERMISSIONS, PlaceholderRegistries.create(RegistryDataLoader.allKnownRegistries(), PlaceholderRegistries.PlaceholderBehavior.REJECT_ALL), FeatureFlags.REGISTRY.allFlags(), ValidationWrapper.Provider.LAZY);
      parseBox.findAmbiguities(parseContext);
   }

   public static <T extends PermissionSetSupplier> PermissionProviderCheck<T> hasPermission(final PermissionCheck permission) {
      return new PermissionProviderCheck<T>(permission);
   }

   public static enum CommandSelection {
      ALL(true, true),
      DEDICATED(false, true),
      INTEGRATED(true, false);

      private final boolean includeIntegrated;
      private final boolean includeDedicated;

      private CommandSelection(final boolean includeIntegrated, final boolean includeDedicated) {
         this.includeIntegrated = includeIntegrated;
         this.includeDedicated = includeDedicated;
      }

      // $FF: synthetic method
      private static CommandSelection[] $values() {
         return new CommandSelection[]{ALL, DEDICATED, INTEGRATED};
      }
   }

   @FunctionalInterface
   public interface ParseFunction {
      void parse(StringReader value) throws CommandSyntaxException;
   }
}
