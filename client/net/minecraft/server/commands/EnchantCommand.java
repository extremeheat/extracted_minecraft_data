package net.minecraft.server.commands;

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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantCommand {
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.enchant.failed.entity", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.enchant.failed.itemless", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.enchant.failed.incompatible", new Object[]{var0});
   });
   private static final Dynamic2CommandExceptionType ERROR_LEVEL_TOO_HIGH = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.enchant.failed.level", new Object[]{var0, var1});
   });
   private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(new TranslatableComponent("commands.enchant.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("enchant").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("enchantment", ItemEnchantmentArgument.enchantment()).executes((var0x) -> {
         return enchant((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ItemEnchantmentArgument.getEnchantment(var0x, "enchantment"), 1);
      })).then(Commands.argument("level", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return enchant((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ItemEnchantmentArgument.getEnchantment(var0x, "enchantment"), IntegerArgumentType.getInteger(var0x, "level"));
      })))));
   }

   private static int enchant(CommandSourceStack var0, Collection<? extends Entity> var1, Enchantment var2, int var3) throws CommandSyntaxException {
      if (var3 > var2.getMaxLevel()) {
         throw ERROR_LEVEL_TOO_HIGH.create(var3, var2.getMaxLevel());
      } else {
         int var4 = 0;
         Iterator var5 = var1.iterator();

         while(true) {
            while(true) {
               while(true) {
                  while(var5.hasNext()) {
                     Entity var6 = (Entity)var5.next();
                     if (var6 instanceof LivingEntity) {
                        LivingEntity var7 = (LivingEntity)var6;
                        ItemStack var8 = var7.getMainHandItem();
                        if (!var8.isEmpty()) {
                           if (var2.canEnchant(var8) && EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantments(var8).keySet(), var2)) {
                              var8.enchant(var2, var3);
                              ++var4;
                           } else if (var1.size() == 1) {
                              throw ERROR_INCOMPATIBLE.create(var8.getItem().getName(var8).getString());
                           }
                        } else if (var1.size() == 1) {
                           throw ERROR_NO_ITEM.create(var7.getName().getString());
                        }
                     } else if (var1.size() == 1) {
                        throw ERROR_NOT_LIVING_ENTITY.create(var6.getName().getString());
                     }
                  }

                  if (var4 == 0) {
                     throw ERROR_NOTHING_HAPPENED.create();
                  }

                  if (var1.size() == 1) {
                     var0.sendSuccess(new TranslatableComponent("commands.enchant.success.single", new Object[]{var2.getFullname(var3), ((Entity)var1.iterator().next()).getDisplayName()}), true);
                  } else {
                     var0.sendSuccess(new TranslatableComponent("commands.enchant.success.multiple", new Object[]{var2.getFullname(var3), var1.size()}), true);
                  }

                  return var4;
               }
            }
         }
      }
   }
}
