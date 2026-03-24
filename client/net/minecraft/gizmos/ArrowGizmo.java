package net.minecraft.gizmos;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record ArrowGizmo(Vec3 start, Vec3 end, int color, float width) implements Gizmo {
   public static final float DEFAULT_WIDTH = 2.5F;

   public ArrowGizmo {
      super();
   }

   public void emit(final GizmoPrimitives primitives, final float alphaMultiplier) {
      int color = ARGB.multiplyAlpha(this.color, alphaMultiplier);
      primitives.addLine(this.start, this.end, color, this.width);
      Quaternionf rotation = (new Quaternionf()).rotationTo(new Vector3f(1.0F, 0.0F, 0.0F), this.end.subtract(this.start).toVector3f().normalize());
      float len = (float)Mth.clamp(this.end.distanceTo(this.start) * 0.10000000149011612, 0.10000000149011612, 1.0);
      Vector3f[] tips = new Vector3f[]{rotation.transform(-len, len, 0.0F, new Vector3f()), rotation.transform(-len, 0.0F, len, new Vector3f()), rotation.transform(-len, -len, 0.0F, new Vector3f()), rotation.transform(-len, 0.0F, -len, new Vector3f())};

      for(Vector3f tip : tips) {
         primitives.addLine(this.end.add((double)tip.x, (double)tip.y, (double)tip.z), this.end, color, this.width);
      }

   }
}
