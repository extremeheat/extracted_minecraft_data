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
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;

public abstract class RenderType extends RenderStateShard {
   private static final int MEGABYTE = 1048576;
   public static final int BIG_BUFFER_SIZE = 4194304;
   public static final int SMALL_BUFFER_SIZE = 786432;
   public static final int TRANSIENT_BUFFER_SIZE = 1536;
   private static final RenderType SOLID;
   private static final RenderType CUTOUT_MIPPED;
   private static final RenderType CUTOUT;
   private static final RenderType TRANSLUCENT;
   private static final RenderType TRANSLUCENT_MOVING_BLOCK;
   private static final Function<ResourceLocation, RenderType> ARMOR_CUTOUT_NO_CULL;
   private static final Function<ResourceLocation, RenderType> ENTITY_SOLID;
   private static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT;
   private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL;
   private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL_Z_OFFSET;
   private static final Function<ResourceLocation, RenderType> ITEM_ENTITY_TRANSLUCENT_CULL;
   private static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_CULL;
   private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT;
   private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE;
   private static final Function<ResourceLocation, RenderType> ENTITY_SMOOTH_CUTOUT;
   private static final BiFunction<ResourceLocation, Boolean, RenderType> BEACON_BEAM;
   private static final Function<ResourceLocation, RenderType> ENTITY_DECAL;
   private static final Function<ResourceLocation, RenderType> ENTITY_NO_OUTLINE;
   private static final Function<ResourceLocation, RenderType> ENTITY_SHADOW;
   private static final Function<ResourceLocation, RenderType> DRAGON_EXPLOSION_ALPHA;
   private static final BiFunction<ResourceLocation, RenderStateShard.TransparencyStateShard, RenderType> EYES;
   private static final RenderType LEASH;
   private static final RenderType WATER_MASK;
   private static final RenderType ARMOR_ENTITY_GLINT;
   private static final RenderType GLINT_TRANSLUCENT;
   private static final RenderType GLINT;
   private static final RenderType ENTITY_GLINT;
   private static final RenderType ENTITY_GLINT_DIRECT;
   private static final Function<ResourceLocation, RenderType> CRUMBLING;
   private static final Function<ResourceLocation, RenderType> TEXT;
   private static final RenderType TEXT_BACKGROUND;
   private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY;
   private static final Function<ResourceLocation, RenderType> TEXT_POLYGON_OFFSET;
   private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY_POLYGON_OFFSET;
   private static final Function<ResourceLocation, RenderType> TEXT_SEE_THROUGH;
   private static final RenderType TEXT_BACKGROUND_SEE_THROUGH;
   private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY_SEE_THROUGH;
   private static final RenderType LIGHTNING;
   private static final RenderType DRAGON_RAYS;
   private static final RenderType DRAGON_RAYS_DEPTH;
   private static final RenderType TRIPWIRE;
   private static final RenderType END_PORTAL;
   private static final RenderType END_GATEWAY;
   private static final RenderType CLOUDS;
   private static final RenderType CLOUDS_DEPTH_ONLY;
   public static final CompositeRenderType LINES;
   public static final CompositeRenderType LINE_STRIP;
   private static final Function<Double, CompositeRenderType> DEBUG_LINE_STRIP;
   private static final CompositeRenderType DEBUG_FILLED_BOX;
   private static final CompositeRenderType DEBUG_QUADS;
   private static final CompositeRenderType DEBUG_STRUCTURE_QUADS;
   private static final CompositeRenderType DEBUG_SECTION_QUADS;
   private static final CompositeRenderType GUI;
   private static final CompositeRenderType GUI_OVERLAY;
   private static final CompositeRenderType GUI_TEXT_HIGHLIGHT;
   private static final CompositeRenderType GUI_GHOST_RECIPE_OVERLAY;
   private static final ImmutableList<RenderType> CHUNK_BUFFER_LAYERS;
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

   private static CompositeState translucentState(RenderStateShard.ShaderStateShard var0) {
      return RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(var0).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(TRANSLUCENT_TARGET).createCompositeState(true);
   }

   public static RenderType translucent() {
      return TRANSLUCENT;
   }

