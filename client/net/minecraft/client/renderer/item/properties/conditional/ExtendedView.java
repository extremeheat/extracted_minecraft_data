package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record ExtendedView() implements ConditionalItemModelProperty {
   public static final MapCodec<ExtendedView> MAP_CODEC = MapCodec.unit(new ExtendedView());

   public ExtendedView() {
      super();
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      return var5 == ItemDisplayContext.GUI && Minecraft.getInstance().hasShiftDown();
   }

   public MapCodec<ExtendedView> type() {
      return MAP_CODEC;
   }
}
