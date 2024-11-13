package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record ExtendedView() implements ConditionalItemModelProperty {
   public static final MapCodec<ExtendedView> MAP_CODEC = MapCodec.unit(new ExtendedView());

   public ExtendedView() {
      super();
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      return var5 == ItemDisplayContext.GUI && Screen.hasShiftDown();
   }

   public MapCodec<ExtendedView> type() {
      return MAP_CODEC;
   }
}
