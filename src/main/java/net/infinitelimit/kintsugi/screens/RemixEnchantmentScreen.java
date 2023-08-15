package net.infinitelimit.kintsugi.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import java.util.ArrayList;
import java.util.List;

import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.menus.RemixEnchantmentMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RemixEnchantmentScreen extends AbstractContainerScreen<RemixEnchantmentMenu> {
    /** The ResourceLocation containing the Enchantment GUI texture location */
    private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation(Kintsugi.MOD_ID, "textures/gui/container/enchanting_table.png");
    /** The ResourceLocation containing the texture for the Book rendered above the enchantment table */
    private static final ResourceLocation ENCHANTING_BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");

    /** Text for UI **/
    private static final ResourceLocation ALT_FONT = new ResourceLocation("minecraft", "alt");
    private static final Style ROOT_STYLE = Style.EMPTY.withFont(ALT_FONT);

    /** A Random instance for use with the enchantment gui */
    private final RandomSource random = RandomSource.create();
    private BookModel bookModel;
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;

    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;
    private List<EnchantmentSelectionButton> buttons;

    public RemixEnchantmentScreen(RemixEnchantmentMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 292;
        this.inventoryLabelX = 123;

    }

    protected void init() {
        super.init();
        this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
        this.buttons = new ArrayList<>();
    }

    public void onEnchantmentClick(int i) {
        this.menu.clickMenuButton(this.minecraft.player, i);
        this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, i);
    }

    public void containerTick() {
        super.containerTick();
        this.tickBook();
    }

    /**
     * Called when a mouse button is clicked within the GUI element.
     * <p>
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     * @param pButton the button that was clicked.
     */
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int pX = (this.width - this.imageWidth) / 2;
        int pY = (this.height - this.imageHeight) / 2;
        for (int k = 0; k < this.menu.getAvailableEnchantments().size(); k++) {
            double d0 = pMouseX - (double) (pX + 4);
            double d1 = pMouseY - (double) (pY + 18 + 19 * k);

            if (d0 >= 0.0D && d1 >= 0.0D && d0 < 104.0D && d1 < 19.0D) {
                this.onEnchantmentClick(k);
                return true;
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int pX = (this.width - this.imageWidth) / 2;
        int pY = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, pX, pY, 0, 0F, 0F,  this.imageWidth, this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        this.renderBook(pGuiGraphics, pX + 114, pY + 15, pPartialTick);
        int xOffset = pX + 5;
        int textXOffset = xOffset + 2;

        List<Enchantment> enchantments = this.menu.getAvailableEnchantments();
        int total = enchantments.size();
        for (int i = 0; i < total; i++) {
            int maxWidth = 104;
            Enchantment enchantment = enchantments.get(i);
            if (this.menu.getSelectedEnchantment() == i) {
                pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, xOffset, pY + 18 + 19 * i, 104, 166, 104, 19, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            } else if (pMouseY >= pY + 18 + 19 * i && pMouseY < pY + 18 + 19 * (i + 1) && pMouseX >= xOffset && pMouseX < xOffset + 104) {
                pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, xOffset, pY + 18 + 19 * i, 0, 166 + 19, 104, 19, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            } else {
                pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, xOffset, pY + 18 + 19 * i, 0, 166, 104, 19, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }

            MutableComponent formattedText = Component.translatable(enchantment.getDescriptionId());

            int color = 0x685e4a;
            pGuiGraphics.drawWordWrap(this.font, formattedText, textXOffset, pY + 20 + 19 * i, maxWidth, color);
            FormattedText runes = formattedText.withStyle(ROOT_STYLE);
            pGuiGraphics.drawWordWrap(font, runes, textXOffset,  pY + 20 + 19 * i + 7, 102, color);
        }

        int range = this.menu.getMaxPower();
        for (int i = 0; i < range; i++) {
            int k;
            if (i < this.menu.getFuelCount()) {
              k = 0;
            } else {
              k = 1;
            }
            pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, pX + 198 + 12 * i, pY + 53, k * 10, 234, 10, 8, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }

        boolean flag = this.minecraft.player.getAbilities().instabuild;
        if (!flag && this.menu.getLevelCost() >= 0) {
            int color = 0x685e4a;
            MutableComponent text = Component.translatable("container.kintsugi.enchant.levelcost", this.menu.getLevelCost());

            pGuiGraphics.drawWordWrap(font, text, pX + 181, pY + 58, 102, color);

        }
    }

    private void renderBook(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick) {
        float f = Mth.lerp(pPartialTick, this.oOpen, this.open);
        float f1 = Mth.lerp(pPartialTick, this.oFlip, this.flip);
        Lighting.setupForEntityInInventory();
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((float)pX + 33.0F, (float)pY + 31.0F, 100.0F);
        float f2 = 40.0F;
        pGuiGraphics.pose().scale(-40.0F, 40.0F, 40.0F);
        pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(25.0F));
        pGuiGraphics.pose().translate((1.0F - f) * 0.2F, (1.0F - f) * 0.1F, (1.0F - f) * 0.25F);
        float f3 = -(1.0F - f) * 90.0F - 90.0F;
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(f3));
        pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(180.0F));
        float f4 = Mth.clamp(Mth.frac(f1 + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
        float f5 = Mth.clamp(Mth.frac(f1 + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
        this.bookModel.setupAnim(0.0F, f4, f5, f);
        VertexConsumer vertexconsumer = pGuiGraphics.bufferSource().getBuffer(this.bookModel.renderType(ENCHANTING_BOOK_LOCATION));
        this.bookModel.renderToBuffer(pGuiGraphics.pose(), vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pGuiGraphics.flush();
        pGuiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }

    /**
     * Renders the graphical user interface (GUI) element.
     * @param pGuiGraphics the GuiGraphics object used for rendering.
     * @param pMouseX the x-coordinate of the mouse cursor.
     * @param pMouseY the y-coordinate of the mouse cursor.
     * @param pPartialTick the partial tick time.
     */
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pPartialTick = this.minecraft.getFrameTime();
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);


        for(EnchantmentSelectionButton button : this.buttons) {
            if (button.isHoveredOrFocused()) {
                button.renderToolTip(pGuiGraphics, pMouseX, pMouseY);
            }

            button.visible = button.index < this.menu.getAvailableEnchantments().size();
        }
    }

    public void tickBook() {
        ItemStack itemstack = this.menu.getSlot(2).getItem();
        if (!ItemStack.matches(itemstack, this.last)) {
            this.last = itemstack;

            do {
                this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while(this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
        }

        ++this.time;
        this.oFlip = this.flip;
        this.oOpen = this.open;
        boolean flag = !itemstack.isEmpty();

        if (flag) {
            this.open += 0.2F;
        } else {
            this.open -= 0.2F;
        }

        this.open = Mth.clamp(this.open, 0.0F, 1.0F);
        float f1 = (this.flipT - this.flip) * 0.4F;
        float f = 0.2F;
        f1 = Mth.clamp(f1, -0.2F, 0.2F);
        this.flipA += (f1 - this.flipA) * 0.9F;
        this.flip += this.flipA;
    }

    @OnlyIn(Dist.CLIENT)
    class EnchantmentSelectionButton extends Button {
        final int index;
        public EnchantmentSelectionButton(int pX, int pY, int pIndex, Button.OnPress pOnPress) {
            super(pX, pY, 104, 19, CommonComponents.EMPTY, pOnPress, DEFAULT_NARRATION);
            this.index = pIndex;
            Enchantment enchantment = RemixEnchantmentScreen.this.menu.getAvailableEnchantments().get(pIndex);
            setMessage(Component.translatable(enchantment.getDescriptionId()));
            this.visible = false;
        }

        @Override
        protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            Minecraft minecraft = Minecraft.getInstance();
            pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            pGuiGraphics.blitNineSliced(ENCHANTING_TABLE_LOCATION,
                    this.getX(), this.getY(), this.getWidth(), this.getHeight(),
                    4, 4, 104, 19,
                    0, 166);
            pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            int i = getFGColor();
            this.renderString(pGuiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
        }

        public void renderToolTip(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
            /*
            if (this.isHovered && RemixEnchantmentMenu.this.menu.getOffers().size() > this.index + MerchantScreen.this.scrollOff) {
                if (pMouseX < this.getX() + 20) {
                    ItemStack itemstack = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getCostA();
                    pGuiGraphics.renderTooltip(MerchantScreen.this.font, itemstack, pMouseX, pMouseY);
                } else if (pMouseX < this.getX() + 50 && pMouseX > this.getX() + 30) {
                    ItemStack itemstack2 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getCostB();
                    if (!itemstack2.isEmpty()) {
                        pGuiGraphics.renderTooltip(MerchantScreen.this.font, itemstack2, pMouseX, pMouseY);
                    }
                } else if (pMouseX > this.getX() + 65) {
                    ItemStack itemstack1 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getResult();
                    pGuiGraphics.renderTooltip(MerchantScreen.this.font, itemstack1, pMouseX, pMouseY);
                }
            }

             */

        }

    }
}
