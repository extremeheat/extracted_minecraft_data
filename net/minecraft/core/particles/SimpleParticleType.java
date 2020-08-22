package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;

public class SimpleParticleType extends ParticleType implements ParticleOptions {
   private static final ParticleOptions.Deserializer DESERIALIZER = new ParticleOptions.Deserializer() {
      public SimpleParticleType fromCommand(ParticleType var1, StringReader var2) throws CommandSyntaxException {
         return (SimpleParticleType)var1;
      }

      public SimpleParticleType fromNetwork(ParticleType var1, FriendlyByteBuf var2) {
         return (SimpleParticleType)var1;
      }

      // $FF: synthetic method
      public ParticleOptions fromNetwork(ParticleType var1, FriendlyByteBuf var2) {
         return this.fromNetwork(var1, var2);
      }

      // $FF: synthetic method
      public ParticleOptions fromCommand(ParticleType var1, StringReader var2) throws CommandSyntaxException {
         return this.fromCommand(var1, var2);
      }
   };

   protected SimpleParticleType(boolean var1) {
      super(var1, DESERIALIZER);
   }

   public ParticleType getType() {
      return this;
   }

   public void writeToNetwork(FriendlyByteBuf var1) {
   }

   public String writeToString() {
      return Registry.PARTICLE_TYPE.getKey(this).toString();
   }
}
