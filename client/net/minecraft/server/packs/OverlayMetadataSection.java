package net.minecraft.server.packs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.InclusiveRange;

public record OverlayMetadataSection(List<OverlayEntry> overlays) {
   private static final Pattern DIR_VALIDATOR = Pattern.compile("[-_a-zA-Z0-9.]+");
   private static final Codec<OverlayMetadataSection> CODEC = RecordCodecBuilder.create((var0) -> var0.group(OverlayMetadataSection.OverlayEntry.CODEC.listOf().fieldOf("entries").forGetter(OverlayMetadataSection::overlays)).apply(var0, OverlayMetadataSection::new));
   public static final MetadataSectionType<OverlayMetadataSection> TYPE;

   public OverlayMetadataSection(List<OverlayEntry> var1) {
      super();
      this.overlays = var1;
   }

   private static DataResult<String> validateOverlayDir(String var0) {
      return !DIR_VALIDATOR.matcher(var0).matches() ? DataResult.error(() -> var0 + " is not accepted directory name") : DataResult.success(var0);
   }

   public List<String> overlaysForVersion(int var1) {
      return this.overlays.stream().filter((var1x) -> var1x.isApplicable(var1)).map(OverlayEntry::overlay).toList();
   }

   static {
      TYPE = new MetadataSectionType<OverlayMetadataSection>("overlays", CODEC);
   }

   public static record OverlayEntry(InclusiveRange<Integer> format, String overlay) {
      static final Codec<OverlayEntry> CODEC = RecordCodecBuilder.create((var0) -> var0.group(InclusiveRange.codec(Codec.INT).fieldOf("formats").forGetter(OverlayEntry::format), Codec.STRING.validate(OverlayMetadataSection::validateOverlayDir).fieldOf("directory").forGetter(OverlayEntry::overlay)).apply(var0, OverlayEntry::new));

      public OverlayEntry(InclusiveRange<Integer> var1, String var2) {
         super();
         this.format = var1;
         this.overlay = var2;
      }

      public boolean isApplicable(int var1) {
         return this.format.isValueInRange(var1);
      }
   }
}
