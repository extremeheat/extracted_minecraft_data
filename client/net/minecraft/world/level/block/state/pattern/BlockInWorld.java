package net.minecraft.world.level.block.state.pattern;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class BlockInWorld {
   private final LevelReader level;
   private final BlockPos pos;
   private final boolean loadChunks;
   private @Nullable BlockState state;
   private @Nullable BlockEntity entity;
   private boolean cachedEntity;

   public BlockInWorld(LevelReader var1, BlockPos var2, boolean var3) {
      super();
      this.level = var1;
      this.pos = var2.immutable();
      this.loadChunks = var3;
   }

   public BlockState getState() {
      if (this.state == null && (this.loadChunks || this.level.hasChunkAt(this.pos))) {
         this.state = this.level.getBlockState(this.pos);
      }

      return this.state;
   }

   public @Nullable BlockEntity getEntity() {
      if (this.entity == null && !this.cachedEntity) {
         this.entity = this.level.getBlockEntity(this.pos);
         this.cachedEntity = true;
      }

      return this.entity;
   }

   public LevelReader getLevel() {
      return this.level;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public static Predicate<@Nullable BlockInWorld> hasState(Predicate<BlockState> var0) {
      return (var1) -> var1 != null && var0.test(var1.getState());
   }
}
