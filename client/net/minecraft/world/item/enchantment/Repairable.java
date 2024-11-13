package net.minecraft.world.item.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record Repairable(HolderSet<Item> items) {
   public static final Codec<Repairable> CODEC = RecordCodecBuilder.create((var0) -> var0.group(RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("items").forGetter(Repairable::items)).apply(var0, Repairable::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, Repairable> STREAM_CODEC;

   public Repairable(HolderSet<Item> var1) {
      super();
      this.items = var1;
   }

   public boolean isValidRepairItem(ItemStack var1) {
      return var1.is(this.items);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderSet(Registries.ITEM), Repairable::items, Repairable::new);
   }
}
