package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class ClientSuggestionProvider implements ISuggestionProvider {
   private final NetHandlerPlayClient field_197016_a;
   private final Minecraft field_210248_b;
   private int field_197017_b = -1;
   private CompletableFuture<Suggestions> field_197018_c;

   public ClientSuggestionProvider(NetHandlerPlayClient var1, Minecraft var2) {
      super();
      this.field_197016_a = var1;
      this.field_210248_b = var2;
   }

   public Collection<String> func_197011_j() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.field_197016_a.func_175106_d().iterator();

      while(var2.hasNext()) {
         NetworkPlayerInfo var3 = (NetworkPlayerInfo)var2.next();
         var1.add(var3.func_178845_a().getName());
      }

      return var1;
   }

   public Collection<String> func_211270_p() {
      return (Collection)(this.field_210248_b.field_71476_x != null && this.field_210248_b.field_71476_x.field_72313_a == RayTraceResult.Type.ENTITY ? Collections.singleton(this.field_210248_b.field_71476_x.field_72308_g.func_189512_bd()) : Collections.emptyList());
   }

   public Collection<String> func_197012_k() {
      return this.field_197016_a.func_195514_j().func_96441_U().func_96531_f();
   }

   public Collection<ResourceLocation> func_197010_l() {
      return this.field_210248_b.func_147118_V().func_195477_a();
   }

   public Collection<ResourceLocation> func_199612_m() {
      return this.field_197016_a.func_199526_e().func_199511_c();
   }

   public boolean func_197034_c(int var1) {
      EntityPlayerSP var2 = this.field_210248_b.field_71439_g;
      return var2 != null ? var2.func_211513_k(var1) : var1 == 0;
   }

   public CompletableFuture<Suggestions> func_197009_a(CommandContext<ISuggestionProvider> var1, SuggestionsBuilder var2) {
      if (this.field_197018_c != null) {
         this.field_197018_c.cancel(false);
      }

      this.field_197018_c = new CompletableFuture();
      int var3 = ++this.field_197017_b;
      this.field_197016_a.func_147297_a(new CPacketTabComplete(var3, var1.getInput()));
      return this.field_197018_c;
   }

   private static String func_209001_a(double var0) {
      return String.format(Locale.ROOT, "%.2f", var0);
   }

   private static String func_209002_a(int var0) {
      return Integer.toString(var0);
   }

   public Collection<ISuggestionProvider.Coordinates> func_199613_a(boolean var1) {
      if (this.field_210248_b.field_71476_x != null && this.field_210248_b.field_71476_x.field_72313_a == RayTraceResult.Type.BLOCK) {
         if (var1) {
            Vec3d var3 = this.field_210248_b.field_71476_x.field_72307_f;
            return Collections.singleton(new ISuggestionProvider.Coordinates(func_209001_a(var3.field_72450_a), func_209001_a(var3.field_72448_b), func_209001_a(var3.field_72449_c)));
         } else {
            BlockPos var2 = this.field_210248_b.field_71476_x.func_178782_a();
            return Collections.singleton(new ISuggestionProvider.Coordinates(func_209002_a(var2.func_177958_n()), func_209002_a(var2.func_177956_o()), func_209002_a(var2.func_177952_p())));
         }
      } else {
         return Collections.singleton(ISuggestionProvider.Coordinates.field_209005_b);
      }
   }

   public void func_197015_a(int var1, Suggestions var2) {
      if (var1 == this.field_197017_b) {
         this.field_197018_c.complete(var2);
         this.field_197018_c = null;
         this.field_197017_b = -1;
      }

   }
}
