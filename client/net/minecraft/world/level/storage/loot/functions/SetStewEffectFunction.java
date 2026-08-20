package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetStewEffectFunction extends LootItemConditionalFunction {
   private static final Codec<List<EffectEntry>> EFFECTS_LIST;
   public static final MapCodec<SetStewEffectFunction> MAP_CODEC;
   private final List<EffectEntry> effects;

   private SetStewEffectFunction(final Optional<Holder<LootItemCondition>> condition, final List<EffectEntry> effects) {
      super(condition);
      this.effects = effects;
   }

   public MapCodec<SetStewEffectFunction> codec() {
      return MAP_CODEC;
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      Validatable.validate(context, "effects", this.effects);
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      if (itemStack.is(Items.SUSPICIOUS_STEW) && !this.effects.isEmpty()) {
         EffectEntry entry = (EffectEntry)Util.getRandom(this.effects, context.getRandom());
         Holder<MobEffect> effect = entry.effect();
         int duration = ((NumberProvider)entry.duration().value()).getInt(context);
         if (!((MobEffect)effect.value()).isInstantaneous()) {
            duration *= 20;
         }

         SuspiciousStewEffects.Entry newEntry = new SuspiciousStewEffects.Entry(effect, duration);
         itemStack.update(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY, newEntry, SuspiciousStewEffects::withEffectAdded);
         return itemStack;
      } else {
         return itemStack;
      }
   }

   public static Builder stewEffect() {
      return new Builder();
   }

   static {
      EFFECTS_LIST = SetStewEffectFunction.EffectEntry.CODEC.listOf().validate((entries) -> {
         Set<Holder<MobEffect>> seenEffects = new ObjectOpenHashSet();

         for(EffectEntry entry : entries) {
            if (!seenEffects.add(entry.effect())) {
               return DataResult.error(() -> "Encountered duplicate mob effect: '" + String.valueOf(entry.effect()) + "'");
            }
         }

         return DataResult.success(entries);
      });
      MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(EFFECTS_LIST.optionalFieldOf("effects", List.of()).forGetter((f) -> f.effects)).apply(i, SetStewEffectFunction::new));
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final ImmutableList.Builder<EffectEntry> effects = ImmutableList.builder();

      public Builder() {
         super();
      }

      protected Builder getThis() {
         return this;
      }

      public Builder withEffect(final Holder<MobEffect> effect, final Holder<NumberProvider> duration) {
         this.effects.add(new EffectEntry(effect, duration));
         return this;
      }

      public LootItemFunction build() {
         return new SetStewEffectFunction(this.getCondition(), this.effects.build());
      }
   }

   private static record EffectEntry(Holder<MobEffect> effect, Holder<NumberProvider> duration) implements LootContextUser {
      public static final Codec<EffectEntry> CODEC = RecordCodecBuilder.create((i) -> i.group(MobEffect.CODEC.fieldOf("type").forGetter(EffectEntry::effect), NumberProviders.CODEC.fieldOf("duration").forGetter(EffectEntry::duration)).apply(i, EffectEntry::new));

      private EffectEntry {
         super();
      }

      public void validate(final ValidationContext context) {
         LootContextUser.super.validate(context);
         Validatable.validateHolder(context, "duration", this.duration);
      }
   }
}
