package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.advancements.criterion.CollectionPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.SingleComponentItemPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record AttributeModifiersPredicate(Optional<CollectionPredicate<ItemAttributeModifiers.Entry, EntryPredicate>> modifiers) implements SingleComponentItemPredicate<ItemAttributeModifiers> {
   public static final Codec<AttributeModifiersPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(CollectionPredicate.codec(AttributeModifiersPredicate.EntryPredicate.CODEC).optionalFieldOf("modifiers").forGetter(AttributeModifiersPredicate::modifiers)).apply(i, AttributeModifiersPredicate::new));

   public AttributeModifiersPredicate {
      super();
   }

   public DataComponentType<ItemAttributeModifiers> componentType() {
      return DataComponents.ATTRIBUTE_MODIFIERS;
   }

   public boolean matches(final ItemAttributeModifiers value) {
      return !this.modifiers.isPresent() || ((CollectionPredicate)this.modifiers.get()).test(value.modifiers());
   }

   public static record EntryPredicate(Optional<HolderSet<Attribute>> attribute, Optional<Identifier> id, MinMaxBounds.Doubles amount, Optional<AttributeModifier.Operation> operation, Optional<EquipmentSlotGroup> slot) implements Predicate<ItemAttributeModifiers.Entry> {
      public static final Codec<EntryPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.homogeneousList(Registries.ATTRIBUTE).optionalFieldOf("attribute").forGetter(EntryPredicate::attribute), Identifier.CODEC.optionalFieldOf("id").forGetter(EntryPredicate::id), MinMaxBounds.Doubles.CODEC.optionalFieldOf("amount", MinMaxBounds.Doubles.ANY).forGetter(EntryPredicate::amount), AttributeModifier.Operation.CODEC.optionalFieldOf("operation").forGetter(EntryPredicate::operation), EquipmentSlotGroup.CODEC.optionalFieldOf("slot").forGetter(EntryPredicate::slot)).apply(i, EntryPredicate::new));

      public EntryPredicate {
         super();
      }

      public boolean test(final ItemAttributeModifiers.Entry value) {
         if (this.attribute.isPresent() && !((HolderSet)this.attribute.get()).contains(value.attribute())) {
            return false;
         } else if (this.id.isPresent() && !((Identifier)this.id.get()).equals(value.modifier().id())) {
            return false;
         } else if (!this.amount.matches(value.modifier().amount())) {
            return false;
         } else if (this.operation.isPresent() && this.operation.get() != value.modifier().operation()) {
            return false;
         } else {
            return !this.slot.isPresent() || this.slot.get() == value.slot();
         }
      }
   }
}
