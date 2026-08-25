package net.minecraft.world.level.levelgen.material.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

public interface MaterialRule {
   Codec<MaterialRule> DIRECT_CODEC = BuiltInRegistries.MATERIAL_RULE_TYPE.byNameCodec().dispatch(MaterialRule::codec, Function.identity());
   Codec<Holder<MaterialRule>> HOLDER_CODEC = RegistryCodecs.holder(Registries.MATERIAL_RULE, DIRECT_CODEC);
   Codec<MaterialRule> CODEC = HOLDER_CODEC.xmap((holder) -> {
      Objects.requireNonNull(holder);
      int index$1 = 0;
      Object var10000;
      //$FF: index$1->value
      //0->net/minecraft/core/Holder$Direct
      //1->net/minecraft/core/Holder$Reference
      switch (holder.typeSwitch<invokedynamic>(holder, index$1)) {
         case 0:
            Holder.Direct<MaterialRule> direct = (Holder.Direct)holder;
            var10000 = direct.value();
            break;
         case 1:
            Holder.Reference<MaterialRule> reference = (Holder.Reference)holder;
            var10000 = new HolderHolder(reference);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return (MaterialRule)var10000;
   }, (value) -> {
      Objects.requireNonNull(value);
      int index$2 = 0;
      Holder var8;
      //$FF: index$2->value
      //0->net/minecraft/world/level/levelgen/material/rule/MaterialRule$HolderHolder
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

   RuleEvaluator compile(MaterialRuleContext context);

   MapCodec<? extends MaterialRule> codec();

   public static record HolderHolder(Holder<MaterialRule> holder) implements MaterialRule {
      public HolderHolder {
         super();
      }

      public RuleEvaluator compile(final MaterialRuleContext context) {
         return ((MaterialRule)this.holder.value()).compile(context);
      }

      public MapCodec<HolderHolder> codec() {
         throw new UnsupportedOperationException("HolderHolder cannot be serialized");
      }
   }
}
