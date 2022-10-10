package net.minecraft.command.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.UserListEntryBan;
import net.minecraft.util.text.TextComponentTranslation;

public class BanListCommand {
   public static void func_198229_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("banlist").requires((var0x) -> {
         return (var0x.func_197028_i().func_184103_al().func_152608_h().func_152689_b() || var0x.func_197028_i().func_184103_al().func_72363_f().func_152689_b()) && var0x.func_197034_c(3);
      })).executes((var0x) -> {
         PlayerList var1 = ((CommandSource)var0x.getSource()).func_197028_i().func_184103_al();
         return func_198230_a((CommandSource)var0x.getSource(), Lists.newArrayList(Iterables.concat(var1.func_152608_h().func_199043_f(), var1.func_72363_f().func_199043_f())));
      })).then(Commands.func_197057_a("ips").executes((var0x) -> {
         return func_198230_a((CommandSource)var0x.getSource(), ((CommandSource)var0x.getSource()).func_197028_i().func_184103_al().func_72363_f().func_199043_f());
      }))).then(Commands.func_197057_a("players").executes((var0x) -> {
         return func_198230_a((CommandSource)var0x.getSource(), ((CommandSource)var0x.getSource()).func_197028_i().func_184103_al().func_152608_h().func_199043_f());
      })));
   }

   private static int func_198230_a(CommandSource var0, Collection<? extends UserListEntryBan<?>> var1) {
      if (var1.isEmpty()) {
         var0.func_197030_a(new TextComponentTranslation("commands.banlist.none", new Object[0]), false);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.banlist.list", new Object[]{var1.size()}), false);
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            UserListEntryBan var3 = (UserListEntryBan)var2.next();
            var0.func_197030_a(new TextComponentTranslation("commands.banlist.entry", new Object[]{var3.func_199041_e(), var3.func_199040_b(), var3.func_73686_f()}), false);
         }
      }

      return var1.size();
   }
}
