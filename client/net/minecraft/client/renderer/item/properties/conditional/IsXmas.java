package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record IsXmas(boolean value) implements ConditionalItemModelProperty {
   public static final MapCodec<IsXmas> MAP_CODEC = MapCodec.unit(IsXmas::new);

   public IsXmas() {
      this(ChestRenderer.xmasTextures());
   }

   public IsXmas(boolean var1) {
      super();
      this.value = var1;
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      return this.value;
   }

   public MapCodec<IsXmas> type() {
      return MAP_CODEC;
   }

   public boolean value() {
      return this.value;
   }
}
