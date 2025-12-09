package net.minecraft.gametest.framework;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Rotation;

public record TestData<EnvironmentType>(EnvironmentType environment, Identifier structure, int maxTicks, int setupTicks, boolean required, Rotation rotation, boolean manualOnly, int maxAttempts, int requiredSuccesses, boolean skyAccess) {
   public static final MapCodec<TestData<Holder<TestEnvironmentDefinition>>> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(TestEnvironmentDefinition.CODEC.fieldOf("environment").forGetter(TestData::environment), Identifier.CODEC.fieldOf("structure").forGetter(TestData::structure), ExtraCodecs.POSITIVE_INT.fieldOf("max_ticks").forGetter(TestData::maxTicks), ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("setup_ticks", 0).forGetter(TestData::setupTicks), Codec.BOOL.optionalFieldOf("required", true).forGetter(TestData::required), Rotation.CODEC.optionalFieldOf("rotation", Rotation.NONE).forGetter(TestData::rotation), Codec.BOOL.optionalFieldOf("manual_only", false).forGetter(TestData::manualOnly), ExtraCodecs.POSITIVE_INT.optionalFieldOf("max_attempts", 1).forGetter(TestData::maxAttempts), ExtraCodecs.POSITIVE_INT.optionalFieldOf("required_successes", 1).forGetter(TestData::requiredSuccesses), Codec.BOOL.optionalFieldOf("sky_access", false).forGetter(TestData::skyAccess)).apply(var0, TestData::new));

   public TestData(EnvironmentType var1, Identifier var2, int var3, int var4, boolean var5, Rotation var6) {
      this(var1, var2, var3, var4, var5, var6, false, 1, 1, false);
   }

   public TestData(EnvironmentType var1, Identifier var2, int var3, int var4, boolean var5) {
      this(var1, var2, var3, var4, var5, Rotation.NONE);
   }

   public TestData(EnvironmentType var1, Identifier var2, int var3, int var4, boolean var5, Rotation var6, boolean var7, int var8, int var9, boolean var10) {
      super();
      this.environment = var1;
      this.structure = var2;
      this.maxTicks = var3;
      this.setupTicks = var4;
      this.required = var5;
      this.rotation = var6;
      this.manualOnly = var7;
      this.maxAttempts = var8;
      this.requiredSuccesses = var9;
      this.skyAccess = var10;
   }

   public <T> TestData<T> map(Function<EnvironmentType, T> var1) {
      return new TestData<T>(var1.apply(this.environment), this.structure, this.maxTicks, this.setupTicks, this.required, this.rotation, this.manualOnly, this.maxAttempts, this.requiredSuccesses, this.skyAccess);
   }
}
