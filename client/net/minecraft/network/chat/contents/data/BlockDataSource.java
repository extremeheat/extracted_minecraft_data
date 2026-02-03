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
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public record BlockDataSource(String posPattern, @Nullable Coordinates compiledPos) implements DataSource {
   public static final MapCodec<BlockDataSource> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("block").forGetter(BlockDataSource::posPattern)).apply(i, BlockDataSource::new));

   public BlockDataSource(final String pos) {
      this(pos, compilePos(pos));
   }

   public BlockDataSource {
      super();
   }

   private static @Nullable Coordinates compilePos(final String pos) {
      try {
         return BlockPosArgument.blockPos().parse(new StringReader(pos));
      } catch (CommandSyntaxException var2) {
         return null;
      }
   }

   public Stream<CompoundTag> getData(final CommandSourceStack sender) {
      if (this.compiledPos != null) {
         ServerLevel level = sender.getLevel();
         BlockPos pos = this.compiledPos.getBlockPos(sender);
         if (level.isLoaded(pos)) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity != null) {
               return Stream.of(entity.saveWithFullMetadata((HolderLookup.Provider)sender.registryAccess()));
            }
         }
      }

      return Stream.empty();
   }

   public MapCodec<BlockDataSource> codec() {
      return MAP_CODEC;
   }

   public String toString() {
      return "block=" + this.posPattern;
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof BlockDataSource) {
            BlockDataSource that = (BlockDataSource)o;
            if (this.posPattern.equals(that.posPattern)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.posPattern.hashCode();
   }
}
