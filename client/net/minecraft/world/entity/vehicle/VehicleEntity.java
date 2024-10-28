package net.minecraft.world.entity.vehicle;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

   public boolean hurt(DamageSource var1, float var2) {
      if (!this.level().isClientSide && !this.isRemoved()) {
         if (this.isInvulnerableTo(var1)) {
            return false;
         } else {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.markHurt();
            this.setDamage(this.getDamage() + var2 * 10.0F);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, var1.getEntity());
            boolean var3 = var1.getEntity() instanceof Player && ((Player)var1.getEntity()).getAbilities().instabuild;
            if ((var3 || !(this.getDamage() > 40.0F)) && !this.shouldSourceDestroy(var1)) {
               if (var3) {
                  this.discard();
               }
            } else {
               this.destroy(var1);
            }

            return true;
         }
      } else {
         return true;
      }
   }

   boolean shouldSourceDestroy(DamageSource var1) {
      return false;
   }

   public void destroy(Item var1) {
      this.kill();
      if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         ItemStack var2 = new ItemStack(var1);
         var2.set(DataComponents.CUSTOM_NAME, this.getCustomName());
         this.spawnAtLocation(var2);
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

   protected void destroy(DamageSource var1) {
      this.destroy(this.getDropItem());
   }

   abstract Item getDropItem();

   static {
      DATA_ID_HURT = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.INT);
      DATA_ID_HURTDIR = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.INT);
      DATA_ID_DAMAGE = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.FLOAT);
   }
}
