package net.minecraft.client;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.Nullable;

public enum GraphicsPreset implements StringRepresentable {
   FAST("fast", "options.graphics.fast"),
   FANCY("fancy", "options.graphics.fancy"),
   FABULOUS("fabulous", "options.graphics.fabulous"),
   CUSTOM("custom", "options.graphics.custom");

   private final String serializedName;
   private final String key;
   public static final Codec<GraphicsPreset> CODEC = StringRepresentable.<GraphicsPreset>fromEnum(GraphicsPreset::values);

   private GraphicsPreset(final String var3, final String var4) {
      this.serializedName = var3;
      this.key = var4;
   }

   public String getSerializedName() {
      return this.serializedName;
   }

   public String getKey() {
      return this.key;
   }

   public void apply(Minecraft var1) {
      OptionsSubScreen var2 = var1.screen instanceof OptionsSubScreen ? (OptionsSubScreen)var1.screen : null;
      switch (this.ordinal()) {
         case 0:
            boolean var5 = true;
            this.set(var2, var1.options.biomeBlendRadius(), 1);
            this.set(var2, var1.options.renderDistance(), 8);
            this.set(var2, var1.options.prioritizeChunkUpdates(), PrioritizeChunkUpdates.NONE);
            this.set(var2, var1.options.simulationDistance(), 6);
            this.set(var2, var1.options.ambientOcclusion(), false);
            this.set(var2, var1.options.cloudStatus(), CloudStatus.FAST);
            this.set(var2, var1.options.particles(), ParticleStatus.DECREASED);
            this.set(var2, var1.options.mipmapLevels(), 2);
            this.set(var2, var1.options.entityShadows(), false);
            this.set(var2, var1.options.entityDistanceScaling(), 0.75);
            this.set(var2, var1.options.menuBackgroundBlurriness(), 2);
            this.set(var2, var1.options.cloudRange(), 32);
            this.set(var2, var1.options.cutoutLeaves(), true);
            this.set(var2, var1.options.improvedTransparency(), false);
            this.set(var2, var1.options.weatherRadius(), 5);
            this.set(var2, var1.options.maxAnisotropyBit(), 0);
            break;
         case 1:
            boolean var4 = true;
            this.set(var2, var1.options.biomeBlendRadius(), 2);
            this.set(var2, var1.options.renderDistance(), 16);
            this.set(var2, var1.options.prioritizeChunkUpdates(), PrioritizeChunkUpdates.PLAYER_AFFECTED);
            this.set(var2, var1.options.simulationDistance(), 12);
            this.set(var2, var1.options.ambientOcclusion(), true);
            this.set(var2, var1.options.cloudStatus(), CloudStatus.FANCY);
            this.set(var2, var1.options.particles(), ParticleStatus.ALL);
            this.set(var2, var1.options.mipmapLevels(), 4);
            this.set(var2, var1.options.entityShadows(), true);
            this.set(var2, var1.options.entityDistanceScaling(), 1.0);
            this.set(var2, var1.options.menuBackgroundBlurriness(), 5);
            this.set(var2, var1.options.cloudRange(), 64);
            this.set(var2, var1.options.cutoutLeaves(), true);
            this.set(var2, var1.options.improvedTransparency(), false);
            this.set(var2, var1.options.weatherRadius(), 10);
            this.set(var2, var1.options.maxAnisotropyBit(), 1);
            break;
         case 2:
            boolean var3 = true;
            this.set(var2, var1.options.biomeBlendRadius(), 2);
            this.set(var2, var1.options.renderDistance(), 32);
            this.set(var2, var1.options.prioritizeChunkUpdates(), PrioritizeChunkUpdates.PLAYER_AFFECTED);
            this.set(var2, var1.options.simulationDistance(), 12);
            this.set(var2, var1.options.ambientOcclusion(), true);
            this.set(var2, var1.options.cloudStatus(), CloudStatus.FANCY);
            this.set(var2, var1.options.particles(), ParticleStatus.ALL);
            this.set(var2, var1.options.mipmapLevels(), 4);
            this.set(var2, var1.options.entityShadows(), true);
            this.set(var2, var1.options.entityDistanceScaling(), 1.25);
            this.set(var2, var1.options.menuBackgroundBlurriness(), 5);
            this.set(var2, var1.options.cloudRange(), 128);
            this.set(var2, var1.options.cutoutLeaves(), true);
            this.set(var2, var1.options.improvedTransparency(), true);
            this.set(var2, var1.options.weatherRadius(), 10);
            this.set(var2, var1.options.maxAnisotropyBit(), 2);
      }

   }

   <T> void set(@Nullable OptionsSubScreen var1, OptionInstance<T> var2, T var3) {
      if (var2.get() != var3) {
         var2.set(var3);
         if (var1 != null) {
            var1.resetOption(var2);
         }
      }

   }

   // $FF: synthetic method
   private static GraphicsPreset[] $values() {
      return new GraphicsPreset[]{FAST, FANCY, FABULOUS, CUSTOM};
   }
}
