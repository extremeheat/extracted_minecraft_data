package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public class CrossbowPull implements RangeSelectItemModelProperty {
   public static final MapCodec<CrossbowPull> MAP_CODEC = MapCodec.unit(new CrossbowPull());

   public CrossbowPull() {
      super();
   }

   public float get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      if (var3 == null) {
         return 0.0F;
      } else if (CrossbowItem.isCharged(var1)) {
         return 0.0F;
      } else {
         int var5 = CrossbowItem.getChargeDuration(var1, var3);
         return (float)UseDuration.useDuration(var1, var3) / (float)var5;
      }
   }

   public MapCodec<CrossbowPull> type() {
      return MAP_CODEC;
   }
}
