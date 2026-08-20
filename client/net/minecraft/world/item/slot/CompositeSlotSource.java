package net.minecraft.world.item.slot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public abstract class CompositeSlotSource implements SlotSource {
   protected final HolderSet<SlotSource> terms;
   private final Function<LootContext, SlotCollection> compositeSlotSource;

   protected CompositeSlotSource(final HolderSet<SlotSource> terms) {
      super();
      this.terms = terms;
      this.compositeSlotSource = group(terms);
   }

   private static Function<LootContext, SlotCollection> group(final HolderSet<SlotSource> terms) {
      if (!terms.isBound()) {
         return (context) -> {
            List<SlotCollection> collections = new ArrayList();

            for(Holder<SlotSource> term : terms) {
               collections.add(((SlotSource)term.value()).provide(context));
            }

            return SlotCollection.concat(collections);
         };
      } else {
         Function var10000;
         switch (terms.size()) {
            case 0:
               var10000 = (var0) -> SlotCollection.EMPTY;
               break;
            case 1:
               Holder<SlotSource> term = terms.get(0);
               var10000 = (context) -> ((SlotSource)term.value()).provide(context);
               break;
            case 2:
               Holder<SlotSource> first = terms.get(0);
               Holder<SlotSource> second = terms.get(1);
               var10000 = (context) -> SlotCollection.concat(((SlotSource)first.value()).provide(context), ((SlotSource)second.value()).provide(context));
               break;
            default:
               var10000 = (context) -> {
                  List<SlotCollection> collections = new ArrayList();

                  for(Holder<SlotSource> term : terms) {
                     collections.add(((SlotSource)term.value()).provide(context));
                  }

                  return SlotCollection.concat(collections);
               };
         }

         return var10000;
      }
   }

   protected static <T extends CompositeSlotSource> MapCodec<T> createCodec(final Function<HolderSet<SlotSource>, T> factory) {
      return RecordCodecBuilder.mapCodec((i) -> i.group(SlotSources.LIST_CODEC.fieldOf("terms").forGetter((t) -> t.terms)).apply(i, factory));
   }

   protected static <T extends CompositeSlotSource> Codec<T> createInlineCodec(final Function<HolderSet<SlotSource>, T> factory) {
      return SlotSources.LIST_CODEC.xmap(factory, (t) -> t.terms);
   }

   public abstract MapCodec<? extends CompositeSlotSource> codec();

   public SlotCollection provide(final LootContext context) {
      return (SlotCollection)this.compositeSlotSource.apply(context);
   }

   public void validate(final ValidationContext context) {
      SlotSource.super.validate(context);
      Validatable.validateHolderSet(context, "terms", this.terms);
   }
}
