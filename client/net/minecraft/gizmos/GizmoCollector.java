package net.minecraft.gizmos;

public interface GizmoCollector {
   GizmoProperties IGNORED = new GizmoProperties() {
      public GizmoProperties setAlwaysOnTop() {
         return this;
      }

      public GizmoProperties persistForMillis(int var1) {
         return this;
      }

      public GizmoProperties fadeOut() {
         return this;
      }
   };
   GizmoCollector NOOP = (var0) -> IGNORED;

   GizmoProperties add(Gizmo var1);
}
