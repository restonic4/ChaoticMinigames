package com.chaotic_loom.chaotic_minigames.core.client.gui;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.LanServer;
import net.minecraft.client.server.LanServerDetection;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

public class ServerListScreen extends JoinMultiplayerScreen {
    public static final int BUTTON_ROW_WIDTH = 308;
    public static final int TOP_ROW_BUTTON_WIDTH = 100;
    public static final int LOWER_ROW_BUTTON_WIDTH = 74;
    public static final int FOOTER_HEIGHT = 64;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ServerStatusPinger pinger = new ServerStatusPinger();
    private final Screen lastScreen;
    protected ServerSelectionList serverSelectionList;
    private ServerList servers;
    private Button selectButton;
    @Nullable
    private List<Component> toolTip;
    private boolean initedOnce;

    public ServerListScreen(Screen screen) {
        super(screen);
        this.lastScreen = screen;
    }

    @Override
    protected void init() {
        if (this.initedOnce) {
            this.serverSelectionList.updateSize(this.width, this.height, 32, this.height - 64);
        } else {
            this.initedOnce = true;
            this.servers = new ServerList(this.minecraft);
            loadServerList();

            this.serverSelectionList = new ServerSelectionList(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
            this.serverSelectionList.updateOnlineServers(this.servers);
        }

        this.addWidget(this.serverSelectionList);
        this.selectButton = this.addRenderableWidget(
                Button.builder(Component.translatable("selectServer.select"), buttonx -> this.joinSelectedServer()).width(100).build()
        );

        Button button3 = this.addRenderableWidget(
                Button.builder(Component.translatable("selectServer.refresh"), buttonx -> this.refreshServerList()).width(74).build()
        );
        Button button4 = this.addRenderableWidget(
                Button.builder(CommonComponents.GUI_CANCEL, buttonx -> this.minecraft.setScreen(new TitleScreen())).width(74).build()
        );

        GridLayout gridLayout = new GridLayout();
        GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(1);
        LinearLayout linearLayout = rowHelper.addChild(new LinearLayout(308, 20, LinearLayout.Orientation.HORIZONTAL));
        linearLayout.addChild(this.selectButton);
        rowHelper.addChild(SpacerElement.height(4));
        LinearLayout linearLayout2 = rowHelper.addChild(new LinearLayout(308, 20, LinearLayout.Orientation.HORIZONTAL));
        linearLayout2.addChild(button3);
        linearLayout2.addChild(button4);
        gridLayout.arrangeElements();
        FrameLayout.centerInRectangle(gridLayout, 0, this.height - 64, this.width, 64);
        this.onSelectedChange();
    }

    private void loadServerList() {
        this.servers.serverList.clear();
        this.servers.hiddenServerList.clear();

        this.servers.serverList.addAll(CMSharedConstants.SERVERS);
    }

    @Override
    public void tick() {
        this.pinger.tick();
    }

    @Override
    public void removed() {
        this.pinger.removeAll();
        this.serverSelectionList.removed();
    }

    private void refreshServerList() {
        this.minecraft.setScreen(new ServerListScreen(this.lastScreen));
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) {
            return true;
        } else if (i == 294) {
            this.refreshServerList();
            return true;
        } else if (this.serverSelectionList.getSelected() != null) {
            if (CommonInputs.selected(i)) {
                this.joinSelectedServer();
                return true;
            } else {
                return this.serverSelectionList.keyPressed(i, j, k);
            }
        } else {
            return false;
        }
    }

    private boolean superKeyPressedCopy(int i, int j, int k) {
        if (i == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else if (super.keyPressed(i, j, k)) {
            return true;
        } else {
            Object var10000;
            switch (i) {
                case 258:
                    var10000 = this.createTabEvent();
                    break;
                case 259:
                case 260:
                case 261:
                default:
                    var10000 = null;
                    break;
                case 262:
                    var10000 = this.createArrowEvent(ScreenDirection.RIGHT);
                    break;
                case 263:
                    var10000 = this.createArrowEvent(ScreenDirection.LEFT);
                    break;
                case 264:
                    var10000 = this.createArrowEvent(ScreenDirection.DOWN);
                    break;
                case 265:
                    var10000 = this.createArrowEvent(ScreenDirection.UP);
            }

            FocusNavigationEvent focusNavigationEvent = (FocusNavigationEvent) var10000;
            if (focusNavigationEvent != null) {
                ComponentPath componentPath = super.nextFocusPath((FocusNavigationEvent)focusNavigationEvent);
                if (componentPath == null && focusNavigationEvent instanceof FocusNavigationEvent.TabNavigation) {
                    this.clearFocus();
                    componentPath = super.nextFocusPath((FocusNavigationEvent)focusNavigationEvent);
                }

                if (componentPath != null) {
                    this.changeFocus(componentPath);
                }
            }

            return false;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        this.toolTip = null;
        this.renderBackground(guiGraphics);
        this.serverSelectionList.render(guiGraphics, i, j, f);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);

        for(Renderable renderable : super.renderables) {
            renderable.render(guiGraphics, i, j, f);
        }

        if (this.toolTip != null) {
            guiGraphics.renderComponentTooltip(this.font, this.toolTip, i, j);
        }
    }

    public void joinSelectedServer() {
        ServerSelectionList.Entry entry = this.serverSelectionList.getSelected();
        if (entry instanceof ServerSelectionList.OnlineServerEntry) {
            this.join(((ServerSelectionList.OnlineServerEntry)entry).getServerData());
        } else if (entry instanceof ServerSelectionList.NetworkServerEntry) {
            LanServer lanServer = ((ServerSelectionList.NetworkServerEntry)entry).getServerData();
            this.join(new ServerData(lanServer.getMotd(), lanServer.getAddress(), true));
        }
    }

    private void join(ServerData serverData) {
        ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString(serverData.ip), serverData, false);
    }

    public void setSelected(ServerSelectionList.Entry entry) {
        this.serverSelectionList.setSelected(entry);
        this.onSelectedChange();
    }

    protected void onSelectedChange() {
        this.selectButton.active = false;
        ServerSelectionList.Entry entry = this.serverSelectionList.getSelected();
        if (entry != null && !(entry instanceof ServerSelectionList.LANHeader)) {
            this.selectButton.active = true;
        }
    }

    public ServerStatusPinger getPinger() {
        return this.pinger;
    }

    public void setToolTip(List<Component> list) {
        this.toolTip = list;
    }

    public ServerList getServers() {
        return this.servers;
    }
}
