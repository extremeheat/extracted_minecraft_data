package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
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
   private static final DynamicCommandExceptionType ERROR_SINGLE = new DynamicCommandExceptionType((name) -> Component.translatableEscape("clear.failed.single", name));
   private static final DynamicCommandExceptionType ERROR_MULTIPLE = new DynamicCommandExceptionType((count) -> Component.translatableEscape("clear.failed.multiple", count));
   private static final CommandResponseTracker.Messages<ServerPlayer> RESPONSE_TEST = CommandResponseTracker.messages((CommandResponseTracker.SingleHandler)((player, totalValue) -> Component.translatable("commands.clear.test.single", totalValue, player.getDisplayName())), (CommandResponseTracker.MultipleHandler)((playerCount, totalValue) -> Component.translatable("commands.clear.test.multiple", totalValue, playerCount)));
   private static final CommandResponseTracker.Messages<ServerPlayer> RESPONSE_CLEAR = CommandResponseTracker.messages((CommandResponseTracker.SingleHandler)((player, totalValue) -> Component.translatable("commands.clear.success.single", totalValue, player.getDisplayName())), (CommandResponseTracker.MultipleHandler)((playerCount, totalValue) -> Component.translatable("commands.clear.success.multiple", totalValue, playerCount)));
   private static final CommandResponseTracker.Dispatch<CommandSyntaxException, ServerPlayer> ERROR_DISPATCH = new CommandResponseTracker.Dispatch<CommandSyntaxException, ServerPlayer>((player, var1) -> ERROR_SINGLE.create(player.getDisplayName()), (playerCount, var1) -> ERROR_MULTIPLE.create(playerCount));

   public ClearInventoryCommands() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext context) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clear").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).executes((c) -> clearUnlimited((CommandSourceStack)c.getSource(), Collections.singleton(((CommandSourceStack)c.getSource()).getPlayerOrException()), (var0) -> true))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((c) -> clearUnlimited((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), (var0) -> true))).then(((RequiredArgumentBuilder)Commands.argument("item", ItemPredicateArgument.itemPredicate(context)).executes((c) -> clearUnlimited((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), ItemPredicateArgument.getItemPredicate(c, "item")))).then(Commands.argument("maxCount", IntegerArgumentType.integer(0)).executes((c) -> clearInventory((CommandSourceStack)c.getSource(), EntityArgument.getPlayers(c, "targets"), ItemPredicateArgument.getItemPredicate(c, "item"), IntegerArgumentType.getInteger(c, "maxCount")))))));
   }

   private static int clearUnlimited(final CommandSourceStack source, final Collection<ServerPlayer> players, final Predicate<ItemStack> predicate) throws CommandSyntaxException {
      return clearInventory(source, players, predicate, -1);
   }

   private static int clearInventory(final CommandSourceStack source, final Collection<ServerPlayer> players, final Predicate<ItemStack> predicate, final int maxCount) throws CommandSyntaxException {
      CommandResponseTracker<ServerPlayer> tracker = CommandResponseTracker.<ServerPlayer>create();
      boolean countingOnly = maxCount == 0;

      for(ServerPlayer player : players) {
         tracker.track(player, player.getInventory().clearOrCountMatchingItems(predicate, countingOnly, maxCount, player.inventoryMenu.getCraftSlots()));
         if (!countingOnly) {
            player.containerMenu.broadcastChanges();
            player.inventoryMenu.slotsChanged(player.getInventory());
         }
      }

      if (tracker.totalValue() == 0) {
         throw (CommandSyntaxException)tracker.dispatch(CommandResponseTracker.ElementType.ANY, ERROR_DISPATCH);
      } else {
         return tracker.sendFeedback(source, true, CommandResponseTracker.ElementType.NON_ZERO, maxCount == 0 ? RESPONSE_TEST : RESPONSE_CLEAR);
      }
   }
}
