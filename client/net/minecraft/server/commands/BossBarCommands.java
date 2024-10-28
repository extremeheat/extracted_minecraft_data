package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class BossBarCommands {
   private static final DynamicCommandExceptionType ERROR_ALREADY_EXISTS = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.bossbar.create.failed", var0);
   });
   private static final DynamicCommandExceptionType ERROR_DOESNT_EXIST = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.bossbar.unknown", var0);
   });
   private static final SimpleCommandExceptionType ERROR_NO_PLAYER_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.players.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_NAME_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.name.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_COLOR_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.color.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_STYLE_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.style.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_VALUE_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.value.unchanged"));
   private static final SimpleCommandExceptionType ERROR_NO_MAX_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.max.unchanged"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_HIDDEN = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.visibility.unchanged.hidden"));
   private static final SimpleCommandExceptionType ERROR_ALREADY_VISIBLE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.visibility.unchanged.visible"));
   public static final SuggestionProvider<CommandSourceStack> SUGGEST_BOSS_BAR = (var0, var1) -> {
      return SharedSuggestionProvider.suggestResource((Iterable)((CommandSourceStack)var0.getSource()).getServer().getCustomBossEvents().getIds(), var1);
   };

   public BossBarCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("bossbar").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("add").then(Commands.argument("id", ResourceLocationArgument.id()).then(Commands.argument("name", ComponentArgument.textComponent(var1)).executes((var0x) -> {
         return createBar((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "id"), ComponentArgument.getComponent(var0x, "name"));
      }))))).then(Commands.literal("remove").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).executes((var0x) -> {
         return removeBar((CommandSourceStack)var0x.getSource(), getBossBar(var0x));
      })))).then(Commands.literal("list").executes((var0x) -> {
         return listBars((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("set").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).then(Commands.literal("name").then(Commands.argument("name", ComponentArgument.textComponent(var1)).executes((var0x) -> {
         return setName((CommandSourceStack)var0x.getSource(), getBossBar(var0x), ComponentArgument.getComponent(var0x, "name"));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("color").then(Commands.literal("pink").executes((var0x) -> {
         return setColor((CommandSourceStack)var0x.getSource(), getBossBar(var0x), BossEvent.BossBarColor.PINK);
      }))).then(Commands.literal("blue").executes((var0x) -> {
         return setColor((CommandSourceStack)var0x.getSource(), getBossBar(var0x), BossEvent.BossBarColor.BLUE);
      }))).then(Commands.literal("red").executes((var0x) -> {
         return setColor((CommandSourceStack)var0x.getSource(), getBossBar(var0x), BossEvent.BossBarColor.RED);
      }))).then(Commands.literal("green").executes((var0x) -> {
         return setColor((CommandSourceStack)var0x.getSource(), getBossBar(var0x), BossEvent.BossBarColor.GREEN);
      }))).then(Commands.literal("yellow").executes((var0x) -> {
         return setColor((CommandSourceStack)var0x.getSource(), getBossBar(var0x), BossEvent.BossBarColor.YELLOW);
      }))).then(Commands.literal("purple").executes((var0x) -> {
         return setColor((CommandSourceStack)var0x.getSource(), getBossBar(var0x), BossEvent.BossBarColor.PURPLE);
      }))).then(Commands.literal("white").executes((var0x) -> {
         return setColor((CommandSourceStack)var0x.getSource(), getBossBar(var0x), BossEvent.BossBarColor.WHITE);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("style").then(Commands.literal("progress").executes((var0x) -> {
         return setStyle((CommandSourceStack)var0x.getSource(), getBossBar(var0x), BossEvent.BossBarOverlay.PROGRESS);
      }))).then(Commands.literal("notched_6").executes((var0x) -> {
         return setStyle((CommandSourceStack)var0x.getSource(), getBossBar(var0x), BossEvent.BossBarOverlay.NOTCHED_6);
      }))).then(Commands.literal("notched_10").executes((var0x) -> {
         return setStyle((CommandSourceStack)var0x.getSource(), getBossBar(var0x), BossEvent.BossBarOverlay.NOTCHED_10);
      }))).then(Commands.literal("notched_12").executes((var0x) -> {
         return setStyle((CommandSourceStack)var0x.getSource(), getBossBar(var0x), BossEvent.BossBarOverlay.NOTCHED_12);
      }))).then(Commands.literal("notched_20").executes((var0x) -> {
         return setStyle((CommandSourceStack)var0x.getSource(), getBossBar(var0x), BossEvent.BossBarOverlay.NOTCHED_20);
      })))).then(Commands.literal("value").then(Commands.argument("value", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return setValue((CommandSourceStack)var0x.getSource(), getBossBar(var0x), IntegerArgumentType.getInteger(var0x, "value"));
      })))).then(Commands.literal("max").then(Commands.argument("max", IntegerArgumentType.integer(1)).executes((var0x) -> {
         return setMax((CommandSourceStack)var0x.getSource(), getBossBar(var0x), IntegerArgumentType.getInteger(var0x, "max"));
      })))).then(Commands.literal("visible").then(Commands.argument("visible", BoolArgumentType.bool()).executes((var0x) -> {
         return setVisible((CommandSourceStack)var0x.getSource(), getBossBar(var0x), BoolArgumentType.getBool(var0x, "visible"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("players").executes((var0x) -> {
         return setPlayers((CommandSourceStack)var0x.getSource(), getBossBar(var0x), Collections.emptyList());
      })).then(Commands.argument("targets", EntityArgument.players()).executes((var0x) -> {
         return setPlayers((CommandSourceStack)var0x.getSource(), getBossBar(var0x), EntityArgument.getOptionalPlayers(var0x, "targets"));
      })))))).then(Commands.literal("get").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).then(Commands.literal("value").executes((var0x) -> {
         return getValue((CommandSourceStack)var0x.getSource(), getBossBar(var0x));
      }))).then(Commands.literal("max").executes((var0x) -> {
         return getMax((CommandSourceStack)var0x.getSource(), getBossBar(var0x));
      }))).then(Commands.literal("visible").executes((var0x) -> {
         return getVisible((CommandSourceStack)var0x.getSource(), getBossBar(var0x));
      }))).then(Commands.literal("players").executes((var0x) -> {
         return getPlayers((CommandSourceStack)var0x.getSource(), getBossBar(var0x));
      })))));
   }

   private static int getValue(CommandSourceStack var0, CustomBossEvent var1) {
      var0.sendSuccess(() -> {
         return Component.translatable("commands.bossbar.get.value", var1.getDisplayName(), var1.getValue());
      }, true);
      return var1.getValue();
   }

   private static int getMax(CommandSourceStack var0, CustomBossEvent var1) {
      var0.sendSuccess(() -> {
         return Component.translatable("commands.bossbar.get.max", var1.getDisplayName(), var1.getMax());
      }, true);
      return var1.getMax();
   }

   private static int getVisible(CommandSourceStack var0, CustomBossEvent var1) {
      if (var1.isVisible()) {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.bossbar.get.visible.visible", var1.getDisplayName());
         }, true);
         return 1;
      } else {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.bossbar.get.visible.hidden", var1.getDisplayName());
         }, true);
         return 0;
      }
   }

   private static int getPlayers(CommandSourceStack var0, CustomBossEvent var1) {
      if (var1.getPlayers().isEmpty()) {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.bossbar.get.players.none", var1.getDisplayName());
         }, true);
      } else {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.bossbar.get.players.some", var1.getDisplayName(), var1.getPlayers().size(), ComponentUtils.formatList(var1.getPlayers(), Player::getDisplayName));
         }, true);
      }

      return var1.getPlayers().size();
   }

   private static int setVisible(CommandSourceStack var0, CustomBossEvent var1, boolean var2) throws CommandSyntaxException {
      if (var1.isVisible() == var2) {
         if (var2) {
            throw ERROR_ALREADY_VISIBLE.create();
         } else {
            throw ERROR_ALREADY_HIDDEN.create();
         }
      } else {
         var1.setVisible(var2);
         if (var2) {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.bossbar.set.visible.success.visible", var1.getDisplayName());
            }, true);
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.bossbar.set.visible.success.hidden", var1.getDisplayName());
            }, true);
         }

         return 0;
      }
   }

   private static int setValue(CommandSourceStack var0, CustomBossEvent var1, int var2) throws CommandSyntaxException {
      if (var1.getValue() == var2) {
         throw ERROR_NO_VALUE_CHANGE.create();
      } else {
         var1.setValue(var2);
         var0.sendSuccess(() -> {
            return Component.translatable("commands.bossbar.set.value.success", var1.getDisplayName(), var2);
         }, true);
         return var2;
      }
   }

   private static int setMax(CommandSourceStack var0, CustomBossEvent var1, int var2) throws CommandSyntaxException {
      if (var1.getMax() == var2) {
         throw ERROR_NO_MAX_CHANGE.create();
      } else {
         var1.setMax(var2);
         var0.sendSuccess(() -> {
            return Component.translatable("commands.bossbar.set.max.success", var1.getDisplayName(), var2);
         }, true);
         return var2;
      }
   }

   private static int setColor(CommandSourceStack var0, CustomBossEvent var1, BossEvent.BossBarColor var2) throws CommandSyntaxException {
      if (var1.getColor().equals(var2)) {
         throw ERROR_NO_COLOR_CHANGE.create();
      } else {
         var1.setColor(var2);
         var0.sendSuccess(() -> {
            return Component.translatable("commands.bossbar.set.color.success", var1.getDisplayName());
         }, true);
         return 0;
      }
   }

   private static int setStyle(CommandSourceStack var0, CustomBossEvent var1, BossEvent.BossBarOverlay var2) throws CommandSyntaxException {
      if (var1.getOverlay().equals(var2)) {
         throw ERROR_NO_STYLE_CHANGE.create();
      } else {
         var1.setOverlay(var2);
         var0.sendSuccess(() -> {
            return Component.translatable("commands.bossbar.set.style.success", var1.getDisplayName());
         }, true);
         return 0;
      }
   }

   private static int setName(CommandSourceStack var0, CustomBossEvent var1, Component var2) throws CommandSyntaxException {
      MutableComponent var3 = ComponentUtils.updateForEntity(var0, (Component)var2, (Entity)null, 0);
      if (var1.getName().equals(var3)) {
         throw ERROR_NO_NAME_CHANGE.create();
      } else {
         var1.setName(var3);
         var0.sendSuccess(() -> {
            return Component.translatable("commands.bossbar.set.name.success", var1.getDisplayName());
         }, true);
         return 0;
      }
   }

   private static int setPlayers(CommandSourceStack var0, CustomBossEvent var1, Collection<ServerPlayer> var2) throws CommandSyntaxException {
      boolean var3 = var1.setPlayers(var2);
      if (!var3) {
         throw ERROR_NO_PLAYER_CHANGE.create();
      } else {
         if (var1.getPlayers().isEmpty()) {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.bossbar.set.players.success.none", var1.getDisplayName());
            }, true);
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.bossbar.set.players.success.some", var1.getDisplayName(), var2.size(), ComponentUtils.formatList(var2, Player::getDisplayName));
            }, true);
         }

         return var1.getPlayers().size();
      }
   }

   private static int listBars(CommandSourceStack var0) {
      Collection var1 = var0.getServer().getCustomBossEvents().getEvents();
      if (var1.isEmpty()) {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.bossbar.list.bars.none");
         }, false);
      } else {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.bossbar.list.bars.some", var1.size(), ComponentUtils.formatList(var1, CustomBossEvent::getDisplayName));
         }, false);
      }

      return var1.size();
   }

   private static int createBar(CommandSourceStack var0, ResourceLocation var1, Component var2) throws CommandSyntaxException {
      CustomBossEvents var3 = var0.getServer().getCustomBossEvents();
      if (var3.get(var1) != null) {
         throw ERROR_ALREADY_EXISTS.create(var1.toString());
      } else {
         CustomBossEvent var4 = var3.create(var1, ComponentUtils.updateForEntity(var0, (Component)var2, (Entity)null, 0));
         var0.sendSuccess(() -> {
            return Component.translatable("commands.bossbar.create.success", var4.getDisplayName());
         }, true);
         return var3.getEvents().size();
      }
   }

   private static int removeBar(CommandSourceStack var0, CustomBossEvent var1) {
      CustomBossEvents var2 = var0.getServer().getCustomBossEvents();
      var1.removeAllPlayers();
      var2.remove(var1);
      var0.sendSuccess(() -> {
         return Component.translatable("commands.bossbar.remove.success", var1.getDisplayName());
      }, true);
      return var2.getEvents().size();
   }

   public static CustomBossEvent getBossBar(CommandContext<CommandSourceStack> var0) throws CommandSyntaxException {
      ResourceLocation var1 = ResourceLocationArgument.getId(var0, "id");
      CustomBossEvent var2 = ((CommandSourceStack)var0.getSource()).getServer().getCustomBossEvents().get(var1);
      if (var2 == null) {
         throw ERROR_DOESNT_EXIST.create(var1.toString());
      } else {
         return var2;
      }
   }
}
