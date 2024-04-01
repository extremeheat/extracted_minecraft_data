package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockParticleOption implements ParticleOptions {
   public static final ParticleOptions.Deserializer<BlockParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<BlockParticleOption>() {
      public BlockParticleOption fromCommand(ParticleType<BlockParticleOption> var1, StringReader var2, HolderLookup.Provider var3) throws CommandSyntaxException {
         var2.expect(' ');
         return new BlockParticleOption(var1, BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), var2, false).blockState());
      }
   };
   private final ParticleType<BlockParticleOption> type;
   private final BlockState state;

   public static Codec<BlockParticleOption> codec(ParticleType<BlockParticleOption> var0) {
      return BlockState.CODEC.xmap(var1 -> new BlockParticleOption(var0, var1), var0x -> var0x.state);
   }

   public static StreamCodec<? super RegistryFriendlyByteBuf, BlockParticleOption> streamCodec(ParticleType<BlockParticleOption> var0) {
      return ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY).map(var1 -> new BlockParticleOption(var0, var1), var0x -> var0x.state);
   }

   public BlockParticleOption(ParticleType<BlockParticleOption> var1, BlockState var2) {
      super();
      this.type = var1;
      this.state = var2;
   }

   @Override
   public String writeToString(HolderLookup.Provider var1) {
      return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()) + " " + BlockStateParser.serialize(this.state);
   }

   @Override
   public ParticleType<BlockParticleOption> getType() {
      return this.type;
   }

   public BlockState getState() {
      return this.state;
   }
}
