package net.minecraft.data.tags;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.references.BlockItemId;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public abstract class BlockItemTagsProvider {
   private final Function<BlockItemTagId, CombinedAppender> tagSupplier;

   protected BlockItemTagsProvider(final Function<BlockItemTagId, CombinedAppender> tagSupplier) {
      super();
      this.tagSupplier = tagSupplier;
   }

   protected CombinedAppender tag(final BlockItemTagId tag) {
      return (CombinedAppender)this.tagSupplier.apply(tag);
   }

   protected abstract void run();

   public static CombinedAppender wrapForBlocks(final TagAppender<Block> appender) {
      return new CombinedAppender() {
         public CombinedAppender addAll(final Stream<BlockItemId> ids) {
            appender.addAll(ids.map(BlockItemId::block));
            return this;
         }

         public CombinedAppender addTag(final BlockItemTagId id) {
            appender.addTag(id.block());
            return this;
         }
      };
   }

   public static CombinedAppender wrapForItems(final TagAppender<Item> appender) {
      return new CombinedAppender() {
         public CombinedAppender addAll(final Stream<BlockItemId> ids) {
            appender.addAll(ids.map(BlockItemId::item));
            return this;
         }

         public CombinedAppender addTag(final BlockItemTagId id) {
            appender.addTag(id.item());
            return this;
         }
      };
   }

   public interface CombinedAppender {
      CombinedAppender addAll(Stream<BlockItemId> ids);

      CombinedAppender addTag(BlockItemTagId id);

      default CombinedAppender addTag(final BlockItemTagId... ids) {
         for(BlockItemTagId id : ids) {
            this.addTag(id);
         }

         return this;
      }

      default CombinedAppender add(final BlockItemId... ids) {
         this.addAll(Arrays.stream(ids));
         return this;
      }

      default CombinedAppender addAll(final Collection<BlockItemId> ids) {
         this.addAll(ids.stream());
         return this;
      }
   }
}
