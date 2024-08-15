package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
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
import net.minecraft.world.entity.EntitySelector;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class GameRenderer implements AutoCloseable {
   private static final ResourceLocation BLUR_LOCATION = ResourceLocation.withDefaultNamespace("shaders/post/blur.json");
   public static final int MAX_BLUR_RADIUS = 10;
   static final Logger LOGGER = LogUtils.getLogger();
   private static final boolean DEPTH_BUFFER_DEBUG = false;
   public static final float PROJECTION_Z_NEAR = 0.05F;
   private static final float GUI_Z_NEAR = 1000.0F;
   final Minecraft minecraft;
   private final ResourceManager resourceManager;
   private final RandomSource random = RandomSource.create();
   private float renderDistance;
   public final ItemInHandRenderer itemInHandRenderer;
   private final RenderBuffers renderBuffers;
   private int confusionAnimationTick;
   private float fovModifier;
   private float oldFovModifier;
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
   private final CrossFrameResourcePool resourcePool = new CrossFrameResourcePool(3);
   @Nullable
   PostChain postEffect;
   @Nullable
   private PostChain blurEffect;
   private boolean effectActive;
   private final Camera mainCamera = new Camera();
   @Nullable
   public ShaderInstance blitShader;
   private final Map<String, ShaderInstance> shaders = Maps.newHashMap();
   @Nullable
   private static ShaderInstance positionShader;
   @Nullable
   private static ShaderInstance positionColorShader;
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
      this.lightTexture = new LightTexture(this, var1);
      this.renderBuffers = var4;
      this.postEffect = null;
   }

   @Override
   public void close() {
      this.lightTexture.close();
      this.overlayTexture.close();
      this.resourcePool.close();
      this.shutdownEffect();
      this.shutdownShaders();
      if (this.blurEffect != null) {
         this.blurEffect.close();
      }

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
         this.loadEffect(ResourceLocation.withDefaultNamespace("shaders/post/creeper.json"));
      } else if (var1 instanceof Spider) {
         this.loadEffect(ResourceLocation.withDefaultNamespace("shaders/post/spider.json"));
      } else if (var1 instanceof EnderMan) {
         this.loadEffect(ResourceLocation.withDefaultNamespace("shaders/post/invert.json"));
      }
   }

   private void loadEffect(ResourceLocation var1) {
      if (this.postEffect != null) {
         this.postEffect.close();
      }

      try {
         this.postEffect = PostChain.load(this.resourceManager, this.minecraft.getTextureManager(), var1, Set.of(PostChain.MAIN_TARGET_ID));
         this.effectActive = true;
      } catch (IOException var3) {
         LOGGER.warn("Failed to load shader: {}", var1, var3);
         this.effectActive = false;
      } catch (JsonSyntaxException var4) {
         LOGGER.warn("Failed to parse shader: {}", var1, var4);
         this.effectActive = false;
      }
   }

   private void loadBlurEffect(ResourceProvider var1) {
      if (this.blurEffect != null) {
         this.blurEffect.close();
      }

      try {
         this.blurEffect = PostChain.load(var1, this.minecraft.getTextureManager(), BLUR_LOCATION, Set.of(PostChain.MAIN_TARGET_ID));
      } catch (IOException var3) {
         LOGGER.warn("Failed to load shader: {}", BLUR_LOCATION, var3);
      } catch (JsonSyntaxException var4) {
         LOGGER.warn("Failed to parse shader: {}", BLUR_LOCATION, var4);
      }
   }

   public void processBlurEffect() {
      float var1 = (float)this.minecraft.options.getMenuBackgroundBlurriness();
      if (this.blurEffect != null && var1 >= 1.0F) {
         this.blurEffect.setUniform("Radius", var1);
         this.blurEffect.process(this.minecraft.getMainRenderTarget(), this.resourcePool, this.minecraft.getDeltaTracker());
      }
   }

   public PreparableReloadListener createReloadListener() {
      return new SimplePreparableReloadListener<GameRenderer.ResourceCache>() {
         protected GameRenderer.ResourceCache prepare(ResourceManager var1, ProfilerFiller var2) {
            Map var3 = var1.listResources(
               "shaders",
               var0 -> {
                  String var1x = var0.getPath();
                  return var1x.endsWith(".json")
                     || var1x.endsWith(Program.Type.FRAGMENT.getExtension())
                     || var1x.endsWith(Program.Type.VERTEX.getExtension())
                     || var1x.endsWith(".glsl");
               }
            );
            HashMap var4 = new HashMap();
            var3.forEach((var1x, var2x) -> {
               try (InputStream var3x = var2x.open()) {
                  byte[] var4x = var3x.readAllBytes();
                  var4.put(var1x, new Resource(var2x.source(), () -> new ByteArrayInputStream(var4x)));
               } catch (Exception var8) {
                  GameRenderer.LOGGER.warn("Failed to read resource {}", var1x, var8);
               }
            });
            return new GameRenderer.ResourceCache(var1, var4);
         }

         protected void apply(GameRenderer.ResourceCache var1, ResourceManager var2, ProfilerFiller var3) {
            GameRenderer.this.reloadShaders(var1);
            if (GameRenderer.this.postEffect != null) {
               GameRenderer.this.postEffect.close();
            }

            GameRenderer.this.postEffect = null;
            GameRenderer.this.checkEntityPostEffect(GameRenderer.this.minecraft.getCameraEntity());
         }

         @Override
         public String getName() {
            return "Shader Loader";
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
         var3.add(Pair.of(new ShaderInstance(var1, "particle", DefaultVertexFormat.PARTICLE), (Consumer<ShaderInstance>)var0 -> particleShader = var0));
         var3.add(Pair.of(new ShaderInstance(var1, "position", DefaultVertexFormat.POSITION), (Consumer<ShaderInstance>)var0 -> positionShader = var0));
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "position_color", DefaultVertexFormat.POSITION_COLOR), (Consumer<ShaderInstance>)var0 -> positionColorShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "position_color_lightmap", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP),
               (Consumer<ShaderInstance>)var0 -> positionColorLightmapShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "position_color_tex_lightmap", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
               (Consumer<ShaderInstance>)var0 -> positionColorTexLightmapShader = var0
            )
         );
         var3.add(
            Pair.of(new ShaderInstance(var1, "position_tex", DefaultVertexFormat.POSITION_TEX), (Consumer<ShaderInstance>)var0 -> positionTexShader = var0)
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "position_tex_color", DefaultVertexFormat.POSITION_TEX_COLOR),
               (Consumer<ShaderInstance>)var0 -> positionTexColorShader = var0
            )
         );
         var3.add(
            Pair.of(new ShaderInstance(var1, "rendertype_solid", DefaultVertexFormat.BLOCK), (Consumer<ShaderInstance>)var0 -> rendertypeSolidShader = var0)
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_cutout_mipped", DefaultVertexFormat.BLOCK),
               (Consumer<ShaderInstance>)var0 -> rendertypeCutoutMippedShader = var0
            )
         );
         var3.add(
            Pair.of(new ShaderInstance(var1, "rendertype_cutout", DefaultVertexFormat.BLOCK), (Consumer<ShaderInstance>)var0 -> rendertypeCutoutShader = var0)
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_translucent", DefaultVertexFormat.BLOCK),
               (Consumer<ShaderInstance>)var0 -> rendertypeTranslucentShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_translucent_moving_block", DefaultVertexFormat.BLOCK),
               (Consumer<ShaderInstance>)var0 -> rendertypeTranslucentMovingBlockShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_armor_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeArmorCutoutNoCullShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_solid", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntitySolidShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_cutout", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntityCutoutShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntityCutoutNoCullShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_cutout_no_cull_z_offset", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntityCutoutNoCullZOffsetShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_item_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeItemEntityTranslucentCullShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_translucent_cull", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntityTranslucentCullShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_translucent", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntityTranslucentShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntityTranslucentEmissiveShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_smooth_cutout", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntitySmoothCutoutShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_beacon_beam", DefaultVertexFormat.BLOCK),
               (Consumer<ShaderInstance>)var0 -> rendertypeBeaconBeamShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_decal", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntityDecalShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_no_outline", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntityNoOutlineShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_shadow", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntityShadowShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_alpha", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntityAlphaShader = var0
            )
         );
         var3.add(
            Pair.of(new ShaderInstance(var1, "rendertype_eyes", DefaultVertexFormat.NEW_ENTITY), (Consumer<ShaderInstance>)var0 -> rendertypeEyesShader = var0)
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_energy_swirl", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeEnergySwirlShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_leash", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP),
               (Consumer<ShaderInstance>)var0 -> rendertypeLeashShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_water_mask", DefaultVertexFormat.POSITION),
               (Consumer<ShaderInstance>)var0 -> rendertypeWaterMaskShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_outline", DefaultVertexFormat.POSITION_TEX_COLOR),
               (Consumer<ShaderInstance>)var0 -> rendertypeOutlineShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_armor_entity_glint", DefaultVertexFormat.POSITION_TEX),
               (Consumer<ShaderInstance>)var0 -> rendertypeArmorEntityGlintShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_glint_translucent", DefaultVertexFormat.POSITION_TEX),
               (Consumer<ShaderInstance>)var0 -> rendertypeGlintTranslucentShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_glint", DefaultVertexFormat.POSITION_TEX), (Consumer<ShaderInstance>)var0 -> rendertypeGlintShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_glint", DefaultVertexFormat.POSITION_TEX),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntityGlintShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_entity_glint_direct", DefaultVertexFormat.POSITION_TEX),
               (Consumer<ShaderInstance>)var0 -> rendertypeEntityGlintDirectShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_text", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
               (Consumer<ShaderInstance>)var0 -> rendertypeTextShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_text_background", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP),
               (Consumer<ShaderInstance>)var0 -> rendertypeTextBackgroundShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_text_intensity", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
               (Consumer<ShaderInstance>)var0 -> rendertypeTextIntensityShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_text_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
               (Consumer<ShaderInstance>)var0 -> rendertypeTextSeeThroughShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_text_background_see_through", DefaultVertexFormat.POSITION_COLOR_LIGHTMAP),
               (Consumer<ShaderInstance>)var0 -> rendertypeTextBackgroundSeeThroughShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_text_intensity_see_through", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP),
               (Consumer<ShaderInstance>)var0 -> rendertypeTextIntensitySeeThroughShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_lightning", DefaultVertexFormat.POSITION_COLOR),
               (Consumer<ShaderInstance>)var0 -> rendertypeLightningShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_tripwire", DefaultVertexFormat.BLOCK), (Consumer<ShaderInstance>)var0 -> rendertypeTripwireShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_end_portal", DefaultVertexFormat.POSITION),
               (Consumer<ShaderInstance>)var0 -> rendertypeEndPortalShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_end_gateway", DefaultVertexFormat.POSITION),
               (Consumer<ShaderInstance>)var0 -> rendertypeEndGatewayShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_clouds", DefaultVertexFormat.POSITION_COLOR),
               (Consumer<ShaderInstance>)var0 -> rendertypeCloudsShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_lines", DefaultVertexFormat.POSITION_COLOR_NORMAL),
               (Consumer<ShaderInstance>)var0 -> rendertypeLinesShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_crumbling", DefaultVertexFormat.BLOCK), (Consumer<ShaderInstance>)var0 -> rendertypeCrumblingShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_gui", DefaultVertexFormat.POSITION_COLOR), (Consumer<ShaderInstance>)var0 -> rendertypeGuiShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_gui_overlay", DefaultVertexFormat.POSITION_COLOR),
               (Consumer<ShaderInstance>)var0 -> rendertypeGuiOverlayShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_gui_text_highlight", DefaultVertexFormat.POSITION_COLOR),
               (Consumer<ShaderInstance>)var0 -> rendertypeGuiTextHighlightShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_gui_ghost_recipe_overlay", DefaultVertexFormat.POSITION_COLOR),
               (Consumer<ShaderInstance>)var0 -> rendertypeGuiGhostRecipeOverlayShader = var0
            )
         );
         var3.add(
            Pair.of(
               new ShaderInstance(var1, "rendertype_breeze_wind", DefaultVertexFormat.NEW_ENTITY),
               (Consumer<ShaderInstance>)var0 -> rendertypeBreezeWindShader = var0
            )
         );
         this.loadBlurEffect(var1);
      } catch (IOException var5) {
         var3.forEach(var0 -> ((ShaderInstance)var0.getFirst()).close());
         throw new RuntimeException("could not reload shaders", var5);
      }

      this.shutdownShaders();
      var3.forEach(var1x -> {
         ShaderInstance var2x = (ShaderInstance)var1x.getFirst();
         this.shaders.put(var2x.getName(), var2x);
         ((Consumer)var1x.getSecond()).accept(var2x);
      });
      this.lightTexture.loadShader(var1);
   }

   private void shutdownShaders() {
      RenderSystem.assertOnRenderThread();
      this.shaders.values().forEach(ShaderInstance::close);
      this.shaders.clear();
   }

   @Nullable
   public ShaderInstance getShader(@Nullable String var1) {
      return var1 == null ? null : this.shaders.get(var1);
   }

   public void tick() {
      this.tickFov();
      this.lightTexture.tick();
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(this.minecraft.player);
      }

      this.mainCamera.tick();
      this.itemInHandRenderer.tick();
      this.confusionAnimationTick++;
      if (this.minecraft.level.tickRateManager().runsNormally()) {
         this.minecraft.levelRenderer.tickParticles(this.mainCamera);
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
            this.itemActivationTicks--;
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
      this.resourcePool.clear();
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
            this.minecraft.crosshairPickEntity = var7 instanceof EntityHitResult var8 ? var8.getEntity() : null;
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
      EntityHitResult var19 = ProjectileUtil.getEntityHitResult(var1, var11, var16, var18, EntitySelector.CAN_BE_PICKED, var9);
      return var19 != null && var19.getLocation().distanceToSqr(var11) < var13 ? filterHitResult(var19, var11, var4) : filterHitResult(var12, var11, var2);
   }

   private static HitResult filterHitResult(HitResult var0, Vec3 var1, double var2) {
      Vec3 var4 = var0.getLocation();
      if (!var4.closerThan(var1, var2)) {
         Vec3 var5 = var0.getLocation();
         Direction var6 = Direction.getApproximateNearest(var5.x - var1.x, var5.y - var1.y, var5.z - var1.z);
         return BlockHitResult.miss(var5, var6, BlockPos.containing(var5));
      } else {
         return var0;
      }
   }

   private void tickFov() {
      float var1;
      if (this.minecraft.getCameraEntity() instanceof AbstractClientPlayer var2) {
         Options var6 = this.minecraft.options;
         boolean var4 = var6.getCameraType().isFirstPerson();
         float var5 = var6.fovEffectScale().get().floatValue();
         var1 = Mth.lerp(var5, 1.0F, var2.getFieldOfViewModifier(var4));
      } else {
         var1 = 1.0F;
      }

      this.oldFovModifier = this.fovModifier;
      this.fovModifier = this.fovModifier + (var1 - this.fovModifier) * 0.5F;
      this.fovModifier = Mth.clamp(this.fovModifier, 0.1F, 1.5F);
   }

   private float getFov(Camera var1, float var2, boolean var3) {
      if (this.panoramicMode) {
         return 90.0F;
      } else {
         float var4 = 70.0F;
         if (var3) {
            var4 = (float)this.minecraft.options.fov().get().intValue();
            var4 *= Mth.lerp(var2, this.oldFovModifier, this.fovModifier);
         }

         if (var1.getEntity() instanceof LivingEntity var5 && var5.isDeadOrDying()) {
            float var9 = Math.min((float)var5.deathTime + var2, 20.0F);
            var4 /= (1.0F - 500.0F / (var9 + 500.0F)) * 2.0F + 1.0F;
         }

         FogType var8 = var1.getFluidInCamera();
         if (var8 == FogType.LAVA || var8 == FogType.WATER) {
            float var10 = this.minecraft.options.fovEffectScale().get().floatValue();
            var4 *= Mth.lerp(var10, 1.0F, 0.85714287F);
         }

         return var4;
      }
   }

   private void bobHurt(PoseStack var1, float var2) {
      if (this.minecraft.getCameraEntity() instanceof LivingEntity var3) {
         float var7 = (float)var3.hurtTime - var2;
         if (var3.isDeadOrDying()) {
            float var5 = Math.min((float)var3.deathTime + var2, 20.0F);
            var1.mulPose(Axis.ZP.rotationDegrees(40.0F - 8000.0F / (var5 + 200.0F)));
         }

         if (var7 < 0.0F) {
            return;
         }

         var7 /= (float)var3.hurtDuration;
         var7 = Mth.sin(var7 * var7 * var7 * var7 * 3.1415927F);
         float var10 = var3.getHurtDir();
         var1.mulPose(Axis.YP.rotationDegrees(-var10));
         float var6 = (float)((double)(-var7) * 14.0 * this.minecraft.options.damageTiltStrength().get());
         var1.mulPose(Axis.ZP.rotationDegrees(var6));
         var1.mulPose(Axis.YP.rotationDegrees(var10));
      }
   }

   private void bobView(PoseStack var1, float var2) {
      if (this.minecraft.getCameraEntity() instanceof AbstractClientPlayer var3) {
         float var7 = var3.walkDist - var3.walkDistO;
         float var5 = -(var3.walkDist + var7 * var2);
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
      this.renderLevel(DeltaTracker.ZERO);
      this.zoom = 1.0F;
   }

   private void renderItemInHand(Camera var1, float var2, Matrix4f var3) {
      if (!this.panoramicMode) {
         Matrix4f var4 = this.getProjectionMatrix(this.getFov(var1, var2, false));
         RenderSystem.setProjectionMatrix(var4, VertexSorting.DISTANCE_TO_ORIGIN);
         PoseStack var5 = new PoseStack();
         var5.pushPose();
         var5.mulPose(var3.invert(new Matrix4f()));
         Matrix4fStack var6 = RenderSystem.getModelViewStack();
         var6.pushMatrix().mul(var3);
         this.bobHurt(var5, var2);
         if (this.minecraft.options.bobView().get()) {
            this.bobView(var5, var2);
         }

         boolean var7 = this.minecraft.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.minecraft.getCameraEntity()).isSleeping();
         if (this.minecraft.options.getCameraType().isFirstPerson()
            && !var7
            && !this.minecraft.options.hideGui
            && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.lightTexture.turnOnLightLayer();
            this.itemInHandRenderer
               .renderHandsWithItems(
                  var2,
                  var5,
                  this.renderBuffers.bufferSource(),
                  this.minecraft.player,
                  this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, var2)
               );
            this.lightTexture.turnOffLightLayer();
         }

         var6.popMatrix();
         var5.popPose();
         if (this.minecraft.options.getCameraType().isFirstPerson() && !var7) {
            ScreenEffectRenderer.renderScreenEffect(this.minecraft, var5);
         }
      }
   }

   public Matrix4f getProjectionMatrix(float var1) {
      Matrix4f var2 = new Matrix4f();
      if (this.zoom != 1.0F) {
         var2.translate(this.zoomX, -this.zoomY, 0.0F);
         var2.scale(this.zoom, this.zoom, 1.0F);
      }

      return var2.perspective(
         var1 * 0.017453292F, (float)this.minecraft.getWindow().getWidth() / (float)this.minecraft.getWindow().getHeight(), 0.05F, this.getDepthFar()
      );
   }

   public float getDepthFar() {
      return this.renderDistance * 4.0F;
   }

   public static float getNightVisionScale(LivingEntity var0, float var1) {
      MobEffectInstance var2 = var0.getEffect(MobEffects.NIGHT_VISION);
      return !var2.endsWithin(200) ? 1.0F : 0.7F + Mth.sin(((float)var2.getDuration() - var1) * 3.1415927F * 0.2F) * 0.3F;
   }

   public void render(DeltaTracker var1, boolean var2) {
      if (!this.minecraft.isWindowActive()
         && this.minecraft.options.pauseOnLostFocus
         && (!this.minecraft.options.touchscreen().get() || !this.minecraft.mouseHandler.isRightPressed())) {
         if (Util.getMillis() - this.lastActiveTime > 500L) {
            this.minecraft.pauseGame(false);
         }
      } else {
         this.lastActiveTime = Util.getMillis();
      }

      if (!this.minecraft.noRender) {
         boolean var3 = this.minecraft.isGameLoadFinished();
         int var4 = (int)(
            this.minecraft.mouseHandler.xpos() * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth()
         );
         int var5 = (int)(
            this.minecraft.mouseHandler.ypos() * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight()
         );
         RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         if (var3 && var2 && this.minecraft.level != null) {
            this.minecraft.getProfiler().push("level");
            this.renderLevel(var1);
            this.tryTakeScreenshotIfNeeded();
            this.minecraft.levelRenderer.doEntityOutline();
            if (this.postEffect != null && this.effectActive) {
               RenderSystem.disableBlend();
               RenderSystem.disableDepthTest();
               RenderSystem.resetTextureMatrix();
               this.postEffect.process(this.minecraft.getMainRenderTarget(), this.resourcePool, var1);
            }

            this.minecraft.getMainRenderTarget().bindWrite(true);
         }

         Window var6 = this.minecraft.getWindow();
         RenderSystem.clear(256);
         Matrix4f var7 = new Matrix4f()
            .setOrtho(
               0.0F, (float)((double)var6.getWidth() / var6.getGuiScale()), (float)((double)var6.getHeight() / var6.getGuiScale()), 0.0F, 1000.0F, 21000.0F
            );
         RenderSystem.setProjectionMatrix(var7, VertexSorting.ORTHOGRAPHIC_Z);
         Matrix4fStack var8 = RenderSystem.getModelViewStack();
         var8.pushMatrix();
         var8.translation(0.0F, 0.0F, -11000.0F);
         Lighting.setupFor3DItems();
         GuiGraphics var9 = new GuiGraphics(this.minecraft, this.renderBuffers.bufferSource());
         if (var3 && var2 && this.minecraft.level != null) {
            this.minecraft.getProfiler().popPush("gui");
            if (!this.minecraft.options.hideGui) {
               this.renderItemActivationAnimation(var9, var1.getGameTimeDeltaPartialTick(false));
            }

            this.minecraft.gui.render(var9, var1);
            var9.flush();
            RenderSystem.clear(256);
            this.minecraft.getProfiler().pop();
         }

         if (this.minecraft.getOverlay() != null) {
            try {
               this.minecraft.getOverlay().render(var9, var4, var5, var1.getGameTimeDeltaTicks());
            } catch (Throwable var15) {
               CrashReport var11 = CrashReport.forThrowable(var15, "Rendering overlay");
               CrashReportCategory var12 = var11.addCategory("Overlay render details");
               var12.setDetail("Overlay name", () -> this.minecraft.getOverlay().getClass().getCanonicalName());
               throw new ReportedException(var11);
            }
         } else if (var3 && this.minecraft.screen != null) {
            try {
               this.minecraft.screen.renderWithTooltip(var9, var4, var5, var1.getGameTimeDeltaTicks());
            } catch (Throwable var14) {
               CrashReport var16 = CrashReport.forThrowable(var14, "Rendering screen");
               CrashReportCategory var18 = var16.addCategory("Screen render details");
               var18.setDetail("Screen name", () -> this.minecraft.screen.getClass().getCanonicalName());
               var18.setDetail(
                  "Mouse location",
                  () -> String.format(
                        Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", var4, var5, this.minecraft.mouseHandler.xpos(), this.minecraft.mouseHandler.ypos()
                     )
               );
               var18.setDetail(
                  "Screen size",
                  () -> String.format(
                        Locale.ROOT,
                        "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f",
                        this.minecraft.getWindow().getGuiScaledWidth(),
                        this.minecraft.getWindow().getGuiScaledHeight(),
                        this.minecraft.getWindow().getWidth(),
                        this.minecraft.getWindow().getHeight(),
                        this.minecraft.getWindow().getGuiScale()
                     )
               );
               throw new ReportedException(var16);
            }

            try {
               if (this.minecraft.screen != null) {
                  this.minecraft.screen.handleDelayedNarration();
               }
            } catch (Throwable var13) {
               CrashReport var17 = CrashReport.forThrowable(var13, "Narrating screen");
               CrashReportCategory var19 = var17.addCategory("Screen details");
               var19.setDetail("Screen name", () -> this.minecraft.screen.getClass().getCanonicalName());
               throw new ReportedException(var17);
            }
         }

         if (var3 && var2 && this.minecraft.level != null) {
            this.minecraft.gui.renderSavingIndicator(var9, var1);
         }

         if (var3) {
            this.minecraft.getProfiler().push("toasts");
            this.minecraft.getToastManager().render(var9);
            this.minecraft.getProfiler().pop();
         }

         var9.flush();
         var8.popMatrix();
         this.resourcePool.endFrame();
      }
   }

   private void tryTakeScreenshotIfNeeded() {
      if (!this.hasWorldScreenshot && this.minecraft.isLocalServer()) {
         long var1 = Util.getMillis();
         if (var1 - this.lastScreenshotAttempt >= 1000L) {
            this.lastScreenshotAttempt = var1;
            IntegratedServer var3 = this.minecraft.getSingleplayerServer();
            if (var3 != null && !var3.isStopped()) {
               var3.getWorldScreenshotFile().ifPresent(var1x -> {
                  if (Files.isRegularFile(var1x)) {
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

            try (NativeImage var6 = new NativeImage(64, 64, false)) {
               var2.resizeSubRectTo(var4, var5, var2x, var3, var6);
               var6.writeToFile(var1);
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

   public void renderLevel(DeltaTracker var1) {
      float var2 = var1.getGameTimeDeltaPartialTick(true);
      this.lightTexture.updateLightTexture(var2);
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(this.minecraft.player);
      }

      this.pick(var2);
      this.minecraft.getProfiler().push("center");
      boolean var3 = this.shouldRenderBlockOutline();
      this.minecraft.getProfiler().popPush("camera");
      Camera var4 = this.mainCamera;
      Object var5 = this.minecraft.getCameraEntity() == null ? this.minecraft.player : this.minecraft.getCameraEntity();
      float var6 = this.minecraft.level.tickRateManager().isEntityFrozen((Entity)var5) ? 1.0F : var2;
      var4.setup(
         this.minecraft.level, (Entity)var5, !this.minecraft.options.getCameraType().isFirstPerson(), this.minecraft.options.getCameraType().isMirrored(), var6
      );
      this.renderDistance = (float)(this.minecraft.options.getEffectiveRenderDistance() * 16);
      float var7 = this.getFov(var4, var2, true);
      Matrix4f var8 = this.getProjectionMatrix(var7);
      PoseStack var9 = new PoseStack();
      this.bobHurt(var9, var4.getPartialTickTime());
      if (this.minecraft.options.bobView().get()) {
         this.bobView(var9, var4.getPartialTickTime());
      }

      var8.mul(var9.last().pose());
      float var10 = this.minecraft.options.screenEffectScale().get().floatValue();
      float var11 = Mth.lerp(var2, this.minecraft.player.oSpinningEffectIntensity, this.minecraft.player.spinningEffectIntensity) * var10 * var10;
      if (var11 > 0.0F) {
         int var12 = this.minecraft.player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
         float var13 = 5.0F / (var11 * var11 + 5.0F) - var11 * 0.04F;
         var13 *= var13;
         Vector3f var14 = new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
         float var15 = ((float)this.confusionAnimationTick + var2) * (float)var12 * 0.017453292F;
         var8.rotate(var15, var14);
         var8.scale(1.0F / var13, 1.0F, 1.0F);
         var8.rotate(-var15, var14);
      }

      float var16 = Math.max(var7, (float)this.minecraft.options.fov().get().intValue());
      Matrix4f var18 = this.getProjectionMatrix(var16);
      RenderSystem.setProjectionMatrix(var8, VertexSorting.DISTANCE_TO_ORIGIN);
      Quaternionf var19 = var4.rotation().conjugate(new Quaternionf());
      Matrix4f var20 = new Matrix4f().rotation(var19);
      this.minecraft.levelRenderer.prepareCullFrustum(var4.getPosition(), var20, var18);
      this.minecraft.getMainRenderTarget().bindWrite(true);
      this.minecraft.levelRenderer.renderLevel(this.resourcePool, var1, var3, var4, this, this.lightTexture, var20, var8);
      this.minecraft.getProfiler().popPush("hand");
      if (this.renderHand) {
         RenderSystem.clear(256);
         this.renderItemInHand(var4, var2, var20);
      }

      this.minecraft.getProfiler().pop();
   }

   public void resetData() {
      this.itemActivationItem = null;
      this.minecraft.getMapTextureManager().resetData();
      this.mainCamera.reset();
      this.hasWorldScreenshot = false;
   }

   public void displayItemActivation(ItemStack var1) {
      this.itemActivationItem = var1;
      this.itemActivationTicks = 40;
      this.itemActivationOffX = this.random.nextFloat() * 2.0F - 1.0F;
      this.itemActivationOffY = this.random.nextFloat() * 2.0F - 1.0F;
   }

   private void renderItemActivationAnimation(GuiGraphics var1, float var2) {
      if (this.itemActivationItem != null && this.itemActivationTicks > 0) {
         int var3 = 40 - this.itemActivationTicks;
         float var4 = ((float)var3 + var2) / 40.0F;
         float var5 = var4 * var4;
         float var6 = var4 * var5;
         float var7 = 10.25F * var6 * var5 - 24.95F * var5 * var5 + 25.5F * var6 - 13.8F * var5 + 4.0F * var4;
         float var8 = var7 * 3.1415927F;
         float var9 = this.itemActivationOffX * (float)(var1.guiWidth() / 4);
         float var10 = this.itemActivationOffY * (float)(var1.guiHeight() / 4);
         PoseStack var11 = new PoseStack();
         var11.pushPose();
         var11.translate(
            (float)(var1.guiWidth() / 2) + var9 * Mth.abs(Mth.sin(var8 * 2.0F)), (float)(var1.guiHeight() / 2) + var10 * Mth.abs(Mth.sin(var8 * 2.0F)), -50.0F
         );
         float var12 = 50.0F + 175.0F * Mth.sin(var8);
         var11.scale(var12, -var12, var12);
         var11.mulPose(Axis.YP.rotationDegrees(900.0F * Mth.abs(Mth.sin(var8))));
         var11.mulPose(Axis.XP.rotationDegrees(6.0F * Mth.cos(var4 * 8.0F)));
         var11.mulPose(Axis.ZP.rotationDegrees(6.0F * Mth.cos(var4 * 8.0F)));
         this.minecraft
            .getItemRenderer()
            .renderStatic(
               this.itemActivationItem, ItemDisplayContext.FIXED, 15728880, OverlayTexture.NO_OVERLAY, var11, var1.bufferSource(), this.minecraft.level, 0
            );
         var11.popPose();
      }
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
