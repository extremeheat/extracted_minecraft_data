package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ClientboundUpdateAdvancementsPacket implements Packet<ClientGamePacketListener> {
   private final boolean reset;
   private final List<AdvancementHolder> added;
   private final Set<ResourceLocation> removed;
   private final Map<ResourceLocation, AdvancementProgress> progress;

   public ClientboundUpdateAdvancementsPacket(
      boolean var1, Collection<AdvancementHolder> var2, Set<ResourceLocation> var3, Map<ResourceLocation, AdvancementProgress> var4
   ) {
      super();
      this.reset = var1;
      this.added = List.copyOf(var2);
      this.removed = Set.copyOf(var3);
      this.progress = Map.copyOf(var4);
   }

   public ClientboundUpdateAdvancementsPacket(FriendlyByteBuf var1) {
      super();
      this.reset = var1.readBoolean();
      this.added = var1.readList(AdvancementHolder::read);
      this.removed = var1.readCollection(Sets::newLinkedHashSetWithExpectedSize, FriendlyByteBuf::readResourceLocation);
      this.progress = var1.readMap(FriendlyByteBuf::readResourceLocation, AdvancementProgress::fromNetwork);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeBoolean(this.reset);
      var1.writeCollection(this.added, (var0, var1x) -> var1x.write(var0));
      var1.writeCollection(this.removed, FriendlyByteBuf::writeResourceLocation);
      var1.writeMap(this.progress, FriendlyByteBuf::writeResourceLocation, (var0, var1x) -> var1x.serializeToNetwork(var0));
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateAdvancementsPacket(this);
   }

   public List<AdvancementHolder> getAdded() {
      return this.added;
   }

   public Set<ResourceLocation> getRemoved() {
      return this.removed;
   }

   public Map<ResourceLocation, AdvancementProgress> getProgress() {
      return this.progress;
   }

   public boolean shouldReset() {
      return this.reset;
   }
}
