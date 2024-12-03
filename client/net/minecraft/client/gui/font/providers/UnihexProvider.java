package net.minecraft.client.gui.font.providers;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
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
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.CodepointMap;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FastBufferedInputStream;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class UnihexProvider implements GlyphProvider {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int GLYPH_HEIGHT = 16;
   private static final int DIGITS_PER_BYTE = 2;
   private static final int DIGITS_FOR_WIDTH_8 = 32;
   private static final int DIGITS_FOR_WIDTH_16 = 64;
   private static final int DIGITS_FOR_WIDTH_24 = 96;
   private static final int DIGITS_FOR_WIDTH_32 = 128;
   private final CodepointMap<Glyph> glyphs;

   UnihexProvider(CodepointMap<Glyph> var1) {
      super();
      this.glyphs = var1;
   }

   @Nullable
   public GlyphInfo getGlyph(int var1) {
      return this.glyphs.get(var1);
   }

   public IntSet getSupportedGlyphs() {
      return this.glyphs.keySet();
   }

   @VisibleForTesting
   static void unpackBitsToBytes(IntBuffer var0, int var1, int var2, int var3) {
      int var4 = 32 - var2 - 1;
      int var5 = 32 - var3 - 1;

      for(int var6 = var4; var6 >= var5; --var6) {
         if (var6 < 32 && var6 >= 0) {
            boolean var7 = (var1 >> var6 & 1) != 0;
            var0.put(var7 ? -1 : 0);
         } else {
            var0.put(0);
         }
      }

   }

   static void unpackBitsToBytes(IntBuffer var0, LineData var1, int var2, int var3) {
      for(int var4 = 0; var4 < 16; ++var4) {
         int var5 = var1.line(var4);
         unpackBitsToBytes(var0, var5, var2, var3);
      }

   }

   @VisibleForTesting
   static void readFromStream(InputStream var0, ReaderOutput var1) throws IOException {
      int var2 = 0;
      ByteArrayList var3 = new ByteArrayList(128);

      while(true) {
         boolean var4 = copyUntil(var0, var3, 58);
         int var5 = var3.size();
         if (var5 == 0 && !var4) {
            return;
         }

         if (!var4 || var5 != 4 && var5 != 5 && var5 != 6) {
            throw new IllegalArgumentException("Invalid entry at line " + var2 + ": expected 4, 5 or 6 hex digits followed by a colon");
         }

         int var6 = 0;

         for(int var7 = 0; var7 < var5; ++var7) {
            var6 = var6 << 4 | decodeHex(var2, var3.getByte(var7));
         }

         var3.clear();
         copyUntil(var0, var3, 10);
         int var9 = var3.size();
         LineData var10000;
         switch (var9) {
            case 32 -> var10000 = UnihexProvider.ByteContents.read(var2, var3);
            case 64 -> var10000 = UnihexProvider.ShortContents.read(var2, var3);
            case 96 -> var10000 = UnihexProvider.IntContents.read24(var2, var3);
            case 128 -> var10000 = UnihexProvider.IntContents.read32(var2, var3);
            default -> throw new IllegalArgumentException("Invalid entry at line " + var2 + ": expected hex number describing (8,16,24,32) x 16 bitmap, followed by a new line");
         }

         LineData var8 = var10000;
         var1.accept(var6, var8);
         ++var2;
         var3.clear();
      }
   }

   static int decodeHex(int var0, ByteList var1, int var2) {
      return decodeHex(var0, var1.getByte(var2));
   }

   private static int decodeHex(int var0, byte var1) {
      byte var10000;
      switch (var1) {
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
            throw new IllegalArgumentException("Invalid entry at line " + var0 + ": expected hex digit, got " + (char)var1);
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

   private static boolean copyUntil(InputStream var0, ByteList var1, int var2) throws IOException {
      while(true) {
         int var3 = var0.read();
         if (var3 == -1) {
            return false;
         }

         if (var3 == var2) {
            return true;
         }

         var1.add((byte)var3);
      }
   }

   static record OverrideRange(int from, int to, Dimensions dimensions) {
      final int from;
      final int to;
      final Dimensions dimensions;
      private static final Codec<OverrideRange> RAW_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.CODEPOINT.fieldOf("from").forGetter(OverrideRange::from), ExtraCodecs.CODEPOINT.fieldOf("to").forGetter(OverrideRange::to), UnihexProvider.Dimensions.MAP_CODEC.forGetter(OverrideRange::dimensions)).apply(var0, OverrideRange::new));
      public static final Codec<OverrideRange> CODEC;

      private OverrideRange(int var1, int var2, Dimensions var3) {
         super();
         this.from = var1;
         this.to = var2;
         this.dimensions = var3;
      }

      static {
         CODEC = RAW_CODEC.validate((var0) -> var0.from >= var0.to ? DataResult.error(() -> "Invalid range: [" + var0.from + ";" + var0.to + "]") : DataResult.success(var0));
      }
   }

   public static record Dimensions(int left, int right) {
      final int left;
      final int right;
      public static final MapCodec<Dimensions> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.INT.fieldOf("left").forGetter(Dimensions::left), Codec.INT.fieldOf("right").forGetter(Dimensions::right)).apply(var0, Dimensions::new));
      public static final Codec<Dimensions> CODEC;

      public Dimensions(int var1, int var2) {
         super();
         this.left = var1;
         this.right = var2;
      }

      public int pack() {
         return pack(this.left, this.right);
      }

      public static int pack(int var0, int var1) {
         return (var0 & 255) << 8 | var1 & 255;
      }

      public static int left(int var0) {
         return (byte)(var0 >> 8);
      }

      public static int right(int var0) {
         return (byte)var0;
      }

      static {
         CODEC = MAP_CODEC.codec();
      }
   }

   public static class Definition implements GlyphProviderDefinition {
      public static final MapCodec<Definition> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("hex_file").forGetter((var0x) -> var0x.hexFile), UnihexProvider.OverrideRange.CODEC.listOf().fieldOf("size_overrides").forGetter((var0x) -> var0x.sizeOverrides)).apply(var0, Definition::new));
      private final ResourceLocation hexFile;
      private final List<OverrideRange> sizeOverrides;

      private Definition(ResourceLocation var1, List<OverrideRange> var2) {
         super();
         this.hexFile = var1;
         this.sizeOverrides = var2;
      }

      public GlyphProviderType type() {
         return GlyphProviderType.UNIHEX;
      }

      public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
         return Either.left(this::load);
      }

      private GlyphProvider load(ResourceManager var1) throws IOException {
         InputStream var2 = var1.open(this.hexFile);

         UnihexProvider var3;
         try {
            var3 = this.loadData(var2);
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            var2.close();
         }

         return var3;
      }

      private UnihexProvider loadData(InputStream var1) throws IOException {
         CodepointMap var2 = new CodepointMap((var0) -> new LineData[var0], (var0) -> new LineData[var0][]);
         Objects.requireNonNull(var2);
         ReaderOutput var3 = var2::put;
         ZipInputStream var4 = new ZipInputStream(var1);

         UnihexProvider var17;
         try {
            ZipEntry var5;
            while((var5 = var4.getNextEntry()) != null) {
               String var6 = var5.getName();
               if (var6.endsWith(".hex")) {
                  UnihexProvider.LOGGER.info("Found {}, loading", var6);
                  UnihexProvider.readFromStream(new FastBufferedInputStream(var4), var3);
               }
            }

            CodepointMap var16 = new CodepointMap((var0) -> new Glyph[var0], (var0) -> new Glyph[var0][]);

            for(OverrideRange var8 : this.sizeOverrides) {
               int var9 = var8.from;
               int var10 = var8.to;
               Dimensions var11 = var8.dimensions;

               for(int var12 = var9; var12 <= var10; ++var12) {
                  LineData var13 = (LineData)var2.remove(var12);
                  if (var13 != null) {
                     var16.put(var12, new Glyph(var13, var11.left, var11.right));
                  }
               }
            }

            var2.forEach((var1x, var2x) -> {
               int var3 = var2x.calculateWidth();
               int var4 = UnihexProvider.Dimensions.left(var3);
               int var5 = UnihexProvider.Dimensions.right(var3);
               var16.put(var1x, new Glyph(var2x, var4, var5));
            });
            var17 = new UnihexProvider(var16);
         } catch (Throwable var15) {
            try {
               var4.close();
            } catch (Throwable var14) {
               var15.addSuppressed(var14);
            }

            throw var15;
         }

         var4.close();
         return var17;
      }
   }

   public interface LineData {
      int line(int var1);

      int bitWidth();

      default int mask() {
         int var1 = 0;

         for(int var2 = 0; var2 < 16; ++var2) {
            var1 |= this.line(var2);
         }

         return var1;
      }

      default int calculateWidth() {
         int var1 = this.mask();
         int var2 = this.bitWidth();
         int var3;
         int var4;
         if (var1 == 0) {
            var3 = 0;
            var4 = var2;
         } else {
            var3 = Integer.numberOfLeadingZeros(var1);
            var4 = 32 - Integer.numberOfTrailingZeros(var1) - 1;
         }

         return UnihexProvider.Dimensions.pack(var3, var4);
      }
   }

   static record ByteContents(byte[] contents) implements LineData {
      private ByteContents(byte[] var1) {
         super();
         this.contents = var1;
      }

      public int line(int var1) {
         return this.contents[var1] << 24;
      }

      static LineData read(int var0, ByteList var1) {
         byte[] var2 = new byte[16];
         int var3 = 0;

         for(int var4 = 0; var4 < 16; ++var4) {
            int var5 = UnihexProvider.decodeHex(var0, var1, var3++);
            int var6 = UnihexProvider.decodeHex(var0, var1, var3++);
            byte var7 = (byte)(var5 << 4 | var6);
            var2[var4] = var7;
         }

         return new ByteContents(var2);
      }

      public int bitWidth() {
         return 8;
      }
   }

   static record ShortContents(short[] contents) implements LineData {
      private ShortContents(short[] var1) {
         super();
         this.contents = var1;
      }

      public int line(int var1) {
         return this.contents[var1] << 16;
      }

      static LineData read(int var0, ByteList var1) {
         short[] var2 = new short[16];
         int var3 = 0;

         for(int var4 = 0; var4 < 16; ++var4) {
            int var5 = UnihexProvider.decodeHex(var0, var1, var3++);
            int var6 = UnihexProvider.decodeHex(var0, var1, var3++);
            int var7 = UnihexProvider.decodeHex(var0, var1, var3++);
            int var8 = UnihexProvider.decodeHex(var0, var1, var3++);
            short var9 = (short)(var5 << 12 | var6 << 8 | var7 << 4 | var8);
            var2[var4] = var9;
         }

         return new ShortContents(var2);
      }

      public int bitWidth() {
         return 16;
      }
   }

   static record IntContents(int[] contents, int bitWidth) implements LineData {
      private static final int SIZE_24 = 24;

      private IntContents(int[] var1, int var2) {
         super();
         this.contents = var1;
         this.bitWidth = var2;
      }

      public int line(int var1) {
         return this.contents[var1];
      }

      static LineData read24(int var0, ByteList var1) {
         int[] var2 = new int[16];
         int var3 = 0;
         int var4 = 0;

         for(int var5 = 0; var5 < 16; ++var5) {
            int var6 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var7 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var8 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var9 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var10 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var11 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var12 = var6 << 20 | var7 << 16 | var8 << 12 | var9 << 8 | var10 << 4 | var11;
            var2[var5] = var12 << 8;
            var3 |= var12;
         }

         return new IntContents(var2, 24);
      }

      public static LineData read32(int var0, ByteList var1) {
         int[] var2 = new int[16];
         int var3 = 0;
         int var4 = 0;

         for(int var5 = 0; var5 < 16; ++var5) {
            int var6 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var7 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var8 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var9 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var10 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var11 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var12 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var13 = UnihexProvider.decodeHex(var0, var1, var4++);
            int var14 = var6 << 28 | var7 << 24 | var8 << 20 | var9 << 16 | var10 << 12 | var11 << 8 | var12 << 4 | var13;
            var2[var5] = var14;
            var3 |= var14;
         }

         return new IntContents(var2, 32);
      }
   }

   static record Glyph(LineData contents, int left, int right) implements GlyphInfo {
      final LineData contents;
      final int left;
      final int right;

      Glyph(LineData var1, int var2, int var3) {
         super();
         this.contents = var1;
         this.left = var2;
         this.right = var3;
      }

      public int width() {
         return this.right - this.left + 1;
      }

      public float getAdvance() {
         return (float)(this.width() / 2 + 1);
      }

      public float getShadowOffset() {
         return 0.5F;
      }

      public float getBoldOffset() {
         return 0.5F;
      }

      public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1) {
         return (BakedGlyph)var1.apply(new SheetGlyphInfo() {
            public float getOversample() {
               return 2.0F;
            }

            public int getPixelWidth() {
               return Glyph.this.width();
            }

            public int getPixelHeight() {
               return 16;
            }

            public void upload(int var1, int var2) {
               IntBuffer var3 = MemoryUtil.memAllocInt(Glyph.this.width() * 16);
               UnihexProvider.unpackBitsToBytes(var3, Glyph.this.contents, Glyph.this.left, Glyph.this.right);
               var3.rewind();
               GlStateManager.upload(0, var1, var2, Glyph.this.width(), 16, NativeImage.Format.RGBA, var3, MemoryUtil::memFree);
            }

            public boolean isColored() {
               return true;
            }
         });
      }
   }

   @FunctionalInterface
   public interface ReaderOutput {
      void accept(int var1, LineData var2);
   }
}
