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
import net.minecraft.server.management.UserListWhitelist;
import net.minecraft.server.management.UserListWhitelistEntry;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class WhitelistCommand {
   private static final SimpleCommandExceptionType field_198887_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.whitelist.alreadyOn", new Object[0]));
   private static final SimpleCommandExceptionType field_198888_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.whitelist.alreadyOff", new Object[0]));
   private static final SimpleCommandExceptionType field_198889_c = new SimpleCommandExceptionType(new TextComponentTranslation("commands.whitelist.add.failed", new Object[0]));
   private static final SimpleCommandExceptionType field_198890_d = new SimpleCommandExceptionType(new TextComponentTranslation("commands.whitelist.remove.failed", new Object[0]));

   public static void func_198873_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("whitelist").requires((var0x) -> {
         return var0x.func_197034_c(3);
      })).then(Commands.func_197057_a("on").executes((var0x) -> {
         return func_198884_b((CommandSource)var0x.getSource());
      }))).then(Commands.func_197057_a("off").executes((var0x) -> {
         return func_198885_c((CommandSource)var0x.getSource());
      }))).then(Commands.func_197057_a("list").executes((var0x) -> {
         return func_198886_d((CommandSource)var0x.getSource());
      }))).then(Commands.func_197057_a("add").then(Commands.func_197056_a("targets", GameProfileArgument.func_197108_a()).suggests((var0x, var1) -> {
         PlayerList var2 = ((CommandSource)var0x.getSource()).func_197028_i().func_184103_al();
         return ISuggestionProvider.func_197013_a(var2.func_181057_v().stream().filter((var1x) -> {
            return !var2.func_152599_k().func_152705_a(var1x.func_146103_bH());
         }).map((var0) -> {
            return var0.func_146103_bH().getName();
         }), var1);
      }).executes((var0x) -> {
         return func_198880_a((CommandSource)var0x.getSource(), GameProfileArgument.func_197109_a(var0x, "targets"));
      })))).then(Commands.func_197057_a("remove").then(Commands.func_197056_a("targets", GameProfileArgument.func_197108_a()).suggests((var0x, var1) -> {
         return ISuggestionProvider.func_197008_a(((CommandSource)var0x.getSource()).func_197028_i().func_184103_al().func_152598_l(), var1);
      }).executes((var0x) -> {
         return func_198876_b((CommandSource)var0x.getSource(), GameProfileArgument.func_197109_a(var0x, "targets"));
      })))).then(Commands.func_197057_a("reload").executes((var0x) -> {
         return func_198883_a((CommandSource)var0x.getSource());
      })));
   }

   private static int func_198883_a(CommandSource var0) {
      var0.func_197028_i().func_184103_al().func_187244_a();
      var0.func_197030_a(new TextComponentTranslation("commands.whitelist.reloaded", new Object[0]), true);
      var0.func_197028_i().func_205743_a(var0);
      return 1;
   }

   private static int func_198880_a(CommandSource var0, Collection<GameProfile> var1) throws CommandSyntaxException {
      UserListWhitelist var2 = var0.func_197028_i().func_184103_al().func_152599_k();
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         GameProfile var5 = (GameProfile)var4.next();
         if (!var2.func_152705_a(var5)) {
            UserListWhitelistEntry var6 = new UserListWhitelistEntry(var5);
            var2.func_152687_a(var6);
            var0.func_197030_a(new TextComponentTranslation("commands.whitelist.add.success", new Object[]{TextComponentUtils.func_197679_a(var5)}), true);
            ++var3;
         }
      }

      if (var3 == 0) {
         throw field_198889_c.create();
      } else {
         return var3;
      }
   }

   private static int func_198876_b(CommandSource var0, Collection<GameProfile> var1) throws CommandSyntaxException {
      UserListWhitelist var2 = var0.func_197028_i().func_184103_al().func_152599_k();
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         GameProfile var5 = (GameProfile)var4.next();
         if (var2.func_152705_a(var5)) {
            UserListWhitelistEntry var6 = new UserListWhitelistEntry(var5);
            var2.func_199042_b(var6);
            var0.func_197030_a(new TextComponentTranslation("commands.whitelist.remove.success", new Object[]{TextComponentUtils.func_197679_a(var5)}), true);
            ++var3;
         }
      }

      if (var3 == 0) {
         throw field_198890_d.create();
      } else {
         var0.func_197028_i().func_205743_a(var0);
         return var3;
      }
   }

   private static int func_198884_b(CommandSource var0) throws CommandSyntaxException {
      PlayerList var1 = var0.func_197028_i().func_184103_al();
      if (var1.func_72383_n()) {
         throw field_198887_a.create();
      } else {
         var1.func_72371_a(true);
         var0.func_197030_a(new TextComponentTranslation("commands.whitelist.enabled", new Object[0]), true);
         var0.func_197028_i().func_205743_a(var0);
         return 1;
      }
   }

   private static int func_198885_c(CommandSource var0) throws CommandSyntaxException {
      PlayerList var1 = var0.func_197028_i().func_184103_al();
      if (!var1.func_72383_n()) {
         throw field_198888_b.create();
      } else {
         var1.func_72371_a(false);
         var0.func_197030_a(new TextComponentTranslation("commands.whitelist.disabled", new Object[0]), true);
         return 1;
      }
   }

   private static int func_198886_d(CommandSource var0) {
      String[] var1 = var0.func_197028_i().func_184103_al().func_152598_l();
      if (var1.length == 0) {
         var0.func_197030_a(new TextComponentTranslation("commands.whitelist.none", new Object[0]), false);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.whitelist.list", new Object[]{var1.length, String.join(", ", var1)}), false);
      }

      return var1.length;
   }
}
