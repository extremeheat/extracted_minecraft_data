package net.minecraft.network.chat.contents.data;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.CompilableString;
import net.minecraft.world.level.block.entity.BlockEntity;

public record BlockDataSource(CompilableString<Coordinates> coordinates) implements DataSource {
   public static final Codec<CompilableString<Coordinates>> BLOCK_POS_CODEC = CompilableString.codec(new CompilableString.CommandParserHelper<Coordinates>() {
      protected Coordinates parse(final StringReader reader) throws CommandSyntaxException {
         return BlockPosArgument.blockPos().parse(reader);
      }

      protected String errorMessage(final String original, final CommandSyntaxException exception) {
         return "Invalid coordinates path: " + original + ": " + exception.getMessage();
      }
   });
   public static final MapCodec<BlockDataSource> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BLOCK_POS_CODEC.fieldOf("block").forGetter(BlockDataSource::coordinates)).apply(i, BlockDataSource::new));

   public BlockDataSource {
      super();
   }

   public Stream<CompoundTag> getData(final CommandSourceStack sender) {
      ServerLevel level = sender.getLevel();
      BlockPos pos = ((Coordinates)this.coordinates.compiled()).getBlockPos(sender);
      if (level.isLoaded(pos)) {
         BlockEntity entity = level.getBlockEntity(pos);
         if (entity != null) {
            return Stream.of(entity.saveWithFullMetadata((HolderLookup.Provider)sender.registryAccess()));
         }
      }

      return Stream.empty();
   }

   public MapCodec<BlockDataSource> codec() {
      return MAP_CODEC;
   }
}
