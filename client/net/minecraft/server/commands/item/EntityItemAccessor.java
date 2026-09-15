package net.minecraft.server.commands.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.ArgProvider;
import net.minecraft.server.commands.CommandResponseTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.slot.SlotCollection;
import net.minecraft.world.item.slot.SlotSource;
import org.jspecify.annotations.Nullable;

public record EntityItemAccessor(Collection<? extends Entity> entities) implements ItemAccessor<Entity> {
   public static final ArgProvider.Factory<ItemAccessor<?>> PROVIDER = (arg) -> ArgProvider.create("entity", () -> Commands.argument(arg, EntityArgument.entities()), (c) -> new EntityItemAccessor(EntityArgument.getEntities(c, arg)));
   private static final CommandResponseTracker.Messages<Entity> RESPONSE_SET;
   private static final CommandResponseTracker.MessagesWithArg<Entity, ItemStack> RESPONSE_SET_KNOWN_ITEM;
   private static final CommandResponseTracker.Messages<Entity> RESPONSE_MODIFY;

   public EntityItemAccessor {
      super();
   }

   public SlotCollection getSlots(final CommandSourceStack source, final SlotSource slotSource) {
      List<SlotCollection> slotCollections = new ArrayList();

      for(Entity entity : this.entities) {
         SlotCollection slots = ItemCommands.getSlotsFromProvider(source, entity, slotSource);
         slotCollections.add(slots);
      }

      return SlotCollection.concat(slotCollections);
   }

   public void setItems(final CommandSourceStack source, final SlotSource slotSource, final ItemAccessor.SetterFunction<Entity> function) {
      for(Entity entity : this.entities) {
         SlotCollection targetSlots = ItemCommands.getSlotsFromProvider(source, entity, slotSource);
         int updatedSlots = function.apply(entity, targetSlots);
         if (updatedSlots > 0 && entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.containerMenu.broadcastChanges();
         }
      }

   }

   public int getReplaceSuccess(final CommandSourceStack source, final CommandResponseTracker<Entity> tracker, final @Nullable ItemStack knownItem) throws CommandSyntaxException {
      return knownItem != null ? tracker.sendFeedback(source, true, (CommandResponseTracker.MessagesWithArg)RESPONSE_SET_KNOWN_ITEM, knownItem) : tracker.sendFeedback(source, true, RESPONSE_SET);
   }

   public int getModifySuccess(final CommandSourceStack source, final CommandResponseTracker<Entity> tracker) throws CommandSyntaxException {
      return tracker.sendFeedback(source, true, RESPONSE_MODIFY);
   }

   static {
      RESPONSE_SET = CommandResponseTracker.messages((SimpleCommandExceptionType)ItemCommands.ERROR_TARGET_NO_CHANGES, (CommandResponseTracker.SingleHandler)((entity, slotCount) -> Component.translatable("commands.item.entity.replace.success.single", slotCount, entity.getDisplayName())), (CommandResponseTracker.MultipleHandler)((entityCount, var1) -> Component.translatable("commands.item.entity.replace.success.multiple", entityCount)));
      DynamicCommandExceptionType var10000 = ItemCommands.ERROR_TARGET_NO_CHANGES_KNOWN_ITEM;
      Objects.requireNonNull(var10000);
      RESPONSE_SET_KNOWN_ITEM = CommandResponseTracker.messages((CommandResponseTracker.ErrorHandlerWithArg)(var10000::create), (CommandResponseTracker.SingleHandlerWithArg)((entity, slotCount, itemStack) -> Component.translatable("commands.item.entity.replace.success.single.known_item", slotCount, entity.getDisplayName(), itemStack.getDisplayName())), (CommandResponseTracker.MultipleHandlerWithArg)((entityCount, var1, itemStack) -> Component.translatable("commands.item.entity.replace.success.multiple.known_item", entityCount, itemStack.getDisplayName())));
      RESPONSE_MODIFY = CommandResponseTracker.messages((SimpleCommandExceptionType)ItemCommands.ERROR_TARGET_NO_CHANGES, (CommandResponseTracker.SingleHandler)((entity, slotCount) -> Component.translatable("commands.item.entity.modify.success.single", slotCount, entity.getDisplayName())), (CommandResponseTracker.MultipleHandler)((entityCount, var1) -> Component.translatable("commands.item.entity.modify.success.multiple", entityCount)));
   }
}
