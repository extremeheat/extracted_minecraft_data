package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class Particle {
   private static final AABB INITIAL_AABB = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0D);
   protected final ClientLevel level;
   // $FF: renamed from: xo double
   protected double field_107;
   // $FF: renamed from: yo double
   protected double field_108;
   // $FF: renamed from: zo double
   protected double field_109;
   // $FF: renamed from: x double
   protected double field_110;
   // $FF: renamed from: y double
   protected double field_111;
   // $FF: renamed from: z double
   protected double field_112;
   // $FF: renamed from: xd double
   protected double field_113;
   // $FF: renamed from: yd double
   protected double field_114;
   // $FF: renamed from: zd double
   protected double field_115;
   // $FF: renamed from: bb net.minecraft.world.phys.AABB
   private AABB field_116;
   protected boolean onGround;
   protected boolean hasPhysics;
   private boolean stoppedByCollision;
   protected boolean removed;
   protected float bbWidth;
   protected float bbHeight;
   protected final Random random;
   protected int age;
   protected int lifetime;
   protected float gravity;
   protected float rCol;
   protected float gCol;
   protected float bCol;
   protected float alpha;
   protected float roll;
   protected float oRoll;
   protected float friction;
   protected boolean speedUpWhenYMotionIsBlocked;

   protected Particle(ClientLevel var1, double var2, double var4, double var6) {
      super();
      this.field_116 = INITIAL_AABB;
      this.hasPhysics = true;
      this.bbWidth = 0.6F;
      this.bbHeight = 1.8F;
      this.random = new Random();
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.alpha = 1.0F;
      this.friction = 0.98F;
      this.speedUpWhenYMotionIsBlocked = false;
      this.level = var1;
      this.setSize(0.2F, 0.2F);
      this.setPos(var2, var4, var6);
      this.field_107 = var2;
      this.field_108 = var4;
      this.field_109 = var6;
      this.lifetime = (int)(4.0F / (this.random.nextFloat() * 0.9F + 0.1F));
   }

   public Particle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6);
      this.field_113 = var8 + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      this.field_114 = var10 + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      this.field_115 = var12 + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      double var14 = (Math.random() + Math.random() + 1.0D) * 0.15000000596046448D;
      double var16 = Math.sqrt(this.field_113 * this.field_113 + this.field_114 * this.field_114 + this.field_115 * this.field_115);
      this.field_113 = this.field_113 / var16 * var14 * 0.4000000059604645D;
      this.field_114 = this.field_114 / var16 * var14 * 0.4000000059604645D + 0.10000000149011612D;
      this.field_115 = this.field_115 / var16 * var14 * 0.4000000059604645D;
   }

   public Particle setPower(float var1) {
      this.field_113 *= (double)var1;
      this.field_114 = (this.field_114 - 0.10000000149011612D) * (double)var1 + 0.10000000149011612D;
      this.field_115 *= (double)var1;
      return this;
   }

   public void setParticleSpeed(double var1, double var3, double var5) {
      this.field_113 = var1;
      this.field_114 = var3;
      this.field_115 = var5;
   }

   public Particle scale(float var1) {
      this.setSize(0.2F * var1, 0.2F * var1);
      return this;
   }

   public void setColor(float var1, float var2, float var3) {
      this.rCol = var1;
      this.gCol = var2;
      this.bCol = var3;
   }

   protected void setAlpha(float var1) {
      this.alpha = var1;
   }

   public void setLifetime(int var1) {
      this.lifetime = var1;
   }

   public int getLifetime() {
      return this.lifetime;
   }

   public void tick() {
      this.field_107 = this.field_110;
      this.field_108 = this.field_111;
      this.field_109 = this.field_112;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.field_114 -= 0.04D * (double)this.gravity;
         this.move(this.field_113, this.field_114, this.field_115);
         if (this.speedUpWhenYMotionIsBlocked && this.field_111 == this.field_108) {
            this.field_113 *= 1.1D;
            this.field_115 *= 1.1D;
         }

         this.field_113 *= (double)this.friction;
         this.field_114 *= (double)this.friction;
         this.field_115 *= (double)this.friction;
         if (this.onGround) {
            this.field_113 *= 0.699999988079071D;
            this.field_115 *= 0.699999988079071D;
         }

      }
   }

   public abstract void render(VertexConsumer var1, Camera var2, float var3);

   public abstract ParticleRenderType getRenderType();

   public String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + ", Pos (" + this.field_110 + "," + this.field_111 + "," + this.field_112 + "), RGBA (" + this.rCol + "," + this.gCol + "," + this.bCol + "," + this.alpha + "), Age " + this.age;
   }

   public void remove() {
      this.removed = true;
   }

   protected void setSize(float var1, float var2) {
      if (var1 != this.bbWidth || var2 != this.bbHeight) {
         this.bbWidth = var1;
         this.bbHeight = var2;
         AABB var3 = this.getBoundingBox();
         double var4 = (var3.minX + var3.maxX - (double)var1) / 2.0D;
         double var6 = (var3.minZ + var3.maxZ - (double)var1) / 2.0D;
         this.setBoundingBox(new AABB(var4, var3.minY, var6, var4 + (double)this.bbWidth, var3.minY + (double)this.bbHeight, var6 + (double)this.bbWidth));
      }

   }

   public void setPos(double var1, double var3, double var5) {
      this.field_110 = var1;
      this.field_111 = var3;
      this.field_112 = var5;
      float var7 = this.bbWidth / 2.0F;
      float var8 = this.bbHeight;
      this.setBoundingBox(new AABB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)var8, var5 + (double)var7));
   }

   public void move(double var1, double var3, double var5) {
      if (!this.stoppedByCollision) {
         double var7 = var1;
         double var9 = var3;
         if (this.hasPhysics && (var1 != 0.0D || var3 != 0.0D || var5 != 0.0D) && var1 * var1 + var3 * var3 + var5 * var5 < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
            Vec3 var13 = Entity.collideBoundingBox((Entity)null, new Vec3(var1, var3, var5), this.getBoundingBox(), this.level, List.of());
            var1 = var13.field_414;
            var3 = var13.field_415;
            var5 = var13.field_416;
         }

         if (var1 != 0.0D || var3 != 0.0D || var5 != 0.0D) {
            this.setBoundingBox(this.getBoundingBox().move(var1, var3, var5));
            this.setLocationFromBoundingbox();
         }

         if (Math.abs(var3) >= 9.999999747378752E-6D && Math.abs(var3) < 9.999999747378752E-6D) {
            this.stoppedByCollision = true;
         }

         this.onGround = var3 != var3 && var9 < 0.0D;
         if (var7 != var1) {
            this.field_113 = 0.0D;
         }

         if (var5 != var5) {
            this.field_115 = 0.0D;
         }

      }
   }

   protected void setLocationFromBoundingbox() {
      AABB var1 = this.getBoundingBox();
      this.field_110 = (var1.minX + var1.maxX) / 2.0D;
      this.field_111 = var1.minY;
      this.field_112 = (var1.minZ + var1.maxZ) / 2.0D;
   }

   protected int getLightColor(float var1) {
      BlockPos var2 = new BlockPos(this.field_110, this.field_111, this.field_112);
      return this.level.hasChunkAt(var2) ? LevelRenderer.getLightColor(this.level, var2) : 0;
   }

   public boolean isAlive() {
      return !this.removed;
   }

   public AABB getBoundingBox() {
      return this.field_116;
   }

   public void setBoundingBox(AABB var1) {
      this.field_116 = var1;
   }

   public Optional<ParticleGroup> getParticleGroup() {
      return Optional.empty();
   }
}
