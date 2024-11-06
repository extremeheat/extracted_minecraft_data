package net.minecraft.client.renderer.item.properties.conditional;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record Damaged() implements ConditionalItemModelProperty {
   public static final MapCodec<Damaged> MAP_CODEC = MapCodec.unit(new Damaged());

   public Damaged() {
      super();
   }

   public boolean get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      return var1.isDamaged();
   }

   public MapCodec<Damaged> type() {
      return MAP_CODEC;
   }
}
