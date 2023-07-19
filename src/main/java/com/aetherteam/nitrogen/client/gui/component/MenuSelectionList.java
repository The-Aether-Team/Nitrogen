package com.aetherteam.nitrogen.client.gui.component;

import com.aetherteam.nitrogen.api.menu.Menu;
import com.aetherteam.nitrogen.client.gui.screen.MenuSelectionScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class MenuSelectionList extends ObjectSelectionList<MenuSelectionList.MenuEntry> {
    private final MenuSelectionScreen parent;

    private static final int ENTRY_PADDING = 2;

    public MenuSelectionList(MenuSelectionScreen parent, int width, int height, int top, int bottom, int itemHeight) {
        super(parent.getMinecraftInstance(), width, height, top, bottom, itemHeight);
        this.parent = parent;
        this.refreshList();
    }

    @Override
    protected void renderSelection(PoseStack poseStack, int top, int width, int height, int outerColor, int innerColor) {
        int i = this.x0 + (this.width - width) / 2;
        int j = this.x0 + (this.width + width) / 2;
        fill(poseStack, i + 1, top - 3, j - 7, top + height + 1, -1);
    }

    @Override
    protected int getScrollbarPosition() {
        return (this.parent.width / 2) + (this.parent.frameWidth / 2) - 18;
    }

    @Override
    public int getRowWidth() {
        return 115;
    }

    public void refreshList() {
        this.clearEntries();
        this.parent.buildMenuList(this::addEntry, (menu) -> new MenuEntry(this.parent, menu));
    }

    public class MenuEntry extends ObjectSelectionList.Entry<MenuEntry> {
        private final MenuSelectionScreen parent;
        private final Menu menu;

        public MenuEntry(MenuSelectionScreen parent, Menu menu) {
            this.parent = parent;
            this.menu = menu;
        }

        @Override
        public Component getNarration() {
            return this.menu.getName();
        }

        @Override
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
            poseStack.pushPose();
            fillGradient(poseStack, left, top - ENTRY_PADDING, left + MenuSelectionList.this.getRowWidth() - (ENTRY_PADDING * 2) - 6, top + MenuSelectionList.this.itemHeight - (ENTRY_PADDING * 2), -10066330, -8750470);
            poseStack.popPose();
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShaderTexture(0, this.menu.getIcon());
            poseStack.pushPose();
            GuiComponent.blit(poseStack, left + ENTRY_PADDING + 1, top + 1, 0, 0, 16, 16, 16, 16);
            poseStack.popPose();

            Font font = this.parent.getFontRenderer();
            int fontWidth = MenuSelectionList.this.getRowWidth() - (ENTRY_PADDING * 2) - 24;
            List<FormattedCharSequence> lines = font.split(this.menu.getName(), fontWidth);


            int length = 1;
            for (FormattedCharSequence line : lines) {
                int y = top + (length * 10) - ((lines.size() * 10) / 2);
                font.draw(poseStack, line, left + ENTRY_PADDING + 21, y, 0xFFFFFF);
                length++;
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.parent.setSelected(this);
            MenuSelectionList.this.setSelected(this);
            return false;
        }

        public Menu getMenu() {
            return this.menu;
        }
    }
}
