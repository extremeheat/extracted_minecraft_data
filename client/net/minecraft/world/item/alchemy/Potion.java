package net.minecraft.world.item.alchemy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

public class Potion {
   @Nullable
   private final String name;
   private final ImmutableList<MobEffectInstance> effects;

   public static Potion byName(String var0) {
      return (Potion)Registry.POTION.get(ResourceLocation.tryParse(var0));
   }

   public Potion(MobEffectInstance... var1) {
      this((String)null, var1);
   }

   public Potion(@Nullable String var1, MobEffectInstance... var2) {
      super();
      this.name = var1;
      this.effects = ImmutableList.copyOf(var2);
   }

   public String getName(String var1) {
      return var1 + (this.name == null ? Registry.POTION.getKey(this).getPath() : this.name);
   }

   public List<MobEffectInstance> getEffects() {
      return this.effects;
   }

   public boolean hasInstantEffects() {
      if (!this.effects.isEmpty()) {
         UnmodifiableIterator var1 = this.effects.iterator();

         while(var1.hasNext()) {
            MobEffectInstance var2 = (MobEffectInstance)var1.next();
            if (var2.getEffect().isInstantenous()) {
               return true;
            }
         }
      }

      return false;
   }
}
