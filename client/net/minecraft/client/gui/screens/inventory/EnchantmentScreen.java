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
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentScreen extends AbstractContainerScreen<EnchantmentMenu> {
   private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");
   private static final ResourceLocation ENCHANTING_BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final Random random = new Random();
   private BookModel bookModel;
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

   protected void init() {
      super.init();
      this.bookModel = new BookModel(this.minecraft.getEntityModels().getLayer(ModelLayers.BOOK));
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

   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      Lighting.setupForFlatItems();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(ENCHANTING_TABLE_LOCATION);
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      this.blit(var1, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      RenderSystem.matrixMode(5889);
      RenderSystem.pushMatrix();
      RenderSystem.loadIdentity();
      int var7 = (int)this.minecraft.getWindow().getGuiScale();
      RenderSystem.viewport((this.width - 320) / 2 * var7, (this.height - 240) / 2 * var7, 320 * var7, 240 * var7);
      RenderSystem.translatef(-0.34F, 0.23F, 0.0F);
      RenderSystem.multMatrix(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
      RenderSystem.matrixMode(5888);
      var1.pushPose();
      PoseStack.Pose var8 = var1.last();
      var8.pose().setIdentity();
      var8.normal().setIdentity();
      var1.translate(0.0D, 3.299999952316284D, 1984.0D);
      float var9 = 5.0F;
      var1.scale(5.0F, 5.0F, 5.0F);
      var1.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      var1.mulPose(Vector3f.XP.rotationDegrees(20.0F));
      float var10 = Mth.lerp(var2, this.oOpen, this.open);
      var1.translate((double)((1.0F - var10) * 0.2F), (double)((1.0F - var10) * 0.1F), (double)((1.0F - var10) * 0.25F));
      float var11 = -(1.0F - var10) * 90.0F - 90.0F;
      var1.mulPose(Vector3f.YP.rotationDegrees(var11));
      var1.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      float var12 = Mth.lerp(var2, this.oFlip, this.flip) + 0.25F;
      float var13 = Mth.lerp(var2, this.oFlip, this.flip) + 0.75F;
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
      this.bookModel.setupAnim(0.0F, var12, var13, var10);
      MultiBufferSource.BufferSource var14 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      VertexConsumer var15 = var14.getBuffer(this.bookModel.renderType(ENCHANTING_BOOK_LOCATION));
      this.bookModel.renderToBuffer(var1, var15, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      var14.endBatch();
      var1.popPose();
      RenderSystem.matrixMode(5889);
      RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
      RenderSystem.popMatrix();
      RenderSystem.matrixMode(5888);
      Lighting.setupFor3DItems();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNames.getInstance().initSeed((long)((EnchantmentMenu)this.menu).getEnchantmentSeed());
      int var16 = ((EnchantmentMenu)this.menu).getGoldCount();

      for(int var17 = 0; var17 < 3; ++var17) {
         int var18 = var5 + 60;
         int var19 = var18 + 20;
         this.setBlitOffset(0);
         this.minecraft.getTextureManager().bind(ENCHANTING_TABLE_LOCATION);
         int var20 = ((EnchantmentMenu)this.menu).costs[var17];
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (var20 == 0) {
            this.blit(var1, var18, var6 + 14 + 19 * var17, 0, 185, 108, 19);
         } else {
            String var21 = "" + var20;
            int var22 = 86 - this.font.width(var21);
            FormattedText var23 = EnchantmentNames.getInstance().getRandomName(this.font, var22);
            int var24 = 6839882;
            if ((var16 < var17 + 1 || this.minecraft.player.experienceLevel < var20) && !this.minecraft.player.getAbilities().instabuild) {
               this.blit(var1, var18, var6 + 14 + 19 * var17, 0, 185, 108, 19);
               this.blit(var1, var18 + 1, var6 + 15 + 19 * var17, 16 * var17, 239, 16, 16);
               this.font.drawWordWrap(var23, var19, var6 + 16 + 19 * var17, var22, (var24 & 16711422) >> 1);
               var24 = 4226832;
            } else {
               int var25 = var3 - (var5 + 60);
               int var26 = var4 - (var6 + 14 + 19 * var17);
               if (var25 >= 0 && var26 >= 0 && var25 < 108 && var26 < 19) {
                  this.blit(var1, var18, var6 + 14 + 19 * var17, 0, 204, 108, 19);
                  var24 = 16777088;
               } else {
                  this.blit(var1, var18, var6 + 14 + 19 * var17, 0, 166, 108, 19);
               }

               this.blit(var1, var18 + 1, var6 + 15 + 19 * var17, 16 * var17, 223, 16, 16);
               this.font.drawWordWrap(var23, var19, var6 + 16 + 19 * var17, var22, var24);
               var24 = 8453920;
            }

            this.font.drawShadow(var1, var21, (float)(var19 + 86 - this.font.width(var21)), (float)(var6 + 16 + 19 * var17 + 7), var24);
         }
      }

   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      var4 = this.minecraft.getFrameTime();
      this.renderBackground(var1);
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
      boolean var5 = this.minecraft.player.getAbilities().instabuild;
      int var6 = ((EnchantmentMenu)this.menu).getGoldCount();

      for(int var7 = 0; var7 < 3; ++var7) {
         int var8 = ((EnchantmentMenu)this.menu).costs[var7];
         Enchantment var9 = Enchantment.byId(((EnchantmentMenu)this.menu).enchantClue[var7]);
         int var10 = ((EnchantmentMenu)this.menu).levelClue[var7];
         int var11 = var7 + 1;
         if (this.isHovering(60, 14 + 19 * var7, 108, 17, (double)var2, (double)var3) && var8 > 0 && var10 >= 0 && var9 != null) {
            ArrayList var12 = Lists.newArrayList();
            var12.add((new TranslatableComponent("container.enchant.clue", new Object[]{var9.getFullname(var10)})).withStyle(ChatFormatting.WHITE));
            if (!var5) {
               var12.add(TextComponent.EMPTY);
               if (this.minecraft.player.experienceLevel < var8) {
                  var12.add((new TranslatableComponent("container.enchant.level.requirement", new Object[]{((EnchantmentMenu)this.menu).costs[var7]})).withStyle(ChatFormatting.RED));
               } else {
                  TranslatableComponent var13;
                  if (var11 == 1) {
                     var13 = new TranslatableComponent("container.enchant.lapis.one");
                  } else {
                     var13 = new TranslatableComponent("container.enchant.lapis.many", new Object[]{var11});
                  }

                  var12.add(var13.withStyle(var6 >= var11 ? ChatFormatting.GRAY : ChatFormatting.RED));
                  TranslatableComponent var14;
                  if (var11 == 1) {
                     var14 = new TranslatableComponent("container.enchant.level.one");
                  } else {
                     var14 = new TranslatableComponent("container.enchant.level.many", new Object[]{var11});
                  }

                  var12.add(var14.withStyle(ChatFormatting.GRAY));
               }
            }

            this.renderComponentTooltip(var1, var12, var2, var3);
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
