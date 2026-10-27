package net.minecraft.client.renderer.rendertype;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.oit.OitStage;
import net.minecraft.client.renderer.pipeline.RenderPipelines;
import net.minecraft.client.renderer.pipeline.variant.BlockRenderPipelineVariant;
import net.minecraft.client.renderer.pipeline.variant.DebugPrimitiveRenderPipelineVariant;
import net.minecraft.client.renderer.pipeline.variant.EntityRenderPipelineVariant;
import net.minecraft.client.renderer.pipeline.variant.ItemRenderPipelineVariant;
import net.minecraft.client.renderer.pipeline.variant.LineRenderPipelineVariant;
import net.minecraft.client.renderer.pipeline.variant.TextRenderPipelineVariant;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class RenderTypes {
   static final BiFunction<Identifier, Boolean, RenderType> OUTLINE = Util.memoize((BiFunction)((texture, cullState) -> RenderType.create("outline", RenderSetup.nonOitBuilder(cullState ? RenderPipelines.OUTLINE_CULL : RenderPipelines.OUTLINE_NO_CULL).withTexture("Sampler0", texture).setOutline(RenderSetup.OutlineProperty.IS_OUTLINE).createRenderSetup())));
   private static final RenderType SOLID_MOVING_BLOCK;
   private static final RenderType CUTOUT_MOVING_BLOCK;
   private static final RenderType TRANSLUCENT_MOVING_BLOCK;
   private static final Function<Identifier, RenderType> ARMOR_CUTOUT_NO_CULL;
   private static final Function<Identifier, RenderType> ARMOR_CUTOUT_NO_CULL_GLINT;
   private static final Function<Identifier, RenderType> ARMOR_TRIM;
   private static final Function<Identifier, RenderType> ARMOR_TRIM_DECAL;
   private static final Function<Identifier, RenderType> WOLF_ARMOR_CRACKS;
   private static final Function<Identifier, RenderType> ENTITY_SOLID;
   private static final Function<Identifier, RenderType> ENTITY_SOLID_GLINT;
   private static final Function<Identifier, RenderType> ENTITY_SOLID_Z_OFFSET_FORWARD;
   private static final Function<Identifier, RenderType> ENTITY_CUTOUT_CULL;
   private static final BiFunction<Identifier, Boolean, RenderType> ENTITY_CUTOUT;
   private static final BiFunction<Identifier, Boolean, RenderType> ENTITY_CUTOUT_Z_OFFSET;
   private static final BiFunction<Identifier, Identifier, RenderType> ENTITY_CUTOUT_DISSOLVE;
   private static final Function<Identifier, RenderType> ENTITY_TRANSLUCENT_CULL;
   private static final Function<Identifier, RenderType> ITEM_CUTOUT;
   private static final Function<Identifier, RenderType> ITEM_CUTOUT_GLINT;
   private static final Function<Identifier, RenderType> ITEM_CUTOUT_GLINT_SPECIAL;
   private static final Function<Identifier, RenderType> ITEM_TRANSLUCENT;
   private static final Function<Identifier, RenderType> ITEM_TRANSLUCENT_GLINT;
   private static final Function<Identifier, RenderType> ITEM_TRANSLUCENT_GLINT_SPECIAL;
   private static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT;
   private static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE;
   private static final Function<Identifier, RenderType> END_CRYSTAL_BEAM;
   private static final BiFunction<Identifier, Boolean, RenderType> BEACON_BEAM;
   private static final Function<Identifier, RenderType> BANNER_PATTERN;
   private static final Function<Identifier, RenderType> ENTITY_SHADOW;
   private static final Function<Identifier, RenderType> EYES;
   private static final RenderType LEASH;
   private static final RenderType WATER_MASK;
   private static final RenderType OIT_WATER_MASK;
   private static final RenderType PATTERNED_SHIELD_GLINT;
   private static final RenderType TRIMMED_ARMOR_GLINT;
   private static final Function<Identifier, RenderType> CRUMBLING;
   private static final Function<Identifier, RenderType> TEXT;
   private static final Function<Identifier, RenderType> TEXT_GRAYSCALE;
   private static final Function<Identifier, RenderType> TEXT_POLYGON_OFFSET;
   private static final Function<Identifier, RenderType> TEXT_GRAYSCALE_POLYGON_OFFSET;
   private static final Function<Identifier, RenderType> TEXT_SEE_THROUGH;
   private static final Function<Identifier, RenderType> TEXT_GRAYSCALE_SEE_THROUGH;
   private static final RenderType LIGHTNING;
   private static final RenderType DRAGON_RAYS;
   private static final RenderType END_PORTAL;
   private static final RenderType END_GATEWAY;
   public static final RenderType LINES;
   public static final RenderType LINES_TRANSLUCENT;
   public static final RenderType LINES_TRANSLUCENT_NO_DEPTH_WRITE;
   public static final RenderType LINES_DEPTH_BIAS;
   private static final RenderType DEBUG_FILLED_BOX;
   private static final RenderType DEBUG_POINT;
   private static final RenderType DEBUG_QUADS;
   private static final RenderType DEBUG_TRIANGLE_FAN;
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

   public static RenderType armorCutoutNoCull(final Identifier texture) {
      return (RenderType)ARMOR_CUTOUT_NO_CULL.apply(texture);
   }

   public static RenderType armorCutoutNoCullGlint(final Identifier texture) {
      return (RenderType)ARMOR_CUTOUT_NO_CULL_GLINT.apply(texture);
   }

   public static RenderType armorTrim(final Identifier texture, final boolean decal) {
      return (RenderType)(decal ? ARMOR_TRIM_DECAL : ARMOR_TRIM).apply(texture);
   }

   public static RenderType wolfArmorCracks(final Identifier texture) {
      return (RenderType)WOLF_ARMOR_CRACKS.apply(texture);
   }

   public static RenderType entitySolid(final Identifier texture) {
      return (RenderType)ENTITY_SOLID.apply(texture);
   }

   public static RenderType entitySolidGlint(final Identifier texture) {
      return (RenderType)ENTITY_SOLID_GLINT.apply(texture);
   }

   public static RenderType entitySolidZOffsetForward(final Identifier texture) {
      return (RenderType)ENTITY_SOLID_Z_OFFSET_FORWARD.apply(texture);
   }

   public static RenderType entityCutoutCull(final Identifier texture) {
      return (RenderType)ENTITY_CUTOUT_CULL.apply(texture);
   }

   public static RenderType entityCutout(final Identifier texture, final boolean affectsOutline) {
      return (RenderType)ENTITY_CUTOUT.apply(texture, affectsOutline);
   }

   public static RenderType entityCutout(final Identifier texture) {
      return entityCutout(texture, true);
   }

   public static RenderType entityCutoutZOffset(final Identifier texture, final boolean affectsOutline) {
      return (RenderType)ENTITY_CUTOUT_Z_OFFSET.apply(texture, affectsOutline);
   }

   public static RenderType entityCutoutZOffset(final Identifier texture) {
      return entityCutoutZOffset(texture, true);
   }

   public static RenderType entityCutoutDissolve(final Identifier texture, final Identifier maskTexture) {
      return (RenderType)ENTITY_CUTOUT_DISSOLVE.apply(texture, maskTexture);
   }

   public static RenderType entityTranslucentCull(final Identifier texture) {
      return (RenderType)ENTITY_TRANSLUCENT_CULL.apply(texture);
   }

   public static RenderType itemCutout(final Identifier texture) {
      return (RenderType)ITEM_CUTOUT.apply(texture);
   }

   public static RenderType itemCutoutGlint(final Identifier texture) {
      return (RenderType)ITEM_CUTOUT_GLINT.apply(texture);
   }

   public static RenderType itemCutoutGlintSpecial(final Identifier texture) {
      return (RenderType)ITEM_CUTOUT_GLINT_SPECIAL.apply(texture);
   }

   public static RenderType itemTranslucent(final Identifier texture) {
      return (RenderType)ITEM_TRANSLUCENT.apply(texture);
   }

   public static RenderType itemTranslucentGlint(final Identifier texture) {
      return (RenderType)ITEM_TRANSLUCENT_GLINT.apply(texture);
   }

   public static RenderType itemTranslucentGlintSpecial(final Identifier texture) {
      return (RenderType)ITEM_TRANSLUCENT_GLINT_SPECIAL.apply(texture);
   }

   public static RenderType entityTranslucent(final Identifier texture, final boolean affectsOutline) {
      return (RenderType)ENTITY_TRANSLUCENT.apply(texture, affectsOutline);
   }

   public static RenderType entityTranslucent(final Identifier texture) {
      return entityTranslucent(texture, true);
   }

   public static RenderType entityTranslucentEmissive(final Identifier texture) {
      return (RenderType)ENTITY_TRANSLUCENT_EMISSIVE.apply(texture, true);
   }

   public static RenderType endCrystalBeam(final Identifier texture) {
      return (RenderType)END_CRYSTAL_BEAM.apply(texture);
   }

   public static RenderType beaconBeam(final Identifier texture, final boolean translucent) {
      return (RenderType)BEACON_BEAM.apply(texture, translucent);
   }

   public static RenderType bannerPattern(final Identifier texture) {
      return (RenderType)BANNER_PATTERN.apply(texture);
   }

   public static RenderType entityShadow(final Identifier texture) {
      return (RenderType)ENTITY_SHADOW.apply(texture);
   }

   public static RenderType eyes(final Identifier texture) {
      return (RenderType)EYES.apply(texture);
   }

   public static RenderType breezeEyes(final Identifier texture) {
      return (RenderType)ENTITY_TRANSLUCENT_EMISSIVE.apply(texture, false);
   }

   public static RenderType breezeWind(final Identifier texture, final float uOffset, final float vOffset) {
      return RenderType.create("breeze_wind", RenderSetup.builder((stage) -> RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.BREEZE_WIND, stage))).withTexture("Sampler0", texture).setTextureTransform(new TextureTransform.OffsetTextureTransform(uOffset, vOffset)).useLightmap().sortOnUpload().createRenderSetup());
   }

   public static RenderType energySwirl(final Identifier texture, final float uOffset, final float vOffset) {
      return RenderType.create("energy_swirl", RenderSetup.builder((stage) -> RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.ENERGY_SWIRL, stage))).withTexture("Sampler0", texture).setTextureTransform(new TextureTransform.OffsetTextureTransform(uOffset, vOffset)).useLightmap().useOverlay().sortOnUpload().createRenderSetup());
   }

   public static RenderType leash() {
      return LEASH;
   }

   public static RenderType waterMask() {
      return Minecraft.getInstance().gameRenderer.useImprovedTransparency() ? OIT_WATER_MASK : WATER_MASK;
   }

   public static RenderType outline(final Identifier texture) {
      return (RenderType)OUTLINE.apply(texture, false);
   }

   public static RenderType patternedShieldGlint() {
      return PATTERNED_SHIELD_GLINT;
   }

   public static RenderType trimmedArmorGlint() {
      return TRIMMED_ARMOR_GLINT;
   }

   public static RenderType crumbling(final Identifier texture) {
      return (RenderType)CRUMBLING.apply(texture);
   }

   public static RenderType text(final Identifier texture) {
      return (RenderType)TEXT.apply(texture);
   }

   public static RenderType textGrayscale(final Identifier texture) {
      return (RenderType)TEXT_GRAYSCALE.apply(texture);
   }

   public static RenderType textPolygonOffset(final Identifier texture) {
      return (RenderType)TEXT_POLYGON_OFFSET.apply(texture);
   }

   public static RenderType textGrayscalePolygonOffset(final Identifier texture) {
      return (RenderType)TEXT_GRAYSCALE_POLYGON_OFFSET.apply(texture);
   }

   public static RenderType textSeeThrough(final Identifier texture) {
      return (RenderType)TEXT_SEE_THROUGH.apply(texture);
   }

   public static RenderType textGrayscaleSeeThrough(final Identifier texture) {
      return (RenderType)TEXT_GRAYSCALE_SEE_THROUGH.apply(texture);
   }

   public static RenderType lightning() {
      return LIGHTNING;
   }

   public static RenderType dragonRays() {
      return DRAGON_RAYS;
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

   public static RenderType linesTranslucentNoDepthWrite() {
      return LINES_TRANSLUCENT_NO_DEPTH_WRITE;
   }

   public static RenderType linesDepthBias() {
      return LINES_DEPTH_BIAS;
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

   public static RenderType blockScreenEffect(final Identifier texture) {
      return (RenderType)BLOCK_SCREEN_EFFECT.apply(texture);
   }

   public static RenderType fireScreenEffect(final Identifier texture) {
      return (RenderType)FIRE_SCREEN_EFFECT.apply(texture);
   }

   static {
      SOLID_MOVING_BLOCK = RenderType.create("solid_moving_block", RenderSetup.nonOitBuilder(RenderPipelines.block(new RenderPipelines.BlockParameters(BlockRenderPipelineVariant.SOLID, (OitStage)null))).useLightmap().withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS, () -> RenderSystem.getSamplerCache().getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.NEAREST, true)).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup());
      CUTOUT_MOVING_BLOCK = RenderType.create("cutout_moving_block", RenderSetup.nonOitBuilder(RenderPipelines.block(new RenderPipelines.BlockParameters(BlockRenderPipelineVariant.CUTOUT, (OitStage)null))).useLightmap().withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS, () -> RenderSystem.getSamplerCache().getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.NEAREST, true)).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup());
      TRANSLUCENT_MOVING_BLOCK = RenderType.create("translucent_moving_block", RenderSetup.builder((stage) -> RenderPipelines.block(new RenderPipelines.BlockParameters(BlockRenderPipelineVariant.TRANSLUCENT, stage))).useLightmap().withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS, () -> RenderSystem.getSamplerCache().getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.NEAREST, true)).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).sortOnUpload().createRenderSetup());
      ARMOR_CUTOUT_NO_CULL = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.ARMOR_CUTOUT_NO_CULL, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("armor_cutout_no_cull", state);
      }));
      ARMOR_CUTOUT_NO_CULL_GLINT = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.ARMOR_CUTOUT_NO_CULL_GLINT, (OitStage)null))).withTexture("Sampler0", texture).withTexture("GlintSampler", ItemFeatureRenderer.ENCHANTED_GLINT_ARMOR).setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("armor_cutout_no_cull_glint", state);
      }));
      ARMOR_TRIM = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.ARMOR_CUTOUT_NO_CULL, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("armor_trim", state);
      }));
      ARMOR_TRIM_DECAL = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.ARMOR_DECAL_CUTOUT_NO_CULL, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("armor_trim_decal", state);
      }));
      WOLF_ARMOR_CRACKS = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.WOLF_ARMOR_CRACKS, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).affectsCrumbling().sortOnUpload().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).withForcedSolidModelPhase().createRenderSetup();
         return RenderType.create("wolf_armor_cracks", state);
      }));
      ENTITY_SOLID = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.SOLID, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().useOverlay().affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("entity_solid", state);
      }));
      ENTITY_SOLID_GLINT = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.SOLID_GLINT, (OitStage)null))).withTexture("Sampler0", texture).withTexture("GlintSampler", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM).setTextureTransform(TextureTransform.ENTITY_GLINT_TEXTURING).useLightmap().useOverlay().affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("entity_solid_glint", state);
      }));
      ENTITY_SOLID_Z_OFFSET_FORWARD = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.SOLID, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING_FORWARD).affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("entity_solid_z_offset_forward", state);
      }));
      ENTITY_CUTOUT_CULL = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.CUTOUT_CULL, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().useOverlay().affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("entity_cutout_cull", state);
      }));
      ENTITY_CUTOUT = Util.memoize((BiFunction)((texture, affectsOutline) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.CUTOUT, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().useOverlay().affectsCrumbling().setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE).createRenderSetup();
         return RenderType.create("entity_cutout", state);
      }));
      ENTITY_CUTOUT_Z_OFFSET = Util.memoize((BiFunction)((texture, affectsOutline) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.CUTOUT, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).affectsCrumbling().setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE).createRenderSetup();
         return RenderType.create("entity_cutout_z_offset", state);
      }));
      ENTITY_CUTOUT_DISSOLVE = Util.memoize((BiFunction)((texture, maskTexture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.CUTOUT_DISSOLVE, (OitStage)null))).withTexture("Sampler0", texture).withTexture("DissolveMaskSampler", maskTexture).useLightmap().useOverlay().affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("entity_cutout_dissolve", state);
      }));
      ENTITY_TRANSLUCENT_CULL = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.builder((stage) -> RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.TRANSLUCENT_CULL, stage))).withTexture("Sampler0", texture).useLightmap().useOverlay().affectsCrumbling().sortOnUpload().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("entity_translucent_cull_item_target", state);
      }));
      ITEM_CUTOUT = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.item(new RenderPipelines.ItemParameters(ItemRenderPipelineVariant.CUTOUT, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().useOverlay().affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("item_cutout", state);
      }));
      ITEM_CUTOUT_GLINT = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.item(new RenderPipelines.ItemParameters(ItemRenderPipelineVariant.CUTOUT_GLINT, (OitStage)null))).withTexture("Sampler0", texture).withTexture("GlintSampler", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM).setTextureTransform(TextureTransform.GLINT_TEXTURING).useLightmap().useOverlay().affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("item_cutout_glint", state);
      }));
      ITEM_CUTOUT_GLINT_SPECIAL = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.item(new RenderPipelines.ItemParameters(ItemRenderPipelineVariant.CUTOUT_GLINT_SPECIAL, (OitStage)null))).withTexture("Sampler0", texture).withTexture("GlintSampler", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM).setTextureTransform(TextureTransform.GLINT_TEXTURING).useLightmap().useOverlay().affectsCrumbling().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("item_cutout_glint_special", state);
      }));
      ITEM_TRANSLUCENT = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.builder((stage) -> RenderPipelines.item(new RenderPipelines.ItemParameters(ItemRenderPipelineVariant.TRANSLUCENT, stage))).withTexture("Sampler0", texture).useLightmap().useOverlay().affectsCrumbling().sortOnUpload().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("item_translucent", state);
      }));
      ITEM_TRANSLUCENT_GLINT = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.builder((stage) -> RenderPipelines.item(new RenderPipelines.ItemParameters(ItemRenderPipelineVariant.TRANSLUCENT_GLINT, stage))).withTexture("Sampler0", texture).withTexture("GlintSampler", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM).setTextureTransform(TextureTransform.GLINT_TEXTURING).useLightmap().useOverlay().affectsCrumbling().sortOnUpload().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("item_translucent_glint", state);
      }));
      ITEM_TRANSLUCENT_GLINT_SPECIAL = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.builder((stage) -> RenderPipelines.item(new RenderPipelines.ItemParameters(ItemRenderPipelineVariant.TRANSLUCENT_GLINT_SPECIAL, stage))).withTexture("Sampler0", texture).withTexture("GlintSampler", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM).setTextureTransform(TextureTransform.GLINT_TEXTURING).useLightmap().useOverlay().affectsCrumbling().sortOnUpload().setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup();
         return RenderType.create("item_translucent_glint_special", state);
      }));
      ENTITY_TRANSLUCENT = Util.memoize((BiFunction)((texture, affectsOutline) -> {
         RenderSetup state = RenderSetup.builder((stage) -> RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.TRANSLUCENT, stage))).withTexture("Sampler0", texture).useLightmap().useOverlay().affectsCrumbling().sortOnUpload().setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE).createRenderSetup();
         return RenderType.create("entity_translucent", state);
      }));
      ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize((BiFunction)((texture, affectsOutline) -> {
         RenderSetup state = RenderSetup.builder((stage) -> RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.TRANSLUCENT_EMISSIVE, stage))).withTexture("Sampler0", texture).useOverlay().affectsCrumbling().sortOnUpload().setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE).createRenderSetup();
         return RenderType.create("entity_translucent_emissive", state);
      }));
      END_CRYSTAL_BEAM = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.END_CRYSTAL_BEAM, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().setOutline(RenderSetup.OutlineProperty.NONE).createRenderSetup();
         return RenderType.create("end_crystal_beam", state);
      }));
      BEACON_BEAM = Util.memoize((BiFunction)((texture, translucent) -> {
         RenderSetup.RenderSetupBuilder builder = RenderSetup.builder((stage) -> RenderPipelines.beaconBeam(new RenderPipelines.BeaconBeamParameters(translucent, stage))).withTexture("Sampler0", texture).sortOnUpload();
         return RenderType.create("beacon_beam", builder.createRenderSetup());
      }));
      BANNER_PATTERN = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.nonOitBuilder(RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.BANNER_PATTERN, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().sortOnUpload().withForcedSolidModelPhase().createRenderSetup();
         return RenderType.create("banner_pattern", state);
      }));
      ENTITY_SHADOW = Util.memoize((Function)((texture) -> {
         RenderSetup state = RenderSetup.builder(RenderPipelines::entityShadow).withTexture("Sampler0", texture).useLightmap().useOverlay().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup();
         return RenderType.create("entity_shadow", state);
      }));
      EYES = Util.memoize((Function)((texture) -> RenderType.create("eyes", RenderSetup.builder((stage) -> RenderPipelines.entity(new RenderPipelines.EntityParameters(EntityRenderPipelineVariant.EYES, stage))).withTexture("Sampler0", texture).sortOnUpload().createRenderSetup())));
      LEASH = RenderType.create("leash", RenderSetup.nonOitBuilder(RenderPipelines.LEASH).useLightmap().createRenderSetup());
      WATER_MASK = RenderType.create("water_mask", RenderSetup.nonOitBuilder(RenderPipelines.waterMask(new RenderPipelines.WaterMaskParameters(false))).createRenderSetup());
      OIT_WATER_MASK = RenderType.create("oit_water_mask", RenderSetup.nonOitBuilder(RenderPipelines.waterMask(new RenderPipelines.WaterMaskParameters(true))).createRenderSetup());
      PATTERNED_SHIELD_GLINT = RenderType.create("patterned_shield_glint", RenderSetup.nonOitBuilder(RenderPipelines.GLINT).withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM).setTextureTransform(TextureTransform.ENTITY_GLINT_TEXTURING).withForcedSolidModelPhase().createRenderSetup());
      TRIMMED_ARMOR_GLINT = RenderType.create("trimmed_armor_glint", RenderSetup.nonOitBuilder(RenderPipelines.GLINT).withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ARMOR).setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).withForcedSolidModelPhase().createRenderSetup());
      CRUMBLING = Util.memoize((Function)((texture) -> RenderType.create("crumbling", RenderSetup.builder(RenderPipelines::crumbling).withTexture("Sampler0", texture).sortOnUpload().createRenderSetup())));
      TEXT = Util.memoize((Function)((texture) -> RenderType.create("text", RenderSetup.builder((stage) -> RenderPipelines.text(new RenderPipelines.TextParameters(TextRenderPipelineVariant.WORLD, stage))).withTexture("Sampler0", texture).useLightmap().createRenderSetup())));
      TEXT_GRAYSCALE = Util.memoize((Function)((texture) -> RenderType.create("text_grayscale", RenderSetup.builder((stage) -> RenderPipelines.text(new RenderPipelines.TextParameters(TextRenderPipelineVariant.WORLD_GRAYSCALE, stage))).withTexture("Sampler0", texture).useLightmap().createRenderSetup())));
      TEXT_POLYGON_OFFSET = Util.memoize((Function)((texture) -> RenderType.create("text_polygon_offset", RenderSetup.builder((stage) -> RenderPipelines.text(new RenderPipelines.TextParameters(TextRenderPipelineVariant.WORLD_POLYGON_OFFSET, stage))).withTexture("Sampler0", texture).useLightmap().sortOnUpload().createRenderSetup())));
      TEXT_GRAYSCALE_POLYGON_OFFSET = Util.memoize((Function)((texture) -> RenderType.create("text_grayscale_polygon_offset", RenderSetup.builder((stage) -> RenderPipelines.text(new RenderPipelines.TextParameters(TextRenderPipelineVariant.WORLD_GRAYSCALE_POLYGON_OFFSET, stage))).withTexture("Sampler0", texture).useLightmap().sortOnUpload().createRenderSetup())));
      TEXT_SEE_THROUGH = Util.memoize((Function)((texture) -> RenderType.create("text_see_through", RenderSetup.nonOitBuilder(RenderPipelines.text(new RenderPipelines.TextParameters(TextRenderPipelineVariant.WORLD_SEE_THROUGH, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().createRenderSetup())));
      TEXT_GRAYSCALE_SEE_THROUGH = Util.memoize((Function)((texture) -> RenderType.create("text_grayscale_see_through", RenderSetup.nonOitBuilder(RenderPipelines.text(new RenderPipelines.TextParameters(TextRenderPipelineVariant.WORLD_GRAYSCALE_SEE_THROUGH, (OitStage)null))).withTexture("Sampler0", texture).useLightmap().sortOnUpload().createRenderSetup())));
      LIGHTNING = RenderType.create("lightning", RenderSetup.builder((stage) -> RenderPipelines.additiveVisualEffect(new RenderPipelines.AdditiveVisualEffectParameters(PrimitiveTopology.QUADS, stage))).sortOnUpload().createRenderSetup());
      DRAGON_RAYS = RenderType.create("dragon_rays", RenderSetup.builder((stage) -> RenderPipelines.additiveVisualEffect(new RenderPipelines.AdditiveVisualEffectParameters(PrimitiveTopology.TRIANGLES, stage))).createRenderSetup());
      END_PORTAL = RenderType.create("end_portal", RenderSetup.nonOitBuilder(RenderPipelines.endPortal(new RenderPipelines.EndPortalParameters(false, false))).withTexture("Sampler0", AbstractEndPortalRenderer.END_SKY_LOCATION).withTexture("Sampler1", AbstractEndPortalRenderer.END_PORTAL_LOCATION).setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup());
      END_GATEWAY = RenderType.create("end_gateway", RenderSetup.nonOitBuilder(RenderPipelines.endPortal(new RenderPipelines.EndPortalParameters(true, false))).withTexture("Sampler0", AbstractEndPortalRenderer.END_SKY_LOCATION).withTexture("Sampler1", AbstractEndPortalRenderer.END_PORTAL_LOCATION).setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE).createRenderSetup());
      LINES = RenderType.create("lines", RenderSetup.nonOitBuilder(RenderPipelines.lines(new RenderPipelines.LinesParameters(LineRenderPipelineVariant.SOLID, (OitStage)null))).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());
      LINES_TRANSLUCENT = RenderType.create("lines_translucent", RenderSetup.nonOitBuilder(RenderPipelines.lines(new RenderPipelines.LinesParameters(LineRenderPipelineVariant.TRANSLUCENT, (OitStage)null))).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());
      LINES_TRANSLUCENT_NO_DEPTH_WRITE = RenderType.create("lines_translucent_no_depth_write", RenderSetup.builder((stage) -> RenderPipelines.lines(new RenderPipelines.LinesParameters(LineRenderPipelineVariant.TRANSLUCENT_NO_DEPTH_WRITE, stage))).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());
      LINES_DEPTH_BIAS = RenderType.create("lines_translucent_depth_bias", RenderSetup.builder((stage) -> RenderPipelines.lines(new RenderPipelines.LinesParameters(LineRenderPipelineVariant.SOLID_WITH_DEPTH_BIAS, stage))).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());
      DEBUG_FILLED_BOX = RenderType.create("debug_filled_box", RenderSetup.builder((stage) -> RenderPipelines.debugPrimitive(new RenderPipelines.DebugPrimitiveParameters(DebugPrimitiveRenderPipelineVariant.QUADS, stage))).sortOnUpload().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup());
      DEBUG_POINT = RenderType.create("debug_point", RenderSetup.builder((stage) -> RenderPipelines.debugPrimitive(new RenderPipelines.DebugPrimitiveParameters(DebugPrimitiveRenderPipelineVariant.POINTS, stage))).createRenderSetup());
      DEBUG_QUADS = RenderType.create("debug_quads", RenderSetup.builder((stage) -> RenderPipelines.debugPrimitive(new RenderPipelines.DebugPrimitiveParameters(DebugPrimitiveRenderPipelineVariant.QUADS, stage))).sortOnUpload().createRenderSetup());
      DEBUG_TRIANGLE_FAN = RenderType.create("debug_triangle_fan", RenderSetup.builder((stage) -> RenderPipelines.debugPrimitive(new RenderPipelines.DebugPrimitiveParameters(DebugPrimitiveRenderPipelineVariant.TRIANGLE_FAN, stage))).createRenderSetup());
      BLOCK_SCREEN_EFFECT = Util.memoize((Function)((texture) -> RenderType.create("block_screen_effect", RenderSetup.nonOitBuilder(RenderPipelines.SCREEN_EFFECT).withTexture("Sampler0", texture).createRenderSetup())));
      FIRE_SCREEN_EFFECT = Util.memoize((Function)((texture) -> RenderType.create("fire_screen_effect", RenderSetup.nonOitBuilder(RenderPipelines.SCREEN_EFFECT).withTexture("Sampler0", texture).createRenderSetup())));
   }
}
