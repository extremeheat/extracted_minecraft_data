package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LootCommand {
   public static final SuggestionProvider SUGGEST_LOOT_TABLE = (var0, var1) -> {
      LootTables var2 = ((CommandSourceStack)var0.getSource()).getServer().getLootTables();
      return SharedSuggestionProvider.suggestResource((Iterable)var2.getIds(), var1);
   };
   private static final DynamicCommandExceptionType ERROR_NO_HELD_ITEMS = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.drop.no_held_items", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType ERROR_NO_LOOT_TABLE = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("commands.drop.no_loot_table", new Object[]{var0});
   });

   public static void register(CommandDispatcher var0) {
      var0.register((LiteralArgumentBuilder)addTargets(Commands.literal("loot").requires((var0x) -> {
         return var0x.hasPermission(2);
      }), (var0x, var1) -> {
         return var0x.then(Commands.literal("fish").then(Commands.argument("loot_table", ResourceLocationArgument.id()).suggests(SUGGEST_LOOT_TABLE).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((var1x) -> {
            return dropFishingLoot(var1x, ResourceLocationArgument.getId(var1x, "loot_table"), BlockPosArgument.getLoadedBlockPos(var1x, "pos"), ItemStack.EMPTY, var1);
         })).then(Commands.argument("tool", ItemArgument.item()).executes((var1x) -> {
            return dropFishingLoot(var1x, ResourceLocationArgument.getId(var1x, "loot_table"), BlockPosArgument.getLoadedBlockPos(var1x, "pos"), ItemArgument.getItem(var1x, "tool").createItemStack(1, false), var1);
         }))).then(Commands.literal("mainhand").executes((var1x) -> {
            return dropFishingLoot(var1x, ResourceLocationArgument.getId(var1x, "loot_table"), BlockPosArgument.getLoadedBlockPos(var1x, "pos"), getSourceHandItem((CommandSourceStack)var1x.getSource(), EquipmentSlot.MAINHAND), var1);
         }))).then(Commands.literal("offhand").executes((var1x) -> {
            return dropFishingLoot(var1x, ResourceLocationArgument.getId(var1x, "loot_table"), BlockPosArgument.getLoadedBlockPos(var1x, "pos"), getSourceHandItem((CommandSourceStack)var1x.getSource(), EquipmentSlot.OFFHAND), var1);
         }))))).then(Commands.literal("loot").then(Commands.argument("loot_table", ResourceLocationArgument.id()).suggests(SUGGEST_LOOT_TABLE).executes((var1x) -> {
            return dropChestLoot(var1x, ResourceLocationArgument.getId(var1x, "loot_table"), var1);
         }))).then(Commands.literal("kill").then(Commands.argument("target", EntityArgument.entity()).executes((var1x) -> {
            return dropKillLoot(var1x, EntityArgument.getEntity(var1x, "target"), var1);
         }))).then(Commands.literal("mine").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((var1x) -> {
            return dropBlockLoot(var1x, BlockPosArgument.getLoadedBlockPos(var1x, "pos"), ItemStack.EMPTY, var1);
         })).then(Commands.argument("tool", ItemArgument.item()).executes((var1x) -> {
            return dropBlockLoot(var1x, BlockPosArgument.getLoadedBlockPos(var1x, "pos"), ItemArgument.getItem(var1x, "tool").createItemStack(1, false), var1);
         }))).then(Commands.literal("mainhand").executes((var1x) -> {
            return dropBlockLoot(var1x, BlockPosArgument.getLoadedBlockPos(var1x, "pos"), getSourceHandItem((CommandSourceStack)var1x.getSource(), EquipmentSlot.MAINHAND), var1);
         }))).then(Commands.literal("offhand").executes((var1x) -> {
            return dropBlockLoot(var1x, BlockPosArgument.getLoadedBlockPos(var1x, "pos"), getSourceHandItem((CommandSourceStack)var1x.getSource(), EquipmentSlot.OFFHAND), var1);
         }))));
      }));
   }

   private static ArgumentBuilder addTargets(ArgumentBuilder var0, LootCommand.TailProvider var1) {
      return var0.then(((LiteralArgumentBuilder)Commands.literal("replace").then(Commands.literal("entity").then(Commands.argument("entities", EntityArgument.entities()).then(var1.construct(Commands.argument("slot", SlotArgument.slot()), (var0x, var1x, var2) -> {
         return entityReplace(EntityArgument.getEntities(var0x, "entities"), SlotArgument.getSlot(var0x, "slot"), var1x.size(), var1x, var2);
      }).then(var1.construct(Commands.argument("count", IntegerArgumentType.integer(0)), (var0x, var1x, var2) -> {
         return entityReplace(EntityArgument.getEntities(var0x, "entities"), SlotArgument.getSlot(var0x, "slot"), IntegerArgumentType.getInteger(var0x, "count"), var1x, var2);
      })))))).then(Commands.literal("block").then(Commands.argument("targetPos", BlockPosArgument.blockPos()).then(var1.construct(Commands.argument("slot", SlotArgument.slot()), (var0x, var1x, var2) -> {
         return blockReplace((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "targetPos"), SlotArgument.getSlot(var0x, "slot"), var1x.size(), var1x, var2);
      }).then(var1.construct(Commands.argument("count", IntegerArgumentType.integer(0)), (var0x, var1x, var2) -> {
         return blockReplace((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "targetPos"), IntegerArgumentType.getInteger(var0x, "slot"), IntegerArgumentType.getInteger(var0x, "count"), var1x, var2);
      })))))).then(Commands.literal("insert").then(var1.construct(Commands.argument("targetPos", BlockPosArgument.blockPos()), (var0x, var1x, var2) -> {
         return blockDistribute((CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "targetPos"), var1x, var2);
      }))).then(Commands.literal("give").then(var1.construct(Commands.argument("players", EntityArgument.players()), (var0x, var1x, var2) -> {
         return playerGive(EntityArgument.getPlayers(var0x, "players"), var1x, var2);
      }))).then(Commands.literal("spawn").then(var1.construct(Commands.argument("targetPos", Vec3Argument.vec3()), (var0x, var1x, var2) -> {
         return dropInWorld((CommandSourceStack)var0x.getSource(), Vec3Argument.getVec3(var0x, "targetPos"), var1x, var2);
      })));
   }

   private static Container getContainer(CommandSourceStack var0, BlockPos var1) throws CommandSyntaxException {
      BlockEntity var2 = var0.getLevel().getBlockEntity(var1);
      if (!(var2 instanceof Container)) {
         throw ReplaceItemCommand.ERROR_NOT_A_CONTAINER.create();
      } else {
         return (Container)var2;
      }
   }

   private static int blockDistribute(CommandSourceStack var0, BlockPos var1, List var2, LootCommand.Callback var3) throws CommandSyntaxException {
      Container var4 = getContainer(var0, var1);
      ArrayList var5 = Lists.newArrayListWithCapacity(var2.size());
      Iterator var6 = var2.iterator();

      while(var6.hasNext()) {
         ItemStack var7 = (ItemStack)var6.next();
         if (distributeToContainer(var4, var7.copy())) {
            var4.setChanged();
            var5.add(var7);
         }
      }

      var3.accept(var5);
      return var5.size();
   }

   private static boolean distributeToContainer(Container var0, ItemStack var1) {
      boolean var2 = false;

      for(int var3 = 0; var3 < var0.getContainerSize() && !var1.isEmpty(); ++var3) {
         ItemStack var4 = var0.getItem(var3);
         if (var0.canPlaceItem(var3, var1)) {
            if (var4.isEmpty()) {
               var0.setItem(var3, var1);
               var2 = true;
               break;
            }

            if (canMergeItems(var4, var1)) {
               int var5 = var1.getMaxStackSize() - var4.getCount();
               int var6 = Math.min(var1.getCount(), var5);
               var1.shrink(var6);
               var4.grow(var6);
               var2 = true;
            }
         }
      }

      return var2;
   }

   private static int blockReplace(CommandSourceStack var0, BlockPos var1, int var2, int var3, List var4, LootCommand.Callback var5) throws CommandSyntaxException {
      Container var6 = getContainer(var0, var1);
      int var7 = var6.getContainerSize();
      if (var2 >= 0 && var2 < var7) {
         ArrayList var8 = Lists.newArrayListWithCapacity(var4.size());

         for(int var9 = 0; var9 < var3; ++var9) {
            int var10 = var2 + var9;
            ItemStack var11 = var9 < var4.size() ? (ItemStack)var4.get(var9) : ItemStack.EMPTY;
            if (var6.canPlaceItem(var10, var11)) {
               var6.setItem(var10, var11);
               var8.add(var11);
            }
         }

         var5.accept(var8);
         return var8.size();
      } else {
         throw ReplaceItemCommand.ERROR_INAPPLICABLE_SLOT.create(var2);
      }
   }

   private static boolean canMergeItems(ItemStack var0, ItemStack var1) {
      return var0.getItem() == var1.getItem() && var0.getDamageValue() == var1.getDamageValue() && var0.getCount() <= var0.getMaxStackSize() && Objects.equals(var0.getTag(), var1.getTag());
   }

   private static int playerGive(Collection var0, List var1, LootCommand.Callback var2) throws CommandSyntaxException {
      ArrayList var3 = Lists.newArrayListWithCapacity(var1.size());
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         ItemStack var5 = (ItemStack)var4.next();
         Iterator var6 = var0.iterator();

         while(var6.hasNext()) {
            ServerPlayer var7 = (ServerPlayer)var6.next();
            if (var7.inventory.add(var5.copy())) {
               var3.add(var5);
            }
         }
      }

      var2.accept(var3);
      return var3.size();
   }

   private static void setSlots(Entity var0, List var1, int var2, int var3, List var4) {
      for(int var5 = 0; var5 < var3; ++var5) {
         ItemStack var6 = var5 < var1.size() ? (ItemStack)var1.get(var5) : ItemStack.EMPTY;
         if (var0.setSlot(var2 + var5, var6.copy())) {
            var4.add(var6);
         }
      }

   }

   private static int entityReplace(Collection var0, int var1, int var2, List var3, LootCommand.Callback var4) throws CommandSyntaxException {
      ArrayList var5 = Lists.newArrayListWithCapacity(var3.size());
      Iterator var6 = var0.iterator();

      while(var6.hasNext()) {
         Entity var7 = (Entity)var6.next();
         if (var7 instanceof ServerPlayer) {
            ServerPlayer var8 = (ServerPlayer)var7;
            var8.inventoryMenu.broadcastChanges();
            setSlots(var7, var3, var1, var2, var5);
            var8.inventoryMenu.broadcastChanges();
         } else {
            setSlots(var7, var3, var1, var2, var5);
         }
      }

      var4.accept(var5);
      return var5.size();
   }

   private static int dropInWorld(CommandSourceStack var0, Vec3 var1, List var2, LootCommand.Callback var3) throws CommandSyntaxException {
      ServerLevel var4 = var0.getLevel();
      var2.forEach((var2x) -> {
         ItemEntity var3 = new ItemEntity(var4, var1.x, var1.y, var1.z, var2x.copy());
         var3.setDefaultPickUpDelay();
         var4.addFreshEntity(var3);
      });
      var3.accept(var2);
      return var2.size();
   }

   private static void callback(CommandSourceStack var0, List var1) {
      if (var1.size() == 1) {
         ItemStack var2 = (ItemStack)var1.get(0);
         var0.sendSuccess(new TranslatableComponent("commands.drop.success.single", new Object[]{var2.getCount(), var2.getDisplayName()}), false);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.drop.success.multiple", new Object[]{var1.size()}), false);
      }

   }

   private static void callback(CommandSourceStack var0, List var1, ResourceLocation var2) {
      if (var1.size() == 1) {
         ItemStack var3 = (ItemStack)var1.get(0);
         var0.sendSuccess(new TranslatableComponent("commands.drop.success.single_with_table", new Object[]{var3.getCount(), var3.getDisplayName(), var2}), false);
      } else {
         var0.sendSuccess(new TranslatableComponent("commands.drop.success.multiple_with_table", new Object[]{var1.size(), var2}), false);
      }

   }

   private static ItemStack getSourceHandItem(CommandSourceStack var0, EquipmentSlot var1) throws CommandSyntaxException {
      Entity var2 = var0.getEntityOrException();
      if (var2 instanceof LivingEntity) {
         return ((LivingEntity)var2).getItemBySlot(var1);
      } else {
         throw ERROR_NO_HELD_ITEMS.create(var2.getDisplayName());
      }
   }

   private static int dropBlockLoot(CommandContext var0, BlockPos var1, ItemStack var2, LootCommand.DropConsumer var3) throws CommandSyntaxException {
      CommandSourceStack var4 = (CommandSourceStack)var0.getSource();
      ServerLevel var5 = var4.getLevel();
      BlockState var6 = var5.getBlockState(var1);
      BlockEntity var7 = var5.getBlockEntity(var1);
      LootContext.Builder var8 = (new LootContext.Builder(var5)).withParameter(LootContextParams.BLOCK_POS, var1).withParameter(LootContextParams.BLOCK_STATE, var6).withOptionalParameter(LootContextParams.BLOCK_ENTITY, var7).withOptionalParameter(LootContextParams.THIS_ENTITY, var4.getEntity()).withParameter(LootContextParams.TOOL, var2);
      List var9 = var6.getDrops(var8);
      return var3.accept(var0, var9, (var2x) -> {
         callback(var4, var2x, var6.getBlock().getLootTable());
      });
   }

   private static int dropKillLoot(CommandContext var0, Entity var1, LootCommand.DropConsumer var2) throws CommandSyntaxException {
      if (!(var1 instanceof LivingEntity)) {
         throw ERROR_NO_LOOT_TABLE.create(var1.getDisplayName());
      } else {
         ResourceLocation var3 = ((LivingEntity)var1).getLootTable();
         CommandSourceStack var4 = (CommandSourceStack)var0.getSource();
         LootContext.Builder var5 = new LootContext.Builder(var4.getLevel());
         Entity var6 = var4.getEntity();
         if (var6 instanceof Player) {
            var5.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, (Player)var6);
         }

         var5.withParameter(LootContextParams.DAMAGE_SOURCE, DamageSource.MAGIC);
         var5.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, var6);
         var5.withOptionalParameter(LootContextParams.KILLER_ENTITY, var6);
         var5.withParameter(LootContextParams.THIS_ENTITY, var1);
         var5.withParameter(LootContextParams.BLOCK_POS, new BlockPos(var4.getPosition()));
         LootTable var7 = var4.getServer().getLootTables().get(var3);
         List var8 = var7.getRandomItems(var5.create(LootContextParamSets.ENTITY));
         return var2.accept(var0, var8, (var2x) -> {
            callback(var4, var2x, var3);
         });
      }
   }

   private static int dropChestLoot(CommandContext var0, ResourceLocation var1, LootCommand.DropConsumer var2) throws CommandSyntaxException {
      CommandSourceStack var3 = (CommandSourceStack)var0.getSource();
      LootContext.Builder var4 = (new LootContext.Builder(var3.getLevel())).withOptionalParameter(LootContextParams.THIS_ENTITY, var3.getEntity()).withParameter(LootContextParams.BLOCK_POS, new BlockPos(var3.getPosition()));
      return drop(var0, var1, var4.create(LootContextParamSets.CHEST), var2);
   }

   private static int dropFishingLoot(CommandContext var0, ResourceLocation var1, BlockPos var2, ItemStack var3, LootCommand.DropConsumer var4) throws CommandSyntaxException {
      CommandSourceStack var5 = (CommandSourceStack)var0.getSource();
      LootContext var6 = (new LootContext.Builder(var5.getLevel())).withParameter(LootContextParams.BLOCK_POS, var2).withParameter(LootContextParams.TOOL, var3).create(LootContextParamSets.FISHING);
      return drop(var0, var1, var6, var4);
   }

   private static int drop(CommandContext var0, ResourceLocation var1, LootContext var2, LootCommand.DropConsumer var3) throws CommandSyntaxException {
      CommandSourceStack var4 = (CommandSourceStack)var0.getSource();
      LootTable var5 = var4.getServer().getLootTables().get(var1);
      List var6 = var5.getRandomItems(var2);
      return var3.accept(var0, var6, (var1x) -> {
         callback(var4, var1x);
      });
   }

   @FunctionalInterface
   interface TailProvider {
      ArgumentBuilder construct(ArgumentBuilder var1, LootCommand.DropConsumer var2);
   }

   @FunctionalInterface
   interface DropConsumer {
      int accept(CommandContext var1, List var2, LootCommand.Callback var3) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface Callback {
      void accept(List var1) throws CommandSyntaxException;
   }
}
