package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record IsUsingItem() implements ConditionalItemModelProperty {
   public static final MapCodec<IsUsingItem> MAP_CODEC = MapCodec.unit(new IsUsingItem());

   public IsUsingItem() {
      super();
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      if (var3 == null) {
         return false;
      } else {
         return var3.isUsingItem() && var3.getUseItem() == var1;
      }
   }

   public MapCodec<IsUsingItem> type() {
      return MAP_CODEC;
   }
}
