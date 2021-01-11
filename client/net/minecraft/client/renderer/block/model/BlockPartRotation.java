package net.minecraft.client.renderer.block.model;

import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

public class BlockPartRotation {
   public final Vector3f field_178344_a;
   public final EnumFacing.Axis field_178342_b;
   public final float field_178343_c;
   public final boolean field_178341_d;

   public BlockPartRotation(Vector3f var1, EnumFacing.Axis var2, float var3, boolean var4) {
      super();
      this.field_178344_a = var1;
      this.field_178342_b = var2;
      this.field_178343_c = var3;
      this.field_178341_d = var4;
   }
}
