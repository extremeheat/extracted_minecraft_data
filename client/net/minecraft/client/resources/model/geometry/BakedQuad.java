package net.minecraft.client.resources.model.geometry;

import com.mojang.blaze3d.platform.Transparency;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public record BakedQuad(Vector3fc position0, Vector3fc position1, Vector3fc position2, Vector3fc position3, long packedUV0, long packedUV1, long packedUV2, long packedUV3, Direction direction, MaterialInfo materialInfo) {
   public static final int VERTEX_COUNT = 4;
   public static final int FLAG_TRANSLUCENT = 1;
   public static final int FLAG_ANIMATED = 2;

   public BakedQuad {
      super();
   }

   public Vector3fc position(final int vertex) {
      Vector3fc var10000;
      switch (vertex) {
         case 0 -> var10000 = this.position0;
         case 1 -> var10000 = this.position1;
         case 2 -> var10000 = this.position2;
         case 3 -> var10000 = this.position3;
         default -> throw new IndexOutOfBoundsException(vertex);
      }

      return var10000;
   }

   public long packedUV(final int vertex) {
      long var10000;
      switch (vertex) {
         case 0 -> var10000 = this.packedUV0;
         case 1 -> var10000 = this.packedUV1;
         case 2 -> var10000 = this.packedUV2;
         case 3 -> var10000 = this.packedUV3;
         default -> throw new IndexOutOfBoundsException(vertex);
      }

      return var10000;
   }

   public static record MaterialInfo(TextureAtlasSprite sprite, ChunkSectionLayer layer, RenderType itemRenderType, RenderType itemGlintRenderType, RenderType itemGlintSpecialRenderType, int tintIndex, @Nullable Direction shadeDirectionOverride, int lightEmission) {
      public MaterialInfo {
         super();
      }

      public static MaterialInfo of(final Material.Baked material, final Transparency transparency, final int tintIndex, final @Nullable Direction shadeDirectionOverride, final int lightEmission) {
         ChunkSectionLayer layer = ChunkSectionLayer.byTransparency(transparency);
         RenderType itemRenderType;
         RenderType itemGlintRenderType;
         RenderType itemGlintSpecialRenderType;
         if (material.sprite().atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS)) {
            itemRenderType = transparency.hasTranslucent() ? Sheets.translucentBlockItemSheet() : Sheets.cutoutBlockItemSheet();
            itemGlintRenderType = transparency.hasTranslucent() ? Sheets.translucentBlockItemGlintSheet() : Sheets.cutoutBlockItemGlintSheet();
            itemGlintSpecialRenderType = transparency.hasTranslucent() ? Sheets.translucentBlockItemGlintSpecialSheet() : Sheets.cutoutBlockItemGlintSpecialSheet();
         } else {
            itemRenderType = transparency.hasTranslucent() ? Sheets.translucentItemSheet() : Sheets.cutoutItemSheet();
            itemGlintRenderType = transparency.hasTranslucent() ? Sheets.translucentItemGlintSheet() : Sheets.cutoutItemGlintSheet();
            itemGlintSpecialRenderType = transparency.hasTranslucent() ? Sheets.translucentItemGlintSpecialSheet() : Sheets.cutoutItemGlintSpecialSheet();
         }

         return new MaterialInfo(material.sprite(), layer, itemRenderType, itemGlintRenderType, itemGlintSpecialRenderType, tintIndex, shadeDirectionOverride, lightEmission);
      }

      public boolean isTinted() {
         return this.tintIndex != -1;
      }

      public @BakedQuad.MaterialFlags int flags() {
         int flags = 0;
         flags |= this.layer.translucent() ? 1 : 0;
         flags |= this.sprite.contents().isAnimated() ? 2 : 0;
         return flags;
      }
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.TYPE_USE})
   public @interface MaterialFlags {
   }
}
