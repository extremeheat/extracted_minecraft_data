package net.minecraft.world.entity;

import net.minecraft.core.PositionAndRotation;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class AbstractInterpolationHandler implements InterpolationHandler {
   protected final Entity entity;
   protected int interpolationSteps;
   protected final PositionAndRotation.Mutable lastPositionAndRotation = new PositionAndRotation.Mutable();

   public AbstractInterpolationHandler(final Entity entity, final int interpolationSteps) {
      super();
      this.entity = entity;
      this.interpolationSteps = interpolationSteps;
   }

   protected abstract PositionAndRotation.Mutable interpolationData();

   protected abstract void startInterpolating(PositionPath position, float yRot, float xRot);

   protected abstract void doInterpolate();

   public @Nullable PositionAndRotation target() {
      return this.hasActiveInterpolation() ? this.interpolationData().immutable() : null;
   }

   public boolean interpolateTo(final @Nullable PositionPath position, final float yRot, final float xRot, final boolean hasRotation) {
      PositionAndRotation current = (PositionAndRotation)(this.hasActiveInterpolation() ? this.interpolationData() : this.entity.storePositionAndRotation());
      this.interpolateTo(position != null ? position : PositionPath.of(current.position()), hasRotation ? yRot : current.yRot(), hasRotation ? xRot : current.xRot());
      return true;
   }

   protected void interpolateTo(final PositionPath position, final float yRot, final float xRot) {
      if (this.interpolationSteps == 0) {
         this.entity.snapTo(position.endPosition(), yRot, xRot);
         this.cancel();
      } else if (!this.hasActiveInterpolation() || !this.interpolationData().is(position.endPosition(), yRot, xRot)) {
         this.startInterpolating(position, yRot, xRot);
         this.setLastPositionAndRotation();
         this.entity.onInterpolationStart(this);
      }
   }

   public void applyPredictedMovement(final Vec3 delta) {
      this.adjustInterpolationPosition(delta);
      this.lastPositionAndRotation.addDelta(delta);
   }

   public void interpolate() {
      if (!this.hasActiveInterpolation()) {
         this.cancel();
      } else {
         this.adjustInterpolationTargetFromDeltas();
         this.doInterpolate();
         this.setLastPositionAndRotation();
      }
   }

   private void adjustInterpolationPosition(final Vec3 delta) {
      Vec3 adjustedPosition = this.interpolationData().position().add(delta);
      if (this.entity.level().noCollision(this.entity, this.entity.makeBoundingBox(adjustedPosition))) {
         this.interpolationData().addDelta(delta);
      }

   }

   private void adjustInterpolationTargetFromDeltas() {
      Vec3 deltaSinceLastInterpolation = this.entity.position().subtract(this.lastPositionAndRotation.position());
      if (deltaSinceLastInterpolation.lengthSqr() > 9.999999747378752E-6) {
         this.adjustInterpolationPosition(deltaSinceLastInterpolation);
      }

      float deltaYRotSinceLastInterpolation = this.entity.getYRot() - this.lastPositionAndRotation.yRot();
      float deltaXRotSinceLastInterpolation = this.entity.getXRot() - this.lastPositionAndRotation.xRot();
      this.interpolationData().addRotation(deltaYRotSinceLastInterpolation, deltaXRotSinceLastInterpolation);
   }

   private void setLastPositionAndRotation() {
      this.lastPositionAndRotation.set(this.entity.position(), this.entity.getYRot(), this.entity.getXRot());
   }
}
