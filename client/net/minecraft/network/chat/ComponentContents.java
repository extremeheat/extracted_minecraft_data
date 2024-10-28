package net.minecraft.network.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;

public interface ComponentContents {
   default <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> var1, Style var2) {
      return Optional.empty();
   }

   default <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
      return Optional.empty();
   }

   default MutableComponent resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      return MutableComponent.create(this);
   }

   Type<?> type();

   public static record Type<T extends ComponentContents>(MapCodec<T> codec, String id) implements StringRepresentable {
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
