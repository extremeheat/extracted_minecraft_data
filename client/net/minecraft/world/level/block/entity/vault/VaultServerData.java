package net.minecraft.world.level.block.entity.vault;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VaultServerData {
   static final String TAG_NAME = "server_data";
   static Codec<VaultServerData> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(UUIDUtil.CODEC_LINKED_SET.lenientOptionalFieldOf("rewarded_players", Set.of()).forGetter((var0x) -> {
         return var0x.rewardedPlayers;
      }), Codec.LONG.lenientOptionalFieldOf("state_updating_resumes_at", 0L).forGetter((var0x) -> {
         return var0x.stateUpdatingResumesAt;
      }), ItemStack.CODEC.listOf().lenientOptionalFieldOf("items_to_eject", List.of()).forGetter((var0x) -> {
         return var0x.itemsToEject;
      }), Codec.INT.lenientOptionalFieldOf("total_ejections_needed", 0).forGetter((var0x) -> {
         return var0x.totalEjectionsNeeded;
      })).apply(var0, VaultServerData::new);
   });
   private static final int MAX_REWARD_PLAYERS = 128;
   private final Set<UUID> rewardedPlayers = new ObjectLinkedOpenHashSet();
   private long stateUpdatingResumesAt;
   private final List<ItemStack> itemsToEject = new ObjectArrayList();
   private long lastInsertFailTimestamp;
   private int totalEjectionsNeeded;
   boolean isDirty;

   VaultServerData(Set<UUID> var1, long var2, List<ItemStack> var4, int var5) {
      super();
      this.rewardedPlayers.addAll(var1);
      this.stateUpdatingResumesAt = var2;
      this.itemsToEject.addAll(var4);
      this.totalEjectionsNeeded = var5;
   }

   VaultServerData() {
      super();
   }

   void setLastInsertFailTimestamp(long var1) {
      this.lastInsertFailTimestamp = var1;
   }

   long getLastInsertFailTimestamp() {
      return this.lastInsertFailTimestamp;
   }

   Set<UUID> getRewardedPlayers() {
      return this.rewardedPlayers;
   }

   boolean hasRewardedPlayer(Player var1) {
      return this.rewardedPlayers.contains(var1.getUUID());
   }

   @VisibleForTesting
   public void addToRewardedPlayers(Player var1) {
      this.rewardedPlayers.add(var1.getUUID());
      if (this.rewardedPlayers.size() > 128) {
         Iterator var2 = this.rewardedPlayers.iterator();
         if (var2.hasNext()) {
            var2.next();
            var2.remove();
         }
      }

      this.markChanged();
   }

   long stateUpdatingResumesAt() {
      return this.stateUpdatingResumesAt;
   }

   void pauseStateUpdatingUntil(long var1) {
      this.stateUpdatingResumesAt = var1;
      this.markChanged();
   }

   List<ItemStack> getItemsToEject() {
      return this.itemsToEject;
   }

   void markEjectionFinished() {
      this.totalEjectionsNeeded = 0;
      this.markChanged();
   }

   void setItemsToEject(List<ItemStack> var1) {
      this.itemsToEject.clear();
      this.itemsToEject.addAll(var1);
      this.totalEjectionsNeeded = this.itemsToEject.size();
      this.markChanged();
   }

   ItemStack getNextItemToEject() {
      return this.itemsToEject.isEmpty() ? ItemStack.EMPTY : (ItemStack)Objects.requireNonNullElse((ItemStack)this.itemsToEject.get(this.itemsToEject.size() - 1), ItemStack.EMPTY);
   }

   ItemStack popNextItemToEject() {
      if (this.itemsToEject.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.markChanged();
         return (ItemStack)Objects.requireNonNullElse((ItemStack)this.itemsToEject.remove(this.itemsToEject.size() - 1), ItemStack.EMPTY);
      }
   }

   void set(VaultServerData var1) {
      this.stateUpdatingResumesAt = var1.stateUpdatingResumesAt();
      this.itemsToEject.clear();
      this.itemsToEject.addAll(var1.itemsToEject);
      this.rewardedPlayers.clear();
      this.rewardedPlayers.addAll(var1.rewardedPlayers);
   }

   private void markChanged() {
      this.isDirty = true;
   }

   public float ejectionProgress() {
      return this.totalEjectionsNeeded == 1 ? 1.0F : 1.0F - Mth.inverseLerp((float)this.getItemsToEject().size(), 1.0F, (float)this.totalEjectionsNeeded);
   }
}
