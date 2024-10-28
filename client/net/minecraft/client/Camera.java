package net.minecraft.client;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartBehavior;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera {
   private static final float DEFAULT_CAMERA_DISTANCE = 4.0F;
   private static final Vector3f FORWARDS = new Vector3f(0.0F, 0.0F, -1.0F);
   private static final Vector3f UP = new Vector3f(0.0F, 1.0F, 0.0F);
   private static final Vector3f LEFT = new Vector3f(-1.0F, 0.0F, 0.0F);
   private boolean initialized;
   private BlockGetter level;
   private Entity entity;
   private Vec3 position;
   private final BlockPos.MutableBlockPos blockPosition;
   private final Vector3f forwards;
   private final Vector3f up;
   private final Vector3f left;
   private float xRot;
   private float yRot;
   private final Quaternionf rotation;
   private boolean detached;
   private float eyeHeight;
   private float eyeHeightOld;
   private float partialTickTime;
   public static final float FOG_DISTANCE_SCALE = 0.083333336F;

   public Camera() {
      super();
      this.position = Vec3.ZERO;
      this.blockPosition = new BlockPos.MutableBlockPos();
      this.forwards = new Vector3f(FORWARDS);
      this.up = new Vector3f(UP);
      this.left = new Vector3f(LEFT);
      this.rotation = new Quaternionf();
   }

   public void setup(BlockGetter var1, Entity var2, boolean var3, boolean var4, float var5) {
      label39: {
         this.initialized = true;
         this.level = var1;
         this.entity = var2;
         this.detached = var3;
         this.partialTickTime = var5;
         if (var2.isPassenger()) {
            Entity var8 = var2.getVehicle();
            if (var8 instanceof Minecart) {
               Minecart var6 = (Minecart)var8;
               MinecartBehavior var12 = var6.getBehavior();
               if (var12 instanceof NewMinecartBehavior) {
                  NewMinecartBehavior var7 = (NewMinecartBehavior)var12;
                  if (var7.cartHasPosRotLerp()) {
                     Vec3 var13 = var6.getPassengerRidingPosition(var2).subtract(var6.position()).subtract(var2.getVehicleAttachmentPoint(var6)).add(new Vec3(0.0, (double)Mth.lerp(var5, this.eyeHeightOld, this.eyeHeight), 0.0));
                     this.setRotation(var2.getViewYRot(var5), var2.getViewXRot(var5));
                     this.setPosition(var7.getCartLerpPosition(var5).add(var13));
                     break label39;
                  }
               }
            }
         }

         this.setRotation(var2.getViewYRot(var5), var2.getViewXRot(var5));
         this.setPosition(Mth.lerp((double)var5, var2.xo, var2.getX()), Mth.lerp((double)var5, var2.yo, var2.getY()) + (double)Mth.lerp(var5, this.eyeHeightOld, this.eyeHeight), Mth.lerp((double)var5, var2.zo, var2.getZ()));
      }

      if (var3) {
         if (var4) {
            this.setRotation(this.yRot + 180.0F, -this.xRot);
         }

         float var10000;
         if (var2 instanceof LivingEntity) {
            LivingEntity var11 = (LivingEntity)var2;
            var10000 = var11.getScale();
         } else {
            var10000 = 1.0F;
         }

         float var9 = var10000;
         this.move(-this.getMaxZoom(4.0F * var9), 0.0F, 0.0F);
      } else if (var2 instanceof LivingEntity && ((LivingEntity)var2).isSleeping()) {
         Direction var10 = ((LivingEntity)var2).getBedOrientation();
         this.setRotation(var10 != null ? var10.toYRot() - 180.0F : 0.0F, 0.0F);
         this.move(0.0F, 0.3F, 0.0F);
      }

   }

   public void tick() {
      if (this.entity != null) {
         this.eyeHeightOld = this.eyeHeight;
         this.eyeHeight += (this.entity.getEyeHeight() - this.eyeHeight) * 0.5F;
      }

   }

   private float getMaxZoom(float var1) {
      float var2 = 0.1F;

      for(int var3 = 0; var3 < 8; ++var3) {
         float var4 = (float)((var3 & 1) * 2 - 1);
         float var5 = (float)((var3 >> 1 & 1) * 2 - 1);
         float var6 = (float)((var3 >> 2 & 1) * 2 - 1);
         Vec3 var7 = this.position.add((double)(var4 * 0.1F), (double)(var5 * 0.1F), (double)(var6 * 0.1F));
         Vec3 var8 = var7.add((new Vec3(this.forwards)).scale((double)(-var1)));
         BlockHitResult var9 = this.level.clip(new ClipContext(var7, var8, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, this.entity));
         if (((HitResult)var9).getType() != HitResult.Type.MISS) {
            float var10 = (float)((HitResult)var9).getLocation().distanceToSqr(this.position);
            if (var10 < Mth.square(var1)) {
               var1 = Mth.sqrt(var10);
            }
         }
      }

      return var1;
   }

   protected void move(float var1, float var2, float var3) {
      Vector3f var4 = (new Vector3f(var3, var2, -var1)).rotate(this.rotation);
      this.setPosition(new Vec3(this.position.x + (double)var4.x, this.position.y + (double)var4.y, this.position.z + (double)var4.z));
   }

   protected void setRotation(float var1, float var2) {
      this.xRot = var2;
      this.yRot = var1;
      this.rotation.rotationYXZ(3.1415927F - var1 * 0.017453292F, -var2 * 0.017453292F, 0.0F);
      FORWARDS.rotate(this.rotation, this.forwards);
      UP.rotate(this.rotation, this.up);
      LEFT.rotate(this.rotation, this.left);
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

   public Quaternionf rotation() {
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

   public NearPlane getNearPlane() {
      Minecraft var1 = Minecraft.getInstance();
      double var2 = (double)var1.getWindow().getWidth() / (double)var1.getWindow().getHeight();
      double var4 = Math.tan((double)((float)(Integer)var1.options.fov().get() * 0.017453292F) / 2.0) * 0.05000000074505806;
      double var6 = var4 * var2;
      Vec3 var8 = (new Vec3(this.forwards)).scale(0.05000000074505806);
      Vec3 var9 = (new Vec3(this.left)).scale(var6);
      Vec3 var10 = (new Vec3(this.up)).scale(var4);
      return new NearPlane(var8, var9, var10);
   }

   public FogType getFluidInCamera() {
      if (!this.initialized) {
         return FogType.NONE;
      } else {
         FluidState var1 = this.level.getFluidState(this.blockPosition);
         if (var1.is(FluidTags.WATER) && this.position.y < (double)((float)this.blockPosition.getY() + var1.getHeight(this.level, this.blockPosition))) {
            return FogType.WATER;
         } else {
            NearPlane var2 = this.getNearPlane();
            List var3 = Arrays.asList(var2.forward, var2.getTopLeft(), var2.getTopRight(), var2.getBottomLeft(), var2.getBottomRight());
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               Vec3 var5 = (Vec3)var4.next();
               Vec3 var6 = this.position.add(var5);
               BlockPos var7 = BlockPos.containing(var6);
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

   public float getPartialTickTime() {
      return this.partialTickTime;
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
