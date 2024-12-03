package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerBlock extends BushBlock implements SuspiciousEffectHolder {
   protected static final MapCodec<SuspiciousStewEffects> EFFECTS_FIELD;
   public static final MapCodec<FlowerBlock> CODEC;
   protected static final float AABB_OFFSET = 3.0F;
   protected static final VoxelShape SHAPE;
   private final SuspiciousStewEffects suspiciousStewEffects;

   public MapCodec<? extends FlowerBlock> codec() {
      return CODEC;
   }

   public FlowerBlock(Holder<MobEffect> var1, float var2, BlockBehaviour.Properties var3) {
      this(makeEffectList(var1, var2), var3);
   }

   public FlowerBlock(SuspiciousStewEffects var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.suspiciousStewEffects = var1;
   }

   protected static SuspiciousStewEffects makeEffectList(Holder<MobEffect> var0, float var1) {
      return new SuspiciousStewEffects(List.of(new SuspiciousStewEffects.Entry(var0, Mth.floor(var1 * 20.0F))));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Vec3 var5 = var1.getOffset(var3);
      return SHAPE.move(var5.x, var5.y, var5.z);
   }

   public SuspiciousStewEffects getSuspiciousEffects() {
      return this.suspiciousStewEffects;
   }

   @Nullable
   public MobEffectInstance getBeeInteractionEffect() {
      return null;
   }

   static {
      EFFECTS_FIELD = SuspiciousStewEffects.CODEC.fieldOf("suspicious_stew_effects");
      CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(EFFECTS_FIELD.forGetter(FlowerBlock::getSuspiciousEffects), propertiesCodec()).apply(var0, FlowerBlock::new));
      SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
   }
}
