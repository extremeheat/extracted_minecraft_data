package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class EndCrystalItem extends Item {
   public EndCrystalItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (!var4.is(Blocks.OBSIDIAN) && !var4.is(Blocks.BEDROCK)) {
         return InteractionResult.FAIL;
      } else {
         BlockPos var5 = var3.above();
         if (!var2.isEmptyBlock(var5)) {
            return InteractionResult.FAIL;
         } else {
            double var6 = (double)var5.getX();
            double var8 = (double)var5.getY();
            double var10 = (double)var5.getZ();
            List var12 = var2.getEntities((Entity)null, new AABB(var6, var8, var10, var6 + 1.0D, var8 + 2.0D, var10 + 1.0D));
            if (!var12.isEmpty()) {
               return InteractionResult.FAIL;
            } else {
               if (var2 instanceof ServerLevel) {
                  EndCrystal var13 = new EndCrystal(var2, var6 + 0.5D, var8, var10 + 0.5D);
                  var13.setShowBottom(false);
                  var2.addFreshEntity(var13);
                  var2.gameEvent(var1.getPlayer(), GameEvent.ENTITY_PLACE, var5);
                  EndDragonFight var14 = ((ServerLevel)var2).dragonFight();
                  if (var14 != null) {
                     var14.tryRespawn();
                  }
               }

               var1.getItemInHand().shrink(1);
               return InteractionResult.sidedSuccess(var2.isClientSide);
            }
         }
      }
   }

   public boolean isFoil(ItemStack var1) {
      return true;
   }
}
