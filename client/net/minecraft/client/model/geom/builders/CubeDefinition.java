package net.minecraft.client.model.geom.builders;

import com.mojang.math.Vector3f;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.ModelPart;

public final class CubeDefinition {
   @Nullable
   private final String comment;
   private final Vector3f origin;
   private final Vector3f dimensions;
   private final CubeDeformation grow;
   private final boolean mirror;
   private final UVPair texCoord;
   private final UVPair texScale;

   protected CubeDefinition(@Nullable String var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, CubeDeformation var10, boolean var11, float var12, float var13) {
      super();
      this.comment = var1;
      this.texCoord = new UVPair(var2, var3);
      this.origin = new Vector3f(var4, var5, var6);
      this.dimensions = new Vector3f(var7, var8, var9);
      this.grow = var10;
      this.mirror = var11;
      this.texScale = new UVPair(var12, var13);
   }

   public ModelPart.Cube bake(int var1, int var2) {
      return new ModelPart.Cube((int)this.texCoord.method_98(), (int)this.texCoord.method_99(), this.origin.method_82(), this.origin.method_83(), this.origin.method_84(), this.dimensions.method_82(), this.dimensions.method_83(), this.dimensions.method_84(), this.grow.growX, this.grow.growY, this.grow.growZ, this.mirror, (float)var1 * this.texScale.method_98(), (float)var2 * this.texScale.method_99());
   }
}
