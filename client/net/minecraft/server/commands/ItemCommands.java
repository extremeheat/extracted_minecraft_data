package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ItemCommands {
   static final Dynamic3CommandExceptionType ERROR_TARGET_NOT_A_CONTAINER = new Dynamic3CommandExceptionType((var0, var1, var2) -> {
      return Component.translatable("commands.item.target.not_a_container", var0, var1, var2);
   });
   private static final Dynamic3CommandExceptionType ERROR_SOURCE_NOT_A_CONTAINER = new Dynamic3CommandExceptionType((var0, var1, var2) -> {
      return Component.translatable("commands.item.source.not_a_container", var0, var1, var2);
   });
   static final DynamicCommandExceptionType ERROR_TARGET_INAPPLICABLE_SLOT = new DynamicCommandExceptionType((var0) -> {
      return Component.translatable("commands.item.target.no_such_slot", var0);
   });
   private static final DynamicCommandExceptionType ERROR_SOURCE_INAPPLICABLE_SLOT = new DynamicCommandExceptionType((var0) -> {
      return Component.translatable("commands.item.source.no_such_slot", var0);
   });
   private static final DynamicCommandExceptionType ERROR_TARGET_NO_CHANGES = new DynamicCommandExceptionType((var0) -> {
      return Component.translatable("commands.item.target.no_changes", var0);
   });
   private static final Dynamic2CommandExceptionType ERROR_TARGET_NO_CHANGES_KNOWN_ITEM = new Dynamic2CommandExceptionType((var0, var1) -> {
      return Component.translatable("commands.item.target.no_changed.known_item", var0, var1);
   });
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_MODIFIER = (var0, var1) -> {
      ItemModifierManager var2 = ((CommandSourceStack)var0.getSource()).getServer().getItemModifierManager();
      return SharedSuggestionProvider.suggestResource((Iterable)var2.getKeys(), var1);
   };

   public ItemCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("item").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((LiteralArgumentBuilder)Commands.literal("replace").then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("slot", SlotArgument.slot()).then(Commands.literal("with").then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item(var1)).executes((var0x) -> {
         return setBlockItem((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), SlotArgument.getSlot(var0x, "slot"), ItemArgument.getItem(var0x, "item").createItemStack(1, false));
      })).then(Commands.argument("count", IntegerArgumentType.integer(1, 64)).executes((var0x) -> {
         return setBlockItem((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), SlotArgument.getSlot(var0x, "slot"), ItemArgument.getItem(var0x, "item").createItemStack(IntegerArgumentType.getInteger(var0x, "count"), true));
      }))))).then(((LiteralArgumentBuilder)Commands.literal("from").then(Commands.literal("block").then(Commands.argument("source", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("sourceSlot", SlotArgument.slot()).executes((var0x) -> {
         return blockToBlock((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "source"), SlotArgument.getSlot(var0x, "sourceSlot"), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), SlotArgument.getSlot(var0x, "slot"));
      })).then(Commands.argument("modifier", ResourceLocationArgument.id()).suggests(SUGGEST_MODIFIER).executes((var0x) -> {
         return blockToBlock((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "source"), SlotArgument.getSlot(var0x, "sourceSlot"), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), SlotArgument.getSlot(var0x, "slot"), ResourceLocationArgument.getItemModifier(var0x, "modifier"));
      })))))).then(Commands.literal("entity").then(Commands.argument("source", EntityArgument.entity()).then(((RequiredArgumentBuilder)Commands.argument("sourceSlot", SlotArgument.slot()).executes((var0x) -> {
         return entityToBlock((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "source"), SlotArgument.getSlot(var0x, "sourceSlot"), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), SlotArgument.getSlot(var0x, "slot"));
      })).then(Commands.argument("modifier", ResourceLocationArgument.id()).suggests(SUGGEST_MODIFIER).executes((var0x) -> {
         return entityToBlock((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "source"), SlotArgument.getSlot(var0x, "sourceSlot"), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), SlotArgument.getSlot(var0x, "slot"), ResourceLocationArgument.getItemModifier(var0x, "modifier"));
      })))))))))).then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("slot", SlotArgument.slot()).then(Commands.literal("with").then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item(var1)).executes((var0x) -> {
         return setEntityItem((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), SlotArgument.getSlot(var0x, "slot"), ItemArgument.getItem(var0x, "item").createItemStack(1, false));
      })).then(Commands.argument("count", IntegerArgumentType.integer(1, 64)).executes((var0x) -> {
         return setEntityItem((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), SlotArgument.getSlot(var0x, "slot"), ItemArgument.getItem(var0x, "item").createItemStack(IntegerArgumentType.getInteger(var0x, "count"), true));
      }))))).then(((LiteralArgumentBuilder)Commands.literal("from").then(Commands.literal("block").then(Commands.argument("source", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("sourceSlot", SlotArgument.slot()).executes((var0x) -> {
         return blockToEntities((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "source"), SlotArgument.getSlot(var0x, "sourceSlot"), EntityArgument.getEntities(var0x, "targets"), SlotArgument.getSlot(var0x, "slot"));
      })).then(Commands.argument("modifier", ResourceLocationArgument.id()).suggests(SUGGEST_MODIFIER).executes((var0x) -> {
         return blockToEntities((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "source"), SlotArgument.getSlot(var0x, "sourceSlot"), EntityArgument.getEntities(var0x, "targets"), SlotArgument.getSlot(var0x, "slot"), ResourceLocationArgument.getItemModifier(var0x, "modifier"));
      })))))).then(Commands.literal("entity").then(Commands.argument("source", EntityArgument.entity()).then(((RequiredArgumentBuilder)Commands.argument("sourceSlot", SlotArgument.slot()).executes((var0x) -> {
         return entityToEntities((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "source"), SlotArgument.getSlot(var0x, "sourceSlot"), EntityArgument.getEntities(var0x, "targets"), SlotArgument.getSlot(var0x, "slot"));
      })).then(Commands.argument("modifier", ResourceLocationArgument.id()).suggests(SUGGEST_MODIFIER).executes((var0x) -> {
         return entityToEntities((CommandSourceStack)var0x.getSource(), EntityArgument.getEntity(var0x, "source"), SlotArgument.getSlot(var0x, "sourceSlot"), EntityArgument.getEntities(var0x, "targets"), SlotArgument.getSlot(var0x, "slot"), ResourceLocationArgument.getItemModifier(var0x, "modifier"));
      }))))))))))).then(((LiteralArgumentBuilder)Commands.literal("modify").then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(Commands.argument("slot", SlotArgument.slot()).then(Commands.argument("modifier", ResourceLocationArgument.id()).suggests(SUGGEST_MODIFIER).executes((var0x) -> {
         return modifyBlockItem((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), SlotArgument.getSlot(var0x, "slot"), ResourceLocationArgument.getItemModifier(var0x, "modifier"));
      })))))).then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("slot", SlotArgument.slot()).then(Commands.argument("modifier", ResourceLocationArgument.id()).suggests(SUGGEST_MODIFIER).executes((var0x) -> {
         return modifyEntityItem((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), SlotArgument.getSlot(var0x, "slot"), ResourceLocationArgument.getItemModifier(var0x, "modifier"));
      })))))));
   }

   private static int modifyBlockItem(CommandSourceStack var0, BlockPos var1, int var2, LootItemFunction var3) throws CommandSyntaxException {
      Container var4 = getContainer(var0, var1, ERROR_TARGET_NOT_A_CONTAINER);
      if (var2 >= 0 && var2 < var4.getContainerSize()) {
         ItemStack var5 = applyModifier(var0, var3, var4.getItem(var2));
         var4.setItem(var2, var5);
         var0.sendSuccess(Component.translatable("commands.item.block.set.success", var1.getX(), var1.getY(), var1.getZ(), var5.getDisplayName()), true);
         return 1;
      } else {
         throw ERROR_TARGET_INAPPLICABLE_SLOT.create(var2);
      }
   }

   private static int modifyEntityItem(CommandSourceStack var0, Collection<? extends Entity> var1, int var2, LootItemFunction var3) throws CommandSyntaxException {
      HashMap var4 = Maps.newHashMapWithExpectedSize(var1.size());
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Entity var6 = (Entity)var5.next();
         SlotAccess var7 = var6.getSlot(var2);
         if (var7 != SlotAccess.NULL) {
            ItemStack var8 = applyModifier(var0, var3, var7.get().copy());
            if (var7.set(var8)) {
               var4.put(var6, var8);
               if (var6 instanceof ServerPlayer) {
                  ((ServerPlayer)var6).containerMenu.broadcastChanges();
               }
            }
         }
      }

      if (var4.isEmpty()) {
         throw ERROR_TARGET_NO_CHANGES.create(var2);
      } else {
         if (var4.size() == 1) {
            Map.Entry var9 = (Map.Entry)var4.entrySet().iterator().next();
            var0.sendSuccess(Component.translatable("commands.item.entity.set.success.single", ((Entity)var9.getKey()).getDisplayName(), ((ItemStack)var9.getValue()).getDisplayName()), true);
         } else {
            var0.sendSuccess(Component.translatable("commands.item.entity.set.success.multiple", var4.size()), true);
         }

         return var4.size();
      }
   }

   private static int setBlockItem(CommandSourceStack var0, BlockPos var1, int var2, ItemStack var3) throws CommandSyntaxException {
      Container var4 = getContainer(var0, var1, ERROR_TARGET_NOT_A_CONTAINER);
      if (var2 >= 0 && var2 < var4.getContainerSize()) {
         var4.setItem(var2, var3);
         var0.sendSuccess(Component.translatable("commands.item.block.set.success", var1.getX(), var1.getY(), var1.getZ(), var3.getDisplayName()), true);
         return 1;
      } else {
         throw ERROR_TARGET_INAPPLICABLE_SLOT.create(var2);
      }
   }

   private static Container getContainer(CommandSourceStack var0, BlockPos var1, Dynamic3CommandExceptionType var2) throws CommandSyntaxException {
      BlockEntity var3 = var0.getLevel().getBlockEntity(var1);
      if (!(var3 instanceof Container)) {
         throw var2.create(var1.getX(), var1.getY(), var1.getZ());
      } else {
         return (Container)var3;
      }
   }

   private static int setEntityItem(CommandSourceStack var0, Collection<? extends Entity> var1, int var2, ItemStack var3) throws CommandSyntaxException {
      ArrayList var4 = Lists.newArrayListWithCapacity(var1.size());
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Entity var6 = (Entity)var5.next();
         SlotAccess var7 = var6.getSlot(var2);
         if (var7 != SlotAccess.NULL && var7.set(var3.copy())) {
            var4.add(var6);
            if (var6 instanceof ServerPlayer) {
               ((ServerPlayer)var6).containerMenu.broadcastChanges();
            }
         }
      }

      if (var4.isEmpty()) {
         throw ERROR_TARGET_NO_CHANGES_KNOWN_ITEM.create(var3.getDisplayName(), var2);
      } else {
         if (var4.size() == 1) {
            var0.sendSuccess(Component.translatable("commands.item.entity.set.success.single", ((Entity)var4.iterator().next()).getDisplayName(), var3.getDisplayName()), true);
         } else {
            var0.sendSuccess(Component.translatable("commands.item.entity.set.success.multiple", var4.size(), var3.getDisplayName()), true);
         }

         return var4.size();
      }
   }

   private static int blockToEntities(CommandSourceStack var0, BlockPos var1, int var2, Collection<? extends Entity> var3, int var4) throws CommandSyntaxException {
      return setEntityItem(var0, var3, var4, getBlockItem(var0, var1, var2));
   }

   private static int blockToEntities(CommandSourceStack var0, BlockPos var1, int var2, Collection<? extends Entity> var3, int var4, LootItemFunction var5) throws CommandSyntaxException {
      return setEntityItem(var0, var3, var4, applyModifier(var0, var5, getBlockItem(var0, var1, var2)));
   }

   private static int blockToBlock(CommandSourceStack var0, BlockPos var1, int var2, BlockPos var3, int var4) throws CommandSyntaxException {
      return setBlockItem(var0, var3, var4, getBlockItem(var0, var1, var2));
   }

   private static int blockToBlock(CommandSourceStack var0, BlockPos var1, int var2, BlockPos var3, int var4, LootItemFunction var5) throws CommandSyntaxException {
      return setBlockItem(var0, var3, var4, applyModifier(var0, var5, getBlockItem(var0, var1, var2)));
   }

   private static int entityToBlock(CommandSourceStack var0, Entity var1, int var2, BlockPos var3, int var4) throws CommandSyntaxException {
      return setBlockItem(var0, var3, var4, getEntityItem(var1, var2));
   }

   private static int entityToBlock(CommandSourceStack var0, Entity var1, int var2, BlockPos var3, int var4, LootItemFunction var5) throws CommandSyntaxException {
      return setBlockItem(var0, var3, var4, applyModifier(var0, var5, getEntityItem(var1, var2)));
   }

   private static int entityToEntities(CommandSourceStack var0, Entity var1, int var2, Collection<? extends Entity> var3, int var4) throws CommandSyntaxException {
      return setEntityItem(var0, var3, var4, getEntityItem(var1, var2));
   }

   private static int entityToEntities(CommandSourceStack var0, Entity var1, int var2, Collection<? extends Entity> var3, int var4, LootItemFunction var5) throws CommandSyntaxException {
      return setEntityItem(var0, var3, var4, applyModifier(var0, var5, getEntityItem(var1, var2)));
   }

   private static ItemStack applyModifier(CommandSourceStack var0, LootItemFunction var1, ItemStack var2) {
      ServerLevel var3 = var0.getLevel();
      LootContext.Builder var4 = (new LootContext.Builder(var3)).withParameter(LootContextParams.ORIGIN, var0.getPosition()).withOptionalParameter(LootContextParams.THIS_ENTITY, var0.getEntity());
      return (ItemStack)var1.apply(var2, var4.create(LootContextParamSets.COMMAND));
   }

   private static ItemStack getEntityItem(Entity var0, int var1) throws CommandSyntaxException {
      SlotAccess var2 = var0.getSlot(var1);
      if (var2 == SlotAccess.NULL) {
         throw ERROR_SOURCE_INAPPLICABLE_SLOT.create(var1);
      } else {
         return var2.get().copy();
      }
   }

   private static ItemStack getBlockItem(CommandSourceStack var0, BlockPos var1, int var2) throws CommandSyntaxException {
      Container var3 = getContainer(var0, var1, ERROR_SOURCE_NOT_A_CONTAINER);
      if (var2 >= 0 && var2 < var3.getContainerSize()) {
         return var3.getItem(var2).copy();
      } else {
         throw ERROR_SOURCE_INAPPLICABLE_SLOT.create(var2);
      }
   }
}
