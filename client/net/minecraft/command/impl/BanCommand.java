package net.minecraft.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.UserListBans;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class BanCommand {
   private static final SimpleCommandExceptionType field_198239_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.ban.failed", new Object[0]));

   public static void func_198235_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("ban").requires((var0x) -> {
         return var0x.func_197028_i().func_184103_al().func_152608_h().func_152689_b() && var0x.func_197034_c(3);
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("targets", GameProfileArgument.func_197108_a()).executes((var0x) -> {
         return func_198236_a((CommandSource)var0x.getSource(), GameProfileArgument.func_197109_a(var0x, "targets"), (ITextComponent)null);
      })).then(Commands.func_197056_a("reason", MessageArgument.func_197123_a()).executes((var0x) -> {
         return func_198236_a((CommandSource)var0x.getSource(), GameProfileArgument.func_197109_a(var0x, "targets"), MessageArgument.func_197124_a(var0x, "reason"));
      }))));
   }

   private static int func_198236_a(CommandSource var0, Collection<GameProfile> var1, @Nullable ITextComponent var2) throws CommandSyntaxException {
      UserListBans var3 = var0.func_197028_i().func_184103_al().func_152608_h();
      int var4 = 0;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         GameProfile var6 = (GameProfile)var5.next();
         if (!var3.func_152702_a(var6)) {
            UserListBansEntry var7 = new UserListBansEntry(var6, (Date)null, var0.func_197037_c(), (Date)null, var2 == null ? null : var2.getString());
            var3.func_152687_a(var7);
            ++var4;
            var0.func_197030_a(new TextComponentTranslation("commands.ban.success", new Object[]{TextComponentUtils.func_197679_a(var6), var7.func_73686_f()}), true);
            EntityPlayerMP var8 = var0.func_197028_i().func_184103_al().func_177451_a(var6.getId());
            if (var8 != null) {
               var8.field_71135_a.func_194028_b(new TextComponentTranslation("multiplayer.disconnect.banned", new Object[0]));
            }
         }
      }

      if (var4 == 0) {
         throw field_198239_a.create();
      } else {
         return var4;
      }
   }
}
