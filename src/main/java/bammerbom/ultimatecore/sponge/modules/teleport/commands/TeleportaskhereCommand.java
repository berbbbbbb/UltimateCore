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
package bammerbom.ultimatecore.sponge.modules.teleport.commands;

import bammerbom.ultimatecore.sponge.UltimateCore;
import bammerbom.ultimatecore.sponge.api.command.Command;
import bammerbom.ultimatecore.sponge.api.data.GlobalData;
import bammerbom.ultimatecore.sponge.api.module.Module;
import bammerbom.ultimatecore.sponge.api.module.Modules;
import bammerbom.ultimatecore.sponge.api.permission.Permission;
import bammerbom.ultimatecore.sponge.api.teleport.Teleportation;
import bammerbom.ultimatecore.sponge.modules.teleport.api.TeleportKeys;
import bammerbom.ultimatecore.sponge.modules.teleport.api.TeleportPermissions;
import bammerbom.ultimatecore.sponge.modules.teleport.api.TpaRequest;
import bammerbom.ultimatecore.sponge.utils.Messages;
import bammerbom.ultimatecore.sponge.utils.Selector;
import bammerbom.ultimatecore.sponge.utils.VariableUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeleportaskhereCommand implements Command {
    @Override
    public Module getModule() {
        return Modules.TELEPORT.get();
    }

    @Override
    public String getIdentifier() {
        return "teleportaskhere";
    }

    @Override
    public Permission getPermission() {
        return TeleportPermissions.UC_TELEPORT_TELEPORTASKHERE;
    }

    @Override
    public List<Permission> getPermissions() {
        return Arrays.asList(TeleportPermissions.UC_TELEPORT_TELEPORTASKHERE);
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("teleportaskhere", "teleportah", "tpahere", "asktphere", "askteleporthere");
    }

    @Override
    public CommandResult run(CommandSource sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.getFormatted("core.noplayer"));
            return CommandResult.empty();
        }
        Player p = (Player) sender;
        if (!sender.hasPermission(TeleportPermissions.UC_TELEPORT_TELEPORTASKHERE.get())) {
            sender.sendMessage(Messages.getFormatted("core.nopermissions"));
            return CommandResult.empty();
        }
        if (args.length == 0) {
            sender.sendMessage(getUsage());
            return CommandResult.empty();
        }
        Player t = Selector.one(sender, args[0]).orElse(null);
        if (t == null) {
            sender.sendMessage(Messages.getFormatted("core.playernotfound", "%player%", args[0]));
            return CommandResult.empty();
        }

        UUID tpid = UUID.randomUUID();
        Teleportation tel = UltimateCore.get().getTeleportService().createTeleportation(sender, Arrays.asList(t), p::getTransform, tele -> {
            p.sendMessage(Messages.getFormatted("teleport.command.teleportaskhere.accept", "%player%", t.getName()));
        }, (tele, reason) -> {
            if (reason.equalsIgnoreCase("tpdeny")) {
                p.sendMessage(Messages.getFormatted("teleport.command.teleportaskhere.deny", "%player%", t.getName()));
            }
        }, true, false);
        HashMap<UUID, TpaRequest> tels = GlobalData.get(TeleportKeys.TELEPORT_ASKHERE_REQUESTS).get();
        tels.put(tpid, new TpaRequest(p, t, tel));
        GlobalData.offer(TeleportKeys.TELEPORT_ASKHERE_REQUESTS, tels);

        sender.sendMessage(Messages.getFormatted("teleport.command.teleportaskhere.send", "%player%", VariableUtil.getNameEntity(t)));
        t.sendMessage(Messages.getFormatted("teleport.command.teleportaskhere.receive", "%player%", VariableUtil.getNameSource(sender), "%tpid%", tpid));
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource sender, String[] args, String curs, Integer curn) {
        return null;
    }
}
