package net.minecraft.client.resources.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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

   public TextureAtlasSprite sprite() {
      return Minecraft.getInstance().getTextureAtlas(this.atlasLocation()).apply(this.texture());
   }

   public RenderType renderType(Function<ResourceLocation, RenderType> var1) {
      if (this.renderType == null) {
         this.renderType = (RenderType)var1.apply(this.atlasLocation);
      }

      return this.renderType;
   }

   public VertexConsumer buffer(MultiBufferSource var1, Function<ResourceLocation, RenderType> var2) {
      return this.sprite().wrap(var1.getBuffer(this.renderType(var2)));
   }

   public VertexConsumer buffer(MultiBufferSource var1, Function<ResourceLocation, RenderType> var2, boolean var3, boolean var4) {
      return this.sprite().wrap(ItemRenderer.getFoilBuffer(var1, this.renderType(var2), var3, var4));
   }

   @Override
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

   @Override
   public int hashCode() {
      return Objects.hash(this.atlasLocation, this.texture);
   }

   @Override
   public String toString() {
      return "Material{atlasLocation=" + this.atlasLocation + ", texture=" + this.texture + "}";
   }
}
