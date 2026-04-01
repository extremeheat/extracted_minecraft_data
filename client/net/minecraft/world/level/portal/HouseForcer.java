package net.minecraft.world.level.portal;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class HouseForcer {
   private final ServerLevel level;

   public HouseForcer(final ServerLevel level) {
      super();
      this.level = level;
   }

   public void createHouse(final BlockPos origin, final LivingBlock door, final List<LivingBlock> blocks) {
      BlockPos.MutableBlockPos mutablePosition = origin.mutable();
      Block var6 = door.getBlockState().getBlock();
      if (var6 instanceof DoorBlock doorBlock) {
         BlockPos doorBottom = origin.offset(1, 0, 2);
         BlockPos doorTop = origin.offset(1, 1, 2);
         if (!this.level.getBlockState(doorBottom).canBeReplaced() || !this.level.getBlockState(doorTop).canBeReplaced()) {
            return;
         }

         Set<ServerPlayer> players = new HashSet();

         for(int height = 0; height < 3; ++height) {
            for(int width = 0; width < 3; ++width) {
               for(int depth = 0; depth < 3; ++depth) {
                  mutablePosition.set(origin.getX() + width, origin.getY() + height, origin.getZ() + depth);
                  if (this.level.getBlockState(mutablePosition).canBeReplaced()) {
                     if (height < 2) {
                        if (width == 1 && depth == 2 && height == 0) {
                           this.level.setBlock(mutablePosition, doorBlock.defaultBlockState(), 3);
                           this.level.setBlock(mutablePosition.above(), (BlockState)doorBlock.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3);
                           door.discard();
                           ServerPlayer owner = door.getAttributablePlayer();
                           if (owner != null) {
                              players.add(owner);
                           }
                           continue;
                        }

                        if (width == 1 && depth == 2 && height == 1 || width == 1 && depth == 1) {
                           continue;
                        }
                     }

                     if (blocks.isEmpty()) {
                        return;
                     }

                     LivingBlock block = (LivingBlock)blocks.removeFirst();
                     ServerPlayer owner = block.getAttributablePlayer();
                     if (owner != null) {
                        players.add(owner);
                     }

                     Block blockType = block.getBlockState().getBlock();
                     this.level.setBlock(mutablePosition, blockType.defaultBlockState(), 3);
                     block.discard();
                  }
               }
            }
         }

         PlayerTrigger var10001 = CriteriaTriggers.HOUSE_BUILT;
         Objects.requireNonNull(var10001);
         players.forEach(var10001::trigger);
      }

   }
}
