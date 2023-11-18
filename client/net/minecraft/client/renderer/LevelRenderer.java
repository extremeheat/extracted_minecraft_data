package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
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
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.level.ChunkMap;
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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
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
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;
import org.slf4j.Logger;

public class LevelRenderer implements ResourceManagerReloadListener, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int CHUNK_SIZE = 16;
   private static final int HALF_CHUNK_SIZE = 8;
   private static final float SKY_DISC_RADIUS = 512.0F;
   private static final int MINIMUM_ADVANCED_CULLING_DISTANCE = 60;
   private static final double CEILED_SECTION_DIAGONAL = Math.ceil(Math.sqrt(3.0) * 16.0);
   private static final int MIN_FOG_DISTANCE = 32;
   private static final int RAIN_RADIUS = 10;
   private static final int RAIN_DIAMETER = 21;
   private static final int TRANSPARENT_SORT_COUNT = 15;
   private static final int HALF_A_SECOND_IN_MILLIS = 500;
   private static final ResourceLocation MOON_LOCATION = new ResourceLocation("textures/environment/moon_phases.png");
   private static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation CLOUDS_LOCATION = new ResourceLocation("textures/environment/clouds.png");
   private static final ResourceLocation END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation FORCEFIELD_LOCATION = new ResourceLocation("textures/misc/forcefield.png");
   private static final ResourceLocation RAIN_LOCATION = new ResourceLocation("textures/environment/rain.png");
   private static final ResourceLocation SNOW_LOCATION = new ResourceLocation("textures/environment/snow.png");
   public static final Direction[] DIRECTIONS = Direction.values();
   private final Minecraft minecraft;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
   private final RenderBuffers renderBuffers;
   @Nullable
   private ClientLevel level;
   private final BlockingQueue<ChunkRenderDispatcher.RenderChunk> recentlyCompiledChunks = new LinkedBlockingQueue<>();
   private final AtomicReference<LevelRenderer.RenderChunkStorage> renderChunkStorage = new AtomicReference<>();
   private final ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum = new ObjectArrayList(10000);
   private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
   @Nullable
   private Future<?> lastFullRenderChunkUpdate;
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
   private final Map<BlockPos, SoundInstance> playingRecords = Maps.newHashMap();
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
   private double lastCameraX = 4.9E-324;
   private double lastCameraY = 4.9E-324;
   private double lastCameraZ = 4.9E-324;
   private int lastCameraChunkX = -2147483648;
   private int lastCameraChunkY = -2147483648;
   private int lastCameraChunkZ = -2147483648;
   private double prevCamX = 4.9E-324;
   private double prevCamY = 4.9E-324;
   private double prevCamZ = 4.9E-324;
   private double prevCamRotX = 4.9E-324;
   private double prevCamRotY = 4.9E-324;
   private int prevCloudX = -2147483648;
   private int prevCloudY = -2147483648;
   private int prevCloudZ = -2147483648;
   private Vec3 prevCloudColor = Vec3.ZERO;
   @Nullable
   private CloudStatus prevCloudsType;
   @Nullable
   private ChunkRenderDispatcher chunkRenderDispatcher;
   private int lastViewDistance = -1;
   private int renderedEntities;
   private int culledEntities;
   private Frustum cullingFrustum;
   private boolean captureFrustum;
   @Nullable
   private Frustum capturedFrustum;
   private final Vector4f[] frustumPoints = new Vector4f[8];
   private final Vector3d frustumPos = new Vector3d(0.0, 0.0, 0.0);
   private double xTransparentOld;
   private double yTransparentOld;
   private double zTransparentOld;
   private boolean needsFullRenderChunkUpdate = true;
   private final AtomicLong nextFullUpdateMillis = new AtomicLong(0L);
   private final AtomicBoolean needsFrustumUpdate = new AtomicBoolean(false);
   private int rainSoundTime;
   private final float[] rainSizeX = new float[1024];
   private final float[] rainSizeZ = new float[1024];

   public LevelRenderer(Minecraft var1, EntityRenderDispatcher var2, BlockEntityRenderDispatcher var3, RenderBuffers var4) {
      super();
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
         BufferBuilder var15 = var14.getBuilder();
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
               Biome var27 = var10.getBiome(var19).value();
               if (var27.hasPrecipitation()) {
                  int var28 = var10.getHeight(Heightmap.Types.MOTION_BLOCKING, var21, var20);
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
                     if (var33 == Biome.Precipitation.RAIN) {
                        if (var17 != 0) {
                           if (var17 >= 0) {
                              var14.end();
                           }

                           var17 = 0;
                           RenderSystem.setShaderTexture(0, RAIN_LOCATION);
                           var15.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                        }

                        int var34 = this.ticks + var21 * var21 * 3121 + var21 * 45238971 + var20 * var20 * 418711 + var20 * 13761 & 31;
                        float var35 = -((float)var34 + var2) / 32.0F * (3.0F + var32.nextFloat());
                        double var36 = (double)var21 + 0.5 - var3;
                        double var38 = (double)var20 + 0.5 - var7;
                        float var40 = (float)Math.sqrt(var36 * var36 + var38 * var38) / (float)var16;
                        float var41 = ((1.0F - var40 * var40) * 0.5F + 0.5F) * var9;
                        var19.set(var21, var31, var20);
                        int var42 = getLightColor(var10, var19);
                        var15.vertex((double)var21 - var3 - var23 + 0.5, (double)var30 - var5, (double)var20 - var7 - var25 + 0.5)
                           .uv(0.0F, (float)var29 * 0.25F + var35)
                           .color(1.0F, 1.0F, 1.0F, var41)
                           .uv2(var42)
                           .endVertex();
                        var15.vertex((double)var21 - var3 + var23 + 0.5, (double)var30 - var5, (double)var20 - var7 + var25 + 0.5)
                           .uv(1.0F, (float)var29 * 0.25F + var35)
                           .color(1.0F, 1.0F, 1.0F, var41)
                           .uv2(var42)
                           .endVertex();
                        var15.vertex((double)var21 - var3 + var23 + 0.5, (double)var29 - var5, (double)var20 - var7 + var25 + 0.5)
                           .uv(1.0F, (float)var30 * 0.25F + var35)
                           .color(1.0F, 1.0F, 1.0F, var41)
                           .uv2(var42)
                           .endVertex();
                        var15.vertex((double)var21 - var3 - var23 + 0.5, (double)var29 - var5, (double)var20 - var7 - var25 + 0.5)
                           .uv(0.0F, (float)var30 * 0.25F + var35)
                           .color(1.0F, 1.0F, 1.0F, var41)
                           .uv2(var42)
                           .endVertex();
                     } else if (var33 == Biome.Precipitation.SNOW) {
                        if (var17 != 1) {
                           if (var17 >= 0) {
                              var14.end();
                           }

                           var17 = 1;
                           RenderSystem.setShaderTexture(0, SNOW_LOCATION);
                           var15.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                        }

                        float var48 = -((float)(this.ticks & 511) + var2) / 512.0F;
                        float var49 = (float)(var32.nextDouble() + (double)var18 * 0.01 * (double)((float)var32.nextGaussian()));
                        float var50 = (float)(var32.nextDouble() + (double)(var18 * (float)var32.nextGaussian()) * 0.001);
                        double var37 = (double)var21 + 0.5 - var3;
                        double var39 = (double)var20 + 0.5 - var7;
                        float var51 = (float)Math.sqrt(var37 * var37 + var39 * var39) / (float)var16;
                        float var52 = ((1.0F - var51 * var51) * 0.3F + 0.5F) * var9;
                        var19.set(var21, var31, var20);
                        int var43 = getLightColor(var10, var19);
                        int var44 = var43 >> 16 & 65535;
                        int var45 = var43 & 65535;
                        int var46 = (var44 * 3 + 240) / 4;
                        int var47 = (var45 * 3 + 240) / 4;
                        var15.vertex((double)var21 - var3 - var23 + 0.5, (double)var30 - var5, (double)var20 - var7 - var25 + 0.5)
                           .uv(0.0F + var49, (float)var29 * 0.25F + var48 + var50)
                           .color(1.0F, 1.0F, 1.0F, var52)
                           .uv2(var47, var46)
                           .endVertex();
                        var15.vertex((double)var21 - var3 + var23 + 0.5, (double)var30 - var5, (double)var20 - var7 + var25 + 0.5)
                           .uv(1.0F + var49, (float)var29 * 0.25F + var48 + var50)
                           .color(1.0F, 1.0F, 1.0F, var52)
                           .uv2(var47, var46)
                           .endVertex();
                        var15.vertex((double)var21 - var3 + var23 + 0.5, (double)var29 - var5, (double)var20 - var7 + var25 + 0.5)
                           .uv(1.0F + var49, (float)var30 * 0.25F + var48 + var50)
                           .color(1.0F, 1.0F, 1.0F, var52)
                           .uv2(var47, var46)
                           .endVertex();
                        var15.vertex((double)var21 - var3 - var23 + 0.5, (double)var29 - var5, (double)var20 - var7 - var25 + 0.5)
                           .uv(0.0F + var49, (float)var30 * 0.25F + var48 + var50)
                           .color(1.0F, 1.0F, 1.0F, var52)
                           .uv2(var47, var46)
                           .endVertex();
                     }
                  }
               }
            }
         }

         if (var17 >= 0) {
            var14.end();
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
               Biome var12 = var4.getBiome(var11).value();
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
                  SimpleParticleType var26 = !var18.is(FluidTags.LAVA) && !var17.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(var17)
                     ? ParticleTypes.RAIN
                     : ParticleTypes.SMOKE;
                  this.minecraft
                     .level
                     .addParticle(var26, (double)var6.getX() + var13, (double)var6.getY() + var24, (double)var6.getZ() + var15, 0.0, 0.0, 0.0);
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

   @Override
   public void close() {
      if (this.entityEffect != null) {
         this.entityEffect.close();
      }

      if (this.transparencyChain != null) {
         this.transparencyChain.close();
      }
   }

   @Override
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

      ResourceLocation var1 = new ResourceLocation("shaders/post/entity_outline.json");

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
      ResourceLocation var1 = new ResourceLocation("shaders/post/transparency.json");

      try {
         PostChain var2 = new PostChain(this.minecraft.getTextureManager(), this.minecraft.getResourceManager(), this.minecraft.getMainRenderTarget(), var1);
         var2.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         RenderTarget var9 = var2.getTempTarget("translucent");
         RenderTarget var10 = var2.getTempTarget("itemEntity");
         RenderTarget var11 = var2.getTempTarget("particles");
         RenderTarget var13 = var2.getTempTarget("weather");
         RenderTarget var7 = var2.getTempTarget("clouds");
         this.transparencyChain = var2;
         this.translucentTarget = var9;
         this.itemEntityTarget = var10;
         this.particlesTarget = var11;
         this.weatherTarget = var13;
         this.cloudsTarget = var7;
      } catch (Exception var8) {
         String var3 = var8 instanceof JsonSyntaxException ? "parse" : "load";
         String var4 = "Failed to " + var3 + " shader: " + var1;
         LevelRenderer.TransparencyShaderException var5 = new LevelRenderer.TransparencyShaderException(var4, var8);
         if (this.minecraft.getResourcePackRepository().getSelectedIds().size() > 1) {
            Component var6 = this.minecraft.getResourceManager().listPacks().findFirst().map(var0 -> Component.literal(var0.packId())).orElse(null);
            this.minecraft.options.graphicsMode().set(GraphicsStatus.FANCY);
            this.minecraft.clearResourcePacksOnError(var5, var6);
         } else {
            CrashReport var12 = this.minecraft.fillReport(new CrashReport(var4, var5));
            this.minecraft.options.graphicsMode().set(GraphicsStatus.FANCY);
            this.minecraft.options.save();
            LOGGER.error(LogUtils.FATAL_MARKER, var4, var5);
            this.minecraft.emergencySave();
            Minecraft.crash(var12);
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
         RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ZERO,
            GlStateManager.DestFactor.ONE
         );
         this.entityTarget.blitToScreen(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), false);
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      }
   }

   protected boolean shouldShowEntityOutlines() {
      return !this.minecraft.gameRenderer.isPanoramicMode() && this.entityTarget != null && this.entityEffect != null && this.minecraft.player != null;
   }

   private void createDarkSky() {
      Tesselator var1 = Tesselator.getInstance();
      BufferBuilder var2 = var1.getBuilder();
      if (this.darkBuffer != null) {
         this.darkBuffer.close();
      }

      this.darkBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
      BufferBuilder.RenderedBuffer var3 = buildSkyDisc(var2, -16.0F);
      this.darkBuffer.bind();
      this.darkBuffer.upload(var3);
      VertexBuffer.unbind();
   }

   private void createLightSky() {
      Tesselator var1 = Tesselator.getInstance();
      BufferBuilder var2 = var1.getBuilder();
      if (this.skyBuffer != null) {
         this.skyBuffer.close();
      }

      this.skyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
      BufferBuilder.RenderedBuffer var3 = buildSkyDisc(var2, 16.0F);
      this.skyBuffer.bind();
      this.skyBuffer.upload(var3);
      VertexBuffer.unbind();
   }

   private static BufferBuilder.RenderedBuffer buildSkyDisc(BufferBuilder var0, float var1) {
      float var2 = Math.signum(var1) * 512.0F;
      float var3 = 512.0F;
      RenderSystem.setShader(GameRenderer::getPositionShader);
      var0.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
      var0.vertex(0.0, (double)var1, 0.0).endVertex();

      for(int var4 = -180; var4 <= 180; var4 += 45) {
         var0.vertex((double)(var2 * Mth.cos((float)var4 * 0.017453292F)), (double)var1, (double)(512.0F * Mth.sin((float)var4 * 0.017453292F))).endVertex();
      }

      return var0.end();
   }

   private void createStars() {
      Tesselator var1 = Tesselator.getInstance();
      BufferBuilder var2 = var1.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionShader);
      if (this.starBuffer != null) {
         this.starBuffer.close();
      }

      this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
      BufferBuilder.RenderedBuffer var3 = this.drawStars(var2);
      this.starBuffer.bind();
      this.starBuffer.upload(var3);
      VertexBuffer.unbind();
   }

   private BufferBuilder.RenderedBuffer drawStars(BufferBuilder var1) {
      RandomSource var2 = RandomSource.create(10842L);
      var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

      for(int var3 = 0; var3 < 1500; ++var3) {
         double var4 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var8 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var10 = (double)(0.15F + var2.nextFloat() * 0.1F);
         double var12 = var4 * var4 + var6 * var6 + var8 * var8;
         if (var12 < 1.0 && var12 > 0.01) {
            var12 = 1.0 / Math.sqrt(var12);
            var4 *= var12;
            var6 *= var12;
            var8 *= var12;
            double var14 = var4 * 100.0;
            double var16 = var6 * 100.0;
            double var18 = var8 * 100.0;
            double var20 = Math.atan2(var4, var8);
            double var22 = Math.sin(var20);
            double var24 = Math.cos(var20);
            double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
            double var28 = Math.sin(var26);
            double var30 = Math.cos(var26);
            double var32 = var2.nextDouble() * 3.141592653589793 * 2.0;
            double var34 = Math.sin(var32);
            double var36 = Math.cos(var32);

            for(int var38 = 0; var38 < 4; ++var38) {
               double var39 = 0.0;
               double var41 = (double)((var38 & 2) - 1) * var10;
               double var43 = (double)((var38 + 1 & 2) - 1) * var10;
               double var45 = 0.0;
               double var47 = var41 * var36 - var43 * var34;
               double var49 = var43 * var36 + var41 * var34;
               double var53 = var47 * var28 + 0.0 * var30;
               double var55 = 0.0 * var28 - var47 * var30;
               double var57 = var55 * var22 - var49 * var24;
               double var61 = var49 * var22 + var55 * var24;
               var1.vertex(var14 + var57, var16 + var53, var18 + var61).endVertex();
            }
         }
      }

      return var1.end();
   }

   public void setLevel(@Nullable ClientLevel var1) {
      this.lastCameraX = 4.9E-324;
      this.lastCameraY = 4.9E-324;
      this.lastCameraZ = 4.9E-324;
      this.lastCameraChunkX = -2147483648;
      this.lastCameraChunkY = -2147483648;
      this.lastCameraChunkZ = -2147483648;
      this.entityRenderDispatcher.setLevel(var1);
      this.level = var1;
      if (var1 != null) {
         this.allChanged();
      } else {
         if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
            this.viewArea = null;
         }

         if (this.chunkRenderDispatcher != null) {
            this.chunkRenderDispatcher.dispose();
         }

         this.chunkRenderDispatcher = null;
         this.globalBlockEntities.clear();
         this.renderChunkStorage.set(null);
         this.renderChunksInFrustum.clear();
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
         if (this.chunkRenderDispatcher == null) {
            this.chunkRenderDispatcher = new ChunkRenderDispatcher(
               this.level, this, Util.backgroundExecutor(), this.minecraft.is64Bit(), this.renderBuffers.fixedBufferPack()
            );
         } else {
            this.chunkRenderDispatcher.setLevel(this.level);
         }

         this.needsFullRenderChunkUpdate = true;
         this.generateClouds = true;
         this.recentlyCompiledChunks.clear();
         ItemBlockRenderTypes.setFancy(Minecraft.useFancyGraphics());
         this.lastViewDistance = this.minecraft.options.getEffectiveRenderDistance();
         if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
         }

         this.chunkRenderDispatcher.blockUntilClear();
         synchronized(this.globalBlockEntities) {
            this.globalBlockEntities.clear();
         }

         this.viewArea = new ViewArea(this.chunkRenderDispatcher, this.level, this.minecraft.options.getEffectiveRenderDistance(), this);
         if (this.lastFullRenderChunkUpdate != null) {
            try {
               this.lastFullRenderChunkUpdate.get();
               this.lastFullRenderChunkUpdate = null;
            } catch (Exception var3) {
               LOGGER.warn("Full update failed", var3);
            }
         }

         this.renderChunkStorage.set(new LevelRenderer.RenderChunkStorage(this.viewArea.chunks.length));
         this.renderChunksInFrustum.clear();
         Entity var5 = this.minecraft.getCameraEntity();
         if (var5 != null) {
            this.viewArea.repositionCamera(var5.getX(), var5.getZ());
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

   public String getChunkStatistics() {
      int var1 = this.viewArea.chunks.length;
      int var2 = this.countRenderedChunks();
      return String.format(
         Locale.ROOT,
         "C: %d/%d %sD: %d, %s",
         var2,
         var1,
         this.minecraft.smartCull ? "(s) " : "",
         this.lastViewDistance,
         this.chunkRenderDispatcher == null ? "null" : this.chunkRenderDispatcher.getStats()
      );
   }

   public ChunkRenderDispatcher getChunkRenderDispatcher() {
      return this.chunkRenderDispatcher;
   }

   public double getTotalChunks() {
      return (double)this.viewArea.chunks.length;
   }

   public double getLastViewDistance() {
      return (double)this.lastViewDistance;
   }

   public int countRenderedChunks() {
      int var1 = 0;
      ObjectListIterator var2 = this.renderChunksInFrustum.iterator();

      while(var2.hasNext()) {
         LevelRenderer.RenderChunkInfo var3 = (LevelRenderer.RenderChunkInfo)var2.next();
         if (!var3.chunk.getCompiledChunk().hasNoRenderableLayers()) {
            ++var1;
         }
      }

      return var1;
   }

   public String getEntityStatistics() {
      return "E: "
         + this.renderedEntities
         + "/"
         + this.level.getEntityCount()
         + ", B: "
         + this.culledEntities
         + ", SD: "
         + this.level.getServerSimulationDistance();
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
      if (this.lastCameraChunkX != var12 || this.lastCameraChunkY != var13 || this.lastCameraChunkZ != var14) {
         this.lastCameraX = var6;
         this.lastCameraY = var8;
         this.lastCameraZ = var10;
         this.lastCameraChunkX = var12;
         this.lastCameraChunkY = var13;
         this.lastCameraChunkZ = var14;
         this.viewArea.repositionCamera(var6, var10);
      }

      this.chunkRenderDispatcher.setCamera(var5);
      this.level.getProfiler().popPush("cull");
      this.minecraft.getProfiler().popPush("culling");
      BlockPos var15 = var1.getBlockPosition();
      double var16 = Math.floor(var5.x / 8.0);
      double var18 = Math.floor(var5.y / 8.0);
      double var20 = Math.floor(var5.z / 8.0);
      this.needsFullRenderChunkUpdate = this.needsFullRenderChunkUpdate || var16 != this.prevCamX || var18 != this.prevCamY || var20 != this.prevCamZ;
      this.nextFullUpdateMillis.updateAndGet(var1x -> {
         if (var1x > 0L && System.currentTimeMillis() > var1x) {
            this.needsFullRenderChunkUpdate = true;
            return 0L;
         } else {
            return var1x;
         }
      });
      this.prevCamX = var16;
      this.prevCamY = var18;
      this.prevCamZ = var20;
      this.minecraft.getProfiler().popPush("update");
      boolean var22 = this.minecraft.smartCull;
      if (var4 && this.level.getBlockState(var15).isSolidRender(this.level, var15)) {
         var22 = false;
      }

      if (!var3) {
         if (this.needsFullRenderChunkUpdate && (this.lastFullRenderChunkUpdate == null || this.lastFullRenderChunkUpdate.isDone())) {
            this.minecraft.getProfiler().push("full_update_schedule");
            this.needsFullRenderChunkUpdate = false;
            boolean var23 = var22;
            this.lastFullRenderChunkUpdate = Util.backgroundExecutor().submit(() -> {
               ArrayDeque var4x = Queues.newArrayDeque();
               this.initializeQueueForFullUpdate(var1, var4x);
               LevelRenderer.RenderChunkStorage var5x = new LevelRenderer.RenderChunkStorage(this.viewArea.chunks.length);
               this.updateRenderChunks(var5x.renderChunks, var5x.renderInfoMap, var5, var4x, var23);
               this.renderChunkStorage.set(var5x);
               this.needsFrustumUpdate.set(true);
            });
            this.minecraft.getProfiler().pop();
         }

         LevelRenderer.RenderChunkStorage var28 = this.renderChunkStorage.get();
         if (!this.recentlyCompiledChunks.isEmpty()) {
            this.minecraft.getProfiler().push("partial_update");
            ArrayDeque var24 = Queues.newArrayDeque();

            while(!this.recentlyCompiledChunks.isEmpty()) {
               ChunkRenderDispatcher.RenderChunk var25 = this.recentlyCompiledChunks.poll();
               LevelRenderer.RenderChunkInfo var26 = var28.renderInfoMap.get(var25);
               if (var26 != null && var26.chunk == var25) {
                  var24.add(var26);
               }
            }

            this.updateRenderChunks(var28.renderChunks, var28.renderInfoMap, var5, var24, var22);
            this.needsFrustumUpdate.set(true);
            this.minecraft.getProfiler().pop();
         }

         double var29 = Math.floor((double)(var1.getXRot() / 2.0F));
         double var30 = Math.floor((double)(var1.getYRot() / 2.0F));
         if (this.needsFrustumUpdate.compareAndSet(true, false) || var29 != this.prevCamRotX || var30 != this.prevCamRotY) {
            this.applyFrustum(new Frustum(var2).offsetToFullyIncludeCameraCube(8));
            this.prevCamRotX = var29;
            this.prevCamRotY = var30;
         }
      }

      this.minecraft.getProfiler().pop();
   }

   private void applyFrustum(Frustum var1) {
      if (!Minecraft.getInstance().isSameThread()) {
         throw new IllegalStateException("applyFrustum called from wrong thread: " + Thread.currentThread().getName());
      } else {
         this.minecraft.getProfiler().push("apply_frustum");
         this.renderChunksInFrustum.clear();

         for(LevelRenderer.RenderChunkInfo var3 : this.renderChunkStorage.get().renderChunks) {
            if (var1.isVisible(var3.chunk.getBoundingBox())) {
               this.renderChunksInFrustum.add(var3);
            }
         }

         this.minecraft.getProfiler().pop();
      }
   }

   private void initializeQueueForFullUpdate(Camera var1, Queue<LevelRenderer.RenderChunkInfo> var2) {
      boolean var3 = true;
      Vec3 var4 = var1.getPosition();
      BlockPos var5 = var1.getBlockPosition();
      ChunkRenderDispatcher.RenderChunk var6 = this.viewArea.getRenderChunkAt(var5);
      if (var6 == null) {
         boolean var7 = var5.getY() > this.level.getMinBuildHeight();
         int var8 = var7 ? this.level.getMaxBuildHeight() - 8 : this.level.getMinBuildHeight() + 8;
         int var9 = Mth.floor(var4.x / 16.0) * 16;
         int var10 = Mth.floor(var4.z / 16.0) * 16;
         ArrayList var11 = Lists.newArrayList();

         for(int var12 = -this.lastViewDistance; var12 <= this.lastViewDistance; ++var12) {
            for(int var13 = -this.lastViewDistance; var13 <= this.lastViewDistance; ++var13) {
               ChunkRenderDispatcher.RenderChunk var14 = this.viewArea
                  .getRenderChunkAt(new BlockPos(var9 + SectionPos.sectionToBlockCoord(var12, 8), var8, var10 + SectionPos.sectionToBlockCoord(var13, 8)));
               if (var14 != null) {
                  var11.add(new LevelRenderer.RenderChunkInfo(var14, null, 0));
               }
            }
         }

         var11.sort(Comparator.comparingDouble(var1x -> var5.distSqr(var1x.chunk.getOrigin().offset(8, 8, 8))));
         var2.addAll(var11);
      } else {
         var2.add(new LevelRenderer.RenderChunkInfo(var6, null, 0));
      }
   }

   public void addRecentlyCompiledChunk(ChunkRenderDispatcher.RenderChunk var1) {
      this.recentlyCompiledChunks.add(var1);
   }

   private void updateRenderChunks(
      LinkedHashSet<LevelRenderer.RenderChunkInfo> var1, LevelRenderer.RenderInfoMap var2, Vec3 var3, Queue<LevelRenderer.RenderChunkInfo> var4, boolean var5
   ) {
      boolean var6 = true;
      BlockPos var7 = new BlockPos(Mth.floor(var3.x / 16.0) * 16, Mth.floor(var3.y / 16.0) * 16, Mth.floor(var3.z / 16.0) * 16);
      BlockPos var8 = var7.offset(8, 8, 8);
      Entity.setViewScale(
         Mth.clamp((double)this.minecraft.options.getEffectiveRenderDistance() / 8.0, 1.0, 2.5) * this.minecraft.options.entityDistanceScaling().get()
      );

      while(!var4.isEmpty()) {
         LevelRenderer.RenderChunkInfo var9 = (LevelRenderer.RenderChunkInfo)var4.poll();
         ChunkRenderDispatcher.RenderChunk var10 = var9.chunk;
         var1.add(var9);
         boolean var11 = Math.abs(var10.getOrigin().getX() - var7.getX()) > 60
            || Math.abs(var10.getOrigin().getY() - var7.getY()) > 60
            || Math.abs(var10.getOrigin().getZ() - var7.getZ()) > 60;

         for(Direction var15 : DIRECTIONS) {
            ChunkRenderDispatcher.RenderChunk var16 = this.getRelativeFrom(var7, var10, var15);
            if (var16 != null && (!var5 || !var9.hasDirection(var15.getOpposite()))) {
               if (var5 && var9.hasSourceDirections()) {
                  ChunkRenderDispatcher.CompiledChunk var17 = var10.getCompiledChunk();
                  boolean var18 = false;

                  for(int var19 = 0; var19 < DIRECTIONS.length; ++var19) {
                     if (var9.hasSourceDirection(var19) && var17.facesCanSeeEachother(DIRECTIONS[var19].getOpposite(), var15)) {
                        var18 = true;
                        break;
                     }
                  }

                  if (!var18) {
                     continue;
                  }
               }

               if (var5 && var11) {
                  BlockPos var23 = var16.getOrigin();
                  BlockPos var25 = var23.offset(
                     (var15.getAxis() == Direction.Axis.X ? var8.getX() <= var23.getX() : var8.getX() >= var23.getX()) ? 0 : 16,
                     (var15.getAxis() == Direction.Axis.Y ? var8.getY() <= var23.getY() : var8.getY() >= var23.getY()) ? 0 : 16,
                     (var15.getAxis() == Direction.Axis.Z ? var8.getZ() <= var23.getZ() : var8.getZ() >= var23.getZ()) ? 0 : 16
                  );
                  Vec3 var27 = new Vec3((double)var25.getX(), (double)var25.getY(), (double)var25.getZ());
                  Vec3 var20 = var3.subtract(var27).normalize().scale(CEILED_SECTION_DIAGONAL);
                  boolean var21 = true;

                  while(var3.subtract(var27).lengthSqr() > 3600.0) {
                     var27 = var27.add(var20);
                     if (var27.y > (double)this.level.getMaxBuildHeight() || var27.y < (double)this.level.getMinBuildHeight()) {
                        break;
                     }

                     ChunkRenderDispatcher.RenderChunk var22 = this.viewArea.getRenderChunkAt(BlockPos.containing(var27.x, var27.y, var27.z));
                     if (var22 == null || var2.get(var22) == null) {
                        var21 = false;
                        break;
                     }
                  }

                  if (!var21) {
                     continue;
                  }
               }

               LevelRenderer.RenderChunkInfo var24 = var2.get(var16);
               if (var24 != null) {
                  var24.addSourceDirection(var15);
               } else if (!var16.hasAllNeighbors()) {
                  if (!this.closeToBorder(var7, var10)) {
                     this.nextFullUpdateMillis.set(System.currentTimeMillis() + 500L);
                  }
               } else {
                  LevelRenderer.RenderChunkInfo var26 = new LevelRenderer.RenderChunkInfo(var16, var15, var9.step + 1);
                  var26.setDirections(var9.directions, var15);
                  var4.add(var26);
                  var2.put(var16, var26);
               }
            }
         }
      }
   }

   @Nullable
   private ChunkRenderDispatcher.RenderChunk getRelativeFrom(BlockPos var1, ChunkRenderDispatcher.RenderChunk var2, Direction var3) {
      BlockPos var4 = var2.getRelativeOrigin(var3);
      if (Mth.abs(var1.getX() - var4.getX()) > this.lastViewDistance * 16) {
         return null;
      } else if (Mth.abs(var1.getY() - var4.getY()) > this.lastViewDistance * 16
         || var4.getY() < this.level.getMinBuildHeight()
         || var4.getY() >= this.level.getMaxBuildHeight()) {
         return null;
      } else {
         return Mth.abs(var1.getZ() - var4.getZ()) > this.lastViewDistance * 16 ? null : this.viewArea.getRenderChunkAt(var4);
      }
   }

   private boolean closeToBorder(BlockPos var1, ChunkRenderDispatcher.RenderChunk var2) {
      int var3 = SectionPos.blockToSectionCoord(var1.getX());
      int var4 = SectionPos.blockToSectionCoord(var1.getZ());
      BlockPos var5 = var2.getOrigin();
      int var6 = SectionPos.blockToSectionCoord(var5.getX());
      int var7 = SectionPos.blockToSectionCoord(var5.getZ());
      return !ChunkMap.isChunkInRange(var6, var7, var3, var4, this.lastViewDistance - 3);
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

   public void prepareCullFrustum(PoseStack var1, Vec3 var2, Matrix4f var3) {
      Matrix4f var4 = var1.last().pose();
      double var5 = var2.x();
      double var7 = var2.y();
      double var9 = var2.z();
      this.cullingFrustum = new Frustum(var4, var3);
      this.cullingFrustum.prepare(var5, var7, var9);
   }

   public void renderLevel(PoseStack var1, float var2, long var3, boolean var5, Camera var6, GameRenderer var7, LightTexture var8, Matrix4f var9) {
      RenderSystem.setShaderGameTime(this.level.getGameTime(), var2);
      this.blockEntityRenderDispatcher.prepare(this.level, var6, this.minecraft.hitResult);
      this.entityRenderDispatcher.prepare(this.level, var6, this.minecraft.crosshairPickEntity);
      ProfilerFiller var10 = this.level.getProfiler();
      var10.popPush("light_update_queue");
      this.level.pollLightUpdates();
      var10.popPush("light_updates");
      this.level.getChunkSource().getLightEngine().runLightUpdates();
      Vec3 var11 = var6.getPosition();
      double var12 = var11.x();
      double var14 = var11.y();
      double var16 = var11.z();
      Matrix4f var18 = var1.last().pose();
      var10.popPush("culling");
      boolean var19 = this.capturedFrustum != null;
      Frustum var20;
      if (var19) {
         var20 = this.capturedFrustum;
         var20.prepare(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z);
      } else {
         var20 = this.cullingFrustum;
      }

      this.minecraft.getProfiler().popPush("captureFrustum");
      if (this.captureFrustum) {
         this.captureFrustum(var18, var9, var11.x, var11.y, var11.z, var19 ? new Frustum(var18, var9) : var20);
         this.captureFrustum = false;
      }

      var10.popPush("clear");
      FogRenderer.setupColor(var6, var2, this.minecraft.level, this.minecraft.options.getEffectiveRenderDistance(), var7.getDarkenWorldAmount(var2));
      FogRenderer.levelFogColor();
      RenderSystem.clear(16640, Minecraft.ON_OSX);
      float var21 = var7.getRenderDistance();
      boolean var22 = this.minecraft.level.effects().isFoggyAt(Mth.floor(var12), Mth.floor(var14))
         || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
      var10.popPush("sky");
      RenderSystem.setShader(GameRenderer::getPositionShader);
      this.renderSky(var1, var9, var2, var6, var22, () -> FogRenderer.setupFog(var6, FogRenderer.FogMode.FOG_SKY, var21, var22, var2));
      var10.popPush("fog");
      FogRenderer.setupFog(var6, FogRenderer.FogMode.FOG_TERRAIN, Math.max(var21, 32.0F), var22, var2);
      var10.popPush("terrain_setup");
      this.setupRender(var6, var20, var19, this.minecraft.player.isSpectator());
      var10.popPush("compilechunks");
      this.compileChunks(var6);
      var10.popPush("terrain");
      this.renderChunkLayer(RenderType.solid(), var1, var12, var14, var16, var9);
      this.renderChunkLayer(RenderType.cutoutMipped(), var1, var12, var14, var16, var9);
      this.renderChunkLayer(RenderType.cutout(), var1, var12, var14, var16, var9);
      if (this.level.effects().constantAmbientLight()) {
         Lighting.setupNetherLevel(var1.last().pose());
      } else {
         Lighting.setupLevel(var1.last().pose());
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

      boolean var23 = false;
      MultiBufferSource.BufferSource var24 = this.renderBuffers.bufferSource();

      for(Entity var26 : this.level.entitiesForRendering()) {
         if (this.entityRenderDispatcher.shouldRender(var26, var20, var12, var14, var16) || var26.hasIndirectPassenger(this.minecraft.player)) {
            BlockPos var27 = var26.blockPosition();
            if ((this.level.isOutsideBuildHeight(var27.getY()) || this.isChunkCompiled(var27))
               && (var26 != var6.getEntity() || var6.isDetached() || var6.getEntity() instanceof LivingEntity && ((LivingEntity)var6.getEntity()).isSleeping())
               && (!(var26 instanceof LocalPlayer) || var6.getEntity() == var26)) {
               ++this.renderedEntities;
               if (var26.tickCount == 0) {
                  var26.xOld = var26.getX();
                  var26.yOld = var26.getY();
                  var26.zOld = var26.getZ();
               }

               Object var28;
               if (this.shouldShowEntityOutlines() && this.minecraft.shouldEntityAppearGlowing(var26)) {
                  var23 = true;
                  OutlineBufferSource var29 = this.renderBuffers.outlineBufferSource();
                  var28 = var29;
                  int var30 = var26.getTeamColor();
                  var29.setColor(FastColor.ARGB32.red(var30), FastColor.ARGB32.green(var30), FastColor.ARGB32.blue(var30), 255);
               } else {
                  var28 = var24;
               }

               this.renderEntity(var26, var12, var14, var16, var2, var1, (MultiBufferSource)var28);
            }
         }
      }

      var24.endLastBatch();
      this.checkPoseStack(var1);
      var24.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
      var24.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
      var24.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
      var24.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));
      var10.popPush("blockentities");
      ObjectListIterator var39 = this.renderChunksInFrustum.iterator();

      while(var39.hasNext()) {
         LevelRenderer.RenderChunkInfo var43 = (LevelRenderer.RenderChunkInfo)var39.next();
         List var48 = var43.chunk.getCompiledChunk().getRenderableBlockEntities();
         if (!var48.isEmpty()) {
            for(BlockEntity var56 : var48) {
               BlockPos var57 = var56.getBlockPos();
               Object var31 = var24;
               var1.pushPose();
               var1.translate((double)var57.getX() - var12, (double)var57.getY() - var14, (double)var57.getZ() - var16);
               SortedSet var32 = (SortedSet)this.destructionProgress.get(var57.asLong());
               if (var32 != null && !var32.isEmpty()) {
                  int var33 = ((BlockDestructionProgress)var32.last()).getProgress();
                  if (var33 >= 0) {
                     PoseStack.Pose var34 = var1.last();
                     SheetedDecalTextureGenerator var35 = new SheetedDecalTextureGenerator(
                        this.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(var33)), var34.pose(), var34.normal(), 1.0F
                     );
                     var31 = var2x -> {
                        VertexConsumer var3x = var24.getBuffer(var2x);
                        return var2x.affectsCrumbling() ? VertexMultiConsumer.create(var35, var3x) : var3x;
                     };
                  }
               }

               this.blockEntityRenderDispatcher.render(var56, var2, var1, (MultiBufferSource)var31);
               var1.popPose();
            }
         }
      }

      synchronized(this.globalBlockEntities) {
         for(BlockEntity var49 : this.globalBlockEntities) {
            BlockPos var53 = var49.getBlockPos();
            var1.pushPose();
            var1.translate((double)var53.getX() - var12, (double)var53.getY() - var14, (double)var53.getZ() - var16);
            this.blockEntityRenderDispatcher.render(var49, var2, var1, var24);
            var1.popPose();
         }
      }

      this.checkPoseStack(var1);
      var24.endBatch(RenderType.solid());
      var24.endBatch(RenderType.endPortal());
      var24.endBatch(RenderType.endGateway());
      var24.endBatch(Sheets.solidBlockSheet());
      var24.endBatch(Sheets.cutoutBlockSheet());
      var24.endBatch(Sheets.bedSheet());
      var24.endBatch(Sheets.shulkerBoxSheet());
      var24.endBatch(Sheets.signSheet());
      var24.endBatch(Sheets.hangingSignSheet());
      var24.endBatch(Sheets.chestSheet());
      this.renderBuffers.outlineBufferSource().endOutlineBatch();
      if (var23) {
         this.entityEffect.process(var2);
         this.minecraft.getMainRenderTarget().bindWrite(false);
      }

      var10.popPush("destroyProgress");
      ObjectIterator var41 = this.destructionProgress.long2ObjectEntrySet().iterator();

      while(var41.hasNext()) {
         Entry var45 = (Entry)var41.next();
         BlockPos var50 = BlockPos.of(var45.getLongKey());
         double var54 = (double)var50.getX() - var12;
         double var58 = (double)var50.getY() - var14;
         double var59 = (double)var50.getZ() - var16;
         if (!(var54 * var54 + var58 * var58 + var59 * var59 > 1024.0)) {
            SortedSet var60 = (SortedSet)var45.getValue();
            if (var60 != null && !var60.isEmpty()) {
               int var61 = ((BlockDestructionProgress)var60.last()).getProgress();
               var1.pushPose();
               var1.translate((double)var50.getX() - var12, (double)var50.getY() - var14, (double)var50.getZ() - var16);
               PoseStack.Pose var36 = var1.last();
               SheetedDecalTextureGenerator var37 = new SheetedDecalTextureGenerator(
                  this.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(var61)), var36.pose(), var36.normal(), 1.0F
               );
               this.minecraft.getBlockRenderer().renderBreakingTexture(this.level.getBlockState(var50), var50, this.level, var1, var37);
               var1.popPose();
            }
         }
      }

      this.checkPoseStack(var1);
      HitResult var42 = this.minecraft.hitResult;
      if (var5 && var42 != null && var42.getType() == HitResult.Type.BLOCK) {
         var10.popPush("outline");
         BlockPos var46 = ((BlockHitResult)var42).getBlockPos();
         BlockState var51 = this.level.getBlockState(var46);
         if (!var51.isAir() && this.level.getWorldBorder().isWithinBounds(var46)) {
            VertexConsumer var55 = var24.getBuffer(RenderType.lines());
            this.renderHitOutline(var1, var55, var6.getEntity(), var12, var14, var16, var46, var51);
         }
      }

      this.minecraft.debugRenderer.render(var1, var24, var12, var14, var16);
      var24.endLastBatch();
      PoseStack var47 = RenderSystem.getModelViewStack();
      RenderSystem.applyModelViewMatrix();
      var24.endBatch(Sheets.translucentCullBlockSheet());
      var24.endBatch(Sheets.bannerSheet());
      var24.endBatch(Sheets.shieldSheet());
      var24.endBatch(RenderType.armorGlint());
      var24.endBatch(RenderType.armorEntityGlint());
      var24.endBatch(RenderType.glint());
      var24.endBatch(RenderType.glintDirect());
      var24.endBatch(RenderType.glintTranslucent());
      var24.endBatch(RenderType.entityGlint());
      var24.endBatch(RenderType.entityGlintDirect());
      var24.endBatch(RenderType.waterMask());
      this.renderBuffers.crumblingBufferSource().endBatch();
      if (this.transparencyChain != null) {
         var24.endBatch(RenderType.lines());
         var24.endBatch();
         this.translucentTarget.clear(Minecraft.ON_OSX);
         this.translucentTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
         var10.popPush("translucent");
         this.renderChunkLayer(RenderType.translucent(), var1, var12, var14, var16, var9);
         var10.popPush("string");
         this.renderChunkLayer(RenderType.tripwire(), var1, var12, var14, var16, var9);
         this.particlesTarget.clear(Minecraft.ON_OSX);
         this.particlesTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
         RenderStateShard.PARTICLES_TARGET.setupRenderState();
         var10.popPush("particles");
         this.minecraft.particleEngine.render(var1, var24, var8, var6, var2);
         RenderStateShard.PARTICLES_TARGET.clearRenderState();
      } else {
         var10.popPush("translucent");
         if (this.translucentTarget != null) {
            this.translucentTarget.clear(Minecraft.ON_OSX);
         }

         this.renderChunkLayer(RenderType.translucent(), var1, var12, var14, var16, var9);
         var24.endBatch(RenderType.lines());
         var24.endBatch();
         var10.popPush("string");
         this.renderChunkLayer(RenderType.tripwire(), var1, var12, var14, var16, var9);
         var10.popPush("particles");
         this.minecraft.particleEngine.render(var1, var24, var8, var6, var2);
      }

      var47.pushPose();
      var47.mulPoseMatrix(var1.last().pose());
      RenderSystem.applyModelViewMatrix();
      if (this.minecraft.options.getCloudsType() != CloudStatus.OFF) {
         if (this.transparencyChain != null) {
            this.cloudsTarget.clear(Minecraft.ON_OSX);
            RenderStateShard.CLOUDS_TARGET.setupRenderState();
            var10.popPush("clouds");
            this.renderClouds(var1, var9, var2, var12, var14, var16);
            RenderStateShard.CLOUDS_TARGET.clearRenderState();
         } else {
            var10.popPush("clouds");
            RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
            this.renderClouds(var1, var9, var2, var12, var14, var16);
         }
      }

      if (this.transparencyChain != null) {
         RenderStateShard.WEATHER_TARGET.setupRenderState();
         var10.popPush("weather");
         this.renderSnowAndRain(var8, var2, var12, var14, var16);
         this.renderWorldBorder(var6);
         RenderStateShard.WEATHER_TARGET.clearRenderState();
         this.transparencyChain.process(var2);
         this.minecraft.getMainRenderTarget().bindWrite(false);
      } else {
         RenderSystem.depthMask(false);
         var10.popPush("weather");
         this.renderSnowAndRain(var8, var2, var12, var14, var16);
         this.renderWorldBorder(var6);
         RenderSystem.depthMask(true);
      }

      var47.popPose();
      RenderSystem.applyModelViewMatrix();
      this.renderDebug(var1, var24, var6);
      var24.endLastBatch();
      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
      FogRenderer.setupNoFog();
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
      this.entityRenderDispatcher
         .render(var1, var11 - var2, var13 - var4, var15 - var6, var17, var8, var9, var10, this.entityRenderDispatcher.getPackedLightCoords(var1, var8));
   }

   private void renderChunkLayer(RenderType var1, PoseStack var2, double var3, double var5, double var7, Matrix4f var9) {
      RenderSystem.assertOnRenderThread();
      var1.setupRenderState();
      if (var1 == RenderType.translucent()) {
         this.minecraft.getProfiler().push("translucent_sort");
         double var10 = var3 - this.xTransparentOld;
         double var12 = var5 - this.yTransparentOld;
         double var14 = var7 - this.zTransparentOld;
         if (var10 * var10 + var12 * var12 + var14 * var14 > 1.0) {
            int var16 = SectionPos.posToSectionCoord(var3);
            int var17 = SectionPos.posToSectionCoord(var5);
            int var18 = SectionPos.posToSectionCoord(var7);
            boolean var19 = var16 != SectionPos.posToSectionCoord(this.xTransparentOld)
               || var18 != SectionPos.posToSectionCoord(this.zTransparentOld)
               || var17 != SectionPos.posToSectionCoord(this.yTransparentOld);
            this.xTransparentOld = var3;
            this.yTransparentOld = var5;
            this.zTransparentOld = var7;
            int var20 = 0;
            ObjectListIterator var21 = this.renderChunksInFrustum.iterator();

            while(var21.hasNext()) {
               LevelRenderer.RenderChunkInfo var22 = (LevelRenderer.RenderChunkInfo)var21.next();
               if (var20 < 15 && (var19 || var22.isAxisAlignedWith(var16, var17, var18)) && var22.chunk.resortTransparency(var1, this.chunkRenderDispatcher)) {
                  ++var20;
               }
            }
         }

         this.minecraft.getProfiler().pop();
      }

      this.minecraft.getProfiler().push("filterempty");
      this.minecraft.getProfiler().popPush(() -> "render_" + var1);
      boolean var23 = var1 != RenderType.translucent();
      ObjectListIterator var11 = this.renderChunksInFrustum.listIterator(var23 ? 0 : this.renderChunksInFrustum.size());
      ShaderInstance var24 = RenderSystem.getShader();

      for(int var13 = 0; var13 < 12; ++var13) {
         int var26 = RenderSystem.getShaderTexture(var13);
         var24.setSampler("Sampler" + var13, var26);
      }

      if (var24.MODEL_VIEW_MATRIX != null) {
         var24.MODEL_VIEW_MATRIX.set(var2.last().pose());
      }

      if (var24.PROJECTION_MATRIX != null) {
         var24.PROJECTION_MATRIX.set(var9);
      }

      if (var24.COLOR_MODULATOR != null) {
         var24.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
      }

      if (var24.GLINT_ALPHA != null) {
         var24.GLINT_ALPHA.set(RenderSystem.getShaderGlintAlpha());
      }

      if (var24.FOG_START != null) {
         var24.FOG_START.set(RenderSystem.getShaderFogStart());
      }

      if (var24.FOG_END != null) {
         var24.FOG_END.set(RenderSystem.getShaderFogEnd());
      }

      if (var24.FOG_COLOR != null) {
         var24.FOG_COLOR.set(RenderSystem.getShaderFogColor());
      }

      if (var24.FOG_SHAPE != null) {
         var24.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
      }

      if (var24.TEXTURE_MATRIX != null) {
         var24.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
      }

      if (var24.GAME_TIME != null) {
         var24.GAME_TIME.set(RenderSystem.getShaderGameTime());
      }

      RenderSystem.setupShaderLights(var24);
      var24.apply();
      Uniform var25 = var24.CHUNK_OFFSET;

      while(var23 ? var11.hasNext() : var11.hasPrevious()) {
         LevelRenderer.RenderChunkInfo var27 = var23 ? (LevelRenderer.RenderChunkInfo)var11.next() : (LevelRenderer.RenderChunkInfo)var11.previous();
         ChunkRenderDispatcher.RenderChunk var15 = var27.chunk;
         if (!var15.getCompiledChunk().isEmpty(var1)) {
            VertexBuffer var28 = var15.getBuffer(var1);
            BlockPos var29 = var15.getOrigin();
            if (var25 != null) {
               var25.set((float)((double)var29.getX() - var3), (float)((double)var29.getY() - var5), (float)((double)var29.getZ() - var7));
               var25.upload();
            }

            var28.bind();
            var28.draw();
         }
      }

      if (var25 != null) {
         var25.set(0.0F, 0.0F, 0.0F);
      }

      var24.clear();
      VertexBuffer.unbind();
      this.minecraft.getProfiler().pop();
      var1.clearRenderState();
   }

   private void renderDebug(PoseStack var1, MultiBufferSource var2, Camera var3) {
      if (this.minecraft.chunkPath || this.minecraft.chunkVisibility) {
         double var4 = var3.getPosition().x();
         double var6 = var3.getPosition().y();
         double var8 = var3.getPosition().z();

         for(ObjectListIterator var10 = this.renderChunksInFrustum.iterator(); var10.hasNext(); var1.popPose()) {
            LevelRenderer.RenderChunkInfo var11 = (LevelRenderer.RenderChunkInfo)var10.next();
            ChunkRenderDispatcher.RenderChunk var12 = var11.chunk;
            BlockPos var13 = var12.getOrigin();
            var1.pushPose();
            var1.translate((double)var13.getX() - var4, (double)var13.getY() - var6, (double)var13.getZ() - var8);
            Matrix4f var14 = var1.last().pose();
            if (this.minecraft.chunkPath) {
               VertexConsumer var15 = var2.getBuffer(RenderType.lines());
               int var16 = var11.step == 0 ? 0 : Mth.hsvToRgb((float)var11.step / 50.0F, 0.9F, 0.9F);
               int var17 = var16 >> 16 & 0xFF;
               int var18 = var16 >> 8 & 0xFF;
               int var19 = var16 & 0xFF;

               for(int var20 = 0; var20 < DIRECTIONS.length; ++var20) {
                  if (var11.hasSourceDirection(var20)) {
                     Direction var21 = DIRECTIONS[var20];
                     var15.vertex(var14, 8.0F, 8.0F, 8.0F)
                        .color(var17, var18, var19, 255)
                        .normal((float)var21.getStepX(), (float)var21.getStepY(), (float)var21.getStepZ())
                        .endVertex();
                     var15.vertex(var14, (float)(8 - 16 * var21.getStepX()), (float)(8 - 16 * var21.getStepY()), (float)(8 - 16 * var21.getStepZ()))
                        .color(var17, var18, var19, 255)
                        .normal((float)var21.getStepX(), (float)var21.getStepY(), (float)var21.getStepZ())
                        .endVertex();
                  }
               }
            }

            if (this.minecraft.chunkVisibility && !var12.getCompiledChunk().hasNoRenderableLayers()) {
               VertexConsumer var28 = var2.getBuffer(RenderType.lines());
               int var29 = 0;

               for(Direction var36 : DIRECTIONS) {
                  for(Direction var24 : DIRECTIONS) {
                     boolean var25 = var12.getCompiledChunk().facesCanSeeEachother(var36, var24);
                     if (!var25) {
                        ++var29;
                        var28.vertex(var14, (float)(8 + 8 * var36.getStepX()), (float)(8 + 8 * var36.getStepY()), (float)(8 + 8 * var36.getStepZ()))
                           .color(255, 0, 0, 255)
                           .normal((float)var36.getStepX(), (float)var36.getStepY(), (float)var36.getStepZ())
                           .endVertex();
                        var28.vertex(var14, (float)(8 + 8 * var24.getStepX()), (float)(8 + 8 * var24.getStepY()), (float)(8 + 8 * var24.getStepZ()))
                           .color(255, 0, 0, 255)
                           .normal((float)var24.getStepX(), (float)var24.getStepY(), (float)var24.getStepZ())
                           .endVertex();
                     }
                  }
               }

               if (var29 > 0) {
                  VertexConsumer var31 = var2.getBuffer(RenderType.debugQuads());
                  float var33 = 0.5F;
                  float var35 = 0.2F;
                  var31.vertex(var14, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var31.vertex(var14, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
               }
            }
         }
      }

      if (this.capturedFrustum != null) {
         var1.pushPose();
         var1.translate(
            (float)(this.frustumPos.x - var3.getPosition().x),
            (float)(this.frustumPos.y - var3.getPosition().y),
            (float)(this.frustumPos.z - var3.getPosition().z)
         );
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
      var1.vertex(var2, this.frustumPoints[var3].x(), this.frustumPoints[var3].y(), this.frustumPoints[var3].z())
         .color(0, 0, 0, 255)
         .normal(0.0F, 0.0F, -1.0F)
         .endVertex();
   }

   private void addFrustumQuad(VertexConsumer var1, Matrix4f var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      float var10 = 0.25F;
      var1.vertex(var2, this.frustumPoints[var3].x(), this.frustumPoints[var3].y(), this.frustumPoints[var3].z())
         .color((float)var7, (float)var8, (float)var9, 0.25F)
         .endVertex();
      var1.vertex(var2, this.frustumPoints[var4].x(), this.frustumPoints[var4].y(), this.frustumPoints[var4].z())
         .color((float)var7, (float)var8, (float)var9, 0.25F)
         .endVertex();
      var1.vertex(var2, this.frustumPoints[var5].x(), this.frustumPoints[var5].y(), this.frustumPoints[var5].z())
         .color((float)var7, (float)var8, (float)var9, 0.25F)
         .endVertex();
      var1.vertex(var2, this.frustumPoints[var6].x(), this.frustumPoints[var6].y(), this.frustumPoints[var6].z())
         .color((float)var7, (float)var8, (float)var9, 0.25F)
         .endVertex();
   }

   public void captureFrustum() {
      this.captureFrustum = true;
   }

   public void killFrustum() {
      this.capturedFrustum = null;
   }

   public void tick() {
      ++this.ticks;
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
      BufferBuilder var3 = var2.getBuilder();

      for(int var4 = 0; var4 < 6; ++var4) {
         var1.pushPose();
         if (var4 == 1) {
            var1.mulPose(Axis.XP.rotationDegrees(90.0F));
         }

         if (var4 == 2) {
            var1.mulPose(Axis.XP.rotationDegrees(-90.0F));
         }

         if (var4 == 3) {
            var1.mulPose(Axis.XP.rotationDegrees(180.0F));
         }

         if (var4 == 4) {
            var1.mulPose(Axis.ZP.rotationDegrees(90.0F));
         }

         if (var4 == 5) {
            var1.mulPose(Axis.ZP.rotationDegrees(-90.0F));
         }

         Matrix4f var5 = var1.last().pose();
         var3.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         var3.vertex(var5, -100.0F, -100.0F, -100.0F).uv(0.0F, 0.0F).color(40, 40, 40, 255).endVertex();
         var3.vertex(var5, -100.0F, -100.0F, 100.0F).uv(0.0F, 16.0F).color(40, 40, 40, 255).endVertex();
         var3.vertex(var5, 100.0F, -100.0F, 100.0F).uv(16.0F, 16.0F).color(40, 40, 40, 255).endVertex();
         var3.vertex(var5, 100.0F, -100.0F, -100.0F).uv(16.0F, 0.0F).color(40, 40, 40, 255).endVertex();
         var2.end();
         var1.popPose();
      }

      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
   }

   public void renderSky(PoseStack var1, Matrix4f var2, float var3, Camera var4, boolean var5, Runnable var6) {
      var6.run();
      if (!var5) {
         FogType var7 = var4.getFluidInCamera();
         if (var7 != FogType.POWDER_SNOW && var7 != FogType.LAVA && !this.doesMobEffectBlockSky(var4)) {
            if (this.minecraft.level.effects().skyType() == DimensionSpecialEffects.SkyType.END) {
               this.renderEndSky(var1);
            } else if (this.minecraft.level.effects().skyType() == DimensionSpecialEffects.SkyType.NORMAL) {
               Vec3 var8 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), var3);
               float var9 = (float)var8.x;
               float var10 = (float)var8.y;
               float var11 = (float)var8.z;
               FogRenderer.levelFogColor();
               BufferBuilder var12 = Tesselator.getInstance().getBuilder();
               RenderSystem.depthMask(false);
               RenderSystem.setShaderColor(var9, var10, var11, 1.0F);
               ShaderInstance var13 = RenderSystem.getShader();
               this.skyBuffer.bind();
               this.skyBuffer.drawWithShader(var1.last().pose(), var2, var13);
               VertexBuffer.unbind();
               RenderSystem.enableBlend();
               float[] var14 = this.level.effects().getSunriseColor(this.level.getTimeOfDay(var3), var3);
               if (var14 != null) {
                  RenderSystem.setShader(GameRenderer::getPositionColorShader);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  var1.pushPose();
                  var1.mulPose(Axis.XP.rotationDegrees(90.0F));
                  float var15 = Mth.sin(this.level.getSunAngle(var3)) < 0.0F ? 180.0F : 0.0F;
                  var1.mulPose(Axis.ZP.rotationDegrees(var15));
                  var1.mulPose(Axis.ZP.rotationDegrees(90.0F));
                  float var16 = var14[0];
                  float var17 = var14[1];
                  float var18 = var14[2];
                  Matrix4f var19 = var1.last().pose();
                  var12.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
                  var12.vertex(var19, 0.0F, 100.0F, 0.0F).color(var16, var17, var18, var14[3]).endVertex();
                  boolean var20 = true;

                  for(int var21 = 0; var21 <= 16; ++var21) {
                     float var22 = (float)var21 * 6.2831855F / 16.0F;
                     float var23 = Mth.sin(var22);
                     float var24 = Mth.cos(var22);
                     var12.vertex(var19, var23 * 120.0F, var24 * 120.0F, -var24 * 40.0F * var14[3]).color(var14[0], var14[1], var14[2], 0.0F).endVertex();
                  }

                  BufferUploader.drawWithShader(var12.end());
                  var1.popPose();
               }

               RenderSystem.blendFuncSeparate(
                  GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
               );
               var1.pushPose();
               float var26 = 1.0F - this.level.getRainLevel(var3);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, var26);
               var1.mulPose(Axis.YP.rotationDegrees(-90.0F));
               var1.mulPose(Axis.XP.rotationDegrees(this.level.getTimeOfDay(var3) * 360.0F));
               Matrix4f var28 = var1.last().pose();
               float var29 = 30.0F;
               RenderSystem.setShader(GameRenderer::getPositionTexShader);
               RenderSystem.setShaderTexture(0, SUN_LOCATION);
               var12.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
               var12.vertex(var28, -var29, 100.0F, -var29).uv(0.0F, 0.0F).endVertex();
               var12.vertex(var28, var29, 100.0F, -var29).uv(1.0F, 0.0F).endVertex();
               var12.vertex(var28, var29, 100.0F, var29).uv(1.0F, 1.0F).endVertex();
               var12.vertex(var28, -var29, 100.0F, var29).uv(0.0F, 1.0F).endVertex();
               BufferUploader.drawWithShader(var12.end());
               var29 = 20.0F;
               RenderSystem.setShaderTexture(0, MOON_LOCATION);
               int var31 = this.level.getMoonPhase();
               int var32 = var31 % 4;
               int var33 = var31 / 4 % 2;
               float var34 = (float)(var32 + 0) / 4.0F;
               float var35 = (float)(var33 + 0) / 2.0F;
               float var36 = (float)(var32 + 1) / 4.0F;
               float var37 = (float)(var33 + 1) / 2.0F;
               var12.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
               var12.vertex(var28, -var29, -100.0F, var29).uv(var36, var37).endVertex();
               var12.vertex(var28, var29, -100.0F, var29).uv(var34, var37).endVertex();
               var12.vertex(var28, var29, -100.0F, -var29).uv(var34, var35).endVertex();
               var12.vertex(var28, -var29, -100.0F, -var29).uv(var36, var35).endVertex();
               BufferUploader.drawWithShader(var12.end());
               float var25 = this.level.getStarBrightness(var3) * var26;
               if (var25 > 0.0F) {
                  RenderSystem.setShaderColor(var25, var25, var25, var25);
                  FogRenderer.setupNoFog();
                  this.starBuffer.bind();
                  this.starBuffer.drawWithShader(var1.last().pose(), var2, GameRenderer.getPositionShader());
                  VertexBuffer.unbind();
                  var6.run();
               }

               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.disableBlend();
               RenderSystem.defaultBlendFunc();
               var1.popPose();
               RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
               double var27 = this.minecraft.player.getEyePosition(var3).y - this.level.getLevelData().getHorizonHeight(this.level);
               if (var27 < 0.0) {
                  var1.pushPose();
                  var1.translate(0.0F, 12.0F, 0.0F);
                  this.darkBuffer.bind();
                  this.darkBuffer.drawWithShader(var1.last().pose(), var2, var13);
                  VertexBuffer.unbind();
                  var1.popPose();
               }

               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.depthMask(true);
            }
         }
      }
   }

   private boolean doesMobEffectBlockSky(Camera var1) {
      Entity var3 = var1.getEntity();
      if (!(var3 instanceof LivingEntity)) {
         return false;
      } else {
         LivingEntity var2 = (LivingEntity)var3;
         return var2.hasEffect(MobEffects.BLINDNESS) || var2.hasEffect(MobEffects.DARKNESS);
      }
   }

   public void renderClouds(PoseStack var1, Matrix4f var2, float var3, double var4, double var6, double var8) {
      float var10 = this.level.effects().getCloudHeight();
      if (!Float.isNaN(var10)) {
         RenderSystem.disableCull();
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
         );
         RenderSystem.depthMask(true);
         float var11 = 12.0F;
         float var12 = 4.0F;
         double var13 = 2.0E-4;
         double var15 = (double)(((float)this.ticks + var3) * 0.03F);
         double var17 = (var4 + var15) / 12.0;
         double var19 = (double)(var10 - (float)var6 + 0.33F);
         double var21 = var8 / 12.0 + 0.33000001311302185;
         var17 -= (double)(Mth.floor(var17 / 2048.0) * 2048);
         var21 -= (double)(Mth.floor(var21 / 2048.0) * 2048);
         float var23 = (float)(var17 - (double)Mth.floor(var17));
         float var24 = (float)(var19 / 4.0 - (double)Mth.floor(var19 / 4.0)) * 4.0F;
         float var25 = (float)(var21 - (double)Mth.floor(var21));
         Vec3 var26 = this.level.getCloudColor(var3);
         int var27 = (int)Math.floor(var17);
         int var28 = (int)Math.floor(var19 / 4.0);
         int var29 = (int)Math.floor(var21);
         if (var27 != this.prevCloudX
            || var28 != this.prevCloudY
            || var29 != this.prevCloudZ
            || this.minecraft.options.getCloudsType() != this.prevCloudsType
            || this.prevCloudColor.distanceToSqr(var26) > 2.0E-4) {
            this.prevCloudX = var27;
            this.prevCloudY = var28;
            this.prevCloudZ = var29;
            this.prevCloudColor = var26;
            this.prevCloudsType = this.minecraft.options.getCloudsType();
            this.generateClouds = true;
         }

         if (this.generateClouds) {
            this.generateClouds = false;
            BufferBuilder var30 = Tesselator.getInstance().getBuilder();
            if (this.cloudBuffer != null) {
               this.cloudBuffer.close();
            }

            this.cloudBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            BufferBuilder.RenderedBuffer var31 = this.buildClouds(var30, var17, var19, var21, var26);
            this.cloudBuffer.bind();
            this.cloudBuffer.upload(var31);
            VertexBuffer.unbind();
         }

         RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
         RenderSystem.setShaderTexture(0, CLOUDS_LOCATION);
         FogRenderer.levelFogColor();
         var1.pushPose();
         var1.scale(12.0F, 1.0F, 12.0F);
         var1.translate(-var23, var24, -var25);
         if (this.cloudBuffer != null) {
            this.cloudBuffer.bind();
            int var35 = this.prevCloudsType == CloudStatus.FANCY ? 0 : 1;

            for(int var36 = var35; var36 < 2; ++var36) {
               if (var36 == 0) {
                  RenderSystem.colorMask(false, false, false, false);
               } else {
                  RenderSystem.colorMask(true, true, true, true);
               }

               ShaderInstance var32 = RenderSystem.getShader();
               this.cloudBuffer.drawWithShader(var1.last().pose(), var2, var32);
            }

            VertexBuffer.unbind();
         }

         var1.popPose();
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      }
   }

   private BufferBuilder.RenderedBuffer buildClouds(BufferBuilder var1, double var2, double var4, double var6, Vec3 var8) {
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
      RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
      var1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
      float var28 = (float)Math.floor(var4 / 4.0) * 4.0F;
      if (this.prevCloudsType == CloudStatus.FANCY) {
         for(int var29 = -3; var29 <= 4; ++var29) {
            for(int var30 = -3; var30 <= 4; ++var30) {
               float var31 = (float)(var29 * 8);
               float var32 = (float)(var30 * 8);
               if (var28 > -5.0F) {
                  var1.vertex((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 8.0F))
                     .uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15)
                     .color(var22, var23, var24, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  var1.vertex((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + 8.0F))
                     .uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15)
                     .color(var22, var23, var24, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  var1.vertex((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + 0.0F))
                     .uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15)
                     .color(var22, var23, var24, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  var1.vertex((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 0.0F))
                     .uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15)
                     .color(var22, var23, var24, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
               }

               if (var28 <= 5.0F) {
                  var1.vertex((double)(var31 + 0.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 8.0F))
                     .uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15)
                     .color(var16, var17, var18, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  var1.vertex((double)(var31 + 8.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 8.0F))
                     .uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15)
                     .color(var16, var17, var18, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  var1.vertex((double)(var31 + 8.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 0.0F))
                     .uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15)
                     .color(var16, var17, var18, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  var1.vertex((double)(var31 + 0.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 0.0F))
                     .uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15)
                     .color(var16, var17, var18, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
               }

               if (var29 > -1) {
                  for(int var33 = 0; var33 < 8; ++var33) {
                     var1.vertex((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 8.0F))
                        .uv((var31 + (float)var33 + 0.5F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15)
                        .color(var19, var20, var21, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     var1.vertex((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + 8.0F))
                        .uv((var31 + (float)var33 + 0.5F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15)
                        .color(var19, var20, var21, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     var1.vertex((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + 0.0F))
                        .uv((var31 + (float)var33 + 0.5F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15)
                        .color(var19, var20, var21, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     var1.vertex((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 0.0F))
                        .uv((var31 + (float)var33 + 0.5F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15)
                        .color(var19, var20, var21, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                  }
               }

               if (var29 <= 1) {
                  for(int var38 = 0; var38 < 8; ++var38) {
                     var1.vertex((double)(var31 + (float)var38 + 1.0F - 9.765625E-4F), (double)(var28 + 0.0F), (double)(var32 + 8.0F))
                        .uv((var31 + (float)var38 + 0.5F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15)
                        .color(var19, var20, var21, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     var1.vertex((double)(var31 + (float)var38 + 1.0F - 9.765625E-4F), (double)(var28 + 4.0F), (double)(var32 + 8.0F))
                        .uv((var31 + (float)var38 + 0.5F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15)
                        .color(var19, var20, var21, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     var1.vertex((double)(var31 + (float)var38 + 1.0F - 9.765625E-4F), (double)(var28 + 4.0F), (double)(var32 + 0.0F))
                        .uv((var31 + (float)var38 + 0.5F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15)
                        .color(var19, var20, var21, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     var1.vertex((double)(var31 + (float)var38 + 1.0F - 9.765625E-4F), (double)(var28 + 0.0F), (double)(var32 + 0.0F))
                        .uv((var31 + (float)var38 + 0.5F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15)
                        .color(var19, var20, var21, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                  }
               }

               if (var30 > -1) {
                  for(int var39 = 0; var39 < 8; ++var39) {
                     var1.vertex((double)(var31 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var39 + 0.0F))
                        .uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + (float)var39 + 0.5F) * 0.00390625F + var15)
                        .color(var25, var26, var27, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     var1.vertex((double)(var31 + 8.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var39 + 0.0F))
                        .uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + (float)var39 + 0.5F) * 0.00390625F + var15)
                        .color(var25, var26, var27, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     var1.vertex((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var39 + 0.0F))
                        .uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + (float)var39 + 0.5F) * 0.00390625F + var15)
                        .color(var25, var26, var27, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     var1.vertex((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var39 + 0.0F))
                        .uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + (float)var39 + 0.5F) * 0.00390625F + var15)
                        .color(var25, var26, var27, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                  }
               }

               if (var30 <= 1) {
                  for(int var40 = 0; var40 < 8; ++var40) {
                     var1.vertex((double)(var31 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var40 + 1.0F - 9.765625E-4F))
                        .uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + (float)var40 + 0.5F) * 0.00390625F + var15)
                        .color(var25, var26, var27, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     var1.vertex((double)(var31 + 8.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var40 + 1.0F - 9.765625E-4F))
                        .uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + (float)var40 + 0.5F) * 0.00390625F + var15)
                        .color(var25, var26, var27, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     var1.vertex((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var40 + 1.0F - 9.765625E-4F))
                        .uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + (float)var40 + 0.5F) * 0.00390625F + var15)
                        .color(var25, var26, var27, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     var1.vertex((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var40 + 1.0F - 9.765625E-4F))
                        .uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + (float)var40 + 0.5F) * 0.00390625F + var15)
                        .color(var25, var26, var27, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                  }
               }
            }
         }
      } else {
         boolean var34 = true;
         boolean var35 = true;

         for(int var36 = -32; var36 < 32; var36 += 32) {
            for(int var37 = -32; var37 < 32; var37 += 32) {
               var1.vertex((double)(var36 + 0), (double)var28, (double)(var37 + 32))
                  .uv((float)(var36 + 0) * 0.00390625F + var14, (float)(var37 + 32) * 0.00390625F + var15)
                  .color(var16, var17, var18, 0.8F)
                  .normal(0.0F, -1.0F, 0.0F)
                  .endVertex();
               var1.vertex((double)(var36 + 32), (double)var28, (double)(var37 + 32))
                  .uv((float)(var36 + 32) * 0.00390625F + var14, (float)(var37 + 32) * 0.00390625F + var15)
                  .color(var16, var17, var18, 0.8F)
                  .normal(0.0F, -1.0F, 0.0F)
                  .endVertex();
               var1.vertex((double)(var36 + 32), (double)var28, (double)(var37 + 0))
                  .uv((float)(var36 + 32) * 0.00390625F + var14, (float)(var37 + 0) * 0.00390625F + var15)
                  .color(var16, var17, var18, 0.8F)
                  .normal(0.0F, -1.0F, 0.0F)
                  .endVertex();
               var1.vertex((double)(var36 + 0), (double)var28, (double)(var37 + 0))
                  .uv((float)(var36 + 0) * 0.00390625F + var14, (float)(var37 + 0) * 0.00390625F + var15)
                  .color(var16, var17, var18, 0.8F)
                  .normal(0.0F, -1.0F, 0.0F)
                  .endVertex();
            }
         }
      }

      return var1.end();
   }

   private void compileChunks(Camera var1) {
      this.minecraft.getProfiler().push("populate_chunks_to_compile");
      LevelLightEngine var2 = this.level.getLightEngine();
      RenderRegionCache var3 = new RenderRegionCache();
      BlockPos var4 = var1.getBlockPosition();
      ArrayList var5 = Lists.newArrayList();
      ObjectListIterator var6 = this.renderChunksInFrustum.iterator();

      while(var6.hasNext()) {
         LevelRenderer.RenderChunkInfo var7 = (LevelRenderer.RenderChunkInfo)var6.next();
         ChunkRenderDispatcher.RenderChunk var8 = var7.chunk;
         SectionPos var9 = SectionPos.of(var8.getOrigin());
         if (var8.isDirty() && var2.lightOnInSection(var9)) {
            boolean var10 = false;
            if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.NEARBY) {
               BlockPos var11 = var8.getOrigin().offset(8, 8, 8);
               var10 = var11.distSqr(var4) < 768.0 || var8.isDirtyFromPlayer();
            } else if (this.minecraft.options.prioritizeChunkUpdates().get() == PrioritizeChunkUpdates.PLAYER_AFFECTED) {
               var10 = var8.isDirtyFromPlayer();
            }

            if (var10) {
               this.minecraft.getProfiler().push("build_near_sync");
               this.chunkRenderDispatcher.rebuildChunkSync(var8, var3);
               var8.setNotDirty();
               this.minecraft.getProfiler().pop();
            } else {
               var5.add(var8);
            }
         }
      }

      this.minecraft.getProfiler().popPush("upload");
      this.chunkRenderDispatcher.uploadAllPendingUploads();
      this.minecraft.getProfiler().popPush("schedule_async_compile");

      for(ChunkRenderDispatcher.RenderChunk var13 : var5) {
         var13.rebuildChunkAsync(this.chunkRenderDispatcher, var3);
         var13.setNotDirty();
      }

      this.minecraft.getProfiler().pop();
   }

   private void renderWorldBorder(Camera var1) {
      BufferBuilder var2 = Tesselator.getInstance().getBuilder();
      WorldBorder var3 = this.level.getWorldBorder();
      double var4 = (double)(this.minecraft.options.getEffectiveRenderDistance() * 16);
      if (!(var1.getPosition().x < var3.getMaxX() - var4)
         || !(var1.getPosition().x > var3.getMinX() + var4)
         || !(var1.getPosition().z < var3.getMaxZ() - var4)
         || !(var1.getPosition().z > var3.getMinZ() + var4)) {
         double var6 = 1.0 - var3.getDistanceToBorder(var1.getPosition().x, var1.getPosition().z) / var4;
         var6 = Math.pow(var6, 4.0);
         var6 = Mth.clamp(var6, 0.0, 1.0);
         double var8 = var1.getPosition().x;
         double var10 = var1.getPosition().z;
         double var12 = (double)this.minecraft.gameRenderer.getDepthFar();
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
         );
         RenderSystem.setShaderTexture(0, FORCEFIELD_LOCATION);
         RenderSystem.depthMask(Minecraft.useShaderTransparency());
         PoseStack var14 = RenderSystem.getModelViewStack();
         var14.pushPose();
         RenderSystem.applyModelViewMatrix();
         int var15 = var3.getStatus().getColor();
         float var16 = (float)(var15 >> 16 & 0xFF) / 255.0F;
         float var17 = (float)(var15 >> 8 & 0xFF) / 255.0F;
         float var18 = (float)(var15 & 0xFF) / 255.0F;
         RenderSystem.setShaderColor(var16, var17, var18, (float)var6);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.polygonOffset(-3.0F, -3.0F);
         RenderSystem.enablePolygonOffset();
         RenderSystem.disableCull();
         float var19 = (float)(Util.getMillis() % 3000L) / 3000.0F;
         float var20 = (float)(-Mth.frac(var1.getPosition().y * 0.5));
         float var21 = var20 + (float)var12;
         var2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         double var22 = Math.max((double)Mth.floor(var10 - var4), var3.getMinZ());
         double var24 = Math.min((double)Mth.ceil(var10 + var4), var3.getMaxZ());
         float var26 = (float)(Mth.floor(var22) & 1) * 0.5F;
         if (var8 > var3.getMaxX() - var4) {
            float var27 = var26;

            for(double var28 = var22; var28 < var24; var27 += 0.5F) {
               double var30 = Math.min(1.0, var24 - var28);
               float var32 = (float)var30 * 0.5F;
               var2.vertex(var3.getMaxX() - var8, -var12, var28 - var10).uv(var19 - var27, var19 + var21).endVertex();
               var2.vertex(var3.getMaxX() - var8, -var12, var28 + var30 - var10).uv(var19 - (var32 + var27), var19 + var21).endVertex();
               var2.vertex(var3.getMaxX() - var8, var12, var28 + var30 - var10).uv(var19 - (var32 + var27), var19 + var20).endVertex();
               var2.vertex(var3.getMaxX() - var8, var12, var28 - var10).uv(var19 - var27, var19 + var20).endVertex();
               ++var28;
            }
         }

         if (var8 < var3.getMinX() + var4) {
            float var38 = var26;

            for(double var41 = var22; var41 < var24; var38 += 0.5F) {
               double var44 = Math.min(1.0, var24 - var41);
               float var47 = (float)var44 * 0.5F;
               var2.vertex(var3.getMinX() - var8, -var12, var41 - var10).uv(var19 + var38, var19 + var21).endVertex();
               var2.vertex(var3.getMinX() - var8, -var12, var41 + var44 - var10).uv(var19 + var47 + var38, var19 + var21).endVertex();
               var2.vertex(var3.getMinX() - var8, var12, var41 + var44 - var10).uv(var19 + var47 + var38, var19 + var20).endVertex();
               var2.vertex(var3.getMinX() - var8, var12, var41 - var10).uv(var19 + var38, var19 + var20).endVertex();
               ++var41;
            }
         }

         var22 = Math.max((double)Mth.floor(var8 - var4), var3.getMinX());
         var24 = Math.min((double)Mth.ceil(var8 + var4), var3.getMaxX());
         var26 = (float)(Mth.floor(var22) & 1) * 0.5F;
         if (var10 > var3.getMaxZ() - var4) {
            float var39 = var26;

            for(double var42 = var22; var42 < var24; var39 += 0.5F) {
               double var45 = Math.min(1.0, var24 - var42);
               float var48 = (float)var45 * 0.5F;
               var2.vertex(var42 - var8, -var12, var3.getMaxZ() - var10).uv(var19 + var39, var19 + var21).endVertex();
               var2.vertex(var42 + var45 - var8, -var12, var3.getMaxZ() - var10).uv(var19 + var48 + var39, var19 + var21).endVertex();
               var2.vertex(var42 + var45 - var8, var12, var3.getMaxZ() - var10).uv(var19 + var48 + var39, var19 + var20).endVertex();
               var2.vertex(var42 - var8, var12, var3.getMaxZ() - var10).uv(var19 + var39, var19 + var20).endVertex();
               ++var42;
            }
         }

         if (var10 < var3.getMinZ() + var4) {
            float var40 = var26;

            for(double var43 = var22; var43 < var24; var40 += 0.5F) {
               double var46 = Math.min(1.0, var24 - var43);
               float var49 = (float)var46 * 0.5F;
               var2.vertex(var43 - var8, -var12, var3.getMinZ() - var10).uv(var19 - var40, var19 + var21).endVertex();
               var2.vertex(var43 + var46 - var8, -var12, var3.getMinZ() - var10).uv(var19 - (var49 + var40), var19 + var21).endVertex();
               var2.vertex(var43 + var46 - var8, var12, var3.getMinZ() - var10).uv(var19 - (var49 + var40), var19 + var20).endVertex();
               var2.vertex(var43 - var8, var12, var3.getMinZ() - var10).uv(var19 - var40, var19 + var20).endVertex();
               ++var43;
            }
         }

         BufferUploader.drawWithShader(var2.end());
         RenderSystem.enableCull();
         RenderSystem.polygonOffset(0.0F, 0.0F);
         RenderSystem.disablePolygonOffset();
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
         var14.popPose();
         RenderSystem.applyModelViewMatrix();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.depthMask(true);
      }
   }

   private void renderHitOutline(PoseStack var1, VertexConsumer var2, Entity var3, double var4, double var6, double var8, BlockPos var10, BlockState var11) {
      renderShape(
         var1,
         var2,
         var11.getShape(this.level, var10, CollisionContext.of(var3)),
         (double)var10.getX() - var4,
         (double)var10.getY() - var6,
         (double)var10.getZ() - var8,
         0.0F,
         0.0F,
         0.0F,
         0.4F
      );
   }

   private static Vec3 mixColor(float var0) {
      float var1 = 5.99999F;
      int var2 = (int)(Mth.clamp(var0, 0.0F, 1.0F) * 5.99999F);
      float var3 = var0 * 5.99999F - (float)var2;

      return switch(var2) {
         case 0 -> new Vec3(1.0, (double)var3, 0.0);
         case 1 -> new Vec3((double)(1.0F - var3), 1.0, 0.0);
         case 2 -> new Vec3(0.0, 1.0, (double)var3);
         case 3 -> new Vec3(0.0, 1.0 - (double)var3, 1.0);
         case 4 -> new Vec3((double)var3, 0.0, 1.0);
         case 5 -> new Vec3(1.0, 0.0, 1.0 - (double)var3);
         default -> throw new IllegalStateException("Unexpected value: " + var2);
      };
   }

   private static Vec3 shiftHue(float var0, float var1, float var2, float var3) {
      Vec3 var4 = mixColor(var3).scale((double)var0);
      Vec3 var5 = mixColor((var3 + 0.33333334F) % 1.0F).scale((double)var1);
      Vec3 var6 = mixColor((var3 + 0.6666667F) % 1.0F).scale((double)var2);
      Vec3 var7 = var4.add(var5).add(var6);
      double var8 = Math.max(Math.max(1.0, var7.x), Math.max(var7.y, var7.z));
      return new Vec3(var7.x / var8, var7.y / var8, var7.z / var8);
   }

   public static void renderVoxelShape(
      PoseStack var0,
      VertexConsumer var1,
      VoxelShape var2,
      double var3,
      double var5,
      double var7,
      float var9,
      float var10,
      float var11,
      float var12,
      boolean var13
   ) {
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

   private static void renderShape(
      PoseStack var0, VertexConsumer var1, VoxelShape var2, double var3, double var5, double var7, float var9, float var10, float var11, float var12
   ) {
      PoseStack.Pose var13 = var0.last();
      var2.forAllEdges(
         (var12x, var14, var16, var18, var20, var22) -> {
            float var24 = (float)(var18 - var12x);
            float var25 = (float)(var20 - var14);
            float var26 = (float)(var22 - var16);
            float var27 = Mth.sqrt(var24 * var24 + var25 * var25 + var26 * var26);
            var24 /= var27;
            var25 /= var27;
            var26 /= var27;
            var1.vertex(var13.pose(), (float)(var12x + var3), (float)(var14 + var5), (float)(var16 + var7))
               .color(var9, var10, var11, var12)
               .normal(var13.normal(), var24, var25, var26)
               .endVertex();
            var1.vertex(var13.pose(), (float)(var18 + var3), (float)(var20 + var5), (float)(var22 + var7))
               .color(var9, var10, var11, var12)
               .normal(var13.normal(), var24, var25, var26)
               .endVertex();
         }
      );
   }

   public static void renderLineBox(
      VertexConsumer var0, double var1, double var3, double var5, double var7, double var9, double var11, float var13, float var14, float var15, float var16
   ) {
      renderLineBox(new PoseStack(), var0, var1, var3, var5, var7, var9, var11, var13, var14, var15, var16, var13, var14, var15);
   }

   public static void renderLineBox(PoseStack var0, VertexConsumer var1, AABB var2, float var3, float var4, float var5, float var6) {
      renderLineBox(var0, var1, var2.minX, var2.minY, var2.minZ, var2.maxX, var2.maxY, var2.maxZ, var3, var4, var5, var6, var3, var4, var5);
   }

   public static void renderLineBox(
      PoseStack var0,
      VertexConsumer var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      float var14,
      float var15,
      float var16,
      float var17
   ) {
      renderLineBox(var0, var1, var2, var4, var6, var8, var10, var12, var14, var15, var16, var17, var14, var15, var16);
   }

   public static void renderLineBox(
      PoseStack var0,
      VertexConsumer var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18,
      float var19,
      float var20
   ) {
      Matrix4f var21 = var0.last().pose();
      Matrix3f var22 = var0.last().normal();
      float var23 = (float)var2;
      float var24 = (float)var4;
      float var25 = (float)var6;
      float var26 = (float)var8;
      float var27 = (float)var10;
      float var28 = (float)var12;
      var1.vertex(var21, var23, var24, var25).color(var14, var19, var20, var17).normal(var22, 1.0F, 0.0F, 0.0F).endVertex();
      var1.vertex(var21, var26, var24, var25).color(var14, var19, var20, var17).normal(var22, 1.0F, 0.0F, 0.0F).endVertex();
      var1.vertex(var21, var23, var24, var25).color(var18, var15, var20, var17).normal(var22, 0.0F, 1.0F, 0.0F).endVertex();
      var1.vertex(var21, var23, var27, var25).color(var18, var15, var20, var17).normal(var22, 0.0F, 1.0F, 0.0F).endVertex();
      var1.vertex(var21, var23, var24, var25).color(var18, var19, var16, var17).normal(var22, 0.0F, 0.0F, 1.0F).endVertex();
      var1.vertex(var21, var23, var24, var28).color(var18, var19, var16, var17).normal(var22, 0.0F, 0.0F, 1.0F).endVertex();
      var1.vertex(var21, var26, var24, var25).color(var14, var15, var16, var17).normal(var22, 0.0F, 1.0F, 0.0F).endVertex();
      var1.vertex(var21, var26, var27, var25).color(var14, var15, var16, var17).normal(var22, 0.0F, 1.0F, 0.0F).endVertex();
      var1.vertex(var21, var26, var27, var25).color(var14, var15, var16, var17).normal(var22, -1.0F, 0.0F, 0.0F).endVertex();
      var1.vertex(var21, var23, var27, var25).color(var14, var15, var16, var17).normal(var22, -1.0F, 0.0F, 0.0F).endVertex();
      var1.vertex(var21, var23, var27, var25).color(var14, var15, var16, var17).normal(var22, 0.0F, 0.0F, 1.0F).endVertex();
      var1.vertex(var21, var23, var27, var28).color(var14, var15, var16, var17).normal(var22, 0.0F, 0.0F, 1.0F).endVertex();
      var1.vertex(var21, var23, var27, var28).color(var14, var15, var16, var17).normal(var22, 0.0F, -1.0F, 0.0F).endVertex();
      var1.vertex(var21, var23, var24, var28).color(var14, var15, var16, var17).normal(var22, 0.0F, -1.0F, 0.0F).endVertex();
      var1.vertex(var21, var23, var24, var28).color(var14, var15, var16, var17).normal(var22, 1.0F, 0.0F, 0.0F).endVertex();
      var1.vertex(var21, var26, var24, var28).color(var14, var15, var16, var17).normal(var22, 1.0F, 0.0F, 0.0F).endVertex();
      var1.vertex(var21, var26, var24, var28).color(var14, var15, var16, var17).normal(var22, 0.0F, 0.0F, -1.0F).endVertex();
      var1.vertex(var21, var26, var24, var25).color(var14, var15, var16, var17).normal(var22, 0.0F, 0.0F, -1.0F).endVertex();
      var1.vertex(var21, var23, var27, var28).color(var14, var15, var16, var17).normal(var22, 1.0F, 0.0F, 0.0F).endVertex();
      var1.vertex(var21, var26, var27, var28).color(var14, var15, var16, var17).normal(var22, 1.0F, 0.0F, 0.0F).endVertex();
      var1.vertex(var21, var26, var24, var28).color(var14, var15, var16, var17).normal(var22, 0.0F, 1.0F, 0.0F).endVertex();
      var1.vertex(var21, var26, var27, var28).color(var14, var15, var16, var17).normal(var22, 0.0F, 1.0F, 0.0F).endVertex();
      var1.vertex(var21, var26, var27, var25).color(var14, var15, var16, var17).normal(var22, 0.0F, 0.0F, 1.0F).endVertex();
      var1.vertex(var21, var26, var27, var28).color(var14, var15, var16, var17).normal(var22, 0.0F, 0.0F, 1.0F).endVertex();
   }

   public static void addChainedFilledBoxVertices(
      PoseStack var0,
      VertexConsumer var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      float var14,
      float var15,
      float var16,
      float var17
   ) {
      addChainedFilledBoxVertices(var0, var1, (float)var2, (float)var4, (float)var6, (float)var8, (float)var10, (float)var12, var14, var15, var16, var17);
   }

   public static void addChainedFilledBoxVertices(
      PoseStack var0,
      VertexConsumer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11
   ) {
      Matrix4f var12 = var0.last().pose();
      var1.vertex(var12, var2, var3, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var3, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var3, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var3, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var6, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var6, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var6, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var3, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var6, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var3, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var3, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var3, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var6, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var6, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var6, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var3, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var6, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var3, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var3, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var3, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var3, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var3, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var3, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var6, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var6, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var2, var6, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var6, var4).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var6, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var6, var7).color(var8, var9, var10, var11).endVertex();
      var1.vertex(var12, var5, var6, var7).color(var8, var9, var10, var11).endVertex();
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

   public void playStreamingMusic(@Nullable SoundEvent var1, BlockPos var2) {
      SoundInstance var3 = this.playingRecords.get(var2);
      if (var3 != null) {
         this.minecraft.getSoundManager().stop(var3);
         this.playingRecords.remove(var2);
      }

      if (var1 != null) {
         RecordItem var4 = RecordItem.getBySound(var1);
         if (var4 != null) {
            this.minecraft.gui.setNowPlaying(var4.getDisplayName());
         }

         SimpleSoundInstance var5 = SimpleSoundInstance.forRecord(var1, Vec3.atCenterOf(var2));
         this.playingRecords.put(var2, var5);
         this.minecraft.getSoundManager().play(var5);
      }

      this.notifyNearbyEntities(this.level, var2, var1 != null);
   }

   private void notifyNearbyEntities(Level var1, BlockPos var2, boolean var3) {
      for(LivingEntity var6 : var1.getEntitiesOfClass(LivingEntity.class, new AABB(var2).inflate(3.0))) {
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
         var18.setDetail("ID", BuiltInRegistries.PARTICLE_TYPE.getKey(var1.getType()));
         var18.setDetail("Parameters", var1.writeToString());
         var18.setDetail("Position", () -> CrashReportCategory.formatLocation(this.level, var4, var6, var8));
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
   private Particle addParticleInternal(
      ParticleOptions var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14
   ) {
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
      ParticleStatus var2 = this.minecraft.options.particles().get();
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
      switch(var1) {
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
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void levelEvent(int var1, BlockPos var2, int var3) {
      RandomSource var4 = this.level.random;
      switch(var1) {
         case 1000:
            this.level.playLocalSound(var2, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 1001:
            this.level.playLocalSound(var2, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0F, 1.2F, false);
            break;
         case 1002:
            this.level.playLocalSound(var2, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0F, 1.2F, false);
            break;
         case 1003:
            this.level.playLocalSound(var2, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 1.0F, 1.2F, false);
            break;
         case 1004:
            this.level.playLocalSound(var2, SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.2F, false);
            break;
         case 1009:
            if (var3 == 0) {
               this.level
                  .playLocalSound(var2, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var4.nextFloat() - var4.nextFloat()) * 0.8F, false);
            } else if (var3 == 1) {
               this.level
                  .playLocalSound(
                     var2, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.7F, 1.6F + (var4.nextFloat() - var4.nextFloat()) * 0.4F, false
                  );
            }
            break;
         case 1010:
            Item var73 = Item.byId(var3);
            if (var73 instanceof RecordItem var64) {
               this.playStreamingMusic(var64.getSound(), var2);
            }
            break;
         case 1011:
            this.playStreamingMusic(null, var2);
            break;
         case 1015:
            this.level.playLocalSound(var2, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1016:
            this.level.playLocalSound(var2, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1017:
            this.level
               .playLocalSound(var2, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 10.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1018:
            this.level.playLocalSound(var2, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1019:
            this.level
               .playLocalSound(
                  var2, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false
               );
            break;
         case 1020:
            this.level
               .playLocalSound(
                  var2, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false
               );
            break;
         case 1021:
            this.level
               .playLocalSound(
                  var2, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false
               );
            break;
         case 1022:
            this.level
               .playLocalSound(var2, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false);
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
            this.level
               .playLocalSound(
                  var2, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false
               );
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
            this.level
               .playLocalSound(
                  var2, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false
               );
            break;
         case 1041:
            this.level
               .playLocalSound(
                  var2, SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false
               );
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
            this.level
               .playLocalSound(
                  var2, SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA_INTO_CAULDRON, SoundSource.BLOCKS, 2.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false
               );
            break;
         case 1047:
            this.level
               .playLocalSound(
                  var2, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER_INTO_CAULDRON, SoundSource.BLOCKS, 2.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false
               );
            break;
         case 1048:
            this.level
               .playLocalSound(
                  var2, SoundEvents.SKELETON_CONVERTED_TO_STRAY, SoundSource.HOSTILE, 2.0F, (var4.nextFloat() - var4.nextFloat()) * 0.2F + 1.0F, false
               );
            break;
         case 1500:
            ComposterBlock.handleFill(this.level, var2, var3 > 0);
            break;
         case 1501:
            this.level.playLocalSound(var2, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var4.nextFloat() - var4.nextFloat()) * 0.8F, false);

            for(int var63 = 0; var63 < 8; ++var63) {
               this.level
                  .addParticle(
                     ParticleTypes.LARGE_SMOKE,
                     (double)var2.getX() + var4.nextDouble(),
                     (double)var2.getY() + 1.2,
                     (double)var2.getZ() + var4.nextDouble(),
                     0.0,
                     0.0,
                     0.0
                  );
            }
            break;
         case 1502:
            this.level
               .playLocalSound(var2, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5F, 2.6F + (var4.nextFloat() - var4.nextFloat()) * 0.8F, false);

            for(int var62 = 0; var62 < 5; ++var62) {
               double var72 = (double)var2.getX() + var4.nextDouble() * 0.6 + 0.2;
               double var84 = (double)var2.getY() + var4.nextDouble() * 0.6 + 0.2;
               double var93 = (double)var2.getZ() + var4.nextDouble() * 0.6 + 0.2;
               this.level.addParticle(ParticleTypes.SMOKE, var72, var84, var93, 0.0, 0.0, 0.0);
            }
            break;
         case 1503:
            this.level.playLocalSound(var2, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);

            for(int var61 = 0; var61 < 16; ++var61) {
               double var71 = (double)var2.getX() + (5.0 + var4.nextDouble() * 6.0) / 16.0;
               double var83 = (double)var2.getY() + 0.8125;
               double var92 = (double)var2.getZ() + (5.0 + var4.nextDouble() * 6.0) / 16.0;
               this.level.addParticle(ParticleTypes.SMOKE, var71, var83, var92, 0.0, 0.0, 0.0);
            }
            break;
         case 1504:
            PointedDripstoneBlock.spawnDripParticle(this.level, var2, this.level.getBlockState(var2));
            break;
         case 1505:
            BoneMealItem.addGrowthParticles(this.level, var2, var3);
            this.level.playLocalSound(var2, SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            break;
         case 2000:
            Direction var32 = Direction.from3DDataValue(var3);
            int var36 = var32.getStepX();
            int var41 = var32.getStepY();
            int var49 = var32.getStepZ();
            double var55 = (double)var2.getX() + (double)var36 * 0.6 + 0.5;
            double var70 = (double)var2.getY() + (double)var41 * 0.6 + 0.5;
            double var82 = (double)var2.getZ() + (double)var49 * 0.6 + 0.5;

            for(int var91 = 0; var91 < 10; ++var91) {
               double var94 = var4.nextDouble() * 0.2 + 0.01;
               double var18 = var55 + (double)var36 * 0.01 + (var4.nextDouble() - 0.5) * (double)var49 * 0.5;
               double var20 = var70 + (double)var41 * 0.01 + (var4.nextDouble() - 0.5) * (double)var41 * 0.5;
               double var98 = var82 + (double)var49 * 0.01 + (var4.nextDouble() - 0.5) * (double)var36 * 0.5;
               double var24 = (double)var36 * var94 + var4.nextGaussian() * 0.01;
               double var26 = (double)var41 * var94 + var4.nextGaussian() * 0.01;
               double var28 = (double)var49 * var94 + var4.nextGaussian() * 0.01;
               this.addParticle(ParticleTypes.SMOKE, var18, var20, var98, var24, var26, var28);
            }
            break;
         case 2001:
            BlockState var31 = Block.stateById(var3);
            if (!var31.isAir()) {
               SoundType var35 = var31.getSoundType();
               this.level.playLocalSound(var2, var35.getBreakSound(), SoundSource.BLOCKS, (var35.getVolume() + 1.0F) / 2.0F, var35.getPitch() * 0.8F, false);
            }

            this.level.addDestroyBlockEffect(var2, var31);
            break;
         case 2002:
         case 2007:
            Vec3 var30 = Vec3.atBottomCenterOf(var2);

            for(int var33 = 0; var33 < 8; ++var33) {
               this.addParticle(
                  new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)),
                  var30.x,
                  var30.y,
                  var30.z,
                  var4.nextGaussian() * 0.15,
                  var4.nextDouble() * 0.2,
                  var4.nextGaussian() * 0.15
               );
            }

            float var34 = (float)(var3 >> 16 & 0xFF) / 255.0F;
            float var40 = (float)(var3 >> 8 & 0xFF) / 255.0F;
            float var48 = (float)(var3 >> 0 & 0xFF) / 255.0F;
            SimpleParticleType var54 = var1 == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;

            for(int var60 = 0; var60 < 100; ++var60) {
               double var69 = var4.nextDouble() * 4.0;
               double var81 = var4.nextDouble() * 3.141592653589793 * 2.0;
               double var90 = Math.cos(var81) * var69;
               double var96 = 0.01 + var4.nextDouble() * 0.5;
               double var97 = Math.sin(var81) * var69;
               Particle var21 = this.addParticleInternal(
                  var54, var54.getType().getOverrideLimiter(), var30.x + var90 * 0.1, var30.y + 0.3, var30.z + var97 * 0.1, var90, var96, var97
               );
               if (var21 != null) {
                  float var22 = 0.75F + var4.nextFloat() * 0.25F;
                  var21.setColor(var34 * var22, var40 * var22, var48 * var22);
                  var21.setPower((float)var69);
               }
            }

            this.level.playLocalSound(var2, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0F, var4.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2003:
            double var5 = (double)var2.getX() + 0.5;
            double var39 = (double)var2.getY();
            double var53 = (double)var2.getZ() + 0.5;

            for(int var67 = 0; var67 < 8; ++var67) {
               this.addParticle(
                  new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)),
                  var5,
                  var39,
                  var53,
                  var4.nextGaussian() * 0.15,
                  var4.nextDouble() * 0.2,
                  var4.nextGaussian() * 0.15
               );
            }

            for(double var68 = 0.0; var68 < 6.283185307179586; var68 += 0.15707963267948966) {
               this.addParticle(
                  ParticleTypes.PORTAL,
                  var5 + Math.cos(var68) * 5.0,
                  var39 - 0.4,
                  var53 + Math.sin(var68) * 5.0,
                  Math.cos(var68) * -5.0,
                  0.0,
                  Math.sin(var68) * -5.0
               );
               this.addParticle(
                  ParticleTypes.PORTAL,
                  var5 + Math.cos(var68) * 5.0,
                  var39 - 0.4,
                  var53 + Math.sin(var68) * 5.0,
                  Math.cos(var68) * -7.0,
                  0.0,
                  Math.sin(var68) * -7.0
               );
            }
            break;
         case 2004:
            for(int var38 = 0; var38 < 20; ++var38) {
               double var47 = (double)var2.getX() + 0.5 + (var4.nextDouble() - 0.5) * 2.0;
               double var59 = (double)var2.getY() + 0.5 + (var4.nextDouble() - 0.5) * 2.0;
               double var77 = (double)var2.getZ() + 0.5 + (var4.nextDouble() - 0.5) * 2.0;
               this.level.addParticle(ParticleTypes.SMOKE, var47, var59, var77, 0.0, 0.0, 0.0);
               this.level.addParticle(ParticleTypes.FLAME, var47, var59, var77, 0.0, 0.0, 0.0);
            }
            break;
         case 2005:
            BoneMealItem.addGrowthParticles(this.level, var2, var3);
            break;
         case 2006:
            for(int var58 = 0; var58 < 200; ++var58) {
               float var66 = var4.nextFloat() * 4.0F;
               float var76 = var4.nextFloat() * 6.2831855F;
               double var80 = (double)(Mth.cos(var76) * var66);
               double var89 = 0.01 + var4.nextDouble() * 0.5;
               double var95 = (double)(Mth.sin(var76) * var66);
               Particle var19 = this.addParticleInternal(
                  ParticleTypes.DRAGON_BREATH,
                  false,
                  (double)var2.getX() + var80 * 0.1,
                  (double)var2.getY() + 0.3,
                  (double)var2.getZ() + var95 * 0.1,
                  var80,
                  var89,
                  var95
               );
               if (var19 != null) {
                  var19.setPower(var66);
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
            for(int var57 = 0; var57 < 8; ++var57) {
               this.level
                  .addParticle(
                     ParticleTypes.CLOUD,
                     (double)var2.getX() + var4.nextDouble(),
                     (double)var2.getY() + 1.2,
                     (double)var2.getZ() + var4.nextDouble(),
                     0.0,
                     0.0,
                     0.0
                  );
            }
            break;
         case 3000:
            this.level
               .addParticle(
                  ParticleTypes.EXPLOSION_EMITTER, true, (double)var2.getX() + 0.5, (double)var2.getY() + 0.5, (double)var2.getZ() + 0.5, 0.0, 0.0, 0.0
               );
            this.level
               .playLocalSound(
                  var2,
                  SoundEvents.END_GATEWAY_SPAWN,
                  SoundSource.BLOCKS,
                  10.0F,
                  (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F,
                  false
               );
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
            int var37 = var3 >> 6;
            if (var37 > 0) {
               if (var4.nextFloat() < 0.3F + (float)var37 * 0.1F) {
                  float var44 = 0.15F + 0.02F * (float)var37 * (float)var37 * var4.nextFloat();
                  float var50 = 0.4F + 0.3F * (float)var37 * var4.nextFloat();
                  this.level.playLocalSound(var2, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, var44, var50, false);
               }

               byte var45 = (byte)(var3 & 63);
               UniformInt var51 = UniformInt.of(0, var37);
               float var10 = 0.005F;
               Supplier var11 = () -> new Vec3(
                     Mth.nextDouble(var4, -0.004999999888241291, 0.004999999888241291),
                     Mth.nextDouble(var4, -0.004999999888241291, 0.004999999888241291),
                     Mth.nextDouble(var4, -0.004999999888241291, 0.004999999888241291)
                  );
               if (var45 == 0) {
                  for(Direction var15 : Direction.values()) {
                     float var16 = var15 == Direction.DOWN ? 3.1415927F : 0.0F;
                     double var17 = var15.getAxis() == Direction.Axis.Y ? 0.65 : 0.57;
                     ParticleUtils.spawnParticlesOnBlockFace(this.level, var2, new SculkChargeParticleOptions(var16), var51, var15, var11, var17);
                  }
               } else {
                  for(Direction var78 : MultifaceBlock.unpack(var45)) {
                     float var85 = var78 == Direction.UP ? 3.1415927F : 0.0F;
                     double var87 = 0.35;
                     ParticleUtils.spawnParticlesOnBlockFace(this.level, var2, new SculkChargeParticleOptions(var85), var51, var78, var11, 0.35);
                  }
               }
            } else {
               this.level.playLocalSound(var2, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
               boolean var46 = this.level.getBlockState(var2).isCollisionShapeFullBlock(this.level, var2);
               int var52 = var46 ? 40 : 20;
               float var56 = var46 ? 0.45F : 0.25F;
               float var65 = 0.07F;

               for(int var75 = 0; var75 < var52; ++var75) {
                  float var79 = 2.0F * var4.nextFloat() - 1.0F;
                  float var86 = 2.0F * var4.nextFloat() - 1.0F;
                  float var88 = 2.0F * var4.nextFloat() - 1.0F;
                  this.level
                     .addParticle(
                        ParticleTypes.SCULK_CHARGE_POP,
                        (double)var2.getX() + 0.5 + (double)(var79 * var56),
                        (double)var2.getY() + 0.5 + (double)(var86 * var56),
                        (double)var2.getZ() + 0.5 + (double)(var88 * var56),
                        (double)(var79 * 0.07F),
                        (double)(var86 * 0.07F),
                        (double)(var88 * 0.07F)
                     );
               }
            }
            break;
         case 3007:
            for(int var42 = 0; var42 < 10; ++var42) {
               this.level
                  .addParticle(
                     new ShriekParticleOption(var42 * 5),
                     false,
                     (double)var2.getX() + 0.5,
                     (double)var2.getY() + SculkShriekerBlock.TOP_Y,
                     (double)var2.getZ() + 0.5,
                     0.0,
                     0.0,
                     0.0
                  );
            }

            BlockState var43 = this.level.getBlockState(var2);
            boolean var9 = var43.hasProperty(BlockStateProperties.WATERLOGGED) && var43.getValue(BlockStateProperties.WATERLOGGED);
            if (!var9) {
               this.level
                  .playLocalSound(
                     (double)var2.getX() + 0.5,
                     (double)var2.getY() + SculkShriekerBlock.TOP_Y,
                     (double)var2.getZ() + 0.5,
                     SoundEvents.SCULK_SHRIEKER_SHRIEK,
                     SoundSource.BLOCKS,
                     2.0F,
                     0.6F + this.level.random.nextFloat() * 0.4F,
                     false
                  );
            }
            break;
         case 3008:
            BlockState var6 = Block.stateById(var3);
            Block var8 = var6.getBlock();
            if (var8 instanceof BrushableBlock var7) {
               this.level.playLocalSound(var2, var7.getBrushCompletedSound(), SoundSource.PLAYERS, 1.0F, 1.0F, false);
            }

            this.level.addDestroyBlockEffect(var2, var6);
            break;
         case 3009:
            ParticleUtils.spawnParticlesOnBlockFaces(this.level, var2, ParticleTypes.EGG_CRACK, UniformInt.of(3, 6));
      }
   }

   public void destroyBlockProgress(int var1, BlockPos var2, int var3) {
      if (var3 >= 0 && var3 < 10) {
         BlockDestructionProgress var5 = (BlockDestructionProgress)this.destroyingBlocks.get(var1);
         if (var5 != null) {
            this.removeProgress(var5);
         }

         if (var5 == null || var5.getPos().getX() != var2.getX() || var5.getPos().getY() != var2.getY() || var5.getPos().getZ() != var2.getZ()) {
            var5 = new BlockDestructionProgress(var1, var2);
            this.destroyingBlocks.put(var1, var5);
         }

         var5.setProgress(var3);
         var5.updateTick(this.ticks);
         ((SortedSet)this.destructionProgress.computeIfAbsent(var5.getPos().asLong(), var0 -> Sets.newTreeSet())).add(var5);
      } else {
         BlockDestructionProgress var4 = (BlockDestructionProgress)this.destroyingBlocks.remove(var1);
         if (var4 != null) {
            this.removeProgress(var4);
         }
      }
   }

   public boolean hasRenderedAllChunks() {
      return this.chunkRenderDispatcher.isQueueEmpty();
   }

   public void needsUpdate() {
      this.needsFullRenderChunkUpdate = true;
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

   public boolean isChunkCompiled(BlockPos var1) {
      ChunkRenderDispatcher.RenderChunk var2 = this.viewArea.getRenderChunkAt(var1);
      return var2 != null && var2.compiled.get() != ChunkRenderDispatcher.CompiledChunk.UNCOMPILED;
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

   static class RenderChunkInfo {
      final ChunkRenderDispatcher.RenderChunk chunk;
      private byte sourceDirections;
      byte directions;
      final int step;

      RenderChunkInfo(ChunkRenderDispatcher.RenderChunk var1, @Nullable Direction var2, int var3) {
         super();
         this.chunk = var1;
         if (var2 != null) {
            this.addSourceDirection(var2);
         }

         this.step = var3;
      }

      public void setDirections(byte var1, Direction var2) {
         this.directions = (byte)(this.directions | var1 | 1 << var2.ordinal());
      }

      public boolean hasDirection(Direction var1) {
         return (this.directions & 1 << var1.ordinal()) > 0;
      }

      public void addSourceDirection(Direction var1) {
         this.sourceDirections = (byte)(this.sourceDirections | this.sourceDirections | 1 << var1.ordinal());
      }

      public boolean hasSourceDirection(int var1) {
         return (this.sourceDirections & 1 << var1) > 0;
      }

      public boolean hasSourceDirections() {
         return this.sourceDirections != 0;
      }

      public boolean isAxisAlignedWith(int var1, int var2, int var3) {
         BlockPos var4 = this.chunk.getOrigin();
         return var1 == var4.getX() / 16 || var3 == var4.getZ() / 16 || var2 == var4.getY() / 16;
      }

      @Override
      public int hashCode() {
         return this.chunk.getOrigin().hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof LevelRenderer.RenderChunkInfo)) {
            return false;
         } else {
            LevelRenderer.RenderChunkInfo var2 = (LevelRenderer.RenderChunkInfo)var1;
            return this.chunk.getOrigin().equals(var2.chunk.getOrigin());
         }
      }
   }

   static class RenderChunkStorage {
      public final LevelRenderer.RenderInfoMap renderInfoMap;
      public final LinkedHashSet<LevelRenderer.RenderChunkInfo> renderChunks;

      public RenderChunkStorage(int var1) {
         super();
         this.renderInfoMap = new LevelRenderer.RenderInfoMap(var1);
         this.renderChunks = new LinkedHashSet<>(var1);
      }
   }

   static class RenderInfoMap {
      private final LevelRenderer.RenderChunkInfo[] infos;

      RenderInfoMap(int var1) {
         super();
         this.infos = new LevelRenderer.RenderChunkInfo[var1];
      }

      public void put(ChunkRenderDispatcher.RenderChunk var1, LevelRenderer.RenderChunkInfo var2) {
         this.infos[var1.index] = var2;
      }

      @Nullable
      public LevelRenderer.RenderChunkInfo get(ChunkRenderDispatcher.RenderChunk var1) {
         int var2 = var1.index;
         return var2 >= 0 && var2 < this.infos.length ? this.infos[var2] : null;
      }
   }

   public static class TransparencyShaderException extends RuntimeException {
      public TransparencyShaderException(String var1, Throwable var2) {
         super(var1, var2);
      }
   }
}
