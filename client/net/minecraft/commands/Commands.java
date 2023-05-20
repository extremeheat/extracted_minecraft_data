package net.minecraft.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.gametest.framework.TestCommand;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.server.commands.AttributeCommand;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.commands.BanListCommands;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.commands.BossBarCommands;
import net.minecraft.server.commands.ClearInventoryCommands;
import net.minecraft.server.commands.CloneCommands;
import net.minecraft.server.commands.DamageCommand;
import net.minecraft.server.commands.DataPackCommand;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.commands.DebugCommand;
import net.minecraft.server.commands.DefaultGameModeCommands;
import net.minecraft.server.commands.DifficultyCommand;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.server.commands.EmoteCommands;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.commands.ExperienceCommand;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.server.commands.ForceLoadCommand;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.commands.HelpCommand;
import net.minecraft.server.commands.ItemCommands;
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
import net.minecraft.server.commands.PublishCommand;
import net.minecraft.server.commands.RecipeCommand;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.commands.RideCommand;
import net.minecraft.server.commands.SaveAllCommand;
import net.minecraft.server.commands.SaveOffCommand;
import net.minecraft.server.commands.SaveOnCommand;
import net.minecraft.server.commands.SayCommand;
import net.minecraft.server.commands.ScheduleCommand;
import net.minecraft.server.commands.ScoreboardCommand;
import net.minecraft.server.commands.SeedCommand;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.commands.SetPlayerIdleTimeoutCommand;
import net.minecraft.server.commands.SetSpawnCommand;
import net.minecraft.server.commands.SetWorldSpawnCommand;
import net.minecraft.server.commands.SpawnArmorTrimsCommand;
import net.minecraft.server.commands.SpectateCommand;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.commands.StopCommand;
import net.minecraft.server.commands.StopSoundCommand;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.commands.TagCommand;
import net.minecraft.server.commands.TeamCommand;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.commands.TellRawCommand;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.commands.TitleCommand;
import net.minecraft.server.commands.TriggerCommand;
import net.minecraft.server.commands.WeatherCommand;
import net.minecraft.server.commands.WhitelistCommand;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class Commands {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int LEVEL_ALL = 0;
   public static final int LEVEL_MODERATORS = 1;
   public static final int LEVEL_GAMEMASTERS = 2;
   public static final int LEVEL_ADMINS = 3;
   public static final int LEVEL_OWNERS = 4;
   private final CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher();

   public Commands(Commands.CommandSelection var1, CommandBuildContext var2) {
      super();
      AdvancementCommands.register(this.dispatcher);
      AttributeCommand.register(this.dispatcher, var2);
      ExecuteCommand.register(this.dispatcher, var2);
      BossBarCommands.register(this.dispatcher);
      ClearInventoryCommands.register(this.dispatcher, var2);
      CloneCommands.register(this.dispatcher, var2);
      DamageCommand.register(this.dispatcher, var2);
      DataCommands.register(this.dispatcher);
      DataPackCommand.register(this.dispatcher);
      DebugCommand.register(this.dispatcher);
      DefaultGameModeCommands.register(this.dispatcher);
      DifficultyCommand.register(this.dispatcher);
      EffectCommands.register(this.dispatcher, var2);
      EmoteCommands.register(this.dispatcher);
      EnchantCommand.register(this.dispatcher, var2);
      ExperienceCommand.register(this.dispatcher);
      FillCommand.register(this.dispatcher, var2);
      FillBiomeCommand.register(this.dispatcher, var2);
      ForceLoadCommand.register(this.dispatcher);
      FunctionCommand.register(this.dispatcher);
      GameModeCommand.register(this.dispatcher);
      GameRuleCommand.register(this.dispatcher);
      GiveCommand.register(this.dispatcher, var2);
      HelpCommand.register(this.dispatcher);
      ItemCommands.register(this.dispatcher, var2);
      KickCommand.register(this.dispatcher);
      KillCommand.register(this.dispatcher);
      ListPlayersCommand.register(this.dispatcher);
      LocateCommand.register(this.dispatcher, var2);
      LootCommand.register(this.dispatcher, var2);
      MsgCommand.register(this.dispatcher);
      ParticleCommand.register(this.dispatcher, var2);
      PlaceCommand.register(this.dispatcher);
      PlaySoundCommand.register(this.dispatcher);
      ReloadCommand.register(this.dispatcher);
      RecipeCommand.register(this.dispatcher);
      RideCommand.register(this.dispatcher);
      SayCommand.register(this.dispatcher);
      ScheduleCommand.register(this.dispatcher);
      ScoreboardCommand.register(this.dispatcher);
      SeedCommand.register(this.dispatcher, var1 != Commands.CommandSelection.INTEGRATED);
      SetBlockCommand.register(this.dispatcher, var2);
      SetSpawnCommand.register(this.dispatcher);
      SetWorldSpawnCommand.register(this.dispatcher);
      SpectateCommand.register(this.dispatcher);
      SpreadPlayersCommand.register(this.dispatcher);
      StopSoundCommand.register(this.dispatcher);
      SummonCommand.register(this.dispatcher, var2);
      TagCommand.register(this.dispatcher);
      TeamCommand.register(this.dispatcher);
      TeamMsgCommand.register(this.dispatcher);
      TeleportCommand.register(this.dispatcher);
      TellRawCommand.register(this.dispatcher);
      TimeCommand.register(this.dispatcher);
      TitleCommand.register(this.dispatcher);
      TriggerCommand.register(this.dispatcher);
      WeatherCommand.register(this.dispatcher);
      WorldBorderCommand.register(this.dispatcher);
      if (JvmProfiler.INSTANCE.isAvailable()) {
         JfrCommand.register(this.dispatcher);
      }

      if (SharedConstants.IS_RUNNING_IN_IDE) {
         TestCommand.register(this.dispatcher);
         SpawnArmorTrimsCommand.register(this.dispatcher);
      }

      if (var1.includeDedicated) {
         BanIpCommands.register(this.dispatcher);
         BanListCommands.register(this.dispatcher);
         BanPlayerCommands.register(this.dispatcher);
         DeOpCommands.register(this.dispatcher);
         OpCommand.register(this.dispatcher);
         PardonCommand.register(this.dispatcher);
         PardonIpCommand.register(this.dispatcher);
         PerfCommand.register(this.dispatcher);
         SaveAllCommand.register(this.dispatcher);
         SaveOffCommand.register(this.dispatcher);
         SaveOnCommand.register(this.dispatcher);
         SetPlayerIdleTimeoutCommand.register(this.dispatcher);
         StopCommand.register(this.dispatcher);
         WhitelistCommand.register(this.dispatcher);
      }

      if (var1.includeIntegrated) {
         PublishCommand.register(this.dispatcher);
      }

      this.dispatcher.setConsumer((var0, var1x, var2x) -> ((CommandSourceStack)var0.getSource()).onCommandComplete(var0, var1x, var2x));
   }

   public static <S> ParseResults<S> mapSource(ParseResults<S> var0, UnaryOperator<S> var1) {
      CommandContextBuilder var2 = var0.getContext();
      CommandContextBuilder var3 = var2.withSource(var1.apply(var2.getSource()));
      return new ParseResults(var3, var0.getReader(), var0.getExceptions());
   }

   public int performPrefixedCommand(CommandSourceStack var1, String var2) {
      var2 = var2.startsWith("/") ? var2.substring(1) : var2;
      return this.performCommand(this.dispatcher.parse(var2, var1), var2);
   }

   public int performCommand(ParseResults<CommandSourceStack> var1, String var2) {
      CommandSourceStack var3 = (CommandSourceStack)var1.getContext().getSource();
      var3.getServer().getProfiler().push(() -> "/" + var2);

      int var18;
      try {
         try {
            return this.dispatcher.execute(var1);
         } catch (CommandRuntimeException var13) {
            var3.sendFailure(var13.getComponent());
            return 0;
         } catch (CommandSyntaxException var14) {
            var3.sendFailure(ComponentUtils.fromMessage(var14.getRawMessage()));
            if (var14.getInput() != null && var14.getCursor() >= 0) {
               var18 = Math.min(var14.getInput().length(), var14.getCursor());
               MutableComponent var21 = Component.empty()
                  .withStyle(ChatFormatting.GRAY)
                  .withStyle(var1x -> var1x.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + var2)));
               if (var18 > 10) {
                  var21.append(CommonComponents.ELLIPSIS);
               }

               var21.append(var14.getInput().substring(Math.max(0, var18 - 10), var18));
               if (var18 < var14.getInput().length()) {
                  MutableComponent var22 = Component.literal(var14.getInput().substring(var18)).withStyle(ChatFormatting.RED, ChatFormatting.UNDERLINE);
                  var21.append(var22);
               }

               var21.append(Component.translatable("command.context.here").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
               var3.sendFailure(var21);
            }
         } catch (Exception var15) {
            MutableComponent var5 = Component.literal(var15.getMessage() == null ? var15.getClass().getName() : var15.getMessage());
            if (LOGGER.isDebugEnabled()) {
               LOGGER.error("Command exception: /{}", var2, var15);
               StackTraceElement[] var6 = var15.getStackTrace();

               for(int var7 = 0; var7 < Math.min(var6.length, 3); ++var7) {
                  var5.append("\n\n")
                     .append(var6[var7].getMethodName())
                     .append("\n ")
                     .append(var6[var7].getFileName())
                     .append(":")
                     .append(String.valueOf(var6[var7].getLineNumber()));
               }
            }

            var3.sendFailure(
               Component.translatable("command.failed").withStyle(var1x -> var1x.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var5)))
            );
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               var3.sendFailure(Component.literal(Util.describeError(var15)));
               LOGGER.error("'/{}' threw an exception", var2, var15);
            }

            return 0;
         }

         var18 = 0;
      } finally {
         var3.getServer().getProfiler().pop();
      }

      return var18;
   }

   public void sendCommands(ServerPlayer var1) {
      HashMap var2 = Maps.newHashMap();
      RootCommandNode var3 = new RootCommandNode();
      var2.put(this.dispatcher.getRoot(), var3);
      this.fillUsableCommands(this.dispatcher.getRoot(), var3, var1.createCommandSourceStack(), var2);
      var1.connection.send(new ClientboundCommandsPacket(var3));
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void fillUsableCommands(
      CommandNode<CommandSourceStack> var1,
      CommandNode<SharedSuggestionProvider> var2,
      CommandSourceStack var3,
      Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> var4
   ) {
      for(CommandNode var6 : var1.getChildren()) {
         if (var6.canUse(var3)) {
            ArgumentBuilder var7 = var6.createBuilder();
            var7.requires(var0 -> true);
            if (var7.getCommand() != null) {
               var7.executes(var0 -> 0);
            }

            if (var7 instanceof RequiredArgumentBuilder var8 && var8.getSuggestionsProvider() != null) {
               var8.suggests(SuggestionProviders.safelySwap(var8.getSuggestionsProvider()));
            }

            if (var7.getRedirect() != null) {
               var7.redirect((CommandNode)var4.get(var7.getRedirect()));
            }

            CommandNode var9 = var7.build();
            var4.put(var6, var9);
            var2.addChild(var9);
            if (!var6.getChildren().isEmpty()) {
               this.fillUsableCommands(var6, var9, var3, var4);
            }
         }
      }
   }

   public static LiteralArgumentBuilder<CommandSourceStack> literal(String var0) {
      return LiteralArgumentBuilder.literal(var0);
   }

   public static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String var0, ArgumentType<T> var1) {
      return RequiredArgumentBuilder.argument(var0, var1);
   }

   public static Predicate<String> createValidator(Commands.ParseFunction var0) {
      return var1 -> {
         try {
            var0.parse(new StringReader(var1));
            return true;
         } catch (CommandSyntaxException var3) {
            return false;
         }
      };
   }

   public CommandDispatcher<CommandSourceStack> getDispatcher() {
      return this.dispatcher;
   }

   @Nullable
   public static <S> CommandSyntaxException getParseException(ParseResults<S> var0) {
      if (!var0.getReader().canRead()) {
         return null;
      } else if (var0.getExceptions().size() == 1) {
         return (CommandSyntaxException)var0.getExceptions().values().iterator().next();
      } else {
         return var0.getContext().getRange().isEmpty()
            ? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(var0.getReader())
            : CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(var0.getReader());
      }
   }

   public static CommandBuildContext createValidationContext(final HolderLookup.Provider var0) {
      return new CommandBuildContext() {
         @Override
         public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> var1) {
            final HolderLookup.RegistryLookup var2 = var0.lookupOrThrow(var1);
            return new HolderLookup.Delegate<T>(var2) {
               @Override
               public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
                  return Optional.of(this.getOrThrow(var1));
               }

               @Override
               public HolderSet.Named<T> getOrThrow(TagKey<T> var1) {
                  Optional var2x = var2.get(var1);
                  return var2x.orElseGet(() -> HolderSet.emptyNamed(var2, var1));
               }
            };
         }
      };
   }

   public static void validate() {
      CommandBuildContext var0 = createValidationContext(VanillaRegistries.createLookup());
      CommandDispatcher var1 = new Commands(Commands.CommandSelection.ALL, var0).getDispatcher();
      RootCommandNode var2 = var1.getRoot();
      var1.findAmbiguities(
         (var1x, var2x, var3x, var4x) -> LOGGER.warn(
               "Ambiguity between arguments {} and {} with inputs: {}", new Object[]{var1.getPath(var2x), var1.getPath(var3x), var4x}
            )
      );
      Set var3 = ArgumentUtils.findUsedArgumentTypes(var2);
      Set var4 = var3.stream().filter(var0x -> !ArgumentTypeInfos.isClassRecognized(var0x.getClass())).collect(Collectors.toSet());
      if (!var4.isEmpty()) {
         LOGGER.warn("Missing type registration for following arguments:\n {}", var4.stream().map(var0x -> "\t" + var0x).collect(Collectors.joining(",\n")));
         throw new IllegalStateException("Unregistered argument types");
      }
   }

   public static enum CommandSelection {
      ALL(true, true),
      DEDICATED(false, true),
      INTEGRATED(true, false);

      final boolean includeIntegrated;
      final boolean includeDedicated;

      private CommandSelection(boolean var3, boolean var4) {
         this.includeIntegrated = var3;
         this.includeDedicated = var4;
      }
   }

   @FunctionalInterface
   public interface ParseFunction {
      void parse(StringReader var1) throws CommandSyntaxException;
   }
}
