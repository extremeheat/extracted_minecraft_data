package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;

public class LevelRenderer implements ResourceManagerReloadListener, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int SECTION_SIZE = 16;
   public static final int HALF_SECTION_SIZE = 8;
   private static final float SKY_DISC_RADIUS = 512.0F;
   private static final int MIN_FOG_DISTANCE = 32;
   private static final int RAIN_RADIUS = 10;
   private static final int RAIN_DIAMETER = 21;
   private static final int TRANSPARENT_SORT_COUNT = 15;
   private static final ResourceLocation MOON_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
   private static final ResourceLocation SUN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");
   protected static final ResourceLocation CLOUDS_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/clouds.png");
   private static final ResourceLocation END_SKY_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");
   private static final ResourceLocation FORCEFIELD_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/forcefield.png");
   private static final ResourceLocation RAIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/rain.png");
   private static final ResourceLocation SNOW_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/snow.png");
   public static final Direction[] DIRECTIONS = Direction.values();
   private final Minecraft minecraft;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
   private final RenderBuffers renderBuffers;
   @Nullable
   private ClientLevel level;
   private final SectionOcclusionGraph sectionOcclusionGraph = new SectionOcclusionGraph();
   private final ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections = new ObjectArrayList(10000);
   private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
   @Nullable
   private ViewArea viewArea;
   @Nullable
   private VertexBuffer starBuffer;
   @Nullable
   private VertexBuffer skyBuffer;
   @Nullable
   private VertexBuffer darkBuffer;
   private boolean generateClouds = true;
   @Nullable
   private VertexBuffer cloudBuffer;
   private final RunningTrimmedMean frameTimes = new RunningTrimmedMean(100);
   private int ticks;
   private final Int2ObjectMap<BlockDestructionProgress> destroyingBlocks = new Int2ObjectOpenHashMap();
   private final Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress = new Long2ObjectOpenHashMap();
   private final Map<BlockPos, SoundInstance> playingJukeboxSongs = Maps.newHashMap();
   @Nullable
   private RenderTarget entityTarget;
   @Nullable
   private PostChain entityEffect;
   @Nullable
   private RenderTarget translucentTarget;
   @Nullable
   private RenderTarget itemEntityTarget;
   @Nullable
   private RenderTarget particlesTarget;
   @Nullable
   private RenderTarget weatherTarget;
   @Nullable
   private RenderTarget cloudsTarget;
   @Nullable
   private PostChain transparencyChain;
   private int lastCameraSectionX = -2147483648;
   private int lastCameraSectionY = -2147483648;
   private int lastCameraSectionZ = -2147483648;
   private double prevCamX = 4.9E-324;
   private double prevCamY = 4.9E-324;
   private double prevCamZ = 4.9E-324;
   private double prevCamRotX = 4.9E-324;
   private double prevCamRotY = 4.9E-324;
   private int prevCloudX = -2147483648;
   private int prevCloudY = -2147483648;
   private int prevCloudZ = -2147483648;
   private Vec3 prevCloudColor;
   @Nullable
   private CloudStatus prevCloudsType;
   @Nullable
   private SectionRenderDispatcher sectionRenderDispatcher;
   private int lastViewDistance;
   private int renderedEntities;
   private int culledEntities;
   private Frustum cullingFrustum;
   private boolean captureFrustum;
   @Nullable
   private Frustum capturedFrustum;
   private final Vector4f[] frustumPoints;
   private final Vector3d frustumPos;
   private double xTransparentOld;
   private double yTransparentOld;
   private double zTransparentOld;
   private int rainSoundTime;
   private final float[] rainSizeX;
   private final float[] rainSizeZ;

   public LevelRenderer(Minecraft var1, EntityRenderDispatcher var2, BlockEntityRenderDispatcher var3, RenderBuffers var4) {
      super();
      this.prevCloudColor = Vec3.ZERO;
      this.lastViewDistance = -1;
      this.frustumPoints = new Vector4f[8];
      this.frustumPos = new Vector3d(0.0, 0.0, 0.0);
      this.rainSizeX = new float[1024];
      this.rainSizeZ = new float[1024];
      this.minecraft = var1;
      this.entityRenderDispatcher = var2;
      this.blockEntityRenderDispatcher = var3;
      this.renderBuffers = var4;

      for(int var5 = 0; var5 < 32; ++var5) {
         for(int var6 = 0; var6 < 32; ++var6) {
            float var7 = (float)(var6 - 16);
            float var8 = (float)(var5 - 16);
            float var9 = Mth.sqrt(var7 * var7 + var8 * var8);
            this.rainSizeX[var5 << 5 | var6] = -var8 / var9;
            this.rainSizeZ[var5 << 5 | var6] = var7 / var9;
         }
      }

      this.createStars();
      this.createLightSky();
      this.createDarkSky();
   }

   private void renderSnowAndRain(LightTexture var1, float var2, double var3, double var5, double var7) {
      float var9 = this.minecraft.level.getRainLevel(var2);
      if (!(var9 <= 0.0F)) {
         var1.turnOnLightLayer();
         ClientLevel var10 = this.minecraft.level;
         int var11 = Mth.floor(var3);
         int var12 = Mth.floor(var5);
         int var13 = Mth.floor(var7);
         Tesselator var14 = Tesselator.getInstance();
         BufferBuilder var15 = null;
         RenderSystem.disableCull();
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         byte var16 = 5;
         if (Minecraft.useFancyGraphics()) {
            var16 = 10;
         }

         RenderSystem.depthMask(Minecraft.useShaderTransparency());
         byte var17 = -1;
         float var18 = (float)this.ticks + var2;
         RenderSystem.setShader(GameRenderer::getParticleShader);
         BlockPos.MutableBlockPos var19 = new BlockPos.MutableBlockPos();

         for(int var20 = var13 - var16; var20 <= var13 + var16; ++var20) {
            for(int var21 = var11 - var16; var21 <= var11 + var16; ++var21) {
               int var22 = (var20 - var13 + 16) * 32 + var21 - var11 + 16;
               double var23 = (double)this.rainSizeX[var22] * 0.5;
               double var25 = (double)this.rainSizeZ[var22] * 0.5;
               var19.set((double)var21, var5, (double)var20);
               Biome var27 = (Biome)((Level)var10).getBiome(var19).value();
               if (var27.hasPrecipitation()) {
                  int var28 = ((Level)var10).getHeight(Heightmap.Types.MOTION_BLOCKING, var21, var20);
                  int var29 = var12 - var16;
                  int var30 = var12 + var16;
                  if (var29 < var28) {
                     var29 = var28;
                  }

                  if (var30 < var28) {
                     var30 = var28;
                  }

                  int var31 = var28;
                  if (var28 < var12) {
                     var31 = var12;
                  }

                  if (var29 != var30) {
                     RandomSource var32 = RandomSource.create((long)(var21 * var21 * 3121 + var21 * 45238971 ^ var20 * var20 * 418711 + var20 * 13761));
                     var19.set(var21, var29, var20);
                     Biome.Precipitation var33 = var27.getPrecipitationAt(var19);
                     float var36;
                     double var39;
                     int var45;
                     if (var33 == Biome.Precipitation.RAIN) {
                        if (var17 != 0) {
                           if (var17 >= 0) {
                              BufferUploader.drawWithShader(var15.buildOrThrow());
                           }

                           var17 = 0;
                           RenderSystem.setShaderTexture(0, RAIN_LOCATION);
                           var15 = var14.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                        }

                        int var34 = this.ticks & 131071;
                        int var35 = var21 * var21 * 3121 + var21 * 45238971 + var20 * var20 * 418711 + var20 * 13761 & 255;
                        var36 = 3.0F + var32.nextFloat();
                        float var37 = -((float)(var34 + var35) + var2) / 32.0F * var36;
                        float var38 = var37 % 32.0F;
                        var39 = (double)var21 + 0.5 - var3;
                        double var41 = (double)var20 + 0.5 - var7;
                        float var43 = (float)Math.sqrt(var39 * var39 + var41 * var41) / (float)var16;
                        float var44 = ((1.0F - var43 * var43) * 0.5F + 0.5F) * var9;
                        var19.set(var21, var31, var20);
                        var45 = getLightColor(var10, var19);
                        var15.addVertex((float)((double)var21 - var3 - var23 + 0.5), (float)((double)var30 - var5), (float)((double)var20 - var7 - var25 + 0.5)).setUv(0.0F, (float)var29 * 0.25F + var38).setColor(1.0F, 1.0F, 1.0F, var44).setLight(var45);
                        var15.addVertex((float)((double)var21 - var3 + var23 + 0.5), (float)((double)var30 - var5), (float)((double)var20 - var7 + var25 + 0.5)).setUv(1.0F, (float)var29 * 0.25F + var38).setColor(1.0F, 1.0F, 1.0F, var44).setLight(var45);
                        var15.addVertex((float)((double)var21 - var3 + var23 + 0.5), (float)((double)var29 - var5), (float)((double)var20 - var7 + var25 + 0.5)).setUv(1.0F, (float)var30 * 0.25F + var38).setColor(1.0F, 1.0F, 1.0F, var44).setLight(var45);
                        var15.addVertex((float)((double)var21 - var3 - var23 + 0.5), (float)((double)var29 - var5), (float)((double)var20 - var7 - var25 + 0.5)).setUv(0.0F, (float)var30 * 0.25F + var38).setColor(1.0F, 1.0F, 1.0F, var44).setLight(var45);
                     } else if (var33 == Biome.Precipitation.SNOW) {
                        if (var17 != 1) {
                           if (var17 >= 0) {
                              BufferUploader.drawWithShader(var15.buildOrThrow());
                           }

                           var17 = 1;
                           RenderSystem.setShaderTexture(0, SNOW_LOCATION);
                           var15 = var14.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                        }

                        float var50 = -((float)(this.ticks & 511) + var2) / 512.0F;
                        float var51 = (float)(var32.nextDouble() + (double)var18 * 0.01 * (double)((float)var32.nextGaussian()));
                        var36 = (float)(var32.nextDouble() + (double)(var18 * (float)var32.nextGaussian()) * 0.001);
                        double var52 = (double)var21 + 0.5 - var3;
                        var39 = (double)var20 + 0.5 - var7;
                        float var53 = (float)Math.sqrt(var52 * var52 + var39 * var39) / (float)var16;
                        float var42 = ((1.0F - var53 * var53) * 0.3F + 0.5F) * var9;
                        var19.set(var21, var31, var20);
                        int var48 = getLightColor(var10, var19);
                        int var49 = var48 >> 16 & '\uffff';
                        var45 = var48 & '\uffff';
                        int var46 = (var49 * 3 + 240) / 4;
                        int var47 = (var45 * 3 + 240) / 4;
                        var15.addVertex((float)((double)var21 - var3 - var23 + 0.5), (float)((double)var30 - var5), (float)((double)var20 - var7 - var25 + 0.5)).setUv(0.0F + var51, (float)var29 * 0.25F + var50 + var36).setColor(1.0F, 1.0F, 1.0F, var42).setUv2(var47, var46);
                        var15.addVertex((float)((double)var21 - var3 + var23 + 0.5), (float)((double)var30 - var5), (float)((double)var20 - var7 + var25 + 0.5)).setUv(1.0F + var51, (float)var29 * 0.25F + var50 + var36).setColor(1.0F, 1.0F, 1.0F, var42).setUv2(var47, var46);
                        var15.addVertex((float)((double)var21 - var3 + var23 + 0.5), (float)((double)var29 - var5), (float)((double)var20 - var7 + var25 + 0.5)).setUv(1.0F + var51, (float)var30 * 0.25F + var50 + var36).setColor(1.0F, 1.0F, 1.0F, var42).setUv2(var47, var46);
                        var15.addVertex((float)((double)var21 - var3 - var23 + 0.5), (float)((double)var29 - var5), (float)((double)var20 - var7 - var25 + 0.5)).setUv(0.0F + var51, (float)var30 * 0.25F + var50 + var36).setColor(1.0F, 1.0F, 1.0F, var42).setUv2(var47, var46);
                     }
                  }
               }
            }
         }

         if (var17 >= 0) {
            BufferUploader.drawWithShader(var15.buildOrThrow());
         }

         RenderSystem.enableCull();
         RenderSystem.disableBlend();
         var1.turnOffLightLayer();
      }
   }

   public void tickRain(Camera var1) {
      float var2 = this.minecraft.level.getRainLevel(1.0F) / (Minecraft.useFancyGraphics() ? 1.0F : 2.0F);
      if (!(var2 <= 0.0F)) {
         RandomSource var3 = RandomSource.create((long)this.ticks * 312987231L);
         ClientLevel var4 = this.minecraft.level;
         BlockPos var5 = BlockPos.containing(var1.getPosition());
         BlockPos var6 = null;
         int var7 = (int)(100.0F * var2 * var2) / (this.minecraft.options.particles().get() == ParticleStatus.DECREASED ? 2 : 1);

         for(int var8 = 0; var8 < var7; ++var8) {
            int var9 = var3.nextInt(21) - 10;
            int var10 = var3.nextInt(21) - 10;
            BlockPos var11 = var4.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var5.offset(var9, 0, var10));
            if (var11.getY() > var4.getMinBuildHeight() && var11.getY() <= var5.getY() + 10 && var11.getY() >= var5.getY() - 10) {
               Biome var12 = (Biome)var4.getBiome(var11).value();
               if (var12.getPrecipitationAt(var11) == Biome.Precipitation.RAIN) {
                  var6 = var11.below();
                  if (this.minecraft.options.particles().get() == ParticleStatus.MINIMAL) {
                     break;
                  }

                  double var13 = var3.nextDouble();
                  double var15 = var3.nextDouble();
                  BlockState var17 = var4.getBlockState(var6);
                  FluidState var18 = var4.getFluidState(var6);
                  VoxelShape var19 = var17.getCollisionShape(var4, var6);
                  double var20 = var19.max(Direction.Axis.Y, var13, var15);
                  double var22 = (double)var18.getHeight(var4, var6);
                  double var24 = Math.max(var20, var22);
                  SimpleParticleType var26 = !var18.is(FluidTags.LAVA) && !var17.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(var17) ? ParticleTypes.RAIN : ParticleTypes.SMOKE;
                  this.minecraft.level.addParticle(var26, (double)var6.getX() + var13, (double)var6.getY() + var24, (double)var6.getZ() + var15, 0.0, 0.0, 0.0);
               }
            }
         }

         if (var6 != null && var3.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            if (var6.getY() > var5.getY() + 1 && var4.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var5).getY() > Mth.floor((float)var5.getY())) {
               this.minecraft.level.playLocalSound(var6, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1F, 0.5F, false);
            } else {
               this.minecraft.level.playLocalSound(var6, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2F, 1.0F, false);
            }
         }

      }
   }

   public void close() {
      if (this.entityEffect != null) {
         this.entityEffect.close();
      }

      if (this.transparencyChain != null) {
         this.transparencyChain.close();
      }

   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.initOutline();
      if (Minecraft.useShaderTransparency()) {
         this.initTransparency();
      }

   }

   public void initOutline() {
      if (this.entityEffect != null) {
         this.entityEffect.close();
      }

      ResourceLocation var1 = ResourceLocation.withDefaultNamespace("shaders/post/entity_outline.json");

      try {
         this.entityEffect = new PostChain(this.minecraft.getTextureManager(), this.minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), var1);
         this.entityEffect.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         this.entityTarget = this.entityEffect.getTempTarget("final");
      } catch (IOException var3) {
         LOGGER.warn("Failed to load shader: {}", var1, var3);
         this.entityEffect = null;
         this.entityTarget = null;
      } catch (JsonSyntaxException var4) {
         LOGGER.warn("Failed to parse shader: {}", var1, var4);
         this.entityEffect = null;
         this.entityTarget = null;
      }

   }

   private void initTransparency() {
      this.deinitTransparency();
      ResourceLocation var1 = ResourceLocation.withDefaultNamespace("shaders/post/transparency.json");

      try {
         PostChain var2 = new PostChain(this.minecraft.getTextureManager(), this.minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), var1);
         var2.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         RenderTarget var9 = var2.getTempTarget("translucent");
         RenderTarget var10 = var2.getTempTarget("itemEntity");
         RenderTarget var11 = var2.getTempTarget("particles");
         RenderTarget var12 = var2.getTempTarget("weather");
         RenderTarget var7 = var2.getTempTarget("clouds");
         this.transparencyChain = var2;
         this.translucentTarget = var9;
         this.itemEntityTarget = var10;
         this.particlesTarget = var11;
         this.weatherTarget = var12;
         this.cloudsTarget = var7;
      } catch (Exception var8) {
         String var3 = var8 instanceof JsonSyntaxException ? "parse" : "load";
         String var4 = "Failed to " + var3 + " shader: " + String.valueOf(var1);
         TransparencyShaderException var5 = new TransparencyShaderException(var4, var8);
         if (this.minecraft.getResourcePackRepository().getSelectedIds().size() > 1) {
            Component var6 = (Component)this.minecraft.getResourceManager().listPacks().findFirst().map((var0) -> {
               return Component.literal(var0.packId());
            }).orElse((Object)null);
            this.minecraft.options.graphicsMode().set(GraphicsStatus.FANCY);
            this.minecraft.clearResourcePacksOnError(var5, var6, (Minecraft.GameLoadCookie)null);
         } else {
            this.minecraft.options.graphicsMode().set(GraphicsStatus.FANCY);
            this.minecraft.options.save();
            LOGGER.error(LogUtils.FATAL_MARKER, var4, var5);
            this.minecraft.emergencySaveAndCrash(new CrashReport(var4, var5));
         }
      }

   }

   private void deinitTransparency() {
      if (this.transparencyChain != null) {
         this.transparencyChain.close();
         this.translucentTarget.destroyBuffers();
         this.itemEntityTarget.destroyBuffers();
         this.particlesTarget.destroyBuffers();
         this.weatherTarget.destroyBuffers();
         this.cloudsTarget.destroyBuffers();
         this.transparencyChain = null;
         this.translucentTarget = null;
         this.itemEntityTarget = null;
         this.particlesTarget = null;
         this.weatherTarget = null;
         this.cloudsTarget = null;
      }

   }

   public void doEntityOutline() {
      if (this.shouldShowEntityOutlines()) {
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         this.entityTarget.blitToScreen(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), false);
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      }

   }

   protected boolean shouldShowEntityOutlines() {
      return !this.minecraft.gameRenderer.isPanoramicMode() && this.entityTarget != null && this.entityEffect != null && this.minecraft.player != null;
   }

   private void createDarkSky() {
      if (this.darkBuffer != null) {
         this.darkBuffer.close();
      }

      this.darkBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
      this.darkBuffer.bind();
      this.darkBuffer.upload(buildSkyDisc(Tesselator.getInstance(), -16.0F));
      VertexBuffer.unbind();
   }

   private void createLightSky() {
      if (this.skyBuffer != null) {
         this.skyBuffer.close();
      }

      this.skyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
      this.skyBuffer.bind();
      this.skyBuffer.upload(buildSkyDisc(Tesselator.getInstance(), 16.0F));
      VertexBuffer.unbind();
   }

   private static MeshData buildSkyDisc(Tesselator var0, float var1) {
      float var2 = Math.signum(var1) * 512.0F;
      float var3 = 512.0F;
      BufferBuilder var4 = var0.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
      var4.addVertex(0.0F, var1, 0.0F);

      for(int var5 = -180; var5 <= 180; var5 += 45) {
         var4.addVertex(var2 * Mth.cos((float)var5 * 0.017453292F), var1, 512.0F * Mth.sin((float)var5 * 0.017453292F));
      }

      return var4.buildOrThrow();
   }

   private void createStars() {
      if (this.starBuffer != null) {
         this.starBuffer.close();
      }

      this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
      this.starBuffer.bind();
      this.starBuffer.upload(this.drawStars(Tesselator.getInstance()));
      VertexBuffer.unbind();
   }

   private MeshData drawStars(Tesselator var1) {
      RandomSource var2 = RandomSource.create(10842L);
      boolean var3 = true;
      float var4 = 100.0F;
      BufferBuilder var5 = var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

      for(int var6 = 0; var6 < 1500; ++var6) {
         float var7 = var2.nextFloat() * 2.0F - 1.0F;
         float var8 = var2.nextFloat() * 2.0F - 1.0F;
         float var9 = var2.nextFloat() * 2.0F - 1.0F;
         float var10 = 0.15F + var2.nextFloat() * 0.1F;
         float var11 = Mth.lengthSquared(var7, var8, var9);
         if (!(var11 <= 0.010000001F) && !(var11 >= 1.0F)) {
            Vector3f var12 = (new Vector3f(var7, var8, var9)).normalize(100.0F);
            float var13 = (float)(var2.nextDouble() * 3.1415927410125732 * 2.0);
            Quaternionf var14 = (new Quaternionf()).rotateTo(new Vector3f(0.0F, 0.0F, -1.0F), var12).rotateZ(var13);
            var5.addVertex(var12.add((new Vector3f(var10, -var10, 0.0F)).rotate(var14)));
            var5.addVertex(var12.add((new Vector3f(var10, var10, 0.0F)).rotate(var14)));
            var5.addVertex(var12.add((new Vector3f(-var10, var10, 0.0F)).rotate(var14)));
            var5.addVertex(var12.add((new Vector3f(-var10, -var10, 0.0F)).rotate(var14)));
         }
      }

      return var5.buildOrThrow();
   }

   public void setLevel(@Nullable ClientLevel var1) {
      this.lastCameraSectionX = -2147483648;
      this.lastCameraSectionY = -2147483648;
      this.lastCameraSectionZ = -2147483648;
      this.entityRenderDispatcher.setLevel(var1);
      this.level = var1;
      if (var1 != null) {
         this.allChanged();
      } else {
         if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
            this.viewArea = null;
         }

         if (this.sectionRenderDispatcher != null) {
            this.sectionRenderDispatcher.dispose();
         }

         this.sectionRenderDispatcher = null;
         this.globalBlockEntities.clear();
         this.sectionOcclusionGraph.waitAndReset((ViewArea)null);
         this.visibleSections.clear();
      }

   }

   public void graphicsChanged() {
      if (Minecraft.useShaderTransparency()) {
         this.initTransparency();
      } else {
         this.deinitTransparency();
      }

   }

   public void allChanged() {
      if (this.level != null) {
         this.graphicsChanged();
         this.level.clearTintCaches();
         if (this.sectionRenderDispatcher == null) {
            this.sectionRenderDispatcher = new SectionRenderDispatcher(this.level, this, Util.backgroundExecutor(), this.renderBuffers, this.minecraft.getBlockRenderer(), this.minecraft.getBlockEntityRenderDispatcher());
         } else {
            this.sectionRenderDispatcher.setLevel(this.level);
         }

         this.generateClouds = true;
         ItemBlockRenderTypes.setFancy(Minecraft.useFancyGraphics());
         this.lastViewDistance = this.minecraft.options.getEffectiveRenderDistance();
         if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
         }

         this.sectionRenderDispatcher.blockUntilClear();
         synchronized(this.globalBlockEntities) {
            this.globalBlockEntities.clear();
         }

         this.viewArea = new ViewArea(this.sectionRenderDispatcher, this.level, this.minecraft.options.getEffectiveRenderDistance(), this);
         this.sectionOcclusionGraph.waitAndReset(this.viewArea);
         this.visibleSections.clear();
         Entity var1 = this.minecraft.getCameraEntity();
         if (var1 != null) {
            this.viewArea.repositionCamera(var1.getX(), var1.getZ());
         }

      }
   }

   public void resize(int var1, int var2) {
      this.needsUpdate();
      if (this.entityEffect != null) {
         this.entityEffect.resize(var1, var2);
      }

      if (this.transparencyChain != null) {
         this.transparencyChain.resize(var1, var2);
      }

   }

   public String getSectionStatistics() {
      int var1 = this.viewArea.sections.length;
      int var2 = this.countRenderedSections();
      return String.format(Locale.ROOT, "C: %d/%d %sD: %d, %s", var2, var1, this.minecraft.smartCull ? "(s) " : "", this.lastViewDistance, this.sectionRenderDispatcher == null ? "null" : this.sectionRenderDispatcher.getStats());
   }

   public SectionRenderDispatcher getSectionRenderDispatcher() {
      return this.sectionRenderDispatcher;
   }

   public double getTotalSections() {
      return (double)this.viewArea.sections.length;
   }

   public double getLastViewDistance() {
      return (double)this.lastViewDistance;
   }

   public int countRenderedSections() {
      int var1 = 0;
      ObjectListIterator var2 = this.visibleSections.iterator();

      while(var2.hasNext()) {
         SectionRenderDispatcher.RenderSection var3 = (SectionRenderDispatcher.RenderSection)var2.next();
         if (!var3.getCompiled().hasNoRenderableLayers()) {
            ++var1;
         }
      }

      return var1;
   }

   public String getEntityStatistics() {
      int var10000 = this.renderedEntities;
      return "E: " + var10000 + "/" + this.level.getEntityCount() + ", B: " + this.culledEntities + ", SD: " + this.level.getServerSimulationDistance();
   }

   private void setupRender(Camera var1, Frustum var2, boolean var3, boolean var4) {
      Vec3 var5 = var1.getPosition();
      if (this.minecraft.options.getEffectiveRenderDistance() != this.lastViewDistance) {
         this.allChanged();
      }

      this.level.getProfiler().push("camera");
      double var6 = this.minecraft.player.getX();
      double var8 = this.minecraft.player.getY();
      double var10 = this.minecraft.player.getZ();
      int var12 = SectionPos.posToSectionCoord(var6);
      int var13 = SectionPos.posToSectionCoord(var8);
      int var14 = SectionPos.posToSectionCoord(var10);
      if (this.lastCameraSectionX != var12 || this.lastCameraSectionY != var13 || this.lastCameraSectionZ != var14) {
         this.lastCameraSectionX = var12;
         this.lastCameraSectionY = var13;
         this.lastCameraSectionZ = var14;
         this.viewArea.repositionCamera(var6, var10);
      }

      this.sectionRenderDispatcher.setCamera(var5);
      this.level.getProfiler().popPush("cull");
      this.minecraft.getProfiler().popPush("culling");
      BlockPos var15 = var1.getBlockPosition();
      double var16 = Math.floor(var5.x / 8.0);
      double var18 = Math.floor(var5.y / 8.0);
      double var20 = Math.floor(var5.z / 8.0);
      if (var16 != this.prevCamX || var18 != this.prevCamY || var20 != this.prevCamZ) {
         this.sectionOcclusionGraph.invalidate();
      }

      this.prevCamX = var16;
      this.prevCamY = var18;
      this.prevCamZ = var20;
      this.minecraft.getProfiler().popPush("update");
      if (!var3) {
         boolean var22 = this.minecraft.smartCull;
         if (var4 && this.level.getBlockState(var15).isSolidRender(this.level, var15)) {
            var22 = false;
         }

         Entity.setViewScale(Mth.clamp((double)this.minecraft.options.getEffectiveRenderDistance() / 8.0, 1.0, 2.5) * (Double)this.minecraft.options.entityDistanceScaling().get());
         this.minecraft.getProfiler().push("section_occlusion_graph");
         this.sectionOcclusionGraph.update(var22, var1, var2, this.visibleSections);
         this.minecraft.getProfiler().pop();
         double var23 = Math.floor((double)(var1.getXRot() / 2.0F));
         double var25 = Math.floor((double)(var1.getYRot() / 2.0F));
         if (this.sectionOcclusionGraph.consumeFrustumUpdate() || var23 != this.prevCamRotX || var25 != this.prevCamRotY) {
            this.applyFrustum(offsetFrustum(var2));
            this.prevCamRotX = var23;
            this.prevCamRotY = var25;
         }
      }

      this.minecraft.getProfiler().pop();
   }

   public static Frustum offsetFrustum(Frustum var0) {
      return (new Frustum(var0)).offsetToFullyIncludeCameraCube(8);
   }

   private void applyFrustum(Frustum var1) {
      if (!Minecraft.getInstance().isSameThread()) {
         throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
      } else {
         this.minecraft.getProfiler().push("apply_frustum");
         this.visibleSections.clear();
         this.sectionOcclusionGraph.addSectionsInFrustum(var1, this.visibleSections);
         this.minecraft.getProfiler().pop();
      }
   }

   public void addRecentlyCompiledSection(SectionRenderDispatcher.RenderSection var1) {
      this.sectionOcclusionGraph.onSectionCompiled(var1);
   }

   private void captureFrustum(Matrix4f var1, Matrix4f var2, double var3, double var5, double var7, Frustum var9) {
      this.capturedFrustum = var9;
      Matrix4f var10 = new Matrix4f(var2);
      var10.mul(var1);
      var10.invert();
      this.frustumPos.x = var3;
      this.frustumPos.y = var5;
      this.frustumPos.z = var7;
      this.frustumPoints[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
      this.frustumPoints[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
      this.frustumPoints[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
      this.frustumPoints[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
      this.frustumPoints[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
      this.frustumPoints[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
      this.frustumPoints[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.frustumPoints[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

      for(int var11 = 0; var11 < 8; ++var11) {
         var10.transform(this.frustumPoints[var11]);
         this.frustumPoints[var11].div(this.frustumPoints[var11].w());
      }

   }

   public void prepareCullFrustum(Vec3 var1, Matrix4f var2, Matrix4f var3) {
      this.cullingFrustum = new Frustum(var2, var3);
      this.cullingFrustum.prepare(var1.x(), var1.y(), var1.z());
   }

   public void renderLevel(DeltaTracker var1, boolean var2, Camera var3, GameRenderer var4, LightTexture var5, Matrix4f var6, Matrix4f var7) {
      TickRateManager var8 = this.minecraft.level.tickRateManager();
      float var9 = var1.getGameTimeDeltaPartialTick(false);
      RenderSystem.setShaderGameTime(this.level.getGameTime(), var9);
      this.blockEntityRenderDispatcher.prepare(this.level, var3, this.minecraft.hitResult);
      this.entityRenderDispatcher.prepare(this.level, var3, this.minecraft.crosshairPickEntity);
      ProfilerFiller var10 = this.level.getProfiler();
      var10.popPush("light_update_queue");
      this.level.pollLightUpdates();
      var10.popPush("light_updates");
      this.level.getChunkSource().getLightEngine().runLightUpdates();
      Vec3 var11 = var3.getPosition();
      double var12 = var11.x();
      double var14 = var11.y();
      double var16 = var11.z();
      var10.popPush("culling");
      boolean var18 = this.capturedFrustum != null;
      Frustum var19;
      if (var18) {
         var19 = this.capturedFrustum;
         var19.prepare(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z);
      } else {
         var19 = this.cullingFrustum;
      }

      this.minecraft.getProfiler().popPush("captureFrustum");
      if (this.captureFrustum) {
         this.captureFrustum(var6, var7, var11.x, var11.y, var11.z, var18 ? new Frustum(var6, var7) : var19);
         this.captureFrustum = false;
      }

      var10.popPush("clear");
      FogRenderer.setupColor(var3, var9, this.minecraft.level, this.minecraft.options.getEffectiveRenderDistance(), var4.getDarkenWorldAmount(var9));
      FogRenderer.levelFogColor();
      RenderSystem.clear(16640, Minecraft.ON_OSX);
      float var20 = var4.getRenderDistance();
      boolean var21 = this.minecraft.level.effects().isFoggyAt(Mth.floor(var12), Mth.floor(var14)) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
      var10.popPush("sky");
      RenderSystem.setShader(GameRenderer::getPositionShader);
      this.renderSky(var6, var7, var9, var3, var21, () -> {
         FogRenderer.setupFog(var3, FogRenderer.FogMode.FOG_SKY, var20, var21, var9);
      });
      var10.popPush("fog");
      FogRenderer.setupFog(var3, FogRenderer.FogMode.FOG_TERRAIN, Math.max(var20, 32.0F), var21, var9);
      var10.popPush("terrain_setup");
      this.setupRender(var3, var19, var18, this.minecraft.player.isSpectator());
      var10.popPush("compile_sections");
      this.compileSections(var3);
      var10.popPush("terrain");
      this.renderSectionLayer(RenderType.solid(), var12, var14, var16, var6, var7);
      this.renderSectionLayer(RenderType.cutoutMipped(), var12, var14, var16, var6, var7);
      this.renderSectionLayer(RenderType.cutout(), var12, var14, var16, var6, var7);
      if (this.level.effects().constantAmbientLight()) {
         Lighting.setupNetherLevel();
      } else {
         Lighting.setupLevel();
      }

      var10.popPush("entities");
      this.renderedEntities = 0;
      this.culledEntities = 0;
      if (this.itemEntityTarget != null) {
         this.itemEntityTarget.clear(Minecraft.ON_OSX);
         this.itemEntityTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
         this.minecraft.getMainRenderTarget().bindWrite(false);
      }

      if (this.weatherTarget != null) {
         this.weatherTarget.clear(Minecraft.ON_OSX);
      }

      if (this.shouldShowEntityOutlines()) {
         this.entityTarget.clear(Minecraft.ON_OSX);
         this.minecraft.getMainRenderTarget().bindWrite(false);
      }

      Matrix4fStack var22 = RenderSystem.getModelViewStack();
      var22.pushMatrix();
      var22.mul(var6);
      RenderSystem.applyModelViewMatrix();
      boolean var23 = false;
      PoseStack var24 = new PoseStack();
      MultiBufferSource.BufferSource var25 = this.renderBuffers.bufferSource();
      Iterator var26 = this.level.entitiesForRendering().iterator();

      while(true) {
         Entity var27;
         do {
            BlockPos var28;
            do {
               do {
                  do {
                     if (!var26.hasNext()) {
                        var25.endLastBatch();
                        this.checkPoseStack(var24);
                        var25.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
                        var25.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
                        var25.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
                        var25.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));
                        var10.popPush("blockentities");
                        ObjectListIterator var40 = this.visibleSections.iterator();

                        while(true) {
                           List var46;
                           do {
                              if (!var40.hasNext()) {
                                 synchronized(this.globalBlockEntities) {
                                    Iterator var44 = this.globalBlockEntities.iterator();

                                    while(true) {
                                       if (!var44.hasNext()) {
                                          break;
                                       }

                                       BlockEntity var48 = (BlockEntity)var44.next();
                                       BlockPos var51 = var48.getBlockPos();
                                       var24.pushPose();
                                       var24.translate((double)var51.getX() - var12, (double)var51.getY() - var14, (double)var51.getZ() - var16);
                                       this.blockEntityRenderDispatcher.render(var48, var9, var24, var25);
                                       var24.popPose();
                                    }
                                 }

                                 this.checkPoseStack(var24);
                                 var25.endBatch(RenderType.solid());
                                 var25.endBatch(RenderType.endPortal());
                                 var25.endBatch(RenderType.endGateway());
                                 var25.endBatch(Sheets.solidBlockSheet());
                                 var25.endBatch(Sheets.cutoutBlockSheet());
                                 var25.endBatch(Sheets.bedSheet());
                                 var25.endBatch(Sheets.shulkerBoxSheet());
                                 var25.endBatch(Sheets.signSheet());
                                 var25.endBatch(Sheets.hangingSignSheet());
                                 var25.endBatch(Sheets.chestSheet());
                                 this.renderBuffers.outlineBufferSource().endOutlineBatch();
                                 if (var23) {
                                    this.entityEffect.process(var1.getGameTimeDeltaTicks());
                                    this.minecraft.getMainRenderTarget().bindWrite(false);
                                 }

                                 var10.popPush("destroyProgress");
                                 ObjectIterator var41 = this.destructionProgress.long2ObjectEntrySet().iterator();

                                 while(var41.hasNext()) {
                                    Long2ObjectMap.Entry var45 = (Long2ObjectMap.Entry)var41.next();
                                    var28 = BlockPos.of(var45.getLongKey());
                                    double var54 = (double)var28.getX() - var12;
                                    double var57 = (double)var28.getY() - var14;
                                    double var58 = (double)var28.getZ() - var16;
                                    if (!(var54 * var54 + var57 * var57 + var58 * var58 > 1024.0)) {
                                       SortedSet var59 = (SortedSet)var45.getValue();
                                       if (var59 != null && !var59.isEmpty()) {
                                          int var60 = ((BlockDestructionProgress)var59.last()).getProgress();
                                          var24.pushPose();
                                          var24.translate((double)var28.getX() - var12, (double)var28.getY() - var14, (double)var28.getZ() - var16);
                                          PoseStack.Pose var37 = var24.last();
                                          SheetedDecalTextureGenerator var38 = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(var60)), var37, 1.0F);
                                          this.minecraft.getBlockRenderer().renderBreakingTexture(this.level.getBlockState(var28), var28, this.level, var24, var38);
                                          var24.popPose();
                                       }
                                    }
                                 }

                                 this.checkPoseStack(var24);
                                 HitResult var42 = this.minecraft.hitResult;
                                 if (var2 && var42 != null && var42.getType() == HitResult.Type.BLOCK) {
                                    var10.popPush("outline");
                                    BlockPos var47 = ((BlockHitResult)var42).getBlockPos();
                                    BlockState var49 = this.level.getBlockState(var47);
                                    if (!var49.isAir() && this.level.getWorldBorder().isWithinBounds(var47)) {
                                       VertexConsumer var55 = var25.getBuffer(RenderType.lines());
                                       this.renderHitOutline(var24, var55, var3.getEntity(), var12, var14, var16, var47, var49);
                                    }
                                 }

                                 this.minecraft.debugRenderer.render(var24, var25, var12, var14, var16);
                                 var25.endLastBatch();
                                 var25.endBatch(Sheets.translucentCullBlockSheet());
                                 var25.endBatch(Sheets.bannerSheet());
                                 var25.endBatch(Sheets.shieldSheet());
                                 var25.endBatch(RenderType.armorEntityGlint());
                                 var25.endBatch(RenderType.glint());
                                 var25.endBatch(RenderType.glintTranslucent());
                                 var25.endBatch(RenderType.entityGlint());
                                 var25.endBatch(RenderType.entityGlintDirect());
                                 var25.endBatch(RenderType.waterMask());
                                 this.renderBuffers.crumblingBufferSource().endBatch();
                                 if (this.transparencyChain != null) {
                                    var25.endBatch(RenderType.lines());
                                    var25.endBatch();
                                    this.translucentTarget.clear(Minecraft.ON_OSX);
                                    this.translucentTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
                                    var10.popPush("translucent");
                                    this.renderSectionLayer(RenderType.translucent(), var12, var14, var16, var6, var7);
                                    var10.popPush("string");
                                    this.renderSectionLayer(RenderType.tripwire(), var12, var14, var16, var6, var7);
                                    this.particlesTarget.clear(Minecraft.ON_OSX);
                                    this.particlesTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
                                    RenderStateShard.PARTICLES_TARGET.setupRenderState();
                                    var10.popPush("particles");
                                    this.minecraft.particleEngine.render(var5, var3, var9);
                                    RenderStateShard.PARTICLES_TARGET.clearRenderState();
                                 } else {
                                    var10.popPush("translucent");
                                    if (this.translucentTarget != null) {
                                       this.translucentTarget.clear(Minecraft.ON_OSX);
                                    }

                                    this.renderSectionLayer(RenderType.translucent(), var12, var14, var16, var6, var7);
                                    var25.endBatch(RenderType.lines());
                                    var25.endBatch();
                                    var10.popPush("string");
                                    this.renderSectionLayer(RenderType.tripwire(), var12, var14, var16, var6, var7);
                                    var10.popPush("particles");
                                    this.minecraft.particleEngine.render(var5, var3, var9);
                                 }

                                 if (this.minecraft.options.getCloudsType() != CloudStatus.OFF) {
                                    if (this.transparencyChain != null) {
                                       this.cloudsTarget.clear(Minecraft.ON_OSX);
                                    }

                                    var10.popPush("clouds");
                                    this.renderClouds(var24, var6, var7, var9, var12, var14, var16);
                                 }

                                 if (this.transparencyChain != null) {
                                    RenderStateShard.WEATHER_TARGET.setupRenderState();
                                    var10.popPush("weather");
                                    this.renderSnowAndRain(var5, var9, var12, var14, var16);
                                    this.renderWorldBorder(var3);
                                    RenderStateShard.WEATHER_TARGET.clearRenderState();
                                    this.transparencyChain.process(var1.getGameTimeDeltaTicks());
                                    this.minecraft.getMainRenderTarget().bindWrite(false);
                                 } else {
                                    RenderSystem.depthMask(false);
                                    var10.popPush("weather");
                                    this.renderSnowAndRain(var5, var9, var12, var14, var16);
                                    this.renderWorldBorder(var3);
                                    RenderSystem.depthMask(true);
                                 }

                                 this.renderDebug(var24, var25, var3);
                                 var25.endLastBatch();
                                 var22.popMatrix();
                                 RenderSystem.applyModelViewMatrix();
                                 RenderSystem.depthMask(true);
                                 RenderSystem.disableBlend();
                                 FogRenderer.setupNoFog();
                                 return;
                              }

                              SectionRenderDispatcher.RenderSection var43 = (SectionRenderDispatcher.RenderSection)var40.next();
                              var46 = var43.getCompiled().getRenderableBlockEntities();
                           } while(var46.isEmpty());

                           Iterator var50 = var46.iterator();

                           while(var50.hasNext()) {
                              BlockEntity var53 = (BlockEntity)var50.next();
                              BlockPos var56 = var53.getBlockPos();
                              Object var32 = var25;
                              var24.pushPose();
                              var24.translate((double)var56.getX() - var12, (double)var56.getY() - var14, (double)var56.getZ() - var16);
                              SortedSet var33 = (SortedSet)this.destructionProgress.get(var56.asLong());
                              if (var33 != null && !var33.isEmpty()) {
                                 int var34 = ((BlockDestructionProgress)var33.last()).getProgress();
                                 if (var34 >= 0) {
                                    PoseStack.Pose var35 = var24.last();
                                    SheetedDecalTextureGenerator var36 = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(var34)), var35, 1.0F);
                                    var32 = (var2x) -> {
                                       VertexConsumer var3 = var25.getBuffer(var2x);
                                       return var2x.affectsCrumbling() ? VertexMultiConsumer.create(var36, var3) : var3;
                                    };
                                 }
                              }

                              this.blockEntityRenderDispatcher.render(var53, var9, var24, (MultiBufferSource)var32);
                              var24.popPose();
                           }
                        }
                     }

                     var27 = (Entity)var26.next();
                  } while(!this.entityRenderDispatcher.shouldRender(var27, var19, var12, var14, var16) && !var27.hasIndirectPassenger(this.minecraft.player));

                  var28 = var27.blockPosition();
               } while(!this.level.isOutsideBuildHeight(var28.getY()) && !this.isSectionCompiled(var28));
            } while(var27 == var3.getEntity() && !var3.isDetached() && (!(var3.getEntity() instanceof LivingEntity) || !((LivingEntity)var3.getEntity()).isSleeping()));
         } while(var27 instanceof LocalPlayer && var3.getEntity() != var27);

         ++this.renderedEntities;
         if (var27.tickCount == 0) {
            var27.xOld = var27.getX();
            var27.yOld = var27.getY();
            var27.zOld = var27.getZ();
         }

         Object var29;
         if (this.shouldShowEntityOutlines() && this.minecraft.shouldEntityAppearGlowing(var27)) {
            var23 = true;
            OutlineBufferSource var30 = this.renderBuffers.outlineBufferSource();
            var29 = var30;
            int var31 = var27.getTeamColor();
            var30.setColor(FastColor.ARGB32.red(var31), FastColor.ARGB32.green(var31), FastColor.ARGB32.blue(var31), 255);
         } else {
            var29 = var25;
         }

         float var52 = var1.getGameTimeDeltaPartialTick(!var8.isEntityFrozen(var27));
         this.renderEntity(var27, var12, var14, var16, var52, var24, (MultiBufferSource)var29);
      }
   }

   private void checkPoseStack(PoseStack var1) {
      if (!var1.clear()) {
         throw new IllegalStateException("Pose stack not empty");
      }
   }

   private void renderEntity(Entity var1, double var2, double var4, double var6, float var8, PoseStack var9, MultiBufferSource var10) {
      double var11 = Mth.lerp((double)var8, var1.xOld, var1.getX());
      double var13 = Mth.lerp((double)var8, var1.yOld, var1.getY());
      double var15 = Mth.lerp((double)var8, var1.zOld, var1.getZ());
      float var17 = Mth.lerp(var8, var1.yRotO, var1.getYRot());
      this.entityRenderDispatcher.render(var1, var11 - var2, var13 - var4, var15 - var6, var17, var8, var9, var10, this.entityRenderDispatcher.getPackedLightCoords(var1, var8));
   }

   private void renderSectionLayer(RenderType var1, double var2, double var4, double var6, Matrix4f var8, Matrix4f var9) {
      RenderSystem.assertOnRenderThread();
      var1.setupRenderState();
      if (var1 == RenderType.translucent()) {
         this.minecraft.getProfiler().push("translucent_sort");
         double var10 = var2 - this.xTransparentOld;
         double var12 = var4 - this.yTransparentOld;
         double var14 = var6 - this.zTransparentOld;
         if (var10 * var10 + var12 * var12 + var14 * var14 > 1.0) {
            int var16 = SectionPos.posToSectionCoord(var2);
            int var17 = SectionPos.posToSectionCoord(var4);
            int var18 = SectionPos.posToSectionCoord(var6);
            boolean var19 = var16 != SectionPos.posToSectionCoord(this.xTransparentOld) || var18 != SectionPos.posToSectionCoord(this.zTransparentOld) || var17 != SectionPos.posToSectionCoord(this.yTransparentOld);
            this.xTransparentOld = var2;
            this.yTransparentOld = var4;
            this.zTransparentOld = var6;
            int var20 = 0;
            ObjectListIterator var21 = this.visibleSections.iterator();

            label76:
            while(true) {
               SectionRenderDispatcher.RenderSection var22;
               do {
                  do {
                     if (!var21.hasNext()) {
                        break label76;
                     }

                     var22 = (SectionRenderDispatcher.RenderSection)var21.next();
                  } while(var20 >= 15);
               } while(!var19 && !var22.isAxisAlignedWith(var16, var17, var18));

               if (var22.resortTransparency(var1, this.sectionRenderDispatcher)) {
                  ++var20;
               }
            }
         }

         this.minecraft.getProfiler().pop();
      }

      this.minecraft.getProfiler().push("filterempty");
      this.minecraft.getProfiler().popPush(() -> {
         return "render_" + String.valueOf(var1);
      });
      boolean var23 = var1 != RenderType.translucent();
      ObjectListIterator var11 = this.visibleSections.listIterator(var23 ? 0 : this.visibleSections.size());
      ShaderInstance var24 = RenderSystem.getShader();
      var24.setDefaultUniforms(VertexFormat.Mode.QUADS, var8, var9, this.minecraft.getWindow());
      var24.apply();
      Uniform var13 = var24.CHUNK_OFFSET;

      while(true) {
         if (var23) {
            if (!var11.hasNext()) {
               break;
            }
         } else if (!var11.hasPrevious()) {
            break;
         }

         SectionRenderDispatcher.RenderSection var25 = var23 ? (SectionRenderDispatcher.RenderSection)var11.next() : (SectionRenderDispatcher.RenderSection)var11.previous();
         if (!var25.getCompiled().isEmpty(var1)) {
            VertexBuffer var15 = var25.getBuffer(var1);
            BlockPos var26 = var25.getOrigin();
            if (var13 != null) {
               var13.set((float)((double)var26.getX() - var2), (float)((double)var26.getY() - var4), (float)((double)var26.getZ() - var6));
               var13.upload();
            }

            var15.bind();
            var15.draw();
         }
      }

      if (var13 != null) {
         var13.set(0.0F, 0.0F, 0.0F);
      }

      var24.clear();
      VertexBuffer.unbind();
      this.minecraft.getProfiler().pop();
      var1.clearRenderState();
   }

   private void renderDebug(PoseStack var1, MultiBufferSource var2, Camera var3) {
      if (this.minecraft.sectionPath || this.minecraft.sectionVisibility) {
         double var4 = var3.getPosition().x();
         double var6 = var3.getPosition().y();
         double var8 = var3.getPosition().z();
         ObjectListIterator var10 = this.visibleSections.iterator();

         label75:
         while(true) {
            SectionRenderDispatcher.RenderSection var11;
            SectionOcclusionGraph.Node var12;
            do {
               if (!var10.hasNext()) {
                  break label75;
               }

               var11 = (SectionRenderDispatcher.RenderSection)var10.next();
               var12 = this.sectionOcclusionGraph.getNode(var11);
            } while(var12 == null);

            BlockPos var13 = var11.getOrigin();
            var1.pushPose();
            var1.translate((double)var13.getX() - var4, (double)var13.getY() - var6, (double)var13.getZ() - var8);
            Matrix4f var14 = var1.last().pose();
            VertexConsumer var15;
            int var16;
            int var18;
            int var19;
            if (this.minecraft.sectionPath) {
               var15 = var2.getBuffer(RenderType.lines());
               var16 = var12.step == 0 ? 0 : Mth.hsvToRgb((float)var12.step / 50.0F, 0.9F, 0.9F);
               int var17 = var16 >> 16 & 255;
               var18 = var16 >> 8 & 255;
               var19 = var16 & 255;

               for(int var20 = 0; var20 < DIRECTIONS.length; ++var20) {
                  if (var12.hasSourceDirection(var20)) {
                     Direction var21 = DIRECTIONS[var20];
                     var15.addVertex(var14, 8.0F, 8.0F, 8.0F).setColor(var17, var18, var19, 255).setNormal((float)var21.getStepX(), (float)var21.getStepY(), (float)var21.getStepZ());
                     var15.addVertex(var14, (float)(8 - 16 * var21.getStepX()), (float)(8 - 16 * var21.getStepY()), (float)(8 - 16 * var21.getStepZ())).setColor(var17, var18, var19, 255).setNormal((float)var21.getStepX(), (float)var21.getStepY(), (float)var21.getStepZ());
                  }
               }
            }

            if (this.minecraft.sectionVisibility && !var11.getCompiled().hasNoRenderableLayers()) {
               var15 = var2.getBuffer(RenderType.lines());
               var16 = 0;
               Direction[] var28 = DIRECTIONS;
               var18 = var28.length;
               var19 = 0;

               while(true) {
                  if (var19 >= var18) {
                     if (var16 > 0) {
                        VertexConsumer var29 = var2.getBuffer(RenderType.debugQuads());
                        float var30 = 0.5F;
                        float var31 = 0.2F;
                        var29.addVertex(var14, 0.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 15.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 15.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 0.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 0.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 15.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 15.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 0.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 0.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 0.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 0.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 0.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 15.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 15.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 15.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 15.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 0.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 15.5F, 0.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 15.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 0.5F, 15.5F, 0.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 0.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 15.5F, 15.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 15.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                        var29.addVertex(var14, 0.5F, 0.5F, 15.5F).setColor(0.9F, 0.9F, 0.0F, 0.2F);
                     }
                     break;
                  }

                  Direction var32 = var28[var19];
                  Direction[] var33 = DIRECTIONS;
                  int var22 = var33.length;

                  for(int var23 = 0; var23 < var22; ++var23) {
                     Direction var24 = var33[var23];
                     boolean var25 = var11.getCompiled().facesCanSeeEachother(var32, var24);
                     if (!var25) {
                        ++var16;
                        var15.addVertex(var14, (float)(8 + 8 * var32.getStepX()), (float)(8 + 8 * var32.getStepY()), (float)(8 + 8 * var32.getStepZ())).setColor(255, 0, 0, 255).setNormal((float)var32.getStepX(), (float)var32.getStepY(), (float)var32.getStepZ());
                        var15.addVertex(var14, (float)(8 + 8 * var24.getStepX()), (float)(8 + 8 * var24.getStepY()), (float)(8 + 8 * var24.getStepZ())).setColor(255, 0, 0, 255).setNormal((float)var24.getStepX(), (float)var24.getStepY(), (float)var24.getStepZ());
                     }
                  }

                  ++var19;
               }
            }

            var1.popPose();
         }
      }

      if (this.capturedFrustum != null) {
         var1.pushPose();
         var1.translate((float)(this.frustumPos.x - var3.getPosition().x), (float)(this.frustumPos.y - var3.getPosition().y), (float)(this.frustumPos.z - var3.getPosition().z));
         Matrix4f var26 = var1.last().pose();
         VertexConsumer var5 = var2.getBuffer(RenderType.debugQuads());
         this.addFrustumQuad(var5, var26, 0, 1, 2, 3, 0, 1, 1);
         this.addFrustumQuad(var5, var26, 4, 5, 6, 7, 1, 0, 0);
         this.addFrustumQuad(var5, var26, 0, 1, 5, 4, 1, 1, 0);
         this.addFrustumQuad(var5, var26, 2, 3, 7, 6, 0, 0, 1);
         this.addFrustumQuad(var5, var26, 0, 4, 7, 3, 0, 1, 0);
         this.addFrustumQuad(var5, var26, 1, 5, 6, 2, 1, 0, 1);
         VertexConsumer var27 = var2.getBuffer(RenderType.lines());
         this.addFrustumVertex(var27, var26, 0);
         this.addFrustumVertex(var27, var26, 1);
         this.addFrustumVertex(var27, var26, 1);
         this.addFrustumVertex(var27, var26, 2);
         this.addFrustumVertex(var27, var26, 2);
         this.addFrustumVertex(var27, var26, 3);
         this.addFrustumVertex(var27, var26, 3);
         this.addFrustumVertex(var27, var26, 0);
         this.addFrustumVertex(var27, var26, 4);
         this.addFrustumVertex(var27, var26, 5);
         this.addFrustumVertex(var27, var26, 5);
         this.addFrustumVertex(var27, var26, 6);
         this.addFrustumVertex(var27, var26, 6);
         this.addFrustumVertex(var27, var26, 7);
         this.addFrustumVertex(var27, var26, 7);
         this.addFrustumVertex(var27, var26, 4);
         this.addFrustumVertex(var27, var26, 0);
         this.addFrustumVertex(var27, var26, 4);
         this.addFrustumVertex(var27, var26, 1);
         this.addFrustumVertex(var27, var26, 5);
         this.addFrustumVertex(var27, var26, 2);
         this.addFrustumVertex(var27, var26, 6);
         this.addFrustumVertex(var27, var26, 3);
         this.addFrustumVertex(var27, var26, 7);
         var1.popPose();
      }

   }

   private void addFrustumVertex(VertexConsumer var1, Matrix4f var2, int var3) {
      var1.addVertex(var2, this.frustumPoints[var3].x(), this.frustumPoints[var3].y(), this.frustumPoints[var3].z()).setColor(-16777216).setNormal(0.0F, 0.0F, -1.0F);
   }

   private void addFrustumQuad(VertexConsumer var1, Matrix4f var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      float var10 = 0.25F;
      var1.addVertex(var2, this.frustumPoints[var3].x(), this.frustumPoints[var3].y(), this.frustumPoints[var3].z()).setColor((float)var7, (float)var8, (float)var9, 0.25F);
      var1.addVertex(var2, this.frustumPoints[var4].x(), this.frustumPoints[var4].y(), this.frustumPoints[var4].z()).setColor((float)var7, (float)var8, (float)var9, 0.25F);
      var1.addVertex(var2, this.frustumPoints[var5].x(), this.frustumPoints[var5].y(), this.frustumPoints[var5].z()).setColor((float)var7, (float)var8, (float)var9, 0.25F);
      var1.addVertex(var2, this.frustumPoints[var6].x(), this.frustumPoints[var6].y(), this.frustumPoints[var6].z()).setColor((float)var7, (float)var8, (float)var9, 0.25F);
   }

   public void captureFrustum() {
      this.captureFrustum = true;
   }

   public void killFrustum() {
      this.capturedFrustum = null;
   }

   public void tick() {
      if (this.level.tickRateManager().runsNormally()) {
         ++this.ticks;
      }

      if (this.ticks % 20 == 0) {
         ObjectIterator var1 = this.destroyingBlocks.values().iterator();

         while(var1.hasNext()) {
            BlockDestructionProgress var2 = (BlockDestructionProgress)var1.next();
            int var3 = var2.getUpdatedRenderTick();
            if (this.ticks - var3 > 400) {
               var1.remove();
               this.removeProgress(var2);
            }
         }

      }
   }

   private void removeProgress(BlockDestructionProgress var1) {
      long var2 = var1.getPos().asLong();
      Set var4 = (Set)this.destructionProgress.get(var2);
      var4.remove(var1);
      if (var4.isEmpty()) {
         this.destructionProgress.remove(var2);
      }

   }

   private void renderEndSky(PoseStack var1) {
      RenderSystem.enableBlend();
      RenderSystem.depthMask(false);
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.setShaderTexture(0, END_SKY_LOCATION);
      Tesselator var2 = Tesselator.getInstance();

      for(int var3 = 0; var3 < 6; ++var3) {
         var1.pushPose();
         if (var3 == 1) {
            var1.mulPose(Axis.XP.rotationDegrees(90.0F));
         }

         if (var3 == 2) {
            var1.mulPose(Axis.XP.rotationDegrees(-90.0F));
         }

         if (var3 == 3) {
            var1.mulPose(Axis.XP.rotationDegrees(180.0F));
         }

         if (var3 == 4) {
            var1.mulPose(Axis.ZP.rotationDegrees(90.0F));
         }

         if (var3 == 5) {
            var1.mulPose(Axis.ZP.rotationDegrees(-90.0F));
         }

         Matrix4f var4 = var1.last().pose();
         BufferBuilder var5 = var2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         var5.addVertex(var4, -100.0F, -100.0F, -100.0F).setUv(0.0F, 0.0F).setColor(-14145496);
         var5.addVertex(var4, -100.0F, -100.0F, 100.0F).setUv(0.0F, 16.0F).setColor(-14145496);
         var5.addVertex(var4, 100.0F, -100.0F, 100.0F).setUv(16.0F, 16.0F).setColor(-14145496);
         var5.addVertex(var4, 100.0F, -100.0F, -100.0F).setUv(16.0F, 0.0F).setColor(-14145496);
         BufferUploader.drawWithShader(var5.buildOrThrow());
         var1.popPose();
      }

      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
   }

   public void renderSky(Matrix4f var1, Matrix4f var2, float var3, Camera var4, boolean var5, Runnable var6) {
      var6.run();
      if (!var5) {
         FogType var7 = var4.getFluidInCamera();
         if (var7 != FogType.POWDER_SNOW && var7 != FogType.LAVA && !this.doesMobEffectBlockSky(var4)) {
            PoseStack var8 = new PoseStack();
            var8.mulPose(var1);
            if (this.minecraft.level.effects().skyType() == DimensionSpecialEffects.SkyType.END) {
               this.renderEndSky(var8);
            } else if (this.minecraft.level.effects().skyType() == DimensionSpecialEffects.SkyType.NORMAL) {
               Vec3 var9 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), var3);
               float var10 = (float)var9.x;
               float var11 = (float)var9.y;
               float var12 = (float)var9.z;
               FogRenderer.levelFogColor();
               Tesselator var13 = Tesselator.getInstance();
               RenderSystem.depthMask(false);
               RenderSystem.setShaderColor(var10, var11, var12, 1.0F);
               ShaderInstance var14 = RenderSystem.getShader();
               this.skyBuffer.bind();
               this.skyBuffer.drawWithShader(var8.last().pose(), var2, var14);
               VertexBuffer.unbind();
               RenderSystem.enableBlend();
               float[] var15 = this.level.effects().getSunriseColor(this.level.getTimeOfDay(var3), var3);
               float var16;
               float var18;
               float var24;
               float var25;
               float var26;
               if (var15 != null) {
                  RenderSystem.setShader(GameRenderer::getPositionColorShader);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  var8.pushPose();
                  var8.mulPose(Axis.XP.rotationDegrees(90.0F));
                  var16 = Mth.sin(this.level.getSunAngle(var3)) < 0.0F ? 180.0F : 0.0F;
                  var8.mulPose(Axis.ZP.rotationDegrees(var16));
                  var8.mulPose(Axis.ZP.rotationDegrees(90.0F));
                  float var17 = var15[0];
                  var18 = var15[1];
                  float var19 = var15[2];
                  Matrix4f var20 = var8.last().pose();
                  BufferBuilder var21 = var13.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
                  var21.addVertex(var20, 0.0F, 100.0F, 0.0F).setColor(var17, var18, var19, var15[3]);
                  boolean var22 = true;

                  for(int var23 = 0; var23 <= 16; ++var23) {
                     var24 = (float)var23 * 6.2831855F / 16.0F;
                     var25 = Mth.sin(var24);
                     var26 = Mth.cos(var24);
                     var21.addVertex(var20, var25 * 120.0F, var26 * 120.0F, -var26 * 40.0F * var15[3]).setColor(var15[0], var15[1], var15[2], 0.0F);
                  }

                  BufferUploader.drawWithShader(var21.buildOrThrow());
                  var8.popPose();
               }

               RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
               var8.pushPose();
               var16 = 1.0F - this.level.getRainLevel(var3);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, var16);
               var8.mulPose(Axis.YP.rotationDegrees(-90.0F));
               var8.mulPose(Axis.XP.rotationDegrees(this.level.getTimeOfDay(var3) * 360.0F));
               Matrix4f var29 = var8.last().pose();
               var18 = 30.0F;
               RenderSystem.setShader(GameRenderer::getPositionTexShader);
               RenderSystem.setShaderTexture(0, SUN_LOCATION);
               BufferBuilder var30 = var13.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
               var30.addVertex(var29, -var18, 100.0F, -var18).setUv(0.0F, 0.0F);
               var30.addVertex(var29, var18, 100.0F, -var18).setUv(1.0F, 0.0F);
               var30.addVertex(var29, var18, 100.0F, var18).setUv(1.0F, 1.0F);
               var30.addVertex(var29, -var18, 100.0F, var18).setUv(0.0F, 1.0F);
               BufferUploader.drawWithShader(var30.buildOrThrow());
               var18 = 20.0F;
               RenderSystem.setShaderTexture(0, MOON_LOCATION);
               int var31 = this.level.getMoonPhase();
               int var32 = var31 % 4;
               int var33 = var31 / 4 % 2;
               float var34 = (float)(var32 + 0) / 4.0F;
               var24 = (float)(var33 + 0) / 2.0F;
               var25 = (float)(var32 + 1) / 4.0F;
               var26 = (float)(var33 + 1) / 2.0F;
               var30 = var13.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
               var30.addVertex(var29, -var18, -100.0F, var18).setUv(var25, var26);
               var30.addVertex(var29, var18, -100.0F, var18).setUv(var34, var26);
               var30.addVertex(var29, var18, -100.0F, -var18).setUv(var34, var24);
               var30.addVertex(var29, -var18, -100.0F, -var18).setUv(var25, var24);
               BufferUploader.drawWithShader(var30.buildOrThrow());
               float var27 = this.level.getStarBrightness(var3) * var16;
               if (var27 > 0.0F) {
                  RenderSystem.setShaderColor(var27, var27, var27, var27);
                  FogRenderer.setupNoFog();
                  this.starBuffer.bind();
                  this.starBuffer.drawWithShader(var8.last().pose(), var2, GameRenderer.getPositionShader());
                  VertexBuffer.unbind();
                  var6.run();
               }

               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.disableBlend();
               RenderSystem.defaultBlendFunc();
               var8.popPose();
               RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
               double var28 = this.minecraft.player.getEyePosition(var3).y - this.level.getLevelData().getHorizonHeight(this.level);
               if (var28 < 0.0) {
                  var8.pushPose();
                  var8.translate(0.0F, 12.0F, 0.0F);
                  this.darkBuffer.bind();
                  this.darkBuffer.drawWithShader(var8.last().pose(), var2, var14);
                  VertexBuffer.unbind();
                  var8.popPose();
               }

               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.depthMask(true);
            }
         }
      }
   }

   private boolean doesMobEffectBlockSky(Camera var1) {
      Entity var3 = var1.getEntity();
      if (!(var3 instanceof LivingEntity var2)) {
         return false;
      } else {
         return var2.hasEffect(MobEffects.BLINDNESS) || var2.hasEffect(MobEffects.DARKNESS);
      }
   }

   public void renderClouds(PoseStack var1, Matrix4f var2, Matrix4f var3, float var4, double var5, double var7, double var9) {
      float var11 = this.level.effects().getCloudHeight();
      if (!Float.isNaN(var11)) {
         float var12 = 12.0F;
         float var13 = 4.0F;
         double var14 = 2.0E-4;
         double var16 = (double)(((float)this.ticks + var4) * 0.03F);
         double var18 = (var5 + var16) / 12.0;
         double var20 = (double)(var11 - (float)var7 + 0.33F);
         double var22 = var9 / 12.0 + 0.33000001311302185;
         var18 -= (double)(Mth.floor(var18 / 2048.0) * 2048);
         var22 -= (double)(Mth.floor(var22 / 2048.0) * 2048);
         float var24 = (float)(var18 - (double)Mth.floor(var18));
         float var25 = (float)(var20 / 4.0 - (double)Mth.floor(var20 / 4.0)) * 4.0F;
         float var26 = (float)(var22 - (double)Mth.floor(var22));
         Vec3 var27 = this.level.getCloudColor(var4);
         int var28 = (int)Math.floor(var18);
         int var29 = (int)Math.floor(var20 / 4.0);
         int var30 = (int)Math.floor(var22);
         if (var28 != this.prevCloudX || var29 != this.prevCloudY || var30 != this.prevCloudZ || this.minecraft.options.getCloudsType() != this.prevCloudsType || this.prevCloudColor.distanceToSqr(var27) > 2.0E-4) {
            this.prevCloudX = var28;
            this.prevCloudY = var29;
            this.prevCloudZ = var30;
            this.prevCloudColor = var27;
            this.prevCloudsType = this.minecraft.options.getCloudsType();
            this.generateClouds = true;
         }

         if (this.generateClouds) {
            this.generateClouds = false;
            if (this.cloudBuffer != null) {
               this.cloudBuffer.close();
            }

            this.cloudBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            this.cloudBuffer.bind();
            this.cloudBuffer.upload(this.buildClouds(Tesselator.getInstance(), var18, var20, var22, var27));
            VertexBuffer.unbind();
         }

         FogRenderer.levelFogColor();
         var1.pushPose();
         var1.mulPose(var2);
         var1.scale(12.0F, 1.0F, 12.0F);
         var1.translate(-var24, var25, -var26);
         if (this.cloudBuffer != null) {
            this.cloudBuffer.bind();
            int var31 = this.prevCloudsType == CloudStatus.FANCY ? 0 : 1;

            for(int var32 = var31; var32 < 2; ++var32) {
               RenderType var33 = var32 == 0 ? RenderType.cloudsDepthOnly() : RenderType.clouds();
               var33.setupRenderState();
               ShaderInstance var34 = RenderSystem.getShader();
               this.cloudBuffer.drawWithShader(var1.last().pose(), var3, var34);
               var33.clearRenderState();
            }

            VertexBuffer.unbind();
         }

         var1.popPose();
      }
   }

   private MeshData buildClouds(Tesselator var1, double var2, double var4, double var6, Vec3 var8) {
      float var9 = 4.0F;
      float var10 = 0.00390625F;
      boolean var11 = true;
      boolean var12 = true;
      float var13 = 9.765625E-4F;
      float var14 = (float)Mth.floor(var2) * 0.00390625F;
      float var15 = (float)Mth.floor(var6) * 0.00390625F;
      float var16 = (float)var8.x;
      float var17 = (float)var8.y;
      float var18 = (float)var8.z;
      float var19 = var16 * 0.9F;
      float var20 = var17 * 0.9F;
      float var21 = var18 * 0.9F;
      float var22 = var16 * 0.7F;
      float var23 = var17 * 0.7F;
      float var24 = var18 * 0.7F;
      float var25 = var16 * 0.8F;
      float var26 = var17 * 0.8F;
      float var27 = var18 * 0.8F;
      BufferBuilder var28 = var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
      float var29 = (float)Math.floor(var4 / 4.0) * 4.0F;
      if (this.prevCloudsType == CloudStatus.FANCY) {
         for(int var30 = -3; var30 <= 4; ++var30) {
            for(int var31 = -3; var31 <= 4; ++var31) {
               float var32 = (float)(var30 * 8);
               float var33 = (float)(var31 * 8);
               if (var29 > -5.0F) {
                  var28.addVertex(var32 + 0.0F, var29 + 0.0F, var33 + 8.0F).setUv((var32 + 0.0F) * 0.00390625F + var14, (var33 + 8.0F) * 0.00390625F + var15).setColor(var22, var23, var24, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
                  var28.addVertex(var32 + 8.0F, var29 + 0.0F, var33 + 8.0F).setUv((var32 + 8.0F) * 0.00390625F + var14, (var33 + 8.0F) * 0.00390625F + var15).setColor(var22, var23, var24, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
                  var28.addVertex(var32 + 8.0F, var29 + 0.0F, var33 + 0.0F).setUv((var32 + 8.0F) * 0.00390625F + var14, (var33 + 0.0F) * 0.00390625F + var15).setColor(var22, var23, var24, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
                  var28.addVertex(var32 + 0.0F, var29 + 0.0F, var33 + 0.0F).setUv((var32 + 0.0F) * 0.00390625F + var14, (var33 + 0.0F) * 0.00390625F + var15).setColor(var22, var23, var24, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
               }

               if (var29 <= 5.0F) {
                  var28.addVertex(var32 + 0.0F, var29 + 4.0F - 9.765625E-4F, var33 + 8.0F).setUv((var32 + 0.0F) * 0.00390625F + var14, (var33 + 8.0F) * 0.00390625F + var15).setColor(var16, var17, var18, 0.8F).setNormal(0.0F, 1.0F, 0.0F);
                  var28.addVertex(var32 + 8.0F, var29 + 4.0F - 9.765625E-4F, var33 + 8.0F).setUv((var32 + 8.0F) * 0.00390625F + var14, (var33 + 8.0F) * 0.00390625F + var15).setColor(var16, var17, var18, 0.8F).setNormal(0.0F, 1.0F, 0.0F);
                  var28.addVertex(var32 + 8.0F, var29 + 4.0F - 9.765625E-4F, var33 + 0.0F).setUv((var32 + 8.0F) * 0.00390625F + var14, (var33 + 0.0F) * 0.00390625F + var15).setColor(var16, var17, var18, 0.8F).setNormal(0.0F, 1.0F, 0.0F);
                  var28.addVertex(var32 + 0.0F, var29 + 4.0F - 9.765625E-4F, var33 + 0.0F).setUv((var32 + 0.0F) * 0.00390625F + var14, (var33 + 0.0F) * 0.00390625F + var15).setColor(var16, var17, var18, 0.8F).setNormal(0.0F, 1.0F, 0.0F);
               }

               int var34;
               if (var30 > -1) {
                  for(var34 = 0; var34 < 8; ++var34) {
                     var28.addVertex(var32 + (float)var34 + 0.0F, var29 + 0.0F, var33 + 8.0F).setUv((var32 + (float)var34 + 0.5F) * 0.00390625F + var14, (var33 + 8.0F) * 0.00390625F + var15).setColor(var19, var20, var21, 0.8F).setNormal(-1.0F, 0.0F, 0.0F);
                     var28.addVertex(var32 + (float)var34 + 0.0F, var29 + 4.0F, var33 + 8.0F).setUv((var32 + (float)var34 + 0.5F) * 0.00390625F + var14, (var33 + 8.0F) * 0.00390625F + var15).setColor(var19, var20, var21, 0.8F).setNormal(-1.0F, 0.0F, 0.0F);
                     var28.addVertex(var32 + (float)var34 + 0.0F, var29 + 4.0F, var33 + 0.0F).setUv((var32 + (float)var34 + 0.5F) * 0.00390625F + var14, (var33 + 0.0F) * 0.00390625F + var15).setColor(var19, var20, var21, 0.8F).setNormal(-1.0F, 0.0F, 0.0F);
                     var28.addVertex(var32 + (float)var34 + 0.0F, var29 + 0.0F, var33 + 0.0F).setUv((var32 + (float)var34 + 0.5F) * 0.00390625F + var14, (var33 + 0.0F) * 0.00390625F + var15).setColor(var19, var20, var21, 0.8F).setNormal(-1.0F, 0.0F, 0.0F);
                  }
               }

               if (var30 <= 1) {
                  for(var34 = 0; var34 < 8; ++var34) {
                     var28.addVertex(var32 + (float)var34 + 1.0F - 9.765625E-4F, var29 + 0.0F, var33 + 8.0F).setUv((var32 + (float)var34 + 0.5F) * 0.00390625F + var14, (var33 + 8.0F) * 0.00390625F + var15).setColor(var19, var20, var21, 0.8F).setNormal(1.0F, 0.0F, 0.0F);
                     var28.addVertex(var32 + (float)var34 + 1.0F - 9.765625E-4F, var29 + 4.0F, var33 + 8.0F).setUv((var32 + (float)var34 + 0.5F) * 0.00390625F + var14, (var33 + 8.0F) * 0.00390625F + var15).setColor(var19, var20, var21, 0.8F).setNormal(1.0F, 0.0F, 0.0F);
                     var28.addVertex(var32 + (float)var34 + 1.0F - 9.765625E-4F, var29 + 4.0F, var33 + 0.0F).setUv((var32 + (float)var34 + 0.5F) * 0.00390625F + var14, (var33 + 0.0F) * 0.00390625F + var15).setColor(var19, var20, var21, 0.8F).setNormal(1.0F, 0.0F, 0.0F);
                     var28.addVertex(var32 + (float)var34 + 1.0F - 9.765625E-4F, var29 + 0.0F, var33 + 0.0F).setUv((var32 + (float)var34 + 0.5F) * 0.00390625F + var14, (var33 + 0.0F) * 0.00390625F + var15).setColor(var19, var20, var21, 0.8F).setNormal(1.0F, 0.0F, 0.0F);
                  }
               }

               if (var31 > -1) {
                  for(var34 = 0; var34 < 8; ++var34) {
                     var28.addVertex(var32 + 0.0F, var29 + 4.0F, var33 + (float)var34 + 0.0F).setUv((var32 + 0.0F) * 0.00390625F + var14, (var33 + (float)var34 + 0.5F) * 0.00390625F + var15).setColor(var25, var26, var27, 0.8F).setNormal(0.0F, 0.0F, -1.0F);
                     var28.addVertex(var32 + 8.0F, var29 + 4.0F, var33 + (float)var34 + 0.0F).setUv((var32 + 8.0F) * 0.00390625F + var14, (var33 + (float)var34 + 0.5F) * 0.00390625F + var15).setColor(var25, var26, var27, 0.8F).setNormal(0.0F, 0.0F, -1.0F);
                     var28.addVertex(var32 + 8.0F, var29 + 0.0F, var33 + (float)var34 + 0.0F).setUv((var32 + 8.0F) * 0.00390625F + var14, (var33 + (float)var34 + 0.5F) * 0.00390625F + var15).setColor(var25, var26, var27, 0.8F).setNormal(0.0F, 0.0F, -1.0F);
                     var28.addVertex(var32 + 0.0F, var29 + 0.0F, var33 + (float)var34 + 0.0F).setUv((var32 + 0.0F) * 0.00390625F + var14, (var33 + (float)var34 + 0.5F) * 0.00390625F + var15).setColor(var25, var26, var27, 0.8F).setNormal(0.0F, 0.0F, -1.0F);
                  }
               }

               if (var31 <= 1) {
                  for(var34 = 0; var34 < 8; ++var34) {
                     var28.addVertex(var32 + 0.0F, var29 + 4.0F, var33 + (float)var34 + 1.0F - 9.765625E-4F).setUv((var32 + 0.0F) * 0.00390625F + var14, (var33 + (float)var34 + 0.5F) * 0.00390625F + var15).setColor(var25, var26, var27, 0.8F).setNormal(0.0F, 0.0F, 1.0F);
                     var28.addVertex(var32 + 8.0F, var29 + 4.0F, var33 + (float)var34 + 1.0F - 9.765625E-4F).setUv((var32 + 8.0F) * 0.00390625F + var14, (var33 + (float)var34 + 0.5F) * 0.00390625F + var15).setColor(var25, var26, var27, 0.8F).setNormal(0.0F, 0.0F, 1.0F);
                     var28.addVertex(var32 + 8.0F, var29 + 0.0F, var33 + (float)var34 + 1.0F - 9.765625E-4F).setUv((var32 + 8.0F) * 0.00390625F + var14, (var33 + (float)var34 + 0.5F) * 0.00390625F + var15).setColor(var25, var26, var27, 0.8F).setNormal(0.0F, 0.0F, 1.0F);
                     var28.addVertex(var32 + 0.0F, var29 + 0.0F, var33 + (float)var34 + 1.0F - 9.765625E-4F).setUv((var32 + 0.0F) * 0.00390625F + var14, (var33 + (float)var34 + 0.5F) * 0.00390625F + var15).setColor(var25, var26, var27, 0.8F).setNormal(0.0F, 0.0F, 1.0F);
                  }
               }
            }
         }
      } else {
         boolean var35 = true;
         boolean var36 = true;

         for(int var37 = -32; var37 < 32; var37 += 32) {
            for(int var38 = -32; var38 < 32; var38 += 32) {
               var28.addVertex((float)(var37 + 0), var29, (float)(var38 + 32)).setUv((float)(var37 + 0) * 0.00390625F + var14, (float)(var38 + 32) * 0.00390625F + var15).setColor(var16, var17, var18, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
               var28.addVertex((float)(var37 + 32), var29, (float)(var38 + 32)).setUv((float)(var37 + 32) * 0.00390625F + var14, (float)(var38 + 32) * 0.00390625F + var15).setColor(var16, var17, var18, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
               var28.addVertex((float)(var37 + 32), var29, (float)(var38 + 0)).setUv((float)(var37 + 32) * 0.00390625F + var14, (float)(var38 + 0) * 0.00390625F + var15).setColor(var16, var17, var18, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
               var28.addVertex((float)(var37 + 0), var29, (float)(var38 + 0)).setUv((float)(var37 + 0) * 0.00390625F + var14, (float)(var38 + 0) * 0.00390625F + var15).setColor(var16, var17, var18, 0.8F).setNormal(0.0F, -1.0F, 0.0F);
            }
         }
      }

      return var28.buildOrThrow();
   }

   private void compileSections(Camera var1) {
      this.minecraft.getProfiler().push("populate_sections_to_compile");
      LevelLightEngine var2 = this.level.getLightEngine();
      RenderRegionCache var3 = new RenderRegionCache();
      BlockPos var4 = var1.getBlockPosition();
      ArrayList var5 = Lists.newArrayList();
      ObjectListIterator var6 = this.visibleSections.iterator();

      while(true) {
         SectionRenderDispatcher.RenderSection var7;
         SectionPos var8;
         do {
            do {
               if (!var6.hasNext()) {
                  this.minecraft.getProfiler().popPush("upload");
                  this.sectionRenderDispatcher.uploadAllPendingUploads();
                  this.minecraft.getProfiler().popPush("schedule_async_compile");
                  Iterator var11 = var5.iterator();

                  while(var11.hasNext()) {
                     var7 = (SectionRenderDispatcher.RenderSection)var11.next();
                     var7.rebuildSectionAsync(this.sectionRenderDispatcher, var3);
                     var7.setNotDirty();
                  }

                  this.minecraft.getProfiler().pop();
                  return;
               }

               var7 = (SectionRenderDispatcher.RenderSection)var6.next();
               var8 = SectionPos.of(var7.getOrigin());
            } while(!var7.isDirty());
         } while(!var2.lightOnInSection(var8));

         boolean var9 = false;
         if (this.minecraft.options.prioritizeChunkUpdates().get() != PrioritizeChunkUpdates.NEARBY) {
            if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.PLAYER_AFFECTED) {
               var9 = var7.isDirtyFromPlayer();
            }
         } else {
            BlockPos var10 = var7.getOrigin().offset(8, 8, 8);
            var9 = var10.distSqr(var4) < 768.0 || var7.isDirtyFromPlayer();
         }

         if (var9) {
            this.minecraft.getProfiler().push("build_near_sync");
            this.sectionRenderDispatcher.rebuildSectionSync(var7, var3);
            var7.setNotDirty();
            this.minecraft.getProfiler().pop();
         } else {
            var5.add(var7);
         }
      }
   }

   private void renderWorldBorder(Camera var1) {
      WorldBorder var2 = this.level.getWorldBorder();
      double var3 = (double)(this.minecraft.options.getEffectiveRenderDistance() * 16);
      if (!(var1.getPosition().x < var2.getMaxX() - var3) || !(var1.getPosition().x > var2.getMinX() + var3) || !(var1.getPosition().z < var2.getMaxZ() - var3) || !(var1.getPosition().z > var2.getMinZ() + var3)) {
         double var5 = 1.0 - var2.getDistanceToBorder(var1.getPosition().x, var1.getPosition().z) / var3;
         var5 = Math.pow(var5, 4.0);
         var5 = Mth.clamp(var5, 0.0, 1.0);
         double var7 = var1.getPosition().x;
         double var9 = var1.getPosition().z;
         double var11 = (double)this.minecraft.gameRenderer.getDepthFar();
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         RenderSystem.setShaderTexture(0, FORCEFIELD_LOCATION);
         RenderSystem.depthMask(Minecraft.useShaderTransparency());
         int var13 = var2.getStatus().getColor();
         float var14 = (float)(var13 >> 16 & 255) / 255.0F;
         float var15 = (float)(var13 >> 8 & 255) / 255.0F;
         float var16 = (float)(var13 & 255) / 255.0F;
         RenderSystem.setShaderColor(var14, var15, var16, (float)var5);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.polygonOffset(-3.0F, -3.0F);
         RenderSystem.enablePolygonOffset();
         RenderSystem.disableCull();
         float var17 = (float)(Util.getMillis() % 3000L) / 3000.0F;
         float var18 = (float)(-Mth.frac(var1.getPosition().y * 0.5));
         float var19 = var18 + (float)var11;
         BufferBuilder var20 = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         double var21 = Math.max((double)Mth.floor(var9 - var3), var2.getMinZ());
         double var23 = Math.min((double)Mth.ceil(var9 + var3), var2.getMaxZ());
         float var25 = (float)(Mth.floor(var21) & 1) * 0.5F;
         float var26;
         double var27;
         double var29;
         float var31;
         if (var7 > var2.getMaxX() - var3) {
            var26 = var25;

            for(var27 = var21; var27 < var23; var26 += 0.5F) {
               var29 = Math.min(1.0, var23 - var27);
               var31 = (float)var29 * 0.5F;
               var20.addVertex((float)(var2.getMaxX() - var7), (float)(-var11), (float)(var27 - var9)).setUv(var17 - var26, var17 + var19);
               var20.addVertex((float)(var2.getMaxX() - var7), (float)(-var11), (float)(var27 + var29 - var9)).setUv(var17 - (var31 + var26), var17 + var19);
               var20.addVertex((float)(var2.getMaxX() - var7), (float)var11, (float)(var27 + var29 - var9)).setUv(var17 - (var31 + var26), var17 + var18);
               var20.addVertex((float)(var2.getMaxX() - var7), (float)var11, (float)(var27 - var9)).setUv(var17 - var26, var17 + var18);
               ++var27;
            }
         }

         if (var7 < var2.getMinX() + var3) {
            var26 = var25;

            for(var27 = var21; var27 < var23; var26 += 0.5F) {
               var29 = Math.min(1.0, var23 - var27);
               var31 = (float)var29 * 0.5F;
               var20.addVertex((float)(var2.getMinX() - var7), (float)(-var11), (float)(var27 - var9)).setUv(var17 + var26, var17 + var19);
               var20.addVertex((float)(var2.getMinX() - var7), (float)(-var11), (float)(var27 + var29 - var9)).setUv(var17 + var31 + var26, var17 + var19);
               var20.addVertex((float)(var2.getMinX() - var7), (float)var11, (float)(var27 + var29 - var9)).setUv(var17 + var31 + var26, var17 + var18);
               var20.addVertex((float)(var2.getMinX() - var7), (float)var11, (float)(var27 - var9)).setUv(var17 + var26, var17 + var18);
               ++var27;
            }
         }

         var21 = Math.max((double)Mth.floor(var7 - var3), var2.getMinX());
         var23 = Math.min((double)Mth.ceil(var7 + var3), var2.getMaxX());
         var25 = (float)(Mth.floor(var21) & 1) * 0.5F;
         if (var9 > var2.getMaxZ() - var3) {
            var26 = var25;

            for(var27 = var21; var27 < var23; var26 += 0.5F) {
               var29 = Math.min(1.0, var23 - var27);
               var31 = (float)var29 * 0.5F;
               var20.addVertex((float)(var27 - var7), (float)(-var11), (float)(var2.getMaxZ() - var9)).setUv(var17 + var26, var17 + var19);
               var20.addVertex((float)(var27 + var29 - var7), (float)(-var11), (float)(var2.getMaxZ() - var9)).setUv(var17 + var31 + var26, var17 + var19);
               var20.addVertex((float)(var27 + var29 - var7), (float)var11, (float)(var2.getMaxZ() - var9)).setUv(var17 + var31 + var26, var17 + var18);
               var20.addVertex((float)(var27 - var7), (float)var11, (float)(var2.getMaxZ() - var9)).setUv(var17 + var26, var17 + var18);
               ++var27;
            }
         }

         if (var9 < var2.getMinZ() + var3) {
            var26 = var25;

            for(var27 = var21; var27 < var23; var26 += 0.5F) {
               var29 = Math.min(1.0, var23 - var27);
               var31 = (float)var29 * 0.5F;
               var20.addVertex((float)(var27 - var7), (float)(-var11), (float)(var2.getMinZ() - var9)).setUv(var17 - var26, var17 + var19);
               var20.addVertex((float)(var27 + var29 - var7), (float)(-var11), (float)(var2.getMinZ() - var9)).setUv(var17 - (var31 + var26), var17 + var19);
               var20.addVertex((float)(var27 + var29 - var7), (float)var11, (float)(var2.getMinZ() - var9)).setUv(var17 - (var31 + var26), var17 + var18);
               var20.addVertex((float)(var27 - var7), (float)var11, (float)(var2.getMinZ() - var9)).setUv(var17 - var26, var17 + var18);
               ++var27;
            }
         }

         MeshData var32 = var20.build();
         if (var32 != null) {
            BufferUploader.drawWithShader(var32);
         }

         RenderSystem.enableCull();
         RenderSystem.polygonOffset(0.0F, 0.0F);
         RenderSystem.disablePolygonOffset();
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.depthMask(true);
      }
   }

   private void renderHitOutline(PoseStack var1, VertexConsumer var2, Entity var3, double var4, double var6, double var8, BlockPos var10, BlockState var11) {
      renderShape(var1, var2, var11.getShape(this.level, var10, CollisionContext.of(var3)), (double)var10.getX() - var4, (double)var10.getY() - var6, (double)var10.getZ() - var8, 0.0F, 0.0F, 0.0F, 0.4F);
   }

   private static Vec3 mixColor(float var0) {
      float var1 = 5.99999F;
      int var2 = (int)(Mth.clamp(var0, 0.0F, 1.0F) * 5.99999F);
      float var3 = var0 * 5.99999F - (float)var2;
      Vec3 var10000;
      switch (var2) {
         case 0 -> var10000 = new Vec3(1.0, (double)var3, 0.0);
         case 1 -> var10000 = new Vec3((double)(1.0F - var3), 1.0, 0.0);
         case 2 -> var10000 = new Vec3(0.0, 1.0, (double)var3);
         case 3 -> var10000 = new Vec3(0.0, 1.0 - (double)var3, 1.0);
         case 4 -> var10000 = new Vec3((double)var3, 0.0, 1.0);
         case 5 -> var10000 = new Vec3(1.0, 0.0, 1.0 - (double)var3);
         default -> throw new IllegalStateException("Unexpected value: " + var2);
      }

      return var10000;
   }

   private static Vec3 shiftHue(float var0, float var1, float var2, float var3) {
      Vec3 var4 = mixColor(var3).scale((double)var0);
      Vec3 var5 = mixColor((var3 + 0.33333334F) % 1.0F).scale((double)var1);
      Vec3 var6 = mixColor((var3 + 0.6666667F) % 1.0F).scale((double)var2);
      Vec3 var7 = var4.add(var5).add(var6);
      double var8 = Math.max(Math.max(1.0, var7.x), Math.max(var7.y, var7.z));
      return new Vec3(var7.x / var8, var7.y / var8, var7.z / var8);
   }

   public static void renderVoxelShape(PoseStack var0, VertexConsumer var1, VoxelShape var2, double var3, double var5, double var7, float var9, float var10, float var11, float var12, boolean var13) {
      List var14 = var2.toAabbs();
      if (!var14.isEmpty()) {
         int var15 = var13 ? var14.size() : var14.size() * 8;
         renderShape(var0, var1, Shapes.create((AABB)var14.get(0)), var3, var5, var7, var9, var10, var11, var12);

         for(int var16 = 1; var16 < var14.size(); ++var16) {
            AABB var17 = (AABB)var14.get(var16);
            float var18 = (float)var16 / (float)var15;
            Vec3 var19 = shiftHue(var9, var10, var11, var18);
            renderShape(var0, var1, Shapes.create(var17), var3, var5, var7, (float)var19.x, (float)var19.y, (float)var19.z, var12);
         }

      }
   }

   private static void renderShape(PoseStack var0, VertexConsumer var1, VoxelShape var2, double var3, double var5, double var7, float var9, float var10, float var11, float var12) {
      PoseStack.Pose var13 = var0.last();
      var2.forAllEdges((var12x, var14, var16, var18, var20, var22) -> {
         float var24 = (float)(var18 - var12x);
         float var25 = (float)(var20 - var14);
         float var26 = (float)(var22 - var16);
         float var27 = Mth.sqrt(var24 * var24 + var25 * var25 + var26 * var26);
         var24 /= var27;
         var25 /= var27;
         var26 /= var27;
         var1.addVertex(var13, (float)(var12x + var3), (float)(var14 + var5), (float)(var16 + var7)).setColor(var9, var10, var11, var12).setNormal(var13, var24, var25, var26);
         var1.addVertex(var13, (float)(var18 + var3), (float)(var20 + var5), (float)(var22 + var7)).setColor(var9, var10, var11, var12).setNormal(var13, var24, var25, var26);
      });
   }

   public static void renderLineBox(VertexConsumer var0, double var1, double var3, double var5, double var7, double var9, double var11, float var13, float var14, float var15, float var16) {
      renderLineBox(new PoseStack(), var0, var1, var3, var5, var7, var9, var11, var13, var14, var15, var16, var13, var14, var15);
   }

   public static void renderLineBox(PoseStack var0, VertexConsumer var1, AABB var2, float var3, float var4, float var5, float var6) {
      renderLineBox(var0, var1, var2.minX, var2.minY, var2.minZ, var2.maxX, var2.maxY, var2.maxZ, var3, var4, var5, var6, var3, var4, var5);
   }

   public static void renderLineBox(PoseStack var0, VertexConsumer var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, float var15, float var16, float var17) {
      renderLineBox(var0, var1, var2, var4, var6, var8, var10, var12, var14, var15, var16, var17, var14, var15, var16);
   }

   public static void renderLineBox(PoseStack var0, VertexConsumer var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, float var15, float var16, float var17, float var18, float var19, float var20) {
      PoseStack.Pose var21 = var0.last();
      float var22 = (float)var2;
      float var23 = (float)var4;
      float var24 = (float)var6;
      float var25 = (float)var8;
      float var26 = (float)var10;
      float var27 = (float)var12;
      var1.addVertex(var21, var22, var23, var24).setColor(var14, var19, var20, var17).setNormal(var21, 1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var25, var23, var24).setColor(var14, var19, var20, var17).setNormal(var21, 1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var22, var23, var24).setColor(var18, var15, var20, var17).setNormal(var21, 0.0F, 1.0F, 0.0F);
      var1.addVertex(var21, var22, var26, var24).setColor(var18, var15, var20, var17).setNormal(var21, 0.0F, 1.0F, 0.0F);
      var1.addVertex(var21, var22, var23, var24).setColor(var18, var19, var16, var17).setNormal(var21, 0.0F, 0.0F, 1.0F);
      var1.addVertex(var21, var22, var23, var27).setColor(var18, var19, var16, var17).setNormal(var21, 0.0F, 0.0F, 1.0F);
      var1.addVertex(var21, var25, var23, var24).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 1.0F, 0.0F);
      var1.addVertex(var21, var25, var26, var24).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 1.0F, 0.0F);
      var1.addVertex(var21, var25, var26, var24).setColor(var14, var15, var16, var17).setNormal(var21, -1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var22, var26, var24).setColor(var14, var15, var16, var17).setNormal(var21, -1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var22, var26, var24).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 0.0F, 1.0F);
      var1.addVertex(var21, var22, var26, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 0.0F, 1.0F);
      var1.addVertex(var21, var22, var26, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, -1.0F, 0.0F);
      var1.addVertex(var21, var22, var23, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, -1.0F, 0.0F);
      var1.addVertex(var21, var22, var23, var27).setColor(var14, var15, var16, var17).setNormal(var21, 1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var25, var23, var27).setColor(var14, var15, var16, var17).setNormal(var21, 1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var25, var23, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 0.0F, -1.0F);
      var1.addVertex(var21, var25, var23, var24).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 0.0F, -1.0F);
      var1.addVertex(var21, var22, var26, var27).setColor(var14, var15, var16, var17).setNormal(var21, 1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var25, var26, var27).setColor(var14, var15, var16, var17).setNormal(var21, 1.0F, 0.0F, 0.0F);
      var1.addVertex(var21, var25, var23, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 1.0F, 0.0F);
      var1.addVertex(var21, var25, var26, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 1.0F, 0.0F);
      var1.addVertex(var21, var25, var26, var24).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 0.0F, 1.0F);
      var1.addVertex(var21, var25, var26, var27).setColor(var14, var15, var16, var17).setNormal(var21, 0.0F, 0.0F, 1.0F);
   }

   public static void addChainedFilledBoxVertices(PoseStack var0, VertexConsumer var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, float var15, float var16, float var17) {
      addChainedFilledBoxVertices(var0, var1, (float)var2, (float)var4, (float)var6, (float)var8, (float)var10, (float)var12, var14, var15, var16, var17);
   }

   public static void addChainedFilledBoxVertices(PoseStack var0, VertexConsumer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11) {
      Matrix4f var12 = var0.last().pose();
      var1.addVertex(var12, var2, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var3, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var2, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var4).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var7).setColor(var8, var9, var10, var11);
      var1.addVertex(var12, var5, var6, var7).setColor(var8, var9, var10, var11);
   }

   public void blockChanged(BlockGetter var1, BlockPos var2, BlockState var3, BlockState var4, int var5) {
      this.setBlockDirty(var2, (var5 & 8) != 0);
   }

   private void setBlockDirty(BlockPos var1, boolean var2) {
      for(int var3 = var1.getZ() - 1; var3 <= var1.getZ() + 1; ++var3) {
         for(int var4 = var1.getX() - 1; var4 <= var1.getX() + 1; ++var4) {
            for(int var5 = var1.getY() - 1; var5 <= var1.getY() + 1; ++var5) {
               this.setSectionDirty(SectionPos.blockToSectionCoord(var4), SectionPos.blockToSectionCoord(var5), SectionPos.blockToSectionCoord(var3), var2);
            }
         }
      }

   }

   public void setBlocksDirty(int var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = var3 - 1; var7 <= var6 + 1; ++var7) {
         for(int var8 = var1 - 1; var8 <= var4 + 1; ++var8) {
            for(int var9 = var2 - 1; var9 <= var5 + 1; ++var9) {
               this.setSectionDirty(SectionPos.blockToSectionCoord(var8), SectionPos.blockToSectionCoord(var9), SectionPos.blockToSectionCoord(var7));
            }
         }
      }

   }

   public void setBlockDirty(BlockPos var1, BlockState var2, BlockState var3) {
      if (this.minecraft.getModelManager().requiresRender(var2, var3)) {
         this.setBlocksDirty(var1.getX(), var1.getY(), var1.getZ(), var1.getX(), var1.getY(), var1.getZ());
      }

   }

   public void setSectionDirtyWithNeighbors(int var1, int var2, int var3) {
      for(int var4 = var3 - 1; var4 <= var3 + 1; ++var4) {
         for(int var5 = var1 - 1; var5 <= var1 + 1; ++var5) {
            for(int var6 = var2 - 1; var6 <= var2 + 1; ++var6) {
               this.setSectionDirty(var5, var6, var4);
            }
         }
      }

   }

   public void setSectionDirty(int var1, int var2, int var3) {
      this.setSectionDirty(var1, var2, var3, false);
   }

   private void setSectionDirty(int var1, int var2, int var3, boolean var4) {
      this.viewArea.setDirty(var1, var2, var3, var4);
   }

   public void playJukeboxSong(Holder<JukeboxSong> var1, BlockPos var2) {
      if (this.level != null) {
         this.stopJukeboxSong(var2);
         JukeboxSong var3 = (JukeboxSong)var1.value();
         SoundEvent var4 = (SoundEvent)var3.soundEvent().value();
         SimpleSoundInstance var5 = SimpleSoundInstance.forJukeboxSong(var4, Vec3.atCenterOf(var2));
         this.playingJukeboxSongs.put(var2, var5);
         this.minecraft.getSoundManager().play(var5);
         this.minecraft.gui.setNowPlaying(var3.description());
         this.notifyNearbyEntities(this.level, var2, true);
      }
   }

   private void stopJukeboxSong(BlockPos var1) {
      SoundInstance var2 = (SoundInstance)this.playingJukeboxSongs.remove(var1);
      if (var2 != null) {
         this.minecraft.getSoundManager().stop(var2);
      }

   }

   public void stopJukeboxSongAndNotifyNearby(BlockPos var1) {
      this.stopJukeboxSong(var1);
      if (this.level != null) {
         this.notifyNearbyEntities(this.level, var1, false);
      }

   }

   private void notifyNearbyEntities(Level var1, BlockPos var2, boolean var3) {
      List var4 = var1.getEntitiesOfClass(LivingEntity.class, (new AABB(var2)).inflate(3.0));
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         LivingEntity var6 = (LivingEntity)var5.next();
         var6.setRecordPlayingNearby(var2, var3);
      }

   }

   public void addParticle(ParticleOptions var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      this.addParticle(var1, var2, false, var3, var5, var7, var9, var11, var13);
   }

   public void addParticle(ParticleOptions var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14) {
      try {
         this.addParticleInternal(var1, var2, var3, var4, var6, var8, var10, var12, var14);
      } catch (Throwable var19) {
         CrashReport var17 = CrashReport.forThrowable(var19, "Exception while adding particle");
         CrashReportCategory var18 = var17.addCategory("Particle being added");
         var18.setDetail("ID", (Object)BuiltInRegistries.PARTICLE_TYPE.getKey(var1.getType()));
         var18.setDetail("Parameters", () -> {
            return ParticleTypes.CODEC.encodeStart(this.level.registryAccess().createSerializationContext(NbtOps.INSTANCE), var1).toString();
         });
         var18.setDetail("Position", () -> {
            return CrashReportCategory.formatLocation(this.level, var4, var6, var8);
         });
         throw new ReportedException(var17);
      }
   }

   private <T extends ParticleOptions> void addParticle(T var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this.addParticle(var1, var1.getType().getOverrideLimiter(), var2, var4, var6, var8, var10, var12);
   }

   @Nullable
   private Particle addParticleInternal(ParticleOptions var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      return this.addParticleInternal(var1, var2, false, var3, var5, var7, var9, var11, var13);
   }

   @Nullable
   private Particle addParticleInternal(ParticleOptions var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14) {
      Camera var16 = this.minecraft.gameRenderer.getMainCamera();
      ParticleStatus var17 = this.calculateParticleLevel(var3);
      if (var2) {
         return this.minecraft.particleEngine.createParticle(var1, var4, var6, var8, var10, var12, var14);
      } else if (var16.getPosition().distanceToSqr(var4, var6, var8) > 1024.0) {
         return null;
      } else {
         return var17 == ParticleStatus.MINIMAL ? null : this.minecraft.particleEngine.createParticle(var1, var4, var6, var8, var10, var12, var14);
      }
   }

   private ParticleStatus calculateParticleLevel(boolean var1) {
      ParticleStatus var2 = (ParticleStatus)this.minecraft.options.particles().get();
      if (var1 && var2 == ParticleStatus.MINIMAL && this.level.random.nextInt(10) == 0) {
         var2 = ParticleStatus.DECREASED;
      }

      if (var2 == ParticleStatus.DECREASED && this.level.random.nextInt(3) == 0) {
         var2 = ParticleStatus.MINIMAL;
      }

      return var2;
   }

   public void clear() {
   }

   public void globalLevelEvent(int var1, BlockPos var2, int var3) {
      switch (var1) {
         case 1023:
         case 1028:
         case 1038:
            Camera var4 = this.minecraft.gameRenderer.getMainCamera();
            if (var4.isInitialized()) {
               double var5 = (double)var2.getX() - var4.getPosition().x;
               double var7 = (double)var2.getY() - var4.getPosition().y;
               double var9 = (double)var2.getZ() - var4.getPosition().z;
               double var11 = Math.sqrt(var5 * var5 + var7 * var7 + var9 * var9);
               double var13 = var4.getPosition().x;
               double var15 = var4.getPosition().y;
               double var17 = var4.getPosition().z;
               if (var11 > 0.0) {
                  var13 += var5 / var11 * 2.0;
                  var15 += var7 / var11 * 2.0;
                  var17 += var9 / var11 * 2.0;
               }

               if (var1 == 1023) {
                  this.level.playLocalSound(var13, var15, var17, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0F, 1.0F, false);
               } else if (var1 == 1038) {
                  this.level.playLocalSound(var13, var15, var17, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.0F, 1.0F, false);
               } else {
                  this.level.playLocalSound(var13, var15, var17, SoundEvents.ENDER_DRAGON_DEATH, SoundSource.HOSTILE, 5.0F, 1.0F, false);
               }
            }
         default:
      }
   }

   public void levelEvent(int var1, BlockPos var2, int var3) {
      RandomSource var4 = this.level.random;
      int var7;
      double var8;
      double var10;
      double var12;
      double var17;
      float var39;
      int var45;
      float var46;
      double var49;
      double var55;
      double var57;
      switch (var1) {
         case 1000:
            this.level.playLocalSound(var2, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1001:
            this.level.playLocalSound(var2, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0F, 1.2F, false);
            break;
         case 1002:
            this.level.playLocalSound(var2, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0F, 1.2F, false);
            break;
         case 1004:
            this.level.playLocalSound(var2, SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.2F, false);
            break;
         case 1009:
            if (var3 == 0) {
               this.level.playLocalSound(var2, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var4.nextFloat() - var4.nextFloat()) * 0.8F, false);
            } else if (var3 == 1) {
               this.level.playLocalSound(var2, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.7F, 1.6F + (var4.nextFloat() - var4.nextFloat()) * 0.4F, false);
            }
            break;
         case 1010:
            this.level.registryAccess().registryOrThrow(Registries.JUKEBOX_SONG).getHolder(var3).ifPresent((var2x) -> {
               this.playJukeboxSong(var2x, var2);
            });
            break;
         case 1011:
            this.stopJukeboxSongAndNotifyNearby(var2);
            break;
         case 1015:
            this.level.playLocalSound(var2, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1016:
            this.level.playLocalSound(var2, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1017:
            this.level.playLocalSound(var2, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 10.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1018:
            this.level.playLocalSound(var2, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1019:
            this.level.playLocalSound(var2, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1020:
            this.level.playLocalSound(var2, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1021:
            this.level.playLocalSound(var2, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1022:
            this.level.playLocalSound(var2, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1024:
            this.level.playLocalSound(var2, SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1025:
            this.level.playLocalSound(var2, SoundEvents.BAT_TAKEOFF, SoundSource.NEUTRAL, 0.05F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1026:
            this.level.playLocalSound(var2, SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1027:
            this.level.playLocalSound(var2, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1029:
            this.level.playLocalSound(var2, SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1.0F, var4.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1030:
            this.level.playLocalSound(var2, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, var4.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1031:
            this.level.playLocalSound(var2, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1032:
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRAVEL, var4.nextFloat() * 0.4F + 0.8F, 0.25F));
            break;
         case 1033:
            this.level.playLocalSound(var2, SoundEvents.CHORUS_FLOWER_GROW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1034:
            this.level.playLocalSound(var2, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1035:
            this.level.playLocalSound(var2, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1039:
            this.level.playLocalSound(var2, SoundEvents.PHANTOM_BITE, SoundSource.HOSTILE, 0.3F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1040:
            this.level.playLocalSound(var2, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1041:
            this.level.playLocalSound(var2, SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1042:
            this.level.playLocalSound(var2, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1043:
            this.level.playLocalSound(var2, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1044:
            this.level.playLocalSound(var2, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1045:
            this.level.playLocalSound(var2, SoundEvents.POINTED_DRIPSTONE_LAND, SoundSource.BLOCKS, 2.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1046:
            this.level.playLocalSound(var2, SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON, SoundSource.BLOCKS, 2.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1047:
            this.level.playLocalSound(var2, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundSource.BLOCKS, 2.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1048:
            this.level.playLocalSound(var2, SoundEvents.SKELETON_CONVERTED_TO_STRAY, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1049:
            this.level.playLocalSound(var2, SoundEvents.CRAFTER_CRAFT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1050:
            this.level.playLocalSound(var2, SoundEvents.CRAFTER_FAIL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1051:
            this.level.playLocalSound(var2, SoundEvents.WIND_CHARGE_THROW, SoundSource.BLOCKS, 0.5F, 0.4F / (this.level.getRandom().nextFloat() * 0.4F + 0.8F), false);
         case 2010:
            this.shootParticles(var3, var2, var4, ParticleTypes.WHITE_SMOKE);
            break;
         case 1500:
            ComposterBlock.handleFill(this.level, var2, var3 > 0);
            break;
         case 1501:
            this.level.playLocalSound(var2, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var4.nextFloat() - var4.nextFloat()) * 0.8F, false);

            for(var45 = 0; var45 < 8; ++var45) {
               this.level.addParticle(ParticleTypes.LARGE_SMOKE, (double)var2.getX() + var4.nextDouble(), (double)var2.getY() + 1.2, (double)var2.getZ() + var4.nextDouble(), 0.0, 0.0, 0.0);
            }

            return;
         case 1502:
            this.level.playLocalSound(var2, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5F, 2.6F + (var4.nextFloat() - var4.nextFloat()) * 0.8F, false);

            for(var45 = 0; var45 < 5; ++var45) {
               var49 = (double)var2.getX() + var4.nextDouble() * 0.6 + 0.2;
               var55 = (double)var2.getY() + var4.nextDouble() * 0.6 + 0.2;
               var57 = (double)var2.getZ() + var4.nextDouble() * 0.6 + 0.2;
               this.level.addParticle(ParticleTypes.SMOKE, var49, var55, var57, 0.0, 0.0, 0.0);
            }

            return;
         case 1503:
            this.level.playLocalSound(var2, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);

            for(var45 = 0; var45 < 16; ++var45) {
               var49 = (double)var2.getX() + (5.0 + var4.nextDouble() * 6.0) / 16.0;
               var55 = (double)var2.getY() + 0.8125;
               var57 = (double)var2.getZ() + (5.0 + var4.nextDouble() * 6.0) / 16.0;
               this.level.addParticle(ParticleTypes.SMOKE, var49, var55, var57, 0.0, 0.0, 0.0);
            }

            return;
         case 1504:
            PointedDripstoneBlock.spawnDripParticle(this.level, var2, this.level.getBlockState(var2));
            break;
         case 1505:
            BoneMealItem.addGrowthParticles(this.level, var2, var3);
            this.level.playLocalSound(var2, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 2000:
            this.shootParticles(var3, var2, var4, ParticleTypes.SMOKE);
            break;
         case 2001:
            BlockState var24 = Block.stateById(var3);
            if (!var24.isAir()) {
               SoundType var27 = var24.getSoundType();
               this.level.playLocalSound(var2, var27.getBreakSound(), SoundSource.BLOCKS, (var27.getVolume() + 1.0F) / 2.0F, var27.getPitch() * 0.8F, false);
            }

            this.level.addDestroyBlockEffect(var2, var24);
            break;
         case 2002:
         case 2007:
            Vec3 var23 = Vec3.atBottomCenterOf(var2);

            for(int var25 = 0; var25 < 8; ++var25) {
               this.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), var23.x, var23.y, var23.z, var4.nextGaussian() * 0.15, var4.nextDouble() * 0.2, var4.nextGaussian() * 0.15);
            }

            float var26 = (float)(var3 >> 16 & 255) / 255.0F;
            float var35 = (float)(var3 >> 8 & 255) / 255.0F;
            var39 = (float)(var3 >> 0 & 255) / 255.0F;
            SimpleParticleType var44 = var1 == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;

            for(var45 = 0; var45 < 100; ++var45) {
               var49 = var4.nextDouble() * 4.0;
               var55 = var4.nextDouble() * 3.141592653589793 * 2.0;
               var57 = Math.cos(var55) * var49;
               var17 = 0.01 + var4.nextDouble() * 0.5;
               double var59 = Math.sin(var55) * var49;
               Particle var21 = this.addParticleInternal(var44, var44.getType().getOverrideLimiter(), var23.x + var57 * 0.1, var23.y + 0.3, var23.z + var59 * 0.1, var57, var17, var59);
               if (var21 != null) {
                  float var22 = 0.75F + var4.nextFloat() * 0.25F;
                  var21.setColor(var26 * var22, var35 * var22, var39 * var22);
                  var21.setPower((float)var49);
               }
            }

            this.level.playLocalSound(var2, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0F, var4.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2003:
            double var5 = (double)var2.getX() + 0.5;
            double var33 = (double)var2.getY();
            double var40 = (double)var2.getZ() + 0.5;

            for(int var47 = 0; var47 < 8; ++var47) {
               this.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), var5, var33, var40, var4.nextGaussian() * 0.15, var4.nextDouble() * 0.2, var4.nextGaussian() * 0.15);
            }

            for(var49 = 0.0; var49 < 6.283185307179586; var49 += 0.15707963267948966) {
               this.addParticle(ParticleTypes.PORTAL, var5 + Math.cos(var49) * 5.0, var33 - 0.4, var40 + Math.sin(var49) * 5.0, Math.cos(var49) * -5.0, 0.0, Math.sin(var49) * -5.0);
               this.addParticle(ParticleTypes.PORTAL, var5 + Math.cos(var49) * 5.0, var33 - 0.4, var40 + Math.sin(var49) * 5.0, Math.cos(var49) * -7.0, 0.0, Math.sin(var49) * -7.0);
            }

            return;
         case 2004:
            for(var7 = 0; var7 < 20; ++var7) {
               var8 = (double)var2.getX() + 0.5 + (var4.nextDouble() - 0.5) * 2.0;
               var10 = (double)var2.getY() + 0.5 + (var4.nextDouble() - 0.5) * 2.0;
               var12 = (double)var2.getZ() + 0.5 + (var4.nextDouble() - 0.5) * 2.0;
               this.level.addParticle(ParticleTypes.SMOKE, var8, var10, var12, 0.0, 0.0, 0.0);
               this.level.addParticle(ParticleTypes.FLAME, var8, var10, var12, 0.0, 0.0, 0.0);
            }

            return;
         case 2006:
            for(var45 = 0; var45 < 200; ++var45) {
               var46 = var4.nextFloat() * 4.0F;
               float var53 = var4.nextFloat() * 6.2831855F;
               var55 = (double)(Mth.cos(var53) * var46);
               var57 = 0.01 + var4.nextDouble() * 0.5;
               var17 = (double)(Mth.sin(var53) * var46);
               Particle var19 = this.addParticleInternal(ParticleTypes.DRAGON_BREATH, false, (double)var2.getX() + var55 * 0.1, (double)var2.getY() + 0.3, (double)var2.getZ() + var17 * 0.1, var55, var57, var17);
               if (var19 != null) {
                  var19.setPower(var46);
               }
            }

            if (var3 == 1) {
               this.level.playLocalSound(var2, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.0F, var4.nextFloat() * 0.1F + 0.9F, false);
            }
            break;
         case 2008:
            this.level.addParticle(ParticleTypes.EXPLOSION, (double)var2.getX() + 0.5, (double)var2.getY() + 0.5, (double)var2.getZ() + 0.5, 0.0, 0.0, 0.0);
            break;
         case 2009:
            for(var45 = 0; var45 < 8; ++var45) {
               this.level.addParticle(ParticleTypes.CLOUD, (double)var2.getX() + var4.nextDouble(), (double)var2.getY() + 1.2, (double)var2.getZ() + var4.nextDouble(), 0.0, 0.0, 0.0);
            }

            return;
         case 2011:
            ParticleUtils.spawnParticleInBlock(this.level, var2, var3, ParticleTypes.HAPPY_VILLAGER);
            break;
         case 2012:
            ParticleUtils.spawnParticleInBlock(this.level, var2, var3, ParticleTypes.HAPPY_VILLAGER);
            break;
         case 2013:
            ParticleUtils.spawnSmashAttackParticles(this.level, var2, var3);
            break;
         case 3000:
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)var2.getX() + 0.5, (double)var2.getY() + 0.5, (double)var2.getZ() + 0.5, 0.0, 0.0, 0.0);
            this.level.playLocalSound(var2, SoundEvents.END_GATEWAY_SPAWN, SoundSource.BLOCKS, 10.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
            break;
         case 3001:
            this.level.playLocalSound(var2, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 64.0F, 0.8F + this.level.random.nextFloat() * 0.3F, false);
            break;
         case 3002:
            if (var3 >= 0 && var3 < Direction.Axis.VALUES.length) {
               ParticleUtils.spawnParticlesAlongAxis(Direction.Axis.VALUES[var3], this.level, var2, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(10, 19));
            } else {
               ParticleUtils.spawnParticlesOnBlockFaces(this.level, var2, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(3, 5));
            }
            break;
         case 3003:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, var2, ParticleTypes.WAX_ON, UniformInt.of(3, 5));
            this.level.playLocalSound(var2, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 3004:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, var2, ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
            break;
         case 3005:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, var2, ParticleTypes.SCRAPE, UniformInt.of(3, 5));
            break;
         case 3006:
            var7 = var3 >> 6;
            float var42;
            float var56;
            if (var7 > 0) {
               if (var4.nextFloat() < 0.3F + (float)var7 * 0.1F) {
                  var39 = 0.15F + 0.02F * (float)var7 * (float)var7 * var4.nextFloat();
                  float var34 = 0.4F + 0.3F * (float)var7 * var4.nextFloat();
                  this.level.playLocalSound(var2, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, var39, var34, false);
               }

               byte var41 = (byte)(var3 & 63);
               UniformInt var37 = UniformInt.of(0, var7);
               var42 = 0.005F;
               Supplier var11 = () -> {
                  return new Vec3(Mth.nextDouble(var4, -0.004999999888241291, 0.004999999888241291), Mth.nextDouble(var4, -0.004999999888241291, 0.004999999888241291), Mth.nextDouble(var4, -0.004999999888241291, 0.004999999888241291));
               };
               if (var41 == 0) {
                  Direction[] var48 = Direction.values();
                  int var13 = var48.length;

                  for(int var14 = 0; var14 < var13; ++var14) {
                     Direction var15 = var48[var14];
                     float var16 = var15 == Direction.DOWN ? 3.1415927F : 0.0F;
                     var17 = var15.getAxis() == Direction.Axis.Y ? 0.65 : 0.57;
                     ParticleUtils.spawnParticlesOnBlockFace(this.level, var2, new SculkChargeParticleOptions(var16), var37, var15, var11, var17);
                  }

                  return;
               } else {
                  Iterator var50 = MultifaceBlock.unpack(var41).iterator();

                  while(var50.hasNext()) {
                     Direction var52 = (Direction)var50.next();
                     var56 = var52 == Direction.UP ? 3.1415927F : 0.0F;
                     var57 = 0.35;
                     ParticleUtils.spawnParticlesOnBlockFace(this.level, var2, new SculkChargeParticleOptions(var56), var37, var52, var11, 0.35);
                  }

                  return;
               }
            } else {
               this.level.playLocalSound(var2, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
               boolean var43 = this.level.getBlockState(var2).isCollisionShapeFullBlock(this.level, var2);
               int var38 = var43 ? 40 : 20;
               var42 = var43 ? 0.45F : 0.25F;
               var46 = 0.07F;

               for(int var51 = 0; var51 < var38; ++var51) {
                  float var54 = 2.0F * var4.nextFloat() - 1.0F;
                  var56 = 2.0F * var4.nextFloat() - 1.0F;
                  float var58 = 2.0F * var4.nextFloat() - 1.0F;
                  this.level.addParticle(ParticleTypes.SCULK_CHARGE_POP, (double)var2.getX() + 0.5 + (double)(var54 * var42), (double)var2.getY() + 0.5 + (double)(var56 * var42), (double)var2.getZ() + 0.5 + (double)(var58 * var42), (double)(var54 * 0.07F), (double)(var56 * 0.07F), (double)(var58 * 0.07F));
               }

               return;
            }
         case 3007:
            for(int var32 = 0; var32 < 10; ++var32) {
               this.level.addParticle(new ShriekParticleOption(var32 * 5), false, (double)var2.getX() + 0.5, (double)var2.getY() + SculkShriekerBlock.TOP_Y, (double)var2.getZ() + 0.5, 0.0, 0.0, 0.0);
            }

            BlockState var36 = this.level.getBlockState(var2);
            boolean var9 = var36.hasProperty(BlockStateProperties.WATERLOGGED) && (Boolean)var36.getValue(BlockStateProperties.WATERLOGGED);
            if (!var9) {
               this.level.playLocalSound((double)var2.getX() + 0.5, (double)var2.getY() + SculkShriekerBlock.TOP_Y, (double)var2.getZ() + 0.5, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.BLOCKS, 2.0F, 0.6F + this.level.random.nextFloat() * 0.4F, false);
            }
            break;
         case 3008:
            BlockState var6 = Block.stateById(var3);
            Block var31 = var6.getBlock();
            if (var31 instanceof BrushableBlock var29) {
               this.level.playLocalSound(var2, var29.getBrushCompletedSound(), SoundSource.PLAYERS, 1.0F, 1.0F, false);
            }

            this.level.addDestroyBlockEffect(var2, var6);
            break;
         case 3009:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, var2, ParticleTypes.EGG_CRACK, UniformInt.of(3, 6));
            break;
         case 3011:
            TrialSpawner.addSpawnParticles(this.level, var2, var4, TrialSpawner.FlameParticle.decode(var3).particleType);
            break;
         case 3012:
            this.level.playLocalSound(var2, SoundEvents.TRIAL_SPAWNER_SPAWN_MOB, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addSpawnParticles(this.level, var2, var4, TrialSpawner.FlameParticle.decode(var3).particleType);
            break;
         case 3013:
            this.level.playLocalSound(var2, SoundEvents.TRIAL_SPAWNER_DETECT_PLAYER, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addDetectPlayerParticles(this.level, var2, var4, var3, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER);
            break;
         case 3014:
            this.level.playLocalSound(var2, SoundEvents.TRIAL_SPAWNER_EJECT_ITEM, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addEjectItemParticles(this.level, var2, var4);
            break;
         case 3015:
            BlockEntity var30 = this.level.getBlockEntity(var2);
            if (var30 instanceof VaultBlockEntity var28) {
               VaultBlockEntity.Client.emitActivationParticles(this.level, var28.getBlockPos(), var28.getBlockState(), var28.getSharedData(), var3 == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME);
               this.level.playLocalSound(var2, SoundEvents.VAULT_ACTIVATE, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            }
            break;
         case 3016:
            VaultBlockEntity.Client.emitDeactivationParticles(this.level, var2, var3 == 0 ? ParticleTypes.SMALL_FLAME : ParticleTypes.SOUL_FIRE_FLAME);
            this.level.playLocalSound(var2, SoundEvents.VAULT_DEACTIVATE, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            break;
         case 3017:
            TrialSpawner.addEjectItemParticles(this.level, var2, var4);
            break;
         case 3018:
            for(var7 = 0; var7 < 10; ++var7) {
               var8 = var4.nextGaussian() * 0.02;
               var10 = var4.nextGaussian() * 0.02;
               var12 = var4.nextGaussian() * 0.02;
               this.level.addParticle(ParticleTypes.POOF, (double)var2.getX() + var4.nextDouble(), (double)var2.getY() + var4.nextDouble(), (double)var2.getZ() + var4.nextDouble(), var8, var10, var12);
            }

            this.level.playLocalSound(var2, SoundEvents.COBWEB_PLACE, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            break;
         case 3019:
            this.level.playLocalSound(var2, SoundEvents.TRIAL_SPAWNER_DETECT_PLAYER, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addDetectPlayerParticles(this.level, var2, var4, var3, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS);
            break;
         case 3020:
            this.level.playLocalSound(var2, SoundEvents.TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundSource.BLOCKS, var3 == 0 ? 0.3F : 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addDetectPlayerParticles(this.level, var2, var4, 0, ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS);
            TrialSpawner.addBecomeOminousParticles(this.level, var2, var4);
            break;
         case 3021:
            this.level.playLocalSound(var2, SoundEvents.TRIAL_SPAWNER_SPAWN_ITEM, SoundSource.BLOCKS, 1.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, true);
            TrialSpawner.addSpawnParticles(this.level, var2, var4, TrialSpawner.FlameParticle.decode(var3).particleType);
      }

   }

   public void destroyBlockProgress(int var1, BlockPos var2, int var3) {
      BlockDestructionProgress var4;
      if (var3 >= 0 && var3 < 10) {
         var4 = (BlockDestructionProgress)this.destroyingBlocks.get(var1);
         if (var4 != null) {
            this.removeProgress(var4);
         }

         if (var4 == null || var4.getPos().getX() != var2.getX() || var4.getPos().getY() != var2.getY() || var4.getPos().getZ() != var2.getZ()) {
            var4 = new BlockDestructionProgress(var1, var2);
            this.destroyingBlocks.put(var1, var4);
         }

         var4.setProgress(var3);
         var4.updateTick(this.ticks);
         ((SortedSet)this.destructionProgress.computeIfAbsent(var4.getPos().asLong(), (var0) -> {
            return Sets.newTreeSet();
         })).add(var4);
      } else {
         var4 = (BlockDestructionProgress)this.destroyingBlocks.remove(var1);
         if (var4 != null) {
            this.removeProgress(var4);
         }
      }

   }

   public boolean hasRenderedAllSections() {
      return this.sectionRenderDispatcher.isQueueEmpty();
   }

   public void onChunkLoaded(ChunkPos var1) {
      this.sectionOcclusionGraph.onChunkLoaded(var1);
   }

   public void needsUpdate() {
      this.sectionOcclusionGraph.invalidate();
      this.generateClouds = true;
   }

   public void updateGlobalBlockEntities(Collection<BlockEntity> var1, Collection<BlockEntity> var2) {
      synchronized(this.globalBlockEntities) {
         this.globalBlockEntities.removeAll(var1);
         this.globalBlockEntities.addAll(var2);
      }
   }

   public static int getLightColor(BlockAndTintGetter var0, BlockPos var1) {
      return getLightColor(var0, var0.getBlockState(var1), var1);
   }

   public static int getLightColor(BlockAndTintGetter var0, BlockState var1, BlockPos var2) {
      if (var1.emissiveRendering(var0, var2)) {
         return 15728880;
      } else {
         int var3 = var0.getBrightness(LightLayer.SKY, var2);
         int var4 = var0.getBrightness(LightLayer.BLOCK, var2);
         int var5 = var1.getLightEmission();
         if (var4 < var5) {
            var4 = var5;
         }

         return var3 << 20 | var4 << 4;
      }
   }

   public boolean isSectionCompiled(BlockPos var1) {
      SectionRenderDispatcher.RenderSection var2 = this.viewArea.getRenderSectionAt(var1);
      return var2 != null && var2.compiled.get() != SectionRenderDispatcher.CompiledSection.UNCOMPILED;
   }

   @Nullable
   public RenderTarget entityTarget() {
      return this.entityTarget;
   }

   @Nullable
   public RenderTarget getTranslucentTarget() {
      return this.translucentTarget;
   }

   @Nullable
   public RenderTarget getItemEntityTarget() {
      return this.itemEntityTarget;
   }

   @Nullable
   public RenderTarget getParticlesTarget() {
      return this.particlesTarget;
   }

   @Nullable
   public RenderTarget getWeatherTarget() {
      return this.weatherTarget;
   }

   @Nullable
   public RenderTarget getCloudsTarget() {
      return this.cloudsTarget;
   }

   private void shootParticles(int var1, BlockPos var2, RandomSource var3, SimpleParticleType var4) {
      Direction var5 = Direction.from3DDataValue(var1);
      int var6 = var5.getStepX();
      int var7 = var5.getStepY();
      int var8 = var5.getStepZ();
      double var9 = (double)var2.getX() + (double)var6 * 0.6 + 0.5;
      double var11 = (double)var2.getY() + (double)var7 * 0.6 + 0.5;
      double var13 = (double)var2.getZ() + (double)var8 * 0.6 + 0.5;

      for(int var15 = 0; var15 < 10; ++var15) {
         double var16 = var3.nextDouble() * 0.2 + 0.01;
         double var18 = var9 + (double)var6 * 0.01 + (var3.nextDouble() - 0.5) * (double)var8 * 0.5;
         double var20 = var11 + (double)var7 * 0.01 + (var3.nextDouble() - 0.5) * (double)var7 * 0.5;
         double var22 = var13 + (double)var8 * 0.01 + (var3.nextDouble() - 0.5) * (double)var6 * 0.5;
         double var24 = (double)var6 * var16 + var3.nextGaussian() * 0.01;
         double var26 = (double)var7 * var16 + var3.nextGaussian() * 0.01;
         double var28 = (double)var8 * var16 + var3.nextGaussian() * 0.01;
         this.addParticle(var4, var18, var20, var22, var24, var26, var28);
      }

   }

   public static class TransparencyShaderException extends RuntimeException {
      public TransparencyShaderException(String var1, Throwable var2) {
         super(var1, var2);
      }
   }
}
