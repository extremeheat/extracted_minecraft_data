package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerBlock extends BushBlock implements SuspiciousEffectHolder {
   protected static final MapCodec<List<SuspiciousEffectHolder.EffectEntry>> EFFECTS_FIELD = SuspiciousEffectHolder.EffectEntry.LIST_CODEC
      .fieldOf("suspicious_stew_effects");
   public static final MapCodec<FlowerBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(EFFECTS_FIELD.forGetter(FlowerBlock::getSuspiciousEffects), propertiesCodec()).apply(var0, FlowerBlock::new)
   );
   protected static final float AABB_OFFSET = 3.0F;
   protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
   private final List<SuspiciousEffectHolder.EffectEntry> suspiciousStewEffects;

   @Override
   public MapCodec<? extends FlowerBlock> codec() {
      return CODEC;
   }

   public FlowerBlock(MobEffect var1, int var2, BlockBehaviour.Properties var3) {
      this(makeEffectList(var1, var2), var3);
   }

   public FlowerBlock(List<SuspiciousEffectHolder.EffectEntry> var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.suspiciousStewEffects = var1;
   }

   protected static List<SuspiciousEffectHolder.EffectEntry> makeEffectList(MobEffect var0, int var1) {
      int var2;
      if (var0.isInstantenous()) {
         var2 = var1;
      } else {
         var2 = var1 * 20;
      }

      return List.of(new SuspiciousEffectHolder.EffectEntry(var0, var2));
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Vec3 var5 = var1.getOffset(var2, var3);
      return SHAPE.move(var5.x, var5.y, var5.z);
   }

   @Override
   public List<SuspiciousEffectHolder.EffectEntry> getSuspiciousEffects() {
      return this.suspiciousStewEffects;
   }
}
