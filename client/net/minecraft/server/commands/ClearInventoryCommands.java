package net.minecraft.server.commands;

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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ClearInventoryCommands {
   private static final DynamicCommandExceptionType ERROR_SINGLE = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("clear.failed.single", var0);
   });
   private static final DynamicCommandExceptionType ERROR_MULTIPLE = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("clear.failed.multiple", var0);
   });

   public ClearInventoryCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clear").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).executes((var0x) -> {
         return clearUnlimited((CommandSourceStack)var0x.getSource(), Collections.singleton(((CommandSourceStack)var0x.getSource()).getPlayerOrException()), (var0) -> {
            return true;
         });
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((var0x) -> {
         return clearUnlimited((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), (var0) -> {
            return true;
         });
      })).then(((RequiredArgumentBuilder)Commands.argument("item", ItemPredicateArgument.itemPredicate(var1)).executes((var0x) -> {
         return clearUnlimited((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), ItemPredicateArgument.getItemPredicate(var0x, "item"));
      })).then(Commands.argument("maxCount", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return clearInventory((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), ItemPredicateArgument.getItemPredicate(var0x, "item"), IntegerArgumentType.getInteger(var0x, "maxCount"));
      })))));
   }

   private static int clearUnlimited(CommandSourceStack var0, Collection<ServerPlayer> var1, Predicate<ItemStack> var2) throws CommandSyntaxException {
      return clearInventory(var0, var1, var2, -1);
   }

   private static int clearInventory(CommandSourceStack var0, Collection<ServerPlayer> var1, Predicate<ItemStack> var2, int var3) throws CommandSyntaxException {
      int var4 = 0;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         ServerPlayer var6 = (ServerPlayer)var5.next();
         var4 += var6.getInventory().clearOrCountMatchingItems(var2, var3, var6.inventoryMenu.getCraftSlots());
         var6.containerMenu.broadcastChanges();
         var6.inventoryMenu.slotsChanged(var6.getInventory());
      }

      if (var4 == 0) {
         if (var1.size() == 1) {
            throw ERROR_SINGLE.create(((ServerPlayer)var1.iterator().next()).getName());
         } else {
            throw ERROR_MULTIPLE.create(var1.size());
         }
      } else {
         if (var3 == 0) {
            if (var1.size() == 1) {
               var0.sendSuccess(() -> {
                  return Component.translatable("commands.clear.test.single", var4, ((ServerPlayer)var1.iterator().next()).getDisplayName());
               }, true);
            } else {
               var0.sendSuccess(() -> {
                  return Component.translatable("commands.clear.test.multiple", var4, var1.size());
               }, true);
            }
         } else if (var1.size() == 1) {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.clear.success.single", var4, ((ServerPlayer)var1.iterator().next()).getDisplayName());
            }, true);
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.clear.success.multiple", var4, var1.size());
            }, true);
         }

         return var4;
      }
   }
}
