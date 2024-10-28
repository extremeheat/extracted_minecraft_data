package net.minecraft.network.chat.contents;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.StringRepresentable;

public interface DataSource {
   MapCodec<DataSource> CODEC = ComponentSerialization.createLegacyComponentMatcher(new Type[]{EntityDataSource.TYPE, BlockDataSource.TYPE, StorageDataSource.TYPE}, Type::codec, DataSource::type, "source");

   Stream<CompoundTag> getData(CommandSourceStack var1) throws CommandSyntaxException;

   Type<?> type();

   public static record Type<T extends DataSource>(MapCodec<T> codec, String id) implements StringRepresentable {
      public Type(MapCodec<T> var1, String var2) {
         super();
         this.codec = var1;
         this.id = var2;
      }

      public String getSerializedName() {
         return this.id;
      }

      public MapCodec<T> codec() {
         return this.codec;
      }

      public String id() {
         return this.id;
      }
   }
}
