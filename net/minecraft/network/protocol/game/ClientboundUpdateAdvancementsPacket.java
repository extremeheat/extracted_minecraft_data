package net.minecraft.network.protocol.game;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ClientboundUpdateAdvancementsPacket implements Packet {
   private boolean reset;
   private Map added;
   private Set removed;
   private Map progress;

   public ClientboundUpdateAdvancementsPacket() {
   }

   public ClientboundUpdateAdvancementsPacket(boolean var1, Collection var2, Set var3, Map var4) {
      this.reset = var1;
      this.added = Maps.newHashMap();
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         Advancement var6 = (Advancement)var5.next();
         this.added.put(var6.getId(), var6.deconstruct());
      }

      this.removed = var3;
      this.progress = Maps.newHashMap(var4);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateAdvancementsPacket(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.reset = var1.readBoolean();
      this.added = Maps.newHashMap();
      this.removed = Sets.newLinkedHashSet();
      this.progress = Maps.newHashMap();
      int var2 = var1.readVarInt();

      int var3;
      ResourceLocation var4;
      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1.readResourceLocation();
         Advancement.Builder var5 = Advancement.Builder.fromNetwork(var1);
         this.added.put(var4, var5);
      }

      var2 = var1.readVarInt();

      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1.readResourceLocation();
         this.removed.add(var4);
      }

      var2 = var1.readVarInt();

      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1.readResourceLocation();
         this.progress.put(var4, AdvancementProgress.fromNetwork(var1));
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeBoolean(this.reset);
      var1.writeVarInt(this.added.size());
      Iterator var2 = this.added.entrySet().iterator();

      Entry var3;
      while(var2.hasNext()) {
         var3 = (Entry)var2.next();
         ResourceLocation var4 = (ResourceLocation)var3.getKey();
         Advancement.Builder var5 = (Advancement.Builder)var3.getValue();
         var1.writeResourceLocation(var4);
         var5.serializeToNetwork(var1);
      }

      var1.writeVarInt(this.removed.size());
      var2 = this.removed.iterator();

      while(var2.hasNext()) {
         ResourceLocation var6 = (ResourceLocation)var2.next();
         var1.writeResourceLocation(var6);
      }

      var1.writeVarInt(this.progress.size());
      var2 = this.progress.entrySet().iterator();

      while(var2.hasNext()) {
         var3 = (Entry)var2.next();
         var1.writeResourceLocation((ResourceLocation)var3.getKey());
         ((AdvancementProgress)var3.getValue()).serializeToNetwork(var1);
      }

   }

   public Map getAdded() {
      return this.added;
   }

   public Set getRemoved() {
      return this.removed;
   }

   public Map getProgress() {
      return this.progress;
   }

   public boolean shouldReset() {
      return this.reset;
   }
}
