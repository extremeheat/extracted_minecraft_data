package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;

public class DefaultGameModeCommand {
   public static void func_198340_a(CommandDispatcher<CommandSource> var0) {
      LiteralArgumentBuilder var1 = (LiteralArgumentBuilder)Commands.func_197057_a("defaultgamemode").requires((var0x) -> {
         return var0x.func_197034_c(2);
      });
      GameType[] var2 = GameType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameType var5 = var2[var4];
         if (var5 != GameType.NOT_SET) {
            var1.then(Commands.func_197057_a(var5.func_77149_b()).executes((var1x) -> {
               return func_198341_a((CommandSource)var1x.getSource(), var5);
            }));
         }
      }

      var0.register(var1);
   }

   private static int func_198341_a(CommandSource var0, GameType var1) {
      int var2 = 0;
      MinecraftServer var3 = var0.func_197028_i();
      var3.func_71235_a(var1);
      if (var3.func_104056_am()) {
         Iterator var4 = var3.func_184103_al().func_181057_v().iterator();

         while(var4.hasNext()) {
            EntityPlayerMP var5 = (EntityPlayerMP)var4.next();
            if (var5.field_71134_c.func_73081_b() != var1) {
               var5.func_71033_a(var1);
               ++var2;
            }
         }
      }

      var0.func_197030_a(new TextComponentTranslation("commands.defaultgamemode.success", new Object[]{var1.func_196220_c()}), true);
      return var2;
   }
}
