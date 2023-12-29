package net.minecraft.network.protocol.game;

import java.util.UUID;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.BossEvent;

public class ClientboundBossEventPacket implements Packet<ClientGamePacketListener> {
   private static final int FLAG_DARKEN = 1;
   private static final int FLAG_MUSIC = 2;
   private static final int FLAG_FOG = 4;
   private final UUID id;
   private final ClientboundBossEventPacket.Operation operation;
   static final ClientboundBossEventPacket.Operation REMOVE_OPERATION = new ClientboundBossEventPacket.Operation() {
      @Override
      public ClientboundBossEventPacket.OperationType getType() {
         return ClientboundBossEventPacket.OperationType.REMOVE;
      }

      @Override
      public void dispatch(UUID var1, ClientboundBossEventPacket.Handler var2) {
         var2.remove(var1);
      }

      @Override
      public void write(FriendlyByteBuf var1) {
      }
   };

   private ClientboundBossEventPacket(UUID var1, ClientboundBossEventPacket.Operation var2) {
      super();
      this.id = var1;
      this.operation = var2;
   }

   public ClientboundBossEventPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readUUID();
      ClientboundBossEventPacket.OperationType var2 = var1.readEnum(ClientboundBossEventPacket.OperationType.class);
      this.operation = var2.reader.apply(var1);
   }

   public static ClientboundBossEventPacket createAddPacket(BossEvent var0) {
      return new ClientboundBossEventPacket(var0.getId(), new ClientboundBossEventPacket.AddOperation(var0));
   }

   public static ClientboundBossEventPacket createRemovePacket(UUID var0) {
      return new ClientboundBossEventPacket(var0, REMOVE_OPERATION);
   }

   public static ClientboundBossEventPacket createUpdateProgressPacket(BossEvent var0) {
      return new ClientboundBossEventPacket(var0.getId(), new ClientboundBossEventPacket.UpdateProgressOperation(var0.getProgress()));
   }

   public static ClientboundBossEventPacket createUpdateNamePacket(BossEvent var0) {
      return new ClientboundBossEventPacket(var0.getId(), new ClientboundBossEventPacket.UpdateNameOperation(var0.getName()));
   }

   public static ClientboundBossEventPacket createUpdateStylePacket(BossEvent var0) {
      return new ClientboundBossEventPacket(var0.getId(), new ClientboundBossEventPacket.UpdateStyleOperation(var0.getColor(), var0.getOverlay()));
   }

   public static ClientboundBossEventPacket createUpdatePropertiesPacket(BossEvent var0) {
      return new ClientboundBossEventPacket(
         var0.getId(),
         new ClientboundBossEventPacket.UpdatePropertiesOperation(var0.shouldDarkenScreen(), var0.shouldPlayBossMusic(), var0.shouldCreateWorldFog())
      );
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUUID(this.id);
      var1.writeEnum(this.operation.getType());
      this.operation.write(var1);
   }

   static int encodeProperties(boolean var0, boolean var1, boolean var2) {
      int var3 = 0;
      if (var0) {
         var3 |= 1;
      }

      if (var1) {
         var3 |= 2;
      }

      if (var2) {
         var3 |= 4;
      }

      return var3;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBossUpdate(this);
   }

   public void dispatch(ClientboundBossEventPacket.Handler var1) {
      this.operation.dispatch(this.id, var1);
   }

   static class AddOperation implements ClientboundBossEventPacket.Operation {
      private final Component name;
      private final float progress;
      private final BossEvent.BossBarColor color;
      private final BossEvent.BossBarOverlay overlay;
      private final boolean darkenScreen;
      private final boolean playMusic;
      private final boolean createWorldFog;

      AddOperation(BossEvent var1) {
         super();
         this.name = var1.getName();
         this.progress = var1.getProgress();
         this.color = var1.getColor();
         this.overlay = var1.getOverlay();
         this.darkenScreen = var1.shouldDarkenScreen();
         this.playMusic = var1.shouldPlayBossMusic();
         this.createWorldFog = var1.shouldCreateWorldFog();
      }

      private AddOperation(FriendlyByteBuf var1) {
         super();
         this.name = var1.readComponentTrusted();
         this.progress = var1.readFloat();
         this.color = var1.readEnum(BossEvent.BossBarColor.class);
         this.overlay = var1.readEnum(BossEvent.BossBarOverlay.class);
         short var2 = var1.readUnsignedByte();
         this.darkenScreen = (var2 & 1) > 0;
         this.playMusic = (var2 & 2) > 0;
         this.createWorldFog = (var2 & 4) > 0;
      }

      @Override
      public ClientboundBossEventPacket.OperationType getType() {
         return ClientboundBossEventPacket.OperationType.ADD;
      }

      @Override
      public void dispatch(UUID var1, ClientboundBossEventPacket.Handler var2) {
         var2.add(var1, this.name, this.progress, this.color, this.overlay, this.darkenScreen, this.playMusic, this.createWorldFog);
      }

      @Override
      public void write(FriendlyByteBuf var1) {
         var1.writeComponent(this.name);
         var1.writeFloat(this.progress);
         var1.writeEnum(this.color);
         var1.writeEnum(this.overlay);
         var1.writeByte(ClientboundBossEventPacket.encodeProperties(this.darkenScreen, this.playMusic, this.createWorldFog));
      }
   }

   public interface Handler {
      default void add(
         UUID var1, Component var2, float var3, BossEvent.BossBarColor var4, BossEvent.BossBarOverlay var5, boolean var6, boolean var7, boolean var8
      ) {
      }

      default void remove(UUID var1) {
      }

      default void updateProgress(UUID var1, float var2) {
      }

      default void updateName(UUID var1, Component var2) {
      }

      default void updateStyle(UUID var1, BossEvent.BossBarColor var2, BossEvent.BossBarOverlay var3) {
      }

      default void updateProperties(UUID var1, boolean var2, boolean var3, boolean var4) {
      }
   }

   interface Operation {
      ClientboundBossEventPacket.OperationType getType();

      void dispatch(UUID var1, ClientboundBossEventPacket.Handler var2);

      void write(FriendlyByteBuf var1);
   }

   static enum OperationType {
      ADD(ClientboundBossEventPacket.AddOperation::new),
      REMOVE(var0 -> ClientboundBossEventPacket.REMOVE_OPERATION),
      UPDATE_PROGRESS(ClientboundBossEventPacket.UpdateProgressOperation::new),
      UPDATE_NAME(ClientboundBossEventPacket.UpdateNameOperation::new),
      UPDATE_STYLE(ClientboundBossEventPacket.UpdateStyleOperation::new),
      UPDATE_PROPERTIES(ClientboundBossEventPacket.UpdatePropertiesOperation::new);

      final Function<FriendlyByteBuf, ClientboundBossEventPacket.Operation> reader;

      private OperationType(Function<FriendlyByteBuf, ClientboundBossEventPacket.Operation> var3) {
         this.reader = var3;
      }
   }

   static class UpdateNameOperation implements ClientboundBossEventPacket.Operation {
      private final Component name;

      UpdateNameOperation(Component var1) {
         super();
         this.name = var1;
      }

      private UpdateNameOperation(FriendlyByteBuf var1) {
         super();
         this.name = var1.readComponentTrusted();
      }

      @Override
      public ClientboundBossEventPacket.OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_NAME;
      }

      @Override
      public void dispatch(UUID var1, ClientboundBossEventPacket.Handler var2) {
         var2.updateName(var1, this.name);
      }

      @Override
      public void write(FriendlyByteBuf var1) {
         var1.writeComponent(this.name);
      }
   }

   static class UpdateProgressOperation implements ClientboundBossEventPacket.Operation {
      private final float progress;

      UpdateProgressOperation(float var1) {
         super();
         this.progress = var1;
      }

      private UpdateProgressOperation(FriendlyByteBuf var1) {
         super();
         this.progress = var1.readFloat();
      }

      @Override
      public ClientboundBossEventPacket.OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_PROGRESS;
      }

      @Override
      public void dispatch(UUID var1, ClientboundBossEventPacket.Handler var2) {
         var2.updateProgress(var1, this.progress);
      }

      @Override
      public void write(FriendlyByteBuf var1) {
         var1.writeFloat(this.progress);
      }
   }

   static class UpdatePropertiesOperation implements ClientboundBossEventPacket.Operation {
      private final boolean darkenScreen;
      private final boolean playMusic;
      private final boolean createWorldFog;

      UpdatePropertiesOperation(boolean var1, boolean var2, boolean var3) {
         super();
         this.darkenScreen = var1;
         this.playMusic = var2;
         this.createWorldFog = var3;
      }

      private UpdatePropertiesOperation(FriendlyByteBuf var1) {
         super();
         short var2 = var1.readUnsignedByte();
         this.darkenScreen = (var2 & 1) > 0;
         this.playMusic = (var2 & 2) > 0;
         this.createWorldFog = (var2 & 4) > 0;
      }

      @Override
      public ClientboundBossEventPacket.OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_PROPERTIES;
      }

      @Override
      public void dispatch(UUID var1, ClientboundBossEventPacket.Handler var2) {
         var2.updateProperties(var1, this.darkenScreen, this.playMusic, this.createWorldFog);
      }

      @Override
      public void write(FriendlyByteBuf var1) {
         var1.writeByte(ClientboundBossEventPacket.encodeProperties(this.darkenScreen, this.playMusic, this.createWorldFog));
      }
   }

   static class UpdateStyleOperation implements ClientboundBossEventPacket.Operation {
      private final BossEvent.BossBarColor color;
      private final BossEvent.BossBarOverlay overlay;

      UpdateStyleOperation(BossEvent.BossBarColor var1, BossEvent.BossBarOverlay var2) {
         super();
         this.color = var1;
         this.overlay = var2;
      }

      private UpdateStyleOperation(FriendlyByteBuf var1) {
         super();
         this.color = var1.readEnum(BossEvent.BossBarColor.class);
         this.overlay = var1.readEnum(BossEvent.BossBarOverlay.class);
      }

      @Override
      public ClientboundBossEventPacket.OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_STYLE;
      }

      @Override
      public void dispatch(UUID var1, ClientboundBossEventPacket.Handler var2) {
         var2.updateStyle(var1, this.color, this.overlay);
      }

      @Override
      public void write(FriendlyByteBuf var1) {
         var1.writeEnum(this.color);
         var1.writeEnum(this.overlay);
      }
   }
}
