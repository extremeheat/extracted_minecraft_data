package net.minecraft.network.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public interface ComponentContents {
   default <T> Optional<T> visit(final FormattedText.StyledContentConsumer<T> output, final Style currentStyle) {
      return Optional.empty();
   }

   default <T> Optional<T> visit(final FormattedText.ContentConsumer<T> output) {
      return Optional.empty();
   }

   default MutableComponent resolve(final @Nullable CommandSourceStack source, final @Nullable Entity entity, final int recursionDepth) throws CommandSyntaxException {
      return MutableComponent.create(this);
   }

   MapCodec<? extends ComponentContents> codec();
}
