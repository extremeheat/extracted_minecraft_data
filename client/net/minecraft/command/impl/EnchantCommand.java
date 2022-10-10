package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EnchantmentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

public class EnchantCommand {
   private static final DynamicCommandExceptionType field_202652_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.enchant.failed.entity", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType field_202653_b = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.enchant.failed.itemless", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType field_202654_c = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.enchant.failed.incompatible", new Object[]{var0});
   });
   private static final Dynamic2CommandExceptionType field_202655_d = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("commands.enchant.failed.level", new Object[]{var0, var1});
   });
   private static final SimpleCommandExceptionType field_202656_e = new SimpleCommandExceptionType(new TextComponentTranslation("commands.enchant.failed", new Object[0]));

   public static void func_202649_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("enchant").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197056_a("targets", EntityArgument.func_197093_b()).then(((RequiredArgumentBuilder)Commands.func_197056_a("enchantment", EnchantmentArgument.func_201945_a()).executes((var0x) -> {
         return func_202651_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), EnchantmentArgument.func_201944_a(var0x, "enchantment"), 1);
      })).then(Commands.func_197056_a("level", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_202651_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), EnchantmentArgument.func_201944_a(var0x, "enchantment"), IntegerArgumentType.getInteger(var0x, "level"));
      })))));
   }

   private static int func_202651_a(CommandSource var0, Collection<? extends Entity> var1, Enchantment var2, int var3) throws CommandSyntaxException {
      if (var3 > var2.func_77325_b()) {
         throw field_202655_d.create(var3, var2.func_77325_b());
      } else {
         int var4 = 0;
         Iterator var5 = var1.iterator();

         while(true) {
            while(true) {
               while(true) {
                  while(var5.hasNext()) {
                     Entity var6 = (Entity)var5.next();
                     if (var6 instanceof EntityLivingBase) {
                        EntityLivingBase var7 = (EntityLivingBase)var6;
                        ItemStack var8 = var7.func_184614_ca();
                        if (!var8.func_190926_b()) {
                           if (var2.func_92089_a(var8) && EnchantmentHelper.func_201840_a(EnchantmentHelper.func_82781_a(var8).keySet(), var2)) {
                              var8.func_77966_a(var2, var3);
                              ++var4;
                           } else if (var1.size() == 1) {
                              throw field_202654_c.create(var8.func_77973_b().func_200295_i(var8).getString());
                           }
                        } else if (var1.size() == 1) {
                           throw field_202653_b.create(var7.func_200200_C_().getString());
                        }
                     } else if (var1.size() == 1) {
                        throw field_202652_a.create(var6.func_200200_C_().getString());
                     }
                  }

                  if (var4 == 0) {
                     throw field_202656_e.create();
                  }

                  if (var1.size() == 1) {
                     var0.func_197030_a(new TextComponentTranslation("commands.enchant.success.single", new Object[]{var2.func_200305_d(var3), ((Entity)var1.iterator().next()).func_145748_c_()}), true);
                  } else {
                     var0.func_197030_a(new TextComponentTranslation("commands.enchant.success.multiple", new Object[]{var2.func_200305_d(var3), var1.size()}), true);
                  }

                  return var4;
               }
            }
         }
      }
   }
}
