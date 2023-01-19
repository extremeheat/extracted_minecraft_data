package net.minecraft.world.level.gameevent;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

public class DynamicGameEventListener<T extends GameEventListener> {
   private T listener;
   @Nullable
   private SectionPos lastSection;

   public DynamicGameEventListener(T var1) {
      super();
      this.listener = var1;
   }

   public void add(ServerLevel var1) {
      this.move(var1);
   }

   public void updateListener(T var1, @Nullable Level var2) {
      GameEventListener var3 = this.listener;
      if (var3 != var1) {
         if (var2 instanceof ServerLevel var4) {
            ifChunkExists((LevelReader)var4, this.lastSection, var1x -> var1x.unregister(var3));
            ifChunkExists((LevelReader)var4, this.lastSection, var1x -> var1x.register(var1));
         }

         this.listener = var1;
      }
   }

   public T getListener() {
      return this.listener;
   }

   public void remove(ServerLevel var1) {
      ifChunkExists(var1, this.lastSection, var1x -> var1x.unregister(this.listener));
   }

   public void move(ServerLevel var1) {
      this.listener.getListenerSource().getPosition(var1).map(SectionPos::of).ifPresent(var2 -> {
         if (this.lastSection == null || !this.lastSection.equals(var2)) {
            ifChunkExists(var1, this.lastSection, var1xx -> var1xx.unregister(this.listener));
            this.lastSection = var2;
            ifChunkExists(var1, this.lastSection, var1xx -> var1xx.register(this.listener));
         }
      });
   }

   private static void ifChunkExists(LevelReader var0, @Nullable SectionPos var1, Consumer<GameEventDispatcher> var2) {
      if (var1 != null) {
         ChunkAccess var3 = var0.getChunk(var1.x(), var1.z(), ChunkStatus.FULL, false);
         if (var3 != null) {
            var2.accept(var3.getEventDispatcher(var1.y()));
         }
      }
   }
}
