package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockParticleOption implements ParticleOptions {
   public static final ParticleOptions.Deserializer<BlockParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<BlockParticleOption>() {
      public BlockParticleOption fromCommand(ParticleType<BlockParticleOption> var1, StringReader var2) throws CommandSyntaxException {
         var2.expect(' ');
         return new BlockParticleOption(var1, (new BlockStateParser(var2, false)).parse(false).getState());
      }

      public BlockParticleOption fromNetwork(ParticleType<BlockParticleOption> var1, FriendlyByteBuf var2) {
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
   private final ParticleType<BlockParticleOption> type;
   private final BlockState state;

   public static Codec<BlockParticleOption> codec(ParticleType<BlockParticleOption> var0) {
      return BlockState.CODEC.xmap((var1) -> {
         return new BlockParticleOption(var0, var1);
      }, (var0x) -> {
         return var0x.state;
      });
   }

   public BlockParticleOption(ParticleType<BlockParticleOption> var1, BlockState var2) {
      super();
      this.type = var1;
      this.state = var2;
   }

   public void writeToNetwork(FriendlyByteBuf var1) {
      var1.writeVarInt(Block.BLOCK_STATE_REGISTRY.getId(this.state));
   }

   public String writeToString() {
      ResourceLocation var10000 = Registry.PARTICLE_TYPE.getKey(this.getType());
      return var10000 + " " + BlockStateParser.serialize(this.state);
   }

   public ParticleType<BlockParticleOption> getType() {
      return this.type;
   }

   public BlockState getState() {
      return this.state;
   }
}
