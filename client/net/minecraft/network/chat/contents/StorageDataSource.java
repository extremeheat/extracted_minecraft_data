package net.minecraft.network.chat.contents;

import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record StorageDataSource(ResourceLocation a) implements DataSource {
   private final ResourceLocation id;

   public StorageDataSource(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public Stream<CompoundTag> getData(CommandSourceStack var1) {
      CompoundTag var2 = var1.getServer().getCommandStorage().get(this.id);
      return Stream.of(var2);
   }

   public String toString() {
      return "storage=" + this.id;
   }

   public ResourceLocation id() {
      return this.id;
   }
}
