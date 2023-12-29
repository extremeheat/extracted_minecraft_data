package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public record TimeCheck(Optional<Long> b, IntRange c) implements LootItemCondition {
   private final Optional<Long> period;
   private final IntRange value;
   public static final Codec<TimeCheck> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.strictOptionalField(Codec.LONG, "period").forGetter(TimeCheck::period), IntRange.CODEC.fieldOf("value").forGetter(TimeCheck::value)
            )
            .apply(var0, TimeCheck::new)
   );

   public TimeCheck(Optional<Long> var1, IntRange var2) {
      super();
      this.period = var1;
      this.value = var2;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.TIME_CHECK;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.value.getReferencedContextParams();
   }

   public boolean test(LootContext var1) {
      ServerLevel var2 = var1.getLevel();
      long var3 = var2.getDayTime();
      if (this.period.isPresent()) {
         var3 %= this.period.get();
      }

      return this.value.test(var1, (int)var3);
   }

   public static TimeCheck.Builder time(IntRange var0) {
      return new TimeCheck.Builder(var0);
   }

   public static class Builder implements LootItemCondition.Builder {
      private Optional<Long> period = Optional.empty();
      private final IntRange value;

      public Builder(IntRange var1) {
         super();
         this.value = var1;
      }

      public TimeCheck.Builder setPeriod(long var1) {
         this.period = Optional.of(var1);
         return this;
      }

      public TimeCheck build() {
         return new TimeCheck(this.period, this.value);
      }
   }
}
