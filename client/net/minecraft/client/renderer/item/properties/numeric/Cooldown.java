package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record Cooldown() implements RangeSelectItemModelProperty {
   public static final MapCodec<Cooldown> MAP_CODEC = MapCodec.unit(new Cooldown());

   public Cooldown() {
      super();
   }

   public float get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      float var10000;
      if (var3 instanceof Player var5) {
         var10000 = var5.getCooldowns().getCooldownPercent(var1, 0.0F);
      } else {
         var10000 = 0.0F;
      }

      return var10000;
   }

   public MapCodec<Cooldown> type() {
      return MAP_CODEC;
   }
}
