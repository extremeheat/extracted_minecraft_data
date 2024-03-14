package net.minecraft.world.level.block.entity.vault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public class VaultSharedData {
   static final String TAG_NAME = "shared_data";
   static Codec<VaultSharedData> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ItemStack.optionalFieldOf("display_item").forGetter(var0x -> var0x.displayItem),
               UUIDUtil.CODEC_LINKED_SET.optionalFieldOf("connected_players", Set.of()).forGetter(var0x -> var0x.connectedPlayers),
               Codec.DOUBLE
                  .optionalFieldOf("connected_particles_range", VaultConfig.DEFAULT.deactivationRange())
                  .forGetter(var0x -> var0x.connectedParticlesRange)
            )
            .apply(var0, VaultSharedData::new)
   );
   private ItemStack displayItem = ItemStack.EMPTY;
   private Set<UUID> connectedPlayers = new ObjectLinkedOpenHashSet();
   private double connectedParticlesRange = VaultConfig.DEFAULT.deactivationRange();
   boolean isDirty;

   VaultSharedData(ItemStack var1, Set<UUID> var2, double var3) {
      super();
      this.displayItem = var1;
      this.connectedPlayers.addAll(var2);
      this.connectedParticlesRange = var3;
   }

   VaultSharedData() {
      super();
   }

   public ItemStack getDisplayItem() {
      return this.displayItem;
   }

   public boolean hasDisplayItem() {
      return !this.displayItem.isEmpty();
   }

   public void setDisplayItem(ItemStack var1) {
      if (!ItemStack.matches(this.displayItem, var1)) {
         this.displayItem = var1.copy();
         this.markDirty();
      }
   }

   boolean hasConnectedPlayers() {
      return !this.connectedPlayers.isEmpty();
   }

   Set<UUID> getConnectedPlayers() {
      return this.connectedPlayers;
   }

   double connectedParticlesRange() {
      return this.connectedParticlesRange;
   }

   void updateConnectedPlayersWithinRange(ServerLevel var1, BlockPos var2, VaultServerData var3, VaultConfig var4, double var5) {
      Set var7 = var4.playerDetector()
         .detect(var1, var4.entitySelector(), var2, var5)
         .stream()
         .filter(var1x -> !var3.getRewardedPlayers().contains(var1x))
         .collect(Collectors.toSet());
      if (!this.connectedPlayers.equals(var7)) {
         this.connectedPlayers = var7;
         this.markDirty();
      }
   }

   private void markDirty() {
      this.isDirty = true;
   }

   void set(VaultSharedData var1) {
      this.displayItem = var1.displayItem;
      this.connectedPlayers = var1.connectedPlayers;
      this.connectedParticlesRange = var1.connectedParticlesRange;
   }
}
