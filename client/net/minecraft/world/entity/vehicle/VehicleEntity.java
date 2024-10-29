package net.minecraft.world.entity.vehicle;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public abstract class VehicleEntity extends Entity {
   protected static final EntityDataAccessor<Integer> DATA_ID_HURT;
   protected static final EntityDataAccessor<Integer> DATA_ID_HURTDIR;
   protected static final EntityDataAccessor<Float> DATA_ID_DAMAGE;

   public VehicleEntity(EntityType<?> var1, Level var2) {
      super(var1, var2);
   }

   public boolean hurtClient(DamageSource var1) {
      return true;
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (this.isRemoved()) {
         return true;
      } else if (this.isInvulnerableToBase(var2)) {
         return false;
      } else {
         boolean var10000;
         label32: {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.markHurt();
            this.setDamage(this.getDamage() + var3 * 10.0F);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, var2.getEntity());
            Entity var6 = var2.getEntity();
            if (var6 instanceof Player) {
               Player var5 = (Player)var6;
               if (var5.getAbilities().instabuild) {
                  var10000 = true;
                  break label32;
               }
            }

            var10000 = false;
         }

         boolean var4 = var10000;
         if ((var4 || !(this.getDamage() > 40.0F)) && !this.shouldSourceDestroy(var2)) {
            if (var4) {
               this.discard();
            }
         } else {
            this.destroy(var1, var2);
         }

         return true;
      }
   }

   boolean shouldSourceDestroy(DamageSource var1) {
      return false;
   }

   public boolean ignoreExplosion(Explosion var1) {
      return var1.getIndirectSourceEntity() instanceof Mob && !var1.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
   }

   public void destroy(ServerLevel var1, Item var2) {
      this.kill(var1);
      if (var1.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         ItemStack var3 = new ItemStack(var2);
         var3.set(DataComponents.CUSTOM_NAME, this.getCustomName());
         this.spawnAtLocation(var1, var3);
      }
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_ID_HURT, 0);
      var1.define(DATA_ID_HURTDIR, 1);
      var1.define(DATA_ID_DAMAGE, 0.0F);
   }

   public void setHurtTime(int var1) {
      this.entityData.set(DATA_ID_HURT, var1);
   }

   public void setHurtDir(int var1) {
      this.entityData.set(DATA_ID_HURTDIR, var1);
   }

   public void setDamage(float var1) {
      this.entityData.set(DATA_ID_DAMAGE, var1);
   }

   public float getDamage() {
      return (Float)this.entityData.get(DATA_ID_DAMAGE);
   }

   public int getHurtTime() {
      return (Integer)this.entityData.get(DATA_ID_HURT);
   }

   public int getHurtDir() {
      return (Integer)this.entityData.get(DATA_ID_HURTDIR);
   }

   protected void destroy(ServerLevel var1, DamageSource var2) {
      this.destroy(var1, this.getDropItem());
   }

   public int getDimensionChangingDelay() {
      return 10;
   }

   protected abstract Item getDropItem();

   static {
      DATA_ID_HURT = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.INT);
      DATA_ID_HURTDIR = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.INT);
      DATA_ID_DAMAGE = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.FLOAT);
   }
}
