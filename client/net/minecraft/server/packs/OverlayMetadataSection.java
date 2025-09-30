package net.minecraft.server.packs;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.util.InclusiveRange;

public record OverlayMetadataSection(List<OverlayEntry> overlays) {
   private static final Pattern DIR_VALIDATOR = Pattern.compile("[-_a-zA-Z0-9.]+");
   public static final MetadataSectionType<OverlayMetadataSection> CLIENT_TYPE;
   public static final MetadataSectionType<OverlayMetadataSection> SERVER_TYPE;

   public OverlayMetadataSection(List<OverlayEntry> var1) {
      super();
      this.overlays = var1;
   }

   private static DataResult<String> validateOverlayDir(String var0) {
      return !DIR_VALIDATOR.matcher(var0).matches() ? DataResult.error(() -> var0 + " is not accepted directory name") : DataResult.success(var0);
   }

   @VisibleForTesting
   public static Codec<OverlayMetadataSection> codecForPackType(PackType var0) {
      return RecordCodecBuilder.create((var1) -> var1.group(OverlayMetadataSection.OverlayEntry.listCodecForPackType(var0).fieldOf("entries").forGetter(OverlayMetadataSection::overlays)).apply(var1, OverlayMetadataSection::new));
   }

   public static MetadataSectionType<OverlayMetadataSection> forPackType(PackType var0) {
      MetadataSectionType var10000;
      switch (var0) {
         case CLIENT_RESOURCES -> var10000 = CLIENT_TYPE;
         case SERVER_DATA -> var10000 = SERVER_TYPE;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public List<String> overlaysForVersion(PackFormat var1) {
      return this.overlays.stream().filter((var1x) -> var1x.isApplicable(var1)).map(OverlayEntry::overlay).toList();
   }

   static {
      CLIENT_TYPE = new MetadataSectionType<OverlayMetadataSection>("overlays", codecForPackType(PackType.CLIENT_RESOURCES));
      SERVER_TYPE = new MetadataSectionType<OverlayMetadataSection>("overlays", codecForPackType(PackType.SERVER_DATA));
   }

   public static record OverlayEntry(InclusiveRange<PackFormat> format, String overlay) {
      public OverlayEntry(InclusiveRange<PackFormat> var1, String var2) {
         super();
         this.format = var1;
         this.overlay = var2;
      }

      static Codec<List<OverlayEntry>> listCodecForPackType(PackType var0) {
         int var1 = PackFormat.lastPreMinorVersion(var0);
         return OverlayMetadataSection.OverlayEntry.IntermediateEntry.CODEC.listOf().flatXmap((var1x) -> PackFormat.validateHolderList(var1x, var1, (var0, var1xx) -> new OverlayEntry(var1xx, var0.overlay())), (var1x) -> DataResult.success(var1x.stream().map((var1xx) -> new IntermediateEntry(PackFormat.IntermediaryFormat.fromRange(var1xx.format(), var1), var1xx.overlay())).toList()));
      }

      public boolean isApplicable(PackFormat var1) {
         return this.format.isValueInRange(var1);
      }

      static record IntermediateEntry(PackFormat.IntermediaryFormat format, String overlay) implements PackFormat.IntermediaryFormatHolder {
         static final Codec<IntermediateEntry> CODEC = RecordCodecBuilder.create((var0) -> var0.group(PackFormat.IntermediaryFormat.OVERLAY_CODEC.forGetter(IntermediateEntry::format), Codec.STRING.validate(OverlayMetadataSection::validateOverlayDir).fieldOf("directory").forGetter(IntermediateEntry::overlay)).apply(var0, IntermediateEntry::new));

         IntermediateEntry(PackFormat.IntermediaryFormat var1, String var2) {
            super();
            this.format = var1;
            this.overlay = var2;
         }

         public String toString() {
            return this.overlay;
         }
      }
   }
}
