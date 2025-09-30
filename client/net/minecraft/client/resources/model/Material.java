package net.minecraft.client.resources.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;

public class Material {
   public static final Comparator<Material> COMPARATOR = Comparator.comparing(Material::atlasLocation).thenComparing(Material::texture);
   private final ResourceLocation atlasLocation;
   private final ResourceLocation texture;
   @Nullable
   private RenderType renderType;

   public Material(ResourceLocation var1, ResourceLocation var2) {
      super();
      this.atlasLocation = var1;
      this.texture = var2;
   }

   public ResourceLocation atlasLocation() {
      return this.atlasLocation;
   }

   public ResourceLocation texture() {
      return this.texture;
   }

   public RenderType renderType(Function<ResourceLocation, RenderType> var1) {
      if (this.renderType == null) {
         this.renderType = (RenderType)var1.apply(this.atlasLocation);
      }

      return this.renderType;
   }

   public VertexConsumer buffer(MaterialSet var1, MultiBufferSource var2, Function<ResourceLocation, RenderType> var3) {
      return var1.get(this).wrap(var2.getBuffer(this.renderType(var3)));
   }

   public VertexConsumer buffer(MaterialSet var1, MultiBufferSource var2, Function<ResourceLocation, RenderType> var3, boolean var4, boolean var5) {
      return var1.get(this).wrap(ItemRenderer.getFoilBuffer(var2, this.renderType(var3), var4, var5));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Material var2 = (Material)var1;
         return this.atlasLocation.equals(var2.atlasLocation) && this.texture.equals(var2.texture);
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
