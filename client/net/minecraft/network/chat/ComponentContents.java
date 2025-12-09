package net.minecraft.network.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

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

   MapCodec<? extends ComponentContents> codec();
}
