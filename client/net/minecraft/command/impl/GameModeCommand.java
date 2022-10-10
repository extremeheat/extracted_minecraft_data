package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

public class GameModeCommand {
   public static void func_198482_a(CommandDispatcher<CommandSource> var0) {
      LiteralArgumentBuilder var1 = (LiteralArgumentBuilder)Commands.func_197057_a("gamemode").requires((var0x) -> {
         return var0x.func_197034_c(2);
      });
      GameType[] var2 = GameType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameType var5 = var2[var4];
         if (var5 != GameType.NOT_SET) {
            var1.then(((LiteralArgumentBuilder)Commands.func_197057_a(var5.func_77149_b()).executes((var1x) -> {
               return func_198484_a(var1x, Collections.singleton(((CommandSource)var1x.getSource()).func_197035_h()), var5);
            })).then(Commands.func_197056_a("target", EntityArgument.func_197094_d()).executes((var1x) -> {
               return func_198484_a(var1x, EntityArgument.func_197090_e(var1x, "target"), var5);
            })));
         }
      }

      var0.register(var1);
   }

   private static void func_208517_a(CommandSource var0, EntityPlayerMP var1, GameType var2) {
      TextComponentTranslation var3 = new TextComponentTranslation("gameMode." + var2.func_77149_b(), new Object[0]);
      if (var0.func_197022_f() == var1) {
         var0.func_197030_a(new TextComponentTranslation("commands.gamemode.success.self", new Object[]{var3}), true);
      } else {
         if (var0.func_197023_e().func_82736_K().func_82766_b("sendCommandFeedback")) {
            var1.func_145747_a(new TextComponentTranslation("gameMode.changed", new Object[]{var3}));
         }

         var0.func_197030_a(new TextComponentTranslation("commands.gamemode.success.other", new Object[]{var1.func_145748_c_(), var3}), true);
      }

   }

   private static int func_198484_a(CommandContext<CommandSource> var0, Collection<EntityPlayerMP> var1, GameType var2) {
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         EntityPlayerMP var5 = (EntityPlayerMP)var4.next();
         if (var5.field_71134_c.func_73081_b() != var2) {
            var5.func_71033_a(var2);
            func_208517_a((CommandSource)var0.getSource(), var5, var2);
            ++var3;
         }
      }

      return var3;
   }
}
