package net.minecraft.world.item.slot;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public abstract class TransformedSlotSource implements SlotSource {
   protected final SlotSource slotSource;

   protected TransformedSlotSource(final SlotSource slotSource) {
      super();
      this.slotSource = slotSource;
   }

   public abstract MapCodec<? extends TransformedSlotSource> codec();

   protected static <T extends TransformedSlotSource> Products.P1<RecordCodecBuilder.Mu<T>, SlotSource> commonFields(final RecordCodecBuilder.Instance<T> i) {
      return i.group(SlotSources.CODEC.fieldOf("slot_source").forGetter((t) -> t.slotSource));
   }

   protected abstract SlotCollection transform(SlotCollection slots);

   public final SlotCollection provide(final LootContext context) {
      return this.transform(this.slotSource.provide(context));
   }

   public void validate(final ValidationContext context) {
      SlotSource.super.validate(context);
      Validatable.validate(context, "slot_source", this.slotSource);
   }
}
