package net.minecraft.gizmos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public class SimpleGizmoCollector implements GizmoCollector {
   private final List<GizmoInstance> gizmos = new ArrayList();
   private final List<GizmoInstance> temporaryGizmos = new ArrayList();

   public SimpleGizmoCollector() {
      super();
   }

   public GizmoProperties add(final Gizmo gizmo) {
      GizmoInstance instance = new GizmoInstance(gizmo);
      this.gizmos.add(instance);
      return instance;
   }

   public List<GizmoInstance> drainGizmos() {
      ArrayList<GizmoInstance> result = new ArrayList(this.gizmos);
      result.addAll(this.temporaryGizmos);
      long currentMillis = Util.getMillis();
      this.gizmos.removeIf((gizmo) -> gizmo.getExpireTimeMillis() < currentMillis);
      this.temporaryGizmos.clear();
      return result;
   }

   public List<GizmoInstance> getGizmos() {
      return this.gizmos;
   }

   public void addTemporaryGizmos(final Collection<GizmoInstance> gizmos) {
      this.temporaryGizmos.addAll(gizmos);
   }

   public static class GizmoInstance implements GizmoProperties {
      private final Gizmo gizmo;
      private boolean isAlwaysOnTop;
      private long startTimeMillis;
      private long expireTimeMillis;
      private boolean shouldFadeOut;

      private GizmoInstance(final Gizmo gizmo) {
         super();
         this.gizmo = gizmo;
      }

      public GizmoProperties setAlwaysOnTop() {
         this.isAlwaysOnTop = true;
         return this;
      }

      public GizmoProperties persistForMillis(final int milliseconds) {
         this.startTimeMillis = Util.getMillis();
         this.expireTimeMillis = this.startTimeMillis + (long)milliseconds;
         return this;
      }

      public GizmoProperties fadeOut() {
         this.shouldFadeOut = true;
         return this;
      }

      public float getAlphaMultiplier(final long currentMillis) {
         if (this.shouldFadeOut) {
            long duration = this.expireTimeMillis - this.startTimeMillis;
            long timeSinceStart = currentMillis - this.startTimeMillis;
            return 1.0F - Mth.clamp((float)timeSinceStart / (float)duration, 0.0F, 1.0F);
         } else {
            return 1.0F;
         }
      }

      public boolean isAlwaysOnTop() {
         return this.isAlwaysOnTop;
      }

      public long getExpireTimeMillis() {
         return this.expireTimeMillis;
      }

      public Gizmo gizmo() {
         return this.gizmo;
      }
   }
}
