package net.minecraft.gizmos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.util.Mth;

public class SimpleGizmoCollector implements GizmoCollector {
   private final List<GizmoInstance> gizmos = new ArrayList();
   private final List<GizmoInstance> temporaryGizmos = new ArrayList();

   public SimpleGizmoCollector() {
      super();
   }

   public GizmoProperties add(Gizmo var1) {
      GizmoInstance var2 = new GizmoInstance(var1);
      this.gizmos.add(var2);
      return var2;
   }

   public List<GizmoInstance> drainGizmos() {
      ArrayList var1 = new ArrayList(this.gizmos);
      var1.addAll(this.temporaryGizmos);
      long var2 = Util.getMillis();
      this.gizmos.removeIf((var2x) -> var2x.getExpireTimeMillis() < var2);
      this.temporaryGizmos.clear();
      return var1;
   }

   public List<GizmoInstance> getGizmos() {
      return this.gizmos;
   }

   public void addTemporaryGizmos(Collection<GizmoInstance> var1) {
      this.temporaryGizmos.addAll(var1);
   }

   public static class GizmoInstance implements GizmoProperties {
      private final Gizmo gizmo;
      private boolean isAlwaysOnTop;
      private long startTimeMillis;
      private long expireTimeMillis;
      private boolean shouldFadeOut;

      GizmoInstance(Gizmo var1) {
         super();
         this.gizmo = var1;
      }

      public GizmoProperties setAlwaysOnTop() {
         this.isAlwaysOnTop = true;
         return this;
      }

      public GizmoProperties persistForMillis(int var1) {
         this.startTimeMillis = Util.getMillis();
         this.expireTimeMillis = this.startTimeMillis + (long)var1;
         return this;
      }

      public GizmoProperties fadeOut() {
         this.shouldFadeOut = true;
         return this;
      }

      public float getAlphaMultiplier(long var1) {
         if (this.shouldFadeOut) {
            long var3 = this.expireTimeMillis - this.startTimeMillis;
            long var5 = var1 - this.startTimeMillis;
            return 1.0F - Mth.clamp((float)var5 / (float)var3, 0.0F, 1.0F);
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
