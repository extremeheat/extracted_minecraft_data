package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetFireworkExplosionFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetFireworkExplosionFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(var0.group(FireworkExplosion.Shape.CODEC.optionalFieldOf("shape").forGetter((var0x) -> {
         return var0x.shape;
      }), FireworkExplosion.COLOR_LIST_CODEC.optionalFieldOf("colors").forGetter((var0x) -> {
         return var0x.colors;
      }), FireworkExplosion.COLOR_LIST_CODEC.optionalFieldOf("fade_colors").forGetter((var0x) -> {
         return var0x.fadeColors;
      }), Codec.BOOL.optionalFieldOf("trail").forGetter((var0x) -> {
         return var0x.trail;
      }), Codec.BOOL.optionalFieldOf("twinkle").forGetter((var0x) -> {
         return var0x.twinkle;
      }))).apply(var0, SetFireworkExplosionFunction::new);
   });
   public static final FireworkExplosion DEFAULT_VALUE;
   final Optional<FireworkExplosion.Shape> shape;
   final Optional<IntList> colors;
   final Optional<IntList> fadeColors;
   final Optional<Boolean> trail;
   final Optional<Boolean> twinkle;

   public SetFireworkExplosionFunction(List<LootItemCondition> var1, Optional<FireworkExplosion.Shape> var2, Optional<IntList> var3, Optional<IntList> var4, Optional<Boolean> var5, Optional<Boolean> var6) {
      super(var1);
      this.shape = var2;
      this.colors = var3;
      this.fadeColors = var4;
      this.trail = var5;
      this.twinkle = var6;
   }

   protected ItemStack run(ItemStack var1, LootContext var2) {
      var1.update(DataComponents.FIREWORK_EXPLOSION, DEFAULT_VALUE, this::apply);
      return var1;
   }

   private FireworkExplosion apply(FireworkExplosion var1) {
      Optional var10002 = this.shape;
      Objects.requireNonNull(var1);
      FireworkExplosion.Shape var3 = (FireworkExplosion.Shape)var10002.orElseGet(var1::shape);
      Optional var10003 = this.colors;
      Objects.requireNonNull(var1);
      IntList var4 = (IntList)var10003.orElseGet(var1::colors);
      Optional var10004 = this.fadeColors;
      Objects.requireNonNull(var1);
      IntList var5 = (IntList)var10004.orElseGet(var1::fadeColors);
      Optional var10005 = this.trail;
      Objects.requireNonNull(var1);
      boolean var2 = (Boolean)var10005.orElseGet(var1::hasTrail);
      Optional var10006 = this.twinkle;
      Objects.requireNonNull(var1);
      return new FireworkExplosion(var3, var4, var5, var2, (Boolean)var10006.orElseGet(var1::hasTwinkle));
   }

   public LootItemFunctionType<SetFireworkExplosionFunction> getType() {
      return LootItemFunctions.SET_FIREWORK_EXPLOSION;
   }

   static {
      DEFAULT_VALUE = new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(), IntList.of(), false, false);
   }
}
