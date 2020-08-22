package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;

public abstract class RenderType extends RenderStateShard {
   private static final RenderType SOLID;
   private static final RenderType CUTOUT_MIPPED;
   private static final RenderType CUTOUT;
   private static final RenderType TRANSLUCENT;
   private static final RenderType TRANSLUCENT_NO_CRUMBLING;
   private static final RenderType LEASH;
   private static final RenderType WATER_MASK;
   private static final RenderType GLINT;
   private static final RenderType ENTITY_GLINT;
   private static final RenderType LIGHTNING;
   public static final RenderType.CompositeRenderType LINES;
   private final VertexFormat format;
   private final int mode;
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

   private static RenderType.CompositeState translucentState() {
      return RenderType.CompositeState.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(true);
   }

   public static RenderType translucent() {
      return TRANSLUCENT;
   }

   public static RenderType translucentNoCrumbling() {
      return TRANSLUCENT_NO_CRUMBLING;
   }

   public static RenderType entitySolid(ResourceLocation var0) {
      RenderType.CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
      return create("entity_solid", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, var1);
   }

   public static RenderType entityCutout(ResourceLocation var0) {
      RenderType.CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
      return create("entity_cutout", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, var1);
   }

   public static RenderType entityCutoutNoCull(ResourceLocation var0) {
      RenderType.CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
      return create("entity_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, var1);
   }

   public static RenderType entityTranslucentCull(ResourceLocation var0) {
      RenderType.CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
      return create("entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, var1);
   }

   public static RenderType entityTranslucent(ResourceLocation var0) {
      RenderType.CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
      return create("entity_translucent", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, var1);
   }

   public static RenderType entitySmoothCutout(ResourceLocation var0) {
      RenderType.CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setAlphaState(MIDWAY_ALPHA).setDiffuseLightingState(DIFFUSE_LIGHTING).setShadeModelState(SMOOTH_SHADE).setCullState(NO_CULL).setLightmapState(LIGHTMAP).createCompositeState(true);
      return create("entity_smooth_cutout", DefaultVertexFormat.NEW_ENTITY, 7, 256, var1);
   }

   public static RenderType beaconBeam(ResourceLocation var0, boolean var1) {
      RenderType.CompositeState var2 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(var1 ? TRANSLUCENT_TRANSPARENCY : NO_TRANSPARENCY).setWriteMaskState(var1 ? COLOR_WRITE : COLOR_DEPTH_WRITE).setFogState(NO_FOG).createCompositeState(false);
      return create("beacon_beam", DefaultVertexFormat.BLOCK, 7, 256, false, true, var2);
   }

   public static RenderType entityDecal(ResourceLocation var0) {
      RenderType.CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setDepthTestState(EQUAL_DEPTH_TEST).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false);
      return create("entity_decal", DefaultVertexFormat.NEW_ENTITY, 7, 256, var1);
   }

   public static RenderType entityNoOutline(ResourceLocation var0) {
      RenderType.CompositeState var1 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).createCompositeState(false);
      return create("entity_no_outline", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, var1);
   }

   public static RenderType entityAlpha(ResourceLocation var0, float var1) {
      RenderType.CompositeState var2 = RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setAlphaState(new RenderStateShard.AlphaStateShard(var1)).setCullState(NO_CULL).createCompositeState(true);
      return create("entity_alpha", DefaultVertexFormat.NEW_ENTITY, 7, 256, var2);
   }

