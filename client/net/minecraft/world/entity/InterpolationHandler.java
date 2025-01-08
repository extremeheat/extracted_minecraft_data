package net.minecraft.world.entity;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class InterpolationHandler {
   public static final int DEFAULT_INTERPOLATION_STEPS = 3;
   private final Entity entity;
   private int interpolationSteps;
   private final InterpolationData interpolationData;
   @Nullable
   private Vec3 previousTickPosition;
   @Nullable
   private Vec2 previousTickRot;
   @Nullable
   private final Consumer<InterpolationHandler> onInterpolationStart;

   public InterpolationHandler(Entity var1) {
      this(var1, 3, (Consumer)null);
   }

   public InterpolationHandler(Entity var1, int var2) {
      this(var1, var2, (Consumer)null);
   }

   public InterpolationHandler(Entity var1, @Nullable Consumer<InterpolationHandler> var2) {
      this(var1, 3, var2);
   }

   public InterpolationHandler(Entity var1, int var2, @Nullable Consumer<InterpolationHandler> var3) {
      super();
      this.interpolationData = new InterpolationData(0, Vec3.ZERO, 0.0F, 0.0F);
      this.interpolationSteps = var2;
      this.entity = var1;
      this.onInterpolationStart = var3;
   }

   public Vec3 position() {
      return this.interpolationData.steps > 0 ? this.interpolationData.position : this.entity.position();
   }

   public float yRot() {
      return this.interpolationData.steps > 0 ? this.interpolationData.yRot : this.entity.getYRot();
   }

   public float xRot() {
      return this.interpolationData.steps > 0 ? this.interpolationData.xRot : this.entity.getXRot();
   }

   public void interpolateTo(Vec3 var1, float var2, float var3) {
      if (this.interpolationSteps == 0) {
         this.entity.moveTo(var1, var2, var3);
         this.cancel();
      } else {
         this.interpolationData.steps = this.interpolationSteps;
         this.interpolationData.position = var1;
         this.interpolationData.yRot = var2;
         this.interpolationData.xRot = var3;
         this.previousTickPosition = this.entity.position();
         this.previousTickRot = new Vec2(this.entity.getXRot(), this.entity.getYRot());
         if (this.onInterpolationStart != null) {
            this.onInterpolationStart.accept(this);
         }

      }
   }

   public boolean hasActiveInterpolation() {
      return this.interpolationData.steps > 0;
   }

   public void setInterpolationLength(int var1) {
      this.interpolationSteps = var1;
   }

   public void interpolate() {
      if (!this.hasActiveInterpolation()) {
         this.cancel();
      } else {
         double var1 = 1.0 / (double)this.interpolationData.steps;
         if (this.previousTickPosition != null) {
            Vec3 var3 = this.entity.position().subtract(this.previousTickPosition);
            if (this.entity.level().noCollision(this.entity, this.entity.makeBoundingBox().move(this.interpolationData.position.add(var3)))) {
               this.interpolationData.addDelta(var3);
            }
         }

         if (this.previousTickRot != null) {
            float var12 = this.entity.getYRot() - this.previousTickRot.y;
            float var4 = this.entity.getXRot() - this.previousTickRot.x;
            this.interpolationData.addRotation(var12, var4);
         }

         double var13 = Mth.lerp(var1, this.entity.getX(), this.interpolationData.position.x);
         double var5 = Mth.lerp(var1, this.entity.getY(), this.interpolationData.position.y);
         double var7 = Mth.lerp(var1, this.entity.getZ(), this.interpolationData.position.z);
         Vec3 var9 = new Vec3(var13, var5, var7);
         float var10 = (float)Mth.rotLerp(var1, (double)this.entity.getYRot(), (double)this.interpolationData.yRot);
         float var11 = (float)Mth.lerp(var1, (double)this.entity.getXRot(), (double)this.interpolationData.xRot);
         this.entity.setPos(var9);
         this.entity.setRot(var10, var11);
         this.interpolationData.decrease();
         this.previousTickPosition = var9;
         this.previousTickRot = new Vec2(this.entity.getXRot(), this.entity.getYRot());
      }
   }

   public void cancel() {
      this.interpolationData.steps = 0;
      this.previousTickPosition = null;
      this.previousTickRot = null;
   }

   static class InterpolationData {
      protected int steps;
      Vec3 position;
      float yRot;
      float xRot;

      InterpolationData(int var1, Vec3 var2, float var3, float var4) {
         super();
         this.steps = var1;
         this.position = var2;
         this.yRot = var3;
         this.xRot = var4;
      }

      public void decrease() {
         --this.steps;
      }

      public void addDelta(Vec3 var1) {
         this.position = this.position.add(var1);
      }

      public void addRotation(float var1, float var2) {
         this.yRot += var1;
         this.xRot += var2;
      }
   }
}
