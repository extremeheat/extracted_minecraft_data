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
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
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

public record ItemAttributeModifiers(List<Entry> modifiers) {
   public static final ItemAttributeModifiers EMPTY = new ItemAttributeModifiers(List.of());
   public static final Codec<ItemAttributeModifiers> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttributeModifiers> STREAM_CODEC;
   public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT;

   public ItemAttributeModifiers(List<Entry> var1) {
      super();
      this.modifiers = var1;
   }

   public static Builder builder() {
      return new Builder();
   }

   public ItemAttributeModifiers withModifierAdded(Holder<Attribute> var1, AttributeModifier var2, EquipmentSlotGroup var3) {
      ImmutableList.Builder var4 = ImmutableList.builderWithExpectedSize(this.modifiers.size() + 1);

      for(Entry var6 : this.modifiers) {
         if (!var6.matches(var1, var2.id())) {
            var4.add(var6);
         }
      }

      var4.add(new Entry(var1, var2, var3));
      return new ItemAttributeModifiers(var4.build());
   }

   public void forEach(EquipmentSlotGroup var1, TriConsumer<Holder<Attribute>, AttributeModifier, Display> var2) {
      for(Entry var4 : this.modifiers) {
         if (var4.slot.equals(var1)) {
            var2.accept(var4.attribute, var4.modifier, var4.display);
         }
      }

   }

   public void forEach(EquipmentSlotGroup var1, BiConsumer<Holder<Attribute>, AttributeModifier> var2) {
      for(Entry var4 : this.modifiers) {
         if (var4.slot.equals(var1)) {
            var2.accept(var4.attribute, var4.modifier);
         }
      }

   }

   public void forEach(EquipmentSlot var1, BiConsumer<Holder<Attribute>, AttributeModifier> var2) {
      for(Entry var4 : this.modifiers) {
         if (var4.slot.test(var1)) {
            var2.accept(var4.attribute, var4.modifier);
         }
      }

   }

