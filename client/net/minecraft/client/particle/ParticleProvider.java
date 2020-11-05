package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;

public interface ParticleProvider<T extends ParticleOptions> {
   @Nullable
   Particle createParticle(T var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13);
}
