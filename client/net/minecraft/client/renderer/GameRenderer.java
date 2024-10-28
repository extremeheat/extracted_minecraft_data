package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class GameRenderer implements AutoCloseable {
   private static final ResourceLocation NAUSEA_LOCATION = new ResourceLocation("textures/misc/nausea.png");
   private static final ResourceLocation BLUR_LOCATION = new ResourceLocation("shaders/post/blur.json");
   private static final float MAX_BLUR_RADIUS = 10.0F;
   static final Logger LOGGER = LogUtils.getLogger();
   private static final boolean DEPTH_BUFFER_DEBUG = false;
   public static final float PROJECTION_Z_NEAR = 0.05F;
   private static final float GUI_Z_NEAR = 1000.0F;
   final Minecraft minecraft;
   private final ResourceManager resourceManager;
   private final RandomSource random = RandomSource.create();
   private float renderDistance;
   public final ItemInHandRenderer itemInHandRenderer;
   private final MapRenderer mapRenderer;
   private final RenderBuffers renderBuffers;
   private int confusionAnimationTick;
   private float fov;
   private float oldFov;
   private float darkenWorldAmount;
   private float darkenWorldAmountO;
   private boolean renderHand = true;
   private boolean renderBlockOutline = true;
   private long lastScreenshotAttempt;
   private boolean hasWorldScreenshot;
   private long lastActiveTime = Util.getMillis();
   private final LightTexture lightTexture;
   private final OverlayTexture overlayTexture = new OverlayTexture();
   private boolean panoramicMode;
   private float zoom = 1.0F;
   private float zoomX;
   private float zoomY;
   public static final int ITEM_ACTIVATION_ANIMATION_LENGTH = 40;
   @Nullable
   private ItemStack itemActivationItem;
   private int itemActivationTicks;
   private float itemActivationOffX;
   private float itemActivationOffY;
   @Nullable
   PostChain postEffect;
   @Nullable
   private PostChain blurEffect;
   private boolean effectActive;
   private final Camera mainCamera = new Camera();
   public ShaderInstance blitShader;
   private final Map<String, ShaderInstance> shaders = Maps.newHashMap();
   @Nullable
   private static ShaderInstance positionShader;
   @Nullable
   private static ShaderInstance positionColorShader;
   @Nullable
   private static ShaderInstance positionColorTexShader;
   @Nullable
   private static ShaderInstance positionTexShader;
   @Nullable
   private static ShaderInstance positionTexColorShader;
   @Nullable
   private static ShaderInstance particleShader;
   @Nullable
   private static ShaderInstance positionColorLightmapShader;
   @Nullable
   private static ShaderInstance positionColorTexLightmapShader;
   @Nullable
   private static ShaderInstance rendertypeSolidShader;
   @Nullable
   private static ShaderInstance rendertypeCutoutMippedShader;
   @Nullable
   private static ShaderInstance rendertypeCutoutShader;
   @Nullable
   private static ShaderInstance rendertypeTranslucentShader;
   @Nullable
   private static ShaderInstance rendertypeTranslucentMovingBlockShader;
   @Nullable
   private static ShaderInstance rendertypeArmorCutoutNoCullShader;
   @Nullable
   private static ShaderInstance rendertypeEntitySolidShader;
   @Nullable
   private static ShaderInstance rendertypeEntityCutoutShader;
   @Nullable
   private static ShaderInstance rendertypeEntityCutoutNoCullShader;
   @Nullable
   private static ShaderInstance rendertypeEntityCutoutNoCullZOffsetShader;
   @Nullable
   private static ShaderInstance rendertypeItemEntityTranslucentCullShader;
   @Nullable
   private static ShaderInstance rendertypeEntityTranslucentCullShader;
   @Nullable
   private static ShaderInstance rendertypeEntityTranslucentShader;
   @Nullable
   private static ShaderInstance rendertypeEntityTranslucentEmissiveShader;
   @Nullable
   private static ShaderInstance rendertypeEntitySmoothCutoutShader;
   @Nullable
   private static ShaderInstance rendertypeBeaconBeamShader;
   @Nullable
   private static ShaderInstance rendertypeEntityDecalShader;
   @Nullable
   private static ShaderInstance rendertypeEntityNoOutlineShader;
   @Nullable
   private static ShaderInstance rendertypeEntityShadowShader;
   @Nullable
   private static ShaderInstance rendertypeEntityAlphaShader;
   @Nullable
   private static ShaderInstance rendertypeEyesShader;
   @Nullable
   private static ShaderInstance rendertypeEnergySwirlShader;
   @Nullable
   private static ShaderInstance rendertypeBreezeWindShader;
   @Nullable
   private static ShaderInstance rendertypeLeashShader;
   @Nullable
   private static ShaderInstance rendertypeWaterMaskShader;
   @Nullable
   private static ShaderInstance rendertypeOutlineShader;
   @Nullable
   private static ShaderInstance rendertypeArmorGlintShader;
   @Nullable
   private static ShaderInstance rendertypeArmorEntityGlintShader;
   @Nullable
   private static ShaderInstance rendertypeGlintTranslucentShader;
   @Nullable
   private static ShaderInstance rendertypeGlintShader;
   @Nullable
   private static ShaderInstance rendertypeGlintDirectShader;
   @Nullable
   private static ShaderInstance rendertypeEntityGlintShader;
   @Nullable
   private static ShaderInstance rendertypeEntityGlintDirectShader;
   @Nullable
   private static ShaderInstance rendertypeTextShader;
   @Nullable
   private static ShaderInstance rendertypeTextBackgroundShader;
   @Nullable
   private static ShaderInstance rendertypeTextIntensityShader;
   @Nullable
   private static ShaderInstance rendertypeTextSeeThroughShader;
   @Nullable
   private static ShaderInstance rendertypeTextBackgroundSeeThroughShader;
   @Nullable
   private static ShaderInstance rendertypeTextIntensitySeeThroughShader;
   @Nullable
   private static ShaderInstance rendertypeLightningShader;
   @Nullable
   private static ShaderInstance rendertypeTripwireShader;
   @Nullable
   private static ShaderInstance rendertypeEndPortalShader;
   @Nullable
   private static ShaderInstance rendertypeEndGatewayShader;
   @Nullable
   private static ShaderInstance rendertypeCloudsShader;
   @Nullable
   private static ShaderInstance rendertypeLinesShader;
   @Nullable
   private static ShaderInstance rendertypeCrumblingShader;
   @Nullable
   private static ShaderInstance rendertypeGuiShader;
   @Nullable
   private static ShaderInstance rendertypeGuiOverlayShader;
   @Nullable
   private static ShaderInstance rendertypeGuiTextHighlightShader;
   @Nullable
   private static ShaderInstance rendertypeGuiGhostRecipeOverlayShader;

   public GameRenderer(Minecraft var1, ItemInHandRenderer var2, ResourceManager var3, RenderBuffers var4) {
      super();
      this.minecraft = var1;
      this.resourceManager = var3;
      this.itemInHandRenderer = var2;
      this.mapRenderer = new MapRenderer(var1.getTextureManager(), var1.getMapDecorationTextures());
      this.lightTexture = new LightTexture(this, var1);
      this.renderBuffers = var4;
      this.postEffect = null;
   }

   public void close() {
      this.lightTexture.close();
      this.mapRenderer.close();
      this.overlayTexture.close();
      this.shutdownEffect();
      this.shutdownShaders();
      if (this.blitShader != null) {
         this.blitShader.close();
      }

   }

   public void setRenderHand(boolean var1) {
      this.renderHand = var1;
   }

   public void setRenderBlockOutline(boolean var1) {
      this.renderBlockOutline = var1;
   }

   public void setPanoramicMode(boolean var1) {
      this.panoramicMode = var1;
   }

   public boolean isPanoramicMode() {
      return this.panoramicMode;
   }

   public void shutdownEffect() {
      if (this.postEffect != null) {
         this.postEffect.close();
      }

      this.postEffect = null;
   }

   public void togglePostEffect() {
      this.effectActive = !this.effectActive;
   }

   public void checkEntityPostEffect(@Nullable Entity var1) {
      if (this.postEffect != null) {
         this.postEffect.close();
      }

      this.postEffect = null;
      if (var1 instanceof Creeper) {
         this.loadEffect(new ResourceLocation("shaders/post/creeper.json"));
      } else if (var1 instanceof Spider) {
         this.loadEffect(new ResourceLocation("shaders/post/spider.json"));
      } else if (var1 instanceof EnderMan) {
         this.loadEffect(new ResourceLocation("shaders/post/invert.json"));
      }

   }

   private void loadEffect(ResourceLocation var1) {
      if (this.postEffect != null) {
         this.postEffect.close();
      }

      try {
         this.postEffect = new PostChain(this.minecraft.getTextureManager(), this.resourceManager, this.minecraft.getMainRenderTarget(), var1);
         this.postEffect.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         this.effectActive = true;
      } catch (IOException var3) {
         LOGGER.warn("Failed to load shader: {}", var1, var3);
         this.effectActive = false;
      } catch (JsonSyntaxException var4) {
         LOGGER.warn("Failed to parse shader: {}", var1, var4);
         this.effectActive = false;
      }

   }

   public void loadBlurEffect() {
      if (this.blurEffect != null) {
         this.blurEffect.close();
      }

      try {
         this.blurEffect = new PostChain(this.minecraft.getTextureManager(), this.resourceManager, this.minecraft.getMainRenderTarget(), BLUR_LOCATION);
         this.blurEffect.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
      } catch (IOException var2) {
         LOGGER.warn("Failed to load shader: {}", BLUR_LOCATION, var2);
      } catch (JsonSyntaxException var3) {
         LOGGER.warn("Failed to parse shader: {}", BLUR_LOCATION, var3);
      }

   }

   public void processBlurEffect(float var1) {
      float var2 = (float)this.minecraft.options.getMenuBackgroundBlurriness();
      float var3 = var2 * 10.0F;
      if (this.blurEffect != null && var3 >= 1.0F) {
         RenderSystem.enableBlend();
         this.blurEffect.setUniform("Radius", var3);
         this.blurEffect.process(var1);
         RenderSystem.disableBlend();
      }

   }

   public PreparableReloadListener createReloadListener() {
      return new SimplePreparableReloadListener<ResourceCache>() {
         protected ResourceCache prepare(ResourceManager var1, ProfilerFiller var2) {
            Map var3 = var1.listResources("shaders", (var0) -> {
               String var1 = var0.getPath();
               return var1.endsWith(".json") || var1.endsWith(Program.Type.FRAGMENT.getExtension()) || var1.endsWith(Program.Type.VERTEX.getExtension()) || var1.endsWith(".glsl");
            });
            HashMap var4 = new HashMap();
            var3.forEach((var1x, var2x) -> {
               try {
                  InputStream var3 = var2x.open();

                  try {
                     byte[] var4x = var3.readAllBytes();
                     var4.put(var1x, new Resource(var2x.source(), () -> {
                        return new ByteArrayInputStream(var4x);
                     }));
                  } catch (Throwable var7) {
                     if (var3 != null) {
                        try {
                           var3.close();
                        } catch (Throwable var6) {
                           var7.addSuppressed(var6);
                        }
                     }

                     throw var7;
                  }

                  if (var3 != null) {
                     var3.close();
                  }
               } catch (Exception var8) {
                  GameRenderer.LOGGER.warn("Failed to read resource {}", var1x, var8);
               }

            });
            return new ResourceCache(var1, var4);
         }

         protected void apply(ResourceCache var1, ResourceManager var2, ProfilerFiller var3) {
            GameRenderer.this.reloadShaders(var1);
            if (GameRenderer.this.postEffect != null) {
               GameRenderer.this.postEffect.close();
            }

            GameRenderer.this.postEffect = null;
            GameRenderer.this.checkEntityPostEffect(GameRenderer.this.minecraft.getCameraEntity());
         }

         public String getName() {
            return "Shader Loader";
         }

         // $FF: synthetic method
         protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
            return this.prepare(var1, var2);
         }
      };
   }

   public void preloadUiShader(ResourceProvider var1) {
      if (this.blitShader != null) {
         throw new RuntimeException("Blit shader already preloaded");
      } else {
         try {
            this.blitShader = new ShaderInstance(var1, "blit_screen", DefaultVertexFormat.BLIT_SCREEN);
         } catch (IOException var3) {
            throw new RuntimeException("could not preload blit shader", var3);
         }

         rendertypeGuiShader = this.preloadShader(var1, "rendertype_gui", DefaultVertexFormat.POSITION_COLOR);
         rendertypeGuiOverlayShader = this.preloadShader(var1, "rendertype_gui_overlay", DefaultVertexFormat.POSITION_COLOR);
         positionShader = this.preloadShader(var1, "position", DefaultVertexFormat.POSITION);
         positionColorShader = this.preloadShader(var1, "position_color", DefaultVertexFormat.POSITION_COLOR);
         positionColorTexShader = this.preloadShader(var1, "position_color_tex", DefaultVertexFormat.POSITION_COLOR_TEX);
         positionTexShader = this.preloadShader(var1, "position_tex", DefaultVertexFormat.POSITION_TEX);
         positionTexColorShader = this.preloadShader(var1, "position_tex_color", DefaultVertexFormat.POSITION_TEX_COLOR);
         rendertypeTextShader = this.preloadShader(var1, "rendertype_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
      }
   }

   private ShaderInstance preloadShader(ResourceProvider var1, String var2, VertexFormat var3) {
      try {
         ShaderInstance var4 = new ShaderInstance(var1, var2, var3);
         this.shaders.put(var2, var4);
         return var4;
      } catch (Exception var5) {
         throw new IllegalStateException("could not preload shader " + var2, var5);
      }
   }

   void reloadShaders(ResourceProvider var1) {
      RenderSystem.assertOnRenderThread();
      ArrayList var2 = Lists.newArrayList();
      var2.addAll(Program.Type.FRAGMENT.getPrograms().values());
      var2.addAll(Program.Type.VERTEX.getPrograms().values());
      var2.forEach(Program::close);
      ArrayList var3 = Lists.newArrayListWithCapacity(this.shaders.size());

      try {
         var3.add(Pair.of(new ShaderInstance(var1, "particle", DefaultVertexFormat.PARTICLE), (var0) -> {
            particleShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "position", DefaultVertexFormat.POSITION), (var0) -> {
            positionShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "position_color", DefaultVertexFormat.POSITION_COLOR), (var0) -> {
            positionColorShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "position_color_lightmap", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP), (var0) -> {
            positionColorLightmapShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "position_color_tex", DefaultVertexFormat.POSITION_COLOR_TEX), (var0) -> {
            positionColorTexShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "position_color_tex_lightmap", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), (var0) -> {
            positionColorTexLightmapShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "position_tex", DefaultVertexFormat.POSITION_TEX), (var0) -> {
            positionTexShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "position_tex_color", DefaultVertexFormat.POSITION_TEX_COLOR), (var0) -> {
            positionTexColorShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_solid", DefaultVertexFormat.BLOCK), (var0) -> {
            rendertypeSolidShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_cutout_mipped", DefaultVertexFormat.BLOCK), (var0) -> {
            rendertypeCutoutMippedShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_cutout", DefaultVertexFormat.BLOCK), (var0) -> {
            rendertypeCutoutShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_translucent", DefaultVertexFormat.BLOCK), (var0) -> {
            rendertypeTranslucentShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_translucent_moving_block", DefaultVertexFormat.BLOCK), (var0) -> {
            rendertypeTranslucentMovingBlockShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_armor_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeArmorCutoutNoCullShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_solid", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEntitySolidShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_cutout", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEntityCutoutShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEntityCutoutNoCullShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_cutout_no_cull_z_offset", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEntityCutoutNoCullZOffsetShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_item_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeItemEntityTranslucentCullShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEntityTranslucentCullShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_translucent", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEntityTranslucentShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEntityTranslucentEmissiveShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_smooth_cutout", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEntitySmoothCutoutShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_beacon_beam", DefaultVertexFormat.BLOCK), (var0) -> {
            rendertypeBeaconBeamShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_decal", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEntityDecalShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_no_outline", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEntityNoOutlineShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_shadow", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEntityShadowShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_alpha", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEntityAlphaShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_eyes", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEyesShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_energy_swirl", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeEnergySwirlShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_leash", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP), (var0) -> {
            rendertypeLeashShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_water_mask", DefaultVertexFormat.POSITION), (var0) -> {
            rendertypeWaterMaskShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_outline", DefaultVertexFormat.POSITION_COLOR_TEX), (var0) -> {
            rendertypeOutlineShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_armor_glint", DefaultVertexFormat.POSITION_TEX), (var0) -> {
            rendertypeArmorGlintShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_armor_entity_glint", DefaultVertexFormat.POSITION_TEX), (var0) -> {
            rendertypeArmorEntityGlintShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_glint_translucent", DefaultVertexFormat.POSITION_TEX), (var0) -> {
            rendertypeGlintTranslucentShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_glint", DefaultVertexFormat.POSITION_TEX), (var0) -> {
            rendertypeGlintShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_glint_direct", DefaultVertexFormat.POSITION_TEX), (var0) -> {
            rendertypeGlintDirectShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_glint", DefaultVertexFormat.POSITION_TEX), (var0) -> {
            rendertypeEntityGlintShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_entity_glint_direct", DefaultVertexFormat.POSITION_TEX), (var0) -> {
            rendertypeEntityGlintDirectShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), (var0) -> {
            rendertypeTextShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_text_background", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP), (var0) -> {
            rendertypeTextBackgroundShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_text_intensity", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), (var0) -> {
            rendertypeTextIntensityShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_text_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), (var0) -> {
            rendertypeTextSeeThroughShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_text_background_see_through", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP), (var0) -> {
            rendertypeTextBackgroundSeeThroughShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_text_intensity_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), (var0) -> {
            rendertypeTextIntensitySeeThroughShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_lightning", DefaultVertexFormat.POSITION_COLOR), (var0) -> {
            rendertypeLightningShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_tripwire", DefaultVertexFormat.BLOCK), (var0) -> {
            rendertypeTripwireShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_end_portal", DefaultVertexFormat.POSITION), (var0) -> {
            rendertypeEndPortalShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_end_gateway", DefaultVertexFormat.POSITION), (var0) -> {
            rendertypeEndGatewayShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_clouds", DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL), (var0) -> {
            rendertypeCloudsShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_lines", DefaultVertexFormat.POSITION_COLOR_NORMAL), (var0) -> {
            rendertypeLinesShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_crumbling", DefaultVertexFormat.BLOCK), (var0) -> {
            rendertypeCrumblingShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_gui", DefaultVertexFormat.POSITION_COLOR), (var0) -> {
            rendertypeGuiShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_gui_overlay", DefaultVertexFormat.POSITION_COLOR), (var0) -> {
            rendertypeGuiOverlayShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_gui_text_highlight", DefaultVertexFormat.POSITION_COLOR), (var0) -> {
            rendertypeGuiTextHighlightShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_gui_ghost_recipe_overlay", DefaultVertexFormat.POSITION_COLOR), (var0) -> {
            rendertypeGuiGhostRecipeOverlayShader = var0;
         }));
         var3.add(Pair.of(new ShaderInstance(var1, "rendertype_breeze_wind", DefaultVertexFormat.NEW_ENTITY), (var0) -> {
            rendertypeBreezeWindShader = var0;
         }));
         this.loadBlurEffect();
      } catch (IOException var5) {
         var3.forEach((var0) -> {
            ((ShaderInstance)var0.getFirst()).close();
         });
         throw new RuntimeException("could not reload shaders", var5);
      }

      this.shutdownShaders();
      var3.forEach((var1x) -> {
         ShaderInstance var2 = (ShaderInstance)var1x.getFirst();
         this.shaders.put(var2.getName(), var2);
         ((Consumer)var1x.getSecond()).accept(var2);
      });
   }

   private void shutdownShaders() {
      RenderSystem.assertOnRenderThread();
      this.shaders.values().forEach(ShaderInstance::close);
      this.shaders.clear();
   }

   @Nullable
   public ShaderInstance getShader(@Nullable String var1) {
      return var1 == null ? null : (ShaderInstance)this.shaders.get(var1);
   }

   public void tick() {
      this.tickFov();
      this.lightTexture.tick();
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(this.minecraft.player);
      }

      this.mainCamera.tick();
      this.itemInHandRenderer.tick();
      ++this.confusionAnimationTick;
      if (this.minecraft.level.tickRateManager().runsNormally()) {
         this.minecraft.levelRenderer.tickRain(this.mainCamera);
         this.darkenWorldAmountO = this.darkenWorldAmount;
         if (this.minecraft.gui.getBossOverlay().shouldDarkenScreen()) {
            this.darkenWorldAmount += 0.05F;
            if (this.darkenWorldAmount > 1.0F) {
               this.darkenWorldAmount = 1.0F;
            }
         } else if (this.darkenWorldAmount > 0.0F) {
            this.darkenWorldAmount -= 0.0125F;
         }

         if (this.itemActivationTicks > 0) {
            --this.itemActivationTicks;
            if (this.itemActivationTicks == 0) {
               this.itemActivationItem = null;
            }
         }

      }
   }

   @Nullable
   public PostChain currentEffect() {
      return this.postEffect;
   }

   public void resize(int var1, int var2) {
      if (this.postEffect != null) {
         this.postEffect.resize(var1, var2);
      }

      if (this.blurEffect != null) {
         this.blurEffect.resize(var1, var2);
      }

      this.minecraft.levelRenderer.resize(var1, var2);
   }

   public void pick(float var1) {
      Entity var2 = this.minecraft.getCameraEntity();
      if (var2 != null) {
         if (this.minecraft.level != null && this.minecraft.player != null) {
            this.minecraft.getProfiler().push("pick");
            double var3 = this.minecraft.player.blockInteractionRange();
            double var5 = this.minecraft.player.entityInteractionRange();
            HitResult var7 = this.pick(var2, var3, var5, var1);
            this.minecraft.hitResult = var7;
            Minecraft var10000 = this.minecraft;
            Entity var10001;
            if (var7 instanceof EntityHitResult) {
               EntityHitResult var8 = (EntityHitResult)var7;
               var10001 = var8.getEntity();
            } else {
               var10001 = null;
            }

            var10000.crosshairPickEntity = var10001;
            this.minecraft.getProfiler().pop();
         }
      }
   }

   private HitResult pick(Entity var1, double var2, double var4, float var6) {
      double var7 = Math.max(var2, var4);
      double var9 = Mth.square(var7);
      Vec3 var11 = var1.getEyePosition(var6);
      HitResult var12 = var1.pick(var7, var6, false);
      double var13 = var12.getLocation().distanceToSqr(var11);
      if (var12.getType() != HitResult.Type.MISS) {
         var9 = var13;
         var7 = Math.sqrt(var13);
      }

      Vec3 var15 = var1.getViewVector(var6);
      Vec3 var16 = var11.add(var15.x * var7, var15.y * var7, var15.z * var7);
      float var17 = 1.0F;
      AABB var18 = var1.getBoundingBox().expandTowards(var15.scale(var7)).inflate(1.0, 1.0, 1.0);
      EntityHitResult var19 = ProjectileUtil.getEntityHitResult(var1, var11, var16, var18, (var0) -> {
         return !var0.isSpectator() && var0.isPickable();
      }, var9);
      return var19 != null && var19.getLocation().distanceToSqr(var11) < var13 ? filterHitResult(var19, var11, var4) : filterHitResult(var12, var11, var2);
   }

   private static HitResult filterHitResult(HitResult var0, Vec3 var1, double var2) {
      Vec3 var4 = var0.getLocation();
      if (!var4.closerThan(var1, var2)) {
         Vec3 var5 = var0.getLocation();
         Direction var6 = Direction.getNearest(var5.x - var1.x, var5.y - var1.y, var5.z - var1.z);
         return BlockHitResult.miss(var5, var6, BlockPos.containing(var5));
      } else {
         return var0;
      }
   }

   private void tickFov() {
      float var1 = 1.0F;
      Entity var3 = this.minecraft.getCameraEntity();
      if (var3 instanceof AbstractClientPlayer var2) {
         var1 = var2.getFieldOfViewModifier();
      }

      this.oldFov = this.fov;
      this.fov += (var1 - this.fov) * 0.5F;
      if (this.fov > 1.5F) {
         this.fov = 1.5F;
      }

      if (this.fov < 0.1F) {
         this.fov = 0.1F;
      }

   }

   private double getFov(Camera var1, float var2, boolean var3) {
      if (this.panoramicMode) {
         return 90.0;
      } else {
         double var4 = 70.0;
         if (var3) {
            var4 = (double)(Integer)this.minecraft.options.fov().get();
            var4 *= (double)Mth.lerp(var2, this.oldFov, this.fov);
         }

         if (var1.getEntity() instanceof LivingEntity && ((LivingEntity)var1.getEntity()).isDeadOrDying()) {
            float var6 = Math.min((float)((LivingEntity)var1.getEntity()).deathTime + var2, 20.0F);
            var4 /= (double)((1.0F - 500.0F / (var6 + 500.0F)) * 2.0F + 1.0F);
         }

         FogType var7 = var1.getFluidInCamera();
         if (var7 == FogType.LAVA || var7 == FogType.WATER) {
            var4 *= Mth.lerp((Double)this.minecraft.options.fovEffectScale().get(), 1.0, 0.8571428656578064);
         }

         return var4;
      }
   }

   private void bobHurt(PoseStack var1, float var2) {
      Entity var4 = this.minecraft.getCameraEntity();
      if (var4 instanceof LivingEntity var3) {
         float var7 = (float)var3.hurtTime - var2;
         float var5;
         if (var3.isDeadOrDying()) {
            var5 = Math.min((float)var3.deathTime + var2, 20.0F);
            var1.mulPose(Axis.ZP.rotationDegrees(40.0F - 8000.0F / (var5 + 200.0F)));
         }

         if (var7 < 0.0F) {
            return;
         }

         var7 /= (float)var3.hurtDuration;
         var7 = Mth.sin(var7 * var7 * var7 * var7 * 3.1415927F);
         var5 = var3.getHurtDir();
         var1.mulPose(Axis.YP.rotationDegrees(-var5));
         float var6 = (float)((double)(-var7) * 14.0 * (Double)this.minecraft.options.damageTiltStrength().get());
         var1.mulPose(Axis.ZP.rotationDegrees(var6));
         var1.mulPose(Axis.YP.rotationDegrees(var5));
      }

   }

   private void bobView(PoseStack var1, float var2) {
      if (this.minecraft.getCameraEntity() instanceof Player) {
         Player var3 = (Player)this.minecraft.getCameraEntity();
         float var4 = var3.walkDist - var3.walkDistO;
         float var5 = -(var3.walkDist + var4 * var2);
         float var6 = Mth.lerp(var2, var3.oBob, var3.bob);
         var1.translate(Mth.sin(var5 * 3.1415927F) * var6 * 0.5F, -Math.abs(Mth.cos(var5 * 3.1415927F) * var6), 0.0F);
         var1.mulPose(Axis.ZP.rotationDegrees(Mth.sin(var5 * 3.1415927F) * var6 * 3.0F));
         var1.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(var5 * 3.1415927F - 0.2F) * var6) * 5.0F));
      }
   }

   public void renderZoomed(float var1, float var2, float var3) {
      this.zoom = var1;
      this.zoomX = var2;
      this.zoomY = var3;
      this.setRenderBlockOutline(false);
      this.setRenderHand(false);
      this.renderLevel(1.0F, 0L);
      this.zoom = 1.0F;
   }

   private void renderItemInHand(Camera var1, float var2, Matrix4f var3) {
      if (!this.panoramicMode) {
         this.resetProjectionMatrix(this.getProjectionMatrix(this.getFov(var1, var2, false)));
         PoseStack var4 = new PoseStack();
         var4.pushPose();
         var4.mulPose(var3.invert(new Matrix4f()));
         Matrix4fStack var5 = RenderSystem.getModelViewStack();
         var5.pushMatrix().mul(var3);
         RenderSystem.applyModelViewMatrix();
         this.bobHurt(var4, var2);
         if ((Boolean)this.minecraft.options.bobView().get()) {
            this.bobView(var4, var2);
         }

         boolean var6 = this.minecraft.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.minecraft.getCameraEntity()).isSleeping();
         if (this.minecraft.options.getCameraType().isFirstPerson() && !var6 && !this.minecraft.options.hideGui && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.lightTexture.turnOnLightLayer();
            this.itemInHandRenderer.renderHandsWithItems(var2, var4, this.renderBuffers.bufferSource(), this.minecraft.player, this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, var2));
            this.lightTexture.turnOffLightLayer();
         }

         var5.popMatrix();
         RenderSystem.applyModelViewMatrix();
         var4.popPose();
         if (this.minecraft.options.getCameraType().isFirstPerson() && !var6) {
            ScreenEffectRenderer.renderScreenEffect(this.minecraft, var4);
         }

      }
   }

   public void resetProjectionMatrix(Matrix4f var1) {
      RenderSystem.setProjectionMatrix(var1, VertexSorting.DISTANCE_TO_ORIGIN);
   }

   public Matrix4f getProjectionMatrix(double var1) {
      Matrix4f var3 = new Matrix4f();
      if (this.zoom != 1.0F) {
         var3.translate(this.zoomX, -this.zoomY, 0.0F);
         var3.scale(this.zoom, this.zoom, 1.0F);
      }

      return var3.perspective((float)(var1 * 0.01745329238474369), (float)this.minecraft.getWindow().getWidth() / (float)this.minecraft.getWindow().getHeight(), 0.05F, this.getDepthFar());
   }

   public float getDepthFar() {
      return this.renderDistance * 4.0F;
   }

   public static float getNightVisionScale(LivingEntity var0, float var1) {
      MobEffectInstance var2 = var0.getEffect(MobEffects.NIGHT_VISION);
      return !var2.endsWithin(200) ? 1.0F : 0.7F + Mth.sin(((float)var2.getDuration() - var1) * 3.1415927F * 0.2F) * 0.3F;
   }

   public void render(float var1, long var2, boolean var4) {
      if (!this.minecraft.isWindowActive() && this.minecraft.options.pauseOnLostFocus && (!(Boolean)this.minecraft.options.touchscreen().get() || !this.minecraft.mouseHandler.isRightPressed())) {
         if (Util.getMillis() - this.lastActiveTime > 500L) {
            this.minecraft.pauseGame(false);
         }
      } else {
         this.lastActiveTime = Util.getMillis();
      }

      if (!this.minecraft.noRender) {
         float var5 = this.minecraft.level != null && this.minecraft.level.tickRateManager().runsNormally() ? var1 : 1.0F;
         boolean var6 = this.minecraft.isGameLoadFinished();
         int var7 = (int)(this.minecraft.mouseHandler.xpos() * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth());
         int var8 = (int)(this.minecraft.mouseHandler.ypos() * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight());
         RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         if (var6 && var4 && this.minecraft.level != null) {
            this.minecraft.getProfiler().push("level");
            this.renderLevel(var1, var2);
            this.tryTakeScreenshotIfNeeded();
            this.minecraft.levelRenderer.doEntityOutline();
            if (this.postEffect != null && this.effectActive) {
               RenderSystem.disableBlend();
               RenderSystem.disableDepthTest();
               RenderSystem.resetTextureMatrix();
               this.postEffect.process(var5);
            }

            this.minecraft.getMainRenderTarget().bindWrite(true);
         }

         Window var9 = this.minecraft.getWindow();
         RenderSystem.clear(256, Minecraft.ON_OSX);
         Matrix4f var10 = (new Matrix4f()).setOrtho(0.0F, (float)((double)var9.getWidth() / var9.getGuiScale()), (float)((double)var9.getHeight() / var9.getGuiScale()), 0.0F, 1000.0F, 21000.0F);
         RenderSystem.setProjectionMatrix(var10, VertexSorting.ORTHOGRAPHIC_Z);
         Matrix4fStack var11 = RenderSystem.getModelViewStack();
         var11.pushMatrix();
         var11.translation(0.0F, 0.0F, -11000.0F);
         RenderSystem.applyModelViewMatrix();
         Lighting.setupFor3DItems();
         GuiGraphics var12 = new GuiGraphics(this.minecraft, this.renderBuffers.bufferSource());
         if (var6 && var4 && this.minecraft.level != null) {
            this.minecraft.getProfiler().popPush("gui");
            if (this.minecraft.player != null) {
               float var13 = Mth.lerp(var5, this.minecraft.player.oSpinningEffectIntensity, this.minecraft.player.spinningEffectIntensity);
               float var14 = ((Double)this.minecraft.options.screenEffectScale().get()).floatValue();
               if (var13 > 0.0F && this.minecraft.player.hasEffect(MobEffects.CONFUSION) && var14 < 1.0F) {
                  this.renderConfusionOverlay(var12, var13 * (1.0F - var14));
               }
            }

            if (!this.minecraft.options.hideGui) {
               this.renderItemActivationAnimation(this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), var5);
            }

            this.minecraft.gui.render(var12, var5);
            RenderSystem.clear(256, Minecraft.ON_OSX);
            this.minecraft.getProfiler().pop();
         }

         CrashReportCategory var15;
         CrashReport var19;
         if (this.minecraft.getOverlay() != null) {
            try {
               this.minecraft.getOverlay().render(var12, var7, var8, this.minecraft.getDeltaFrameTime());
            } catch (Throwable var18) {
               var19 = CrashReport.forThrowable(var18, "Rendering overlay");
               var15 = var19.addCategory("Overlay render details");
               var15.setDetail("Overlay name", () -> {
                  return this.minecraft.getOverlay().getClass().getCanonicalName();
               });
               throw new ReportedException(var19);
            }
         } else if (var6 && this.minecraft.screen != null) {
            try {
               this.minecraft.screen.renderWithTooltip(var12, var7, var8, this.minecraft.getDeltaFrameTime());
            } catch (Throwable var17) {
               var19 = CrashReport.forThrowable(var17, "Rendering screen");
               var15 = var19.addCategory("Screen render details");
               var15.setDetail("Screen name", () -> {
                  return this.minecraft.screen.getClass().getCanonicalName();
               });
               var15.setDetail("Mouse location", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", var7, var8, this.minecraft.mouseHandler.xpos(), this.minecraft.mouseHandler.ypos());
               });
               var15.setDetail("Screen size", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), this.minecraft.getWindow().getGuiScale());
               });
               throw new ReportedException(var19);
            }

            try {
               if (this.minecraft.screen != null) {
                  this.minecraft.screen.handleDelayedNarration();
               }
            } catch (Throwable var16) {
               var19 = CrashReport.forThrowable(var16, "Narrating screen");
               var15 = var19.addCategory("Screen details");
               var15.setDetail("Screen name", () -> {
                  return this.minecraft.screen.getClass().getCanonicalName();
               });
               throw new ReportedException(var19);
            }
         }

         if (var6 && var4 && this.minecraft.level != null) {
            this.minecraft.gui.renderSavingIndicator(var12, var5);
         }

         if (var6) {
            this.minecraft.getProfiler().push("toasts");
            this.minecraft.getToasts().render(var12);
            this.minecraft.getProfiler().pop();
         }

         var12.flush();
         var11.popMatrix();
         RenderSystem.applyModelViewMatrix();
      }
   }

   private void tryTakeScreenshotIfNeeded() {
      if (!this.hasWorldScreenshot && this.minecraft.isLocalServer()) {
         long var1 = Util.getMillis();
         if (var1 - this.lastScreenshotAttempt >= 1000L) {
            this.lastScreenshotAttempt = var1;
            IntegratedServer var3 = this.minecraft.getSingleplayerServer();
            if (var3 != null && !var3.isStopped()) {
               var3.getWorldScreenshotFile().ifPresent((var1x) -> {
                  if (Files.isRegularFile(var1x, new LinkOption[0])) {
                     this.hasWorldScreenshot = true;
                  } else {
                     this.takeAutoScreenshot(var1x);
                  }

               });
            }
         }
      }
   }

   private void takeAutoScreenshot(Path var1) {
      if (this.minecraft.levelRenderer.countRenderedSections() > 10 && this.minecraft.levelRenderer.hasRenderedAllSections()) {
         NativeImage var2 = Screenshot.takeScreenshot(this.minecraft.getMainRenderTarget());
         Util.ioPool().execute(() -> {
            int var2x = var2.getWidth();
            int var3 = var2.getHeight();
            int var4 = 0;
            int var5 = 0;
            if (var2x > var3) {
               var4 = (var2x - var3) / 2;
               var2x = var3;
            } else {
               var5 = (var3 - var2x) / 2;
               var3 = var2x;
            }

            try {
               NativeImage var6 = new NativeImage(64, 64, false);

               try {
                  var2.resizeSubRectTo(var4, var5, var2x, var3, var6);
                  var6.writeToFile(var1);
               } catch (Throwable var15) {
                  try {
                     var6.close();
                  } catch (Throwable var14) {
                     var15.addSuppressed(var14);
                  }

                  throw var15;
               }

               var6.close();
            } catch (IOException var16) {
               LOGGER.warn("Couldn't save auto screenshot", var16);
            } finally {
               var2.close();
            }

         });
      }

   }

   private boolean shouldRenderBlockOutline() {
      if (!this.renderBlockOutline) {
         return false;
      } else {
         Entity var1 = this.minecraft.getCameraEntity();
         boolean var2 = var1 instanceof Player && !this.minecraft.options.hideGui;
         if (var2 && !((Player)var1).getAbilities().mayBuild) {
            ItemStack var3 = ((LivingEntity)var1).getMainHandItem();
            HitResult var4 = this.minecraft.hitResult;
            if (var4 != null && var4.getType() == HitResult.Type.BLOCK) {
               BlockPos var5 = ((BlockHitResult)var4).getBlockPos();
               BlockState var6 = this.minecraft.level.getBlockState(var5);
               if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
                  var2 = var6.getMenuProvider(this.minecraft.level, var5) != null;
               } else {
                  BlockInWorld var7 = new BlockInWorld(this.minecraft.level, var5, false);
                  Registry var8 = this.minecraft.level.registryAccess().registryOrThrow(Registries.BLOCK);
                  var2 = !var3.isEmpty() && (var3.canBreakBlockInAdventureMode(var7) || var3.canPlaceOnBlockInAdventureMode(var7));
               }
            }
         }

         return var2;
      }
   }

   public void renderLevel(float var1, long var2) {
      this.lightTexture.updateLightTexture(var1);
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(this.minecraft.player);
      }

      this.pick(var1);
      this.minecraft.getProfiler().push("center");
      boolean var4 = this.shouldRenderBlockOutline();
      this.minecraft.getProfiler().popPush("camera");
      Camera var5 = this.mainCamera;
      Object var6 = this.minecraft.getCameraEntity() == null ? this.minecraft.player : this.minecraft.getCameraEntity();
      var5.setup(this.minecraft.level, (Entity)var6, !this.minecraft.options.getCameraType().isFirstPerson(), this.minecraft.options.getCameraType().isMirrored(), this.minecraft.level.tickRateManager().isEntityFrozen((Entity)var6) ? 1.0F : var1);
      this.renderDistance = (float)(this.minecraft.options.getEffectiveRenderDistance() * 16);
      double var7 = this.getFov(var5, var1, true);
      Matrix4f var9 = this.getProjectionMatrix(var7);
      PoseStack var10 = new PoseStack();
      this.bobHurt(var10, var5.getPartialTickTime());
      if ((Boolean)this.minecraft.options.bobView().get()) {
         this.bobView(var10, var5.getPartialTickTime());
      }

      var9.mul(var10.last().pose());
      float var11 = ((Double)this.minecraft.options.screenEffectScale().get()).floatValue();
      float var12 = Mth.lerp(var1, this.minecraft.player.oSpinningEffectIntensity, this.minecraft.player.spinningEffectIntensity) * var11 * var11;
      if (var12 > 0.0F) {
         int var13 = this.minecraft.player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
         float var14 = 5.0F / (var12 * var12 + 5.0F) - var12 * 0.04F;
         var14 *= var14;
         Vector3f var15 = new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
         float var16 = ((float)this.confusionAnimationTick + var1) * (float)var13 * 0.017453292F;
         var9.rotate(var16, var15);
         var9.scale(1.0F / var14, 1.0F, 1.0F);
         var9.rotate(-var16, var15);
      }

      this.resetProjectionMatrix(var9);
      Matrix4f var17 = (new Matrix4f()).rotationXYZ(var5.getXRot() * 0.017453292F, var5.getYRot() * 0.017453292F + 3.1415927F, 0.0F);
      this.minecraft.levelRenderer.prepareCullFrustum(var5.getPosition(), var17, this.getProjectionMatrix(Math.max(var7, (double)(Integer)this.minecraft.options.fov().get())));
      this.minecraft.levelRenderer.renderLevel(var1, var2, var4, var5, this, this.lightTexture, var17, var9);
      this.minecraft.getProfiler().popPush("hand");
      if (this.renderHand) {
         RenderSystem.clear(256, Minecraft.ON_OSX);
         this.renderItemInHand(var5, var1, var17);
      }

      this.minecraft.getProfiler().pop();
   }

   public void resetData() {
      this.itemActivationItem = null;
      this.mapRenderer.resetData();
      this.mainCamera.reset();
      this.hasWorldScreenshot = false;
   }

   public MapRenderer getMapRenderer() {
      return this.mapRenderer;
   }

   public void displayItemActivation(ItemStack var1) {
      this.itemActivationItem = var1;
      this.itemActivationTicks = 40;
      this.itemActivationOffX = this.random.nextFloat() * 2.0F - 1.0F;
      this.itemActivationOffY = this.random.nextFloat() * 2.0F - 1.0F;
   }

   private void renderItemActivationAnimation(int var1, int var2, float var3) {
      if (this.itemActivationItem != null && this.itemActivationTicks > 0) {
         int var4 = 40 - this.itemActivationTicks;
         float var5 = ((float)var4 + var3) / 40.0F;
         float var6 = var5 * var5;
         float var7 = var5 * var6;
         float var8 = 10.25F * var7 * var6 - 24.95F * var6 * var6 + 25.5F * var7 - 13.8F * var6 + 4.0F * var5;
         float var9 = var8 * 3.1415927F;
         float var10 = this.itemActivationOffX * (float)(var1 / 4);
         float var11 = this.itemActivationOffY * (float)(var2 / 4);
         RenderSystem.enableDepthTest();
         RenderSystem.disableCull();
         PoseStack var12 = new PoseStack();
         var12.pushPose();
         var12.translate((float)(var1 / 2) + var10 * Mth.abs(Mth.sin(var9 * 2.0F)), (float)(var2 / 2) + var11 * Mth.abs(Mth.sin(var9 * 2.0F)), -50.0F);
         float var13 = 50.0F + 175.0F * Mth.sin(var9);
         var12.scale(var13, -var13, var13);
         var12.mulPose(Axis.YP.rotationDegrees(900.0F * Mth.abs(Mth.sin(var9))));
         var12.mulPose(Axis.XP.rotationDegrees(6.0F * Mth.cos(var5 * 8.0F)));
         var12.mulPose(Axis.ZP.rotationDegrees(6.0F * Mth.cos(var5 * 8.0F)));
         MultiBufferSource.BufferSource var14 = this.renderBuffers.bufferSource();
         this.minecraft.getItemRenderer().renderStatic(this.itemActivationItem, ItemDisplayContext.FIXED, 15728880, OverlayTexture.NO_OVERLAY, var12, var14, this.minecraft.level, 0);
         var12.popPose();
         var14.endBatch();
         RenderSystem.enableCull();
         RenderSystem.disableDepthTest();
      }
   }

   private void renderConfusionOverlay(GuiGraphics var1, float var2) {
      int var3 = var1.guiWidth();
      int var4 = var1.guiHeight();
      var1.pose().pushPose();
      float var5 = Mth.lerp(var2, 2.0F, 1.0F);
      var1.pose().translate((float)var3 / 2.0F, (float)var4 / 2.0F, 0.0F);
      var1.pose().scale(var5, var5, var5);
      var1.pose().translate((float)(-var3) / 2.0F, (float)(-var4) / 2.0F, 0.0F);
      float var6 = 0.2F * var2;
      float var7 = 0.4F * var2;
      float var8 = 0.2F * var2;
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
      var1.setColor(var6, var7, var8, 1.0F);
      var1.blit(NAUSEA_LOCATION, 0, 0, -90, 0.0F, 0.0F, var3, var4, var3, var4);
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      var1.pose().popPose();
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public float getDarkenWorldAmount(float var1) {
      return Mth.lerp(var1, this.darkenWorldAmountO, this.darkenWorldAmount);
   }

   public float getRenderDistance() {
      return this.renderDistance;
   }

   public Camera getMainCamera() {
      return this.mainCamera;
   }

   public LightTexture lightTexture() {
      return this.lightTexture;
   }

   public OverlayTexture overlayTexture() {
      return this.overlayTexture;
   }

   @Nullable
   public static ShaderInstance getPositionShader() {
      return positionShader;
   }

   @Nullable
   public static ShaderInstance getPositionColorShader() {
      return positionColorShader;
   }

   @Nullable
   public static ShaderInstance getPositionColorTexShader() {
      return positionColorTexShader;
   }

   @Nullable
   public static ShaderInstance getPositionTexShader() {
      return positionTexShader;
   }

   @Nullable
   public static ShaderInstance getPositionTexColorShader() {
      return positionTexColorShader;
   }

   @Nullable
   public static ShaderInstance getParticleShader() {
      return particleShader;
   }

   @Nullable
   public static ShaderInstance getPositionColorLightmapShader() {
      return positionColorLightmapShader;
   }

   @Nullable
   public static ShaderInstance getPositionColorTexLightmapShader() {
      return positionColorTexLightmapShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeSolidShader() {
      return rendertypeSolidShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeCutoutMippedShader() {
      return rendertypeCutoutMippedShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeCutoutShader() {
      return rendertypeCutoutShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTranslucentShader() {
      return rendertypeTranslucentShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTranslucentMovingBlockShader() {
      return rendertypeTranslucentMovingBlockShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeArmorCutoutNoCullShader() {
      return rendertypeArmorCutoutNoCullShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntitySolidShader() {
      return rendertypeEntitySolidShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityCutoutShader() {
      return rendertypeEntityCutoutShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityCutoutNoCullShader() {
      return rendertypeEntityCutoutNoCullShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityCutoutNoCullZOffsetShader() {
      return rendertypeEntityCutoutNoCullZOffsetShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeItemEntityTranslucentCullShader() {
      return rendertypeItemEntityTranslucentCullShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityTranslucentCullShader() {
      return rendertypeEntityTranslucentCullShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityTranslucentShader() {
      return rendertypeEntityTranslucentShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityTranslucentEmissiveShader() {
      return rendertypeEntityTranslucentEmissiveShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntitySmoothCutoutShader() {
      return rendertypeEntitySmoothCutoutShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeBeaconBeamShader() {
      return rendertypeBeaconBeamShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityDecalShader() {
      return rendertypeEntityDecalShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityNoOutlineShader() {
      return rendertypeEntityNoOutlineShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityShadowShader() {
      return rendertypeEntityShadowShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityAlphaShader() {
      return rendertypeEntityAlphaShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEyesShader() {
      return rendertypeEyesShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEnergySwirlShader() {
      return rendertypeEnergySwirlShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeBreezeWindShader() {
      return rendertypeBreezeWindShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeLeashShader() {
      return rendertypeLeashShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeWaterMaskShader() {
      return rendertypeWaterMaskShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeOutlineShader() {
      return rendertypeOutlineShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeArmorGlintShader() {
      return rendertypeArmorGlintShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeArmorEntityGlintShader() {
      return rendertypeArmorEntityGlintShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGlintTranslucentShader() {
      return rendertypeGlintTranslucentShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGlintShader() {
      return rendertypeGlintShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGlintDirectShader() {
      return rendertypeGlintDirectShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityGlintShader() {
      return rendertypeEntityGlintShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEntityGlintDirectShader() {
      return rendertypeEntityGlintDirectShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTextShader() {
      return rendertypeTextShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTextBackgroundShader() {
      return rendertypeTextBackgroundShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTextIntensityShader() {
      return rendertypeTextIntensityShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTextSeeThroughShader() {
      return rendertypeTextSeeThroughShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTextBackgroundSeeThroughShader() {
      return rendertypeTextBackgroundSeeThroughShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTextIntensitySeeThroughShader() {
      return rendertypeTextIntensitySeeThroughShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeLightningShader() {
      return rendertypeLightningShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeTripwireShader() {
      return rendertypeTripwireShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEndPortalShader() {
      return rendertypeEndPortalShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeEndGatewayShader() {
      return rendertypeEndGatewayShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeCloudsShader() {
      return rendertypeCloudsShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeLinesShader() {
      return rendertypeLinesShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeCrumblingShader() {
      return rendertypeCrumblingShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGuiShader() {
      return rendertypeGuiShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGuiOverlayShader() {
      return rendertypeGuiOverlayShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGuiTextHighlightShader() {
      return rendertypeGuiTextHighlightShader;
   }

   @Nullable
   public static ShaderInstance getRendertypeGuiGhostRecipeOverlayShader() {
      return rendertypeGuiGhostRecipeOverlayShader;
   }

   public static record ResourceCache(ResourceProvider original, Map<ResourceLocation, Resource> cache) implements ResourceProvider {
      public ResourceCache(ResourceProvider var1, Map<ResourceLocation, Resource> var2) {
         super();
         this.original = var1;
         this.cache = var2;
      }

      public Optional<Resource> getResource(ResourceLocation var1) {
         Resource var2 = (Resource)this.cache.get(var1);
         return var2 != null ? Optional.of(var2) : this.original.getResource(var1);
      }

      public ResourceProvider original() {
         return this.original;
      }

      public Map<ResourceLocation, Resource> cache() {
         return this.cache;
      }
   }
}
