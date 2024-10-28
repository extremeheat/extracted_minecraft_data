package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class DyeItem extends Item implements SignApplicator {
   private static final Map<DyeColor, DyeItem> ITEM_BY_COLOR = Maps.newEnumMap(DyeColor.class);
   private final DyeColor dyeColor;

   public DyeItem(DyeColor var1, Item.Properties var2) {
      super(var2);
      this.dyeColor = var1;
      ITEM_BY_COLOR.put(var1, this);
   }

   public InteractionResult interactLivingEntity(ItemStack var1, Player var2, LivingEntity var3, InteractionHand var4) {
      if (var3 instanceof Sheep var5) {
         if (var5.isAlive() && !var5.isSheared() && var5.getColor() != this.dyeColor) {
            var5.level().playSound((Player)var2, (Entity)var5, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!var2.level().isClientSide) {
               var5.setColor(this.dyeColor);
               var1.shrink(1);
            }

            return InteractionResult.sidedSuccess(var2.level().isClientSide);
         }
      }

      return InteractionResult.PASS;
   }

   public DyeColor getDyeColor() {
      return this.dyeColor;
   }

   public static DyeItem byColor(DyeColor var0) {
      return (DyeItem)ITEM_BY_COLOR.get(var0);
   }

   public boolean tryApplyToSign(Level var1, SignBlockEntity var2, boolean var3, Player var4) {
      if (var2.updateText((var1x) -> {
         return var1x.setColor(this.getDyeColor());
      }, var3)) {
         var1.playSound((Player)null, (BlockPos)var2.getBlockPos(), SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }
}
