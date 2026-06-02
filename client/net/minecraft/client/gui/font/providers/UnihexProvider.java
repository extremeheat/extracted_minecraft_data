package net.minecraft.client.gui.font.providers;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.font.GlyphBitmap;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.UnbakedGlyph;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FastBufferedInputStream;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class UnihexProvider implements GlyphProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int GLYPH_HEIGHT = 16;
   private static final int DIGITS_PER_BYTE = 2;
   private static final int DIGITS_FOR_WIDTH_8 = 32;
   private static final int DIGITS_FOR_WIDTH_16 = 64;
   private static final int DIGITS_FOR_WIDTH_24 = 96;
   private static final int DIGITS_FOR_WIDTH_32 = 128;
   private final CodepointMap<Glyph> glyphs;

   private UnihexProvider(final CodepointMap<Glyph> glyphs) {
      super();
      this.glyphs = glyphs;
   }

   public @Nullable UnbakedGlyph getGlyph(final int codepoint) {
      return this.glyphs.get(codepoint);
   }

   public IntSet getSupportedGlyphs() {
      return this.glyphs.keySet();
   }

   @VisibleForTesting
   static void unpackBitsToBytes(final IntBuffer output, final int value, final int left, final int right) {
      int startBit = 32 - left - 1;
      int endBit = 32 - right - 1;

      for(int i = startBit; i >= endBit; --i) {
         if (i < 32 && i >= 0) {
            boolean isSet = (value >> i & 1) != 0;
            output.put(isSet ? -1 : 0);
         } else {
            output.put(0);
         }
      }

   }

   private static void unpackBitsToBytes(final IntBuffer output, final LineData data, final int left, final int right) {
      for(int i = 0; i < 16; ++i) {
         int line = data.line(i);
         unpackBitsToBytes(output, line, left, right);
      }

   }

   @VisibleForTesting
   static void readFromStream(final InputStream input, final ReaderOutput output) throws IOException {
      int line = 0;
      ByteList buffer = new ByteArrayList(128);

      while(true) {
         boolean foundColon = copyUntil(input, buffer, 58);
         int codepointDigitCount = buffer.size();
         if (codepointDigitCount == 0 && !foundColon) {
            return;
         }

         if (!foundColon || codepointDigitCount != 4 && codepointDigitCount != 5 && codepointDigitCount != 6) {
            throw new IllegalArgumentException("Invalid entry at line " + line + ": expected 4, 5 or 6 hex digits followed by a colon");
         }

         int codepoint = 0;

         for(int i = 0; i < codepointDigitCount; ++i) {
            codepoint = codepoint << 4 | decodeHex(line, buffer.getByte(i));
         }

         buffer.clear();
         copyUntil(input, buffer, 10);
         int dataDigitCount = buffer.size();
         LineData var10000;
         switch (dataDigitCount) {
            case 32 -> var10000 = UnihexProvider.ByteContents.read(line, buffer);
            case 64 -> var10000 = UnihexProvider.ShortContents.read(line, buffer);
            case 96 -> var10000 = UnihexProvider.IntContents.read24(line, buffer);
            case 128 -> var10000 = UnihexProvider.IntContents.read32(line, buffer);
            default -> throw new IllegalArgumentException("Invalid entry at line " + line + ": expected hex number describing (8,16,24,32) x 16 bitmap, followed by a new line");
         }

         LineData contents = var10000;
         output.accept(codepoint, contents);
         ++line;
         buffer.clear();
      }
   }

   private static int decodeHex(final int line, final ByteList input, final int index) {
      return decodeHex(line, input.getByte(index));
   }

   private static int decodeHex(final int line, final byte b) {
      byte var10000;
      switch (b) {
         case 48:
            var10000 = 0;
            break;
         case 49:
            var10000 = 1;
            break;
         case 50:
            var10000 = 2;
            break;
         case 51:
            var10000 = 3;
            break;
         case 52:
            var10000 = 4;
            break;
         case 53:
            var10000 = 5;
            break;
         case 54:
            var10000 = 6;
            break;
         case 55:
            var10000 = 7;
            break;
         case 56:
            var10000 = 8;
            break;
         case 57:
            var10000 = 9;
            break;
         case 58:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         default:
            throw new IllegalArgumentException("Invalid entry at line " + line + ": expected hex digit, got " + (char)b);
         case 65:
            var10000 = 10;
            break;
         case 66:
            var10000 = 11;
            break;
         case 67:
            var10000 = 12;
            break;
         case 68:
            var10000 = 13;
            break;
         case 69:
            var10000 = 14;
            break;
         case 70:
            var10000 = 15;
      }

      return var10000;
   }

   private static boolean copyUntil(final InputStream input, final ByteList output, final int delimiter) throws IOException {
      while(true) {
         int b = input.read();
         if (b == -1) {
            return false;
         }

         if (b == delimiter) {
            return true;
         }

         output.add((byte)b);
      }
   }

   private static record OverrideRange(int from, int to, Dimensions dimensions) {
      private static final Codec<OverrideRange> RAW_CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.CODEPOINT.fieldOf("from").forGetter(OverrideRange::from), ExtraCodecs.CODEPOINT.fieldOf("to").forGetter(OverrideRange::to), UnihexProvider.Dimensions.MAP_CODEC.forGetter(OverrideRange::dimensions)).apply(i, OverrideRange::new));
      public static final Codec<OverrideRange> CODEC;

      private OverrideRange {
         super();
      }

      static {
         CODEC = RAW_CODEC.validate((o) -> o.from >= o.to ? DataResult.error(() -> "Invalid range: [" + o.from + ";" + o.to + "]") : DataResult.success(o));
      }
   }

   public static record Dimensions(int left, int right) {
      public static final MapCodec<Dimensions> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.INT.fieldOf("left").forGetter(Dimensions::left), Codec.INT.fieldOf("right").forGetter(Dimensions::right)).apply(i, Dimensions::new));
      public static final Codec<Dimensions> CODEC;

      public Dimensions {
         super();
      }

      public int pack() {
         return pack(this.left, this.right);
      }

      public static int pack(final int left, final int right) {
         return (left & 255) << 8 | right & 255;
      }

      public static int left(final int packed) {
         return (byte)(packed >> 8);
      }

      public static int right(final int packed) {
         return (byte)packed;
      }

      static {
         CODEC = MAP_CODEC.codec();
      }
   }

   public static class Definition implements GlyphProviderDefinition {
      public static final MapCodec<Definition> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("hex_file").forGetter((o) -> o.hexFile), UnihexProvider.OverrideRange.CODEC.listOf().optionalFieldOf("size_overrides", List.of()).forGetter((o) -> o.sizeOverrides)).apply(i, Definition::new));
      private final Identifier hexFile;
      private final List<OverrideRange> sizeOverrides;

      private Definition(final Identifier hexFile, final List<OverrideRange> sizeOverrides) {
         super();
         this.hexFile = hexFile;
         this.sizeOverrides = sizeOverrides;
      }

      public GlyphProviderType type() {
         return GlyphProviderType.UNIHEX;
      }

      public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
         return Either.left(this::load);
      }

      private GlyphProvider load(final ResourceManager resourceManager) throws IOException {
         InputStream raw = resourceManager.open(this.hexFile);

         UnihexProvider var3;
         try {
            var3 = this.loadData(raw);
         } catch (Throwable var6) {
            if (raw != null) {
               try {
                  raw.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (raw != null) {
            raw.close();
         }

         return var3;
      }

      private UnihexProvider loadData(final InputStream zipFile) throws IOException {
         CodepointMap<LineData> bits = new CodepointMap<LineData>((x$0) -> new LineData[x$0], (x$0) -> new LineData[x$0][]);
         Objects.requireNonNull(bits);
         ReaderOutput output = bits::put;
         ZipInputStream zis = new ZipInputStream(zipFile);

         UnihexProvider var17;
         try {
            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {
               String name = entry.getName();
               if (name.endsWith(".hex")) {
                  UnihexProvider.LOGGER.info("Found {}, loading", name);
                  UnihexProvider.readFromStream(new FastBufferedInputStream(zis), output);
               }
            }

            CodepointMap<Glyph> glyphs = new CodepointMap<Glyph>((x$0) -> new Glyph[x$0], (x$0) -> new Glyph[x$0][]);

            for(OverrideRange sizeOverride : this.sizeOverrides) {
               int from = sizeOverride.from;
               int to = sizeOverride.to;
               Dimensions size = sizeOverride.dimensions;

               for(int c = from; c <= to; ++c) {
                  LineData codepointBits = bits.remove(c);
                  if (codepointBits != null) {
                     glyphs.put(c, new Glyph(codepointBits, size.left, size.right));
                  }
               }
            }

            bits.forEach((codepoint, glyphBits) -> {
               int packedSize = glyphBits.calculateWidth();
               int left = UnihexProvider.Dimensions.left(packedSize);
               int right = UnihexProvider.Dimensions.right(packedSize);
               glyphs.put(codepoint, new Glyph(glyphBits, left, right));
            });
            var17 = new UnihexProvider(glyphs);
         } catch (Throwable var15) {
            try {
               zis.close();
            } catch (Throwable var14) {
               var15.addSuppressed(var14);
            }

            throw var15;
         }

         zis.close();
         return var17;
      }
   }

   public interface LineData {
      int line(int index);

      int bitWidth();

      default int mask() {
         int mask = 0;

         for(int i = 0; i < 16; ++i) {
            mask |= this.line(i);
         }

         return mask;
      }

      default int calculateWidth() {
         int mask = this.mask();
         int bitWidth = this.bitWidth();
         int left;
         int right;
         if (mask == 0) {
            left = 0;
            right = bitWidth;
         } else {
            left = Integer.numberOfLeadingZeros(mask);
            right = 32 - Integer.numberOfTrailingZeros(mask) - 1;
         }

         return UnihexProvider.Dimensions.pack(left, right);
      }
   }

   private static record ByteContents(byte[] contents) implements LineData {
      private ByteContents {
         super();
      }

      public int line(final int index) {
         return this.contents[index] << 24;
      }

      private static LineData read(final int line, final ByteList input) {
         byte[] content = new byte[16];
         int pos = 0;

         for(int i = 0; i < 16; ++i) {
            int n1 = UnihexProvider.decodeHex(line, input, pos++);
            int n0 = UnihexProvider.decodeHex(line, input, pos++);
            byte v = (byte)(n1 << 4 | n0);
            content[i] = v;
         }

         return new ByteContents(content);
      }

      public int bitWidth() {
         return 8;
      }
   }

   private static record ShortContents(short[] contents) implements LineData {
      private ShortContents {
         super();
      }

      public int line(final int index) {
         return this.contents[index] << 16;
      }

      private static LineData read(final int line, final ByteList input) {
         short[] content = new short[16];
         int pos = 0;

         for(int i = 0; i < 16; ++i) {
            int n3 = UnihexProvider.decodeHex(line, input, pos++);
            int n2 = UnihexProvider.decodeHex(line, input, pos++);
            int n1 = UnihexProvider.decodeHex(line, input, pos++);
            int n0 = UnihexProvider.decodeHex(line, input, pos++);
            short v = (short)(n3 << 12 | n2 << 8 | n1 << 4 | n0);
            content[i] = v;
         }

         return new ShortContents(content);
      }

      public int bitWidth() {
         return 16;
      }
   }

   private static record IntContents(int[] contents, int bitWidth) implements LineData {
      private static final int SIZE_24 = 24;

      private IntContents {
         super();
      }

      public int line(final int index) {
         return this.contents[index];
      }

      private static LineData read24(final int line, final ByteList input) {
         int[] content = new int[16];
         int mask = 0;
         int pos = 0;

         for(int i = 0; i < 16; ++i) {
            int n5 = UnihexProvider.decodeHex(line, input, pos++);
            int n4 = UnihexProvider.decodeHex(line, input, pos++);
            int n3 = UnihexProvider.decodeHex(line, input, pos++);
            int n2 = UnihexProvider.decodeHex(line, input, pos++);
            int n1 = UnihexProvider.decodeHex(line, input, pos++);
            int n0 = UnihexProvider.decodeHex(line, input, pos++);
            int v = n5 << 20 | n4 << 16 | n3 << 12 | n2 << 8 | n1 << 4 | n0;
            content[i] = v << 8;
            mask |= v;
         }

         return new IntContents(content, 24);
      }

      public static LineData read32(final int line, final ByteList input) {
         int[] content = new int[16];
         int mask = 0;
         int pos = 0;

         for(int i = 0; i < 16; ++i) {
            int n7 = UnihexProvider.decodeHex(line, input, pos++);
            int n6 = UnihexProvider.decodeHex(line, input, pos++);
            int n5 = UnihexProvider.decodeHex(line, input, pos++);
            int n4 = UnihexProvider.decodeHex(line, input, pos++);
            int n3 = UnihexProvider.decodeHex(line, input, pos++);
            int n2 = UnihexProvider.decodeHex(line, input, pos++);
            int n1 = UnihexProvider.decodeHex(line, input, pos++);
            int n0 = UnihexProvider.decodeHex(line, input, pos++);
            int v = n7 << 28 | n6 << 24 | n5 << 20 | n4 << 16 | n3 << 12 | n2 << 8 | n1 << 4 | n0;
            content[i] = v;
            mask |= v;
         }

         return new IntContents(content, 32);
      }
   }

   private static record Glyph(LineData contents, int left, int right) implements UnbakedGlyph {
      private Glyph {
         super();
      }

      public int width() {
         return this.right - this.left + 1;
      }

      public GlyphInfo info() {
         return new GlyphInfo() {
            {
               Objects.requireNonNull(Glyph.this);
            }

            public float getAdvance() {
               return (float)Glyph.this.width() / 2.0F + 1.0F;
            }

            public float getShadowOffset() {
               return 0.5F;
            }

            public float getBoldOffset() {
               return 0.5F;
            }
         };
      }

      public BakedGlyph bake(final UnbakedGlyph.Stitcher stitcher) {
         return stitcher.stitch(this.info(), new GlyphBitmap() {
            {
               Objects.requireNonNull(Glyph.this);
            }

            public float getOversample() {
               return 2.0F;
            }

            public int getPixelWidth() {
               return Glyph.this.width();
            }

            public int getPixelHeight() {
               return 16;
            }

            public void upload(final int x, final int y, final GpuTexture texture) {
               IntBuffer targetBuffer = MemoryUtil.memAllocInt(Glyph.this.width() * 16);
               UnihexProvider.unpackBitsToBytes(targetBuffer, Glyph.this.contents, Glyph.this.left, Glyph.this.right);
               targetBuffer.rewind();
               RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, MemoryUtil.memByteBuffer(targetBuffer), 0, 0, x, y, Glyph.this.width(), 16);
               MemoryUtil.memFree(targetBuffer);
            }

            public boolean isColored() {
               return true;
            }
         });
      }
   }

   @FunctionalInterface
   public interface ReaderOutput {
      void accept(int codepoint, LineData glyph);
   }
}
