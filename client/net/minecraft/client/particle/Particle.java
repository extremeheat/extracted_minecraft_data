package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RewindableStream;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public abstract class Particle {
   private static final AABB INITIAL_AABB = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   protected final Level level;
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
   public static double xOff;
   public static double yOff;
   public static double zOff;

   protected Particle(Level var1, double var2, double var4, double var6) {
      super();
      this.bb = INITIAL_AABB;
      this.hasPhysics = true;
      this.bbWidth = 0.6F;
      this.bbHeight = 1.8F;
      this.random = new Random();
      this.rCol = 1.0F;
      this.gCol = 1.0F;
      this.bCol = 1.0F;
      this.alpha = 1.0F;
      this.level = var1;
      this.setSize(0.2F, 0.2F);
      this.setPos(var2, var4, var6);
      this.xo = var2;
      this.yo = var4;
      this.zo = var6;
      this.lifetime = (int)(4.0F / (this.random.nextFloat() * 0.9F + 0.1F));
   }

   public Particle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6);
      this.xd = var8 + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      this.yd = var10 + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      this.zd = var12 + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      float var14 = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
      float var15 = Mth.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
      this.xd = this.xd / (double)var15 * (double)var14 * 0.4000000059604645D;
      this.yd = this.yd / (double)var15 * (double)var14 * 0.4000000059604645D + 0.10000000149011612D;
      this.zd = this.zd / (double)var15 * (double)var14 * 0.4000000059604645D;
   }

   public Particle setPower(float var1) {
      this.xd *= (double)var1;
      this.yd = (this.yd - 0.10000000149011612D) * (double)var1 + 0.10000000149011612D;
      this.zd *= (double)var1;
      return this;
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
         this.yd -= 0.04D * (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.9800000190734863D;
         this.yd *= 0.9800000190734863D;
         this.zd *= 0.9800000190734863D;
         if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }

   public abstract void render(BufferBuilder var1, Camera var2, float var3, float var4, float var5, float var6, float var7, float var8);

   public abstract ParticleRenderType getRenderType();

   public String toString() {
      return this.getClass().getSimpleName() + ", Pos (" + this.x + "," + this.y + "," + this.z + "), RGBA (" + this.rCol + "," + this.gCol + "," + this.bCol + "," + this.alpha + "), Age " + this.age;
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
      this.x = var1;
      this.y = var3;
      this.z = var5;
      float var7 = this.bbWidth / 2.0F;
      float var8 = this.bbHeight;
      this.setBoundingBox(new AABB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)var8, var5 + (double)var7));
   }

   public void move(double var1, double var3, double var5) {
      double var7 = var1;
      double var9 = var3;
      if (this.hasPhysics && (var1 != 0.0D || var3 != 0.0D || var5 != 0.0D)) {
         Vec3 var13 = Entity.collideBoundingBoxHeuristically((Entity)null, new Vec3(var1, var3, var5), this.getBoundingBox(), this.level, CollisionContext.empty(), new RewindableStream(Stream.empty()));
         var1 = var13.x;
         var3 = var13.y;
         var5 = var13.z;
      }

      if (var1 != 0.0D || var3 != 0.0D || var5 != 0.0D) {
         this.setBoundingBox(this.getBoundingBox().move(var1, var3, var5));
         this.setLocationFromBoundingbox();
      }

      this.onGround = var3 != var3 && var9 < 0.0D;
      if (var7 != var1) {
         this.xd = 0.0D;
      }

      if (var5 != var5) {
         this.zd = 0.0D;
      }

   }

   protected void setLocationFromBoundingbox() {
      AABB var1 = this.getBoundingBox();
      this.x = (var1.minX + var1.maxX) / 2.0D;
      this.y = var1.minY;
      this.z = (var1.minZ + var1.maxZ) / 2.0D;
   }

   protected int getLightColor(float var1) {
      BlockPos var2 = new BlockPos(this.x, this.y, this.z);
      return this.level.hasChunkAt(var2) ? this.level.getLightColor(var2, 0) : 0;
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
}
