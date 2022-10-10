package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.PotionArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;

public class EffectCommand {
   private static final SimpleCommandExceptionType field_198361_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.effect.give.failed", new Object[0]));
   private static final SimpleCommandExceptionType field_198362_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.effect.clear.everything.failed", new Object[0]));
   private static final SimpleCommandExceptionType field_198363_c = new SimpleCommandExceptionType(new TextComponentTranslation("commands.effect.clear.specific.failed", new Object[0]));

   public static void func_198353_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("effect").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197057_a("clear").then(((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197093_b()).executes((var0x) -> {
         return func_198354_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"));
      })).then(Commands.func_197056_a("effect", PotionArgument.func_197126_a()).executes((var0x) -> {
         return func_198355_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), PotionArgument.func_197125_a(var0x, "effect"));
      }))))).then(Commands.func_197057_a("give").then(Commands.func_197056_a("targets", EntityArgument.func_197093_b()).then(((RequiredArgumentBuilder)Commands.func_197056_a("effect", PotionArgument.func_197126_a()).executes((var0x) -> {
         return func_198360_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), PotionArgument.func_197125_a(var0x, "effect"), (Integer)null, 0, true);
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("seconds", IntegerArgumentType.integer(1, 1000000)).executes((var0x) -> {
         return func_198360_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), PotionArgument.func_197125_a(var0x, "effect"), IntegerArgumentType.getInteger(var0x, "seconds"), 0, true);
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("amplifier", IntegerArgumentType.integer(0, 255)).executes((var0x) -> {
         return func_198360_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), PotionArgument.func_197125_a(var0x, "effect"), IntegerArgumentType.getInteger(var0x, "seconds"), IntegerArgumentType.getInteger(var0x, "amplifier"), true);
      })).then(Commands.func_197056_a("hideParticles", BoolArgumentType.bool()).executes((var0x) -> {
         return func_198360_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), PotionArgument.func_197125_a(var0x, "effect"), IntegerArgumentType.getInteger(var0x, "seconds"), IntegerArgumentType.getInteger(var0x, "amplifier"), !BoolArgumentType.getBool(var0x, "hideParticles"));
      }))))))));
   }

   private static int func_198360_a(CommandSource var0, Collection<? extends Entity> var1, Potion var2, @Nullable Integer var3, int var4, boolean var5) throws CommandSyntaxException {
      int var6 = 0;
      int var7;
      if (var3 != null) {
         if (var2.func_76403_b()) {
            var7 = var3;
         } else {
            var7 = var3 * 20;
         }
      } else if (var2.func_76403_b()) {
         var7 = 1;
      } else {
         var7 = 600;
      }

      Iterator var8 = var1.iterator();

      while(var8.hasNext()) {
         Entity var9 = (Entity)var8.next();
         if (var9 instanceof EntityLivingBase) {
            PotionEffect var10 = new PotionEffect(var2, var7, var4, false, var5);
            if (((EntityLivingBase)var9).func_195064_c(var10)) {
               ++var6;
            }
         }
      }

      if (var6 == 0) {
         throw field_198361_a.create();
      } else {
         if (var1.size() == 1) {
            var0.func_197030_a(new TextComponentTranslation("commands.effect.give.success.single", new Object[]{var2.func_199286_c(), ((Entity)var1.iterator().next()).func_145748_c_(), var7 / 20}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.effect.give.success.multiple", new Object[]{var2.func_199286_c(), var1.size(), var7 / 20}), true);
         }

         return var6;
      }
   }

   private static int func_198354_a(CommandSource var0, Collection<? extends Entity> var1) throws CommandSyntaxException {
      int var2 = 0;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Entity var4 = (Entity)var3.next();
         if (var4 instanceof EntityLivingBase && ((EntityLivingBase)var4).func_195061_cb()) {
            ++var2;
         }
      }

      if (var2 == 0) {
         throw field_198362_b.create();
      } else {
         if (var1.size() == 1) {
            var0.func_197030_a(new TextComponentTranslation("commands.effect.clear.everything.success.single", new Object[]{((Entity)var1.iterator().next()).func_145748_c_()}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.effect.clear.everything.success.multiple", new Object[]{var1.size()}), true);
         }

         return var2;
      }
   }

   private static int func_198355_a(CommandSource var0, Collection<? extends Entity> var1, Potion var2) throws CommandSyntaxException {
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         Entity var5 = (Entity)var4.next();
         if (var5 instanceof EntityLivingBase && ((EntityLivingBase)var5).func_195063_d(var2)) {
            ++var3;
         }
      }

      if (var3 == 0) {
         throw field_198363_c.create();
      } else {
         if (var1.size() == 1) {
            var0.func_197030_a(new TextComponentTranslation("commands.effect.clear.specific.success.single", new Object[]{var2.func_199286_c(), ((Entity)var1.iterator().next()).func_145748_c_()}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.effect.clear.specific.success.multiple", new Object[]{var2.func_199286_c(), var1.size()}), true);
         }

         return var3;
      }
   }
}