   public double compute(double var1, EquipmentSlot var3) {
      double var4 = var1;

      for(Entry var7 : this.modifiers) {
         if (var7.slot.test(var3)) {
            double var8 = var7.modifier.amount();
            double var10001;
            switch (var7.modifier.operation()) {
               case ADD_VALUE -> var10001 = var8;
               case ADD_MULTIPLIED_BASE -> var10001 = var8 * var1;
               case ADD_MULTIPLIED_TOTAL -> var10001 = var8 * var4;
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            var4 += var10001;
         }
      }

      return var4;
   }

   static {
      CODEC = ItemAttributeModifiers.Entry.CODEC.listOf().xmap(ItemAttributeModifiers::new, ItemAttributeModifiers::modifiers);
      STREAM_CODEC = StreamCodec.composite(ItemAttributeModifiers.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ItemAttributeModifiers::modifiers, ItemAttributeModifiers::new);
      ATTRIBUTE_MODIFIER_FORMAT = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ROOT));
   }

   public interface Display {
      Codec<Display> CODEC = ItemAttributeModifiers.Display.Type.CODEC.dispatch("type", Display::type, (var0) -> var0.codec);
      StreamCodec<RegistryFriendlyByteBuf, Display> STREAM_CODEC = ItemAttributeModifiers.Display.Type.STREAM_CODEC.cast().dispatch(Display::type, Type::streamCodec);

      static Display attributeModifiers() {
         return ItemAttributeModifiers.Display.Default.INSTANCE;
      }

      static Display hidden() {
         return ItemAttributeModifiers.Display.Hidden.INSTANCE;
      }

      static Display override(Component var0) {
         return new OverrideText(var0);
      }

      Type type();

      void apply(Consumer<Component> var1, @Nullable Player var2, Holder<Attribute> var3, AttributeModifier var4);

      public static enum Type implements StringRepresentable {
         DEFAULT("default", 0, ItemAttributeModifiers.Display.Default.CODEC, ItemAttributeModifiers.Display.Default.STREAM_CODEC),
         HIDDEN("hidden", 1, ItemAttributeModifiers.Display.Hidden.CODEC, ItemAttributeModifiers.Display.Hidden.STREAM_CODEC),
         OVERRIDE("override", 2, ItemAttributeModifiers.Display.OverrideText.CODEC, ItemAttributeModifiers.Display.OverrideText.STREAM_CODEC);

         static final Codec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);
         private static final IntFunction<Type> BY_ID = ByIdMap.<Type>continuous(Type::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
         static final StreamCodec<ByteBuf, Type> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Type::id);
         private final String name;
         private final int id;
         final MapCodec<? extends Display> codec;
         private final StreamCodec<RegistryFriendlyByteBuf, ? extends Display> streamCodec;

         private Type(final String var3, final int var4, final MapCodec<? extends Display> var5, final StreamCodec<RegistryFriendlyByteBuf, ? extends Display> var6) {
            this.name = var3;
            this.id = var4;
            this.codec = var5;
            this.streamCodec = var6;
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
         static final Default INSTANCE = new Default();
         static final MapCodec<Default> CODEC;
         static final StreamCodec<RegistryFriendlyByteBuf, Default> STREAM_CODEC;

         public Default() {
            super();
         }

         public Type type() {
            return ItemAttributeModifiers.Display.Type.DEFAULT;
         }

         public void apply(Consumer<Component> var1, @Nullable Player var2, Holder<Attribute> var3, AttributeModifier var4) {
            double var5 = var4.amount();
            boolean var7 = false;
            if (var2 != null) {
               if (var4.is(Item.BASE_ATTACK_DAMAGE_ID)) {
                  var5 += var2.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                  var7 = true;
               } else if (var4.is(Item.BASE_ATTACK_SPEED_ID)) {
                  var5 += var2.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                  var7 = true;
               }
            }

            double var8;
            if (var4.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && var4.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
               if (var3.is(Attributes.KNOCKBACK_RESISTANCE)) {
                  var8 = var5 * 10.0;
               } else {
                  var8 = var5;
               }
            } else {
               var8 = var5 * 100.0;
            }

            if (var7) {
               var1.accept(CommonComponents.space().append((Component)Component.translatable("attribute.modifier.equals." + var4.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(var8), Component.translatable(((Attribute)var3.value()).getDescriptionId()))).withStyle(ChatFormatting.DARK_GREEN));
            } else if (var5 > 0.0) {
               var1.accept(Component.translatable("attribute.modifier.plus." + var4.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(var8), Component.translatable(((Attribute)var3.value()).getDescriptionId())).withStyle(((Attribute)var3.value()).getStyle(true)));
            } else if (var5 < 0.0) {
               var1.accept(Component.translatable("attribute.modifier.take." + var4.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(-var8), Component.translatable(((Attribute)var3.value()).getDescriptionId())).withStyle(((Attribute)var3.value()).getStyle(false)));
            }

         }

         static {
            CODEC = MapCodec.unit(INSTANCE);
            STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, Default>unit(INSTANCE);
         }
      }

      public static record Hidden() implements Display {
         static final Hidden INSTANCE = new Hidden();
         static final MapCodec<Hidden> CODEC;
         static final StreamCodec<RegistryFriendlyByteBuf, Hidden> STREAM_CODEC;

         public Hidden() {
            super();
         }

         public Type type() {
            return ItemAttributeModifiers.Display.Type.HIDDEN;
         }

         public void apply(Consumer<Component> var1, @Nullable Player var2, Holder<Attribute> var3, AttributeModifier var4) {
         }

         static {
            CODEC = MapCodec.unit(INSTANCE);
            STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, Hidden>unit(INSTANCE);
         }
      }

      public static record OverrideText(Component component) implements Display {
         static final MapCodec<OverrideText> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ComponentSerialization.CODEC.fieldOf("value").forGetter(OverrideText::component)).apply(var0, OverrideText::new));
         static final StreamCodec<RegistryFriendlyByteBuf, OverrideText> STREAM_CODEC;

         public OverrideText(Component var1) {
            super();
            this.component = var1;
         }

         public Type type() {
            return ItemAttributeModifiers.Display.Type.OVERRIDE;
         }

         public void apply(Consumer<Component> var1, @Nullable Player var2, Holder<Attribute> var3, AttributeModifier var4) {
            var1.accept(this.component);
         }

         static {
            STREAM_CODEC = StreamCodec.composite(ComponentSerialization.STREAM_CODEC, OverrideText::component, OverrideText::new);
         }
      }
   }

   public static record Entry(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot, Display display) {
      final Holder<Attribute> attribute;
      final AttributeModifier modifier;
      final EquipmentSlotGroup slot;
      final Display display;
      public static final Codec<Entry> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Attribute.CODEC.fieldOf("type").forGetter(Entry::attribute), AttributeModifier.MAP_CODEC.forGetter(Entry::modifier), EquipmentSlotGroup.CODEC.optionalFieldOf("slot", EquipmentSlotGroup.ANY).forGetter(Entry::slot), ItemAttributeModifiers.Display.CODEC.optionalFieldOf("display", ItemAttributeModifiers.Display.Default.INSTANCE).forGetter(Entry::display)).apply(var0, Entry::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;

      public Entry(Holder<Attribute> var1, AttributeModifier var2, EquipmentSlotGroup var3) {
         this(var1, var2, var3, ItemAttributeModifiers.Display.attributeModifiers());
      }

      public Entry(Holder<Attribute> var1, AttributeModifier var2, EquipmentSlotGroup var3, Display var4) {
         super();
         this.attribute = var1;
         this.modifier = var2;
         this.slot = var3;
         this.display = var4;
      }

      public boolean matches(Holder<Attribute> var1, ResourceLocation var2) {
         return var1.equals(this.attribute) && this.modifier.is(var2);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(Attribute.STREAM_CODEC, Entry::attribute, AttributeModifier.STREAM_CODEC, Entry::modifier, EquipmentSlotGroup.STREAM_CODEC, Entry::slot, ItemAttributeModifiers.Display.STREAM_CODEC, Entry::display, Entry::new);
      }
   }

   public static class Builder {
      private final ImmutableList.Builder<Entry> entries = ImmutableList.builder();

      Builder() {
         super();
      }

      public Builder add(Holder<Attribute> var1, AttributeModifier var2, EquipmentSlotGroup var3) {
         this.entries.add(new Entry(var1, var2, var3));
         return this;
      }

      public Builder add(Holder<Attribute> var1, AttributeModifier var2, EquipmentSlotGroup var3, Display var4) {
         this.entries.add(new Entry(var1, var2, var3, var4));
         return this;
      }

      public ItemAttributeModifiers build() {
         return new ItemAttributeModifiers(this.entries.build());
      }
   }
}
