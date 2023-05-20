package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkCatalystBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;

public class SculkCatalystBlockEntity extends BlockEntity implements GameEventListener {
   private final BlockPositionSource blockPosSource = new BlockPositionSource(this.worldPosition);
   private final SculkSpreader sculkSpreader = SculkSpreader.createLevelSpreader();

   public SculkCatalystBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SCULK_CATALYST, var1, var2);
   }

   @Override
   public PositionSource getListenerSource() {
      return this.blockPosSource;
   }

   @Override
   public int getListenerRadius() {
      return 8;
   }

   @Override
   public GameEventListener.DeliveryMode getDeliveryMode() {
      return GameEventListener.DeliveryMode.BY_DISTANCE;
   }

   @Override
   public boolean handleGameEvent(ServerLevel var1, GameEvent var2, GameEvent.Context var3, Vec3 var4) {
      if (var2 == GameEvent.ENTITY_DIE) {
         Entity var6 = var3.sourceEntity();
         if (var6 instanceof LivingEntity var5) {
            if (!((LivingEntity)var5).wasExperienceConsumed()) {
               int var7 = ((LivingEntity)var5).getExperienceReward();
               if (((LivingEntity)var5).shouldDropExperience() && var7 > 0) {
                  this.sculkSpreader.addCursors(BlockPos.containing(var4.relative(Direction.UP, 0.5)), var7);
                  this.tryAwardItSpreadsAdvancement((LivingEntity)var5);
               }

               ((LivingEntity)var5).skipDropExperience();
               SculkCatalystBlock.bloom(var1, this.worldPosition, this.getBlockState(), var1.getRandom());
            }

            return true;
         }
      }

      return false;
   }

   private void tryAwardItSpreadsAdvancement(LivingEntity var1) {
      LivingEntity var2 = var1.getLastHurtByMob();
      if (var2 instanceof ServerPlayer var3) {
         DamageSource var4 = var1.getLastDamageSource() == null ? this.level.damageSources().playerAttack((Player)var3) : var1.getLastDamageSource();
         CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.trigger((ServerPlayer)var3, var1, var4);
      }
   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, SculkCatalystBlockEntity var3) {
      var3.sculkSpreader.updateCursors(var0, var1, var0.getRandom(), true);
   }

   @Override
   public void load(CompoundTag var1) {
      super.load(var1);
      this.sculkSpreader.load(var1);
   }

   @Override
   protected void saveAdditional(CompoundTag var1) {
      this.sculkSpreader.save(var1);
      super.saveAdditional(var1);
   }

   @VisibleForTesting
   public SculkSpreader getSculkSpreader() {
      return this.sculkSpreader;
   }
}
