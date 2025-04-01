package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.MobTrophyBlock;

public record GradeColor() implements ItemTintSource {
   public static final MapCodec<GradeColor> MAP_CODEC = MapCodec.unit(new GradeColor());

   public GradeColor() {
      super();
   }

   public int calculate(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3) {
      BlockItemStateProperties var4 = (BlockItemStateProperties)var1.get(DataComponents.BLOCK_STATE);
      if (var4 != null) {
         MobTrophyBlock.Grade var5 = (MobTrophyBlock.Grade)var4.get(MobTrophyBlock.GRADE);
         if (var5 != null) {
            return ARGB.opaque(var5.color());
         }
      }

      return ARGB.opaque(MobTrophyBlock.Grade.GRASS.color());
   }

   public MapCodec<GradeColor> type() {
      return MAP_CODEC;
   }
}
