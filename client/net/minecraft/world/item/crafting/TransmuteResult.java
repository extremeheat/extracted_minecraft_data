package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;

public record TransmuteResult(Optional<Holder<Item>> item, int count, DataComponentPatch components) {
   public static final MapCodec<TransmuteResult> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Item.CODEC.optionalFieldOf("id").forGetter(TransmuteResult::item), ExtraCodecs.intRange(1, 99).optionalFieldOf("count", 1).forGetter(TransmuteResult::count), DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(TransmuteResult::components)).apply(i, TransmuteResult::new));
   public static final Codec<TransmuteResult> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, TransmuteResult> STREAM_CODEC;
   public static final TransmuteResult KEEP_INPUT_ITEM;

   public TransmuteResult(final Holder<Item> item) {
      this(Optional.of(item), 1, DataComponentPatch.EMPTY);
   }

   public TransmuteResult(final Item item) {
      this(item.builtInRegistryHolder());
   }

   public TransmuteResult {
      super();
   }

   public static TransmuteResult fromTemplate(final ItemStackTemplate template) {
      return new TransmuteResult(Optional.of(template.item()), template.count(), template.components());
   }

   public ItemStackTemplate resolve(final Holder<Item> inputItem) {
      return this.resolve(inputItem, this.count);
   }

   public ItemStackTemplate resolve(final Holder<Item> inputItem, final int count) {
      return new ItemStackTemplate((Holder)this.item.orElse(inputItem), count, this.components);
   }

   static {
      CODEC = Codec.withAlternative(MAP_CODEC.codec(), Item.CODEC, TransmuteResult::new);
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.optional(Item.STREAM_CODEC), TransmuteResult::item, ByteBufCodecs.VAR_INT, TransmuteResult::count, DataComponentPatch.STREAM_CODEC, TransmuteResult::components, TransmuteResult::new);
      KEEP_INPUT_ITEM = new TransmuteResult(Optional.empty(), 1, DataComponentPatch.EMPTY);
   }
}
