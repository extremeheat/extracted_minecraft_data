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
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceOrIdArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LootCommand {
   public static final SuggestionProvider<CommandSourceStack> SUGGEST_LOOT_TABLE = (var0, var1) -> {
      ReloadableServerRegistries.Holder var2 = ((CommandSourceStack)var0.getSource()).getServer().reloadableRegistries();
      return SharedSuggestionProvider.suggestResource(var2.getKeys(Registries.LOOT_TABLE), var1);
   };
   private static final DynamicCommandExceptionType ERROR_NO_HELD_ITEMS = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("commands.drop.no_held_items", var0)
   );
   private static final DynamicCommandExceptionType ERROR_NO_LOOT_TABLE = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("commands.drop.no_loot_table", var0)
   );

   public LootCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register(
         addTargets(
            (LiteralArgumentBuilder)Commands.literal("loot").requires(var0x -> var0x.hasPermission(2)),
            (var1x, var2) -> var1x.then(
                     Commands.literal("fish")
                        .then(
                           Commands.argument("loot_table", ResourceOrIdArgument.lootTable(var1))
                              .suggests(SUGGEST_LOOT_TABLE)
                              .then(
                                 ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument(
                                                "pos", BlockPosArgument.blockPos()
                                             )
                                             .executes(
                                                var1xx -> dropFishingLoot(
                                                      var1xx,
                                                      ResourceOrIdArgument.getLootTable(var1xx, "loot_table"),
                                                      BlockPosArgument.getLoadedBlockPos(var1xx, "pos"),
                                                      ItemStack.EMPTY,
                                                      var2
                                                   )
                                             ))
                                          .then(
                                             Commands.argument("tool", ItemArgument.item(var1))
                                                .executes(
                                                   var1xx -> dropFishingLoot(
                                                         var1xx,
                                                         ResourceOrIdArgument.getLootTable(var1xx, "loot_table"),
                                                         BlockPosArgument.getLoadedBlockPos(var1xx, "pos"),
                                                         ItemArgument.getItem(var1xx, "tool").createItemStack(1, false),
                                                         var2
                                                      )
                                                )
                                          ))
                                       .then(
                                          Commands.literal("mainhand")
                                             .executes(
                                                var1xx -> dropFishingLoot(
                                                      var1xx,
                                                      ResourceOrIdArgument.getLootTable(var1xx, "loot_table"),
                                                      BlockPosArgument.getLoadedBlockPos(var1xx, "pos"),
                                                      getSourceHandItem((CommandSourceStack)var1xx.getSource(), EquipmentSlot.MAINHAND),
                                                      var2
                                                   )
                                             )
                                       ))
                                    .then(
                                       Commands.literal("offhand")
                                          .executes(
                                             var1xx -> dropFishingLoot(
                                                   var1xx,
                                                   ResourceOrIdArgument.getLootTable(var1xx, "loot_table"),
                                                   BlockPosArgument.getLoadedBlockPos(var1xx, "pos"),
                                                   getSourceHandItem((CommandSourceStack)var1xx.getSource(), EquipmentSlot.OFFHAND),
                                                   var2
                                                )
                                          )
                                    )
                              )
                        )
                  )
                  .then(
                     Commands.literal("loot")
                        .then(
                           Commands.argument("loot_table", ResourceOrIdArgument.lootTable(var1))
                              .suggests(SUGGEST_LOOT_TABLE)
                              .executes(var1xx -> dropChestLoot(var1xx, ResourceOrIdArgument.getLootTable(var1xx, "loot_table"), var2))
                        )
                  )
                  .then(
                     Commands.literal("kill")
                        .then(
                           Commands.argument("target", EntityArgument.entity())
                              .executes(var1xx -> dropKillLoot(var1xx, EntityArgument.getEntity(var1xx, "target"), var2))
                        )
                  )
                  .then(
                     Commands.literal("mine")
                        .then(
                           ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos())
                                       .executes(var1xx -> dropBlockLoot(var1xx, BlockPosArgument.getLoadedBlockPos(var1xx, "pos"), ItemStack.EMPTY, var2)))
                                    .then(
                                       Commands.argument("tool", ItemArgument.item(var1))
                                          .executes(
                                             var1xx -> dropBlockLoot(
                                                   var1xx,
                                                   BlockPosArgument.getLoadedBlockPos(var1xx, "pos"),
                                                   ItemArgument.getItem(var1xx, "tool").createItemStack(1, false),
                                                   var2
                                                )
                                          )
                                    ))
                                 .then(
                                    Commands.literal("mainhand")
                                       .executes(
                                          var1xx -> dropBlockLoot(
                                                var1xx,
                                                BlockPosArgument.getLoadedBlockPos(var1xx, "pos"),
                                                getSourceHandItem((CommandSourceStack)var1xx.getSource(), EquipmentSlot.MAINHAND),
                                                var2
                                             )
                                       )
                                 ))
                              .then(
                                 Commands.literal("offhand")
                                    .executes(
                                       var1xx -> dropBlockLoot(
                                             var1xx,
                                             BlockPosArgument.getLoadedBlockPos(var1xx, "pos"),
                                             getSourceHandItem((CommandSourceStack)var1xx.getSource(), EquipmentSlot.OFFHAND),
                                             var2
                                          )
                                    )
                              )
                        )
                  )
         )
      );
   }

   private static <T extends ArgumentBuilder<CommandSourceStack, T>> T addTargets(T var0, LootCommand.TailProvider var1) {
      return (T)var0.then(
            ((LiteralArgumentBuilder)Commands.literal("replace")
                  .then(
                     Commands.literal("entity")
                        .then(
                           Commands.argument("entities", EntityArgument.entities())
                              .then(
                                 var1.construct(
                                       Commands.argument("slot", SlotArgument.slot()),
                                       (var0x, var1x, var2) -> entityReplace(
                                             EntityArgument.getEntities(var0x, "entities"), SlotArgument.getSlot(var0x, "slot"), var1x.size(), var1x, var2
                                          )
                                    )
                                    .then(
                                       var1.construct(
                                          Commands.argument("count", IntegerArgumentType.integer(0)),
                                          (var0x, var1x, var2) -> entityReplace(
                                                EntityArgument.getEntities(var0x, "entities"),
                                                SlotArgument.getSlot(var0x, "slot"),
                                                IntegerArgumentType.getInteger(var0x, "count"),
                                                var1x,
                                                var2
                                             )
                                       )
                                    )
                              )
                        )
                  ))
               .then(
                  Commands.literal("block")
                     .then(
                        Commands.argument("targetPos", BlockPosArgument.blockPos())
                           .then(
                              var1.construct(
                                    Commands.argument("slot", SlotArgument.slot()),
                                    (var0x, var1x, var2) -> blockReplace(
                                          (CommandSourceStack)var0x.getSource(),
                                          BlockPosArgument.getLoadedBlockPos(var0x, "targetPos"),
                                          SlotArgument.getSlot(var0x, "slot"),
                                          var1x.size(),
                                          var1x,
                                          var2
                                       )
                                 )
                                 .then(
                                    var1.construct(
                                       Commands.argument("count", IntegerArgumentType.integer(0)),
                                       (var0x, var1x, var2) -> blockReplace(
                                             (CommandSourceStack)var0x.getSource(),
                                             BlockPosArgument.getLoadedBlockPos(var0x, "targetPos"),
                                             IntegerArgumentType.getInteger(var0x, "slot"),
                                             IntegerArgumentType.getInteger(var0x, "count"),
                                             var1x,
                                             var2
                                          )
                                    )
                                 )
                           )
                     )
               )
         )
         .then(
            Commands.literal("insert")
               .then(
                  var1.construct(
                     Commands.argument("targetPos", BlockPosArgument.blockPos()),
                     (var0x, var1x, var2) -> blockDistribute(
                           (CommandSourceStack)var0x.getSource(), BlockPosArgument.getLoadedBlockPos(var0x, "targetPos"), var1x, var2
                        )
                  )
               )
         )
         .then(
            Commands.literal("give")
               .then(
                  var1.construct(
                     Commands.argument("players", EntityArgument.players()),
                     (var0x, var1x, var2) -> playerGive(EntityArgument.getPlayers(var0x, "players"), var1x, var2)
                  )
               )
         )
         .then(
            Commands.literal("spawn")
               .then(
                  var1.construct(
                     Commands.argument("targetPos", Vec3Argument.vec3()),
                     (var0x, var1x, var2) -> dropInWorld((CommandSourceStack)var0x.getSource(), Vec3Argument.getVec3(var0x, "targetPos"), var1x, var2)
                  )
               )
         );
   }

   private static Container getContainer(CommandSourceStack var0, BlockPos var1) throws CommandSyntaxException {
      BlockEntity var2 = var0.getLevel().getBlockEntity(var1);
      if (!(var2 instanceof Container)) {
         throw ItemCommands.ERROR_TARGET_NOT_A_CONTAINER.create(var1.getX(), var1.getY(), var1.getZ());
      } else {
         return (Container)var2;
      }
   }

   private static int blockDistribute(CommandSourceStack var0, BlockPos var1, List<ItemStack> var2, LootCommand.Callback var3) throws CommandSyntaxException {
      Container var4 = getContainer(var0, var1);
      ArrayList var5 = Lists.newArrayListWithCapacity(var2.size());

      for(ItemStack var7 : var2) {
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

   private static int blockReplace(CommandSourceStack var0, BlockPos var1, int var2, int var3, List<ItemStack> var4, LootCommand.Callback var5) throws CommandSyntaxException {
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
         throw ItemCommands.ERROR_TARGET_INAPPLICABLE_SLOT.create(var2);
      }
   }

   private static boolean canMergeItems(ItemStack var0, ItemStack var1) {
      return var0.getCount() <= var0.getMaxStackSize() && ItemStack.isSameItemSameComponents(var0, var1);
   }

   private static int playerGive(Collection<ServerPlayer> var0, List<ItemStack> var1, LootCommand.Callback var2) throws CommandSyntaxException {
      ArrayList var3 = Lists.newArrayListWithCapacity(var1.size());

      for(ItemStack var5 : var1) {
         for(ServerPlayer var7 : var0) {
            if (var7.getInventory().add(var5.copy())) {
               var3.add(var5);
            }
         }
      }

      var2.accept(var3);
      return var3.size();
   }

   private static void setSlots(Entity var0, List<ItemStack> var1, int var2, int var3, List<ItemStack> var4) {
      for(int var5 = 0; var5 < var3; ++var5) {
         ItemStack var6 = var5 < var1.size() ? (ItemStack)var1.get(var5) : ItemStack.EMPTY;
         SlotAccess var7 = var0.getSlot(var2 + var5);
         if (var7 != SlotAccess.NULL && var7.set(var6.copy())) {
            var4.add(var6);
         }
      }
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static int entityReplace(Collection<? extends Entity> var0, int var1, int var2, List<ItemStack> var3, LootCommand.Callback var4) throws CommandSyntaxException {
      ArrayList var5 = Lists.newArrayListWithCapacity(var3.size());

      for(Entity var7 : var0) {
         if (var7 instanceof ServerPlayer var8) {
            setSlots(var7, var3, var1, var2, var5);
            var8.containerMenu.broadcastChanges();
         } else {
            setSlots(var7, var3, var1, var2, var5);
         }
      }

      var4.accept(var5);
      return var5.size();
   }

   private static int dropInWorld(CommandSourceStack var0, Vec3 var1, List<ItemStack> var2, LootCommand.Callback var3) throws CommandSyntaxException {
      ServerLevel var4 = var0.getLevel();
      var2.forEach(var2x -> {
         ItemEntity var3xx = new ItemEntity(var4, var1.x, var1.y, var1.z, var2x.copy());
         var3xx.setDefaultPickUpDelay();
         var4.addFreshEntity(var3xx);
      });
      var3.accept(var2);
      return var2.size();
   }

   private static void callback(CommandSourceStack var0, List<ItemStack> var1) {
      if (var1.size() == 1) {
         ItemStack var2 = (ItemStack)var1.get(0);
         var0.sendSuccess(() -> Component.translatable("commands.drop.success.single", var2.getCount(), var2.getDisplayName()), false);
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.drop.success.multiple", var1.size()), false);
      }
   }

   private static void callback(CommandSourceStack var0, List<ItemStack> var1, ResourceKey<LootTable> var2) {
      if (var1.size() == 1) {
         ItemStack var3 = (ItemStack)var1.get(0);
         var0.sendSuccess(
            () -> Component.translatable(
                  "commands.drop.success.single_with_table", var3.getCount(), var3.getDisplayName(), Component.translationArg(var2.location())
               ),
            false
         );
      } else {
         var0.sendSuccess(
            () -> Component.translatable("commands.drop.success.multiple_with_table", var1.size(), Component.translationArg(var2.location())), false
         );
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

   private static int dropBlockLoot(CommandContext<CommandSourceStack> var0, BlockPos var1, ItemStack var2, LootCommand.DropConsumer var3) throws CommandSyntaxException {
      CommandSourceStack var4 = (CommandSourceStack)var0.getSource();
      ServerLevel var5 = var4.getLevel();
      BlockState var6 = var5.getBlockState(var1);
      BlockEntity var7 = var5.getBlockEntity(var1);
      LootParams.Builder var8 = new LootParams.Builder(var5)
         .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var1))
         .withParameter(LootContextParams.BLOCK_STATE, var6)
         .withOptionalParameter(LootContextParams.BLOCK_ENTITY, var7)
         .withOptionalParameter(LootContextParams.THIS_ENTITY, var4.getEntity())
         .withParameter(LootContextParams.TOOL, var2);
      List var9 = var6.getDrops(var8);
      return var3.accept(var0, var9, var2x -> callback(var4, var2x, var6.getBlock().getLootTable()));
   }

   private static int dropKillLoot(CommandContext<CommandSourceStack> var0, Entity var1, LootCommand.DropConsumer var2) throws CommandSyntaxException {
      if (!(var1 instanceof LivingEntity)) {
         throw ERROR_NO_LOOT_TABLE.create(var1.getDisplayName());
      } else {
         ResourceKey var3 = ((LivingEntity)var1).getLootTable();
         CommandSourceStack var4 = (CommandSourceStack)var0.getSource();
         LootParams.Builder var5 = new LootParams.Builder(var4.getLevel());
         Entity var6 = var4.getEntity();
         if (var6 instanceof Player var7) {
            var5.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, var7);
         }

         var5.withParameter(LootContextParams.DAMAGE_SOURCE, var1.damageSources().magic());
         var5.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, var6);
         var5.withOptionalParameter(LootContextParams.KILLER_ENTITY, var6);
         var5.withParameter(LootContextParams.THIS_ENTITY, var1);
         var5.withParameter(LootContextParams.ORIGIN, var4.getPosition());
         LootParams var10 = var5.create(LootContextParamSets.ENTITY);
         LootTable var8 = var4.getServer().reloadableRegistries().getLootTable(var3);
         ObjectArrayList var9 = var8.getRandomItems(var10);
         return var2.accept(var0, var9, var2x -> callback(var4, var2x, var3));
      }
   }

   private static int dropChestLoot(CommandContext<CommandSourceStack> var0, Holder<LootTable> var1, LootCommand.DropConsumer var2) throws CommandSyntaxException {
      CommandSourceStack var3 = (CommandSourceStack)var0.getSource();
      LootParams var4 = new LootParams.Builder(var3.getLevel())
         .withOptionalParameter(LootContextParams.THIS_ENTITY, var3.getEntity())
         .withParameter(LootContextParams.ORIGIN, var3.getPosition())
         .create(LootContextParamSets.CHEST);
      return drop(var0, var1, var4, var2);
   }

   private static int dropFishingLoot(
      CommandContext<CommandSourceStack> var0, Holder<LootTable> var1, BlockPos var2, ItemStack var3, LootCommand.DropConsumer var4
   ) throws CommandSyntaxException {
      CommandSourceStack var5 = (CommandSourceStack)var0.getSource();
      LootParams var6 = new LootParams.Builder(var5.getLevel())
         .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var2))
         .withParameter(LootContextParams.TOOL, var3)
         .withOptionalParameter(LootContextParams.THIS_ENTITY, var5.getEntity())
         .create(LootContextParamSets.FISHING);
      return drop(var0, var1, var6, var4);
   }

   private static int drop(CommandContext<CommandSourceStack> var0, Holder<LootTable> var1, LootParams var2, LootCommand.DropConsumer var3) throws CommandSyntaxException {
      CommandSourceStack var4 = (CommandSourceStack)var0.getSource();
      ObjectArrayList var5 = ((LootTable)var1.value()).getRandomItems(var2);
      return var3.accept(var0, var5, var1x -> callback(var4, var1x));
   }

   @FunctionalInterface
   interface Callback {
      void accept(List<ItemStack> var1) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface DropConsumer {
      int accept(CommandContext<CommandSourceStack> var1, List<ItemStack> var2, LootCommand.Callback var3) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface TailProvider {
      ArgumentBuilder<CommandSourceStack, ?> construct(ArgumentBuilder<CommandSourceStack, ?> var1, LootCommand.DropConsumer var2);
   }
}
