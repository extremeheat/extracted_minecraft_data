package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

public class Clear implements RuleBlockEntityModifier {
   private static final Clear INSTANCE = new Clear();
   public static final MapCodec<Clear> CODEC;

   public Clear() {
      super();
   }

   public CompoundTag apply(RandomSource var1, @Nullable CompoundTag var2) {
      return new CompoundTag();
   }

   public RuleBlockEntityModifierType<?> getType() {
      return RuleBlockEntityModifierType.CLEAR;
   }

   static {
      CODEC = MapCodec.unit(INSTANCE);
   }
}
