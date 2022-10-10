package net.minecraft.client.network;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;

public class NetworkPlayerInfo {
   private final GameProfile field_178867_a;
   private final Map<Type, ResourceLocation> field_187107_a = Maps.newEnumMap(Type.class);
   private GameType field_178866_b;
   private int field_78829_b;
   private boolean field_178864_d;
   private String field_178863_g;
   private ITextComponent field_178872_h;
   private int field_178873_i;
   private int field_178870_j;
   private long field_178871_k;
   private long field_178868_l;
   private long field_178869_m;

   public NetworkPlayerInfo(GameProfile var1) {
      super();
      this.field_178867_a = var1;
   }

   public NetworkPlayerInfo(SPacketPlayerListItem.AddPlayerData var1) {
      super();
      this.field_178867_a = var1.func_179962_a();
      this.field_178866_b = var1.func_179960_c();
      this.field_78829_b = var1.func_179963_b();
      this.field_178872_h = var1.func_179961_d();
   }

   public GameProfile func_178845_a() {
      return this.field_178867_a;
   }

   public GameType func_178848_b() {
      return this.field_178866_b;
   }

   protected void func_178839_a(GameType var1) {
      this.field_178866_b = var1;
   }

   public int func_178853_c() {
      return this.field_78829_b;
   }

   protected void func_178838_a(int var1) {
      this.field_78829_b = var1;
   }

   public boolean func_178856_e() {
      return this.func_178837_g() != null;
   }

   public String func_178851_f() {
      return this.field_178863_g == null ? DefaultPlayerSkin.func_177332_b(this.field_178867_a.getId()) : this.field_178863_g;
   }

   public ResourceLocation func_178837_g() {
      this.func_178841_j();
      return (ResourceLocation)MoreObjects.firstNonNull(this.field_187107_a.get(Type.SKIN), DefaultPlayerSkin.func_177334_a(this.field_178867_a.getId()));
   }

   @Nullable
   public ResourceLocation func_178861_h() {
      this.func_178841_j();
      return (ResourceLocation)this.field_187107_a.get(Type.CAPE);
   }

   @Nullable
   public ResourceLocation func_187106_i() {
      this.func_178841_j();
      return (ResourceLocation)this.field_187107_a.get(Type.ELYTRA);
   }

   @Nullable
   public ScorePlayerTeam func_178850_i() {
      return Minecraft.func_71410_x().field_71441_e.func_96441_U().func_96509_i(this.func_178845_a().getName());
   }

   protected void func_178841_j() {
      synchronized(this) {
         if (!this.field_178864_d) {
            this.field_178864_d = true;
            Minecraft.func_71410_x().func_152342_ad().func_152790_a(this.field_178867_a, (var1, var2, var3) -> {
               switch(var1) {
               case SKIN:
                  this.field_187107_a.put(Type.SKIN, var2);
                  this.field_178863_g = var3.getMetadata("model");
                  if (this.field_178863_g == null) {
                     this.field_178863_g = "default";
                  }
                  break;
               case CAPE:
                  this.field_187107_a.put(Type.CAPE, var2);
                  break;
               case ELYTRA:
                  this.field_187107_a.put(Type.ELYTRA, var2);
               }

            }, true);
         }

      }
   }

   public void func_178859_a(@Nullable ITextComponent var1) {
      this.field_178872_h = var1;
   }

   @Nullable
   public ITextComponent func_178854_k() {
      return this.field_178872_h;
   }

   public int func_178835_l() {
      return this.field_178873_i;
   }

   public void func_178836_b(int var1) {
      this.field_178873_i = var1;
   }

   public int func_178860_m() {
      return this.field_178870_j;
   }

   public void func_178857_c(int var1) {
      this.field_178870_j = var1;
   }

   public long func_178847_n() {
      return this.field_178871_k;
   }

   public void func_178846_a(long var1) {
      this.field_178871_k = var1;
   }

   public long func_178858_o() {
      return this.field_178868_l;
   }

   public void func_178844_b(long var1) {
      this.field_178868_l = var1;
   }

   public long func_178855_p() {
      return this.field_178869_m;
   }

   public void func_178843_c(long var1) {
      this.field_178869_m = var1;
   }
}
