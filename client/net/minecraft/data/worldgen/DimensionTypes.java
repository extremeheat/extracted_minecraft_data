package net.minecraft.data.worldgen;

import java.util.Optional;
import java.util.OptionalLong;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionSpecialEffects;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionTypes {
   public static final float CLOUD_LEVEL = 192.0F;

   public DimensionTypes() {
      super();
   }

   public static void bootstrap(BootstrapContext<DimensionType> var0) {
      DimensionSpecialEffects var1 = new DimensionSpecialEffects(Optional.of(192.0F), true, Optional.of(DimensionSpecialEffects.OverworldSky.INSTANCE), false, false, DimensionSpecialEffects.FogScaler.OVERWORLD, false, true);
      Optional var2 = Optional.of(new DimensionSpecialEffects.CubeSky(ResourceLocation.withDefaultNamespace("block/bedrock"), 4, 50.0F, Component.translatable("sky.default_hub")));
      Optional var3 = Optional.of(new DimensionSpecialEffects.CodeSky());
      var0.register(BuiltinDimensionTypes.OVERWORLD, new DimensionType(OptionalLong.empty(), true, false, false, true, 1.0, true, false, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD, new DimensionSpecialEffects(Optional.empty(), false, var3, true, true, DimensionSpecialEffects.FogScaler.UNSCALED, false, false), 0.0F, new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)));
      var0.register(BuiltinDimensionTypes.GENERATED, new DimensionType(OptionalLong.empty(), true, false, false, true, 1.0, true, false, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD, var1, 0.0F, new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)));
      var0.register(BuiltinDimensionTypes.NETHER, new DimensionType(OptionalLong.of(18000L), false, true, true, false, 8.0, false, true, 0, 256, 128, BlockTags.INFINIBURN_NETHER, new DimensionSpecialEffects(Optional.empty(), true, Optional.empty(), false, true, DimensionSpecialEffects.FogScaler.UNSCALED, true, false), 0.1F, new DimensionType.MonsterSettings(true, false, ConstantInt.of(7), 15)));
      var0.register(BuiltinDimensionTypes.END, new DimensionType(OptionalLong.of(6000L), false, false, false, false, 1.0, false, false, 0, 256, 256, BlockTags.INFINIBURN_END, new DimensionSpecialEffects(Optional.empty(), false, Optional.of(DimensionSpecialEffects.EndSky.INSTANCE), true, false, DimensionSpecialEffects.FogScaler.END, false, false), 0.0F, new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)));
      var0.register(BuiltinDimensionTypes.OVERWORLD_CAVES, new DimensionType(OptionalLong.empty(), true, true, false, true, 1.0, true, false, -64, 384, 384, BlockTags.INFINIBURN_OVERWORLD, var1, 0.0F, new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)));
   }
}
