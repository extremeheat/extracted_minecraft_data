package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CompassAngle implements RangeSelectItemModelProperty {
   public static final MapCodec<CompassAngle> MAP_CODEC;
   private final CompassAngleState state;

   public CompassAngle(boolean var1, CompassAngleState.CompassTarget var2) {
      this(new CompassAngleState(var1, var2));
   }

   private CompassAngle(CompassAngleState var1) {
      super();
      this.state = var1;
   }

   public float get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4) {
      return this.state.get(var1, var2, var3, var4);
   }

   public MapCodec<CompassAngle> type() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = CompassAngleState.MAP_CODEC.xmap(CompassAngle::new, (var0) -> {
         return var0.state;
      });
   }
}
