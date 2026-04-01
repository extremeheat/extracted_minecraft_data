package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.ColorLerper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record Jeb() implements ItemTintSource {
   public static final MapCodec<Jeb> CODEC = MapCodec.unit(Jeb::new);

   public Jeb() {
      super();
   }

   public int calculate(final ItemStack itemStack, final @Nullable ClientLevel level, final @Nullable LivingEntity owner) {
      return ColorLerper.getLerpedColor(ColorLerper.Type.LIVING_BLOCK_GROUPS, level != null ? (float)level.getGameTime() : 0.0F);
   }

   public MapCodec<Jeb> type() {
      return CODEC;
   }
}
