package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class CrossbowPull implements RangeSelectItemModelProperty {
   public static final MapCodec<CrossbowPull> MAP_CODEC = MapCodec.unit(new CrossbowPull());

   public CrossbowPull() {
      super();
   }

   public float get(ItemStack var1, @Nullable ClientLevel var2, @Nullable ItemOwner var3, int var4) {
      LivingEntity var5 = var3 == null ? null : var3.asLivingEntity();
      if (var5 == null) {
         return 0.0F;
      } else if (CrossbowItem.isCharged(var1)) {
         return 0.0F;
      } else {
         int var6 = CrossbowItem.getChargeDuration(var1, var5);
         return (float)UseDuration.useDuration(var1, var5) / (float)var6;
      }
   }

   public MapCodec<CrossbowPull> type() {
      return MAP_CODEC;
   }
}
