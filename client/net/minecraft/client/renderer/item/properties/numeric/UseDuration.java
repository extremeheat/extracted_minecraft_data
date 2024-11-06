package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record UseDuration(boolean remaining) implements RangeSelectItemModelProperty {
   public static final MapCodec<UseDuration> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.BOOL.optionalFieldOf("remaining", false).forGetter(UseDuration::remaining)).apply(var0, UseDuration::new);
   });

   public UseDuration(boolean var1) {
      super();
      this.remaining = var1;
   }

   public float get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      if (var3 != null && var3.getUseItem() == var1) {
         return this.remaining ? (float)var3.getUseItemRemainingTicks() : (float)useDuration(var1, var3);
      } else {
         return 0.0F;
      }
   }

   public MapCodec<UseDuration> type() {
      return MAP_CODEC;
   }

   public static int useDuration(ItemStack var0, LivingEntity var1) {
      return var0.getUseDuration(var1) - var1.getUseItemRemainingTicks();
   }

   public boolean remaining() {
      return this.remaining;
   }
}
