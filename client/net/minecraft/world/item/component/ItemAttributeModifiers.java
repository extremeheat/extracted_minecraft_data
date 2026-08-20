package net.minecraft.world.item.component;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.function.TriConsumer;
import org.jspecify.annotations.Nullable;

public record ItemAttributeModifiers(List<Entry> modifiers) {
   public static final ItemAttributeModifiers EMPTY = new ItemAttributeModifiers(List.of());
   public static final Codec<ItemAttributeModifiers> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttributeModifiers> STREAM_CODEC;
   public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT;

   public ItemAttributeModifiers {
      super();
   }

   public static Builder builder() {
      return new Builder();
   }

   public ItemAttributeModifiers withModifierAdded(final Holder<Attribute> attribute, final AttributeModifier modifier, final EquipmentSlotGroup slot) {
      ImmutableList.Builder<Entry> newModifiers = ImmutableList.builderWithExpectedSize(this.modifiers.size() + 1);

      for(Entry entry : this.modifiers) {
         if (!entry.matches(attribute, modifier.id())) {
            newModifiers.add(entry);
         }
      }

      newModifiers.add(new Entry(attribute, modifier, slot));
      return new ItemAttributeModifiers(newModifiers.build());
   }

   public void forEach(final EquipmentSlotGroup slot, final TriConsumer<Holder<Attribute>, AttributeModifier, Display> consumer) {
      for(Entry entry : this.modifiers) {
         if (entry.slot.equals(slot)) {
            consumer.accept(entry.attribute, entry.modifier, entry.display);
         }
      }

   }

   public void forEach(final EquipmentSlotGroup slot, final BiConsumer<Holder<Attribute>, AttributeModifier> consumer) {
      for(Entry entry : this.modifiers) {
         if (entry.slot.equals(slot)) {
            consumer.accept(entry.attribute, entry.modifier);
         }
      }

   }

   public void forEach(final EquipmentSlot slot, final BiConsumer<Holder<Attribute>, AttributeModifier> consumer) {
      for(Entry entry : this.modifiers) {
         if (entry.slot.test(slot)) {
            consumer.accept(entry.attribute, entry.modifier);
         }
      }

   }