   private static CompositeState translucentMovingBlockState() {
      return RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_TRANSLUCENT_MOVING_BLOCK_SHADER).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(true);
   }

   public static RenderType translucentMovingBlock() {
      return TRANSLUCENT_MOVING_BLOCK;
   }

   private static CompositeRenderType createArmorCutoutNoCull(String var0, ResourceLocation var1, boolean var2) {
      CompositeState var3 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var1, false, false)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).setDepthTestState(var2 ? EQUAL_DEPTH_TEST : LEQUAL_DEPTH_TEST).createCompositeState(true);
      return create(var0, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, var3);
   }

   public static RenderType armorCutoutNoCull(ResourceLocation var0) {
      return (RenderType)ARMOR_CUTOUT_NO_CULL.apply(var0);
   }

   public static RenderType createArmorDecalCutoutNoCull(ResourceLocation var0) {
      return createArmorCutoutNoCull("armor_decal_cutout_no_cull", var0, true);
   }

   public static RenderType entitySolid(ResourceLocation var0) {
      return (RenderType)ENTITY_SOLID.apply(var0);
   }

   public static RenderType entityCutout(ResourceLocation var0) {
      return (RenderType)ENTITY_CUTOUT.apply(var0);
   }

   public static RenderType entityCutoutNoCull(ResourceLocation var0, boolean var1) {
      return (RenderType)ENTITY_CUTOUT_NO_CULL.apply(var0, var1);
   }

   public static RenderType entityCutoutNoCull(ResourceLocation var0) {
      return entityCutoutNoCull(var0, true);
   }

   public static RenderType entityCutoutNoCullZOffset(ResourceLocation var0, boolean var1) {
      return (RenderType)ENTITY_CUTOUT_NO_CULL_Z_OFFSET.apply(var0, var1);
   }

   public static RenderType entityCutoutNoCullZOffset(ResourceLocation var0) {
      return entityCutoutNoCullZOffset(var0, true);
   }

   public static RenderType itemEntityTranslucentCull(ResourceLocation var0) {
      return (RenderType)ITEM_ENTITY_TRANSLUCENT_CULL.apply(var0);
   }

   public static RenderType entityTranslucentCull(ResourceLocation var0) {
      return (RenderType)ENTITY_TRANSLUCENT_CULL.apply(var0);
   }

   public static RenderType entityTranslucent(ResourceLocation var0, boolean var1) {
      return (RenderType)ENTITY_TRANSLUCENT.apply(var0, var1);
   }

   public static RenderType entityTranslucent(ResourceLocation var0) {
      return entityTranslucent(var0, true);
   }

   public static RenderType entityTranslucentEmissive(ResourceLocation var0, boolean var1) {
      return (RenderType)ENTITY_TRANSLUCENT_EMISSIVE.apply(var0, var1);
   }

   public static RenderType entityTranslucentEmissive(ResourceLocation var0) {
      return entityTranslucentEmissive(var0, true);
   }

   public static RenderType entitySmoothCutout(ResourceLocation var0) {
      return (RenderType)ENTITY_SMOOTH_CUTOUT.apply(var0);
   }

   public static RenderType beaconBeam(ResourceLocation var0, boolean var1) {
      return (RenderType)BEACON_BEAM.apply(var0, var1);
   }

   public static RenderType entityDecal(ResourceLocation var0) {
      return (RenderType)ENTITY_DECAL.apply(var0);
   }

   public static RenderType entityNoOutline(ResourceLocation var0) {
      return (RenderType)ENTITY_NO_OUTLINE.apply(var0);
   }

   public static RenderType entityShadow(ResourceLocation var0) {
      return (RenderType)ENTITY_SHADOW.apply(var0);
   }

   public static RenderType dragonExplosionAlpha(ResourceLocation var0) {
      return (RenderType)DRAGON_EXPLOSION_ALPHA.apply(var0);
   }

   public static RenderType eyes(ResourceLocation var0) {
      return (RenderType)EYES.apply(var0, ADDITIVE_TRANSPARENCY);
   }

   public static RenderType breezeEyes(ResourceLocation var0) {
      return (RenderType)ENTITY_TRANSLUCENT_EMISSIVE.apply(var0, false);
   }

   public static RenderType breezeWind(ResourceLocation var0, float var1, float var2) {
      return create("breeze_wind", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_BREEZE_WIND_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard(var1, var2)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(NO_OVERLAY).createCompositeState(false));
   }

   public static RenderType energySwirl(ResourceLocation var0, float var1, float var2) {
      return create("energy_swirl", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard(var1, var2)).setTransparencyState(ADDITIVE_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
   }

   public static RenderType leash() {
      return LEASH;
   }

   public static RenderType waterMask() {
      return WATER_MASK;
   }

   public static RenderType outline(ResourceLocation var0) {
      return (RenderType)RenderType.CompositeRenderType.OUTLINE.apply(var0, NO_CULL);
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

   public static RenderType entityGlintDirect() {
      return ENTITY_GLINT_DIRECT;
   }

   public static RenderType crumbling(ResourceLocation var0) {
      return (RenderType)CRUMBLING.apply(var0);
   }

   public static RenderType text(ResourceLocation var0) {
      return (RenderType)TEXT.apply(var0);
   }

   public static RenderType textBackground() {
      return TEXT_BACKGROUND;
   }

   public static RenderType textIntensity(ResourceLocation var0) {
      return (RenderType)TEXT_INTENSITY.apply(var0);
   }

   public static RenderType textPolygonOffset(ResourceLocation var0) {
      return (RenderType)TEXT_POLYGON_OFFSET.apply(var0);
   }

   public static RenderType textIntensityPolygonOffset(ResourceLocation var0) {
      return (RenderType)TEXT_INTENSITY_POLYGON_OFFSET.apply(var0);
   }

   public static RenderType textSeeThrough(ResourceLocation var0) {
      return (RenderType)TEXT_SEE_THROUGH.apply(var0);
   }

   public static RenderType textBackgroundSeeThrough() {
      return TEXT_BACKGROUND_SEE_THROUGH;
   }

   public static RenderType textIntensitySeeThrough(ResourceLocation var0) {
      return (RenderType)TEXT_INTENSITY_SEE_THROUGH.apply(var0);
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

   private static CompositeState tripwireState() {
      return RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_TRIPWIRE_SHADER).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(WEATHER_TARGET).createCompositeState(true);
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

   private static CompositeRenderType createClouds(boolean var0) {
      return create("clouds", DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL, VertexFormat.Mode.QUADS, 786432, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_CLOUDS_SHADER).setTextureState(new RenderStateShard.TextureStateShard(LevelRenderer.CLOUDS_LOCATION, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(var0 ? DEPTH_WRITE : COLOR_DEPTH_WRITE).setOutputState(CLOUDS_TARGET).createCompositeState(true));
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

   public static RenderType lineStrip() {
      return LINE_STRIP;
   }

   public static RenderType debugLineStrip(double var0) {
      return (RenderType)DEBUG_LINE_STRIP.apply(var0);
   }

   public static RenderType debugFilledBox() {
      return DEBUG_FILLED_BOX;
   }

   public static RenderType debugQuads() {
      return DEBUG_QUADS;
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

   public static RenderType guiTextHighlight() {
      return GUI_TEXT_HIGHLIGHT;
   }

   public static RenderType guiGhostRecipeOverlay() {
      return GUI_GHOST_RECIPE_OVERLAY;
   }

   public RenderType(String var1, VertexFormat var2, VertexFormat.Mode var3, int var4, boolean var5, boolean var6, Runnable var7, Runnable var8) {
      super(var1, var7, var8);
      this.format = var2;
      this.mode = var3;
      this.bufferSize = var4;
      this.affectsCrumbling = var5;
      this.sortOnUpload = var6;
   }

   static CompositeRenderType create(String var0, VertexFormat var1, VertexFormat.Mode var2, int var3, CompositeState var4) {
      return create(var0, var1, var2, var3, false, false, var4);
   }

   private static CompositeRenderType create(String var0, VertexFormat var1, VertexFormat.Mode var2, int var3, boolean var4, boolean var5, CompositeState var6) {
      return new CompositeRenderType(var0, var1, var2, var3, var4, var5, var6);
   }

   public void draw(MeshData var1) {
      this.setupRenderState();
      BufferUploader.drawWithShader(var1);
      this.clearRenderState();
   }

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

   static {
      SOLID = create("solid", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 4194304, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_SOLID_SHADER).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true));
      CUTOUT_MIPPED = create("cutout_mipped", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 4194304, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_CUTOUT_MIPPED_SHADER).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true));
      CUTOUT = create("cutout", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 786432, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_CUTOUT_SHADER).setTextureState(BLOCK_SHEET).createCompositeState(true));
      TRANSLUCENT = create("translucent", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 786432, true, true, translucentState(RENDERTYPE_TRANSLUCENT_SHADER));
      TRANSLUCENT_MOVING_BLOCK = create("translucent_moving_block", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 786432, false, true, translucentMovingBlockState());
      ARMOR_CUTOUT_NO_CULL = Util.memoize((var0) -> {
         return createArmorCutoutNoCull("armor_cutout_no_cull", var0, false);
      });
      ENTITY_SOLID = Util.memoize((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_SOLID_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
         return create("entity_solid", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, var1);
      });
      ENTITY_CUTOUT = Util.memoize((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_CUTOUT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(NO_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
         return create("entity_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, var1);
      });
      ENTITY_CUTOUT_NO_CULL = Util.memoize((var0, var1) -> {
         CompositeState var2 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(var1);
         return create("entity_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, var2);
      });
      ENTITY_CUTOUT_NO_CULL_Z_OFFSET = Util.memoize((var0, var1) -> {
         CompositeState var2 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(var1);
         return create("entity_cutout_no_cull_z_offset", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, var2);
      });
      ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE).createCompositeState(true);
         return create("item_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, var1);
      });
      ENTITY_TRANSLUCENT_CULL = Util.memoize((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
         return create("entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, var1);
      });
      ENTITY_TRANSLUCENT = Util.memoize((var0, var1) -> {
         CompositeState var2 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(var1);
         return create("entity_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, var2);
      });
      ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize((var0, var1) -> {
         CompositeState var2 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).setOverlayState(OVERLAY).createCompositeState(var1);
         return create("entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, var2);
      });
      ENTITY_SMOOTH_CUTOUT = Util.memoize((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_SMOOTH_CUTOUT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setCullState(NO_CULL).setLightmapState(LIGHTMAP).createCompositeState(true);
         return create("entity_smooth_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, var1);
      });
      BEACON_BEAM = Util.memoize((var0, var1) -> {
         CompositeState var2 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_BEACON_BEAM_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(var1 ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY).setWriteMaskState(var1 ? COLOR_WRITE : COLOR_DEPTH_WRITE).createCompositeState(false);
         return create("beacon_beam", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, true, var2);
      });
      ENTITY_DECAL = Util.memoize((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_DECAL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setDepthTestState(EQUAL_DEPTH_TEST).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false);
         return create("entity_decal", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, var1);
      });
      ENTITY_NO_OUTLINE = Util.memoize((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_NO_OUTLINE_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).createCompositeState(false);
         return create("entity_no_outline", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, var1);
      });
      ENTITY_SHADOW = Util.memoize((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_SHADOW_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).setDepthTestState(LEQUAL_DEPTH_TEST).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false);
         return create("entity_shadow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, false, var1);
      });
      DRAGON_EXPLOSION_ALPHA = Util.memoize((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_ALPHA_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setCullState(NO_CULL).createCompositeState(true);
         return create("entity_alpha", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, var1);
      });
      EYES = Util.memoize((var0, var1) -> {
         RenderStateShard.TextureStateShard var2 = new RenderStateShard.TextureStateShard(var0, false, false);
         return create("eyes", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_EYES_SHADER).setTextureState(var2).setTransparencyState(var1).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
      });
      LEASH = create("leash", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.TRIANGLE_STRIP, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_LEASH_SHADER).setTextureState(NO_TEXTURE).setCullState(NO_CULL).setLightmapState(LIGHTMAP).createCompositeState(false));
      WATER_MASK = create("water_mask", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_WATER_MASK_SHADER).setTextureState(NO_TEXTURE).setWriteMaskState(DEPTH_WRITE).createCompositeState(false));
      ARMOR_ENTITY_GLINT = create("armor_entity_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ENTITY, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
      GLINT_TRANSLUCENT = create("glint_translucent", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_GLINT_TRANSLUCENT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(false));
      GLINT = create("glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).createCompositeState(false));
      ENTITY_GLINT = create("entity_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ENTITY, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));
      ENTITY_GLINT_DIRECT = create("entity_glint_direct", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ENTITY, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));
      CRUMBLING = Util.memoize((var0) -> {
         RenderStateShard.TextureStateShard var1 = new RenderStateShard.TextureStateShard(var0, false, false);
         return create("crumbling", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_CRUMBLING_SHADER).setTextureState(var1).setTransparencyState(CRUMBLING_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setLayeringState(POLYGON_OFFSET_LAYERING).createCompositeState(false));
      });
      TEXT = Util.memoize((var0) -> {
         return create("text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 786432, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_TEXT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(false));
      });
      TEXT_BACKGROUND = create("text_background", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_TEXT_BACKGROUND_SHADER).setTextureState(NO_TEXTURE).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(false));
      TEXT_INTENSITY = Util.memoize((var0) -> {
         return create("text_intensity", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 786432, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_TEXT_INTENSITY_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(false));
      });
      TEXT_POLYGON_OFFSET = Util.memoize((var0) -> {
         return create("text_polygon_offset", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_TEXT_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setLayeringState(POLYGON_OFFSET_LAYERING).createCompositeState(false));
      });
      TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize((var0) -> {
         return create("text_intensity_polygon_offset", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_TEXT_INTENSITY_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setLayeringState(POLYGON_OFFSET_LAYERING).createCompositeState(false));
      });
      TEXT_SEE_THROUGH = Util.memoize((var0) -> {
         return create("text_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_TEXT_SEE_THROUGH_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setDepthTestState(NO_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
      });
      TEXT_BACKGROUND_SEE_THROUGH = create("text_background_see_through", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_TEXT_BACKGROUND_SEE_THROUGH_SHADER).setTextureState(NO_TEXTURE).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setDepthTestState(NO_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
      TEXT_INTENSITY_SEE_THROUGH = Util.memoize((var0) -> {
         return create("text_intensity_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setDepthTestState(NO_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
      });
      LIGHTNING = create("lightning", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setWriteMaskState(COLOR_DEPTH_WRITE).setTransparencyState(LIGHTNING_TRANSPARENCY).setOutputState(WEATHER_TARGET).createCompositeState(false));
      DRAGON_RAYS = create("dragon_rays", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 1536, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_LIGHTNING_SHADER).setWriteMaskState(COLOR_WRITE).setTransparencyState(LIGHTNING_TRANSPARENCY).createCompositeState(false));
      DRAGON_RAYS_DEPTH = create("dragon_rays_depth", DefaultVertexFormat.POSITION, VertexFormat.Mode.TRIANGLES, 1536, false, false, RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_SHADER).setWriteMaskState(DEPTH_WRITE).createCompositeState(false));
      TRIPWIRE = create("tripwire", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, true, true, tripwireState());
      END_PORTAL = create("end_portal", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 1536, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_END_PORTAL_SHADER).setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(TheEndPortalRenderer.END_SKY_LOCATION, false, false).add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build()).createCompositeState(false));
      END_GATEWAY = create("end_gateway", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 1536, false, false, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_END_GATEWAY_SHADER).setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(TheEndPortalRenderer.END_SKY_LOCATION, false, false).add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build()).createCompositeState(false));
      CLOUDS = createClouds(false);
      CLOUDS_DEPTH_ONLY = createClouds(true);
      LINES = create("lines", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(VIEW_OFFSET_Z_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).createCompositeState(false));
      LINE_STRIP = create("line_strip", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINE_STRIP, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_LINES_SHADER).setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(VIEW_OFFSET_Z_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setOutputState(ITEM_ENTITY_TARGET).setWriteMaskState(COLOR_DEPTH_WRITE).setCullState(NO_CULL).createCompositeState(false));
      DEBUG_LINE_STRIP = Util.memoize((var0) -> {
         return create("debug_line_strip", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINE_STRIP, 1536, RenderType.CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(var0))).setTransparencyState(NO_TRANSPARENCY).setCullState(NO_CULL).createCompositeState(false));
      });
      DEBUG_FILLED_BOX = create("debug_filled_box", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP, 1536, false, true, RenderType.CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setLayeringState(VIEW_OFFSET_Z_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(false));
      DEBUG_QUADS = create("debug_quads", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).createCompositeState(false));
      DEBUG_STRUCTURE_QUADS = create("debug_structure_quads", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setDepthTestState(LEQUAL_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
      DEBUG_SECTION_QUADS = create("debug_section_quads", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setLayeringState(VIEW_OFFSET_Z_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(CULL).createCompositeState(false));
      GUI = create("gui", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 786432, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_GUI_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(false));
      GUI_OVERLAY = create("gui_overlay", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_GUI_OVERLAY_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDepthTestState(NO_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
      GUI_TEXT_HIGHLIGHT = create("gui_text_highlight", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_GUI_TEXT_HIGHLIGHT_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDepthTestState(NO_DEPTH_TEST).setColorLogicState(OR_REVERSE_COLOR_LOGIC).createCompositeState(false));
      GUI_GHOST_RECIPE_OVERLAY = create("gui_ghost_recipe_overlay", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_GUI_GHOST_RECIPE_OVERLAY_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDepthTestState(GREATER_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
      CHUNK_BUFFER_LAYERS = ImmutableList.of(solid(), cutoutMipped(), cutout(), translucent(), tripwire());
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
      final OutlineProperty outlineProperty;
      final ImmutableList<RenderStateShard> states;

      CompositeState(RenderStateShard.EmptyTextureStateShard var1, RenderStateShard.ShaderStateShard var2, RenderStateShard.TransparencyStateShard var3, RenderStateShard.DepthTestStateShard var4, RenderStateShard.CullStateShard var5, RenderStateShard.LightmapStateShard var6, RenderStateShard.OverlayStateShard var7, RenderStateShard.LayeringStateShard var8, RenderStateShard.OutputStateShard var9, RenderStateShard.TexturingStateShard var10, RenderStateShard.WriteMaskStateShard var11, RenderStateShard.LineStateShard var12, RenderStateShard.ColorLogicStateShard var13, OutlineProperty var14) {
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
         this.states = ImmutableList.of(this.textureState, this.shaderState, this.transparencyState, this.depthTestState, this.cullState, this.lightmapState, this.overlayState, this.layeringState, this.outputState, this.texturingState, this.writeMaskState, this.colorLogicState, new RenderStateShard[]{this.lineState});
      }

      public String toString() {
         String var10000 = String.valueOf(this.states);
         return "CompositeState[" + var10000 + ", outlineProperty=" + String.valueOf(this.outlineProperty) + "]";
      }

      public static CompositeStateBuilder builder() {
         return new CompositeStateBuilder();
      }

      public static class CompositeStateBuilder {
         private RenderStateShard.EmptyTextureStateShard textureState;
         private RenderStateShard.ShaderStateShard shaderState;
         private RenderStateShard.TransparencyStateShard transparencyState;
         private RenderStateShard.DepthTestStateShard depthTestState;
         private RenderStateShard.CullStateShard cullState;
         private RenderStateShard.LightmapStateShard lightmapState;
         private RenderStateShard.OverlayStateShard overlayState;
         private RenderStateShard.LayeringStateShard layeringState;
         private RenderStateShard.OutputStateShard outputState;
         private RenderStateShard.TexturingStateShard texturingState;
         private RenderStateShard.WriteMaskStateShard writeMaskState;
         private RenderStateShard.LineStateShard lineState;
         private RenderStateShard.ColorLogicStateShard colorLogicState;

         CompositeStateBuilder() {
            super();
            this.textureState = RenderStateShard.NO_TEXTURE;
            this.shaderState = RenderStateShard.NO_SHADER;
            this.transparencyState = RenderStateShard.NO_TRANSPARENCY;
            this.depthTestState = RenderStateShard.LEQUAL_DEPTH_TEST;
            this.cullState = RenderStateShard.CULL;
            this.lightmapState = RenderStateShard.NO_LIGHTMAP;
            this.overlayState = RenderStateShard.NO_OVERLAY;
            this.layeringState = RenderStateShard.NO_LAYERING;
            this.outputState = RenderStateShard.MAIN_TARGET;
            this.texturingState = RenderStateShard.DEFAULT_TEXTURING;
            this.writeMaskState = RenderStateShard.COLOR_DEPTH_WRITE;
            this.lineState = RenderStateShard.DEFAULT_LINE;
            this.colorLogicState = RenderStateShard.NO_COLOR_LOGIC;
         }

         public CompositeStateBuilder setTextureState(RenderStateShard.EmptyTextureStateShard var1) {
            this.textureState = var1;
            return this;
         }

         public CompositeStateBuilder setShaderState(RenderStateShard.ShaderStateShard var1) {
            this.shaderState = var1;
            return this;
         }

         public CompositeStateBuilder setTransparencyState(RenderStateShard.TransparencyStateShard var1) {
            this.transparencyState = var1;
            return this;
         }

         public CompositeStateBuilder setDepthTestState(RenderStateShard.DepthTestStateShard var1) {
            this.depthTestState = var1;
            return this;
         }

         public CompositeStateBuilder setCullState(RenderStateShard.CullStateShard var1) {
            this.cullState = var1;
            return this;
         }

         public CompositeStateBuilder setLightmapState(RenderStateShard.LightmapStateShard var1) {
            this.lightmapState = var1;
            return this;
         }

         public CompositeStateBuilder setOverlayState(RenderStateShard.OverlayStateShard var1) {
            this.overlayState = var1;
            return this;
         }

         public CompositeStateBuilder setLayeringState(RenderStateShard.LayeringStateShard var1) {
            this.layeringState = var1;
            return this;
         }

         public CompositeStateBuilder setOutputState(RenderStateShard.OutputStateShard var1) {
            this.outputState = var1;
            return this;
         }

         public CompositeStateBuilder setTexturingState(RenderStateShard.TexturingStateShard var1) {
            this.texturingState = var1;
            return this;
         }

         public CompositeStateBuilder setWriteMaskState(RenderStateShard.WriteMaskStateShard var1) {
            this.writeMaskState = var1;
            return this;
         }

         public CompositeStateBuilder setLineState(RenderStateShard.LineStateShard var1) {
            this.lineState = var1;
            return this;
         }

         public CompositeStateBuilder setColorLogicState(RenderStateShard.ColorLogicStateShard var1) {
            this.colorLogicState = var1;
            return this;
         }

         public CompositeState createCompositeState(boolean var1) {
            return this.createCompositeState(var1 ? RenderType.OutlineProperty.AFFECTS_OUTLINE : RenderType.OutlineProperty.NONE);
         }

         public CompositeState createCompositeState(OutlineProperty var1) {
            return new CompositeState(this.textureState, this.shaderState, this.transparencyState, this.depthTestState, this.cullState, this.lightmapState, this.overlayState, this.layeringState, this.outputState, this.texturingState, this.writeMaskState, this.lineState, this.colorLogicState, var1);
         }
      }
   }

   private static final class CompositeRenderType extends RenderType {
      static final BiFunction<ResourceLocation, RenderStateShard.CullStateShard, RenderType> OUTLINE = Util.memoize((var0, var1) -> {
         return RenderType.create("outline", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 1536, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_OUTLINE_SHADER).setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setCullState(var1).setDepthTestState(NO_DEPTH_TEST).setOutputState(OUTLINE_TARGET).createCompositeState(RenderType.OutlineProperty.IS_OUTLINE));
      });
      private final CompositeState state;
      private final Optional<RenderType> outline;
      private final boolean isOutline;

      CompositeRenderType(String var1, VertexFormat var2, VertexFormat.Mode var3, int var4, boolean var5, boolean var6, CompositeState var7) {
         super(var1, var2, var3, var4, var5, var6, () -> {
            var7.states.forEach(RenderStateShard::setupRenderState);
         }, () -> {
            var7.states.forEach(RenderStateShard::clearRenderState);
         });
         this.state = var7;
         this.outline = var7.outlineProperty == RenderType.OutlineProperty.AFFECTS_OUTLINE ? var7.textureState.cutoutTexture().map((var1x) -> {
            return (RenderType)OUTLINE.apply(var1x, var7.cullState);
         }) : Optional.empty();
         this.isOutline = var7.outlineProperty == RenderType.OutlineProperty.IS_OUTLINE;
      }

      public Optional<RenderType> outline() {
         return this.outline;
      }

      public boolean isOutline() {
         return this.isOutline;
      }

      protected final CompositeState state() {
         return this.state;
      }

      public String toString() {
         String var10000 = this.name;
         return "RenderType[" + var10000 + ":" + String.valueOf(this.state) + "]";
      }
   }

   private static enum OutlineProperty {
      NONE("none"),
      IS_OUTLINE("is_outline"),
      AFFECTS_OUTLINE("affects_outline");

      private final String name;

      private OutlineProperty(final String var3) {
         this.name = var3;
      }

      public String toString() {
         return this.name;
      }

      // $FF: synthetic method
      private static OutlineProperty[] $values() {
         return new OutlineProperty[]{NONE, IS_OUTLINE, AFFECTS_OUTLINE};
      }
   }
}
