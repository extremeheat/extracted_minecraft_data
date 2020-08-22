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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class BossBarCommands {
   private static final DynamicCommandExceptionType ERROR_ALREADY_EXISTS = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.bossbar.create.failed", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType ERROR_DOESNT_EXIST = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.bossbar.unknown", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType ERROR_NO_PLAYER_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.players.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NO_NAME_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.name.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NO_COLOR_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.color.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NO_STYLE_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.style.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NO_VALUE_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.value.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NO_MAX_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.max.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_ALREADY_HIDDEN = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.visibility.unchanged.hidden", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_ALREADY_VISIBLE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.visibility.unchanged.visible", new Object[0]));
   public static final SuggestionProvider SUGGEST_BOSS_BAR = (var0, var1) -> {
      return SharedSuggestionProvider.suggestResource((Iterable)((CommandSourceStack)var0.getSource()).getServer().getCustomBossEvents().getIds(), var1);
   };

   public static void register(CommandDispatcher var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("bossbar").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("add").then(Commands.argument("id", ResourceLocationArgument.id()).then(Commands.argument("name", ComponentArgument.textComponent()).executes((var0x) -> {
         return createBar((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "id"), ComponentArgument.getComponent(var0x, "name"));
      }))))).then(Commands.literal("remove").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).executes((var0x) -> {
         return removeBar((CommandSourceStack)var0x.getSource(), getBossBar(var0x));
      })))).then(Commands.literal("list").executes((var0x) -> {
         return listBars((CommandSourceStack)var0x.getSource());
      }))).then(Commands.literal("set").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).then(Commands.literal("name").then(Commands.argument("name", ComponentArgument.textComponent()).executes((var0x) -> {
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
      var0.sendSuccess(new TranslatableComponent("commands.bossbar.get.value", new Object[]{var1.getDisplayName(), var1.getValue()}), true);
      return var1.getValue();
   }

   private static int getMax(CommandSourceStack var0, CustomBossEvent var1) {
      var0.sendSuccess(new TranslatableComponent("commands.bossbar.get.max", new Object[]{var1.getDisplayName(), var1.getMax()}), true);
      return var1.getMax();
   }

   private static int getVisible(CommandSourceStack var0, CustomBossEvent var1) {
      if (var1.isVisible()) {
         var0.sendSuccess(new TranslatableComponent("commands.bossbar.get.visible.visible", new Object[]{var1.getDisplayName()}), true);
         return 1;
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.bossbar.get.visible.hidden", new Object[]{var1.getDisplayName()}), true);
         return 0;
      }
   }

   private static int getPlayers(CommandSourceStack var0, CustomBossEvent var1) {
      if (var1.getPlayers().isEmpty()) {
         var0.sendSuccess(new TranslatableComponent("commands.bossbar.get.players.none", new Object[]{var1.getDisplayName()}), true);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.bossbar.get.players.some", new Object[]{var1.getDisplayName(), var1.getPlayers().size(), ComponentUtils.formatList(var1.getPlayers(), Player::getDisplayName)}), true);
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
            var0.sendSuccess(new TranslatableComponent("commands.bossbar.set.visible.success.visible", new Object[]{var1.getDisplayName()}), true);
         } else {
            var0.sendSuccess(new TranslatableComponent("commands.bossbar.set.visible.success.hidden", new Object[]{var1.getDisplayName()}), true);
         }

         return 0;
      }
   }

   private static int setValue(CommandSourceStack var0, CustomBossEvent var1, int var2) throws CommandSyntaxException {
      if (var1.getValue() == var2) {
         throw ERROR_NO_VALUE_CHANGE.create();
      } else {
         var1.setValue(var2);
         var0.sendSuccess(new TranslatableComponent("commands.bossbar.set.value.success", new Object[]{var1.getDisplayName(), var2}), true);
         return var2;
      }
   }

   private static int setMax(CommandSourceStack var0, CustomBossEvent var1, int var2) throws CommandSyntaxException {
      if (var1.getMax() == var2) {
         throw ERROR_NO_MAX_CHANGE.create();
      } else {
         var1.setMax(var2);
         var0.sendSuccess(new TranslatableComponent("commands.bossbar.set.max.success", new Object[]{var1.getDisplayName(), var2}), true);
         return var2;
      }
   }

   private static int setColor(CommandSourceStack var0, CustomBossEvent var1, BossEvent.BossBarColor var2) throws CommandSyntaxException {
      if (var1.getColor().equals(var2)) {
         throw ERROR_NO_COLOR_CHANGE.create();
      } else {
         var1.setColor(var2);
         var0.sendSuccess(new TranslatableComponent("commands.bossbar.set.color.success", new Object[]{var1.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setStyle(CommandSourceStack var0, CustomBossEvent var1, BossEvent.BossBarOverlay var2) throws CommandSyntaxException {
      if (var1.getOverlay().equals(var2)) {
         throw ERROR_NO_STYLE_CHANGE.create();
      } else {
         var1.setOverlay(var2);
         var0.sendSuccess(new TranslatableComponent("commands.bossbar.set.style.success", new Object[]{var1.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setName(CommandSourceStack var0, CustomBossEvent var1, Component var2) throws CommandSyntaxException {
      Component var3 = ComponentUtils.updateForEntity(var0, var2, (Entity)null, 0);
      if (var1.getName().equals(var3)) {
         throw ERROR_NO_NAME_CHANGE.create();
      } else {
         var1.setName(var3);
         var0.sendSuccess(new TranslatableComponent("commands.bossbar.set.name.success", new Object[]{var1.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setPlayers(CommandSourceStack var0, CustomBossEvent var1, Collection var2) throws CommandSyntaxException {
      boolean var3 = var1.setPlayers(var2);
      if (!var3) {
         throw ERROR_NO_PLAYER_CHANGE.create();
      } else {
         if (var1.getPlayers().isEmpty()) {
            var0.sendSuccess(new TranslatableComponent("commands.bossbar.set.players.success.none", new Object[]{var1.getDisplayName()}), true);
         } else {
            var0.sendSuccess(new TranslatableComponent("commands.bossbar.set.players.success.some", new Object[]{var1.getDisplayName(), var2.size(), ComponentUtils.formatList(var2, Player::getDisplayName)}), true);
         }

         return var1.getPlayers().size();
      }
   }

   private static int listBars(CommandSourceStack var0) {
      Collection var1 = var0.getServer().getCustomBossEvents().getEvents();
      if (var1.isEmpty()) {
         var0.sendSuccess(new TranslatableComponent("commands.bossbar.list.bars.none", new Object[0]), false);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.bossbar.list.bars.some", new Object[]{var1.size(), ComponentUtils.formatList(var1, CustomBossEvent::getDisplayName)}), false);
      }

      return var1.size();
   }

   private static int createBar(CommandSourceStack var0, ResourceLocation var1, Component var2) throws CommandSyntaxException {
      CustomBossEvents var3 = var0.getServer().getCustomBossEvents();
      if (var3.get(var1) != null) {
         throw ERROR_ALREADY_EXISTS.create(var1.toString());
      } else {
         CustomBossEvent var4 = var3.create(var1, ComponentUtils.updateForEntity(var0, var2, (Entity)null, 0));
         var0.sendSuccess(new TranslatableComponent("commands.bossbar.create.success", new Object[]{var4.getDisplayName()}), true);
         return var3.getEvents().size();
      }
   }

   private static int removeBar(CommandSourceStack var0, CustomBossEvent var1) {
      CustomBossEvents var2 = var0.getServer().getCustomBossEvents();
      var1.removeAllPlayers();
      var2.remove(var1);
      var0.sendSuccess(new TranslatableComponent("commands.bossbar.remove.success", new Object[]{var1.getDisplayName()}), true);
      return var2.getEvents().size();
   }

   public static CustomBossEvent getBossBar(CommandContext var0) throws CommandSyntaxException {
      ResourceLocation var1 = ResourceLocationArgument.getId(var0, "id");
      CustomBossEvent var2 = ((CommandSourceStack)var0.getSource()).getServer().getCustomBossEvents().get(var1);
      if (var2 == null) {
         throw ERROR_DOESNT_EXIST.create(var1.toString());
      } else {
         return var2;
      }
   }
}
