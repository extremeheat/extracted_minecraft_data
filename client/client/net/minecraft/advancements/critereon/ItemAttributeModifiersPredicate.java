package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record ItemAttributeModifiersPredicate(
   Optional<CollectionPredicate<ItemAttributeModifiers.Entry, ItemAttributeModifiersPredicate.EntryPredicate>> modifiers
) implements SingleComponentItemPredicate<ItemAttributeModifiers> {
   public static final Codec<ItemAttributeModifiersPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               CollectionPredicate.codec(ItemAttributeModifiersPredicate.EntryPredicate.CODEC)
                  .optionalFieldOf("modifiers")
                  .forGetter(ItemAttributeModifiersPredicate::modifiers)
            )
            .apply(var0, ItemAttributeModifiersPredicate::new)
   );

   public ItemAttributeModifiersPredicate(Optional<CollectionPredicate<ItemAttributeModifiers.Entry, ItemAttributeModifiersPredicate.EntryPredicate>> modifiers) {
      super();
      this.modifiers = modifiers;
   }

   @Override
   public DataComponentType<ItemAttributeModifiers> componentType() {
      return DataComponents.ATTRIBUTE_MODIFIERS;
   }

   public boolean matches(ItemStack var1, ItemAttributeModifiers var2) {
      return !this.modifiers.isPresent() || this.modifiers.get().test(var2.modifiers());
   }

   public static record EntryPredicate(
      Optional<HolderSet<Attribute>> attribute,
      Optional<UUID> id,
      Optional<String> name,
      MinMaxBounds.Doubles amount,
      Optional<AttributeModifier.Operation> operation,
      Optional<EquipmentSlotGroup> slot
   ) implements Predicate<ItemAttributeModifiers.Entry> {
      public static final Codec<ItemAttributeModifiersPredicate.EntryPredicate> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  RegistryCodecs.homogeneousList(Registries.ATTRIBUTE)
                     .optionalFieldOf("attribute")
                     .forGetter(ItemAttributeModifiersPredicate.EntryPredicate::attribute),
                  UUIDUtil.LENIENT_CODEC.optionalFieldOf("uuid").forGetter(ItemAttributeModifiersPredicate.EntryPredicate::id),
                  Codec.STRING.optionalFieldOf("name").forGetter(ItemAttributeModifiersPredicate.EntryPredicate::name),
                  MinMaxBounds.Doubles.CODEC
                     .optionalFieldOf("amount", MinMaxBounds.Doubles.ANY)
                     .forGetter(ItemAttributeModifiersPredicate.EntryPredicate::amount),
                  AttributeModifier.Operation.CODEC.optionalFieldOf("operation").forGetter(ItemAttributeModifiersPredicate.EntryPredicate::operation),
                  EquipmentSlotGroup.CODEC.optionalFieldOf("slot").forGetter(ItemAttributeModifiersPredicate.EntryPredicate::slot)
               )
               .apply(var0, ItemAttributeModifiersPredicate.EntryPredicate::new)
      );

      public EntryPredicate(
         Optional<HolderSet<Attribute>> attribute,
         Optional<UUID> id,
         Optional<String> name,
         MinMaxBounds.Doubles amount,
         Optional<AttributeModifier.Operation> operation,
         Optional<EquipmentSlotGroup> slot
      ) {
         super();
         this.attribute = attribute;
         this.id = id;
         this.name = name;
         this.amount = amount;
         this.operation = operation;
         this.slot = slot;
      }

      public boolean test(ItemAttributeModifiers.Entry var1) {
         if (this.attribute.isPresent() && !this.attribute.get().contains(var1.attribute())) {
            return false;
         } else if (this.id.isPresent() && !this.id.get().equals(var1.modifier().id())) {
            return false;
         } else if (this.name.isPresent() && !this.name.get().equals(var1.modifier().name())) {
            return false;
         } else if (!this.amount.matches(var1.modifier().amount())) {
            return false;
         } else {
            return this.operation.isPresent() && this.operation.get() != var1.modifier().operation()
               ? false
               : !this.slot.isPresent() || this.slot.get() == var1.slot();
         }
      }
   }
}
