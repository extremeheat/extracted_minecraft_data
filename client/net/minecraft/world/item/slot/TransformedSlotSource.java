package net.minecraft.world.item.slot;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;

public abstract class TransformedSlotSource implements SlotSource {
   protected final SlotSource slotSource;

   protected TransformedSlotSource(SlotSource var1) {
      super();
      this.slotSource = var1;
   }

   public abstract MapCodec<? extends TransformedSlotSource> codec();

   protected static <T extends TransformedSlotSource> Products.P1<RecordCodecBuilder.Mu<T>, SlotSource> commonFields(RecordCodecBuilder.Instance<T> var0) {
      return var0.group(SlotSources.CODEC.fieldOf("slot_source").forGetter((var0x) -> var0x.slotSource));
   }

   protected abstract SlotCollection transform(SlotCollection var1);

   public final SlotCollection provide(LootContext var1) {
      return this.transform(this.slotSource.provide(var1));
   }

   public void validate(ValidationContext var1) {
      SlotSource.super.validate(var1);
      this.slotSource.validate(var1.forChild(new ProblemReporter.FieldPathElement("slot_source")));
   }
}
