package net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

public class AppendStatic implements RuleBlockEntityModifier {
   public static final Codec<AppendStatic> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(CompoundTag.CODEC.fieldOf("data").forGetter(var0x -> var0x.tag)).apply(var0, AppendStatic::new)
   );
   private final CompoundTag tag;

   public AppendStatic(CompoundTag var1) {
      super();
      this.tag = var1;
   }

   @Override
   public CompoundTag apply(RandomSource var1, @Nullable CompoundTag var2) {
      return var2 == null ? this.tag.copy() : var2.merge(this.tag);
   }

   @Override
   public RuleBlockEntityModifierType<?> getType() {
      return RuleBlockEntityModifierType.APPEND_STATIC;
   }
}
