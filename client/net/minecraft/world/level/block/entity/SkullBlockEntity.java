package net.minecraft.world.level.block.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Services;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SkullBlockEntity extends BlockEntity {
   public static final String TAG_SKULL_OWNER = "SkullOwner";
   public static final String TAG_NOTE_BLOCK_SOUND = "note_block_sound";
   @Nullable
   private static GameProfileCache profileCache;
   @Nullable
   private static MinecraftSessionService sessionService;
   @Nullable
   private static Executor mainThreadExecutor;
   private static final Executor CHECKED_MAIN_THREAD_EXECUTOR = var0 -> {
      Executor var1 = mainThreadExecutor;
      if (var1 != null) {
         var1.execute(var0);
      }
   };
   @Nullable
   private GameProfile owner;
   @Nullable
   private ResourceLocation noteBlockSound;
   private int animationTickCount;
   private boolean isAnimating;

   public SkullBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SKULL, var1, var2);
   }

   public static void setup(Services var0, Executor var1) {
      profileCache = var0.profileCache();
      sessionService = var0.sessionService();
      mainThreadExecutor = var1;
   }

   public static void clear() {
      profileCache = null;
      sessionService = null;
      mainThreadExecutor = null;
   }

   @Override
   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      if (this.owner != null) {
         CompoundTag var2 = new CompoundTag();
         NbtUtils.writeGameProfile(var2, this.owner);
         var1.put("SkullOwner", var2);
      }

      if (this.noteBlockSound != null) {
         var1.putString("note_block_sound", this.noteBlockSound.toString());
      }
   }

   @Override
   public void load(CompoundTag var1) {
      super.load(var1);
      if (var1.contains("SkullOwner", 10)) {
         this.setOwner(NbtUtils.readGameProfile(var1.getCompound("SkullOwner")));
      } else if (var1.contains("ExtraType", 8)) {
         String var2 = var1.getString("ExtraType");
         if (!StringUtil.isNullOrEmpty(var2)) {
            this.setOwner(new GameProfile(Util.NIL_UUID, var2));
         }
      }

      if (var1.contains("note_block_sound", 8)) {
         this.noteBlockSound = ResourceLocation.tryParse(var1.getString("note_block_sound"));
      }
   }

   public static void animation(Level var0, BlockPos var1, BlockState var2, SkullBlockEntity var3) {
      if (var2.hasProperty(SkullBlock.POWERED) && var2.getValue(SkullBlock.POWERED)) {
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
   public GameProfile getOwnerProfile() {
      return this.owner;
   }

   @Nullable
   public ResourceLocation getNoteBlockSound() {
      return this.noteBlockSound;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public void setOwner(@Nullable GameProfile var1) {
      synchronized(this) {
         this.owner = var1;
      }

      this.updateOwnerProfile();
   }

   private void updateOwnerProfile() {
      if (this.owner != null && !Util.isBlank(this.owner.getName()) && !hasTextures(this.owner)) {
         fetchGameProfile(this.owner.getName()).thenAcceptAsync(var1 -> {
            this.owner = (GameProfile)var1.orElse(this.owner);
            this.setChanged();
         }, CHECKED_MAIN_THREAD_EXECUTOR);
      } else {
         this.setChanged();
      }
   }

   @Nullable
   public static GameProfile getOrResolveGameProfile(CompoundTag var0) {
      if (var0.contains("SkullOwner", 10)) {
         return NbtUtils.readGameProfile(var0.getCompound("SkullOwner"));
      } else {
         if (var0.contains("SkullOwner", 8)) {
            String var1 = var0.getString("SkullOwner");
            if (!Util.isBlank(var1)) {
               var0.remove("SkullOwner");
               resolveGameProfile(var0, var1);
            }
         }

         return null;
      }
   }

   public static void resolveGameProfile(CompoundTag var0) {
      String var1 = var0.getString("SkullOwner");
      if (!Util.isBlank(var1)) {
         resolveGameProfile(var0, var1);
      }
   }

   private static void resolveGameProfile(CompoundTag var0, String var1) {
      fetchGameProfile(var1)
         .thenAccept(
            var2 -> var0.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), (GameProfile)var2.orElse(new GameProfile(Util.NIL_UUID, var1))))
         );
   }

   private static CompletableFuture<Optional<GameProfile>> fetchGameProfile(String var0) {
      GameProfileCache var1 = profileCache;
      return var1 == null
         ? CompletableFuture.completedFuture(Optional.empty())
         : var1.getAsync(var0)
            .thenCompose(var0x -> var0x.isPresent() ? fillProfileTextures((GameProfile)var0x.get()) : CompletableFuture.completedFuture(Optional.empty()))
            .thenApplyAsync((Function<? super Optional, ? extends Optional<GameProfile>>)(var0x -> {
               GameProfileCache var1x = profileCache;
               if (var1x != null) {
                  var0x.ifPresent(var1x::add);
                  return var0x;
               } else {
                  return Optional.empty();
               }
            }), CHECKED_MAIN_THREAD_EXECUTOR);
   }

   private static CompletableFuture<Optional<GameProfile>> fillProfileTextures(GameProfile var0) {
      return hasTextures(var0) ? CompletableFuture.completedFuture(Optional.of(var0)) : CompletableFuture.supplyAsync(() -> {
         MinecraftSessionService var1 = sessionService;
         if (var1 != null) {
            ProfileResult var2 = var1.fetchProfile(var0.getId(), true);
            return var2 == null ? Optional.of(var0) : Optional.of(var2.profile());
         } else {
            return Optional.empty();
         }
      }, Util.backgroundExecutor());
   }

   private static boolean hasTextures(GameProfile var0) {
      return var0.getProperties().containsKey("textures");
   }
}
