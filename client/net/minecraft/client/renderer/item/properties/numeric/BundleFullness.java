package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;

public record BundleFullness() implements RangeSelectItemModelProperty {
   public static final MapCodec<BundleFullness> MAP_CODEC = MapCodec.unit(new BundleFullness());

   public BundleFullness() {
      super();
   }

   public float get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      return BundleItem.getFullnessDisplay(var1);
   }

   public MapCodec<BundleFullness> type() {
      return MAP_CODEC;
   }
}
