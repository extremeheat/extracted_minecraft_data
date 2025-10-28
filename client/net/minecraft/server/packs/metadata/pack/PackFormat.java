package net.minecraft.server.packs.metadata.pack;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record PackFormat(int major, int minor) implements Comparable<PackFormat> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<PackFormat> BOTTOM_CODEC = fullCodec(0);
   public static final Codec<PackFormat> TOP_CODEC = fullCodec(2147483647);

   public PackFormat(int var1, int var2) {
      super();
      this.major = var1;
      this.minor = var2;
   }

   private static Codec<PackFormat> fullCodec(int var0) {
      return ExtraCodecs.compactListCodec(ExtraCodecs.NON_NEGATIVE_INT, ExtraCodecs.NON_NEGATIVE_INT.listOf(1, 256)).xmap((var1) -> var1.size() > 1 ? of((Integer)var1.getFirst(), (Integer)var1.get(1)) : of((Integer)var1.getFirst(), var0), (var1) -> var1.minor != var0 ? List.of(var1.major(), var1.minor()) : List.of(var1.major()));
   }

   public static <ResultType, HolderType extends IntermediaryFormatHolder> DataResult<List<ResultType>> validateHolderList(List<HolderType> var0, int var1, BiFunction<HolderType, InclusiveRange<PackFormat>, ResultType> var2) {
      int var3 = var0.stream().map(IntermediaryFormatHolder::format).mapToInt(IntermediaryFormat::effectiveMinMajorVersion).min().orElse(2147483647);
      ArrayList var4 = new ArrayList(var0.size());

      for(IntermediaryFormatHolder var6 : var0) {
         IntermediaryFormat var7 = var6.format();
         if (var7.min().isEmpty() && var7.max().isEmpty() && var7.supported().isEmpty()) {
            LOGGER.warn("Unknown or broken overlay entry {}", var6);
         } else {
            DataResult var8 = var7.validate(var1, false, var3 <= var1, "Overlay \"" + String.valueOf(var6) + "\"", "formats");
            if (!var8.isSuccess()) {
               DataResult.Error var10000 = (DataResult.Error)var8.error().get();
               Objects.requireNonNull(var10000);
               return DataResult.error(var10000::message);
            }

            var4.add(var2.apply(var6, (InclusiveRange)var8.getOrThrow()));
         }
      }

      return DataResult.success(List.copyOf(var4));
   }

   @VisibleForTesting
   public static int lastPreMinorVersion(PackType var0) {
      byte var10000;
      switch (var0) {
         case CLIENT_RESOURCES -> var10000 = 64;
         case SERVER_DATA -> var10000 = 81;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static MapCodec<InclusiveRange<PackFormat>> packCodec(PackType var0) {
      int var1 = lastPreMinorVersion(var0);
      return PackFormat.IntermediaryFormat.PACK_CODEC.flatXmap((var1x) -> var1x.validate(var1, true, false, "Pack", "supported_formats"), (var1x) -> DataResult.success(PackFormat.IntermediaryFormat.fromRange(var1x, var1)));
   }

   public static PackFormat of(int var0, int var1) {
      return new PackFormat(var0, var1);
   }

   public static PackFormat of(int var0) {
      return new PackFormat(var0, 0);
   }

   public InclusiveRange<PackFormat> minorRange() {
      return new InclusiveRange<PackFormat>(this, of(this.major, 2147483647));
   }

   public int compareTo(PackFormat var1) {
      int var2 = Integer.compare(this.major(), var1.major());
      return var2 != 0 ? var2 : Integer.compare(this.minor(), var1.minor());
   }

   public String toString() {
      return this.minor == 2147483647 ? String.format(Locale.ROOT, "%d.*", this.major()) : String.format(Locale.ROOT, "%d.%d", this.major(), this.minor());
   }

   // $FF: synthetic method
   public int compareTo(final Object var1) {
      return this.compareTo((PackFormat)var1);
   }

   public static record IntermediaryFormat(Optional<PackFormat> min, Optional<PackFormat> max, Optional<Integer> format, Optional<InclusiveRange<Integer>> supported) {
      static final MapCodec<IntermediaryFormat> PACK_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(PackFormat.BOTTOM_CODEC.optionalFieldOf("min_format").forGetter(IntermediaryFormat::min), PackFormat.TOP_CODEC.optionalFieldOf("max_format").forGetter(IntermediaryFormat::max), Codec.INT.optionalFieldOf("pack_format").forGetter(IntermediaryFormat::format), InclusiveRange.codec(Codec.INT).optionalFieldOf("supported_formats").forGetter(IntermediaryFormat::supported)).apply(var0, IntermediaryFormat::new));
      public static final MapCodec<IntermediaryFormat> OVERLAY_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(PackFormat.BOTTOM_CODEC.optionalFieldOf("min_format").forGetter(IntermediaryFormat::min), PackFormat.TOP_CODEC.optionalFieldOf("max_format").forGetter(IntermediaryFormat::max), InclusiveRange.codec(Codec.INT).optionalFieldOf("formats").forGetter(IntermediaryFormat::supported)).apply(var0, (var0x, var1, var2) -> new IntermediaryFormat(var0x, var1, var0x.map(PackFormat::major), var2)));

      public IntermediaryFormat(Optional<PackFormat> var1, Optional<PackFormat> var2, Optional<Integer> var3, Optional<InclusiveRange<Integer>> var4) {
         super();
         this.min = var1;
         this.max = var2;
         this.format = var3;
         this.supported = var4;
      }

      public static IntermediaryFormat fromRange(InclusiveRange<PackFormat> var0, int var1) {
         InclusiveRange var2 = var0.map(PackFormat::major);
         return new IntermediaryFormat(Optional.of((PackFormat)var0.minInclusive()), Optional.of((PackFormat)var0.maxInclusive()), var2.isValueInRange(var1) ? Optional.of((Integer)var2.minInclusive()) : Optional.empty(), var2.isValueInRange(var1) ? Optional.of(new InclusiveRange((Integer)var2.minInclusive(), (Integer)var2.maxInclusive())) : Optional.empty());
      }

      public int effectiveMinMajorVersion() {
         if (this.min.isPresent()) {
            return this.supported.isPresent() ? Math.min(((PackFormat)this.min.get()).major(), (Integer)((InclusiveRange)this.supported.get()).minInclusive()) : ((PackFormat)this.min.get()).major();
         } else {
            return this.supported.isPresent() ? (Integer)((InclusiveRange)this.supported.get()).minInclusive() : 2147483647;
         }
      }

      public DataResult<InclusiveRange<PackFormat>> validate(int var1, boolean var2, boolean var3, String var4, String var5) {
         if (this.min.isPresent() != this.max.isPresent()) {
            return DataResult.error(() -> var4 + " missing field, must declare both min_format and max_format");
         } else if (var3 && this.supported.isEmpty()) {
            return DataResult.error(() -> var4 + " missing required field " + var5 + ", must be present in all overlays for any overlays to work across game versions");
         } else if (this.min.isPresent()) {
            return this.validateNewFormat(var1, var2, var3, var4, var5);
         } else if (this.supported.isPresent()) {
            return this.validateOldFormat(var1, var2, var4, var5);
         } else if (var2 && this.format.isPresent()) {
            int var6 = (Integer)this.format.get();
            return var6 > var1 ? DataResult.error(() -> var4 + " declares support for version newer than " + var1 + ", but is missing mandatory fields min_format and max_format") : DataResult.success(new InclusiveRange(PackFormat.of(var6)));
         } else {
            return DataResult.error(() -> var4 + " could not be parsed, missing format version information");
         }
      }

      private DataResult<InclusiveRange<PackFormat>> validateNewFormat(int var1, boolean var2, boolean var3, String var4, String var5) {
         int var6 = ((PackFormat)this.min.get()).major();
         int var7 = ((PackFormat)this.max.get()).major();
         if (((PackFormat)this.min.get()).compareTo((PackFormat)this.max.get()) > 0) {
            return DataResult.error(() -> var4 + " min_format (" + String.valueOf(this.min.get()) + ") is greater than max_format (" + String.valueOf(this.max.get()) + ")");
         } else {
            if (var6 > var1 && !var3) {
               if (this.supported.isPresent()) {
                  return DataResult.error(() -> var4 + " key " + var5 + " is deprecated starting from pack format " + (var1 + 1) + ". Remove " + var5 + " from your pack.mcmeta.");
               }

               if (var2 && this.format.isPresent()) {
                  String var10 = this.validatePackFormatForRange(var6, var7);
                  if (var10 != null) {
                     return DataResult.error(() -> var10);
                  }
               }
            } else {
               if (!this.supported.isPresent()) {
                  return DataResult.error(() -> var4 + " declares support for format " + var6 + ", but game versions supporting formats 17 to " + var1 + " require a " + var5 + " field. Add \"" + var5 + "\": [" + var6 + ", " + var1 + "] or require a version greater or equal to " + (var1 + 1) + ".0.");
               }

               InclusiveRange var8 = (InclusiveRange)this.supported.get();
               if ((Integer)var8.minInclusive() != var6) {
                  return DataResult.error(() -> var4 + " version declaration mismatch between " + var5 + " (from " + String.valueOf(var8.minInclusive()) + ") and min_format (" + String.valueOf(this.min.get()) + ")");
               }

               if ((Integer)var8.maxInclusive() != var7 && (Integer)var8.maxInclusive() != var1) {
                  return DataResult.error(() -> var4 + " version declaration mismatch between " + var5 + " (up to " + String.valueOf(var8.maxInclusive()) + ") and max_format (" + String.valueOf(this.max.get()) + ")");
               }

               if (var2) {
                  if (!this.format.isPresent()) {
                     return DataResult.error(() -> var4 + " declares support for formats up to " + var1 + ", but game versions supporting formats 17 to " + var1 + " require a pack_format field. Add \"pack_format\": " + var6 + " or require a version greater or equal to " + (var1 + 1) + ".0.");
                  }

                  String var9 = this.validatePackFormatForRange(var6, var7);
                  if (var9 != null) {
                     return DataResult.error(() -> var9);
                  }
               }
            }

            return DataResult.success(new InclusiveRange((PackFormat)this.min.get(), (PackFormat)this.max.get()));
         }
      }

      private DataResult<InclusiveRange<PackFormat>> validateOldFormat(int var1, boolean var2, String var3, String var4) {
         InclusiveRange var5 = (InclusiveRange)this.supported.get();
         int var6 = (Integer)var5.minInclusive();
         int var7 = (Integer)var5.maxInclusive();
         if (var7 > var1) {
            return DataResult.error(() -> var3 + " declares support for version newer than " + var1 + ", but is missing mandatory fields min_format and max_format");
         } else {
            if (var2) {
               if (!this.format.isPresent()) {
                  return DataResult.error(() -> var3 + " declares support for formats up to " + var1 + ", but game versions supporting formats 17 to " + var1 + " require a pack_format field. Add \"pack_format\": " + var6 + " or require a version greater or equal to " + (var1 + 1) + ".0.");
               }

               String var8 = this.validatePackFormatForRange(var6, var7);
               if (var8 != null) {
                  return DataResult.error(() -> var8);
               }
            }

            return DataResult.success((new InclusiveRange(var6, var7)).map(PackFormat::of));
         }
      }

      private @Nullable String validatePackFormatForRange(int var1, int var2) {
         int var3 = (Integer)this.format.get();
         if (var3 >= var1 && var3 <= var2) {
            return var3 < 15 ? "Multi-version packs cannot support minimum version of less than 15, since this will leave versions in range unable to load pack." : null;
         } else {
            return "Pack declared support for versions " + var1 + " to " + var2 + " but declared main format is " + var3;
         }
      }
   }

   public interface IntermediaryFormatHolder {
      IntermediaryFormat format();
   }
}
