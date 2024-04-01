package net.minecraft.world.item.trading;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record ItemCost(Holder<Item> d, int e, DataComponentPredicate f, ItemStack g) {
   private final Holder<Item> item;
   private final int count;
   private final DataComponentPredicate components;
   private final ItemStack itemStack;
   public static final Codec<ItemCost> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("id").forGetter(ItemCost::item),
               ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(ItemCost::count),
               ExtraCodecs.strictOptionalField(DataComponentPredicate.CODEC, "components", DataComponentPredicate.EMPTY).forGetter(ItemCost::components)
            )
            .apply(var0, ItemCost::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemCost> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.holderRegistry(Registries.ITEM),
      ItemCost::item,
      ByteBufCodecs.VAR_INT,
      ItemCost::count,
      DataComponentPredicate.STREAM_CODEC,
      ItemCost::components,
      ItemCost::new
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, Optional<ItemCost>> OPTIONAL_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs::optional);

   public ItemCost(ItemLike var1) {
      this(var1, 1);
   }

   public ItemCost(ItemLike var1, int var2) {
      this(var1.asItem().builtInRegistryHolder(), var2, DataComponentPredicate.EMPTY);
   }

   public ItemCost(Holder<Item> var1, int var2, DataComponentPredicate var3) {
      this(var1, var2, var3, createStack(var1, var2, var3));
   }

   public ItemCost(Holder<Item> var1, int var2, DataComponentPredicate var3, ItemStack var4) {
      super();
      this.item = var1;
      this.count = var2;
      this.components = var3;
      this.itemStack = var4;
   }

   public ItemCost withComponents(UnaryOperator<DataComponentPredicate.Builder> var1) {
      return new ItemCost(this.item, this.count, var1.apply(DataComponentPredicate.builder()).build());
   }

   private static ItemStack createStack(Holder<Item> var0, int var1, DataComponentPredicate var2) {
      return new ItemStack(var0, var1, var2.asPatch());
   }

   public boolean test(ItemStack var1) {
      return var1.is(this.item) && this.components.test((DataComponentHolder)var1);
   }
}
