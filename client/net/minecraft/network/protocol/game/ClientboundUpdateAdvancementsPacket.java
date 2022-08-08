package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ClientboundUpdateAdvancementsPacket implements Packet<ClientGamePacketListener> {
   private final boolean reset;
   private final Map<ResourceLocation, Advancement.Builder> added;
   private final Set<ResourceLocation> removed;
   private final Map<ResourceLocation, AdvancementProgress> progress;

   public ClientboundUpdateAdvancementsPacket(boolean var1, Collection<Advancement> var2, Set<ResourceLocation> var3, Map<ResourceLocation, AdvancementProgress> var4) {
      super();
      this.reset = var1;
      ImmutableMap.Builder var5 = ImmutableMap.builder();
      Iterator var6 = var2.iterator();

      while(var6.hasNext()) {
         Advancement var7 = (Advancement)var6.next();
         var5.put(var7.getId(), var7.deconstruct());
      }

      this.added = var5.build();
      this.removed = ImmutableSet.copyOf(var3);
      this.progress = ImmutableMap.copyOf(var4);
   }

   public ClientboundUpdateAdvancementsPacket(FriendlyByteBuf var1) {
      super();
      this.reset = var1.readBoolean();
      this.added = var1.readMap(FriendlyByteBuf::readResourceLocation, Advancement.Builder::fromNetwork);
      this.removed = (Set)var1.readCollection(Sets::newLinkedHashSetWithExpectedSize, FriendlyByteBuf::readResourceLocation);
      this.progress = var1.readMap(FriendlyByteBuf::readResourceLocation, AdvancementProgress::fromNetwork);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeBoolean(this.reset);
      var1.writeMap(this.added, FriendlyByteBuf::writeResourceLocation, (var0, var1x) -> {
         var1x.serializeToNetwork(var0);
      });
      var1.writeCollection(this.removed, FriendlyByteBuf::writeResourceLocation);
      var1.writeMap(this.progress, FriendlyByteBuf::writeResourceLocation, (var0, var1x) -> {
         var1x.serializeToNetwork(var0);
      });
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateAdvancementsPacket(this);
   }

   public Map<ResourceLocation, Advancement.Builder> getAdded() {
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
