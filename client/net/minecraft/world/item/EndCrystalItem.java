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
   public EndCrystalItem(final Item.Properties properties) {
      super(properties);
   }

   public InteractionResult useOn(final UseOnContext context) {
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      BlockState blockState = level.getBlockState(pos);
      if (!blockState.is(Blocks.OBSIDIAN) && !blockState.is(Blocks.BEDROCK)) {
         return InteractionResult.FAIL;
      } else {
         BlockPos above = pos.above();
         if (!level.isEmptyBlock(above)) {
            return InteractionResult.FAIL;
         } else {
            double x = (double)above.getX();
            double y = (double)above.getY();
            double z = (double)above.getZ();
            List<Entity> entities = level.getEntities((Entity)null, new AABB(x, y, z, x + 1.0, y + 2.0, z + 1.0));
            if (!entities.isEmpty()) {
               return InteractionResult.FAIL;
            } else {
               if (level instanceof ServerLevel) {
                  EndCrystal crystal = new EndCrystal(level, x + 0.5, y, z + 0.5);
                  crystal.setShowBottom(false);
                  level.addFreshEntity(crystal);
                  level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, above);
                  EndDragonFight fight = ((ServerLevel)level).getDragonFight();
                  if (fight != null) {
                     fight.tryRespawn();
                  }
               }

               context.getItemInHand().shrink(1);
               return InteractionResult.SUCCESS;
            }
         }
      }
   }
}
