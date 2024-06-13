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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetAttributesFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetAttributesFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  SetAttributesFunction.Modifier.CODEC.listOf().fieldOf("modifiers").forGetter(var0x -> var0x.modifiers),
                  Codec.BOOL.optionalFieldOf("replace", true).forGetter(var0x -> var0x.replace)
               )
            )
            .apply(var0, SetAttributesFunction::new)
   );
   private final List<SetAttributesFunction.Modifier> modifiers;
   private final boolean replace;

   SetAttributesFunction(List<LootItemCondition> var1, List<SetAttributesFunction.Modifier> var2, boolean var3) {
      super(var1);
      this.modifiers = List.copyOf(var2);
      this.replace = var3;
   }

   @Override
   public LootItemFunctionType<SetAttributesFunction> getType() {
      return LootItemFunctions.SET_ATTRIBUTES;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.modifiers.stream().flatMap(var0 -> var0.amount.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      if (this.replace) {
         var1.set(DataComponents.ATTRIBUTE_MODIFIERS, this.updateModifiers(var2, ItemAttributeModifiers.EMPTY));
      } else {
         var1.update(
            DataComponents.ATTRIBUTE_MODIFIERS,
            ItemAttributeModifiers.EMPTY,
            var3 -> var3.modifiers().isEmpty() ? this.updateModifiers(var2, var1.getItem().getDefaultAttributeModifiers()) : this.updateModifiers(var2, var3)
         );
      }

      return var1;
   }

   private ItemAttributeModifiers updateModifiers(LootContext var1, ItemAttributeModifiers var2) {
      RandomSource var3 = var1.getRandom();

      for (SetAttributesFunction.Modifier var5 : this.modifiers) {
         EquipmentSlotGroup var6 = Util.getRandom(var5.slots, var3);
         var2 = var2.withModifierAdded(var5.attribute, new AttributeModifier(var5.id, (double)var5.amount.getFloat(var1), var5.operation), var6);
      }

      return var2;
   }

   public static SetAttributesFunction.ModifierBuilder modifier(
      ResourceLocation var0, Holder<Attribute> var1, AttributeModifier.Operation var2, NumberProvider var3
   ) {
      return new SetAttributesFunction.ModifierBuilder(var0, var1, var2, var3);
   }

   public static SetAttributesFunction.Builder setAttributes() {
      return new SetAttributesFunction.Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetAttributesFunction.Builder> {
      private final boolean replace;
      private final List<SetAttributesFunction.Modifier> modifiers = Lists.newArrayList();

      public Builder(boolean var1) {
         super();
         this.replace = var1;
      }

      public Builder() {
         this(false);
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
         return new SetAttributesFunction(this.getConditions(), this.modifiers, this.replace);
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

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

      public SetAttributesFunction.ModifierBuilder forSlot(EquipmentSlotGroup var1) {
         this.slots.add(var1);
         return this;
      }

      public SetAttributesFunction.Modifier build() {
         return new SetAttributesFunction.Modifier(this.id, this.attribute, this.operation, this.amount, List.copyOf(this.slots));
      }
   }
}
