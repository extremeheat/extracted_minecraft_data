package net.minecraft.client;

import com.mojang.blaze3d.GraphicsWorkarounds;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public enum GraphicsPreset implements StringRepresentable {
   FAST("fast", "options.graphics.fast"),
   FANCY("fancy", "options.graphics.fancy"),
   FABULOUS("fabulous", "options.graphics.fabulous"),
   CUSTOM("custom", "options.graphics.custom");

   private final String serializedName;
   private final String key;
   public static final Codec<GraphicsPreset> CODEC = StringRepresentable.<GraphicsPreset>fromEnum(GraphicsPreset::values);

   private GraphicsPreset(final String serializedName, final String key) {
      this.serializedName = serializedName;
      this.key = key;
   }

   public String getSerializedName() {
      return this.serializedName;
   }

   public String getKey() {
      return this.key;
   }

   public void apply(final Minecraft minecraft) {
      OptionsSubScreen screen = minecraft.screen instanceof OptionsSubScreen ? (OptionsSubScreen)minecraft.screen : null;
      GpuDevice device = RenderSystem.getDevice();
      switch (this.ordinal()) {
         case 0:
            int viewDistance = 8;
            this.set(screen, minecraft.options.biomeBlendRadius(), 1);
            this.set(screen, minecraft.options.renderDistance(), 8);
            this.set(screen, minecraft.options.prioritizeChunkUpdates(), PrioritizeChunkUpdates.NONE);
            this.set(screen, minecraft.options.simulationDistance(), 6);
            this.set(screen, minecraft.options.ambientOcclusion(), false);
            this.set(screen, minecraft.options.cloudStatus(), CloudStatus.FAST);
            this.set(screen, minecraft.options.particles(), ParticleStatus.DECREASED);
            this.set(screen, minecraft.options.mipmapLevels(), 2);
            this.set(screen, minecraft.options.entityShadows(), false);
            this.set(screen, minecraft.options.entityDistanceScaling(), 0.75);
            this.set(screen, minecraft.options.menuBackgroundBlurriness(), 2);
            this.set(screen, minecraft.options.cloudRange(), 32);
            this.set(screen, minecraft.options.cutoutLeaves(), false);
            this.set(screen, minecraft.options.improvedTransparency(), false);
            this.set(screen, minecraft.options.weatherRadius(), 5);
            this.set(screen, minecraft.options.maxAnisotropyBit(), 1);
            this.set(screen, minecraft.options.textureFiltering(), TextureFilteringMethod.NONE);
            break;
         case 1:
            int viewDistance = 16;
            this.set(screen, minecraft.options.biomeBlendRadius(), 2);
            this.set(screen, minecraft.options.renderDistance(), 16);
            this.set(screen, minecraft.options.prioritizeChunkUpdates(), PrioritizeChunkUpdates.PLAYER_AFFECTED);
            this.set(screen, minecraft.options.simulationDistance(), 12);
            this.set(screen, minecraft.options.ambientOcclusion(), true);
            this.set(screen, minecraft.options.cloudStatus(), CloudStatus.FANCY);
            this.set(screen, minecraft.options.particles(), ParticleStatus.ALL);
            this.set(screen, minecraft.options.mipmapLevels(), 4);
            this.set(screen, minecraft.options.entityShadows(), true);
            this.set(screen, minecraft.options.entityDistanceScaling(), 1.0);
            this.set(screen, minecraft.options.menuBackgroundBlurriness(), 5);
            this.set(screen, minecraft.options.cloudRange(), 64);
            this.set(screen, minecraft.options.cutoutLeaves(), true);
            this.set(screen, minecraft.options.improvedTransparency(), false);
            this.set(screen, minecraft.options.weatherRadius(), 10);
            this.set(screen, minecraft.options.maxAnisotropyBit(), 1);
            this.set(screen, minecraft.options.textureFiltering(), TextureFilteringMethod.RGSS);
            break;
         case 2:
            int viewDistance = 32;
            this.set(screen, minecraft.options.biomeBlendRadius(), 2);
            this.set(screen, minecraft.options.renderDistance(), 32);
            this.set(screen, minecraft.options.prioritizeChunkUpdates(), PrioritizeChunkUpdates.PLAYER_AFFECTED);
            this.set(screen, minecraft.options.simulationDistance(), 12);
            this.set(screen, minecraft.options.ambientOcclusion(), true);
            this.set(screen, minecraft.options.cloudStatus(), CloudStatus.FANCY);
            this.set(screen, minecraft.options.particles(), ParticleStatus.ALL);
            this.set(screen, minecraft.options.mipmapLevels(), 4);
            this.set(screen, minecraft.options.entityShadows(), true);
            this.set(screen, minecraft.options.entityDistanceScaling(), 1.25);
            this.set(screen, minecraft.options.menuBackgroundBlurriness(), 5);
            this.set(screen, minecraft.options.cloudRange(), 128);
            this.set(screen, minecraft.options.cutoutLeaves(), true);
            this.set(screen, minecraft.options.improvedTransparency(), Util.getPlatform() != Util.OS.OSX);
            this.set(screen, minecraft.options.weatherRadius(), 10);
            this.set(screen, minecraft.options.maxAnisotropyBit(), 2);
            if (GraphicsWorkarounds.get(device).isAmd()) {
               this.set(screen, minecraft.options.textureFiltering(), TextureFilteringMethod.RGSS);
            } else {
               this.set(screen, minecraft.options.textureFiltering(), TextureFilteringMethod.ANISOTROPIC);
            }
      }

   }

   <T> void set(final @Nullable OptionsSubScreen screen, final OptionInstance<T> option, final T value) {
      if (option.get() != value) {
         option.set(value);
         if (screen != null) {
            screen.resetOption(option);
         }
      }

   }

   // $FF: synthetic method
   private static GraphicsPreset[] $values() {
      return new GraphicsPreset[]{FAST, FANCY, FABULOUS, CUSTOM};
   }
}
