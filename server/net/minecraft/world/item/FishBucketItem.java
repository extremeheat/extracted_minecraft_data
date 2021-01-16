package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;

public class FishBucketItem extends BucketItem {
   private final EntityType<?> type;

   public FishBucketItem(EntityType<?> var1, Fluid var2, Item.Properties var3) {
      super(var2, var3);
      this.type = var1;
   }

   public void checkExtraContent(Level var1, ItemStack var2, BlockPos var3) {
      if (var1 instanceof ServerLevel) {
         this.spawn((ServerLevel)var1, var2, var3);
      }

   }

   protected void playEmptySound(@Nullable Player var1, LevelAccessor var2, BlockPos var3) {
      var2.playSound(var1, var3, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
   }

   private void spawn(ServerLevel var1, ItemStack var2, BlockPos var3) {
      Entity var4 = this.type.spawn(var1, var2, (Player)null, var3, MobSpawnType.BUCKET, true, false);
      if (var4 != null) {
         ((AbstractFish)var4).setFromBucket(true);
      }

   }
}
