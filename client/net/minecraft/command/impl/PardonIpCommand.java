package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.regex.Matcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.server.management.UserListIPBans;
import net.minecraft.util.text.TextComponentTranslation;

public class PardonIpCommand {
   private static final SimpleCommandExceptionType field_198558_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.pardonip.invalid", new Object[0]));
   private static final SimpleCommandExceptionType field_198559_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.pardonip.failed", new Object[0]));

   public static void func_198553_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("pardon-ip").requires((var0x) -> {
         return var0x.func_197028_i().func_184103_al().func_72363_f().func_152689_b() && var0x.func_197034_c(3);
      })).then(Commands.func_197056_a("target", StringArgumentType.word()).suggests((var0x, var1) -> {
         return ISuggestionProvider.func_197008_a(((CommandSource)var0x.getSource()).func_197028_i().func_184103_al().func_72363_f().func_152685_a(), var1);
      }).executes((var0x) -> {
         return func_198557_a((CommandSource)var0x.getSource(), StringArgumentType.getString(var0x, "target"));
      })));
   }

   private static int func_198557_a(CommandSource var0, String var1) throws CommandSyntaxException {
      Matcher var2 = BanIpCommand.field_198225_a.matcher(var1);
      if (!var2.matches()) {
         throw field_198558_a.create();
      } else {
         UserListIPBans var3 = var0.func_197028_i().func_184103_al().func_72363_f();
         if (!var3.func_199044_a(var1)) {
            throw field_198559_b.create();
         } else {
            var3.func_152684_c(var1);
            var0.func_197030_a(new TextComponentTranslation("commands.pardonip.success", new Object[]{var1}), true);
            return 1;
         }
      }
   }
}
