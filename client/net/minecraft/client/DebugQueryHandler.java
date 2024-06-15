package net.minecraft.client;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQueryPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQueryPacket;

public class DebugQueryHandler {
   private final ClientPacketListener connection;
   private int transactionId = -1;
   @Nullable
   private Consumer<CompoundTag> callback;

   public DebugQueryHandler(ClientPacketListener var1) {
      super();
      this.connection = var1;
   }

   public boolean handleResponse(int var1, @Nullable CompoundTag var2) {
      if (this.transactionId == var1 && this.callback != null) {
         this.callback.accept(var2);
         this.callback = null;
         return true;
      } else {
         return false;
      }
   }

   private int startTransaction(Consumer<CompoundTag> var1) {
      this.callback = var1;
      return ++this.transactionId;
   }

   public void queryEntityTag(int var1, Consumer<CompoundTag> var2) {
      int var3 = this.startTransaction(var2);
      this.connection.send(new ServerboundEntityTagQueryPacket(var3, var1));
   }

   public void queryBlockEntityTag(BlockPos var1, Consumer<CompoundTag> var2) {
      int var3 = this.startTransaction(var2);
      this.connection.send(new ServerboundBlockEntityTagQueryPacket(var3, var1));
   }
}
