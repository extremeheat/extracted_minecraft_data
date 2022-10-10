package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class LocateCommand {
   private static final SimpleCommandExceptionType field_198536_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.locate.failed", new Object[0]));

   public static void func_198528_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("locate").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197057_a("Village").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "Village");
      }))).then(Commands.func_197057_a("Mineshaft").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "Mineshaft");
      }))).then(Commands.func_197057_a("Mansion").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "Mansion");
      }))).then(Commands.func_197057_a("Igloo").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "Igloo");
      }))).then(Commands.func_197057_a("Desert_Pyramid").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "Desert_Pyramid");
      }))).then(Commands.func_197057_a("Jungle_Pyramid").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "Jungle_Pyramid");
      }))).then(Commands.func_197057_a("Swamp_Hut").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "Swamp_Hut");
      }))).then(Commands.func_197057_a("Stronghold").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "Stronghold");
      }))).then(Commands.func_197057_a("Monument").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "Monument");
      }))).then(Commands.func_197057_a("Fortress").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "Fortress");
      }))).then(Commands.func_197057_a("EndCity").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "EndCity");
      }))).then(Commands.func_197057_a("Ocean_Ruin").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "Ocean_Ruin");
      }))).then(Commands.func_197057_a("Buried_Treasure").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "Buried_Treasure");
      }))).then(Commands.func_197057_a("Shipwreck").executes((var0x) -> {
         return func_198534_a((CommandSource)var0x.getSource(), "Shipwreck");
      })));
   }

   private static int func_198534_a(CommandSource var0, String var1) throws CommandSyntaxException {
      BlockPos var2 = new BlockPos(var0.func_197036_d());
      BlockPos var3 = var0.func_197023_e().func_211157_a(var1, var2, 100, false);
      if (var3 == null) {
         throw field_198536_a.create();
      } else {
         int var4 = MathHelper.func_76141_d(func_211907_a(var2.func_177958_n(), var2.func_177952_p(), var3.func_177958_n(), var3.func_177952_p()));
         ITextComponent var5 = TextComponentUtils.func_197676_a(new TextComponentTranslation("chat.coordinates", new Object[]{var3.func_177958_n(), "~", var3.func_177952_p()})).func_211710_a((var1x) -> {
            var1x.func_150238_a(TextFormatting.GREEN).func_150241_a(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + var3.func_177958_n() + " ~ " + var3.func_177952_p())).func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentTranslation("chat.coordinates.tooltip", new Object[0])));
         });
         var0.func_197030_a(new TextComponentTranslation("commands.locate.success", new Object[]{var1, var5, var4}), false);
         return var4;
      }
   }

   private static float func_211907_a(int var0, int var1, int var2, int var3) {
      int var4 = var2 - var0;
      int var5 = var3 - var1;
      return MathHelper.func_76129_c((float)(var4 * var4 + var5 * var5));
   }
}
