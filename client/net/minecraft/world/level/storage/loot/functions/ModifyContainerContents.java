package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulator;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulators;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ModifyContainerContents extends LootItemConditionalFunction {
   public static final MapCodec<ModifyContainerContents> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(ContainerComponentManipulators.CODEC.fieldOf("component").forGetter((f) -> f.component), LootItemFunctions.CODEC.fieldOf("modifier").forGetter((f) -> f.modifier))).apply(i, ModifyContainerContents::new));
   private final ContainerComponentManipulator<?> component;
   private final Holder<LootItemFunction> modifier;

   private ModifyContainerContents(final Optional<Holder<LootItemCondition>> condition, final ContainerComponentManipulator<?> component, final Holder<LootItemFunction> modifier) {
      super(condition);
      this.component = component;
      this.modifier = modifier;
   }

   public MapCodec<ModifyContainerContents> codec() {
      return MAP_CODEC;
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      if (itemStack.isEmpty()) {
         return itemStack;
      } else {
         this.component.modifyItems(itemStack, (c) -> (ItemStack)((LootItemFunction)this.modifier.value()).apply(c, context));
         return itemStack;
      }
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validateHolder(context, "modifier", this.modifier);
   }
}
