package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record IsViewEntity() implements ConditionalItemModelProperty {
   public static final MapCodec<IsViewEntity> MAP_CODEC = MapCodec.unit(new IsViewEntity());

   public IsViewEntity() {
      super();
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      Minecraft var6 = Minecraft.getInstance();
      Entity var7 = var6.getCameraEntity();
      return var7 != null ? var3 == var7 : var3 == var6.player;
   }

   public MapCodec<IsViewEntity> type() {
      return MAP_CODEC;
   }
}
