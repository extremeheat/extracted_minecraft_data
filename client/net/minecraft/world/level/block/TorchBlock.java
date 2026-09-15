package net.minecraft.world.level.block;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
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
   protected final SimpleParticleType flameParticle;

   protected TorchBlock(final SimpleParticleType flameParticle, final BlockBehaviour.Properties properties) {
      super(properties);
      this.flameParticle = flameParticle;
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      double x = (double)pos.getX() + 0.5;
      double y = (double)pos.getY() + 0.7;
      double z = (double)pos.getZ() + 0.5;
      level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
      level.addParticle(this.flameParticle, x, y, z, 0.0, 0.0, 0.0);
   }

   static {
      PARTICLE_OPTIONS_FIELD = BuiltInRegistries.PARTICLE_TYPE.byNameCodec().comapFlatMap((type) -> {
         DataResult var10000;
         if (type instanceof SimpleParticleType simple) {
            var10000 = DataResult.success(simple);
         } else {
            var10000 = DataResult.error(() -> "Not a SimpleParticleType: " + String.valueOf(type));
         }

         return var10000;
      }, (type) -> type).fieldOf("particle_options");
   }
}
