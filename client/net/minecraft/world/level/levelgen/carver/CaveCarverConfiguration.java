package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class CaveCarverConfiguration extends CarverConfiguration {
   public static final Codec<CaveCarverConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(CarverConfiguration.CODEC.forGetter((var0x) -> {
         return var0x;
      }), FloatProvider.CODEC.fieldOf("horizontal_radius_multiplier").forGetter((var0x) -> {
         return var0x.horizontalRadiusMultiplier;
      }), FloatProvider.CODEC.fieldOf("vertical_radius_multiplier").forGetter((var0x) -> {
         return var0x.verticalRadiusMultiplier;
      }), FloatProvider.codec(-1.0F, 1.0F).fieldOf("floor_level").forGetter((var0x) -> {
         return var0x.floorLevel;
      })).apply(var0, CaveCarverConfiguration::new);
   });
   public final FloatProvider horizontalRadiusMultiplier;
   public final FloatProvider verticalRadiusMultiplier;
   final FloatProvider floorLevel;

   public CaveCarverConfiguration(float var1, HeightProvider var2, FloatProvider var3, VerticalAnchor var4, CarverDebugSettings var5, HolderSet<Block> var6, FloatProvider var7, FloatProvider var8, FloatProvider var9) {
      super(var1, var2, var3, var4, var5, var6);
      this.horizontalRadiusMultiplier = var7;
      this.verticalRadiusMultiplier = var8;
      this.floorLevel = var9;
   }

   public CaveCarverConfiguration(float var1, HeightProvider var2, FloatProvider var3, VerticalAnchor var4, HolderSet<Block> var5, FloatProvider var6, FloatProvider var7, FloatProvider var8) {
      this(var1, var2, var3, var4, CarverDebugSettings.DEFAULT, var5, var6, var7, var8);
   }

   public CaveCarverConfiguration(CarverConfiguration var1, FloatProvider var2, FloatProvider var3, FloatProvider var4) {
      this(var1.probability, var1.y, var1.yScale, var1.lavaLevel, var1.debugSettings, var1.replaceable, var2, var3, var4);
   }
}
