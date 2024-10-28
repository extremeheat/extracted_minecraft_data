package net.minecraft.world.level.block;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TorchBlock extends BaseTorchBlock {
   protected static final MapCodec<SimpleParticleType> PARTICLE_OPTIONS_FIELD;
   public static final MapCodec<TorchBlock> CODEC;
   protected final SimpleParticleType flameParticle;

   public MapCodec<? extends TorchBlock> codec() {
      return CODEC;
   }

   protected TorchBlock(SimpleParticleType var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.flameParticle = var1;
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      double var5 = (double)var3.getX() + 0.5;
      double var7 = (double)var3.getY() + 0.7;
      double var9 = (double)var3.getZ() + 0.5;
      var2.addParticle(ParticleTypes.SMOKE, var5, var7, var9, 0.0, 0.0, 0.0);
      var2.addParticle(this.flameParticle, var5, var7, var9, 0.0, 0.0, 0.0);
   }

   static {
      PARTICLE_OPTIONS_FIELD = BuiltInRegistries.PARTICLE_TYPE.byNameCodec().comapFlatMap((var0) -> {
         DataResult var10000;
         if (var0 instanceof SimpleParticleType var1) {
            var10000 = DataResult.success(var1);
         } else {
            var10000 = DataResult.error(() -> {
               return "Not a SimpleParticleType: " + String.valueOf(var0);
            });
         }

         return var10000;
      }, (var0) -> {
         return var0;
      }).fieldOf("particle_options");
      CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(PARTICLE_OPTIONS_FIELD.forGetter((var0x) -> {
            return var0x.flameParticle;
         }), propertiesCodec()).apply(var0, TorchBlock::new);
      });
   }
}
