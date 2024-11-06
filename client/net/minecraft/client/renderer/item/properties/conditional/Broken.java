package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record Broken() implements ConditionalItemModelProperty {
   public static final MapCodec<Broken> MAP_CODEC = MapCodec.unit(new Broken());

   public Broken() {
      super();
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      return var1.nextDamageWillBreak();
   }

   public MapCodec<Broken> type() {
      return MAP_CODEC;
   }
}
