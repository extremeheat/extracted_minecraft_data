package net.minecraft.world.level.block.entity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Services;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SkullBlockEntity extends BlockEntity {
   private static final String TAG_PROFILE = "profile";
   private static final String TAG_NOTE_BLOCK_SOUND = "note_block_sound";
   private static final String TAG_CUSTOM_NAME = "custom_name";
   @Nullable
   private static Executor mainThreadExecutor;
   @Nullable
   private static LoadingCache<String, CompletableFuture<Optional<GameProfile>>> profileCacheByName;
   @Nullable
   private static LoadingCache<UUID, CompletableFuture<Optional<GameProfile>>> profileCacheById;
   public static final Executor CHECKED_MAIN_THREAD_EXECUTOR = (var0) -> {
      Executor var1 = mainThreadExecutor;
      if (var1 != null) {
         var1.execute(var0);
      }

   };
   @Nullable
   private ResolvableProfile owner;
   @Nullable
   private ResourceLocation noteBlockSound;
   private int animationTickCount;
   private boolean isAnimating;
   @Nullable
   private Component customName;

   public SkullBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SKULL, var1, var2);
   }

   public static void setup(final Services var0, Executor var1) {
      mainThreadExecutor = var1;
      final BooleanSupplier var2 = () -> profileCacheById == null;
      profileCacheByName = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader<String, CompletableFuture<Optional<GameProfile>>>() {
         public CompletableFuture<Optional<GameProfile>> load(String var1) {
            return SkullBlockEntity.fetchProfileByName(var1, var0);
         }

         // $FF: synthetic method
         public Object load(final Object var1) throws Exception {
            return this.load((String)var1);
         }
      });
      profileCacheById = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(10L)).maximumSize(256L).build(new CacheLoader<UUID, CompletableFuture<Optional<GameProfile>>>() {
         public CompletableFuture<Optional<GameProfile>> load(UUID var1) {
            return SkullBlockEntity.fetchProfileById(var1, var0, var2);
         }

         // $FF: synthetic method
         public Object load(final Object var1) throws Exception {
            return this.load((UUID)var1);
         }
      });
   }

   static CompletableFuture<Optional<GameProfile>> fetchProfileByName(String var0, Services var1) {
      return var1.profileCache().getAsync(var0).thenCompose((var0x) -> {
         LoadingCache var1 = profileCacheById;
         return var1 != null && !var0x.isEmpty() ? ((CompletableFuture)var1.getUnchecked(((GameProfile)var0x.get()).getId())).thenApply((var1x) -> var1x.or(() -> var0x)) : CompletableFuture.completedFuture(Optional.empty());
      });
   }

   static CompletableFuture<Optional<GameProfile>> fetchProfileById(UUID var0, Services var1, BooleanSupplier var2) {
      return CompletableFuture.supplyAsync(() -> {
         if (var2.getAsBoolean()) {
            return Optional.empty();
         } else {
            ProfileResult var3 = var1.sessionService().fetchProfile(var0, true);
            return Optional.ofNullable(var3).map(ProfileResult::profile);
         }
      }, Util.backgroundExecutor().forName("fetchProfile"));
   }

   public static void clear() {
      mainThreadExecutor = null;
      profileCacheByName = null;
      profileCacheById = null;
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.storeNullable("profile", ResolvableProfile.CODEC, this.owner);
      var1.storeNullable("note_block_sound", ResourceLocation.CODEC, this.noteBlockSound);
      var1.storeNullable("custom_name", ComponentSerialization.CODEC, var2.createSerializationContext(NbtOps.INSTANCE), this.customName);
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.setOwner((ResolvableProfile)var1.read("profile", ResolvableProfile.CODEC).orElse((Object)null));
      this.noteBlockSound = (ResourceLocation)var1.read("note_block_sound", ResourceLocation.CODEC).orElse((Object)null);
      this.customName = parseCustomNameSafe(var1.get("custom_name"), var2);
   }

   public static void animation(Level var0, BlockPos var1, BlockState var2, SkullBlockEntity var3) {
      if (var2.hasProperty(SkullBlock.POWERED) && (Boolean)var2.getValue(SkullBlock.POWERED)) {
         var3.isAnimating = true;
         ++var3.animationTickCount;
      } else {
         var3.isAnimating = false;
      }

   }

   public float getAnimation(float var1) {
      return this.isAnimating ? (float)this.animationTickCount + var1 : (float)this.animationTickCount;
   }

   @Nullable
   public ResolvableProfile getOwnerProfile() {
      return this.owner;
   }

   @Nullable
   public ResourceLocation getNoteBlockSound() {
      return this.noteBlockSound;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public void setOwner(@Nullable ResolvableProfile var1) {
      synchronized(this) {
         this.owner = var1;
      }

      this.updateOwnerProfile();
   }

   private void updateOwnerProfile() {
      if (this.owner != null && !this.owner.isResolved()) {
         this.owner.resolve().thenAcceptAsync((var1) -> {
            this.owner = var1;
            this.setChanged();
         }, CHECKED_MAIN_THREAD_EXECUTOR);
      } else {
         this.setChanged();
      }
   }

   public static CompletableFuture<Optional<GameProfile>> fetchGameProfile(String var0) {
      LoadingCache var1 = profileCacheByName;
      return var1 != null && StringUtil.isValidPlayerName(var0) ? (CompletableFuture)var1.getUnchecked(var0) : CompletableFuture.completedFuture(Optional.empty());
   }

   public static CompletableFuture<Optional<GameProfile>> fetchGameProfile(UUID var0) {
      LoadingCache var1 = profileCacheById;
      return var1 != null ? (CompletableFuture)var1.getUnchecked(var0) : CompletableFuture.completedFuture(Optional.empty());
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      super.applyImplicitComponents(var1);
      this.setOwner((ResolvableProfile)var1.get(DataComponents.PROFILE));
      this.noteBlockSound = (ResourceLocation)var1.get(DataComponents.NOTE_BLOCK_SOUND);
      this.customName = (Component)var1.get(DataComponents.CUSTOM_NAME);
   }

   protected void collectImplicitComponents(DataComponentMap.Builder var1) {
      super.collectImplicitComponents(var1);
      var1.set(DataComponents.PROFILE, this.owner);
      var1.set(DataComponents.NOTE_BLOCK_SOUND, this.noteBlockSound);
      var1.set(DataComponents.CUSTOM_NAME, this.customName);
   }

   public void removeComponentsFromTag(CompoundTag var1) {
      super.removeComponentsFromTag(var1);
      var1.remove("profile");
      var1.remove("note_block_sound");
      var1.remove("custom_name");
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
