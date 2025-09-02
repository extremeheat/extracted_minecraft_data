package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class RenderType extends RenderStateShard {
   private static final int MEGABYTE = 1048576;
   public static final int BIG_BUFFER_SIZE = 4194304;
   public static final int SMALL_BUFFER_SIZE = 786432;
   public static final int TRANSIENT_BUFFER_SIZE = 1536;
   private static final RenderType SOLID;
   private static final RenderType CUTOUT_MIPPED;
   private static final RenderType CUTOUT;
   private static final RenderType TRANSLUCENT_MOVING_BLOCK;
   private static final Function<ResourceLocation, RenderType> ARMOR_CUTOUT_NO_CULL;
   private static final Function<ResourceLocation, RenderType> ARMOR_TRANSLUCENT;
   private static final Function<ResourceLocation, RenderType> ENTITY_SOLID;
   private static final Function<ResourceLocation, RenderType> ENTITY_SOLID_Z_OFFSET_FORWARD;
   private static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT;
   private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL;
   private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL_Z_OFFSET;
   private static final Function<ResourceLocation, RenderType> ITEM_ENTITY_TRANSLUCENT_CULL;
   private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT;
   private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE;
   private static final Function<ResourceLocation, RenderType> ENTITY_SMOOTH_CUTOUT;
   private static final BiFunction<ResourceLocation, Boolean, RenderType> BEACON_BEAM;
   private static final Function<ResourceLocation, RenderType> ENTITY_DECAL;
   private static final Function<ResourceLocation, RenderType> ENTITY_NO_OUTLINE;
   private static final Function<ResourceLocation, RenderType> ENTITY_SHADOW;
   private static final Function<ResourceLocation, RenderType> DRAGON_EXPLOSION_ALPHA;
   private static final Function<ResourceLocation, RenderType> EYES;
   private static final RenderType LEASH;
   private static final RenderType WATER_MASK;
   private static final RenderType ARMOR_ENTITY_GLINT;
   private static final RenderType GLINT_TRANSLUCENT;
   private static final RenderType GLINT;
   private static final RenderType ENTITY_GLINT;
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
   public static final CompositeRenderType LINES;
   public static final CompositeRenderType SECONDARY_BLOCK_OUTLINE;
   public static final CompositeRenderType LINE_STRIP;
   private static final Function<Double, CompositeRenderType> DEBUG_LINE_STRIP;
   private static final CompositeRenderType DEBUG_FILLED_BOX;
   private static final CompositeRenderType DEBUG_QUADS;
   private static final CompositeRenderType DEBUG_TRIANGLE_FAN;
   private static final CompositeRenderType DEBUG_STRUCTURE_QUADS;
   private static final CompositeRenderType DEBUG_SECTION_QUADS;
   private static final Function<ResourceLocation, RenderType> OPAQUE_PARTICLE;
   private static final Function<ResourceLocation, RenderType> TRANSLUCENT_PARTICLE;
   private static final Function<ResourceLocation, RenderType> WEATHER_DEPTH_WRITE;
   private static final Function<ResourceLocation, RenderType> WEATHER_NO_DEPTH_WRITE;
   private static final RenderType SUNRISE_SUNSET;
   private static final Function<ResourceLocation, RenderType> CELESTIAL;
   private static final Function<ResourceLocation, RenderType> BLOCK_SCREEN_EFFECT;
   private static final Function<ResourceLocation, RenderType> FIRE_SCREEN_EFFECT;
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

   public static RenderType translucentMovingBlock() {
      return TRANSLUCENT_MOVING_BLOCK;
   }

   public static RenderType armorCutoutNoCull(ResourceLocation var0) {
      return (RenderType)ARMOR_CUTOUT_NO_CULL.apply(var0);
   }

   public static RenderType createArmorDecalCutoutNoCull(ResourceLocation var0) {
      CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(true);
      return create("armor_decal_cutout_no_cull", 1536, true, false, RenderPipelines.ARMOR_DECAL_CUTOUT_NO_CULL, var1);
   }

   public static RenderType armorTranslucent(ResourceLocation var0) {
      return (RenderType)ARMOR_TRANSLUCENT.apply(var0);
   }

   public static RenderType entitySolid(ResourceLocation var0) {
      return (RenderType)ENTITY_SOLID.apply(var0);
   }

   public static RenderType entitySolidZOffsetForward(ResourceLocation var0) {
      return (RenderType)ENTITY_SOLID_Z_OFFSET_FORWARD.apply(var0);
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
      return (RenderType)EYES.apply(var0);
   }

   public static RenderType breezeEyes(ResourceLocation var0) {
      return (RenderType)ENTITY_TRANSLUCENT_EMISSIVE.apply(var0, false);
   }

   public static RenderType breezeWind(ResourceLocation var0, float var1, float var2) {
      return create("breeze_wind", 1536, false, true, RenderPipelines.BREEZE_WIND, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard(var1, var2)).setLightmapState(LIGHTMAP).setOverlayState(NO_OVERLAY).createCompositeState(false));
   }

   public static RenderType energySwirl(ResourceLocation var0, float var1, float var2) {
      return create("energy_swirl", 1536, false, true, RenderPipelines.ENERGY_SWIRL, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard(var1, var2)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
   }

   public static RenderType leash() {
      return LEASH;
   }

   public static RenderType waterMask() {
      return WATER_MASK;
   }

   public static RenderType outline(ResourceLocation var0) {
      return (RenderType)RenderType.CompositeRenderType.OUTLINE.apply(var0, false);
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

   public static RenderType tripwire() {
      return TRIPWIRE;
   }

   public static RenderType endPortal() {
      return END_PORTAL;
   }

   public static RenderType endGateway() {
      return END_GATEWAY;
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
      return (RenderType)DEBUG_LINE_STRIP.apply(var0);
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

   public static RenderType opaqueParticle(ResourceLocation var0) {
      return (RenderType)OPAQUE_PARTICLE.apply(var0);
   }

   public static RenderType translucentParticle(ResourceLocation var0) {
      return (RenderType)TRANSLUCENT_PARTICLE.apply(var0);
   }

   private static Function<ResourceLocation, RenderType> createWeather(RenderPipeline var0) {
      return Util.memoize((Function)((var1) -> create("weather", 1536, false, false, var0, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var1, false)).setOutputState(WEATHER_TARGET).setLightmapState(LIGHTMAP).createCompositeState(false))));
   }

   public static RenderType weather(ResourceLocation var0, boolean var1) {
      return (RenderType)(var1 ? WEATHER_DEPTH_WRITE : WEATHER_NO_DEPTH_WRITE).apply(var0);
   }

   public static RenderType sunriseSunset() {
      return SUNRISE_SUNSET;
   }

   public static RenderType celestial(ResourceLocation var0) {
      return (RenderType)CELESTIAL.apply(var0);
   }

   public static RenderType blockScreenEffect(ResourceLocation var0) {
      return (RenderType)BLOCK_SCREEN_EFFECT.apply(var0);
   }

   public static RenderType fireScreenEffect(ResourceLocation var0) {
      return (RenderType)FIRE_SCREEN_EFFECT.apply(var0);
   }

   public RenderType(String var1, int var2, boolean var3, boolean var4, Runnable var5, Runnable var6) {
      super(var1, var5, var6);
      this.bufferSize = var2;
      this.affectsCrumbling = var3;
      this.sortOnUpload = var4;
   }

   static CompositeRenderType create(String var0, int var1, RenderPipeline var2, CompositeState var3) {
      return create(var0, var1, false, false, var2, var3);
   }

   private static CompositeRenderType create(String var0, int var1, boolean var2, boolean var3, RenderPipeline var4, CompositeState var5) {
      return new CompositeRenderType(var0, var1, var2, var3, var4, var5);
   }

   public abstract void draw(MeshData var1);

   public int bufferSize() {
      return this.bufferSize;
   }

   public abstract VertexFormat format();

   public abstract VertexFormat.Mode mode();

   public Optional<RenderType> outline() {
      return Optional.empty();
   }

   public boolean isOutline() {
      return false;
   }

   public abstract RenderPipeline pipeline();

   public boolean affectsCrumbling() {
      return this.affectsCrumbling;
   }

   public boolean canConsolidateConsecutiveGeometry() {
      return !this.mode().connectedPrimitives;
   }

   public boolean sortOnUpload() {
      return this.sortOnUpload;
   }

   static {
      SOLID = create("solid", 1536, true, false, RenderPipelines.SOLID, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true));
      CUTOUT_MIPPED = create("cutout_mipped", 1536, true, false, RenderPipelines.CUTOUT_MIPPED, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true));
      CUTOUT = create("cutout", 1536, true, false, RenderPipelines.CUTOUT, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET).createCompositeState(true));
      TRANSLUCENT_MOVING_BLOCK = create("translucent_moving_block", 786432, false, true, RenderPipelines.TRANSLUCENT_MOVING_BLOCK, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(true));
      ARMOR_CUTOUT_NO_CULL = Util.memoize((Function)((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(true);
         return create("armor_cutout_no_cull", 1536, true, false, RenderPipelines.ARMOR_CUTOUT_NO_CULL, var1);
      }));
      ARMOR_TRANSLUCENT = Util.memoize((Function)((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(true);
         return create("armor_translucent", 1536, true, true, RenderPipelines.ARMOR_TRANSLUCENT, var1);
      }));
      ENTITY_SOLID = Util.memoize((Function)((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
         return create("entity_solid", 1536, true, false, RenderPipelines.ENTITY_SOLID, var1);
      }));
      ENTITY_SOLID_Z_OFFSET_FORWARD = Util.memoize((Function)((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING_FORWARD).createCompositeState(true);
         return create("entity_solid_z_offset_forward", 1536, true, false, RenderPipelines.ENTITY_SOLID_Z_OFFSET_FORWARD, var1);
      }));
      ENTITY_CUTOUT = Util.memoize((Function)((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
         return create("entity_cutout", 1536, true, false, RenderPipelines.ENTITY_CUTOUT, var1);
      }));
      ENTITY_CUTOUT_NO_CULL = Util.memoize((BiFunction)((var0, var1) -> {
         CompositeState var2 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(var1);
         return create("entity_cutout_no_cull", 1536, true, false, RenderPipelines.ENTITY_CUTOUT_NO_CULL, var2);
      }));
      ENTITY_CUTOUT_NO_CULL_Z_OFFSET = Util.memoize((BiFunction)((var0, var1) -> {
         CompositeState var2 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(var1);
         return create("entity_cutout_no_cull_z_offset", 1536, true, false, RenderPipelines.ENTITY_CUTOUT_NO_CULL_Z_OFFSET, var2);
      }));
      ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize((Function)((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setOutputState(ITEM_ENTITY_TARGET).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
         return create("item_entity_translucent_cull", 1536, true, true, RenderPipelines.ITEM_ENTITY_TRANSLUCENT_CULL, var1);
      }));
      ENTITY_TRANSLUCENT = Util.memoize((BiFunction)((var0, var1) -> {
         CompositeState var2 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(var1);
         return create("entity_translucent", 1536, true, true, RenderPipelines.ENTITY_TRANSLUCENT, var2);
      }));
      ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize((BiFunction)((var0, var1) -> {
         CompositeState var2 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setOverlayState(OVERLAY).createCompositeState(var1);
         return create("entity_translucent_emissive", 1536, true, true, RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE, var2);
      }));
      ENTITY_SMOOTH_CUTOUT = Util.memoize((Function)((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
         return create("entity_smooth_cutout", 1536, RenderPipelines.ENTITY_SMOOTH_CUTOUT, var1);
      }));
      BEACON_BEAM = Util.memoize((BiFunction)((var0, var1) -> {
         CompositeState var2 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).createCompositeState(false);
         return create("beacon_beam", 1536, false, true, var1 ? RenderPipelines.BEACON_BEAM_TRANSLUCENT : RenderPipelines.BEACON_BEAM_OPAQUE, var2);
      }));
      ENTITY_DECAL = Util.memoize((Function)((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false);
         return create("entity_decal", 1536, RenderPipelines.ENTITY_DECAL, var1);
      }));
      ENTITY_NO_OUTLINE = Util.memoize((Function)((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false);
         return create("entity_no_outline", 1536, false, true, RenderPipelines.ENTITY_NO_OUTLINE, var1);
      }));
      ENTITY_SHADOW = Util.memoize((Function)((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false);
         return create("entity_shadow", 1536, false, false, RenderPipelines.ENTITY_SHADOW, var1);
      }));
      DRAGON_EXPLOSION_ALPHA = Util.memoize((Function)((var0) -> {
         CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).createCompositeState(true);
         return create("entity_alpha", 1536, RenderPipelines.DRAGON_EXPLOSION_ALPHA, var1);
      }));
      EYES = Util.memoize((Function)((var0) -> {
         RenderStateShard.TextureStateShard var1 = new RenderStateShard.TextureStateShard(var0, false);
         return create("eyes", 1536, false, true, RenderPipelines.EYES, RenderType.CompositeState.builder().setTextureState(var1).createCompositeState(false));
      }));
      LEASH = create("leash", 1536, RenderPipelines.LEASH, RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).setLightmapState(LIGHTMAP).createCompositeState(false));
      WATER_MASK = create("water_mask", 1536, RenderPipelines.WATER_MASK, RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).createCompositeState(false));
      ARMOR_ENTITY_GLINT = create("armor_entity_glint", 1536, RenderPipelines.GLINT, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ARMOR, false)).setTexturingState(ARMOR_ENTITY_GLINT_TEXTURING).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
      GLINT_TRANSLUCENT = create("glint_translucent", 1536, RenderPipelines.GLINT, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, false)).setTexturingState(GLINT_TEXTURING).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(false));
      GLINT = create("glint", 1536, RenderPipelines.GLINT, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, false)).setTexturingState(GLINT_TEXTURING).createCompositeState(false));
      ENTITY_GLINT = create("entity_glint", 1536, RenderPipelines.GLINT, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, false)).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));
      CRUMBLING = Util.memoize((Function)((var0) -> {
         RenderStateShard.TextureStateShard var1 = new RenderStateShard.TextureStateShard(var0, false);
         return create("crumbling", 1536, false, true, RenderPipelines.CRUMBLING, RenderType.CompositeState.builder().setTextureState(var1).createCompositeState(false));
      }));
      TEXT = Util.memoize((Function)((var0) -> create("text", 786432, false, false, RenderPipelines.TEXT, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).createCompositeState(false))));
      TEXT_BACKGROUND = create("text_background", 1536, false, true, RenderPipelines.TEXT_BACKGROUND, RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).setLightmapState(LIGHTMAP).createCompositeState(false));
      TEXT_INTENSITY = Util.memoize((Function)((var0) -> create("text_intensity", 786432, false, false, RenderPipelines.TEXT_INTENSITY, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).createCompositeState(false))));
      TEXT_POLYGON_OFFSET = Util.memoize((Function)((var0) -> create("text_polygon_offset", 1536, false, true, RenderPipelines.TEXT_POLYGON_OFFSET, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).createCompositeState(false))));
      TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize((Function)((var0) -> create("text_intensity_polygon_offset", 1536, false, true, RenderPipelines.TEXT_INTENSITY, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).createCompositeState(false))));
      TEXT_SEE_THROUGH = Util.memoize((Function)((var0) -> create("text_see_through", 1536, false, false, RenderPipelines.TEXT_SEE_THROUGH, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).createCompositeState(false))));
      TEXT_BACKGROUND_SEE_THROUGH = create("text_background_see_through", 1536, false, true, RenderPipelines.TEXT_BACKGROUND_SEE_THROUGH, RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).setLightmapState(LIGHTMAP).createCompositeState(false));
      TEXT_INTENSITY_SEE_THROUGH = Util.memoize((Function)((var0) -> create("text_intensity_see_through", 1536, false, true, RenderPipelines.TEXT_INTENSITY_SEE_THROUGH, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).createCompositeState(false))));
      LIGHTNING = create("lightning", 1536, false, true, RenderPipelines.LIGHTNING, RenderType.CompositeState.builder().setOutputState(WEATHER_TARGET).createCompositeState(false));
      DRAGON_RAYS = create("dragon_rays", 1536, false, false, RenderPipelines.DRAGON_RAYS, RenderType.CompositeState.builder().createCompositeState(false));
      DRAGON_RAYS_DEPTH = create("dragon_rays_depth", 1536, false, false, RenderPipelines.DRAGON_RAYS_DEPTH, RenderType.CompositeState.builder().createCompositeState(false));
      TRIPWIRE = create("tripwire", 1536, true, true, RenderPipelines.TRIPWIRE, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setOutputState(WEATHER_TARGET).createCompositeState(true));
      END_PORTAL = create("end_portal", 1536, false, false, RenderPipelines.END_PORTAL, RenderType.CompositeState.builder().setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(AbstractEndPortalRenderer.END_SKY_LOCATION, false).add(AbstractEndPortalRenderer.END_PORTAL_LOCATION, false).build()).createCompositeState(false));
      END_GATEWAY = create("end_gateway", 1536, false, false, RenderPipelines.END_GATEWAY, RenderType.CompositeState.builder().setTextureState(RenderStateShard.MultiTextureStateShard.builder().add(AbstractEndPortalRenderer.END_SKY_LOCATION, false).add(AbstractEndPortalRenderer.END_PORTAL_LOCATION, false).build()).createCompositeState(false));
      LINES = create("lines", 1536, RenderPipelines.LINES, RenderType.CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(VIEW_OFFSET_Z_LAYERING).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(false));
      SECONDARY_BLOCK_OUTLINE = create("secondary_block_outline", 1536, RenderPipelines.SECONDARY_BLOCK_OUTLINE, RenderType.CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(7.0))).setLayeringState(VIEW_OFFSET_Z_LAYERING).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(false));
      LINE_STRIP = create("line_strip", 1536, RenderPipelines.LINE_STRIP, RenderType.CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(VIEW_OFFSET_Z_LAYERING).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(false));
      DEBUG_LINE_STRIP = Util.memoize((Function)((var0) -> create("debug_line_strip", 1536, RenderPipelines.DEBUG_LINE_STRIP, RenderType.CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(var0))).createCompositeState(false))));
      DEBUG_FILLED_BOX = create("debug_filled_box", 1536, false, true, RenderPipelines.DEBUG_FILLED_BOX, RenderType.CompositeState.builder().setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
      DEBUG_QUADS = create("debug_quads", 1536, false, true, RenderPipelines.DEBUG_QUADS, RenderType.CompositeState.builder().createCompositeState(false));
      DEBUG_TRIANGLE_FAN = create("debug_triangle_fan", 1536, false, true, RenderPipelines.DEBUG_TRIANGLE_FAN, RenderType.CompositeState.builder().createCompositeState(false));
      DEBUG_STRUCTURE_QUADS = create("debug_structure_quads", 1536, false, true, RenderPipelines.DEBUG_STRUCTURE_QUADS, RenderType.CompositeState.builder().createCompositeState(false));
      DEBUG_SECTION_QUADS = create("debug_section_quads", 1536, false, true, RenderPipelines.DEBUG_SECTION_QUADS, RenderType.CompositeState.builder().setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false));
      OPAQUE_PARTICLE = Util.memoize((Function)((var0) -> create("opaque_particle", 1536, false, false, RenderPipelines.OPAQUE_PARTICLE, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setLightmapState(LIGHTMAP).createCompositeState(false))));
      TRANSLUCENT_PARTICLE = Util.memoize((Function)((var0) -> create("translucent_particle", 1536, false, false, RenderPipelines.TRANSLUCENT_PARTICLE, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setOutputState(PARTICLES_TARGET).setLightmapState(LIGHTMAP).createCompositeState(false))));
      WEATHER_DEPTH_WRITE = createWeather(RenderPipelines.WEATHER_DEPTH_WRITE);
      WEATHER_NO_DEPTH_WRITE = createWeather(RenderPipelines.WEATHER_NO_DEPTH_WRITE);
      SUNRISE_SUNSET = create("sunrise_sunset", 1536, false, false, RenderPipelines.SUNRISE_SUNSET, RenderType.CompositeState.builder().createCompositeState(false));
      CELESTIAL = Util.memoize((Function)((var0) -> create("celestial", 1536, false, false, RenderPipelines.CELESTIAL, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).createCompositeState(false))));
      BLOCK_SCREEN_EFFECT = Util.memoize((Function)((var0) -> create("block_screen_effect", 1536, false, false, RenderPipelines.BLOCK_SCREEN_EFFECT, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).createCompositeState(false))));
      FIRE_SCREEN_EFFECT = Util.memoize((Function)((var0) -> create("fire_screen_effect", 1536, false, false, RenderPipelines.FIRE_SCREEN_EFFECT, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).createCompositeState(false))));
   }

   protected static enum OutlineProperty {
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

   protected static final class CompositeState {
      final RenderStateShard.EmptyTextureStateShard textureState;
      final RenderStateShard.OutputStateShard outputState;
      final OutlineProperty outlineProperty;
      final ImmutableList<RenderStateShard> states;

      CompositeState(RenderStateShard.EmptyTextureStateShard var1, RenderStateShard.LightmapStateShard var2, RenderStateShard.OverlayStateShard var3, RenderStateShard.LayeringStateShard var4, RenderStateShard.OutputStateShard var5, RenderStateShard.TexturingStateShard var6, RenderStateShard.LineStateShard var7, OutlineProperty var8) {
         super();
         this.textureState = var1;
         this.outputState = var5;
         this.outlineProperty = var8;
         this.states = ImmutableList.of(var1, var2, var3, var4, var5, var6, var7);
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
         private RenderStateShard.LightmapStateShard lightmapState;
         private RenderStateShard.OverlayStateShard overlayState;
         private RenderStateShard.LayeringStateShard layeringState;
         private RenderStateShard.OutputStateShard outputState;
         private RenderStateShard.TexturingStateShard texturingState;
         private RenderStateShard.LineStateShard lineState;

         CompositeStateBuilder() {
            super();
            this.textureState = RenderStateShard.NO_TEXTURE;
            this.lightmapState = RenderStateShard.NO_LIGHTMAP;
            this.overlayState = RenderStateShard.NO_OVERLAY;
            this.layeringState = RenderStateShard.NO_LAYERING;
            this.outputState = RenderStateShard.MAIN_TARGET;
            this.texturingState = RenderStateShard.DEFAULT_TEXTURING;
            this.lineState = RenderStateShard.DEFAULT_LINE;
         }

         protected CompositeStateBuilder setTextureState(RenderStateShard.EmptyTextureStateShard var1) {
            this.textureState = var1;
            return this;
         }

         protected CompositeStateBuilder setLightmapState(RenderStateShard.LightmapStateShard var1) {
            this.lightmapState = var1;
            return this;
         }

         protected CompositeStateBuilder setOverlayState(RenderStateShard.OverlayStateShard var1) {
            this.overlayState = var1;
            return this;
         }

         protected CompositeStateBuilder setLayeringState(RenderStateShard.LayeringStateShard var1) {
            this.layeringState = var1;
            return this;
         }

         protected CompositeStateBuilder setOutputState(RenderStateShard.OutputStateShard var1) {
            this.outputState = var1;
            return this;
         }

         protected CompositeStateBuilder setTexturingState(RenderStateShard.TexturingStateShard var1) {
            this.texturingState = var1;
            return this;
         }

         protected CompositeStateBuilder setLineState(RenderStateShard.LineStateShard var1) {
            this.lineState = var1;
            return this;
         }

         protected CompositeState createCompositeState(boolean var1) {
            return this.createCompositeState(var1 ? RenderType.OutlineProperty.AFFECTS_OUTLINE : RenderType.OutlineProperty.NONE);
         }

         protected CompositeState createCompositeState(OutlineProperty var1) {
            return new CompositeState(this.textureState, this.lightmapState, this.overlayState, this.layeringState, this.outputState, this.texturingState, this.lineState, var1);
         }
      }
   }

   static final class CompositeRenderType extends RenderType {
      static final BiFunction<ResourceLocation, Boolean, RenderType> OUTLINE = Util.memoize((BiFunction)((var0, var1) -> RenderType.create("outline", 1536, var1 ? RenderPipelines.OUTLINE_CULL : RenderPipelines.OUTLINE_NO_CULL, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false)).setOutputState(OUTLINE_TARGET).createCompositeState(RenderType.OutlineProperty.IS_OUTLINE))));
      private final CompositeState state;
      private final RenderPipeline renderPipeline;
      private final Optional<RenderType> outline;
      private final boolean isOutline;

      CompositeRenderType(String var1, int var2, boolean var3, boolean var4, RenderPipeline var5, CompositeState var6) {
         super(var1, var2, var3, var4, () -> var6.states.forEach(RenderStateShard::setupRenderState), () -> var6.states.forEach(RenderStateShard::clearRenderState));
         this.state = var6;
         this.renderPipeline = var5;
         this.outline = var6.outlineProperty == RenderType.OutlineProperty.AFFECTS_OUTLINE ? var6.textureState.cutoutTexture().map((var1x) -> (RenderType)OUTLINE.apply(var1x, var5.isCull())) : Optional.empty();
         this.isOutline = var6.outlineProperty == RenderType.OutlineProperty.IS_OUTLINE;
      }

      public Optional<RenderType> outline() {
         return this.outline;
      }

      public boolean isOutline() {
         return this.isOutline;
      }

      public VertexFormat format() {
         return this.renderPipeline.getVertexFormat();
      }

      public VertexFormat.Mode mode() {
         return this.renderPipeline.getVertexFormatMode();
      }

      public RenderPipeline pipeline() {
         return this.renderPipeline;
      }

      public void draw(MeshData var1) {
         this.setupRenderState();
         GpuBufferSlice var2 = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), RenderSystem.getTextureMatrix(), RenderSystem.getShaderLineWidth());
         MeshData var3 = var1;

         try {
            GpuBuffer var4 = this.renderPipeline.getVertexFormat().uploadImmediateVertexBuffer(var1.vertexBuffer());
            GpuBuffer var5;
            VertexFormat.IndexType var6;
            if (var1.indexBuffer() == null) {
               RenderSystem.AutoStorageIndexBuffer var7 = RenderSystem.getSequentialBuffer(var1.drawState().mode());
               var5 = var7.getBuffer(var1.drawState().indexCount());
               var6 = var7.type();
            } else {
               var5 = this.renderPipeline.getVertexFormat().uploadImmediateIndexBuffer(var1.indexBuffer());
               var6 = var1.drawState().indexType();
            }

            RenderTarget var18 = this.state.outputState.getRenderTarget();
            GpuTextureView var8 = RenderSystem.outputColorTextureOverride != null ? RenderSystem.outputColorTextureOverride : var18.getColorTextureView();
            GpuTextureView var9 = var18.useDepth ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : var18.getDepthTextureView()) : null;

            try (RenderPass var10 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Immediate draw for " + this.getName(), var8, OptionalInt.empty(), var9, OptionalDouble.empty())) {
               var10.setPipeline(this.renderPipeline);
               ScissorState var11 = RenderSystem.getScissorStateForRenderTypeDraws();
               if (var11.enabled()) {
                  var10.enableScissor(var11.x(), var11.y(), var11.width(), var11.height());
               }

               RenderSystem.bindDefaultUniforms(var10);
               var10.setUniform("DynamicTransforms", var2);
               var10.setVertexBuffer(0, var4);

               for(int var12 = 0; var12 < 12; ++var12) {
                  GpuTextureView var13 = RenderSystem.getShaderTexture(var12);
                  if (var13 != null) {
                     var10.bindSampler("Sampler" + var12, var13);
                  }
               }

               var10.setIndexBuffer(var5, var6);
               var10.drawIndexed(0, 0, var1.drawState().indexCount(), 1);
            }
         } catch (Throwable var17) {
            if (var1 != null) {
               try {
                  var3.close();
               } catch (Throwable var14) {
                  var17.addSuppressed(var14);
               }
            }

            throw var17;
         }

         if (var1 != null) {
            var1.close();
         }

         this.clearRenderState();
      }

      public String toString() {
         String var10000 = this.name;
         return "RenderType[" + var10000 + ":" + String.valueOf(this.state) + "]";
      }
   }
}
