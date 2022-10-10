package net.minecraft.command.impl;

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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.CustomBossEvent;
import net.minecraft.server.CustomBossEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.BossInfo;

public class BossBarCommand {
   private static final DynamicCommandExceptionType field_201432_b = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.bossbar.create.failed", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType field_201433_c = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.bossbar.unknown", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType field_201434_d = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.players.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType field_201435_e = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.name.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType field_201436_f = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.color.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType field_201437_g = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.style.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType field_201438_h = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.value.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType field_201439_i = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.max.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType field_201440_j = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.visibility.unchanged.hidden", new Object[0]));
   private static final SimpleCommandExceptionType field_201441_k = new SimpleCommandExceptionType(new TextComponentTranslation("commands.bossbar.set.visibility.unchanged.visible", new Object[0]));
   public static final SuggestionProvider<CommandSource> field_201431_a = (var0, var1) -> {
      return ISuggestionProvider.func_197014_a(((CommandSource)var0.getSource()).func_197028_i().func_201300_aS().func_201377_a(), var1);
   };

   public static void func_201413_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("bossbar").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197057_a("add").then(Commands.func_197056_a("id", ResourceLocationArgument.func_197197_a()).then(Commands.func_197056_a("name", ComponentArgument.func_197067_a()).executes((var0x) -> {
         return func_201400_a((CommandSource)var0x.getSource(), ResourceLocationArgument.func_197195_e(var0x, "id"), ComponentArgument.func_197068_a(var0x, "name"));
      }))))).then(Commands.func_197057_a("remove").then(Commands.func_197056_a("id", ResourceLocationArgument.func_197197_a()).suggests(field_201431_a).executes((var0x) -> {
         return func_201407_e((CommandSource)var0x.getSource(), func_201416_a(var0x));
      })))).then(Commands.func_197057_a("list").executes((var0x) -> {
         return func_201428_a((CommandSource)var0x.getSource());
      }))).then(Commands.func_197057_a("set").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("id", ResourceLocationArgument.func_197197_a()).suggests(field_201431_a).then(Commands.func_197057_a("name").then(Commands.func_197056_a("name", ComponentArgument.func_197067_a()).executes((var0x) -> {
         return func_201420_a((CommandSource)var0x.getSource(), func_201416_a(var0x), ComponentArgument.func_197068_a(var0x, "name"));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("color").then(Commands.func_197057_a("pink").executes((var0x) -> {
         return func_201415_a((CommandSource)var0x.getSource(), func_201416_a(var0x), BossInfo.Color.PINK);
      }))).then(Commands.func_197057_a("blue").executes((var0x) -> {
         return func_201415_a((CommandSource)var0x.getSource(), func_201416_a(var0x), BossInfo.Color.BLUE);
      }))).then(Commands.func_197057_a("red").executes((var0x) -> {
         return func_201415_a((CommandSource)var0x.getSource(), func_201416_a(var0x), BossInfo.Color.RED);
      }))).then(Commands.func_197057_a("green").executes((var0x) -> {
         return func_201415_a((CommandSource)var0x.getSource(), func_201416_a(var0x), BossInfo.Color.GREEN);
      }))).then(Commands.func_197057_a("yellow").executes((var0x) -> {
         return func_201415_a((CommandSource)var0x.getSource(), func_201416_a(var0x), BossInfo.Color.YELLOW);
      }))).then(Commands.func_197057_a("purple").executes((var0x) -> {
         return func_201415_a((CommandSource)var0x.getSource(), func_201416_a(var0x), BossInfo.Color.PURPLE);
      }))).then(Commands.func_197057_a("white").executes((var0x) -> {
         return func_201415_a((CommandSource)var0x.getSource(), func_201416_a(var0x), BossInfo.Color.WHITE);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("style").then(Commands.func_197057_a("progress").executes((var0x) -> {
         return func_201390_a((CommandSource)var0x.getSource(), func_201416_a(var0x), BossInfo.Overlay.PROGRESS);
      }))).then(Commands.func_197057_a("notched_6").executes((var0x) -> {
         return func_201390_a((CommandSource)var0x.getSource(), func_201416_a(var0x), BossInfo.Overlay.NOTCHED_6);
      }))).then(Commands.func_197057_a("notched_10").executes((var0x) -> {
         return func_201390_a((CommandSource)var0x.getSource(), func_201416_a(var0x), BossInfo.Overlay.NOTCHED_10);
      }))).then(Commands.func_197057_a("notched_12").executes((var0x) -> {
         return func_201390_a((CommandSource)var0x.getSource(), func_201416_a(var0x), BossInfo.Overlay.NOTCHED_12);
      }))).then(Commands.func_197057_a("notched_20").executes((var0x) -> {
         return func_201390_a((CommandSource)var0x.getSource(), func_201416_a(var0x), BossInfo.Overlay.NOTCHED_20);
      })))).then(Commands.func_197057_a("value").then(Commands.func_197056_a("value", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_201397_a((CommandSource)var0x.getSource(), func_201416_a(var0x), IntegerArgumentType.getInteger(var0x, "value"));
      })))).then(Commands.func_197057_a("max").then(Commands.func_197056_a("max", IntegerArgumentType.integer(1)).executes((var0x) -> {
         return func_201394_b((CommandSource)var0x.getSource(), func_201416_a(var0x), IntegerArgumentType.getInteger(var0x, "max"));
      })))).then(Commands.func_197057_a("visible").then(Commands.func_197056_a("visible", BoolArgumentType.bool()).executes((var0x) -> {
         return func_201410_a((CommandSource)var0x.getSource(), func_201416_a(var0x), BoolArgumentType.getBool(var0x, "visible"));
      })))).then(((LiteralArgumentBuilder)Commands.func_197057_a("players").executes((var0x) -> {
         return func_201405_a((CommandSource)var0x.getSource(), func_201416_a(var0x), Collections.emptyList());
      })).then(Commands.func_197056_a("targets", EntityArgument.func_197094_d()).executes((var0x) -> {
         return func_201405_a((CommandSource)var0x.getSource(), func_201416_a(var0x), EntityArgument.func_201309_d(var0x, "targets"));
      })))))).then(Commands.func_197057_a("get").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("id", ResourceLocationArgument.func_197197_a()).suggests(field_201431_a).then(Commands.func_197057_a("value").executes((var0x) -> {
         return func_201414_a((CommandSource)var0x.getSource(), func_201416_a(var0x));
      }))).then(Commands.func_197057_a("max").executes((var0x) -> {
         return func_201402_b((CommandSource)var0x.getSource(), func_201416_a(var0x));
      }))).then(Commands.func_197057_a("visible").executes((var0x) -> {
         return func_201389_c((CommandSource)var0x.getSource(), func_201416_a(var0x));
      }))).then(Commands.func_197057_a("players").executes((var0x) -> {
         return func_201425_d((CommandSource)var0x.getSource(), func_201416_a(var0x));
      })))));
   }

   private static int func_201414_a(CommandSource var0, CustomBossEvent var1) {
      var0.func_197030_a(new TextComponentTranslation("commands.bossbar.get.value", new Object[]{var1.func_201369_e(), var1.func_201365_c()}), true);
      return var1.func_201365_c();
   }

   private static int func_201402_b(CommandSource var0, CustomBossEvent var1) {
      var0.func_197030_a(new TextComponentTranslation("commands.bossbar.get.max", new Object[]{var1.func_201369_e(), var1.func_201367_d()}), true);
      return var1.func_201367_d();
   }

   private static int func_201389_c(CommandSource var0, CustomBossEvent var1) {
      if (var1.func_201359_g()) {
         var0.func_197030_a(new TextComponentTranslation("commands.bossbar.get.visible.visible", new Object[]{var1.func_201369_e()}), true);
         return 1;
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.bossbar.get.visible.hidden", new Object[]{var1.func_201369_e()}), true);
         return 0;
      }
   }

   private static int func_201425_d(CommandSource var0, CustomBossEvent var1) {
      if (var1.func_186757_c().isEmpty()) {
         var0.func_197030_a(new TextComponentTranslation("commands.bossbar.get.players.none", new Object[]{var1.func_201369_e()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.bossbar.get.players.some", new Object[]{var1.func_201369_e(), var1.func_186757_c().size(), TextComponentUtils.func_197677_b(var1.func_186757_c(), EntityPlayer::func_145748_c_)}), true);
      }

      return var1.func_186757_c().size();
   }

   private static int func_201410_a(CommandSource var0, CustomBossEvent var1, boolean var2) throws CommandSyntaxException {
      if (var1.func_201359_g() == var2) {
         if (var2) {
            throw field_201441_k.create();
         } else {
            throw field_201440_j.create();
         }
      } else {
         var1.func_186758_d(var2);
         if (var2) {
            var0.func_197030_a(new TextComponentTranslation("commands.bossbar.set.visible.success.visible", new Object[]{var1.func_201369_e()}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.bossbar.set.visible.success.hidden", new Object[]{var1.func_201369_e()}), true);
         }

         return 0;
      }
   }

   private static int func_201397_a(CommandSource var0, CustomBossEvent var1, int var2) throws CommandSyntaxException {
      if (var1.func_201365_c() == var2) {
         throw field_201438_h.create();
      } else {
         var1.func_201362_a(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.bossbar.set.value.success", new Object[]{var1.func_201369_e(), var2}), true);
         return var2;
      }
   }

   private static int func_201394_b(CommandSource var0, CustomBossEvent var1, int var2) throws CommandSyntaxException {
      if (var1.func_201367_d() == var2) {
         throw field_201439_i.create();
      } else {
         var1.func_201366_b(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.bossbar.set.max.success", new Object[]{var1.func_201369_e(), var2}), true);
         return var2;
      }
   }

   private static int func_201415_a(CommandSource var0, CustomBossEvent var1, BossInfo.Color var2) throws CommandSyntaxException {
      if (var1.func_186736_g().equals(var2)) {
         throw field_201436_f.create();
      } else {
         var1.func_186745_a(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.bossbar.set.color.success", new Object[]{var1.func_201369_e()}), true);
         return 0;
      }
   }

   private static int func_201390_a(CommandSource var0, CustomBossEvent var1, BossInfo.Overlay var2) throws CommandSyntaxException {
      if (var1.func_186740_h().equals(var2)) {
         throw field_201437_g.create();
      } else {
         var1.func_186746_a(var2);
         var0.func_197030_a(new TextComponentTranslation("commands.bossbar.set.style.success", new Object[]{var1.func_201369_e()}), true);
         return 0;
      }
   }

   private static int func_201420_a(CommandSource var0, CustomBossEvent var1, ITextComponent var2) throws CommandSyntaxException {
      ITextComponent var3 = TextComponentUtils.func_197680_a(var0, var2, (Entity)null);
      if (var1.func_186744_e().equals(var3)) {
         throw field_201435_e.create();
      } else {
         var1.func_186739_a(var3);
         var0.func_197030_a(new TextComponentTranslation("commands.bossbar.set.name.success", new Object[]{var1.func_201369_e()}), true);
         return 0;
      }
   }

   private static int func_201405_a(CommandSource var0, CustomBossEvent var1, Collection<EntityPlayerMP> var2) throws CommandSyntaxException {
      boolean var3 = var1.func_201368_a(var2);
      if (!var3) {
         throw field_201434_d.create();
      } else {
         if (var1.func_186757_c().isEmpty()) {
            var0.func_197030_a(new TextComponentTranslation("commands.bossbar.set.players.success.none", new Object[]{var1.func_201369_e()}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.bossbar.set.players.success.some", new Object[]{var1.func_201369_e(), var2.size(), TextComponentUtils.func_197677_b(var2, EntityPlayer::func_145748_c_)}), true);
         }

         return var1.func_186757_c().size();
      }
   }

   private static int func_201428_a(CommandSource var0) {
      Collection var1 = var0.func_197028_i().func_201300_aS().func_201378_b();
      if (var1.isEmpty()) {
         var0.func_197030_a(new TextComponentTranslation("commands.bossbar.list.bars.none", new Object[0]), false);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.bossbar.list.bars.some", new Object[]{var1.size(), TextComponentUtils.func_197677_b(var1, CustomBossEvent::func_201369_e)}), false);
      }

      return var1.size();
   }

   private static int func_201400_a(CommandSource var0, ResourceLocation var1, ITextComponent var2) throws CommandSyntaxException {
      CustomBossEvents var3 = var0.func_197028_i().func_201300_aS();
      if (var3.func_201384_a(var1) != null) {
         throw field_201432_b.create(var1.toString());
      } else {
         CustomBossEvent var4 = var3.func_201379_a(var1, TextComponentUtils.func_197680_a(var0, var2, (Entity)null));
         var0.func_197030_a(new TextComponentTranslation("commands.bossbar.create.success", new Object[]{var4.func_201369_e()}), true);
         return var3.func_201378_b().size();
      }
   }

   private static int func_201407_e(CommandSource var0, CustomBossEvent var1) {
      CustomBossEvents var2 = var0.func_197028_i().func_201300_aS();
      var1.func_201360_b();
      var2.func_201385_a(var1);
      var0.func_197030_a(new TextComponentTranslation("commands.bossbar.remove.success", new Object[]{var1.func_201369_e()}), true);
      return var2.func_201378_b().size();
   }

   public static CustomBossEvent func_201416_a(CommandContext<CommandSource> var0) throws CommandSyntaxException {
      ResourceLocation var1 = ResourceLocationArgument.func_197195_e(var0, "id");
      CustomBossEvent var2 = ((CommandSource)var0.getSource()).func_197028_i().func_201300_aS().func_201384_a(var1);
      if (var2 == null) {
         throw field_201433_c.create(var1.toString());
      } else {
         return var2;
      }
   }
}