   public double compute(final Holder<Attribute> attribute, final double baseValue, final EquipmentSlot slot) {
      double value = baseValue;

      for(Entry entry : this.modifiers) {
         if (entry.slot.test(slot) && entry.attribute == attribute) {
            double amount = entry.modifier.amount();
            double var10001;
            switch (entry.modifier.operation()) {
               case ADD_VALUE -> var10001 = amount;
               case ADD_MULTIPLIED_BASE -> var10001 = amount * baseValue;
               case ADD_MULTIPLIED_TOTAL -> var10001 = amount * value;
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            value += var10001;
         }
      }

      return value;
   }

   static {
      CODEC = ItemAttributeModifiers.Entry.CODEC.listOf().xmap(ItemAttributeModifiers::new, ItemAttributeModifiers::modifiers);
      STREAM_CODEC = StreamCodec.composite(ItemAttributeModifiers.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ItemAttributeModifiers::modifiers, ItemAttributeModifiers::new);
      ATTRIBUTE_MODIFIER_FORMAT = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ROOT));
   }

   public interface Display {
      Codec<Display> CODEC = ItemAttributeModifiers.Display.Type.CODEC.dispatch(Display::type, (type) -> type.codec);
      StreamCodec<RegistryFriendlyByteBuf, Display> STREAM_CODEC = ItemAttributeModifiers.Display.Type.STREAM_CODEC.cast().dispatch(Display::type, Type::streamCodec);

      static Display attributeModifiers() {
         return ItemAttributeModifiers.Display.Default.INSTANCE;
      }

      static Display hidden() {
         return ItemAttributeModifiers.Display.Hidden.INSTANCE;
      }

      static Display override(final Component component) {
         return new OverrideText(component);
      }

      Type type();

      void apply(Consumer<Component> consumer, @Nullable Player player, Holder<Attribute> attribute, AttributeModifier modifier);

      public static enum Type implements StringRepresentable {
         DEFAULT("default", 0, ItemAttributeModifiers.Display.Default.CODEC, ItemAttributeModifiers.Display.Default.STREAM_CODEC),
         HIDDEN("hidden", 1, ItemAttributeModifiers.Display.Hidden.CODEC, ItemAttributeModifiers.Display.Hidden.STREAM_CODEC),
         OVERRIDE("override", 2, ItemAttributeModifiers.Display.OverrideText.CODEC, ItemAttributeModifiers.Display.OverrideText.STREAM_CODEC);

         private static final Codec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);
         private static final IntFunction<Type> BY_ID = ByIdMap.<Type>continuous(Type::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
         private static final StreamCodec<ByteBuf, Type> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Type::id);
         private final String name;
         private final int id;
         private final MapCodec<? extends Display> codec;
         private final StreamCodec<RegistryFriendlyByteBuf, ? extends Display> streamCodec;

         private Type(final String name, final int id, final MapCodec<? extends Display> codec, final StreamCodec<RegistryFriendlyByteBuf, ? extends Display> streamCodec) {
            this.name = name;
            this.id = id;
            this.codec = codec;
            this.streamCodec = streamCodec;
         }

         public String getSerializedName() {
            return this.name;
         }

         private int id() {
            return this.id;
         }

         private StreamCodec<RegistryFriendlyByteBuf, ? extends Display> streamCodec() {
            return this.streamCodec;
         }

         // $FF: synthetic method
         private static Type[] $values() {
            return new Type[]{DEFAULT, HIDDEN, OVERRIDE};
         }
      }

      public static record Default() implements Display {
         private static final Default INSTANCE = new Default();
         private static final MapCodec<Default> CODEC;
         private static final StreamCodec<RegistryFriendlyByteBuf, Default> STREAM_CODEC;

         public Default() {
            super();
         }

         public Type type() {
            return ItemAttributeModifiers.Display.Type.DEFAULT;
         }

         public void apply(final Consumer<Component> consumer, final @Nullable Player player, final Holder<Attribute> attribute, final AttributeModifier modifier) {
            double amount = modifier.amount();
            boolean displayWithBase = false;
            if (player != null) {
               if (modifier.is(Item.BASE_ATTACK_DAMAGE_ID)) {
                  amount += player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                  displayWithBase = true;
               } else if (modifier.is(Item.BASE_ATTACK_SPEED_ID)) {
                  amount += player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                  displayWithBase = true;
               }
            }

            double displayAmount;
            if (modifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && modifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
               if (attribute.is(Attributes.KNOCKBACK_RESISTANCE)) {
                  displayAmount = amount * 10.0;
               } else {
                  displayAmount = amount;
               }
            } else {
               displayAmount = amount * 100.0;
            }

            if (displayWithBase) {
               consumer.accept(CommonComponents.space().append((Component)Component.translatable("attribute.modifier.equals." + modifier.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount), Component.translatable(((Attribute)attribute.value()).getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
            } else if (amount > 0.0) {
               consumer.accept(Component.translatable("attribute.modifier.plus." + modifier.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(displayAmount), Component.translatable(((Attribute)attribute.value()).getDescriptionId())).withStyle(((Attribute)attribute.value()).getStyle(true)));
            } else if (amount < 0.0) {
               consumer.accept(Component.translatable("attribute.modifier.take." + modifier.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(-displayAmount), Component.translatable(((Attribute)attribute.value()).getDescriptionId())).withStyle(((Attribute)attribute.value()).getStyle(false)));
            }

         }

         static {
            CODEC = MapCodec.unit(INSTANCE);
            STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, Default>unit(INSTANCE);
         }
      }

      public static record Hidden() implements Display {
         private static final Hidden INSTANCE = new Hidden();
         private static final MapCodec<Hidden> CODEC;
         private static final StreamCodec<RegistryFriendlyByteBuf, Hidden> STREAM_CODEC;

         public Hidden() {
            super();
         }

         public Type type() {
            return ItemAttributeModifiers.Display.Type.HIDDEN;
         }

         public void apply(final Consumer<Component> consumer, final @Nullable Player player, final Holder<Attribute> attribute, final AttributeModifier modifier) {
         }

         static {
            CODEC = MapCodec.unit(INSTANCE);
            STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, Hidden>unit(INSTANCE);
         }
      }

      public static record OverrideText(Component component) implements Display {
         private static final MapCodec<OverrideText> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ComponentSerialization.CODEC.fieldOf("value").forGetter(OverrideText::component)).apply(i, OverrideText::new));
         private static final StreamCodec<RegistryFriendlyByteBuf, OverrideText> STREAM_CODEC;

         public OverrideText {
            super();
         }

         public Type type() {
            return ItemAttributeModifiers.Display.Type.OVERRIDE;
         }

         public void apply(final Consumer<Component> consumer, final @Nullable Player player, final Holder<Attribute> attribute, final AttributeModifier modifier) {
            consumer.accept(this.component);
         }

         static {
            STREAM_CODEC = StreamCodec.composite(ComponentSerialization.STREAM_CODEC, OverrideText::component, OverrideText::new);
         }
      }
   }

   public static record Entry(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot, Display display) {
      public static final Codec<Entry> CODEC = RecordCodecBuilder.create((i) -> i.group(Attribute.CODEC.fieldOf("type").forGetter(Entry::attribute), AttributeModifier.MAP_CODEC.forGetter(Entry::modifier), EquipmentSlotGroup.CODEC.optionalFieldOf("slot", EquipmentSlotGroup.ANY).forGetter(Entry::slot), ItemAttributeModifiers.Display.CODEC.optionalFieldOf("display", ItemAttributeModifiers.Display.Default.INSTANCE).forGetter(Entry::display)).apply(i, Entry::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;

      public Entry(final Holder<Attribute> attribute, final AttributeModifier modifier, final EquipmentSlotGroup slot) {
         this(attribute, modifier, slot, ItemAttributeModifiers.Display.attributeModifiers());
      }

      public Entry {
         super();
      }

      public boolean matches(final Holder<Attribute> attribute, final Identifier id) {
         return attribute.equals(this.attribute) && this.modifier.is(id);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(Attribute.STREAM_CODEC, Entry::attribute, AttributeModifier.STREAM_CODEC, Entry::modifier, EquipmentSlotGroup.STREAM_CODEC, Entry::slot, ItemAttributeModifiers.Display.STREAM_CODEC, Entry::display, Entry::new);
      }
   }

   public static class Builder {
      private final ImmutableList.Builder<Entry> entries = ImmutableList.builder();

      private Builder() {
         super();
      }

      public Builder add(final Holder<Attribute> attribute, final AttributeModifier modifier, final EquipmentSlotGroup slot) {
         this.entries.add(new Entry(attribute, modifier, slot));
         return this;
      }

      public Builder add(final Holder<Attribute> attribute, final AttributeModifier modifier, final EquipmentSlotGroup slot, final Display display) {
         this.entries.add(new Entry(attribute, modifier, slot, display));
         return this;
      }

      public ItemAttributeModifiers build() {
         return new ItemAttributeModifiers(this.entries.build());
      }
   }
}
