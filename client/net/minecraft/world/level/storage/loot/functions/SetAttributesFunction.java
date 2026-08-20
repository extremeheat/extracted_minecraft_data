package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetAttributesFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetAttributesFunction> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(SetAttributesFunction.Modifier.CODEC.listOf().fieldOf("modifiers").forGetter((f) -> f.modifiers), Codec.BOOL.optionalFieldOf("replace", true).forGetter((f) -> f.replace))).apply(i, SetAttributesFunction::new));
   private final List<Modifier> modifiers;
   private final boolean replace;

   private SetAttributesFunction(final Optional<Holder<LootItemCondition>> condition, final List<Modifier> modifiers, final boolean replace) {
      super(condition);
      this.modifiers = List.copyOf(modifiers);
      this.replace = replace;
   }

   public MapCodec<SetAttributesFunction> codec() {
      return MAP_CODEC;
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validate(context, "modifiers", this.modifiers);
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      if (this.replace) {
         itemStack.set(DataComponents.ATTRIBUTE_MODIFIERS, this.updateModifiers(context, ItemAttributeModifiers.EMPTY));
      } else {
         itemStack.update(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY, (itemModifiers) -> this.updateModifiers(context, itemModifiers));
      }

      return itemStack;
   }

   private ItemAttributeModifiers updateModifiers(final LootContext context, ItemAttributeModifiers itemModifiers) {
      RandomSource random = context.getRandom();

      for(Modifier modifier : this.modifiers) {
         EquipmentSlotGroup slot = (EquipmentSlotGroup)Util.getRandom(modifier.slots, random);
         itemModifiers = itemModifiers.withModifierAdded(modifier.attribute, new AttributeModifier(modifier.id, (double)((NumberProvider)modifier.amount.value()).getFloat(context), modifier.operation), slot);
      }

      return itemModifiers;
   }

   public static ModifierBuilder modifier(final Identifier id, final Holder<Attribute> attribute, final AttributeModifier.Operation operation, final Holder<NumberProvider> amount) {
      return new ModifierBuilder(id, attribute, operation, amount);
   }

   public static Builder setAttributes() {
      return new Builder();
   }

   public static class ModifierBuilder {
      private final Identifier id;
      private final Holder<Attribute> attribute;
      private final AttributeModifier.Operation operation;
      private final Holder<NumberProvider> amount;
      private final Set<EquipmentSlotGroup> slots = EnumSet.noneOf(EquipmentSlotGroup.class);

      public ModifierBuilder(final Identifier id, final Holder<Attribute> attribute, final AttributeModifier.Operation operation, final Holder<NumberProvider> amount) {
         super();
         this.id = id;
         this.attribute = attribute;
         this.operation = operation;
         this.amount = amount;
      }

      public ModifierBuilder forSlot(final EquipmentSlotGroup slot) {
         this.slots.add(slot);
         return this;
      }

      public Modifier build() {
         return new Modifier(this.id, this.attribute, this.operation, this.amount, List.copyOf(this.slots));
      }
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final boolean replace;
      private final List<Modifier> modifiers;

      public Builder(final boolean replace) {
         super();
         this.modifiers = Lists.newArrayList();
         this.replace = replace;
      }

      public Builder() {
         this(false);
      }

      protected Builder getThis() {
         return this;
      }

      public Builder withModifier(final ModifierBuilder modifier) {
         this.modifiers.add(modifier.build());
         return this;
      }

      public LootItemFunction build() {
         return new SetAttributesFunction(this.getCondition(), this.modifiers, this.replace);
      }
   }

   private static record Modifier(Identifier id, Holder<Attribute> attribute, AttributeModifier.Operation operation, Holder<NumberProvider> amount, List<EquipmentSlotGroup> slots) implements LootContextUser {
      private static final Codec<List<EquipmentSlotGroup>> SLOTS_CODEC;
      public static final Codec<Modifier> CODEC;

      private Modifier {
         super();
      }

      public void validate(final ValidationContext context) {
         LootContextUser.super.validate(context);
         Validatable.validateHolder(context, "amount", this.amount);
      }

      static {
         SLOTS_CODEC = ExtraCodecs.nonEmptyList(ExtraCodecs.compactListCodec(EquipmentSlotGroup.CODEC));
         CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("id").forGetter(Modifier::id), Attribute.CODEC.fieldOf("attribute").forGetter(Modifier::attribute), AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(Modifier::operation), NumberProviders.CODEC.fieldOf("amount").forGetter(Modifier::amount), SLOTS_CODEC.fieldOf("slot").forGetter(Modifier::slots)).apply(i, Modifier::new));
      }
   }
}
