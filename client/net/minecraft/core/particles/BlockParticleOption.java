package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
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
         return new BlockParticleOption(var1, BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), (StringReader)var2, false).blockState());
      }

      // $FF: synthetic method
      public ParticleOptions fromCommand(ParticleType var1, StringReader var2, HolderLookup.Provider var3) throws CommandSyntaxException {
         return this.fromCommand(var1, var2, var3);
      }
   };
   private final ParticleType<BlockParticleOption> type;
   private final BlockState state;

   public static MapCodec<BlockParticleOption> codec(ParticleType<BlockParticleOption> var0) {
      return BlockState.CODEC.xmap((var1) -> {
         return new BlockParticleOption(var0, var1);
      }, (var0x) -> {
         return var0x.state;
      }).fieldOf("value");
   }

   public static StreamCodec<? super RegistryFriendlyByteBuf, BlockParticleOption> streamCodec(ParticleType<BlockParticleOption> var0) {
      return ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY).map((var1) -> {
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

   public String writeToString(HolderLookup.Provider var1) {
      String var10000 = String.valueOf(BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()));
      return var10000 + " " + BlockStateParser.serialize(this.state);
   }

   public ParticleType<BlockParticleOption> getType() {
      return this.type;
   }

   public BlockState getState() {
      return this.state;
   }
}
