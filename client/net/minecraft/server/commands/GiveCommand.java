package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.item.ItemStack;

public class GiveCommand {
   public static final int MAX_ALLOWED_ITEMSTACKS = 100;

   public GiveCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext context) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("give").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item(context)).executes((c) -> giveItem((CommandSourceStack)c.getSource(), ItemArgument.getItem(c, "item"), EntityArgument.getPlayers(c, "targets"), 1))).then(Commands.argument("count", IntegerArgumentType.integer(1)).executes((c) -> giveItem((CommandSourceStack)c.getSource(), ItemArgument.getItem(c, "item"), EntityArgument.getPlayers(c, "targets"), IntegerArgumentType.getInteger(c, "count")))))));
   }

   private static int giveItem(final CommandSourceStack source, final ItemInput input, final Collection<ServerPlayer> players, final int count) throws CommandSyntaxException {
      ItemStack prototypeItemStack = input.createItemStack(1);
      int maxStackSize = prototypeItemStack.getMaxStackSize();
      int maxAllowedCount = maxStackSize * 100;
      if (count > maxAllowedCount) {
         source.sendFailure(Component.translatable("commands.give.failed.toomanyitems", maxAllowedCount, prototypeItemStack.getDisplayName()));
         return 0;
      } else {
         for(ServerPlayer player : players) {
            for(int i = 0; i < count; ++i) {
               LivingBlock.createStack(player.level(), player.blockPosition(), player, prototypeItemStack);
            }
         }

         if (players.size() == 1) {
            source.sendSuccess(() -> Component.translatable("commands.give.success.single", count, prototypeItemStack.getDisplayName(), ((ServerPlayer)players.iterator().next()).getDisplayName()), true);
         } else {
            source.sendSuccess(() -> Component.translatable("commands.give.success.single", count, prototypeItemStack.getDisplayName(), players.size()), true);
         }

         return players.size();
      }
   }
}
