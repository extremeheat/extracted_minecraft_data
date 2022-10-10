package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class TimeCommand {
   public static void func_198823_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("time").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("set").then(Commands.func_197057_a("day").executes((var0x) -> {
         return func_198829_a((CommandSource)var0x.getSource(), 1000);
      }))).then(Commands.func_197057_a("noon").executes((var0x) -> {
         return func_198829_a((CommandSource)var0x.getSource(), 6000);
      }))).then(Commands.func_197057_a("night").executes((var0x) -> {
         return func_198829_a((CommandSource)var0x.getSource(), 13000);
      }))).then(Commands.func_197057_a("midnight").executes((var0x) -> {
         return func_198829_a((CommandSource)var0x.getSource(), 18000);
      }))).then(Commands.func_197056_a("time", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_198829_a((CommandSource)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "time"));
      })))).then(Commands.func_197057_a("add").then(Commands.func_197056_a("time", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_198826_b((CommandSource)var0x.getSource(), IntegerArgumentType.getInteger(var0x, "time"));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("query").then(Commands.func_197057_a("daytime").executes((var0x) -> {
         return func_198824_c((CommandSource)var0x.getSource(), func_198833_a(((CommandSource)var0x.getSource()).func_197023_e()));
      }))).then(Commands.func_197057_a("gametime").executes((var0x) -> {
         return func_198824_c((CommandSource)var0x.getSource(), (int)(((CommandSource)var0x.getSource()).func_197023_e().func_82737_E() % 2147483647L));
      }))).then(Commands.func_197057_a("day").executes((var0x) -> {
         return func_198824_c((CommandSource)var0x.getSource(), (int)(((CommandSource)var0x.getSource()).func_197023_e().func_72820_D() / 24000L % 2147483647L));
      }))));
   }

   private static int func_198833_a(WorldServer var0) {
      return (int)(var0.func_72820_D() % 24000L);
   }

   private static int func_198824_c(CommandSource var0, int var1) {
      var0.func_197030_a(new TextComponentTranslation("commands.time.query", new Object[]{var1}), false);
      return var1;
   }

   public static int func_198829_a(CommandSource var0, int var1) {
      Iterator var2 = var0.func_197028_i().func_212370_w().iterator();

      while(var2.hasNext()) {
         WorldServer var3 = (WorldServer)var2.next();
         var3.func_72877_b((long)var1);
      }

      var0.func_197030_a(new TextComponentTranslation("commands.time.set", new Object[]{var1}), true);
      return func_198833_a(var0.func_197023_e());
   }

   public static int func_198826_b(CommandSource var0, int var1) {
      Iterator var2 = var0.func_197028_i().func_212370_w().iterator();

      while(var2.hasNext()) {
         WorldServer var3 = (WorldServer)var2.next();
         var3.func_72877_b(var3.func_72820_D() + (long)var1);
      }

      int var4 = func_198833_a(var0.func_197023_e());
      var0.func_197030_a(new TextComponentTranslation("commands.time.set", new Object[]{var4}), true);
      return var4;
   }
}
