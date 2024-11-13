package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record FishingRodCast() implements ConditionalItemModelProperty {
   public static final MapCodec<FishingRodCast> MAP_CODEC = MapCodec.unit(new FishingRodCast());

   public FishingRodCast() {
      super();
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      if (var3 instanceof Player var6) {
         if (var6.fishing != null) {
            HumanoidArm var7 = FishingHookRenderer.getHoldingArm(var6);
            return var3.getItemHeldByArm(var7) == var1;
         }
      }

      return false;
   }

   public MapCodec<FishingRodCast> type() {
      return MAP_CODEC;
   }
}
