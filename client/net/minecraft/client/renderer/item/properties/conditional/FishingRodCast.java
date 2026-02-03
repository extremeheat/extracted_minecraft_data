package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record FishingRodCast() implements ConditionalItemModelProperty {
   public static final MapCodec<FishingRodCast> MAP_CODEC = MapCodec.unit(new FishingRodCast());

   public FishingRodCast() {
      super();
   }

   public boolean get(final ItemStack itemStack, final @Nullable ClientLevel level, final @Nullable LivingEntity owner, final int seed, final ItemDisplayContext displayContext) {
      if (owner instanceof Player player) {
         if (player.fishing != null) {
            HumanoidArm holdingArm = FishingHookRenderer.getHoldingArm(player);
            return owner.getItemHeldByArm(holdingArm) == itemStack;
         }
      }

      return false;
   }

   public MapCodec<FishingRodCast> type() {
      return MAP_CODEC;
   }
}
