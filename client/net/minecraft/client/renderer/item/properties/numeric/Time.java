package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.MoonPhase;

public class Time extends NeedleDirectionHelper implements RangeSelectItemModelProperty {
   public static final MapCodec<Time> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.BOOL.optionalFieldOf("wobble", true).forGetter(NeedleDirectionHelper::wobble), Time.TimeSource.CODEC.fieldOf("source").forGetter((o) -> o.source)).apply(i, Time::new));
   private final TimeSource source;
   private final RandomSource randomSource = RandomSource.create();
   private final NeedleDirectionHelper.Wobbler wobbler;

   public Time(final boolean wooble, final TimeSource source) {
      super(wooble);
      this.source = source;
      this.wobbler = this.newWobbler(0.9F);
   }

   protected float calculate(final ItemStack itemStack, final ClientLevel level, final int seed, final ItemOwner owner) {
      float targetRotation = this.source.get(level, itemStack, owner, this.randomSource);
      long gameTime = level.getGameTime();
      if (this.wobbler.shouldUpdate(gameTime)) {
         this.wobbler.update(gameTime, targetRotation);
      }

      return this.wobbler.rotation();
   }

   public MapCodec<Time> type() {
      return MAP_CODEC;
   }

   public static enum TimeSource implements StringRepresentable {
      RANDOM("random") {
         public float get(final ClientLevel level, final ItemStack itemStack, final ItemOwner owner, final RandomSource random) {
            return random.nextFloat();
         }
      },
      DAYTIME("daytime") {
         public float get(final ClientLevel level, final ItemStack itemStack, final ItemOwner owner, final RandomSource random) {
            return (Float)level.environmentAttributes().getValue(EnvironmentAttributes.SUN_ANGLE, owner.position()) / 360.0F;
         }
      },
      MOON_PHASE("moon_phase") {
         public float get(final ClientLevel level, final ItemStack itemStack, final ItemOwner owner, final RandomSource random) {
            return (float)((MoonPhase)level.environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, owner.position())).index() / (float)MoonPhase.COUNT;
         }
      };

      public static final Codec<TimeSource> CODEC = StringRepresentable.<TimeSource>fromEnum(TimeSource::values);
      private final String name;

      private TimeSource(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      abstract float get(final ClientLevel level, final ItemStack itemStack, final ItemOwner owner, final RandomSource random);

      // $FF: synthetic method
      private static TimeSource[] $values() {
         return new TimeSource[]{RANDOM, DAYTIME, MOON_PHASE};
      }
   }
}
