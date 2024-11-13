package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

public record SuspiciousStewEffects(List<Entry> effects) implements ConsumableListener, TooltipProvider {
   public static final SuspiciousStewEffects EMPTY = new SuspiciousStewEffects(List.of());
   public static final int DEFAULT_DURATION = 160;
   public static final Codec<SuspiciousStewEffects> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, SuspiciousStewEffects> STREAM_CODEC;

   public SuspiciousStewEffects(List<Entry> var1) {
      super();
      this.effects = var1;
   }

   public SuspiciousStewEffects withEffectAdded(Entry var1) {
      return new SuspiciousStewEffects(Util.copyAndAdd(this.effects, var1));
   }

   public void onConsume(Level var1, LivingEntity var2, ItemStack var3, Consumable var4) {
      for(Entry var6 : this.effects) {
         var2.addEffect(var6.createEffectInstance());
      }

   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3) {
      if (var3.isCreative()) {
         ArrayList var4 = new ArrayList();

         for(Entry var6 : this.effects) {
            var4.add(var6.createEffectInstance());
         }

         PotionContents.addPotionTooltip(var4, var2, 1.0F, var1.tickRate());
      }

   }

   static {
      CODEC = SuspiciousStewEffects.Entry.CODEC.listOf().xmap(SuspiciousStewEffects::new, SuspiciousStewEffects::effects);
      STREAM_CODEC = SuspiciousStewEffects.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()).map(SuspiciousStewEffects::new, SuspiciousStewEffects::effects);
   }

   public static record Entry(Holder<MobEffect> effect, int duration) {
      public static final Codec<Entry> CODEC = RecordCodecBuilder.create((var0) -> var0.group(MobEffect.CODEC.fieldOf("id").forGetter(Entry::effect), Codec.INT.lenientOptionalFieldOf("duration", 160).forGetter(Entry::duration)).apply(var0, Entry::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;

      public Entry(Holder<MobEffect> var1, int var2) {
         super();
         this.effect = var1;
         this.duration = var2;
      }

      public MobEffectInstance createEffectInstance() {
         return new MobEffectInstance(this.effect, this.duration);
      }

      static {
         STREAM_CODEC = StreamCodec.composite(MobEffect.STREAM_CODEC, Entry::effect, ByteBufCodecs.VAR_INT, Entry::duration, Entry::new);
      }
   }
}
