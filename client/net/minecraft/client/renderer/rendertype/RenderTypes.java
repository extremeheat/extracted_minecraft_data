package net.minecraft.client.renderer.rendertype;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class RenderTypes {
   static final BiFunction<Identifier, Boolean, RenderType> OUTLINE = Util.memoize((BiFunction)((var0, var1) -> RenderType.create("outline", RenderSetup.builder(var1 ? RenderPipelines.OUTLINE_CULL : RenderPipelines.OUTLINE_NO_CULL).withTexture("Sampler0", var0).setOutputTarget(OutputTarget.OUTLINE_TARGET).setOutline(RenderSetup.OutlineProperty.IS_OUTLINE).createRenderSetup())));
   public static final Supplier<GpuSampler> MOVING_BLOCK_SAMPLER = () -> RenderSystem.getSamplerCache().getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.NEAREST, true);
   private static final RenderType SOLID_MOVING_BLOCK;
   private static final RenderType CUTOUT_MOVING_BLOCK;
   private static final RenderType TRANSLUCENT_MOVING_BLOCK;
   private static final Function<Identifier, RenderType> ARMOR_CUTOUT_NO_CULL;
   private static final Function<Identifier, RenderType> ARMOR_TRANSLUCENT;
   private static final Function<Identifier, RenderType> ENTITY_SOLID;
   private static final Function<Identifier, RenderType> ENTITY_SOLID_Z_OFFSET_FORWARD;
   private static final Function<Identifier, RenderType> ENTITY_CUTOUT;
   private static final BiFunction<Identifier, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL;
   private static final BiFunction<Identifier, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL_Z_OFFSET;
   private static final Function<Identifier, RenderType> ITEM_ENTITY_TRANSLUCENT_CULL;
   private static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT;
   private static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE;
   private static final Function<Identifier, RenderType> ENTITY_SMOOTH_CUTOUT;
   private static final BiFunction<Identifier, Boolean, RenderType> BEACON_BEAM;
   private static final Function<Identifier, RenderType> ENTITY_DECAL;
   private static final Function<Identifier, RenderType> ENTITY_NO_OUTLINE;
   private static final Function<Identifier, RenderType> ENTITY_SHADOW;
   private static final Function<Identifier, RenderType> DRAGON_EXPLOSION_ALPHA;
   private static final Function<Identifier, RenderType> EYES;
   private static final RenderType LEASH;
   private static final RenderType WATER_MASK;
   private static final RenderType ARMOR_ENTITY_GLINT;
   private static final RenderType GLINT_TRANSLUCENT;
   private static final RenderType GLINT;
   private static final RenderType ENTITY_GLINT;
   private static final Function<Identifier, RenderType> CRUMBLING;
   private static final Function<Identifier, RenderType> TEXT;
   private static final RenderType TEXT_BACKGROUND;
   private static final Function<Identifier, RenderType> TEXT_INTENSITY;
   private static final Function<Identifier, RenderType> TEXT_POLYGON_OFFSET;
   private static final Function<Identifier, RenderType> TEXT_INTENSITY_POLYGON_OFFSET;
   private static final Function<Identifier, RenderType> TEXT_SEE_THROUGH;
   private static final RenderType TEXT_BACKGROUND_SEE_THROUGH;
   private static final Function<Identifier, RenderType> TEXT_INTENSITY_SEE_THROUGH;
   private static final RenderType LIGHTNING;
   private static final RenderType DRAGON_RAYS;
   private static final RenderType DRAGON_RAYS_DEPTH;
   private static final RenderType TRIPWIRE_MOVING_BLOCk;
   private static final RenderType END_PORTAL;
   private static final RenderType END_GATEWAY;
   public static final RenderType LINES;
   public static final RenderType LINES_TRANSLUCENT;
   public static final RenderType SECONDARY_BLOCK_OUTLINE;
   private static final RenderType DEBUG_FILLED_BOX;
   private static final RenderType DEBUG_POINT;
   private static final RenderType DEBUG_QUADS;
   private static final RenderType DEBUG_TRIANGLE_FAN;
   private static final Function<Identifier, RenderType> WEATHER_DEPTH_WRITE;
   private static final Function<Identifier, RenderType> WEATHER_NO_DEPTH_WRITE;
   private static final Function<Identifier, RenderType> BLOCK_SCREEN_EFFECT;
   private static final Function<Identifier, RenderType> FIRE_SCREEN_EFFECT;

   public RenderTypes() {
      super();
   }

   public static RenderType solidMovingBlock() {
      return SOLID_MOVING_BLOCK;
   }

   public static RenderType cutoutMovingBlock() {
      return CUTOUT_MOVING_BLOCK;
   }

   public static RenderType translucentMovingBlock() {
      return TRANSLUCENT_MOVING_BLOCK;
   }

   public static RenderType armorCutoutNoCull(Identifier var0) {
      return (RenderType)ARMOR_CUTOUT_NO_CULL.apply(var0);
   }

   public static RenderType createArmorDecalCutoutNoCull(Identifier var0) {
      RenderSetup var1 = RenderSetup.builder(RenderPipelines.ARMOR_DECAL_CUTOUT_NO_CULL).withTexture("Sampler0", var0).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
      return RenderType.create("armor_decal_cutout_no_cull", var1);
   }

   public static RenderType armorTranslucent(Identifier var0) {
      return (RenderType)ARMOR_TRANSLUCENT.apply(var0);
   }

   public static RenderType entitySolid(Identifier var0) {
      return (RenderType)ENTITY_SOLID.apply(var0);
   }

   public static RenderType entitySolidZOffsetForward(Identifier var0) {
      return (RenderType)ENTITY_SOLID_Z_OFFSET_FORWARD.apply(var0);
   }

   public static RenderType entityCutout(Identifier var0) {
      return (RenderType)ENTITY_CUTOUT.apply(var0);
   }

   public static RenderType entityCutoutNoCull(Identifier var0, boolean var1) {
      return (RenderType)ENTITY_CUTOUT_NO_CULL.apply(var0, var1);
   }

   public static RenderType entityCutoutNoCull(Identifier var0) {
      return entityCutoutNoCull(var0, true);
   }

   public static RenderType entityCutoutNoCullZOffset(Identifier var0, boolean var1) {
      return (RenderType)ENTITY_CUTOUT_NO_CULL_Z_OFFSET.apply(var0, var1);
   }

   public static RenderType entityCutoutNoCullZOffset(Identifier var0) {
      return entityCutoutNoCullZOffset(var0, true);
   }

   public static RenderType itemEntityTranslucentCull(Identifier var0) {
      return (RenderType)ITEM_ENTITY_TRANSLUCENT_CULL.apply(var0);
   }

   public static RenderType entityTranslucent(Identifier var0, boolean var1) {
      return (RenderType)ENTITY_TRANSLUCENT.apply(var0, var1);
   }

   public static RenderType entityTranslucent(Identifier var0) {
      return entityTranslucent(var0, true);
   }

   public static RenderType entityTranslucentEmissive(Identifier var0, boolean var1) {
      return (RenderType)ENTITY_TRANSLUCENT_EMISSIVE.apply(var0, var1);
   }

   public static RenderType entityTranslucentEmissive(Identifier var0) {
      return entityTranslucentEmissive(var0, true);
   }

   public static RenderType entitySmoothCutout(Identifier var0) {
      return (RenderType)ENTITY_SMOOTH_CUTOUT.apply(var0);
   }

   public static RenderType beaconBeam(Identifier var0, boolean var1) {
      return (RenderType)BEACON_BEAM.apply(var0, var1);
   }

   public static RenderType entityDecal(Identifier var0) {
      return (RenderType)ENTITY_DECAL.apply(var0);
   }

   public static RenderType entityNoOutline(Identifier var0) {
      return (RenderType)ENTITY_NO_OUTLINE.apply(var0);
   }

   public static RenderType entityShadow(Identifier var0) {
      return (RenderType)ENTITY_SHADOW.apply(var0);
   }

   public static RenderType dragonExplosionAlpha(Identifier var0) {
      return (RenderType)DRAGON_EXPLOSION_ALPHA.apply(var0);
   }

   public static RenderType eyes(Identifier var0) {
      return (RenderType)EYES.apply(var0);
   }

   public static RenderType breezeEyes(Identifier var0) {
      return (RenderType)ENTITY_TRANSLUCENT_EMISSIVE.apply(var0, false);
   }

   public static RenderType breezeWind(Identifier var0, float var1, float var2) {
      return RenderType.create("breeze_wind", RenderSetup.builder(RenderPipelines.BREEZE_WIND).withTexture("Sampler0", var0).setTextureTransform(new TextureTransform.OffsetTextureTransform(var1, var2)).useLightmap().sortOnUpload().createRenderSetup());
   }

   public static RenderType energySwirl(Identifier var0, float var1, float var2) {
      return RenderType.create("energy_swirl", RenderSetup.builder(RenderPipelines.ENERGY_SWIRL).withTexture("Sampler0", var0).setTextureTransform(new TextureTransform.OffsetTextureTransform(var1, var2)).useLightmap().useOverlay().sortOnUpload().createRenderSetup());
   }

   public static RenderType leash() {
      return LEASH;
   }

   public static RenderType waterMask() {
      return WATER_MASK;
   }

   public static RenderType outline(Identifier var0) {
      return (RenderType)OUTLINE.apply(var0, false);
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

   public static RenderType crumbling(Identifier var0) {
      return (RenderType)CRUMBLING.apply(var0);
   }

   public static RenderType text(Identifier var0) {
      return (RenderType)TEXT.apply(var0);
   }

   public static RenderType textBackground() {
      return TEXT_BACKGROUND;
   }

   public static RenderType textIntensity(Identifier var0) {
      return (RenderType)TEXT_INTENSITY.apply(var0);
   }

   public static RenderType textPolygonOffset(Identifier var0) {
      return (RenderType)TEXT_POLYGON_OFFSET.apply(var0);
   }

   public static RenderType textIntensityPolygonOffset(Identifier var0) {
      return (RenderType)TEXT_INTENSITY_POLYGON_OFFSET.apply(var0);
   }

   public static RenderType textSeeThrough(Identifier var0) {
      return (RenderType)TEXT_SEE_THROUGH.apply(var0);
   }

   public static RenderType textBackgroundSeeThrough() {
      return TEXT_BACKGROUND_SEE_THROUGH;
   }

   public static RenderType textIntensitySeeThrough(Identifier var0) {
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

   public static RenderType tripwireMovingBlock() {
      return TRIPWIRE_MOVING_BLOCk;
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

   public static RenderType linesTranslucent() {
      return LINES_TRANSLUCENT;
   }

   public static RenderType secondaryBlockOutline() {
      return SECONDARY_BLOCK_OUTLINE;
   }

   public static RenderType debugFilledBox() {
      return DEBUG_FILLED_BOX;
   }

   public static RenderType debugPoint() {
      return DEBUG_POINT;
   }

   public static RenderType debugQuads() {
      return DEBUG_QUADS;
   }

   public static RenderType debugTriangleFan() {
      return DEBUG_TRIANGLE_FAN;
   }

   private static Function<Identifier, RenderType> createWeather(RenderPipeline var0) {
      return Util.memoize((Function)((var1) -> RenderType.create("weather", RenderSetup.builder(var0).withTexture("Sampler0", var1).setOutputTarget(OutputTarget.WEATHER_TARGET).useLightmap().createRenderSetup())));
   }

   public static RenderType weather(Identifier var0, boolean var1) {
      return (RenderType)(var1 ? WEATHER_DEPTH_WRITE : WEATHER_NO_DEPTH_WRITE).apply(var0);
   }

   public static RenderType blockScreenEffect(Identifier var0) {
      return (RenderType)BLOCK_SCREEN_EFFECT.apply(var0);
   }

   public static RenderType fireScreenEffect(Identifier var0) {
      return (RenderType)FIRE_SCREEN_EFFECT.apply(var0);
   }

   static {
      SOLID_MOVING_BLOCK = RenderType.create("solid_moving_block", RenderSetup.builder(RenderPipelines.SOLID_BLOCK).useLightmap().withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS, MOVING_BLOCK_SAMPLER).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup());
      CUTOUT_MOVING_BLOCK = RenderType.create("cutout_moving_block", RenderSetup.builder(RenderPipelines.CUTOUT_BLOCK).useLightmap().withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS, MOVING_BLOCK_SAMPLER).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup());
      TRANSLUCENT_MOVING_BLOCK = RenderType.create("translucent_moving_block", RenderSetup.builder(RenderPipelines.TRANSLUCENT_MOVING_BLOCK).useLightmap().withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS, MOVING_BLOCK_SAMPLER).setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET).sortOnUpload().bufferSize(786432).setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup());
      ARMOR_CUTOUT_NO_CULL = Util.memoize((Function)((var0) -> {
         RenderSetup var1 = RenderSetup.builder(RenderPipelines.ARMOR_CUTOUT_NO_CULL).withTexture("Sampler0", var0).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("armor_cutout_no_cull", var1);
      }));
      ARMOR_TRANSLUCENT = Util.memoize((Function)((var0) -> {
         RenderSetup var1 = RenderSetup.builder(RenderPipelines.ARMOR_TRANSLUCENT).withTexture("Sampler0", var0).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).affectsCrumbling().sortOnUpload().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("armor_translucent", var1);
      }));
      ENTITY_SOLID = Util.memoize((Function)((var0) -> {
         RenderSetup var1 = RenderSetup.builder(RenderPipelines.ENTITY_SOLID).withTexture("Sampler0", var0).useLightmap().useOverlay().affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("entity_solid", var1);
      }));
      ENTITY_SOLID_Z_OFFSET_FORWARD = Util.memoize((Function)((var0) -> {
         RenderSetup var1 = RenderSetup.builder(RenderPipelines.ENTITY_SOLID_Z_OFFSET_FORWARD).withTexture("Sampler0", var0).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING_FORWARD).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("entity_solid_z_offset_forward", var1);
      }));
      ENTITY_CUTOUT = Util.memoize((Function)((var0) -> {
         RenderSetup var1 = RenderSetup.builder(RenderPipelines.ENTITY_CUTOUT).withTexture("Sampler0", var0).useLightmap().useOverlay().affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("entity_cutout", var1);
      }));
      ENTITY_CUTOUT_NO_CULL = Util.memoize((BiFunction)((var0, var1) -> {
         RenderSetup var2 = RenderSetup.builder(RenderPipelines.ENTITY_CUTOUT_NO_CULL).withTexture("Sampler0", var0).useLightmap().useOverlay().affectsCrumbling().setOutline(var1 ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE).createRenderSetup();
         return RenderType.create("entity_cutout_no_cull", var2);
      }));
      ENTITY_CUTOUT_NO_CULL_Z_OFFSET = Util.memoize((BiFunction)((var0, var1) -> {
         RenderSetup var2 = RenderSetup.builder(RenderPipelines.ENTITY_CUTOUT_NO_CULL_Z_OFFSET).withTexture("Sampler0", var0).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).affectsCrumbling().setOutline(var1 ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE).createRenderSetup();
         return RenderType.create("entity_cutout_no_cull_z_offset", var2);
      }));
      ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize((Function)((var0) -> {
         RenderSetup var1 = RenderSetup.builder(RenderPipelines.ITEM_ENTITY_TRANSLUCENT_CULL).withTexture("Sampler0", var0).setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET).useLightmap().useOverlay().affectsCrumbling().sortOnUpload().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("item_entity_translucent_cull", var1);
      }));
      ENTITY_TRANSLUCENT = Util.memoize((BiFunction)((var0, var1) -> {
         RenderSetup var2 = RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT).withTexture("Sampler0", var0).useLightmap().useOverlay().affectsCrumbling().sortOnUpload().setOutline(var1 ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE).createRenderSetup();
         return RenderType.create("entity_translucent", var2);
      }));
      ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize((BiFunction)((var0, var1) -> {
         RenderSetup var2 = RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE).withTexture("Sampler0", var0).useOverlay().affectsCrumbling().sortOnUpload().setOutline(var1 ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE).createRenderSetup();
         return RenderType.create("entity_translucent_emissive", var2);
      }));
      ENTITY_SMOOTH_CUTOUT = Util.memoize((Function)((var0) -> {
         RenderSetup var1 = RenderSetup.builder(RenderPipelines.ENTITY_SMOOTH_CUTOUT).withTexture("Sampler0", var0).useLightmap().useOverlay().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("entity_smooth_cutout", var1);
      }));
      BEACON_BEAM = Util.memoize((BiFunction)((var0, var1) -> {
         RenderSetup var2 = RenderSetup.builder(var1 ? RenderPipelines.BEACON_BEAM_TRANSLUCENT : RenderPipelines.BEACON_BEAM_OPAQUE).withTexture("Sampler0", var0).sortOnUpload().createRenderSetup();
         return RenderType.create("beacon_beam", var2);
      }));
      ENTITY_DECAL = Util.memoize((Function)((var0) -> {
         RenderSetup var1 = RenderSetup.builder(RenderPipelines.ENTITY_DECAL).withTexture("Sampler0", var0).useLightmap().useOverlay().createRenderSetup();
         return RenderType.create("entity_decal", var1);
      }));
      ENTITY_NO_OUTLINE = Util.memoize((Function)((var0) -> {
         RenderSetup var1 = RenderSetup.builder(RenderPipelines.ENTITY_NO_OUTLINE).withTexture("Sampler0", var0).useLightmap().useOverlay().sortOnUpload().createRenderSetup();
         return RenderType.create("entity_no_outline", var1);
      }));
      ENTITY_SHADOW = Util.memoize((Function)((var0) -> {
         RenderSetup var1 = RenderSetup.builder(RenderPipelines.ENTITY_SHADOW).withTexture("Sampler0", var0).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup();
         return RenderType.create("entity_shadow", var1);
      }));
      DRAGON_EXPLOSION_ALPHA = Util.memoize((Function)((var0) -> {
         RenderSetup var1 = RenderSetup.builder(RenderPipelines.DRAGON_EXPLOSION_ALPHA).withTexture("Sampler0", var0).setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("entity_alpha", var1);
      }));
      EYES = Util.memoize((Function)((var0) -> RenderType.create("eyes", RenderSetup.builder(RenderPipelines.EYES).withTexture("Sampler0", var0).sortOnUpload().createRenderSetup())));
      LEASH = RenderType.create("leash", RenderSetup.builder(RenderPipelines.LEASH).useLightmap().createRenderSetup());
      WATER_MASK = RenderType.create("water_mask", RenderSetup.builder(RenderPipelines.WATER_MASK).createRenderSetup());
      ARMOR_ENTITY_GLINT = RenderType.create("armor_entity_glint", RenderSetup.builder(RenderPipelines.GLINT).withTexture("Sampler0", ItemRenderer.ENCHANTED_GLINT_ARMOR).setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());
      GLINT_TRANSLUCENT = RenderType.create("glint_translucent", RenderSetup.builder(RenderPipelines.GLINT).withTexture("Sampler0", ItemRenderer.ENCHANTED_GLINT_ITEM).setTextureTransform(TextureTransform.GLINT_TEXTURING).setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET).createRenderSetup());
      GLINT = RenderType.create("glint", RenderSetup.builder(RenderPipelines.GLINT).withTexture("Sampler0", ItemRenderer.ENCHANTED_GLINT_ITEM).setTextureTransform(TextureTransform.GLINT_TEXTURING).createRenderSetup());
      ENTITY_GLINT = RenderType.create("entity_glint", RenderSetup.builder(RenderPipelines.GLINT).withTexture("Sampler0", ItemRenderer.ENCHANTED_GLINT_ITEM).setTextureTransform(TextureTransform.ENTITY_GLINT_TEXTURING).createRenderSetup());
      CRUMBLING = Util.memoize((Function)((var0) -> RenderType.create("crumbling", RenderSetup.builder(RenderPipelines.CRUMBLING).withTexture("Sampler0", var0).sortOnUpload().createRenderSetup())));
      TEXT = Util.memoize((Function)((var0) -> RenderType.create("text", RenderSetup.builder(RenderPipelines.TEXT).withTexture("Sampler0", var0).useLightmap().bufferSize(786432).createRenderSetup())));
      TEXT_BACKGROUND = RenderType.create("text_background", RenderSetup.builder(RenderPipelines.TEXT_BACKGROUND).useLightmap().sortOnUpload().createRenderSetup());
      TEXT_INTENSITY = Util.memoize((Function)((var0) -> RenderType.create("text_intensity", RenderSetup.builder(RenderPipelines.TEXT_INTENSITY).withTexture("Sampler0", var0).useLightmap().bufferSize(786432).createRenderSetup())));
      TEXT_POLYGON_OFFSET = Util.memoize((Function)((var0) -> RenderType.create("text_polygon_offset", RenderSetup.builder(RenderPipelines.TEXT_POLYGON_OFFSET).withTexture("Sampler0", var0).useLightmap().sortOnUpload().createRenderSetup())));
      TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize((Function)((var0) -> RenderType.create("text_intensity_polygon_offset", RenderSetup.builder(RenderPipelines.TEXT_INTENSITY).withTexture("Sampler0", var0).useLightmap().sortOnUpload().createRenderSetup())));
      TEXT_SEE_THROUGH = Util.memoize((Function)((var0) -> RenderType.create("text_see_through", RenderSetup.builder(RenderPipelines.TEXT_SEE_THROUGH).withTexture("Sampler0", var0).useLightmap().createRenderSetup())));
      TEXT_BACKGROUND_SEE_THROUGH = RenderType.create("text_background_see_through", RenderSetup.builder(RenderPipelines.TEXT_BACKGROUND_SEE_THROUGH).useLightmap().sortOnUpload().createRenderSetup());
      TEXT_INTENSITY_SEE_THROUGH = Util.memoize((Function)((var0) -> RenderType.create("text_intensity_see_through", RenderSetup.builder(RenderPipelines.TEXT_INTENSITY_SEE_THROUGH).withTexture("Sampler0", var0).useLightmap().sortOnUpload().createRenderSetup())));
      LIGHTNING = RenderType.create("lightning", RenderSetup.builder(RenderPipelines.LIGHTNING).setOutputTarget(OutputTarget.WEATHER_TARGET).sortOnUpload().createRenderSetup());
      DRAGON_RAYS = RenderType.create("dragon_rays", RenderSetup.builder(RenderPipelines.DRAGON_RAYS).createRenderSetup());
      DRAGON_RAYS_DEPTH = RenderType.create("dragon_rays_depth", RenderSetup.builder(RenderPipelines.DRAGON_RAYS_DEPTH).createRenderSetup());
      TRIPWIRE_MOVING_BLOCk = RenderType.create("tripwire_moving_block", RenderSetup.builder(RenderPipelines.TRIPWIRE_BLOCK).useLightmap().withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS, MOVING_BLOCK_SAMPLER).setOutputTarget(OutputTarget.WEATHER_TARGET).affectsCrumbling().sortOnUpload().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup());
      END_PORTAL = RenderType.create("end_portal", RenderSetup.builder(RenderPipelines.END_PORTAL).withTexture("Sampler0", AbstractEndPortalRenderer.END_SKY_LOCATION).withTexture("Sampler1", AbstractEndPortalRenderer.END_PORTAL_LOCATION).createRenderSetup());
      END_GATEWAY = RenderType.create("end_gateway", RenderSetup.builder(RenderPipelines.END_GATEWAY).withTexture("Sampler0", AbstractEndPortalRenderer.END_SKY_LOCATION).withTexture("Sampler1", AbstractEndPortalRenderer.END_PORTAL_LOCATION).createRenderSetup());
      LINES = RenderType.create("lines", RenderSetup.builder(RenderPipelines.LINES).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET).createRenderSetup());
      LINES_TRANSLUCENT = RenderType.create("lines_translucent", RenderSetup.builder(RenderPipelines.LINES_TRANSLUCENT).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET).createRenderSetup());
      SECONDARY_BLOCK_OUTLINE = RenderType.create("secondary_block_outline", RenderSetup.builder(RenderPipelines.SECONDARY_BLOCK_OUTLINE).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET).createRenderSetup());
      DEBUG_FILLED_BOX = RenderType.create("debug_filled_box", RenderSetup.builder(RenderPipelines.DEBUG_FILLED_BOX).sortOnUpload().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());
      DEBUG_POINT = RenderType.create("debug_point", RenderSetup.builder(RenderPipelines.DEBUG_POINTS).createRenderSetup());
      DEBUG_QUADS = RenderType.create("debug_quads", RenderSetup.builder(RenderPipelines.DEBUG_QUADS).sortOnUpload().createRenderSetup());
      DEBUG_TRIANGLE_FAN = RenderType.create("debug_triangle_fan", RenderSetup.builder(RenderPipelines.DEBUG_TRIANGLE_FAN).sortOnUpload().createRenderSetup());
      WEATHER_DEPTH_WRITE = createWeather(RenderPipelines.WEATHER_DEPTH_WRITE);
      WEATHER_NO_DEPTH_WRITE = createWeather(RenderPipelines.WEATHER_NO_DEPTH_WRITE);
      BLOCK_SCREEN_EFFECT = Util.memoize((Function)((var0) -> RenderType.create("block_screen_effect", RenderSetup.builder(RenderPipelines.BLOCK_SCREEN_EFFECT).withTexture("Sampler0", var0).createRenderSetup())));
      FIRE_SCREEN_EFFECT = Util.memoize((Function)((var0) -> RenderType.create("fire_screen_effect", RenderSetup.builder(RenderPipelines.FIRE_SCREEN_EFFECT).withTexture("Sampler0", var0).createRenderSetup())));
   }
}
