package net.minecraft.world.item.component;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record ItemAttributeModifiers(List<Entry> modifiers, boolean showInTooltip) {
   public static final ItemAttributeModifiers EMPTY = new ItemAttributeModifiers(List.of(), true);
   private static final Codec<ItemAttributeModifiers> FULL_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ItemAttributeModifiers.Entry.CODEC.listOf().fieldOf("modifiers").forGetter(ItemAttributeModifiers::modifiers), Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(ItemAttributeModifiers::showInTooltip)).apply(var0, ItemAttributeModifiers::new);
   });
   public static final Codec<ItemAttributeModifiers> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttributeModifiers> STREAM_CODEC;
   public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT;

   public ItemAttributeModifiers(List<Entry> var1, boolean var2) {
      super();
      this.modifiers = var1;
      this.showInTooltip = var2;
   }

   public ItemAttributeModifiers withTooltip(boolean var1) {
      return new ItemAttributeModifiers(this.modifiers, var1);
   }

   public static Builder builder() {
      return new Builder();
   }

   public ItemAttributeModifiers withModifierAdded(Holder<Attribute> var1, AttributeModifier var2, EquipmentSlotGroup var3) {
      ImmutableList.Builder var4 = ImmutableList.builderWithExpectedSize(this.modifiers.size() + 1);
      Iterator var5 = this.modifiers.iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         if (!var6.matches(var1, var2.id())) {
            var4.add(var6);
         }
      }

      var4.add(new Entry(var1, var2, var3));
      return new ItemAttributeModifiers(var4.build(), this.showInTooltip);
   }

   public void forEach(EquipmentSlotGroup var1, BiConsumer<Holder<Attribute>, AttributeModifier> var2) {
      Iterator var3 = this.modifiers.iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (var4.slot.equals(var1)) {
            var2.accept(var4.attribute, var4.modifier);
         }
      }

   }

   public void forEach(EquipmentSlot var1, BiConsumer<Holder<Attribute>, AttributeModifier> var2) {
      Iterator var3 = this.modifiers.iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (var4.slot.test(var1)) {
            var2.accept(var4.attribute, var4.modifier);
         }
      }

   }

   public double compute(double var1, EquipmentSlot var3) {
      double var4 = var1;
      Iterator var6 = this.modifiers.iterator();

      while(var6.hasNext()) {
         Entry var7 = (Entry)var6.next();
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

   public List<Entry> modifiers() {
      return this.modifiers;
   }

   public boolean showInTooltip() {
      return this.showInTooltip;
   }

   static {
      CODEC = Codec.withAlternative(FULL_CODEC, ItemAttributeModifiers.Entry.CODEC.listOf(), (var0) -> {
         return new ItemAttributeModifiers(var0, true);
      });
      STREAM_CODEC = StreamCodec.composite(ItemAttributeModifiers.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ItemAttributeModifiers::modifiers, ByteBufCodecs.BOOL, ItemAttributeModifiers::showInTooltip, ItemAttributeModifiers::new);
      ATTRIBUTE_MODIFIER_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("#.##"), (var0) -> {
         var0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
      });
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

      public ItemAttributeModifiers build() {
         return new ItemAttributeModifiers(this.entries.build(), true);
      }
   }

   public static record Entry(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot) {
      final Holder<Attribute> attribute;
      final AttributeModifier modifier;
      final EquipmentSlotGroup slot;
      public static final Codec<Entry> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Attribute.CODEC.fieldOf("type").forGetter(Entry::attribute), AttributeModifier.MAP_CODEC.forGetter(Entry::modifier), EquipmentSlotGroup.CODEC.optionalFieldOf("slot", EquipmentSlotGroup.ANY).forGetter(Entry::slot)).apply(var0, Entry::new);
      });
      public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;

      public Entry(Holder<Attribute> var1, AttributeModifier var2, EquipmentSlotGroup var3) {
         super();
         this.attribute = var1;
         this.modifier = var2;
         this.slot = var3;
      }

      public boolean matches(Holder<Attribute> var1, ResourceLocation var2) {
         return var1.equals(this.attribute) && this.modifier.is(var2);
      }

      public Holder<Attribute> attribute() {
         return this.attribute;
      }

      public AttributeModifier modifier() {
         return this.modifier;
      }

      public EquipmentSlotGroup slot() {
         return this.slot;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(Attribute.STREAM_CODEC, Entry::attribute, AttributeModifier.STREAM_CODEC, Entry::modifier, EquipmentSlotGroup.STREAM_CODEC, Entry::slot, Entry::new);
      }
   }
}
