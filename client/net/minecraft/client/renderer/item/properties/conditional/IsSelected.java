package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public record IsSelected() implements ConditionalItemModelProperty {
   public static final MapCodec<IsSelected> MAP_CODEC = MapCodec.unit(new IsSelected());

   public IsSelected() {
      super();
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      boolean var10000;
      if (var3 instanceof LocalPlayer var6) {
         if (var6.getInventory().getSelected() == var1) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public MapCodec<IsSelected> type() {
      return MAP_CODEC;
   }
}
