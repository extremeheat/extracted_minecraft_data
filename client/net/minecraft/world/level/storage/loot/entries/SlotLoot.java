package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.slot.SlotSource;
import net.minecraft.world.item.slot.SlotSources;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SlotLoot extends SingleEntryContainerBase {
   public static final MapCodec<SlotLoot> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotSources.CODEC.fieldOf("slot_source").forGetter((t) -> t.slotSource)).and(uniformFields(i)).apply(i, SlotLoot::new));
   private final Holder<SlotSource> slotSource;

   private SlotLoot(final Holder<SlotSource> slotSource, final int weight, final int quality, final Optional<Holder<LootItemCondition>> condition, final Optional<Holder<LootItemFunction>> modifier) {
      super(weight, quality, condition, modifier);
      this.slotSource = slotSource;
   }

   public MapCodec<SlotLoot> codec() {
      return MAP_CODEC;
   }

   public void createItemStack(final Consumer<ItemStack> output, final LootContext context) {
      ((SlotSource)this.slotSource.value()).provide(context).itemCopies().filter((stack) -> !stack.isEmpty()).forEach(output);
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validateHolder(context, "slot_source", this.slotSource);
   }
}
