package net.minecraft.world.item.crafting.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.block.entity.FuelValues;

public interface SlotDisplay {
   Codec<SlotDisplay> CODEC = BuiltInRegistries.SLOT_DISPLAY.byNameCodec().dispatch(SlotDisplay::type, Type::codec);
   StreamCodec<RegistryFriendlyByteBuf, SlotDisplay> STREAM_CODEC = ByteBufCodecs.registry(Registries.SLOT_DISPLAY).dispatch(SlotDisplay::type, Type::streamCodec);

   <T> Stream<T> resolve(ContextMap context, DisplayContentsFactory<T> builder);

   Type<? extends SlotDisplay> type();

   default boolean isEnabled(final FeatureFlagSet enabledFeatures) {
      return true;
   }

   default List<ItemStack> resolveForStacks(final ContextMap context) {
      return this.resolve(context, SlotDisplay.ItemStackContentsFactory.INSTANCE).toList();
   }

   default ItemStack resolveForFirstStack(final ContextMap context) {
      return (ItemStack)this.resolve(context, SlotDisplay.ItemStackContentsFactory.INSTANCE).findFirst().orElse(ItemStack.EMPTY);
   }

   private static <T> Stream<T> applyDemoTransformation(final ContextMap context, final DisplayContentsFactory<T> factory, final SlotDisplay firstDisplay, final SlotDisplay secondDisplay, final RandomSource randomSource, final BinaryOperator<ItemStack> operation) {
      if (factory instanceof DisplayContentsFactory.ForStacks<T> stacks) {
         List<ItemStack> firstItems = firstDisplay.resolveForStacks(context);
         if (firstItems.isEmpty()) {
            return Stream.empty();
         } else {
            List<ItemStack> secondItems = secondDisplay.resolveForStacks(context);
            if (secondItems.isEmpty()) {
               return Stream.empty();
            } else {
               Stream var10000 = Stream.generate(() -> {
                  ItemStack first = (ItemStack)Util.getRandom(firstItems, randomSource);
                  ItemStack second = (ItemStack)Util.getRandom(secondItems, randomSource);
                  return (ItemStack)operation.apply(first, second);
               }).limit(256L).filter((s) -> !s.isEmpty()).limit(16L);
               Objects.requireNonNull(stacks);
               return var10000.map(stacks::forStack);
            }
         }
      } else {
         return Stream.empty();
      }
   }

   private static <T> Stream<T> applyDemoTransformation(final ContextMap context, final DisplayContentsFactory<T> factory, final SlotDisplay firstDisplay, final SlotDisplay secondDisplay, final BinaryOperator<ItemStack> operation) {
      if (factory instanceof DisplayContentsFactory.ForStacks<T> stacks) {
         List<ItemStack> firstItems = firstDisplay.resolveForStacks(context);
         if (firstItems.isEmpty()) {
            return Stream.empty();
         } else {
            List<ItemStack> secondItems = secondDisplay.resolveForStacks(context);
            if (secondItems.isEmpty()) {
               return Stream.empty();
            } else {
               int cycle = firstItems.size() * secondItems.size();
               Stream var10000 = IntStream.range(0, cycle).mapToObj((index) -> {
                  int firstItemCount = firstItems.size();
                  int firstItemIndex = index % firstItemCount;
                  int secondItemIndex = index / firstItemCount;
                  ItemStack first = (ItemStack)firstItems.get(firstItemIndex);
                  ItemStack second = (ItemStack)secondItems.get(secondItemIndex);
                  return (ItemStack)operation.apply(first, second);
               }).filter((s) -> !s.isEmpty()).limit(16L);
               Objects.requireNonNull(stacks);
               return var10000.map(stacks::forStack);
            }
         }
      } else {
         return Stream.empty();
      }
   }

