package net.minecraft.gizmos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleGizmoCollector implements GizmoCollector {
   private final List<GizmoInstance> gizmos = new ArrayList();

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
      this.gizmos.clear();
      return var1;
   }

   public List<GizmoInstance> getGizmos() {
      return this.gizmos;
   }

   public void addGizmos(Collection<GizmoInstance> var1) {
      this.gizmos.addAll(var1);
   }

   public void clear() {
      this.gizmos.clear();
   }

   public static class GizmoInstance implements GizmoProperties {
      private final Gizmo gizmo;
      private boolean isAlwaysOnTop;

      GizmoInstance(Gizmo var1) {
         super();
         this.gizmo = var1;
      }

      public GizmoProperties setAlwaysOnTop() {
         this.isAlwaysOnTop = true;
         return this;
      }

      public boolean isAlwaysOnTop() {
         return this.isAlwaysOnTop;
      }

      public Gizmo gizmo() {
         return this.gizmo;
      }
   }
}
