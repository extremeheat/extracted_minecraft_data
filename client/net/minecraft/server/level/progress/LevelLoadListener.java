package net.minecraft.server.level.progress;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public interface LevelLoadListener {
   static LevelLoadListener compose(final LevelLoadListener var0, final LevelLoadListener var1) {
      return new LevelLoadListener() {
         public void start(Stage var1x, int var2) {
            var0.start(var1x, var2);
            var1.start(var1x, var2);
         }

         public void update(Stage var1x, int var2, int var3) {
            var0.update(var1x, var2, var3);
            var1.update(var1x, var2, var3);
         }

         public void finish(Stage var1x) {
            var0.finish(var1x);
            var1.finish(var1x);
         }

         public void updateFocus(ResourceKey<Level> var1x, ChunkPos var2) {
            var0.updateFocus(var1x, var2);
            var1.updateFocus(var1x, var2);
         }
      };
   }

   void start(Stage var1, int var2);

   void update(Stage var1, int var2, int var3);

   void finish(Stage var1);

   void updateFocus(ResourceKey<Level> var1, ChunkPos var2);

   public static enum Stage {
      START_SERVER,
      PREPARE_GLOBAL_SPAWN,
      LOAD_INITIAL_CHUNKS,
      LOAD_PLAYER_CHUNKS;

      private Stage() {
      }

      // $FF: synthetic method
      private static Stage[] $values() {
         return new Stage[]{START_SERVER, PREPARE_GLOBAL_SPAWN, LOAD_INITIAL_CHUNKS, LOAD_PLAYER_CHUNKS};
      }
   }
}
