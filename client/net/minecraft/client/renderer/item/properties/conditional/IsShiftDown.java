package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record IsShiftDown() implements ConditionalItemModelProperty {
   public static final MapCodec<IsShiftDown> MAP_CODEC = MapCodec.unit(new IsShiftDown());

   public IsShiftDown() {
      super();
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      return Screen.hasShiftDown();
   }

   public MapCodec<IsShiftDown> type() {
      return MAP_CODEC;
   }
}
