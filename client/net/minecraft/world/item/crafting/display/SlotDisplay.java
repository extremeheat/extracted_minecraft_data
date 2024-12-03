package net.minecraft.world.item.crafting.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.minecraft.world.level.block.entity.FuelValues;

public interface SlotDisplay {
   Codec<SlotDisplay> CODEC = BuiltInRegistries.SLOT_DISPLAY.byNameCodec().dispatch(SlotDisplay::type, Type::codec);
   StreamCodec<RegistryFriendlyByteBuf, SlotDisplay> STREAM_CODEC = ByteBufCodecs.registry(Registries.SLOT_DISPLAY).dispatch(SlotDisplay::type, Type::streamCodec);

   <T> Stream<T> resolve(ContextMap var1, DisplayContentsFactory<T> var2);

   Type<? extends SlotDisplay> type();

   default boolean isEnabled(FeatureFlagSet var1) {
      return true;
   }

   default List<ItemStack> resolveForStacks(ContextMap var1) {
      return this.resolve(var1, SlotDisplay.ItemStackContentsFactory.INSTANCE).toList();
   }

   default ItemStack resolveForFirstStack(ContextMap var1) {
      return (ItemStack)this.resolve(var1, SlotDisplay.ItemStackContentsFactory.INSTANCE).findFirst().orElse(ItemStack.EMPTY);
   }

