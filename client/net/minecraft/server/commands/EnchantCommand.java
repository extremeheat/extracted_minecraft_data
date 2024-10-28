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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantCommand {
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.enchant.failed.entity", var0);
   });
   private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.enchant.failed.itemless", var0);
   });
   private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.enchant.failed.incompatible", var0);
   });
   private static final Dynamic2CommandExceptionType ERROR_LEVEL_TOO_HIGH = new Dynamic2CommandExceptionType((var0, var1) -> {
      return Component.translatableEscape("commands.enchant.failed.level", var0, var1);
   });
   private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(Component.translatable("commands.enchant.failed"));

   public EnchantCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("enchant").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("enchantment", ResourceArgument.resource(var1, Registries.ENCHANTMENT)).executes((var0x) -> {
         return enchant((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ResourceArgument.getEnchantment(var0x, "enchantment"), 1);
      })).then(Commands.argument("level", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return enchant((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ResourceArgument.getEnchantment(var0x, "enchantment"), IntegerArgumentType.getInteger(var0x, "level"));
      })))));
   }

   private static int enchant(CommandSourceStack var0, Collection<? extends Entity> var1, Holder<Enchantment> var2, int var3) throws CommandSyntaxException {
      Enchantment var4 = (Enchantment)var2.value();
      if (var3 > var4.getMaxLevel()) {
         throw ERROR_LEVEL_TOO_HIGH.create(var3, var4.getMaxLevel());
      } else {
         int var5 = 0;
         Iterator var6 = var1.iterator();

         while(true) {
            while(true) {
               while(true) {
                  while(var6.hasNext()) {
                     Entity var7 = (Entity)var6.next();
                     if (var7 instanceof LivingEntity) {
                        LivingEntity var8 = (LivingEntity)var7;
                        ItemStack var9 = var8.getMainHandItem();
                        if (!var9.isEmpty()) {
                           if (var4.canEnchant(var9) && EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantmentsForCrafting(var9).keySet(), var2)) {
                              var9.enchant(var2, var3);
                              ++var5;
                           } else if (var1.size() == 1) {
                              throw ERROR_INCOMPATIBLE.create(var9.getItem().getName(var9).getString());
                           }
                        } else if (var1.size() == 1) {
                           throw ERROR_NO_ITEM.create(var8.getName().getString());
                        }
                     } else if (var1.size() == 1) {
                        throw ERROR_NOT_LIVING_ENTITY.create(var7.getName().getString());
                     }
                  }

                  if (var5 == 0) {
                     throw ERROR_NOTHING_HAPPENED.create();
                  }

                  if (var1.size() == 1) {
                     var0.sendSuccess(() -> {
                        return Component.translatable("commands.enchant.success.single", Enchantment.getFullname(var2, var3), ((Entity)var1.iterator().next()).getDisplayName());
                     }, true);
                  } else {
                     var0.sendSuccess(() -> {
                        return Component.translatable("commands.enchant.success.multiple", Enchantment.getFullname(var2, var3), var1.size());
                     }, true);
                  }

                  return var5;
               }
            }
         }
      }
   }
}
