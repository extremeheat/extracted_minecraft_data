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

   public void setFlyingSpeed(float var1) {
      this.flyingSpeed = var1;
   }

   public float getWalkingSpeed() {
      return this.walkingSpeed;
   }

   public void setWalkingSpeed(float var1) {
      this.walkingSpeed = var1;
   }

   public Packed pack() {
      return new Packed(this.invulnerable, this.flying, this.mayfly, this.instabuild, this.mayBuild, this.flyingSpeed, this.walkingSpeed);
   }

   public void apply(Packed var1) {
      this.invulnerable = var1.invulnerable;
      this.flying = var1.flying;
      this.mayfly = var1.mayFly;
      this.instabuild = var1.instabuild;
      this.mayBuild = var1.mayBuild;
      this.flyingSpeed = var1.flyingSpeed;
      this.walkingSpeed = var1.walkingSpeed;
   }

   public static record Packed(boolean invulnerable, boolean flying, boolean mayFly, boolean instabuild, boolean mayBuild, float flyingSpeed, float walkingSpeed) {
      final boolean invulnerable;
      final boolean flying;
      final boolean mayFly;
      final boolean instabuild;
      final boolean mayBuild;
      final float flyingSpeed;
      final float walkingSpeed;
      public static final Codec<Packed> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.BOOL.fieldOf("invulnerable").orElse(false).forGetter(Packed::invulnerable), Codec.BOOL.fieldOf("flying").orElse(false).forGetter(Packed::flying), Codec.BOOL.fieldOf("mayfly").orElse(false).forGetter(Packed::mayFly), Codec.BOOL.fieldOf("instabuild").orElse(false).forGetter(Packed::instabuild), Codec.BOOL.fieldOf("mayBuild").orElse(true).forGetter(Packed::mayBuild), Codec.FLOAT.fieldOf("flySpeed").orElse(0.05F).forGetter(Packed::flyingSpeed), Codec.FLOAT.fieldOf("walkSpeed").orElse(0.1F).forGetter(Packed::walkingSpeed)).apply(var0, Packed::new));

      public Packed(boolean var1, boolean var2, boolean var3, boolean var4, boolean var5, float var6, float var7) {
         super();
         this.invulnerable = var1;
         this.flying = var2;
         this.mayFly = var3;
         this.instabuild = var4;
         this.mayBuild = var5;
         this.flyingSpeed = var6;
         this.walkingSpeed = var7;
      }
   }
}
