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
      int var4 = var1.getItem().getMaxStackSize();
      int var5 = var4 * 100;
      if (var3 > var5) {
         var0.sendFailure(Component.translatable("commands.give.failed.toomanyitems", var5, var1.createItemStack(var3, false).getDisplayName()));
         return 0;
      } else {
         Iterator var6 = var2.iterator();

         label44:
         while(var6.hasNext()) {
            ServerPlayer var7 = (ServerPlayer)var6.next();
            int var8 = var3;

            while(true) {
               while(true) {
                  if (var8 <= 0) {
                     continue label44;
                  }

                  int var9 = Math.min(var4, var8);
                  var8 -= var9;
                  ItemStack var10 = var1.createItemStack(var9, false);
                  boolean var11 = var7.getInventory().add(var10);
                  ItemEntity var12;
                  if (var11 && var10.isEmpty()) {
                     var10.setCount(1);
                     var12 = var7.drop(var10, false);
                     if (var12 != null) {
                        var12.makeFakeItem();
                     }

                     var7.level.playSound((Player)null, var7.getX(), var7.getY(), var7.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((var7.getRandom().nextFloat() - var7.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                     var7.containerMenu.broadcastChanges();
                  } else {
                     var12 = var7.drop(var10, false);
                     if (var12 != null) {
                        var12.setNoPickUpDelay();
                        var12.setOwner(var7.getUUID());
                     }
                  }
               }
            }
         }

         if (var2.size() == 1) {
            var0.sendSuccess(Component.translatable("commands.give.success.single", var3, var1.createItemStack(var3, false).getDisplayName(), ((ServerPlayer)var2.iterator().next()).getDisplayName()), true);
         } else {
            var0.sendSuccess(Component.translatable("commands.give.success.single", var3, var1.createItemStack(var3, false).getDisplayName(), var2.size()), true);
         }

         return var2.size();
      }
   }
}
