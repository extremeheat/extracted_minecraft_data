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
import net.minecraft.server.management.UserListBans;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class PardonCommand {
   private static final SimpleCommandExceptionType field_198552_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.pardon.failed", new Object[0]));

   public static void func_198547_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("pardon").requires((var0x) -> {
         return var0x.func_197028_i().func_184103_al().func_72363_f().func_152689_b() && var0x.func_197034_c(3);
      })).then(Commands.func_197056_a("targets", GameProfileArgument.func_197108_a()).suggests((var0x, var1) -> {
         return ISuggestionProvider.func_197008_a(((CommandSource)var0x.getSource()).func_197028_i().func_184103_al().func_152608_h().func_152685_a(), var1);
      }).executes((var0x) -> {
         return func_198548_a((CommandSource)var0x.getSource(), GameProfileArgument.func_197109_a(var0x, "targets"));
      })));
   }

   private static int func_198548_a(CommandSource var0, Collection<GameProfile> var1) throws CommandSyntaxException {
      UserListBans var2 = var0.func_197028_i().func_184103_al().func_152608_h();
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         GameProfile var5 = (GameProfile)var4.next();
         if (var2.func_152702_a(var5)) {
            var2.func_152684_c(var5);
            ++var3;
            var0.func_197030_a(new TextComponentTranslation("commands.pardon.success", new Object[]{TextComponentUtils.func_197679_a(var5)}), true);
         }
      }

      if (var3 == 0) {
         throw field_198552_a.create();
      } else {
         return var3;
      }
   }
}
