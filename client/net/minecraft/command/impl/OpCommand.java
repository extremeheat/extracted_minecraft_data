package net.minecraft.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentTranslation;

public class OpCommand {
   private static final SimpleCommandExceptionType field_198546_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.op.failed", new Object[0]));

   public static void func_198541_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("op").requires((var0x) -> {
         return var0x.func_197034_c(3);
      })).then(Commands.func_197056_a("targets", GameProfileArgument.func_197108_a()).suggests((var0x, var1) -> {
         PlayerList var2 = ((CommandSource)var0x.getSource()).func_197028_i().func_184103_al();
         return ISuggestionProvider.func_197013_a(var2.func_181057_v().stream().filter((var1x) -> {
            return !var2.func_152596_g(var1x.func_146103_bH());
         }).map((var0) -> {
            return var0.func_146103_bH().getName();
         }), var1);
      }).executes((var0x) -> {
         return func_198542_a((CommandSource)var0x.getSource(), GameProfileArgument.func_197109_a(var0x, "targets"));
      })));
   }

   private static int func_198542_a(CommandSource var0, Collection<GameProfile> var1) throws CommandSyntaxException {
      PlayerList var2 = var0.func_197028_i().func_184103_al();
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         GameProfile var5 = (GameProfile)var4.next();
         if (!var2.func_152596_g(var5)) {
            var2.func_152605_a(var5);
            ++var3;
            var0.func_197030_a(new TextComponentTranslation("commands.op.success", new Object[]{((GameProfile)var1.iterator().next()).getName()}), true);
         }
      }

      if (var3 == 0) {
         throw field_198546_a.create();
      } else {
         return var3;
      }
   }
}
