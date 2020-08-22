package net.minecraft.world.entity.animal.horse;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class SkeletonTrapGoal extends Goal {
   private final SkeletonHorse horse;

   public SkeletonTrapGoal(SkeletonHorse var1) {
      this.horse = var1;
   }

   public boolean canUse() {
      return this.horse.level.hasNearbyAlivePlayer(this.horse.getX(), this.horse.getY(), this.horse.getZ(), 10.0D);
   }

   public void tick() {
      DifficultyInstance var1 = this.horse.level.getCurrentDifficultyAt(new BlockPos(this.horse));
      this.horse.setTrap(false);
      this.horse.setTamed(true);
      this.horse.setAge(0);
      ((ServerLevel)this.horse.level).addGlobalEntity(new LightningBolt(this.horse.level, this.horse.getX(), this.horse.getY(), this.horse.getZ(), true));
      Skeleton var2 = this.createSkeleton(var1, this.horse);
      var2.startRiding(this.horse);

      for(int var3 = 0; var3 < 3; ++var3) {
         AbstractHorse var4 = this.createHorse(var1);
         Skeleton var5 = this.createSkeleton(var1, var4);
         var5.startRiding(var4);
         var4.push(this.horse.getRandom().nextGaussian() * 0.5D, 0.0D, this.horse.getRandom().nextGaussian() * 0.5D);
      }

   }

   private AbstractHorse createHorse(DifficultyInstance var1) {
      SkeletonHorse var2 = (SkeletonHorse)EntityType.SKELETON_HORSE.create(this.horse.level);
      var2.finalizeSpawn(this.horse.level, var1, MobSpawnType.TRIGGERED, (SpawnGroupData)null, (CompoundTag)null);
      var2.setPos(this.horse.getX(), this.horse.getY(), this.horse.getZ());
      var2.invulnerableTime = 60;
      var2.setPersistenceRequired();
      var2.setTamed(true);
      var2.setAge(0);
      var2.level.addFreshEntity(var2);
      return var2;
   }

   private Skeleton createSkeleton(DifficultyInstance var1, AbstractHorse var2) {
      Skeleton var3 = (Skeleton)EntityType.SKELETON.create(var2.level);
      var3.finalizeSpawn(var2.level, var1, MobSpawnType.TRIGGERED, (SpawnGroupData)null, (CompoundTag)null);
      var3.setPos(var2.getX(), var2.getY(), var2.getZ());
      var3.invulnerableTime = 60;
      var3.setPersistenceRequired();
      if (var3.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
         var3.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
      }

      var3.setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem(var3.getRandom(), var3.getMainHandItem(), (int)(5.0F + var1.getSpecialMultiplier() * (float)var3.getRandom().nextInt(18)), false));
      var3.setItemSlot(EquipmentSlot.HEAD, EnchantmentHelper.enchantItem(var3.getRandom(), var3.getItemBySlot(EquipmentSlot.HEAD), (int)(5.0F + var1.getSpecialMultiplier() * (float)var3.getRandom().nextInt(18)), false));
      var3.level.addFreshEntity(var3);
      return var3;
   }
}
