package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class GiveCommand {
   public static final int MAX_ALLOWED_ITEMSTACKS = 100;

   public GiveCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("give").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item(var1)).executes((var0x) -> {
         return giveItem((CommandSourceStack)var0x.getSource(), ItemArgument.getItem(var0x, "item"), EntityArgument.getPlayers(var0x, "targets"), 1);
      })).then(Commands.argument("count", IntegerArgumentType.integer(1)).executes((var0x) -> {
         return giveItem((CommandSourceStack)var0x.getSource(), ItemArgument.getItem(var0x, "item"), EntityArgument.getPlayers(var0x, "targets"), IntegerArgumentType.getInteger(var0x, "count"));
      })))));
   }

   private static int giveItem(CommandSourceStack var0, ItemInput var1, Collection<ServerPlayer> var2, int var3) throws CommandSyntaxException {
      ItemStack var4 = var1.createItemStack(1, false);
      int var5 = var4.getMaxStackSize();
      int var6 = var5 * 100;
      if (var3 > var6) {
         var0.sendFailure(Component.translatable("commands.give.failed.toomanyitems", var6, var4.getDisplayName()));
         return 0;
      } else {
         Iterator var7 = var2.iterator();

         label44:
         while(var7.hasNext()) {
            ServerPlayer var8 = (ServerPlayer)var7.next();
            int var9 = var3;

            while(true) {
               while(true) {
                  if (var9 <= 0) {
                     continue label44;
                  }

                  int var10 = Math.min(var5, var9);
                  var9 -= var10;
                  ItemStack var11 = var1.createItemStack(var10, false);
                  boolean var12 = var8.getInventory().add(var11);
                  ItemEntity var13;
                  if (var12 && var11.isEmpty()) {
                     var13 = var8.drop(var4, false);
                     if (var13 != null) {
                        var13.makeFakeItem();
                     }

                     var8.level().playSound((Player)null, var8.getX(), var8.getY(), var8.getZ(), (SoundEvent)SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((var8.getRandom().nextFloat() - var8.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                     var8.containerMenu.broadcastChanges();
                  } else {
                     var13 = var8.drop(var11, false);
                     if (var13 != null) {
                        var13.setNoPickUpDelay();
                        var13.setTarget(var8.getUUID());
                     }
                  }
               }
            }
         }

         if (var2.size() == 1) {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.give.success.single", var3, var4.getDisplayName(), ((ServerPlayer)var2.iterator().next()).getDisplayName());
            }, true);
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.give.success.single", var3, var4.getDisplayName(), var2.size());
            }, true);
         }

         return var2.size();
      }
   }
}
