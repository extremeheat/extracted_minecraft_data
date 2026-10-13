package net.minecraft.gizmos;

public interface GizmoProperties {
   GizmoProperties withEntityHitboxes();

   GizmoProperties setAlwaysOnTop();

   GizmoProperties persistForMillis(int milliseconds);

   GizmoProperties fadeOut();
}
