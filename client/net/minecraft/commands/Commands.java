package net.minecraft.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.gametest.framework.TestCommand;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.server.commands.AttributeCommand;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.commands.BanListCommands;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.commands.BossBarCommands;
import net.minecraft.server.commands.ClearInventoryCommands;
import net.minecraft.server.commands.CloneCommands;
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
import net.minecraft.server.commands.FillCommand;
import net.minecraft.server.commands.ForceLoadCommand;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.commands.HelpCommand;
import net.minecraft.server.commands.KickCommand;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.server.commands.ListPlayersCommand;
import net.minecraft.server.commands.LocateBiomeCommand;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.commands.OpCommand;
import net.minecraft.server.commands.PardonCommand;
import net.minecraft.server.commands.PardonIpCommand;
import net.minecraft.server.commands.ParticleCommand;
import net.minecraft.server.commands.PlaySoundCommand;
import net.minecraft.server.commands.PublishCommand;
import net.minecraft.server.commands.RecipeCommand;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.commands.ReplaceItemCommand;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Commands {
   private static final Logger LOGGER = LogManager.getLogger();
   private final CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher();

   public Commands(Commands.CommandSelection var1) {
      super();
      AdvancementCommands.register(this.dispatcher);
      AttributeCommand.register(this.dispatcher);
      ExecuteCommand.register(this.dispatcher);
      BossBarCommands.register(this.dispatcher);
      ClearInventoryCommands.register(this.dispatcher);
      CloneCommands.register(this.dispatcher);
      DataCommands.register(this.dispatcher);
      DataPackCommand.register(this.dispatcher);
      DebugCommand.register(this.dispatcher);
      DefaultGameModeCommands.register(this.dispatcher);
      DifficultyCommand.register(this.dispatcher);
      EffectCommands.register(this.dispatcher);
      EmoteCommands.register(this.dispatcher);
      EnchantCommand.register(this.dispatcher);
      ExperienceCommand.register(this.dispatcher);
      FillCommand.register(this.dispatcher);
      ForceLoadCommand.register(this.dispatcher);
      FunctionCommand.register(this.dispatcher);
      GameModeCommand.register(this.dispatcher);
      GameRuleCommand.register(this.dispatcher);
      GiveCommand.register(this.dispatcher);
      HelpCommand.register(this.dispatcher);
      KickCommand.register(this.dispatcher);
      KillCommand.register(this.dispatcher);
      ListPlayersCommand.register(this.dispatcher);
      LocateCommand.register(this.dispatcher);
      LocateBiomeCommand.register(this.dispatcher);
      LootCommand.register(this.dispatcher);
      MsgCommand.register(this.dispatcher);
      ParticleCommand.register(this.dispatcher);
      PlaySoundCommand.register(this.dispatcher);
      ReloadCommand.register(this.dispatcher);
      RecipeCommand.register(this.dispatcher);
      ReplaceItemCommand.register(this.dispatcher);
      SayCommand.register(this.dispatcher);
      ScheduleCommand.register(this.dispatcher);
      ScoreboardCommand.register(this.dispatcher);
      SeedCommand.register(this.dispatcher, var1 != Commands.CommandSelection.INTEGRATED);
      SetBlockCommand.register(this.dispatcher);
      SetSpawnCommand.register(this.dispatcher);
      SetWorldSpawnCommand.register(this.dispatcher);
      SpectateCommand.register(this.dispatcher);
      SpreadPlayersCommand.register(this.dispatcher);
      StopSoundCommand.register(this.dispatcher);
      SummonCommand.register(this.dispatcher);
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
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         TestCommand.register(this.dispatcher);
      }

      if (var1.includeDedicated) {
         BanIpCommands.register(this.dispatcher);
         BanListCommands.register(this.dispatcher);
         BanPlayerCommands.register(this.dispatcher);
         DeOpCommands.register(this.dispatcher);
         OpCommand.register(this.dispatcher);
         PardonCommand.register(this.dispatcher);
         PardonIpCommand.register(this.dispatcher);
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

      this.dispatcher.findAmbiguities((var1x, var2, var3, var4) -> {
         LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", this.dispatcher.getPath(var2), this.dispatcher.getPath(var3), var4);
      });
      this.dispatcher.setConsumer((var0, var1x, var2) -> {
         ((CommandSourceStack)var0.getSource()).onCommandComplete(var0, var1x, var2);
      });
   }

   public int performCommand(CommandSourceStack var1, String var2) {
      StringReader var3 = new StringReader(var2);
      if (var3.canRead() && var3.peek() == '/') {
         var3.skip();
      }

      var1.getServer().getProfiler().push(var2);

      byte var19;
      try {
         TextComponent var10000;
         byte var18;
         try {
            int var4 = this.dispatcher.execute(var3, var1);
            return var4;
         } catch (CommandRuntimeException var13) {
            var1.sendFailure(var13.getComponent());
            var18 = 0;
            return var18;
         } catch (CommandSyntaxException var14) {
            var1.sendFailure(ComponentUtils.fromMessage(var14.getRawMessage()));
            if (var14.getInput() != null && var14.getCursor() >= 0) {
               int var17 = Math.min(var14.getInput().length(), var14.getCursor());
               MutableComponent var20 = (new TextComponent("")).withStyle(ChatFormatting.GRAY).withStyle((var1x) -> {
                  return var1x.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, var2));
               });
               if (var17 > 10) {
                  var20.append("...");
               }

               var20.append(var14.getInput().substring(Math.max(0, var17 - 10), var17));
               if (var17 < var14.getInput().length()) {
                  MutableComponent var21 = (new TextComponent(var14.getInput().substring(var17))).withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.UNDERLINE});
                  var20.append((Component)var21);
               }

               var20.append((Component)(new TranslatableComponent("command.context.here")).withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.ITALIC}));
               var1.sendFailure(var20);
            }

            var18 = 0;
            return var18;
         } catch (Exception var15) {
            var10000 = new TextComponent;
         }

         var10000.<init>(var15.getMessage() == null ? var15.getClass().getName() : var15.getMessage());
         TextComponent var5 = var10000;
         if (LOGGER.isDebugEnabled()) {
            LOGGER.error("Command exception: {}", var2, var15);
            StackTraceElement[] var6 = var15.getStackTrace();

            for(int var7 = 0; var7 < Math.min(var6.length, 3); ++var7) {
               var5.append("\n\n").append(var6[var7].getMethodName()).append("\n ").append(var6[var7].getFileName()).append(":").append(String.valueOf(var6[var7].getLineNumber()));
            }
         }

         var1.sendFailure((new TranslatableComponent("command.failed")).withStyle((var1x) -> {
            return var1x.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var5));
         }));
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            var1.sendFailure(new TextComponent(Util.describeError(var15)));
            LOGGER.error("'" + var2 + "' threw an exception", var15);
         }

         var19 = 0;
      } finally {
         var1.getServer().getProfiler().pop();
      }

      return var19;
   }

   public void sendCommands(ServerPlayer var1) {
      HashMap var2 = Maps.newHashMap();
      RootCommandNode var3 = new RootCommandNode();
      var2.put(this.dispatcher.getRoot(), var3);
      this.fillUsableCommands(this.dispatcher.getRoot(), var3, var1.createCommandSourceStack(), var2);
      var1.connection.send(new ClientboundCommandsPacket(var3));
   }

   private void fillUsableCommands(CommandNode<CommandSourceStack> var1, CommandNode<SharedSuggestionProvider> var2, CommandSourceStack var3, Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> var4) {
      Iterator var5 = var1.getChildren().iterator();

      while(var5.hasNext()) {
         CommandNode var6 = (CommandNode)var5.next();
         if (var6.canUse(var3)) {
            ArgumentBuilder var7 = var6.createBuilder();
            var7.requires((var0) -> {
               return true;
            });
            if (var7.getCommand() != null) {
               var7.executes((var0) -> {
                  return 0;
               });
            }

            if (var7 instanceof RequiredArgumentBuilder) {
               RequiredArgumentBuilder var8 = (RequiredArgumentBuilder)var7;
               if (var8.getSuggestionsProvider() != null) {
                  var8.suggests(SuggestionProviders.safelySwap(var8.getSuggestionsProvider()));
               }
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
      return (var1) -> {
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
         return var0.getContext().getRange().isEmpty() ? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(var0.getReader()) : CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(var0.getReader());
      }
   }

   public static void validate() {
      RootCommandNode var0 = (new Commands(Commands.CommandSelection.ALL)).getDispatcher().getRoot();
      Set var1 = ArgumentTypes.findUsedArgumentTypes(var0);
      Set var2 = (Set)var1.stream().filter((var0x) -> {
         return !ArgumentTypes.isTypeRegistered(var0x);
      }).collect(Collectors.toSet());
      if (!var2.isEmpty()) {
         LOGGER.warn("Missing type registration for following arguments:\n {}", var2.stream().map((var0x) -> {
            return "\t" + var0x;
         }).collect(Collectors.joining(",\n")));
         throw new IllegalStateException("Unregistered argument types");
      }
   }

   public static enum CommandSelection {
      ALL(true, true),
      DEDICATED(false, true),
      INTEGRATED(true, false);

      private final boolean includeIntegrated;
      private final boolean includeDedicated;

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
