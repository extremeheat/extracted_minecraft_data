package net.minecraft.client.renderer.item.properties.numeric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class Time extends NeedleDirectionHelper implements RangeSelectItemModelProperty {
   public static final MapCodec<Time> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.BOOL.optionalFieldOf("wobble", true).forGetter(NeedleDirectionHelper::wobble), Time.TimeSource.CODEC.fieldOf("source").forGetter((var0x) -> var0x.source)).apply(var0, Time::new));
   private final TimeSource source;
   private final RandomSource randomSource = RandomSource.create();
   private final NeedleDirectionHelper.Wobbler wobbler;

   public Time(boolean var1, TimeSource var2) {
      super(var1);
      this.source = var2;
      this.wobbler = this.newWobbler(0.9F);
   }

   protected float calculate(ItemStack var1, ClientLevel var2, int var3, Entity var4) {
      float var5 = this.source.get(var2, var1, var4, this.randomSource);
      long var6 = var2.getGameTime();
      if (this.wobbler.shouldUpdate(var6)) {
         this.wobbler.update(var6, var5);
      }

      return this.wobbler.rotation();
   }

   public MapCodec<Time> type() {
      return MAP_CODEC;
   }

   public static enum TimeSource implements StringRepresentable {
      RANDOM("random") {
         public float get(ClientLevel var1, ItemStack var2, Entity var3, RandomSource var4) {
            return var4.nextFloat();
         }
      },
      DAYTIME("daytime") {
         public float get(ClientLevel var1, ItemStack var2, Entity var3, RandomSource var4) {
            return var1.getTimeOfDay(1.0F);
         }
      },
      MOON_PHASE("moon_phase") {
         public float get(ClientLevel var1, ItemStack var2, Entity var3, RandomSource var4) {
            return (float)var1.getMoonPhase() / 8.0F;
         }
      };

      public static final Codec<TimeSource> CODEC = StringRepresentable.<TimeSource>fromEnum(TimeSource::values);
      private final String name;

      TimeSource(final String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      abstract float get(ClientLevel var1, ItemStack var2, Entity var3, RandomSource var4);

      // $FF: synthetic method
      private static TimeSource[] $values() {
         return new TimeSource[]{RANDOM, DAYTIME, MOON_PHASE};
      }
   }
}
