package net.minecraft.command;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.impl.AdvancementCommand;
import net.minecraft.command.impl.BanCommand;
import net.minecraft.command.impl.BanIpCommand;
import net.minecraft.command.impl.BanListCommand;
import net.minecraft.command.impl.BossBarCommand;
import net.minecraft.command.impl.ClearCommand;
import net.minecraft.command.impl.CloneCommand;
import net.minecraft.command.impl.DataPackCommand;
import net.minecraft.command.impl.DeOpCommand;
import net.minecraft.command.impl.DebugCommand;
import net.minecraft.command.impl.DefaultGameModeCommand;
import net.minecraft.command.impl.DifficultyCommand;
import net.minecraft.command.impl.EffectCommand;
import net.minecraft.command.impl.EnchantCommand;
import net.minecraft.command.impl.ExecuteCommand;
import net.minecraft.command.impl.ExperienceCommand;
import net.minecraft.command.impl.FillCommand;
import net.minecraft.command.impl.ForceLoadCommand;
import net.minecraft.command.impl.FunctionCommand;
import net.minecraft.command.impl.GameModeCommand;
import net.minecraft.command.impl.GameRuleCommand;
import net.minecraft.command.impl.GiveCommand;
import net.minecraft.command.impl.HelpCommand;
import net.minecraft.command.impl.KickCommand;
import net.minecraft.command.impl.KillCommand;
import net.minecraft.command.impl.ListCommand;
import net.minecraft.command.impl.LocateCommand;
import net.minecraft.command.impl.MeCommand;
import net.minecraft.command.impl.MessageCommand;
import net.minecraft.command.impl.OpCommand;
import net.minecraft.command.impl.PardonCommand;
import net.minecraft.command.impl.PardonIpCommand;
import net.minecraft.command.impl.ParticleCommand;
import net.minecraft.command.impl.PlaySoundCommand;
import net.minecraft.command.impl.PublishCommand;
import net.minecraft.command.impl.RecipeCommand;
import net.minecraft.command.impl.ReloadCommand;
import net.minecraft.command.impl.ReplaceItemCommand;
import net.minecraft.command.impl.SaveAllCommand;
import net.minecraft.command.impl.SaveOffCommand;
import net.minecraft.command.impl.SaveOnCommand;
import net.minecraft.command.impl.SayCommand;
import net.minecraft.command.impl.ScoreboardCommand;
import net.minecraft.command.impl.SeedCommand;
import net.minecraft.command.impl.SetBlockCommand;
import net.minecraft.command.impl.SetIdleTimeoutCommand;
import net.minecraft.command.impl.SetWorldSpawnCommand;
import net.minecraft.command.impl.SpawnPointCommand;
import net.minecraft.command.impl.SpreadPlayersCommand;
import net.minecraft.command.impl.StopCommand;
import net.minecraft.command.impl.StopSoundCommand;
import net.minecraft.command.impl.SummonCommand;
import net.minecraft.command.impl.TagCommand;
import net.minecraft.command.impl.TeamCommand;
import net.minecraft.command.impl.TeleportCommand;
import net.minecraft.command.impl.TellRawCommand;
import net.minecraft.command.impl.TimeCommand;
import net.minecraft.command.impl.TitleCommand;
import net.minecraft.command.impl.TriggerCommand;
import net.minecraft.command.impl.WeatherCommand;
import net.minecraft.command.impl.WhitelistCommand;
import net.minecraft.command.impl.WorldBorderCommand;
import net.minecraft.command.impl.data.DataCommand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketCommandList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Commands {
   private static final Logger field_197061_a = LogManager.getLogger();
   private final CommandDispatcher<CommandSource> field_197062_b = new CommandDispatcher();

   public Commands(boolean var1) {
      super();
      AdvancementCommand.func_198199_a(this.field_197062_b);
      ExecuteCommand.func_198378_a(this.field_197062_b);
      BossBarCommand.func_201413_a(this.field_197062_b);
      ClearCommand.func_198243_a(this.field_197062_b);
      CloneCommand.func_198265_a(this.field_197062_b);
      DataCommand.func_198937_a(this.field_197062_b);
      DataPackCommand.func_198299_a(this.field_197062_b);
      DebugCommand.func_198330_a(this.field_197062_b);
      DefaultGameModeCommand.func_198340_a(this.field_197062_b);
      DifficultyCommand.func_198344_a(this.field_197062_b);
      EffectCommand.func_198353_a(this.field_197062_b);
      MeCommand.func_198364_a(this.field_197062_b);
      EnchantCommand.func_202649_a(this.field_197062_b);
      ExperienceCommand.func_198437_a(this.field_197062_b);
      FillCommand.func_198465_a(this.field_197062_b);
      FunctionCommand.func_198476_a(this.field_197062_b);
      GameModeCommand.func_198482_a(this.field_197062_b);
      GameRuleCommand.func_198487_a(this.field_197062_b);
      GiveCommand.func_198494_a(this.field_197062_b);
      HelpCommand.func_198510_a(this.field_197062_b);
      KickCommand.func_198514_a(this.field_197062_b);
      KillCommand.func_198518_a(this.field_197062_b);
      ListCommand.func_198522_a(this.field_197062_b);
      LocateCommand.func_198528_a(this.field_197062_b);
      MessageCommand.func_198537_a(this.field_197062_b);
      ParticleCommand.func_198563_a(this.field_197062_b);
      PlaySoundCommand.func_198572_a(this.field_197062_b);
      PublishCommand.func_198581_a(this.field_197062_b);
      ReloadCommand.func_198597_a(this.field_197062_b);
      RecipeCommand.func_198589_a(this.field_197062_b);
      ReplaceItemCommand.func_198602_a(this.field_197062_b);
      SayCommand.func_198625_a(this.field_197062_b);
      ScoreboardCommand.func_198647_a(this.field_197062_b);
      SeedCommand.func_198671_a(this.field_197062_b);
      SetBlockCommand.func_198684_a(this.field_197062_b);
      SpawnPointCommand.func_198695_a(this.field_197062_b);
      SetWorldSpawnCommand.func_198702_a(this.field_197062_b);
      SpreadPlayersCommand.func_198716_a(this.field_197062_b);
      StopSoundCommand.func_198730_a(this.field_197062_b);
      SummonCommand.func_198736_a(this.field_197062_b);
      TagCommand.func_198743_a(this.field_197062_b);
      TeamCommand.func_198771_a(this.field_197062_b);
      TeleportCommand.func_198809_a(this.field_197062_b);
      TellRawCommand.func_198818_a(this.field_197062_b);
      ForceLoadCommand.func_212712_a(this.field_197062_b);
      TimeCommand.func_198823_a(this.field_197062_b);
      TitleCommand.func_198839_a(this.field_197062_b);
      TriggerCommand.func_198852_a(this.field_197062_b);
      WeatherCommand.func_198862_a(this.field_197062_b);
      WorldBorderCommand.func_198894_a(this.field_197062_b);
      if (var1) {
         BanIpCommand.func_198220_a(this.field_197062_b);
         BanListCommand.func_198229_a(this.field_197062_b);
         BanCommand.func_198235_a(this.field_197062_b);
         DeOpCommand.func_198321_a(this.field_197062_b);
         OpCommand.func_198541_a(this.field_197062_b);
         PardonCommand.func_198547_a(this.field_197062_b);
         PardonIpCommand.func_198553_a(this.field_197062_b);
         SaveAllCommand.func_198611_a(this.field_197062_b);
         SaveOffCommand.func_198617_a(this.field_197062_b);
         SaveOnCommand.func_198621_a(this.field_197062_b);
         SetIdleTimeoutCommand.func_198690_a(this.field_197062_b);
         StopCommand.func_198725_a(this.field_197062_b);
         WhitelistCommand.func_198873_a(this.field_197062_b);
      }

      this.field_197062_b.findAmbiguities((var1x, var2, var3, var4) -> {
         field_197061_a.warn("Ambiguity between arguments {} and {} with inputs: {}", this.field_197062_b.getPath(var2), this.field_197062_b.getPath(var3), var4);
      });
      this.field_197062_b.setConsumer((var0, var1x, var2) -> {
         ((CommandSource)var0.getSource()).func_197038_a(var0, var1x, var2);
      });
   }

   public void func_200378_a(File var1) {
      try {
         Files.write((new GsonBuilder()).setPrettyPrinting().create().toJson(ArgumentTypes.func_200388_a(this.field_197062_b, this.field_197062_b.getRoot())), var1, StandardCharsets.UTF_8);
      } catch (IOException var3) {
         field_197061_a.error("Couldn't write out command tree!", var3);
      }

   }

   public int func_197059_a(CommandSource var1, String var2) {
      StringReader var3 = new StringReader(var2);
      if (var3.canRead() && var3.peek() == '/') {
         var3.skip();
      }

      var1.func_197028_i().field_71304_b.func_76320_a(var2);

      byte var18;
      try {
         try {
            int var4 = this.field_197062_b.execute(var3, var1);
            return var4;
         } catch (CommandException var13) {
            var1.func_197021_a(var13.func_197003_a());
            var18 = 0;
            return var18;
         } catch (CommandSyntaxException var14) {
            var1.func_197021_a(TextComponentUtils.func_202465_a(var14.getRawMessage()));
            if (var14.getInput() != null && var14.getCursor() >= 0) {
               int var17 = Math.min(var14.getInput().length(), var14.getCursor());
               ITextComponent var20 = (new TextComponentString("")).func_211708_a(TextFormatting.GRAY).func_211710_a((var1x) -> {
                  var1x.func_150241_a(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, var2));
               });
               if (var17 > 10) {
                  var20.func_150258_a("...");
               }

               var20.func_150258_a(var14.getInput().substring(Math.max(0, var17 - 10), var17));
               if (var17 < var14.getInput().length()) {
                  ITextComponent var21 = (new TextComponentString(var14.getInput().substring(var17))).func_211709_a(new TextFormatting[]{TextFormatting.RED, TextFormatting.UNDERLINE});
                  var20.func_150257_a(var21);
               }

               var20.func_150257_a((new TextComponentTranslation("command.context.here", new Object[0])).func_211709_a(new TextFormatting[]{TextFormatting.RED, TextFormatting.ITALIC}));
               var1.func_197021_a(var20);
            }
         } catch (Exception var15) {
            TextComponentString var5 = new TextComponentString(var15.getMessage() == null ? var15.getClass().getName() : var15.getMessage());
            if (field_197061_a.isDebugEnabled()) {
               StackTraceElement[] var6 = var15.getStackTrace();

               for(int var7 = 0; var7 < Math.min(var6.length, 3); ++var7) {
                  var5.func_150258_a("\n\n").func_150258_a(var6[var7].getMethodName()).func_150258_a("\n ").func_150258_a(var6[var7].getFileName()).func_150258_a(":").func_150258_a(String.valueOf(var6[var7].getLineNumber()));
               }
            }

            var1.func_197021_a((new TextComponentTranslation("command.failed", new Object[0])).func_211710_a((var1x) -> {
               var1x.func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var5));
            }));
            byte var19 = 0;
            return var19;
         }

         var18 = 0;
      } finally {
         var1.func_197028_i().field_71304_b.func_76319_b();
      }

      return var18;
   }

   public void func_197051_a(EntityPlayerMP var1) {
      HashMap var2 = Maps.newHashMap();
      RootCommandNode var3 = new RootCommandNode();
      var2.put(this.field_197062_b.getRoot(), var3);
      this.func_197052_a(this.field_197062_b.getRoot(), var3, var1.func_195051_bN(), var2);
      var1.field_71135_a.func_147359_a(new SPacketCommandList(var3));
   }

   private void func_197052_a(CommandNode<CommandSource> var1, CommandNode<ISuggestionProvider> var2, CommandSource var3, Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> var4) {
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
                  var8.suggests(SuggestionProviders.func_197496_b(var8.getSuggestionsProvider()));
               }
            }

            if (var7.getRedirect() != null) {
               var7.redirect((CommandNode)var4.get(var7.getRedirect()));
            }

            CommandNode var9 = var7.build();
            var4.put(var6, var9);
            var2.addChild(var9);
            if (!var6.getChildren().isEmpty()) {
               this.func_197052_a(var6, var9, var3, var4);
            }
         }
      }

   }

   public static LiteralArgumentBuilder<CommandSource> func_197057_a(String var0) {
      return LiteralArgumentBuilder.literal(var0);
   }

   public static <T> RequiredArgumentBuilder<CommandSource, T> func_197056_a(String var0, ArgumentType<T> var1) {
      return RequiredArgumentBuilder.argument(var0, var1);
   }

   public static Predicate<String> func_212590_a(Commands.IParser var0) {
      return (var1) -> {
         try {
            var0.parse(new StringReader(var1));
            return true;
         } catch (CommandSyntaxException var3) {
            return false;
         }
      };
   }

   public CommandDispatcher<CommandSource> func_197054_a() {
      return this.field_197062_b;
   }

   @FunctionalInterface
   public interface IParser {
      void parse(StringReader var1) throws CommandSyntaxException;
   }
}
