package net.minecraft.network.chat.contents;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record StorageDataSource(ResourceLocation id) implements DataSource {
   public static final MapCodec<StorageDataSource> SUB_CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(ResourceLocation.CODEC.fieldOf("storage").forGetter(StorageDataSource::id)).apply(var0, StorageDataSource::new)
   );
   public static final DataSource.Type<StorageDataSource> TYPE = new DataSource.Type<>(SUB_CODEC, "storage");

   public StorageDataSource(ResourceLocation id) {
      super();
      this.id = id;
   }

   @Override
   public Stream<CompoundTag> getData(CommandSourceStack var1) {
      CompoundTag var2 = var1.getServer().getCommandStorage().get(this.id);
      return Stream.of(var2);
   }

   @Override
   public DataSource.Type<?> type() {
      return TYPE;
   }

   @Override
   public String toString() {
      return "storage=" + this.id;
   }
}