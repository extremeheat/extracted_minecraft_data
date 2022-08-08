package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class Particle {
   private static final AABB INITIAL_AABB = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   private static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);
   protected final ClientLevel level;
   protected double xo;
   protected double yo;
   protected double zo;
   protected double x;
   protected double y;
   protected double z;
   protected double xd;
   protected double yd;
   protected double zd;
   private AABB bb;
   protected boolean onGround;
   protected boolean hasPhysics;
   private boolean stoppedByCollision;
   protected boolean removed;
   protected float bbWidth;
   protected float bbHeight;
   protected final RandomSource random;
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
      this.bb = INITIAL_AABB;
      this.hasPhysics = true;
      this.bbWidth = 0.6F;
      this.bbHeight = 1.8F;
      this.random = RandomSource.create();
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.alpha = 1.0F;
      this.friction = 0.98F;
      this.speedUpWhenYMotionIsBlocked = false;
      this.level = var1;
      this.setSize(0.2F, 0.2F);
      this.setPos(var2, var4, var6);
      this.xo = var2;
      this.yo = var4;
      this.zo = var6;
      this.lifetime = (int)(4.0F / (this.random.nextFloat() * 0.9F + 0.1F));
   }

   public Particle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6);
      this.xd = var8 + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
      this.yd = var10 + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
      this.zd = var12 + (Math.random() * 2.0 - 1.0) * 0.4000000059604645;
      double var14 = (Math.random() + Math.random() + 1.0) * 0.15000000596046448;
      double var16 = Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
      this.xd = this.xd / var16 * var14 * 0.4000000059604645;
      this.yd = this.yd / var16 * var14 * 0.4000000059604645 + 0.10000000149011612;
      this.zd = this.zd / var16 * var14 * 0.4000000059604645;
   }

   public Particle setPower(float var1) {
      this.xd *= (double)var1;
      this.yd = (this.yd - 0.10000000149011612) * (double)var1 + 0.10000000149011612;
      this.zd *= (double)var1;
      return this;
   }

   public void setParticleSpeed(double var1, double var3, double var5) {
      this.xd = var1;
      this.yd = var3;
      this.zd = var5;
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
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.yd -= 0.04 * (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
         }

         this.xd *= (double)this.friction;
         this.yd *= (double)this.friction;
         this.zd *= (double)this.friction;
         if (this.onGround) {
            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
         }

      }
   }

   public abstract void render(VertexConsumer var1, Camera var2, float var3);

   public abstract ParticleRenderType getRenderType();

   public String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + ", Pos (" + this.x + "," + this.y + "," + this.z + "), RGBA (" + this.rCol + "," + this.gCol + "," + this.bCol + "," + this.alpha + "), Age " + this.age;
   }

   public void remove() {
      this.removed = true;
   }

   protected void setSize(float var1, float var2) {
      if (var1 != this.bbWidth || var2 != this.bbHeight) {
         this.bbWidth = var1;
         this.bbHeight = var2;
         AABB var3 = this.getBoundingBox();
         double var4 = (var3.minX + var3.maxX - (double)var1) / 2.0;
         double var6 = (var3.minZ + var3.maxZ - (double)var1) / 2.0;
         this.setBoundingBox(new AABB(var4, var3.minY, var6, var4 + (double)this.bbWidth, var3.minY + (double)this.bbHeight, var6 + (double)this.bbWidth));
      }

   }

   public void setPos(double var1, double var3, double var5) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
      float var7 = this.bbWidth / 2.0F;
      float var8 = this.bbHeight;
      this.setBoundingBox(new AABB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)var8, var5 + (double)var7));
   }

   public void move(double var1, double var3, double var5) {
      if (!this.stoppedByCollision) {
         double var7 = var1;
         double var9 = var3;
         if (this.hasPhysics && (var1 != 0.0 || var3 != 0.0 || var5 != 0.0) && var1 * var1 + var3 * var3 + var5 * var5 < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
            Vec3 var13 = Entity.collideBoundingBox((Entity)null, new Vec3(var1, var3, var5), this.getBoundingBox(), this.level, List.of());
            var1 = var13.x;
            var3 = var13.y;
            var5 = var13.z;
         }

         if (var1 != 0.0 || var3 != 0.0 || var5 != 0.0) {
            this.setBoundingBox(this.getBoundingBox().move(var1, var3, var5));
            this.setLocationFromBoundingbox();
         }

         if (Math.abs(var3) >= 9.999999747378752E-6 && Math.abs(var3) < 9.999999747378752E-6) {
            this.stoppedByCollision = true;
         }

         this.onGround = var3 != var3 && var9 < 0.0;
         if (var7 != var1) {
            this.xd = 0.0;
         }

         if (var5 != var5) {
            this.zd = 0.0;
         }

      }
   }

   protected void setLocationFromBoundingbox() {
      AABB var1 = this.getBoundingBox();
      this.x = (var1.minX + var1.maxX) / 2.0;
      this.y = var1.minY;
      this.z = (var1.minZ + var1.maxZ) / 2.0;
   }

   protected int getLightColor(float var1) {
      BlockPos var2 = new BlockPos(this.x, this.y, this.z);
      return this.level.hasChunkAt(var2) ? LevelRenderer.getLightColor(this.level, var2) : 0;
   }

   public boolean isAlive() {
      return !this.removed;
   }

   public AABB getBoundingBox() {
      return this.bb;
   }

   public void setBoundingBox(AABB var1) {
      this.bb = var1;
   }

   public Optional<ParticleGroup> getParticleGroup() {
      return Optional.empty();
   }
}
