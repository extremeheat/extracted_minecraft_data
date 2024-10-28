package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class ItemParticleOption implements ParticleOptions {
   private static final Codec<ItemStack> ITEM_CODEC;
   private final ParticleType<ItemParticleOption> type;
   private final ItemStack itemStack;

   public static MapCodec<ItemParticleOption> codec(ParticleType<ItemParticleOption> var0) {
      return ITEM_CODEC.xmap((var1) -> {
         return new ItemParticleOption(var0, var1);
      }, (var0x) -> {
         return var0x.itemStack;
      }).fieldOf("item");
   }

   public static StreamCodec<? super RegistryFriendlyByteBuf, ItemParticleOption> streamCodec(ParticleType<ItemParticleOption> var0) {
      return ItemStack.STREAM_CODEC.map((var1) -> {
         return new ItemParticleOption(var0, var1);
      }, (var0x) -> {
         return var0x.itemStack;
      });
   }

   public ItemParticleOption(ParticleType<ItemParticleOption> var1, ItemStack var2) {
      super();
      if (var2.isEmpty()) {
         throw new IllegalArgumentException("Empty stacks are not allowed");
      } else {
         this.type = var1;
         this.itemStack = var2;
      }
   }

   public ParticleType<ItemParticleOption> getType() {
      return this.type;
   }

   public ItemStack getItem() {
      return this.itemStack;
   }

   static {
      ITEM_CODEC = Codec.withAlternative(ItemStack.SINGLE_ITEM_CODEC, ItemStack.ITEM_NON_AIR_CODEC, ItemStack::new);
   }
}
