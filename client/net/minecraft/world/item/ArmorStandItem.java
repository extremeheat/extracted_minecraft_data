package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ArmorStandItem extends Item {
   public ArmorStandItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResult useOn(UseOnContext var1) {
      Direction var2 = var1.getClickedFace();
      if (var2 == Direction.DOWN) {
         return InteractionResult.FAIL;
      } else {
         Level var3 = var1.getLevel();
         BlockPlaceContext var4 = new BlockPlaceContext(var1);
         BlockPos var5 = var4.getClickedPos();
         ItemStack var6 = var1.getItemInHand();
         Vec3 var7 = Vec3.atBottomCenterOf(var5);
         AABB var8 = EntityType.ARMOR_STAND.getDimensions().makeBoundingBox(var7.x(), var7.y(), var7.z());
         if (var3.noCollision(null, var8) && var3.getEntities(null, var8).isEmpty()) {
            if (var3 instanceof ServerLevel var9) {
               ArmorStand var10 = EntityType.ARMOR_STAND
                  .create((ServerLevel)var9, var6.getTag(), null, var1.getPlayer(), var5, MobSpawnType.SPAWN_EGG, true, true);
               if (var10 == null) {
                  return InteractionResult.FAIL;
               }

               float var11 = (float)Mth.floor((Mth.wrapDegrees(var1.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
               var10.moveTo(var10.getX(), var10.getY(), var10.getZ(), var11, 0.0F);
               this.randomizePose(var10, var3.random);
               ((ServerLevel)var9).addFreshEntityWithPassengers(var10);
               var3.playSound(null, var10.getX(), var10.getY(), var10.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
               var10.gameEvent(GameEvent.ENTITY_PLACE, var1.getPlayer());
            }

            var6.shrink(1);
            return InteractionResult.sidedSuccess(var3.isClientSide);
         } else {
            return InteractionResult.FAIL;
         }
      }
   }

   private void randomizePose(ArmorStand var1, RandomSource var2) {
      Rotations var3 = var1.getHeadPose();
      float var5 = var2.nextFloat() * 5.0F;
      float var6 = var2.nextFloat() * 20.0F - 10.0F;
      Rotations var4 = new Rotations(var3.getX() + var5, var3.getY() + var6, var3.getZ());
      var1.setHeadPose(var4);
      var3 = var1.getBodyPose();
      var5 = var2.nextFloat() * 10.0F - 5.0F;
      var4 = new Rotations(var3.getX(), var3.getY() + var5, var3.getZ());
      var1.setBodyPose(var4);
   }
}
