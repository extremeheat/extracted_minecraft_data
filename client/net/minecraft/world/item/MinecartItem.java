package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class MinecartItem extends Item {
   private final EntityType<? extends AbstractMinecart> type;

   public MinecartItem(EntityType<? extends AbstractMinecart> var1, Item.Properties var2) {
      super(var2);
      this.type = var1;
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (!var4.is(BlockTags.RAILS)) {
         return InteractionResult.FAIL;
      } else {
         ItemStack var5 = var1.getItemInHand();
         RailShape var6 = var4.getBlock() instanceof BaseRailBlock ? (RailShape)var4.getValue(((BaseRailBlock)var4.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
         double var7 = 0.0;
         if (var6.isSlope()) {
            var7 = 0.5;
         }

         Vec3 var9 = new Vec3((double)var3.getX() + 0.5, (double)var3.getY() + 0.0625 + var7, (double)var3.getZ() + 0.5);
         AbstractMinecart var10 = AbstractMinecart.createMinecart(var2, var9.x, var9.y, var9.z, this.type, EntitySpawnReason.DISPENSER, var5, var1.getPlayer());
         if (var10 == null) {
            return InteractionResult.FAIL;
         } else {
            if (AbstractMinecart.useExperimentalMovement(var2)) {
               for(Entity var13 : var2.getEntities((Entity)null, var10.getBoundingBox())) {
                  if (var13 instanceof AbstractMinecart) {
                     return InteractionResult.FAIL;
                  }
               }
            }

            if (var2 instanceof ServerLevel) {
               ServerLevel var14 = (ServerLevel)var2;
               var14.addFreshEntity(var10);
               var14.gameEvent(GameEvent.ENTITY_PLACE, var3, GameEvent.Context.of(var1.getPlayer(), var14.getBlockState(var3.below())));
            }

            var5.shrink(1);
            return InteractionResult.SUCCESS;
         }
      }
   }
}
