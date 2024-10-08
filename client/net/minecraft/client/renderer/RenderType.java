package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

public abstract class RenderType extends RenderStateShard {
   private static final int MEGABYTE = 1048576;
   public static final int BIG_BUFFER_SIZE = 4194304;
   public static final int SMALL_BUFFER_SIZE = 786432;
   public static final int TRANSIENT_BUFFER_SIZE = 1536;
   private static final RenderType SOLID = create(
      "solid",
      DefaultVertexFormat.BLOCK,
      VertexFormat.Mode.QUADS,
      4194304,
      true,
      false,
      RenderType.CompositeState.builder()
         .setLightmapState(LIGHTMAP)
         .setShaderState(RENDERTYPE_SOLID_SHADER)
         .setTextureState(BLOCK_SHEET_MIPPED)
         .createCompositeState(true)
   );
   private static final RenderType CUTOUT_MIPPED = create(
      "cutout_mipped",
      DefaultVertexFormat.BLOCK,
      VertexFormat.Mode.QUADS,
      4194304,
      true,
      false,
      RenderType.CompositeState.builder()
         .setLightmapState(LIGHTMAP)
         .setShaderState(RENDERTYPE_CUTOUT_MIPPED_SHADER)
         .setTextureState(BLOCK_SHEET_MIPPED)
         .createCompositeState(true)
   );
   private static final RenderType CUTOUT = create(
      "cutout",
      DefaultVertexFormat.BLOCK,
      VertexFormat.Mode.QUADS,
      786432,
      true,
      false,
      RenderType.CompositeState.builder()
         .setLightmapState(LIGHTMAP)
         .setShaderState(RENDERTYPE_CUTOUT_SHADER)
         .setTextureState(BLOCK_SHEET)
         .createCompositeState(true)
   );
   private static final RenderType TRANSLUCENT = create(
      "translucent", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 786432, true, true, translucentState(RENDERTYPE_TRANSLUCENT_SHADER)
   );
   private static final RenderType TRANSLUCENT_MOVING_BLOCK = create(
      "translucent_moving_block", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 786432, false, true, translucentMovingBlockState()
   );
   private static final Function<ResourceLocation, RenderType> ARMOR_CUTOUT_NO_CULL = Util.memoize(
      var0 -> createArmorCutoutNoCull("armor_cutout_no_cull", var0, false)
   );
   private static final Function<ResourceLocation, RenderType> ENTITY_SOLID = Util.memoize(
      var0 -> {
         RenderType.CompositeState var1 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setTransparencyState(NO_TRANSPARENCY)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(true);
         return create("entity_solid", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, var1);
      }
   );
   private static final Function<ResourceLocation, RenderType> ENTITY_SOLID_Z_OFFSET_FORWARD = Util.memoize(
      var0 -> {
         RenderType.CompositeState var1 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setTransparencyState(NO_TRANSPARENCY)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setLayeringState(VIEW_OFFSET_Z_LAYERING_FORWARD)
            .createCompositeState(true);
         return create("entity_solid_z_offset_forward", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, var1);
      }
   );
   private static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT = Util.memoize(
      var0 -> {
         RenderType.CompositeState var1 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setTransparencyState(NO_TRANSPARENCY)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(true);
         return create("entity_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, var1);
      }
   );
   private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL = Util.memoize(
      (var0, var1) -> {
         RenderType.CompositeState var2 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setTransparencyState(NO_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(var1);
         return create("entity_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, var2);
      }
   );
   private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL_Z_OFFSET = Util.memoize(
      (var0, var1) -> {
         RenderType.CompositeState var2 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setTransparencyState(NO_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(var1);
         return create("entity_cutout_no_cull_z_offset", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, var2);
      }
   );
   private static final Function<ResourceLocation, RenderType> ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize(
      var0 -> {
         RenderType.CompositeState var1 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setOutputState(ITEM_ENTITY_TARGET)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .createCompositeState(true);
         return create("item_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, var1);
      }
   );
   private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT = Util.memoize(
      (var0, var1) -> {
         RenderType.CompositeState var2 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(var1);
         return create("entity_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, var2);
      }
   );
   private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize(
      (var0, var1) -> {
         RenderType.CompositeState var2 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setWriteMaskState(COLOR_WRITE)
            .setOverlayState(OVERLAY)
            .createCompositeState(var1);
         return create("entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, var2);
      }
   );
   private static final Function<ResourceLocation, RenderType> ENTITY_SMOOTH_CUTOUT = Util.memoize(
      var0 -> {
         RenderType.CompositeState var1 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_SMOOTH_CUTOUT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .createCompositeState(true);
         return create("entity_smooth_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, var1);
      }
   );
   private static final BiFunction<ResourceLocation, Boolean, RenderType> BEACON_BEAM = Util.memoize(
      (var0, var1) -> {
         RenderType.CompositeState var2 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_BEACON_BEAM_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setTransparencyState(var1 ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY)
            .setWriteMaskState(var1 ? COLOR_WRITE : COLOR_DEPTH_WRITE)
            .createCompositeState(false);
         return create("beacon_beam", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, true, var2);
      }
   );
   private static final Function<ResourceLocation, RenderType> ENTITY_DECAL = Util.memoize(
      var0 -> {
         RenderType.CompositeState var1 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_DECAL_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setDepthTestState(EQUAL_DEPTH_TEST)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(false);
         return create("entity_decal", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, var1);
      }
   );
   private static final Function<ResourceLocation, RenderType> ENTITY_NO_OUTLINE = Util.memoize(
      var0 -> {
         RenderType.CompositeState var1 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_NO_OUTLINE_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(false);
         return create("entity_no_outline", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, var1);
      }
   );
   private static final Function<ResourceLocation, RenderType> ENTITY_SHADOW = Util.memoize(
      var0 -> {
         RenderType.CompositeState var1 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_SHADOW_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setWriteMaskState(COLOR_WRITE)
            .setDepthTestState(LEQUAL_DEPTH_TEST)
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(false);
         return create("entity_shadow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, false, var1);
      }
   );
   private static final Function<ResourceLocation, RenderType> DRAGON_EXPLOSION_ALPHA = Util.memoize(
      var0 -> {
         RenderType.CompositeState var1 = RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_ALPHA_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setCullState(NO_CULL)
            .createCompositeState(true);
         return create("entity_alpha", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, var1);
      }
   );
   private static final BiFunction<ResourceLocation, RenderStateShard.TransparencyStateShard, RenderType> EYES = Util.memoize(
      (var0, var1) -> {
         RenderStateShard.TextureStateShard var2 = new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false);
         return create(
            "eyes",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
               .setShaderState(RENDERTYPE_EYES_SHADER)
               .setTextureState(var2)
               .setTransparencyState(var1)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         );
      }
   );
   private static final RenderType LEASH = create(
      "leash",
      DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
      VertexFormat.Mode.TRIANGLE_STRIP,
      1536,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_LEASH_SHADER)
         .setTextureState(NO_TEXTURE)
         .setCullState(NO_CULL)
         .setLightmapState(LIGHTMAP)
         .createCompositeState(false)
   );
   private static final RenderType WATER_MASK = create(
      "water_mask",
      DefaultVertexFormat.POSITION,
      VertexFormat.Mode.QUADS,
      1536,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_WATER_MASK_SHADER)
         .setTextureState(NO_TEXTURE)
         .setWriteMaskState(DEPTH_WRITE)
         .createCompositeState(false)
   );
   private static final RenderType ARMOR_ENTITY_GLINT = create(
      "armor_entity_glint",
      DefaultVertexFormat.POSITION_TEX,
      VertexFormat.Mode.QUADS,
      1536,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER)
         .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ENTITY, TriState.DEFAULT, false))
         .setWriteMaskState(COLOR_WRITE)
         .setCullState(NO_CULL)
         .setDepthTestState(EQUAL_DEPTH_TEST)
         .setTransparencyState(GLINT_TRANSPARENCY)
         .setTexturingState(ENTITY_GLINT_TEXTURING)
         .setLayeringState(VIEW_OFFSET_Z_LAYERING)
         .createCompositeState(false)
   );
   private static final RenderType GLINT_TRANSLUCENT = create(
      "glint_translucent",
      DefaultVertexFormat.POSITION_TEX,
      VertexFormat.Mode.QUADS,
      1536,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_GLINT_TRANSLUCENT_SHADER)
         .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, TriState.DEFAULT, false))
         .setWriteMaskState(COLOR_WRITE)
         .setCullState(NO_CULL)
         .setDepthTestState(EQUAL_DEPTH_TEST)
         .setTransparencyState(GLINT_TRANSPARENCY)
         .setTexturingState(GLINT_TEXTURING)
         .setOutputState(ITEM_ENTITY_TARGET)
         .createCompositeState(false)
   );
   private static final RenderType GLINT = create(
      "glint",
      DefaultVertexFormat.POSITION_TEX,
      VertexFormat.Mode.QUADS,
      1536,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_GLINT_SHADER)
         .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, TriState.DEFAULT, false))
         .setWriteMaskState(COLOR_WRITE)
         .setCullState(NO_CULL)
         .setDepthTestState(EQUAL_DEPTH_TEST)
         .setTransparencyState(GLINT_TRANSPARENCY)
         .setTexturingState(GLINT_TEXTURING)
         .createCompositeState(false)
   );
   private static final RenderType ENTITY_GLINT = create(
      "entity_glint",
      DefaultVertexFormat.POSITION_TEX,
      VertexFormat.Mode.QUADS,
      1536,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER)
         .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ENTITY, TriState.DEFAULT, false))
         .setWriteMaskState(COLOR_WRITE)
         .setCullState(NO_CULL)
         .setDepthTestState(EQUAL_DEPTH_TEST)
         .setTransparencyState(GLINT_TRANSPARENCY)
         .setTexturingState(ENTITY_GLINT_TEXTURING)
         .createCompositeState(false)
   );
   private static final Function<ResourceLocation, RenderType> CRUMBLING = Util.memoize(
      var0 -> {
         RenderStateShard.TextureStateShard var1 = new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false);
         return create(
            "crumbling",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
               .setShaderState(RENDERTYPE_CRUMBLING_SHADER)
               .setTextureState(var1)
               .setTransparencyState(CRUMBLING_TRANSPARENCY)
               .setWriteMaskState(COLOR_WRITE)
               .setLayeringState(POLYGON_OFFSET_LAYERING)
               .createCompositeState(false)
         );
      }
   );
   private static final Function<ResourceLocation, RenderType> TEXT = Util.memoize(
      var0 -> create(
            "text",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            786432,
            false,
            false,
            RenderType.CompositeState.builder()
               .setShaderState(RENDERTYPE_TEXT_SHADER)
               .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setLightmapState(LIGHTMAP)
               .createCompositeState(false)
         )
   );
   private static final RenderType TEXT_BACKGROUND = create(
      "text_background",
      DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
      VertexFormat.Mode.QUADS,
      1536,
      false,
      true,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_TEXT_BACKGROUND_SHADER)
         .setTextureState(NO_TEXTURE)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setLightmapState(LIGHTMAP)
         .createCompositeState(false)
   );
   private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY = Util.memoize(
      var0 -> create(
            "text_intensity",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            786432,
            false,
            false,
            RenderType.CompositeState.builder()
               .setShaderState(RENDERTYPE_TEXT_INTENSITY_SHADER)
               .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setLightmapState(LIGHTMAP)
               .createCompositeState(false)
         )
   );
   private static final Function<ResourceLocation, RenderType> TEXT_POLYGON_OFFSET = Util.memoize(
      var0 -> create(
            "text_polygon_offset",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
               .setShaderState(RENDERTYPE_TEXT_SHADER)
               .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setLightmapState(LIGHTMAP)
               .setLayeringState(POLYGON_OFFSET_LAYERING)
               .createCompositeState(false)
         )
   );
   private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize(
      var0 -> create(
            "text_intensity_polygon_offset",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
               .setShaderState(RENDERTYPE_TEXT_INTENSITY_SHADER)
               .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setLightmapState(LIGHTMAP)
               .setLayeringState(POLYGON_OFFSET_LAYERING)
               .createCompositeState(false)
         )
   );
   private static final Function<ResourceLocation, RenderType> TEXT_SEE_THROUGH = Util.memoize(
      var0 -> create(
            "text_see_through",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            false,
            RenderType.CompositeState.builder()
               .setShaderState(RENDERTYPE_TEXT_SEE_THROUGH_SHADER)
               .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setLightmapState(LIGHTMAP)
               .setDepthTestState(NO_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         )
   );
   private static final RenderType TEXT_BACKGROUND_SEE_THROUGH = create(
      "text_background_see_through",
      DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
      VertexFormat.Mode.QUADS,
      1536,
      false,
      true,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH_SHADER)
         .setTextureState(NO_TEXTURE)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setLightmapState(LIGHTMAP)
         .setDepthTestState(NO_DEPTH_TEST)
         .setWriteMaskState(COLOR_WRITE)
         .createCompositeState(false)
   );
   private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY_SEE_THROUGH = Util.memoize(
      var0 -> create(
            "text_intensity_see_through",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
               .setShaderState(RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH_SHADER)
               .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setLightmapState(LIGHTMAP)
               .setDepthTestState(NO_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         )
   );
   private static final RenderType LIGHTNING = create(
      "lightning",
      DefaultVertexFormat.POSITION_COLOR,
      VertexFormat.Mode.QUADS,
      1536,
      false,
      true,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
         .setWriteMaskState(COLOR_DEPTH_WRITE)
         .setTransparencyState(LIGHTNING_TRANSPARENCY)
         .setOutputState(WEATHER_TARGET)
         .createCompositeState(false)
   );
   private static final RenderType DRAGON_RAYS = create(
      "dragon_rays",
      DefaultVertexFormat.POSITION_COLOR,
      VertexFormat.Mode.TRIANGLES,
      1536,
      false,
      false,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
         .setWriteMaskState(COLOR_WRITE)
         .setTransparencyState(LIGHTNING_TRANSPARENCY)
         .createCompositeState(false)
   );
   private static final RenderType DRAGON_RAYS_DEPTH = create(
      "dragon_rays_depth",
      DefaultVertexFormat.POSITION,
      VertexFormat.Mode.TRIANGLES,
      1536,
      false,
      false,
      RenderType.CompositeState.builder().setShaderState(POSITION_SHADER).setWriteMaskState(DEPTH_WRITE).createCompositeState(false)
   );
   private static final RenderType TRIPWIRE = create("tripwire", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, true, true, tripwireState());
   private static final RenderType END_PORTAL = create(
      "end_portal",
      DefaultVertexFormat.POSITION,
      VertexFormat.Mode.QUADS,
      1536,
      false,
      false,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_END_PORTAL_SHADER)
         .setTextureState(
            RenderStateShard.MultiTextureStateShard.builder()
               .add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
               .add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false)
               .build()
         )
         .createCompositeState(false)
   );
   private static final RenderType END_GATEWAY = create(
      "end_gateway",
      DefaultVertexFormat.POSITION,
      VertexFormat.Mode.QUADS,
      1536,
      false,
      false,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_END_GATEWAY_SHADER)
         .setTextureState(
            RenderStateShard.MultiTextureStateShard.builder()
               .add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
               .add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false)
               .build()
         )
         .createCompositeState(false)
   );
   private static final RenderType FLAT_CLOUDS = createClouds(false, false);
   private static final RenderType CLOUDS = createClouds(false, true);
   private static final RenderType CLOUDS_DEPTH_ONLY = createClouds(true, true);
   public static final RenderType.CompositeRenderType LINES = create(
      "lines",
      DefaultVertexFormat.POSITION_COLOR_NORMAL,
      VertexFormat.Mode.LINES,
      1536,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_LINES_SHADER)
         .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
         .setLayeringState(VIEW_OFFSET_Z_LAYERING)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setOutputState(ITEM_ENTITY_TARGET)
         .setWriteMaskState(COLOR_DEPTH_WRITE)
         .setCullState(NO_CULL)
         .createCompositeState(false)
   );
   public static final RenderType.CompositeRenderType SECONDARY_BLOCK_OUTLINE = create(
      "secondary_block_outline",
      DefaultVertexFormat.POSITION_COLOR_NORMAL,
      VertexFormat.Mode.LINES,
      1536,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_LINES_SHADER)
         .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(7.0)))
         .setLayeringState(VIEW_OFFSET_Z_LAYERING)
         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
         .setOutputState(ITEM_ENTITY_TARGET)
         .setWriteMaskState(COLOR_WRITE)
         .setCullState(NO_CULL)
         .createCompositeState(false)
   );
   public static final RenderType.CompositeRenderType LINE_STRIP = create(
      "line_strip",
      DefaultVertexFormat.POSITION_COLOR_NORMAL,
      VertexFormat.Mode.LINE_STRIP,
      1536,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_LINES_SHADER)
         .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
         .setLayeringState(VIEW_OFFSET_Z_LAYERING)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setOutputState(ITEM_ENTITY_TARGET)
         .setWriteMaskState(COLOR_DEPTH_WRITE)
         .setCullState(NO_CULL)
         .createCompositeState(false)
   );
   private static final Function<Double, RenderType.CompositeRenderType> DEBUG_LINE_STRIP = Util.memoize(
      var0 -> create(
            "debug_line_strip",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.DEBUG_LINE_STRIP,
            1536,
            RenderType.CompositeState.builder()
               .setShaderState(POSITION_COLOR_SHADER)
               .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(var0)))
               .setTransparencyState(NO_TRANSPARENCY)
               .setCullState(NO_CULL)
               .createCompositeState(false)
         )
   );
   private static final RenderType.CompositeRenderType DEBUG_FILLED_BOX = create(
      "debug_filled_box",
      DefaultVertexFormat.POSITION_COLOR,
      VertexFormat.Mode.TRIANGLE_STRIP,
      1536,
      false,
      true,
      RenderType.CompositeState.builder()
         .setShaderState(POSITION_COLOR_SHADER)
         .setLayeringState(VIEW_OFFSET_Z_LAYERING)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .createCompositeState(false)
   );
   private static final RenderType.CompositeRenderType DEBUG_QUADS = create(
      "debug_quads",
      DefaultVertexFormat.POSITION_COLOR,
      VertexFormat.Mode.QUADS,
      1536,
      false,
      true,
      RenderType.CompositeState.builder()
         .setShaderState(POSITION_COLOR_SHADER)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setCullState(NO_CULL)
         .createCompositeState(false)
   );
   private static final RenderType.CompositeRenderType DEBUG_TRIANGLE_FAN = create(
      "debug_triangle_fan",
      DefaultVertexFormat.POSITION_COLOR,
      VertexFormat.Mode.TRIANGLE_FAN,
      1536,
      false,
      true,
      RenderType.CompositeState.builder()
         .setShaderState(POSITION_COLOR_SHADER)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setCullState(NO_CULL)
         .createCompositeState(false)
   );
   private static final RenderType.CompositeRenderType DEBUG_STRUCTURE_QUADS = create(
      "debug_structure_quads",
      DefaultVertexFormat.POSITION_COLOR,
      VertexFormat.Mode.QUADS,
      1536,
      false,
      true,
      RenderType.CompositeState.builder()
         .setShaderState(POSITION_COLOR_SHADER)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setCullState(NO_CULL)
         .setDepthTestState(LEQUAL_DEPTH_TEST)
         .setWriteMaskState(COLOR_WRITE)
         .createCompositeState(false)
   );
   private static final RenderType.CompositeRenderType DEBUG_SECTION_QUADS = create(
      "debug_section_quads",
      DefaultVertexFormat.POSITION_COLOR,
      VertexFormat.Mode.QUADS,
      1536,
      false,
      true,
      RenderType.CompositeState.builder()
         .setShaderState(POSITION_COLOR_SHADER)
         .setLayeringState(VIEW_OFFSET_Z_LAYERING)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setCullState(CULL)
         .createCompositeState(false)
   );
   private static final RenderType.CompositeRenderType GUI = create(
      "gui",
      DefaultVertexFormat.POSITION_COLOR,
      VertexFormat.Mode.QUADS,
      786432,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_GUI_SHADER)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setDepthTestState(LEQUAL_DEPTH_TEST)
         .createCompositeState(false)
   );
   private static final RenderType.CompositeRenderType GUI_OVERLAY = create(
      "gui_overlay",
      DefaultVertexFormat.POSITION_COLOR,
      VertexFormat.Mode.QUADS,
      1536,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_GUI_OVERLAY_SHADER)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setDepthTestState(NO_DEPTH_TEST)
         .setWriteMaskState(COLOR_WRITE)
         .createCompositeState(false)
   );
   private static final Function<ResourceLocation, RenderType> GUI_TEXTURED_OVERLAY = Util.memoize(
      var0 -> create(
            "gui_textured_overlay",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            RenderType.CompositeState.builder()
               .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.DEFAULT, false))
               .setShaderState(POSITION_TEXTURE_COLOR_SHADER)
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setDepthTestState(NO_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         )
   );
   private static final Function<ResourceLocation, RenderType> GUI_OPAQUE_TEXTURED_BACKGROUND = Util.memoize(
      var0 -> create(
            "gui_opaque_textured_background",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            786432,
            RenderType.CompositeState.builder()
               .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
               .setShaderState(POSITION_TEXTURE_COLOR_SHADER)
               .setTransparencyState(NO_TRANSPARENCY)
               .setDepthTestState(LEQUAL_DEPTH_TEST)
               .createCompositeState(false)
         )
   );
   private static final RenderType.CompositeRenderType GUI_NAUSEA_OVERLAY = create(
      "gui_nausea_overlay",
      DefaultVertexFormat.POSITION_TEX_COLOR,
      VertexFormat.Mode.QUADS,
      1536,
      RenderType.CompositeState.builder()
         .setTextureState(new RenderStateShard.TextureStateShard(Gui.NAUSEA_LOCATION, TriState.DEFAULT, false))
         .setShaderState(POSITION_TEXTURE_COLOR_SHADER)
         .setTransparencyState(NAUSEA_OVERLAY_TRANSPARENCY)
         .setDepthTestState(NO_DEPTH_TEST)
         .setWriteMaskState(COLOR_WRITE)
         .createCompositeState(false)
   );
   private static final RenderType.CompositeRenderType GUI_TEXT_HIGHLIGHT = create(
      "gui_text_highlight",
      DefaultVertexFormat.POSITION_COLOR,
      VertexFormat.Mode.QUADS,
      1536,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_GUI_TEXT_HIGHLIGHT_SHADER)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setDepthTestState(NO_DEPTH_TEST)
         .setColorLogicState(OR_REVERSE_COLOR_LOGIC)
         .createCompositeState(false)
   );
   private static final RenderType.CompositeRenderType GUI_GHOST_RECIPE_OVERLAY = create(
      "gui_ghost_recipe_overlay",
      DefaultVertexFormat.POSITION_COLOR,
      VertexFormat.Mode.QUADS,
      1536,
      RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY_SHADER)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setDepthTestState(GREATER_DEPTH_TEST)
         .setWriteMaskState(COLOR_WRITE)
         .createCompositeState(false)
   );
   private static final Function<ResourceLocation, RenderType> GUI_TEXTURED = Util.memoize(
      var0 -> create(
            "gui_textured",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            786432,
            RenderType.CompositeState.builder()
               .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
               .setShaderState(POSITION_TEXTURE_COLOR_SHADER)
               .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
               .setDepthTestState(LEQUAL_DEPTH_TEST)
               .createCompositeState(false)
         )
   );
   private static final Function<ResourceLocation, RenderType> VIGNETTE = Util.memoize(
      var0 -> create(
            "vignette",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            786432,
            RenderType.CompositeState.builder()
               .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.DEFAULT, false))
               .setShaderState(POSITION_TEXTURE_COLOR_SHADER)
               .setTransparencyState(VIGNETTE_TRANSPARENCY)
               .setDepthTestState(NO_DEPTH_TEST)
               .setWriteMaskState(COLOR_WRITE)
               .createCompositeState(false)
         )
   );
   private static final Function<ResourceLocation, RenderType> CROSSHAIR = Util.memoize(
      var0 -> create(
            "crosshair",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            786432,
            RenderType.CompositeState.builder()
               .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
               .setShaderState(POSITION_TEXTURE_COLOR_SHADER)
               .setTransparencyState(CROSSHAIR_TRANSPARENCY)
               .createCompositeState(false)
         )
   );
   private static final RenderType.CompositeRenderType MOJANG_LOGO = create(
      "mojang_logo",
      DefaultVertexFormat.POSITION_TEX_COLOR,
      VertexFormat.Mode.QUADS,
      786432,
      RenderType.CompositeState.builder()
         .setTextureState(new RenderStateShard.TextureStateShard(LoadingOverlay.MOJANG_STUDIOS_LOGO_LOCATION, TriState.DEFAULT, false))
         .setShaderState(POSITION_TEXTURE_COLOR_SHADER)
         .setTransparencyState(MOJANG_LOGO_TRANSPARENCY)
         .setDepthTestState(NO_DEPTH_TEST)
         .setWriteMaskState(COLOR_WRITE)
         .createCompositeState(false)
   );
   private static final ImmutableList<RenderType> CHUNK_BUFFER_LAYERS = ImmutableList.of(solid(), cutoutMipped(), cutout(), translucent(), tripwire());
   private final VertexFormat format;
   private final VertexFormat.Mode mode;
   private final int bufferSize;
   private final boolean affectsCrumbling;
   private final boolean sortOnUpload;

   public static RenderType solid() {
      return SOLID;
   }

   public static RenderType cutoutMipped() {
      return CUTOUT_MIPPED;
   }

   public static RenderType cutout() {
      return CUTOUT;
   }

   private static RenderType.CompositeState translucentState(RenderStateShard.ShaderStateShard var0) {
      return RenderType.CompositeState.builder()
         .setLightmapState(LIGHTMAP)
         .setShaderState(var0)
         .setTextureState(BLOCK_SHEET_MIPPED)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setOutputState(TRANSLUCENT_TARGET)
         .createCompositeState(true);
   }

   public static RenderType translucent() {
      return TRANSLUCENT;
   }

   private static RenderType.CompositeState translucentMovingBlockState() {
      return RenderType.CompositeState.builder()
         .setLightmapState(LIGHTMAP)
         .setShaderState(RENDERTYPE_TRANSLUCENT_MOVING_BLOCK_SHADER)
         .setTextureState(BLOCK_SHEET_MIPPED)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setOutputState(ITEM_ENTITY_TARGET)
         .createCompositeState(true);
   }

   public static RenderType translucentMovingBlock() {
      return TRANSLUCENT_MOVING_BLOCK;
   }

   private static RenderType.CompositeRenderType createArmorCutoutNoCull(String var0, ResourceLocation var1, boolean var2) {
      RenderType.CompositeState var3 = RenderType.CompositeState.builder()
         .setShaderState(RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
         .setTextureState(new RenderStateShard.TextureStateShard(var1, TriState.FALSE, false))
         .setTransparencyState(NO_TRANSPARENCY)
         .setCullState(NO_CULL)
         .setLightmapState(LIGHTMAP)
         .setOverlayState(OVERLAY)
         .setLayeringState(VIEW_OFFSET_Z_LAYERING)
         .setDepthTestState(var2 ? EQUAL_DEPTH_TEST : LEQUAL_DEPTH_TEST)
         .createCompositeState(true);
      return create(var0, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, var3);
   }

   public static RenderType armorCutoutNoCull(ResourceLocation var0) {
      return ARMOR_CUTOUT_NO_CULL.apply(var0);
   }

   public static RenderType createArmorDecalCutoutNoCull(ResourceLocation var0) {
      return createArmorCutoutNoCull("armor_decal_cutout_no_cull", var0, true);
   }

   public static RenderType entitySolid(ResourceLocation var0) {
      return ENTITY_SOLID.apply(var0);
   }

   public static RenderType entitySolidZOffsetForward(ResourceLocation var0) {
      return ENTITY_SOLID_Z_OFFSET_FORWARD.apply(var0);
   }

   public static RenderType entityCutout(ResourceLocation var0) {
      return ENTITY_CUTOUT.apply(var0);
   }

   public static RenderType entityCutoutNoCull(ResourceLocation var0, boolean var1) {
      return ENTITY_CUTOUT_NO_CULL.apply(var0, var1);
   }

   public static RenderType entityCutoutNoCull(ResourceLocation var0) {
      return entityCutoutNoCull(var0, true);
   }

   public static RenderType entityCutoutNoCullZOffset(ResourceLocation var0, boolean var1) {
      return ENTITY_CUTOUT_NO_CULL_Z_OFFSET.apply(var0, var1);
   }

   public static RenderType entityCutoutNoCullZOffset(ResourceLocation var0) {
      return entityCutoutNoCullZOffset(var0, true);
   }

   public static RenderType itemEntityTranslucentCull(ResourceLocation var0) {
      return ITEM_ENTITY_TRANSLUCENT_CULL.apply(var0);
   }

   public static RenderType entityTranslucent(ResourceLocation var0, boolean var1) {
      return ENTITY_TRANSLUCENT.apply(var0, var1);
   }

   public static RenderType entityTranslucent(ResourceLocation var0) {
      return entityTranslucent(var0, true);
   }

   public static RenderType entityTranslucentEmissive(ResourceLocation var0, boolean var1) {
      return ENTITY_TRANSLUCENT_EMISSIVE.apply(var0, var1);
   }

   public static RenderType entityTranslucentEmissive(ResourceLocation var0) {
      return entityTranslucentEmissive(var0, true);
   }

   public static RenderType entitySmoothCutout(ResourceLocation var0) {
      return ENTITY_SMOOTH_CUTOUT.apply(var0);
   }

   public static RenderType beaconBeam(ResourceLocation var0, boolean var1) {
      return BEACON_BEAM.apply(var0, var1);
   }

   public static RenderType entityDecal(ResourceLocation var0) {
      return ENTITY_DECAL.apply(var0);
   }

   public static RenderType entityNoOutline(ResourceLocation var0) {
      return ENTITY_NO_OUTLINE.apply(var0);
   }

   public static RenderType entityShadow(ResourceLocation var0) {
      return ENTITY_SHADOW.apply(var0);
   }

   public static RenderType dragonExplosionAlpha(ResourceLocation var0) {
      return DRAGON_EXPLOSION_ALPHA.apply(var0);
   }

   public static RenderType eyes(ResourceLocation var0) {
      return EYES.apply(var0, TRANSLUCENT_TRANSPARENCY);
   }

   public static RenderType breezeEyes(ResourceLocation var0) {
      return ENTITY_TRANSLUCENT_EMISSIVE.apply(var0, false);
   }

   public static RenderType breezeWind(ResourceLocation var0, float var1, float var2) {
      return create(
         "breeze_wind",
         DefaultVertexFormat.NEW_ENTITY,
         VertexFormat.Mode.QUADS,
         1536,
         false,
         true,
         RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_BREEZE_WIND_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setTexturingState(new RenderStateShard.OffsetTexturingStateShard(var1, var2))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(NO_OVERLAY)
            .createCompositeState(false)
      );
   }

   public static RenderType energySwirl(ResourceLocation var0, float var1, float var2) {
      return create(
         "energy_swirl",
         DefaultVertexFormat.NEW_ENTITY,
         VertexFormat.Mode.QUADS,
         1536,
         false,
         true,
         RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
            .setTexturingState(new RenderStateShard.OffsetTexturingStateShard(var1, var2))
            .setTransparencyState(ADDITIVE_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(false)
      );
   }

   public static RenderType leash() {
      return LEASH;
   }

   public static RenderType waterMask() {
      return WATER_MASK;
   }

   public static RenderType outline(ResourceLocation var0) {
      return RenderType.CompositeRenderType.OUTLINE.apply(var0, NO_CULL);
   }

   public static RenderType armorEntityGlint() {
      return ARMOR_ENTITY_GLINT;
   }

   public static RenderType glintTranslucent() {
      return GLINT_TRANSLUCENT;
   }

   public static RenderType glint() {
      return GLINT;
   }

   public static RenderType entityGlint() {
      return ENTITY_GLINT;
   }

   public static RenderType crumbling(ResourceLocation var0) {
      return CRUMBLING.apply(var0);
   }

   public static RenderType text(ResourceLocation var0) {
      return TEXT.apply(var0);
   }

   public static RenderType textBackground() {
      return TEXT_BACKGROUND;
   }

   public static RenderType textIntensity(ResourceLocation var0) {
      return TEXT_INTENSITY.apply(var0);
   }

   public static RenderType textPolygonOffset(ResourceLocation var0) {
      return TEXT_POLYGON_OFFSET.apply(var0);
   }

   public static RenderType textIntensityPolygonOffset(ResourceLocation var0) {
      return TEXT_INTENSITY_POLYGON_OFFSET.apply(var0);
   }

   public static RenderType textSeeThrough(ResourceLocation var0) {
      return TEXT_SEE_THROUGH.apply(var0);
   }

   public static RenderType textBackgroundSeeThrough() {
      return TEXT_BACKGROUND_SEE_THROUGH;
   }

   public static RenderType textIntensitySeeThrough(ResourceLocation var0) {
      return TEXT_INTENSITY_SEE_THROUGH.apply(var0);
   }

   public static RenderType lightning() {
      return LIGHTNING;
   }

   public static RenderType dragonRays() {
      return DRAGON_RAYS;
   }

   public static RenderType dragonRaysDepth() {
      return DRAGON_RAYS_DEPTH;
   }

   private static RenderType.CompositeState tripwireState() {
      return RenderType.CompositeState.builder()
         .setLightmapState(LIGHTMAP)
         .setShaderState(RENDERTYPE_TRIPWIRE_SHADER)
         .setTextureState(BLOCK_SHEET_MIPPED)
         .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
         .setOutputState(WEATHER_TARGET)
         .createCompositeState(true);
   }

   public static RenderType tripwire() {
      return TRIPWIRE;
   }

   public static RenderType endPortal() {
      return END_PORTAL;
   }

   public static RenderType endGateway() {
      return END_GATEWAY;
   }

   private static RenderType.CompositeRenderType createClouds(boolean var0, boolean var1) {
      return create(
         "clouds",
         DefaultVertexFormat.POSITION_COLOR,
         VertexFormat.Mode.QUADS,
         786432,
         false,
         false,
         RenderType.CompositeState.builder()
            .setShaderState(RENDERTYPE_CLOUDS_SHADER)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(var1 ? CULL : NO_CULL)
            .setWriteMaskState(var0 ? DEPTH_WRITE : COLOR_DEPTH_WRITE)
            .setOutputState(CLOUDS_TARGET)
            .createCompositeState(true)
      );
   }

   public static RenderType flatClouds() {
      return FLAT_CLOUDS;
   }

   public static RenderType clouds() {
      return CLOUDS;
   }

   public static RenderType cloudsDepthOnly() {
      return CLOUDS_DEPTH_ONLY;
   }

   public static RenderType lines() {
      return LINES;
   }

   public static RenderType secondaryBlockOutline() {
      return SECONDARY_BLOCK_OUTLINE;
   }

   public static RenderType lineStrip() {
      return LINE_STRIP;
   }

   public static RenderType debugLineStrip(double var0) {
      return DEBUG_LINE_STRIP.apply(var0);
   }

   public static RenderType debugFilledBox() {
      return DEBUG_FILLED_BOX;
   }

   public static RenderType debugQuads() {
      return DEBUG_QUADS;
   }

   public static RenderType debugTriangleFan() {
      return DEBUG_TRIANGLE_FAN;
   }

   public static RenderType debugStructureQuads() {
      return DEBUG_STRUCTURE_QUADS;
   }

   public static RenderType debugSectionQuads() {
      return DEBUG_SECTION_QUADS;
   }

   public static RenderType gui() {
      return GUI;
   }

   public static RenderType guiOverlay() {
      return GUI_OVERLAY;
   }

   public static RenderType guiTexturedOverlay(ResourceLocation var0) {
      return GUI_TEXTURED_OVERLAY.apply(var0);
   }

   public static RenderType guiOpaqueTexturedBackground(ResourceLocation var0) {
      return GUI_OPAQUE_TEXTURED_BACKGROUND.apply(var0);
   }

   public static RenderType guiNauseaOverlay() {
      return GUI_NAUSEA_OVERLAY;
   }

   public static RenderType guiTextHighlight() {
      return GUI_TEXT_HIGHLIGHT;
   }

   public static RenderType guiGhostRecipeOverlay() {
      return GUI_GHOST_RECIPE_OVERLAY;
   }

   public static RenderType guiTextured(ResourceLocation var0) {
      return GUI_TEXTURED.apply(var0);
   }

   public static RenderType vignette(ResourceLocation var0) {
      return VIGNETTE.apply(var0);
   }

   public static RenderType crosshair(ResourceLocation var0) {
      return CROSSHAIR.apply(var0);
   }

   public static RenderType mojangLogo() {
      return MOJANG_LOGO;
   }

   public RenderType(String var1, VertexFormat var2, VertexFormat.Mode var3, int var4, boolean var5, boolean var6, Runnable var7, Runnable var8) {
      super(var1, var7, var8);
      this.format = var2;
      this.mode = var3;
      this.bufferSize = var4;
      this.affectsCrumbling = var5;
      this.sortOnUpload = var6;
   }

   static RenderType.CompositeRenderType create(String var0, VertexFormat var1, VertexFormat.Mode var2, int var3, RenderType.CompositeState var4) {
      return create(var0, var1, var2, var3, false, false, var4);
   }

   private static RenderType.CompositeRenderType create(
      String var0, VertexFormat var1, VertexFormat.Mode var2, int var3, boolean var4, boolean var5, RenderType.CompositeState var6
   ) {
      return new RenderType.CompositeRenderType(var0, var1, var2, var3, var4, var5, var6);
   }

   public void draw(MeshData var1) {
      this.setupRenderState();
      BufferUploader.drawWithShader(var1);
      this.clearRenderState();
   }

   @Override
   public String toString() {
      return this.name;
   }

   public static List<RenderType> chunkBufferLayers() {
      return CHUNK_BUFFER_LAYERS;
   }

   public int bufferSize() {
      return this.bufferSize;
   }

   public VertexFormat format() {
      return this.format;
   }

   public VertexFormat.Mode mode() {
      return this.mode;
   }

   public Optional<RenderType> outline() {
      return Optional.empty();
   }

   public boolean isOutline() {
      return false;
   }

   public boolean affectsCrumbling() {
      return this.affectsCrumbling;
   }

   public boolean canConsolidateConsecutiveGeometry() {
      return !this.mode.connectedPrimitives;
   }

   public boolean sortOnUpload() {
      return this.sortOnUpload;
   }

   static final class CompositeRenderType extends RenderType {
      static final BiFunction<ResourceLocation, RenderStateShard.CullStateShard, RenderType> OUTLINE = Util.memoize(
         (var0, var1) -> RenderType.create(
               "outline",
               DefaultVertexFormat.POSITION_TEX_COLOR,
               VertexFormat.Mode.QUADS,
               1536,
               RenderType.CompositeState.builder()
                  .setShaderState(RENDERTYPE_OUTLINE_SHADER)
                  .setTextureState(new RenderStateShard.TextureStateShard(var0, TriState.FALSE, false))
                  .setCullState(var1)
                  .setDepthTestState(NO_DEPTH_TEST)
                  .setOutputState(OUTLINE_TARGET)
                  .createCompositeState(RenderType.OutlineProperty.IS_OUTLINE)
            )
      );
      private final RenderType.CompositeState state;
      private final Optional<RenderType> outline;
      private final boolean isOutline;

      CompositeRenderType(String var1, VertexFormat var2, VertexFormat.Mode var3, int var4, boolean var5, boolean var6, RenderType.CompositeState var7) {
         super(
            var1,
            var2,
            var3,
            var4,
            var5,
            var6,
            () -> var7.states.forEach(RenderStateShard::setupRenderState),
            () -> var7.states.forEach(RenderStateShard::clearRenderState)
         );
         this.state = var7;
         this.outline = var7.outlineProperty == RenderType.OutlineProperty.AFFECTS_OUTLINE
            ? var7.textureState.cutoutTexture().map(var1x -> OUTLINE.apply(var1x, var7.cullState))
            : Optional.empty();
         this.isOutline = var7.outlineProperty == RenderType.OutlineProperty.IS_OUTLINE;
      }

      @Override
      public Optional<RenderType> outline() {
         return this.outline;
      }

      @Override
      public boolean isOutline() {
         return this.isOutline;
      }

      protected final RenderType.CompositeState state() {
         return this.state;
      }

      @Override
      public String toString() {
         return "RenderType[" + this.name + ":" + this.state + "]";
      }
   }

   protected static final class CompositeState {
      final RenderStateShard.EmptyTextureStateShard textureState;
      private final RenderStateShard.ShaderStateShard shaderState;
      private final RenderStateShard.TransparencyStateShard transparencyState;
      private final RenderStateShard.DepthTestStateShard depthTestState;
      final RenderStateShard.CullStateShard cullState;
      private final RenderStateShard.LightmapStateShard lightmapState;
      private final RenderStateShard.OverlayStateShard overlayState;
      private final RenderStateShard.LayeringStateShard layeringState;
      private final RenderStateShard.OutputStateShard outputState;
      private final RenderStateShard.TexturingStateShard texturingState;
      private final RenderStateShard.WriteMaskStateShard writeMaskState;
      private final RenderStateShard.LineStateShard lineState;
      private final RenderStateShard.ColorLogicStateShard colorLogicState;
      final RenderType.OutlineProperty outlineProperty;
      final ImmutableList<RenderStateShard> states;

      CompositeState(
         RenderStateShard.EmptyTextureStateShard var1,
         RenderStateShard.ShaderStateShard var2,
         RenderStateShard.TransparencyStateShard var3,
         RenderStateShard.DepthTestStateShard var4,
         RenderStateShard.CullStateShard var5,
         RenderStateShard.LightmapStateShard var6,
         RenderStateShard.OverlayStateShard var7,
         RenderStateShard.LayeringStateShard var8,
         RenderStateShard.OutputStateShard var9,
         RenderStateShard.TexturingStateShard var10,
         RenderStateShard.WriteMaskStateShard var11,
         RenderStateShard.LineStateShard var12,
         RenderStateShard.ColorLogicStateShard var13,
         RenderType.OutlineProperty var14
      ) {
         super();
         this.textureState = var1;
         this.shaderState = var2;
         this.transparencyState = var3;
         this.depthTestState = var4;
         this.cullState = var5;
         this.lightmapState = var6;
         this.overlayState = var7;
         this.layeringState = var8;
         this.outputState = var9;
         this.texturingState = var10;
         this.writeMaskState = var11;
         this.lineState = var12;
         this.colorLogicState = var13;
         this.outlineProperty = var14;
         this.states = ImmutableList.of(
            this.textureState,
            this.shaderState,
            this.transparencyState,
            this.depthTestState,
            this.cullState,
            this.lightmapState,
            this.overlayState,
            this.layeringState,
            this.outputState,
            this.texturingState,
            this.writeMaskState,
            this.colorLogicState,
            new RenderStateShard[]{this.lineState}
         );
      }

      @Override
      public String toString() {
         return "CompositeState[" + this.states + ", outlineProperty=" + this.outlineProperty + "]";
      }

      public static RenderType.CompositeState.CompositeStateBuilder builder() {
         return new RenderType.CompositeState.CompositeStateBuilder();
      }

      public static class CompositeStateBuilder {
         private RenderStateShard.EmptyTextureStateShard textureState = RenderStateShard.NO_TEXTURE;
         private RenderStateShard.ShaderStateShard shaderState = RenderStateShard.NO_SHADER;
         private RenderStateShard.TransparencyStateShard transparencyState = RenderStateShard.NO_TRANSPARENCY;
         private RenderStateShard.DepthTestStateShard depthTestState = RenderStateShard.LEQUAL_DEPTH_TEST;
         private RenderStateShard.CullStateShard cullState = RenderStateShard.CULL;
         private RenderStateShard.LightmapStateShard lightmapState = RenderStateShard.NO_LIGHTMAP;
         private RenderStateShard.OverlayStateShard overlayState = RenderStateShard.NO_OVERLAY;
         private RenderStateShard.LayeringStateShard layeringState = RenderStateShard.NO_LAYERING;
         private RenderStateShard.OutputStateShard outputState = RenderStateShard.MAIN_TARGET;
         private RenderStateShard.TexturingStateShard texturingState = RenderStateShard.DEFAULT_TEXTURING;
         private RenderStateShard.WriteMaskStateShard writeMaskState = RenderStateShard.COLOR_DEPTH_WRITE;
         private RenderStateShard.LineStateShard lineState = RenderStateShard.DEFAULT_LINE;
         private RenderStateShard.ColorLogicStateShard colorLogicState = RenderStateShard.NO_COLOR_LOGIC;

         CompositeStateBuilder() {
            super();
         }

         public RenderType.CompositeState.CompositeStateBuilder setTextureState(RenderStateShard.EmptyTextureStateShard var1) {
            this.textureState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setShaderState(RenderStateShard.ShaderStateShard var1) {
            this.shaderState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setTransparencyState(RenderStateShard.TransparencyStateShard var1) {
            this.transparencyState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setDepthTestState(RenderStateShard.DepthTestStateShard var1) {
            this.depthTestState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setCullState(RenderStateShard.CullStateShard var1) {
            this.cullState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setLightmapState(RenderStateShard.LightmapStateShard var1) {
            this.lightmapState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setOverlayState(RenderStateShard.OverlayStateShard var1) {
            this.overlayState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setLayeringState(RenderStateShard.LayeringStateShard var1) {
            this.layeringState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setOutputState(RenderStateShard.OutputStateShard var1) {
            this.outputState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setTexturingState(RenderStateShard.TexturingStateShard var1) {
            this.texturingState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setWriteMaskState(RenderStateShard.WriteMaskStateShard var1) {
            this.writeMaskState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setLineState(RenderStateShard.LineStateShard var1) {
            this.lineState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setColorLogicState(RenderStateShard.ColorLogicStateShard var1) {
            this.colorLogicState = var1;
            return this;
         }

         public RenderType.CompositeState createCompositeState(boolean var1) {
            return this.createCompositeState(var1 ? RenderType.OutlineProperty.AFFECTS_OUTLINE : RenderType.OutlineProperty.NONE);
         }

         public RenderType.CompositeState createCompositeState(RenderType.OutlineProperty var1) {
            return new RenderType.CompositeState(
               this.textureState,
               this.shaderState,
               this.transparencyState,
               this.depthTestState,
               this.cullState,
               this.lightmapState,
               this.overlayState,
               this.layeringState,
               this.outputState,
               this.texturingState,
               this.writeMaskState,
               this.lineState,
               this.colorLogicState,
               var1
            );
         }
      }
   }

   static enum OutlineProperty {
      NONE("none"),
      IS_OUTLINE("is_outline"),
      AFFECTS_OUTLINE("affects_outline");

      private final String name;

      private OutlineProperty(final String nullxx) {
         this.name = nullxx;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
