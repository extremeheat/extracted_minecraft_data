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

public class SculkCatalystBlockEntity extends BlockEntity implements GameEventListener {
   private final BlockPositionSource blockPosSource = new BlockPositionSource(this.worldPosition);
   private final SculkSpreader sculkSpreader = SculkSpreader.createLevelSpreader();

   public SculkCatalystBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SCULK_CATALYST, var1, var2);
   }

   @Override
   public boolean handleEventsImmediately() {
      return true;
   }

   @Override
   public PositionSource getListenerSource() {
      return this.blockPosSource;
   }

   @Override
   public int getListenerRadius() {
      return 8;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean handleGameEvent(ServerLevel var1, GameEvent.Message var2) {
      if (this.isRemoved()) {
         return false;
      } else {
         GameEvent.Context var3 = var2.context();
         if (var2.gameEvent() == GameEvent.ENTITY_DIE) {
            Entity var5 = var3.sourceEntity();
            if (var5 instanceof LivingEntity var4) {
               if (!var4.wasExperienceConsumed()) {
                  int var9 = var4.getExperienceReward();
                  if (var4.shouldDropExperience() && var9 > 0) {
                     this.sculkSpreader.addCursors(new BlockPos(var2.source().relative(Direction.UP, 0.5)), var9);
                     LivingEntity var6 = var4.getLastHurtByMob();
                     if (var6 instanceof ServerPlayer var7) {
                        DamageSource var8 = var4.getLastDamageSource() == null ? DamageSource.playerAttack((Player)var7) : var4.getLastDamageSource();
                        CriteriaTriggers.KILL_MOB_NEAR_SCULK_CATALYST.trigger((ServerPlayer)var7, var3.sourceEntity(), var8);
                     }
                  }

                  var4.skipDropExperience();
                  SculkCatalystBlock.bloom(var1, this.worldPosition, this.getBlockState(), var1.getRandom());
               }

               return true;
            }
         }

         return false;
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
