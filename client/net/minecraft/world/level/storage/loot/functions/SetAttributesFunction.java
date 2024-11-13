package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetAttributesFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetAttributesFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).and(var0.group(SetAttributesFunction.Modifier.CODEC.listOf().fieldOf("modifiers").forGetter((var0x) -> var0x.modifiers), Codec.BOOL.optionalFieldOf("replace", true).forGetter((var0x) -> var0x.replace))).apply(var0, SetAttributesFunction::new));
   private final List<Modifier> modifiers;
   private final boolean replace;

   SetAttributesFunction(List<LootItemCondition> var1, List<Modifier> var2, boolean var3) {
      super(var1);
      this.modifiers = List.copyOf(var2);
      this.replace = var3;
   }

   public LootItemFunctionType<SetAttributesFunction> getType() {
      return LootItemFunctions.SET_ATTRIBUTES;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return (Set)this.modifiers.stream().flatMap((var0) -> var0.amount.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (this.replace) {
         var1.set(DataComponents.ATTRIBUTE_MODIFIERS, this.updateModifiers(var2, ItemAttributeModifiers.EMPTY));
      } else {
         var1.update(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY, (var2x) -> this.updateModifiers(var2, var2x));
      }

      return var1;
   }

   private ItemAttributeModifiers updateModifiers(LootContext var1, ItemAttributeModifiers var2) {
      RandomSource var3 = var1.getRandom();

      for(Modifier var5 : this.modifiers) {
         EquipmentSlotGroup var6 = (EquipmentSlotGroup)Util.getRandom(var5.slots, var3);
         var2 = var2.withModifierAdded(var5.attribute, new AttributeModifier(var5.id, (double)var5.amount.getFloat(var1), var5.operation), var6);
      }

      return var2;
   }

   public static ModifierBuilder modifier(ResourceLocation var0, Holder<Attribute> var1, AttributeModifier.Operation var2, NumberProvider var3) {
      return new ModifierBuilder(var0, var1, var2, var3);
   }

   public static Builder setAttributes() {
      return new Builder();
   }

   public static class ModifierBuilder {
      private final ResourceLocation id;
      private final Holder<Attribute> attribute;
      private final AttributeModifier.Operation operation;
      private final NumberProvider amount;
      private final Set<EquipmentSlotGroup> slots = EnumSet.noneOf(EquipmentSlotGroup.class);

      public ModifierBuilder(ResourceLocation var1, Holder<Attribute> var2, AttributeModifier.Operation var3, NumberProvider var4) {
         super();
         this.id = var1;
         this.attribute = var2;
         this.operation = var3;
         this.amount = var4;
      }

      public ModifierBuilder forSlot(EquipmentSlotGroup var1) {
         this.slots.add(var1);
         return this;
      }

      public Modifier build() {
         return new Modifier(this.id, this.attribute, this.operation, this.amount, List.copyOf(this.slots));
      }
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final boolean replace;
      private final List<Modifier> modifiers;

      public Builder(boolean var1) {
         super();
         this.modifiers = Lists.newArrayList();
         this.replace = var1;
      }

      public Builder() {
         this(false);
      }

      protected Builder getThis() {
         return this;
      }

      public Builder withModifier(ModifierBuilder var1) {
         this.modifiers.add(var1.build());
         return this;
      }

      public LootItemFunction build() {
         return new SetAttributesFunction(this.getConditions(), this.modifiers, this.replace);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   static record Modifier(ResourceLocation id, Holder<Attribute> attribute, AttributeModifier.Operation operation, NumberProvider amount, List<EquipmentSlotGroup> slots) {
      final ResourceLocation id;
      final Holder<Attribute> attribute;
      final AttributeModifier.Operation operation;
      final NumberProvider amount;
      final List<EquipmentSlotGroup> slots;
      private static final Codec<List<EquipmentSlotGroup>> SLOTS_CODEC;
      public static final Codec<Modifier> CODEC;

      Modifier(ResourceLocation var1, Holder<Attribute> var2, AttributeModifier.Operation var3, NumberProvider var4, List<EquipmentSlotGroup> var5) {
         super();
         this.id = var1;
         this.attribute = var2;
         this.operation = var3;
         this.amount = var4;
         this.slots = var5;
      }

      static {
         SLOTS_CODEC = ExtraCodecs.nonEmptyList(ExtraCodecs.compactListCodec(EquipmentSlotGroup.CODEC));
         CODEC = RecordCodecBuilder.create((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("id").forGetter(Modifier::id), Attribute.CODEC.fieldOf("attribute").forGetter(Modifier::attribute), AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(Modifier::operation), NumberProviders.CODEC.fieldOf("amount").forGetter(Modifier::amount), SLOTS_CODEC.fieldOf("slot").forGetter(Modifier::slots)).apply(var0, Modifier::new));
      }
   }
}
