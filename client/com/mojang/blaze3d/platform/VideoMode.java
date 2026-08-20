package com.mojang.blaze3d.platform;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.lwjgl.sdl.SDLPixels;
import org.lwjgl.sdl.SDL_DisplayMode;
import org.lwjgl.sdl.SDL_PixelFormatDetails;
import org.slf4j.Logger;

public final class VideoMode {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<VideoMode> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.INT.fieldOf("width").forGetter(VideoMode::getWidth), Codec.INT.fieldOf("height").forGetter(VideoMode::getHeight), Codec.INT.fieldOf("red_bits").forGetter(VideoMode::getRedBits), Codec.INT.fieldOf("green_bits").forGetter(VideoMode::getGreenBits), Codec.INT.fieldOf("blue_bits").forGetter(VideoMode::getBlueBits), Codec.FLOAT.fieldOf("refresh_rate").forGetter(VideoMode::getRefreshRate)).apply(instance, VideoMode::new));
   private static final Gson GSON = new Gson();
   private final int width;
   private final int height;
   private final int redBits;
   private final int greenBits;
   private final int blueBits;
   private final float refreshRate;

   public VideoMode(final int width, final int height, final int redBits, final int greenBits, final int blueBits, final int refreshRate) {
      this(width, height, redBits, greenBits, blueBits, (float)refreshRate);
   }

   private VideoMode(final int width, final int height, final int redBits, final int greenBits, final int blueBits, final float refreshRate) {
      super();
      this.width = width;
      this.height = height;
      this.redBits = redBits;
      this.greenBits = greenBits;
      this.blueBits = blueBits;
      this.refreshRate = refreshRate;
   }

   public VideoMode(final SDL_DisplayMode mode) {
      super();
      this.width = mode.w();
      this.height = mode.h();
      this.refreshRate = mode.refresh_rate();
      SDL_PixelFormatDetails details = SDLPixels.SDL_GetPixelFormatDetails(mode.format());
      this.redBits = details != null ? details.Rbits() : 8;
      this.greenBits = details != null ? details.Gbits() : 8;
      this.blueBits = details != null ? details.Bbits() : 8;
   }

   public static Optional<VideoMode> read(final @Nullable String s) {
      if (s == null) {
         return Optional.empty();
      } else {
         try {
            JsonElement json = JsonParser.parseString(s);
            return CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial((errorx) -> LOGGER.warn("Failed to parse video mode '{}': {}", s, errorx));
         } catch (RuntimeException error) {
            LOGGER.warn("Failed to parse video mode '{}'", s, error);
            return Optional.empty();
         }
      }
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getRedBits() {
      return this.redBits;
   }

   public int getGreenBits() {
      return this.greenBits;
   }

   public int getBlueBits() {
      return this.blueBits;
   }

   public float getRefreshRate() {
      return this.refreshRate;
   }

   public String refreshRateLabel() {
      float rate = this.getRefreshRate();
      return (double)rate == Math.rint((double)rate) ? Integer.toString((int)rate) : String.format(Locale.ROOT, "%.2f", rate);
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         VideoMode videoMode = (VideoMode)o;
         return this.width == videoMode.width && this.height == videoMode.height && this.redBits == videoMode.redBits && this.greenBits == videoMode.greenBits && this.blueBits == videoMode.blueBits && Float.compare(this.refreshRate, videoMode.refreshRate) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.width, this.height, this.redBits, this.greenBits, this.blueBits, this.refreshRate});
   }

   public String toString() {
      return String.format(Locale.ROOT, "%sx%s@%s (%sbit)", this.width, this.height, this.refreshRateLabel(), this.redBits + this.greenBits + this.blueBits);
   }

   public String write() {
      Optional var10000 = CODEC.encodeStart(JsonOps.INSTANCE, this).result();
      Gson var10001 = GSON;
      Objects.requireNonNull(var10001);
      return (String)var10000.map(var10001::toJson).orElseThrow();
   }
}
