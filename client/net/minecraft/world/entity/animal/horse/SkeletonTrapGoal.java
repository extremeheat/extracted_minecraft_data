package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;

public class SkeletonTrapGoal extends Goal {
   private final SkeletonHorse horse;

   public SkeletonTrapGoal(SkeletonHorse var1) {
      super();
      this.horse = var1;
   }

   public boolean canUse() {
      return this.horse.level().hasNearbyAlivePlayer(this.horse.getX(), this.horse.getY(), this.horse.getZ(), 10.0);
   }

   public void tick() {
      ServerLevel var1 = (ServerLevel)this.horse.level();
      DifficultyInstance var2 = var1.getCurrentDifficultyAt(this.horse.blockPosition());
      this.horse.setTrap(false);
      this.horse.setTamed(true);
      this.horse.setAge(0);
      LightningBolt var3 = (LightningBolt)EntityType.LIGHTNING_BOLT.create(var1);
      if (var3 != null) {
         var3.moveTo(this.horse.getX(), this.horse.getY(), this.horse.getZ());
         var3.setVisualOnly(true);
         var1.addFreshEntity(var3);
         Skeleton var4 = this.createSkeleton(var2, this.horse);
         if (var4 != null) {
            var4.startRiding(this.horse);
            var1.addFreshEntityWithPassengers(var4);

            for(int var5 = 0; var5 < 3; ++var5) {
               AbstractHorse var6 = this.createHorse(var2);
               if (var6 != null) {
                  Skeleton var7 = this.createSkeleton(var2, var6);
                  if (var7 != null) {
                     var7.startRiding(var6);
                     var6.push(this.horse.getRandom().triangle(0.0, 1.1485), 0.0, this.horse.getRandom().triangle(0.0, 1.1485));
                     var1.addFreshEntityWithPassengers(var6);
                  }
               }
            }

         }
      }
   }

   @Nullable
   private AbstractHorse createHorse(DifficultyInstance var1) {
      SkeletonHorse var2 = (SkeletonHorse)EntityType.SKELETON_HORSE.create(this.horse.level());
      if (var2 != null) {
         var2.finalizeSpawn((ServerLevel)this.horse.level(), var1, MobSpawnType.TRIGGERED, (SpawnGroupData)null);
         var2.setPos(this.horse.getX(), this.horse.getY(), this.horse.getZ());
         var2.invulnerableTime = 60;
         var2.setPersistenceRequired();
         var2.setTamed(true);
         var2.setAge(0);
      }

      return var2;
   }

   @Nullable
   private Skeleton createSkeleton(DifficultyInstance var1, AbstractHorse var2) {
      Skeleton var3 = (Skeleton)EntityType.SKELETON.create(var2.level());
      if (var3 != null) {
         var3.finalizeSpawn((ServerLevel)var2.level(), var1, MobSpawnType.TRIGGERED, (SpawnGroupData)null);
         var3.setPos(var2.getX(), var2.getY(), var2.getZ());
         var3.invulnerableTime = 60;
         var3.setPersistenceRequired();
         if (var3.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            var3.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
         }

         this.enchant(var3, EquipmentSlot.MAINHAND, var1);
         this.enchant(var3, EquipmentSlot.HEAD, var1);
      }

      return var3;
   }

   private void enchant(Skeleton var1, EquipmentSlot var2, DifficultyInstance var3) {
      ItemStack var4 = var1.getItemBySlot(var2);
      var4.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
      EnchantmentHelper.enchantItemFromProvider(var4, var1.level().registryAccess(), VanillaEnchantmentProviders.MOB_SPAWN_EQUIPMENT, var3, var1.getRandom());
      var1.setItemSlot(var2, var4);
   }
}
