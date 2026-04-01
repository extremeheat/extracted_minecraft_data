package net.minecraft.world.entity.livingblock.behavior;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.commands.PlaceCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.slf4j.Logger;

public class CombineInto9x9Behavior implements LivingBlockBehavior {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int CHECK_INTERVAL = 200;
   private static final int NEEDED_COBBLESTONES = 272;
   private static final int NEEDED_GLASS_BLOCKS = 16;
   private static final int NEEDED_TORCHES = 4;
   private final Block torch;
   private final Identifier nineByNine;
   private int lastTriggeredTick = 0;

   public static LivingBlockBehaviorType nineByNine(final Block torch, final Identifier structure) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new CombineInto9x9Behavior(torch, structure)));
   }

   public CombineInto9x9Behavior(final Block torch, final Identifier nineByNine) {
      super();
      this.torch = torch;
      this.nineByNine = nineByNine;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.tickCount - this.lastTriggeredTick >= 200;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      this.lastTriggeredTick = tickCount;
      List<LivingBlock> entities = level.getEntities(EntityType.LIVING_BLOCK, entity.getBoundingBox().inflate(20.0), (var0) -> true);
      List<LivingBlock> torches = entities.stream().filter((livingBlock) -> livingBlock.isAlive() && livingBlock.getBlockState().is(this.torch)).limit(4L).toList();
      if (torches.size() < 4) {
         return false;
      } else {
         List<LivingBlock> glassBlocks = entities.stream().filter((livingBlock) -> livingBlock.isAlive() && livingBlock.getBlockState().is(Blocks.GLASS)).limit(16L).toList();
         if (glassBlocks.size() < 16) {
            return false;
         } else {
            List<LivingBlock> cobblestones = entities.stream().filter((livingBlock) -> livingBlock.isAlive() && livingBlock.getBlockState().is(Blocks.COBBLESTONE)).limit(272L).toList();
            if (cobblestones.size() < 272) {
               return false;
            } else {
               torches.forEach(Entity::discard);
               glassBlocks.forEach(Entity::discard);
               cobblestones.forEach(Entity::discard);

               try {
                  PlaceCommand.placeTemplate(this.nineByNine, entity.blockPosition().below(), this.rotationFromDirection(entity.getDirection()), Mirror.NONE, 1.0F, 0, true, level);
                  return true;
               } catch (CommandSyntaxException e) {
                  LOGGER.error("Couldn't place 9x9", e);
                  return false;
               }
            }
         }
      }
   }

   private Rotation rotationFromDirection(final Direction direction) {
      Rotation var10000;
      switch (direction) {
         case WEST -> var10000 = Rotation.CLOCKWISE_90;
         case NORTH -> var10000 = Rotation.CLOCKWISE_180;
         case EAST -> var10000 = Rotation.COUNTERCLOCKWISE_90;
         default -> var10000 = Rotation.NONE;
      }

      return var10000;
   }
}
