package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

public class AppendStatic implements RuleBlockEntityModifier {
   public static final MapCodec<AppendStatic> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(CompoundTag.CODEC.fieldOf("data").forGetter((var0x) -> {
         return var0x.tag;
      })).apply(var0, AppendStatic::new);
   });
   private final CompoundTag tag;

   public AppendStatic(CompoundTag var1) {
      super();
      this.tag = var1;
   }

   public CompoundTag apply(RandomSource var1, @Nullable CompoundTag var2) {
      return var2 == null ? this.tag.copy() : var2.merge(this.tag);
   }

   public RuleBlockEntityModifierType<?> getType() {
      return RuleBlockEntityModifierType.APPEND_STATIC;
   }
}
