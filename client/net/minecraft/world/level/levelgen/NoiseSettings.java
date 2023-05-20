package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.dimension.DimensionType;

public record NoiseSettings(int g, int h, int i, int j) {
   private final int minY;
   private final int height;
   private final int noiseSizeHorizontal;
   private final int noiseSizeVertical;
   public static final Codec<NoiseSettings> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.intRange(DimensionType.MIN_Y, DimensionType.MAX_Y).fieldOf("min_y").forGetter(NoiseSettings::minY),
                  Codec.intRange(0, DimensionType.Y_SIZE).fieldOf("height").forGetter(NoiseSettings::height),
                  Codec.intRange(1, 4).fieldOf("size_horizontal").forGetter(NoiseSettings::noiseSizeHorizontal),
                  Codec.intRange(1, 4).fieldOf("size_vertical").forGetter(NoiseSettings::noiseSizeVertical)
               )
               .apply(var0, NoiseSettings::new)
      )
      .comapFlatMap(NoiseSettings::guardY, Function.identity());
   protected static final NoiseSettings OVERWORLD_NOISE_SETTINGS = create(-64, 384, 1, 2);
   protected static final NoiseSettings NETHER_NOISE_SETTINGS = create(0, 128, 1, 2);
   protected static final NoiseSettings END_NOISE_SETTINGS = create(0, 128, 2, 1);
   protected static final NoiseSettings CAVES_NOISE_SETTINGS = create(-64, 192, 1, 2);
   protected static final NoiseSettings FLOATING_ISLANDS_NOISE_SETTINGS = create(0, 256, 2, 1);

   public NoiseSettings(int var1, int var2, int var3, int var4) {
      super();
      this.minY = var1;
      this.height = var2;
      this.noiseSizeHorizontal = var3;
      this.noiseSizeVertical = var4;
   }

   private static DataResult<NoiseSettings> guardY(NoiseSettings var0) {
      if (var0.minY() + var0.height() > DimensionType.MAX_Y + 1) {
         return DataResult.error(() -> "min_y + height cannot be higher than: " + (DimensionType.MAX_Y + 1));
      } else if (var0.height() % 16 != 0) {
         return DataResult.error(() -> "height has to be a multiple of 16");
      } else {
         return var0.minY() % 16 != 0 ? DataResult.error(() -> "min_y has to be a multiple of 16") : DataResult.success(var0);
      }
   }

   public static NoiseSettings create(int var0, int var1, int var2, int var3) {
      NoiseSettings var4 = new NoiseSettings(var0, var1, var2, var3);
      guardY(var4).error().ifPresent(var0x -> {
         throw new IllegalStateException(var0x.message());
      });
      return var4;
   }

   public int getCellHeight() {
      return QuartPos.toBlock(this.noiseSizeVertical());
   }

   public int getCellWidth() {
      return QuartPos.toBlock(this.noiseSizeHorizontal());
   }

   public NoiseSettings clampToHeightAccessor(LevelHeightAccessor var1) {
      int var2 = Math.max(this.minY, var1.getMinBuildHeight());
      int var3 = Math.min(this.minY + this.height, var1.getMaxBuildHeight()) - var2;
      return new NoiseSettings(var2, var3, this.noiseSizeHorizontal, this.noiseSizeVertical);
   }
}
