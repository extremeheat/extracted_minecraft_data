package net.minecraft.world.level.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.phys.Vec3;

public record DimensionSpecialEffects(Optional<Float> cloudLevel, boolean hasGround, Optional<Sky> sky, boolean forceBrightLightmap, boolean constantAmbientLight, FogScaler fogScaler, boolean isAlwaysFoggy, boolean hasSunriseAndSunset) {
   public static final Codec<DimensionSpecialEffects> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.FLOAT.optionalFieldOf("cloud_level").forGetter(DimensionSpecialEffects::cloudLevel), Codec.BOOL.fieldOf("has_ground").forGetter(DimensionSpecialEffects::hasGround), DimensionSpecialEffects.Sky.CODEC.optionalFieldOf("sky_type").forGetter(DimensionSpecialEffects::sky), Codec.BOOL.fieldOf("force_bright_lightmap").forGetter(DimensionSpecialEffects::forceBrightLightmap), Codec.BOOL.fieldOf("constant_ambient_light").forGetter(DimensionSpecialEffects::constantAmbientLight), DimensionSpecialEffects.FogScaler.CODEC.fieldOf("fog_scaler").forGetter(DimensionSpecialEffects::fogScaler), Codec.BOOL.fieldOf("is_always_foggy").forGetter(DimensionSpecialEffects::isAlwaysFoggy), Codec.BOOL.fieldOf("has_sunrise_and_sunset").forGetter(DimensionSpecialEffects::hasSunriseAndSunset)).apply(var0, DimensionSpecialEffects::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, DimensionSpecialEffects> STREAM_CODEC;
   private static final float SUNRISE_AND_SUNSET_TIMESPAN = 0.4F;

   public DimensionSpecialEffects(Optional<Float> var1, boolean var2, Optional<Sky> var3, boolean var4, boolean var5, FogScaler var6, boolean var7, boolean var8) {
      super();
      this.cloudLevel = var1;
      this.hasGround = var2;
      this.sky = var3;
      this.forceBrightLightmap = var4;
      this.constantAmbientLight = var5;
      this.fogScaler = var6;
      this.isAlwaysFoggy = var7;
      this.hasSunriseAndSunset = var8;
   }

   public DimensionSpecialEffects withSkyType(Optional<Sky> var1) {
      return new DimensionSpecialEffects(this.cloudLevel, this.hasGround, var1, this.forceBrightLightmap, this.constantAmbientLight, this.fogScaler, this.isAlwaysFoggy, this.hasSunriseAndSunset);
   }

   public boolean isSunriseOrSunset(float var1) {
      if (!this.hasSunriseAndSunset) {
         return false;
      } else {
         float var2 = Mth.cos(var1 * 6.2831855F);
         return var2 >= -0.4F && var2 <= 0.4F;
      }
   }

   public int getSunriseOrSunsetColor(float var1) {
      if (!this.hasSunriseAndSunset) {
         return 0;
      } else {
         float var2 = Mth.cos(var1 * 6.2831855F);
         float var3 = var2 / 0.4F * 0.5F + 0.5F;
         float var4 = Mth.square(1.0F - (1.0F - Mth.sin(var3 * 3.1415927F)) * 0.99F);
         return ARGB.colorFromFloat(var4, var3 * 0.3F + 0.7F, var3 * var3 * 0.7F + 0.2F, 0.2F);
      }
   }

   public float getCloudHeight() {
      return (Float)this.cloudLevel.orElse(0.0F / 0.0F);
   }

   public Vec3 getBrightnessDependentFogColor(Vec3 var1, float var2) {
      return this.fogScaler.getBrightnessDependentFogColor(var1, var2);
   }

   public boolean isFoggyAt(int var1, int var2) {
      return this.isAlwaysFoggy;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT.apply(ByteBufCodecs::optional), DimensionSpecialEffects::cloudLevel, ByteBufCodecs.BOOL, DimensionSpecialEffects::hasGround, DimensionSpecialEffects.Sky.STREAM_CODEC.apply(ByteBufCodecs::optional), DimensionSpecialEffects::sky, ByteBufCodecs.BOOL, DimensionSpecialEffects::forceBrightLightmap, ByteBufCodecs.BOOL, DimensionSpecialEffects::constantAmbientLight, DimensionSpecialEffects.FogScaler.STREAM_CODEC, DimensionSpecialEffects::fogScaler, ByteBufCodecs.BOOL, DimensionSpecialEffects::isAlwaysFoggy, ByteBufCodecs.BOOL, DimensionSpecialEffects::hasSunriseAndSunset, DimensionSpecialEffects::new);
   }

   public interface Sky extends TooltipProvider {
      Codec<Sky> CODEC = DimensionSpecialEffects.SkyType.CODEC.dispatch(Sky::type, (var0) -> var0.codec);
      StreamCodec<RegistryFriendlyByteBuf, Sky> STREAM_CODEC = DimensionSpecialEffects.SkyType.STREAM_CODEC.cast().dispatch(Sky::type, (var0) -> var0.streamCodec);

      SkyType type();

      Component displayName();

      default void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
         var2.accept(Component.translatable("sky.tooltip", this.displayName()));
      }
   }

   public static class OverworldSky implements Sky {
      public static final OverworldSky INSTANCE = new OverworldSky();
      public static final MapCodec<OverworldSky> MAP_CODEC;
      public static final StreamCodec<RegistryFriendlyByteBuf, OverworldSky> STREAM_CODEC;
      public static final Component NAME;

      public OverworldSky() {
         super();
      }

      public SkyType type() {
         return DimensionSpecialEffects.SkyType.OVERWORLD;
      }

      public Component displayName() {
         return NAME;
      }

      static {
         MAP_CODEC = MapCodec.unit(INSTANCE);
         STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, OverworldSky>unit(INSTANCE);
         NAME = Component.translatable("sky.overworld");
      }
   }

   public static class EndSky implements Sky {
      public static final Component NAME = Component.translatable("sky.end");
      public static final EndSky INSTANCE = new EndSky();
      public static final MapCodec<EndSky> MAP_CODEC;
      public static final StreamCodec<RegistryFriendlyByteBuf, EndSky> STREAM_CODEC;

      public EndSky() {
         super();
      }

      public SkyType type() {
         return DimensionSpecialEffects.SkyType.END;
      }

      public Component displayName() {
         return NAME;
      }

      static {
         MAP_CODEC = MapCodec.unit(INSTANCE);
         STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, EndSky>unit(INSTANCE);
      }
   }

   public static class Panorama implements Sky {
      public static final Component NAME = Component.translatable("sky.panorama");
      public static final Panorama INSTANCE = new Panorama();
      public static final MapCodec<Panorama> MAP_CODEC;
      public static final StreamCodec<RegistryFriendlyByteBuf, Panorama> STREAM_CODEC;

      public Panorama() {
         super();
      }

      public SkyType type() {
         return DimensionSpecialEffects.SkyType.PANORAMA;
      }

      public Component displayName() {
         return NAME;
      }

      static {
         MAP_CODEC = MapCodec.unit(INSTANCE);
         STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, Panorama>unit(INSTANCE);
      }
   }

   public static class CodeSky implements Sky {
      public static final Component NAME = Component.translatable("sky.code");
      public static final CodeSky INSTANCE = new CodeSky();
      public static final MapCodec<CodeSky> MAP_CODEC;
      public static final StreamCodec<RegistryFriendlyByteBuf, CodeSky> STREAM_CODEC;

      public CodeSky() {
         super();
      }

      public SkyType type() {
         return DimensionSpecialEffects.SkyType.CODE;
      }

      public Component displayName() {
         return NAME;
      }

      static {
         MAP_CODEC = MapCodec.unit(INSTANCE);
         STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, CodeSky>unit(INSTANCE);
      }
   }

   public static record CubeSky(ResourceLocation textureId, int repeats, float size, Component title) implements Sky {
      public static final MapCodec<CubeSky> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("texture").forGetter(CubeSky::textureId), ExtraCodecs.POSITIVE_INT.fieldOf("repeats").forGetter(CubeSky::repeats), Codec.FLOAT.fieldOf("size").forGetter(CubeSky::size), ComponentSerialization.CODEC.fieldOf("name").forGetter(CubeSky::title)).apply(var0, CubeSky::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, CubeSky> STREAM_CODEC;

      public CubeSky(ResourceLocation var1, int var2, float var3, Component var4) {
         super();
         this.textureId = var1;
         this.repeats = var2;
         this.size = var3;
         this.title = var4;
      }

      public SkyType type() {
         return DimensionSpecialEffects.SkyType.CUBE;
      }

      public Component displayName() {
         return this.title;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, CubeSky::textureId, ByteBufCodecs.VAR_INT, CubeSky::repeats, ByteBufCodecs.FLOAT, CubeSky::size, ComponentSerialization.TRUSTED_STREAM_CODEC, CubeSky::title, CubeSky::new);
      }
   }

   public static enum SkyType implements StringRepresentable {
      OVERWORLD(0, "overworld", DimensionSpecialEffects.OverworldSky.MAP_CODEC, DimensionSpecialEffects.OverworldSky.STREAM_CODEC),
      END(1, "end", DimensionSpecialEffects.EndSky.MAP_CODEC, DimensionSpecialEffects.EndSky.STREAM_CODEC),
      CUBE(2, "cube", DimensionSpecialEffects.CubeSky.MAP_CODEC, DimensionSpecialEffects.CubeSky.STREAM_CODEC),
      PANORAMA(3, "panorama", DimensionSpecialEffects.Panorama.MAP_CODEC, DimensionSpecialEffects.Panorama.STREAM_CODEC),
      CODE(4, "code", DimensionSpecialEffects.CodeSky.MAP_CODEC, DimensionSpecialEffects.CodeSky.STREAM_CODEC);

      private static final IntFunction<SkyType> BY_ID = ByIdMap.<SkyType>continuous((var0) -> var0.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final Codec<SkyType> CODEC = StringRepresentable.<SkyType>fromEnum(SkyType::values);
      public static final StreamCodec<RegistryFriendlyByteBuf, SkyType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (var0) -> var0.id).cast();
      private final int id;
      private final String name;
      final MapCodec<? extends Sky> codec;
      final StreamCodec<? super RegistryFriendlyByteBuf, ? extends Sky> streamCodec;

      private <T extends Sky> SkyType(final int var3, final String var4, final MapCodec<T> var5, final StreamCodec<RegistryFriendlyByteBuf, T> var6) {
         this.id = var3;
         this.name = var4;
         this.codec = var5;
         this.streamCodec = var6;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static SkyType[] $values() {
         return new SkyType[]{OVERWORLD, END, CUBE, PANORAMA, CODE};
      }
   }

   public static enum FogScaler implements StringRepresentable {
      UNSCALED(0, "unscaled") {
         public Vec3 getBrightnessDependentFogColor(Vec3 var1, float var2) {
            return var1;
         }
      },
      OVERWORLD(1, "overworld") {
         public Vec3 getBrightnessDependentFogColor(Vec3 var1, float var2) {
            return var1.multiply((double)(var2 * 0.94F + 0.06F), (double)(var2 * 0.94F + 0.06F), (double)(var2 * 0.91F + 0.09F));
         }
      },
      END(2, "end") {
         public Vec3 getBrightnessDependentFogColor(Vec3 var1, float var2) {
            return var1.scale(0.15000000596046448);
         }
      };

      private static final IntFunction<FogScaler> BY_ID = ByIdMap.<FogScaler>continuous((var0) -> var0.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final Codec<FogScaler> CODEC = StringRepresentable.<FogScaler>fromEnum(FogScaler::values);
      public static final StreamCodec<ByteBuf, FogScaler> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (var0) -> var0.id);
      private final int id;
      private final String name;

      FogScaler(final int var3, final String var4) {
         this.id = var3;
         this.name = var4;
      }

      public String getSerializedName() {
         return this.name;
      }

      public abstract Vec3 getBrightnessDependentFogColor(Vec3 var1, float var2);

      // $FF: synthetic method
      private static FogScaler[] $values() {
         return new FogScaler[]{UNSCALED, OVERWORLD, END};
      }
   }
}
