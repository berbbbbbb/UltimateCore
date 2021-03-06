/*
 * This file is part of UltimateCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) Bammerbom
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package bammerbom.ultimatecore.sponge.modules.tablist;

import bammerbom.ultimatecore.sponge.UltimateCore;
import bammerbom.ultimatecore.sponge.api.config.defaultconfigs.module.ModuleConfig;
import bammerbom.ultimatecore.sponge.api.config.defaultconfigs.module.RawModuleConfig;
import bammerbom.ultimatecore.sponge.api.module.Module;
import bammerbom.ultimatecore.sponge.modules.tablist.listeners.TablistListener;
import bammerbom.ultimatecore.sponge.modules.tablist.runnables.NamesHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Optional;

public class TablistModule implements Module {
    ModuleConfig config;
    NamesHandler runnable;

    @Override
    public String getIdentifier() {
        return "tablist";
    }


    @Override
    public Text getDescription() {
        return Text.of("Modify the tablist to look however you want, including headers and footers.");
    }

    @Override
    public Optional<ModuleConfig> getConfig() {
        return Optional.of(config);
    }

    @Override
    public void onInit(GameInitializationEvent event) {
        config = new RawModuleConfig("tablist");
        int delay = config.get().getNode("refresh").getInt();
        runnable = new NamesHandler();
        Sponge.getScheduler().createTaskBuilder().execute(runnable::update).name("UltimateCore tablist task").delayTicks(delay).intervalTicks(delay).submit(UltimateCore.get());
        Sponge.getEventManager().registerListeners(UltimateCore.get(), new TablistListener());
    }

    @Override
    public void onReload(@Nullable GameReloadEvent event) {
        runnable.clearCache();
    }

    public NamesHandler getRunnable() {
        return runnable;
    }
}
