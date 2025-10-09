package net.minecraft.gizmos;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record ArrowGizmo(Vec3 start, Vec3 end, int color, float width) implements Gizmo {
   public static final float DEFAULT_WIDTH = 2.5F;

   public ArrowGizmo(Vec3 var1, Vec3 var2, int var3, float var4) {
      super();
      this.start = var1;
      this.end = var2;
      this.color = var3;
      this.width = var4;
   }

   public void emit(GizmoPrimitives var1) {
      var1.addLine(this.start, this.end, this.color, this.width);
      Quaternionf var2 = (new Quaternionf()).rotationTo(new Vector3f(1.0F, 0.0F, 0.0F), this.end.subtract(this.start).toVector3f().normalize());
      float var3 = (float)Mth.clamp(this.end.distanceTo(this.start) * 0.10000000149011612, 0.10000000149011612, 1.0);
      Vector3f[] var4 = new Vector3f[]{var2.transform(-var3, var3, 0.0F, new Vector3f()), var2.transform(-var3, 0.0F, var3, new Vector3f()), var2.transform(-var3, -var3, 0.0F, new Vector3f()), var2.transform(-var3, 0.0F, -var3, new Vector3f())};

      for(Vector3f var8 : var4) {
         var1.addLine(this.end.add((double)var8.x, (double)var8.y, (double)var8.z), this.end, this.color, this.width);
      }

   }
}
