package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

public record BlockDataSource(String posPattern, @Nullable Coordinates compiledPos) implements DataSource {
   public static final MapCodec<BlockDataSource> SUB_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("block").forGetter(BlockDataSource::posPattern)).apply(var0, BlockDataSource::new));
   public static final DataSource.Type<BlockDataSource> TYPE;

   public BlockDataSource(String var1) {
      this(var1, compilePos(var1));
   }

   public BlockDataSource(String var1, @Nullable Coordinates var2) {
      super();
      this.posPattern = var1;
      this.compiledPos = var2;
   }

   @Nullable
   private static Coordinates compilePos(String var0) {
      try {
         return BlockPosArgument.blockPos().parse(new StringReader(var0));
      } catch (CommandSyntaxException var2) {
         return null;
      }
   }

   public Stream<CompoundTag> getData(CommandSourceStack var1) {
      if (this.compiledPos != null) {
         ServerLevel var2 = var1.getLevel();
         BlockPos var3 = this.compiledPos.getBlockPos(var1);
         if (var2.isLoaded(var3)) {
            BlockEntity var4 = var2.getBlockEntity(var3);
            if (var4 != null) {
               return Stream.of(var4.saveWithFullMetadata(var1.registryAccess()));
            }
         }
      }

      return Stream.empty();
   }

   public DataSource.Type<?> type() {
      return TYPE;
   }

   public String toString() {
      return "block=" + this.posPattern;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof BlockDataSource) {
            BlockDataSource var2 = (BlockDataSource)var1;
            if (this.posPattern.equals(var2.posPattern)) {
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

   static {
      TYPE = new DataSource.Type<BlockDataSource>(SUB_CODEC, "block");
   }
}
