package net.minecraft.world.level.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SkullBlockEntity extends BlockEntity implements TickableBlockEntity {
   @Nullable
   private static GameProfileCache profileCache;
   @Nullable
   private static MinecraftSessionService sessionService;
   @Nullable
   private GameProfile owner;
   private int mouthTickCount;
   private boolean isMovingMouth;

   public SkullBlockEntity() {
      super(BlockEntityType.SKULL);
   }

   public static void setProfileCache(GameProfileCache var0) {
      profileCache = var0;
   }

   public static void setSessionService(MinecraftSessionService var0) {
      sessionService = var0;
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      if (this.owner != null) {
         CompoundTag var2 = new CompoundTag();
         NbtUtils.writeGameProfile(var2, this.owner);
         var1.put("SkullOwner", var2);
      }

      return var1;
   }

   public void load(BlockState var1, CompoundTag var2) {
      super.load(var1, var2);
      if (var2.contains("SkullOwner", 10)) {
         this.setOwner(NbtUtils.readGameProfile(var2.getCompound("SkullOwner")));
      } else if (var2.contains("ExtraType", 8)) {
         String var3 = var2.getString("ExtraType");
         if (!StringUtil.isNullOrEmpty(var3)) {
            this.setOwner(new GameProfile((UUID)null, var3));
         }
      }

   }

   public void tick() {
      BlockState var1 = this.getBlockState();
      if (var1.is(Blocks.DRAGON_HEAD) || var1.is(Blocks.DRAGON_WALL_HEAD)) {
         if (this.level.hasNeighborSignal(this.worldPosition)) {
            this.isMovingMouth = true;
            ++this.mouthTickCount;
         } else {
            this.isMovingMouth = false;
         }
      }

   }

   public float getMouthAnimation(float var1) {
      return this.isMovingMouth ? (float)this.mouthTickCount + var1 : (float)this.mouthTickCount;
   }

   @Nullable
   public GameProfile getOwnerProfile() {
      return this.owner;
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 4, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

   public void setOwner(@Nullable GameProfile var1) {
      this.owner = var1;
      this.updateOwnerProfile();
   }

   private void updateOwnerProfile() {
      this.owner = updateGameprofile(this.owner);
      this.setChanged();
   }

   @Nullable
   public static GameProfile updateGameprofile(@Nullable GameProfile var0) {
      if (var0 != null && !StringUtil.isNullOrEmpty(var0.getName())) {
         if (var0.isComplete() && var0.getProperties().containsKey("textures")) {
            return var0;
         } else if (profileCache != null && sessionService != null) {
            GameProfile var1 = profileCache.get(var0.getName());
            if (var1 == null) {
               return var0;
            } else {
               Property var2 = (Property)Iterables.getFirst(var1.getProperties().get("textures"), (Object)null);
               if (var2 == null) {
                  var1 = sessionService.fillProfileProperties(var1, true);
               }

               return var1;
            }
         } else {
            return var0;
         }
      } else {
         return var0;
      }
   }
}
