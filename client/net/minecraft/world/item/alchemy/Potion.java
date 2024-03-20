package net.minecraft.world.item.alchemy;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;

public class Potion {
   @Nullable
   private final String name;
   private final List<MobEffectInstance> effects;

   public Potion(MobEffectInstance... var1) {
      this(null, var1);
   }

   public Potion(@Nullable String var1, MobEffectInstance... var2) {
      super();
      this.name = var1;
      this.effects = List.of(var2);
   }

   public static String getName(Optional<Holder<Potion>> var0, String var1) {
      if (var0.isPresent()) {
         String var2 = ((Potion)((Holder)var0.get()).value()).name;
         if (var2 != null) {
            return var1 + var2;
         }
      }

      String var3 = var0.flatMap(Holder::unwrapKey).map(var0x -> var0x.location().getPath()).orElse("empty");
      return var1 + var3;
   }

   public List<MobEffectInstance> getEffects() {
      return this.effects;
   }

   public boolean hasInstantEffects() {
      if (!this.effects.isEmpty()) {
         for(MobEffectInstance var2 : this.effects) {
            if (var2.getEffect().value().isInstantenous()) {
               return true;
            }
         }
      }

      return false;
   }
}