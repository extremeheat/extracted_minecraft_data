package net.minecraft.world.entity.player;

import net.minecraft.nbt.CompoundTag;

public class Abilities {
   private static final boolean DEFAULT_INVULNERABLE = false;
   private static final boolean DEFAULY_FLYING = false;
   private static final boolean DEFAULT_MAY_FLY = false;
   private static final boolean DEFAULT_INSTABUILD = false;
   private static final boolean DEFAULT_MAY_BUILD = true;
   private static final float DEFAULT_FLYING_SPEED = 0.05F;
   private static final float DEFAULT_WALKING_SPEED = 0.1F;
   public boolean invulnerable;
   public boolean flying;
   public boolean mayfly;
   public boolean instabuild;
   public boolean mayBuild = true;
   private float flyingSpeed = 0.05F;
   private float walkingSpeed = 0.1F;

   public Abilities() {
      super();
   }

   public void addSaveData(CompoundTag var1) {
      CompoundTag var2 = new CompoundTag();
      var2.putBoolean("invulnerable", this.invulnerable);
      var2.putBoolean("flying", this.flying);
      var2.putBoolean("mayfly", this.mayfly);
      var2.putBoolean("instabuild", this.instabuild);
      var2.putBoolean("mayBuild", this.mayBuild);
      var2.putFloat("flySpeed", this.flyingSpeed);
      var2.putFloat("walkSpeed", this.walkingSpeed);
      var1.put("abilities", var2);
   }

   public void loadSaveData(CompoundTag var1) {
      CompoundTag var2 = var1.getCompoundOrEmpty("abilities");
      this.invulnerable = var2.getBooleanOr("invulnerable", false);
      this.flying = var2.getBooleanOr("flying", false);
      this.mayfly = var2.getBooleanOr("mayfly", false);
      this.instabuild = var2.getBooleanOr("instabuild", false);
      this.flyingSpeed = var2.getFloatOr("flySpeed", 0.05F);
      this.walkingSpeed = var2.getFloatOr("walkSpeed", 0.1F);
      this.mayBuild = var2.getBooleanOr("mayBuild", true);
   }

   public float getFlyingSpeed() {
      return this.flyingSpeed;
   }

   public void setFlyingSpeed(float var1) {
      this.flyingSpeed = var1;
   }

   public float getWalkingSpeed() {
      return this.walkingSpeed;
   }

   public void setWalkingSpeed(float var1) {
      this.walkingSpeed = var1;
   }
}
