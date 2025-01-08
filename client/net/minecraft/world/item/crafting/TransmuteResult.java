package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public record TransmuteResult(Holder<Item> item, int count, DataComponentPatch components) {
   private static final Codec<TransmuteResult> FULL_CODEC = RecordCodecBuilder.create((var0) -> var0.group(Item.CODEC.fieldOf("id").forGetter(TransmuteResult::item), ExtraCodecs.intRange(1, 99).optionalFieldOf("count", 1).forGetter(TransmuteResult::count), DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(TransmuteResult::components)).apply(var0, TransmuteResult::new));
   public static final Codec<TransmuteResult> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, TransmuteResult> STREAM_CODEC;

   public TransmuteResult(Item var1) {
      this(var1.builtInRegistryHolder(), 1, DataComponentPatch.EMPTY);
   }

   public TransmuteResult(Holder<Item> var1, int var2, DataComponentPatch var3) {
      super();
      this.item = var1;
      this.count = var2;
      this.components = var3;
   }

   private static DataResult<TransmuteResult> validate(TransmuteResult var0) {
      return ItemStack.validateStrict(new ItemStack(var0.item, var0.count, var0.components)).map((var1) -> var0);
   }

   public ItemStack apply(ItemStack var1) {
      ItemStack var2 = var1.transmuteCopy(this.item.value(), this.count);
      var2.applyComponents(this.components);
      return var2;
   }

   public SlotDisplay display() {
      return new SlotDisplay.ItemStackSlotDisplay(new ItemStack(this.item, this.count, this.components));
   }

   static {
      CODEC = Codec.withAlternative(FULL_CODEC, Item.CODEC, (var0) -> new TransmuteResult((Item)var0.value())).validate(TransmuteResult::validate);
      STREAM_CODEC = StreamCodec.composite(Item.STREAM_CODEC, TransmuteResult::item, ByteBufCodecs.VAR_INT, TransmuteResult::count, DataComponentPatch.STREAM_CODEC, TransmuteResult::components, TransmuteResult::new);
   }
}
