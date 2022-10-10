package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.UserListIPBans;
import net.minecraft.server.management.UserListIPBansEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class BanIpCommand {
   public static final Pattern field_198225_a = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
   private static final SimpleCommandExceptionType field_198226_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.banip.invalid", new Object[0]));
   private static final SimpleCommandExceptionType field_198227_c = new SimpleCommandExceptionType(new TextComponentTranslation("commands.banip.failed", new Object[0]));

   public static void func_198220_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("ban-ip").requires((var0x) -> {
         return var0x.func_197028_i().func_184103_al().func_72363_f().func_152689_b() && var0x.func_197034_c(3);
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("target", StringArgumentType.word()).executes((var0x) -> {
         return func_198223_a((CommandSource)var0x.getSource(), StringArgumentType.getString(var0x, "target"), (ITextComponent)null);
      })).then(Commands.func_197056_a("reason", MessageArgument.func_197123_a()).executes((var0x) -> {
         return func_198223_a((CommandSource)var0x.getSource(), StringArgumentType.getString(var0x, "target"), MessageArgument.func_197124_a(var0x, "reason"));
      }))));
   }

   private static int func_198223_a(CommandSource var0, String var1, @Nullable ITextComponent var2) throws CommandSyntaxException {
      Matcher var3 = field_198225_a.matcher(var1);
      if (var3.matches()) {
         return func_198224_b(var0, var1, var2);
      } else {
         EntityPlayerMP var4 = var0.func_197028_i().func_184103_al().func_152612_a(var1);
         if (var4 != null) {
            return func_198224_b(var0, var4.func_71114_r(), var2);
         } else {
            throw field_198226_b.create();
         }
      }
   }

   private static int func_198224_b(CommandSource var0, String var1, @Nullable ITextComponent var2) throws CommandSyntaxException {
      UserListIPBans var3 = var0.func_197028_i().func_184103_al().func_72363_f();
      if (var3.func_199044_a(var1)) {
         throw field_198227_c.create();
      } else {
         List var4 = var0.func_197028_i().func_184103_al().func_72382_j(var1);
         UserListIPBansEntry var5 = new UserListIPBansEntry(var1, (Date)null, var0.func_197037_c(), (Date)null, var2 == null ? null : var2.getString());
         var3.func_152687_a(var5);
         var0.func_197030_a(new TextComponentTranslation("commands.banip.success", new Object[]{var1, var5.func_73686_f()}), true);
         if (!var4.isEmpty()) {
            var0.func_197030_a(new TextComponentTranslation("commands.banip.info", new Object[]{var4.size(), EntitySelector.func_197350_a(var4)}), true);
         }

         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            EntityPlayerMP var7 = (EntityPlayerMP)var6.next();
            var7.field_71135_a.func_194028_b(new TextComponentTranslation("multiplayer.disconnect.ip_banned", new Object[0]));
         }

         return var4.size();
      }
   }
}
