package net.minecraft.command.impl;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextComponentUtils;

public class TagCommand {
   private static final SimpleCommandExceptionType field_198752_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.tag.add.failed", new Object[0]));
   private static final SimpleCommandExceptionType field_198753_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.tag.remove.failed", new Object[0]));

   public static void func_198743_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("tag").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197093_b()).then(Commands.func_197057_a("add").then(Commands.func_197056_a("name", StringArgumentType.word()).executes((var0x) -> {
         return func_198749_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), StringArgumentType.getString(var0x, "name"));
      })))).then(Commands.func_197057_a("remove").then(Commands.func_197056_a("name", StringArgumentType.word()).suggests((var0x, var1) -> {
         return ISuggestionProvider.func_197005_b(func_198748_a(EntityArgument.func_197097_b(var0x, "targets")), var1);
      }).executes((var0x) -> {
         return func_198750_b((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), StringArgumentType.getString(var0x, "name"));
      })))).then(Commands.func_197057_a("list").executes((var0x) -> {
         return func_198744_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"));
      }))));
   }

   private static Collection<String> func_198748_a(Collection<? extends Entity> var0) {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         var1.addAll(var3.func_184216_O());
      }

      return var1;
   }

   private static int func_198749_a(CommandSource var0, Collection<? extends Entity> var1, String var2) throws CommandSyntaxException {
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         Entity var5 = (Entity)var4.next();
         if (var5.func_184211_a(var2)) {
            ++var3;
         }
      }

      if (var3 == 0) {
         throw field_198752_a.create();
      } else {
         if (var1.size() == 1) {
            var0.func_197030_a(new TextComponentTranslation("commands.tag.add.success.single", new Object[]{var2, ((Entity)var1.iterator().next()).func_145748_c_()}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.tag.add.success.multiple", new Object[]{var2, var1.size()}), true);
         }

         return var3;
      }
   }

   private static int func_198750_b(CommandSource var0, Collection<? extends Entity> var1, String var2) throws CommandSyntaxException {
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         Entity var5 = (Entity)var4.next();
         if (var5.func_184197_b(var2)) {
            ++var3;
         }
      }

      if (var3 == 0) {
         throw field_198753_b.create();
      } else {
         if (var1.size() == 1) {
            var0.func_197030_a(new TextComponentTranslation("commands.tag.remove.success.single", new Object[]{var2, ((Entity)var1.iterator().next()).func_145748_c_()}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.tag.remove.success.multiple", new Object[]{var2, var1.size()}), true);
         }

         return var3;
      }
   }

   private static int func_198744_a(CommandSource var0, Collection<? extends Entity> var1) {
      HashSet var2 = Sets.newHashSet();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Entity var4 = (Entity)var3.next();
         var2.addAll(var4.func_184216_O());
      }

      if (var1.size() == 1) {
         Entity var5 = (Entity)var1.iterator().next();
         if (var2.isEmpty()) {
            var0.func_197030_a(new TextComponentTranslation("commands.tag.list.single.empty", new Object[]{var5.func_145748_c_()}), false);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.tag.list.single.success", new Object[]{var5.func_145748_c_(), var2.size(), TextComponentUtils.func_197678_a(var2)}), false);
         }
      } else if (var2.isEmpty()) {
         var0.func_197030_a(new TextComponentTranslation("commands.tag.list.multiple.empty", new Object[]{var1.size()}), false);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.tag.list.multiple.success", new Object[]{var1.size(), var2.size(), TextComponentUtils.func_197678_a(var2)}), false);
      }

      return var2.size();
   }
}
