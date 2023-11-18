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
   private final CodepointMap<UnihexProvider.Glyph> glyphs;

   UnihexProvider(CodepointMap<UnihexProvider.Glyph> var1) {
      super();
      this.glyphs = var1;
   }

   @Nullable
   @Override
   public GlyphInfo getGlyph(int var1) {
      return this.glyphs.get(var1);
   }

   @Override
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

   static void unpackBitsToBytes(IntBuffer var0, UnihexProvider.LineData var1, int var2, int var3) {
      for(int var4 = 0; var4 < 16; ++var4) {
         int var5 = var1.line(var4);
         unpackBitsToBytes(var0, var5, var2, var3);
      }
   }

   @VisibleForTesting
   static void readFromStream(InputStream var0, UnihexProvider.ReaderOutput var1) throws IOException {
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

         UnihexProvider.LineData var8 = switch(var9) {
            case 32 -> UnihexProvider.ByteContents.read(var2, var3);
            case 64 -> UnihexProvider.ShortContents.read(var2, var3);
            case 96 -> UnihexProvider.IntContents.read24(var2, var3);
            case 128 -> UnihexProvider.IntContents.read32(var2, var3);
            default -> throw new IllegalArgumentException(
            "Invalid entry at line " + var2 + ": expected hex number describing (8,16,24,32) x 16 bitmap, followed by a new line"
         );
         };
         var1.accept(var6, var8);
         ++var2;
         var3.clear();
      }
   }

   static int decodeHex(int var0, ByteList var1, int var2) {
      return decodeHex(var0, var1.getByte(var2));
   }

   private static int decodeHex(int var0, byte var1) {
      return switch(var1) {
         case 48 -> 0;
         case 49 -> 1;
         case 50 -> 2;
         case 51 -> 3;
         case 52 -> 4;
         case 53 -> 5;
         case 54 -> 6;
         case 55 -> 7;
         case 56 -> 8;
         case 57 -> 9;
         default -> throw new IllegalArgumentException("Invalid entry at line " + var0 + ": expected hex digit, got " + (char)var1);
         case 65 -> 10;
         case 66 -> 11;
         case 67 -> 12;
         case 68 -> 13;
         case 69 -> 14;
         case 70 -> 15;
      };
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

   static record ByteContents(byte[] a) implements UnihexProvider.LineData {
      private final byte[] contents;

      private ByteContents(byte[] var1) {
         super();
         this.contents = var1;
      }

      @Override
      public int line(int var1) {
         return this.contents[var1] << 24;
      }

      static UnihexProvider.LineData read(int var0, ByteList var1) {
         byte[] var2 = new byte[16];
         int var3 = 0;

         for(int var4 = 0; var4 < 16; ++var4) {
            int var5 = UnihexProvider.decodeHex(var0, var1, var3++);
            int var6 = UnihexProvider.decodeHex(var0, var1, var3++);
            byte var7 = (byte)(var5 << 4 | var6);
            var2[var4] = var7;
         }

         return new UnihexProvider.ByteContents(var2);
      }

      @Override
      public int bitWidth() {
         return 8;
      }
   }

   public static class Definition implements GlyphProviderDefinition {
      public static final MapCodec<UnihexProvider.Definition> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  ResourceLocation.CODEC.fieldOf("hex_file").forGetter(var0x -> var0x.hexFile),
                  UnihexProvider.OverrideRange.CODEC.listOf().fieldOf("size_overrides").forGetter(var0x -> var0x.sizeOverrides)
               )
               .apply(var0, UnihexProvider.Definition::new)
      );
      private final ResourceLocation hexFile;
      private final List<UnihexProvider.OverrideRange> sizeOverrides;

      private Definition(ResourceLocation var1, List<UnihexProvider.OverrideRange> var2) {
         super();
         this.hexFile = var1;
         this.sizeOverrides = var2;
      }

      @Override
      public GlyphProviderType type() {
         return GlyphProviderType.UNIHEX;
      }

      @Override
      public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
         return Either.left(this::load);
      }

      private GlyphProvider load(ResourceManager var1) throws IOException {
         UnihexProvider var3;
         try (InputStream var2 = var1.open(this.hexFile)) {
            var3 = this.loadData(var2);
         }

         return var3;
      }

      private UnihexProvider loadData(InputStream var1) throws IOException {
         CodepointMap var2 = new CodepointMap<>(var0 -> new UnihexProvider.LineData[var0], var0 -> new UnihexProvider.LineData[var0][]);
         UnihexProvider.ReaderOutput var3 = var2::put;

         UnihexProvider var17;
         try (ZipInputStream var4 = new ZipInputStream(var1)) {
            ZipEntry var5;
            while((var5 = var4.getNextEntry()) != null) {
               String var6 = var5.getName();
               if (var6.endsWith(".hex")) {
                  UnihexProvider.LOGGER.info("Found {}, loading", var6);
                  UnihexProvider.readFromStream(new FastBufferedInputStream(var4), var3);
               }
            }

            CodepointMap var16 = new CodepointMap<>(var0 -> new UnihexProvider.Glyph[var0], var0 -> new UnihexProvider.Glyph[var0][]);

            for(UnihexProvider.OverrideRange var8 : this.sizeOverrides) {
               int var9 = var8.from;
               int var10 = var8.to;
               UnihexProvider.Dimensions var11 = var8.dimensions;

               for(int var12 = var9; var12 <= var10; ++var12) {
                  UnihexProvider.LineData var13 = (UnihexProvider.LineData)var2.remove(var12);
                  if (var13 != null) {
                     var16.put(var12, new UnihexProvider.Glyph(var13, var11.left, var11.right));
                  }
               }
            }

            var2.forEach((var1x, var2x) -> {
               int var3x = var2x.calculateWidth();
               int var4x = UnihexProvider.Dimensions.left(var3x);
               int var5x = UnihexProvider.Dimensions.right(var3x);
               var16.put(var1x, new UnihexProvider.Glyph(var2x, var4x, var5x));
            });
            var17 = new UnihexProvider(var16);
         }

         return var17;
      }
   }

   public static record Dimensions(int c, int d) {
      final int left;
      final int right;
      public static final MapCodec<UnihexProvider.Dimensions> MAP_CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  Codec.INT.fieldOf("left").forGetter(UnihexProvider.Dimensions::left), Codec.INT.fieldOf("right").forGetter(UnihexProvider.Dimensions::right)
               )
               .apply(var0, UnihexProvider.Dimensions::new)
      );
      public static final Codec<UnihexProvider.Dimensions> CODEC = MAP_CODEC.codec();

      public Dimensions(int var1, int var2) {
         super();
         this.left = var1;
         this.right = var2;
      }

      public int pack() {
         return pack(this.left, this.right);
      }

      public static int pack(int var0, int var1) {
         return (var0 & 0xFF) << 8 | var1 & 0xFF;
      }

      public static int left(int var0) {
         return (byte)(var0 >> 8);
      }

      public static int right(int var0) {
         return (byte)var0;
      }
   }

   static record Glyph(UnihexProvider.LineData a, int b, int c) implements GlyphInfo {
      final UnihexProvider.LineData contents;
      final int left;
      final int right;

      Glyph(UnihexProvider.LineData var1, int var2, int var3) {
         super();
         this.contents = var1;
         this.left = var2;
         this.right = var3;
      }

      public int width() {
         return this.right - this.left + 1;
      }

      @Override
      public float getAdvance() {
         return (float)(this.width() / 2 + 1);
      }

      @Override
      public float getShadowOffset() {
         return 0.5F;
      }

      @Override
      public float getBoldOffset() {
         return 0.5F;
      }

      @Override
      public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1) {
         return (BakedGlyph)var1.apply(new SheetGlyphInfo() {
            @Override
            public float getOversample() {
               return 2.0F;
            }

            @Override
            public int getPixelWidth() {
               return Glyph.this.width();
            }

            @Override
            public int getPixelHeight() {
               return 16;
            }

            @Override
            public void upload(int var1, int var2) {
               IntBuffer var3 = MemoryUtil.memAllocInt(Glyph.this.width() * 16);
               UnihexProvider.unpackBitsToBytes(var3, Glyph.this.contents, Glyph.this.left, Glyph.this.right);
               var3.rewind();
               GlStateManager.upload(0, var1, var2, Glyph.this.width(), 16, NativeImage.Format.RGBA, var3, MemoryUtil::memFree);
            }

            @Override
            public boolean isColored() {
               return true;
            }
         });
      }
   }

   static record IntContents(int[] a, int b) implements UnihexProvider.LineData {
      private final int[] contents;
      private final int bitWidth;
      private static final int SIZE_24 = 24;

      private IntContents(int[] var1, int var2) {
         super();
         this.contents = var1;
         this.bitWidth = var2;
      }

      @Override
      public int line(int var1) {
         return this.contents[var1];
      }

      static UnihexProvider.LineData read24(int var0, ByteList var1) {
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

         return new UnihexProvider.IntContents(var2, 24);
      }

      public static UnihexProvider.LineData read32(int var0, ByteList var1) {
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

         return new UnihexProvider.IntContents(var2, 32);
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

   static record OverrideRange(int b, int c, UnihexProvider.Dimensions d) {
      final int from;
      final int to;
      final UnihexProvider.Dimensions dimensions;
      private static final Codec<UnihexProvider.OverrideRange> RAW_CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.CODEPOINT.fieldOf("from").forGetter(UnihexProvider.OverrideRange::from),
                  ExtraCodecs.CODEPOINT.fieldOf("to").forGetter(UnihexProvider.OverrideRange::to),
                  UnihexProvider.Dimensions.MAP_CODEC.forGetter(UnihexProvider.OverrideRange::dimensions)
               )
               .apply(var0, UnihexProvider.OverrideRange::new)
      );
      public static final Codec<UnihexProvider.OverrideRange> CODEC = ExtraCodecs.validate(
         RAW_CODEC, var0 -> var0.from >= var0.to ? DataResult.error(() -> "Invalid range: [" + var0.from + ";" + var0.to + "]") : DataResult.success(var0)
      );

      private OverrideRange(int var1, int var2, UnihexProvider.Dimensions var3) {
         super();
         this.from = var1;
         this.to = var2;
         this.dimensions = var3;
      }
   }

   @FunctionalInterface
   public interface ReaderOutput {
      void accept(int var1, UnihexProvider.LineData var2);
   }

   static record ShortContents(short[] a) implements UnihexProvider.LineData {
      private final short[] contents;

      private ShortContents(short[] var1) {
         super();
         this.contents = var1;
      }

      @Override
      public int line(int var1) {
         return this.contents[var1] << 16;
      }

      static UnihexProvider.LineData read(int var0, ByteList var1) {
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

         return new UnihexProvider.ShortContents(var2);
      }

      @Override
      public int bitWidth() {
         return 16;
      }
   }
}
