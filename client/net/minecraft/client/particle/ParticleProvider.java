package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;

public interface ParticleProvider<T extends ParticleOptions> {
   @Nullable
   Particle createParticle(T var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13);
}
