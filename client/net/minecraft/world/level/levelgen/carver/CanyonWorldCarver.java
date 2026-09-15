package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.CarverOutput;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public record CanyonWorldCarver(float probability, HeightProvider y, FloatProvider verticalRotation, Shape shape) implements WorldCarver {
   public static final MapCodec<CanyonWorldCarver> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((c) -> c.probability), HeightProvider.CODEC.fieldOf("y").forGetter((c) -> c.y), FloatProviders.CODEC.fieldOf("vertical_rotation").forGetter((c) -> c.verticalRotation), CanyonWorldCarver.Shape.CODEC.fieldOf("shape").forGetter((c) -> c.shape)).apply(i, CanyonWorldCarver::new));

   public CanyonWorldCarver {
      super();
   }

   public boolean isStartChunk(final RandomSource random) {
      return random.nextFloat() <= this.probability;
   }

   public boolean carve(final WorldGenerationContext context, final RandomSource random, final ChunkPos chunkPos, final ChunkPos sourceChunkPos, final CarverOutput output) {
      int maxDistance = (this.getRange() * 2 - 1) * 16;
      double x = (double)sourceChunkPos.getBlockX(random.nextInt(16));
      int y = this.y.sample(random, context);
      double z = (double)sourceChunkPos.getBlockZ(random.nextInt(16));
      float horizontalRotation = random.nextFloat() * 6.2831855F;
      float verticalRotation = this.verticalRotation.sample(random);
      double yScale = (double)this.shape.yScale().sample(random);
      float thickness = this.shape.thickness().sample(random);
      int distance = (int)((float)maxDistance * this.shape.distanceFactor().sample(random));
      int initialStep = 0;
      this.doCarve(context, chunkPos, random.nextLong(), x, (double)y, z, thickness, horizontalRotation, verticalRotation, 0, distance, yScale, output);
      return true;
   }

   private void doCarve(final WorldGenerationContext context, final ChunkPos chunkPos, final long tunnelSeed, double x, double y, double z, final float thickness, float horizontalRotation, float verticalRotation, final int step, final int distance, final double yScale, final CarverOutput output) {
      RandomSource random = RandomSource.createThreadLocalInstance(tunnelSeed);
      float[] widthFactorPerHeight = this.initWidthFactors(context, random);
      float yRota = 0.0F;
      float xRota = 0.0F;

      for(int currentStep = step; currentStep < distance; ++currentStep) {
         double horizontalRadius = 1.5 + (double)(Mth.sin((double)((float)currentStep * 3.1415927F / (float)distance)) * thickness);
         double verticalRadius = horizontalRadius * yScale;
         horizontalRadius *= (double)this.shape.horizontalRadiusFactor().sample(random);
         verticalRadius = this.updateVerticalRadius(random, verticalRadius, (float)distance, (float)currentStep);
         float xc = Mth.cos((double)verticalRotation);
         float xs = Mth.sin((double)verticalRotation);
         x += (double)(Mth.cos((double)horizontalRotation) * xc);
         y += (double)xs;
         z += (double)(Mth.sin((double)horizontalRotation) * xc);
         verticalRotation *= 0.7F;
         verticalRotation += xRota * 0.05F;
         horizontalRotation += yRota * 0.05F;
         xRota *= 0.8F;
         yRota *= 0.5F;
         xRota += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         yRota += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
         if (random.nextInt(4) != 0) {
            if (!WorldCarver.canReach(chunkPos, x, z, currentStep, distance, thickness)) {
               return;
            }

            WorldCarver.carveEllipsoid(chunkPos, x, y, z, horizontalRadius, verticalRadius, output, (xd, yd, zd, y1) -> this.shouldSkip(context, widthFactorPerHeight, xd, yd, zd, y1));
         }
      }

   }

   private float[] initWidthFactors(final WorldGenerationContext context, final RandomSource random) {
      int depth = context.getGenDepth();
      float[] widthFactorPerHeight = new float[depth];
      float widthFactor = 1.0F;

      for(int yIndex = 0; yIndex < depth; ++yIndex) {
         if (yIndex == 0 || random.nextInt(this.shape.widthSmoothness()) == 0) {
            widthFactor = 1.0F + random.nextFloat() * random.nextFloat();
         }

         widthFactorPerHeight[yIndex] = widthFactor * widthFactor;
      }

      return widthFactorPerHeight;
   }

   private double updateVerticalRadius(final RandomSource random, final double verticalRadius, final float distance, final float currentStep) {
      float verticalMultiplier = 1.0F - Mth.abs(0.5F - currentStep / distance) * 2.0F;
      float factor = this.shape.verticalRadiusDefaultFactor() + this.shape.verticalRadiusCenterFactor() * verticalMultiplier;
      return (double)factor * verticalRadius * (double)Mth.randomBetween(random, 0.75F, 1.0F);
   }

   private boolean shouldSkip(final WorldGenerationContext context, final float[] widthFactorPerHeight, final double xd, final double yd, final double zd, final int y) {
      int yIndex = y - context.getMinGenY();
      return (xd * xd + zd * zd) * (double)widthFactorPerHeight[yIndex - 1] + yd * yd / 6.0 >= 1.0;
   }

   public MapCodec<CanyonWorldCarver> codec() {
      return MAP_CODEC;
   }

   public static record Shape(FloatProvider distanceFactor, FloatProvider thickness, int widthSmoothness, FloatProvider horizontalRadiusFactor, float verticalRadiusDefaultFactor, float verticalRadiusCenterFactor, FloatProvider yScale) {
      public static final Codec<Shape> CODEC = RecordCodecBuilder.create((i) -> i.group(FloatProviders.CODEC.fieldOf("distance_factor").forGetter((c) -> c.distanceFactor), FloatProviders.CODEC.fieldOf("thickness").forGetter((c) -> c.thickness), ExtraCodecs.POSITIVE_INT.fieldOf("width_smoothness").forGetter((c) -> c.widthSmoothness), FloatProviders.CODEC.fieldOf("horizontal_radius_factor").forGetter((c) -> c.horizontalRadiusFactor), Codec.FLOAT.fieldOf("vertical_radius_default_factor").forGetter((c) -> c.verticalRadiusDefaultFactor), Codec.FLOAT.fieldOf("vertical_radius_center_factor").forGetter((c) -> c.verticalRadiusCenterFactor), FloatProviders.CODEC.fieldOf("y_scale").forGetter((c) -> c.yScale)).apply(i, Shape::new));

      public Shape {
         super();
      }
   }
}
