package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentScreen extends AbstractContainerScreen {
   private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");
   private static final ResourceLocation ENCHANTING_BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private static final BookModel BOOK_MODEL = new BookModel();
   private final Random random = new Random();
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   private ItemStack last;

   public EnchantmentScreen(EnchantmentMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.last = ItemStack.EMPTY;
   }

   protected void renderLabels(int var1, int var2) {
      this.font.draw(this.title.getColoredString(), 12.0F, 5.0F, 4210752);
      this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
   }

   public void tick() {
      super.tick();
      this.tickBook();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      int var6 = (this.width - this.imageWidth) / 2;
      int var7 = (this.height - this.imageHeight) / 2;

      for(int var8 = 0; var8 < 3; ++var8) {
         double var9 = var1 - (double)(var6 + 60);
         double var11 = var3 - (double)(var7 + 14 + 19 * var8);
         if (var9 >= 0.0D && var11 >= 0.0D && var9 < 108.0D && var11 < 19.0D && ((EnchantmentMenu)this.menu).clickMenuButton(this.minecraft.player, var8)) {
            this.minecraft.gameMode.handleInventoryButtonClick(((EnchantmentMenu)this.menu).containerId, var8);
            return true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   protected void renderBg(float var1, int var2, int var3) {
      Lighting.setupForFlatItems();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(ENCHANTING_TABLE_LOCATION);
      int var4 = (this.width - this.imageWidth) / 2;
      int var5 = (this.height - this.imageHeight) / 2;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.imageHeight);
      RenderSystem.matrixMode(5889);
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      int var6 = (int)this.minecraft.getWindow().getGuiScale();
      RenderSystem.viewport((this.width - 320) / 2 * var6, (this.height - 240) / 2 * var6, 320 * var6, 240 * var6);
      RenderSystem.translatef(-0.34F, 0.23F, 0.0F);
      RenderSystem.multMatrix(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
      RenderSystem.matrixMode(5888);
      PoseStack var7 = new PoseStack();
      var7.pushPose();
      PoseStack.Pose var8 = var7.last();
      var8.pose().setIdentity();
      var8.normal().setIdentity();
      var7.translate(0.0D, 3.299999952316284D, 1984.0D);
      float var9 = 5.0F;
      var7.scale(5.0F, 5.0F, 5.0F);
      var7.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      var7.mulPose(Vector3f.XP.rotationDegrees(20.0F));
      float var10 = Mth.lerp(var1, this.oOpen, this.open);
      var7.translate((double)((1.0F - var10) * 0.2F), (double)((1.0F - var10) * 0.1F), (double)((1.0F - var10) * 0.25F));
      float var11 = -(1.0F - var10) * 90.0F - 90.0F;
      var7.mulPose(Vector3f.YP.rotationDegrees(var11));
      var7.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      float var12 = Mth.lerp(var1, this.oFlip, this.flip) + 0.25F;
      float var13 = Mth.lerp(var1, this.oFlip, this.flip) + 0.75F;
      var12 = (var12 - (float)Mth.fastFloor((double)var12)) * 1.6F - 0.3F;
      var13 = (var13 - (float)Mth.fastFloor((double)var13)) * 1.6F - 0.3F;
      if (var12 < 0.0F) {
         var12 = 0.0F;
      }

      if (var13 < 0.0F) {
         var13 = 0.0F;
      }

      if (var12 > 1.0F) {
         var12 = 1.0F;
      }

      if (var13 > 1.0F) {
         var13 = 1.0F;
      }

      RenderSystem.enableRescaleNormal();
      BOOK_MODEL.setupAnim(0.0F, var12, var13, var10);
      MultiBufferSource.BufferSource var14 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      VertexConsumer var15 = var14.getBuffer(BOOK_MODEL.renderType(ENCHANTING_BOOK_LOCATION));
      BOOK_MODEL.renderToBuffer(var7, var15, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      var14.endBatch();
      var7.popPose();
      RenderSystem.matrixMode(5889);
      RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
      RenderSystem.popMatrix();
      RenderSystem.matrixMode(5888);
      Lighting.setupFor3DItems();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNames.getInstance().initSeed((long)((EnchantmentMenu)this.menu).getEnchantmentSeed());
      int var16 = ((EnchantmentMenu)this.menu).getGoldCount();

      for(int var17 = 0; var17 < 3; ++var17) {
         int var18 = var4 + 60;
         int var19 = var18 + 20;
         this.setBlitOffset(0);
         this.minecraft.getTextureManager().bind(ENCHANTING_TABLE_LOCATION);
         int var20 = ((EnchantmentMenu)this.menu).costs[var17];
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (var20 == 0) {
            this.blit(var18, var5 + 14 + 19 * var17, 0, 185, 108, 19);
         } else {
            String var21 = "" + var20;
            int var22 = 86 - this.font.width(var21);
            String var23 = EnchantmentNames.getInstance().getRandomName(this.font, var22);
            Font var24 = this.minecraft.getFontManager().get(Minecraft.ALT_FONT);
            int var25 = 6839882;
            if ((var16 < var17 + 1 || this.minecraft.player.experienceLevel < var20) && !this.minecraft.player.abilities.instabuild) {
               this.blit(var18, var5 + 14 + 19 * var17, 0, 185, 108, 19);
               this.blit(var18 + 1, var5 + 15 + 19 * var17, 16 * var17, 239, 16, 16);
               var24.drawWordWrap(var23, var19, var5 + 16 + 19 * var17, var22, (var25 & 16711422) >> 1);
               var25 = 4226832;
            } else {
               int var26 = var2 - (var4 + 60);
               int var27 = var3 - (var5 + 14 + 19 * var17);
               if (var26 >= 0 && var27 >= 0 && var26 < 108 && var27 < 19) {
                  this.blit(var18, var5 + 14 + 19 * var17, 0, 204, 108, 19);
                  var25 = 16777088;
               } else {
                  this.blit(var18, var5 + 14 + 19 * var17, 0, 166, 108, 19);
               }

               this.blit(var18 + 1, var5 + 15 + 19 * var17, 16 * var17, 223, 16, 16);
               var24.drawWordWrap(var23, var19, var5 + 16 + 19 * var17, var22, var25);
               var25 = 8453920;
            }

            var24 = this.minecraft.font;
            var24.drawShadow(var21, (float)(var19 + 86 - var24.width(var21)), (float)(var5 + 16 + 19 * var17 + 7), var25);
         }
      }

   }

   public void render(int var1, int var2, float var3) {
      var3 = this.minecraft.getFrameTime();
      this.renderBackground();
      super.render(var1, var2, var3);
      this.renderTooltip(var1, var2);
      boolean var4 = this.minecraft.player.abilities.instabuild;
      int var5 = ((EnchantmentMenu)this.menu).getGoldCount();

      for(int var6 = 0; var6 < 3; ++var6) {
         int var7 = ((EnchantmentMenu)this.menu).costs[var6];
         Enchantment var8 = Enchantment.byId(((EnchantmentMenu)this.menu).enchantClue[var6]);
         int var9 = ((EnchantmentMenu)this.menu).levelClue[var6];
         int var10 = var6 + 1;
         if (this.isHovering(60, 14 + 19 * var6, 108, 17, (double)var1, (double)var2) && var7 > 0 && var9 >= 0 && var8 != null) {
            ArrayList var11 = Lists.newArrayList();
            var11.add("" + ChatFormatting.WHITE + ChatFormatting.ITALIC + I18n.get("container.enchant.clue", var8.getFullname(var9).getColoredString()));
            if (!var4) {
               var11.add("");
               if (this.minecraft.player.experienceLevel < var7) {
                  var11.add(ChatFormatting.RED + I18n.get("container.enchant.level.requirement", ((EnchantmentMenu)this.menu).costs[var6]));
               } else {
                  String var12;
                  if (var10 == 1) {
                     var12 = I18n.get("container.enchant.lapis.one");
                  } else {
                     var12 = I18n.get("container.enchant.lapis.many", var10);
                  }

                  ChatFormatting var13 = var5 >= var10 ? ChatFormatting.GRAY : ChatFormatting.RED;
                  var11.add(var13 + "" + var12);
                  if (var10 == 1) {
                     var12 = I18n.get("container.enchant.level.one");
                  } else {
                     var12 = I18n.get("container.enchant.level.many", var10);
                  }

                  var11.add(ChatFormatting.GRAY + "" + var12);
               }
            }

            this.renderTooltip(var11, var1, var2);
            break;
         }
      }

   }

   public void tickBook() {
      ItemStack var1 = ((EnchantmentMenu)this.menu).getSlot(0).getItem();
      if (!ItemStack.matches(var1, this.last)) {
         this.last = var1;

         do {
            this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
         } while(this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
      }

      ++this.time;
      this.oFlip = this.flip;
      this.oOpen = this.open;
      boolean var2 = false;

      for(int var3 = 0; var3 < 3; ++var3) {
         if (((EnchantmentMenu)this.menu).costs[var3] != 0) {
            var2 = true;
         }
      }

      if (var2) {
         this.open += 0.2F;
      } else {
         this.open -= 0.2F;
      }

      this.open = Mth.clamp(this.open, 0.0F, 1.0F);
      float var5 = (this.flipT - this.flip) * 0.4F;
      float var4 = 0.2F;
      var5 = Mth.clamp(var5, -0.2F, 0.2F);
      this.flipA += (var5 - this.flipA) * 0.9F;
      this.flip += this.flipA;
   }
}
