package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

public class Passthrough implements RuleBlockEntityModifier {
   public static final Passthrough INSTANCE = new Passthrough();
   public static final MapCodec<Passthrough> CODEC = MapCodec.unit(INSTANCE);

   public Passthrough() {
      super();
   }

   @Nullable
   @Override
   public CompoundTag apply(RandomSource var1, @Nullable CompoundTag var2) {
      return var2;
   }

   @Override
   public RuleBlockEntityModifierType<?> getType() {
      return RuleBlockEntityModifierType.PASSTHROUGH;
   }
}
