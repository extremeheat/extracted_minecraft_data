package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class TitleCommand {
   public static void func_198839_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("title").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197094_d()).then(Commands.func_197057_a("clear").executes((var0x) -> {
         return func_198840_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"));
      }))).then(Commands.func_197057_a("reset").executes((var0x) -> {
         return func_198844_b((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"));
      }))).then(Commands.func_197057_a("title").then(Commands.func_197056_a("title", ComponentArgument.func_197067_a()).executes((var0x) -> {
         return func_198846_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), ComponentArgument.func_197068_a(var0x, "title"), SPacketTitle.Type.TITLE);
      })))).then(Commands.func_197057_a("subtitle").then(Commands.func_197056_a("title", ComponentArgument.func_197067_a()).executes((var0x) -> {
         return func_198846_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), ComponentArgument.func_197068_a(var0x, "title"), SPacketTitle.Type.SUBTITLE);
      })))).then(Commands.func_197057_a("actionbar").then(Commands.func_197056_a("title", ComponentArgument.func_197067_a()).executes((var0x) -> {
         return func_198846_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), ComponentArgument.func_197068_a(var0x, "title"), SPacketTitle.Type.ACTIONBAR);
      })))).then(Commands.func_197057_a("times").then(Commands.func_197056_a("fadeIn", IntegerArgumentType.integer(0)).then(Commands.func_197056_a("stay", IntegerArgumentType.integer(0)).then(Commands.func_197056_a("fadeOut", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_198845_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), IntegerArgumentType.getInteger(var0x, "fadeIn"), IntegerArgumentType.getInteger(var0x, "stay"), IntegerArgumentType.getInteger(var0x, "fadeOut"));
      })))))));
   }

   private static int func_198840_a(CommandSource var0, Collection<EntityPlayerMP> var1) {
      SPacketTitle var2 = new SPacketTitle(SPacketTitle.Type.CLEAR, (ITextComponent)null);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
         var4.field_71135_a.func_147359_a(var2);
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.title.cleared.single", new Object[]{((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.title.cleared.multiple", new Object[]{var1.size()}), true);
      }

      return var1.size();
   }

   private static int func_198844_b(CommandSource var0, Collection<EntityPlayerMP> var1) {
      SPacketTitle var2 = new SPacketTitle(SPacketTitle.Type.RESET, (ITextComponent)null);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
         var4.field_71135_a.func_147359_a(var2);
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.title.reset.single", new Object[]{((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.title.reset.multiple", new Object[]{var1.size()}), true);
      }

      return var1.size();
   }

   private static int func_198846_a(CommandSource var0, Collection<EntityPlayerMP> var1, ITextComponent var2, SPacketTitle.Type var3) throws CommandSyntaxException {
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         EntityPlayerMP var5 = (EntityPlayerMP)var4.next();
         var5.field_71135_a.func_147359_a(new SPacketTitle(var3, TextComponentUtils.func_197680_a(var0, var2, var5)));
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.title.show." + var3.name().toLowerCase(Locale.ROOT) + ".single", new Object[]{((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.title.show." + var3.name().toLowerCase(Locale.ROOT) + ".multiple", new Object[]{var1.size()}), true);
      }

      return var1.size();
   }

   private static int func_198845_a(CommandSource var0, Collection<EntityPlayerMP> var1, int var2, int var3, int var4) {
      SPacketTitle var5 = new SPacketTitle(var2, var3, var4);
      Iterator var6 = var1.iterator();

      while(var6.hasNext()) {
         EntityPlayerMP var7 = (EntityPlayerMP)var6.next();
         var7.field_71135_a.func_147359_a(var5);
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.title.times.single", new Object[]{((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.title.times.multiple", new Object[]{var1.size()}), true);
      }

      return var1.size();
   }
}
