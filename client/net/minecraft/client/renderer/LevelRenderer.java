package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
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
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelRenderer implements ResourceManagerReloadListener, AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation MOON_LOCATION = new ResourceLocation("textures/environment/moon_phases.png");
   private static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation CLOUDS_LOCATION = new ResourceLocation("textures/environment/clouds.png");
   private static final ResourceLocation END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation FORCEFIELD_LOCATION = new ResourceLocation("textures/misc/forcefield.png");
   private static final ResourceLocation RAIN_LOCATION = new ResourceLocation("textures/environment/rain.png");
   private static final ResourceLocation SNOW_LOCATION = new ResourceLocation("textures/environment/snow.png");
   public static final Direction[] DIRECTIONS = Direction.values();
   private final Minecraft minecraft;
   private final TextureManager textureManager;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final RenderBuffers renderBuffers;
   private ClientLevel level;
   private Set<ChunkRenderDispatcher.RenderChunk> chunksToCompile = Sets.newLinkedHashSet();
   private final ObjectList<LevelRenderer.RenderChunkInfo> renderChunks = new ObjectArrayList(69696);
   private final Set<BlockEntity> globalBlockEntities = Sets.newHashSet();
   private ViewArea viewArea;
   private final VertexFormat skyFormat;
   @Nullable
   private VertexBuffer starBuffer;
   @Nullable
   private VertexBuffer skyBuffer;
   @Nullable
   private VertexBuffer darkBuffer;
   private boolean generateClouds;
   @Nullable
   private VertexBuffer cloudBuffer;
   private final RunningTrimmedMean frameTimes;
   private int ticks;
   private final Int2ObjectMap<BlockDestructionProgress> destroyingBlocks;
   private final Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress;
   private final Map<BlockPos, SoundInstance> playingRecords;
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
   private double lastCameraX;
   private double lastCameraY;
   private double lastCameraZ;
   private int lastCameraChunkX;
   private int lastCameraChunkY;
   private int lastCameraChunkZ;
   private double prevCamX;
   private double prevCamY;
   private double prevCamZ;
   private double prevCamRotX;
   private double prevCamRotY;
   private int prevCloudX;
   private int prevCloudY;
   private int prevCloudZ;
   private Vec3 prevCloudColor;
   private CloudStatus prevCloudsType;
   private ChunkRenderDispatcher chunkRenderDispatcher;
   private final VertexFormat format;
   private int lastViewDistance;
   private int renderedEntities;
   private int culledEntities;
   private boolean captureFrustum;
   @Nullable
   private Frustum capturedFrustum;
   private final Vector4f[] frustumPoints;
   private final Vector3d frustumPos;
   private double xTransparentOld;
   private double yTransparentOld;
   private double zTransparentOld;
   private boolean needsUpdate;
   private int frameId;
   private int rainSoundTime;
   private final float[] rainSizeX;
   private final float[] rainSizeZ;

   public LevelRenderer(Minecraft var1, RenderBuffers var2) {
      super();
      this.skyFormat = DefaultVertexFormat.POSITION;
      this.generateClouds = true;
      this.frameTimes = new RunningTrimmedMean(100);
      this.destroyingBlocks = new Int2ObjectOpenHashMap();
      this.destructionProgress = new Long2ObjectOpenHashMap();
      this.playingRecords = Maps.newHashMap();
      this.lastCameraX = 4.9E-324D;
      this.lastCameraY = 4.9E-324D;
      this.lastCameraZ = 4.9E-324D;
      this.lastCameraChunkX = -2147483648;
      this.lastCameraChunkY = -2147483648;
      this.lastCameraChunkZ = -2147483648;
      this.prevCamX = 4.9E-324D;
      this.prevCamY = 4.9E-324D;
      this.prevCamZ = 4.9E-324D;
      this.prevCamRotX = 4.9E-324D;
      this.prevCamRotY = 4.9E-324D;
      this.prevCloudX = -2147483648;
      this.prevCloudY = -2147483648;
      this.prevCloudZ = -2147483648;
      this.prevCloudColor = Vec3.ZERO;
      this.format = DefaultVertexFormat.BLOCK;
      this.lastViewDistance = -1;
      this.frustumPoints = new Vector4f[8];
      this.frustumPos = new Vector3d(0.0D, 0.0D, 0.0D);
      this.needsUpdate = true;
      this.rainSizeX = new float[1024];
      this.rainSizeZ = new float[1024];
      this.minecraft = var1;
      this.entityRenderDispatcher = var1.getEntityRenderDispatcher();
      this.renderBuffers = var2;
      this.textureManager = var1.getTextureManager();

      for(int var3 = 0; var3 < 32; ++var3) {
         for(int var4 = 0; var4 < 32; ++var4) {
            float var5 = (float)(var4 - 16);
            float var6 = (float)(var3 - 16);
            float var7 = Mth.sqrt(var5 * var5 + var6 * var6);
            this.rainSizeX[var3 << 5 | var4] = -var6 / var7;
            this.rainSizeZ[var3 << 5 | var4] = var5 / var7;
         }
      }

      this.createStars();
      this.createLightSky();
      this.createDarkSky();
   }

   private void renderSnowAndRain(LightTexture var1, float var2, double var3, double var5, double var7) {
      float var9 = this.minecraft.level.getRainLevel(var2);
      if (var9 > 0.0F) {
         var1.turnOnLightLayer();
         ClientLevel var10 = this.minecraft.level;
         int var11 = Mth.floor(var3);
         int var12 = Mth.floor(var5);
         int var13 = Mth.floor(var7);
         Tesselator var14 = Tesselator.getInstance();
         BufferBuilder var15 = var14.getBuilder();
         RenderSystem.enableAlphaTest();
         RenderSystem.disableCull();
         RenderSystem.normal3f(0.0F, 1.0F, 0.0F);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.defaultAlphaFunc();
         RenderSystem.enableDepthTest();
         byte var16 = 5;
         if (Minecraft.useFancyGraphics()) {
            var16 = 10;
         }

         RenderSystem.depthMask(Minecraft.useShaderTransparency());
         byte var17 = -1;
         float var18 = (float)this.ticks + var2;
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos.MutableBlockPos var19 = new BlockPos.MutableBlockPos();

         for(int var20 = var13 - var16; var20 <= var13 + var16; ++var20) {
            for(int var21 = var11 - var16; var21 <= var11 + var16; ++var21) {
               int var22 = (var20 - var13 + 16) * 32 + var21 - var11 + 16;
               double var23 = (double)this.rainSizeX[var22] * 0.5D;
               double var25 = (double)this.rainSizeZ[var22] * 0.5D;
               var19.set(var21, 0, var20);
               Biome var27 = var10.getBiome(var19);
               if (var27.getPrecipitation() != Biome.Precipitation.NONE) {
                  int var28 = var10.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var19).getY();
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
                     Random var32 = new Random((long)(var21 * var21 * 3121 + var21 * 45238971 ^ var20 * var20 * 418711 + var20 * 13761));
                     var19.set(var21, var29, var20);
                     float var33 = var27.getTemperature(var19);
                     float var35;
                     float var41;
                     if (var33 >= 0.15F) {
                        if (var17 != 0) {
                           if (var17 >= 0) {
                              var14.end();
                           }

                           var17 = 0;
                           this.minecraft.getTextureManager().bind(RAIN_LOCATION);
                           var15.begin(7, DefaultVertexFormat.PARTICLE);
                        }

                        int var34 = this.ticks + var21 * var21 * 3121 + var21 * 45238971 + var20 * var20 * 418711 + var20 * 13761 & 31;
                        var35 = -((float)var34 + var2) / 32.0F * (3.0F + var32.nextFloat());
                        double var36 = (double)((float)var21 + 0.5F) - var3;
                        double var38 = (double)((float)var20 + 0.5F) - var7;
                        float var40 = Mth.sqrt(var36 * var36 + var38 * var38) / (float)var16;
                        var41 = ((1.0F - var40 * var40) * 0.5F + 0.5F) * var9;
                        var19.set(var21, var31, var20);
                        int var42 = getLightColor(var10, var19);
                        var15.vertex((double)var21 - var3 - var23 + 0.5D, (double)var30 - var5, (double)var20 - var7 - var25 + 0.5D).uv(0.0F, (float)var29 * 0.25F + var35).color(1.0F, 1.0F, 1.0F, var41).uv2(var42).endVertex();
                        var15.vertex((double)var21 - var3 + var23 + 0.5D, (double)var30 - var5, (double)var20 - var7 + var25 + 0.5D).uv(1.0F, (float)var29 * 0.25F + var35).color(1.0F, 1.0F, 1.0F, var41).uv2(var42).endVertex();
                        var15.vertex((double)var21 - var3 + var23 + 0.5D, (double)var29 - var5, (double)var20 - var7 + var25 + 0.5D).uv(1.0F, (float)var30 * 0.25F + var35).color(1.0F, 1.0F, 1.0F, var41).uv2(var42).endVertex();
                        var15.vertex((double)var21 - var3 - var23 + 0.5D, (double)var29 - var5, (double)var20 - var7 - var25 + 0.5D).uv(0.0F, (float)var30 * 0.25F + var35).color(1.0F, 1.0F, 1.0F, var41).uv2(var42).endVertex();
                     } else {
                        if (var17 != 1) {
                           if (var17 >= 0) {
                              var14.end();
                           }

                           var17 = 1;
                           this.minecraft.getTextureManager().bind(SNOW_LOCATION);
                           var15.begin(7, DefaultVertexFormat.PARTICLE);
                        }

                        float var49 = -((float)(this.ticks & 511) + var2) / 512.0F;
                        var35 = (float)(var32.nextDouble() + (double)var18 * 0.01D * (double)((float)var32.nextGaussian()));
                        float var50 = (float)(var32.nextDouble() + (double)(var18 * (float)var32.nextGaussian()) * 0.001D);
                        double var37 = (double)((float)var21 + 0.5F) - var3;
                        double var39 = (double)((float)var20 + 0.5F) - var7;
                        var41 = Mth.sqrt(var37 * var37 + var39 * var39) / (float)var16;
                        float var48 = ((1.0F - var41 * var41) * 0.3F + 0.5F) * var9;
                        var19.set(var21, var31, var20);
                        int var43 = getLightColor(var10, var19);
                        int var44 = var43 >> 16 & '\uffff';
                        int var45 = (var43 & '\uffff') * 3;
                        int var46 = (var44 * 3 + 240) / 4;
                        int var47 = (var45 * 3 + 240) / 4;
                        var15.vertex((double)var21 - var3 - var23 + 0.5D, (double)var30 - var5, (double)var20 - var7 - var25 + 0.5D).uv(0.0F + var35, (float)var29 * 0.25F + var49 + var50).color(1.0F, 1.0F, 1.0F, var48).uv2(var47, var46).endVertex();
                        var15.vertex((double)var21 - var3 + var23 + 0.5D, (double)var30 - var5, (double)var20 - var7 + var25 + 0.5D).uv(1.0F + var35, (float)var29 * 0.25F + var49 + var50).color(1.0F, 1.0F, 1.0F, var48).uv2(var47, var46).endVertex();
                        var15.vertex((double)var21 - var3 + var23 + 0.5D, (double)var29 - var5, (double)var20 - var7 + var25 + 0.5D).uv(1.0F + var35, (float)var30 * 0.25F + var49 + var50).color(1.0F, 1.0F, 1.0F, var48).uv2(var47, var46).endVertex();
                        var15.vertex((double)var21 - var3 - var23 + 0.5D, (double)var29 - var5, (double)var20 - var7 - var25 + 0.5D).uv(0.0F + var35, (float)var30 * 0.25F + var49 + var50).color(1.0F, 1.0F, 1.0F, var48).uv2(var47, var46).endVertex();
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
         RenderSystem.defaultAlphaFunc();
         RenderSystem.disableAlphaTest();
         var1.turnOffLightLayer();
      }
   }

   public void tickRain(Camera var1) {
      float var2 = this.minecraft.level.getRainLevel(1.0F) / (Minecraft.useFancyGraphics() ? 1.0F : 2.0F);
      if (var2 > 0.0F) {
         Random var3 = new Random((long)this.ticks * 312987231L);
         ClientLevel var4 = this.minecraft.level;
         BlockPos var5 = new BlockPos(var1.getPosition());
         BlockPos var6 = null;
         int var7 = (int)(100.0F * var2 * var2) / (this.minecraft.options.particles == ParticleStatus.DECREASED ? 2 : 1);

         for(int var8 = 0; var8 < var7; ++var8) {
            int var9 = var3.nextInt(21) - 10;
            int var10 = var3.nextInt(21) - 10;
            BlockPos var11 = var4.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var5.offset(var9, 0, var10)).below();
            Biome var12 = var4.getBiome(var11);
            if (var11.getY() > 0 && var11.getY() <= var5.getY() + 10 && var11.getY() >= var5.getY() - 10 && var12.getPrecipitation() == Biome.Precipitation.RAIN && var12.getTemperature(var11) >= 0.15F) {
               var6 = var11;
               if (this.minecraft.options.particles == ParticleStatus.MINIMAL) {
                  break;
               }

               double var13 = var3.nextDouble();
               double var15 = var3.nextDouble();
               BlockState var17 = var4.getBlockState(var11);
               FluidState var18 = var4.getFluidState(var11);
               VoxelShape var19 = var17.getCollisionShape(var4, var11);
               double var20 = var19.max(Direction.Axis.Y, var13, var15);
               double var22 = (double)var18.getHeight(var4, var11);
               double var24 = Math.max(var20, var22);
               SimpleParticleType var26 = !var18.is(FluidTags.LAVA) && !var17.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(var17) ? ParticleTypes.RAIN : ParticleTypes.SMOKE;
               this.minecraft.level.addParticle(var26, (double)var11.getX() + var13, (double)var11.getY() + var24, (double)var11.getZ() + var15, 0.0D, 0.0D, 0.0D);
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
      this.textureManager.bind(FORCEFIELD_LOCATION);
      RenderSystem.texParameter(3553, 10242, 10497);
      RenderSystem.texParameter(3553, 10243, 10497);
      RenderSystem.bindTexture(0);
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
         RenderTarget var10 = var2.getTempTarget("translucent");
         RenderTarget var11 = var2.getTempTarget("itemEntity");
         RenderTarget var12 = var2.getTempTarget("particles");
         RenderTarget var14 = var2.getTempTarget("weather");
         RenderTarget var7 = var2.getTempTarget("clouds");
         this.transparencyChain = var2;
         this.translucentTarget = var10;
         this.itemEntityTarget = var11;
         this.particlesTarget = var12;
         this.weatherTarget = var14;
         this.cloudsTarget = var7;
      } catch (Exception var9) {
         String var3 = var9 instanceof JsonSyntaxException ? "parse" : "load";
         String var4 = "Failed to " + var3 + " shader: " + var1;
         LevelRenderer.TransparencyShaderException var5 = new LevelRenderer.TransparencyShaderException(var4, var9);
         if (this.minecraft.getResourcePackRepository().getSelectedIds().size() > 1) {
            TextComponent var6;
            try {
               var6 = new TextComponent(this.minecraft.getResourceManager().getResource(var1).getSourceName());
            } catch (IOException var8) {
               var6 = null;
            }

            this.minecraft.options.graphicsMode = GraphicsStatus.FANCY;
            this.minecraft.clearResourcePacksOnError(var5, var6);
         } else {
            CrashReport var13 = this.minecraft.fillReport(new CrashReport(var4, var5));
            this.minecraft.options.graphicsMode = GraphicsStatus.FANCY;
            this.minecraft.options.save();
            LOGGER.fatal(var4, var5);
            this.minecraft.emergencySave();
            Minecraft.crash(var13);
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
      }

   }

   protected boolean shouldShowEntityOutlines() {
      return this.entityTarget != null && this.entityEffect != null && this.minecraft.player != null;
   }

   private void createDarkSky() {
      Tesselator var1 = Tesselator.getInstance();
      BufferBuilder var2 = var1.getBuilder();
      if (this.darkBuffer != null) {
         this.darkBuffer.close();
      }

      this.darkBuffer = new VertexBuffer(this.skyFormat);
      this.drawSkyHemisphere(var2, -16.0F, true);
      var2.end();
      this.darkBuffer.upload(var2);
   }

   private void createLightSky() {
      Tesselator var1 = Tesselator.getInstance();
      BufferBuilder var2 = var1.getBuilder();
      if (this.skyBuffer != null) {
         this.skyBuffer.close();
      }

      this.skyBuffer = new VertexBuffer(this.skyFormat);
      this.drawSkyHemisphere(var2, 16.0F, false);
      var2.end();
      this.skyBuffer.upload(var2);
   }

   private void drawSkyHemisphere(BufferBuilder var1, float var2, boolean var3) {
      boolean var4 = true;
      boolean var5 = true;
      var1.begin(7, DefaultVertexFormat.POSITION);

      for(int var6 = -384; var6 <= 384; var6 += 64) {
         for(int var7 = -384; var7 <= 384; var7 += 64) {
            float var8 = (float)var6;
            float var9 = (float)(var6 + 64);
            if (var3) {
               var9 = (float)var6;
               var8 = (float)(var6 + 64);
            }

            var1.vertex((double)var8, (double)var2, (double)var7).endVertex();
            var1.vertex((double)var9, (double)var2, (double)var7).endVertex();
            var1.vertex((double)var9, (double)var2, (double)(var7 + 64)).endVertex();
            var1.vertex((double)var8, (double)var2, (double)(var7 + 64)).endVertex();
         }
      }

   }

   private void createStars() {
      Tesselator var1 = Tesselator.getInstance();
      BufferBuilder var2 = var1.getBuilder();
      if (this.starBuffer != null) {
         this.starBuffer.close();
      }

      this.starBuffer = new VertexBuffer(this.skyFormat);
      this.drawStars(var2);
      var2.end();
      this.starBuffer.upload(var2);
   }

   private void drawStars(BufferBuilder var1) {
      Random var2 = new Random(10842L);
      var1.begin(7, DefaultVertexFormat.POSITION);

      for(int var3 = 0; var3 < 1500; ++var3) {
         double var4 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var8 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var10 = (double)(0.15F + var2.nextFloat() * 0.1F);
         double var12 = var4 * var4 + var6 * var6 + var8 * var8;
         if (var12 < 1.0D && var12 > 0.01D) {
            var12 = 1.0D / Math.sqrt(var12);
            var4 *= var12;
            var6 *= var12;
            var8 *= var12;
            double var14 = var4 * 100.0D;
            double var16 = var6 * 100.0D;
            double var18 = var8 * 100.0D;
            double var20 = Math.atan2(var4, var8);
            double var22 = Math.sin(var20);
            double var24 = Math.cos(var20);
            double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
            double var28 = Math.sin(var26);
            double var30 = Math.cos(var26);
            double var32 = var2.nextDouble() * 3.141592653589793D * 2.0D;
            double var34 = Math.sin(var32);
            double var36 = Math.cos(var32);

            for(int var38 = 0; var38 < 4; ++var38) {
               double var39 = 0.0D;
               double var41 = (double)((var38 & 2) - 1) * var10;
               double var43 = (double)((var38 + 1 & 2) - 1) * var10;
               double var45 = 0.0D;
               double var47 = var41 * var36 - var43 * var34;
               double var49 = var43 * var36 + var41 * var34;
               double var53 = var47 * var28 + 0.0D * var30;
               double var55 = 0.0D * var28 - var47 * var30;
               double var57 = var55 * var22 - var49 * var24;
               double var61 = var49 * var22 + var55 * var24;
               var1.vertex(var14 + var57, var16 + var53, var18 + var61).endVertex();
            }
         }
      }

   }

   public void setLevel(@Nullable ClientLevel var1) {
      this.lastCameraX = 4.9E-324D;
      this.lastCameraY = 4.9E-324D;
      this.lastCameraZ = 4.9E-324D;
      this.lastCameraChunkX = -2147483648;
      this.lastCameraChunkY = -2147483648;
      this.lastCameraChunkZ = -2147483648;
      this.entityRenderDispatcher.setLevel(var1);
      this.level = var1;
      if (var1 != null) {
         this.allChanged();
      } else {
         this.chunksToCompile.clear();
         this.renderChunks.clear();
         if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
            this.viewArea = null;
         }

         if (this.chunkRenderDispatcher != null) {
            this.chunkRenderDispatcher.dispose();
         }

         this.chunkRenderDispatcher = null;
         this.globalBlockEntities.clear();
      }

   }

   public void allChanged() {
      if (this.level != null) {
         if (Minecraft.useShaderTransparency()) {
            this.initTransparency();
         } else {
            this.deinitTransparency();
         }

         this.level.clearTintCaches();
         if (this.chunkRenderDispatcher == null) {
            this.chunkRenderDispatcher = new ChunkRenderDispatcher(this.level, this, Util.backgroundExecutor(), this.minecraft.is64Bit(), this.renderBuffers.fixedBufferPack());
         } else {
            this.chunkRenderDispatcher.setLevel(this.level);
         }

         this.needsUpdate = true;
         this.generateClouds = true;
         ItemBlockRenderTypes.setFancy(Minecraft.useFancyGraphics());
         this.lastViewDistance = this.minecraft.options.renderDistance;
         if (this.viewArea != null) {
            this.viewArea.releaseAllBuffers();
         }

         this.resetChunksToCompile();
         synchronized(this.globalBlockEntities) {
            this.globalBlockEntities.clear();
         }

         this.viewArea = new ViewArea(this.chunkRenderDispatcher, this.level, this.minecraft.options.renderDistance, this);
         if (this.level != null) {
            Entity var1 = this.minecraft.getCameraEntity();
            if (var1 != null) {
               this.viewArea.repositionCamera(var1.getX(), var1.getZ());
            }
         }

      }
   }

   protected void resetChunksToCompile() {
      this.chunksToCompile.clear();
      this.chunkRenderDispatcher.blockUntilClear();
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
      return String.format("C: %d/%d %sD: %d, %s", var2, var1, this.minecraft.smartCull ? "(s) " : "", this.lastViewDistance, this.chunkRenderDispatcher == null ? "null" : this.chunkRenderDispatcher.getStats());
   }

   protected int countRenderedChunks() {
      int var1 = 0;
      ObjectListIterator var2 = this.renderChunks.iterator();

      while(var2.hasNext()) {
         LevelRenderer.RenderChunkInfo var3 = (LevelRenderer.RenderChunkInfo)var2.next();
         if (!var3.chunk.getCompiledChunk().hasNoRenderableLayers()) {
            ++var1;
         }
      }

      return var1;
   }

   public String getEntityStatistics() {
      return "E: " + this.renderedEntities + "/" + this.level.getEntityCount() + ", B: " + this.culledEntities;
   }

   private void setupRender(Camera var1, Frustum var2, boolean var3, int var4, boolean var5) {
      Vec3 var6 = var1.getPosition();
      if (this.minecraft.options.renderDistance != this.lastViewDistance) {
         this.allChanged();
      }

      this.level.getProfiler().push("camera");
      double var7 = this.minecraft.player.getX() - this.lastCameraX;
      double var9 = this.minecraft.player.getY() - this.lastCameraY;
      double var11 = this.minecraft.player.getZ() - this.lastCameraZ;
      if (this.lastCameraChunkX != this.minecraft.player.xChunk || this.lastCameraChunkY != this.minecraft.player.yChunk || this.lastCameraChunkZ != this.minecraft.player.zChunk || var7 * var7 + var9 * var9 + var11 * var11 > 16.0D) {
         this.lastCameraX = this.minecraft.player.getX();
         this.lastCameraY = this.minecraft.player.getY();
         this.lastCameraZ = this.minecraft.player.getZ();
         this.lastCameraChunkX = this.minecraft.player.xChunk;
         this.lastCameraChunkY = this.minecraft.player.yChunk;
         this.lastCameraChunkZ = this.minecraft.player.zChunk;
         this.viewArea.repositionCamera(this.minecraft.player.getX(), this.minecraft.player.getZ());
      }

      this.chunkRenderDispatcher.setCamera(var6);
      this.level.getProfiler().popPush("cull");
      this.minecraft.getProfiler().popPush("culling");
      BlockPos var13 = var1.getBlockPosition();
      ChunkRenderDispatcher.RenderChunk var14 = this.viewArea.getRenderChunkAt(var13);
      boolean var15 = true;
      BlockPos var16 = new BlockPos(Mth.floor(var6.x / 16.0D) * 16, Mth.floor(var6.y / 16.0D) * 16, Mth.floor(var6.z / 16.0D) * 16);
      float var17 = var1.getXRot();
      float var18 = var1.getYRot();
      this.needsUpdate = this.needsUpdate || !this.chunksToCompile.isEmpty() || var6.x != this.prevCamX || var6.y != this.prevCamY || var6.z != this.prevCamZ || (double)var17 != this.prevCamRotX || (double)var18 != this.prevCamRotY;
      this.prevCamX = var6.x;
      this.prevCamY = var6.y;
      this.prevCamZ = var6.z;
      this.prevCamRotX = (double)var17;
      this.prevCamRotY = (double)var18;
      this.minecraft.getProfiler().popPush("update");
      LevelRenderer.RenderChunkInfo var32;
      ChunkRenderDispatcher.RenderChunk var33;
      if (!var3 && this.needsUpdate) {
         this.needsUpdate = false;
         this.renderChunks.clear();
         ArrayDeque var19 = Queues.newArrayDeque();
         Entity.setViewScale(Mth.clamp((double)this.minecraft.options.renderDistance / 8.0D, 1.0D, 2.5D) * (double)this.minecraft.options.entityDistanceScaling);
         boolean var20 = this.minecraft.smartCull;
         int var25;
         int var26;
         if (var14 != null) {
            if (var5 && this.level.getBlockState(var13).isSolidRender(this.level, var13)) {
               var20 = false;
            }

            var14.setFrame(var4);
            var19.add(new LevelRenderer.RenderChunkInfo(var14, (Direction)null, 0));
         } else {
            int var21 = var13.getY() > 0 ? 248 : 8;
            int var22 = Mth.floor(var6.x / 16.0D) * 16;
            int var23 = Mth.floor(var6.z / 16.0D) * 16;
            ArrayList var24 = Lists.newArrayList();
            var25 = -this.lastViewDistance;

            while(true) {
               if (var25 > this.lastViewDistance) {
                  var24.sort(Comparator.comparingDouble((var1x) -> {
                     return var13.distSqr(var1x.chunk.getOrigin().offset(8, 8, 8));
                  }));
                  var19.addAll(var24);
                  break;
               }

               for(var26 = -this.lastViewDistance; var26 <= this.lastViewDistance; ++var26) {
                  ChunkRenderDispatcher.RenderChunk var27 = this.viewArea.getRenderChunkAt(new BlockPos(var22 + (var25 << 4) + 8, var21, var23 + (var26 << 4) + 8));
                  if (var27 != null && var2.isVisible(var27.bb)) {
                     var27.setFrame(var4);
                     var24.add(new LevelRenderer.RenderChunkInfo(var27, (Direction)null, 0));
                  }
               }

               ++var25;
            }
         }

         this.minecraft.getProfiler().push("iteration");

         while(!var19.isEmpty()) {
            var32 = (LevelRenderer.RenderChunkInfo)var19.poll();
            var33 = var32.chunk;
            Direction var34 = var32.sourceDirection;
            this.renderChunks.add(var32);
            Direction[] var36 = DIRECTIONS;
            var25 = var36.length;

            for(var26 = 0; var26 < var25; ++var26) {
               Direction var38 = var36[var26];
               ChunkRenderDispatcher.RenderChunk var28 = this.getRelativeFrom(var16, var33, var38);
               if ((!var20 || !var32.hasDirection(var38.getOpposite())) && (!var20 || var34 == null || var33.getCompiledChunk().facesCanSeeEachother(var34.getOpposite(), var38)) && var28 != null && var28.hasAllNeighbors() && var28.setFrame(var4) && var2.isVisible(var28.bb)) {
                  LevelRenderer.RenderChunkInfo var29 = new LevelRenderer.RenderChunkInfo(var28, var38, var32.step + 1);
                  var29.setDirections(var32.directions, var38);
                  var19.add(var29);
               }
            }
         }

         this.minecraft.getProfiler().pop();
      }

      this.minecraft.getProfiler().popPush("rebuildNear");
      Set var30 = this.chunksToCompile;
      this.chunksToCompile = Sets.newLinkedHashSet();
      ObjectListIterator var31 = this.renderChunks.iterator();

      while(true) {
         while(true) {
            do {
               if (!var31.hasNext()) {
                  this.chunksToCompile.addAll(var30);
                  this.minecraft.getProfiler().pop();
                  return;
               }

               var32 = (LevelRenderer.RenderChunkInfo)var31.next();
               var33 = var32.chunk;
            } while(!var33.isDirty() && !var30.contains(var33));

            this.needsUpdate = true;
            BlockPos var35 = var33.getOrigin().offset(8, 8, 8);
            boolean var37 = var35.distSqr(var13) < 768.0D;
            if (!var33.isDirtyFromPlayer() && !var37) {
               this.chunksToCompile.add(var33);
            } else {
               this.minecraft.getProfiler().push("build near");
               this.chunkRenderDispatcher.rebuildChunkSync(var33);
               var33.setNotDirty();
               this.minecraft.getProfiler().pop();
            }
         }
      }
   }

   @Nullable
   private ChunkRenderDispatcher.RenderChunk getRelativeFrom(BlockPos var1, ChunkRenderDispatcher.RenderChunk var2, Direction var3) {
      BlockPos var4 = var2.getRelativeOrigin(var3);
      if (Mth.abs(var1.getX() - var4.getX()) > this.lastViewDistance * 16) {
         return null;
      } else if (var4.getY() >= 0 && var4.getY() < 256) {
         return Mth.abs(var1.getZ() - var4.getZ()) > this.lastViewDistance * 16 ? null : this.viewArea.getRenderChunkAt(var4);
      } else {
         return null;
      }
   }

   private void captureFrustum(Matrix4f var1, Matrix4f var2, double var3, double var5, double var7, Frustum var9) {
      this.capturedFrustum = var9;
      Matrix4f var10 = var2.copy();
      var10.multiply(var1);
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
         this.frustumPoints[var11].transform(var10);
         this.frustumPoints[var11].perspectiveDivide();
      }

   }

   public void renderLevel(PoseStack var1, float var2, long var3, boolean var5, Camera var6, GameRenderer var7, LightTexture var8, Matrix4f var9) {
      BlockEntityRenderDispatcher.instance.prepare(this.level, this.minecraft.getTextureManager(), this.minecraft.font, var6, this.minecraft.hitResult);
      this.entityRenderDispatcher.prepare(this.level, var6, this.minecraft.crosshairPickEntity);
      ProfilerFiller var10 = this.level.getProfiler();
      var10.popPush("light_updates");
      this.minecraft.level.getChunkSource().getLightEngine().runUpdates(2147483647, true, true);
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
         var20 = new Frustum(var18, var9);
         var20.prepare(var12, var14, var16);
      }

      this.minecraft.getProfiler().popPush("captureFrustum");
      if (this.captureFrustum) {
         this.captureFrustum(var18, var9, var11.x, var11.y, var11.z, var19 ? new Frustum(var18, var9) : var20);
         this.captureFrustum = false;
      }

      var10.popPush("clear");
      FogRenderer.setupColor(var6, var2, this.minecraft.level, this.minecraft.options.renderDistance, var7.getDarkenWorldAmount(var2));
      RenderSystem.clear(16640, Minecraft.ON_OSX);
      float var21 = var7.getRenderDistance();
      boolean var22 = this.minecraft.level.effects().isFoggyAt(Mth.floor(var12), Mth.floor(var14)) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
      if (this.minecraft.options.renderDistance >= 4) {
         FogRenderer.setupFog(var6, FogRenderer.FogMode.FOG_SKY, var21, var22);
         var10.popPush("sky");
         this.renderSky(var1, var2);
      }

      var10.popPush("fog");
      FogRenderer.setupFog(var6, FogRenderer.FogMode.FOG_TERRAIN, Math.max(var21 - 16.0F, 32.0F), var22);
      var10.popPush("terrain_setup");
      this.setupRender(var6, var20, var19, this.frameId++, this.minecraft.player.isSpectator());
      var10.popPush("updatechunks");
      boolean var23 = true;
      int var24 = this.minecraft.options.framerateLimit;
      long var25 = 33333333L;
      long var27;
      if ((double)var24 == Option.FRAMERATE_LIMIT.getMaxValue()) {
         var27 = 0L;
      } else {
         var27 = (long)(1000000000 / var24);
      }

      long var29 = Util.getNanos() - var3;
      long var31 = this.frameTimes.registerValueAndGetMean(var29);
      long var33 = var31 * 3L / 2L;
      long var35 = Mth.clamp(var33, var27, 33333333L);
      this.compileChunksUntil(var3 + var35);
      var10.popPush("terrain");
      this.renderChunkLayer(RenderType.solid(), var1, var12, var14, var16);
      this.renderChunkLayer(RenderType.cutoutMipped(), var1, var12, var14, var16);
      this.renderChunkLayer(RenderType.cutout(), var1, var12, var14, var16);
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

      boolean var37 = false;
      MultiBufferSource.BufferSource var38 = this.renderBuffers.bufferSource();
      Iterator var39 = this.level.entitiesForRendering().iterator();

      while(true) {
         Entity var40;
         int var47;
         do {
            do {
               do {
                  if (!var39.hasNext()) {
                     this.checkPoseStack(var1);
                     var38.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
                     var38.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
                     var38.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
                     var38.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));
                     var10.popPush("blockentities");
                     ObjectListIterator var53 = this.renderChunks.iterator();

                     while(true) {
                        List var60;
                        do {
                           if (!var53.hasNext()) {
                              synchronized(this.globalBlockEntities) {
                                 Iterator var57 = this.globalBlockEntities.iterator();

                                 while(true) {
                                    if (!var57.hasNext()) {
                                       break;
                                    }

                                    BlockEntity var62 = (BlockEntity)var57.next();
                                    BlockPos var63 = var62.getBlockPos();
                                    var1.pushPose();
                                    var1.translate((double)var63.getX() - var12, (double)var63.getY() - var14, (double)var63.getZ() - var16);
                                    BlockEntityRenderDispatcher.instance.render(var62, var2, var1, var38);
                                    var1.popPose();
                                 }
                              }

                              this.checkPoseStack(var1);
                              var38.endBatch(RenderType.solid());
                              var38.endBatch(Sheets.solidBlockSheet());
                              var38.endBatch(Sheets.cutoutBlockSheet());
                              var38.endBatch(Sheets.bedSheet());
                              var38.endBatch(Sheets.shulkerBoxSheet());
                              var38.endBatch(Sheets.signSheet());
                              var38.endBatch(Sheets.chestSheet());
                              this.renderBuffers.outlineBufferSource().endOutlineBatch();
                              if (var37) {
                                 this.entityEffect.process(var2);
                                 this.minecraft.getMainRenderTarget().bindWrite(false);
                              }

                              var10.popPush("destroyProgress");
                              ObjectIterator var54 = this.destructionProgress.long2ObjectEntrySet().iterator();

                              while(var54.hasNext()) {
                                 Entry var58 = (Entry)var54.next();
                                 BlockPos var64 = BlockPos.of(var58.getLongKey());
                                 double var65 = (double)var64.getX() - var12;
                                 double var70 = (double)var64.getY() - var14;
                                 double var73 = (double)var64.getZ() - var16;
                                 if (var65 * var65 + var70 * var70 + var73 * var73 <= 1024.0D) {
                                    SortedSet var74 = (SortedSet)var58.getValue();
                                    if (var74 != null && !var74.isEmpty()) {
                                       int var75 = ((BlockDestructionProgress)var74.last()).getProgress();
                                       var1.pushPose();
                                       var1.translate((double)var64.getX() - var12, (double)var64.getY() - var14, (double)var64.getZ() - var16);
                                       PoseStack.Pose var50 = var1.last();
                                       SheetedDecalTextureGenerator var51 = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(var75)), var50.pose(), var50.normal());
                                       this.minecraft.getBlockRenderer().renderBreakingTexture(this.level.getBlockState(var64), var64, this.level, var1, var51);
                                       var1.popPose();
                                    }
                                 }
                              }

                              this.checkPoseStack(var1);
                              HitResult var55 = this.minecraft.hitResult;
                              if (var5 && var55 != null && var55.getType() == HitResult.Type.BLOCK) {
                                 var10.popPush("outline");
                                 BlockPos var59 = ((BlockHitResult)var55).getBlockPos();
                                 BlockState var66 = this.level.getBlockState(var59);
                                 if (!var66.isAir() && this.level.getWorldBorder().isWithinBounds(var59)) {
                                    VertexConsumer var68 = var38.getBuffer(RenderType.lines());
                                    this.renderHitOutline(var1, var68, var6.getEntity(), var12, var14, var16, var59, var66);
                                 }
                              }

                              RenderSystem.pushMatrix();
                              RenderSystem.multMatrix(var1.last().pose());
                              this.minecraft.debugRenderer.render(var1, var38, var12, var14, var16);
                              RenderSystem.popMatrix();
                              var38.endBatch(Sheets.translucentCullBlockSheet());
                              var38.endBatch(Sheets.bannerSheet());
                              var38.endBatch(Sheets.shieldSheet());
                              var38.endBatch(RenderType.armorGlint());
                              var38.endBatch(RenderType.armorEntityGlint());
                              var38.endBatch(RenderType.glint());
                              var38.endBatch(RenderType.glintDirect());
                              var38.endBatch(RenderType.glintTranslucent());
                              var38.endBatch(RenderType.entityGlint());
                              var38.endBatch(RenderType.entityGlintDirect());
                              var38.endBatch(RenderType.waterMask());
                              this.renderBuffers.crumblingBufferSource().endBatch();
                              if (this.transparencyChain != null) {
                                 var38.endBatch(RenderType.lines());
                                 var38.endBatch();
                                 this.translucentTarget.clear(Minecraft.ON_OSX);
                                 this.translucentTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
                                 var10.popPush("translucent");
                                 this.renderChunkLayer(RenderType.translucent(), var1, var12, var14, var16);
                                 var10.popPush("string");
                                 this.renderChunkLayer(RenderType.tripwire(), var1, var12, var14, var16);
                                 this.particlesTarget.clear(Minecraft.ON_OSX);
                                 this.particlesTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
                                 RenderStateShard.PARTICLES_TARGET.setupRenderState();
                                 var10.popPush("particles");
                                 this.minecraft.particleEngine.render(var1, var38, var8, var6, var2);
                                 RenderStateShard.PARTICLES_TARGET.clearRenderState();
                              } else {
                                 var10.popPush("translucent");
                                 this.renderChunkLayer(RenderType.translucent(), var1, var12, var14, var16);
                                 var38.endBatch(RenderType.lines());
                                 var38.endBatch();
                                 var10.popPush("string");
                                 this.renderChunkLayer(RenderType.tripwire(), var1, var12, var14, var16);
                                 var10.popPush("particles");
                                 this.minecraft.particleEngine.render(var1, var38, var8, var6, var2);
                              }

                              RenderSystem.pushMatrix();
                              RenderSystem.multMatrix(var1.last().pose());
                              if (this.minecraft.options.getCloudsType() != CloudStatus.OFF) {
                                 if (this.transparencyChain != null) {
                                    this.cloudsTarget.clear(Minecraft.ON_OSX);
                                    RenderStateShard.CLOUDS_TARGET.setupRenderState();
                                    var10.popPush("clouds");
                                    this.renderClouds(var1, var2, var12, var14, var16);
                                    RenderStateShard.CLOUDS_TARGET.clearRenderState();
                                 } else {
                                    var10.popPush("clouds");
                                    this.renderClouds(var1, var2, var12, var14, var16);
                                 }
                              }

                              if (this.transparencyChain != null) {
                                 RenderStateShard.WEATHER_TARGET.setupRenderState();
                                 var10.popPush("weather");
                                 this.renderSnowAndRain(var8, var2, var12, var14, var16);
                                 this.renderWorldBounds(var6);
                                 RenderStateShard.WEATHER_TARGET.clearRenderState();
                                 this.transparencyChain.process(var2);
                                 this.minecraft.getMainRenderTarget().bindWrite(false);
                              } else {
                                 RenderSystem.depthMask(false);
                                 var10.popPush("weather");
                                 this.renderSnowAndRain(var8, var2, var12, var14, var16);
                                 this.renderWorldBounds(var6);
                                 RenderSystem.depthMask(true);
                              }

                              this.renderDebug(var6);
                              RenderSystem.shadeModel(7424);
                              RenderSystem.depthMask(true);
                              RenderSystem.disableBlend();
                              RenderSystem.popMatrix();
                              FogRenderer.setupNoFog();
                              return;
                           }

                           LevelRenderer.RenderChunkInfo var56 = (LevelRenderer.RenderChunkInfo)var53.next();
                           var60 = var56.chunk.getCompiledChunk().getRenderableBlockEntities();
                        } while(var60.isEmpty());

                        Iterator var61 = var60.iterator();

                        while(var61.hasNext()) {
                           BlockEntity var67 = (BlockEntity)var61.next();
                           BlockPos var69 = var67.getBlockPos();
                           Object var71 = var38;
                           var1.pushPose();
                           var1.translate((double)var69.getX() - var12, (double)var69.getY() - var14, (double)var69.getZ() - var16);
                           SortedSet var72 = (SortedSet)this.destructionProgress.get(var69.asLong());
                           if (var72 != null && !var72.isEmpty()) {
                              var47 = ((BlockDestructionProgress)var72.last()).getProgress();
                              if (var47 >= 0) {
                                 PoseStack.Pose var48 = var1.last();
                                 SheetedDecalTextureGenerator var49 = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer((RenderType)ModelBakery.DESTROY_TYPES.get(var47)), var48.pose(), var48.normal());
                                 var71 = (var2x) -> {
                                    VertexConsumer var3 = var38.getBuffer(var2x);
                                    return var2x.affectsCrumbling() ? VertexMultiConsumer.create(var49, var3) : var3;
                                 };
                              }
                           }

                           BlockEntityRenderDispatcher.instance.render(var67, var2, var1, (MultiBufferSource)var71);
                           var1.popPose();
                        }
                     }
                  }

                  var40 = (Entity)var39.next();
               } while(!this.entityRenderDispatcher.shouldRender(var40, var20, var12, var14, var16) && !var40.hasIndirectPassenger(this.minecraft.player));
            } while(var40 == var6.getEntity() && !var6.isDetached() && (!(var6.getEntity() instanceof LivingEntity) || !((LivingEntity)var6.getEntity()).isSleeping()));
         } while(var40 instanceof LocalPlayer && var6.getEntity() != var40);

         ++this.renderedEntities;
         if (var40.tickCount == 0) {
            var40.xOld = var40.getX();
            var40.yOld = var40.getY();
            var40.zOld = var40.getZ();
         }

         Object var41;
         if (this.shouldShowEntityOutlines() && this.minecraft.shouldEntityAppearGlowing(var40)) {
            var37 = true;
            OutlineBufferSource var42 = this.renderBuffers.outlineBufferSource();
            var41 = var42;
            int var43 = var40.getTeamColor();
            boolean var44 = true;
            int var45 = var43 >> 16 & 255;
            int var46 = var43 >> 8 & 255;
            var47 = var43 & 255;
            var42.setColor(var45, var46, var47, 255);
         } else {
            var41 = var38;
         }

         this.renderEntity(var40, var12, var14, var16, var2, var1, (MultiBufferSource)var41);
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
      float var17 = Mth.lerp(var8, var1.yRotO, var1.yRot);
      this.entityRenderDispatcher.render(var1, var11 - var2, var13 - var4, var15 - var6, var17, var8, var9, var10, this.entityRenderDispatcher.getPackedLightCoords(var1, var8));
   }

   private void renderChunkLayer(RenderType var1, PoseStack var2, double var3, double var5, double var7) {
      var1.setupRenderState();
      if (var1 == RenderType.translucent()) {
         this.minecraft.getProfiler().push("translucent_sort");
         double var9 = var3 - this.xTransparentOld;
         double var11 = var5 - this.yTransparentOld;
         double var13 = var7 - this.zTransparentOld;
         if (var9 * var9 + var11 * var11 + var13 * var13 > 1.0D) {
            this.xTransparentOld = var3;
            this.yTransparentOld = var5;
            this.zTransparentOld = var7;
            int var15 = 0;
            ObjectListIterator var16 = this.renderChunks.iterator();

            while(var16.hasNext()) {
               LevelRenderer.RenderChunkInfo var17 = (LevelRenderer.RenderChunkInfo)var16.next();
               if (var15 < 15 && var17.chunk.resortTransparency(var1, this.chunkRenderDispatcher)) {
                  ++var15;
               }
            }
         }

         this.minecraft.getProfiler().pop();
      }

      this.minecraft.getProfiler().push("filterempty");
      this.minecraft.getProfiler().popPush(() -> {
         return "render_" + var1;
      });
      boolean var18 = var1 != RenderType.translucent();
      ObjectListIterator var10 = this.renderChunks.listIterator(var18 ? 0 : this.renderChunks.size());

      while(true) {
         if (var18) {
            if (!var10.hasNext()) {
               break;
            }
         } else if (!var10.hasPrevious()) {
            break;
         }

         LevelRenderer.RenderChunkInfo var19 = var18 ? (LevelRenderer.RenderChunkInfo)var10.next() : (LevelRenderer.RenderChunkInfo)var10.previous();
         ChunkRenderDispatcher.RenderChunk var12 = var19.chunk;
         if (!var12.getCompiledChunk().isEmpty(var1)) {
            VertexBuffer var20 = var12.getBuffer(var1);
            var2.pushPose();
            BlockPos var14 = var12.getOrigin();
            var2.translate((double)var14.getX() - var3, (double)var14.getY() - var5, (double)var14.getZ() - var7);
            var20.bind();
            this.format.setupBufferState(0L);
            var20.draw(var2.last().pose(), 7);
            var2.popPose();
         }
      }

      VertexBuffer.unbind();
      RenderSystem.clearCurrentColor();
      this.format.clearBufferState();
      this.minecraft.getProfiler().pop();
      var1.clearRenderState();
   }

   private void renderDebug(Camera var1) {
      Tesselator var2 = Tesselator.getInstance();
      BufferBuilder var3 = var2.getBuilder();
      if (this.minecraft.chunkPath || this.minecraft.chunkVisibility) {
         double var4 = var1.getPosition().x();
         double var6 = var1.getPosition().y();
         double var8 = var1.getPosition().z();
         RenderSystem.depthMask(true);
         RenderSystem.disableCull();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableTexture();

         for(ObjectListIterator var10 = this.renderChunks.iterator(); var10.hasNext(); RenderSystem.popMatrix()) {
            LevelRenderer.RenderChunkInfo var11 = (LevelRenderer.RenderChunkInfo)var10.next();
            ChunkRenderDispatcher.RenderChunk var12 = var11.chunk;
            RenderSystem.pushMatrix();
            BlockPos var13 = var12.getOrigin();
            RenderSystem.translated((double)var13.getX() - var4, (double)var13.getY() - var6, (double)var13.getZ() - var8);
            int var14;
            int var16;
            int var17;
            Direction var18;
            if (this.minecraft.chunkPath) {
               var3.begin(1, DefaultVertexFormat.POSITION_COLOR);
               RenderSystem.lineWidth(10.0F);
               var14 = var11.step == 0 ? 0 : Mth.hsvToRgb((float)var11.step / 50.0F, 0.9F, 0.9F);
               int var15 = var14 >> 16 & 255;
               var16 = var14 >> 8 & 255;
               var17 = var14 & 255;
               var18 = var11.sourceDirection;
               if (var18 != null) {
                  var3.vertex(8.0D, 8.0D, 8.0D).color(var15, var16, var17, 255).endVertex();
                  var3.vertex((double)(8 - 16 * var18.getStepX()), (double)(8 - 16 * var18.getStepY()), (double)(8 - 16 * var18.getStepZ())).color(var15, var16, var17, 255).endVertex();
               }

               var2.end();
               RenderSystem.lineWidth(1.0F);
            }

            if (this.minecraft.chunkVisibility && !var12.getCompiledChunk().hasNoRenderableLayers()) {
               var3.begin(1, DefaultVertexFormat.POSITION_COLOR);
               RenderSystem.lineWidth(10.0F);
               var14 = 0;
               Direction[] var24 = DIRECTIONS;
               var16 = var24.length;

               for(var17 = 0; var17 < var16; ++var17) {
                  var18 = var24[var17];
                  Direction[] var19 = DIRECTIONS;
                  int var20 = var19.length;

                  for(int var21 = 0; var21 < var20; ++var21) {
                     Direction var22 = var19[var21];
                     boolean var23 = var12.getCompiledChunk().facesCanSeeEachother(var18, var22);
                     if (!var23) {
                        ++var14;
                        var3.vertex((double)(8 + 8 * var18.getStepX()), (double)(8 + 8 * var18.getStepY()), (double)(8 + 8 * var18.getStepZ())).color(1, 0, 0, 1).endVertex();
                        var3.vertex((double)(8 + 8 * var22.getStepX()), (double)(8 + 8 * var22.getStepY()), (double)(8 + 8 * var22.getStepZ())).color(1, 0, 0, 1).endVertex();
                     }
                  }
               }

               var2.end();
               RenderSystem.lineWidth(1.0F);
               if (var14 > 0) {
                  var3.begin(7, DefaultVertexFormat.POSITION_COLOR);
                  float var25 = 0.5F;
                  float var26 = 0.2F;
                  var3.vertex(0.5D, 15.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(15.5D, 15.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(15.5D, 15.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(0.5D, 15.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(0.5D, 0.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(15.5D, 0.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(15.5D, 0.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(0.5D, 0.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(0.5D, 15.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(0.5D, 15.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(0.5D, 0.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(0.5D, 0.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(15.5D, 0.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(15.5D, 0.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(15.5D, 15.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(15.5D, 15.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(0.5D, 0.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(15.5D, 0.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(15.5D, 15.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(0.5D, 15.5D, 0.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(0.5D, 15.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(15.5D, 15.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(15.5D, 0.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var3.vertex(0.5D, 0.5D, 15.5D).color(0.9F, 0.9F, 0.0F, 0.2F).endVertex();
                  var2.end();
               }
            }
         }

         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
         RenderSystem.enableCull();
         RenderSystem.enableTexture();
      }

      if (this.capturedFrustum != null) {
         RenderSystem.disableCull();
         RenderSystem.disableTexture();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.lineWidth(10.0F);
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)(this.frustumPos.x - var1.getPosition().x), (float)(this.frustumPos.y - var1.getPosition().y), (float)(this.frustumPos.z - var1.getPosition().z));
         RenderSystem.depthMask(true);
         var3.begin(7, DefaultVertexFormat.POSITION_COLOR);
         this.addFrustumQuad(var3, 0, 1, 2, 3, 0, 1, 1);
         this.addFrustumQuad(var3, 4, 5, 6, 7, 1, 0, 0);
         this.addFrustumQuad(var3, 0, 1, 5, 4, 1, 1, 0);
         this.addFrustumQuad(var3, 2, 3, 7, 6, 0, 0, 1);
         this.addFrustumQuad(var3, 0, 4, 7, 3, 0, 1, 0);
         this.addFrustumQuad(var3, 1, 5, 6, 2, 1, 0, 1);
         var2.end();
         RenderSystem.depthMask(false);
         var3.begin(1, DefaultVertexFormat.POSITION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.addFrustumVertex(var3, 0);
         this.addFrustumVertex(var3, 1);
         this.addFrustumVertex(var3, 1);
         this.addFrustumVertex(var3, 2);
         this.addFrustumVertex(var3, 2);
         this.addFrustumVertex(var3, 3);
         this.addFrustumVertex(var3, 3);
         this.addFrustumVertex(var3, 0);
         this.addFrustumVertex(var3, 4);
         this.addFrustumVertex(var3, 5);
         this.addFrustumVertex(var3, 5);
         this.addFrustumVertex(var3, 6);
         this.addFrustumVertex(var3, 6);
         this.addFrustumVertex(var3, 7);
         this.addFrustumVertex(var3, 7);
         this.addFrustumVertex(var3, 4);
         this.addFrustumVertex(var3, 0);
         this.addFrustumVertex(var3, 4);
         this.addFrustumVertex(var3, 1);
         this.addFrustumVertex(var3, 5);
         this.addFrustumVertex(var3, 2);
         this.addFrustumVertex(var3, 6);
         this.addFrustumVertex(var3, 3);
         this.addFrustumVertex(var3, 7);
         var2.end();
         RenderSystem.popMatrix();
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
         RenderSystem.enableCull();
         RenderSystem.enableTexture();
         RenderSystem.lineWidth(1.0F);
      }

   }

   private void addFrustumVertex(VertexConsumer var1, int var2) {
      var1.vertex((double)this.frustumPoints[var2].x(), (double)this.frustumPoints[var2].y(), (double)this.frustumPoints[var2].z()).endVertex();
   }

   private void addFrustumQuad(VertexConsumer var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      float var9 = 0.25F;
      var1.vertex((double)this.frustumPoints[var2].x(), (double)this.frustumPoints[var2].y(), (double)this.frustumPoints[var2].z()).color((float)var6, (float)var7, (float)var8, 0.25F).endVertex();
      var1.vertex((double)this.frustumPoints[var3].x(), (double)this.frustumPoints[var3].y(), (double)this.frustumPoints[var3].z()).color((float)var6, (float)var7, (float)var8, 0.25F).endVertex();
      var1.vertex((double)this.frustumPoints[var4].x(), (double)this.frustumPoints[var4].y(), (double)this.frustumPoints[var4].z()).color((float)var6, (float)var7, (float)var8, 0.25F).endVertex();
      var1.vertex((double)this.frustumPoints[var5].x(), (double)this.frustumPoints[var5].y(), (double)this.frustumPoints[var5].z()).color((float)var6, (float)var7, (float)var8, 0.25F).endVertex();
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
      RenderSystem.disableAlphaTest();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.depthMask(false);
      this.textureManager.bind(END_SKY_LOCATION);
      Tesselator var2 = Tesselator.getInstance();
      BufferBuilder var3 = var2.getBuilder();

      for(int var4 = 0; var4 < 6; ++var4) {
         var1.pushPose();
         if (var4 == 1) {
            var1.mulPose(Vector3f.XP.rotationDegrees(90.0F));
         }

         if (var4 == 2) {
            var1.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
         }

         if (var4 == 3) {
            var1.mulPose(Vector3f.XP.rotationDegrees(180.0F));
         }

         if (var4 == 4) {
            var1.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
         }

         if (var4 == 5) {
            var1.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
         }

         Matrix4f var5 = var1.last().pose();
         var3.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
         var3.vertex(var5, -100.0F, -100.0F, -100.0F).uv(0.0F, 0.0F).color(40, 40, 40, 255).endVertex();
         var3.vertex(var5, -100.0F, -100.0F, 100.0F).uv(0.0F, 16.0F).color(40, 40, 40, 255).endVertex();
         var3.vertex(var5, 100.0F, -100.0F, 100.0F).uv(16.0F, 16.0F).color(40, 40, 40, 255).endVertex();
         var3.vertex(var5, 100.0F, -100.0F, -100.0F).uv(16.0F, 0.0F).color(40, 40, 40, 255).endVertex();
         var2.end();
         var1.popPose();
      }

      RenderSystem.depthMask(true);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      RenderSystem.enableAlphaTest();
   }

   public void renderSky(PoseStack var1, float var2) {
      if (this.minecraft.level.effects().skyType() == DimensionSpecialEffects.SkyType.END) {
         this.renderEndSky(var1);
      } else if (this.minecraft.level.effects().skyType() == DimensionSpecialEffects.SkyType.NORMAL) {
         RenderSystem.disableTexture();
         Vec3 var3 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getBlockPosition(), var2);
         float var4 = (float)var3.x;
         float var5 = (float)var3.y;
         float var6 = (float)var3.z;
         FogRenderer.levelFogColor();
         BufferBuilder var7 = Tesselator.getInstance().getBuilder();
         RenderSystem.depthMask(false);
         RenderSystem.enableFog();
         RenderSystem.color3f(var4, var5, var6);
         this.skyBuffer.bind();
         this.skyFormat.setupBufferState(0L);
         this.skyBuffer.draw(var1.last().pose(), 7);
         VertexBuffer.unbind();
         this.skyFormat.clearBufferState();
         RenderSystem.disableFog();
         RenderSystem.disableAlphaTest();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         float[] var8 = this.level.effects().getSunriseColor(this.level.getTimeOfDay(var2), var2);
         float var9;
         float var11;
         float var16;
         float var17;
         float var18;
         if (var8 != null) {
            RenderSystem.disableTexture();
            RenderSystem.shadeModel(7425);
            var1.pushPose();
            var1.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            var9 = Mth.sin(this.level.getSunAngle(var2)) < 0.0F ? 180.0F : 0.0F;
            var1.mulPose(Vector3f.ZP.rotationDegrees(var9));
            var1.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
            float var10 = var8[0];
            var11 = var8[1];
            float var12 = var8[2];
            Matrix4f var13 = var1.last().pose();
            var7.begin(6, DefaultVertexFormat.POSITION_COLOR);
            var7.vertex(var13, 0.0F, 100.0F, 0.0F).color(var10, var11, var12, var8[3]).endVertex();
            boolean var14 = true;

            for(int var15 = 0; var15 <= 16; ++var15) {
               var16 = (float)var15 * 6.2831855F / 16.0F;
               var17 = Mth.sin(var16);
               var18 = Mth.cos(var16);
               var7.vertex(var13, var17 * 120.0F, var18 * 120.0F, -var18 * 40.0F * var8[3]).color(var8[0], var8[1], var8[2], 0.0F).endVertex();
            }

            var7.end();
            BufferUploader.end(var7);
            var1.popPose();
            RenderSystem.shadeModel(7424);
         }

         RenderSystem.enableTexture();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         var1.pushPose();
         var9 = 1.0F - this.level.getRainLevel(var2);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, var9);
         var1.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
         var1.mulPose(Vector3f.XP.rotationDegrees(this.level.getTimeOfDay(var2) * 360.0F));
         Matrix4f var21 = var1.last().pose();
         var11 = 30.0F;
         this.textureManager.bind(SUN_LOCATION);
         var7.begin(7, DefaultVertexFormat.POSITION_TEX);
         var7.vertex(var21, -var11, 100.0F, -var11).uv(0.0F, 0.0F).endVertex();
         var7.vertex(var21, var11, 100.0F, -var11).uv(1.0F, 0.0F).endVertex();
         var7.vertex(var21, var11, 100.0F, var11).uv(1.0F, 1.0F).endVertex();
         var7.vertex(var21, -var11, 100.0F, var11).uv(0.0F, 1.0F).endVertex();
         var7.end();
         BufferUploader.end(var7);
         var11 = 20.0F;
         this.textureManager.bind(MOON_LOCATION);
         int var22 = this.level.getMoonPhase();
         int var23 = var22 % 4;
         int var24 = var22 / 4 % 2;
         float var25 = (float)(var23 + 0) / 4.0F;
         var16 = (float)(var24 + 0) / 2.0F;
         var17 = (float)(var23 + 1) / 4.0F;
         var18 = (float)(var24 + 1) / 2.0F;
         var7.begin(7, DefaultVertexFormat.POSITION_TEX);
         var7.vertex(var21, -var11, -100.0F, var11).uv(var17, var18).endVertex();
         var7.vertex(var21, var11, -100.0F, var11).uv(var25, var18).endVertex();
         var7.vertex(var21, var11, -100.0F, -var11).uv(var25, var16).endVertex();
         var7.vertex(var21, -var11, -100.0F, -var11).uv(var17, var16).endVertex();
         var7.end();
         BufferUploader.end(var7);
         RenderSystem.disableTexture();
         float var19 = this.level.getStarBrightness(var2) * var9;
         if (var19 > 0.0F) {
            RenderSystem.color4f(var19, var19, var19, var19);
            this.starBuffer.bind();
            this.skyFormat.setupBufferState(0L);
            this.starBuffer.draw(var1.last().pose(), 7);
            VertexBuffer.unbind();
            this.skyFormat.clearBufferState();
         }

         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.disableBlend();
         RenderSystem.enableAlphaTest();
         RenderSystem.enableFog();
         var1.popPose();
         RenderSystem.disableTexture();
         RenderSystem.color3f(0.0F, 0.0F, 0.0F);
         double var20 = this.minecraft.player.getEyePosition(var2).y - this.level.getLevelData().getHorizonHeight();
         if (var20 < 0.0D) {
            var1.pushPose();
            var1.translate(0.0D, 12.0D, 0.0D);
            this.darkBuffer.bind();
            this.skyFormat.setupBufferState(0L);
            this.darkBuffer.draw(var1.last().pose(), 7);
            VertexBuffer.unbind();
            this.skyFormat.clearBufferState();
            var1.popPose();
         }

         if (this.level.effects().hasGround()) {
            RenderSystem.color3f(var4 * 0.2F + 0.04F, var5 * 0.2F + 0.04F, var6 * 0.6F + 0.1F);
         } else {
            RenderSystem.color3f(var4, var5, var6);
         }

         RenderSystem.enableTexture();
         RenderSystem.depthMask(true);
         RenderSystem.disableFog();
      }
   }

   public void renderClouds(PoseStack var1, float var2, double var3, double var5, double var7) {
      float var9 = this.level.effects().getCloudHeight();
      if (!Float.isNaN(var9)) {
         RenderSystem.disableCull();
         RenderSystem.enableBlend();
         RenderSystem.enableAlphaTest();
         RenderSystem.enableDepthTest();
         RenderSystem.defaultAlphaFunc();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         RenderSystem.enableFog();
         RenderSystem.depthMask(true);
         float var10 = 12.0F;
         float var11 = 4.0F;
         double var12 = 2.0E-4D;
         double var14 = (double)(((float)this.ticks + var2) * 0.03F);
         double var16 = (var3 + var14) / 12.0D;
         double var18 = (double)(var9 - (float)var5 + 0.33F);
         double var20 = var7 / 12.0D + 0.33000001311302185D;
         var16 -= (double)(Mth.floor(var16 / 2048.0D) * 2048);
         var20 -= (double)(Mth.floor(var20 / 2048.0D) * 2048);
         float var22 = (float)(var16 - (double)Mth.floor(var16));
         float var23 = (float)(var18 / 4.0D - (double)Mth.floor(var18 / 4.0D)) * 4.0F;
         float var24 = (float)(var20 - (double)Mth.floor(var20));
         Vec3 var25 = this.level.getCloudColor(var2);
         int var26 = (int)Math.floor(var16);
         int var27 = (int)Math.floor(var18 / 4.0D);
         int var28 = (int)Math.floor(var20);
         if (var26 != this.prevCloudX || var27 != this.prevCloudY || var28 != this.prevCloudZ || this.minecraft.options.getCloudsType() != this.prevCloudsType || this.prevCloudColor.distanceToSqr(var25) > 2.0E-4D) {
            this.prevCloudX = var26;
            this.prevCloudY = var27;
            this.prevCloudZ = var28;
            this.prevCloudColor = var25;
            this.prevCloudsType = this.minecraft.options.getCloudsType();
            this.generateClouds = true;
         }

         if (this.generateClouds) {
            this.generateClouds = false;
            BufferBuilder var29 = Tesselator.getInstance().getBuilder();
            if (this.cloudBuffer != null) {
               this.cloudBuffer.close();
            }

            this.cloudBuffer = new VertexBuffer(DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
            this.buildClouds(var29, var16, var18, var20, var25);
            var29.end();
            this.cloudBuffer.upload(var29);
         }

         this.textureManager.bind(CLOUDS_LOCATION);
         var1.pushPose();
         var1.scale(12.0F, 1.0F, 12.0F);
         var1.translate((double)(-var22), (double)var23, (double)(-var24));
         if (this.cloudBuffer != null) {
            this.cloudBuffer.bind();
            DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.setupBufferState(0L);
            int var31 = this.prevCloudsType == CloudStatus.FANCY ? 0 : 1;

            for(int var30 = var31; var30 < 2; ++var30) {
               if (var30 == 0) {
                  RenderSystem.colorMask(false, false, false, false);
               } else {
                  RenderSystem.colorMask(true, true, true, true);
               }

               this.cloudBuffer.draw(var1.last().pose(), 7);
            }

            VertexBuffer.unbind();
            DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL.clearBufferState();
         }

         var1.popPose();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.disableAlphaTest();
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
         RenderSystem.disableFog();
      }
   }

   private void buildClouds(BufferBuilder var1, double var2, double var4, double var6, Vec3 var8) {
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
      var1.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
      float var28 = (float)Math.floor(var4 / 4.0D) * 4.0F;
      if (this.prevCloudsType == CloudStatus.FANCY) {
         for(int var29 = -3; var29 <= 4; ++var29) {
            for(int var30 = -3; var30 <= 4; ++var30) {
               float var31 = (float)(var29 * 8);
               float var32 = (float)(var30 * 8);
               if (var28 > -5.0F) {
                  var1.vertex((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 8.0F)).uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15).color(var22, var23, var24, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  var1.vertex((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + 8.0F)).uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15).color(var22, var23, var24, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  var1.vertex((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + 0.0F)).uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15).color(var22, var23, var24, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  var1.vertex((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 0.0F)).uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15).color(var22, var23, var24, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               }

               if (var28 <= 5.0F) {
                  var1.vertex((double)(var31 + 0.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 8.0F)).uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15).color(var16, var17, var18, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  var1.vertex((double)(var31 + 8.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 8.0F)).uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15).color(var16, var17, var18, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  var1.vertex((double)(var31 + 8.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 0.0F)).uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15).color(var16, var17, var18, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  var1.vertex((double)(var31 + 0.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 0.0F)).uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15).color(var16, var17, var18, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
               }

               int var33;
               if (var29 > -1) {
                  for(var33 = 0; var33 < 8; ++var33) {
                     var1.vertex((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 8.0F)).uv((var31 + (float)var33 + 0.5F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15).color(var19, var20, var21, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     var1.vertex((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + 8.0F)).uv((var31 + (float)var33 + 0.5F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15).color(var19, var20, var21, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     var1.vertex((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + 0.0F)).uv((var31 + (float)var33 + 0.5F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15).color(var19, var20, var21, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     var1.vertex((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 0.0F)).uv((var31 + (float)var33 + 0.5F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15).color(var19, var20, var21, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                  }
               }

               if (var29 <= 1) {
                  for(var33 = 0; var33 < 8; ++var33) {
                     var1.vertex((double)(var31 + (float)var33 + 1.0F - 9.765625E-4F), (double)(var28 + 0.0F), (double)(var32 + 8.0F)).uv((var31 + (float)var33 + 0.5F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15).color(var19, var20, var21, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     var1.vertex((double)(var31 + (float)var33 + 1.0F - 9.765625E-4F), (double)(var28 + 4.0F), (double)(var32 + 8.0F)).uv((var31 + (float)var33 + 0.5F) * 0.00390625F + var14, (var32 + 8.0F) * 0.00390625F + var15).color(var19, var20, var21, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     var1.vertex((double)(var31 + (float)var33 + 1.0F - 9.765625E-4F), (double)(var28 + 4.0F), (double)(var32 + 0.0F)).uv((var31 + (float)var33 + 0.5F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15).color(var19, var20, var21, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     var1.vertex((double)(var31 + (float)var33 + 1.0F - 9.765625E-4F), (double)(var28 + 0.0F), (double)(var32 + 0.0F)).uv((var31 + (float)var33 + 0.5F) * 0.00390625F + var14, (var32 + 0.0F) * 0.00390625F + var15).color(var19, var20, var21, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                  }
               }

               if (var30 > -1) {
                  for(var33 = 0; var33 < 8; ++var33) {
                     var1.vertex((double)(var31 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var33 + 0.0F)).uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + (float)var33 + 0.5F) * 0.00390625F + var15).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     var1.vertex((double)(var31 + 8.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var33 + 0.0F)).uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + (float)var33 + 0.5F) * 0.00390625F + var15).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     var1.vertex((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var33 + 0.0F)).uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + (float)var33 + 0.5F) * 0.00390625F + var15).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     var1.vertex((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var33 + 0.0F)).uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + (float)var33 + 0.5F) * 0.00390625F + var15).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                  }
               }

               if (var30 <= 1) {
                  for(var33 = 0; var33 < 8; ++var33) {
                     var1.vertex((double)(var31 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var33 + 1.0F - 9.765625E-4F)).uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + (float)var33 + 0.5F) * 0.00390625F + var15).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     var1.vertex((double)(var31 + 8.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var33 + 1.0F - 9.765625E-4F)).uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + (float)var33 + 0.5F) * 0.00390625F + var15).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     var1.vertex((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var33 + 1.0F - 9.765625E-4F)).uv((var31 + 8.0F) * 0.00390625F + var14, (var32 + (float)var33 + 0.5F) * 0.00390625F + var15).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     var1.vertex((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var33 + 1.0F - 9.765625E-4F)).uv((var31 + 0.0F) * 0.00390625F + var14, (var32 + (float)var33 + 0.5F) * 0.00390625F + var15).color(var25, var26, var27, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                  }
               }
            }
         }
      } else {
         boolean var34 = true;
         boolean var35 = true;

         for(int var36 = -32; var36 < 32; var36 += 32) {
            for(int var37 = -32; var37 < 32; var37 += 32) {
               var1.vertex((double)(var36 + 0), (double)var28, (double)(var37 + 32)).uv((float)(var36 + 0) * 0.00390625F + var14, (float)(var37 + 32) * 0.00390625F + var15).color(var16, var17, var18, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               var1.vertex((double)(var36 + 32), (double)var28, (double)(var37 + 32)).uv((float)(var36 + 32) * 0.00390625F + var14, (float)(var37 + 32) * 0.00390625F + var15).color(var16, var17, var18, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               var1.vertex((double)(var36 + 32), (double)var28, (double)(var37 + 0)).uv((float)(var36 + 32) * 0.00390625F + var14, (float)(var37 + 0) * 0.00390625F + var15).color(var16, var17, var18, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               var1.vertex((double)(var36 + 0), (double)var28, (double)(var37 + 0)).uv((float)(var36 + 0) * 0.00390625F + var14, (float)(var37 + 0) * 0.00390625F + var15).color(var16, var17, var18, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
            }
         }
      }

   }

   private void compileChunksUntil(long var1) {
      this.needsUpdate |= this.chunkRenderDispatcher.uploadAllPendingUploads();
      long var3 = Util.getNanos();
      int var5 = 0;
      if (!this.chunksToCompile.isEmpty()) {
         Iterator var6 = this.chunksToCompile.iterator();

         while(var6.hasNext()) {
            ChunkRenderDispatcher.RenderChunk var7 = (ChunkRenderDispatcher.RenderChunk)var6.next();
            if (var7.isDirtyFromPlayer()) {
               this.chunkRenderDispatcher.rebuildChunkSync(var7);
            } else {
               var7.rebuildChunkAsync(this.chunkRenderDispatcher);
            }

            var7.setNotDirty();
            var6.remove();
            ++var5;
            long var8 = Util.getNanos();
            long var10 = var8 - var3;
            long var12 = var10 / (long)var5;
            long var14 = var1 - var8;
            if (var14 < var12) {
               break;
            }
         }
      }

   }

   private void renderWorldBounds(Camera var1) {
      BufferBuilder var2 = Tesselator.getInstance().getBuilder();
      WorldBorder var3 = this.level.getWorldBorder();
      double var4 = (double)(this.minecraft.options.renderDistance * 16);
      if (var1.getPosition().x >= var3.getMaxX() - var4 || var1.getPosition().x <= var3.getMinX() + var4 || var1.getPosition().z >= var3.getMaxZ() - var4 || var1.getPosition().z <= var3.getMinZ() + var4) {
         double var6 = 1.0D - var3.getDistanceToBorder(var1.getPosition().x, var1.getPosition().z) / var4;
         var6 = Math.pow(var6, 4.0D);
         double var8 = var1.getPosition().x;
         double var10 = var1.getPosition().y;
         double var12 = var1.getPosition().z;
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         this.textureManager.bind(FORCEFIELD_LOCATION);
         RenderSystem.depthMask(Minecraft.useShaderTransparency());
         RenderSystem.pushMatrix();
         int var14 = var3.getStatus().getColor();
         float var15 = (float)(var14 >> 16 & 255) / 255.0F;
         float var16 = (float)(var14 >> 8 & 255) / 255.0F;
         float var17 = (float)(var14 & 255) / 255.0F;
         RenderSystem.color4f(var15, var16, var17, (float)var6);
         RenderSystem.polygonOffset(-3.0F, -3.0F);
         RenderSystem.enablePolygonOffset();
         RenderSystem.defaultAlphaFunc();
         RenderSystem.enableAlphaTest();
         RenderSystem.disableCull();
         float var18 = (float)(Util.getMillis() % 3000L) / 3000.0F;
         float var19 = 0.0F;
         float var20 = 0.0F;
         float var21 = 128.0F;
         var2.begin(7, DefaultVertexFormat.POSITION_TEX);
         double var22 = Math.max((double)Mth.floor(var12 - var4), var3.getMinZ());
         double var24 = Math.min((double)Mth.ceil(var12 + var4), var3.getMaxZ());
         float var26;
         double var27;
         double var29;
         float var31;
         if (var8 > var3.getMaxX() - var4) {
            var26 = 0.0F;

            for(var27 = var22; var27 < var24; var26 += 0.5F) {
               var29 = Math.min(1.0D, var24 - var27);
               var31 = (float)var29 * 0.5F;
               this.vertex(var2, var8, var10, var12, var3.getMaxX(), 256, var27, var18 + var26, var18 + 0.0F);
               this.vertex(var2, var8, var10, var12, var3.getMaxX(), 256, var27 + var29, var18 + var31 + var26, var18 + 0.0F);
               this.vertex(var2, var8, var10, var12, var3.getMaxX(), 0, var27 + var29, var18 + var31 + var26, var18 + 128.0F);
               this.vertex(var2, var8, var10, var12, var3.getMaxX(), 0, var27, var18 + var26, var18 + 128.0F);
               ++var27;
            }
         }

         if (var8 < var3.getMinX() + var4) {
            var26 = 0.0F;

            for(var27 = var22; var27 < var24; var26 += 0.5F) {
               var29 = Math.min(1.0D, var24 - var27);
               var31 = (float)var29 * 0.5F;
               this.vertex(var2, var8, var10, var12, var3.getMinX(), 256, var27, var18 + var26, var18 + 0.0F);
               this.vertex(var2, var8, var10, var12, var3.getMinX(), 256, var27 + var29, var18 + var31 + var26, var18 + 0.0F);
               this.vertex(var2, var8, var10, var12, var3.getMinX(), 0, var27 + var29, var18 + var31 + var26, var18 + 128.0F);
               this.vertex(var2, var8, var10, var12, var3.getMinX(), 0, var27, var18 + var26, var18 + 128.0F);
               ++var27;
            }
         }

         var22 = Math.max((double)Mth.floor(var8 - var4), var3.getMinX());
         var24 = Math.min((double)Mth.ceil(var8 + var4), var3.getMaxX());
         if (var12 > var3.getMaxZ() - var4) {
            var26 = 0.0F;

            for(var27 = var22; var27 < var24; var26 += 0.5F) {
               var29 = Math.min(1.0D, var24 - var27);
               var31 = (float)var29 * 0.5F;
               this.vertex(var2, var8, var10, var12, var27, 256, var3.getMaxZ(), var18 + var26, var18 + 0.0F);
               this.vertex(var2, var8, var10, var12, var27 + var29, 256, var3.getMaxZ(), var18 + var31 + var26, var18 + 0.0F);
               this.vertex(var2, var8, var10, var12, var27 + var29, 0, var3.getMaxZ(), var18 + var31 + var26, var18 + 128.0F);
               this.vertex(var2, var8, var10, var12, var27, 0, var3.getMaxZ(), var18 + var26, var18 + 128.0F);
               ++var27;
            }
         }

         if (var12 < var3.getMinZ() + var4) {
            var26 = 0.0F;

            for(var27 = var22; var27 < var24; var26 += 0.5F) {
               var29 = Math.min(1.0D, var24 - var27);
               var31 = (float)var29 * 0.5F;
               this.vertex(var2, var8, var10, var12, var27, 256, var3.getMinZ(), var18 + var26, var18 + 0.0F);
               this.vertex(var2, var8, var10, var12, var27 + var29, 256, var3.getMinZ(), var18 + var31 + var26, var18 + 0.0F);
               this.vertex(var2, var8, var10, var12, var27 + var29, 0, var3.getMinZ(), var18 + var31 + var26, var18 + 128.0F);
               this.vertex(var2, var8, var10, var12, var27, 0, var3.getMinZ(), var18 + var26, var18 + 128.0F);
               ++var27;
            }
         }

         var2.end();
         BufferUploader.end(var2);
         RenderSystem.enableCull();
         RenderSystem.disableAlphaTest();
         RenderSystem.polygonOffset(0.0F, 0.0F);
         RenderSystem.disablePolygonOffset();
         RenderSystem.enableAlphaTest();
         RenderSystem.disableBlend();
         RenderSystem.popMatrix();
         RenderSystem.depthMask(true);
      }
   }

   private void vertex(BufferBuilder var1, double var2, double var4, double var6, double var8, int var10, double var11, float var13, float var14) {
      var1.vertex(var8 - var2, (double)var10 - var4, var11 - var6).uv(var13, var14).endVertex();
   }

   private void renderHitOutline(PoseStack var1, VertexConsumer var2, Entity var3, double var4, double var6, double var8, BlockPos var10, BlockState var11) {
      renderShape(var1, var2, var11.getShape(this.level, var10, CollisionContext.of(var3)), (double)var10.getX() - var4, (double)var10.getY() - var6, (double)var10.getZ() - var8, 0.0F, 0.0F, 0.0F, 0.4F);
   }

   public static void renderVoxelShape(PoseStack var0, VertexConsumer var1, VoxelShape var2, double var3, double var5, double var7, float var9, float var10, float var11, float var12) {
      List var13 = var2.toAabbs();
      int var14 = Mth.ceil((double)var13.size() / 3.0D);

      for(int var15 = 0; var15 < var13.size(); ++var15) {
         AABB var16 = (AABB)var13.get(var15);
         float var17 = ((float)var15 % (float)var14 + 1.0F) / (float)var14;
         float var18 = (float)(var15 / var14);
         float var19 = var17 * (float)(var18 == 0.0F ? 1 : 0);
         float var20 = var17 * (float)(var18 == 1.0F ? 1 : 0);
         float var21 = var17 * (float)(var18 == 2.0F ? 1 : 0);
         renderShape(var0, var1, Shapes.create(var16.move(0.0D, 0.0D, 0.0D)), var3, var5, var7, var19, var20, var21, 1.0F);
      }

   }

   private static void renderShape(PoseStack var0, VertexConsumer var1, VoxelShape var2, double var3, double var5, double var7, float var9, float var10, float var11, float var12) {
      Matrix4f var13 = var0.last().pose();
      var2.forAllEdges((var12x, var14, var16, var18, var20, var22) -> {
         var1.vertex(var13, (float)(var12x + var3), (float)(var14 + var5), (float)(var16 + var7)).color(var9, var10, var11, var12).endVertex();
         var1.vertex(var13, (float)(var18 + var3), (float)(var20 + var5), (float)(var22 + var7)).color(var9, var10, var11, var12).endVertex();
      });
   }

   public static void renderLineBox(PoseStack var0, VertexConsumer var1, AABB var2, float var3, float var4, float var5, float var6) {
      renderLineBox(var0, var1, var2.minX, var2.minY, var2.minZ, var2.maxX, var2.maxY, var2.maxZ, var3, var4, var5, var6, var3, var4, var5);
   }

   public static void renderLineBox(PoseStack var0, VertexConsumer var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, float var15, float var16, float var17) {
      renderLineBox(var0, var1, var2, var4, var6, var8, var10, var12, var14, var15, var16, var17, var14, var15, var16);
   }

   public static void renderLineBox(PoseStack var0, VertexConsumer var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, float var15, float var16, float var17, float var18, float var19, float var20) {
      Matrix4f var21 = var0.last().pose();
      float var22 = (float)var2;
      float var23 = (float)var4;
      float var24 = (float)var6;
      float var25 = (float)var8;
      float var26 = (float)var10;
      float var27 = (float)var12;
      var1.vertex(var21, var22, var23, var24).color(var14, var19, var20, var17).endVertex();
      var1.vertex(var21, var25, var23, var24).color(var14, var19, var20, var17).endVertex();
      var1.vertex(var21, var22, var23, var24).color(var18, var15, var20, var17).endVertex();
      var1.vertex(var21, var22, var26, var24).color(var18, var15, var20, var17).endVertex();
      var1.vertex(var21, var22, var23, var24).color(var18, var19, var16, var17).endVertex();
      var1.vertex(var21, var22, var23, var27).color(var18, var19, var16, var17).endVertex();
      var1.vertex(var21, var25, var23, var24).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var25, var26, var24).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var25, var26, var24).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var22, var26, var24).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var22, var26, var24).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var22, var26, var27).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var22, var26, var27).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var22, var23, var27).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var22, var23, var27).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var25, var23, var27).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var25, var23, var27).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var25, var23, var24).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var22, var26, var27).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var25, var26, var27).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var25, var23, var27).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var25, var26, var27).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var25, var26, var24).color(var14, var15, var16, var17).endVertex();
      var1.vertex(var21, var25, var26, var27).color(var14, var15, var16, var17).endVertex();
   }

   public static void addChainedFilledBoxVertices(BufferBuilder var0, double var1, double var3, double var5, double var7, double var9, double var11, float var13, float var14, float var15, float var16) {
      var0.vertex(var1, var3, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var3, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var3, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var3, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var9, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var9, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var9, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var3, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var9, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var3, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var3, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var3, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var9, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var9, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var9, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var3, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var9, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var3, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var3, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var3, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var3, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var3, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var3, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var9, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var9, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var1, var9, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var9, var5).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var9, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var9, var11).color(var13, var14, var15, var16).endVertex();
      var0.vertex(var7, var9, var11).color(var13, var14, var15, var16).endVertex();
   }

   public void blockChanged(BlockGetter var1, BlockPos var2, BlockState var3, BlockState var4, int var5) {
      this.setBlockDirty(var2, (var5 & 8) != 0);
   }

   private void setBlockDirty(BlockPos var1, boolean var2) {
      for(int var3 = var1.getZ() - 1; var3 <= var1.getZ() + 1; ++var3) {
         for(int var4 = var1.getX() - 1; var4 <= var1.getX() + 1; ++var4) {
            for(int var5 = var1.getY() - 1; var5 <= var1.getY() + 1; ++var5) {
               this.setSectionDirty(var4 >> 4, var5 >> 4, var3 >> 4, var2);
            }
         }
      }

   }

   public void setBlocksDirty(int var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = var3 - 1; var7 <= var6 + 1; ++var7) {
         for(int var8 = var1 - 1; var8 <= var4 + 1; ++var8) {
            for(int var9 = var2 - 1; var9 <= var5 + 1; ++var9) {
               this.setSectionDirty(var8 >> 4, var9 >> 4, var7 >> 4);
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
      SoundInstance var3 = (SoundInstance)this.playingRecords.get(var2);
      if (var3 != null) {
         this.minecraft.getSoundManager().stop(var3);
         this.playingRecords.remove(var2);
      }

      if (var1 != null) {
         RecordItem var4 = RecordItem.getBySound(var1);
         if (var4 != null) {
            this.minecraft.gui.setNowPlaying(var4.getDisplayName());
         }

         SimpleSoundInstance var5 = SimpleSoundInstance.forRecord(var1, (double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
         this.playingRecords.put(var2, var5);
         this.minecraft.getSoundManager().play(var5);
      }

      this.notifyNearbyEntities(this.level, var2, var1 != null);
   }

   private void notifyNearbyEntities(Level var1, BlockPos var2, boolean var3) {
      List var4 = var1.getEntitiesOfClass(LivingEntity.class, (new AABB(var2)).inflate(3.0D));
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
         var18.setDetail("ID", (Object)Registry.PARTICLE_TYPE.getKey(var1.getType()));
         var18.setDetail("Parameters", (Object)var1.writeToString());
         var18.setDetail("Position", () -> {
            return CrashReportCategory.formatLocation(var4, var6, var8);
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
      if (this.minecraft != null && var16.isInitialized() && this.minecraft.particleEngine != null) {
         ParticleStatus var17 = this.calculateParticleLevel(var3);
         if (var2) {
            return this.minecraft.particleEngine.createParticle(var1, var4, var6, var8, var10, var12, var14);
         } else if (var16.getPosition().distanceToSqr(var4, var6, var8) > 1024.0D) {
            return null;
         } else {
            return var17 == ParticleStatus.MINIMAL ? null : this.minecraft.particleEngine.createParticle(var1, var4, var6, var8, var10, var12, var14);
         }
      } else {
         return null;
      }
   }

   private ParticleStatus calculateParticleLevel(boolean var1) {
      ParticleStatus var2 = this.minecraft.options.particles;
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
            if (var11 > 0.0D) {
               var13 += var5 / var11 * 2.0D;
               var15 += var7 / var11 * 2.0D;
               var17 += var9 / var11 * 2.0D;
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

   public void levelEvent(Player var1, int var2, BlockPos var3, int var4) {
      Random var5 = this.level.random;
      int var7;
      float var8;
      float var9;
      double var10;
      double var12;
      double var14;
      double var34;
      switch(var2) {
      case 1000:
         this.level.playLocalSound(var3, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1001:
         this.level.playLocalSound(var3, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1.0F, 1.2F, false);
         break;
      case 1002:
         this.level.playLocalSound(var3, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0F, 1.2F, false);
         break;
      case 1003:
         this.level.playLocalSound(var3, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 1.0F, 1.2F, false);
         break;
      case 1004:
         this.level.playLocalSound(var3, SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.2F, false);
         break;
      case 1005:
         this.level.playLocalSound(var3, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1006:
         this.level.playLocalSound(var3, SoundEvents.WOODEN_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1007:
         this.level.playLocalSound(var3, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1008:
         this.level.playLocalSound(var3, SoundEvents.FENCE_GATE_OPEN, SoundSource.BLOCKS, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1009:
         this.level.playLocalSound(var3, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var5.nextFloat() - var5.nextFloat()) * 0.8F, false);
         break;
      case 1010:
         if (Item.byId(var4) instanceof RecordItem) {
            this.playStreamingMusic(((RecordItem)Item.byId(var4)).getSound(), var3);
         } else {
            this.playStreamingMusic((SoundEvent)null, var3);
         }
         break;
      case 1011:
         this.level.playLocalSound(var3, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1012:
         this.level.playLocalSound(var3, SoundEvents.WOODEN_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1013:
         this.level.playLocalSound(var3, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1014:
         this.level.playLocalSound(var3, SoundEvents.FENCE_GATE_CLOSE, SoundSource.BLOCKS, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1015:
         this.level.playLocalSound(var3, SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1016:
         this.level.playLocalSound(var3, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1017:
         this.level.playLocalSound(var3, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1018:
         this.level.playLocalSound(var3, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1019:
         this.level.playLocalSound(var3, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1020:
         this.level.playLocalSound(var3, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1021:
         this.level.playLocalSound(var3, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1022:
         this.level.playLocalSound(var3, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1024:
         this.level.playLocalSound(var3, SoundEvents.WITHER_SHOOT, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1025:
         this.level.playLocalSound(var3, SoundEvents.BAT_TAKEOFF, SoundSource.NEUTRAL, 0.05F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1026:
         this.level.playLocalSound(var3, SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1027:
         this.level.playLocalSound(var3, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.NEUTRAL, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1029:
         this.level.playLocalSound(var3, SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1030:
         this.level.playLocalSound(var3, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1031:
         this.level.playLocalSound(var3, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1032:
         this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRAVEL, var5.nextFloat() * 0.4F + 0.8F, 0.25F));
         break;
      case 1033:
         this.level.playLocalSound(var3, SoundEvents.CHORUS_FLOWER_GROW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1034:
         this.level.playLocalSound(var3, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1035:
         this.level.playLocalSound(var3, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1036:
         this.level.playLocalSound(var3, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1037:
         this.level.playLocalSound(var3, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1039:
         this.level.playLocalSound(var3, SoundEvents.PHANTOM_BITE, SoundSource.HOSTILE, 0.3F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1040:
         this.level.playLocalSound(var3, SoundEvents.ZOMBIE_CONVERTED_TO_DROWNED, SoundSource.NEUTRAL, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1041:
         this.level.playLocalSound(var3, SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.NEUTRAL, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1042:
         this.level.playLocalSound(var3, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1043:
         this.level.playLocalSound(var3, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1044:
         this.level.playLocalSound(var3, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, this.level.random.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1500:
         ComposterBlock.handleFill(this.level, var3, var4 > 0);
         break;
      case 1501:
         this.level.playLocalSound(var3, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var5.nextFloat() - var5.nextFloat()) * 0.8F, false);

         for(var7 = 0; var7 < 8; ++var7) {
            this.level.addParticle(ParticleTypes.LARGE_SMOKE, (double)var3.getX() + var5.nextDouble(), (double)var3.getY() + 1.2D, (double)var3.getZ() + var5.nextDouble(), 0.0D, 0.0D, 0.0D);
         }

         return;
      case 1502:
         this.level.playLocalSound(var3, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5F, 2.6F + (var5.nextFloat() - var5.nextFloat()) * 0.8F, false);

         for(var7 = 0; var7 < 5; ++var7) {
            var34 = (double)var3.getX() + var5.nextDouble() * 0.6D + 0.2D;
            var10 = (double)var3.getY() + var5.nextDouble() * 0.6D + 0.2D;
            var12 = (double)var3.getZ() + var5.nextDouble() * 0.6D + 0.2D;
            this.level.addParticle(ParticleTypes.SMOKE, var34, var10, var12, 0.0D, 0.0D, 0.0D);
         }

         return;
      case 1503:
         this.level.playLocalSound(var3, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);

         for(var7 = 0; var7 < 16; ++var7) {
            var34 = (double)var3.getX() + (5.0D + var5.nextDouble() * 6.0D) / 16.0D;
            var10 = (double)var3.getY() + 0.8125D;
            var12 = (double)var3.getZ() + (5.0D + var5.nextDouble() * 6.0D) / 16.0D;
            this.level.addParticle(ParticleTypes.SMOKE, var34, var10, var12, 0.0D, 0.0D, 0.0D);
         }

         return;
      case 2000:
         Direction var33 = Direction.from3DDataValue(var4);
         var7 = var33.getStepX();
         int var35 = var33.getStepY();
         int var36 = var33.getStepZ();
         var10 = (double)var3.getX() + (double)var7 * 0.6D + 0.5D;
         var12 = (double)var3.getY() + (double)var35 * 0.6D + 0.5D;
         var14 = (double)var3.getZ() + (double)var36 * 0.6D + 0.5D;

         for(int var42 = 0; var42 < 10; ++var42) {
            double var17 = var5.nextDouble() * 0.2D + 0.01D;
            double var19 = var10 + (double)var7 * 0.01D + (var5.nextDouble() - 0.5D) * (double)var36 * 0.5D;
            double var21 = var12 + (double)var35 * 0.01D + (var5.nextDouble() - 0.5D) * (double)var35 * 0.5D;
            double var43 = var14 + (double)var36 * 0.01D + (var5.nextDouble() - 0.5D) * (double)var7 * 0.5D;
            double var25 = (double)var7 * var17 + var5.nextGaussian() * 0.01D;
            double var27 = (double)var35 * var17 + var5.nextGaussian() * 0.01D;
            double var29 = (double)var36 * var17 + var5.nextGaussian() * 0.01D;
            this.addParticle(ParticleTypes.SMOKE, var19, var21, var43, var25, var27, var29);
         }

         return;
      case 2001:
         BlockState var32 = Block.stateById(var4);
         if (!var32.isAir()) {
            SoundType var38 = var32.getSoundType();
            this.level.playLocalSound(var3, var38.getBreakSound(), SoundSource.BLOCKS, (var38.getVolume() + 1.0F) / 2.0F, var38.getPitch() * 0.8F, false);
         }

         this.minecraft.particleEngine.destroy(var3, var32);
         break;
      case 2002:
      case 2007:
         Vec3 var31 = Vec3.atBottomCenterOf(var3);

         for(var7 = 0; var7 < 8; ++var7) {
            this.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), var31.x, var31.y, var31.z, var5.nextGaussian() * 0.15D, var5.nextDouble() * 0.2D, var5.nextGaussian() * 0.15D);
         }

         float var37 = (float)(var4 >> 16 & 255) / 255.0F;
         var8 = (float)(var4 >> 8 & 255) / 255.0F;
         var9 = (float)(var4 >> 0 & 255) / 255.0F;
         SimpleParticleType var39 = var2 == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;

         for(int var11 = 0; var11 < 100; ++var11) {
            var12 = var5.nextDouble() * 4.0D;
            var14 = var5.nextDouble() * 3.141592653589793D * 2.0D;
            double var41 = Math.cos(var14) * var12;
            double var18 = 0.01D + var5.nextDouble() * 0.5D;
            double var20 = Math.sin(var14) * var12;
            Particle var22 = this.addParticleInternal(var39, var39.getType().getOverrideLimiter(), var31.x + var41 * 0.1D, var31.y + 0.3D, var31.z + var20 * 0.1D, var41, var18, var20);
            if (var22 != null) {
               float var23 = 0.75F + var5.nextFloat() * 0.25F;
               var22.setColor(var37 * var23, var8 * var23, var9 * var23);
               var22.setPower((float)var12);
            }
         }

         this.level.playLocalSound(var3, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 2003:
         double var6 = (double)var3.getX() + 0.5D;
         var34 = (double)var3.getY();
         var10 = (double)var3.getZ() + 0.5D;

         for(int var40 = 0; var40 < 8; ++var40) {
            this.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), var6, var34, var10, var5.nextGaussian() * 0.15D, var5.nextDouble() * 0.2D, var5.nextGaussian() * 0.15D);
         }

         for(var12 = 0.0D; var12 < 6.283185307179586D; var12 += 0.15707963267948966D) {
            this.addParticle(ParticleTypes.PORTAL, var6 + Math.cos(var12) * 5.0D, var34 - 0.4D, var10 + Math.sin(var12) * 5.0D, Math.cos(var12) * -5.0D, 0.0D, Math.sin(var12) * -5.0D);
            this.addParticle(ParticleTypes.PORTAL, var6 + Math.cos(var12) * 5.0D, var34 - 0.4D, var10 + Math.sin(var12) * 5.0D, Math.cos(var12) * -7.0D, 0.0D, Math.sin(var12) * -7.0D);
         }

         return;
      case 2004:
         for(var7 = 0; var7 < 20; ++var7) {
            var34 = (double)var3.getX() + 0.5D + (var5.nextDouble() - 0.5D) * 2.0D;
            var10 = (double)var3.getY() + 0.5D + (var5.nextDouble() - 0.5D) * 2.0D;
            var12 = (double)var3.getZ() + 0.5D + (var5.nextDouble() - 0.5D) * 2.0D;
            this.level.addParticle(ParticleTypes.SMOKE, var34, var10, var12, 0.0D, 0.0D, 0.0D);
            this.level.addParticle(ParticleTypes.FLAME, var34, var10, var12, 0.0D, 0.0D, 0.0D);
         }

         return;
      case 2005:
         BoneMealItem.addGrowthParticles(this.level, var3, var4);
         break;
      case 2006:
         for(var7 = 0; var7 < 200; ++var7) {
            var8 = var5.nextFloat() * 4.0F;
            var9 = var5.nextFloat() * 6.2831855F;
            var10 = (double)(Mth.cos(var9) * var8);
            var12 = 0.01D + var5.nextDouble() * 0.5D;
            var14 = (double)(Mth.sin(var9) * var8);
            Particle var16 = this.addParticleInternal(ParticleTypes.DRAGON_BREATH, false, (double)var3.getX() + var10 * 0.1D, (double)var3.getY() + 0.3D, (double)var3.getZ() + var14 * 0.1D, var10, var12, var14);
            if (var16 != null) {
               var16.setPower(var8);
            }
         }

         if (var4 == 1) {
            this.level.playLocalSound(var3, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.0F, var5.nextFloat() * 0.1F + 0.9F, false);
         }
         break;
      case 2008:
         this.level.addParticle(ParticleTypes.EXPLOSION, (double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
         break;
      case 2009:
         for(var7 = 0; var7 < 8; ++var7) {
            this.level.addParticle(ParticleTypes.CLOUD, (double)var3.getX() + var5.nextDouble(), (double)var3.getY() + 1.2D, (double)var3.getZ() + var5.nextDouble(), 0.0D, 0.0D, 0.0D);
         }

         return;
      case 3000:
         this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
         this.level.playLocalSound(var3, SoundEvents.END_GATEWAY_SPAWN, SoundSource.BLOCKS, 10.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
         break;
      case 3001:
         this.level.playLocalSound(var3, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 64.0F, 0.8F + this.level.random.nextFloat() * 0.3F, false);
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

   public boolean hasRenderedAllChunks() {
      return this.chunksToCompile.isEmpty() && this.chunkRenderDispatcher.isQueueEmpty();
   }

   public void needsUpdate() {
      this.needsUpdate = true;
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

   public static class TransparencyShaderException extends RuntimeException {
      public TransparencyShaderException(String var1, Throwable var2) {
         super(var1, var2);
      }
   }

   class RenderChunkInfo {
      private final ChunkRenderDispatcher.RenderChunk chunk;
      private final Direction sourceDirection;
      private byte directions;
      private final int step;

      private RenderChunkInfo(ChunkRenderDispatcher.RenderChunk var2, Direction var3, @Nullable int var4) {
         super();
         this.chunk = var2;
         this.sourceDirection = var3;
         this.step = var4;
      }

      public void setDirections(byte var1, Direction var2) {
         this.directions = (byte)(this.directions | var1 | 1 << var2.ordinal());
      }

      public boolean hasDirection(Direction var1) {
         return (this.directions & 1 << var1.ordinal()) > 0;
      }

      // $FF: synthetic method
      RenderChunkInfo(ChunkRenderDispatcher.RenderChunk var2, Direction var3, int var4, Object var5) {
         this(var2, var3, var4);
      }
   }
}
