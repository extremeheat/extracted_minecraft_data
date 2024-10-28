package net.minecraft.world.item.trading;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record ItemCost(Holder<Item> item, int count, DataComponentPredicate components, ItemStack itemStack) {
   public static final Codec<ItemCost> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemCost::item), ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(ItemCost::count), DataComponentPredicate.CODEC.optionalFieldOf("components", DataComponentPredicate.EMPTY).forGetter(ItemCost::components)).apply(var0, ItemCost::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemCost> STREAM_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Optional<ItemCost>> OPTIONAL_STREAM_CODEC;

   public ItemCost(ItemLike var1) {
      this(var1, 1);
   }

   public ItemCost(ItemLike var1, int var2) {
      this(var1.asItem().builtInRegistryHolder(), var2, DataComponentPredicate.EMPTY);
   }

   public ItemCost(Holder<Item> var1, int var2, DataComponentPredicate var3) {
      this(var1, var2, var3, createStack(var1, var2, var3));
   }

   public ItemCost(Holder<Item> item, int count, DataComponentPredicate components, ItemStack itemStack) {
      super();
      this.item = item;
      this.count = count;
      this.components = components;
      this.itemStack = itemStack;
   }

   public ItemCost withComponents(UnaryOperator<DataComponentPredicate.Builder> var1) {
      return new ItemCost(this.item, this.count, ((DataComponentPredicate.Builder)var1.apply(DataComponentPredicate.builder())).build());
   }

   private static ItemStack createStack(Holder<Item> var0, int var1, DataComponentPredicate var2) {
      return new ItemStack(var0, var1, var2.asPatch());
   }

   public boolean test(ItemStack var1) {
      return var1.is(this.item) && this.components.test((DataComponentHolder)var1);
   }

   public Holder<Item> item() {
      return this.item;
   }

   public int count() {
      return this.count;
   }

   public DataComponentPredicate components() {
      return this.components;
   }

   public ItemStack itemStack() {
      return this.itemStack;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderRegistry(Registries.ITEM), ItemCost::item, ByteBufCodecs.VAR_INT, ItemCost::count, DataComponentPredicate.STREAM_CODEC, ItemCost::components, ItemCost::new);
      OPTIONAL_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs::optional);
   }
}
