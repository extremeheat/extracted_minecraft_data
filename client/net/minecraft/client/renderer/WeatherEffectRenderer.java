package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.state.WeatherRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class WeatherEffectRenderer {
   private static final float RAIN_PARTICLES_PER_BLOCK = 0.225F;
   private static final int RAIN_RADIUS = 10;
   private static final Identifier RAIN_LOCATION = Identifier.withDefaultNamespace("textures/environment/rain.png");
   private static final Identifier SNOW_LOCATION = Identifier.withDefaultNamespace("textures/environment/snow.png");
   private static final int RAIN_TABLE_SIZE = 32;
   private static final int HALF_RAIN_TABLE_SIZE = 16;
   private static final int INDICES_PER_COLUMN = 6;
   private int rainSoundTime;
   private final float[] columnSizeX = new float[1024];
   private final float[] columnSizeZ = new float[1024];

   public WeatherEffectRenderer() {
      super();

      for(int z = 0; z < 32; ++z) {
         for(int x = 0; x < 32; ++x) {
            float deltaX = (float)(x - 16);
            float deltaZ = (float)(z - 16);
            float distance = Mth.length(deltaX, deltaZ);
            this.columnSizeX[z * 32 + x] = -deltaZ / distance;
            this.columnSizeZ[z * 32 + x] = deltaX / distance;
         }
      }

   }

   public void extractRenderState(final Level level, final int ticks, final float partialTicks, final Vec3 cameraPos, final WeatherRenderState renderState) {
      renderState.intensity = level.getRainLevel(partialTicks);
      if (!(renderState.intensity <= 0.0F)) {
         renderState.radius = (Integer)Minecraft.getInstance().options.weatherRadius().get();
         int cameraBlockX = Mth.floor(cameraPos.x);
         int cameraBlockY = Mth.floor(cameraPos.y);
         int cameraBlockZ = Mth.floor(cameraPos.z);
         BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
         RandomSource random = RandomSource.createThreadLocalInstance();

         for(int z = cameraBlockZ - renderState.radius; z <= cameraBlockZ + renderState.radius; ++z) {
            for(int x = cameraBlockX - renderState.radius; x <= cameraBlockX + renderState.radius; ++x) {
               int terrainHeight = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
               int y0 = Math.max(cameraBlockY - renderState.radius, terrainHeight);
               int y1 = Math.max(cameraBlockY + renderState.radius, terrainHeight);
               if (y1 - y0 != 0) {
                  Biome.Precipitation precipitation = this.getPrecipitationAt(level, mutablePos.set(x, cameraBlockY, z));
                  if (precipitation != Biome.Precipitation.NONE) {
                     int seed = x * x * 3121 + x * 45238971 ^ z * z * 418711 + z * 13761;
                     random.setSeed((long)seed);
                     int lightSampleY = Math.max(cameraBlockY, terrainHeight);
                     int lightCoords = LevelRenderer.getLightCoords(level, mutablePos.set(x, lightSampleY, z));
                     if (precipitation == Biome.Precipitation.RAIN) {
                        renderState.rainColumns.add(this.createRainColumnInstance(random, ticks, x, y0, y1, z, lightCoords, partialTicks));
                     } else if (precipitation == Biome.Precipitation.SNOW) {
                        renderState.snowColumns.add(this.createSnowColumnInstance(random, ticks, x, y0, y1, z, lightCoords, partialTicks));
                     }
                  }
               }
            }
         }

      }
   }

   private void renderWeather(final RenderPass renderPass, final AbstractTexture texture, final int startColumn, final int columnCount) {
      renderPass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());
      renderPass.drawIndexed(0, startColumn * 6, columnCount * 6, 1);
   }

   public void render(final Vec3 cameraPos, final WeatherRenderState renderState) {
      int columnCount = renderState.rainColumns.size() + renderState.snowColumns.size();
      if (columnCount != 0) {
         TextureManager textureManager = Minecraft.getInstance().getTextureManager();
         AbstractTexture rainTexture = textureManager.getTexture(RAIN_LOCATION);
         AbstractTexture snowTexture = textureManager.getTexture(SNOW_LOCATION);
         RenderTarget weatherRenderTarget = OutputTarget.WEATHER_TARGET.getRenderTarget();
         GpuTextureView colorTexture = weatherRenderTarget.getColorTextureView();
         GpuTextureView depthTexture = weatherRenderTarget.getDepthTextureView();
         RenderPipeline renderPipeline = Minecraft.useShaderTransparency() ? RenderPipelines.WEATHER_DEPTH_WRITE : RenderPipelines.WEATHER_NO_DEPTH_WRITE;

         GpuBuffer vertexBuffer;
         GpuBuffer indexBuffer;
         VertexFormat.IndexType indexType;
         try (ByteBufferBuilder builder = ByteBufferBuilder.exactlySized(columnCount * DefaultVertexFormat.PARTICLE.getVertexSize() * 4)) {
            BufferBuilder bufferBuilder = new BufferBuilder(builder, VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
            this.renderInstances(bufferBuilder, renderState.rainColumns, cameraPos, 1.0F, renderState.radius, renderState.intensity);
            this.renderInstances(bufferBuilder, renderState.snowColumns, cameraPos, 0.8F, renderState.radius, renderState.intensity);

            try (MeshData mesh = bufferBuilder.buildOrThrow()) {
               vertexBuffer = renderPipeline.getVertexFormat().uploadImmediateVertexBuffer(mesh.vertexBuffer());
               RenderSystem.AutoStorageIndexBuffer autoIndices = RenderSystem.getSequentialBuffer(mesh.drawState().mode());
               indexBuffer = autoIndices.getBuffer(mesh.drawState().indexCount());
               indexType = autoIndices.type();
            }
         }

         GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f());

         try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Weather Effect", colorTexture, OptionalInt.empty(), depthTexture, OptionalDouble.empty())) {
            renderPass.setPipeline(renderPipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.bindTexture("Sampler2", Minecraft.getInstance().gameRenderer.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
            renderPass.setIndexBuffer(indexBuffer, indexType);
            renderPass.setVertexBuffer(0, vertexBuffer);
            this.renderWeather(renderPass, rainTexture, 0, renderState.rainColumns.size());
            this.renderWeather(renderPass, snowTexture, renderState.rainColumns.size(), renderState.snowColumns.size());
         }

      }
   }

   private ColumnInstance createRainColumnInstance(final RandomSource random, final int ticks, final int x, final int bottomY, final int topY, final int z, final int lightCoords, final float partialTicks) {
      int wrappedTicks = ticks & 131071;
      int tickOffset = x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 & 255;
      float blockPosRainSpeed = 3.0F + random.nextFloat();
      float textureOffset = -((float)(wrappedTicks + tickOffset) + partialTicks) / 32.0F * blockPosRainSpeed;
      float wrappedTextureOffset = textureOffset % 32.0F;
      return new ColumnInstance(x, z, bottomY, topY, 0.0F, wrappedTextureOffset, lightCoords);
   }

   private ColumnInstance createSnowColumnInstance(final RandomSource random, final int ticks, final int x, final int bottomY, final int topY, final int z, final int lightCoords, final float partialTicks) {
      float time = (float)ticks + partialTicks;
      float u = (float)(random.nextDouble() + (double)(time * 0.01F * (float)random.nextGaussian()));
      float v = (float)(random.nextDouble() + (double)(time * (float)random.nextGaussian() * 0.001F));
      float vOffset = -((float)(ticks & 511) + partialTicks) / 512.0F;
      int brightenedLightCoords = LightCoordsUtil.pack((LightCoordsUtil.block(lightCoords) * 3 + 15) / 4, (LightCoordsUtil.sky(lightCoords) * 3 + 15) / 4);
      return new ColumnInstance(x, z, bottomY, topY, u, vOffset + v, brightenedLightCoords);
   }

   private void renderInstances(final VertexConsumer builder, final List<ColumnInstance> columns, final Vec3 cameraPos, final float maxAlpha, final int radius, final float intensity) {
      if (!columns.isEmpty()) {
         float radiusSq = (float)(radius * radius);

         for(ColumnInstance column : columns) {
            float relativeX = (float)((double)column.x + 0.5 - cameraPos.x);
            float relativeZ = (float)((double)column.z + 0.5 - cameraPos.z);
            float distanceSq = (float)Mth.lengthSquared((double)relativeX, (double)relativeZ);
            float alpha = Mth.lerp(Math.min(distanceSq / radiusSq, 1.0F), maxAlpha, 0.5F) * intensity;
            int color = ARGB.white(alpha);
            int index = (column.z - Mth.floor(cameraPos.z) + 16) * 32 + column.x - Mth.floor(cameraPos.x) + 16;
            float halfSizeX = this.columnSizeX[index] / 2.0F;
            float halfSizeZ = this.columnSizeZ[index] / 2.0F;
            float x0 = relativeX - halfSizeX;
            float x1 = relativeX + halfSizeX;
            float y1 = (float)((double)column.topY - cameraPos.y);
            float y0 = (float)((double)column.bottomY - cameraPos.y);
            float z0 = relativeZ - halfSizeZ;
            float z1 = relativeZ + halfSizeZ;
            float u0 = column.uOffset + 0.0F;
            float u1 = column.uOffset + 1.0F;
            float v0 = (float)column.bottomY * 0.25F + column.vOffset;
            float v1 = (float)column.topY * 0.25F + column.vOffset;
            builder.addVertex(x0, y1, z0).setUv(u0, v0).setColor(color).setLight(column.lightCoords);
            builder.addVertex(x1, y1, z1).setUv(u1, v0).setColor(color).setLight(column.lightCoords);
            builder.addVertex(x1, y0, z1).setUv(u1, v1).setColor(color).setLight(column.lightCoords);
            builder.addVertex(x0, y0, z0).setUv(u0, v1).setColor(color).setLight(column.lightCoords);
         }

      }
   }

   public void tickRainParticles(final ClientLevel level, final Camera camera, final int ticks, final ParticleStatus particleStatus, final int weatherRadius) {
      float rainLevel = level.getRainLevel(1.0F);
      if (!(rainLevel <= 0.0F)) {
         RandomSource random = RandomSource.createThreadLocalInstance((long)ticks * 312987231L);
         BlockPos cameraPosition = BlockPos.containing(camera.position());
         BlockPos rainParticlePosition = null;
         int weatherDiameter = 2 * weatherRadius + 1;
         int weatherArea = weatherDiameter * weatherDiameter;
         int rainParticles = (int)(0.225F * (float)weatherArea * rainLevel * rainLevel) / (particleStatus == ParticleStatus.DECREASED ? 2 : 1);

         for(int ii = 0; ii < rainParticles; ++ii) {
            int x = random.nextInt(weatherDiameter) - weatherRadius;
            int z = random.nextInt(weatherDiameter) - weatherRadius;
            BlockPos heightmapPosition = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, cameraPosition.offset(x, 0, z));
            if (heightmapPosition.getY() > level.getMinY() && heightmapPosition.getY() <= cameraPosition.getY() + 10 && heightmapPosition.getY() >= cameraPosition.getY() - 10 && this.getPrecipitationAt(level, heightmapPosition) == Biome.Precipitation.RAIN) {
               rainParticlePosition = heightmapPosition.below();
               if (particleStatus == ParticleStatus.MINIMAL) {
                  break;
               }

               double blockX = random.nextDouble();
               double blockZ = random.nextDouble();
               BlockState block = level.getBlockState(rainParticlePosition);
               FluidState fluid = level.getFluidState(rainParticlePosition);
               VoxelShape blockShape = block.getCollisionShape(level, rainParticlePosition);
               double blockTop = blockShape.max(Direction.Axis.Y, blockX, blockZ);
               double fluidTop = (double)fluid.getHeight(level, rainParticlePosition);
               double particleY = Math.max(blockTop, fluidTop);
               ParticleOptions particleType = !fluid.is(FluidTags.LAVA) && !block.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(block) ? ParticleTypes.RAIN : ParticleTypes.SMOKE;
               level.addParticle(particleType, (double)rainParticlePosition.getX() + blockX, (double)rainParticlePosition.getY() + particleY, (double)rainParticlePosition.getZ() + blockZ, 0.0, 0.0, 0.0);
            }
         }

         if (rainParticlePosition != null && random.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            if (rainParticlePosition.getY() > cameraPosition.getY() + 1 && level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, cameraPosition).getY() > Mth.floor((float)cameraPosition.getY())) {
               level.playLocalSound(rainParticlePosition, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1F, 0.5F, false);
            } else {
               level.playLocalSound(rainParticlePosition, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2F, 1.0F, false);
            }
         }

      }
   }

   private Biome.Precipitation getPrecipitationAt(final Level level, final BlockPos pos) {
      if (!level.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))) {
         return Biome.Precipitation.NONE;
      } else {
         Biome biome = (Biome)level.getBiome(pos).value();
         return biome.getPrecipitationAt(pos, level.getSeaLevel());
      }
   }

   public static record ColumnInstance(int x, int z, int bottomY, int topY, float uOffset, float vOffset, int lightCoords) {
      public ColumnInstance {
         super();
      }
   }
}
