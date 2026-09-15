package net.minecraft.world.level.levelgen.material.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

public interface MaterialCondition {
   Codec<MaterialCondition> DIRECT_CODEC = BuiltInRegistries.MATERIAL_CONDITION_TYPE.byNameCodec().dispatch(MaterialCondition::codec, Function.identity());
   Codec<MaterialCondition> CODEC = RegistryCodecs.holder(Registries.MATERIAL_CONDITION, DIRECT_CODEC).xmap((holder) -> {
      Objects.requireNonNull(holder);
      int index$1 = 0;
      Object var10000;
      //$FF: index$1->value
      //0->net/minecraft/core/Holder$Direct
      //1->net/minecraft/core/Holder$Reference
      switch (holder.typeSwitch<invokedynamic>(holder, index$1)) {
         case 0:
            Holder.Direct<MaterialCondition> direct = (Holder.Direct)holder;
            var10000 = direct.value();
            break;
         case 1:
            Holder.Reference<MaterialCondition> reference = (Holder.Reference)holder;
            var10000 = new HolderHolder(reference);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return (MaterialCondition)var10000;
   }, (value) -> {
      Objects.requireNonNull(value);
      int index$2 = 0;
      Holder var8;
      //$FF: index$2->value
      //0->net/minecraft/world/level/levelgen/material/condition/MaterialCondition$HolderHolder
      switch (value.typeSwitch<invokedynamic>(value, index$2)) {
         case 0:
            HolderHolder $b$0 = (HolderHolder)value;
            HolderHolder var10000 = $b$0;

            try {
               var7 = var10000.holder();
            } catch (Throwable var6) {
               throw new MatchException(var6.toString(), var6);
            }

            Holder patt3$temp = var7;
            var8 = patt3$temp;
            break;
         default:
            var8 = Holder.direct(value);
      }

      return var8;
   });

   ConditionEvaluator compile(MaterialRuleContext context);

   MapCodec<? extends MaterialCondition> codec();

   public static record HolderHolder(Holder<MaterialCondition> holder) implements MaterialCondition {
      public HolderHolder {
         super();
      }

      public ConditionEvaluator compile(final MaterialRuleContext context) {
         return ((MaterialCondition)this.holder.value()).compile(context);
      }

      public MapCodec<HolderHolder> codec() {
         throw new UnsupportedOperationException("HolderHolder cannot be serialized");
      }
   }
}
