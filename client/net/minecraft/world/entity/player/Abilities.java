package net.minecraft.world.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

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

   public float getFlyingSpeed() {
      return this.flyingSpeed;
   }

   public void setFlyingSpeed(final float value) {
      this.flyingSpeed = value;
   }

   public float getWalkingSpeed() {
      return this.walkingSpeed;
   }

   public void setWalkingSpeed(final float value) {
      this.walkingSpeed = value;
   }

   public Packed pack() {
      return new Packed(this.invulnerable, this.flying, this.mayfly, this.instabuild, this.mayBuild, this.flyingSpeed, this.walkingSpeed);
   }

   public void apply(final Packed packed) {
      this.invulnerable = packed.invulnerable;
      this.flying = packed.flying;
      this.mayfly = packed.mayFly;
      this.instabuild = packed.instabuild;
      this.mayBuild = packed.mayBuild;
      this.flyingSpeed = packed.flyingSpeed;
      this.walkingSpeed = packed.walkingSpeed;
   }

   public static record Packed(boolean invulnerable, boolean flying, boolean mayFly, boolean instabuild, boolean mayBuild, float flyingSpeed, float walkingSpeed) {
      public static final Codec<Packed> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.BOOL.fieldOf("invulnerable").orElse(false).forGetter(Packed::invulnerable), Codec.BOOL.fieldOf("flying").orElse(false).forGetter(Packed::flying), Codec.BOOL.fieldOf("mayfly").orElse(false).forGetter(Packed::mayFly), Codec.BOOL.fieldOf("instabuild").orElse(false).forGetter(Packed::instabuild), Codec.BOOL.fieldOf("mayBuild").orElse(true).forGetter(Packed::mayBuild), Codec.FLOAT.fieldOf("flySpeed").orElse(0.05F).forGetter(Packed::flyingSpeed), Codec.FLOAT.fieldOf("walkSpeed").orElse(0.1F).forGetter(Packed::walkingSpeed)).apply(i, Packed::new));

      public Packed {
         super();
      }
   }
}
