package net.minecraft.server.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.commands.CommandPermissionLevels;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IntegerArgument;
import net.minecraft.commands.arguments.LootModifierArgument;
import net.minecraft.commands.arguments.SlotSourceArgument;
import net.minecraft.commands.arguments.ValidationWrapper;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.ArgProvider;
import net.minecraft.server.commands.CommandResponseTracker;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.SlotProvider;
import net.minecraft.world.item.ItemProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.slot.CountingModifier;
import net.minecraft.world.item.slot.SlotCollection;
import net.minecraft.world.item.slot.SlotSelector;
import net.minecraft.world.item.slot.SlotSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.Nullable;

public class ItemCommands {
   public static final Dynamic3CommandExceptionType ERROR_TARGET_NOT_A_CONTAINER = new Dynamic3CommandExceptionType((x, y, z) -> Component.translatableEscape("commands.item.target.not_a_container", x, y, z));
   public static final Dynamic3CommandExceptionType ERROR_SOURCE_NOT_A_CONTAINER = new Dynamic3CommandExceptionType((x, y, z) -> Component.translatableEscape("commands.item.source.not_a_container", x, y, z));
   public static final DynamicCommandExceptionType ERROR_TARGET_INAPPLICABLE_SLOT = new DynamicCommandExceptionType((slot) -> Component.translatableEscape("commands.item.target.no_such_slot", slot));
   private static final SimpleCommandExceptionType ERROR_TARGET_INAPPLICABLE_UNNAMED_SLOT = new SimpleCommandExceptionType(Component.translatable("commands.item.target.no_such_slot.unnamed"));
   private static final DynamicCommandExceptionType ERROR_SOURCE_INAPPLICABLE_SLOT = new DynamicCommandExceptionType((slot) -> Component.translatableEscape("commands.item.source.no_such_slot", slot));
   private static final SimpleCommandExceptionType ERROR_SOURCE_INAPPLICABLE_UNNAMED_SLOT = new SimpleCommandExceptionType(Component.translatable("commands.item.source.no_such_slot.unnamed"));
   public static final SimpleCommandExceptionType ERROR_TARGET_NO_CHANGES = new SimpleCommandExceptionType(Component.translatable("commands.item.target.failed"));
   public static final DynamicCommandExceptionType ERROR_TARGET_NO_CHANGES_KNOWN_ITEM = new DynamicCommandExceptionType((item) -> Component.translatableEscape("commands.item.target.failed.known_item", item));
   public static final List<ArgProvider.Factory<ItemAccessor<?>>> ALL_PROVIDERS;
   public static final List<ArgProvider<ItemAccessor<?>>> TARGET_PROVIDERS;
   public static final List<ArgProvider<ItemAccessor<?>>> SOURCE_PROVIDERS;
   public static final ContextKeySet SLOT_SOURCE_CONTEXT;

   public ItemCommands() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> root = (LiteralArgumentBuilder)Commands.literal("item").requires(Commands.hasPermission(CommandPermissionLevels.LEVEL_GAMEMASTERS));

