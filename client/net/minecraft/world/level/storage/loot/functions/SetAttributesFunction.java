package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetAttributesFunction extends LootItemConditionalFunction {
   public static final Codec<SetAttributesFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(ExtraCodecs.nonEmptyList(SetAttributesFunction.Modifier.CODEC.listOf()).fieldOf("modifiers").forGetter(var0x -> var0x.modifiers))
            .apply(var0, SetAttributesFunction::new)
   );
   private final List<SetAttributesFunction.Modifier> modifiers;

   SetAttributesFunction(List<LootItemCondition> var1, List<SetAttributesFunction.Modifier> var2) {
      super(var1);
      this.modifiers = List.copyOf(var2);
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_ATTRIBUTES;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.modifiers.stream().flatMap(var0 -> var0.amount.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      RandomSource var3 = var2.getRandom();

      for(SetAttributesFunction.Modifier var5 : this.modifiers) {
         UUID var6 = var5.id.orElseGet(UUID::randomUUID);
         EquipmentSlot var7 = Util.getRandom(var5.slots, var3);
         var1.addAttributeModifier(var5.attribute.value(), new AttributeModifier(var6, var5.name, (double)var5.amount.getFloat(var2), var5.operation), var7);
      }

      return var1;
   }

   public static SetAttributesFunction.ModifierBuilder modifier(String var0, Holder<Attribute> var1, AttributeModifier.Operation var2, NumberProvider var3) {
      return new SetAttributesFunction.ModifierBuilder(var0, var1, var2, var3);
   }

   public static SetAttributesFunction.Builder setAttributes() {
      return new SetAttributesFunction.Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetAttributesFunction.Builder> {
      private final List<SetAttributesFunction.Modifier> modifiers = Lists.newArrayList();

      public Builder() {
         super();
      }

      protected SetAttributesFunction.Builder getThis() {
         return this;
      }

      public SetAttributesFunction.Builder withModifier(SetAttributesFunction.ModifierBuilder var1) {
         this.modifiers.add(var1.build());
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new SetAttributesFunction(this.getConditions(), this.modifiers);
      }
   }

   static record Modifier(String b, Holder<Attribute> c, AttributeModifier.Operation d, NumberProvider e, List<EquipmentSlot> f, Optional<UUID> g) {
      final String name;
      final Holder<Attribute> attribute;
      final AttributeModifier.Operation operation;
      final NumberProvider amount;
      final List<EquipmentSlot> slots;
      final Optional<UUID> id;
      private static final Codec<List<EquipmentSlot>> SLOTS_CODEC = ExtraCodecs.nonEmptyList(
         Codec.either(EquipmentSlot.CODEC, EquipmentSlot.CODEC.listOf())
            .xmap(
               var0 -> (List)var0.map(List::of, Function.identity()), var0 -> var0.size() == 1 ? Either.left((EquipmentSlot)var0.get(0)) : Either.right(var0)
            )
      );
      public static final Codec<SetAttributesFunction.Modifier> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.STRING.fieldOf("name").forGetter(SetAttributesFunction.Modifier::name),
                  BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(SetAttributesFunction.Modifier::attribute),
                  AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(SetAttributesFunction.Modifier::operation),
                  NumberProviders.CODEC.fieldOf("amount").forGetter(SetAttributesFunction.Modifier::amount),
                  SLOTS_CODEC.fieldOf("slot").forGetter(SetAttributesFunction.Modifier::slots),
                  ExtraCodecs.strictOptionalField(UUIDUtil.STRING_CODEC, "id").forGetter(SetAttributesFunction.Modifier::id)
               )
               .apply(var0, SetAttributesFunction.Modifier::new)
      );

      Modifier(String var1, Holder<Attribute> var2, AttributeModifier.Operation var3, NumberProvider var4, List<EquipmentSlot> var5, Optional<UUID> var6) {
         super();
         this.name = var1;
         this.attribute = var2;
         this.operation = var3;
         this.amount = var4;
         this.slots = var5;
         this.id = var6;
      }
   }

   public static class ModifierBuilder {
      private final String name;
      private final Holder<Attribute> attribute;
      private final AttributeModifier.Operation operation;
      private final NumberProvider amount;
      private Optional<UUID> id = Optional.empty();
      private final Set<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);

      public ModifierBuilder(String var1, Holder<Attribute> var2, AttributeModifier.Operation var3, NumberProvider var4) {
         super();
         this.name = var1;
         this.attribute = var2;
         this.operation = var3;
         this.amount = var4;
      }

      public SetAttributesFunction.ModifierBuilder forSlot(EquipmentSlot var1) {
         this.slots.add(var1);
         return this;
      }

      public SetAttributesFunction.ModifierBuilder withUuid(UUID var1) {
         this.id = Optional.of(var1);
         return this;
      }

      public SetAttributesFunction.Modifier build() {
         return new SetAttributesFunction.Modifier(this.name, this.attribute, this.operation, this.amount, List.copyOf(this.slots), this.id);
      }
   }
}
