package net.minecraft.client.resources.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class Material {
   public static final Comparator<Material> COMPARATOR = Comparator.comparing(Material::atlasLocation).thenComparing(Material::texture);
   private final Identifier atlasLocation;
   private final Identifier texture;
   private @Nullable RenderType renderType;

   public Material(final Identifier atlasLocation, final Identifier texture) {
      super();
      this.atlasLocation = atlasLocation;
      this.texture = texture;
   }

   public Identifier atlasLocation() {
      return this.atlasLocation;
   }

   public Identifier texture() {
      return this.texture;
   }

   public RenderType renderType(final Function<Identifier, RenderType> renderType) {
      if (this.renderType == null) {
         this.renderType = (RenderType)renderType.apply(this.atlasLocation);
      }

      return this.renderType;
   }

   public VertexConsumer buffer(final MaterialSet materials, final MultiBufferSource bufferSource, final Function<Identifier, RenderType> renderType) {
      return materials.get(this).wrap(bufferSource.getBuffer(this.renderType(renderType)));
   }

   public VertexConsumer buffer(final MaterialSet materials, final MultiBufferSource bufferSource, final Function<Identifier, RenderType> renderType, final boolean sheeted, final boolean hasFoil) {
      return materials.get(this).wrap(ItemRenderer.getFoilBuffer(bufferSource, this.renderType(renderType), sheeted, hasFoil));
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Material material = (Material)o;
         return this.atlasLocation.equals(material.atlasLocation) && this.texture.equals(material.texture);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.atlasLocation, this.texture});
   }

   public String toString() {
      String var10000 = String.valueOf(this.atlasLocation);
      return "Material{atlasLocation=" + var10000 + ", texture=" + String.valueOf(this.texture) + "}";
   }
}
