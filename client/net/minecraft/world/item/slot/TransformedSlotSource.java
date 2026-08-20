package net.minecraft.world.item.slot;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public abstract class TransformedSlotSource implements SlotSource {
   protected final Holder<SlotSource> slotSource;

   protected TransformedSlotSource(final Holder<SlotSource> slotSource) {
      super();
      this.slotSource = slotSource;
   }

   public abstract MapCodec<? extends TransformedSlotSource> codec();

   protected static <T extends TransformedSlotSource> Products.P1<RecordCodecBuilder.Mu<T>, Holder<SlotSource>> commonFields(final RecordCodecBuilder.Instance<T> i) {
      return i.group(SlotSources.CODEC.fieldOf("slot_source").forGetter((t) -> t.slotSource));
   }

   protected abstract SlotCollection transform(SlotCollection slots);

   public final SlotCollection provide(final LootContext context) {
      return this.transform(((SlotSource)this.slotSource.value()).provide(context));
   }

   public void validate(final ValidationContext context) {
      SlotSource.super.validate(context);
      Validatable.validateHolder(context, "slot_source", this.slotSource);
   }
}
