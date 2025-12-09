package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class Passthrough implements RuleBlockEntityModifier {
   public static final Passthrough INSTANCE = new Passthrough();
   public static final MapCodec<Passthrough> CODEC;

   public Passthrough() {
      super();
   }

   public @Nullable CompoundTag apply(RandomSource var1, @Nullable CompoundTag var2) {
      return var2;
   }

   public RuleBlockEntityModifierType<?> getType() {
      return RuleBlockEntityModifierType.PASSTHROUGH;
   }

   static {
      CODEC = MapCodec.unit(INSTANCE);
   }
}
