package net.minecraft.client.model.geom.builders;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

public final class CubeDefinition {
   @Nullable
   private final String comment;
   private final Vector3f origin;
   private final Vector3f dimensions;
   private final CubeDeformation grow;
   private final boolean mirror;
   private final UVPair texCoord;
   private final UVPair texScale;
   private final Set<Direction> visibleFaces;

   protected CubeDefinition(@Nullable String var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, CubeDeformation var10, boolean var11, float var12, float var13, Set<Direction> var14) {
      super();
      this.comment = var1;
      this.texCoord = new UVPair(var2, var3);
      this.origin = new Vector3f(var4, var5, var6);
      this.dimensions = new Vector3f(var7, var8, var9);
      this.grow = var10;
      this.mirror = var11;
      this.texScale = new UVPair(var12, var13);
      this.visibleFaces = var14;
   }

   public ModelPart.Cube bake(int var1, int var2) {
      return new ModelPart.Cube((int)this.texCoord.u(), (int)this.texCoord.v(), this.origin.x(), this.origin.y(), this.origin.z(), this.dimensions.x(), this.dimensions.y(), this.dimensions.z(), this.grow.growX, this.grow.growY, this.grow.growZ, this.mirror, (float)var1 * this.texScale.u(), (float)var2 * this.texScale.v(), this.visibleFaces);
   }
}
