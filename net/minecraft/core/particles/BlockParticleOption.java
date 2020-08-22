package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockParticleOption implements ParticleOptions {
   public static final ParticleOptions.Deserializer DESERIALIZER = new ParticleOptions.Deserializer() {
      public BlockParticleOption fromCommand(ParticleType var1, StringReader var2) throws CommandSyntaxException {
         var2.expect(' ');
         return new BlockParticleOption(var1, (new BlockStateParser(var2, false)).parse(false).getState());
      }

      public BlockParticleOption fromNetwork(ParticleType var1, FriendlyByteBuf var2) {
         return new BlockParticleOption(var1, (BlockState)Block.BLOCK_STATE_REGISTRY.byId(var2.readVarInt()));
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
   private final ParticleType type;
   private final BlockState state;

   public BlockParticleOption(ParticleType var1, BlockState var2) {
      this.type = var1;
      this.state = var2;
   }

   public void writeToNetwork(FriendlyByteBuf var1) {
      var1.writeVarInt(Block.BLOCK_STATE_REGISTRY.getId(this.state));
   }

   public String writeToString() {
      return Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + BlockStateParser.serialize(this.state);
   }

   public ParticleType getType() {
      return this.type;
   }

   public BlockState getState() {
      return this.state;
   }
}
