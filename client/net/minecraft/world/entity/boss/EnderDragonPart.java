package net.minecraft.world.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public class EnderDragonPart extends Entity {
   public final EnderDragon parentMob;
   public final String name;
   private final EntityDimensions size;

   public EnderDragonPart(EnderDragon var1, String var2, float var3, float var4) {
      super(var1.getType(), var1.level);
      this.size = EntityDimensions.scalable(var3, var4);
      this.refreshDimensions();
      this.parentMob = var1;
      this.name = var2;
   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurt(DamageSource var1, float var2) {
      return this.isInvulnerableTo(var1) ? false : this.parentMob.hurt(this, var1, var2);
   }

   public boolean is(Entity var1) {
      return this == var1 || this.parentMob == var1;
   }

   public Packet<?> getAddEntityPacket() {
      throw new UnsupportedOperationException();
   }

   public EntityDimensions getDimensions(Pose var1) {
      return this.size;
   }

   public boolean shouldBeSaved() {
      return false;
   }
}
