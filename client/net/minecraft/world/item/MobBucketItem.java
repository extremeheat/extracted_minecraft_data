package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.Nullable;

public class MobBucketItem extends BucketItem {
   private final EntityType<? extends Mob> type;
   private final SoundEvent emptySound;

   public MobBucketItem(EntityType<? extends Mob> var1, Fluid var2, SoundEvent var3, Item.Properties var4) {
      super(var2, var4);
      this.type = var1;
      this.emptySound = var3;
   }

   public void checkExtraContent(@Nullable LivingEntity var1, Level var2, ItemStack var3, BlockPos var4) {
      if (var2 instanceof ServerLevel) {
         this.spawn((ServerLevel)var2, var3, var4);
         var2.gameEvent(var1, GameEvent.ENTITY_PLACE, var4);
      }

   }

   protected void playEmptySound(@Nullable LivingEntity var1, LevelAccessor var2, BlockPos var3) {
      var2.playSound(var1, var3, this.emptySound, SoundSource.NEUTRAL, 1.0F, 1.0F);
   }

   private void spawn(ServerLevel var1, ItemStack var2, BlockPos var3) {
      Mob var4 = this.type.create(var1, EntityType.createDefaultStackConfig(var1, var2, (LivingEntity)null), var3, EntitySpawnReason.BUCKET, true, false);
      if (var4 instanceof Bucketable var5) {
         CustomData var6 = (CustomData)var2.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY);
         var5.loadFromBucketTag(var6.copyTag());
         var5.setFromBucket(true);
      }

      if (var4 != null) {
         var1.addFreshEntityWithPassengers(var4);
         var4.playAmbientSound();
      }

   }
}
