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
   public static final MapCodec<SetFireworkExplosionFunction> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(FireworkExplosion.Shape.CODEC.optionalFieldOf("shape").forGetter((f) -> f.shape), FireworkExplosion.COLOR_LIST_CODEC.optionalFieldOf("colors").forGetter((f) -> f.colors), FireworkExplosion.COLOR_LIST_CODEC.optionalFieldOf("fade_colors").forGetter((f) -> f.fadeColors), Codec.BOOL.optionalFieldOf("trail").forGetter((f) -> f.trail), Codec.BOOL.optionalFieldOf("twinkle").forGetter((f) -> f.twinkle))).apply(i, SetFireworkExplosionFunction::new));
   public static final FireworkExplosion DEFAULT_VALUE;
   final Optional<FireworkExplosion.Shape> shape;
   final Optional<IntList> colors;
   final Optional<IntList> fadeColors;
   final Optional<Boolean> trail;
   final Optional<Boolean> twinkle;

   public SetFireworkExplosionFunction(final List<LootItemCondition> predicates, final Optional<FireworkExplosion.Shape> shape, final Optional<IntList> colors, final Optional<IntList> fadeColors, final Optional<Boolean> hasTrail, final Optional<Boolean> hasTwinkle) {
      super(predicates);
      this.shape = shape;
      this.colors = colors;
      this.fadeColors = fadeColors;
      this.trail = hasTrail;
      this.twinkle = hasTwinkle;
   }

   protected ItemStack run(final ItemStack itemStack, final LootContext context) {
      itemStack.update(DataComponents.FIREWORK_EXPLOSION, DEFAULT_VALUE, this::apply);
      return itemStack;
   }

   private FireworkExplosion apply(final FireworkExplosion original) {
      Optional var10002 = this.shape;
      Objects.requireNonNull(original);
      FireworkExplosion.Shape var2 = (FireworkExplosion.Shape)var10002.orElseGet(original::shape);
      Optional var10003 = this.colors;
      Objects.requireNonNull(original);
      IntList var3 = (IntList)var10003.orElseGet(original::colors);
      Optional var10004 = this.fadeColors;
      Objects.requireNonNull(original);
      IntList var4 = (IntList)var10004.orElseGet(original::fadeColors);
      Optional var10005 = this.trail;
      Objects.requireNonNull(original);
      boolean var5 = (Boolean)var10005.orElseGet(original::hasTrail);
      Optional var10006 = this.twinkle;
      Objects.requireNonNull(original);
      return new FireworkExplosion(var2, var3, var4, var5, (Boolean)var10006.orElseGet(original::hasTwinkle));
   }

   public MapCodec<SetFireworkExplosionFunction> codec() {
      return MAP_CODEC;
   }

   static {
      DEFAULT_VALUE = new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(), IntList.of(), false, false);
   }
}
