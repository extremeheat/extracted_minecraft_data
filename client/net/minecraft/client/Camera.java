package net.minecraft.client;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Camera {
   private boolean initialized;
   private BlockGetter level;
   private Entity entity;
   private Vec3 position = Vec3.ZERO;
   private final BlockPos.MutableBlockPos blockPosition = new BlockPos.MutableBlockPos();
   private final Vector3f forwards = new Vector3f(0.0F, 0.0F, 1.0F);
   private final Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
   private final Vector3f left = new Vector3f(1.0F, 0.0F, 0.0F);
   private float xRot;
   private float yRot;
   private final Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   private boolean detached;
   private float eyeHeight;
   private float eyeHeightOld;
   public static final float FOG_DISTANCE_SCALE = 0.083333336F;

   public Camera() {
      super();
   }

   public void setup(BlockGetter var1, Entity var2, boolean var3, boolean var4, float var5) {
      this.initialized = true;
      this.level = var1;
      this.entity = var2;
      this.detached = var3;
      this.setRotation(var2.getViewYRot(var5), var2.getViewXRot(var5));
      this.setPosition(
         Mth.lerp((double)var5, var2.xo, var2.getX()),
         Mth.lerp((double)var5, var2.yo, var2.getY()) + (double)Mth.lerp(var5, this.eyeHeightOld, this.eyeHeight),
         Mth.lerp((double)var5, var2.zo, var2.getZ())
      );
      if (var3) {
         if (var4) {
            this.setRotation(this.yRot + 180.0F, -this.xRot);
         }

         this.move(-this.getMaxZoom(4.0), 0.0, 0.0);
      } else if (var2 instanceof LivingEntity && ((LivingEntity)var2).isSleeping()) {
         Direction var6 = ((LivingEntity)var2).getBedOrientation();
         this.setRotation(var6 != null ? var6.toYRot() - 180.0F : 0.0F, 0.0F);
         this.move(0.0, 0.3, 0.0);
      }
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
         Vec3 var8 = new Vec3(
            this.position.x - (double)this.forwards.x() * var1 + (double)var4 + (double)var6,
            this.position.y - (double)this.forwards.y() * var1 + (double)var5,
            this.position.z - (double)this.forwards.z() * var1 + (double)var6
         );
         BlockHitResult var9 = this.level.clip(new ClipContext(var7, var8, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this.entity));
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
      double var7 = (double)this.forwards.x() * var1 + (double)this.up.x() * var3 + (double)this.left.x() * var5;
      double var9 = (double)this.forwards.y() * var1 + (double)this.up.y() * var3 + (double)this.left.y() * var5;
      double var11 = (double)this.forwards.z() * var1 + (double)this.up.z() * var3 + (double)this.left.z() * var5;
      this.setPosition(new Vec3(this.position.x + var7, this.position.y + var9, this.position.z + var11));
   }

   protected void setRotation(float var1, float var2) {
      this.xRot = var2;
      this.yRot = var1;
      this.rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
      this.rotation.mul(Vector3f.YP.rotationDegrees(-var1));
      this.rotation.mul(Vector3f.XP.rotationDegrees(var2));
      this.forwards.set(0.0F, 0.0F, 1.0F);
      this.forwards.transform(this.rotation);
      this.up.set(0.0F, 1.0F, 0.0F);
      this.up.transform(this.rotation);
      this.left.set(1.0F, 0.0F, 0.0F);
      this.left.transform(this.rotation);
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

   public Quaternion rotation() {
      return this.rotation;
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

   public Camera.NearPlane getNearPlane() {
      Minecraft var1 = Minecraft.getInstance();
      double var2 = (double)var1.getWindow().getWidth() / (double)var1.getWindow().getHeight();
      double var4 = Math.tan((double)((float)var1.options.fov().get().intValue() * 0.017453292F) / 2.0) * 0.05000000074505806;
      double var6 = var4 * var2;
      Vec3 var8 = new Vec3(this.forwards).scale(0.05000000074505806);
      Vec3 var9 = new Vec3(this.left).scale(var6);
      Vec3 var10 = new Vec3(this.up).scale(var4);
      return new Camera.NearPlane(var8, var9, var10);
   }

   public FogType getFluidInCamera() {
      if (!this.initialized) {
         return FogType.NONE;
      } else {
         FluidState var1 = this.level.getFluidState(this.blockPosition);
         if (var1.is(FluidTags.WATER) && this.position.y < (double)((float)this.blockPosition.getY() + var1.getHeight(this.level, this.blockPosition))) {
            return FogType.WATER;
         } else {
            Camera.NearPlane var2 = this.getNearPlane();

            for(Vec3 var5 : Arrays.asList(var2.forward, var2.getTopLeft(), var2.getTopRight(), var2.getBottomLeft(), var2.getBottomRight())) {
               Vec3 var6 = this.position.add(var5);
               BlockPos var7 = new BlockPos(var6);
               FluidState var8 = this.level.getFluidState(var7);
               if (var8.is(FluidTags.LAVA)) {
                  if (var6.y <= (double)(var8.getHeight(this.level, var7) + (float)var7.getY())) {
                     return FogType.LAVA;
                  }
               } else {
                  BlockState var9 = this.level.getBlockState(var7);
                  if (var9.is(Blocks.POWDER_SNOW)) {
                     return FogType.POWDER_SNOW;
                  }
               }
            }

            return FogType.NONE;
         }
      }
   }

   public final Vector3f getLookVector() {
      return this.forwards;
   }

   public final Vector3f getUpVector() {
      return this.up;
   }

   public final Vector3f getLeftVector() {
      return this.left;
   }

   public void reset() {
      this.level = null;
      this.entity = null;
      this.initialized = false;
   }

   public static class NearPlane {
      final Vec3 forward;
      private final Vec3 left;
      private final Vec3 up;

      NearPlane(Vec3 var1, Vec3 var2, Vec3 var3) {
         super();
         this.forward = var1;
         this.left = var2;
         this.up = var3;
      }

      public Vec3 getTopLeft() {
         return this.forward.add(this.up).add(this.left);
      }

      public Vec3 getTopRight() {
         return this.forward.add(this.up).subtract(this.left);
      }

      public Vec3 getBottomLeft() {
         return this.forward.subtract(this.up).add(this.left);
      }

      public Vec3 getBottomRight() {
         return this.forward.subtract(this.up).subtract(this.left);
      }

      public Vec3 getPointOnPlane(float var1, float var2) {
         return this.forward.add(this.up.scale((double)var2)).subtract(this.left.scale((double)var1));
      }
   }
}