   public static record Type<T extends SlotDisplay>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
      public Type {
         super();
      }
   }

   public static class ItemStackContentsFactory implements DisplayContentsFactory.ForStacks<ItemStack> {
      public static final ItemStackContentsFactory INSTANCE = new ItemStackContentsFactory();

      public ItemStackContentsFactory() {
         super();
      }

      public ItemStack forStack(final ItemStack stack) {
         return stack;
      }
   }

   public static class Empty implements SlotDisplay {
      public static final Empty INSTANCE = new Empty();
      public static final MapCodec<Empty> MAP_CODEC;
      public static final StreamCodec<RegistryFriendlyByteBuf, Empty> STREAM_CODEC;
      public static final Type<Empty> TYPE;

      private Empty() {
         super();
      }

      public Type<Empty> type() {
         return TYPE;
      }

      public String toString() {
         return "<empty>";
      }

      public <T> Stream<T> resolve(final ContextMap context, final DisplayContentsFactory<T> factory) {
         return Stream.empty();
      }

      static {
         MAP_CODEC = MapCodec.unit(INSTANCE);
         STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, Empty>unit(INSTANCE);
         TYPE = new Type<Empty>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static class AnyFuel implements SlotDisplay {
      public static final AnyFuel INSTANCE = new AnyFuel();
      public static final MapCodec<AnyFuel> MAP_CODEC;
      public static final StreamCodec<RegistryFriendlyByteBuf, AnyFuel> STREAM_CODEC;
      public static final Type<AnyFuel> TYPE;

      private AnyFuel() {
         super();
      }

      public Type<AnyFuel> type() {
         return TYPE;
      }

      public String toString() {
         return "<any fuel>";
      }

      public <T> Stream<T> resolve(final ContextMap context, final DisplayContentsFactory<T> factory) {
         if (factory instanceof DisplayContentsFactory.ForStacks<T> stacks) {
            FuelValues fuelValues = (FuelValues)context.getOptional(SlotDisplayContext.FUEL_VALUES);
            if (fuelValues != null) {
               Stream var10000 = fuelValues.fuelItems().stream();
               Objects.requireNonNull(stacks);
               return var10000.map(stacks::forStack);
            }
         }

         return Stream.empty();
      }

      static {
         MAP_CODEC = MapCodec.unit(INSTANCE);
         STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, AnyFuel>unit(INSTANCE);
         TYPE = new Type<AnyFuel>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static record WithAnyPotion(SlotDisplay display) implements SlotDisplay {
      public static final MapCodec<WithAnyPotion> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotDisplay.CODEC.fieldOf("contents").forGetter(WithAnyPotion::display)).apply(i, WithAnyPotion::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, WithAnyPotion> STREAM_CODEC;
      public static final Type<WithAnyPotion> TYPE;

      public WithAnyPotion {
         super();
      }

      public Type<WithAnyPotion> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(final ContextMap context, final DisplayContentsFactory<T> factory) {
         if (factory instanceof DisplayContentsFactory.ForStacks<T> stacks) {
            List<ItemStack> displayItems = this.display.resolveForStacks(context);
            Optional<? extends HolderLookup.RegistryLookup<Potion>> potions = Optional.ofNullable((HolderLookup.Provider)context.getOptional(SlotDisplayContext.REGISTRIES)).flatMap((r) -> r.lookup(Registries.POTION));
            return potions.stream().flatMap(HolderLookup::listElements).flatMap((potion) -> {
               PotionContents potionContents = new PotionContents(potion);
               return displayItems.stream().map((item) -> {
                  ItemStack itemCopy = item.copy();
                  itemCopy.set(DataComponents.POTION_CONTENTS, potionContents);
                  return stacks.forStack(itemCopy);
               });
            });
         } else {
            return Stream.empty();
         }
      }

      static {
         STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, WithAnyPotion::display, WithAnyPotion::new);
         TYPE = new Type<WithAnyPotion>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static record OnlyWithComponent(SlotDisplay source, DataComponentType<?> component) implements SlotDisplay {
      public static final MapCodec<OnlyWithComponent> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotDisplay.CODEC.fieldOf("contents").forGetter(OnlyWithComponent::source), DataComponentType.CODEC.fieldOf("component").forGetter(OnlyWithComponent::component)).apply(i, OnlyWithComponent::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, OnlyWithComponent> STREAM_CODEC;
      public static final Type<OnlyWithComponent> TYPE;

      public OnlyWithComponent {
         super();
      }

      public <T> Stream<T> resolve(final ContextMap context, final DisplayContentsFactory<T> builder) {
         if (builder instanceof DisplayContentsFactory.ForStacks<T> stacks) {
            Stream var10000 = this.source.resolve(context, SlotDisplay.ItemStackContentsFactory.INSTANCE).filter((s) -> s.has(this.component));
            Objects.requireNonNull(stacks);
            return var10000.map(stacks::forStack);
         } else {
            return Stream.empty();
         }
      }

      public Type<OnlyWithComponent> type() {
         return TYPE;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, OnlyWithComponent::source, DataComponentType.STREAM_CODEC, OnlyWithComponent::component, OnlyWithComponent::new);
         TYPE = new Type<OnlyWithComponent>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static record DyedSlotDemo(SlotDisplay dye, SlotDisplay target) implements SlotDisplay {
      public static final MapCodec<DyedSlotDemo> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotDisplay.CODEC.fieldOf("dye").forGetter(DyedSlotDemo::dye), SlotDisplay.CODEC.fieldOf("target").forGetter(DyedSlotDemo::target)).apply(i, DyedSlotDemo::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, DyedSlotDemo> STREAM_CODEC;
      public static final Type<DyedSlotDemo> TYPE;

      public DyedSlotDemo {
         super();
      }

      public Type<DyedSlotDemo> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(final ContextMap context, final DisplayContentsFactory<T> factory) {
         BinaryOperator<ItemStack> tranformation = (target, dye) -> {
            DyeColor dyeValue = (DyeColor)dye.getOrDefault(DataComponents.DYE, DyeColor.WHITE);
            return DyedItemColor.applyDyes(target.copy(), List.of(dyeValue));
         };
         return SlotDisplay.<T>applyDemoTransformation(context, factory, this.target, this.dye, tranformation);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, DyedSlotDemo::dye, SlotDisplay.STREAM_CODEC, DyedSlotDemo::target, DyedSlotDemo::new);
         TYPE = new Type<DyedSlotDemo>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static record SmithingTrimDemoSlotDisplay(SlotDisplay base, SlotDisplay material, Holder<TrimPattern> pattern) implements SlotDisplay {
      public static final MapCodec<SmithingTrimDemoSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotDisplay.CODEC.fieldOf("base").forGetter(SmithingTrimDemoSlotDisplay::base), SlotDisplay.CODEC.fieldOf("material").forGetter(SmithingTrimDemoSlotDisplay::material), TrimPattern.CODEC.fieldOf("pattern").forGetter(SmithingTrimDemoSlotDisplay::pattern)).apply(i, SmithingTrimDemoSlotDisplay::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTrimDemoSlotDisplay> STREAM_CODEC;
      public static final Type<SmithingTrimDemoSlotDisplay> TYPE;

      public SmithingTrimDemoSlotDisplay {
         super();
      }

      public Type<SmithingTrimDemoSlotDisplay> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(final ContextMap context, final DisplayContentsFactory<T> factory) {
         RandomSource randomSource = RandomSource.create((long)System.identityHashCode(this));
         BinaryOperator<ItemStack> tranformation = (base, material) -> SmithingTrimRecipe.applyTrim(base, material, this.pattern);
         return SlotDisplay.<T>applyDemoTransformation(context, factory, this.base, this.material, randomSource, tranformation);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, SmithingTrimDemoSlotDisplay::base, SlotDisplay.STREAM_CODEC, SmithingTrimDemoSlotDisplay::material, TrimPattern.STREAM_CODEC, SmithingTrimDemoSlotDisplay::pattern, SmithingTrimDemoSlotDisplay::new);
         TYPE = new Type<SmithingTrimDemoSlotDisplay>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static record ItemSlotDisplay(Holder<Item> item) implements SlotDisplay {
      public static final MapCodec<ItemSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Item.CODEC.fieldOf("item").forGetter(ItemSlotDisplay::item)).apply(i, ItemSlotDisplay::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, ItemSlotDisplay> STREAM_CODEC;
      public static final Type<ItemSlotDisplay> TYPE;

      public ItemSlotDisplay(final Item item) {
         this((Holder)item.builtInRegistryHolder());
      }

      public ItemSlotDisplay {
         super();
      }

      public Type<ItemSlotDisplay> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(final ContextMap context, final DisplayContentsFactory<T> factory) {
         if (factory instanceof DisplayContentsFactory.ForStacks<T> stacks) {
            return Stream.of(stacks.forStack(this.item));
         } else {
            return Stream.empty();
         }
      }

      public boolean isEnabled(final FeatureFlagSet enabledFeatures) {
         return ((Item)this.item.value()).isEnabled(enabledFeatures);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(Item.STREAM_CODEC, ItemSlotDisplay::item, ItemSlotDisplay::new);
         TYPE = new Type<ItemSlotDisplay>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static record ItemStackSlotDisplay(ItemStackTemplate stack) implements SlotDisplay {
      public static final MapCodec<ItemStackSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ItemStackTemplate.CODEC.fieldOf("item").forGetter(ItemStackSlotDisplay::stack)).apply(i, ItemStackSlotDisplay::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackSlotDisplay> STREAM_CODEC;
      public static final Type<ItemStackSlotDisplay> TYPE;

      public ItemStackSlotDisplay {
         super();
      }

      public Type<ItemStackSlotDisplay> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(final ContextMap context, final DisplayContentsFactory<T> factory) {
         if (factory instanceof DisplayContentsFactory.ForStacks<T> stacks) {
            return Stream.of(stacks.forStack(this.stack.create()));
         } else {
            return Stream.empty();
         }
      }

      public boolean isEnabled(final FeatureFlagSet enabledFeatures) {
         return ((Item)this.stack.item().value()).isEnabled(enabledFeatures);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ItemStackTemplate.STREAM_CODEC, ItemStackSlotDisplay::stack, ItemStackSlotDisplay::new);
         TYPE = new Type<ItemStackSlotDisplay>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static record TagSlotDisplay(TagKey<Item> tag) implements SlotDisplay {
      public static final MapCodec<TagSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(TagSlotDisplay::tag)).apply(i, TagSlotDisplay::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, TagSlotDisplay> STREAM_CODEC;
      public static final Type<TagSlotDisplay> TYPE;

      public TagSlotDisplay {
         super();
      }

      public Type<TagSlotDisplay> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(final ContextMap context, final DisplayContentsFactory<T> factory) {
         if (factory instanceof DisplayContentsFactory.ForStacks<T> stacks) {
            HolderLookup.Provider registries = (HolderLookup.Provider)context.getOptional(SlotDisplayContext.REGISTRIES);
            if (registries != null) {
               return registries.lookupOrThrow(Registries.ITEM).get(this.tag).map((t) -> {
                  Stream var10000 = t.stream();
                  Objects.requireNonNull(stacks);
                  return var10000.map(stacks::forStack);
               }).stream().flatMap((s) -> s);
            }
         }

         return Stream.empty();
      }

      static {
         STREAM_CODEC = StreamCodec.composite(TagKey.streamCodec(Registries.ITEM), TagSlotDisplay::tag, TagSlotDisplay::new);
         TYPE = new Type<TagSlotDisplay>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static record Composite(List<SlotDisplay> contents) implements SlotDisplay {
      public static final MapCodec<Composite> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotDisplay.CODEC.listOf().fieldOf("contents").forGetter(Composite::contents)).apply(i, Composite::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, Composite> STREAM_CODEC;
      public static final Type<Composite> TYPE;

      public Composite {
         super();
      }

      public Type<Composite> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(final ContextMap context, final DisplayContentsFactory<T> factory) {
         return this.contents.stream().flatMap((d) -> d.resolve(context, factory));
      }

      public boolean isEnabled(final FeatureFlagSet enabledFeatures) {
         return this.contents.stream().allMatch((c) -> c.isEnabled(enabledFeatures));
      }

      static {
         STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), Composite::contents, Composite::new);
         TYPE = new Type<Composite>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static record WithRemainder(SlotDisplay input, SlotDisplay remainder) implements SlotDisplay {
      public static final MapCodec<WithRemainder> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotDisplay.CODEC.fieldOf("input").forGetter(WithRemainder::input), SlotDisplay.CODEC.fieldOf("remainder").forGetter(WithRemainder::remainder)).apply(i, WithRemainder::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, WithRemainder> STREAM_CODEC;
      public static final Type<WithRemainder> TYPE;

      public WithRemainder {
         super();
      }

      public Type<WithRemainder> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(final ContextMap context, final DisplayContentsFactory<T> factory) {
         if (factory instanceof DisplayContentsFactory.ForRemainders<T> remainders) {
            List<T> resolvedRemainders = this.remainder.resolve(context, factory).toList();
            return this.input.resolve(context, factory).map((input) -> remainders.addRemainder(input, resolvedRemainders));
         } else {
            return this.input.<T>resolve(context, factory);
         }
      }

      public boolean isEnabled(final FeatureFlagSet enabledFeatures) {
         return this.input.isEnabled(enabledFeatures) && this.remainder.isEnabled(enabledFeatures);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, WithRemainder::input, SlotDisplay.STREAM_CODEC, WithRemainder::remainder, WithRemainder::new);
         TYPE = new Type<WithRemainder>(MAP_CODEC, STREAM_CODEC);
      }
   }
}