      for(ArgProvider<ItemAccessor<?>> targetProvider : TARGET_PROVIDERS) {
         ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)root.then(wrapSetItems(Commands.literal("replace"), targetProvider, ItemCommands::replaceItemProvider))).then(wrapSetItems(Commands.literal("fill"), targetProvider, ItemCommands::fillItemProvider))).then(wrapSetItems(Commands.literal("override"), targetProvider, ItemCommands::overrideItemProvider))).then(targetProvider.wrap(Commands.literal("modify"), (p) -> p.then(Commands.argument("slots", SlotSourceArgument.slotSource(SLOT_SOURCE_CONTEXT)).then(Commands.argument("modifier", LootModifierArgument.lootModifier(LootContextParamSets.COMMAND_MODIFIER)).executes((c) -> modifyItems((CommandSourceStack)c.getSource(), targetProvider.access(c), SlotSourceArgument.getSlotSource(c, "slots"), LootModifierArgument.getLootModifier(c, "modifier")))))));
      }

      dispatcher.register(root);
   }

   private static ArgumentBuilder<CommandSourceStack, ?> wrapSetItems(final ArgumentBuilder<CommandSourceStack, ?> builder, final ArgProvider<ItemAccessor<?>> targetProvider, final ItemDistributor distributor) {
      return targetProvider.wrap(builder, (t) -> {
         ArgumentBuilder<CommandSourceStack, ?> slotsNode = Commands.argument("slots", SlotSourceArgument.slotSource(SLOT_SOURCE_CONTEXT));

         for(ArgProvider<ItemAccessor<?>> sourceProvider : SOURCE_PROVIDERS) {
            slotsNode.then(sourceProvider.wrap(Commands.literal("from"), (p) -> p.then(((RequiredArgumentBuilder)Commands.argument("sourceSlots", SlotSourceArgument.slotSource(SLOT_SOURCE_CONTEXT)).executes((c) -> setItems((CommandSourceStack)c.getSource(), targetProvider.access(c), SlotSourceArgument.getSlotSource(c, "slots"), distributor, getItems((CommandSourceStack)c.getSource(), sourceProvider.access(c), SlotSourceArgument.getSlotSource(c, "sourceSlots"), (ValidationWrapper)null)))).then(Commands.argument("modifier", LootModifierArgument.lootModifier(LootContextParamSets.COMMAND_MODIFIER)).executes((c) -> setItems((CommandSourceStack)c.getSource(), targetProvider.access(c), SlotSourceArgument.getSlotSource(c, "slots"), distributor, getItems((CommandSourceStack)c.getSource(), sourceProvider.access(c), SlotSourceArgument.getSlotSource(c, "sourceSlots"), LootModifierArgument.getLootModifier(c, "modifier"))))))));
         }

         slotsNode.then(Commands.literal("with").then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item()).executes((c) -> setItem((CommandSourceStack)c.getSource(), targetProvider.access(c), SlotSourceArgument.getSlotSource(c, "slots"), distributor, ItemArgument.getItem(c, "item").createItemStack(1)))).then(Commands.argument("count", IntegerArgument.integer(1, 99)).executes((c) -> setItem((CommandSourceStack)c.getSource(), targetProvider.access(c), SlotSourceArgument.getSlotSource(c, "slots"), distributor, ItemArgument.getItem(c, "item").createItemStack(IntegerArgument.getInteger(c, "count")))))));
         return t.then(slotsNode);
      });
   }

   public static ItemProvider replaceItemProvider(final Collection<ItemStack> items) {
      return ItemProvider.of(items);
   }

   public static ItemProvider fillItemProvider(final Collection<ItemStack> items) {
      return ItemProvider.cycle(items);
   }

   public static ItemProvider overrideItemProvider(final Collection<ItemStack> items) {
      return ItemProvider.of(items).orElseProvide(ItemStack.EMPTY);
   }

   private static <Target> int setItems(final CommandSourceStack source, final ItemAccessor<Target> accessor, final SlotSourceArgument.SlotResult slotSource, final ItemProvider items, final @Nullable ItemStack knownItem) throws CommandSyntaxException {
      MutableInt selectedCount = new MutableInt();
      SlotSelector slotSelector = SlotSelector.tracking(selectedCount);
      CommandResponseTracker<Target> tracker = CommandResponseTracker.<Target>create();
      accessor.setItems(source, slotSource.value(), (target, slots) -> {
         items.restart();
         int count = slots.replaceSlotItems(items, slotSelector);
         tracker.track(target, count);
         return count;
      });
      if (selectedCount.intValue() == 0) {
         Optional var10000 = slotSource.name();
         DynamicCommandExceptionType var10001 = ERROR_TARGET_INAPPLICABLE_SLOT;
         Objects.requireNonNull(var10001);
         var10000 = var10000.map(var10001::create);
         SimpleCommandExceptionType var9 = ERROR_TARGET_INAPPLICABLE_UNNAMED_SLOT;
         Objects.requireNonNull(var9);
         throw (CommandSyntaxException)var10000.orElseGet(var9::create);
      } else {
         return accessor.getReplaceSuccess(source, tracker, knownItem);
      }
   }

   private static int setItems(final CommandSourceStack source, final ItemAccessor<?> accessor, final SlotSourceArgument.SlotResult slotSource, final ItemDistributor distributor, final Collection<ItemStack> items) throws CommandSyntaxException {
      return setItems(source, accessor, slotSource, (ItemProvider)distributor.apply(items), (ItemStack)null);
   }

   private static int setItem(final CommandSourceStack source, final ItemAccessor<?> accessor, final SlotSourceArgument.SlotResult slotSource, final ItemDistributor distributor, final ItemStack item) throws CommandSyntaxException {
      return setItems(source, accessor, slotSource, distributor.apply(List.of(item)), item);
   }

   private static <Target> int modifyItems(final CommandSourceStack source, final ItemAccessor<Target> accessor, final SlotSourceArgument.SlotResult slotSource, final ValidationWrapper<LootItemFunction> modifier) throws CommandSyntaxException {
      MutableInt selectedCount = new MutableInt();
      SlotSelector slotSelector = SlotSelector.tracking(SlotSelector.NON_EMPTY_SLOTS, selectedCount);
      CountingModifier countingModifier = new CountingModifier(applyModifier(source, modifier));
      CommandResponseTracker<Target> tracker = CommandResponseTracker.<Target>create();
      accessor.setItems(source, slotSource.value(), (target, slots) -> {
         int lastUpdated = countingModifier.updatedCount();
         slots.modifySlots(countingModifier, slotSelector);
         int count = countingModifier.updatedCount() - lastUpdated;
         tracker.track(target, count);
         return count;
      });
      if (selectedCount.intValue() == 0) {
         Optional var10000 = slotSource.name();
         DynamicCommandExceptionType var10001 = ERROR_TARGET_INAPPLICABLE_SLOT;
         Objects.requireNonNull(var10001);
         var10000 = var10000.map(var10001::create);
         SimpleCommandExceptionType var9 = ERROR_TARGET_INAPPLICABLE_UNNAMED_SLOT;
         Objects.requireNonNull(var9);
         throw (CommandSyntaxException)var10000.orElseGet(var9::create);
      } else {
         return accessor.getModifySuccess(source, tracker);
      }
   }

   private static Collection<ItemStack> getItems(final CommandSourceStack source, final ItemAccessor<?> accessor, final SlotSourceArgument.SlotResult slotSource, final @Nullable ValidationWrapper<LootItemFunction> modifier) throws CommandSyntaxException {
      SlotCollection slots = accessor.getSlots(source, slotSource.value());
      Collection<ItemStack> items = modifier != null ? slots.itemCopies().map(applyModifier(source, modifier)).toList() : slots.itemCopies().toList();
      if (items.isEmpty()) {
         Optional var10000 = slotSource.name();
         DynamicCommandExceptionType var10001 = ERROR_SOURCE_INAPPLICABLE_SLOT;
         Objects.requireNonNull(var10001);
         var10000 = var10000.map(var10001::create);
         SimpleCommandExceptionType var7 = ERROR_SOURCE_INAPPLICABLE_UNNAMED_SLOT;
         Objects.requireNonNull(var7);
         throw (CommandSyntaxException)var10000.orElseGet(var7::create);
      } else {
         return items;
      }
   }

   private static UnaryOperator<ItemStack> applyModifier(final CommandSourceStack source, final ValidationWrapper<LootItemFunction> modifierWrapper) throws CommandSyntaxException {
      LootItemFunction modifier = modifierWrapper.validateAndGet(LootContextParamSets.COMMAND_MODIFIER).value();
      LootParams lootParams = (new LootParams.Builder(source.getLevel())).withParameter(LootContextParams.ORIGIN, source.getPosition()).withOptionalParameter(LootContextParams.THIS_ENTITY, source.getEntity()).create(LootContextParamSets.COMMAND_MODIFIER);
      LootContext context = (new LootContext.Builder(lootParams)).create(Optional.empty());
      context.pushVisitedElement(LootContext.createVisitedEntry(modifier));
      return (item) -> {
         ItemStack newItem = (ItemStack)modifier.apply(item, context);
         newItem.limitSize(newItem.getMaxStackSize());
         return newItem;
      };
   }

   static SlotCollection getSlotsFromProvider(final CommandSourceStack source, final SlotProvider container, final ValidationWrapper<SlotSource> wrappedSlotSource) throws CommandSyntaxException {
      SlotSource slotSource = wrappedSlotSource.validateAndGet(SLOT_SOURCE_CONTEXT).value();
      LootParams lootParams = (new LootParams.Builder(source.getLevel())).withParameter(LootContextParams.ORIGIN, source.getPosition()).withParameter(LootContextParams.CONTAINER, container).withOptionalParameter(LootContextParams.THIS_ENTITY, source.getEntity()).create(SLOT_SOURCE_CONTEXT);
      LootContext context = (new LootContext.Builder(lootParams)).create(Optional.empty());
      context.pushVisitedElement(LootContext.createVisitedEntry(slotSource));
      return slotSource.provide(context);
   }

   static {
      ALL_PROVIDERS = List.of(EntityItemAccessor.PROVIDER, BlockItemAccessor.PROVIDER);
      TARGET_PROVIDERS = ArgProvider.buildList("target", ALL_PROVIDERS);
      SOURCE_PROVIDERS = ArgProvider.buildList("source", ALL_PROVIDERS);
      SLOT_SOURCE_CONTEXT = LootContextParamSets.COMMAND_SLOT_SOURCE;
   }

   @FunctionalInterface
   private interface ItemDistributor {
      ItemProvider apply(Collection<ItemStack> items);
   }
}
