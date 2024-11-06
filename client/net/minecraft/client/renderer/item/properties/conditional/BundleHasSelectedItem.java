package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;

public record BundleHasSelectedItem() implements ConditionalItemModelProperty {
   public static final MapCodec<BundleHasSelectedItem> MAP_CODEC = MapCodec.unit(new BundleHasSelectedItem());

   public BundleHasSelectedItem() {
      super();
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      return BundleItem.hasSelectedItem(var1);
   }

   public MapCodec<BundleHasSelectedItem> type() {
      return MAP_CODEC;
   }
}
