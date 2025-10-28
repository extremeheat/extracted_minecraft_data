package net.minecraft.world.item.slot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;

public abstract class CompositeSlotSource implements SlotSource {
   protected final List<SlotSource> terms;
   private final Function<LootContext, SlotCollection> compositeSlotSource;

   protected CompositeSlotSource(List<SlotSource> var1) {
      super();
      this.terms = var1;
      this.compositeSlotSource = SlotSources.group(var1);
   }

   protected static <T extends CompositeSlotSource> MapCodec<T> createCodec(Function<List<SlotSource>, T> var0) {
      return RecordCodecBuilder.mapCodec((var1) -> var1.group(SlotSources.CODEC.listOf().fieldOf("terms").forGetter((var0x) -> var0x.terms)).apply(var1, var0));
   }

   protected static <T extends CompositeSlotSource> Codec<T> createInlineCodec(Function<List<SlotSource>, T> var0) {
      return SlotSources.CODEC.listOf().xmap(var0, (var0x) -> var0x.terms);
   }

   public abstract MapCodec<? extends CompositeSlotSource> codec();

   public SlotCollection provide(LootContext var1) {
      return (SlotCollection)this.compositeSlotSource.apply(var1);
   }

   public void validate(ValidationContext var1) {
      SlotSource.super.validate(var1);

      for(int var2 = 0; var2 < this.terms.size(); ++var2) {
         ((SlotSource)this.terms.get(var2)).validate(var1.forChild(new ProblemReporter.IndexedFieldPathElement("terms", var2)));
      }

   }
}
