package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record IsCarried() implements ConditionalItemModelProperty {
   public static final MapCodec<IsCarried> MAP_CODEC = MapCodec.unit(new IsCarried());

   public IsCarried() {
      super();
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      boolean var10000;
      if (var3 instanceof LocalPlayer var5) {
         if (var5.containerMenu.getCarried() == var1) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public MapCodec<IsCarried> type() {
      return MAP_CODEC;
   }
}
