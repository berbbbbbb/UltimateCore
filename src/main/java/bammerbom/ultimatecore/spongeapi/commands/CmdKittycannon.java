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
package bammerbom.ultimatecore.spongeapi.commands;

import bammerbom.ultimatecore.spongeapi.UltimateCommand;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CmdKittycannon implements UltimateCommand {

    Random ra = new Random();

    @Override
    public String getName() {
        return "kittycannon";
    }

    @Override
    public String getPermission() {
        return "uc.kittycannon";
    }

    @Override
    public String getUsage() {
        return "/<command> ";
    }

    @Override
    public Text getDescription() {
        return Text.of("Description");
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList();
    }

    @Override
    public CommandResult run(final CommandSource cs, String label, String[] args) {
        return CommandResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSource cs, String alias, String[] args, String curs, Integer curn) {
        return null;
    }
//    @Override
//    public List<String> getAliases() {
//        return Arrays.asList("kittyboom");
//    }
//
//    @Override
//    public void run(final CommandSource cs, String label, String[] args) {
//        if (!r.isPlayer(cs)) {
//            return CommandResult.empty();
//        }
//        if (!r.perm(cs, "uc.kittycannon", false, true)) {
//            return CommandResult.empty();
//        }
//        Player p = (Player) cs;
//        final Ocelot ocelot = (Ocelot) p.getWorld().spawnEntity(p.getLocation(), EntityType.OCELOT);
//        if (ocelot == null) {
//            return CommandResult.empty();
//        }
//        int i = ra.nextInt(Ocelot.Type.values().length);
//        ocelot.setCatType(Ocelot.Type.values()[i]);
//        ocelot.setTamed(true);
//        i = ra.nextInt(2);
//        if (i == 1) {
//            ocelot.setBaby();
//        }
//        ocelot.setVelocity(p.getEyeLocation().getDirection().multiply(2));
//        Bukkit.getScheduler().scheduleSyncDelayedTask(r.getUC(), new Runnable() {
//            @Override
//            public void run() {
//                Location loc = ocelot.getLocation();
//                ocelot.playEffect(EntityEffect.HURT);
//                ocelot.remove();
//                loc.getWorld().createExplosion(loc, 0.0F, false);
//            }
//        }, 18L);
//    }
//
//    @Override
//    public List<String> onTabComplete(CommandSource cs, Command cmd, String alias, String[] args, String curs, Integer curn) {
//        return new ArrayList<>();
//    }
}
