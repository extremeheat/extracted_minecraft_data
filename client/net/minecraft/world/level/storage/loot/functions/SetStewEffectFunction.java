package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetStewEffectFunction extends LootItemConditionalFunction {
   private static final Codec<List<SetStewEffectFunction.EffectEntry>> EFFECTS_LIST = SetStewEffectFunction.EffectEntry.CODEC.listOf().validate(var0 -> {
      ObjectOpenHashSet var1 = new ObjectOpenHashSet();

      for (SetStewEffectFunction.EffectEntry var3 : var0) {
         if (!var1.add(var3.effect())) {
            return DataResult.error(() -> "Encountered duplicate mob effect: '" + var3.effect() + "'");
         }
      }

      return DataResult.success(var0);
   });
   public static final MapCodec<SetStewEffectFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(EFFECTS_LIST.optionalFieldOf("effects", List.of()).forGetter(var0x -> var0x.effects))
            .apply(var0, SetStewEffectFunction::new)
   );
   private final List<SetStewEffectFunction.EffectEntry> effects;

   SetStewEffectFunction(List<LootItemCondition> var1, List<SetStewEffectFunction.EffectEntry> var2) {
      super(var1);
      this.effects = var2;
   }

   @Override
   public LootItemFunctionType<SetStewEffectFunction> getType() {
      return LootItemFunctions.SET_STEW_EFFECT;
   }

   @Override
   public Set<ContextKey<?>> getReferencedContextParams() {
      return this.effects.stream().flatMap(var0 -> var0.duration().getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.is(Items.SUSPICIOUS_STEW) && !this.effects.isEmpty()) {
         SetStewEffectFunction.EffectEntry var3 = Util.getRandom(this.effects, var2.getRandom());
         Holder var4 = var3.effect();
         int var5 = var3.duration().getInt(var2);
         if (!((MobEffect)var4.value()).isInstantenous()) {
            var5 *= 20;
         }

         SuspiciousStewEffects.Entry var6 = new SuspiciousStewEffects.Entry(var4, var5);
         var1.update(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY, var6, SuspiciousStewEffects::withEffectAdded);
         return var1;
      } else {
         return var1;
      }
   }

   public static SetStewEffectFunction.Builder stewEffect() {
      return new SetStewEffectFunction.Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetStewEffectFunction.Builder> {
      private final com.google.common.collect.ImmutableList.Builder<SetStewEffectFunction.EffectEntry> effects = ImmutableList.builder();

      public Builder() {
         super();
      }

      protected SetStewEffectFunction.Builder getThis() {
         return this;
      }

      public SetStewEffectFunction.Builder withEffect(Holder<MobEffect> var1, NumberProvider var2) {
         this.effects.add(new SetStewEffectFunction.EffectEntry(var1, var2));
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new SetStewEffectFunction(this.getConditions(), this.effects.build());
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
