package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record IsUsingItem() implements ConditionalItemModelProperty {
   public static final MapCodec<IsUsingItem> MAP_CODEC = MapCodec.unit(new IsUsingItem());

   public IsUsingItem() {
      super();
   }

   public boolean get(final ItemStack itemStack, final @Nullable ClientLevel level, final @Nullable LivingEntity owner, final int seed, final ItemDisplayContext displayContext) {
      if (owner == null) {
         return false;
      } else {
         return owner.isUsingItem() && owner.getUseItem() == itemStack;
      }
   }

   public MapCodec<IsUsingItem> type() {
      return MAP_CODEC;
   }
}
