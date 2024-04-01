package net.minecraft.world.item.component;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record ItemAttributeModifiers(List<ItemAttributeModifiers.Entry> e, boolean f) {
   private final List<ItemAttributeModifiers.Entry> modifiers;
   private final boolean showInTooltip;
   public static final ItemAttributeModifiers EMPTY = new ItemAttributeModifiers(List.of(), true);
   private static final Codec<ItemAttributeModifiers> FULL_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ItemAttributeModifiers.Entry.CODEC.listOf().fieldOf("modifiers").forGetter(ItemAttributeModifiers::modifiers),
               ExtraCodecs.strictOptionalField(Codec.BOOL, "show_in_tooltip", true).forGetter(ItemAttributeModifiers::showInTooltip)
            )
            .apply(var0, ItemAttributeModifiers::new)
   );
   public static final Codec<ItemAttributeModifiers> CODEC = ExtraCodecs.withAlternative(
      FULL_CODEC, ItemAttributeModifiers.Entry.CODEC.listOf(), var0 -> new ItemAttributeModifiers(var0, true)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttributeModifiers> STREAM_CODEC = StreamCodec.composite(
      ItemAttributeModifiers.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()),
      ItemAttributeModifiers::modifiers,
      ByteBufCodecs.BOOL,
      ItemAttributeModifiers::showInTooltip,
      ItemAttributeModifiers::new
   );
   public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT = Util.make(
      new DecimalFormat("#.##"), var0 -> var0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT))
   );

   public ItemAttributeModifiers(List<ItemAttributeModifiers.Entry> var1, boolean var2) {
      super();
      this.modifiers = var1;
      this.showInTooltip = var2;
   }

   public ItemAttributeModifiers withTooltip(boolean var1) {
      return new ItemAttributeModifiers(this.modifiers, var1);
   }

   public static ItemAttributeModifiers.Builder builder() {
      return new ItemAttributeModifiers.Builder();
   }

   public ItemAttributeModifiers withModifierAdded(Holder<Attribute> var1, AttributeModifier var2, EquipmentSlotGroup var3) {
      return new ItemAttributeModifiers(Util.copyAndAdd(this.modifiers, new ItemAttributeModifiers.Entry(var1, var2, var3)), this.showInTooltip);
   }

   public void forEach(EquipmentSlot var1, BiConsumer<Holder<Attribute>, AttributeModifier> var2) {
      for(ItemAttributeModifiers.Entry var4 : this.modifiers) {
         if (var4.slot.test(var1)) {
            var2.accept(var4.attribute, var4.modifier);
         }
      }
   }

   public double compute(double var1, EquipmentSlot var3) {
      double var4 = var1;

      for(ItemAttributeModifiers.Entry var7 : this.modifiers) {
         if (var7.slot.test(var3)) {
            double var8 = var7.modifier.amount();

            var4 += switch(var7.modifier.operation()) {
               case ADD_VALUE -> var8;
               case ADD_MULTIPLIED_BASE -> var8 * var1;
               case ADD_MULTIPLIED_TOTAL -> var8 * var4;
            };
         }
      }

      return var4;
   }

   public static class Builder {
      private final com.google.common.collect.ImmutableList.Builder<ItemAttributeModifiers.Entry> entries = ImmutableList.builder();

      Builder() {
         super();
      }

      public ItemAttributeModifiers.Builder add(Holder<Attribute> var1, AttributeModifier var2, EquipmentSlotGroup var3) {
         this.entries.add(new ItemAttributeModifiers.Entry(var1, var2, var3));
         return this;
      }

      public ItemAttributeModifiers build() {
         return new ItemAttributeModifiers(this.entries.build(), true);
      }
   }

   public static record Entry(Holder<Attribute> c, AttributeModifier d, EquipmentSlotGroup e) {
      final Holder<Attribute> attribute;
      final AttributeModifier modifier;
      final EquipmentSlotGroup slot;
      public static final Codec<ItemAttributeModifiers.Entry> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("type").forGetter(ItemAttributeModifiers.Entry::attribute),
                  AttributeModifier.MAP_CODEC.forGetter(ItemAttributeModifiers.Entry::modifier),
                  ExtraCodecs.strictOptionalField(EquipmentSlotGroup.CODEC, "slot", EquipmentSlotGroup.ANY).forGetter(ItemAttributeModifiers.Entry::slot)
               )
               .apply(var0, ItemAttributeModifiers.Entry::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttributeModifiers.Entry> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE),
         ItemAttributeModifiers.Entry::attribute,
         AttributeModifier.STREAM_CODEC,
         ItemAttributeModifiers.Entry::modifier,
         EquipmentSlotGroup.STREAM_CODEC,
         ItemAttributeModifiers.Entry::slot,
         ItemAttributeModifiers.Entry::new
      );

      public Entry(Holder<Attribute> var1, AttributeModifier var2, EquipmentSlotGroup var3) {
         super();
         this.attribute = var1;
         this.modifier = var2;
         this.slot = var3;
      }
   }
}
