package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jspecify.annotations.Nullable;

public record StoredNumberAccess(Identifier storage, NbtPathArgument.NbtPath path) {
   public static final MapCodec<StoredNumberAccess> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("storage").forGetter(StoredNumberAccess::storage), NbtPathArgument.NbtPath.CODEC.fieldOf("path").forGetter(StoredNumberAccess::path)).apply(i, StoredNumberAccess::new));

   public StoredNumberAccess {
      super();
   }

   public @Nullable Number getNumericTag(final LootContext context) {
      CompoundTag value = context.getLevel().getServer().getCommandStorage().get(this.storage);

      try {
         List<Tag> selectedTags = this.path.get(value);
         if (selectedTags.size() == 1) {
            Object var5 = selectedTags.getFirst();
            if (var5 instanceof NumericTag) {
               NumericTag result = (NumericTag)var5;
               return result.box();
            }
         }
      } catch (CommandSyntaxException var6) {
      }

      return null;
   }
}