   public static RenderType eyes(ResourceLocation var0) {
      RenderStateShard.TextureStateShard var1 = new RenderStateShard.TextureStateShard(var0, false, false);
      return create("eyes", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, RenderType.CompositeState.builder().setTextureState(var1).setTransparencyState(ADDITIVE_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setFogState(BLACK_FOG).createCompositeState(false));
   }

   public static RenderType energySwirl(ResourceLocation var0, float var1, float var2) {
      return create("energy_swirl", DefaultVertexFormat.NEW_ENTITY, 7, 256, false, true, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard(var1, var2)).setFogState(BLACK_FOG).setTransparencyState(ADDITIVE_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
   }

   public static RenderType leash() {
      return LEASH;
   }

   public static RenderType waterMask() {
      return WATER_MASK;
   }

   public static RenderType outline(ResourceLocation var0) {
      return create("outline", DefaultVertexFormat.POSITION_COLOR_TEX, 7, 256, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setCullState(NO_CULL).setDepthTestState(NO_DEPTH_TEST).setAlphaState(DEFAULT_ALPHA).setTexturingState(OUTLINE_TEXTURING).setFogState(NO_FOG).setOutputState(OUTLINE_TARGET).createCompositeState(false));
   }

   public static RenderType glint() {
      return GLINT;
   }

   public static RenderType entityGlint() {
      return ENTITY_GLINT;
   }

   public static RenderType crumbling(ResourceLocation var0) {
      RenderStateShard.TextureStateShard var1 = new RenderStateShard.TextureStateShard(var0, false, false);
      return create("crumbling", DefaultVertexFormat.BLOCK, 7, 256, false, true, RenderType.CompositeState.builder().setTextureState(var1).setAlphaState(DEFAULT_ALPHA).setTransparencyState(CRUMBLING_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).setLayeringState(POLYGON_OFFSET_LAYERING).createCompositeState(false));
   }

   public static RenderType text(ResourceLocation var0) {
      return create("text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, 7, 256, false, true, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setAlphaState(DEFAULT_ALPHA).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(false));
   }

   public static RenderType textSeeThrough(ResourceLocation var0) {
      return create("text_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, 7, 256, false, true, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(var0, false, false)).setAlphaState(DEFAULT_ALPHA).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).setDepthTestState(NO_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
   }

   public static RenderType lightning() {
      return LIGHTNING;
   }

   public static RenderType endPortal(int var0) {
      RenderStateShard.TransparencyStateShard var1;
      RenderStateShard.TextureStateShard var2;
      if (var0 <= 1) {
         var1 = TRANSLUCENT_TRANSPARENCY;
         var2 = new RenderStateShard.TextureStateShard(TheEndPortalRenderer.END_SKY_LOCATION, false, false);
      } else {
         var1 = ADDITIVE_TRANSPARENCY;
         var2 = new RenderStateShard.TextureStateShard(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false);
      }

      return create("end_portal", DefaultVertexFormat.POSITION_COLOR, 7, 256, false, true, RenderType.CompositeState.builder().setTransparencyState(var1).setTextureState(var2).setTexturingState(new RenderStateShard.PortalTexturingStateShard(var0)).setFogState(BLACK_FOG).createCompositeState(false));
   }

   public static RenderType lines() {
      return LINES;
   }

   public RenderType(String var1, VertexFormat var2, int var3, int var4, boolean var5, boolean var6, Runnable var7, Runnable var8) {
      super(var1, var7, var8);
      this.format = var2;
      this.mode = var3;
      this.bufferSize = var4;
      this.affectsCrumbling = var5;
      this.sortOnUpload = var6;
   }

   public static RenderType.CompositeRenderType create(String var0, VertexFormat var1, int var2, int var3, RenderType.CompositeState var4) {
      return create(var0, var1, var2, var3, false, false, var4);
   }

   public static RenderType.CompositeRenderType create(String var0, VertexFormat var1, int var2, int var3, boolean var4, boolean var5, RenderType.CompositeState var6) {
      return RenderType.CompositeRenderType.memoize(var0, var1, var2, var3, var4, var5, var6);
   }

   public void end(BufferBuilder var1, int var2, int var3, int var4) {
      if (var1.building()) {
         if (this.sortOnUpload) {
            var1.sortQuads((float)var2, (float)var3, (float)var4);
         }

         var1.end();
         this.setupRenderState();
         BufferUploader.end(var1);
         this.clearRenderState();
      }
   }

   public String toString() {
      return this.name;
   }

   public static List chunkBufferLayers() {
      return ImmutableList.of(solid(), cutoutMipped(), cutout(), translucent());
   }

   public int bufferSize() {
      return this.bufferSize;
   }

   public VertexFormat format() {
      return this.format;
   }

   public int mode() {
      return this.mode;
   }

   public Optional outline() {
      return Optional.empty();
   }

   public boolean affectsCrumbling() {
      return this.affectsCrumbling;
   }

   static {
      SOLID = create("solid", DefaultVertexFormat.BLOCK, 7, 2097152, true, false, RenderType.CompositeState.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true));
      CUTOUT_MIPPED = create("cutout_mipped", DefaultVertexFormat.BLOCK, 7, 131072, true, false, RenderType.CompositeState.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setAlphaState(MIDWAY_ALPHA).createCompositeState(true));
      CUTOUT = create("cutout", DefaultVertexFormat.BLOCK, 7, 131072, true, false, RenderType.CompositeState.builder().setShadeModelState(SMOOTH_SHADE).setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET).setAlphaState(MIDWAY_ALPHA).createCompositeState(true));
      TRANSLUCENT = create("translucent", DefaultVertexFormat.BLOCK, 7, 262144, true, true, translucentState());
      TRANSLUCENT_NO_CRUMBLING = create("translucent_no_crumbling", DefaultVertexFormat.BLOCK, 7, 262144, false, true, translucentState());
      LEASH = create("leash", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, 7, 256, RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).setCullState(NO_CULL).setLightmapState(LIGHTMAP).createCompositeState(false));
      WATER_MASK = create("water_mask", DefaultVertexFormat.POSITION, 7, 256, RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).setWriteMaskState(DEPTH_WRITE).createCompositeState(false));
      GLINT = create("glint", DefaultVertexFormat.POSITION_TEX, 7, 256, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(GLINT_TEXTURING).createCompositeState(false));
      ENTITY_GLINT = create("entity_glint", DefaultVertexFormat.POSITION_TEX, 7, 256, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false)).setWriteMaskState(COLOR_WRITE).setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST).setTransparencyState(GLINT_TRANSPARENCY).setTexturingState(ENTITY_GLINT_TEXTURING).createCompositeState(false));
      LIGHTNING = create("lightning", DefaultVertexFormat.POSITION_COLOR, 7, 256, false, true, RenderType.CompositeState.builder().setWriteMaskState(COLOR_WRITE).setTransparencyState(LIGHTNING_TRANSPARENCY).setShadeModelState(SMOOTH_SHADE).createCompositeState(false));
      LINES = create("lines", DefaultVertexFormat.POSITION_COLOR, 1, 256, RenderType.CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(PROJECTION_LAYERING).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
   }

   static final class CompositeRenderType extends RenderType {
      private static final ObjectOpenCustomHashSet INSTANCES;
      private final RenderType.CompositeState state;
      private final int hashCode;
      private final Optional outline;

      private CompositeRenderType(String var1, VertexFormat var2, int var3, int var4, boolean var5, boolean var6, RenderType.CompositeState var7) {
         super(var1, var2, var3, var4, var5, var6, () -> {
            var7.states.forEach(RenderStateShard::setupRenderState);
         }, () -> {
            var7.states.forEach(RenderStateShard::clearRenderState);
         });
         this.state = var7;
         this.outline = var7.affectsOutline ? var7.textureState.texture().map(RenderType::outline) : Optional.empty();
         this.hashCode = Objects.hash(new Object[]{super.hashCode(), var7});
      }

      private static RenderType.CompositeRenderType memoize(String var0, VertexFormat var1, int var2, int var3, boolean var4, boolean var5, RenderType.CompositeState var6) {
         return (RenderType.CompositeRenderType)INSTANCES.addOrGet(new RenderType.CompositeRenderType(var0, var1, var2, var3, var4, var5, var6));
      }

      public Optional outline() {
         return this.outline;
      }

      public boolean equals(@Nullable Object var1) {
         return this == var1;
      }

      public int hashCode() {
         return this.hashCode;
      }

      static {
         INSTANCES = new ObjectOpenCustomHashSet(RenderType.CompositeRenderType.EqualsStrategy.INSTANCE);
      }

      static enum EqualsStrategy implements Strategy {
         INSTANCE;

         public int hashCode(@Nullable RenderType.CompositeRenderType var1) {
            return var1 == null ? 0 : var1.hashCode;
         }

         public boolean equals(@Nullable RenderType.CompositeRenderType var1, @Nullable RenderType.CompositeRenderType var2) {
            if (var1 == var2) {
               return true;
            } else {
               return var1 != null && var2 != null ? Objects.equals(var1.state, var2.state) : false;
            }
         }

         // $FF: synthetic method
         public boolean equals(@Nullable Object var1, @Nullable Object var2) {
            return this.equals((RenderType.CompositeRenderType)var1, (RenderType.CompositeRenderType)var2);
         }

         // $FF: synthetic method
         public int hashCode(@Nullable Object var1) {
            return this.hashCode((RenderType.CompositeRenderType)var1);
         }
      }
   }

   public static final class CompositeState {
      private final RenderStateShard.TextureStateShard textureState;
      private final RenderStateShard.TransparencyStateShard transparencyState;
      private final RenderStateShard.DiffuseLightingStateShard diffuseLightingState;
      private final RenderStateShard.ShadeModelStateShard shadeModelState;
      private final RenderStateShard.AlphaStateShard alphaState;
      private final RenderStateShard.DepthTestStateShard depthTestState;
      private final RenderStateShard.CullStateShard cullState;
      private final RenderStateShard.LightmapStateShard lightmapState;
      private final RenderStateShard.OverlayStateShard overlayState;
      private final RenderStateShard.FogStateShard fogState;
      private final RenderStateShard.LayeringStateShard layeringState;
      private final RenderStateShard.OutputStateShard outputState;
      private final RenderStateShard.TexturingStateShard texturingState;
      private final RenderStateShard.WriteMaskStateShard writeMaskState;
      private final RenderStateShard.LineStateShard lineState;
      private final boolean affectsOutline;
      private final ImmutableList states;

      private CompositeState(RenderStateShard.TextureStateShard var1, RenderStateShard.TransparencyStateShard var2, RenderStateShard.DiffuseLightingStateShard var3, RenderStateShard.ShadeModelStateShard var4, RenderStateShard.AlphaStateShard var5, RenderStateShard.DepthTestStateShard var6, RenderStateShard.CullStateShard var7, RenderStateShard.LightmapStateShard var8, RenderStateShard.OverlayStateShard var9, RenderStateShard.FogStateShard var10, RenderStateShard.LayeringStateShard var11, RenderStateShard.OutputStateShard var12, RenderStateShard.TexturingStateShard var13, RenderStateShard.WriteMaskStateShard var14, RenderStateShard.LineStateShard var15, boolean var16) {
         this.textureState = var1;
         this.transparencyState = var2;
         this.diffuseLightingState = var3;
         this.shadeModelState = var4;
         this.alphaState = var5;
         this.depthTestState = var6;
         this.cullState = var7;
         this.lightmapState = var8;
         this.overlayState = var9;
         this.fogState = var10;
         this.layeringState = var11;
         this.outputState = var12;
         this.texturingState = var13;
         this.writeMaskState = var14;
         this.lineState = var15;
         this.affectsOutline = var16;
         this.states = ImmutableList.of(this.textureState, this.transparencyState, this.diffuseLightingState, this.shadeModelState, this.alphaState, this.depthTestState, this.cullState, this.lightmapState, this.overlayState, this.fogState, this.layeringState, this.outputState, new RenderStateShard[]{this.texturingState, this.writeMaskState, this.lineState});
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            RenderType.CompositeState var2 = (RenderType.CompositeState)var1;
            return this.affectsOutline == var2.affectsOutline && this.states.equals(var2.states);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.states, this.affectsOutline});
      }

      public static RenderType.CompositeState.CompositeStateBuilder builder() {
         return new RenderType.CompositeState.CompositeStateBuilder();
      }

      // $FF: synthetic method
      CompositeState(RenderStateShard.TextureStateShard var1, RenderStateShard.TransparencyStateShard var2, RenderStateShard.DiffuseLightingStateShard var3, RenderStateShard.ShadeModelStateShard var4, RenderStateShard.AlphaStateShard var5, RenderStateShard.DepthTestStateShard var6, RenderStateShard.CullStateShard var7, RenderStateShard.LightmapStateShard var8, RenderStateShard.OverlayStateShard var9, RenderStateShard.FogStateShard var10, RenderStateShard.LayeringStateShard var11, RenderStateShard.OutputStateShard var12, RenderStateShard.TexturingStateShard var13, RenderStateShard.WriteMaskStateShard var14, RenderStateShard.LineStateShard var15, boolean var16, Object var17) {
         this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
      }

      public static class CompositeStateBuilder {
         private RenderStateShard.TextureStateShard textureState;
         private RenderStateShard.TransparencyStateShard transparencyState;
         private RenderStateShard.DiffuseLightingStateShard diffuseLightingState;
         private RenderStateShard.ShadeModelStateShard shadeModelState;
         private RenderStateShard.AlphaStateShard alphaState;
         private RenderStateShard.DepthTestStateShard depthTestState;
         private RenderStateShard.CullStateShard cullState;
         private RenderStateShard.LightmapStateShard lightmapState;
         private RenderStateShard.OverlayStateShard overlayState;
         private RenderStateShard.FogStateShard fogState;
         private RenderStateShard.LayeringStateShard layeringState;
         private RenderStateShard.OutputStateShard outputState;
         private RenderStateShard.TexturingStateShard texturingState;
         private RenderStateShard.WriteMaskStateShard writeMaskState;
         private RenderStateShard.LineStateShard lineState;

         private CompositeStateBuilder() {
            this.textureState = RenderStateShard.NO_TEXTURE;
            this.transparencyState = RenderStateShard.NO_TRANSPARENCY;
            this.diffuseLightingState = RenderStateShard.NO_DIFFUSE_LIGHTING;
            this.shadeModelState = RenderStateShard.FLAT_SHADE;
            this.alphaState = RenderStateShard.NO_ALPHA;
            this.depthTestState = RenderStateShard.LEQUAL_DEPTH_TEST;
            this.cullState = RenderStateShard.CULL;
            this.lightmapState = RenderStateShard.NO_LIGHTMAP;
            this.overlayState = RenderStateShard.NO_OVERLAY;
            this.fogState = RenderStateShard.FOG;
            this.layeringState = RenderStateShard.NO_LAYERING;
            this.outputState = RenderStateShard.MAIN_TARGET;
            this.texturingState = RenderStateShard.DEFAULT_TEXTURING;
            this.writeMaskState = RenderStateShard.COLOR_DEPTH_WRITE;
            this.lineState = RenderStateShard.DEFAULT_LINE;
         }

         public RenderType.CompositeState.CompositeStateBuilder setTextureState(RenderStateShard.TextureStateShard var1) {
            this.textureState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setTransparencyState(RenderStateShard.TransparencyStateShard var1) {
            this.transparencyState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setDiffuseLightingState(RenderStateShard.DiffuseLightingStateShard var1) {
            this.diffuseLightingState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setShadeModelState(RenderStateShard.ShadeModelStateShard var1) {
            this.shadeModelState = var1;
            return this;
         }

         public RenderType.CompositeState.CompositeStateBuilder setAlphaState(RenderStateShard.AlphaStateShard var1) {
            this.alphaState = var1;
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

         public RenderType.CompositeState.CompositeStateBuilder setFogState(RenderStateShard.FogStateShard var1) {
            this.fogState = var1;
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

         public RenderType.CompositeState createCompositeState(boolean var1) {
            return new RenderType.CompositeState(this.textureState, this.transparencyState, this.diffuseLightingState, this.shadeModelState, this.alphaState, this.depthTestState, this.cullState, this.lightmapState, this.overlayState, this.fogState, this.layeringState, this.outputState, this.texturingState, this.writeMaskState, this.lineState, var1);
         }

         // $FF: synthetic method
         CompositeStateBuilder(Object var1) {
            this();
         }
      }
   }
}
