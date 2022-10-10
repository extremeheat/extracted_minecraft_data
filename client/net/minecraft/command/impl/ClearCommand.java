package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemPredicateArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

public class ClearCommand {
   private static final DynamicCommandExceptionType field_198249_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("clear.failed.single", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType field_198250_b = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("clear.failed.multiple", new Object[]{var0});
   });

   public static void func_198243_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("clear").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).executes((var0x) -> {
         return func_198244_a((CommandSource)var0x.getSource(), Collections.singleton(((CommandSource)var0x.getSource()).func_197035_h()), (var0) -> {
            return true;
         }, -1);
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197094_d()).executes((var0x) -> {
         return func_198244_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), (var0) -> {
            return true;
         }, -1);
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("item", ItemPredicateArgument.func_199846_a()).executes((var0x) -> {
         return func_198244_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), ItemPredicateArgument.func_199847_a(var0x, "item"), -1);
      })).then(Commands.func_197056_a("maxCount", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_198244_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), ItemPredicateArgument.func_199847_a(var0x, "item"), IntegerArgumentType.getInteger(var0x, "maxCount"));
      })))));
   }

   private static int func_198244_a(CommandSource var0, Collection<EntityPlayerMP> var1, Predicate<ItemStack> var2, int var3) throws CommandSyntaxException {
      int var4 = 0;

      EntityPlayerMP var6;
      for(Iterator var5 = var1.iterator(); var5.hasNext(); var4 += var6.field_71071_by.func_195408_a(var2, var3)) {
         var6 = (EntityPlayerMP)var5.next();
      }

      if (var4 == 0) {
         if (var1.size() == 1) {
            throw field_198249_a.create(((EntityPlayerMP)var1.iterator().next()).func_200200_C_().func_150254_d());
         } else {
            throw field_198250_b.create(var1.size());
         }
      } else {
         if (var3 == 0) {
            if (var1.size() == 1) {
               var0.func_197030_a(new TextComponentTranslation("commands.clear.test.single", new Object[]{var4, ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
            } else {
               var0.func_197030_a(new TextComponentTranslation("commands.clear.test.multiple", new Object[]{var4, var1.size()}), true);
            }
         } else if (var1.size() == 1) {
            var0.func_197030_a(new TextComponentTranslation("commands.clear.success.single", new Object[]{var4, ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.clear.success.multiple", new Object[]{var4, var1.size()}), true);
         }

         return var4;
      }
   }
}
