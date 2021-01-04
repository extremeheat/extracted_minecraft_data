package net.minecraft.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Camera {
   private boolean initialized;
   private BlockGetter level;
   private Entity entity;
   private Vec3 position;
   private final BlockPos.MutableBlockPos blockPosition;
   private Vec3 forwards;
   private Vec3 up;
   private Vec3 left;
   private float xRot;
   private float yRot;
   private boolean detached;
   private boolean mirror;
   private float eyeHeight;
   private float eyeHeightOld;

   public Camera() {
      super();
      this.position = Vec3.ZERO;
      this.blockPosition = new BlockPos.MutableBlockPos();
   }

   public void setup(BlockGetter var1, Entity var2, boolean var3, boolean var4, float var5) {
      this.initialized = true;
      this.level = var1;
      this.entity = var2;
      this.detached = var3;
      this.mirror = var4;
      this.setRotation(var2.getViewYRot(var5), var2.getViewXRot(var5));
      this.setPosition(Mth.lerp((double)var5, var2.xo, var2.x), Mth.lerp((double)var5, var2.yo, var2.y) + (double)Mth.lerp(var5, this.eyeHeightOld, this.eyeHeight), Mth.lerp((double)var5, var2.zo, var2.z));
      if (var3) {
         if (var4) {
            this.yRot += 180.0F;
            this.xRot += -this.xRot * 2.0F;
            this.recalculateViewVector();
         }

         this.move(-this.getMaxZoom(4.0D), 0.0D, 0.0D);
      } else if (var2 instanceof LivingEntity && ((LivingEntity)var2).isSleeping()) {
         Direction var6 = ((LivingEntity)var2).getBedOrientation();
         this.setRotation(var6 != null ? var6.toYRot() - 180.0F : 0.0F, 0.0F);
         this.move(0.0D, 0.3D, 0.0D);
      }

      GlStateManager.rotatef(this.xRot, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(this.yRot + 180.0F, 0.0F, 1.0F, 0.0F);
   }

   public void tick() {
      if (this.entity != null) {
         this.eyeHeightOld = this.eyeHeight;
         this.eyeHeight += (this.entity.getEyeHeight() - this.eyeHeight) * 0.5F;
      }

   }

   private double getMaxZoom(double var1) {
      for(int var3 = 0; var3 < 8; ++var3) {
         float var4 = (float)((var3 & 1) * 2 - 1);
         float var5 = (float)((var3 >> 1 & 1) * 2 - 1);
         float var6 = (float)((var3 >> 2 & 1) * 2 - 1);
         var4 *= 0.1F;
         var5 *= 0.1F;
         var6 *= 0.1F;
         Vec3 var7 = this.position.add((double)var4, (double)var5, (double)var6);
         Vec3 var8 = new Vec3(this.position.x - this.forwards.x * var1 + (double)var4 + (double)var6, this.position.y - this.forwards.y * var1 + (double)var5, this.position.z - this.forwards.z * var1 + (double)var6);
         BlockHitResult var9 = this.level.clip(new ClipContext(var7, var8, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.entity));
         if (var9.getType() != HitResult.Type.MISS) {
            double var10 = var9.getLocation().distanceTo(this.position);
            if (var10 < var1) {
               var1 = var10;
            }
         }
      }

      return var1;
   }

   protected void move(double var1, double var3, double var5) {
      double var7 = this.forwards.x * var1 + this.up.x * var3 + this.left.x * var5;
      double var9 = this.forwards.y * var1 + this.up.y * var3 + this.left.y * var5;
      double var11 = this.forwards.z * var1 + this.up.z * var3 + this.left.z * var5;
      this.setPosition(new Vec3(this.position.x + var7, this.position.y + var9, this.position.z + var11));
   }

   protected void recalculateViewVector() {
      float var1 = Mth.cos((this.yRot + 90.0F) * 0.017453292F);
      float var2 = Mth.sin((this.yRot + 90.0F) * 0.017453292F);
      float var3 = Mth.cos(-this.xRot * 0.017453292F);
      float var4 = Mth.sin(-this.xRot * 0.017453292F);
      float var5 = Mth.cos((-this.xRot + 90.0F) * 0.017453292F);
      float var6 = Mth.sin((-this.xRot + 90.0F) * 0.017453292F);
      this.forwards = new Vec3((double)(var1 * var3), (double)var4, (double)(var2 * var3));
      this.up = new Vec3((double)(var1 * var5), (double)var6, (double)(var2 * var5));
      this.left = this.forwards.cross(this.up).scale(-1.0D);
   }

   protected void setRotation(float var1, float var2) {
      this.xRot = var2;
      this.yRot = var1;
      this.recalculateViewVector();
   }

   protected void setPosition(double var1, double var3, double var5) {
      this.setPosition(new Vec3(var1, var3, var5));
   }

   protected void setPosition(Vec3 var1) {
      this.position = var1;
      this.blockPosition.set(var1.x, var1.y, var1.z);
   }

   public Vec3 getPosition() {
      return this.position;
   }

   public BlockPos getBlockPosition() {
      return this.blockPosition;
   }

   public float getXRot() {
      return this.xRot;
   }

   public float getYRot() {
      return this.yRot;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public boolean isDetached() {
      return this.detached;
   }

   public FluidState getFluidInCamera() {
      if (!this.initialized) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         FluidState var1 = this.level.getFluidState(this.blockPosition);
         return !var1.isEmpty() && this.position.y >= (double)((float)this.blockPosition.getY() + var1.getHeight(this.level, this.blockPosition)) ? Fluids.EMPTY.defaultFluidState() : var1;
      }
   }

   public final Vec3 getLookVector() {
      return this.forwards;
   }

   public final Vec3 getUpVector() {
      return this.up;
   }

   public void reset() {
      this.level = null;
      this.entity = null;
      this.initialized = false;
   }
}
