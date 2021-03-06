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
package bammerbom.ultimatecore.sponge.modules.spawn;

import bammerbom.ultimatecore.sponge.UltimateCore;
import bammerbom.ultimatecore.sponge.api.config.defaultconfigs.module.ModuleConfig;
import bammerbom.ultimatecore.sponge.api.config.defaultconfigs.module.RawModuleConfig;
import bammerbom.ultimatecore.sponge.api.module.Module;
import bammerbom.ultimatecore.sponge.modules.spawn.api.SpawnPermissions;
import bammerbom.ultimatecore.sponge.modules.spawn.command.*;
import bammerbom.ultimatecore.sponge.modules.spawn.listeners.SpawnListener;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class SpawnModule implements Module {
    //spawn
    //globalspawn, firstspawn, groupspawn
    //set(global)spawn, setfirstspawn, setgroupspawn
    //del(global)spawn, delfirstspawn, delgroupspawn
    ModuleConfig config;

    @Override
    public String getIdentifier() {
        return "spawn";
    }

    @Override
    public Text getDescription() {
        return Text.of("Set global or group spawns, and set a location where players will spawn when they join for the first time.");
    }

    @Override
    public Optional<ModuleConfig> getConfig() {
        return Optional.of(config);
    }

    @Override
    public void onRegister() {

    }

    @Override
    public void onInit(GameInitializationEvent event) {
        config = new RawModuleConfig("spawn");

        UltimateCore.get().getCommandService().register(new DelfirstspawnCommand());
        UltimateCore.get().getCommandService().register(new DelglobalspawnCommand());
        UltimateCore.get().getCommandService().register(new DelgroupspawnCommand());
        UltimateCore.get().getCommandService().register(new SetfirstspawnCommand());
        UltimateCore.get().getCommandService().register(new SetglobalspawnCommand());
        UltimateCore.get().getCommandService().register(new SetgroupspawnCommand());
        UltimateCore.get().getCommandService().register(new GlobalspawnCommand());
        UltimateCore.get().getCommandService().register(new FirstspawnCommand());
        UltimateCore.get().getCommandService().register(new GroupspawnCommand());
        UltimateCore.get().getCommandService().register(new SpawnCommand());

        Sponge.getEventManager().registerListeners(UltimateCore.get(), new SpawnListener());

        //Register permissions
        new SpawnPermissions();
    }

    @Override
    public void onPostInit(GamePostInitializationEvent event) {

    }

    @Override
    public void onStop(GameStoppingEvent event) {

    }
}
