package net.minecraft.world.level.gameevent;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

public class GameEventListenerRegistrar {
   private final GameEventListener listener;
   @Nullable
   private SectionPos sectionPos;

   public GameEventListenerRegistrar(GameEventListener var1) {
      super();
      this.listener = var1;
   }

   public void onListenerRemoved(Level var1) {
      this.ifEventDispatcherExists(var1, this.sectionPos, (var1x) -> {
         var1x.unregister(this.listener);
      });
   }

   public void onListenerMove(Level var1) {
      Optional var2 = this.listener.getListenerSource().getPosition(var1);
      if (var2.isPresent()) {
         long var3 = SectionPos.blockToSection(((BlockPos)var2.get()).asLong());
         if (this.sectionPos == null || this.sectionPos.asLong() != var3) {
            SectionPos var5 = this.sectionPos;
            this.sectionPos = SectionPos.method_74(var3);
            this.ifEventDispatcherExists(var1, var5, (var1x) -> {
               var1x.unregister(this.listener);
            });
            this.ifEventDispatcherExists(var1, this.sectionPos, (var1x) -> {
               var1x.register(this.listener);
            });
         }
      }

   }

   private void ifEventDispatcherExists(Level var1, @Nullable SectionPos var2, Consumer<GameEventDispatcher> var3) {
      if (var2 != null) {
         ChunkAccess var4 = var1.getChunk(var2.method_78(), var2.method_80(), ChunkStatus.FULL, false);
         if (var4 != null) {
            var3.accept(var4.getEventDispatcher(var2.method_79()));
         }

      }
   }
}