   public static record Type<T extends SlotDisplay>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
      public Type(MapCodec<T> var1, StreamCodec<RegistryFriendlyByteBuf, T> var2) {
         super();
         this.codec = var1;
         this.streamCodec = var2;
      }
   }

   public static class ItemStackContentsFactory implements DisplayContentsFactory.ForStacks<ItemStack> {
      public static final ItemStackContentsFactory INSTANCE = new ItemStackContentsFactory();

      public ItemStackContentsFactory() {
         super();
      }

      public ItemStack forStack(ItemStack var1) {
         return var1;
      }

      // $FF: synthetic method
      public Object forStack(final ItemStack var1) {
         return this.forStack(var1);
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

      public <T> Stream<T> resolve(ContextMap var1, DisplayContentsFactory<T> var2) {
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

      public <T> Stream<T> resolve(ContextMap var1, DisplayContentsFactory<T> var2) {
         if (var2 instanceof DisplayContentsFactory.ForStacks var3) {
            FuelValues var4 = (FuelValues)var1.getOptional(SlotDisplayContext.FUEL_VALUES);
            if (var4 != null) {
               Stream var10000 = var4.fuelItems().stream();
               Objects.requireNonNull(var3);
               return var10000.map(var3::forStack);
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

   public static record SmithingTrimDemoSlotDisplay(SlotDisplay base, SlotDisplay material, SlotDisplay pattern) implements SlotDisplay {
      public static final MapCodec<SmithingTrimDemoSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(SlotDisplay.CODEC.fieldOf("base").forGetter(SmithingTrimDemoSlotDisplay::base), SlotDisplay.CODEC.fieldOf("material").forGetter(SmithingTrimDemoSlotDisplay::material), SlotDisplay.CODEC.fieldOf("pattern").forGetter(SmithingTrimDemoSlotDisplay::pattern)).apply(var0, SmithingTrimDemoSlotDisplay::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTrimDemoSlotDisplay> STREAM_CODEC;
      public static final Type<SmithingTrimDemoSlotDisplay> TYPE;

      public SmithingTrimDemoSlotDisplay(SlotDisplay var1, SlotDisplay var2, SlotDisplay var3) {
         super();
         this.base = var1;
         this.material = var2;
         this.pattern = var3;
      }

      public Type<SmithingTrimDemoSlotDisplay> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(ContextMap var1, DisplayContentsFactory<T> var2) {
         if (var2 instanceof DisplayContentsFactory.ForStacks var3) {
            HolderLookup.Provider var4 = (HolderLookup.Provider)var1.getOptional(SlotDisplayContext.REGISTRIES);
            if (var4 != null) {
               RandomSource var5 = RandomSource.create((long)System.identityHashCode(this));
               List var6 = this.base.resolveForStacks(var1);
               if (var6.isEmpty()) {
                  return Stream.empty();
               }

               List var7 = this.material.resolveForStacks(var1);
               if (var7.isEmpty()) {
                  return Stream.empty();
               }

               List var8 = this.pattern.resolveForStacks(var1);
               if (var8.isEmpty()) {
                  return Stream.empty();
               }

               Stream var10000 = Stream.generate(() -> {
                  ItemStack var5x = (ItemStack)Util.getRandom(var6, var5);
                  ItemStack var6x = (ItemStack)Util.getRandom(var7, var5);
                  ItemStack var7x = (ItemStack)Util.getRandom(var8, var5);
                  return SmithingTrimRecipe.applyTrim(var4, var5x, var6x, var7x);
               }).limit(256L).filter((var0) -> !var0.isEmpty()).limit(16L);
               Objects.requireNonNull(var3);
               return var10000.map(var3::forStack);
            }
         }

         return Stream.empty();
      }

      static {
         STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, SmithingTrimDemoSlotDisplay::base, SlotDisplay.STREAM_CODEC, SmithingTrimDemoSlotDisplay::material, SlotDisplay.STREAM_CODEC, SmithingTrimDemoSlotDisplay::pattern, SmithingTrimDemoSlotDisplay::new);
         TYPE = new Type<SmithingTrimDemoSlotDisplay>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static record ItemSlotDisplay(Holder<Item> item) implements SlotDisplay {
      public static final MapCodec<ItemSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Item.CODEC.fieldOf("item").forGetter(ItemSlotDisplay::item)).apply(var0, ItemSlotDisplay::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, ItemSlotDisplay> STREAM_CODEC;
      public static final Type<ItemSlotDisplay> TYPE;

      public ItemSlotDisplay(Item var1) {
         this((Holder)var1.builtInRegistryHolder());
      }

      public ItemSlotDisplay(Holder<Item> var1) {
         super();
         this.item = var1;
      }

      public Type<ItemSlotDisplay> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(ContextMap var1, DisplayContentsFactory<T> var2) {
         if (var2 instanceof DisplayContentsFactory.ForStacks var3) {
            return Stream.of(var3.forStack(this.item));
         } else {
            return Stream.empty();
         }
      }

      public boolean isEnabled(FeatureFlagSet var1) {
         return ((Item)this.item.value()).isEnabled(var1);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderRegistry(Registries.ITEM), ItemSlotDisplay::item, ItemSlotDisplay::new);
         TYPE = new Type<ItemSlotDisplay>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static record ItemStackSlotDisplay(ItemStack stack) implements SlotDisplay {
      public static final MapCodec<ItemStackSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ItemStack.STRICT_CODEC.fieldOf("item").forGetter(ItemStackSlotDisplay::stack)).apply(var0, ItemStackSlotDisplay::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackSlotDisplay> STREAM_CODEC;
      public static final Type<ItemStackSlotDisplay> TYPE;

      public ItemStackSlotDisplay(ItemStack var1) {
         super();
         this.stack = var1;
      }

      public Type<ItemStackSlotDisplay> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(ContextMap var1, DisplayContentsFactory<T> var2) {
         if (var2 instanceof DisplayContentsFactory.ForStacks var3) {
            return Stream.of(var3.forStack(this.stack));
         } else {
            return Stream.empty();
         }
      }

      public boolean equals(Object var1) {
         boolean var10000;
         if (this != var1) {
            label26: {
               if (var1 instanceof ItemStackSlotDisplay) {
                  ItemStackSlotDisplay var2 = (ItemStackSlotDisplay)var1;
                  if (ItemStack.matches(this.stack, var2.stack)) {
                     break label26;
                  }
               }

               var10000 = false;
               return var10000;
            }
         }

         var10000 = true;
         return var10000;
      }

      public boolean isEnabled(FeatureFlagSet var1) {
         return this.stack.getItem().isEnabled(var1);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ItemStack.STREAM_CODEC, ItemStackSlotDisplay::stack, ItemStackSlotDisplay::new);
         TYPE = new Type<ItemStackSlotDisplay>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static record TagSlotDisplay(TagKey<Item> tag) implements SlotDisplay {
      public static final MapCodec<TagSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(TagSlotDisplay::tag)).apply(var0, TagSlotDisplay::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, TagSlotDisplay> STREAM_CODEC;
      public static final Type<TagSlotDisplay> TYPE;

      public TagSlotDisplay(TagKey<Item> var1) {
         super();
         this.tag = var1;
      }

      public Type<TagSlotDisplay> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(ContextMap var1, DisplayContentsFactory<T> var2) {
         if (var2 instanceof DisplayContentsFactory.ForStacks var3) {
            HolderLookup.Provider var4 = (HolderLookup.Provider)var1.getOptional(SlotDisplayContext.REGISTRIES);
            if (var4 != null) {
               return var4.lookupOrThrow(Registries.ITEM).get(this.tag).map((var1x) -> {
                  Stream var10000 = var1x.stream();
                  Objects.requireNonNull(var3);
                  return var10000.map(var3::forStack);
               }).stream().flatMap((var0) -> var0);
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
      public static final MapCodec<Composite> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(SlotDisplay.CODEC.listOf().fieldOf("contents").forGetter(Composite::contents)).apply(var0, Composite::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, Composite> STREAM_CODEC;
      public static final Type<Composite> TYPE;

      public Composite(List<SlotDisplay> var1) {
         super();
         this.contents = var1;
      }

      public Type<Composite> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(ContextMap var1, DisplayContentsFactory<T> var2) {
         return this.contents.stream().flatMap((var2x) -> var2x.resolve(var1, var2));
      }

      public boolean isEnabled(FeatureFlagSet var1) {
         return this.contents.stream().allMatch((var1x) -> var1x.isEnabled(var1));
      }

      static {
         STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), Composite::contents, Composite::new);
         TYPE = new Type<Composite>(MAP_CODEC, STREAM_CODEC);
      }
   }

   public static record WithRemainder(SlotDisplay input, SlotDisplay remainder) implements SlotDisplay {
      public static final MapCodec<WithRemainder> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(SlotDisplay.CODEC.fieldOf("input").forGetter(WithRemainder::input), SlotDisplay.CODEC.fieldOf("remainder").forGetter(WithRemainder::remainder)).apply(var0, WithRemainder::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, WithRemainder> STREAM_CODEC;
      public static final Type<WithRemainder> TYPE;

      public WithRemainder(SlotDisplay var1, SlotDisplay var2) {
         super();
         this.input = var1;
         this.remainder = var2;
      }

      public Type<WithRemainder> type() {
         return TYPE;
      }

      public <T> Stream<T> resolve(ContextMap var1, DisplayContentsFactory<T> var2) {
         if (var2 instanceof DisplayContentsFactory.ForRemainders var3) {
            List var4 = this.remainder.resolve(var1, var2).toList();
            return this.input.resolve(var1, var2).map((var2x) -> var3.addRemainder(var2x, var4));
         } else {
            return this.input.<T>resolve(var1, var2);
         }
      }

      public boolean isEnabled(FeatureFlagSet var1) {
         return this.input.isEnabled(var1) && this.remainder.isEnabled(var1);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, WithRemainder::input, SlotDisplay.STREAM_CODEC, WithRemainder::remainder, WithRemainder::new);
         TYPE = new Type<WithRemainder>(MAP_CODEC, STREAM_CODEC);
      }
   }
}
