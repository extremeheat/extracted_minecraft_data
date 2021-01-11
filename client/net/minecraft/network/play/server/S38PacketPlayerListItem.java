package net.minecraft.network.play.server;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

public class S38PacketPlayerListItem implements Packet<INetHandlerPlayClient> {
   private S38PacketPlayerListItem.Action field_179770_a;
   private final List<S38PacketPlayerListItem.AddPlayerData> field_179769_b = Lists.newArrayList();

   public S38PacketPlayerListItem() {
      super();
   }

   public S38PacketPlayerListItem(S38PacketPlayerListItem.Action var1, EntityPlayerMP... var2) {
      super();
      this.field_179770_a = var1;
      EntityPlayerMP[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EntityPlayerMP var6 = var3[var5];
         this.field_179769_b.add(new S38PacketPlayerListItem.AddPlayerData(var6.func_146103_bH(), var6.field_71138_i, var6.field_71134_c.func_73081_b(), var6.func_175396_E()));
      }

   }

   public S38PacketPlayerListItem(S38PacketPlayerListItem.Action var1, Iterable<EntityPlayerMP> var2) {
      super();
      this.field_179770_a = var1;
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
         this.field_179769_b.add(new S38PacketPlayerListItem.AddPlayerData(var4.func_146103_bH(), var4.field_71138_i, var4.field_71134_c.func_73081_b(), var4.func_175396_E()));
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179770_a = (S38PacketPlayerListItem.Action)var1.func_179257_a(S38PacketPlayerListItem.Action.class);
      int var2 = var1.func_150792_a();

      for(int var3 = 0; var3 < var2; ++var3) {
         GameProfile var4 = null;
         int var5 = 0;
         WorldSettings.GameType var6 = null;
         IChatComponent var7 = null;
         switch(this.field_179770_a) {
         case ADD_PLAYER:
            var4 = new GameProfile(var1.func_179253_g(), var1.func_150789_c(16));
            int var8 = var1.func_150792_a();
            int var9 = 0;

            for(; var9 < var8; ++var9) {
               String var10 = var1.func_150789_c(32767);
               String var11 = var1.func_150789_c(32767);
               if (var1.readBoolean()) {
                  var4.getProperties().put(var10, new Property(var10, var11, var1.func_150789_c(32767)));
               } else {
                  var4.getProperties().put(var10, new Property(var10, var11));
               }
            }

            var6 = WorldSettings.GameType.func_77146_a(var1.func_150792_a());
            var5 = var1.func_150792_a();
            if (var1.readBoolean()) {
               var7 = var1.func_179258_d();
            }
            break;
         case UPDATE_GAME_MODE:
            var4 = new GameProfile(var1.func_179253_g(), (String)null);
            var6 = WorldSettings.GameType.func_77146_a(var1.func_150792_a());
            break;
         case UPDATE_LATENCY:
            var4 = new GameProfile(var1.func_179253_g(), (String)null);
            var5 = var1.func_150792_a();
            break;
         case UPDATE_DISPLAY_NAME:
            var4 = new GameProfile(var1.func_179253_g(), (String)null);
            if (var1.readBoolean()) {
               var7 = var1.func_179258_d();
            }
            break;
         case REMOVE_PLAYER:
            var4 = new GameProfile(var1.func_179253_g(), (String)null);
         }

         this.field_179769_b.add(new S38PacketPlayerListItem.AddPlayerData(var4, var5, var6, var7));
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_179770_a);
      var1.func_150787_b(this.field_179769_b.size());
      Iterator var2 = this.field_179769_b.iterator();

      while(true) {
         while(var2.hasNext()) {
            S38PacketPlayerListItem.AddPlayerData var3 = (S38PacketPlayerListItem.AddPlayerData)var2.next();
            switch(this.field_179770_a) {
            case ADD_PLAYER:
               var1.func_179252_a(var3.func_179962_a().getId());
               var1.func_180714_a(var3.func_179962_a().getName());
               var1.func_150787_b(var3.func_179962_a().getProperties().size());
               Iterator var4 = var3.func_179962_a().getProperties().values().iterator();

               while(var4.hasNext()) {
                  Property var5 = (Property)var4.next();
                  var1.func_180714_a(var5.getName());
                  var1.func_180714_a(var5.getValue());
                  if (var5.hasSignature()) {
                     var1.writeBoolean(true);
                     var1.func_180714_a(var5.getSignature());
                  } else {
                     var1.writeBoolean(false);
                  }
               }

               var1.func_150787_b(var3.func_179960_c().func_77148_a());
               var1.func_150787_b(var3.func_179963_b());
               if (var3.func_179961_d() == null) {
                  var1.writeBoolean(false);
               } else {
                  var1.writeBoolean(true);
                  var1.func_179256_a(var3.func_179961_d());
               }
               break;
            case UPDATE_GAME_MODE:
               var1.func_179252_a(var3.func_179962_a().getId());
               var1.func_150787_b(var3.func_179960_c().func_77148_a());
               break;
            case UPDATE_LATENCY:
               var1.func_179252_a(var3.func_179962_a().getId());
               var1.func_150787_b(var3.func_179963_b());
               break;
            case UPDATE_DISPLAY_NAME:
               var1.func_179252_a(var3.func_179962_a().getId());
               if (var3.func_179961_d() == null) {
                  var1.writeBoolean(false);
               } else {
                  var1.writeBoolean(true);
                  var1.func_179256_a(var3.func_179961_d());
               }
               break;
            case REMOVE_PLAYER:
               var1.func_179252_a(var3.func_179962_a().getId());
            }
         }

         return;
      }
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147256_a(this);
   }

   public List<S38PacketPlayerListItem.AddPlayerData> func_179767_a() {
      return this.field_179769_b;
   }

   public S38PacketPlayerListItem.Action func_179768_b() {
      return this.field_179770_a;
   }

   public String toString() {
      return Objects.toStringHelper(this).add("action", this.field_179770_a).add("entries", this.field_179769_b).toString();
   }

   public class AddPlayerData {
      private final int field_179966_b;
      private final WorldSettings.GameType field_179967_c;
      private final GameProfile field_179964_d;
      private final IChatComponent field_179965_e;

      public AddPlayerData(GameProfile var2, int var3, WorldSettings.GameType var4, IChatComponent var5) {
         super();
         this.field_179964_d = var2;
         this.field_179966_b = var3;
         this.field_179967_c = var4;
         this.field_179965_e = var5;
      }

      public GameProfile func_179962_a() {
         return this.field_179964_d;
      }

      public int func_179963_b() {
         return this.field_179966_b;
      }

      public WorldSettings.GameType func_179960_c() {
         return this.field_179967_c;
      }

      public IChatComponent func_179961_d() {
         return this.field_179965_e;
      }

      public String toString() {
         return Objects.toStringHelper(this).add("latency", this.field_179966_b).add("gameMode", this.field_179967_c).add("profile", this.field_179964_d).add("displayName", this.field_179965_e == null ? null : IChatComponent.Serializer.func_150696_a(this.field_179965_e)).toString();
      }
   }

   public static enum Action {
      ADD_PLAYER,
      UPDATE_GAME_MODE,
      UPDATE_LATENCY,
      UPDATE_DISPLAY_NAME,
      REMOVE_PLAYER;

      private Action() {
      }
   }
}
