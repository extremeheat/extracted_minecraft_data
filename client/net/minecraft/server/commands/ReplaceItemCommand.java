package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ReplaceItemCommand {
   public static final SimpleCommandExceptionType ERROR_NOT_A_CONTAINER = new SimpleCommandExceptionType(new TranslatableComponent("commands.replaceitem.block.failed", new Object[0]));
   public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_SLOT = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.replaceitem.slot.inapplicable", new Object[]{var0});
   });
   public static final Dynamic2CommandExceptionType ERROR_ENTITY_SLOT = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.replaceitem.entity.failed", new Object[]{var0, var1});
   });

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("replaceitem").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(Commands.argument("slot", SlotArgument.slot()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item()).executes((var0x) -> {
         return setBlockItem((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), SlotArgument.getSlot(var0x, "slot"), ItemArgument.getItem(var0x, "item").createItemStack(1, false));
      })).then(Commands.argument("count", IntegerArgumentType.integer(1, 64)).executes((var0x) -> {
         return setBlockItem((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), SlotArgument.getSlot(var0x, "slot"), ItemArgument.getItem(var0x, "item").createItemStack(IntegerArgumentType.getInteger(var0x, "count"), true));
      }))))))).then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("slot", SlotArgument.slot()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item()).executes((var0x) -> {
         return setEntityItem((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), SlotArgument.getSlot(var0x, "slot"), ItemArgument.getItem(var0x, "item").createItemStack(1, false));
      })).then(Commands.argument("count", IntegerArgumentType.integer(1, 64)).executes((var0x) -> {
         return setEntityItem((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), SlotArgument.getSlot(var0x, "slot"), ItemArgument.getItem(var0x, "item").createItemStack(IntegerArgumentType.getInteger(var0x, "count"), true));
      })))))));
   }

   private static int setBlockItem(CommandSourceStack var0, BlockPos var1, int var2, ItemStack var3) throws CommandSyntaxException {
      BlockEntity var4 = var0.getLevel().getBlockEntity(var1);
      if (!(var4 instanceof Container)) {
         throw ERROR_NOT_A_CONTAINER.create();
      } else {
         Container var5 = (Container)var4;
         if (var2 >= 0 && var2 < var5.getContainerSize()) {
            var5.setItem(var2, var3);
            var0.sendSuccess(new TranslatableComponent("commands.replaceitem.block.success", new Object[]{var1.getX(), var1.getY(), var1.getZ(), var3.getDisplayName()}), true);
            return 1;
         } else {
            throw ERROR_INAPPLICABLE_SLOT.create(var2);
         }
      }
   }

   private static int setEntityItem(CommandSourceStack var0, Collection<? extends Entity> var1, int var2, ItemStack var3) throws CommandSyntaxException {
      ArrayList var4 = Lists.newArrayListWithCapacity(var1.size());
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Entity var6 = (Entity)var5.next();
         if (var6 instanceof ServerPlayer) {
            ((ServerPlayer)var6).inventoryMenu.broadcastChanges();
         }

         if (var6.setSlot(var2, var3.copy())) {
            var4.add(var6);
            if (var6 instanceof ServerPlayer) {
               ((ServerPlayer)var6).inventoryMenu.broadcastChanges();
            }
         }
      }

      if (var4.isEmpty()) {
         throw ERROR_ENTITY_SLOT.create(var3.getDisplayName(), var2);
      } else {
         if (var4.size() == 1) {
            var0.sendSuccess(new TranslatableComponent("commands.replaceitem.entity.success.single", new Object[]{((Entity)var4.iterator().next()).getDisplayName(), var3.getDisplayName()}), true);
         } else {
            var0.sendSuccess(new TranslatableComponent("commands.replaceitem.entity.success.multiple", new Object[]{var4.size(), var3.getDisplayName()}), true);
         }

         return var4.size();
      }
   }
}
